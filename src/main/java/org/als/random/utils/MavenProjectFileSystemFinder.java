package org.als.random.utils;

import java.io.File;
import java.util.*;

public class MavenProjectFileSystemFinder {
    private final String projectPath;
    private Map<String, File> pomFileMap;

    public MavenProjectFileSystemFinder(String projectPath) {
        this.projectPath = projectPath;
    }

    public MavenProjectFileSystemFinder() {
        projectPath = "";
    }

    public List<String> getFilePathList( String dir, String[] fileExtensions, String[] exclusions ) {
        List<String> filePathList = new ArrayList<>();
        File directory = new File(dir);

        if( directory.exists() && Objects.nonNull(directory.listFiles()) ) {
            filePathList.addAll(getFilePathList(directory, fileExtensions, exclusions));
        }

        return filePathList;
    }

    private List<String> getFilePathList( File dir, String[] fileExtensions, String[] exclusions ) {
        List<String> filePathList = new ArrayList<>();
        if( dir.exists() && Objects.nonNull( dir.listFiles() ) ) {
            for( File file : dir.listFiles() ) {
                if( file.exists() && file.isFile() ){
                    if( isFileExtensionIn(file.getName(), fileExtensions, exclusions) ) {
                        filePathList.add( file.getAbsolutePath() );
                    }
                } else if( file.exists() && file.isDirectory() && !file.getAbsolutePath().contains("target") && !file.getAbsolutePath().contains("selenium") ) {
                    filePathList.addAll( getFilePathList(file, fileExtensions, exclusions) );
                }
            }
        }

        return filePathList;
    }

    private boolean isFileExtensionIn( String fileName, String[] fileExtensions, String[] exclusions ) {
        String fileNameLower = fileName.toLowerCase();

        if( Objects.isNull(fileExtensions) || fileExtensions.length == 0 ) {
            return !isFileExtensionNotIn(fileNameLower, exclusions);
        }


        for( String extension : fileExtensions ) {

            String testFiles = String.format("test%s", extension ).toLowerCase();
            if( isFileExtensionNotIn(fileNameLower, exclusions) && fileNameLower.endsWith(extension.toLowerCase()) && !fileNameLower.endsWith(testFiles) ) {
                return true;
            }
        }

        return false;
    }

    private boolean isFileExtensionNotIn( String fileName, String[] exclusions ) {
        if (Objects.isNull(exclusions))
            return true;

        for( String extension : exclusions ) {
            String fileNameLower = fileName.toLowerCase();
            if( fileNameLower.endsWith(extension.toLowerCase()) ) {
                return false;
            }
        }

        return true;
    }

    public Map<String, File> getPomFileMap() {
        if(Objects.isNull(pomFileMap)) {
            File projectDir = new File(projectPath);
            pomFileMap = new HashMap<>();
            pomFileMap = getPomFilePathMap(projectDir, "pom.xml");


        }
        return pomFileMap;
    }

    private Map<String, File> getPomFilePathMap( File directory, String fileName ) {
        Map<String, File> pomFilePathList = new HashMap<>();

        if( directory.exists() && directory.isDirectory() ) {
            File[] fileList = directory.listFiles();
            if( Objects.nonNull(fileList) ) {
                for( File file : fileList ) {
                    if( file.exists() && file.isDirectory() ) {
                        pomFilePathList.putAll(getPomFilePathMap(file, fileName));
                    } else {
                        if( file.exists() && file.isFile() ) {
                            if( file.getName().equals(fileName) ) {
                                pomFilePathList.put(file.getAbsolutePath(), file);
                            }
                        }
                    }
                }
            }
        }

        return pomFilePathList;
    }

    public Map<String, Integer> getFileTypesCountMap(String directoryPath) {
        File directory = new File(directoryPath);
        Map<String, Integer> resultMap = new HashMap<>();
        if( directory.exists() && directory.isDirectory() ) {
            List<String> fileListAbsolutePath = getFilesAbsolutePathList(directory);

            for( String absolutePath : fileListAbsolutePath ) {
                File f = new File(absolutePath);
                if( f.exists() ) {
                    String fileName = f.getName();
                    int lastIndexOf = fileName.lastIndexOf(".");

                    try {
                        if( lastIndexOf > 0 ) {
                            String fileExtension = fileName.substring(lastIndexOf);
                            if (resultMap.containsKey(fileExtension)) {
                                int count = resultMap.get(fileExtension);
                                resultMap.put(fileExtension, count + 1);
                            } else {
                                resultMap.put(fileExtension, 1);
                            }
                        }
                    } catch( Exception e ){
                        e.printStackTrace();
                    }
                }
            }
        }

        return resultMap;
    }

    public List<String> getFilesAbsolutePathList( File directory ) {
        List<String> fileList = new ArrayList<>();

        if( directory.exists() && directory.isDirectory() && Objects.nonNull(directory.listFiles()) ) {
            for( File subDirFile : directory.listFiles() ) {
                if( subDirFile.isFile() ) {
                    fileList.add(subDirFile.getAbsolutePath());
                }
                if( subDirFile.isDirectory() ){
                    fileList.addAll(getFilesAbsolutePathList(subDirFile));
                }
            }
        }

        return fileList;
    }
}
