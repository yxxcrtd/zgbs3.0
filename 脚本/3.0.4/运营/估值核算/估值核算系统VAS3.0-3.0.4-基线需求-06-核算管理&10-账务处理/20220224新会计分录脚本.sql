--费用类型增加资产推荐费
delete from sys_datadict_list d where d.dict_code='FEE_TYPE' and d.item_code='C15';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('FEE_TYPE', 'C15', '资产推荐费', 18, '0', 'system', null, sysdate, 'system', sysdate, null);

--回购标的接口增加回购类型
delete from val_temp_col_check_info info where info.tmp_e_name='ValRepDealSecSubject' and info.col_e_name='targetBackType';
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('债券回购标的', 'ValRepDealSecSubject', '回购类型', 'targetBackType', '1', 'Y', 32, '1', 'TARGET_BACK_TYPE', 'system', null, sysdate, 'system', sysdate);
