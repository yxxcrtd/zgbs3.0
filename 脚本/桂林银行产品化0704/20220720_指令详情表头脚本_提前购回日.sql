delete from CFG_READONLY_FORM_SHOW where PAGECODE = 'FieldEtrDispatcher' and DTCODE = 'FieldEtrDispatcherInfo' and SPCDCODE = 'EX011' and COLCODE = 'advanceRecoveryDate';
INSERT INTO "CFG_READONLY_FORM_SHOW" ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO", "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3", "SPCDCODE_DESC") VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX011', 'advanceRecoveryDate', '提前购回日', NULL, '15', '{"colAlign": "left"}', '1', '0', NULL, NULL, '999999');


delete from CFG_READONLY_FORM_SHOW where PAGECODE = 'CfetsEtrDispatcher' and DTCODE = 'CfetsEtrDispatcherInfo' and SPCDCODE = 'EX011' and COLCODE = 'advanceRecoveryDate';
INSERT INTO "CFG_READONLY_FORM_SHOW" ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO", "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3", "SPCDCODE_DESC") VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX011', 'advanceRecoveryDate', '提前购回日', NULL, '15', '{"colAlign": "left"}', '1', '0', NULL, NULL, '999999');
