delete from CFG_DATATABLE_SHOW where PAGECODE ='CfetsTrdQuery' and DTCODE = 'CfetsTrdQueryRegPInfo' and SPCDCODE = '04' and COLCODE = 'payableAmount';

INSERT INTO CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC, IS_HIDE)
VALUES ('CfetsTrdQuery', 'CfetsTrdQueryRegPInfo', '04', 'payableAmount', '应缴款金额（元）', null, 6, 1, 0, 1, 100, '{"colAlign": "right","dataType":"amount","precision":"2"}', 0, 'COMM', 1, 0, null, null, 0, '999999', null);

UPDATE CFG_DATATABLE_SHOW SET SEQNO = 7 WHERE PAGECODE = 'CfetsTrdQuery' AND DTCODE = 'CfetsTrdQueryRegPInfo' AND SPCDCODE = '04' AND COLCODE = 'businessModelStr' ;
UPDATE CFG_DATATABLE_SHOW SET SEQNO = 8 WHERE PAGECODE = 'CfetsTrdQuery' AND DTCODE = 'CfetsTrdQueryRegPInfo' AND SPCDCODE = '04' AND COLCODE = 'secAcctName' ;

UPDATE CFG_READONLY_FORM_SHOW SET EXTENDCFG = '{"colAlign": "right","dataType":"amount","precision":"4","unit":"yuan"}' WHERE PAGECODE = 'CfetsTrdQuery' AND DTCODE = 'CfetsTrdQueryRegInfo' AND SPCDCODE = '04,B' AND COLCODE = 'issuePrice';
UPDATE CFG_READONLY_FORM_SHOW SET EXTENDCFG = '{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}' WHERE PAGECODE = 'CfetsTrdQuery' AND DTCODE = 'CfetsTrdQueryRegInfo' AND SPCDCODE = '04,B' AND COLCODE = 'payableAmount';
UPDATE CFG_READONLY_FORM_SHOW SET EXTENDCFG = '{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}' WHERE PAGECODE = 'CfetsTrdQuery' AND DTCODE = 'CfetsTrdQueryRegInfo' AND SPCDCODE = '04,B' AND COLCODE = 'settFeeAmt';
UPDATE CFG_READONLY_FORM_SHOW SET EXTENDCFG = '{"colAlign": "right","dataType":"amount","precision":"2","unit":"wanYuan","divisor":"10000"}' WHERE PAGECODE = 'CfetsTrdQuery' AND DTCODE = 'CfetsTrdQueryRegInfo' AND SPCDCODE = '04,B' AND COLCODE = 'subscriptionAmount';