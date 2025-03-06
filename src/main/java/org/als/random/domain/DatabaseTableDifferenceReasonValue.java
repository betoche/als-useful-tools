package org.als.random.domain;

import lombok.Data;

@Data
public class DatabaseTableDifferenceReasonValue {
    private DatabaseTable table1, table2;
    private String reasonTitle;
    private Object value1, value2;
}
