package org.als.random.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class DirectoryFileType implements FileStats, FileTypeStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileTypeStr;

    @OneToMany(mappedBy = "directoryFileType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DirectoryFile> directoryFiles;
    private Long linesCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_id")
    //@JdbcTypeCode(SqlTypes.INTEGER)
    private Directory directory;

    public void addDirectoryFile(DirectoryFile directoryFile) {
        this.directoryFiles.add(directoryFile);
        directoryFile.setDirectoryFileType(this);
    }

    public void removeDirectoryFile(DirectoryFile directoryFile) {
        this.directoryFiles.remove(directoryFile);
        directoryFile.setDirectoryFileType(null);
    }

    @Override
    public Integer getFilesCount() {
        return 0;
    }
}
