package org.als.random.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Data
public class DirectoryFile implements FileStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filePath;
    private Integer filesCount;
    private Long linesCount;

    /*
    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "directory_id")
    private Directory directory;
    */
}
