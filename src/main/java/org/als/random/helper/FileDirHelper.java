package org.als.random.helper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileDirHelper {
    public static boolean containsSubDirectoriesOrSubFiles( File directory ) {
        return isValidDirectory(directory) && Objects.nonNull(directory.listFiles());
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
}
