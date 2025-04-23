package org.als.random.domain;

import jakarta.annotation.Nullable;
import lombok.Getter;
import org.als.random.utils.DBConnectionManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.*;

public class DatabaseTableData {
    private static final String ROWS_JSON_KEY = "rows";
    private static final String PRIMARY_KEY_COLUMN_NAME = "PRIMARY_KEY";
    private Map<Long, DatabaseTableRowData> rowDataMap;
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
            int hashCode = tableRowData.getInt(DatabaseTableRowData.HASH_CODE_JSON_KEY);
            tableData.addTableDataRow(DatabaseTableRowData.parseFromJson(tableData, tableRowData), hashCode);
        }

        return tableData;
    }


    public Map<Long, DatabaseTableRowData> getTableRowDataMap() {
        if( Objects.isNull(rowDataMap) ) {
            rowDataMap = new TreeMap<>();
        }

        return rowDataMap;
    }

    public void addTableDataRow( DatabaseTableRowData tableRowData, @Nullable Integer resultSetHashCode ) {
        DatabaseTableColumnData datum = tableRowData.getTableColumnDataByColumnName(PRIMARY_KEY_COLUMN_NAME);
        Long primaryKey = 0l;
        if( Objects.nonNull(datum) ) {
            if( Objects.nonNull(datum.getValue()) )
                primaryKey = Long.parseLong(datum.getValue().toString());
        } else if( Objects.nonNull(resultSetHashCode) ) {
            primaryKey = Long.parseLong(resultSetHashCode.toString());
        }
        try {
            getTableRowDataMap().put(primaryKey, tableRowData);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public DatabaseTableRowData getTableRowDataByPrimaryKey(Long primaryKey ) {
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
            boolean hasPrimaryKeyColumn = getTable().getColumnList().stream().map(DatabaseTableColumn::getName).toList().contains(DBConnectionManager.PRIMARY_KEY_TABLE_NAME);

            DatabaseTableRowData tableRowData2;
            if(hasPrimaryKeyColumn) {
                tableRowData2 = tableData2.getTableRowDataByPrimaryKey(primaryKey1);
            } else {
                tableRowData2 = tableData2.getTableRowDataByPrimaryKey(primaryKey1);
            }
            if( Objects.nonNull(tableRowData2) ) {
                reasonMessageList.addAll(tableRowData1.getDataDifferenceList(tableRowData2));
            } else {
                reasonMessageList.add(String.format("[Missing Record]: { PRIMARY_KEY: %s, PRIMARY_KEY: N/A }", primaryKey1));
            }
        });

        return reasonMessageList;
    }
}
