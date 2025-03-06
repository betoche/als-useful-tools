package org.als.random.helper;

import org.als.random.domain.DatabaseTable;
import org.als.random.domain.DatabaseTableColumn;
import org.als.random.enums.ColumnTypeEnum;

import java.util.Objects;
import java.util.Set;

public class SqlHelper {

    public static String getWhereClauseString( DatabaseTable table, String searchPattern ) {
        String[] orWhereBlock = table.getColumnList().stream().map(col -> switch(col.getColumnType()) {
                    case ColumnTypeEnum.NVARCHAR -> String.join(" OR ",
                            String.format("( %s = '%s'", col.getName(), searchPattern),
                            String.format("%s LIKE '%%%s%%'", col.getName(), searchPattern),
                            String.format("%s LIKE '%s%%'", col.getName(), searchPattern),
                            String.format("%s LIKE '%%%s' )", col.getName(), searchPattern)
                    );
                    case ColumnTypeEnum.INTEGER -> {
                        try {
                            int num = Integer.parseInt(searchPattern);
                            yield String.join(" OR ",
                                    String.format(" %s = %s ", col.getName(), searchPattern)
                            );
                        } catch( Exception ignored){
                        }
                        yield "";
                    }
                    default -> "";
                }
        ).filter( s -> !s.isEmpty() ).toArray(String[]::new);

        return String.join(" OR ", orWhereBlock);
    }

    public static String getQueryString( DatabaseTable table, String searchPattern ) {
        String whereBlockStr = getWhereClauseString( table, searchPattern );
        if( whereBlockStr.isEmpty() ) {
            return "";
        } else {
            return String.format("SELECT %s FROM %s WHERE %s",
                    getColumnListString(table.getColumnList()), table.getName(), whereBlockStr );
        }
    }

    public static String getColumnListString( Set<DatabaseTableColumn> columns ) {
        if(Objects.isNull(columns) || columns.isEmpty() ) {
            return "";
        }

        return String.join(", ", columns.stream().map(DatabaseTableColumn::getName).toList());
    }
}
