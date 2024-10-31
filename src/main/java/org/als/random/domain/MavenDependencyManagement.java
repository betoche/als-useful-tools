package org.als.random.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MavenDependencyManagement {
    private String groupId;
    private String artifactId;
    private String version;
}