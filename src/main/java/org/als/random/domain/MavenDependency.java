package org.als.random.domain;

import lombok.Builder;

@Builder
public class MavenDependency {
    private String groupId;
    private String artifactId;
    private String version;
}
