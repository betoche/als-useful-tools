package org.als.random.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.als.random.enums.DatabaseTypeEnum;
import org.als.random.enums.RandomConstants;
import org.als.random.helper.FileDirHelper;
import org.als.random.utils.DBSnapshot;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter @Setter
@Builder
public class Database {
    private String name;
    private String username;
    private String password;
    private String dbHost;
    private int dbPort;
    private int totalRecordsCount;
    private List<DatabaseTable> tableList;
    private String snapshotFileName;
    private DatabaseTypeEnum databaseTypeEnum;

    public static final String USERNAME_JSON_KEY = "username";
    public static final String PASSWORD_JSON_KEY = "password";
    public static final String HOST_JSON_KEY = "host";
    public static final String PORT_JSON_KEY = "port";
    public static final String NAME_JSON_KEY = "name";
    public static final String TABLES_JSON_KEY = "tables";
    public static final String RECORDS_COUNT_JSON_KEY = "recordsCount";

    private final static Logger LOGGER = LoggerFactory.getLogger(Database.class);

    public static Database parseFromJson(JSONObject databaseJson) {
        DatabaseBuilder dbBuilder = Database.builder();

        try { dbBuilder.name(databaseJson.getString(NAME_JSON_KEY)); } catch( JSONException ex ) {}
        try { dbBuilder.password(databaseJson.getString(PASSWORD_JSON_KEY)); } catch( JSONException ex ) {}
        try { dbBuilder.username(databaseJson.getString(USERNAME_JSON_KEY)); } catch( JSONException ex ) {}
        try { dbBuilder.dbHost(databaseJson.getString(HOST_JSON_KEY)); } catch( JSONException ex ) {}
        try { dbBuilder.dbPort(databaseJson.getInt(PORT_JSON_KEY)); } catch( JSONException ex ) {}
        try { dbBuilder.totalRecordsCount(databaseJson.getInt(RECORDS_COUNT_JSON_KEY)); } catch( JSONException ex ) {}

        Database db = dbBuilder.build();
        List<DatabaseTable> tableList = new ArrayList<>();

        JSONArray jsonArray = databaseJson.getJSONArray(TABLES_JSON_KEY);

        for( int i = 0 ; i < jsonArray.length() ; i++ ){
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            tableList.add(DatabaseTable.parseFromJson(jsonObj, db));
        }

        tableList.sort(new DatabaseTable.TableSortByTableName());
        db.setTableList(tableList);

        return db;
    }

    public static Database parseFromJsonFile(String snapshotFileName) throws IOException {
        String jsonStr = "";
        try{
            jsonStr = new String(Files.readAllBytes(Paths.get( snapshotFileName )));
        }catch( Exception e ){
            jsonStr = new String(Files.readAllBytes(Paths.get(String.format("%s/%s",RandomConstants.SNAPSHOT_STORAGE_DIRECTORY, snapshotFileName))));
        }

        JSONObject jsonDB = new JSONObject(jsonStr);

        Database db = parseFromJson(jsonDB.getJSONObject(DatabaseSnapshot.SNAPSHOT_KEY_NAME).getJSONObject(DatabaseSnapshot.DATABASE_JSON_KEY_NAME));
        db.setSnapshotFileName(snapshotFileName);

        return db;
    }

    public boolean equals(Database o) {
        if(Objects.isNull(o))
            return false;

        return getName().compareTo(o.getName())==0 && o.compareTables(getTableList()) && getRecordsCount()==o.getRecordsCount();
    }

    private boolean compareTables( List<DatabaseTable> oTables ) {
        if(Objects.isNull(oTables) && Objects.isNull(getTableList()))
            return true;

        if(Objects.isNull(oTables))
            return false;

        if(Objects.isNull(getTableList()))
            return false;

        boolean isEqual = false;

        for( DatabaseTable table1 : oTables ) {
            isEqual = false;

            for( DatabaseTable table2 : getTableList() ) {
                if(table1.equals(table2)) {
                    isEqual = true;
                    break;
                }
            }

            if(!isEqual)
                break;
        }

        return isEqual;
    }

    private int getRecordsCount(){
        int total = 0;

        for( DatabaseTable table : getTableList() ){
            total+=table.getNumberOfRecords();
        }

        return total;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray tableArray = new JSONArray();
        if( Objects.nonNull(getTableList()) ) {
            for (DatabaseTable table : getTableList()) {
                tableArray.put(table.toJson());
            }
        }

        json.put(USERNAME_JSON_KEY, getUsername());
        json.put(PASSWORD_JSON_KEY, getPassword());
        json.put(HOST_JSON_KEY, getDbHost());
        json.put(PORT_JSON_KEY, getDbPort());
        json.put(NAME_JSON_KEY, getName());
        json.put(TABLES_JSON_KEY, tableArray);
        json.put(RECORDS_COUNT_JSON_KEY, getRecordsCount());

        return json;
    }
}
