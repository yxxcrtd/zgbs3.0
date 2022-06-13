delete from SYS_PAGE where PAGE_ID in ('TenderBondSub002','TenderBondSub001');

INSERT INTO SYS_PAGE (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('TenderBondSub002', 'TRD', '/home.html#/approve/biddingTranPurchase', 'cfets/finbond/tenderBondSub/TenderBondSub002', null, null, null, '02', '01', '一级申购交易', 'system', null, sysdate, 'system', sysdate);

INSERT INTO SYS_PAGE (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('TenderBondSub001', 'TRD', '/home.html#/interbank/instructiontrade?a=a', 'cfets/finbond/tenderBondSub/TenderBondSub001', null, null, null, '02', '01', '一级申购查询', 'system', null, sysdate, 'system', sysdate);

--按钮
delete from SYS_PAGE_ELEMENT where PAGE_ID in ('TenderBondSub001','TenderBondSub002');
INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub001', 'TenderBondSub001_btn_export', '01', '01', null, '导出', 'system', null, sysdate, 'system', sysdate, 'COM', null);
INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub001', 'TenderBondSub001_btn_add', '01', '01', null, '申购', 'system', null, sysdate, 'system', sysdate, 'COM', null);
INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub001', 'TenderBondSub001_btn_revoke', '01', '01', null, '撤销', 'system', null, sysdate, 'system', sysdate, 'COM', null);
INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub001', 'TenderBondSub001_btn_search', '01', '01', null, '查询', 'system', null, sysdate, 'system', sysdate, 'COM', null);
INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub002', 'TenderBondSub002_btn_save', '01', '02', null, '保存', 'system', null, sysdate, 'system', sysdate, 'COM', null);
INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID)
VALUES ('TenderBondSub002', 'TenderBondSub002_btn_submit', '01', '02', null, '提交', 'system', null, sysdate, 'system', sysdate, 'COM', null);

delete from SYS_WFL_BUSINESS_MODULE where PAGE_ID in ('TenderBondSub002');

INSERT INTO SYS_WFL_BUSINESS_MODULE (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG) 
VALUES ('TenderBondSub002', 'TenderBondSub002_btn_revoke', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

INSERT INTO SYS_WFL_BUSINESS_MODULE (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG) 
VALUES ('TenderBondSub002', 'TenderBondSub002_btn_save', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

INSERT INTO SYS_WFL_BUSINESS_MODULE (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG) 
VALUES ('TenderBondSub002', 'TenderBondSub002_btn_submit', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');