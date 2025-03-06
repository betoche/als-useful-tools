package org.als.random.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.als.random.enums.ColumnTypeEnum;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@AllArgsConstructor
public class DatabaseTableColumn {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private ColumnTypeEnum columnType;
    @Getter @Setter
    private DatabaseTable table;

    public static final String NAME_JSON_KEY = "name";
    public static final String COLUMN_TYPE_JSON_KEY = "columnType";
    public static final String COLUMN_JSON_KEY = "column";

    public boolean equals(DatabaseTableColumn o) {
        if(Objects.isNull(o))
            return false;

        return getName().compareTo(o.getName())==0 && getColumnType().equals(o.getColumnType());
    }

    public static DatabaseTableColumn parseFromJson(JSONObject json, DatabaseTable table) {
        JSONObject columnJson = json.getJSONObject(COLUMN_JSON_KEY);
        return new DatabaseTableColumn(columnJson.getString(NAME_JSON_KEY), ColumnTypeEnum.findByTypeStringName(columnJson.getString(COLUMN_TYPE_JSON_KEY)), table);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject root = new JSONObject();
        JSONObject json = new JSONObject();

        json.put(NAME_JSON_KEY, getName());
        json.put(COLUMN_TYPE_JSON_KEY, getColumnType().getStrRepresentation());

        root.put(COLUMN_JSON_KEY, json);

        return root;
    }

    public Object getValueFromResultSet(ResultSet rs) throws SQLException {
        Object value = switch(getColumnType()){
            case BIT -> rs.getBoolean(getName());
            case TINYINT -> rs.getByte(getName());
            case DOUBLE -> rs.getDouble(getName());
            case REAL -> getIntegerFromResultSet(rs);
            case INTEGER -> getIntegerFromResultSet(rs);
            case BIGINT -> rs.getLong(getName());
            case SMALLINT -> rs.getShort(getName());
            case DECIMAL -> rs.getBigDecimal(getName());
            case CHAR -> rs.getString(getName());
            case VARCHAR -> rs.getString(getName());
            case CLOB -> rs.getString(getName());
            case BLOB -> rs.getString(getName());
            case DATE -> rs.getDate(getName());
            case TIMESTAMP -> rs.getTimestamp(getName());
            case DATE_ORACLE -> rs.getDate(getName());
            case TIMESTAMP_ORACLE -> rs.getDate(getName());
            case NVARCHAR -> rs.getString(getName());
            case NTEXT -> rs.getString(getName());
            case DATETIME2 -> rs.getTimestamp(getName());
            case NCHAR -> rs.getString(getName());
            case IMAGE -> DatatypeConverter.printBase64Binary(rs.getBytes(getName()));
        };
        return value;
    }

    public Integer getIntegerFromResultSet( ResultSet rs ) throws SQLException {
        Integer result = null;
        int intVal = rs.getInt(getName());
        if( !rs.wasNull() ){
            result = intVal;
        }


        return result;
    }
}
