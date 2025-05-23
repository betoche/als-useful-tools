package org.als.random.utils;

import lombok.Getter;
import org.als.random.domain.Database;
import org.als.random.domain.DatabaseTable;
import org.als.random.enums.DatabaseTypeEnum;
import org.als.random.enums.RandomConstants;
import org.als.random.helper.DateHelper;
import org.als.random.service.DBSnapshotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
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
    private final String databaseHost;
    private final int databasePort;
    private final String title;
    private final DatabaseTypeEnum databaseType;
    @Getter
    private final boolean retrieveData;

    private Database database;
    private Connection dbConn;

    private final static Logger LOGGER = LoggerFactory.getLogger(DBSnapshot.class);

    public DBSnapshot( DatabaseTypeEnum databaseTypeEnum, String databaseName, String databaseUserName,
                       String databasePassword, String host, int port, boolean retrieveData, String title ) {
        this.databaseType = databaseTypeEnum;
        this.databaseName = databaseName;
        this.databaseUserName = databaseUserName;
        this.databasePassword = databasePassword;
        this.retrieveData = retrieveData;
        this.databaseHost = host;
        this.databasePort = port;
        this.title = title;
    }

    public Connection getDbConnection() throws SQLException {
        if(Objects.isNull(dbConn) || dbConn.isClosed() ) {
            dbConn = null;
            dbConn = DBConnectionManager.getDatabaseConnection(databaseType, databaseName, databaseUserName, databasePassword, databaseHost, databasePort);
        }
        return dbConn;
    }

    public Database getDatabase() throws URISyntaxException {
        if(Objects.isNull(this.database)) {
            this.database = Database.builder().name(databaseName).build();
            this.database.setUsername(this.databaseUserName);
            this.database.setPassword(this.databasePassword);
            this.database.setName(this.databaseName);
            this.database.setDatabaseTypeEnum(databaseType);
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

    public void createDatabaseSnapshot() throws JSONException, IOException, URISyntaxException {
        File snapshotsDir = new File(RandomConstants.SNAPSHOT_STORAGE_DIRECTORY);
        if( !snapshotsDir.exists() )
            Files.createDirectory(Path.of(snapshotsDir.getAbsolutePath()));

        String snapshotName = String.format("db_snapshots/%s", DBSnapshotService.generateSnapshotFileName(database));
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
        String databaseHost = "localhost";
        int databasePort = 1433;
        DBSnapshot dbSnapshot = new DBSnapshot(databaseTypeEnum, databaseName, databaseUserName, databasePassword,
                databaseHost, databasePort, true, "Test Snapshot from DBSnapshot.main method");

        try {
            dbSnapshot.createDatabaseSnapshot();
            //dbSnapshot.compareSnapshots("teamconnect_637-snapshot-2025-01-03-00-05.snap", "teamconnect_637-snapshot-2025-01-03-00-10.snap");
        } catch (JSONException | IOException | URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
