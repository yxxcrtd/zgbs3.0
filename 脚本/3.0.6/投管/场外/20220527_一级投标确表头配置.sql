delete from cfg_datatable_show where PAGECODE = 'CfetsTrdFirstBidFirm001' and DTCODE in ('TenderInfo','DistributionInfo');
--投标

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'validStatusStr', '有效状态', 'validStatus','1', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'firstBidStatusStr', '中标状态', 'firstBidStatus','2', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'bidId', '投标编号', '','3', '1', '0', '0','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','1');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'finprodMarketId', '资产代码', '','4', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'finprodAbbr', '资产简称', '','5', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'tenderTargetStr', '招标标的', 'tenderTarget','6', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'bidDateStr', '投标日', '','7', '1', '0', '0','180','{"colAlign": "left","supportSearch":"精确"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'etrBidPriceTotal', '指令投标券面总额(万元)', '','8', '1', '0', '0','120','{"colAlign": "right","supportSearch":"精确","dataType":"amount","precision":"2","divisor":"10000"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'totalBiddingNum', '汇总投标券面总额(万元)', '','9', '1', '0', '0','120','{"colAlign": "right","supportSearch":"精确","dataType":"amount","precision":"2","divisor":"10000"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'bidPrice', '投标标位', '','10', '1', '0', '0','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'biddingMethodStr', '投标方式', 'biddingMethod','11', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'payTypeStr', '缴款方式', 'payType','12', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'bidCfmDateStr', '投标确认日', '','13', '1', '0', '0','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'tradeMarketStr', '交易场所', 'tradeMarket','14', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'counterName', '交易对手', '','15', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'TenderInfo','999999', 'remark', '备注', '','16', '1', '0', '0','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');
--分销买入

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'validStatusStr', '有效状态', 'validStatus','1', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'firstBidStatusStr', '中标状态', 'firstBidStatus','2', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'bidId', '投标编号', '','3', '1', '0', '0','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'finprodMarketId', '资产代码', '','4', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'finprodAbbr', '资产简称', '','5', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','1');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'tenderTargetStr', '招标标的', 'tenderTarget','6', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'bidDateStr', '投标日', '','7', '1', '0', '0','180','{"colAlign": "left","supportSearch":"精确"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'etrBidPriceTotal', '指令投标券面总额(万元)', '','8', '1', '0', '0','120','{"colAlign": "right","supportSearch":"精确","dataType":"amount","precision":"2","divisor":"10000"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'bidPriceTotal', '券面总额(万元)', '','9', '1', '0', '0','120','{"colAlign": "right","supportSearch":"精确","dataType":"amount","precision":"2","divisor":"10000"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'biddingMethodStr', '投标方式', 'biddingMethod','10', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'payTypeStr', '缴款方式', 'payType','11', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'bidPrice', '投标标位', '','12', '1', '0', '0','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'winPrice', '中标标位', '','13', '1', '0', '0','120','{"colAlign": "right","supportSearch":"精确"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'price', '净价(元)', '','14', '1', '0', '0','120','{"colAlign": "right","supportSearch":"精确","dataType":"amount","precision":"4"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'payableAmount', '缴款金额(元)', '','15', '1', '0', '0','120','{"colAlign": "right","supportSearch":"精确","dataType":"amount","precision":"4"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'settFeeAmt', '结算服务费(元)', '','16', '1', '0', '0','120','{"colAlign": "right","supportSearch":"精确","dataType":"amount","precision":"4"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'payDateStr', '缴款日', 'payDate','17', '1', '0', '0','120','{"colAlign": "left","supportSearch":"精确"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'assignRegdateStr', '债权债务登记日', 'assignRegdate','18', '1', '0', '0','120','{"colAlign": "left","supportSearch":"精确"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'listingDateStr', '上市日', 'listingDate','19', '1', '0', '0','120','{"colAlign": "left","supportSearch":"精确"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'bidCfmDateStr', '投标确认日', '','20', '1', '0', '0','120','{"colAlign": "left","supportSearch":"精确"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'dealDateStr', '成交日期', 'dealDate','21', '1', '0', '0','120','{"colAlign": "left","supportSearch":"精确"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'tradeMarketStr', '交易场所', 'tradeMarket','22', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'counterName', '交易对手', '','23', '1', '0', '1','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');

insert into cfg_datatable_show (PAGECODE, DTCODE,SPCDCODE, COLCODE, COLNAME, DICTCODE, SEQNO, DISPLAYED, FROZEN, SORT, MAXLEN, EXTENDCFG, EFFERANGE, RANGEID, STATUS, REMARK1, REMARK2, REMARK3,ISKEY) 
values('CfetsTrdFirstBidFirm001', 'DistributionInfo','999999', 'remark', '备注', '','24', '1', '0', '0','120','{"colAlign": "left","supportSearch":"模糊"}','0','COMM','1','0','','','0');
