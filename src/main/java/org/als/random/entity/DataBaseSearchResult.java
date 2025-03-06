package org.als.random.entity;

import lombok.Data;
import org.als.random.domain.DatabaseTable;
import org.als.random.domain.DatabaseTableColumn;
import org.als.random.helper.SqlHelper;
import org.als.random.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class DataBaseSearchResult {
    private final String pattern;
    List<DatabaseTable> tableList;
    List<String> queryStringList;

    public static final Logger LOGGER = LoggerFactory.getLogger(DataBaseSearchResult.class);

    public DataBaseSearchResult(String pattern) {
        this.pattern = pattern;
    }

    public List<DatabaseTable> getTableList() {
        if( Objects.isNull(this.tableList) ) {
            this.tableList = new ArrayList<>();
        }
        return this.tableList;
    }
    public void addColumn( DatabaseTableColumn column ) {
        if( !containsTable(column.getTable().getName()) ) {
            DatabaseTable table = new DatabaseTable(column.getTable().getName());
            getTableList().add(table);
        }
        getTableByName(column.getTable().getName()).addColumn(column);

    }
    private boolean containsTable( String tableName ) {
        for( DatabaseTable table : getTableList() ){
            if( table.getName().equalsIgnoreCase(tableName)){
                return true;
            }
        }
        return false;
    }
    private DatabaseTable getTableByName( String tableName ) {
        for( DatabaseTable table : getTableList() ){
            if( table.getName().equalsIgnoreCase(tableName) ) {
                return table;
            }
        }
        return null;
    }

    public List<String> getQueryStringList() {
        if( Objects.isNull(this.queryStringList) ){
            this.queryStringList = new ArrayList<>();
            for( DatabaseTable table : getTableList() ) {
                String whereBlockStr = SqlHelper.getWhereClauseString( table, pattern );
                this.queryStringList.add(String.format("%s -> SELECT %s, * FROM %s WHERE %s;", table.getName(),
                        SqlHelper.getColumnListString(table.getColumnList()), table.getName(), whereBlockStr ));
            }
        }
        return this.queryStringList;
    }

    public static DataBaseSearchResult createDataBaseSearchResult(Connection con, List<DatabaseTable> tableList, String pattern ) {
        DataBaseSearchResult searchResult = new DataBaseSearchResult(pattern);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            for (DatabaseTable table : tableList) {
                String queryStr = "";
                try {
                    queryStr = SqlHelper.getQueryString(table, pattern);
                    if( queryStr.isEmpty() ) {
                        continue;
                    }
                    ps = con.prepareStatement(queryStr);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        for (DatabaseTableColumn col : table.getColumnList()) {
                            String colValue = switch (col.getColumnType()) {
                                case INTEGER -> String.valueOf(rs.getInt(col.getName()));
                                case NVARCHAR -> rs.getString(col.getName());
                                default -> "";
                            };

                            if (Objects.nonNull(colValue) && colValue.toLowerCase().contains(pattern.toLowerCase())) {
                                searchResult.addColumn(col);
                            }
                        }
                    }
                } catch (SQLException e) {
                    LOGGER.error(String.format("ERROR with query string: %s", queryStr));
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }catch( Exception e ){
            LOGGER.error(e.getMessage(), e);
        } finally {
            DBConnectionManager.closeConnectionObjects(con, ps, rs);
        }

        return searchResult;
    }
}
