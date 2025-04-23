package org.als.random.helper;

import org.als.random.domain.DatabaseTable;
import org.als.random.domain.DatabaseTableColumn;
import org.als.random.enums.ColumnTypeEnum;
import org.als.random.enums.DatabaseTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

public class SqlHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlHelper.class);

    public static String getWhereClauseString( DatabaseTable table, String searchPattern ) {
        if( Objects.isNull(table.getColumnList()) ){
            LOGGER.error(String.format("Table[%s] has no column list.", table.getName()));
            return "";
        }
        String[] orWhereBlock = table.getColumnList().stream().map(col -> switch(col.getColumnType()) {
                    case ColumnTypeEnum.NVARCHAR -> String.join(" OR ",
                            String.format("( %s = '%s'", col.getName(), searchPattern),
                            String.format("%s LIKE '%%%s%%'", col.getName(), searchPattern),
                            String.format("%s LIKE '%s%%'", col.getName(), searchPattern),
                            String.format("%s LIKE '%%%s' )", col.getName(), searchPattern)
                    );
                    case ColumnTypeEnum.NVARCHAR2 -> String.join(" OR ",
                            String.format("( %s = '%s'", col.getName(), searchPattern),
                            String.format("%s LIKE '%%%s%%'", col.getName(), searchPattern),
                            String.format("%s LIKE '%s%%'", col.getName(), searchPattern),
                            String.format("%s LIKE '%%%s' )", col.getName(), searchPattern)
                    );
                    case ColumnTypeEnum.VARCHAR2 -> String.join(" OR ",
                            String.format("( %s = '%s'", col.getName(), searchPattern),
                            String.format("%s LIKE '%%%s%%'", col.getName(), searchPattern),
                            String.format("%s LIKE '%s%%'", col.getName(), searchPattern),
                            String.format("%s LIKE '%%%s' )", col.getName(), searchPattern)
                    );
                    case ColumnTypeEnum.NUMBER -> {
                        try {
                            int num = Integer.parseInt(searchPattern);
                            yield String.join(" OR ",
                                    String.format(" %s = %s ", col.getName(), searchPattern)
                            );
                        } catch( Exception ignored){
                        }
                        yield "";
                    }
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

        boolean isOracle = false;
        try {
            isOracle = ((DatabaseTableColumn) columns.toArray()[0]).getTable().getDatabase().getDatabaseTypeEnum()== DatabaseTypeEnum.ORACLE;
        } catch( Exception e ){
            LOGGER.error(String.format("%s: %s", e.toString(), e.getMessage()), e);
        }
        if( !isOracle ) {
            return String.join(", ", columns.stream().map(DatabaseTableColumn::getScapedColumnName).toList());
        } else {
            return String.join(", ", columns.stream().map(DatabaseTableColumn::getName).toList());
        }
    }
}
