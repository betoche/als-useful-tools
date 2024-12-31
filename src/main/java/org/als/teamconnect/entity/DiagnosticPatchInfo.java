package org.als.teamconnect.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor
public class DiagnosticPatchInfo {
    private String zipFileName;
    private String suggestedPatchInstructions;
    private List<FoundClass> foundClassFileList;

    @Getter @Setter @AllArgsConstructor
    public static class FoundClass {
        private String className;
        private String classPath;
        private String jarFileName;

        public String getJarFileSimpleName(){
            return jarFileName.substring(jarFileName.lastIndexOf("\\")+1);
        }
        public String getPackagePath(){
            return classPath.substring(classPath.lastIndexOf("target\\classes\\")+15);
        }
    }
}
