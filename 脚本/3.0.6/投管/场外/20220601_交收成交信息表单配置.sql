delete from cfg_datatable_show where PAGECODE = 'cfetsManualSettleQuery'  and DTCODE = 'CfetsManualSettleInfo' and SPCDCODE in ('01FXP','01ZBP');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01FXP', 'entrustId', '指令代码', '','1', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','一级投标分销买入','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01FXP', 'portfolioName', '组合名称', '','2', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','一级投标分销买入','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01FXP', 'qtyd', '券面总额(万元)', '','3', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2","divisor":"10000"}','0','COMM','1','0','','','0','一级投标分销买入','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01FXP', 'jsFeeAmt', '结算服务费（元）', '','4', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2"}','0','COMM','1','0','','','0','一级投标分销买入','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01FXP', 'tradeAmt', '交易金额（元）', '','5', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2"}','0','COMM','1','0','','','0','一级投标分销买入','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01FXP', 'intAmt', '应计利息总额（元）', '','6', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2"}','0','COMM','1','0','','','0','一级投标分销买入','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01FXP', 'payAmt', '缴款金额（元）', '','7', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2"}','0','COMM','1','0','','','0','一级投标分销买入','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01FXP', 'bidPrice', '投标标位', '','8', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','一级投标分销买入','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01FXP', 'busiTypePositionStr', '业务模式', '','9', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','一级投标分销买入','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01FXP', 'secAcctId', '证券账户', '','10', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','一级投标分销买入','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01FXP', 'invManagerName', '投资经理', '','11', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','一级投标分销买入','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'entrustId', '指令代码', '','1', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'portfolioName', '组合名称', '','2', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'totalBiddingNum', '汇总投标券面总额(万元)', '','3', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2","divisor":"10000"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'theWinPrinamt', '理论中标券面总额 (万元)', '','4', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2","divisor":"10000"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'actWinPrinamt', '实际中标券面总额（万元）', '','5', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2","divisor":"10000"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'bidPrice', '投标标位', '','6', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'winPrice', '中标标位', '','7', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'unitPrice', '中标净价(元)', '','8', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'intAmt', '应计利息(元)', '','9', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'payAmt', '缴款金额(元)', '','10', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'jsFeeAmt', '结算服务费(元)', '','11', '1', '0', '0','110','{"colAlign": "right","dataType":"amount","precision":"2"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'busiTypePositionStr', '业务模式', '','12', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'secAcctId', '证券账户', '','13', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','中标结果维护','');

insert into cfg_datatable_show (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,DISPLAYED,FROZEN,SORT,MAXLEN,EXTENDCFG,EFFERANGE,RANGEID,STATUS,REMARK1,REMARK2,REMARK3,ISKEY,SPCDCODE_DESC,IS_HIDE) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo','01ZBP', 'invManagerName', '投资经理', '','14', '1', '0', '0','110','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0','中标结果维护','');


