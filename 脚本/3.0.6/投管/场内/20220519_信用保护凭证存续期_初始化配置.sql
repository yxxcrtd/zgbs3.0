DELETE
FROM SYS_MENU
WHERE PAGE_ID = 'CreditProtectionCertificatePeriod001';
DELETE
FROM SYS_PAGE
WHERE PAGE_ID = 'CreditProtectionCertificatePeriod001';
DELETE
FROM SYS_PAGE
WHERE PAGE_ID = 'CreditProtectionCertificatePeriod002';
DELETE
FROM SYS_PAGE_ELEMENT
WHERE PAGE_ID = 'CreditProtectionCertificatePeriod001';
DELETE
FROM SYS_PAGE_ELEMENT
WHERE PAGE_ID = 'CreditProtectionCertificatePeriod002';
DELETE
FROM SYS_WFL_BUSINESS_MODULE
WHERE PAGE_ID = 'CreditProtectionCertificatePeriod002';
DELETE
FROM MST_BUSINESS_ID_RULE
WHERE SEQ_NO = 'TRD802_01'
  and common_business_type = 'TRD802';

DELETE
FROM CFG_DATATABLE_SHOW
WHERE PAGECODE = 'CreditProtectionCertificatePeriod001';



INSERT INTO SYS_MENU ("MENU_ID", "SYSTEM_CODE", "ICON", "PARENT_MENU_ID", "PAGE_ID", "ORDER_NO", "IS_SPREAD", "REMARK",
                      "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME")
VALUES ('QS_MANAGEMENT_001_005_022', 'STL', NULL, 'QS_MANAGEMENT_001_005', 'CreditProtectionCertificatePeriod001', '22',
        '02', '信用保护凭证存续期', 'system', NULL, TO_TIMESTAMP('2021-06-04 17:05:05.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'),
        'system', TO_TIMESTAMP('2021-09-16 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'));
INSERT INTO SYS_PAGE ("PAGE_ID", "SYSTEM_CODE", "PAGE_URL", "PAGE_FILE", "MENU_ID", "HEIGHT", "WIDTH", "IS_HP_SELECT",
                      "IS_NEED_WORKFLOW", "REMARK", "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER",
                      "UPDATE_TIME")
VALUES ('CreditProtectionCertificatePeriod001', 'TRD', '/home.html#/clear/certificateProtect?a=a', 'vue', NULL, NULL,
        NULL, '02', '01', '信用保护凭证存续期', 'system', NULL,
        TO_TIMESTAMP('2019-05-24 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2021-09-16 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'));
INSERT INTO SYS_PAGE ("PAGE_ID", "SYSTEM_CODE", "PAGE_URL", "PAGE_FILE", "MENU_ID", "HEIGHT", "WIDTH", "IS_HP_SELECT",
                      "IS_NEED_WORKFLOW", "REMARK", "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER",
                      "UPDATE_TIME")
VALUES ('CreditProtectionCertificatePeriod002', 'TRD', '/home.html?a=a#/approve/certificateProtect?a=a', 'vue', NULL, NULL,
        NULL, '02', '01', '信用事件结算', 'system', NULL,
        TO_TIMESTAMP('2019-05-24 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2022-05-18 19:07:51.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'));
INSERT INTO SYS_PAGE_ELEMENT ("PAGE_ID", "ELEMENT_ID", "ELEMENT_TYPE", "IS_CONTROL", "TRANSFER_PAGE_ID", "REMARK",
                              "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "ADMIN_FLAG",
                              "PARENT_ELEMENT_ID")
