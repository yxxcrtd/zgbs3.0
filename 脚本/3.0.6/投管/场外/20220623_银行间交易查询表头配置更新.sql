
DELETE FROM CFG_DATATABLE_SHOW where PAGECODE = 'CfetsTrdQuery' and DTCODE = 'CfetsTrdQueryRegInfo' and SPCDCODE = '34,B' and COLCODE = 'operTypeStr';
UPDATE CFG_DATATABLE_SHOW SET SORT = 0 where PAGECODE = 'CfetsTrdQuery' and DTCODE = 'CfetsTrdQueryRegInfo' and SPCDCODE = '34,B';

 update CFG_DATATABLE_SHOW SET EXTENDCFG = '{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确"}'
 where PAGECODE = 'CfetsTrdQuery' and DTCODE = 'CfetsTrdQueryRegInfo' and SPCDCODE = '34,B' and COLCODE= 'payableAmount';
 update CFG_DATATABLE_SHOW SET EXTENDCFG = '{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确"}'
 where PAGECODE = 'CfetsTrdQuery' and DTCODE = 'CfetsTrdQueryRegInfo' and SPCDCODE = '34,B' and COLCODE= 'settleFeeAmt';
 update CFG_DATATABLE_SHOW SET EXTENDCFG = '{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确"}'
 where PAGECODE = 'CfetsTrdQuery' and DTCODE = 'CfetsTrdQueryRegInfo' and SPCDCODE = '34,B' and COLCODE= 'subAmt';

DELETE FROM CFG_DATATABLE_SHOW where PAGECODE = 'CfetsTrdQuery' and DTCODE = 'CfetsTrdQueryRegInfo' and SPCDCODE = '34,B' and COLCODE = 'index';
INSERT INTO CFG_DATATABLE_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO", "DISPLAYED", "FROZEN", "SORT", "MAXLEN", "EXTENDCFG", "EFFERANGE", "RANGEID", "STATUS", "REMARK1", "REMARK2", "REMARK3", "ISKEY", "SPCDCODE_DESC", "IS_HIDE") VALUES ('CfetsTrdQuery', 'CfetsTrdQueryRegInfo', '34,B', 'index', '序号', NULL, '1', '1', '0', '1', '100', '{"colAlign": "left","supportSearch":"模糊"}', '0', 'COMM', '1', '0', NULL, NULL, '0', '999999', NULL);
