package org.als.random;

import org.als.random.domain.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RandomLogger {
    private final Logger log;

    public RandomLogger(Class clazz) {
        log = LoggerFactory.getLogger(clazz);
    }

    public void log(MavenProject project) {
        log.info(".......................");
        log.info("...  Maven-Project  ...");
        log.info(".......................");
        log.info(String.format("  Project dir: %s", project.getDirectory()));
        log.info(String.format("    - Model Version       : %s", project.getModelVersion()));
        log.info(String.format("    - GroupId             : %s", project.getGroupId()));
        log.info(String.format("    - ArtifactId          : %s", project.getArtifactId()));
        log.info(String.format("    - Version             : %s", project.getVersion()));
        log.info(String.format("    - Packaging           : %s", project.getPackaging()));
        log.info(String.format("    - Name                : %s", project.getName()));

        log.info(String.format("      -- Modules count                 : %s", project.getModuleMap().size()));
        log.info(String.format("      -- Dependencies Management Count : %s", project.getDependencyManagementList().size()));
        log.info(String.format("      -- Dependencies Count            : %s", project.getDependencyList().size()));
        log.info(String.format("      -- Profile Count                 : %s", project.getProfileList().size()));
        log.info(".......................");
    }

    public void info( String str ) {
        log.info(str);
    }

    public void error(IOException e) {
        log.error(e.getMessage(), e);
    }

    public void error(String format) {
        log.error(format);
    }
}
