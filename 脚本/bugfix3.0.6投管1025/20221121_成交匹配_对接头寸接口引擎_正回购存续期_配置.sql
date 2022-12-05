/*正回购存续期接口*/
DELETE FROM INTERF_POSITION_CONFIG WHERE TRADE_PLAT = 'TRANS_MATCH' AND BUSI_ACT = '19' AND INTERF_TYPE = 'REPO003' AND FINPROD_TYPE ='F02-H02' AND PS ='T07' AND ENTITY = 'com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto';

INSERT INTO INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE) 
VALUES ('TRANS_MATCH', 'F02-H02', 'T07', '19', 'TRD', 'com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto', 'REPO003', 'system', null, sysdate, 'system', sysdate, '正回购存续期接口-竞价回购-成交匹配-到期', 'Y');
