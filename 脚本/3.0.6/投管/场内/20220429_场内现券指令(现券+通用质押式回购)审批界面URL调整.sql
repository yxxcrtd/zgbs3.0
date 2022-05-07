
delete from sys_page where page_id = 'etrExchangeTradeSec';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values('etrExchangeTradeSec','ETR','/home.html#/approve/shareOrderBond'
                           ,'etrexchange/secrepo/etrExchangeTradeSec','',null,null,'02','01'
                           ,'场内现券指令-现券','system',null,sysdate,'system',sysdate);

delete from sys_page where page_id = 'etrExchangeTradeRepo';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values('etrExchangeTradeRepo','ETR','/home.html#/approve/shareOrderRepo'
                           ,'etrexchange/secrepo/etrExchangeTradeRepo','',null,null,'02','01'
                           ,'场内现券指令-通用质押式回购','system',null,sysdate,'system',sysdate);