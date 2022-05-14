--指令查询列表: 指令数量
DELETE FROM CFG_DATATABLE_SHOW T WHERE T.PAGECODE = 'FieldEtrInfoQuery' AND T.COLCODE = 'entrustNum';
DELETE FROM CFG_DATATABLE_SHOW T WHERE T.PAGECODE = 'FieldEtrInfoQuery' AND T.COLCODE = 'entrustNumStr';

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'entrustNumStr', '指令数量', 'entrustNum', 18, 1, 0, 1, 100, '{"colAlign": "left","precision":"0","supportSearch":"精确"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'entrustNumStr', '指令数量', 'entrustNum', 18, 1, 0, 1, 100, '{"colAlign": "left","precision":"0","supportSearch":"精确"}', 1, 'admin', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'entrustNumStr', '指令数量', 'entrustNum', 18, 1, 0, 1, 100, '{"colAlign": "left","precision":"0","supportSearch":"精确"}', 1, 'dhj', 1, 0, null, null, 0);


--指令信息
DELETE FROM CFG_READONLY_FORM_SHOW T WHERE T.PAGECODE = 'FieldEtrDispatcher' AND T.DTCODE = 'FieldEtrDispatcherInfo' AND T.SPCDCODE = 'EX005' AND T.COLCODE IN ('entrustNum','pledgeStandardNumber','pledgeRatio');

insert into CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX005', 'entrustNum', '指令数量', null, 12, '{"colAlign": "left","dataType":"amount","precision":"4","unit":"wanYuan"}', 1, 0, null, null);

insert into CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX005', 'pledgeRatio', '折算率', null, 14, '{"colAlign": "left","dataType":"amount","precision":"0","unit":"%"}', 1, 0, null, null);

insert into CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX005', 'pledgeStandardNumber', '标准券数量', null, 15, '{"colAlign": "left","dataType":"amount","precision":"4","unit":"wanYuan"}', 1, 0, null, null);

--指令执行
DELETE FROM CFG_DATATABLE_SHOW T WHERE T.PAGECODE = 'FieldEtrDispatcher' AND T.DTCODE = 'FieldEtrDispatcherExeInfo' AND T.SPCDCODE = 'EX005' AND T.COLCODE IN ('entrustVolume','tradedVolume');
insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX005', 'entrustVolume', '委托数量(万元)', null, 8, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherExeInfo', 'EX005', 'tradedVolume', '已成交量(万元)', null, 12, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2"}', 0, 'COMM', 1, 0, null, null, 0);

--指令成交
DELETE FROM CFG_DATATABLE_SHOW T WHERE T.PAGECODE = 'FieldEtrDispatcher' AND T.DTCODE = 'FieldEtrDispatcherDealInfo' AND T.SPCDCODE = 'EX005' AND T.COLCODE IN ('tradedVolume','tradedVolumeStr');

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherDealInfo', 'EX005', 'tradedVolume', '成交数量(万元)', null, 8, 1, 0, 0, 100, '{"colAlign": "left","dataType":"amount","precision":"2"}', 0, 'COMM', 1, 0, null, null, 0);
