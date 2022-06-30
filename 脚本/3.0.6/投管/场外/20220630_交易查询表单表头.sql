delete from  CFG_READONLY_FORM_SHOW where PAGECODE = 'cfetsManualSettleQuery' and DTCODE = 'CfetsManualSettleInfo' and SPCDCODE in ('03WP','01WP');

INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3, SPCDCODE_DESC) 
VALUES ('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '03WP', 'dipType', '分配方式', null, 1, '{"colAlign": "left"}', 1, 0, null, null, '投标分销买入投标产品列表信息');

INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3, SPCDCODE_DESC) 
VALUES ('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01WP', 'dipType', '中标方式', null, 1, '{"colAlign": "left"}', 1, 0, null, null, '投标自主投标产品列表信息');