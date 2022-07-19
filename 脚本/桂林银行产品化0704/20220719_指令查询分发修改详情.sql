-- 修改详情

delete from  CFG_READONLY_FORM_SHOW where DTCODE = 'CfetsEtrDispatcherInfo' and  pagecode = 'CfetsEtrDispatcher' and SPCDCODE = 'EX011' and COLNAME = '本方交易员' and
COLCODE = 'trader';
delete from  CFG_READONLY_FORM_SHOW where DTCODE = 'CfetsEtrDispatcherInfo' and  pagecode = 'CfetsEtrDispatcher' and SPCDCODE = 'EX011' and COLNAME = '对方交易员' and
 COLCODE = 'counterTrader';
delete from  CFG_READONLY_FORM_SHOW where DTCODE = 'CfetsEtrDispatcherInfo' and  pagecode = 'CfetsEtrDispatcher' and SPCDCODE = 'EX011' and COLNAME = '券面金额' and
 COLCODE = 'pledStrdPrinAmt';
 update CFG_READONLY_FORM_SHOW set EXTENDCFG = '{"colAlign": "left","dataType":"amount","precision":"2","unit":"wanYuan"}' where DTCODE = 'CfetsEtrDispatcherInfo' and  pagecode = 'CfetsEtrDispatcher' and SPCDCODE = 'EX011' and COLNAME = '质押数量' and
 COLCODE = 'pledgeNumber';


delete from  CFG_READONLY_FORM_SHOW where DTCODE = 'FieldEtrDispatcherInfo' and  pagecode = 'FieldEtrDispatcher' and SPCDCODE = 'EX011' and COLNAME = '本方交易员' and
 COLCODE = 'trader';
delete from  CFG_READONLY_FORM_SHOW where DTCODE = 'FieldEtrDispatcherInfo' and  pagecode = 'FieldEtrDispatcher' and SPCDCODE = 'EX011' and COLNAME = '对手方交易员' and
 COLCODE = 'counterTrader';
delete from  CFG_READONLY_FORM_SHOW where DTCODE = 'FieldEtrDispatcherInfo' and  pagecode = 'FieldEtrDispatcher' and SPCDCODE = 'EX011' and COLNAME = '券面金额' and
 COLCODE = 'pledStrdPrinAmt';


delete from  CFG_READONLY_FORM_SHOW where DTCODE = 'FieldEtrDispatcherInfo' and  pagecode = 'FieldEtrDispatcher' and SPCDCODE = 'EX010' and COLNAME = '本方交易员' and
 COLCODE = 'trader';
delete from  CFG_READONLY_FORM_SHOW where DTCODE = 'FieldEtrDispatcherInfo' and  pagecode = 'FieldEtrDispatcher' and SPCDCODE = 'EX010' and COLNAME = '对方交易员' and
 COLCODE = 'counterTrader';
delete from  CFG_READONLY_FORM_SHOW where DTCODE = 'FieldEtrDispatcherInfo' and  pagecode = 'FieldEtrDispatcher' and SPCDCODE = 'EX010' and COLNAME = '券面金额' and
 COLCODE = 'pledStrdPrinAmt';
 update CFG_READONLY_FORM_SHOW set EXTENDCFG = '{"colAlign": "left","dataType":"amount","precision":"2","unit":"wanYuan"}' where DTCODE = 'FieldEtrDispatcherInfo' and  pagecode = 'FieldEtrDispatcher' and SPCDCODE = 'EX010' and COLNAME = '质押数量' and
 COLCODE = 'pledgeNumber';
