package org.als.random.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DatabaseSnapshotRequest {
    private String name;
    private String username;
    private String password;
    private String host;
    private int port;
    private boolean retrieveData;
}
