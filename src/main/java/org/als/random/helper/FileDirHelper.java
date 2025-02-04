package org.als.random.helper;

import java.io.File;
import java.util.Objects;

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
}
