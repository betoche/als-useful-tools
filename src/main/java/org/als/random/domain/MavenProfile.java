package org.als.random.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class MavenProfile implements MavenProjectInterface {
    private String id;
    private boolean isActiveByDefault;
    private Map<String, MavenModule> moduleMap;
    private List<MavenDependency> dependencyList;
    private List<MavenDependencyManagement> dependencyManagementList;

    public MavenProfile(){}

    public void addModulePath( String modulePath ) {
        if (Objects.isNull(moduleMap) ) {
            moduleMap = new HashMap<>();
        }

        moduleMap.put(modulePath, null);
    }
}
