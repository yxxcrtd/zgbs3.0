--自动上市流通批
delete from sys_batch_task where TASK_ID ='10035';
delete from sys_batch_job where BATCH_SEQ_NO ='inv2'and TASK_ID = '10035';
insert into sys_batch_task (TASK_ID, SYSTEM_CODE, TASK_NAME, FTASK_ID, BEAN_NAME, TASK_PRI, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, IS_BATCH_START_TARGET)
values ('10035', 'INV', '新债自动上市流通', '', 'finTenderBondAssetTask', 25, 'system', '', sysdate, 'system', sysdate, '');
insert into sys_batch_job (BATCH_SEQ_NO, TASK_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('inv2', '10035', 'system', '', sysdate, 'system', sysdate);

--自动上市流通批前检查
--卡批
delete from sys_prebatch_task_add where GROUP_ID='preBatchCheckGrp' and TASK_ID='88616';
insert into sys_prebatch_task_add (GROUP_ID, TASK_ID, STOP_FLAG, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('preBatchCheckGrp', '88616', '01', '', 'system', '', sysdate, 'system', sysdate);

delete from sys_batch_task where TASK_ID='88616';
insert into sys_batch_task (TASK_ID, SYSTEM_CODE, TASK_NAME, FTASK_ID, BEAN_NAME, TASK_PRI, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('88616', 'INV', '日终是否自动上市流通', '', 'finTenderBondPreTask1', 26, 'system', '', sysdate, 'system', sysdate);

delete from sys_batch_job where BATCH_SEQ_NO='preBatchCheckGrp' and TASK_ID ='88615';
insert into sys_batch_job (BATCH_SEQ_NO, TASK_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('preBatchCheckGrp', '88615', 'system', '', sysdate, 'system', sysdate);

delete from sys_datadict_list where DICT_CODE ='CHECK_POINT' and ITEM_CODE='88616';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('CHECK_POINT', '88616', '日终是否自动上市流通', 13, '0', 'system', '', sysdate, 'system', sysdate, '');

--不卡批
delete from sys_prebatch_task_add where GROUP_ID='preBatchCheckGrp' and TASK_ID='88617';
insert into sys_prebatch_task_add (GROUP_ID, TASK_ID, STOP_FLAG, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('preBatchCheckGrp', '88617', '00', '', 'system', '', sysdate, 'system', sysdate);

delete from sys_batch_task where TASK_ID='88617';
insert into sys_batch_task (TASK_ID, SYSTEM_CODE, TASK_NAME, FTASK_ID, BEAN_NAME, TASK_PRI, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('88617', 'INV', '日终自动上市流通提醒', '', 'finTenderBondPreTask2', 27, 'system', '', sysdate, 'system', sysdate);

delete from sys_batch_job where BATCH_SEQ_NO='preBatchCheckGrp' and TASK_ID ='88617';
insert into sys_batch_job (BATCH_SEQ_NO, TASK_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('preBatchCheckGrp', '88617', 'system', '', sysdate, 'system', sysdate);

delete from sys_datadict_list where DICT_CODE ='CHECK_POINT' and ITEM_CODE='88617';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('CHECK_POINT', '88617', '日终自动上市流通提醒', 13, '0', 'system', '', sysdate, 'system', sysdate, '');