package org.als.random.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.als.random.helper.DateHelper;
import org.als.random.helper.FileDirHelper;
import org.als.random.service.DBSnapshotService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Builder
public class DatabaseSnapshot {
    private String relativePath;
    private String snapshotFileName;
    private Date creationDate;
    private Database database;
    private Boolean hasData;
    private String title;

    public static final String SNAPSHOT_KEY_NAME = "snapshot";
    public static final String SNAPSHOT_HAS_DATA_KEY_NAME = "hasData";
    public static final String SNAPSHOT_FILE_NAME_JSON_KEY_NAME = "snapshotFileName";
    public static final String SNAPSHOT_TITLE_JSON_KEY_NAME = "snapshotTitle";
    public static final String SNAPSHOT_RELATIVE_PATH_JSON_KEY_NAME = "snapshotRelativePath";
    public static final String CREATION_DATE_JSON_KEY_NAME = "creationDate";
    public static final String DATABASE_JSON_KEY_NAME = "database";

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSnapshot.class);

    public static DatabaseSnapshot parseFromJsonFile(File snapshotFile, boolean withData) throws IOException {
        if(!FileDirHelper.isValidFile(snapshotFile))
            return  null;

        String jsonStr = new String(Files.readAllBytes(Paths.get(snapshotFile.getPath())));
        DatabaseSnapshot snapshot = null;

        try {
            DatabaseSnapshot.DatabaseSnapshotBuilder builder = DatabaseSnapshot.builder();

            JSONObject root = new JSONObject(jsonStr);
            JSONObject json = root.getJSONObject(SNAPSHOT_KEY_NAME);
            builder.snapshotFileName(json.getString(SNAPSHOT_FILE_NAME_JSON_KEY_NAME));
            builder.relativePath(json.getString(SNAPSHOT_RELATIVE_PATH_JSON_KEY_NAME));
            builder.hasData(false);
            try{
                builder.hasData(json.getBoolean(SNAPSHOT_HAS_DATA_KEY_NAME));
            }catch(Exception ignored){}
            try{
                builder.title(json.getString(SNAPSHOT_TITLE_JSON_KEY_NAME));
            }catch(Exception ignored){}

            try {
                builder.creationDate(DateHelper.parseDate(json.getString(CREATION_DATE_JSON_KEY_NAME)));
            } catch (ParseException e) {
                builder.creationDate(null);
                LOGGER.error(String.format("Error parsing the date: %s", json.getString(CREATION_DATE_JSON_KEY_NAME), e));
            }

            if(withData) {
                builder.database(Database.parseFromJson(json.getJSONObject(DATABASE_JSON_KEY_NAME)));
            }

            snapshot = builder.build();
        } catch( JSONException e ){
            LOGGER.error(String.format("ERROR parsing the snapshot file: %s", snapshotFile.getAbsolutePath()));
            LOGGER.error(String.format("%s: %s", e.toString(), e.getMessage()), e);
        }

        return snapshot;
    }

    public static DatabaseSnapshot loadFromJsonFile(String filePath, boolean withData) throws IOException {
        File jsonFile = new File(filePath);
        if(!FileDirHelper.isValidFile(jsonFile))
            return null;

        return parseFromJsonFile(jsonFile, withData);
    }

    public JSONObject toJson() {
        JSONObject root = new JSONObject();
        JSONObject json = new JSONObject();
        String fileName = getSnapshotFileName();
        if( fileName.isEmpty() )
            fileName = DBSnapshotService.generateSnapshotFileName(getDatabase());

        json.put(SNAPSHOT_FILE_NAME_JSON_KEY_NAME, fileName);
        json.put(SNAPSHOT_HAS_DATA_KEY_NAME, getHasData());
        json.put(SNAPSHOT_TITLE_JSON_KEY_NAME, getTitle());
        json.put(SNAPSHOT_RELATIVE_PATH_JSON_KEY_NAME, getRelativePath());
        json.put(CREATION_DATE_JSON_KEY_NAME, DateHelper.formatDate( getCreationDate() ));
        if(Objects.nonNull(getDatabase()) ) {
            json.put(DATABASE_JSON_KEY_NAME, getDatabase().toJson());
        }

        root.put(SNAPSHOT_KEY_NAME, json);

        return root;
    }

    public String getFormatedCreationDate(){
        return DateHelper.formatDate(getCreationDate());
    }

    public String getSimpleFileName() {
        return FileDirHelper.getSimpleFileName(new File(getSnapshotFileName()));
    }

    public int getTableCount() {
        return 0;
    }
    public int getRecordsCount() {
        return 0;
    }
    public String getDatabaseName() {
        return getDatabase().getName();
    }
}
