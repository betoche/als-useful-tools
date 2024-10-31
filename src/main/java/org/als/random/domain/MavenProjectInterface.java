package org.als.random.domain;

import java.util.List;

public interface MavenProjectInterface {
    void addModulePath( String modulePath );
    void setDependencyList( List<MavenDependency> dependencyList );
    void setDependencyManagementList( List<MavenDependencyManagement> dependencyManagementList );
}
