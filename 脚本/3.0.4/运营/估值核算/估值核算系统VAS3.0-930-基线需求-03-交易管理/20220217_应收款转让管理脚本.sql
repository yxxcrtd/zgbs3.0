--应收款转让管理菜单
DELETE FROM SYS_MENU WHERE MENU_ID='DEAL_001_003_009';
INSERT INTO SYS_MENU
(MENU_ID, SYSTEM_CODE, ICON, PARENT_MENU_ID, PAGE_ID, ORDER_NO, IS_SPREAD, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('DEAL_001_003_009', 'VAL', NULL, 'DEAL_001_003', 'TrdSecOverTransferValuationDuration001', 9, '2', '应收款转让管理', 'system', NULL, sysdate, 'system', sysdate);

--应收款转让管理页面表
DELETE FROM SYS_PAGE WHERE PAGE_ID in('TrdSecOverTransferValuationDuration001','TrdSecOverTransferValuationDuration002');
INSERT INTO SYS_PAGE
(PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TrdSecOverTransferValuationDuration001', 'VAL', '/valuation/trademanage/trdsecovertransfervaluationduration/TrdSecOverTransferValuationDuration001', 'valuation/trademanage/trdsecovertransfervaluationduration/TrdSecOverTransferValuationDuration001', 'DEAL_001_003_009', NULL, NULL, '02', '01', '应收款转让管理查询', 'system', NULL, sysdate, 'system', sysdate);
INSERT INTO SYS_PAGE
(PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TrdSecOverTransferValuationDuration002', 'VAL', '/valuation/trademanage/trdsecovertransfervaluationduration/TrdSecOverTransferValuationDuration002', 'valuation/trademanage/trdsecovertransfervaluationduration/TrdSecOverTransferValuationDuration002', 'DEAL_001_003_009', NULL, NULL, '02', '01', '应收款转让管理新建', 'system', NULL, sysdate, 'system', sysdate);

--交易方向 画面数据字典明细关联
DELETE FROM SYS_PAGE_DICTLIST WHERE PAGE_ID='TrdSecOverTransferValuationDuration001' AND DICT_ID='PS';
INSERT INTO SYS_PAGE_DICTLIST
(PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
VALUES('TrdSecOverTransferValuationDuration001', '999999', 'PS', 'T25', '应收款项转入', NULL, 'system', NULL, sysdate, 'system', sysdate, NULL);
INSERT INTO SYS_PAGE_DICTLIST
(PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
VALUES('TrdSecOverTransferValuationDuration001', '999999', 'PS', 'T26', '应收款项转出', NULL, 'system', NULL, sysdate, 'system', sysdate, NULL);

--交易方向 画面数据字典明细关联
DELETE FROM SYS_PAGE_DICTLIST WHERE PAGE_ID='TrdSecOverTransferValuationDuration002' AND DICT_ID='PS';
INSERT INTO SYS_PAGE_DICTLIST
(PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
VALUES('TrdSecOverTransferValuationDuration002', '999999', 'PS', 'T25', '应收款项转入', NULL, 'system', NULL, sysdate, 'system', sysdate, NULL);
INSERT INTO SYS_PAGE_DICTLIST
(PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
VALUES('TrdSecOverTransferValuationDuration002', '999999', 'PS', 'T26', '应收款项转出', NULL, 'system', NULL, sysdate, 'system', sysdate, NULL);

--画面流程配置
DELETE FROM SYS_WFL_BUSINESS_MODULE WHERE PAGE_ID IN ('TrdSecOverTransferValuationDuration002');
INSERT INTO SYS_WFL_BUSINESS_MODULE
(PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
VALUES('TrdSecOverTransferValuationDuration002', 'TrdSecOverTransferValuationDuration001_btn_delete', 'oneApproval', 'system', NULL,sysdate, 'system', sysdate, 'ALL', '02');

INSERT INTO SYS_WFL_BUSINESS_MODULE
(PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
VALUES('TrdSecOverTransferValuationDuration002', 'TrdSecOverTransferValuationDuration002_btn_submit', 'oneApproval', 'system', NULL,sysdate, 'system', sysdate, 'ALL', '02');

INSERT INTO SYS_WFL_BUSINESS_MODULE
(PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
VALUES('TrdSecOverTransferValuationDuration002', 'TrdSecOverTransferValuationDuration002_btn_save', 'oneApproval', 'system', NULL,sysdate, 'system', sysdate, 'ALL', '02');












