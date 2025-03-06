package org.als.random.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class DatabaseDifference {
    @Getter
    private DatabaseTable table1;
    @Getter
    private DatabaseTable table2;
    private List<String> reasonList;

    public static final String DATABASE_DIFFERENCE_TABLE_JSON_KEY = "table";
    public static final String DATABASE_DIFFERENCE_REASON_LIST_JSON_KEY = "reasonList";
    public static final String DATABASE_DIFFERENCE_SQL_QUERY_JSON_KEY = "sqlQuery";

    public DatabaseDifference(DatabaseTable table1) {
        this.table1 = table1;
    }

    public DatabaseDifference(DatabaseTable table1, DatabaseTable table2) {
        this.table1 = table1;
        this.table2 = table2;
    }

    public DatabaseDifference(DatabaseTable table1, DatabaseTable table2, List<String> reasonList) {
        this.table1 = table1;
        this.table2 = table2;
        this.reasonList = reasonList;
    }

    public List<String> getReasonList(){
        if( Objects.isNull(reasonList) ){
            reasonList = new ArrayList<>();
        }
        return reasonList;
    }

    public void addReasonMessage( String reasonMessage ) {
        if(Objects.isNull(reasonList) ){
            reasonList = new ArrayList<>();
        }

        reasonList.add(reasonMessage);
    }
    public void addReasonMessage( List<String> reasonMessageList ) {
        if(Objects.isNull(reasonList) ){
            reasonList = new ArrayList<>();
        }

        reasonList.addAll(reasonMessageList);
    }

    public String getSqlQuery() {
        return String.format("SELECT * FROM %s;", table1.getName());
    }

    public String toString() {
        List<String> output = new ArrayList<>();
        output.add(String.format("Differences for \"%s\": {", table1.getFullName()));
        output.addAll(getReasonList().stream().map(r -> String.format("- %s", r)).toList());
        output.add("}");
        return String.join(System.lineSeparator(), output);
    }


    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        json.put(DATABASE_DIFFERENCE_TABLE_JSON_KEY, getTable1().getName());
        json.put(DATABASE_DIFFERENCE_SQL_QUERY_JSON_KEY, getSqlQuery());
        for( String reason : getReasonList() ) {
            jsonArray.put(reason);
        }
        json.put(DATABASE_DIFFERENCE_REASON_LIST_JSON_KEY, jsonArray);

        return json;
    }
}
