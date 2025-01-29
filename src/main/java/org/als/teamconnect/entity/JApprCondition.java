package org.als.teamconnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class JApprCondition {
    @Column(name="PRIMARY_KEY")
    private Long primaryKey;
    @Column(name="APPROVAL_RULE_ID")
    private Long approvalRuleId;
    @Column(name="CONDITION_ORDER")
    private Long conditionOrder;
    @Column(name="IS_ACTIVE")
    private Integer isActive;
    @Column(name="IS_CURRENT_USER")
    private Integer isCurrentUser;
    @Column(name="IS_CURRENT_USER_ON_RIGHT")
    private Integer isCurrentUserOnRight;
    @Column(name="NEW_VALUE_TYPE_IID")
    private Long newValueTypeIid;
    @Column(name="OLD_VALUE_TYPE_IID")
    private Long oldValueTypeIid;
    @Column(name="RIGHT_ARGUMENT_TYPE_IID")
    private Long rightArgumentTypeIid;
    @Column(name="VERSION")
    private Long version;
    @Column(name="OBJECT_ID")
    private Long objectId;
    @Column(name="RIGHT_OBJECT_ID")
    private Long rightObjectId;
    @Column(name="CONDITION_KEY")
    private String conditionKey;
    @Column(name="CONDITION_QUALIFIER")
    private String conditionQualifier;
    @Column(name="PREFIX_QUALIFIER")
    private String prefixQualifier;
    @Column(name="RIGHT_CONDITION_KEY")
    private String rightConditionKey;
    @Column(name="CONDITION_VALUE")
    private String conditionValue;
    @Column(name="OLD_VALUE")
    private String oldValue;
}
