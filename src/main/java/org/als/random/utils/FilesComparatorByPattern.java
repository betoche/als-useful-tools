package org.als.random.utils;

import org.als.random.helper.FileDirHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilesComparatorByPattern {
    private String primaryDirectory;
    private String secondaryDirectory;
    private String filePattern;
    private List<File> fileList;
    private List<FileDifference> fileDifferenceList;

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesComparatorByPattern.class);

    public List<File> getFileList() {
        if(Objects.isNull(fileList)) {
            fileList = FileDirHelper.findFileListWithPattern( filePattern, primaryDirectory );
        }

        return fileList;
    }

    public List<FileDifference> getFileDifferenceList() {
        if( Objects.isNull(fileDifferenceList) ) {
            fileDifferenceList = new ArrayList<>();

            for( File file : getFileList() ) {
                File secondaryFile = FileDirHelper.findFileInDirectory( FileDirHelper.getSimpleFileName(file), secondaryDirectory );

                fileDifferenceList.add(new FileDifference(file, secondaryFile, primaryDirectory, secondaryDirectory));
            }
        }
        return fileDifferenceList;
    }

    public FilesComparatorByPattern(String filePattern, String primaryDirectory, String secondaryDirectory){
        this.filePattern = filePattern;
        this.primaryDirectory = primaryDirectory;
        this.secondaryDirectory = secondaryDirectory;
    }

    public static void main( String[] args ) {
        LOGGER.info("FilesComparatorByPattern execution [START]");
        String pattern = ".scr.xml";
        String primaryDir = "C:\\Users\\betoc\\Documents\\tickets\\SOH-5146 - Actual accounts are not showing Invoice amounts properly\\designs\\design7_source";
        String secondaryDir = "C:\\Users\\betoc\\repositories\\financialmanagement";

        FilesComparatorByPattern filesComparatorByPattern = new FilesComparatorByPattern(pattern, primaryDir, secondaryDir);
        for(FileDifference difference : filesComparatorByPattern.getFileDifferenceList()) {
            try {
                if( difference.getDifferenceList().size()>0 ){
                    LOGGER.info( difference.toString()+"," );
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("[END] of FilesComparatorByPattern execution.");
    }

}
