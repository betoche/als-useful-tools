package org.als.random.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StringPatternSearchInDB {
    private final String userName;
    private final String password;
    private final String host;
    private final int port;
    private final String databaseName;
    private final String[] searchPatterns;

    private Connection databaseConn;
    private List<DBTable> tableList;
    private List<DBSearchResult> searchResultsList;

    private final static Logger LOGGER = LoggerFactory.getLogger(StringPatternSearchInDB.class);

    public StringPatternSearchInDB( String userName, String password, String host, int port, String databaseName, String[] searchPatterns ) {
        this.userName = userName;
        this.password = password;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.searchPatterns = searchPatterns;
    }

    public List<DBSearchResult> getSearchResultsList() {
        if( Objects.isNull(this.searchResultsList) ) {
            this.searchResultsList = new ArrayList<>();

            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                con = getDatabaseConn();
                for (String pattern : this.searchPatterns) {
                    DBSearchResult searchResult = new DBSearchResult(pattern);
                    for (DBTable table : getTableList()) {
                        try {
                            String queryStr = table.getQueryString(pattern);
                            if( queryStr.isEmpty() ){
                                continue;
                            }
                            con = getDatabaseConn();
                            ps = con.prepareStatement(queryStr);
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                for (DBColumn col : table.getColumns()) {
                                    String colValue = switch (col.getType()) {
                                        case "int" -> String.valueOf(rs.getInt(col.getName()));
                                        case "nvarchar" -> rs.getString(col.getName());
                                        default -> "";
                                    };

                                    if (Objects.nonNull(colValue) && colValue.toLowerCase().contains(pattern.toLowerCase())) {
                                        searchResult.addColumn(col);
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            LOGGER.error(table.getQueryString(pattern));
                            LOGGER.error(e.getMessage(), e);
                        }
                        /*
                        finally {
                            if (Objects.nonNull(rs)) {
                                try {
                                    rs.close();
                                } catch (SQLException e) {
                                }
                            }
                            if (Objects.nonNull(ps)) {
                                try {
                                    ps.close();
                                } catch (SQLException e) {
                                }
                            }
                        }
                        */
                    }
                    this.searchResultsList.add(searchResult);
                }
            }catch( Exception e ){
                LOGGER.error(e.getMessage(), e);
            } finally {
                closeConnectionObjects(con, ps, rs);
            }
        }
        return this.searchResultsList;
    }

    public List<DBTable> getTableList() {
        if( Objects.isNull(this.tableList) ) {
            this.tableList = new ArrayList<>();

            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                String tablesQuery = String.format("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG='%s'", this.databaseName);
                con = getDatabaseConn();
                ps = con.prepareStatement(tablesQuery);
                rs = ps.executeQuery();

                while( rs.next() ) {
                    String tableName = rs.getString("TABLE_NAME");
                    this.tableList.add(new DBTable(tableName));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                closeConnectionObjects(con, ps, rs);
            }

            retrieveColumnList();

        }
        return this.tableList;
    }

    private void retrieveColumnList() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            for( DBTable table : getTableList() ) {
                String tablesQuery = String.format("SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'%s';", table.getName());
                con = getDatabaseConn();
                ps = con.prepareStatement(tablesQuery);
                rs = ps.executeQuery();

                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String dataType = rs.getString("DATA_TYPE");
                    table.addColumn(new DBColumn(columnName, dataType, table));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeConnectionObjects(con, ps, rs);
        }
    }

    private Connection getDatabaseConn() throws SQLException {
        if( Objects.isNull(this.databaseConn) || this.databaseConn.isClosed() ) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

                String url = String.format("jdbc:sqlserver://%s\\dbo:%s;databaseName=%s;Encrypt=true;TrustServerCertificate=true", this.host, this.port, this.databaseName);

                this.databaseConn = DriverManager.getConnection(url, userName, password);
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return this.databaseConn;
    }
    private void closeConnectionObjects(Connection con, PreparedStatement ps, ResultSet rs) {
        if( Objects.nonNull(rs) ){
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        if( Objects.nonNull(ps) ){
            try {
                ps.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        if( Objects.nonNull(con) ) {
            try {
                con.close();
                con = null;
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Getter @Setter @AllArgsConstructor
    class DBColumn {
        private String name;
        private String type;
        private DBTable table;
    }

    class DBTable {
        @Getter
        private String name;
        private List<DBColumn> columns;

        public DBTable( String name ){
            this.name = name;
        }

        public List<DBColumn> getColumns(){
            if(Objects.isNull(this.columns) ){
                this.columns = new ArrayList<>();
            }

            return this.columns;
        }
        public void addColumn( DBColumn col ){
            getColumns().add(col);
        }

        public String getColumnsString() {
            return String.join(", ", getColumns().stream().map(DBColumn::getName).toArray(String[]::new));
        }

        public String getWhereBlockStr(String patternStr) {
            String[] orWhereBlock = getColumns().stream().map(col -> switch(col.getType()) {
                        case "nvarchar" -> String.join(" OR ",
                                String.format("( %s = '%s'", col.getName(), patternStr),
                                String.format("%s LIKE '%%%s%%'", col.getName(), patternStr),
                                String.format("%s LIKE '%s%%'", col.getName(), patternStr),
                                String.format("%s LIKE '%%%s' )", col.getName(), patternStr)
                        );
                        default -> "";
                    }
            ).filter( s -> !s.isEmpty() ).toArray(String[]::new);

            return String.join(" OR ", orWhereBlock);
        }

        public String getQueryString( String pattern ) {
            String whereBlockStr = getWhereBlockStr(pattern);
            if( whereBlockStr.isEmpty() ) {
                return "";
            } else {
                    return String.format("SELECT %s FROM %s WHERE %s",
                            getColumnsString(), getName(), whereBlockStr );
            }
        }
    }

    class DBSearchResult {
        private final String pattern;
        List<DBTable> tableList;
        List<String> queryStringList;

        public DBSearchResult(String pattern) {
            this.pattern = pattern;
        }

        public List<DBTable> getTableList() {
            if( Objects.isNull(this.tableList) ) {
                this.tableList = new ArrayList<>();
            }
            return this.tableList;
        }
        public void addColumn( DBColumn column ) {
            if( !containsTable(column.getTable().getName()) ) {
                DBTable table = new DBTable(column.getTable().getName());
                getTableList().add(table);
            }
            getTableByName(column.getTable().getName()).addColumn(column);

        }
        private boolean containsTable( String tableName ) {
            for( DBTable table : getTableList() ){
                if( table.getName().equalsIgnoreCase(tableName)){
                    return true;
                }
            }
            return false;
        }
        private DBTable getTableByName( String tableName ) {
            for( DBTable table : getTableList() ){
                if( table.getName().equalsIgnoreCase(tableName) ) {
                    return table;
                }
            }
            return null;
        }

        public List<String> getQueryStringList() {
            if( Objects.isNull(this.queryStringList) ){
                this.queryStringList = new ArrayList<>();
                for( DBTable table : getTableList() ) {
                    this.queryStringList.add(String.format("SELECT PRIMARY_KEY, %s FROM %s WHERE %s;",
                            table.getColumnsString(), table.getName(), table.getWhereBlockStr(pattern) ));
                }
            }
            return this.queryStringList;
        }
    }

    public void printTablesWithPattern() {
        String nL = System.lineSeparator();
        String dividerLine = "________________________________________________________________________";
        int totalFound = 0;
        for( DBSearchResult result : getSearchResultsList() ) {
            LOGGER.info(String.format("%s%s%sResults for [%s]:%s%s", nL, dividerLine, nL, result.pattern, nL, String.join(nL, result.getQueryStringList())));
            totalFound += result.getTableList().size();
        }
        LOGGER.info(String.format("   >>> Total tables found: %s <<<", totalFound));
    }

    public static void main( String[] args ){
        String password = "password";
        String username = "teamconnect";
        String host = "localhost";
        int port = 1433;
        String databaseName = "teamconnect_710_custom";
        String[] searchPattern = new String[]{ "YYYY" };

        StringPatternSearchInDB spsidb = new StringPatternSearchInDB( username, password, host, port, databaseName, searchPattern );
        spsidb.printTablesWithPattern();
    }
}
