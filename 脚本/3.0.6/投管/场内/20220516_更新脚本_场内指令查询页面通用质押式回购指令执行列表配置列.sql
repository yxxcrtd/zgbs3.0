--指令执行
DELETE FROM CFG_DATATABLE_SHOW T WHERE T.PAGECODE = 'FieldEtrDispatcher' AND T.DTCODE = 'FieldEtrDispatcherExeInfo' AND T.SPCDCODE = 'EX018';
insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'brokerEntrustId', '委托编号', null, 1, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'entrustBalance', '委托金额', null, 10, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2","unit":"yuan"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'entrustBsStr', '委托方向', null, 6, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'entrustPrice', '委托价格', null, 9, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"4","unit":"yuan"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'entrustStatusStr', '委托状态', null, 11, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'entrustVolume', '委托数量', null, 8, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2","unit":"wanYuan"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'financeAccount', '资金账户', null, 3, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'marketStr', '交易场所', null, 7, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'portfolioName', '组合名称', null, 2, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'progress', '委托进度', null, 15, 1, 0, 0, 110, '{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'stockCode', '资产代码', null, 4, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'stockName', '资产名称', null, 5, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'tradeTime', '委托时间', null, 17, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'tradedBalance', '已成金额', null, 13, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2","unit":"yuan"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'tradedPrice', '成交均价', null, 14, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2","unit":"yuan"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'tradedVolume', '已成交量', null, 12, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2","unit":"wanYuan"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'tradingDay', '委托日期', null, 16, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX018', 'username', '交易员', null, 18, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);


--指令成交
DELETE FROM CFG_DATATABLE_SHOW T WHERE T.PAGECODE = 'FieldEtrDispatcher' AND T.DTCODE = 'FieldEtrDispatcherDealInfo' AND T.SPCDCODE = 'EX018';
insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'brokerTradeId', '成交编号', null, 2, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'deliveryResult', '交收状态', null, 13, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'deliverySchedule', '交收进度%', null, 13, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'entrustBsStr', '委托方向', null, 6, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'entrustId', '委托编号', null, 1, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'financeAccount', '资金账户', null, 3, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'marketStr', '交易场所', null, 7, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'stockCode', '资产代码', null, 4, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'stockName', '资产名称', null, 5, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'tradeTime', '成交时间', null, 12, 1, 0, 0, 100, '{"colAlign": "left","supportSearch":"精确"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'tradedBalance', '成交金额', null, 10, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2","unit":"yuan"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'tradedPrice', '成交价格', null, 9, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"4","unit":"yuan"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'tradedVolume', '成交数量', null, 8, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2","unit":"wanYuan"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX018', 'tradingDay', '成交日期', null, 11, 1, 0, 0, 100, '{"colAlign": "left"}', 0, 'COMM', 1, 0, null, null, 0);
