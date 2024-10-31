package org.als.random.utils;

import org.als.random.RandomLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class StringPatternSearch {
    private String directory;
    private String[] patterns;
    private String[] fileTypes;
    private List<String> filesPathList;
    private Map<String, List<String>> linesWithPatternMap;
    private RandomLogger logger = new RandomLogger(StringPatternSearch.class);

    public StringPatternSearch( String directory, String[] patterns, String[] fileTypes ) {
        this.directory = directory;
        this.patterns = patterns;
        this.fileTypes = fileTypes;
    }

    public List<String> getFilePathList() {
        if(Objects.isNull(filesPathList) ) {
            MavenProjectFileSystemFinder mavenProjFileSystem = new MavenProjectFileSystemFinder();
            filesPathList = mavenProjFileSystem.getFilePathList(directory, fileTypes);
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

    private void showResults() {
        String pattern = String.join(", ", patterns);
        String types = Objects.nonNull(fileTypes)?String.join(", ", fileTypes):"*";

        logger.info("=========================================================");
        logger.info(String.format("Searching for %s into [ %s ] file types...", pattern, types));
        logger.info("=========================================================");
        getLinesWithPatternMap();
        logger.info("  - Results:");
        logger.info("  ========");

        List<String> openWithCode = new ArrayList<>();
        for( Map.Entry<String, List<String>> entry : getLinesWithPatternMap().entrySet() ) {
            String filePath = entry.getKey();
            List<String> lines = entry.getValue();

            for( String l : lines ) {
                String line = l.substring(0, l.lastIndexOf(":"));
                String lineNumber = l.replace(String.format("%s:", line), "");

                logger.info(String.format("    - %s:%s -> %s", filePath, lineNumber, line.trim()));
                openWithCode.add(String.format("%scode --goto \"%s:%s\"", "\n", filePath, lineNumber));
            }
        }
        logger.info("  - Summary:");
        logger.info("  ==========");
        logger.info(String.format("Directory       : %s", directory));
        logger.info(String.format("Patterns        : %s", pattern));
        logger.info(String.format("File Extensions : %s", types));
        logger.info(String.format("Scanned Files   : %s", getFilePathList().size()));
        logger.info(String.format("Matches         : %s", getLinesWithPatternMap().size()));
        logger.info("=========================================================");
        logger.info("  - Open files with code:");
        logger.info("  =======================");
        logger.info( String.join("",openWithCode));
        logger.info("=========================================================");
    }

    public static void main( String[] args ) {
        //String directory = "C:\\Users\\betoc\\repositories\\TCE7.1.0\\teamconnectenterprise";
        String directory = "C:\\Users\\betoc\\repositories\\TCE6.3.5_P25\\teamconnectenterprise";
        //String directory = "C:\\Users\\betoc\\repositories\\TCE7.0.0\\teamconnectenterprise";
        //String directory = "C:\\Users\\betoc\\repositories\\TCE6.3.5_P25\\eclipse";
        //String directory = "C:\\Users\\betoc\\repositories\\TCE6.3.5_P25\\maven-3.6.3";
        //String[] patterns = new String[]{ "submitFormWithEntityCode" };
        //String[] patterns = new String[]{ "Parent Object" };
        //String[] patterns = new String[]{ "m2/repository" };
        //String[] patterns = new String[]{ "java.home" };
        //String[] patterns = new String[]{ ">org.openjfk<" };
        String[] patterns = new String[]{ "design-pomgen-maven-plugin" };


        //String[] fileExtensions = new String[]{ ".java", ".jsp" };
        //String[] fileExtensions = new String[]{ ".xml", ".ini", ".prefs" };
        String[] fileExtensions = new String[]{ ".xml" };

        StringPatternSearch sps = new StringPatternSearch(directory, patterns, fileExtensions);
        sps.showResults();
    }
}