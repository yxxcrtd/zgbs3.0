
delete from INTERF_POSITION_CONFIG where entity = 'com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest';
insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F01','T01','new','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'Bond002','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-买入新建','Y');
insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F01','T01','modify','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'Bond002','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-买入修改','Y');
insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F01','T02','new','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'Bond002','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-卖出新建','Y');
insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F01','T02','modify','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'Bond002','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-卖出修改','Y');

delete from INTERF_POSITION_CONFIG where entity = 'com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSecRepoSaveRepoRequest';
insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F02','03','new','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSecRepoSaveRepoRequest'
,'REPO003','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-正回购新建','Y');
insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F02','03','modify','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSecRepoSaveRepoRequest'
,'REPO003','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-正回购修改','Y');
insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F03','04','new','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSecRepoSaveRepoRequest'
,'REPO003','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-逆回购新建','Y');
insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F03','04','modify','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSecRepoSaveRepoRequest'
,'REPO003','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-逆回购修改','Y');
