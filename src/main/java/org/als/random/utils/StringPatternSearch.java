package org.als.random.utils;

import org.als.random.RandomLogger;
import org.als.random.entity.DirectoryFile;
import org.als.random.entity.FileStats;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class StringPatternSearch {
    private String directory;
    private String[] patterns;
    private String[] fileTypes;
    private String[] exclusions;
    private List<String> filesPathList;
    private Map<String, List<String>> linesWithPatternMap;
    private static RandomLogger logger = new RandomLogger(StringPatternSearch.class);

    public StringPatternSearch( String directory, String[] patterns, String[] fileTypes, String[] exclusions ) {
        this.directory = directory;
        this.patterns = patterns;
        this.fileTypes = fileTypes;
        this.exclusions = exclusions;
    }

    public List<String> getFilePathList() {
        if(Objects.isNull(filesPathList) ) {
            MavenProjectFileSystemFinder mavenProjFileSystem = new MavenProjectFileSystemFinder();
            filesPathList = mavenProjFileSystem.getFilePathList(directory, fileTypes, exclusions);
        }

        return filesPathList;
    }

    public Map<String, List<String>> getLinesWithPatternMap() {
        if( Objects.isNull(linesWithPatternMap) ) {
            linesWithPatternMap = new HashMap<>();

            for( String filePath : getFilePathList() ) {
                File f = new File(filePath);

                if( f.exists() && f.isFile() ) {
                    List<String> lines = getAllFileLines(f.toPath());
                    int lnIdx = 0;
                    for( String l : lines ) {
                        lnIdx++;
                        if( isPatternInLine(l, patterns) ) {
                            String absolutePath = f.getAbsolutePath();
                            if( linesWithPatternMap.containsKey(absolutePath) ) {
                                linesWithPatternMap.get(absolutePath).add(formatLineWithIndex(l, lnIdx));
                            } else {
                                List<String> linesList = new ArrayList<>();
                                linesList.add( formatLineWithIndex(l, lnIdx) );
                                linesWithPatternMap.put(absolutePath, linesList);
                            }
                        }
                    }
                }
            }
        }

        return linesWithPatternMap;
    }

    private List<String> getAllFileLines(Path path) {
        List<String> lines = new ArrayList<>();

        try {
            lines = Files.readAllLines( path, StandardCharsets.UTF_8 );
        } catch (IOException e) {
            //logger.error(String.format( "Error trying to read all lines using UTF_8 - %s", e.getMessage() ));
        }

        if( lines.isEmpty() ) {
            try {
                lines = Files.readAllLines( path, StandardCharsets.ISO_8859_1 );
            } catch (IOException e) {
                logger.error(String.format( "Error trying to read all lines using both UTF_8 and ISO_8859_1 encodings - %s", e.getMessage() ));
            }
        }

        return lines;
    }

    private String formatLineWithIndex( String line, int idx ) {
        return String.format("%s:%s", line, idx);
    }

    private boolean isPatternInLine( String line, String[] patterns ) {
        if( line.trim().startsWith("//") )
            return false;

        for( String p : patterns ) {
            if( line.contains(p) ) {
                return true;
            }
        }

        return false;
    }

    private void showResults() throws IOException {
        List<String> msgList = new ArrayList<>();
        String pattern = String.join(", ", patterns);
        String types = Objects.nonNull(fileTypes)?String.join(", ", fileTypes):"*";

        msgList.add("=========================================================");
        msgList.add(String.format("Searching for \"%s\" into [ %s ] file types...", pattern, types));
        msgList.add("=========================================================");
        getLinesWithPatternMap();
        msgList.add("  - Results:");
        msgList.add("  ========");

        List<String> openWithCode = new ArrayList<>();
        List<String> results = new ArrayList<>();
        results.add("\n");
        for( Map.Entry<String, List<String>> entry : getLinesWithPatternMap().entrySet() ) {
            String filePath = entry.getKey();
            List<String> lines = entry.getValue();

            for( String l : lines ) {
                String line = l.substring(0, l.lastIndexOf(":"));
                String lineNumber = l.replace(String.format("%s:", line), "");
                String relativeFilePath = filePath.replace(directory, "");

                results.add(String.format("- %s:%s -> %s", relativeFilePath, lineNumber, line.trim()));
                openWithCode.add(String.format("code --goto \"%s:%s\"", filePath, lineNumber));
            }
        }
        msgList.addAll(results);
        msgList.add("  - Summary:");
        msgList.add("  ==========");
        msgList.add(String.format("Directory       : %s", directory));
        msgList.add(String.format("Patterns        : %s", pattern));
        msgList.add(String.format("File Extensions : %s", types));
        msgList.add(String.format("Scanned Files   : %s", getFilePathList().size()));
        msgList.add(String.format("Matches         : %s", getLinesWithPatternMap().size()));
        msgList.add("=========================================================");
        msgList.add("  - Open files with code:");
        msgList.add("  =======================");
        msgList.addAll(openWithCode);
        msgList.add("=========================================================");

        logger.info(String.format("%s%s", System.lineSeparator(), String.join(System.lineSeparator(), msgList)));

        saveResultsToFile(msgList);
    }

    public void saveResultsToFile(List<String> outputLines) throws IOException {
        if( !(new File("output")).exists() ) {
            Files.createDirectory(Path.of("output"));
        }
        if( !(new File("output/logs")).exists() ) {
            Files.createDirectory(Path.of("output/logs"));
        }
        Files.write(Paths.get("output/logs/StringPatternSearch.log"), outputLines, StandardCharsets.UTF_8);
    }

    public void sumOddNumbers(){
        List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5, 6);
        int result = numeros.stream().map(n -> { return (n%2==0)?n:0; }).reduce(0, Integer::sum);

        logger.info(String.format("result: %s", result));

    }

    public void createLadder( int size ){
        if( size <= 0 )
            return;

        StringBuilder builder = new StringBuilder();

        for( int i = 0 ; i < size ; i++ ) {
            String characters = "#".repeat(i+1);
            builder.append(String.format("%"+size+"s%s", characters, "\n"));
        }

        logger.info(String.format("Ladder: %s%s", "\n", builder.toString()));

    }

    public static void main( String[] args ) {
        String directory = "C:\\Users\\betoc\\repositories\\TCE7.1.0\\teamconnectenterprise";
        String[] patterns = new String[]{"\"[INFO-"};

        String[] fileExtensions = new String[]{ ".js", ".jsp", ".html" };

        patterns = new String[]{"pagingIndex"};
        //directory = "/mnt/f5725273-d707-49da-a7b3-59582854f1d5/mitratech/repos/TCE7.1.0/teamconnectenterprise";

        StringPatternSearch sps = new StringPatternSearch(directory, patterns, fileExtensions, new String[]{".class", ".png", ".css", ".jar", ".log"});
        //sps.sumOddNumbers();
        //sps.createLadder(6);
        try {
            sps.showResults();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
