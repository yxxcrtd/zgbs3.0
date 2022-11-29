/*债券存续期接口*/
DELETE FROM INTERF_POSITION_CONFIG WHERE TRADE_PLAT = 'TRANS_MATCH' AND BUSI_ACT = '19' AND INTERF_TYPE = 'Bond003' AND FINPROD_TYPE ='F01-B14' AND PS ='T07' AND ENTITY = 'com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto';

INSERT INTO INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE) 
VALUES ('TRANS_MATCH', 'F01-B14', 'T07', '19', 'TRD', 'com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto', 'Bond003', 'system', null, sysdate, 'system', sysdate, '债券存续期接口-可转债-成交匹配-到期', 'Y');
