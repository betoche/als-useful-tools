package org.als.random.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DirectoryFile implements FileStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filePath;
    private Integer filesCount;
    private Long linesCount;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn(name = "directory_file_type_id")
    private DirectoryFileType directoryFileType;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn(name = "directory_id")
    //@JdbcTypeCode(SqlTypes.INTEGER)
    private Directory directory;
}
