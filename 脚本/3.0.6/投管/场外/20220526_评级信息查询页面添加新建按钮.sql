--评级信息查询新建页面 按钮
DELETE FROM SYS_PAGE_ELEMENT T WHERE T.PAGE_ID IN ('MstMarketManagement001','MstMarketManagement002') AND ELEMENT_ID IN ('MstMarketManagement001_btn_add','MstMarketManagement001_TBLROW_edit','MstMarketManagement002_btn_save','MstMarketManagement002_btn_submit');
INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID) VALUES ('MstMarketManagement001', 'MstMarketManagement001_btn_add', '01', '01', null, '新建', 'system', null, sysdate, 'system', sysdate, 'COM', null);
INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID) VALUES ('MstMarketManagement001', 'MstMarketManagement001_TBLROW_edit', '01', '01', null, '编辑', 'system', null, sysdate, 'system', sysdate, 'COM', null);
INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID) VALUES ('MstMarketManagement002', 'MstMarketManagement002_btn_save', '01', '02', null, '保存', 'system', null, sysdate, 'system', sysdate, 'COM', null);
INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID) VALUES ('MstMarketManagement002', 'MstMarketManagement002_btn_submit', '01', '02', null, '提交', 'system', null, sysdate, 'system', sysdate, 'COM', null);

--评级信息查询新建页面 url
DELETE FROM SYS_PAGE T WHERE T.PAGE_ID = 'MstMarketManagement002';
INSERT INTO SYS_PAGE (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES ('MstMarketManagement002', 'MST', '/invest/basicdata/mstmarketmanagement/MstMarketManagement002', 'invest/basicdata/mstmarketmanagement/MstMarketManagement002', '', null, null, '02', '01', '评级信息查询新建页面', 'system', null, sysdate, 'system', sysdate);

--评级信息查询新建流程模板
DELETE FROM SYS_WFL_BUSINESS_MODULE WHERE PAGE_ID = 'MstMarketManagement002';
INSERT INTO SYS_WFL_BUSINESS_MODULE (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG) VALUES ('MstMarketManagement002', 'MstMarketManagement002_btn_save', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');
INSERT INTO SYS_WFL_BUSINESS_MODULE (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG) VALUES ('MstMarketManagement002', 'MstMarketManagement002_btn_submit', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');


--数据字典
DELETE FROM SYS_DATADICT_LIST WHERE DICT_CODE = 'COMMON_BUSINESS_TYPE' AND ITEM_CODE = 'MST37';
INSERT INTO SYS_DATADICT_LIST (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE) VALUES ('COMMON_BUSINESS_TYPE', 'MST37', '评级信息', 937, '0', 'system', null, sysdate, 'system', sysdate, null);