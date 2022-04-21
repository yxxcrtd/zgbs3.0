delete SYS_DATADICT t where t.dict_code = 'DELIVERY_RESULT' and t.system_code = 'TG';
insert into SYS_DATADICT values ('DELIVERY_RESULT','TG','交收结果','','02','system','',sysdate, 'system',sysdate);

delete sys_datadict_list t where t.dict_code = 'DELIVERY_RESULT' and t.item_code in ('01','02','03','04');
insert into SYS_DATADICT_LIST values ('DELIVERY_RESULT','01','正常交收','1','0','system','',sysdate, 'system',sysdate,null);
insert into SYS_DATADICT_LIST values ('DELIVERY_RESULT','02','部分交收','2','0','system','',sysdate, 'system',sysdate,null);
insert into SYS_DATADICT_LIST values ('DELIVERY_RESULT','03','交收失败','3','0','system','',sysdate, 'system',sysdate,null);
insert into SYS_DATADICT_LIST values ('DELIVERY_RESULT','04','未交收','3','0','system','',sysdate, 'system',sysdate,null);
