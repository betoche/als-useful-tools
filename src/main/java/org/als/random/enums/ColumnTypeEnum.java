package org.als.random.enums;

import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
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
    INTEGER(5, "int", JDBCType.INTEGER, Long.class),
    BIGINT(6, "bigint", JDBCType.BIGINT, Long.class),
    SMALLINT(7, "smallint", JDBCType.SMALLINT, Short.class),
    DECIMAL(8, "decimal", JDBCType.DECIMAL, BigDecimal.class),
    CHAR(9, "char", JDBCType.CHAR, Character.class),
    VARCHAR(10, "varchar", JDBCType.VARCHAR, String.class),
    CLOB(11, "clob", JDBCType.CLOB, String.class),
    BLOB(12, "blob", JDBCType.BLOB, Serializable.class),
    DATE(13, "date", JDBCType.DATE, Date.class),
    TIMESTAMP(14, "timestamp", JDBCType.TIMESTAMP, Timestamp.class),
    DATE_ORACLE(15, "date", JDBCType.DATE, java.util.Date.class),
    TIMESTAMP_ORACLE(16, "datetime", JDBCType.TIMESTAMP, java.util.Date.class),
    NVARCHAR(17, "nvarchar", JDBCType.NVARCHAR, String.class),
    NTEXT(18, "ntext", JDBCType.LONGNVARCHAR, String.class),
    DATETIME2(19, "datetime2", JDBCType.TIMESTAMP, Timestamp.class),
    NCHAR(120, "nchar", JDBCType.NCHAR, String.class),
    IMAGE(21, "image", JDBCType.LONGVARBINARY, Byte[].class),
    NUMBER(22, "number", JDBCType.INTEGER, Long.class),
    NVARCHAR2(23, "nvarchar2", JDBCType.NVARCHAR, String.class),
    VARCHAR2(24, "nvarchar2", JDBCType.VARCHAR, String.class),
    ROWID(25, "rowid", JDBCType.ROWID, String.class),
    NUMERIC(26, "numeric", JDBCType.NUMERIC, BigDecimal.class);

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
        if("VARCHAR2".equalsIgnoreCase(typeName)){
            return VARCHAR2;
        }

        for( ColumnTypeEnum type : ColumnTypeEnum.values() ) {
            if( type.getStrRepresentation().equalsIgnoreCase(typeName) ) {
                return type;
            }
        }

        return null;
    }
}
