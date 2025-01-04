package org.als.random.utils;

import org.als.random.domain.Database;
import org.als.random.domain.DatabaseTable;
import org.als.random.enums.DatabaseTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class DBSnapshot {
    private final String databaseName;
    private final String databaseUserName;
    private final String databasePassword;
    private final DatabaseTypeEnum databaseType;
    private final boolean retrieveData;

    private Database database;
    private Connection dbConn;

    private final static Logger LOGGER = LoggerFactory.getLogger(DBSnapshot.class);
    public static final String SNAPSHOTS_DIR_STR = "db_snapshots";

    public DBSnapshot( DatabaseTypeEnum databaseTypeEnum, String databaseName, String databaseUserName,
                       String databasePassword, boolean retrieveData ) {
        this.databaseType = databaseTypeEnum;
        this.databaseName = databaseName;
        this.databaseUserName = databaseUserName;
        this.databasePassword = databasePassword;
        this.retrieveData = retrieveData;
    }

    public Connection getDbConnection() throws SQLException {
        if(Objects.isNull(dbConn) || dbConn.isClosed() ) {
            dbConn = null;
            dbConn = DBConnectionManager.getDatabaseConnection(databaseType, databaseName, databaseUserName, databasePassword, null, null);
        }
        return dbConn;
    }

    private Database getDatabase() {
        if(Objects.isNull(this.database)) {
            this.database = Database.builder().name(databaseName).build();
            try {
                List<DatabaseTable> tableList = DBConnectionManager.getDatabaseTables(databaseType, getDbConnection(), this.databaseName);
                DBConnectionManager.retrieveColumnListDetails(databaseType, getDbConnection(), tableList, retrieveData);
                this.database.setTableList(tableList);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return database;
    }

    public void createDatabaseSnapshot() throws JSONException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        File snapshotsDir = new File(SNAPSHOTS_DIR_STR);
        if( !snapshotsDir.exists() )
            Files.createDirectory(Path.of(snapshotsDir.getAbsolutePath()));

        String snapshotName = String.format("db_snapshots/%s-snapshot-%s.snap", databaseName, sdf.format(Calendar.getInstance().getTime()) );
        JSONObject json = getDatabase().toJson();

        try (FileWriter fileWriter = new FileWriter(snapshotName)) {
            fileWriter.write(json.toString(2)); // Use toString(2) for pretty printing
            System.out.println("JSONObject written to " + snapshotName);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public void compareSnapshots( String snap1, String snap2 ) throws IOException {
        Database db1 = Database.parseFromJsonFile(snap1);
        Database db2 = Database.parseFromJsonFile(snap2);

        if( db1.equals(db2) ) {
            LOGGER.info(String.format("The snapshots %s and %s don't have changes.", snap1, snap2));
        } else {
            LOGGER.info(String.format("The snapshots %s and %s are different!.", snap1, snap2));
        }
    }

    public static void main(String[] args) {
        DatabaseTypeEnum databaseTypeEnum = DatabaseTypeEnum.SQL_SERVER;
        String databaseName = "teamconnect_637";
        String databaseUserName = "teamconnect";
        String databasePassword = "password";
        DBSnapshot dbSnapshot = new DBSnapshot(databaseTypeEnum, databaseName, databaseUserName, databasePassword,
                true);

        try {
            dbSnapshot.createDatabaseSnapshot();
            //dbSnapshot.compareSnapshots("teamconnect_637-snapshot-2025-01-03-00-05.snap", "teamconnect_637-snapshot-2025-01-03-00-10.snap");
        } catch (JSONException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
