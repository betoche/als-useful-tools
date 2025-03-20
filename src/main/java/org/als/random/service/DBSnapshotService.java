package org.als.random.service;

import org.als.random.domain.*;
import org.als.random.enums.DatabaseTypeEnum;
import org.als.random.enums.RandomConstants;
import org.als.random.helper.DateHelper;
import org.als.random.helper.FileDirHelper;
import org.als.random.utils.DBSnapshot;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class DBSnapshotService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DBSnapshotService.class);

    public static DatabaseSnapshot getSnapshotDetailsByFilePath(String filePath, boolean withData) throws IOException {
        return DatabaseSnapshot.loadFromJsonFile(filePath, withData);
    }

    public List<DatabaseSnapshotGroup> getDatabaseSnapshotGroupList() {
        List<DatabaseSnapshotGroup> databaseSnapshotGroups = new ArrayList<>();

        File storageDir = new File(RandomConstants.SNAPSHOT_STORAGE_DIRECTORY);
        if( FileDirHelper.containsSubDirectoriesOrSubFiles(storageDir) ) {
            for( File snapshotsDir : Objects.requireNonNull(storageDir.listFiles())) {
                if( FileDirHelper.isValidDirectory(snapshotsDir) ) {
                    try {
                        databaseSnapshotGroups.add(DatabaseSnapshotGroup.parse(snapshotsDir, false));
                    } catch (JSONException e ) {
                        LOGGER.error(String.format("Error parsing snapshot file: %s", snapshotsDir.getAbsolutePath()));
                        LOGGER.error(String.format("%s: %s", e.toString(), e.getMessage()), e);
                    } catch( IOException e) {
                        LOGGER.error(String.format("%s: %s", e.toString(), e.getMessage()), e);
                    }
                }
            }
        }

        return databaseSnapshotGroups;
    }

    public void storeSnapshot(DatabaseSnapshot snapshot) throws IOException {
        String fileName = generateSnapshotFileName(snapshot.getDatabase());
        File snapshotFile = getSnapshotDirectory(snapshot.getDatabase());
        String fullFilePath = String.format("%s/%s", snapshotFile.getAbsolutePath(), fileName);

        try (FileWriter fileWriter = new FileWriter(fullFilePath)) {
            snapshot.setCreationDate(Calendar.getInstance().getTime());
            snapshot.setRelativePath(fullFilePath);
            fileWriter.write(snapshot.toJson().toString(2)); // Use toString(2) for pretty printing

            LOGGER.info(String.format("Snapshot created: %s", fileName));
        } catch (IOException e) {
            LOGGER.error(String.format("Error creating snapshot: %s", e.getMessage()), e);
        }
    }

    public static String generateSnapshotFileName(Database db) {
        return String.format("%s-snapshot-%s%s", db.getName(), DateHelper.getTodaysDateStr(), RandomConstants.SNAPSHOT_FILE_EXTENSION);
    }

    public static File getSnapshotDirectory(Database db) throws IOException {
        File f = new File(RandomConstants.SNAPSHOT_STORAGE_DIRECTORY);
        File snapshotGroupDir = new File(String.format("%s/%s", f.getAbsolutePath(), db.getName()));

        if( !f.exists() )
            Files.createDirectory(Path.of(f.getAbsolutePath()));

        if( !snapshotGroupDir.exists() )
            Files.createDirectory(Path.of(snapshotGroupDir.getAbsolutePath()));

        return snapshotGroupDir;
    }

    public DatabaseSnapshot createDatabaseSnapshot(DatabaseSnapshotRequest request) {
        String databaseName = request.getName();
        String databaseUserName = request.getUsername();
        String databasePassword = request.getPassword();
        String databaseHost = request.getHost();
        String snapshotTitle = request.getTitle();
        int databasePort = request.getPort();

        boolean retrieveData = request.isRetrieveData();
        DatabaseSnapshot.DatabaseSnapshotBuilder builder = DatabaseSnapshot.builder();
        DatabaseSnapshot databaseSnapshot;

        DBSnapshot dbSnapshot = new DBSnapshot(DatabaseTypeEnum.SQL_SERVER, databaseName, databaseUserName, databasePassword,
                databaseHost, databasePort, retrieveData, snapshotTitle);

        Database database = dbSnapshot.getDatabase();
        database.setDbHost(databaseHost);
        database.setDbPort(databasePort);
        List<DatabaseTable> tableList = database.getTableList();
        //tableList.sort(new DatabaseTable.TableSortByRecordsCount());
        //tableList.sort(new DatabaseTable.TableSortByTableName());
        database.setTableList(tableList);

        builder.database(database);
        builder.snapshotFileName(generateSnapshotFileName(database));
        builder.hasData(dbSnapshot.isRetrieveData());
        builder.title(snapshotTitle);
        databaseSnapshot = builder.build();
        try {
            storeSnapshot(databaseSnapshot);
        } catch (IOException e) {
            LOGGER.error("Error creating database snapshot: {}", e.getMessage(), e);
        }

        return databaseSnapshot;
    }

    public boolean deleteSnapshotFile(String filePath) throws IOException {
        File f = new File(filePath);
        if( !FileDirHelper.isValidFile(f) )
            return false;

        Files.deleteIfExists(Path.of(f.getAbsolutePath()));

        return true;
    }
}
