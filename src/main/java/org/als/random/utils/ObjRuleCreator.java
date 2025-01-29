package org.als.random.utils;

import org.als.teamconnect.entity.JApprCondition;
import org.als.teamconnect.entity.YObjRule;

import java.text.ParseException;

public class ObjRuleCreator {

    public JApprCondition createJApprCondition(){
        JApprCondition jApprCondition = new JApprCondition();
        jApprCondition.setApprovalRuleId(1002L);
        jApprCondition.setConditionKey(".contact.firstName");
        jApprCondition.setConditionOrder(1L);
        jApprCondition.setConditionQualifier("Equal To");
        //jApprCondition.setConditionValue();
        jApprCondition.setIsActive(1);
        jApprCondition.setIsCurrentUser(0);
        jApprCondition.setIsCurrentUserOnRight(0);
        jApprCondition.setNewValueTypeIid(0L);
        jApprCondition.setObjectId(0L);
        //jApprCondition.setOldValue();
        jApprCondition.setOldValueTypeIid(0L);
        jApprCondition.setPrefixQualifier("Not");
        jApprCondition.setPrimaryKey(1002L);
        jApprCondition.setRightArgumentTypeIid(0L);
        //jApprCondition.setRightConditionKey();
        jApprCondition.setRightObjectId(0L);
        jApprCondition.setVersion(1L);

        return jApprCondition;
    }

    public YObjRule createYObjRule() throws ParseException {
        YObjRule yObjRule = new YObjRule();
        yObjRule.setActionDocumentId(0L);
        yObjRule.setActionName("undefined");
        yObjRule.setActionOnRejectDocumentId(0L);
        yObjRule.setActionOnRejectIid(0L);
        yObjRule.setActionOnRejectTemplateId(0L);
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
        //yObjRule.setEndOn("undefined");
        yObjRule.setExpiration(7L);
        yObjRule.setExpireActionIid(3L);
        yObjRule.setFromPhaseId(0L);
        yObjRule.setHistoryCategoryId(10L);
        yObjRule.setHistoryDescriptionIid(1L);
        yObjRule.setHistoryLocationIid(2L);
        yObjRule.setHistoryObjectDefinitionId(21L);
        yObjRule.setInterfaceTypeIid(0L);
        yObjRule.setIsActionActivate(0);
        yObjRule.setIsActionAllocate(0);
        yObjRule.setIsActionAssign(0);
        yObjRule.setIsActionCheckIn(0);
        yObjRule.setIsActionCheckOut(0);
        yObjRule.setIsActionCreate(1);
        yObjRule.setIsActionDeactivate(0);
        yObjRule.setIsActionDelete(1);
        yObjRule.setIsActionDeposit(0);
        yObjRule.setIsActionPhaseChange(0);
        yObjRule.setIsActionPost(0);
        yObjRule.setIsActionTransferFrom(0);
        yObjRule.setIsActionTransferTo(0);
        yObjRule.setIsActionUpdate(1);
        yObjRule.setIsActionUserInvoke(0);
        yObjRule.setIsActionVoid(0);
        yObjRule.setIsActionWithdraw(0);
        yObjRule.setIsActive(1);
        yObjRule.setIsAdditionalApprovalAllowed(0);
        yObjRule.setIsAllowMultipleApproval(0);
        yObjRule.setIsClassCondition(0);
        yObjRule.setIsReassignAllowed(0);
        yObjRule.setIsReviewAllowed(0);
        yObjRule.setIsSendForApprovalAllowed(0);
        yObjRule.setIsUpdateHistory(0);
        yObjRule.setMessage("undefined");
        yObjRule.setModifiedById(3L);
        yObjRule.setModifiedOn("2025-01-28 12:46:51.0");
        yObjRule.setNotificationTemplateId(0L);
        yObjRule.setObjectDefinitionId(21L);
        yObjRule.setPageId(0L);
        yObjRule.setPhaseChangeTypeIid(2L);
        yObjRule.setPrimaryKey(1002L);
        yObjRule.setProcessManagerGroupId(1L);
        yObjRule.setProcessManagerId(0L);
        yObjRule.setQualifierDocumentId(0L);
        yObjRule.setRepeatDayofmonth(1L);
        yObjRule.setRepeatDayofweek(0L);
        yObjRule.setRepeatEndTypeIid(0L);
        yObjRule.setRepeatMinutes(0L);
        yObjRule.setRepeatMonth(1L);
        yObjRule.setRepeatTypeIid(0L);
        yObjRule.setRepeatWeekdaysOnly(0L);
        yObjRule.setRouteId(0L);
        yObjRule.setRuleOrder(3L);
        yObjRule.setRuleTypeIid(7L);
        yObjRule.setShortDescription("New Rule Alberto 3");
        //yObjRule.setStartOn("undefined");
        yObjRule.setStartTypeIid(0L);
        yObjRule.setTemplateId(0L);
        yObjRule.setTimesRepeated(1L);
        yObjRule.setToPhaseId(0L);
        yObjRule.setTransitionId(0L);
        yObjRule.setUniqueKey("NRA3");
        yObjRule.setUpdateWhilePendingIid(0L);
        yObjRule.setUseStopLevelParameter(0L);
        yObjRule.setVersion(2L);
        yObjRule.setWizardId(0L);
        yObjRule.setXmlFormat("undefined");
        return new YObjRule();
    }
}
