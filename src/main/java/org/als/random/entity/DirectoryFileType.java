package org.als.random.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Data
public class DirectoryFileType implements FileStats, FileTypeStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileTypeStr;
    private List<DirectoryFile> directoryFileList;
    private Long linesCount;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Directory directory;
    */

    @Override
    public Integer getFilesCount() {
        return 0;
    }
}
