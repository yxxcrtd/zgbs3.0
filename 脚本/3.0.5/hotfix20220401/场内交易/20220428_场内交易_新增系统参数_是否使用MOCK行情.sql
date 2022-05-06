delete from sys_para where system_code = 'TRD' and para_code = 'TRD_USE_MOCK_QUOTE';
INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
VALUES ('TRD', 'TRD_USE_MOCK_QUOTE', 'N', '01', 'Y,N', '是否使用的mock行情(Y：是 N：否)', 'system', null, sysdate, 'system', sysdate, null, '01');

delete from sys_para where system_code = 'TRD' and para_code = 'TRD_DEFAULT_MOCK_QUOTE';
INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
VALUES ('TRD', 'TRD_DEFAULT_MOCK_QUOTE', '100303.SZ', '01', '000001.SZ,600000.SH', 'mock行情的缺省资产(证券的资产代码)', 'system', null, sysdate, 'system', sysdate, null, '01');