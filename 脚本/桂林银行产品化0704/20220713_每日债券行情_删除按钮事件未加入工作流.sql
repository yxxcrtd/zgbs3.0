
DELETE FROM SYS_WFL_BUSINESS_MODULE T WHERE T.PAGE_ID = 'ComBondMarketInfo002' AND T.ELEMENT_ID = 'ValBondMarketInfo001_btn_delete';

insert into SYS_WFL_BUSINESS_MODULE (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('ComBondMarketInfo002', 'ValBondMarketInfo001_btn_delete', 'oneApproval', 'system', null, SYSDATE, 'system', SYSDATE, 'ALL', '02');


--审批查看页面改成了EtrProject002.html
DELETE FROM SYS_WFL_BUSINESS_MODULE T WHERE T.PAGE_ID = 'EtrProject002' AND T.ELEMENT_ID = 'EtrProject003_btn_submit';

insert into SYS_WFL_BUSINESS_MODULE (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('EtrProject002', 'EtrProject003_btn_submit', 'oneApproval', 'system', null, SYSDATE, 'system', SYSDATE, 'ALL', '02');
