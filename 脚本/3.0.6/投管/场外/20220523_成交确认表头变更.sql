
alter table CFG_DATATABLE_SHOW drop COLUMN IS_HIDE;
alter table CFG_DATATABLE_SHOW add (IS_HIDE CHAR);
comment on column CFG_DATATABLE_SHOW.IS_HIDE is '是否隐藏域';

update CFG_DATATABLE_SHOW set COLCODE = 'rateStr' WHERE PAGECODE = 'CfetsTrdConfirm' AND DTCODE = 'CfetsTrdConfirmExecBidInfo' and COLNAME = '参考收益率/票面利率（%）' ;
update CFG_DATATABLE_SHOW set EXTENDCFG = '{"colAlign": "right"}' WHERE PAGECODE = 'CfetsTrdConfirm' AND DTCODE = 'CfetsTrdConfirmExecBidInfo' and COLNAME = '参考收益率/票面利率（%）' ;

delete from CFG_DATATABLE_SHOW WHERE PAGECODE = 'CfetsTrdConfirm' AND DTCODE = 'CfetsTrdConfirmCstpInfo' AND SPCDCODE = '999999' AND COLCODE = 'cstpTradeId' ;
INSERT INTO CFG_DATATABLE_SHOW (PAGECODE, DTCODE, SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3, ISKEY, SPCDCODE_DESC, IS_HIDE)
VALUES ('CfetsTrdConfirm', 'CfetsTrdConfirmCstpInfo', '999999', 'cstpTradeId', '内部成交编号', null, 1, 0, 0, 1, 100, '{"colAlign": "left","supportSearch":"模糊"}', 0, 'COMM', 1, 0, null, null, 1, '999999', 'Y');
