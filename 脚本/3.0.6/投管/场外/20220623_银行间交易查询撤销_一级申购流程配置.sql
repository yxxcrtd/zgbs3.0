delete from SYS_PAGE where PAGE_ID = 'CfetsTrdFirstBondInvestTrade002';
delete from SYS_WFL_BUSINESS_MODULE where PAGE_ID = 'CfetsTrdFirstBondInvestTrade002';

INSERT INTO SYS_PAGE (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('CfetsTrdFirstBondInvestTrade002', 'TRD', '/cfets/trdquery/CfetsTrdFirstBondInvestTrade002', 'cfets/trdquery/CfetsTrdFirstBondInvestTrade002', 'DEAL_MANAGEMENT_001_002_012', null, null, '02', '01', '银行间交易-一级申购撤销', 'system', null, sysdate, 'system', sysdate);

INSERT INTO SYS_WFL_BUSINESS_MODULE (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG) 
VALUES ('CfetsTrdFirstBondInvestTrade002', 'CfetsTrdFirstBondInvestTrade002_btn_save', 'oneApproval', 'system', '0001', sysdate, 'JT005', sysdate, 'ALL', '02');

INSERT INTO SYS_WFL_BUSINESS_MODULE (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG) 
VALUES ('CfetsTrdFirstBondInvestTrade002', 'CfetsTrdFirstBondInvestTrade002_btn_submit', 'oneApproval', 'system', '0001', sysdate, 'JT005', sysdate, 'ALL', '02');