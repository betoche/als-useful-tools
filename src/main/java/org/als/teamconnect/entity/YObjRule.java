package org.als.teamconnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.als.random.helper.DateHelper;
import org.als.random.helper.StringHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Entity
@Table(name="Y_OBJ_RULE")
@Data
public class YObjRule {
    @Column(name="PRIMARY_KEY")
    private Long primaryKey;
    @Column(name="ACTION_ON_REJECT_IID")
    private Long actionOnRejectIid;
    @Column(name="CONDITION_LOGIC_IID")
    private Long conditionLogicIid;
    @Column(name="CREATED_BY_ID")
    private Long createdById;
    @Column(name="CUSTOM_ACTION_IID")
    private Long customActionIid;
    @Column(name="DUPLICATE_APPROVER_ACTION_IID")
    private Long duplicateApproverActionIid;
    @Column(name="EXPIRATION")
    private Long expiration;
    @Column(name="EXPIRE_ACTION_IID")
    private Long expireActionIid;
    @Column(name="HISTORY_DESCRIPTION_IID")
    private Long historyDescriptionIid;
    @Column(name="HISTORY_LOCATION_IID")
    private Long historyLocationIid;
    @Column(name="IS_ACTION_ACTIVATE")
    private Integer isActionActivate;
    @Column(name="IS_ACTION_ALLOCATE")
    private Integer isActionAllocate;
    @Column(name="IS_ACTION_ASSIGN")
    private Integer isActionAssign;
    @Column(name="IS_ACTION_CHECK_IN")
    private Integer isActionCheckIn;
    @Column(name="IS_ACTION_CHECK_OUT")
    private Integer isActionCheckOut;
    @Column(name="IS_ACTION_CREATE")
    private Integer isActionCreate;
    @Column(name="IS_ACTION_DEACTIVATE")
    private Integer isActionDeactivate;
    @Column(name="IS_ACTION_DELETE")
    private Integer isActionDelete;
    @Column(name="IS_ACTION_DEPOSIT")
    private Integer isActionDeposit;
    @Column(name="IS_ACTION_PHASE_CHANGE")
    private Integer isActionPhaseChange;
    @Column(name="IS_ACTION_POST")
    private Integer isActionPost;
    @Column(name="IS_ACTION_TRANSFER_FROM")
    private Integer isActionTransferFrom;
    @Column(name="IS_ACTION_TRANSFER_TO")
    private Integer isActionTransferTo;
    @Column(name="IS_ACTION_UPDATE")
    private Integer isActionUpdate;
    @Column(name="IS_ACTION_USER_INVOKE")
    private Integer isActionUserInvoke;
    @Column(name="IS_ACTION_VOID")
    private Integer isActionVoid;
    @Column(name="IS_ACTION_WITHDRAW")
    private Integer isActionWithdraw;
    @Column(name="IS_ACTIVE")
    private Integer isActive;
    @Column(name="IS_ADDITIONAL_APPROVAL_ALLOWED")
    private Integer isAdditionalApprovalAllowed;
    @Column(name="IS_ALLOW_MULTIPLE_APPROVAL")
    private Integer isAllowMultipleApproval;
    @Column(name="IS_CLASS_CONDITION")
    private Integer isClassCondition;
    @Column(name="IS_REASSIGN_ALLOWED")
    private Integer isReassignAllowed;
    @Column(name="IS_REVIEW_ALLOWED")
    private Integer isReviewAllowed;
    @Column(name="IS_SEND_FOR_APPROVAL_ALLOWED")
    private Integer isSendForApprovalAllowed;
    @Column(name="MODIFIED_BY_ID")
    private Long modifiedById;
    @Column(name="OBJECT_DEFINITION_ID")
    private Long objectDefinitionId;
    @Column(name="REPEAT_END_TYPE_IID")
    private Long repeatEndTypeIid;
    @Column(name="REPEAT_TYPE_IID")
    private Long repeatTypeIid;
    @Column(name="RULE_ORDER")
    private Long ruleOrder;
    @Column(name="RULE_TYPE_IID")
    private Long ruleTypeIid;
    @Column(name="START_TYPE_IID")
    private Long startTypeIid;
    @Column(name="TIMES_REPEATED")
    private Long timesRepeated;
    @Column(name="UPDATE_WHILE_PENDING_IID")
    private Long updateWhilePendingIid;
    @Column(name="USE_STOP_LEVEL_PARAMETER")
    private Long useStopLevelParameter;
    @Column(name="VERSION")
    private Long version;
    @Column(name="CREATED_ON")
    private Timestamp createdOn;

    public static YObjRule parseFromString(String zfRuleStr) throws IllegalAccessException {
        Map<String, Object> keyValuesMap = StringHelper.getRuleKeyValue(zfRuleStr, true);
        YObjRule objRule = new YObjRule();

        assert keyValuesMap != null;

        for( Map.Entry<String, Object> entry : keyValuesMap.entrySet() ) {
            for( Field field : objRule.getClass().getDeclaredFields() ) {
                field.setAccessible(true);
                if( entry.getKey().toLowerCase().contains(field.getName().toLowerCase()) ) {
                    Object value = entry.getValue();
                    if( value.toString().equalsIgnoreCase("false") ) {
                        value = 0L;
                    } else if( value.toString().equalsIgnoreCase("true") ) {
                        value = 1L;
                    }

                    try {
                        switch (field.getType().getSimpleName()) {
                            case "Long" -> field.set(objRule, Long.valueOf(value.toString()));
                            case "String" -> field.set(objRule, entry.getValue().toString());
                            case "Boolean" -> field.set(objRule, value.toString().equalsIgnoreCase("true"));
                            case "Integer" -> field.set(objRule, Integer.valueOf(value.toString()));
                            case "Timestamp" -> {
                                Timestamp timestamp = null;
                                Date date = DateHelper.parseDateWithTimeZone(value.toString());
                                if(Objects.nonNull(date) ){
                                    timestamp = new Timestamp(date.getTime());
                                }

                                field.set(objRule, timestamp);
                            }
                        }
                    }catch( Exception e ){
                        e.printStackTrace();
                    }

                    break;
                }
            }
        }

        return objRule;
    }

