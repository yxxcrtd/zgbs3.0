DELETE
FROM CFG_READONLY_FORM_SHOW
WHERE PAGECODE = 'CfetsEtrDispatcher'
  AND DTCODE = 'CfetsEtrDispatcherInfo'
  AND SPCDCODE = 'EX018';

INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'entrustDate', '指令日期', NULL, '1',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'portfolioId', '组合代码', NULL, '2',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'portfolioName', '组合名称', NULL, '3',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME",
                                    "DICTCODE", "SEQNO", "EXTENDCFG", "STATUS", "REMARK1",
                                    "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'trdAcctId', '资金账户', NULL, '4',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'otherAssetType', '资产类型', NULL, '5',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'finprodMarketId', '资产代码', NULL, '6',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'finprodAbbr', '资产简称', NULL, '7',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);

INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'busiTypePositionStr', '业务类型', NULL, '8',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);

INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'otherPs', '委托方向', NULL, '9', '{"colAlign": "left"}',
        '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'otherTradeMarket', '交易场所', NULL, '10',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);
INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'otherPriceModel', '价格模式', NULL, '11',
        '{"colAlign": "left"}', '1', '0', NULL, NULL);

INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'unitPrice', '回购利率', NULL, '12',
        '{"colAlign": "left","dataType":"amount","precision":"3","unit":"%"}', '1', '0', NULL, NULL);

INSERT INTO CFG_READONLY_FORM_SHOW ("PAGECODE", "DTCODE", "SPCDCODE", "COLCODE", "COLNAME", "DICTCODE", "SEQNO",
                                    "EXTENDCFG", "STATUS", "REMARK1", "REMARK2", "REMARK3")
VALUES ('CfetsEtrDispatcher', 'CfetsEtrDispatcherInfo', 'EX018', 'entrustAmt', '回购金额', NULL, '13',
        '{"colAlign": "left","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', NULL, NULL);


