package org.als.random.domain;

import lombok.Getter;
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

    private void findDatabaseDifferences(Map<String, DatabaseDifference> databaseDifferenceMap) {
        for( int i = 0 ; i < getDatabaseList().size() ; i++ ) {
            Database db1 = getDatabaseList().get(i);
            for( int j = 0 ; j < getDatabaseList().size() ; j++ ) {
                Database db2 = getDatabaseList().get(j);
                findDatabaseDifferences(databaseDifferenceMap, db1, db2);
            }
        }
    }

    private void findDatabaseDifferences( Map<String, DatabaseDifference> databaseDifferenceMap, Database db1, Database db2 ) {
        for( DatabaseTable table1 : db1.getTableList() ) {
            boolean existTable = false;
            for( DatabaseTable table2 : db2.getTableList() ) {
                if( table1.getName().equalsIgnoreCase(table2.getName()) ) {
                    existTable = true;

                    if( !table1.equals(table2) ) {
                        findTableDifferences(databaseDifferenceMap, table1, table2);
                    }
                    break;
                }
            }

            if( !existTable ) {
                String reasonMessage = String.format("The table named \"%s\" doesn't exist in %s", table1.getName(),
                        db2.getName());
                if( databaseDifferenceMap.containsKey(table1.getName()) ) {
                    databaseDifferenceMap.get(table1.getName()).addReasonMessage(reasonMessage);
                } else {
                    DatabaseDifference dbDiff = new DatabaseDifference(table1);
                    dbDiff.addReasonMessage(reasonMessage);
                    databaseDifferenceMap.put(table1.getName(), dbDiff);
                }
            }
        }
    }

    private void findTableDifferences(Map<String, DatabaseDifference> databaseDifferenceMap, DatabaseTable table1, DatabaseTable table2) {
        if( !table1.equals(table2) ) {
            List<String> reasonMessageList = new ArrayList<>();
            String messageStr = "";
            if( !Objects.equals( table1.getColumnList().size() , table2.getColumnList().size() ) ) {
                messageStr = "Difference in Columns: { %s: %s cols, %s: %s cols}";
                reasonMessageList.add(String.format(messageStr,
                        table1.getFullName(), table1.getColumnList().size(),
                        table2.getFullName(), table2.getColumnList().size()));
            }

            if( !Objects.equals( table1.getLastPrimaryKey(), table2.getLastPrimaryKey() ) ) {
                messageStr = "Last primary key is different: { %s: %s, %s: %s }";
                reasonMessageList.add(String.format(messageStr,
                        table1.getFullName(), table1.getLastPrimaryKey(),
                        table2.getFullName(), table2.getLastPrimaryKey()));
            }

            if( !Objects.equals( table1.getNumberOfRecords(), table2.getNumberOfRecords() ) ) {
                messageStr = "Number of records is different: { %s: %s, %s: %s }";
                reasonMessageList.add(String.format(messageStr,
                        table1.getFullName(), table1.getNumberOfRecords(),
                        table2.getFullName(), table2.getNumberOfRecords()));
            }

            reasonMessageList.addAll(findDifferencesByTableData(table1, table2));

            if( databaseDifferenceMap.containsKey(table1.getName()) ) {
                databaseDifferenceMap.get(table1.getName()).addReasonMessage(reasonMessageList);
            } else {
                databaseDifferenceMap.put(table1.getName(), new DatabaseDifference(table1, reasonMessageList));
            }
        }
    }

    private List<String> findDifferencesByTableData(DatabaseTable table1, DatabaseTable table2) {
        if( table1.getTableData().hasData() && table2.getTableData().hasData() ) {
            DatabaseTableData data1 = table1.getTableData();
            DatabaseTableData data2 = table2.getTableData();

            return data1.getDataDifferenceList(data2);
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
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-00-05.snap"); // Initial
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-00-10.snap"); // new Contact
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-10-58.snap"); // new User
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-03.snap"); // New Group
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-07.snap"); // Add User to Group
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-20.snap"); // Add User, Admin and Setup Rights
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-22.snap"); // Add Tools Rights
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-24.snap"); // Add Category and Custom Field Rights
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-34.snap"); // Logging with newly created user "Alberto"
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-36.snap"); // Change system logging settings
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-42.snap"); // New Object Definition
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-52.snap"); // New Rule Created
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-11-58.snap"); // Activate Rule (update)
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-04.snap"); // New Custom Field created
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-14.snap"); // Add Qualifier And Action (Add Items Button)
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-18.snap"); // Add Qualifier And Action (Save)
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-20.snap"); // Create List Route
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-31.snap"); // Add Stops and Email Notifications to List Route
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-12-35.snap"); // Adding Action to Alberto Test Rule
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-14-24.snap"); // Adding Action to Alberto Test Rule with data
        //snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-19-57.snap"); // Adding new object definition
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-21-01.snap"); // Testing data
        */
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-21-04.snap"); // Testing data
        snapshotListToCompare.add("teamconnect_637-snapshot-2025-01-03-21-10.snap"); // Testing data
        try {
            DatabaseComparatorResults dbComparator = new DatabaseComparatorResults(snapshotListToCompare);
            dbComparator.printDifferenceResults();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        /*
        List<String> list = new ArrayList<>();
        list.add("1kasjdfoipajsdfopijasofi");
        list.add("2foipajsdfopijasofi");
        list.add("3dsjfkhsdhfasjdfoipajsdfopijasofi");
        list.add("4dsjfkhsdhfasjdfoipajsdfopijasofi");

        list.sort((string1, string2) -> Math.abs(string2.length()) - Math.abs(string1.length()));

        for(String s : list) {
            LOGGER.info(s);
        }
        */
    }
}
