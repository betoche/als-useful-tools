package org.als.random.utils;

import org.als.random.domain.DatabaseTable;
import org.als.random.domain.DatabaseTableColumn;
import org.als.random.enums.ColumnTypeEnum;
import org.als.random.entity.DataBaseSearchResult;
import org.als.random.enums.DatabaseTypeEnum;
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
    private List<DatabaseTable> tableList;
    private List<DataBaseSearchResult> searchResultsList;
    private final DatabaseTypeEnum databaseTypeEnum;

    private final static Logger LOGGER = LoggerFactory.getLogger(StringPatternSearchInDB.class);

    public StringPatternSearchInDB( DatabaseTypeEnum databaseTypeEnum, String userName, String password, String host, int port, String databaseName, String[] searchPatterns ) {
        this.databaseTypeEnum = databaseTypeEnum;
        this.userName = userName;
        this.password = password;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.searchPatterns = searchPatterns;
    }

    public List<DataBaseSearchResult> getSearchResultsList() throws SQLException {
        if( Objects.isNull(this.searchResultsList) ) {
            this.searchResultsList = new ArrayList<>();

            for (String pattern : this.searchPatterns) {
                this.searchResultsList.add(DataBaseSearchResult.createDataBaseSearchResult(DBConnectionManager.getDatabaseConnection(databaseTypeEnum, databaseName, userName, password, null, null), getTableList(), pattern));
            }
        }
        return this.searchResultsList;
    }

    public List<DatabaseTable> getTableList() throws SQLException {
        if( Objects.isNull(this.tableList) ) {
            this.tableList = DBConnectionManager.getDatabaseTables(this.databaseTypeEnum, DBConnectionManager.getDatabaseConnection(databaseTypeEnum, databaseName, userName, password, null, null), databaseName);

            DBConnectionManager.retrieveColumnListDetails(databaseTypeEnum, DBConnectionManager.getDatabaseConnection(databaseTypeEnum, databaseName, userName, password, null, null),this.tableList, false);
        }
        return this.tableList;
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

    public void printTablesWithPattern() throws SQLException {
        String nL = System.lineSeparator();
        String dividerLine = "________________________________________________________________________";
        int totalFound = 0;
        for( DataBaseSearchResult result : getSearchResultsList() ) {
            LOGGER.info(String.format("%s%s%sResults for [%s]:%s%s", nL, dividerLine, nL, result.getPattern(), nL,
                    String.join(nL, result.getQueryStringList())
            ));
            totalFound += result.getTableList().size();
        }
        LOGGER.info(String.format("   >>> Total tables found: %s <<<", totalFound));
    }

    public static void main( String[] args ){
        String password = "password";
        String username = "teamconnect";
        String host = "localhost";
        int port = 1433;
        String databaseName = "teamconnect_636";
        String[] searchPattern = new String[]{ "3102" };


        StringPatternSearchInDB spsidb = new StringPatternSearchInDB( DatabaseTypeEnum.SQL_SERVER, username, password, host, port, databaseName, searchPattern );
        try {
            spsidb.printTablesWithPattern();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
