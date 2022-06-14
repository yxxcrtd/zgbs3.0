-----存续期查询头寸接口参数
delete from INTERF_POSITION_CONFIG
 where TRADE_PLAT = 'TRANS_MATCH'
   and FINPROD_TYPE = 'F37'
   and PS in ('T01', 'T02', 'T04')
   and ENTITY = 'com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto';

insert into INTERF_POSITION_CONFIG
  (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values
  ('TRANS_MATCH', 'F37', 'T01', '19', 'TRD', 'com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto', 'CPC001', 'system', null,
   sysdate, 'system', sysdate, '信用保护凭证-买入', 'Y');

insert into INTERF_POSITION_CONFIG
  (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values
  ('TRANS_MATCH', 'F37', 'T02', '19', 'TRD', 'com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto', 'CPC001', 'system', null,
   sysdate, 'system', sysdate, '信用保护凭证-卖出', 'Y');

insert into INTERF_POSITION_CONFIG
  (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values
  ('TRANS_MATCH', 'F37', 'T04', '19', 'TRD', 'com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto', 'CPC001', 'system', null,
   sysdate, 'system', sysdate, '信用保护凭证-申购', 'Y');

--成交回报查询头寸接口参数
delete from INTERF_POSITION_CONFIG
 where TRADE_PLAT = 'TRD_TRADE_REPORT'
   and FINPROD_TYPE = 'F37'
   and PS in ('T01', 'T02', 'T04')
   and ENTITY = 'com.joyintech.service.trade.entrustmsgbymq.dto.TrdTempDto';

insert into INTERF_POSITION_CONFIG
  (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values
  ('TRD_TRADE_REPORT', 'F37', 'T01', '17', 'TRD', 'com.joyintech.service.trade.entrustmsgbymq.dto.TrdTempDto', 'CPC001', 'system', null,
   sysdate, 'system', sysdate, '信用保护凭证-买入', 'Y');

insert into INTERF_POSITION_CONFIG
  (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values
  ('TRD_TRADE_REPORT', 'F37', 'T02', '17', 'TRD', 'com.joyintech.service.trade.entrustmsgbymq.dto.TrdTempDto', 'CPC001', 'system', null,
   sysdate, 'system', sysdate, '信用保护凭证-卖出', 'Y');

insert into INTERF_POSITION_CONFIG
  (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values
  ('TRD_TRADE_REPORT', 'F37', 'T04', '17', 'TRD', 'com.joyintech.service.trade.entrustmsgbymq.dto.TrdTempDto', 'CPC001', 'system', null,
   sysdate, 'system', sysdate, '信用保护凭证-申购', 'Y');
