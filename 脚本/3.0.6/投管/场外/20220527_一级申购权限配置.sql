
update SYS_MENU set page_id= 'TenderBondSub' where MENU_ID = 'DEAL_MANAGEMENT_001_001_007';

delete from SYS_PAGE WHERE PAGE_ID in( 'TenderBondSub');

INSERT INTO SYS_PAGE (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES ('TenderBondSub', 'TRD', '/home.html#/interbank/biddingTransaction?a=a', 'vue', 'DEAL_MANAGEMENT_001_001_007', null, null, '02', '01', '一级投标确认', 'system', null, sysdate, 'system', sysdate);

delete from SYS_PAGE_ELEMENT WHERE PAGE_ID in( 'TenderBondSub','TenderBondSub001','TenderBondSub002');

INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub', 'CfetsTenderBondSubTab', '01', '01', null, '一级申购', 'system', null, sysdate, 'system', sysdate, 'C', null);

INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub', 'TenderBondSub001_btn_receive', '01', '01', null, '批量接收', 'system', null, sysdate, 'system', sysdate, 'C', 'CfetsTenderBondSubTab');

INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub', 'TenderBondSub001_btn_giveUp', '01', '01', null, '批量放弃', 'system', null, sysdate, 'system', sysdate, 'C', 'CfetsTenderBondSubTab');

INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub', 'TenderBondSub001_btn_back', '01', '01', null, '退回', 'system', null, sysdate, 'system', sysdate, 'C', 'CfetsTenderBondSubTab');

INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub', 'TenderBondSub001_btn_deal', '01', '01', null, '申购', 'system', null, sysdate, 'system', sysdate, 'C', 'CfetsTenderBondSubTab');

INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub', 'TenderBondSub001_btn_giveUpSingle', '01', '01', null, '放弃', 'system', null, sysdate, 'system', sysdate, 'C', 'CfetsTenderBondSubTab');

INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub', 'TenderBondSub002_btn_save', '01', '01', null, '保存', 'system', null, sysdate, 'system', sysdate, 'C', 'CfetsTenderBondSubTab');

INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub', 'TenderBondSub002_btn_submit', '01', '01', null, '提交', 'system', null, sysdate, 'system', sysdate, 'C', 'CfetsTenderBondSubTab');

INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub', 'TenderBondSub001_btn_receiveSingle', '01', '01', null, '接收', 'system', null, sysdate, 'system', sysdate, 'C', 'CfetsTenderBondSubTab');