    public static List<YObjRule> parseYObjRuleListFromFile(String filePath) throws IOException, IllegalAccessException {
        if( !(new File(filePath)).exists() )
            return new ArrayList<>();

        List<YObjRule> objRuleList = new ArrayList<>();
        List<String> lineList = Files.readAllLines(Path.of(filePath));
        for( String line : lineList ) {
            List<String> zfRuleChunkList = getZfRuleChunkList(line);
            for( String zfRuleStr : zfRuleChunkList ) {
                objRuleList.add(YObjRule.parseFromString(zfRuleStr));
            }
        }

        return objRuleList;
    }

    public static List<String> getZfRuleChunkList( String line ) {
        String indexOfPattern = "ZFRule@";
        List<String> resultList = new ArrayList<>();
        int indexOfFrom = line.indexOf(indexOfPattern);
        int indexOfTo = line.substring(indexOfFrom+indexOfPattern.length()).indexOf(indexOfPattern);

        String zFRuleStr = "";
        try {
            if( indexOfTo <= 0 ){
                indexOfTo = line.length() - indexOfFrom;
            }
            zFRuleStr = line.substring(indexOfFrom, indexOfFrom + indexOfTo);
        } catch( Exception e ){
            e.printStackTrace();
        }
        resultList.add(zFRuleStr);
        if( line.substring((indexOfFrom + indexOfTo)).indexOf(indexOfPattern)>1 ) {
            String restOfString = line.substring((indexOfFrom + indexOfTo));
            resultList.addAll(getZfRuleChunkList(restOfString));
        }

        return resultList;
    }



    public void setCreatedOn(String dateStr) throws ParseException {
        this.createdOn = new Timestamp( DateHelper.parseDateUS(dateStr).getTime() );
    }
    @Column(name="MODIFIED_ON")
    private Timestamp modifiedOn;
    public void setModifiedOn(String dateStr) throws ParseException {
        this.modifiedOn = new Timestamp( DateHelper.parseDateUS(dateStr).getTime() );
    }
    @Column(name="SHORT_DESCRIPTION")
    private String shortDescription;
    @Column(name="UNIQUE_KEY")
    private String uniqueKey;
    @Column(name="ACTION_DOCUMENT_ID")
    private Long actionDocumentId;
    @Column(name="ACTION_ON_REJECT_DOCUMENT_ID")
    private Long actionOnRejectDocumentId;
    @Column(name="ACTION_ON_REJECT_TEMPLATE_ID")
    private Long actionOnRejectTemplateId;
    @Column(name="ACTION_TYPE_IID")
    private Long actionTypeIid;
    @Column(name="DELAY_UNITS_IID")
    private Long delayUnitsIid;
    @Column(name="DELAY_VALUE")
    private Long delayValue;
    @Column(name="FROM_PHASE_ID")
    private Long fromPhaseId;
    @Column(name="HISTORY_CATEGORY_ID")
    private Long historyCategoryId;
    @Column(name="HISTORY_OBJECT_DEFINITION_ID")
    private Long historyObjectDefinitionId;
    @Column(name="INTERFACE_TYPE_IID")
    private Long interfaceTypeIid;
    @Column(name="IS_UPDATE_HISTORY")
    private Integer isUpdateHistory;
    @Column(name="NOTIFICATION_TEMPLATE_ID")
    private Long notificationTemplateId;
    @Column(name="PAGE_ID")
    private Long pageId;
    @Column(name="PHASE_CHANGE_TYPE_IID")
    private Long phaseChangeTypeIid;
    @Column(name="PROCESS_MANAGER_GROUP_ID")
    private Long processManagerGroupId;
    @Column(name="PROCESS_MANAGER_ID")
    private Long processManagerId;
    @Column(name="QUALIFIER_DOCUMENT_ID")
    private Long qualifierDocumentId;
    @Column(name="REPEAT_DAYOFMONTH")
    private Long repeatDayofmonth;
    @Column(name="REPEAT_DAYOFWEEK")
    private Long repeatDayofweek;
    @Column(name="REPEAT_MINUTES")
    private Long repeatMinutes;
    @Column(name="REPEAT_MONTH")
    private Long repeatMonth;
    @Column(name="REPEAT_WEEKDAYS_ONLY")
    private Long repeatWeekdaysOnly;
    @Column(name="ROUTE_ID")
    private Long routeId;
    @Column(name="TEMPLATE_ID")
    private Long templateId;
    @Column(name="TO_PHASE_ID")
    private Long toPhaseId;
    @Column(name="TRANSITION_ID")
    private Long transitionId;
    @Column(name="WIZARD_ID")
    private Long wizardId;
    @Column(name="END_ON")
    private Timestamp endOn;
    @Column(name="START_ON")
    private Timestamp startOn;
    @Column(name="ACTION_NAME")
    private String actionName;
    @Column(name="CUSTOM_LOGIC")
    private String customLogic;
    @Column(name="DESCRIPTION")
    private String description;
    @Column(name="MESSAGE")
    private String message;
    @Column(name="XML_FORMAT")
    private String xmlFormat;

    public static String Y_OBJ_RULE_SEQ_KEY = "Y_OBJ_RULE_SEQ";
}
