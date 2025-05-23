package org.als.random.utils;

import org.als.random.helper.FileDirHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileDifference {
    private String primaryDirectory;
    private String secondaryDirectory;
    private File primaryFile;
    private File secondaryFile;
    private List<String> differenceList;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDifference.class);

    public FileDifference(File primaryFile, File secondaryFile, String primaryDirectory, String secondaryDirectory) {
        this.primaryFile = primaryFile;
        this.secondaryFile = secondaryFile;
        this.primaryDirectory = primaryDirectory;
        this.secondaryDirectory = secondaryDirectory;
    }

    public List<String> getDifferenceList() throws IOException {
        if(Objects.isNull(differenceList) ) {
            differenceList = new ArrayList<>();

            if(FileDirHelper.isValidFile(primaryFile) && FileDirHelper.isValidFile(secondaryFile)) {
                List<String> primaryFileLines = Files.readAllLines(Path.of(primaryFile.toURI()));
                List<String> secondaryFileLines = Files.readAllLines(Path.of(secondaryFile.toURI()));

                if( primaryFileLines.size()!=secondaryFileLines.size() ) {
                    differenceList.add("  Different line count: {%s    %s lines: %s,%s    %s lines: %s %s  }".formatted(System.lineSeparator(), primaryFileLines.size(), primaryFile.getAbsolutePath(), System.lineSeparator(), secondaryFileLines.size(), secondaryFile.getAbsolutePath(), System.lineSeparator()));
                }

                File wrkPrimaryFile = primaryFile;
                File wrkSecondaryFile = secondaryFile;

                List<String> wrkPrimaryFileLines = primaryFileLines;
                List<String> wrkSecondaryFileLines = secondaryFileLines;
                if( primaryFileLines.size() < secondaryFileLines.size() ) {
                    wrkPrimaryFileLines = secondaryFileLines;
                    wrkSecondaryFileLines = primaryFileLines;

                    wrkPrimaryFile = secondaryFile;
                    wrkSecondaryFile = primaryFile;
                }

                for( int i = 0 ; i < wrkPrimaryFileLines.size() ; i++ ){
                    String primaryLine = wrkPrimaryFileLines.get(i);
                    if( i < wrkSecondaryFileLines.size() ) {
                        String secondaryLine = wrkSecondaryFileLines.get(i);

                        if( !primaryLine.equalsIgnoreCase(secondaryLine) ) {
                            /*
                            if( wrkPrimaryFile.getAbsolutePath().contains("CjbBudgetSettings") ) {
                                LOGGER.info( "File: %s".formatted(wrkPrimaryFile.getAbsolutePath()) );
                            }
                            */
                            differenceList.add("  Line at %s are different: {%s    %s: {%s}%s    %s: {%s} %s  }".formatted(
                                    (i+1),
                                    System.lineSeparator(),
                                    getDirectoryFromFilePath(wrkPrimaryFile.getAbsolutePath()),
                                    primaryLine.trim(),
                                    System.lineSeparator(),
                                    getDirectoryFromFilePath(wrkSecondaryFile.getAbsolutePath()),
                                    secondaryLine.trim(),
                                    System.lineSeparator()));
                        }
                    } else {
                        differenceList.add("  Line at %s doesn't exist: { %s }".formatted((i+1), primaryLine.trim()));
                    }
                }
            }
        }

        return differenceList;
    }

    private String getDirectoryFromFilePath( String filePath ) {
        if( filePath.contains(primaryDirectory) ){
            return FileDirHelper.getLastDirectory( primaryDirectory );
        } else if (filePath.contains(secondaryDirectory)) {
            return FileDirHelper.getLastDirectory( secondaryDirectory );
        }

        return "unknown";
    }

    public String toString() {
        try {
            return "%s : {%s%s%s}".formatted(FileDirHelper.getSimpleFileName(primaryFile), System.lineSeparator(), String.join(System.lineSeparator(), getDifferenceList()), System.lineSeparator());
        } catch (IOException e) {
            return "ERROR: %s: %s".formatted(e.toString(), e.getMessage());
        }
    }
}
