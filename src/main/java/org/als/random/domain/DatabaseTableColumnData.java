package org.als.random.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Getter @Setter
@AllArgsConstructor
public class DatabaseTableColumnData<T> {
    private DatabaseTableColumn column;
    private Object value;
    private DatabaseTableRowData tableRowData;

    private static final String NAME_JSON_KEY = "name";
    private static final String VALUE_JSON_KEY = "value";

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseTableColumnData.class);

    public static DatabaseTableColumnData parse(DatabaseTableRowData tableRowData, DatabaseTableColumn column, ResultSet rs) throws SQLException {
        return new DatabaseTableColumnData(column, column.getValueFromResultSet(rs), tableRowData);
    }

    public static DatabaseTableColumnData parseFromJson(DatabaseTableRowData tableRowData, JSONObject tableColumnDataJson) throws UnsupportedEncodingException {
        DatabaseTableColumn column = tableRowData.getTableData().getTable().getDatabaseTableColumnByName(tableColumnDataJson.getString(NAME_JSON_KEY));
        DatabaseTableColumnData tableColumnData = new DatabaseTableColumnData(column, null, tableRowData);
        Object value = tableColumnData.getValueFromJson(tableColumnDataJson);
        tableColumnData.setValue(value);

        return tableColumnData;
    }

    private T getValueFromJson(JSONObject tableColumnDataJson) throws UnsupportedEncodingException {
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
            case IMAGE -> {
                String base64EncodedString = tableColumnDataJson.getString(VALUE_JSON_KEY);
                yield new String( Base64.getDecoder().decode(base64EncodedString), "UTF-8");
            }
        };

        return (T)returnVal;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put(NAME_JSON_KEY, getColumn().getName());
        if( getValue() instanceof String && ((String) getValue()).startsWith("<?") ){
            json.put(VALUE_JSON_KEY, HtmlUtils.htmlEscape(getValue().toString()));
        } else {
            json.put(VALUE_JSON_KEY, getValue());
        }

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

        try {
            if( getColumn().getColumnType().getJavaType().equals(Timestamp.class) )
                return (T) Timestamp.valueOf(getValue().toString());

            if( getColumn().getColumnType().getJavaType().equals(Long.class) )
                return (T) Long.valueOf(getValue().toString());

            return (T) getColumn().getColumnType().getJavaType().cast(getValue());
        } catch( Exception e ) {
            LOGGER.error(String.format("%s: %s", e.toString(), e.getMessage()), e);
        }
        return null;
    }

    public String getTableColumnName() {
        return String.format("%s.%s", getTableRowData().getTableData().getTable().getName(), getColumn().getName());
    }

    public String getFullName() {
        return String.format("%s.%s", getTableRowData().getTableData().getTable().getFullName(), getColumn().getName());
    }

    public String getTableNameWithSnapshotName(){
        String snapshotName = getTableRowData().getTableData().getTable().getSnapshotTableName();
        String lastIndexOfStr = "snapshot-";
        int lastIndexOf = snapshotName.lastIndexOf(lastIndexOfStr);
        String snapshotDate = snapshotName.substring(lastIndexOf+lastIndexOfStr.length());
        return String.format("%s.%s", snapshotDate.replace(".snap", ""), getColumn().getName());
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

        if(Objects.nonNull(getValue()) && Objects.nonNull(tableColumnData2.getValue())) {
            if (!getValue()/*getCastedValue()*/.equals(tableColumnData2.getValue()/*getCastedValue()*/))
                reasonMessageList.add(String.format("[Data change]:<br> %s: [%s]<br> %s: [%s]", getTableNameWithSnapshotName(), getValue(),
                        tableColumnData2.getTableNameWithSnapshotName(), tableColumnData2.getValue()));
        } else if(Objects.nonNull(getValue()) || Objects.nonNull(tableColumnData2.getValue())) {
            if( Objects.isNull(getValue()) ) {
                reasonMessageList.add(String.format("[Data change]:<br> %s: [%s]<br> %s: [%s]",
                        getTableNameWithSnapshotName(), getValue(),
                        tableColumnData2.getTableNameWithSnapshotName(), tableColumnData2.getValue()));
            }
            if( Objects.isNull(tableColumnData2.getValue()) ){
                reasonMessageList.add(String.format("[Data change]:<br> %s: [%s]<br> %s: [%s]",
                        getTableNameWithSnapshotName(), getValue(),
                        tableColumnData2.getTableNameWithSnapshotName(), tableColumnData2.getValue()));
            }
        }

        return reasonMessageList;
    }
}
