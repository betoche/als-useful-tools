package org.als.random.enums;

import lombok.Getter;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
public enum ColumnTypeEnum {
    BIT(1, "bit", JDBCType.BIT, Boolean.class ),
    TINYINT(2, "tinyint", JDBCType.TINYINT, Byte.class),
    DOUBLE(3, "double", JDBCType.DOUBLE, Double.class),
    REAL(4, "float", JDBCType.REAL, Float.class),
    INTEGER(5, "int", JDBCType.INTEGER, Integer.class),
    BIGINT(6, "bigint", JDBCType.BIGINT, Long.class),
    SMALLINT(7, "smallint", JDBCType.SMALLINT, Short.class),
    DECIMAL(8, "decimal", JDBCType.DECIMAL, BigInteger.class),
    CHAR(9, "char", JDBCType.CHAR, Character.class),
    VARCHAR(10, "varchar", JDBCType.VARCHAR, String.class),
    CLOB(11, "clob", JDBCType.CLOB, String.class),
    BLOB(13, "blob", JDBCType.BLOB, Serializable.class),
    DATE(14, "date", JDBCType.DATE, Date.class),
    TIMESTAMP(15, "timestamp", JDBCType.TIMESTAMP, Timestamp.class),
    DATE_ORACLE(15, "date", JDBCType.DATE, java.util.Date.class),
    TIMESTAMP_ORACLE(15, "datetime", JDBCType.TIMESTAMP, java.util.Date.class),
    NVARCHAR(16, "nvarchar", JDBCType.NVARCHAR, String.class),
    NTEXT(17, "ntext", JDBCType.LONGNVARCHAR, String.class),
    DATETIME2(18, "datetime2", JDBCType.TIMESTAMP, Timestamp.class),
    NCHAR(19, "nchar", JDBCType.NCHAR, String.class),
    IMAGE(18, "image", JDBCType.LONGVARBINARY, Byte[].class);

    private final int idx;
    private final String strRepresentation;
    private final JDBCType jdbcType;
    private final Class<?> javaType;

    private ColumnTypeEnum( int idx, String strRepresentation, JDBCType jdbcType, Class<?> javaType ) {
        this.idx = idx;
        this.strRepresentation = strRepresentation;
        this.jdbcType = jdbcType;
        this.javaType = javaType;
    }

    public boolean equals( ColumnTypeEnum o ) {
        if(Objects.isNull(o))
            return false;

        return o.getIdx()==this.getIdx();
    }

    public static ColumnTypeEnum findByTypeStringName( String typeName ) {
        for( ColumnTypeEnum type : ColumnTypeEnum.values() ) {
            if( type.getStrRepresentation().equalsIgnoreCase(typeName) ) {
                return type;
            }
        }

        return null;
    }
}
