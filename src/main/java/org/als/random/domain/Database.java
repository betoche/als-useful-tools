package org.als.random.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.als.random.utils.DBSnapshot;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private List<DatabaseTable> tableList;
    private String snapshotFileName;

    public static final String NAME_JSON_KEY = "name";
    public static final String TABLES_JSON_KEY = "tables";
    public static final String DATABASE_JSON_KEY = "database";

    public static Database parseFromJson(JSONObject json) {
        DatabaseBuilder dbBuilder = Database.builder();
        JSONObject databaseJson = json.getJSONObject(DATABASE_JSON_KEY);
        dbBuilder.name(databaseJson.get(NAME_JSON_KEY).toString());
        Database db = dbBuilder.build();
        List<DatabaseTable> tableList = new ArrayList<>();

        JSONArray jsonArray = databaseJson.getJSONArray(TABLES_JSON_KEY);

        for( int i = 0 ; i < jsonArray.length() ; i++ ){
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            tableList.add(DatabaseTable.parseFromJson(jsonObj, db));
        }

        db.setTableList(tableList);

        return db;
    }

    public static Database parseFromJsonFile(String snapshotFileName) throws IOException {
        String jsonStr = new String(Files.readAllBytes(Paths.get(String.format("%s/%s", DBSnapshot.SNAPSHOTS_DIR_STR, snapshotFileName))));
        JSONObject jsonDB = new JSONObject(jsonStr);
        Database db = parseFromJson(jsonDB);
        db.setSnapshotFileName(snapshotFileName);

        return db;
    }

    public boolean equals(Database o) {
        if(Objects.isNull(o))
            return false;

        return getName().compareTo(o.getName())==0 && o.compareTables(getTableList());
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

    public JSONObject toJson() throws JSONException {
        JSONObject root = new JSONObject();
        JSONObject json = new JSONObject();
        JSONArray tableArray = new JSONArray();
        if( Objects.nonNull(getTableList()) ) {
            for (DatabaseTable table : getTableList()) {
                tableArray.put(table.toJson());
            }
        }

        json.put(NAME_JSON_KEY, getName());
        json.put(TABLES_JSON_KEY, tableArray);

        root.put(DATABASE_JSON_KEY, json);

        return root;
    }
}
