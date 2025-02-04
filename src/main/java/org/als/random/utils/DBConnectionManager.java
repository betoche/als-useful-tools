package org.als.random.utils;

import jakarta.annotation.Nullable;
import org.als.random.domain.DatabaseTable;
import org.als.random.domain.DatabaseTableColumn;
import org.als.random.domain.DatabaseTableRowData;
import org.als.random.enums.ColumnTypeEnum;
import org.als.random.enums.DatabaseTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DBConnectionManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(DBConnectionManager.class);

    public static Connection getDatabaseConnection(DatabaseTypeEnum databaseType, String databaseName, String userName,
                                                   String password, @Nullable String host, @Nullable Integer port) {
        return switch (databaseType) {
            case DatabaseTypeEnum.SQL_SERVER -> getSQLServerConnection(databaseName, userName, password, host, port);
            case DatabaseTypeEnum.ORACLE -> getOracleDBConnection(databaseName, userName, password, host, port);
        };

    }

    public static List<DatabaseTable> getDatabaseTables(DatabaseTypeEnum databaseTypeEnum, Connection con, String databaseName) {
        List<DatabaseTable> tableList = new ArrayList<>();
        List<String> sqlIgnoredTables = Arrays.asList("QRTZ_CALENDARS","SEQUENCE","QRTZ_CRON_TRIGGERS",
                "QRTZ_FIRED_TRIGGERS", "QRTZ_PAUSED_TRIGGER_GRPS", "QRTZ_SCHEDULER_STATE", "QRTZ_LOCKS",
                "QRTZ_JOB_DETAILS", "QRTZ_SIMPLE_TRIGGERS", "QRTZ_SIMPROP_TRIGGERS", "QRTZ_BLOB_TRIGGERS",
                "QRTZ_TRIGGERS");

        String sqlQuery = switch (databaseTypeEnum) {
            case SQL_SERVER -> "";
            case ORACLE ->  "";
        };

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String tablesQuery = String.format("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                    "WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG='%s'", databaseName);
            ps = con.prepareStatement(tablesQuery);
            rs = ps.executeQuery();

            while( rs.next() ) {
                String tableName = rs.getString("TABLE_NAME");
                if(!sqlIgnoredTables.contains(tableName.toUpperCase()))
                    tableList.add(DatabaseTable.builder().name(tableName).build());
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeConnectionObjects(con, ps, rs);
        }

        return tableList;
    }

    public static void retrieveColumnListDetails(DatabaseTypeEnum databaseTypeEnum, Connection con,
                                                 List<DatabaseTable> tableList, boolean retriveData) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            for( DatabaseTable table : tableList ) {
                String tablesQuery = switch(databaseTypeEnum) {
                    case SQL_SERVER -> String.format("SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'%s';", table.getName());
                    case ORACLE -> "";
                };
                ps = con.prepareStatement(tablesQuery);
                rs = ps.executeQuery();

                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String dataTypeStr = rs.getString("DATA_TYPE");
                    ColumnTypeEnum dataType = ColumnTypeEnum.findByTypeStringName(dataTypeStr);
                    if( Objects.isNull(dataType) ) {
                        LOGGER.info(String.format("{table: %s, col: %s, type: %s}", table.getName(), columnName, dataTypeStr));

                    }

                    table.addColumn(new DatabaseTableColumn(columnName, dataType, table));
                }

                try {
                    String maxPrimaryKeyValueQuery = String.format("SELECT MAX (PRIMARY_KEY) FROM %s", table.getName());
                    ps = con.prepareStatement(maxPrimaryKeyValueQuery);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        int maxPrimaryKey = rs.getInt(1);
                        table.setLastPrimaryKey(maxPrimaryKey);
                    }
                } catch(Exception e) {
                    LOGGER.info(String.format("Error querying the table \"%s\"", table.getName()));
                }

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
                    rs = ps.executeQuery();
                    while( rs.next() ) {
                        table.addTableRowData(DatabaseTableRowData.parse(table.getTableData(), table.getColumnList(), rs));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeConnectionObjects(con, ps, rs);
        }
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
        String tmpHost = "localhost";
        int tmpPort = 1433;

        if(Objects.nonNull(host) )
            tmpHost = host;

        if(Objects.nonNull(port))
            tmpPort = port;

        return null;
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
}
