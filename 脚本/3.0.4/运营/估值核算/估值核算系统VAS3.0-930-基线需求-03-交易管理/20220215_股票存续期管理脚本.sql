--股票存续期管理菜单
DELETE FROM SYS_MENU WHERE MENU_ID='DEAL_001_003_006';
INSERT INTO SYS_MENU
(MENU_ID, SYSTEM_CODE, ICON, PARENT_MENU_ID, PAGE_ID, ORDER_NO, IS_SPREAD, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('DEAL_001_003_006', 'VAL', NULL, 'DEAL_001_003', 'TrdStockValuationDuration001', 6, '2', '股票存续期管理', 'system', NULL, sysdate, 'system', sysdate);

--股票存续期管理页面表
DELETE FROM SYS_PAGE WHERE PAGE_ID in('TrdStockValuationDuration001','TrdStockValuationDuration002');
INSERT INTO SYS_PAGE
(PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TrdStockValuationDuration001', 'VAL', '/valuation/trademanage/trdstockvaluationduration/TrdStockValuationDuration001', 'valuation/trademanage/trdstockvaluationduration/TrdStockValuationDuration001', 'DEAL_001_003_006', NULL, NULL, '02', '01', '股票存续期管理查询', 'system', NULL, sysdate, 'system', sysdate);
INSERT INTO SYS_PAGE
(PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TrdStockValuationDuration002', 'VAL', '/valuation/trademanage/trdstockvaluationduration/TrdStockValuationDuration002', 'valuation/trademanage/trdstockvaluationduration/TrdStockValuationDuration002', 'DEAL_001_003_006', NULL, NULL, '02', '01', '股票存续期管理新建', 'system', NULL, sysdate, 'system', sysdate);

--画面流程配置
DELETE FROM SYS_WFL_BUSINESS_MODULE WHERE PAGE_ID IN ('TrdStockValuationDuration002');
INSERT INTO SYS_WFL_BUSINESS_MODULE
(PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
VALUES('TrdStockValuationDuration002', 'TrdStockValuationDuration001_btn_delete', 'oneApproval', 'system', NULL,sysdate, 'system', sysdate, 'ALL', '02');

INSERT INTO SYS_WFL_BUSINESS_MODULE
(PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
VALUES('TrdStockValuationDuration002', 'TrdStockValuationDuration002_btn_submit', 'oneApproval', 'system', NULL,sysdate, 'system', sysdate, 'ALL', '02');

INSERT INTO SYS_WFL_BUSINESS_MODULE
(PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
VALUES('TrdStockValuationDuration002', 'TrdStockValuationDuration002_btn_save', 'oneApproval', 'system', NULL,sysdate, 'system', sysdate, 'ALL', '02');












