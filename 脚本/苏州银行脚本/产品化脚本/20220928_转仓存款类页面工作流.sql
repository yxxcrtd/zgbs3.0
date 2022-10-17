
   
DELETE FROM sys_wfl_business_module t where t.page_id = 'DepositTransfere001';

insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('DepositTransfere001', 'DepositTransfere001_btn_save', 'oneApproval', 'system', '0001', sysdate, 'system', sysdate, 'ALL', '02');

insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('DepositTransfere001', 'DepositTransfere001_btn_submit', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');
