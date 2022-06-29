-- 银行间交易查询-登记查询-交易登记信息
delete from CFG_READONLY_FORM_SHOW WHERE PAGECODE = 'CfetsTrdQuery' and DTCODE = 'CfetsTrdQueryRegInfo' and SPCDCODE = '34,B' ;
INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
VALUES('CfetsTrdQuery', 'CfetsTrdQueryRegInfo', '34,B', 'finprodMarketId', '资产代码', NULL, 1, '{"colAlign": "left"}', 1, 0, NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
VALUES('CfetsTrdQuery', 'CfetsTrdQueryRegInfo', '34,B', 'finprodAbbr', '资产简称', NULL, 2, '{"colAlign": "left"}', 1, 0, NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
VALUES('CfetsTrdQuery', 'CfetsTrdQueryRegInfo', '34,B', 'ouPartyIdStr', '本方', NULL, 3, '{"colAlign": "left"}', 1, 0, NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
VALUES('CfetsTrdQuery', 'CfetsTrdQueryRegInfo', '34,B', 'traderidStr', '本方交易员', NULL, 4, '{"colAlign": "left"}', 1, 0, NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
VALUES('CfetsTrdQuery', 'CfetsTrdQueryRegInfo', '34,B', 'issuePrice', '发行价格', NULL, 5, '{"colAlign": "left","dataType":"amount","precision":"4","unit":"yuan"}', 1, 0, NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
VALUES('CfetsTrdQuery', 'CfetsTrdQueryRegInfo', '34,B', 'rateStr', '参考收益率', NULL, 6, '{"colAlign": "left"}', 1, 0, NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
VALUES('CfetsTrdQuery', 'CfetsTrdQueryRegInfo', '34,B', 'rateYleid', '票面利率', NULL, 7, '{"colAlign": "left"}', 1, 0, NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
VALUES('CfetsTrdQuery', 'CfetsTrdQueryRegInfo', '34,B', 'subAmt', '认购量', NULL, 8, '{"colAlign": "left","dataType":"amount","precision":"2","unit":"wanYuan"}', 1, 0, NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
VALUES('CfetsTrdQuery', 'CfetsTrdQueryRegInfo', '34,B', 'payableAmount', '应缴款金额', NULL, 9, '{"colAlign": "left","dataType":"amount","precision":"2","unit":"yuan"}', 1, 0, NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
VALUES('CfetsTrdQuery', 'CfetsTrdQueryRegInfo', '34,B', 'settleFeeAmt', '结算服务费', NULL, 10, '{"colAlign": "left","dataType":"amount","precision":"2","unit":"yuan"}', 1, 0, NULL, NULL);
