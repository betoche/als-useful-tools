package org.als.random.utils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.als.random.enums.DatabaseTypeEnum;
import org.als.random.helper.StringHelper;
import org.als.teamconnect.entity.JApprCondition;
import org.als.teamconnect.entity.YDesignChange;
import org.als.teamconnect.entity.YObjRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.text.ParseException;
import java.util.*;

public class ObjRuleCreator {
    private final String username;
    private final String password;
    private final String database;
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjRuleCreator.class);

    public ObjRuleCreator(String username, String password, String database) {
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public YDesignChange createYDesignChangeApprCondition() throws ParseException {
        YDesignChange yDesignChange = new YDesignChange();
        yDesignChange.setCategoryTypeIid(14L);
        yDesignChange.setCreatedById(3L);
        yDesignChange.setCreatedOn("2025-01-28 12:46:51.0");
        yDesignChange.setDescription("Object Alberto Test: Rule Condition 1 for Rule New Rule Alberto 3 added; Rule New Rule Alberto 3 changed");
        String s = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Change>
                <Operation entityName="WRuleCondition" number="1" type="insert">
                <Properties>
                <conditionKey>.contact.firstName</conditionKey>
                <conditionQualifier>Equal To</conditionQualifier>
                <conditionValue/>
                <order>1</order>
                <isActive>true</isActive>
                <isCurrentUserUsed>false</isCurrentUserUsed>
                <prefixQualifier>Not</prefixQualifier>
                <rightConditionKey/>
                <isCurrentUserUsedOnRight>false</isCurrentUserUsedOnRight>
                <rightArgumentTypeIID>0</rightArgumentTypeIID>
                <oldValueTypeIID>0</oldValueTypeIID>
                <oldValue/>
                <newValueTypeIID>0</newValueTypeIID>
                </Properties>
                <Parent>
                <Id uniqueKey="NRA3"/>
                <Parent>
                <Id uniqueCode="ALTS"/>
                </Parent>
                </Parent>
                </Operation>
                <Operation entityName="ZRule" number="2" type="update">
                <Id uniqueKey="NRA3"/>
                <Properties>
                <customLogic>1</customLogic>
                </Properties>
                <Parent>
                <Id uniqueCode="ALTS"/>
                </Parent>
                </Operation>
                </Change>
                """;
        yDesignChange.setDesignChangeData(StringHelper.stringToByteWrapperArray(s));
        yDesignChange.setDesignChangeIndex(28L);
        //yDesignChange.setNotes(null);
        yDesignChange.setObjUniqueCode("ALTS");
        yDesignChange.setPrimaryKey(2503L);
        //yDesignChange.setRecentPackage(null);
        yDesignChange.setTcVersion("6.3.0.0071");
        yDesignChange.setVersion(2L);

        return yDesignChange;
    }
    public YDesignChange createYDesignChangeObjRule637() throws ParseException {
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

        return yDesignChange;
    }

    public JApprCondition createJApprCondition(){
        JApprCondition jApprCondition = new JApprCondition();
        jApprCondition.setApprovalRuleId(1002L);
        jApprCondition.setConditionKey(".contact.firstName");
        jApprCondition.setConditionOrder(1L);
        jApprCondition.setConditionQualifier("Equal To");
        //jApprCondition.setConditionValue(null);
        jApprCondition.setIsActive(1);
        //jApprCondition.setIsCurrentUser(null);
        //jApprCondition.setIsCurrentUserOnRight(null);
        jApprCondition.setNewValueTypeIid(0L);
        //jApprCondition.setObjectId(null);
        //jApprCondition.setOldValue(null);
        jApprCondition.setOldValueTypeIid(0L);
        jApprCondition.setPrefixQualifier("Not");
        jApprCondition.setPrimaryKey(1002L);
        jApprCondition.setRightArgumentTypeIid(0L);
        //jApprCondition.setRightConditionKey(null);
        //jApprCondition.setRightObjectId(null);
        jApprCondition.setVersion(1L);

        return jApprCondition;
    }

    public YObjRule createYObjRule() throws ParseException {
        YObjRule yObjRule = new YObjRule();
        //yObjRule.setActionDocumentId(null);
        //yObjRule.setActionName(null);
        //yObjRule.setActionOnRejectDocumentId(null);
        yObjRule.setActionOnRejectIid(0L);
        //yObjRule.setActionOnRejectTemplateId(null);
        yObjRule.setActionTypeIid(3L);
        yObjRule.setConditionLogicIid(0L);
        yObjRule.setCreatedById(3L);
        yObjRule.setCreatedOn("2025-01-28 12:44:23.0");
        yObjRule.setCustomActionIid(1L);
        yObjRule.setCustomLogic("1");
        yObjRule.setDelayUnitsIid(2L);
        yObjRule.setDelayValue(0L);
        yObjRule.setDescription("New Rule test for Alberto");
        yObjRule.setDuplicateApproverActionIid(0L);
        //yObjRule.setEndOn(null);
        yObjRule.setExpiration(7L);
        yObjRule.setExpireActionIid(3L);
        //yObjRule.setFromPhaseId(null);
        yObjRule.setHistoryCategoryId(10L);
        yObjRule.setHistoryDescriptionIid(1L);
        yObjRule.setHistoryLocationIid(2L);
        yObjRule.setHistoryObjectDefinitionId(21L);
        //yObjRule.setInterfaceTypeIid(null);
        //yObjRule.setIsActionActivate(null);
        //yObjRule.setIsActionAllocate(null);
        //yObjRule.setIsActionAssign(null);
        //yObjRule.setIsActionCheckIn(null);
        //yObjRule.setIsActionCheckOut(null);
        yObjRule.setIsActionCreate(1);
        //yObjRule.setIsActionDeactivate(null);
        yObjRule.setIsActionDelete(1);
        //yObjRule.setIsActionDeposit(null);
        //yObjRule.setIsActionPhaseChange(null);
        //yObjRule.setIsActionPost(null);
        //yObjRule.setIsActionTransferFrom(null);
        //yObjRule.setIsActionTransferTo(null);
        yObjRule.setIsActionUpdate(1);
        //yObjRule.setIsActionUserInvoke(null);
        //yObjRule.setIsActionVoid(null);
        //yObjRule.setIsActionWithdraw(null);
        yObjRule.setIsActive(1);
        //yObjRule.setIsAdditionalApprovalAllowed(null);
        //yObjRule.setIsAllowMultipleApproval(null);
        //yObjRule.setIsClassCondition(null);
        //yObjRule.setIsReassignAllowed(null);
        //yObjRule.setIsReviewAllowed(null);
        //yObjRule.setIsSendForApprovalAllowed(null);
        //yObjRule.setIsUpdateHistory(null);
        //yObjRule.setMessage(null);
        yObjRule.setModifiedById(3L);
        yObjRule.setModifiedOn("2025-01-28 12:46:51.0");
        //yObjRule.setNotificationTemplateId(null);
        yObjRule.setObjectDefinitionId(21L);
        //yObjRule.setPageId(null);
        yObjRule.setPhaseChangeTypeIid(2L);
        yObjRule.setPrimaryKey(1002L);
        yObjRule.setProcessManagerGroupId(1L);
        //yObjRule.setProcessManagerId(null);
        //yObjRule.setQualifierDocumentId(null);
        yObjRule.setRepeatDayofmonth(1L);
        yObjRule.setRepeatDayofweek(0L);
        yObjRule.setRepeatEndTypeIid(0L);
        yObjRule.setRepeatMinutes(0L);
        yObjRule.setRepeatMonth(1L);
        yObjRule.setRepeatTypeIid(0L);
        yObjRule.setRepeatWeekdaysOnly(0L);
        //yObjRule.setRouteId(null);
        yObjRule.setRuleOrder(3L);
        yObjRule.setRuleTypeIid(7L);
        yObjRule.setShortDescription("New Rule Alberto 3");
        //yObjRule.setStartOn(null);
        yObjRule.setStartTypeIid(0L);
        //yObjRule.setTemplateId(null);
        yObjRule.setTimesRepeated(1L);
        //yObjRule.setToPhaseId(null);
        //yObjRule.setTransitionId(null);
        yObjRule.setUniqueKey("NRA3");
        yObjRule.setUpdateWhilePendingIid(0L);
        yObjRule.setUseStopLevelParameter(0L);
        yObjRule.setVersion(2L);
        //yObjRule.setWizardId(null);
        //yObjRule.setXmlFormat(null);
        return yObjRule;
    }

    public Long getSequence(String tableKey) {
        String sqlQuery = String.format("SELECT SEQ_COUNT FROM [SEQUENCE] WHERE SEQ_NAME = '%s'", tableKey );
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Long returnVal = 0L;
        try {
            con = DBConnectionManager.getDatabaseConnection(DatabaseTypeEnum.SQL_SERVER, database, username, password, null, null);
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if( rs.next() ) {
                returnVal = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnectionManager.closeConnectionObjects(con, ps, rs);
        }

        return returnVal;
    }

    public boolean createRecordInDB( Object obj ) throws IllegalAccessException {
        boolean returnVal = false;
        Class<?> entityClass = obj.getClass();

        if (!isEntityClass(entityClass)) {
            throw new IllegalArgumentException(entityClass.getName() + " is not an entity class (missing @Entity annotation).");
        }

        Connection con = null;
        PreparedStatement ps = null;
        String sqlInsert = getSqlInsert(obj);

        try {
            con = DBConnectionManager.getDatabaseConnection(DatabaseTypeEnum.SQL_SERVER, database, username, password, null, null);
            ps = con.prepareStatement(sqlInsert);
            setInsertValues(ps, obj);
            ps.executeUpdate();
            returnVal = true;
        } catch( Exception e ) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            DBConnectionManager.closeConnectionObjects(con, ps, null);
        }

        return returnVal;
    }

    public void setInsertValues(PreparedStatement ps, Object entity ) throws IllegalAccessException, SQLException {
        int idx = 1;
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
                if(Objects.nonNull( field.get(entity) )) {
                    switch (field.getType().getSimpleName()) {
                        case "Long" -> ps.setLong(idx, unboxLong(field.get(entity)));
                        case "String" -> ps.setString(idx, field.get(entity).toString());
                        case "Timestamp" -> ps.setTimestamp(idx, (Timestamp) field.get(entity));
                        case "Integer" -> ps.setInt(idx, unboxInteger(field.get(entity)));
                        case "Byte[]" -> ps.setBytes(idx, convertByteArray( (Byte[])field.get(entity) ));
                    }
                    idx++;
                }
            }
        }
    }

    public long unboxLong( Object l ) {
        if(Objects.isNull(l))
            return 0L;

        return (Long)l;
    }
    public int unboxInteger( Object i ) {
        if( Objects.isNull(i) )
            return 0;

        return (Integer)i;
    }

    public byte[] convertByteArray(Byte[] byteArray) {
        if (byteArray == null) {
            return null; // Or throw an exception, depending on your needs
        }

        byte[] result = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            result[i] = byteArray[i]; // Autoboxing handles the conversion from Byte to byte
        }
        return result;
    }

    private String getSqlInsert( Object entity ) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        String tableName = clazz.getAnnotation(Table.class).name();

        List<String> columnNameList = new ArrayList<>();
        List<String> placeHolders = new ArrayList<>();

        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
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

    private boolean isEntityClass(Class<?> cls) {
        return cls != null && cls.isAnnotationPresent(Entity.class);
    }

    public void createCustomRule637( int count ) throws ParseException, IllegalAccessException {
        Long ruleOrder = 750L;
        Long designChangeIndex = 750L;

        for( int i = 0 ; i < count ; i++) {
            Long objRuleSeq = getSequence(YObjRule.Y_OBJ_RULE_SEQ_KEY);
            Long apprConditionSeq = getSequence(JApprCondition.J_APPR_CONDITION_SEQ_KEY);
            Long yDesignChangeSeq = getSequence(YDesignChange.Y_DESIGN_CHANGE_SEQ_KEY);

            Long objRulePrimaryKey = objRuleSeq + 1L;
            Long apprConditionPrimaryKey = apprConditionSeq + 1L;
            Long designChangePrimaryKey = yDesignChangeSeq + 1L;
            ruleOrder++;

            LOGGER.info(String.format("Progress: {%s of %s}, New keys: { %s: %s, rule_order: %s, %s: %s, %s, %s }", i, count, YObjRule.Y_OBJ_RULE_SEQ_KEY, objRulePrimaryKey, ruleOrder, JApprCondition.J_APPR_CONDITION_SEQ_KEY, apprConditionPrimaryKey, YDesignChange.Y_DESIGN_CHANGE_SEQ_KEY, designChangePrimaryKey));

            YObjRule rule = createYObjRule();
            String shortDescription = String.format("%s - %s", rule.getShortDescription(), objRulePrimaryKey);
            String uniqueKey = String.format("NAR%s", objRulePrimaryKey);
            String description = String.format("FAKE RULES GENERATED by ObjRuleCreator.class: {%s - %s}", rule.getDescription(), objRulePrimaryKey);
            String message = String.format("This is a message for the FAKE rule %s with short description: %s", uniqueKey, shortDescription);

            rule.setUniqueKey(uniqueKey);
            rule.setShortDescription(shortDescription);
            rule.setPrimaryKey(objRulePrimaryKey);
            rule.setDescription(description);
            rule.setMessage(message);
            rule.setRuleOrder(ruleOrder);
            //Missing fields
            rule.setIsClassCondition(0);

            boolean ruleInserted = createRecordInDB(rule);
            if (ruleInserted)
                DBConnectionManager.updateSequence(YObjRule.Y_OBJ_RULE_SEQ_KEY, objRuleSeq, database, username, password);
            else
                LOGGER.error("Rule not inserted, there was an error");

            JApprCondition apprCondition = createJApprCondition();
            apprCondition.setPrimaryKey(apprConditionPrimaryKey);
            apprCondition.setApprovalRuleId(objRulePrimaryKey);
            boolean apprConditionInserted = createRecordInDB(apprCondition);
            if (apprConditionInserted)
                DBConnectionManager.updateSequence(JApprCondition.J_APPR_CONDITION_SEQ_KEY, apprConditionSeq, database, username, password);
            else
                LOGGER.error("Approval condition not inserted, there was an error");

            if (ruleInserted && apprConditionInserted) {

                YDesignChange designChangeObjRule = createYDesignChangeObjRule637();
                designChangeObjRule.setPrimaryKey(designChangePrimaryKey);
                designChangeObjRule.setDesignChangeIndex(designChangeIndex);
                boolean designChange1Inserted = createRecordInDB(designChangeObjRule);
                designChangePrimaryKey++;
                designChangeIndex++;

                YDesignChange designChangeApprConditionRule = createYDesignChangeApprCondition();
                designChangeApprConditionRule.setPrimaryKey(designChangePrimaryKey);
                designChangeApprConditionRule.setDesignChangeIndex(designChangeIndex);
                boolean designChange2Inserted = createRecordInDB(designChangeApprConditionRule);
                designChangeIndex++;

                if (designChange1Inserted && designChange2Inserted)
                    DBConnectionManager.updateSequence(YDesignChange.Y_DESIGN_CHANGE_SEQ_KEY, yDesignChangeSeq, database, username, password);
                else
                    LOGGER.error(String.format("Error inserting design changes: { first: %s, second: %s}", designChange1Inserted, designChange2Inserted));
            }
        }

        LOGGER.info("Finished!");
    }

    public YObjRule createYObjRule710() throws ParseException {
        YObjRule yObjRule = new YObjRule();
        //yObjRule.setActionDocumentId(null);
        //yObjRule.setActionName(null);
        //yObjRule.setActionOnRejectDocumentId(null);
        yObjRule.setActionOnRejectIid(0L);
        //yObjRule.setActionOnRejectTemplateId(null);
        yObjRule.setActionTypeIid(3L);
        yObjRule.setConditionLogicIid(0L);
        yObjRule.setCreatedById(3L);
        yObjRule.setCreatedOn("2025-01-29 19:22:09.0");
        yObjRule.setCustomActionIid(1L);
        //yObjRule.setCustomLogic(null);
        yObjRule.setDelayUnitsIid(2L);
        yObjRule.setDelayValue(0L);
        yObjRule.setDescription("Description New Test Rule TRALS1");
        yObjRule.setDuplicateApproverActionIid(0L);
        //yObjRule.setEndOn(null);
        yObjRule.setExpiration(7L);
        yObjRule.setExpireActionIid(3L);
        //yObjRule.setFromPhaseId(null);
        yObjRule.setHistoryCategoryId(10L);
        yObjRule.setHistoryDescriptionIid(0L);
        yObjRule.setHistoryLocationIid(0L);
        yObjRule.setHistoryObjectDefinitionId(33L);
        //yObjRule.setInterfaceTypeIid(null);
        //yObjRule.setIsActionActivate(null);
        //yObjRule.setIsActionAllocate(null);
        //yObjRule.setIsActionAssign(null);
        //yObjRule.setIsActionCheckIn(null);
        //yObjRule.setIsActionCheckOut(null);
        //yObjRule.setIsActionCreate(null);
        //yObjRule.setIsActionDeactivate(null);
        yObjRule.setIsActionDelete(1);
        //yObjRule.setIsActionDeposit(null);
        //yObjRule.setIsActionPhaseChange(null);
        //yObjRule.setIsActionPost(null);
        //yObjRule.setIsActionTransferFrom(null);
        //yObjRule.setIsActionTransferTo(null);
        //yObjRule.setIsActionUpdate(null);
        //yObjRule.setIsActionUserInvoke(null);
        //yObjRule.setIsActionVoid(null);
        //yObjRule.setIsActionWithdraw(null);
        yObjRule.setIsActive(1);
        //yObjRule.setIsAdditionalApprovalAllowed(null);
        //yObjRule.setIsAllowMultipleApproval(null);
        //yObjRule.setIsClassCondition(null);
        //yObjRule.setIsReassignAllowed(null);
        //yObjRule.setIsReviewAllowed(null);
        //yObjRule.setIsSendForApprovalAllowed(null);
        //yObjRule.setIsUpdateHistory(null);
        //yObjRule.setMessage(null);
        yObjRule.setModifiedById(3L);
        yObjRule.setModifiedOn("2025-01-29 19:22:09.0");
        //yObjRule.setNotificationTemplateId(null);
        yObjRule.setObjectDefinitionId(33L);
        //yObjRule.setPageId(null);
        yObjRule.setPhaseChangeTypeIid(2L);
        yObjRule.setPrimaryKey(702L);
        yObjRule.setProcessManagerGroupId(1L);
        //yObjRule.setProcessManagerId(null);
        //yObjRule.setQualifierDocumentId(null);
        yObjRule.setRepeatDayofmonth(1L);
        yObjRule.setRepeatDayofweek(0L);
        yObjRule.setRepeatEndTypeIid(0L);
        yObjRule.setRepeatMinutes(0L);
        yObjRule.setRepeatMonth(1L);
        yObjRule.setRepeatTypeIid(0L);
        yObjRule.setRepeatWeekdaysOnly(0L);
        //yObjRule.setRouteId(null);
        yObjRule.setRuleOrder(1L);
        yObjRule.setRuleTypeIid(0L);
        yObjRule.setShortDescription("New Test Rule TRALS1");
        //yObjRule.setStartOn(null);
        yObjRule.setStartTypeIid(0L);
        //yObjRule.setTemplateId(null);
        yObjRule.setTimesRepeated(1L);
        //yObjRule.setToPhaseId(null);
        //yObjRule.setTransitionId(null);
        yObjRule.setUniqueKey("TRALS1");
        yObjRule.setUpdateWhilePendingIid(0L);
        yObjRule.setUseStopLevelParameter(0L);
        yObjRule.setVersion(1L);
        //yObjRule.setWizardId(null);
        //yObjRule.setXmlFormat(null);
        return yObjRule;
    }

    public YDesignChange createYDesignChangeObjRule710() throws ParseException {
    YDesignChange yDesignChange = new YDesignChange();
    yDesignChange.setCategoryTypeIid(14L);
    yDesignChange.setCreatedById(3L);
    yDesignChange.setCreatedOn("2025-01-29 19:25:40.0");
    yDesignChange.setDescription("Object Test Child Project: Rule New Test Rule TRALS1 added");
    String s = """
        <?xml version="1.0" encoding="UTF-8"?>
        <Change>
        <Operation entityName="ZRule" number="1" type="insert">
        <Properties>
        <shortDescription>New Test Rule TRALS1</shortDescription>
        <uniqueKey>TRALS1</uniqueKey>
        <isClassCondition>false</isClassCondition>
        <actionName/>
        <isActionActivate>false</isActionActivate>
        <isActionAllocate>false</isActionAllocate>
        <isActionAssign>false</isActionAssign>
        <isActionCheckIn>false</isActionCheckIn>
        <isActionCheckOut>false</isActionCheckOut>
        <isActionCreate>false</isActionCreate>
        <isActionDelete>true</isActionDelete>
        <isActionDeposit>false</isActionDeposit>
        <isActionDeactivate>false</isActionDeactivate>
        <isActionPhaseChange>false</isActionPhaseChange>
        <isActionPost>false</isActionPost>
        <isActionTransferFrom>false</isActionTransferFrom>
        <isActionTransferTo>false</isActionTransferTo>
        <isActionUpdate>false</isActionUpdate>
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
        <order>1</order>
        <typeIID>0</typeIID>
        <updateWhilePendingIID>0</updateWhilePendingIID>
        <xmlFormat/>
        <startTypeIID>0</startTypeIID>
        <startDelayUnitsIID>2</startDelayUnitsIID>
        <delayValue>0</delayValue>
        <startOn/>
        <historyLocationTypeIID>0</historyLocationTypeIID>
        <isUpdateHistory>false</isUpdateHistory>
        <historyDescriptionTypeIID>0</historyDescriptionTypeIID>
        <useStopLevelParameters>false</useStopLevelParameters>
        <isReviewAllowed>false</isReviewAllowed>
        <isSendForApprovalAllowed>false</isSendForApprovalAllowed>
        <isReassignAllowed>false</isReassignAllowed>
        <isAdditionalApprovalAllowed>false</isAdditionalApprovalAllowed>
        <customActionTypeIID>1</customActionTypeIID>
        <actionOnRejectIID>0</actionOnRejectIID>
        <duplicateApproverActionIID>0</duplicateApproverActionIID>
        <description>Description New Test Rule TRALS1</description>
        <repeatTypeIID>0</repeatTypeIID>
        <repeatMinutes>0</repeatMinutes>
        <repeatWeekdaysOnly>false</repeatWeekdaysOnly>
        <repeatDayOfWeek>0</repeatDayOfWeek>
        <repeatDayOfMonth>1</repeatDayOfMonth>
        <repeatMonth>1</repeatMonth>
        <repeatEndTypeIID>0</repeatEndTypeIID>
        <endOn/>
        <historyCategory>HIST</historyCategory>
        <historyObjectDefinition>TECH</historyObjectDefinition>
        <processManagerGroup>WorkflowProcessManager$</processManagerGroup>
        </Properties>
        <Parent>
        <Id uniqueCode="TECH"/>
        </Parent>
        </Operation>
        </Change>
        """;
    yDesignChange.setDesignChangeData( StringHelper.stringToByteWrapperArray(s) );
    yDesignChange.setDesignChangeIndex(25L);
    //yDesignChange.setNotes(null);
    yDesignChange.setObjUniqueCode("TECH");
    yDesignChange.setPrimaryKey(2002L);
    //yDesignChange.setRecentPackage(null);
    yDesignChange.setTcVersion("7.1.0.0001");
    yDesignChange.setVersion(2L);

        return yDesignChange;
    }

    public void createCustomRule710( int count ) throws ParseException, IllegalAccessException {
        Long ruleOrder = 20L;
        Long designChangeIndex = 25L;

        for( int i = 0 ; i < count ; i++) {
            Long objRuleSeq = getSequence(YObjRule.Y_OBJ_RULE_SEQ_KEY);
            Long yDesignChangeSeq = getSequence(YDesignChange.Y_DESIGN_CHANGE_SEQ_KEY);

            Long objRulePrimaryKey = objRuleSeq + 1L;
            Long designChangePrimaryKey = yDesignChangeSeq + 1L;
            ruleOrder++;

            LOGGER.info(String.format("Progress: {%s of %s}, New keys: { %s: %s, rule_order: %s, %s: %s }", i, count, YObjRule.Y_OBJ_RULE_SEQ_KEY, objRulePrimaryKey, ruleOrder, YDesignChange.Y_DESIGN_CHANGE_SEQ_KEY, designChangePrimaryKey));

            YObjRule rule = createYObjRule710();
            String shortDescription = String.format("%s - %s", rule.getShortDescription(), objRulePrimaryKey);
            String uniqueKey = String.format("NAR%s", objRulePrimaryKey);
            String description = String.format("FAKE RULES GENERATED by ObjRuleCreator.class: {%s - %s}", rule.getDescription(), objRulePrimaryKey);
            String message = String.format("This is a message for the FAKE rule %s with short description: %s", uniqueKey, shortDescription);

            rule.setUniqueKey(uniqueKey);
            rule.setShortDescription(shortDescription);
            rule.setPrimaryKey(objRulePrimaryKey);
            rule.setDescription(description);
            rule.setMessage(message);
            rule.setRuleOrder(ruleOrder);
            //Missing fields
            rule.setIsClassCondition(0);

            boolean ruleInserted = createRecordInDB(rule);
            if (ruleInserted)
                DBConnectionManager.updateSequence(YObjRule.Y_OBJ_RULE_SEQ_KEY, objRuleSeq, database, username, password);
            else
                LOGGER.error("Rule not inserted, there was an error");


            if (ruleInserted ) {
                YDesignChange designChangeObjRule = createYDesignChangeObjRule710();
                designChangeObjRule.setPrimaryKey(designChangePrimaryKey);
                designChangeObjRule.setDesignChangeIndex(designChangeIndex);
                boolean designChange1Inserted = createRecordInDB(designChangeObjRule);
                designChangeIndex++;


                if (designChange1Inserted )
                    DBConnectionManager.updateSequence(YDesignChange.Y_DESIGN_CHANGE_SEQ_KEY, yDesignChangeSeq, database, username, password);
                else
                    LOGGER.error(String.format("Error inserting design changes: { first: %s }", designChange1Inserted));
            }
        }

        LOGGER.info("Finished!");
    }

    public void bulkYObjRuleInsert( String filePath, Long createdBy, Long objectDefinitionId, Long ruleOrder, Long processManagerGroup ) throws IOException, IllegalAccessException {
        List<YObjRule> objRuleList = YObjRule.parseYObjRuleListFromFile(filePath);
        LOGGER.info(String.format("About to insert %s records in Y_OBJ_RULE table.", objRuleList.size()));
        int progress = 1;
        for( YObjRule objRule : objRuleList ) {
            ruleOrder++;
            Long objRuleSeq = getSequence(YObjRule.Y_OBJ_RULE_SEQ_KEY);
            Long primaryKey = objRuleSeq + 1L;
            objRule.setPrimaryKey(primaryKey);
            objRule.setObjectDefinitionId(objectDefinitionId);
            objRule.setHistoryObjectDefinitionId(objectDefinitionId);
            objRule.setRuleOrder(ruleOrder);
            objRule.setProcessManagerGroupId(processManagerGroup);
            objRule.setCreatedById(createdBy);
            objRule.setModifiedById(createdBy);
            objRule.setCustomActionIid(1L);
            objRule.setHistoryDescriptionIid(0L);
            objRule.setHistoryLocationIid(0L);
            objRule.setRuleTypeIid(0L);
            if(createRecordInDB(objRule)) {
                DBConnectionManager.updateSequence(YObjRule.Y_OBJ_RULE_SEQ_KEY, objRuleSeq, database, username, password);
                LOGGER.info(String.format("Progress: %s of %s, objRule_primar_key: %s", progress, objRuleList.size(), primaryKey));
                progress++;
            } else {
                LOGGER.error("Error inserting record in DB");
            }
        }
        LOGGER.info("Bulk YObjRuleInsert finished.");
    }
    public void printYObjRuleSubList( String filePath, int from, int to ) throws IOException, IllegalAccessException {
        List<YObjRule> objRuleList = YObjRule.parseYObjRuleListFromFile(filePath);
        List<YObjRule> subList = objRuleList.subList(from, to);
        String nl = System.lineSeparator();
        List<String> subListStr = new ArrayList<>();
        for ( YObjRule obj : subList) {
            subListStr.add(String.format("YObjRule: { primaryKey: %s, shortDescription: %s }", obj.getPrimaryKey(), obj.getShortDescription()));
        }

        LOGGER.info(String.format("ObjRules from %s to %s:%s%s", from, to, nl, (String.join(nl, subListStr))));
    }



    public static void main( String[] args ) throws ParseException, IllegalAccessException, IOException {
        String username = "teamconnect";
        String password = "password";
        String database = "teamconnect_710_custom";

        String filePath = "input/zfrule_list";
        Long createdBy = 3L;
        Long processManagerGroup = 203L;
        Long objectDefinitionId = 29L;
        Long ruleOrder = 100L;


        ObjRuleCreator objRuleCreator = new ObjRuleCreator(username, password, database);
        /*
        //objRuleCreator.createCustomRule(700);
        objRuleCreator.createCustomRule710(700);
        */

        //ActionUserInvoke
        //ObjRuleCreator.LOGGER.info( String.format("ZFRule: %s", objRuleCreator.printRuleKeyValue(text, true) ));
        //objRuleCreator.bulkYObjRuleInsert(filePath, createdBy, objectDefinitionId, ruleOrder, processManagerGroup);

        //List<YObjRule> yObjRuleList = YObjRule.parseYObjRuleListFromFile("input/zfrule_list");
        //ObjRuleCreator.LOGGER.info(String.format("Size: %s, List: %s", yObjRuleList.size(), yObjRuleList ));

        objRuleCreator.printYObjRuleSubList(filePath, 50, 60);

    }
}
