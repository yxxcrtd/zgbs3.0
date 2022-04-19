-- 持仓
delete from CFG_DATATABLE_SHOW where pagecode = 'etrExchangeTradeSecRepo' and DTCODE = 'BONDS';
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','portfolioName','组合名称','',1,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊","filedType":"word"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','dealCurAcctName','资金账户','',2,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊","filedType":"word"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','tradeMarketStr','交易场所','tradeMarket',3,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊","filedType":"word"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','assetTypeStr','资产类型','assetType',4,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊","filedType":"word"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','finprodMarketId','资产代码','',5,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊","filedType":"word"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','secName','资产名称','',6,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊","filedType":"word"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','businessModelStr','业务模式','businessModel',7,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊","filedType":"word"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','invAimStr','投资目的','invAim',7,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊","filedType":"word"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','positionsNum','持仓数量','',8,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确","filedType":"number"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','sellNum','可卖数量','',9,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确","filedType":"number"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','positionUnitCost','持仓单位成本','',10,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"4","supportSearch":"精确","filedType":"number"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','positionsAmt','持仓成本','',11,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确","filedType":"number"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','price','最新价','',12,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"4","supportSearch":"精确","filedType":"number"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','floating','浮动盈亏','',13,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确","filedType":"number"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','BONDS','999999','floatingPL','盈亏比例%','',14,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"4","supportSearch":"精确","filedType":"number"}',0,'COMM',1,0,null,null,0);

--指令列表
delete from CFG_DATATABLE_SHOW where pagecode = 'etrExchangeTradeSecRepo' and DTCODE = 'ETRINFOS';
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','entrustId','指令编号','',1,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','entrustStatusString','指令状态','entrustStatus',2,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','implementPercentAge','执行进度(%)','implementPercent',3,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','dealPercentAge','成交进度(%)','dealPercent',4,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','portfolioName','组合名称','',5,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊"',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','dealCurAcctName','资金账户','',6,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊"',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','finprodMarketId','资产代码','',7,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','finprodAbbr','资产名称','',8,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊"',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','assetTypeString','资产类型','assetType',9,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','tradeMarketString','交易场所','tradeMarket',10,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊"',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','psStr','委托方向','ps',11,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','priceModelString','价格模式','priceModel',12,1,0,1,100,
'{"colAlign": "left","supportSearch":"模糊"',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','price','指令价格','',13,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确","filedType":"number"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','entrustNum','指令数量','',14,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"4","supportSearch":"精确","filedType":"number"}',0,'COMM',1,0,null,null,0);
insert into CFG_DATATABLE_SHOW values('etrExchangeTradeSecRepo','ETRINFOS','999999','entrustAmt','指令金额','',15,1,0,1,100,
'{"colAlign": "right","dataType":"amount","precision":"2","supportSearch":"精确","filedType":"number"}',0,'COMM',1,0,null,null,0);
