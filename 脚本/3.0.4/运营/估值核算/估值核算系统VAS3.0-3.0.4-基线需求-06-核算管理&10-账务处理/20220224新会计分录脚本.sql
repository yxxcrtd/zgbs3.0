--费用类型增加资产推荐费
delete from sys_datadict_list d where d.dict_code='FEE_TYPE' and d.item_code='C15';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('FEE_TYPE', 'C15', '资产推荐费', 18, '0', 'system', null, sysdate, 'system', sysdate, null);
