--配置列表字段
delete from CFG_DATATABLE_SHOW where PAGECODE='FinTenderBondAsset001' and DTCODE='FinTenderBondAsset001List' and SPCDCODE='999999' and RANGEID='COMM' and COLCODE='rate';
INSERT INTO CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
VALUES ('FinTenderBondAsset001', 'FinTenderBondAsset001List', '999999', 'rate', '参考收益率', null, 32, 1, 0, 1, 120, '{"colAlign": "left","supportSearch":"精确"}', 0, 'COMM', 1, 0, null, null, 0);
