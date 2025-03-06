package org.als.random.domain;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseTableRowData {
    private Map<String, DatabaseTableColumnData> tableDataMap;
    public static final String COLUMNS_JSON_KEY = "columns";
    @Getter
    public DatabaseTableData tableData;

    public DatabaseTableRowData( DatabaseTableData tableData ) {
        this.tableData = tableData;
    }


    public static DatabaseTableRowData parse(DatabaseTableData tableData, Collection<DatabaseTableColumn> columnList, ResultSet rs) throws SQLException {
        DatabaseTableRowData tableRowData = new DatabaseTableRowData(tableData);
        for( DatabaseTableColumn column : columnList ){
            tableRowData.addDatabaseTableDatum(DatabaseTableColumnData.parse(tableRowData, column, rs));
        }

        return tableRowData;
    }

    public static DatabaseTableRowData parseFromJson(DatabaseTableData tableData, JSONObject json) throws UnsupportedEncodingException {
        JSONArray jsonData = json.getJSONArray(COLUMNS_JSON_KEY);
        DatabaseTableRowData tableRowData = new DatabaseTableRowData(tableData);
        for( int i = 0 ; i < jsonData.length() ; i++ ) {
            JSONObject tableColumnDataJson = jsonData.getJSONObject(i);
            tableRowData.addDatabaseTableDatum(DatabaseTableColumnData.parseFromJson(tableRowData, tableColumnDataJson));
        }

        return tableRowData;
    }


    public Map<String, DatabaseTableColumnData> getTableColumnData() {
        if(Objects.isNull(tableDataMap) ) {
            tableDataMap = new TreeMap<>();
        }
        return tableDataMap;
    }

    public void addDatabaseTableDatum( DatabaseTableColumnData datum ) {
        getTableColumnData().put(datum.getColumn().getName(), datum);
    }

    public DatabaseTableColumnData getTableColumnDataByColumnName(String columnName ){
        return getTableColumnData().get(columnName);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray columnArray = new JSONArray();
        getTableColumnData().forEach((colName, value) -> {
            columnArray.put(value.toJson());
        });
        json.put(COLUMNS_JSON_KEY, columnArray);
        return json;
    }

    public Collection<String> getDataDifferenceList(DatabaseTableRowData tableRowData2) {
        List<String> reasonMessageList = new ArrayList<>();
        getTableColumnData().forEach(( columnName1, tableColumnData1 ) -> {
            DatabaseTableColumnData tableColumnData2 = tableRowData2.getTableColumnDataByColumnName(columnName1);

            if( Objects.nonNull(tableColumnData2) ) {
                reasonMessageList.addAll(tableColumnData1.getDataDifferenceList(tableColumnData2));
            } else {
                reasonMessageList.add(String.format("[Missing Column]: { %s: %s, %s.%s: N/A }", tableColumnData1.getTableColumnName(), columnName1, tableRowData2.getFullName(), tableColumnData1.getColumnName() ));
            }
        });

        return reasonMessageList;
    }

    public String getFullName() {
        //return String.format("%s.%s", getTableData().getTable().getFullName() );
        return getTableData().getTable().getSnapshotTableName();
    }
}
