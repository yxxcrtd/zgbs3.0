DELETE
FROM CFG_READONLY_FORM_SHOW
WHERE PAGECODE = 'FieldEtrDispatcher'
  AND DTCODE = 'FieldEtrDispatcherInfo'
  AND SPCDCODE = 'EX017';

INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'entrustDate', '指令日期', NULL, '1',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'portfolioId', '组合代码', NULL, '2',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'portfolioName', '组合名称', NULL, '3',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'dealCurAcct', '资金账户', NULL, '4',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'otherAssetType', '资产类型', NULL, '5',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'finprodMarketId', '资产代码', NULL, '6',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'finprodAbbr', '资产简称', NULL, '7',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'secType', '债券分类', NULL, '8', '{"colAlign": "left"}',
        '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'otherPs', '委托方向', NULL, '9', '{"colAlign": "left"}',
        '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'otherTradeMarket', '交易场所', NULL, '10',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'otherPriceModel', '价格模式', NULL, '11',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'unitPrice', '净价（元）', NULL, '12',
        '{"colAlign": "left","dataType":"amount","precision":"3","unit":"yuan"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'allPrice', '全价（元）', NULL, '13',
        '{"colAlign": "left","dataType":"amount","precision":"3","unit":"yuan"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'matureYield', '到期收益率（%）', NULL, '14',
        '{"colAlign": "left","dataType":"amount","precision":"4","unit":"%"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'otherTargetType', '目标类型', NULL, '15',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'entrustNum', '指令数量', NULL, '16',
        '{"colAlign": "left","dataType":"amount","precision":"0","unit":"zhang"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'entrustAmt', '指令金额', NULL, '17',
        '{"colAlign": "left","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'otherCcy', '币种', NULL, '18', '{"colAlign": "left"}',
        '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'otherBusiModel', '投资目的', NULL, '19',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX017', 'invManagerId', '投资经理', NULL, '20',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);