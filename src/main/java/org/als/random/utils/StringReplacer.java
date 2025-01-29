package org.als.random.utils;

import lombok.*;
import org.als.random.domain.FindReplaceResults;
import org.als.random.domain.FindReplaceSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StringReplacer {
    @Getter
    private final String filePathStr;
    private List<String> fileLineList;
    private List<String> trimmedFileLineList;
    @Getter
    private List<FindReplaceSearch> findReplaceSearchList;
    private Map<FindReplaceSearch, List<FindReplaceResults>> findReplaceResultsMap;

    private List<String> processedFileLineList;
    public static final String NL = System.lineSeparator();
    public static int MAX_NUMBER_OF_LINES = 10000;
    public static final Logger LOGGER = LoggerFactory.getLogger(StringReplacer.class);

    public StringReplacer( String filePathStr, List<FindReplaceSearch> findReplaceSearchList) {
        this.filePathStr = filePathStr;
        this.findReplaceSearchList = findReplaceSearchList;
    }

    public List<String> getFileLineList() {
        if (Objects.isNull(this.fileLineList) ) {
            this.fileLineList = new ArrayList<>();

            try {
                this.fileLineList = Files.readAllLines(Path.of(getFilePathStr()) );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this.fileLineList;
    }
    public List<String> getTrimmedFileLineList() {
        if( Objects.isNull(trimmedFileLineList) ) {
            trimmedFileLineList = getFileLineList().stream().map(String::trim).toList();
        }
        return trimmedFileLineList;
    }


    public Map<FindReplaceSearch, List<FindReplaceResults>> getFindReplaceResultsMap() {
        if( Objects.isNull(this.findReplaceResultsMap) ) {
            this.findReplaceResultsMap = new HashMap<>();
            processStringIndexOf();

            for( FindReplaceSearch findReplaceSearch : getFindReplaceSearchList() ) {
                if(!findReplaceSearch.getLineIndexOf().isEmpty()) {
                    List<FindReplaceResults> resultList = getFindReplaceResultsList(findReplaceSearch);
                    this.findReplaceResultsMap.put(findReplaceSearch, resultList);
                } else {
                    this.findReplaceResultsMap.put(findReplaceSearch, new ArrayList<>());
                }
            }
        }

        return this.findReplaceResultsMap;
    }

    public void processStringIndexOf() {
        String nl = System.lineSeparator();

        for( FindReplaceSearch fr : getFindReplaceSearchList() ) {
            int numberOfLines = getNumberOfLines(fr.getFindStr(), MAX_NUMBER_OF_LINES);
            int chunksCount = getFileChunksCount(numberOfLines);
            String findStr = String.join(nl, fr.getFindStr().split(NL));
            findStringIndexOf(findStr, chunksCount, numberOfLines, fr);
        }
    }

    public int getNumberOfLines( String searchStr, int maxLines ) {
        int lineCountToSearch = searchStr.split(NL).length;
        int tmp1 = maxLines/lineCountToSearch;
        return tmp1*lineCountToSearch;
    }
    public int getFileChunksCount( int numberOfLines ) {
        return (getFileLineList().size()/numberOfLines)+1;
    }
    private void findStringIndexOf( String findStr, int chunks, int numberOfLines, FindReplaceSearch fr ) {
        String nl = System.lineSeparator();
        List<String> findStringList = Arrays.stream(fr.getFindStr().split(NL)).map(String::trim).toList();
        LOGGER.info(String.format("File contains %s lines, the search is going to be each %s lines, with a total of %s chunks",
                getFileLineList().size(), numberOfLines, chunks ));
        for (int i = 0; i < chunks; i++) {
            int fromIdx = i*numberOfLines;
            int toIdx = ((i+1)*numberOfLines)+((findStringList.size())-2);
            if( toIdx >= getFileLineList().size() ) {
                toIdx = getFileLineList().size()-2;
            }

            List<String> lineListChunk = getFileLineList().subList(fromIdx, toIdx).stream().map(String::trim).toList();

            String chunkStr = String.join(nl, lineListChunk).replace(nl, "");
            String trimmedString = findStr.replace(nl, "").trim();
            if (chunkStr.contains(trimmedString)) {
                setLineIndexOf(fromIdx, toIdx, findStringList, fr);
            }
        }
    }
    private void setLineIndexOf(int fromIdx, int toIdx, List<String> searchLineList, FindReplaceSearch fr) {
        for( int i = fromIdx; i <= toIdx ; i++ ) {
            String line = getFileLineList().get(i);
            if( line.trim().compareTo(searchLineList.getFirst().trim())==0 ) {
                if( searchStrMatches( i, searchLineList ) ) {
                    fr.addLineIndexOf(i);
                }
            }
        }
    }

    private List<FindReplaceResults> getFindReplaceResultsList(FindReplaceSearch findReplaceSearch) {
        List<FindReplaceResults> resultList = new ArrayList<>();
        for( Integer indexOf : findReplaceSearch.getLineIndexOf() ) {
            List<String> matchedLines = new ArrayList<>();
            int indexTo = indexOf + findReplaceSearch.getFindStr().split(NL).length - 1;
            for( int i = indexOf ; i < indexTo ; i++ ){
                matchedLines.add(getFileLineList().get(i));
            }
            FindReplaceResults results = new FindReplaceResults();
            results.setFrom(indexOf);
            results.setTo(indexTo);
            results.setMatchedLines(matchedLines);
            resultList.add(results);
        }
        return resultList;
    }

    public List<String> getProcessedFileLineList() {
        if( Objects.isNull(this.processedFileLineList) ) {
            this.processedFileLineList = new ArrayList<>(getFileLineList());
            List<Integer> indexesToRemove = new ArrayList<>();

            for( Map.Entry<FindReplaceSearch, List<FindReplaceResults>> entry : getFindReplaceResultsMap().entrySet() ) {
                for( FindReplaceResults result : entry.getValue()) {
                    indexesToRemove.addAll(result.getMatchedLineIndexList());
                }
            }

            indexesToRemove.sort(Collections.reverseOrder());

            for (Integer integer : indexesToRemove) {
                this.processedFileLineList.remove(integer.intValue());
            }
        }
        return this.processedFileLineList;
    }

    private boolean searchStrMatches( int idx, List<String> searchLineList ) {
        for( int i = 0 ; i < searchLineList.size() ; i++ ) {
            if( searchLineList.get(i).trim().compareTo(getFileLineList().get(idx+i).trim())!=0 ) {
                return false;
            }
        }

        return true;
    }

    public void printSearchResults() {
        String nl = System.lineSeparator();
        List<String> output = new ArrayList<>();
        for( Map.Entry<FindReplaceSearch, List<FindReplaceResults>> entry : getFindReplaceResultsMap().entrySet() ) {
            FindReplaceSearch finder = entry.getKey();
            List<FindReplaceResults> results = entry.getValue();
            output.add("-----------------------------------------------------------------");
            output.add(String.format("Searched String: {%s%s", nl, finder.getFindStr()));
            output.add(String.format("  - found: %s times", results.size()));
            output.add("  - Details: {");
            output.addAll(getFormattedResults(results, 6));
            output.add("    }");
            output.add(String.format("}%s", nl));
        }

        LOGGER.info(String.format("Results: %s%s", nl, String.join(nl, output)));
    }

    private List<String> getFormattedResults( List<FindReplaceResults> results, int indentSpace ) {
        List<String> formattedResults = new ArrayList<>();

        for( FindReplaceResults result : results ){
            formattedResults.addAll(result.getFormmatedResults(indentSpace));
        }

        return formattedResults;
    }

    public void createProcessedFile() {
        File f = new File(getFilePathStr());
        if( f.exists() && f.isFile() ) {
            String simpleFileName = f.getName().substring( 0,f.getName().lastIndexOf(".")) ;
            String fileExtension = f.getName().substring( f.getName().lastIndexOf(".")+1);
            String dateStr = (new SimpleDateFormat("yyyyMMddHHmmss")).format(Calendar.getInstance().getTime());
            String fileName = String.format("%s-%s.%s", simpleFileName, dateStr, fileExtension);
            File output = new File(String.format("output/%s",  fileName));

            try {
                LOGGER.info(String.format("Creating file name: %s with fullPath as %s", fileName, output.getAbsolutePath()));
                Files.write(Paths.get(String.format("output/%s",  fileName)), getProcessedFileLineList(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main( String[] args ) {
        //String filePathStr = "C:\\Users\\betoc\\Downloads\\Ilago Logs - After patch - Copy.txt";
        String filePathStr = "C:\\Users\\betoc\\repositories\\personal\\java\\random\\output\\Ilago-Logs-After-patch-20250117133927.txt";
        List<FindReplaceSearch> findReplaceSearchList = new ArrayList<>();
        String textToFind1 = "\tat com.mitratech.teamconnect.base.BQDetailFieldValues.lambda$1(BQDetailFieldValues.java:92)\n" +
                "\tat com.mitratech.teamconnect.base.BQDetailFieldValues.getStringValue(BQDetailFieldValues.java:102)\n" +
                "\tat com.mitratech.teamconnect.base.BQDetailFieldValues.getPrimaryKeyQ(BQDetailFieldValues.java:92)\n" +
                "\tat com.mitratech.teamconnect.base.BQObject.getKeyQ(BQObject.java:109)";


        String textToFind2 = "com.mitratech.teamconnect.base.BQDetailFieldValues.lambda$1(BQDetailFieldValues.java:92)\n" +
                "com.mitratech.teamconnect.base.BQDetailFieldValues.getStringValue(BQDetailFieldValues.java:102)\n" +
                "com.mitratech.teamconnect.base.BQDetailFieldValues.getPrimaryKeyQ(BQDetailFieldValues.java:92)\n" +
                "com.mitratech.teamconnect.base.BQObject.getKeyQ(BQObject.java:109)";

        String textToFind3 = """
                	at com.mitratech.teamconnect.base.BQDetailFieldValues.lambda$1(BQDetailFieldValues.java:92)
                	at com.mitratech.teamconnect.base.BQDetailFieldValues.getStringValue(BQDetailFieldValues.java:102)
                	at com.mitratech.teamconnect.base.BQDetailFieldValues.getPrimaryKeyQ(BQDetailFieldValues.java:92)
                	at com.mitratech.teamconnect.base.BQObject.getKeyQ(BQObject.java:109)
                """;
        //findReplaceStringList.add(new FindReplaceString(textToFind1, ""));
        //findReplaceStringList.add(new FindReplaceString(textToFind2, ""));
        findReplaceSearchList.add(new FindReplaceSearch(textToFind3, ""));
        StringReplacer strReplacer = new StringReplacer(filePathStr, findReplaceSearchList);

        strReplacer.createProcessedFile();
    }
}
