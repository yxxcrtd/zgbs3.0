
DELETE  SYS_BATCH_TASK T WHERE T.TASK_ID = '603' AND T.BEAN_NAME ='valJixuTask';
insert into SYS_BATCH_TASK (TASK_ID, SYSTEM_CODE, TASK_NAME, FTASK_ID, BEAN_NAME, TASK_PRI, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) values ('603', 'VAL', '衍生品业务批处理', null, 'valJixuTask', 31, 'system', null, sysdate, 'system', sysdate);

DELETE SYS_BATCH_JOB T WHERE T.TASK_ID = '603' ;
insert into SYS_BATCH_JOB (BATCH_SEQ_NO, TASK_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) values ('6f8803b3aa6fbaffe050007f01003cad', '603', 'system', null, sysdate, 'system', sysdate);


