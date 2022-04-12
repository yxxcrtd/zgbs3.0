
delete from INTERF_POSITION_CONFIG where entity = 'com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest';
insert into INTERF_POSITION_CONFIG
values ('ETR_PAGE','F01','T01','new','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'Bond002','system',null,sysdate,'system',sysdate,'场内债券指令（现券+通用质押式回购）-买入新建','Y');
insert into INTERF_POSITION_CONFIG
values ('ETR_PAGE','F01','T01','modify','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'Bond002','system',null,sysdate,'system',sysdate,'场内债券指令（现券+通用质押式回购）-买入修改','Y');
insert into INTERF_POSITION_CONFIG
values ('ETR_PAGE','F01','T02','new','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'Bond002','system',null,sysdate,'system',sysdate,'场内债券指令（现券+通用质押式回购）-卖出新建','Y');
insert into INTERF_POSITION_CONFIG
values ('ETR_PAGE','F01','T02','modify','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'Bond002','system',null,sysdate,'system',sysdate,'场内债券指令（现券+通用质押式回购）-卖出修改','Y');

insert into INTERF_POSITION_CONFIG
values ('ETR_PAGE','F07','03','new','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'REPO003','system',null,sysdate,'system',sysdate,'场内债券指令（现券+通用质押式回购）-正回购新建','Y');
insert into INTERF_POSITION_CONFIG
values ('ETR_PAGE','F07','03','modify','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'REPO003','system',null,sysdate,'system',sysdate,'场内债券指令（现券+通用质押式回购）-正回购修改','Y');
insert into INTERF_POSITION_CONFIG
values ('ETR_PAGE','F07','04','new','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'REPO003','system',null,sysdate,'system',sysdate,'场内债券指令（现券+通用质押式回购）-逆回购新建','Y');
insert into INTERF_POSITION_CONFIG
values ('ETR_PAGE','F07','04','modify','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
,'REPO003','system',null,sysdate,'system',sysdate,'场内债券指令（现券+通用质押式回购）-逆回购修改','Y');




delete from sys_menu where menu_id = 'SETTLE_MANAGEMENT_001_003_014' and page_id = 'etrExchangeTradeSecRepo';
insert into sys_menu values('SETTLE_MANAGEMENT_001_003_014','ETR',null,'SETTLE_MANAGEMENT_001_003'
,'etrExchangeTradeSecRepo',3,'02','场内现券指令','system',null,sysdate,'system',sysdate);


delete from sys_page where page_id = 'etrExchangeTradeSecRepo';
insert into sys_page values('etrExchangeTradeSecRepo','ETR','home.html#/instructions/secrepoorder?a=a'
,'etrexchange/secrepo/etrExchangeTradeSecRepo001','SETTLE_MANAGEMENT_001_003_014',null,null,'02','01'
,'场内现券指令','system',null,sysdate,'system',sysdate);


delete from sys_page_element where page_id = 'etrExchangeTradeSecRepo';
insert into sys_page_element values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_close','01','01',null,'关闭'
,'system',null,sysdate,'system',sysdate,null,null);
insert into sys_page_element values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_edit','01','01',null,'修改'
,'system',null,sysdate,'system',sysdate,null,null);
insert into sys_page_element values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_import','01','01',null,'导入'
,'system',null,sysdate,'system',sysdate,null,null);
insert into sys_page_element values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_reCell','01','01',null,'撤回'
,'system',null,sysdate,'system',sysdate,null,null);
insert into sys_page_element values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_save','01','01',null,'保存'
,'system',null,sysdate,'system',sysdate,null,null);
insert into sys_page_element values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_submit','01','01',null,'提交'
,'system',null,sysdate,'system',sysdate,null,null);


delete from sys_wfl_business_module where page_id = 'etrExchangeTradeSecRepo' and element_id = 'etrExchangeTradeSecRepo_btn_submit';
insert into sys_wfl_business_module values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_submit','oneApproval','system'
,null,sysdate,'system',sysdate,'ALL','02');
