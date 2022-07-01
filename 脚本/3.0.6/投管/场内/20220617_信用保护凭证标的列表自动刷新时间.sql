delete from sys_para where SYSTEM_CODE = 'FIN' and PARA_CODE = 'CreditProtectionCertificate001_list_refresh_period';
INSERT INTO sys_para(SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG) VALUES ('FIN', 'CreditProtectionCertificate001_list_refresh_period', '30000', NULL, NULL, '信用保护凭证标的列表自动刷新时间（毫秒）', 'system', NULL, sysdate, 'wsz', sysdate, NULL, '01');


ALTER TABLE FIN_PRODUCT_ADD MODIFY REF_SEC  VARCHAR2(500) ;