/**结算指令接口规则配置增加交收状态字段**/
delete from val_temp_col_check_info info where info.tmp_e_name='ValCleanOrder' and info.col_e_name='settleStatus';
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('结算指令', 'ValCleanOrder', '交收状态', 'settleStatus', '1', 'N', 2, '1', 'IS_SUCCESS', 'system', null,sysdate, 'system', sysdate);

/**指令类型修改为非必填项**/
update val_temp_col_check_info info set info.is_null='N' where info.tmp_e_name='ValCleanOrder' and info.col_e_name='orderType';
/**结算指令接口增加字段：金融产品类型、原交易方向、原交易投资目的**/
delete from val_temp_col_check_info info where info.tmp_e_name='ValCleanOrder' and info.col_e_name='finprodType';
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('结算指令', 'ValCleanOrder', '金融产品类型', 'finprodType', '1', 'Y', 32, '1', 'FINPROD_TYPE', 'system', null,sysdate, 'system', sysdate);
delete from val_temp_col_check_info info where info.tmp_e_name='ValCleanOrder' and info.col_e_name='tradePs';
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('结算指令', 'ValCleanOrder', '原交易方向', 'tradePs', '1', 'Y', 32, '1', 'PS', 'system', null,sysdate, 'system', sysdate);
delete from val_temp_col_check_info info where info.tmp_e_name='ValCleanOrder' and info.col_e_name='invAim';
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('结算指令', 'ValCleanOrder', '原交易投资目的', 'invAim', '1', 'N', 32, '1', 'INV_AIM', 'system', null,sysdate, 'system', sysdate);
