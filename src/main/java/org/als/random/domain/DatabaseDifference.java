package org.als.random.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class DatabaseDifference {
    @Getter
    private DatabaseTable table;
    private List<String> reasonList;
    private String sqlQuery;

    public DatabaseDifference(DatabaseTable table) {
        this.table = table;
    }

    public DatabaseDifference(DatabaseTable table, List<String> reasonList) {
        this.table = table;
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
        return String.format("SELECT * FROM %s;", table.getName());
    }

    public String toString() {
        List<String> output = new ArrayList<>();
        output.add(String.format("Differences for \"%s\": {", table.getFullName()));
        output.addAll(getReasonList().stream().map(r -> String.format("- %s", r)).toList());
        output.add("}");
        return String.join(System.lineSeparator(), output);
    }


}
