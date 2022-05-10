delete from SYS_PAGE_DICTLIST where PAGE_ID='FinTenderBondAsset001' and DICT_ID='ISSUE_STATUS';
INSERT INTO SYS_PAGE_DICTLIST (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
VALUES ('FinTenderBondAsset001', '999999', 'ISSUE_STATUS', '01', null, null, 'system', null, sysdate, 'system', sysdate, null);
INSERT INTO SYS_PAGE_DICTLIST (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
VALUES ('FinTenderBondAsset001', '999999', 'ISSUE_STATUS', '02', null, null, 'system', null, sysdate, 'system', sysdate, null);

--配置审核页面
DELETE FROM SYS_PAGE WHERE PAGE_ID = 'FinTenderBondAsset002';
INSERT INTO SYS_PAGE (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES ('FinTenderBondAsset002', 'FIN', '/home.html?a=a#/approve/newbonetargets?a=a', 'vue', null, null, null, '02', '01', '新债标的新建', 'system', null, SYSDATE, 'system', SYSDATE);

DELETE FROM SYS_PAGE WHERE PAGE_ID = 'FinTenderBondAsset003';
INSERT INTO SYS_PAGE (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES ('FinTenderBondAsset003', 'FIN', '/home.html?a=a#/approve/newbonetargets/clation?a=a', 'vue', null, null, null, '02', '01', '新债标的上市流通', 'system', null, SYSDATE, 'system', SYSDATE);


--卡批
delete from sys_prebatch_task_add where GROUP_ID='preBatchCheckGrp' and TASK_ID='88616';
insert into sys_prebatch_task_add (GROUP_ID, TASK_ID, STOP_FLAG, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('preBatchCheckGrp', '88616', '01', '', 'system', '', sysdate, 'system', sysdate);

delete from sys_batch_task where TASK_ID='88616';
insert into sys_batch_task (TASK_ID, SYSTEM_CODE, TASK_NAME, FTASK_ID, BEAN_NAME, TASK_PRI, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('88616', 'INV', '日终是否自动上市流通', '', 'finTenderBondPreTask1', 26, 'system', '', sysdate, 'system', sysdate);

delete from sys_batch_job where BATCH_SEQ_NO='preBatchCheckGrp' and TASK_ID ='88616';
insert into sys_batch_job (BATCH_SEQ_NO, TASK_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('preBatchCheckGrp', '88616', 'system', '', sysdate, 'system', sysdate);

delete from sys_datadict_list where DICT_CODE ='CHECK_POINT' and ITEM_CODE='88616';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('CHECK_POINT', '88616', '日终是否自动上市流通', 13, '0', 'system', '', sysdate, 'system', sysdate, '');

delete from CFG_DATATABLE_SHOW where PAGECODE='FinTenderBondAsset001' and DTCODE='FinTenderBondAsset001List' and SPCDCODE='999999' and RANGEID='COMM' and COLCODE='finprodMarketId';
INSERT INTO CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
VALUES ('FinTenderBondAsset001', 'FinTenderBondAsset001List', '999999', 'finprodMarketId', '资产代码', null, 3, 1, 0, 1, 100, '{"colAlign": "left","supportSearch":"模糊"}', 0, 'COMM', 1, 0, null, null, 1);