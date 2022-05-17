--指令信息
DELETE FROM CFG_READONLY_FORM_SHOW T WHERE T.PAGECODE = 'FieldEtrDispatcher' AND T.DTCODE = 'FieldEtrDispatcherInfo' AND T.SPCDCODE = 'EX005' AND T.COLCODE IN ('entrustNum');

insert into CFG_READONLY_FORM_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, EXTENDCFG, STATUS, REMARK1, REMARK2, REMARK3)
values ('FieldEtrDispatcher', 'FieldEtrDispatcherInfo', 'EX005', 'entrustNum', '指令数量', null, 12, '{"colAlign": "left","dataType":"amount","precision":"2","unit":"wanYuan"}', 1, 0, null, null);
