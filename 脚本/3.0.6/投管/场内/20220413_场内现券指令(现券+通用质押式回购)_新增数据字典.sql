
delete from SYS_DATADICT where dict_code = 'ASSIGN_DEAL_MODE';
insert into SYS_DATADICT values ('ASSIGN_DEAL_MODE','TG','指定交易方式','','02','system','',sysdate, 'system',sysdate);

delete from SYS_DATADICT_LIST where DICT_CODE ='ASSIGN_DEAL_MODE';
insert into SYS_DATADICT_LIST values ('ASSIGN_DEAL_MODE','0','匹配成交','1','0','system','',sysdate, 'system',sysdate,'');
insert into SYS_DATADICT_LIST values ('ASSIGN_DEAL_MODE','FI9','协商成交','2','0','system','',sysdate, 'system',sysdate,'');


delete from SYS_DATADICT where dict_code = 'PRICE_CLASS';
insert into SYS_DATADICT values ('PRICE_CLASS','TG','计价方式','','02','system','',sysdate, 'system',sysdate);

delete from SYS_DATADICT_LIST where DICT_CODE ='PRICE_CLASS';
insert into SYS_DATADICT_LIST values ('PRICE_CLASS','01','全价','1','0','system','',sysdate, 'system',sysdate,'');
insert into SYS_DATADICT_LIST values ('PRICE_CLASS','02','净价','2','0','system','',sysdate, 'system',sysdate,'');

delete from SYS_DATADICT where dict_code = 'PRICE_TIME_TYPE';
insert into SYS_DATADICT values ('PRICE_TIME_TYPE','TG','价格申报范围类型','','02','system','',sysdate, 'system',sysdate);

delete from SYS_DATADICT_LIST where DICT_CODE ='PRICE_TIME_TYPE';
insert into SYS_DATADICT_LIST values ('PRICE_TIME_TYPE','01','开盘集合竞价','1','0','system','',sysdate, 'system',sysdate,'');
insert into SYS_DATADICT_LIST values ('PRICE_TIME_TYPE','02','连续竞价','2','0','system','',sysdate, 'system',sysdate,'');
insert into SYS_DATADICT_LIST values ('PRICE_TIME_TYPE','03','收盘集合竞价','3','0','system','',sysdate, 'system',sysdate,'');
insert into SYS_DATADICT_LIST values ('PRICE_TIME_TYPE','04','开市停牌阶段','4','0','system','',sysdate, 'system',sysdate,'');
