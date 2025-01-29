package org.als.random.domain;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class DatabaseTableData {
    private static final String ROWS_JSON_KEY = "rows";
    private static final String PRIMARY_KEY_COLUMN_NAME = "PRIMARY_KEY";
    private Map<Integer, DatabaseTableRowData> rowDataMap;
    @Getter
    private DatabaseTable table;

    public DatabaseTableData( DatabaseTable table ) {
        this.table = table;
    }

    public static DatabaseTableData parseFromJsonArray(DatabaseTable table, JSONObject json) throws UnsupportedEncodingException {
        JSONArray jsonArray = json.getJSONArray(ROWS_JSON_KEY);
        DatabaseTableData tableData = new DatabaseTableData(table);
        for( int i = 0 ; i < jsonArray.length() ; i++ ) {
            JSONObject tableRowData = jsonArray.getJSONObject(i);
            tableData.addTableDataRow(DatabaseTableRowData.parseFromJson(tableData, tableRowData));
        }

        return tableData;
    }


    public Map<Integer, DatabaseTableRowData> getTableRowDataMap() {
        if( Objects.isNull(rowDataMap) ) {
            rowDataMap = new TreeMap<>();
        }

        return rowDataMap;
    }

    public void addTableDataRow( DatabaseTableRowData tableRowData ) {
        DatabaseTableColumnData datum = tableRowData.getTableColumnDataByColumnName(PRIMARY_KEY_COLUMN_NAME);
        Integer primaryKey = 0;
        if( Objects.nonNull(datum) ) {
            if( Objects.nonNull(datum.getValue()) )
                primaryKey = (Integer)datum.getValue();
        }
        try {
            getTableRowDataMap().put(primaryKey, tableRowData);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public DatabaseTableRowData getTableRowDataByPrimaryKey(Integer primaryKey ) {
        return getTableRowDataMap().get(primaryKey);
    }

    public boolean hasData() {
        return !getTableRowDataMap().isEmpty();
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray rowArray = new JSONArray();

        getTableRowDataMap().forEach((key, value) -> {
            rowArray.put(value.toJson());
        });

        json.put(ROWS_JSON_KEY, rowArray);

        return json;
    }

    public List<String> getDataDifferenceList(DatabaseTableData tableData2) {
        List<String> reasonMessageList = new ArrayList<>();
        getTableRowDataMap().forEach((primaryKey1, tableRowData1) -> {
            DatabaseTableRowData tableRowData2 = tableData2.getTableRowDataByPrimaryKey(primaryKey1);
            if( Objects.nonNull(tableRowData2) ) {
                reasonMessageList.addAll(tableRowData1.getDataDifferenceList(tableRowData2));
            } else {
                reasonMessageList.add(String.format("[Missing Record]: { PRIMARY_KEY: %s, PRIMARY_KEY: N/A }", primaryKey1));
            }
        });

        return reasonMessageList;
    }
}
