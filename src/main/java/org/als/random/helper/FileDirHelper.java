package org.als.random.helper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileDirHelper {
    public static boolean containsSubDirectoriesOrSubFiles( File directory ) {
        return isValidDirectory(directory) && Objects.nonNull(directory.listFiles()) && directory.listFiles().length>0;
    }
    public static boolean isValidDirectory( File directory ){
        return Objects.nonNull(directory) && directory.exists() && directory.isDirectory();
    }
    public static boolean isValidFile( File file ) {
        return Objects.nonNull(file) && file.exists() && file.isFile();
    }
    public static String getSimpleFileName(File file) {
        if( !isValidFile(file) )
            return "";

        String filePath = file.getAbsolutePath();
        int lastIndexOf = filePath.lastIndexOf("/");
        if(lastIndexOf<0)
            lastIndexOf = filePath.lastIndexOf("\\");

        return filePath.substring(lastIndexOf+1);
    }
    public static String getSimpleDirectoryName(File directory) {
        if( Objects.isNull(directory) )
            return "";
        if( !isValidDirectory(directory) )
            return "";

        String filePath = directory.getAbsolutePath();
        int lastIndexOf = filePath.lastIndexOf("/");
        if(lastIndexOf<0)
            lastIndexOf = filePath.lastIndexOf("\\");

        return filePath.substring(lastIndexOf+1);
    }

    public static void saveListContentToFile( String fileName, List<String> formattedTableData ) throws IOException {
        UUID uuid = UUID.randomUUID();

        String uuidStr = uuid.toString();
        String fName = String.format("%s-%s.log", fileName, uuidStr.substring(uuidStr.length()-5));

        String outputStr = "output";
        File outputDir = new File(outputStr);
        if( !outputDir.exists() ){
            outputDir = Files.createDirectory(Path.of(outputStr)).toFile();
        }

        Files.write(Paths.get(String.format("%s/%s", outputStr, fName)), formattedTableData, StandardCharsets.UTF_8);
    }

    public static List<File> findFileListWithPattern(String filePattern, String primaryDirectory) {
        List<File> fileList = new ArrayList<>();
        File directory = new File(primaryDirectory);
        for( File subFile : Objects.requireNonNull(directory.listFiles())) {
            if( isValidDirectory(subFile) ) {
                fileList.addAll(findFileListWithPattern(filePattern, subFile.getAbsolutePath()));
            } else if( isValidFile(subFile) ){
                if( subFile.getName().endsWith(filePattern) ) {
                    fileList.add(subFile);
                }
            }
        }

        return fileList;
    }

    public static File findFileInDirectory(String simpleFileName, String secondaryDirectory) {
        File directory = new File(secondaryDirectory);
        for( File subFile : Objects.requireNonNull(directory.listFiles())) {
            if( isValidDirectory(subFile) && !subFile.getName().contains(".git") ) {
                File file = findFileInDirectory(simpleFileName, subFile.getAbsolutePath());
                if( Objects.nonNull(file) )
                    return file;
            } else if( isValidFile(subFile) ){
                if( FileDirHelper.getSimpleFileName(subFile).equalsIgnoreCase(simpleFileName) ) {
                    return subFile;
                }
            }
        }

        return null;
    }

    public static String getLastDirectory(String directory) {
        int lastIndexOf = directory.lastIndexOf("\\");

        return directory.substring(lastIndexOf);
    }
}
