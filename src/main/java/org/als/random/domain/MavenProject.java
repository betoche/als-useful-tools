package org.als.random.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
@Builder
public class MavenProject implements MavenProjectInterface {
    private String name;
    private String directory;
    private String modelVersion;
    private String groupId;
    private String artifactId;
    private String version;
    private String packaging;
    private MavenProject parentProject;
    private List<MavenDependency> dependencyList;
    private List<MavenDependencyManagement> dependencyManagementList;
    private Map<String, MavenModule> moduleMap;
    private List<MavenProfile> profileList;

    public void addModulePath( String modulePath ) {
        if (Objects.isNull(moduleMap) ) {
            moduleMap = new HashMap<>();
        }

        moduleMap.put(modulePath, null);
    }
}
