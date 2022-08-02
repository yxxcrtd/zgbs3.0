
update CFG_DATATABLE_SHOW set EXTENDCFG = '{"colAlign": "right","dataType":"amount","precision":"4","supportSearch":"精确","filedType":"number"}'
where PAGECODE = 'EtrExchangeAgreRepTrade001' and DTCODE = 'POSITION' and COLCODE = 'price';

update CFG_DATATABLE_SHOW set EXTENDCFG = '{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"模糊","filedType":"number"}'
where PAGECODE = 'EtrExchangeAgreRepTrade001' and DTCODE = 'UNOPENQUOTA' and COLCODE = 'intAmt';

update CFG_DATATABLE_SHOW set COLNAME = '首次结算日'
where PAGECODE = 'EtrExchangeAgreRepTrade001' and DTCODE = 'UNOPENQUOTA' and COLCODE = 'fristSettleDate';

update CFG_DATATABLE_SHOW set EXTENDCFG = '{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确"}',COLNAME = '质押数量(万元)'
where PAGECODE = 'EtrExchangeAgreRepTrade001' and DTCODE = 'UNOPENQUOTA' and COLCODE = 'repSecNum';
update CFG_DATATABLE_SHOW set COLNAME = '回购金额(元)'
where PAGECODE = 'EtrExchangeAgreRepTrade001' and DTCODE = 'UNOPENQUOTA' and COLCODE = 'tradeAmt';
update CFG_DATATABLE_SHOW set COLNAME = '换出质押数量(万元)'
where PAGECODE = 'EtrExchangeAgreRepTrade001' and DTCODE = 'UNOPENQUOTA' and COLCODE = 'outRepSecNum';
update CFG_DATATABLE_SHOW set COLNAME = '换入质押数量(万元)'
where PAGECODE = 'EtrExchangeAgreRepTrade001' and DTCODE = 'UNOPENQUOTA' and COLCODE = 'newRepSecNum';
update CFG_DATATABLE_SHOW set COLNAME = '原质押数量(万元)'
where PAGECODE = 'EtrExchangeAgreRepTrade001' and DTCODE = 'UNOPENQUOTA' and COLCODE = 'origPledgeStampNum';
update CFG_DATATABLE_SHOW set COLNAME = '原回购金额(元)'
where PAGECODE = 'EtrExchangeAgreRepTrade001' and DTCODE = 'UNOPENQUOTA' and COLCODE = 'origTradeAmt';
update CFG_DATATABLE_SHOW set COLNAME = '占款天数(天)'
where PAGECODE = 'EtrExchangeAgreRepTrade001' and DTCODE = 'UNOPENQUOTA' and COLCODE = 'occupyDays';
update CFG_DATATABLE_SHOW set COLNAME = '回购期限(天)'
where PAGECODE = 'EtrExchangeAgreRepTrade001' and DTCODE = 'UNOPENQUOTA' and COLCODE = 'term';