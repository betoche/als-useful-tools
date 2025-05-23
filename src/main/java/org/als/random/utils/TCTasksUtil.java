package org.als.random.utils;

import org.als.random.enums.DatabaseTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class TCTasksUtil {
    private String databaseName;
    private String username;
    private String password;
    private DatabaseTypeEnum databaseTypeEnum;
    private String dbHost;
    private int dbPort;

    private static final Logger LOGGER = LoggerFactory.getLogger(TCTasksUtil.class);

    public TCTasksUtil(String databaseName, String username, String password, DatabaseTypeEnum databaseTypeEnum, String dbHost, int dbPort) {
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.databaseTypeEnum = databaseTypeEnum;
        this.dbHost = dbHost;
        this.dbPort = dbPort;
    }

    private void insertTasks(int numberOfInserts) throws SQLException {
        int taskPrimaryKey = DBConnectionManager.getLastPrimaryKeyOf("T_TASK", databaseName, username, password, databaseTypeEnum, dbHost, dbPort).intValue();
        int taskDetailPrimaryKey = DBConnectionManager.getLastPrimaryKeyOf("E_TASK_DETAIL", databaseName, username, password, databaseTypeEnum, dbHost, dbPort).intValue();
        int taskAssigneePrimaryKey = DBConnectionManager.getLastPrimaryKeyOf("J_TASK_ASSIGNEE", databaseName, username, password, databaseTypeEnum, dbHost, dbPort).intValue();
        int recordChangePrimaryKey = DBConnectionManager.getLastPrimaryKeyOf("Y_RECORD_CHANGE", databaseName, username, password, databaseTypeEnum, dbHost, dbPort).intValue();

        for(int i = 0 ; i < numberOfInserts ; i++) {
            taskPrimaryKey++;
            taskDetailPrimaryKey++;
            taskAssigneePrimaryKey++;
            recordChangePrimaryKey++;
            String taskName = "Task Name %s bulk created".formatted(taskPrimaryKey);

            String taskInsert = """
                    INSERT INTO T_TASK
                    (PRIMARY_KEY, ACCEPTANCE_IID, ACTUAL_HOURS, COMPLETED_PERCENT, CREATED_BY_ID, DEFAULT_CATEGORY_ID, EMAIL_ALERT_NUM_DAYS_ADVANCE, ESTIMATED_HOURS, IS_EMAIL_NOTIFICATION_ACTIVE, IS_TASK_EXPIRATION_SENT, MODIFIED_BY_ID, PRIORITY_TYPE_IID, RATE_AMOUNT, SECURITY_TYPE_IID, STATUS_IID, TOTAL_AMOUNT, VERSION, WORK_STATUS_IID, CREATED_ON, MODIFIED_ON, SHORT_DESCRIPTION, ACTIVITY_ITEM_ID, APPROVAL_STATUS_ID, CONTACT_ID, CREATED_ON_BEHALF_OF_ID, CURRENT_ASSIGNEE_ID, CURRENT_ASSIGNEE_USER_ID, DOCUMENT_FOLDER_ID, DOCU_SOURCE_LOCATION_ID, FORUM_ID, FORWARDED_BY_ASSIGNEE_ID, MILESTONE_ID, NOTE_ID, PROJECT_ID, TRANSACTION_ID, COMPLETED_ON, DUE_ON, START_ON)
                    VALUES(%s, 1, 0, 0, 3, 17, 5, 0, 0, 0, 3, 3, 0, 0, 1, 0, 1, 1, TIMESTAMP '2025-05-09 00:52:38.000000', TIMESTAMP '2025-05-09 00:52:38.000000', '%s', NULL, NULL, NULL, NULL, 4, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)
                    """.formatted(taskPrimaryKey, taskName).replace("\r\n", " ").replace("\r", " ").replace("\n", " ");
            String taskDetailInsertInsertQuery = """
                    INSERT INTO E_TASK_DETAIL
                    (PRIMARY_KEY, CATEGORY_ID, ENTERPRISE_OBJECT_ID, IS_MANUAL, VERSION)
                    VALUES(%s, 17, %s, 0, 1)
                    """.formatted(taskDetailPrimaryKey, taskPrimaryKey).replace("\r\n", " ").replace("\r", " ").replace("\n", " ");

            String taskAssigneeInsertQuery = """
                    INSERT INTO J_TASK_ASSIGNEE
                    (PRIMARY_KEY, IS_ASSIGNEE_INFORMED, VERSION, ASSIGNED_ON, TASK_ID, USER_ID)
                    VALUES(%s, 0, 1, TIMESTAMP '2025-05-09 00:52:38.000000', %s, 3)
                    """.formatted(taskAssigneePrimaryKey, taskPrimaryKey).replace("\r\n", " ").replace("\r", " ").replace("\n", " ");

            String recordChangeInsertQuery = """
                    INSERT INTO Y_RECORD_CHANGE
                    (PRIMARY_KEY, IS_DELETE, IS_NAME_CHANGE, RECORD_PRIMARY_KEY, VERSION, RECORD_ENTITY_CODE, RECORD_UNIQUE_CODE)
                    VALUES(%s, 0, 0, %s, 1, 'TASK', 'TASK')
                    """.formatted(recordChangePrimaryKey, taskPrimaryKey).replace("\r\n", " ").replace("\r", " ").replace("\n", " ");


            try ( Connection con = DBConnectionManager.getDatabaseConnection(databaseTypeEnum, databaseName, username, password, dbHost, dbPort) ) {
                int taskInsertResult = DBConnectionManager.executeRecordInsert(taskInsert, con);
                int taskDetailInsertResult = DBConnectionManager.executeRecordInsert(taskDetailInsertInsertQuery, con);
                int taskAssigneeInsertResult = DBConnectionManager.executeRecordInsert(taskAssigneeInsertQuery, con);
                int recordChangeInsertResult = DBConnectionManager.executeRecordInsert(recordChangeInsertQuery, con);

                LOGGER.info("insert results[%s]: { task: %s, taskDetail: %s, taskAssignee: %s, recordChange: %s }".formatted(i, taskInsertResult, taskDetailInsertResult, taskAssigneeInsertResult, recordChangeInsertResult));
            }
        }
    }

    public static void main( String[] args ) {
        LOGGER.info("");
        LOGGER.info("++++++++++++++++++++++++++++++");
        LOGGER.info("++ TCTaskUtil main started! ++");
        LOGGER.info("++++++++++++++++++++++++++++++");
        String databaseName = "xe";
        String username = "SOH4918_TCE637PB8";
        String password = "password";
        DatabaseTypeEnum databaseTypeEnum = DatabaseTypeEnum.ORACLE;
        String dbHost = "localhost";
        int dbPort = 1521;

        TCTasksUtil tcTasksUtil = new TCTasksUtil(databaseName, username, password, databaseTypeEnum, dbHost, dbPort);
        try {
            tcTasksUtil.insertTasks(2000);
        } catch (SQLException e) {
            LOGGER.error("%s: %s".formatted(e.toString(), e.getMessage()), e);
        } finally {
            LOGGER.info("+++++++++++++++++++++++++++++++");
            LOGGER.info("++ TCTaskUtil main finished! ++");
            LOGGER.info("+++++++++++++++++++++++++++++++");
        }
    }
}
