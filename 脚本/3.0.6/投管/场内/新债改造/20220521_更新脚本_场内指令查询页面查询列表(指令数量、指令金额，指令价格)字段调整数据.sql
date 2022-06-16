--指令查询列表: 指令数量
DELETE FROM CFG_DATATABLE_SHOW T WHERE T.PAGECODE = 'FieldEtrInfoQuery' AND T.COLCODE = 'entrustNum';
DELETE FROM CFG_DATATABLE_SHOW T WHERE T.PAGECODE = 'FieldEtrInfoQuery' AND T.COLCODE = 'entrustNumStr';

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'entrustNum', '指令数量', '', 18, 1, 0, 1, 100, '{"colAlign": "left","dataType":"amount","precision":"0","supportSearch":"精确"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'entrustNum', '指令数量', '', 18, 1, 0, 1, 100, '{"colAlign": "left","dataType":"amount","precision":"0","supportSearch":"精确"}', 1, 'admin', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'entrustNum', '指令数量', '', 18, 1, 0, 1, 100, '{"colAlign": "left","dataType":"amount","precision":"0","supportSearch":"精确"}', 1, 'dhj', 1, 0, null, null, 0);


/**ZGCPCN-403**/
--指令查询列表 : 指令金额
DELETE FROM CFG_DATATABLE_SHOW T WHERE T.PAGECODE = 'FieldEtrInfoQuery' AND T.COLCODE IN ('entrustAmt','entrustAmtStr','etrPrice','etrPriceStr');
insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'entrustAmt', '指令金额', '', 16, 1, 0, 1, 100, '{"colAlign": "left","dataType":"amount","precision":"2","supportSearch":"精确"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'entrustAmt', '指令金额', '', 16, 1, 0, 1, 100, '{"colAlign": "left","dataType":"amount","precision":"2","supportSearch":"精确"}', 1, 'admin', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'entrustAmt', '指令金额', '', 16, 1, 0, 1, 100, '{"colAlign": "left","dataType":"amount","precision":"2","supportSearch":"精确"}', 1, 'dhj', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'etrPrice', '指令价格', '', 17, 1, 0, 1, 100, '{"colAlign": "left","dataType":"amount","precision":"4","supportSearch":"精确"}', 0, 'COMM', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'etrPrice', '指令价格', '', 17, 1, 0, 1, 100, '{"colAlign": "left","dataType":"amount","precision":"4","supportSearch":"精确"}', 1, 'admin', 1, 0, null, null, 0);

insert into CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY)
values ('FieldEtrInfoQuery', 'FieldEtrInfoQueryList', '999999', 'etrPrice', '指令价格', '', 17, 1, 0, 1, 100, '{"colAlign": "left","dataType":"amount","precision":"4","supportSearch":"精确"}', 1, 'dhj', 1, 0, null, null, 0);