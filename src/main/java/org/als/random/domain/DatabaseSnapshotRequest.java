package org.als.random.domain;

import lombok.Getter;
import lombok.Setter;
import org.als.random.enums.DatabaseTypeEnum;

@Getter @Setter
public class DatabaseSnapshotRequest {
    private String name;
    private String username;
    private String password;
    private String host;
    private int port;
    private String title;
    private DatabaseTypeEnum databaseType;
    private boolean retrieveData;
}
