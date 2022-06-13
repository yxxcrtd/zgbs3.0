delete sys_datadict_list t where t.dict_code = 'DELIVERY_RESULT' and t.item_code in ('05');

insert into SYS_DATADICT_LIST(DICT_CODE,ITEM_CODE,ITEM_VALUE,ORDER_NO,DEFAULT_VALUE,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,INSTITUTION_CODE)
values ('DELIVERY_RESULT','05','交收通知','5','0','system','',sysdate, 'system',sysdate,null);