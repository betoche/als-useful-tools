package org.als.random.utils;

import org.als.random.RandomLogger;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class DirectoryAnalyzer {
    private final String directoryPath;
    private List<String> directoryList;
    private List<String> fileList;
    private Map<String, Integer> fileTypesCountMap;
    private static final String NEW_LINE = System.lineSeparator();
    private RandomLogger log = new RandomLogger(DirectoryAnalyzer.class);

    public DirectoryAnalyzer(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValueDescending(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.<K, V>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void printDirectoryFileTypesCount() {
        StringBuilder builder = new StringBuilder();

        Map<String, Integer> sortedMap = sortMapByValueDescending( getFileTypesCountMap() );

        builder.append(String.format("%sFile types count results", NEW_LINE));
        builder.append(String.format("%s  Directory: %s", NEW_LINE, directoryPath));

        for( Entry<String,Integer> entry : sortedMap.entrySet() ) {
            builder.append(String.format("%s    - %s: %s", NEW_LINE, entry.getKey(), entry.getValue()));
        }

        builder.append(String.format("%s------------------------------------------------------", NEW_LINE));
        builder.append(String.format("%s- SUMMARY -", NEW_LINE));
        builder.append(String.format("%s------------------------------------------------------", NEW_LINE));
        builder.append(String.format("%s   - Total File Extensions: %s", NEW_LINE, getFileTypesCountMap().size()));

        log.info(builder.toString());
    }

    public Map<String, Integer> getFileTypesCountMap(){
        if(Objects.isNull(fileTypesCountMap) ){
            MavenProjectFileSystemFinder mpfsf = new MavenProjectFileSystemFinder();
            fileTypesCountMap = mpfsf.getFileTypesCountMap(directoryPath);
        }
        return fileTypesCountMap;
    }

    public static void main( String[] args ) {
        String directory = "C:\\Users\\betoc\\repositories\\TCE6.3.5_P25\\eclipse";
        DirectoryAnalyzer da = new DirectoryAnalyzer(directory);
        da.printDirectoryFileTypesCount();
    }
}
