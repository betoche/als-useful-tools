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

        public static final String WEB_INF_DIR_NAME = "WEB-INF";

        public String getJarFileSimpleName() {
            int lastIndexOf = jarFileName.lastIndexOf("\\");
            if( lastIndexOf < 1 ) {
                lastIndexOf = jarFileName.lastIndexOf("/");
            }

            return jarFileName.substring(lastIndexOf+1);
        }
        public String getPackagePath() {
            int lastIndexOf = classPath.lastIndexOf("target\\classes\\");
            if( lastIndexOf < 1 ){
                lastIndexOf = classPath.lastIndexOf("target/classes/");
            }
            return classPath.substring(lastIndexOf+15);
        }
        public String getJarLibPath() {
            int indexOf = jarFileName.indexOf(WEB_INF_DIR_NAME);
            int indexOfJar = jarFileName.indexOf(getJarFileSimpleName());

            if( indexOf < 0 || indexOfJar <= 0 ) {
                indexOf = classPath.lastIndexOf("teamconnectenterprise") + "teamconnectenterprise".length()+1;
                indexOfJar = classPath.lastIndexOf("classes") + "classes".length()+1;

                return classPath.substring(indexOf, indexOfJar);
            }

            return jarFileName.substring(indexOf, indexOfJar);

        }

        public Object getSimpleClassName() {
            int lastIndexOf = className.lastIndexOf("\\");
            if( lastIndexOf < 1 ){
                lastIndexOf = className.lastIndexOf("/");
            }

            return className.substring(lastIndexOf+1);
        }
    }
}
