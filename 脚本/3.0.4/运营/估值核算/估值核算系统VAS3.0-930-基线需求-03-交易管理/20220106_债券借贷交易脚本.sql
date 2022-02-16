--债券借贷交易菜单
DELETE FROM SYS_MENU WHERE MENU_ID='DEAL_001_001_022';
INSERT INTO SYS_MENU
(MENU_ID, SYSTEM_CODE, ICON, PARENT_MENU_ID, PAGE_ID, ORDER_NO, IS_SPREAD, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('DEAL_001_001_022', 'VAL', NULL, 'DEAL_001_001', 'TrdBondLendTrade001', 22, '2', '债券借贷交易', 'system', NULL, sysdate, 'system', sysdate);

--债券借贷交易页面表
DELETE FROM SYS_PAGE WHERE PAGE_ID IN ('TrdBondLendTrade001','TrdBondLendTrade002','TrdBondLendTrade003');
INSERT INTO SYS_PAGE
(PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TrdBondLendTrade001', 'VAL', '/valuation/trademanage/bondlend/TrdBondLendTrade001', 'valuation/trademanage/bondlend/TrdBondLendTrade001', 'DEAL_001_001_022', NULL, NULL, '02', '01', '债券借贷交易查询', 'system', NULL, sysdate, 'system', sysdate);
INSERT INTO SYS_PAGE
(PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TrdBondLendTrade002', 'VAL', '/valuation/trademanage/bondlend/TrdBondLendTrade002', 'valuation/trademanage/bondlend/TrdBondLendTrade002', 'DEAL_001_001_022', NULL, NULL, '02', '01', '债券借贷交易新建', 'system', NULL, sysdate, 'system', sysdate);
INSERT INTO SYS_PAGE
(PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TrdBondLendTrade003', 'VAL', '/valuation/trademanage/bondlend/TrdBondLendTrade003', 'valuation/trademanage/bondlend/TrdBondLendTrade003', 'DEAL_001_001_022', NULL, NULL, '02', '01', '债券借贷交易到期确认', 'system', NULL, sysdate, 'system', sysdate);

--画面流程配置
DELETE FROM SYS_WFL_BUSINESS_MODULE WHERE PAGE_ID IN ('TrdBondLendTrade002','TrdBondLendTrade003');
INSERT INTO SYS_WFL_BUSINESS_MODULE
(PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
VALUES('TrdBondLendTrade002', 'TrdBondLendTrade001_btn_delete', 'oneApproval', 'system', NULL,sysdate, 'system', sysdate, 'ALL', '02');

INSERT INTO SYS_WFL_BUSINESS_MODULE
(PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
VALUES('TrdBondLendTrade002', 'TrdBondLendTrade002_btn_submit', 'oneApproval', 'system', NULL,sysdate, 'system', sysdate, 'ALL', '02');

INSERT INTO SYS_WFL_BUSINESS_MODULE
(PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
VALUES('TrdBondLendTrade002', 'TrdBondLendTrade002_btn_save', 'oneApproval', 'system', NULL,sysdate, 'system', sysdate, 'ALL', '02');

INSERT INTO SYS_WFL_BUSINESS_MODULE
(PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
VALUES('TrdBondLendTrade003', 'TrdBondLendTrade003_btn_submit', 'oneApproval', 'system', NULL,sysdate, 'system', sysdate, 'ALL', '02');

INSERT INTO SYS_WFL_BUSINESS_MODULE
(PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
VALUES('TrdBondLendTrade003', 'TrdBondLendTrade003_btn_save', 'oneApproval', 'system', NULL,sysdate, 'system', sysdate, 'ALL', '02');

--自增序列号
create sequence JYSL_SEQUENCE
minvalue 1
maxvalue 9999
start with 1
increment by 1
cache 20;










