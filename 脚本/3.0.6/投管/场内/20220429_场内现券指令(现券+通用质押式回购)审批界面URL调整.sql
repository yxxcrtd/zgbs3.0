
delete from sys_page where page_id = 'etrExchangeTradeSec';
insert into sys_page values('etrExchangeTradeSec','ETR','/home.html#/approve/shareOrderBond'
                           ,'etrexchange/secrepo/etrExchangeTradeSec','',null,null,'02','01'
                           ,'场内现券指令-现券','system',null,sysdate,'system',sysdate);

delete from sys_page where page_id = 'etrExchangeTradeRepo';
insert into sys_page values('etrExchangeTradeRepo','ETR','/home.html#/approve/shareOrderRepo'
                           ,'etrexchange/secrepo/etrExchangeTradeRepo','',null,null,'02','01'
                           ,'场内现券指令-通用质押式回购','system',null,sysdate,'system',sysdate);