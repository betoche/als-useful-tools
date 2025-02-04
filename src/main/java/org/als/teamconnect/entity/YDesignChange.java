package org.als.teamconnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.als.random.helper.DateHelper;
import org.als.random.helper.StringHelper;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="Y_DESIGN_CHANGE")
@Data
public class YDesignChange {
    @Id
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
    public void setCreatedOn(String s) throws ParseException {
        createdOn = new Timestamp(DateHelper.parseDateUS(s).getTime());
    }
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

    public static String Y_DESIGN_CHANGE_SEQ_KEY = "Y_DESIGN_CHANGE_SEQ";

    public static <T> List<String> getAnnotationValues(YDesignChange designChange) throws IllegalAccessException {
        List<String> annotationValues = new ArrayList<>();

        for (Field field : designChange.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                annotationValues.add(String.format("{ type: %s, column: %s, value: %s }", field.getType(), columnAnnotation.name(), field.get(designChange)));
            } else if (field.isAnnotationPresent(Id.class)) {
                annotationValues.add("@" + Id.class.getSimpleName());
            }
        }

        return annotationValues;
    }

    private static String getSqlInsert( Object entity ) throws IllegalAccessException {
        String tableName = entity.getClass().getAnnotation(Table.class).name();

        List<String> columnNameList = new ArrayList<>();
        List<String> placeHolders = new ArrayList<>();

        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                if(Objects.nonNull( field.get(entity) )) {
                    columnNameList.add(columnAnnotation.name());
                    placeHolders.add("?");
                }
            }
        }

        String columns = String.join(", ", columnNameList);
        String placeholders = String.join( ", ", placeHolders);

        return String.format("INSERT INTO %s (%s) VALUES(%s)", tableName, columns, placeholders);
    }

    public static void main(String[] args) throws ParseException, IllegalAccessException {
        YDesignChange yDesignChange = new YDesignChange();
        yDesignChange.setCategoryTypeIid(14L);
        yDesignChange.setCreatedById(3L);
        yDesignChange.setCreatedOn("2025-01-28 12:45:50.0" );
        yDesignChange.setDescription("Object Alberto Test: Rule New Rule Alberto 3 added");
        String s = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Change>
                <Operation entityName="ZRule" number="1" type="insert">
                <Properties>
                <shortDescription>New Rule Alberto 3</shortDescription>
                <uniqueKey>NRA3</uniqueKey>
                <isClassCondition>false</isClassCondition>
                <actionName/>
                <isActionActivate>false</isActionActivate>
                <isActionAllocate>false</isActionAllocate>
                <isActionAssign>false</isActionAssign>
                <isActionCheckIn>false</isActionCheckIn>
                <isActionCheckOut>false</isActionCheckOut>
                <isActionCreate>true</isActionCreate>
                <isActionDelete>true</isActionDelete>
                <isActionDeposit>false</isActionDeposit>
                <isActionDeactivate>false</isActionDeactivate>
                <isActionPhaseChange>false</isActionPhaseChange>
                <isActionPost>false</isActionPost>
                <isActionTransferFrom>false</isActionTransferFrom>
                <isActionTransferTo>false</isActionTransferTo>
                <isActionUpdate>true</isActionUpdate>
                <isActionVoid>false</isActionVoid>
                <isActionWithdraw>false</isActionWithdraw>
                <isActionUserInvoke>false</isActionUserInvoke>
                <actionTypeIID>3</actionTypeIID>
                <phaseChangeTypeIID>2</phaseChangeTypeIID>
                <customLogic/>
                <conditionLogicIID>0</conditionLogicIID>
                <expiration>7</expiration>
                <expireActionIID>3</expireActionIID>
                <timesRepeated>1</timesRepeated>
                <isActive>true</isActive>
                <isAllowMultipleApproval>false</isAllowMultipleApproval>
                <message/>
                <order>3</order>
                <typeIID>7</typeIID>
                <updateWhilePendingIID>0</updateWhilePendingIID>
                <xmlFormat/>
                <startTypeIID>0</startTypeIID>
                <startDelayUnitsIID>2</startDelayUnitsIID>
                <delayValue>0</delayValue>
                <startOn/>
                <historyLocationTypeIID>2</historyLocationTypeIID>
                <isUpdateHistory>false</isUpdateHistory>
                <historyDescriptionTypeIID>1</historyDescriptionTypeIID>
                <useStopLevelParameters>false</useStopLevelParameters>
                <isReviewAllowed>false</isReviewAllowed>
                <isSendForApprovalAllowed>false</isSendForApprovalAllowed>
                <isReassignAllowed>false</isReassignAllowed>
                <isAdditionalApprovalAllowed>false</isAdditionalApprovalAllowed>
                <customActionTypeIID>1</customActionTypeIID>
                <actionOnRejectIID>0</actionOnRejectIID>
                <duplicateApproverActionIID>0</duplicateApproverActionIID>
                <description>New Rule test for Alberto</description>
                <repeatTypeIID>0</repeatTypeIID>
                <repeatMinutes>0</repeatMinutes>
                <repeatWeekdaysOnly>false</repeatWeekdaysOnly>
                <repeatDayOfWeek>0</repeatDayOfWeek>
                <repeatDayOfMonth>1</repeatDayOfMonth>
                <repeatMonth>1</repeatMonth>
                <repeatEndTypeIID>0</repeatEndTypeIID>
                <endOn/>
                <historyCategory>HIST</historyCategory>
                <historyObjectDefinition>ALTS</historyObjectDefinition>
                <processManagerGroup>WorkflowProcessManager$</processManagerGroup>
                </Properties>
                <Parent>
                <Id uniqueCode="ALTS"/>
                </Parent>
                </Operation>
                </Change>
                """;
        yDesignChange.setDesignChangeData(StringHelper.stringToByteWrapperArray(s));
        yDesignChange.setDesignChangeIndex(27L);
        //yDesignChange.setNotes(null);
        yDesignChange.setObjUniqueCode("ALTS");
        yDesignChange.setPrimaryKey(2502L);
        //yDesignChange.setRecentPackage(null);
        yDesignChange.setTcVersion("6.3.0.0071");
        yDesignChange.setVersion(2L);

        List<String> annotationValues = getAnnotationValues(yDesignChange);
        System.out.println("Annotation Values: " + annotationValues);

        String sqlInsert = getSqlInsert(yDesignChange);
        System.out.println(sqlInsert);
    }


}