VALUES ('CreditProtectionCertificatePeriod001', 'CreditProtectionCertificatePeriod001_btn_add', '01', '01', NULL,
        '信用事件结算', 'system', NULL, TO_TIMESTAMP('2019-05-24 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2021-04-21 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'C', NULL);
INSERT INTO SYS_PAGE_ELEMENT ("PAGE_ID", "ELEMENT_ID", "ELEMENT_TYPE", "IS_CONTROL", "TRANSFER_PAGE_ID", "REMARK",
                              "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "ADMIN_FLAG",
                              "PARENT_ELEMENT_ID")
VALUES ('CreditProtectionCertificatePeriod001', 'CreditProtectionCertificatePeriod001_btn_export', '01', '01', NULL,
        '导出', 'system', NULL, TO_TIMESTAMP('2019-05-24 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2021-04-21 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'C', NULL);
INSERT INTO SYS_PAGE_ELEMENT ("PAGE_ID", "ELEMENT_ID", "ELEMENT_TYPE", "IS_CONTROL", "TRANSFER_PAGE_ID", "REMARK",
                              "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "ADMIN_FLAG",
                              "PARENT_ELEMENT_ID")
VALUES ('CreditProtectionCertificatePeriod001', 'CreditProtectionCertificatePeriod001_btn_revoke', '01', '01', NULL,
        '撤销', 'system', NULL, TO_TIMESTAMP('2019-05-24 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2021-04-21 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'C', NULL);
INSERT INTO SYS_PAGE_ELEMENT ("PAGE_ID", "ELEMENT_ID", "ELEMENT_TYPE", "IS_CONTROL", "TRANSFER_PAGE_ID", "REMARK",
                              "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "ADMIN_FLAG",
                              "PARENT_ELEMENT_ID")
VALUES ('CreditProtectionCertificatePeriod001', 'CreditProtectionCertificatePeriod001_btn_search', '01', '01', NULL,
        '查询', 'system', NULL, TO_TIMESTAMP('2019-05-24 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2021-04-21 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'COM', NULL);
INSERT INTO SYS_PAGE_ELEMENT ("PAGE_ID", "ELEMENT_ID", "ELEMENT_TYPE", "IS_CONTROL", "TRANSFER_PAGE_ID", "REMARK",
                              "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "ADMIN_FLAG",
                              "PARENT_ELEMENT_ID")
VALUES ('CreditProtectionCertificatePeriod002', 'CreditProtectionCertificatePeriod002_btn_save', '01', '02', NULL, '保存',
        'system', NULL, TO_TIMESTAMP('2019-05-24 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2021-04-21 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'C', NULL);
INSERT INTO SYS_PAGE_ELEMENT ("PAGE_ID", "ELEMENT_ID", "ELEMENT_TYPE", "IS_CONTROL", "TRANSFER_PAGE_ID", "REMARK",
                              "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "ADMIN_FLAG",
                              "PARENT_ELEMENT_ID")
VALUES ('CreditProtectionCertificatePeriod002', 'CreditProtectionCertificatePeriod002_btn_submit', '01', '02', NULL,
        '提交', 'system', NULL, TO_TIMESTAMP('2019-05-24 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2021-04-21 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'C', NULL);
        
INSERT INTO SYS_WFL_BUSINESS_MODULE ("PAGE_ID", "ELEMENT_ID", "MODULE_ID", "CREATE_USER", "CREATE_DEPT", "CREATE_TIME",
                                     "UPDATE_USER", "UPDATE_TIME", "INSTITUTION_CODE", "IS_SEND_UORG")
VALUES ('CreditProtectionCertificatePeriod002', 'CreditProtectionCertificatePeriod001_btn_revoke', 'oneApproval',
        'system', NULL, TO_TIMESTAMP('2019-05-24 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2021-04-21 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'ALL', '02');
INSERT INTO SYS_WFL_BUSINESS_MODULE ("PAGE_ID", "ELEMENT_ID", "MODULE_ID", "CREATE_USER", "CREATE_DEPT", "CREATE_TIME",
                                     "UPDATE_USER", "UPDATE_TIME", "INSTITUTION_CODE", "IS_SEND_UORG")
VALUES ('CreditProtectionCertificatePeriod002', 'CreditProtectionCertificatePeriod002_btn_save', 'oneApproval',
        'system', NULL, TO_TIMESTAMP('2019-05-24 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2021-04-21 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'ALL', '02');
INSERT INTO SYS_WFL_BUSINESS_MODULE ("PAGE_ID", "ELEMENT_ID", "MODULE_ID", "CREATE_USER", "CREATE_DEPT", "CREATE_TIME",
                                     "UPDATE_USER", "UPDATE_TIME", "INSTITUTION_CODE", "IS_SEND_UORG")
VALUES ('CreditProtectionCertificatePeriod002', 'CreditProtectionCertificatePeriod002_btn_submit', 'oneApproval',
        'system', NULL, TO_TIMESTAMP('2019-05-24 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2021-04-21 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'ALL', '02');

insert into MST_BUSINESS_ID_RULE (SEQ_NO, COMMON_BUSINESS_TYPE, TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYPE_5, TYPE_6,
                                  BUSINESS_ID_RULE_TYPE, PREFIX_NAME, PREFIX_SEQUENCE_NAME, PREFIX_SEQUENCE_LEN,
                                  POSTFIX_NAME, POSTFIX_SEQUENCE_NAME, POSTFIX_SEQUENCE_LEN, DATE_RULE_TYPE,
                                  BUSINESS_ID_SERVICE_NAME, BUSINESS_ID_METHOD_NAME, CREATE_USER, CREATE_DEPT,
                                  CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ( 'TRD802_01', 'TRD802', '', '', '', '', '', '', '04', 'CDSJS', '', '', '', 'CDSJS_SEQUENCE', 4, '01'
       , '', '', 'system', '', sysdate, 'system', sysdate);


INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'validStatusStr', '有效状态', 'validStatus', '1', '1',
        '0', '1', '100', '{"colAlign": "left","supportSearch":"模糊"}', '0', 'COMM', '1', '0', NULL, NULL, '0', '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'busiCode', '业务编号', NULL, '2', '1', '0', '1',
        '100', '{"colAlign": "left","supportSearch":"精确"}', '0', 'COMM', '1', '0', NULL, NULL, '0', '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'paBussTypeStr', '业务类型', 'paBussType', '3', '1',
        '0', '1', '100', '{"colAlign": "left","supportSearch":"模糊"}', '0', 'COMM', '1', '0', NULL, NULL, '0', '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'finprodMarketId', '资产代码', NULL, '4', '1', '0',
        '1', '100', '{"colAlign": "left","supportSearch":"模糊"}', '0', 'COMM', '1', '0', NULL, NULL, '0', '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'finprodMarketName', '资产名称', NULL, '5', '1', '0',
        '1', '100', '{"colAlign": "left","supportSearch":"模糊"}', '0', 'COMM', '1', '0', NULL, NULL, '0', '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'vDateWhole', '起始日', NULL, '6', '1', '0', '1',
        '100', '{"colAlign": "left","supportSearch":"精确"}', '0', 'COMM', '1', '0', NULL, NULL, '0', '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'mDateWhole', '到期日', NULL, '7', '1', '0', '1',
        '100', '{"colAlign": "left","supportSearch":"精确"}', '0', 'COMM', '1', '0', NULL, NULL, '0', '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'repNomCapi', '名义本金（万元）', NULL, '8', '1', '0',
        '1', '100', '{"colAlign": "left","dataType":"amount","precision":"2","supportSearch":"精确"}', '0', 'COMM', '1',
        '0', NULL, NULL, '0', '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'refEntity', '参考实体', NULL, '9', '1', '0', '1',
        '100', '{"colAlign": "left","supportSearch":"模糊"}', '0', 'COMM', '1', '0', NULL, NULL, '0', '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'creditEventStr', '信用事件', 'creditEvent', '10',
        '1', '0', '1', '100', '{"colAlign": "left","supportSearch":"精确"}', '0', 'COMM', '1', '0', NULL, NULL, '0',
        '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'creditEventHappenedDate', '信用事件发生时间', NULL, '11',
        '1', '0', '1', '100', '{"colAlign": "left","supportSearch":"精确"}', '0', 'COMM', '1', '0', NULL, NULL, '0',
        '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'creditEventResolveDate', '信用事件决定日', NULL, '12',
        '1', '0', '1', '100', '{"colAlign": "left","supportSearch":"精确"}', '0', 'COMM', '1', '0', NULL, NULL, '0',
        '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'settleMethodStr', '结算方式', 'settleMethod', '13',
        '1', '0', '1', '100', '{"colAlign": "left","supportSearch":"模糊"}', '0', 'COMM', '1', '0', NULL, NULL, '0',
        '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'deliverBond', '交付债券', NULL, '14', '1', '0', '1',
        '100', '{"colAlign": "left","supportSearch":"模糊"}', '0', 'COMM', '1', '0', NULL, NULL, '0', '999999');
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS",
                                "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC")
VALUES ('CreditProtectionCertificatePeriod001', 'PageInfo', '999999', 'deliverDate', '交付时间', NULL, '15', '1', '0', '1',
        '100', '{"colAlign": "left","supportSearch":"精确"}', '0', 'COMM', '1', '0', NULL, NULL, '0', '999999');


delete from SYS_BATCH_TASK where BEAN_NAME = 'creditProtectionCertificatePeriodRunner';

INSERT INTO SYS_BATCH_TASK ("TASK_ID", "SYSTEM_CODE", "TASK_NAME", "FTASK_ID", "BEAN_NAME", "TASK_PRI", "CREATE_USER",
                            "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "IS_BATCH_START_TARGET")
VALUES ('800001', 'STL', '信用保护凭证到期自动生成', '0', 'creditProtectionCertificatePeriodRunner', '3', 'system', NULL,
        TO_TIMESTAMP('2021-06-10 17:47:35.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system',
        TO_TIMESTAMP('2021-06-10 17:47:35.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), NULL);


DROP SEQUENCE "CDSJS_SEQUENCE";
CREATE SEQUENCE  "CDSJS_SEQUENCE"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 6000 CACHE 20 NOORDER  NOCYCLE ;



