package org.als.random.domain;

import org.als.random.enums.ColumnTypeEnum;
import org.als.random.helper.StringHelper;

import java.util.List;
import java.util.Objects;

public class EntityAttributeDetails {
    private String name;
    private ColumnTypeEnum columnTypeEnum;
    private DatabaseTableColumn column;

    public EntityAttributeDetails(DatabaseTableColumn column) {
        this.column = column;
    }

    public String getName() {
        if(Objects.isNull(name)) {
            name = StringHelper.snakeToCamelCase( column.getName().toLowerCase() );
        }

        return name;
    }

    public ColumnTypeEnum getColumnTypeEnum() {
        if(Objects.isNull(columnTypeEnum) ) {
            columnTypeEnum = column.getColumnType();
        }

        return columnTypeEnum;
    }

    public List<String> getEntityAttributeDeclarationString() {
        try {
            String simpleType = getColumnTypeEnum().getJavaType().getSimpleName();
            if (column.getName().startsWith("IS_")) {
                simpleType = "Integer";
            }
            return List.of(String.format("@Column(name=\"%s\")", column.getName()), String.format("private %s %s;", simpleType, getName()));
        }catch( Exception e ){
            throw e;
        }
    }


}
