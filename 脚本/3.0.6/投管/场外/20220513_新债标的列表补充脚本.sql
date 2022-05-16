delete from CFG_DATATABLE_SHOW where PAGECODE='FinTenderBondAsset001' and DTCODE='FinTenderBondAsset001List' and SPCDCODE='999999' and RANGEID='COMM' and COLCODE='referYield';
delete from CFG_DATATABLE_SHOW where PAGECODE='FinTenderBondAsset001' and DTCODE='FinTenderBondAsset001List' and SPCDCODE='999999' and RANGEID='COMM' and COLCODE='issueDate';
delete from CFG_DATATABLE_SHOW where PAGECODE='FinTenderBondAsset001' and DTCODE='FinTenderBondAsset001List' and SPCDCODE='999999' and RANGEID='COMM' and COLCODE='issueDateStr';
INSERT INTO CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
VALUES ('FinTenderBondAsset001', 'FinTenderBondAsset001List', '999999', 'issueDateStr', '发行日/投标日', null, 14, 1, 0, 1, 160, '{"colAlign": "left","supportSearch":"精确"}', 0, 'COMM', 1, 0, null, null, 0);


alter table TRD_FIN_PRODUCT_BARTER_TOTAL modify FINPROD_ID VARCHAR2(50);
