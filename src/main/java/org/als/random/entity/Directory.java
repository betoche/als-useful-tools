package org.als.random.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Data
public class Directory implements FileStats, FileTypeStats, DirectoryStats  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String directoryPath;
    private Long linesCount;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "parent_directory_id")
    private Directory parentDirectory;

    @OneToMany( mappedBy = "parentDirectory", cascade = CascadeType.ALL )
    @OrderBy("name ASC")
    private List<Directory> subDirectoryList;

    @OneToMany( mappedBy = "directory", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<DirectoryFile> directoryFiles;

    @OneToMany( mappedBy = "directory", cascade = CascadeType.ALL )
    private List<DirectoryFileType> directoryFileTypes;

    @Override
    public Integer getSubDirectoriesCount() {
        return getSubDirectoryList().size();
    }

    @Override
    public Integer getFilesCount() {
        return getDirectoryFiles().size();
    }

    public void addDirectoryFile( DirectoryFile directoryFile ) {
        directoryFiles.add(directoryFile);
        directoryFile.setDirectory(this);
    }

    public void removeDirectoryFile( DirectoryFile directoryFile ) {
        directoryFiles.remove(directoryFile);
        directoryFile.setDirectory(null);
    }
}
