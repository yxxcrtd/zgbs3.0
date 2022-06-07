--指令执行
DELETE FROM CFG_DATATABLE_SHOW T WHERE T.PAGECODE = 'CfetsEtrDispatcher' AND T.DTCODE = 'CfetsEtrDispatcherExeInfo' AND T.SPCDCODE = 'EX018';
insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'accountName', '资金账户', null, 3, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'brokerEntrustId', '委托编号', null, 1, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'entrustBalance', '委托金额', null, 10, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'entrustBsStr', '委托方向', null, 6, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'entrustPrice', '委托价格', null, 9, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"3"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'entrustStatusStr', '委托状态', null, 11, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'entrustVolume', '委托数量', null, 8, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"0"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'marketStr', '交易场所', null, 7, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'portfolioName', '组合名称', null, 2, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'progress', '委托进度', null, 15, 1, 0, 0, 110, '{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'stockCode', '资产代码', null, 4, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'stockName', '资产名称', null, 5, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'tradeTime', '委托时间', null, 17, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'tradedBalance', '已成金额', null, 13, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'tradedPrice', '成交均价', null, 14, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'tradedVolume', '已成交量', null, 12, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"0"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'tradingDay', '委托日期', null, 16, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherExeInfo', 'EX018', 'username', '交易员', null, 18, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

--指令成交
DELETE FROM CFG_DATATABLE_SHOW T WHERE T.PAGECODE = 'CfetsEtrDispatcher' AND T.DTCODE = 'CfetsEtrDispatcherDealInfo' AND T.SPCDCODE = 'EX018';
insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'brokerEntrustId', '委托编号', null, 1, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'brokerTradeId', '成交编号', null, 2, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'entrustBsStr', '委托方向', null, 6, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'financeAccount', '资金账户', null, 3, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'marketStr', '交易场所', null, 7, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'stockCode', '资产代码', null, 4, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'stockName', '资产名称', null, 5, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'tradeTime', '成交时间', null, 12, 1, 0, 0, 100, '{"colAlign": "left","supportSearch":"精确"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'tradedBalance', '成交金额', null, 10, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'tradedPrice', '成交价格', null, 9, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"3"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'tradedVolume', '成交数量', null, 8, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"0"}', 0, 'COMM', 1, 0, null, null, 0, '999999');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC)
values ('CfetsEtrDispatcher', 'CfetsEtrDispatcherDealInfo', 'EX018', 'tradingDay', '成交日期', null, 11, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0, '999999');