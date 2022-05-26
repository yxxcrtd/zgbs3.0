
delete from sys_menu where menu_id = 'SETTLE_MANAGEMENT_001_003_015' and page_id = 'etrCreditProtectionCertificate002';
insert into sys_menu (MENU_ID, SYSTEM_CODE, ICON, PARENT_MENU_ID, PAGE_ID, ORDER_NO, IS_SPREAD, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values('SETTLE_MANAGEMENT_001_003_015','ETR',null,'SETTLE_MANAGEMENT_001_003'
      ,'etrCreditProtectionCertificate002',3,'02','信用保护凭证指令','system',null,sysdate,'system',sysdate);

delete from sys_page where page_id = 'etrCreditProtectionCertificate002';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values('etrCreditProtectionCertificate002','ETR','home.html#/instructions/creditprotection?a=a'
      ,'etrexchange/cpc/etrCreditProtectionCertificate002','SETTLE_MANAGEMENT_001_003_015',null,null,'02','01'
      ,'信用保护凭证指令','system',null,sysdate,'system',sysdate);


delete from sys_page where page_id = 'etrCreditProtectionCertificate';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values('etrCreditProtectionCertificate','ETR','/home.html#/approve/creditProtection?a=a'
      ,'/etrexchange/cpc/etrCreditProtectionCertificate','',null,null,'02','01'
      ,'信用保护凭证指令-审核','system',null,sysdate,'system',sysdate);

delete from sys_page_element where page_id = 'etrCreditProtectionCertificate';
insert into sys_page_element (PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG)
values('etrCreditProtectionCertificate','etrCreditProtectionCertificate_btn_save','01','01',null,'保存'
      ,'system',null,sysdate,'system',sysdate,null);
insert into sys_page_element (PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG)
values('etrCreditProtectionCertificate','etrCreditProtectionCertificate_btn_submit','01','01',null,'提交'
      ,'system',null,sysdate,'system',sysdate,null);

delete from sys_wfl_business_module where page_id in ('etrCreditProtectionCertificate')
                                      and element_id in ('etrCreditProtectionCertificate_btn_submit','etrCreditProtectionCertificate_btn_save');
insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID,
                                     CREATE_USER, CREATE_DEPT, CREATE_TIME,
                                     UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('etrCreditProtectionCertificate','etrCreditProtectionCertificate_btn_submit','oneApproval','system',
        '',sysdate,'system',sysdate,'ALL','02');
insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID,
                                     CREATE_USER, CREATE_DEPT, CREATE_TIME,
                                     UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('etrCreditProtectionCertificate','etrCreditProtectionCertificate_btn_save','oneApproval','system',
        '',sysdate,'system',sysdate,'ALL','02');

DELETE FROM mst_business_id_rule WHERE SEQ_NO = 'ETR56_01' and common_business_type = 'ETR56';
insert into mst_business_id_rule (SEQ_NO, COMMON_BUSINESS_TYPE, TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYPE_5, TYPE_6, BUSINESS_ID_RULE_TYPE, PREFIX_NAME, PREFIX_SEQUENCE_NAME, PREFIX_SEQUENCE_LEN, POSTFIX_NAME, POSTFIX_SEQUENCE_NAME, POSTFIX_SEQUENCE_LEN, DATE_RULE_TYPE, BUSINESS_ID_SERVICE_NAME, BUSINESS_ID_METHOD_NAME, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ETR56_01','ETR56','','','','','','','04','WTCDS','','','','WTREPO_SEQUENCE',4,'01'
       ,'','','system','',sysdate,'system',sysdate);

delete from SYS_DATADICT_LIST where DICT_CODE = 'COMMON_BUSINESS_TYPE' and ITEM_CODE = 'ETR56' and  ITEM_VALUE = '信用保护凭证指令';
INSERT INTO SYS_DATADICT_LIST(DICT_CODE,ITEM_CODE,ITEM_VALUE,ORDER_NO,DEFAULT_VALUE,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,INSTITUTION_CODE) VALUES ('COMMON_BUSINESS_TYPE', 'ETR56', '信用保护凭证指令', '441', '0', 'system', NULL, SYSDATE, 'system', SYSDATE, NULL);

delete from sys_page_element where page_id = 'etrCreditProtectionCertificate002';
INSERT INTO sys_page_element(PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG,PARENT_ELEMENT_ID) VALUES ('etrCreditProtectionCertificate002', 'etrCreditProtectionCertificate002_btn_close', '01', '01', NULL, '关闭', 'system', NULL, SYSDATE, 'system', SYSDATE, NULL, NULL);
INSERT INTO sys_page_element(PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG,PARENT_ELEMENT_ID) VALUES ('etrCreditProtectionCertificate002', 'etrCreditProtectionCertificate002_btn_edit', '01', '01', NULL, '修改', 'system', NULL, SYSDATE, 'system', SYSDATE, NULL, NULL);
INSERT INTO sys_page_element(PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG,PARENT_ELEMENT_ID) VALUES ('etrCreditProtectionCertificate002', 'etrCreditProtectionCertificate002_btn_reCell', '01', '01', NULL, '撤回', 'system', NULL, SYSDATE, 'system', SYSDATE, NULL, NULL);
INSERT INTO sys_page_element(PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG,PARENT_ELEMENT_ID) VALUES ('etrCreditProtectionCertificate002', 'etrCreditProtectionCertificate002_btn_save', '01', '01', NULL, '保存', 'system', NULL, SYSDATE, 'system', SYSDATE, NULL, NULL);
INSERT INTO sys_page_element(PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG,PARENT_ELEMENT_ID) VALUES ('etrCreditProtectionCertificate002', 'etrCreditProtectionCertificate002_btn_submit', '01', '01', NULL, '提交', 'system', NULL, SYSDATE, 'system', SYSDATE, NULL, NULL);
