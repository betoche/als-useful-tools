package org.als.teamconnect.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.compress.utils.BitInputStream;

import java.math.BigInteger;
import java.util.*;

/*
    Columns:
        PRIMARY_KEY int NOT NULL,
        DISPLAY_ORDER int NOT NULL,
        IS_ACTIVE int DEFAULT 1 NOT NULL,
        VERSION int NOT NULL,
        NAME nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
        TREE_POSITION nvarchar(250) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
        PARENT_ID int NULL,
        TABLE_ID int NULL,
 */
@Entity
@Table(name="Y_DETAIL_LOOKUP_ITEM")
@Getter
@Setter
public class DetailLookupItem{
    private @Id @Column(name="PRIMARY_KEY") BigInteger primaryKey;
    private @Column(name="DISPLAY_ORDER") Integer displayOrder;
    private @Column(name="IS_ACTIVE") boolean isActive;
    private @Column(name="VERSION") Integer version;
    private @Column(name="NAME") String name;
    private @Column(name="TREE_POSITION") String treePosition;
    private @Column(name="PARENT_ID") BigInteger parentId;
    private @Column(name="TABLE_ID") Integer tableId;

    private transient DetailLookupItem parent;
    private transient List<DetailLookupItem> children;
    private transient boolean existsInDB;
    private transient boolean parentExistsInDB;
    private transient String dbName;
    private transient String dbParentName;


    public DetailLookupItem(){}

    public DetailLookupItem(BigInteger primaryKey, Integer displayOrder, boolean isActive, Integer version, String name,
                            String treePosition, BigInteger parentId,Integer tableId) {
        this.primaryKey = primaryKey;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
        this.version = version;
        this.name = name;
        this.treePosition = treePosition;
        this.parentId = parentId;
        this.tableId = tableId;
    }

    public static List<DetailLookupItem> parseFromObjectList( List<Object[]> objList ) {
        List<DetailLookupItem> tmpReturnList = new ArrayList<>();

        for( Object[] obj : objList ) {
            BigInteger primaryKey = BigInteger.valueOf(Double.valueOf((double)obj[0]).longValue());
            Integer displayOrder = Double.valueOf((double) obj[1]).intValue();
            boolean isActive = ((double) obj[2]) == 1d;
            Integer version = Double.valueOf((double) obj[3]).intValue();
            String name = String.valueOf(obj[4]);
            String treePosition = String.valueOf(obj[5]);
            BigInteger parentId = null;
            if(!obj[6].toString().isEmpty())
                parentId = BigInteger.valueOf(Double.valueOf((double)obj[6]).longValue());
            Integer tableId = Double.valueOf((double) obj[7]).intValue();

            tmpReturnList.add(new DetailLookupItem(primaryKey, displayOrder, isActive, version, name, treePosition, parentId, tableId));
        }

        addTreeStructure(tmpReturnList);


        return tmpReturnList.stream().filter( i -> Objects.isNull(i.getParent())).toList();
    }

    private static void addTreeStructure( List<DetailLookupItem> list ) {
        Map<BigInteger, DetailLookupItem> map = new HashMap<>();
        for( DetailLookupItem d : list ) {
           map.put(d.getPrimaryKey(), d);
        }

        for( DetailLookupItem d : list ) {
            if( map.containsKey(d.getParentId()) ) {
                d.setParent(map.get(d.getParentId()));
                map.get(d.getParentId()).addChild(d);
            }
        }
    }

    public static int getTotalItemSize( List<DetailLookupItem> detailLookupItemList ) {
        int size = detailLookupItemList.size();
        for( DetailLookupItem item : detailLookupItemList ) {
            size += item.getChildrenSize();
        }

        return size;
    }

    public List<DetailLookupItem> getChildren(){
        if( Objects.isNull(this.children) ){
            this.children = new ArrayList<>();
        }
        return this.children;
    }

    public void addChild( DetailLookupItem child ){
        this.getChildren().add(child);
    }

    public int getChildrenSize() {
        int size = getChildren().size();
        for( DetailLookupItem child : getChildren() ) {
            size += child.getChildrenSize();
        }

        return size;
    }

    public String getDbInfo() {
        if( Objects.nonNull(getDbName()) )
            return String.format("DB Info: {exists: %s, dbName: %s}", isExistsInDB(), getDbName());

        return String.format("DB Info: {exists: %s}", isExistsInDB());
    }
}
