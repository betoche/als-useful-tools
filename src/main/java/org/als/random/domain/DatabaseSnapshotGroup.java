package org.als.random.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.als.random.helper.FileDirHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter @Setter
@Builder
public class DatabaseSnapshotGroup {
    private File databaseGroupDir;
    private List<DatabaseSnapshot> snapshotList;

    public static DatabaseSnapshotGroup parse(File snapshotsDir, boolean withData) throws IOException {
        List<DatabaseSnapshot> snapshotList1 = new ArrayList<>();
        DatabaseSnapshotGroup.DatabaseSnapshotGroupBuilder builder = DatabaseSnapshotGroup.builder();
        builder.databaseGroupDir(snapshotsDir);

        if( FileDirHelper.containsSubDirectoriesOrSubFiles(snapshotsDir) ) {
            for( File snapshotFile : Objects.requireNonNull(snapshotsDir.listFiles())) {
                if( FileDirHelper.isValidFile( snapshotFile ) ) {
                    DatabaseSnapshot snapshot = DatabaseSnapshot.parseFromJsonFile(snapshotFile, withData);

                    if (Objects.nonNull(snapshot))
                        snapshotList1.add(snapshot);
                }
            }
            builder.snapshotList(snapshotList1);
        }

        return builder.build();
    }

    public int getSnapshotListSize() {
        int size = 0;

        if( Objects.nonNull(getSnapshotList()) )
            size = getSnapshotList().size();

        return size;
    }

    public String getSimpleDirectoryName(){
        return FileDirHelper.getSimpleDirectoryName(getDatabaseGroupDir());
    }

    public JSONArray getSnapshotJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for( DatabaseSnapshot snapshot : getSnapshotList() ) {
            jsonArray.put(snapshot.toJson());
        }
        return jsonArray;
    }
}
