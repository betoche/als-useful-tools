package org.als.random.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter @Setter
@AllArgsConstructor
public class DatabaseTableColumnData<T> {
    private DatabaseTableColumn column;
    private Object value;
    private DatabaseTableRowData tableRowData;

    private static final String NAME_JSON_KEY = "name";
    private static final String VALUE_JSON_KEY = "value";

    public static DatabaseTableColumnData parse(DatabaseTableRowData tableRowData, DatabaseTableColumn column, ResultSet rs) throws SQLException {
        return new DatabaseTableColumnData(column, column.getValueFromResultSet(rs), tableRowData);
    }

    public static DatabaseTableColumnData parseFromJson(DatabaseTableRowData tableRowData, JSONObject tableColumnDataJson) {
        DatabaseTableColumn column = tableRowData.getTableData().getTable().getDatabaseTableColumnByName(tableColumnDataJson.getString(NAME_JSON_KEY));
        DatabaseTableColumnData tableColumnData = new DatabaseTableColumnData(column, null, tableRowData);
        Object value = tableColumnData.getValueFromJson(tableColumnDataJson);
        tableColumnData.setValue(value);

        return tableColumnData;
    }

    private T getValueFromJson(JSONObject tableColumnDataJson) {
        if(!tableColumnDataJson.has(VALUE_JSON_KEY))
            return null;

        T returnVal = (T)switch( getColumn().getColumnType() ){
            case BIT -> tableColumnDataJson.getBoolean(VALUE_JSON_KEY);
            case TINYINT -> tableColumnDataJson.getInt(VALUE_JSON_KEY);
            case DOUBLE -> tableColumnDataJson.getDouble(VALUE_JSON_KEY);
            case REAL -> tableColumnDataJson.getInt(VALUE_JSON_KEY);
            case INTEGER -> tableColumnDataJson.getInt(VALUE_JSON_KEY);
            case BIGINT -> tableColumnDataJson.getLong(VALUE_JSON_KEY);
            case SMALLINT -> tableColumnDataJson.getInt(VALUE_JSON_KEY);
            case DECIMAL -> tableColumnDataJson.getBigDecimal(VALUE_JSON_KEY);
            case CHAR -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case VARCHAR -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case CLOB -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case BLOB -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case DATE -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case TIMESTAMP -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case DATE_ORACLE -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case TIMESTAMP_ORACLE -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case NVARCHAR -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case NTEXT -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case DATETIME2 -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case NCHAR -> tableColumnDataJson.getString(VALUE_JSON_KEY);
            case IMAGE -> DatatypeConverter.parseBase64Binary(tableColumnDataJson.getString(VALUE_JSON_KEY));
        };

        return (T)returnVal;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put(NAME_JSON_KEY, getColumn().getName());
        json.put(VALUE_JSON_KEY, getValue());

        return json;
    }

    public boolean equals(DatabaseTableColumnData o) {
        if(Objects.isNull(o) )
            return false;

        if(!getColumn().equals(o.getColumn()))
            return false;

        return getCastedValue().equals(o.getCastedValue());
    }

    public T getCastedValue(){
        if(Objects.isNull(getValue()))
            return null;

        if( getColumn().getColumnType().getJavaType().equals(Timestamp.class) )
            return (T) Timestamp.valueOf(getValue().toString());

        return (T) getColumn().getColumnType().getJavaType().cast(getValue());
    }

    public String getFullName() {
        return String.format("%s.%s", getTableRowData().getTableData().getTable().getFullName(), getColumn().getName());
    }

    public String getColumnName() {
        return column.getName();
    }

    public Collection<String> getDataDifferenceList(DatabaseTableColumnData tableColumnData2) {
        List<String> reasonMessageList = new ArrayList<>();
        if(!getColumn().equals(tableColumnData2.getColumn()) )
            reasonMessageList.add(String.format("[Column Type difference]: { %s: %s, %s: %s }", getFullName(),
                    getColumn().getColumnType(), tableColumnData2.getFullName(),
                    tableColumnData2.getColumn().getColumnType() ));

        if(Objects.nonNull(getCastedValue()) && Objects.nonNull(tableColumnData2.getCastedValue())) {
            if (!getCastedValue().equals(tableColumnData2.getCastedValue()))
                reasonMessageList.add(String.format("[Data change]: { %s: %s, %s: %s }", getFullName(), getValue(),
                        tableColumnData2.getFullName(), tableColumnData2.getValue()));
        }

        return reasonMessageList;
    }
}