--只读表单
delete from CFG_READONLY_FORM_SHOW where PAGECODE = 'cfetsManualSettleQuery'  and DTCODE = 'CfetsManualSettleInfo' and SPCDCODE in ('01FXB','01FXA','01FXP','01FXR','01ZBB','01ZBA','01ZBP','01ZBR');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','finprodMarketId', '资产代码', '', '1','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','finprodAbbr', '资产简称', '', '2','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','ouTraderId', '本方交易员', '', '3','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','counterId', '交易对手', '', '4','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','tenderTargetStr', '招标标的', '', '5','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','ccyStr', '币种', '', '6','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','tradeMarketStr', '交易场所', '', '7','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','tradeMarketModeStr', '交易市场', '', '8','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','bidDateList', '投标日', '', '9','{"colAlign": "left","dataType":"dateRange","format":"yyyy-MM-dd"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','payDate', '缴款日', '', '10','{"colAlign": "left","dataType":"date"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','assignRegdate', '债权债务登记日', '', '11','{"colAlign": "left","dataType":"date"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','listingDate', '上市日', '', '12','{"colAlign": "left","dataType":"date"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','bidModleStr', '投标方式', '', '13','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','payTypeStr', '缴款方式', '', '14','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','etrBidPriceTotal', '指令投标券面总额', '', '15','{"colAlign": "right","dataType":"amount","precision":"2","unit":"wanYuan","divisor":"10000"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','qty', '券面总额', '', '16','{"colAlign": "right","dataType":"amount","precision":"2","unit":"wanYuan","divisor":"10000"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','winPrice', '中标标位', '', '17','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','price', '中标净价', '', '18','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','unitInt', '应计利息', '', '19','{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','jsFeeAmt', '结算服务费', '', '20','{"colAlign": "right","dataType":"amount","precision":"2","unit":"wanYuan","divisor":"10000"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXB','tradeId', '投标编号', '', '21','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXA','price', '中标净价', '', '1','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入金融信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXA','unitInt', '应计利息', '', '2','{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', '', '', '投标分销买入金融信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXA','allPrice', '全价', '', '3','{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', '', '', '投标分销买入金融信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXA','tradeAmt', '交易金额', '', '4','{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', '', '', '投标分销买入金融信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXA','intAmt', '应计利息总额', '', '5','{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', '', '', '投标分销买入金融信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXA','payAmt', '缴款金额', '', '6','{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', '', '', '投标分销买入金融信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXP','spanTextOne', '分配方式', '', '1','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入投标产品列表信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01FXR','remark', '备注', '', '1','{"colAlign": "left"}', '1', '0', '', '', '投标分销买入备注信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','finprodMarketId', '资产代码', '', '1','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','finprodAbbr', '资产简称', '', '2','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','ouTraderId', '本方交易员', '', '3','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','counterId', '交易对手', '', '4','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','tenderTargetStr', '招标标的', '', '5','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','ccyStr', '币种', '', '6','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','tradeMarketStr', '交易场所', '', '7','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','tradeMarketModeStr', '交易市场', '', '8','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','bidDateList', '投标日', '', '9','{"colAlign": "left","dataType":"dateRange","format":"yyyy-MM-dd"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','payDate', '缴款日', '', '10','{"colAlign": "left","dataType":"date"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','assignRegdate', '债权债务登记日', '', '11','{"colAlign": "left","dataType":"date"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','listingDate', '上市日', '', '12','{"colAlign": "left","dataType":"date"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','bidModleStr', '投标方式', '', '13','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','payTypeStr', '缴款方式', '', '14','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','totalBidPrinamt', '汇总投标券面总额', '', '15','{"colAlign": "right","dataType":"amount","precision":"2","unit":"wanYuan","divisor":"10000"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','winPrinamt', '中标券面总额', '', '16','{"colAlign": "right","dataType":"amount","precision":"2","unit":"wanYuan","divisor":"10000"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','winPrice', '中标标位', '', '17','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','jsFeeAmt', '结算服务费', '', '18','{"colAlign": "right","dataType":"amount","precision":"2","unit":"wanYuan","divisor":"10000"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBB','tradeId', '投标编号', '', '19','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护基本信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBA','price', '中标净价', '', '1','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护金额信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBA','unitInt', '应计利息', '', '2','{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', '', '', '中标结果维护金额信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBA','allPrice', '全价', '', '3','{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', '', '', '中标结果维护金额信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBA','tradeAmt', '交易金额', '', '4','{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', '', '', '中标结果维护金额信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBA','intAmt', '应计利息总额', '', '5','{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', '', '', '中标结果维护金额信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBA','payAmt', '缴款金额', '', '6','{"colAlign": "right","dataType":"amount","precision":"2","unit":"yuan"}', '1', '0', '', '', '中标结果维护金额信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBP','spanTextOne', '中标方式', '', '1','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护分配明细信息');

insert into CFG_READONLY_FORM_SHOW (PAGECODE,DTCODE,SPCDCODE,COLCODE,COLNAME,DICTCODE,SEQNO,EXTENDCFG,STATUS,REMARK1,REMARK2,REMARK3,SPCDCODE_DESC) 
values('cfetsManualSettleQuery', 'CfetsManualSettleInfo', '01ZBR','remark', '备注', '', '1','{"colAlign": "left"}', '1', '0', '', '', '中标结果维护备注信息');
