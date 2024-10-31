package org.als.random.domain;

import lombok.Builder;

@Builder
public class MavenModule {
    private String moduleName;
    private MavenProject project;
}
