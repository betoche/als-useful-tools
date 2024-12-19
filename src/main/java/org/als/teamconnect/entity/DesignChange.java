package org.als.teamconnect.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="Y_DESIGN_CHANGE")
@Getter @Setter
public class DesignChange {
    /*
        PRIMARY_KEY int NOT NULL,
        CATEGORY_TYPE_IID int NOT NULL,
        DESIGN_CHANGE_INDEX int NOT NULL,
        VERSION int NOT NULL,
        CREATED_ON datetime2 NOT NULL,
        TC_VERSION nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
        DESIGN_CHANGE_DATA image NOT NULL,
        CREATED_BY_ID int NULL,
        DESCRIPTION nvarchar(2000) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
        NOTES nvarchar(2000) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
        OBJ_UNIQUE_CODE nvarchar(4) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
        RECENT_PACKAGE nvarchar(2000) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
     */

    private @Id @Column(name="PRIMARY_KEY") BigInteger primaryKey;
    private @Column(name="CATEGORY_TYPE_IID") Integer categoryTypeIId;
    private @Column(name="DESIGN_CHANGE_INDEX") Integer designChangeIndex;
    private @Column(name="VERSION") Integer version;
    private @Column(name="CREATED_ON") Date datetime2;
    private @Column(name="TC_VERSION") String tcVersion;
    private @Lob @Column(name="DESIGN_CHANGE_DATA") byte[] designChangeData;
    private @Column(name="CREATED_BY_ID") Integer createdById;
    private @Column(name="DESCRIPTION") String description;
    private @Column(name="NOTES") String notes;
    private @Column(name="OBJ_UNIQUE_CODE") String objUniqueCode;
    private @Column(name="RECENT_PACKAGE") String recentPackage;

    public DesignChange(){}

    public DesignChange(BigInteger primaryKey, Integer categoryTypeIId, Integer designChangeIndex, Integer version,
                        Date datetime2, String tcVersion, byte[] designChangeData, Integer createdById, String description,
                        String notes, String objUniqueCode, String recentPackage){
        this.primaryKey = primaryKey;
        this.categoryTypeIId = categoryTypeIId;
        this.designChangeIndex = designChangeIndex;
        this.version = version;
        this.datetime2 = datetime2;
        this.tcVersion = tcVersion;
        this.designChangeData = designChangeData;
        this.createdById = createdById;
        this.description = description;
        this.notes = notes;
        this.objUniqueCode = objUniqueCode;
        this.recentPackage = recentPackage;
    }

    public static List<DesignChange> parseFromObjectList(List<Object[]> objList ) {
        List<DesignChange> returnList = new ArrayList<>();

        for( Object[] obj : objList ) {
            BigInteger primaryKey = BigInteger.valueOf(Double.valueOf((double)obj[0]).longValue());
            Integer categoryTypeIId = (int)obj[1];
            Integer designChangeIndex = (int)obj[2];
            Integer version = (int)obj[3];
            Date datetime2 = null;
            try {
                datetime2 = parseDate(String.valueOf(obj[4]));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            String tcVersion = String.valueOf(obj[5]);
            byte[] designChangeData = null;
            Integer createdById = (int)obj[7];
            String description = String.valueOf(obj[8]);
            String notes = String.valueOf(obj[9]);
            String objUniqueCode = String.valueOf(obj[10]);
            String recentPackage = String.valueOf(obj[11]);

            returnList.add(new DesignChange( primaryKey, categoryTypeIId, designChangeIndex, version, datetime2,
                    tcVersion, designChangeData, createdById, description, notes, objUniqueCode, recentPackage));
        }

        return returnList;
    }

    private static Date parseDate( String dateStr ) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

        return sdf.parse(dateStr);
    }

}
