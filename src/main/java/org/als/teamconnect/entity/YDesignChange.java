package org.als.teamconnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name="Y_DESIGN_CHANGE")
@Data
public class YDesignChange {
    @Column(name="PRIMARY_KEY")
    private Long primaryKey;
    @Column(name="CATEGORY_TYPE_IID")
    private Long categoryTypeIid;
    @Column(name="DESIGN_CHANGE_INDEX")
    private Long designChangeIndex;
    @Column(name="VERSION")
    private Long version;
    @Column(name="CREATED_ON")
    private Timestamp createdOn;
    @Column(name="TC_VERSION")
    private String tcVersion;
    @Column(name="DESIGN_CHANGE_DATA")
    private Byte[] designChangeData;
    @Column(name="CREATED_BY_ID")
    private Long createdById;
    @Column(name="DESCRIPTION")
    private String description;
    @Column(name="NOTES")
    private String notes;
    @Column(name="OBJ_UNIQUE_CODE")
    private String objUniqueCode;
    @Column(name="RECENT_PACKAGE")
    private String recentPackage;
}
