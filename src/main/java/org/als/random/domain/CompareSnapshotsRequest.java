package org.als.random.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CompareSnapshotsRequest {
    private String snapshot1;
    private String snapshot2;

    public List<String> getSnapshotList() {
        return List.of(snapshot1, snapshot2);
    }
}
