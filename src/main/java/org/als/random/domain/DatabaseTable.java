package org.als.random.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.als.random.utils.DBConnectionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


@Builder
@AllArgsConstructor
public class DatabaseTable {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private int numberOfRecords;
    @Getter @Setter
    private double lastPrimaryKey;
    private DatabaseTableData data;
    @Getter @Setter
    private Database database;
    @Getter @Setter
    private Set<DatabaseTableColumn> columnList;
    private Boolean containsPrimaryKeyColumn;

    public static final String NAME_JSON_KEY = "name";
    public static final String CONTAINS_PRIMARY_KEY_COLUMN = "hasPrimaryKeyColumn";
    public static final String NUMBER_OF_RECORDS_JSON_KEY = "numberOfRecords";
    public static final String LAST_PRIMARY_KEY_JSON_KEY = "lastPrimaryKey";
    public static final String DATA_JSON_KEY = "data";
    public static final String COLUMNS_JSON_KEY = "columns";
    public static final String TABLE_JSON_KEY = "table";
    public static final String ENTITY_CLASS_DEFINITION_JSON_KEY = "entityTableDefinition";

    public static final Logger LOGGER = LoggerFactory.getLogger(DatabaseTable.class);

    public DatabaseTable(String name) {
        this.name = name;
    }
    public static DatabaseTable parseFromJson(JSONObject json, Database db) {
        DatabaseTableBuilder builder = DatabaseTable.builder();
        JSONObject tableJson = json.getJSONObject(TABLE_JSON_KEY);
        builder.name(tableJson.getString(NAME_JSON_KEY));
        try {
            builder.containsPrimaryKeyColumn(tableJson.getBoolean(CONTAINS_PRIMARY_KEY_COLUMN));
        } catch( Exception e ) {
            LOGGER.error( "%s: %s".formatted(e.toString(), e.getMessage()), e );
        }

        builder.lastPrimaryKey(tableJson.getInt(LAST_PRIMARY_KEY_JSON_KEY));
        builder.numberOfRecords(tableJson.getInt(NUMBER_OF_RECORDS_JSON_KEY));
        builder.database(db);
        DatabaseTable table = builder.build();

        JSONArray jsonArray = tableJson.getJSONArray(COLUMNS_JSON_KEY);
        Set<DatabaseTableColumn> columns = new HashSet<>();
        for( int i = 0 ; i < jsonArray.length() ; i++ ) {
            JSONObject columnJson = jsonArray.getJSONObject(i);
            columns.add(DatabaseTableColumn.parseFromJson(columnJson, table));
        }
        table.setColumnList(columns);

        if( tableJson.has(DATA_JSON_KEY) ) {
            try {
                JSONObject data = tableJson.getJSONObject(DATA_JSON_KEY);
                table.setTableData(DatabaseTableData.parseFromJsonArray(table, data));
            }catch( Exception e ) {
                e.printStackTrace();
            }
        }

        return table;
    }

    public boolean doesContainPrimaryKeyColumn() {
        if( Objects.isNull( containsPrimaryKeyColumn ) ) {
            if( getColumnList().stream().map(DatabaseTableColumn::getName).toList().contains(DBConnectionManager.PRIMARY_KEY_TABLE_NAME) ) {
                containsPrimaryKeyColumn = Boolean.TRUE;
            } else {
                containsPrimaryKeyColumn = Boolean.FALSE;
            }
        }
        return containsPrimaryKeyColumn.booleanValue();
    }

    public DatabaseTableData getTableData() {
        if( Objects.isNull(data) ){
            data = new DatabaseTableData(this);
        }

        return data;
    }
    public void setTableData( DatabaseTableData data ) {
        this.data = data;
    }
    public void addTableRowData( DatabaseTableRowData dataRow, int resultSetHashCode  ) {
        getTableData().addTableDataRow(dataRow, resultSetHashCode);
    }

    public void addColumn( DatabaseTableColumn column ) {
        if( Objects.isNull(this.columnList) )
            this.columnList = new HashSet<>();

        this.columnList.add(column);
    }

    public boolean equals(DatabaseTable o) {
        if(Objects.isNull(o))
            return false;

        return getName().compareTo(o.getName())==0
                && getNumberOfRecords()==o.getNumberOfRecords()
                && o.compareColumns(getColumnList())
                && o.getLastPrimaryKey() == getLastPrimaryKey();
    }

    private boolean compareColumns( Set<DatabaseTableColumn> oList ){
        if( Objects.isNull(getColumnList()) && Objects.isNull(oList) )
            return true;

        if(Objects.isNull(getColumnList()))
            return false;

        if(Objects.isNull(oList))
            return false;

        if( getColumnList().size() != oList.size() )
            return false;

        boolean isEqual = false;

        for( DatabaseTableColumn col1 : oList ) {
            isEqual = false;

            for( DatabaseTableColumn col2 : getColumnList() ) {
                if( col1.equals(col2) ) {
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
        JSONArray columnArray = new JSONArray();

        json.put(CONTAINS_PRIMARY_KEY_COLUMN, doesContainPrimaryKeyColumn() );
        json.put(NAME_JSON_KEY, getName());
        json.put(NUMBER_OF_RECORDS_JSON_KEY, getNumberOfRecords());
        json.put(LAST_PRIMARY_KEY_JSON_KEY, getLastPrimaryKey());
        json.put(ENTITY_CLASS_DEFINITION_JSON_KEY, new EntityCodeGenerator(this).getEntityCodeHtml());

        if( Objects.nonNull(getColumnList()) ) {
            for (DatabaseTableColumn column : getColumnList()) {
                columnArray.put(column.toJson());
            }
        }
        json.put(COLUMNS_JSON_KEY, columnArray);

        if(getTableData().hasData()) {
            json.put(DATA_JSON_KEY, getTableData().toJson());
        }

        root.put(TABLE_JSON_KEY, json);

        return root;
    }

    public String getFullName() {
        return String.format("%s.%s", getDatabase().getSnapshotFileName(), getName());
    }
    public String getSnapshotTableName(){
        int indexOf = getDatabase().getSnapshotFileName().lastIndexOf("/") + 1;
        return String.format("%s.%s", getDatabase().getSnapshotFileName().substring(indexOf), getName());
    }
    public String getFullPath(){
        return String.format("%s.%s.%s", getSnapshotTableName(), getDatabase().getName(), getName());
    }

    public DatabaseTableColumn getDatabaseTableColumnByName(String columnName) {
        for( DatabaseTableColumn tableColumn : getColumnList() ) {
            if( tableColumn.getName().equalsIgnoreCase(columnName) ) {
                return tableColumn;
            }
        }

        return null;
    }

    public static class TableSortByRecordsCount implements Comparator<DatabaseTable> {
        public int compare(DatabaseTable a, DatabaseTable b) {
            return b.getNumberOfRecords() - a.getNumberOfRecords();
        }
    }
    public static class TableSortByTableName implements Comparator<DatabaseTable> {
        public int compare(DatabaseTable a, DatabaseTable b) {
            return a.getName().compareTo(b.getName());
        }
    }
    public static class TableSortByColumnCount implements Comparator<DatabaseTable> {
        public int compare(DatabaseTable a, DatabaseTable b) {
            return b.getColumnList().size() - a.getColumnList().size();
        }
    }
}
