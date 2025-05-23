package org.als.random.utils;

import jakarta.annotation.Nullable;
import org.als.random.domain.Database;
import org.als.random.domain.DatabaseTable;
import org.als.random.domain.DatabaseTableColumn;
import org.als.random.domain.DatabaseTableRowData;
import org.als.random.enums.ColumnTypeEnum;
import org.als.random.enums.DatabaseTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DBConnectionManager {
    public static final int TABLE_LIST_QUERY = 0;
    public static final int TABLE_COLUMN_LIST_QUERY = 1;
    public static final String PRIMARY_KEY_TABLE_NAME = "PRIMARY_KEY";

    private final static Logger LOGGER = LoggerFactory.getLogger(DBConnectionManager.class);

    public static List<DatabaseTable> getDatabaseTables(DatabaseTypeEnum databaseTypeEnum, Connection con, String databaseName) throws SQLException, URISyntaxException {
        List<DatabaseTable> tableList = new ArrayList<>();
        List<String> sqlIgnoredTables = Arrays.asList("QRTZ_CALENDARS", /*"SEQUENCE",*/"QRTZ_CRON_TRIGGERS",
                "QRTZ_FIRED_TRIGGERS", "QRTZ_PAUSED_TRIGGER_GRPS", "QRTZ_SCHEDULER_STATE", "QRTZ_LOCKS",
                "QRTZ_JOB_DETAILS", "QRTZ_SIMPLE_TRIGGERS", "QRTZ_SIMPROP_TRIGGERS", "QRTZ_BLOB_TRIGGERS",
                "QRTZ_TRIGGERS");
        sqlIgnoredTables = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        String tmpDatabaseName = databaseName;
        Database database = Database.builder().name(databaseName).username(con.getMetaData().getUserName()).databaseTypeEnum(databaseTypeEnum).build();
        if(databaseTypeEnum==DatabaseTypeEnum.ORACLE){
            try {
                tmpDatabaseName = con.getMetaData().getUserName();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        String tablesQuery = getSQLQueryTablesString(databaseTypeEnum, tmpDatabaseName); //String.format("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG='%s'", databaseName);
        try {

            ps = con.prepareStatement(tablesQuery);
            rs = ps.executeQuery();

            while( rs.next() ) {
                String tableName = rs.getString("TABLE_NAME");
                if(!sqlIgnoredTables.contains(tableName.toUpperCase())) {
                    DatabaseTable.DatabaseTableBuilder builder = DatabaseTable.builder().name(tableName);
                    builder.database(database);
                    tableList.add( builder.build() );
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeConnectionObjects(con, ps, rs);
        }

        return tableList;
    }

    public static String getSQLQueryTablesString( DatabaseTypeEnum databaseType, String databaseName ) {
        return getSQLQueryString(TABLE_LIST_QUERY, databaseType, databaseName,null);
    }

    public static String getSQLQueryTableColumnsString( DatabaseTypeEnum databaseType, String databaseName, String tableName ) {
        return getSQLQueryString(TABLE_COLUMN_LIST_QUERY, databaseType, databaseName, tableName);
    }

    public static String getSQLQueryString( int query, DatabaseTypeEnum databaseType, @Nullable String databaseName, @Nullable String tableName ) {
        if( query==TABLE_COLUMN_LIST_QUERY && Objects.isNull(tableName) ) {
            return "";
        }

        String sqlQuery = switch (databaseType) {
            case SQL_SERVER -> switch (query) {
                case TABLE_LIST_QUERY -> String.format("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG='%s'", databaseName);
                case TABLE_COLUMN_LIST_QUERY -> String.format("SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'%s'", tableName);
                default -> "";
            };
            case ORACLE -> switch(query) {
                case TABLE_LIST_QUERY -> String.format("SELECT TABLE_NAME FROM all_tables WHERE owner = '%s'", databaseName);
                case TABLE_COLUMN_LIST_QUERY -> String.format("SELECT COLUMN_NAME, DATA_TYPE FROM ALL_tab_cols WHERE table_name = '%s' AND OWNER = '%s'", tableName, databaseName );
                default -> "";
            };
        };

        return sqlQuery;
    }

    public static void retrieveColumnListDetails(DatabaseTypeEnum databaseTypeEnum, Connection con,
                                                 List<DatabaseTable> tableList, boolean retriveData) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            for( DatabaseTable table : tableList ) {
                String tmpDatabaseName = "";
                if( databaseTypeEnum == DatabaseTypeEnum.ORACLE ){
                    tmpDatabaseName = con.getMetaData().getUserName();
                }

                String tablesQuery = getSQLQueryTableColumnsString(databaseTypeEnum, tmpDatabaseName, table.getName()); /* switch(databaseTypeEnum) {
                    case SQL_SERVER -> String.format("SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'%s';", table.getName());
                    case ORACLE -> String.format("SELECT COLUMN_NAME, DATA_TYPE FROM ALL_tab_cols WHERE table_name = %s AND OWNER = %s;", table.getName(), table.getDatabase().getName() );
                };
                */
                ps = con.prepareStatement(tablesQuery);
                try {
                    rs = ps.executeQuery();
                } catch(Exception e){
                    e.printStackTrace();
                    throw e;
                }

                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String dataTypeStr = rs.getString("DATA_TYPE");
                    ColumnTypeEnum dataType = ColumnTypeEnum.findByTypeStringName(dataTypeStr);
                    if( Objects.isNull(dataType) ) {
                        LOGGER.info(String.format("%s {col: %s, type: %s} has unknown datatype", table.getName(), columnName, dataTypeStr));
                    }

                    table.addColumn(new DatabaseTableColumn(columnName, dataType, table));
                }

                if( table.doesContainPrimaryKeyColumn() ) {
                    try {
                        String maxPrimaryKeyValueQuery = String.format("SELECT MAX (PRIMARY_KEY) FROM %s", table.getName());
                        ps = con.prepareStatement(maxPrimaryKeyValueQuery);
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            int maxPrimaryKey = rs.getInt(1);
                            table.setLastPrimaryKey(maxPrimaryKey);
                        }
                    } catch (Exception e) {
                        LOGGER.error(String.format("Error querying the table \"%s\"", table.getName()), e);
                    }
                } else
                    table.setLastPrimaryKey(0d);

                String recordsCountQuery = String.format("SELECT COUNT(*) FROM %s", table.getName());
                ps = con.prepareStatement(recordsCountQuery);
                rs = ps.executeQuery();
                if( rs.next() ) {
                    int recordsCount = rs.getInt(1);
                    table.setNumberOfRecords(recordsCount);
                }

                if( retriveData ) {
                    String dataQuery = String.format("SELECT %s FROM %s", String.join(", ", table.getColumnList().stream().map(DatabaseTableColumn::getName).toList()), table.getName());

                    ps = con.prepareStatement(dataQuery);
                    try {
                        rs = ps.executeQuery();
                    } catch( Exception ex ){
                        LOGGER.error(ex.getMessage(), ex);
                    }

                    int idx = 0;
                    while (rs.next()) {
                        int hashCode = rs.hashCode() + idx++;
                        table.addTableRowData(DatabaseTableRowData.parse(table.getTableData(), table.getColumnList(), rs, hashCode), hashCode);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeConnectionObjects(con, ps, rs);
        }
    }

    public static Connection getDatabaseConnection(DatabaseTypeEnum databaseType, String databaseName, String userName,
                                                   String password, @Nullable String host, @Nullable Integer port) {
        return switch (databaseType) {
            case DatabaseTypeEnum.SQL_SERVER -> getSQLServerConnection(databaseName, userName, password, host, port);
            case DatabaseTypeEnum.ORACLE -> getOracleDBConnection(databaseName, userName, password, host, port);
        };
    }
    private static Connection getSQLServerConnection( String databaseName, String userName, String password,
                                                      String host, Integer port ) {
        Connection connection;
        String tmpHost = "localhost";
        int tmpPort = 1433;

        if(Objects.nonNull(host) )
            tmpHost = host;

        if(Objects.nonNull(port))
            tmpPort = port;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            String url = String.format("jdbc:sqlserver://%s\\dbo:%s;databaseName=%s;Encrypt=true;TrustServerCertificate=true", tmpHost, tmpPort, databaseName);

            connection = DriverManager.getConnection(url, userName, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
    private static Connection getOracleDBConnection( String databaseName, String userName, String password,
                                                      @Nullable String host, @Nullable Integer port ) {
        Connection connection;
        String tmpHost = "localhost";
        int tmpPort = 1433;

        if(Objects.nonNull(host) )
            tmpHost = host;

        if(Objects.nonNull(port))
            tmpPort = port;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            String url = String.format("jdbc:oracle:thin:@%s:%s:%s", tmpHost, tmpPort, databaseName);

            connection = DriverManager.getConnection(url, userName, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }


    public static void closeConnectionObjects(Connection con, PreparedStatement ps, ResultSet rs) {
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

    public static void updateSequence(String tableKey, Long currentVal, String database, String username, String password) {
        String sqlQuery = String.format("UPDATE [SEQUENCE] SET SEQ_COUNT = ? WHERE SEQ_NAME = '%s'", tableKey );
        Connection con = null;
        PreparedStatement ps = null;
        long nextVal = currentVal + 500L;

        try {
            con = DBConnectionManager.getDatabaseConnection(DatabaseTypeEnum.SQL_SERVER, database, username, password, null, null);
            ps = con.prepareStatement(sqlQuery);
            ps.setLong(1, nextVal);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnectionManager.closeConnectionObjects(con, ps, null);
        }
    }

    public static Long getLastPrimaryKeyOf( String tableName, String databaseName, String username, String password, DatabaseTypeEnum databaseTypeEnum, String dbHost, int dbPort ) throws SQLException {
        Long lastPrimaryKey = 0l;
        String sqlQuery = "SELECT TOP(1) PRIMARY_KEY FROM %s ORDER BY PRIMARY_KEY DESC FETCH FIRST 1 ROWS ONLY".formatted(tableName);
        if( databaseTypeEnum == DatabaseTypeEnum.ORACLE ) {
            sqlQuery = "SELECT PRIMARY_KEY FROM %s ORDER BY PRIMARY_KEY DESC FETCH FIRST 1 ROWS ONLY".formatted(tableName);
        }
        try(
                Connection con = DBConnectionManager.getDatabaseConnection(databaseTypeEnum, databaseName, username, password, dbHost, dbPort);
                PreparedStatement ps = con.prepareStatement(sqlQuery);
                ResultSet rs = ps.executeQuery()
        ) {
            if( rs.next() ) {
                lastPrimaryKey = rs.getLong(1);
            }
        }

        return lastPrimaryKey;
    }
    public static ResultSet getResultsOfQueryWithoutParameters( String sqlQuery, String databaseName, String username, String password, DatabaseTypeEnum databaseTypeEnum, String dbHost, int dbPort ) throws SQLException {
        try(
                Connection con = DBConnectionManager.getDatabaseConnection(databaseTypeEnum, databaseName, username, password, dbHost, dbPort);
                PreparedStatement ps = con.prepareStatement(sqlQuery)
        ) {
            return ps.executeQuery();
        }
    }

    public static int executeRecordInsert(String insertQuery, Connection con) {
        try( PreparedStatement ps = con.prepareStatement(insertQuery) ){
            return ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("%s: %s".formatted(e.toString(), e.getMessage()), e);
        }

        return 0;
    }
}
