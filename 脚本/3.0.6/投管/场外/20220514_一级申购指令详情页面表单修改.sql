
delete from CFG_READONLY_FORM_SHOW where PAGECODE = 'TenderBondSub' and DTCODE = 'ETRDETINFO' and COLCODE in ('referenceRate','rate','referenceRateStr');

INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3, SPCDCODE_DESC) 
VALUES ('TenderBondSub', 'ETRDETINFO', '02', 'referenceRateStr', '参考收益率', null, 8, '{"colAlign": "right"}', 1, 0, null, null, '999999');

INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3, SPCDCODE_DESC) 
VALUES ('TenderBondSub', 'ETRDETINFO', '01', 'referenceRateStr', '票面利率', null, 8, '{"colAlign": "right"}', 1, 0, null, null, '999999');