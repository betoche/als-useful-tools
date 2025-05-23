package org.als.random.domain;

import lombok.Getter;
import org.als.random.enums.DatabaseTypeEnum;
import org.als.random.helper.FileDirHelper;
import org.als.random.helper.StringHelper;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseComparatorResults {
    @Getter
    private final List<Database> databaseList;

    private Map<String, DatabaseDifference> databaseDifferenceMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseComparatorResults.class);

    private Map<String, List<DatabaseTableRowData>> newRecordsMap;
    private Map<String, List<DatabaseTableRowData>> updatedRecordsMap;

    public Map<String, List<DatabaseTableRowData>> getUpdatedRecordsMap() {
        if( Objects.isNull(updatedRecordsMap) ) {
            updatedRecordsMap = new HashMap<>();

            populateUpdatedRecordsMap(updatedRecordsMap);
        }
        return updatedRecordsMap;
    }

    public Map<String, List<DatabaseTableRowData>> getNewRecordsMap() {
        if( Objects.isNull(newRecordsMap) ) {
            newRecordsMap = new HashMap<>();

            populateNewRecordsMap(newRecordsMap);
        }

        return newRecordsMap;
    }

    private void populateUpdatedRecordsMap(Map<String, List<DatabaseTableRowData>> updatedRecordsMap) {
        Database db1 = databaseList.get(0);
        Database db2 = databaseList.get(1);

        String snapDateStr = "2025-04-29-08-49-25.snap";

        String db1DateStr = StringHelper.getDateStringFromSnapshotFile(db1.getSnapshotFileName());
        String db2DateStr = StringHelper.getDateStringFromSnapshotFile(db2.getSnapshotFileName());

        long db1DateInt = Long.parseLong(db1DateStr.replaceAll("-", ""));
        long db2DateInt = Long.parseLong(db2DateStr.replaceAll("-", ""));

        Database oldDatabase = db1;
        Database newDatabase = db2;

        if( db2DateInt < db1DateInt ) {
            oldDatabase = db2;
            newDatabase = db1;
        }

        for( DatabaseTable table1 : oldDatabase.getTableList() ) {
            if ( !table1.getName().contains("DR$") ) {
                DatabaseTable table2 = newDatabase.getDatabaseTableByName(table1.getName());

                if (Objects.nonNull(table2)) {
                    List<DatabaseTableRowData> tmpUpdatedRecordList = new ArrayList<>();

                    for( Map.Entry<Long, DatabaseTableRowData> entry : table1.getTableData().getTableRowDataMap().entrySet() ) {
                        DatabaseTableRowData rowData1 = entry.getValue();
                        DatabaseTableRowData rowData2;
                        if (table1.doesContainPrimaryKeyColumn()) {
                            rowData2 = table2.getTableData().getTableRowDataByPrimaryKey(entry.getKey());
                        } else {
                            rowData2 = table2.getTableData().getTableRowDataByOtherFields(rowData1);
                        }

                        if (Objects.nonNull(rowData2)) {
                            if( !rowData1.equals(rowData2) ) {
                                tmpUpdatedRecordList.add(rowData1);
                                tmpUpdatedRecordList.add(rowData2);
                            }
                        }
                    }

                    if (!tmpUpdatedRecordList.isEmpty()) {
                        updatedRecordsMap.put(table1.getName(), tmpUpdatedRecordList);
                    }
                }
            }
        }
    }

    public void populateNewRecordsMap(Map<String, List<DatabaseTableRowData>> newRecordsMap) {
        Database db1 = databaseList.get(0);
        Database db2 = databaseList.get(1);
        for( DatabaseTable table1 : db1.getTableList() ) {
            DatabaseTable table2 = db2.getDatabaseTableByName(table1.getName());

            if( Objects.nonNull( table2 ) && table1.getNumberOfRecords()!=table2.getNumberOfRecords() ) {
                if ( !table1.getName().contains("INTERMEDIA14") ) {
                    long recordsCount1 = table1.getNumberOfRecords();
                    long recordsCount2 = table2.getNumberOfRecords();

                    List<DatabaseTableRowData> tmpNewRecordList = new ArrayList<>();
                    if ( recordsCount1==0 || recordsCount2 == 0 ) {
                        if( recordsCount1==0 && recordsCount2 > 0 ) {
                            tmpNewRecordList.addAll(table2.getTableData().getTableRowDataMap().values());
                        } else if( recordsCount1>0 && recordsCount2 == 0 ) {
                            tmpNewRecordList.addAll(table1.getTableData().getTableRowDataMap().values());
                        }
                    } else {
                        DatabaseTableData oldTableData = table1.getTableData();
                        DatabaseTableData newTableData = table2.getTableData();
                        if (recordsCount1 > recordsCount2) {
                            newTableData = table1.getTableData();
                            oldTableData = table2.getTableData();
                        }

                        for (Map.Entry<Long, DatabaseTableRowData> entry : newTableData.getTableRowDataMap().entrySet()) {
                            DatabaseTableRowData rowData1 = entry.getValue();
                            DatabaseTableRowData rowData2;
                            if (table1.doesContainPrimaryKeyColumn()) {
                                rowData2 = oldTableData.getTableRowDataByPrimaryKey(entry.getKey());
                            } else {
                                rowData2 = oldTableData.getTableRowDataByOtherFields(rowData1);
                            }

                            if (Objects.isNull(rowData2)) {
                                tmpNewRecordList.add(rowData1);
                            }
                        }
                    }

                    if (!tmpNewRecordList.isEmpty()) {
                        newRecordsMap.put(table1.getName(), tmpNewRecordList);
                    }
                }
            }
        }
    }

    public DatabaseComparatorResults(List<String> snapshotList) throws IOException {
        databaseList = new ArrayList<>();
        for( String snapshotName : snapshotList ) {
            databaseList.add(Database.parseFromJsonFile(snapshotName));
        }
    }

    public Map<String, DatabaseDifference> getDatabaseDifferenceList() {
        if( Objects.isNull(databaseDifferenceMap) ) {
            databaseDifferenceMap = new HashMap<>();
            findDatabaseDifferences(databaseDifferenceMap);
        }
        return databaseDifferenceMap;
    }

    public JSONArray getDatabaseDifferenceListToJson() {
        JSONArray jsonArray = new JSONArray();

        for( Map.Entry<String, DatabaseDifference> entry : getDatabaseDifferenceList().entrySet() ) {
            jsonArray.put(entry.getValue().toJsonObject());
        }

        return jsonArray;
    }

    private void findDatabaseDifferences(Map<String, DatabaseDifference> databaseDifferenceMap) {
        Set<String> dbSet = new HashSet<>();
        for( int i = 0 ; i < getDatabaseList().size() ; i++ ) {
            Database db1 = getDatabaseList().get(i);
            for( int j = 0 ; j < getDatabaseList().size() ; j++ ) {
                Database db2 = getDatabaseList().get(j);
                if( !db1.equals(db2) && !dbSet.contains(String.format("%s_%s", db1.toString(), db2.toString()))) {
                    findDatabaseDifferences(databaseDifferenceMap, db1, db2);
                    dbSet.add(String.format("%s_%s", db1.toString(), db2.toString()));
                    dbSet.add(String.format("%s_%s", db2.toString(), db1.toString()));
                }
            }
        }
    }

    private void findDatabaseDifferences( Map<String, DatabaseDifference> databaseDifferenceMap, Database db1, Database db2 ) {
        Set<String> processControl = new HashSet<>();
        for( DatabaseTable table1 : db1.getTableList() ) {
            if( !table1.getName().contains("INTERMEDIA14") ){
                String table1Str = table1.getFullPath();
                boolean existTable = false;
                for (DatabaseTable table2 : db2.getTableList()) {
                    String table2Str = table2.getFullPath();
                    String processKey = String.format("%s_%s", table1Str, table2Str);
                    if (!processControl.contains(processKey)) {
                        if (table1.getName().equalsIgnoreCase(table2.getName())) {
                            existTable = true;

                            DatabaseTableDifferenceReason differenceReason = DatabaseTableDifferenceReason.parse(table1, table2);
                            // TODO: Finish DatabaseTableDifferenceReason implementation
                            //differenceReason.get

                            if (!table1.equals(table2)) {
                                findTableDifferences(databaseDifferenceMap, table1, table2);
                            }
                            findDifferencesByTableData(databaseDifferenceMap, table2, table1);

                            break;
                        }
                    }
                    String inverseProcessKey = String.format("%s_%s", table2Str, table1Str);
                    processControl.add(processKey);
                    processControl.add(inverseProcessKey);

                }

                if (!existTable) {
                    String reasonMessage = String.format("The table named \"%s\" doesn't exist in %s", table1.getName(),
                            db2.getName());
                    if (databaseDifferenceMap.containsKey(table1.getName())) {
                        databaseDifferenceMap.get(table1.getName()).addReasonMessage(reasonMessage);
                    } else {
                        DatabaseDifference dbDiff = new DatabaseDifference(table1);
                        dbDiff.addReasonMessage(reasonMessage);
                        databaseDifferenceMap.put(table1.getName(), dbDiff);
                    }
                }
            }
        }
    }

    private void findDifferencesByTableData(Map<String, DatabaseDifference> databaseDifferenceMap, DatabaseTable table2, DatabaseTable table1) {
        List<String> reasonMessageList = findDifferencesByTableData(table2, table1);
        if( databaseDifferenceMap.containsKey(table1.getName()) ) {
            databaseDifferenceMap.get(table1.getName()).addReasonMessage(reasonMessageList);
        } else {
            databaseDifferenceMap.put(table1.getName(), new DatabaseDifference(table1, table2, reasonMessageList));
        }
    }

    private void findTableDifferences(Map<String, DatabaseDifference> databaseDifferenceMap, DatabaseTable table1, DatabaseTable table2) {
        if( !table1.equals(table2) ) {
            List<String> reasonMessageList = new ArrayList<>();
            String messageStr = "";
            if( !Objects.equals( table1.getColumnList().size() , table2.getColumnList().size() ) ) {
                messageStr = "Difference in Columns: { %s cols and %s cols}";
                reasonMessageList.add(String.format(messageStr,
                        table1.getColumnList().size(),
                        table2.getColumnList().size()));
            }

            if( !Objects.equals( table1.getLastPrimaryKey(), table2.getLastPrimaryKey() ) ) {
                messageStr = "Last primary key is different: { %s and %s }";
                reasonMessageList.add(String.format(messageStr,
                        table1.getLastPrimaryKey(),
                        table2.getLastPrimaryKey()));
            }

            if( !Objects.equals( table1.getNumberOfRecords(), table2.getNumberOfRecords() ) ) {
                messageStr = "Number of records is different: { %s and %s }";
                reasonMessageList.add(String.format(messageStr,
                        table1.getNumberOfRecords(),
                        table2.getNumberOfRecords()));

                double fromPK = 0d;
                double toPk = 0d;
                if(table1.getLastPrimaryKey() < table2.getLastPrimaryKey() ) {
                    fromPK = table1.getLastPrimaryKey();
                    toPk = table2.getLastPrimaryKey();
                } else {
                    fromPK = table2.getLastPrimaryKey();
                    toPk = table1.getLastPrimaryKey();
                }

                LOGGER.info("SELECT * FROM %s WHERE PRIMARY_KEY BETWEEN %s AND %s".formatted(table1.getName(), fromPK, toPk));
            }

            if( databaseDifferenceMap.containsKey(table1.getName()) ) {
                databaseDifferenceMap.get(table1.getName()).addReasonMessage(reasonMessageList);
            } else {
                databaseDifferenceMap.put(table1.getName(), new DatabaseDifference(table1, table2, reasonMessageList));
            }
        }
    }

    private List<String> findDifferencesByTableData(DatabaseTable table1, DatabaseTable table2) {
        if( table1.getTableData().hasData() && table2.getTableData().hasData() ) {
            DatabaseTableData data1 = table1.getTableData();
            DatabaseTableData data2 = table2.getTableData();

            List<String> differenceList = data1.getDataDifferenceList(data2);
            //List<String> inverseDifferenceList = data2.getDataDifferenceList(data1);

            Set<String> differenceSet = new HashSet<>();
            differenceSet.addAll(differenceList);
            //differenceSet.addAll(inverseDifferenceList);

            return differenceSet.stream().toList();
        }
        return new ArrayList<>();
    }

    public void printDifferenceResults() {
        List<String> consoleOutput = new ArrayList<>();
        List<String> queries = new ArrayList<>();
        consoleOutput.add("");
        consoleOutput.add("Difference between the following snapshots: {");
        consoleOutput.add( getDatabaseList().stream().map(d -> String.format("- %s", d.getSnapshotFileName())).collect(Collectors.joining(System.lineSeparator())));
        consoleOutput.add("}");

        for( Map.Entry<String, DatabaseDifference> diffMap : getDatabaseDifferenceList().entrySet() ) {
            DatabaseDifference diff = diffMap.getValue();
            consoleOutput.add(diff.toString());
            queries.add(diff.getSqlQuery());
        }
        consoleOutput.add("");
        consoleOutput.add("You can run the following queries to check the data:");
        consoleOutput.addAll(queries);

        LOGGER.info(String.join( System.lineSeparator(), consoleOutput));
    }

    public static void main( String[] args ) {
        List<String> snapshotListToCompare = new ArrayList<>();
        /*
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-00-05.snap"); // Initial
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-00-10.snap"); // new Contact
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-10-58.snap"); // new User
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-03.snap"); // New Group
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-07.snap"); // Add User to Group
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-20.snap"); // Add User, Admin and Setup Rights
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-22.snap"); // Add Tools Rights
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-24.snap"); // Add Category and Custom Field Rights
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-34.snap"); // Logging with newly created user "Alberto"
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-36.snap"); // Change system logging settings
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-42.snap"); // New Object Definition
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-52.snap"); // New Rule Created
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-58.snap"); // Activate Rule (update)
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-04.snap"); // New Custom Field created
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-14.snap"); // Add Qualifier And Action (Add Items Button)
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-18.snap"); // Add Qualifier And Action (Save)
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-20.snap"); // Create List Route
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-31.snap"); // Add Stops and Email Notifications to List Route
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-35.snap"); // Adding Action to Alberto Test Rule
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-14-24.snap"); // Adding Action to Alberto Test Rule with data
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-19-57.snap"); // Adding new object definition
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-21-01.snap"); // Testing data
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-21-04.snap"); // Testing data
        */
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-21-10.snap"); // Testing data
        try {
            DatabaseComparatorResults dbComparator = new DatabaseComparatorResults(snapshotListToCompare);
            dbComparator.printDifferenceResults();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void getNewRowsByTable(boolean printToConsole) throws IOException {
        List<String> formattedTableData = new ArrayList<>();
        List<String> tableInfo = new ArrayList<>();
        String databaseName = "";
        for( Map.Entry<String, List<DatabaseTableRowData>> entry : getNewRecordsMap().entrySet() ) {
            DatabaseTable table = entry.getValue().getFirst().getTableData().getTable();
            databaseName = table.getDatabase().getName();
            if( table.getDatabase().getDatabaseTypeEnum() == DatabaseTypeEnum.ORACLE ){
                databaseName = table.getDatabase().getUsername();
            }
            String[] headers = table.getColumnList().stream().map(DatabaseTableColumn::getName).toArray(String[]::new);
            String[][] data = new String[entry.getValue().size()][headers.length];

            for( int i = 0 ; i < headers.length ; i++ ) {
                String columnName = headers[i];
                int j = 0;
                for (DatabaseTableRowData tableRowData : entry.getValue()) {
                    Object castedValue = tableRowData.getTableColumnDataByColumnName(columnName).getCastedValue();
                    try {
                        if (Objects.nonNull(castedValue)) {
                            data[j][i] = castedValue.toString();
                        } else {
                            data[j][i] = "null";
                        }
                    }catch( Exception e ){
                        LOGGER.error("%s, %s".formatted(e.toString(), e.getMessage()), e);
                    }

                    j++;
                }
            }

            tableInfo.add(String.format("  - %s[%s]", table.getName(), data.length));
            formattedTableData.addAll(StringHelper.getDataToTableFormat(table.getName(), headers, data));
            if(printToConsole)
                StringHelper.printDataToTableFormat( table.getName(), headers, data );
        }

        Collections.sort(tableInfo);
        tableInfo.addFirst("Tables:");
        formattedTableData.addAll(0, tableInfo);
        FileDirHelper.saveListContentToFile( databaseName+"_new_records", formattedTableData );
    }

    public void getUpdatedRowsByTable(boolean printToConsole) throws IOException {
        List<String> formattedTableData = new ArrayList<>();
        List<String> tableInfo = new ArrayList<>();
        String databaseName = "";
        for( Map.Entry<String, List<DatabaseTableRowData>> entry : getUpdatedRecordsMap().entrySet() ) {
            DatabaseTable table = entry.getValue().getFirst().getTableData().getTable();
            databaseName = table.getDatabase().getName();
            if( table.getDatabase().getDatabaseTypeEnum() == DatabaseTypeEnum.ORACLE ){
                databaseName = table.getDatabase().getUsername();
            }
            String[] headers = table.getColumnList().stream().map(DatabaseTableColumn::getName).toArray(String[]::new);
            String[][] data = new String[entry.getValue().size()][headers.length];

            for( int i = 0 ; i < headers.length ; i++ ) {
                String columnName = headers[i];
                int j = 0;
                for (DatabaseTableRowData tableRowData : entry.getValue()) {
                    Object castedValue = tableRowData.getTableColumnDataByColumnName(columnName).getCastedValue();
                    try {
                        if (Objects.nonNull(castedValue)) {
                            data[j][i] = castedValue.toString();
                        } else {
                            data[j][i] = "null";
                        }
                    }catch( Exception e ){
                        LOGGER.error("%s, %s".formatted(e.toString(), e.getMessage()), e);
                    }

                    j++;
                }
            }

            tableInfo.add(String.format("  - %s[%s]", table.getName(), data.length));
            formattedTableData.addAll(StringHelper.getDataToTableFormat(table.getName(), headers, data));
            if(printToConsole)
                StringHelper.printDataToTableFormat( table.getName(), headers, data );
        }

        Collections.sort(tableInfo);
        tableInfo.addFirst("Tables:");
        formattedTableData.addAll(0, tableInfo);
        FileDirHelper.saveListContentToFile( databaseName+"_updated_records", formattedTableData );
    }
}
