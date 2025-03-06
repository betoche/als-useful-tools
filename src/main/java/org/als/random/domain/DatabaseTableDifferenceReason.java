package org.als.random.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseTableDifferenceReason {
    private DatabaseTable table1, table2;
    private List<DatabaseTableDifferenceReasonValue> databaseTableDifferenceReasonValues;

    public DatabaseTableDifferenceReason(DatabaseTable table1, DatabaseTable table2) {
        this.table1 = table1;
        this.table2 = table2;
    }

    public static DatabaseTableDifferenceReason parse( DatabaseTable table1, DatabaseTable table2 ) {
        DatabaseTableDifferenceReason databaseTableDifferenceReason = new DatabaseTableDifferenceReason();
        return databaseTableDifferenceReason;
    }
}
