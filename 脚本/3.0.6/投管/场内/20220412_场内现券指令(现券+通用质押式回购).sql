
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

insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F07','03','new','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
       ,'REPO003','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-正回购新建','Y');
insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F07','03','modify','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
       ,'REPO003','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-正回购修改','Y');
insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F07','04','new','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
       ,'REPO003','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-逆回购新建','Y');
insert into INTERF_POSITION_CONFIG (TRADE_PLAT, FINPROD_TYPE, PS, BUSI_ACT, POSITION_TYPE, ENTITY, INTERF_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, REMARK, ENABLE)
values ('ETR_PAGE','F07','04','modify','ETR','com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest'
       ,'REPO003','system',null,sysdate,'system',sysdate,'场内现券指令（现券+通用质押式回购）-逆回购修改','Y');


delete from sys_menu where menu_id = 'SETTLE_MANAGEMENT_001_003_014' and page_id = 'etrExchangeTradeSecRepo';
insert into sys_menu (MENU_ID, SYSTEM_CODE, ICON, PARENT_MENU_ID, PAGE_ID, ORDER_NO, IS_SPREAD, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values('SETTLE_MANAGEMENT_001_003_014','ETR',null,'SETTLE_MANAGEMENT_001_003'
      ,'etrExchangeTradeSecRepo',3,'02','场内现券指令','system',null,sysdate,'system',sysdate);


delete from sys_page where page_id = 'etrExchangeTradeSecRepo';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values('etrExchangeTradeSecRepo','ETR','home.html#/instructions/shareorder?a=a'
      ,'etrexchange/secrepo/etrExchangeTradeSecRepo001','SETTLE_MANAGEMENT_001_003_014',null,null,'02','01'
      ,'场内现券指令','system',null,sysdate,'system',sysdate);

delete from sys_page where page_id = 'etrExchangeTradeSec';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values('etrExchangeTradeSec','ETR','/home.html#/approve/shareOrderBond?a=a'
      ,'etrexchange/secrepo/etrExchangeTradeSec','',null,null,'02','01'
      ,'场内现券指令-现券','system',null,sysdate,'system',sysdate);

delete from sys_page where page_id = 'etrExchangeTradeRepo';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values('etrExchangeTradeRepo','ETR','/home.html#/approve/shareOrderRepo?a=a'
      ,'etrexchange/secrepo/etrExchangeTradeRepo','',null,null,'02','01'
      ,'场内现券指令-通用质押式回购','system',null,sysdate,'system',sysdate);


delete from sys_page_element where page_id = 'etrExchangeTradeSecRepo';
insert into sys_page_element (PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG)
values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_close','01','01',null,'关闭'
      ,'system',null,sysdate,'system',sysdate,null);
insert into sys_page_element (PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG)
values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_edit','01','01',null,'修改'
      ,'system',null,sysdate,'system',sysdate,null);
insert into sys_page_element (PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG)
values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_import','01','01',null,'导入'
      ,'system',null,sysdate,'system',sysdate,null);
insert into sys_page_element (PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG)
values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_reCell','01','01',null,'撤回'
      ,'system',null,sysdate,'system',sysdate,null);
insert into sys_page_element (PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG)
values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_save','01','01',null,'保存'
      ,'system',null,sysdate,'system',sysdate,null);
insert into sys_page_element (PAGE_ID,ELEMENT_ID,ELEMENT_TYPE,IS_CONTROL,TRANSFER_PAGE_ID,REMARK,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,ADMIN_FLAG)
values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_submit','01','01',null,'提交'
      ,'system',null,sysdate,'system',sysdate,null);

/**
delete from sys_wfl_business_module where page_id = 'etrExchangeTradeSecRepo' and element_id = 'etrExchangeTradeSecRepo_btn_submit';
insert into sys_wfl_business_module values('etrExchangeTradeSecRepo','etrExchangeTradeSecRepo_btn_submit','oneApproval','system'
,null,sysdate,'system',sysdate,'ALL','02');
**/

delete from sys_wfl_business_module where page_id in ('etrExchangeTradeSec','etrExchangeTradeRepo')
                                      and element_id in ('etrExchangeTradeSec_btn_submit','etrExchangeTradeSec_btn_save',
                                                         'etrExchangeTradeRepo_btn_submit','etrExchangeTradeRepo_btn_save');
insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID,
                                     CREATE_USER, CREATE_DEPT, CREATE_TIME,
                                     UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('etrExchangeTradeSec','etrExchangeTradeSec_btn_submit','oneApproval','system',
        '',sysdate,'system',sysdate,'ALL','02');
insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID,
                                     CREATE_USER, CREATE_DEPT, CREATE_TIME,
                                     UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('etrExchangeTradeSec','etrExchangeTradeSec_btn_save','oneApproval','system',
        '',sysdate,'system',sysdate,'ALL','02');
insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID,
                                     CREATE_USER, CREATE_DEPT, CREATE_TIME,
                                     UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('etrExchangeTradeRepo','etrExchangeTradeRepo_btn_submit','oneApproval','system',
        '',sysdate,'system',sysdate,'ALL','02');
insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID,
                                     CREATE_USER, CREATE_DEPT, CREATE_TIME,
                                     UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('etrExchangeTradeRepo','etrExchangeTradeRepo_btn_save','oneApproval','system',
        '',sysdate,'system',sysdate,'ALL','02');

DELETE FROM mst_business_id_rule WHERE SEQ_NO = 'ETR54_01' and common_business_type = 'ETR54';
insert into mst_business_id_rule (SEQ_NO, COMMON_BUSINESS_TYPE, TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYPE_5, TYPE_6, BUSINESS_ID_RULE_TYPE, PREFIX_NAME, PREFIX_SEQUENCE_NAME, PREFIX_SEQUENCE_LEN, POSTFIX_NAME, POSTFIX_SEQUENCE_NAME, POSTFIX_SEQUENCE_LEN, DATE_RULE_TYPE, BUSINESS_ID_SERVICE_NAME, BUSINESS_ID_METHOD_NAME, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ETR54_01','ETR54','','','','','','','04','WTBOND','','','','WTBOND_SEQUENCE',4,'01'
       ,'','','system','',sysdate,'system',sysdate);
DELETE FROM mst_business_id_rule WHERE SEQ_NO = 'ETR55_01' and common_business_type = 'ETR55';
insert into mst_business_id_rule (SEQ_NO, COMMON_BUSINESS_TYPE, TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYPE_5, TYPE_6, BUSINESS_ID_RULE_TYPE, PREFIX_NAME, PREFIX_SEQUENCE_NAME, PREFIX_SEQUENCE_LEN, POSTFIX_NAME, POSTFIX_SEQUENCE_NAME, POSTFIX_SEQUENCE_LEN, DATE_RULE_TYPE, BUSINESS_ID_SERVICE_NAME, BUSINESS_ID_METHOD_NAME, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ETR55_01','ETR55','','','','','','','04','WTREPO','','','','WTREPO_SEQUENCE',4,'01'
       ,'','','system','',sysdate,'system',sysdate);
