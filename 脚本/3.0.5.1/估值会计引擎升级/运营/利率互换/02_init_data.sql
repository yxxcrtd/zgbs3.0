--新增利率互换相关接口
/*****数据字典 start**************************************************************************/
delete from sys_datadict d where d.dict_code='IRS_CLEAN_TYPE';
insert into sys_datadict (DICT_CODE, SYSTEM_CODE, DICT_CNNAME, DICT_ENNAME, UPDATE_FLG, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('IRS_CLEAN_TYPE', 'VAL', '利率互换清算方式', null, '02', 'yy', null, sysdate, 'yy', sysdate);
delete from sys_datadict_list d where d.dict_code='IRS_CLEAN_TYPE';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('IRS_CLEAN_TYPE', '01', '集中清算', 1, '0', 'yy', null, sysdate, 'yy', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('IRS_CLEAN_TYPE', '02', '多边清算', 2, '0', 'yy', null, sysdate, 'yy', sysdate, null);

delete from sys_datadict d where d.dict_code='VALMODEL_ID';
insert into sys_datadict (DICT_CODE, SYSTEM_CODE, DICT_CNNAME, DICT_ENNAME, UPDATE_FLG, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('VALMODEL_ID', 'VAL', '利率互换估值模型', null, '02', 'yy', null, sysdate, 'yy', sysdate);
delete from sys_datadict_list d where d.dict_code='IRS_CLEAN_TYPE';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('VALMODEL_ID', '01', '单曲贴现', 1, '0', 'yy', null, sysdate, 'yy', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('VALMODEL_ID', '02', '收付轧差', 2, '0', 'yy', null, sysdate, 'yy', sysdate, null);


delete from sys_datadict d where d.dict_code='RESET_SCOPE';
insert into sys_datadict (DICT_CODE, SYSTEM_CODE, DICT_CNNAME, DICT_ENNAME, UPDATE_FLG, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('RESET_SCOPE', 'VAL', '利率重置品种范围', null, '02', 'yy', null, sysdate, 'yy', sysdate);
delete from sys_datadict_list d where d.dict_code='RESET_SCOPE';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('RESET_SCOPE', 'F14', '利率互换', 1, '0', 'yy', null, sysdate, 'yy', sysdate, null);

/*****数据字典 end**************************************************************************/


/*****接口定义 start**************************************************************************/
delete from val_interf_define d where d.INTERF_ID in('100216','100217','100218','100219');
insert into val_interf_define (INTERF_ID, INTERF_C_NAME, INTERF_E_NAME, ORDERNO, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('100216', '利率互换标的信息', 'ValIrsInfo', 1202, 'yy', null, sysdate, 'yy', sysdate);
insert into val_interf_define (INTERF_ID, INTERF_C_NAME, INTERF_E_NAME, ORDERNO, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('100217', '利率互换付息计划', 'ValIrsCashPlan', 1202, 'yy', null, sysdate, 'yy', sysdate);
insert into val_interf_define (INTERF_ID, INTERF_C_NAME, INTERF_E_NAME, ORDERNO, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('100218', '利率互换交易信息', 'ValIrsDeal', 2102, 'yy', null, sysdate, 'yy', sysdate);
insert into val_interf_define (INTERF_ID, INTERF_C_NAME, INTERF_E_NAME, ORDERNO, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('100219', '利率互换存续期交易', 'ValIrsPerIodDeal', 2102, 'yy', null, sysdate, 'yy', sysdate);
/*****接口定义 start**************************************************************************/

/*****接口模板 start**************************************************************************/
delete from val_interf_template t where t.interf_id in('100216','100217','100218','100219');
insert into val_interf_template (TMP_C_NAME, TMP_E_NAME, DTO_NAME, INTERF_ID, TMP_TYPE, SYSTEMID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', 'ValIrsInfo', '100216', '01', 'ZG', 'yy', null, sysdate, 'yy', sysdate);
insert into val_interf_template (TMP_C_NAME, TMP_E_NAME, DTO_NAME, INTERF_ID, TMP_TYPE, SYSTEMID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换付息计划', 'ValIrsCashPlan', 'ValIrsCashPlan', '100217', '01', 'ZG', 'yy', null, sysdate, 'yy', sysdate);
insert into val_interf_template (TMP_C_NAME, TMP_E_NAME, DTO_NAME, INTERF_ID, TMP_TYPE, SYSTEMID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', 'ValIrsDeal', '100218', '01', 'ZG', 'yy', null, sysdate, 'yy', sysdate);
insert into val_interf_template (TMP_C_NAME, TMP_E_NAME, DTO_NAME, INTERF_ID, TMP_TYPE, SYSTEMID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换存续期交易', 'ValIrsPerIodDeal', 'ValIrsPerIodDeal', '100219', '01', 'ZG', 'yy', null, sysdate, 'yy', sysdate);
/*****接口模板 start**************************************************************************/

/*****公共数据导入 start**************************************************************************/
delete from bok_batch_task_grp b where b.task_grp_id='1202';
insert into bok_batch_task_grp (TASK_GRP_ID, TASK_GRP_NAME, TASK_GRP_TYPE, P_TASK_GRP_ID, TASK_ID, ORDER_NO, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CREATE_DEPT)
values ('1202', '利率互换', 'InterfData', null, '1202', 209, 'yy', sysdate, 'yy', sysdate, null);
delete from bok_batch_task_grp b where b.p_task_grp_id='1202';
insert into bok_batch_task_grp (TASK_GRP_ID, TASK_GRP_NAME, TASK_GRP_TYPE, P_TASK_GRP_ID, TASK_ID, ORDER_NO, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CREATE_DEPT)
values ('ValIrsInfo', '利率互换标的信息', 'InterfData', '1202', 'ValIrsInfo', 100216, 'yy', sysdate, 'yy', sysdate, null);
insert into bok_batch_task_grp (TASK_GRP_ID, TASK_GRP_NAME, TASK_GRP_TYPE, P_TASK_GRP_ID, TASK_ID, ORDER_NO, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CREATE_DEPT)
values ('ValIrsCashPlan', '利率互换付息计划', 'InterfData', '1202', 'ValIrsCashPlan', 100217, 'yy', sysdate, 'yy', sysdate, null);
/*****公共数据导入 end**************************************************************************/

/*****交易信息导入 start**************************************************************************/
delete from bok_batch_task_grp b where b.task_grp_id='2102';
insert into bok_batch_task_grp (TASK_GRP_ID, TASK_GRP_NAME, TASK_GRP_TYPE, P_TASK_GRP_ID, TASK_ID, ORDER_NO, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CREATE_DEPT)
values ('2102', '利率互换交易', 'InterfDeal', null, '2102', 100097, 'yy', sysdate, 'yy', sysdate, null);
delete from bok_batch_task_grp b where b.p_task_grp_id='2102';
insert into bok_batch_task_grp (TASK_GRP_ID, TASK_GRP_NAME, TASK_GRP_TYPE, P_TASK_GRP_ID, TASK_ID, ORDER_NO, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CREATE_DEPT)
values ('ValIrsDeal', '利率互换交易信息', 'InterfDeal', '2102', 'ValIrsInfo', 100218, 'yy', sysdate, 'yy', sysdate, null);
insert into bok_batch_task_grp (TASK_GRP_ID, TASK_GRP_NAME, TASK_GRP_TYPE, P_TASK_GRP_ID, TASK_ID, ORDER_NO, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CREATE_DEPT)
values ('ValIrsPerIodDeal', '利率互换存续期交易', 'InterfDeal', '2102', 'ValIrsCashPlan', 100219, 'yy', sysdate, 'yy', sysdate, null);
/*****交易信息导入 end**************************************************************************/



/*****接口任务 start**************************************************************************/
delete from val_interf_task t where t.TASK_ID in('59','60');
insert into val_interf_task (TASK_ID, BEAN_NAME, TASK_NAME, ORDERNO, STATUS, RS, SYSTEMID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('59', 'irsBaseInfoRunner', '利率互换-基础数据接口', 1202, 'E', 'R', 'ZG', 'yy', null, sysdate, 'yy', sysdate);
delete from val_interf_task t where t.TASK_ID='60';
insert into val_interf_task (TASK_ID, BEAN_NAME, TASK_NAME, ORDERNO, STATUS, RS, SYSTEMID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('60', 'irsDealRunner', '利率互换-交易数据接口', 2102, 'E', 'R', 'ZG', 'yy', null, sysdate, 'yy', sysdate);
/*****接口任务 start**************************************************************************/


/*****接口校验规则 start**************************************************************************/
--标的信息接口
delete from val_temp_col_check_info info where info.TMP_E_NAME in('ValIrsInfo','ValIrsCashPlan','ValIrsDeal','ValIrsPerIodDeal');
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '交易编号', 'tradeId', '1', 'Y', 32, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '数据动作标志', 'dataActionSign', '1', 'Y', 50, '1', 'ACTION_STATUS', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '币种', 'ccy', '1', 'Y', 50, '1', 'CCY', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '名义本金', 'prinAmt', '3', 'Y', 32, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '起息日', 'vdate', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '到期日', 'mdate', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '期限品种', 'termKind', '1', 'Y', 50, '1', 'TERM_TYPE_IRS', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '互换方式', 'irsType', '1', 'Y', 50, '1', 'IRS_TYPE', 'yy', null, sysdate, 'yy', sysdate);
--insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
--values ('利率互换标的信息', 'ValIrsInfo', '清算方式', 'irsCleanType', '1', 'Y', 50, '1', 'IRS_CLEAN_TYPE', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '本金收益特征', 'capiProfFeature', '1', 'Y', 50, '1', 'AMT_INCOME_TYPE', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '固定利率付息频率', 'fixPayCycle', '1', 'Y', 50, '1', 'FREQUENCY_UNIT', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '固定利率', 'fixRate', '3', 'Y', 50, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '固定利率计息基础', 'fixBasis', '1', 'Y', 50, '1', 'BASIS', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '固定利率首期起息日', 'fixVdate', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '浮动挂钩利率', 'linkRate', '1', 'Y', 50, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '利差', 'spreadRate', '3', 'Y', 50, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '浮动利率计息基础', 'floatBasis', '1', 'Y', 50, '1', 'BASIS', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '浮动利率付息频率', 'floatPayCycle', '1', 'Y', 50, '1', 'FREQUENCY_UNIT', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '浮动利率首期起息日', 'floatVdate', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '首期利率确定日', 'floatComfimDate', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '利率重置频率', 'resetCycle', '1', 'Y', 50, '1', 'FREQUENCY_UNIT', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '交易日历', 'calendarId', '1', 'Y', 50, '1', 'BATCH_QUARTZ_SKIP_HOLIDAY_MARKET_CAL_TYPE', 'yy', null, sysdate, 'yy', sysdate);
--insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
--values ('利率互换标的信息', 'ValIrsInfo', '存续期是否计息', 'isCal', '1', 'Y', 50, '1', 'YES_OR_NO', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换标的信息', 'ValIrsInfo', '重置类型', 'resetType', '1', 'Y', 50, '1', 'INTRESET_TYPE', 'yy', null, sysdate, 'yy', sysdate);



--付息计划规则
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换付息计划', 'ValIrsCashPlan', '交易编号', 'tradeId', '1', 'Y', 32, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换付息计划', 'ValIrsCashPlan', '数据动作标志', 'dataActionSign', '1', 'Y', 50, '1', 'ACTION_STATUS', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换付息计划', 'ValIrsCashPlan', '现金流序号', 'orderNo', '1', 'Y', 50, '1', 'CCY', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换付息计划', 'ValIrsCashPlan', '计息开始日', 'actVdate', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换付息计划', 'ValIrsCashPlan', '计息截止日', 'actMdate', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换付息计划', 'ValIrsCashPlan', '支付日', 'payDay', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换付息计划', 'ValIrsCashPlan', '交易类型', 'dealType', '1', 'Y', 50, '1', 'CASH_TYPE_2', 'yy', null, sysdate, 'yy', sysdate);

--利率互换交易接口规则
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '产品代码', 'prodId', '1', 'Y', 32, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '数据动作标志', 'dataActionSign', '1', 'Y', 50, '1', 'ACTION_STATUS', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '交易编号', 'tradeId', '1', 'Y', 32, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '交易日', 'tradeDate', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '交割日', 'settleDate', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '起息日', 'vdate', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '到期日', 'mdate', '2', 'Y', 10, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '交易方向', 'ps', '1', 'Y', 10, '1', 'PS', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '名义本金', 'prinAmt', '3', 'Y', 32, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '币种', 'ccy', '1', 'Y', 50, '1', 'CCY', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '交易对手', 'counterId', '1', 'Y', 32, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '交易手续费', 'dealFeeAmt', '3', 'Y', 32, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '交易手续费是否随交易支付', 'isDealFeeAmt', '1', 'Y', 32, '1', 'YES_OR_NO', 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '结算费', 'stlFeeAmt', '3', 'Y', 32, '1', null, 'yy', null, sysdate, 'yy', sysdate);
insert into val_temp_col_check_info (TMP_C_NAME, TMP_E_NAME, COL_C_NAME, COL_E_NAME, COL_TYPE, IS_NULL, MAX_LENGTH, IS_EXIST, DICT_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('利率互换交易信息', 'ValIrsDeal', '结算费是否随交易支付', 'isStlFeeAmt', '1', 'Y', 32, '1', 'YES_OR_NO', 'yy', null, sysdate, 'yy', sysdate);



/*****接口校验规则 end**************************************************************************/



/*****批处理增加利率互换批任务 start**************************************************************************/
--任务
delete from   sys_batch_task t where t.task_id in ('529','530','531');
insert into sys_batch_task (TASK_ID, SYSTEM_CODE, TASK_NAME, FTASK_ID, BEAN_NAME, TASK_PRI, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, IS_BATCH_START_TARGET)
values ('529', 'VAL', '利率互换交易-账务明细批处理', null, 'irsTransBookTask', 40, 'yy', null,sysdate, 'yy',sysdate, null);
insert into sys_batch_task (TASK_ID, SYSTEM_CODE, TASK_NAME, FTASK_ID, BEAN_NAME, TASK_PRI, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, IS_BATCH_START_TARGET)
values ('530', 'VAL', '利率互换收付轧差估值模型', null, 'irsCalBookTask', 60, 'yy', null, sysdate, 'yy', sysdate, null);
insert into sys_batch_task (TASK_ID, SYSTEM_CODE, TASK_NAME, FTASK_ID, BEAN_NAME, TASK_PRI, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, IS_BATCH_START_TARGET)
values ('531', 'VAL', '利率互换公允价值变动-账务明细批处理', null, 'irsFairValeAlterationsTask', 70, 'yy', null, sysdate, 'yy', sysdate, null);
--整体任务调度(利率重置、批移到估值中)
delete from sys_batch_task t where t.TASK_ID='10014';
insert into sys_batch_task (TASK_ID, SYSTEM_CODE, TASK_NAME, FTASK_ID, BEAN_NAME, TASK_PRI, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, IS_BATCH_START_TARGET)
values ('10014', 'VAL', '利率重置', null, 'resetRateTask', 8, 'system', null, sysdate, 'system', sysdate, null);

delete from  SYS_BATCH_JOB b where b.batch_seq_no='6f8803b3aa6fbaffe050007f01003cad' and b.task_id in('529','530','531','10014');
insert into SYS_BATCH_JOB (BATCH_SEQ_NO, TASK_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('6f8803b3aa6fbaffe050007f01003cad', '529', 'yy', null, sysdate, 'yy', sysdate);
insert into SYS_BATCH_JOB (BATCH_SEQ_NO, TASK_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('6f8803b3aa6fbaffe050007f01003cad', '530', 'yy', null, sysdate, 'yy', sysdate);
insert into SYS_BATCH_JOB (BATCH_SEQ_NO, TASK_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('6f8803b3aa6fbaffe050007f01003cad', '531', 'yy', null, sysdate, 'yy', sysdate);
insert into SYS_BATCH_JOB (BATCH_SEQ_NO, TASK_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('6f8803b3aa6fbaffe050007f01003cad', '10014', 'yy', null, sysdate, 'yy', sysdate);


--配置表
--业务类型为利率互换
delete from SYS_DATADICT_LIST d where d.DICT_CODE='BUSI_TYPE' and d.item_code='41';
delete from BOK_MATCH_RLAT bm where bm.match_type='01' and bm.source in ('529','530','531');
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('01', 'ALL', '529', '13', 'yy', null, sysdate, 'yy', sysdate);
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('01', 'ALL', '530', '13', 'yy', null, sysdate, 'yy', sysdate);
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('01', 'ALL', '531', '13', 'yy', null, sysdate, 'yy', sysdate);

--利率互换批处理分录配置
delete from BOK_MATCH_RLAT bm where bm.match_type='03' and bm.source in ('529','530','531');
--利率互换交易 涉及凭证
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('03', 'QY', '529', '1801', 'yy', null, sysdate, 'yy', sysdate);
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('03', 'QY', '529', '1805', 'yy', null, sysdate, 'yy', sysdate);
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('03', 'QY', '529', '1811', 'yy', null, sysdate, 'yy', sysdate);
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('03', 'QY', '529', '1812', 'yy', null, sysdate, 'yy', sysdate);
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('03', 'QY', '529', '1807', 'yy', null, sysdate, 'yy', sysdate);
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('03', 'QY', '529', '1808', 'yy', null, sysdate, 'yy', sysdate);
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('03', 'QY', '528', '1816', 'yy', null, sysdate, 'yy', sysdate);
--利率互换计提 涉及凭证
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('03', 'QY', '530', '1809', 'yy', null, sysdate, 'yy', sysdate);
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('03', 'QY', '530', '1810', 'yy', null, sysdate, 'yy', sysdate);
--利率互换公允价值变动 涉及凭证
insert into BOK_MATCH_RLAT (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('03', 'QY', '531', '1802', 'yy', null, sysdate, 'yy', sysdate);

--科目性质配置,利率互换计提科目
delete from bok_match_rlat m where m.match_type='02' and m.SOURCE='530';
insert into bok_match_rlat (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('02', 'QY', '530', 'YSLVHH', 'yy', 'SYSDEPT', sysdate, 'yy', sysdate);
insert into bok_match_rlat (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('02', 'QY', '530', 'YFLVHH', 'yy', 'SYSDEPT', sysdate, 'yy', sysdate);
--科目场景配置,利率互换计提成本场景
delete from  bok_match_rlat m where m.match_type='07' and m.SOURCE='530';
insert into bok_match_rlat (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('07', 'QY', '530', '1801', 'yy', null, sysdate, 'yy',sysdate);
insert into bok_match_rlat (MATCH_TYPE, ACCT_STANDARD, SOURCE, TARGET, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('07', 'QY', '530', '1808', 'yy', null, sysdate, 'yy', sysdate);
/*****批处理增加利率互换批任务 end**************************************************************************/


/*****利率互换计提方式参数：现只支持倒减法 start**************************************************************************/
delete from   bok_bookset_ext e where e.param_code='IRS_ACCR_TYPE';
insert into bok_bookset_ext (BOOKSET_ID, PARAM_CODE, PARAM_VALUE, PARAM_NAME, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, PARAM_TYPE)
values ('00000000000000000002', 'IRS_ACCR_TYPE', '01', '利率互换计提方式', '计提方式: 01：区间倒减，02：每日新增；每日新增，最后一日区间倒减', 'yy', null,sysdate, 'yy',sysdate, '01');
INSERT INTO BOK_BOOKSET_EXT
SELECT A.BOOKSET_ID,B.PARAM_CODE,B.PARAM_VALUE,B.PARAM_NAME,B.REMARK,
B.CREATE_USER,B.CREATE_DEPT,B.CREATE_TIME,B.UPDATE_USER,B.UPDATE_TIME,PARAM_TYPE
FROM BOK_BOOKSET A,BOK_BOOKSET_EXT B
WHERE A.BOOKSET_ID_TMPL = B.BOOKSET_ID
AND B.PARAM_CODE = 'IRS_ACCR_TYPE'
AND A.BOOKSET_ID !='00000000000000000002';

--利率互换浮动端利息计提 现只支持单利
delete from   bok_bookset_ext e where e.param_code='IRS_FLOAT_ACCR_TYPE';
insert into bok_bookset_ext (BOOKSET_ID, PARAM_CODE, PARAM_VALUE, PARAM_NAME, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, PARAM_TYPE)
values ('00000000000000000002', 'IRS_FLOAT_ACCR_TYPE', '01', '利率互换浮动端计提方式', '计提方式: 01：单利，02：复利', 'yy', null,sysdate, 'yy',sysdate, '01');
INSERT INTO BOK_BOOKSET_EXT
SELECT A.BOOKSET_ID,B.PARAM_CODE,B.PARAM_VALUE,B.PARAM_NAME,B.REMARK,
B.CREATE_USER,B.CREATE_DEPT,B.CREATE_TIME,B.UPDATE_USER,B.UPDATE_TIME,PARAM_TYPE
FROM BOK_BOOKSET A,BOK_BOOKSET_EXT B
WHERE A.BOOKSET_ID_TMPL = B.BOOKSET_ID
AND B.PARAM_CODE = 'IRS_FLOAT_ACCR_TYPE'
AND A.BOOKSET_ID !='00000000000000000002';
/*****利率互换计提方式参数：现只支持倒减法 end**************************************************************************/

/**利率互换的估值方案估值模型默认为模型法 start*************************************************************************/
update bok_VAL_SCHEME_DETAIL sd set sd.VALUATION_ID='01' where sd.sec_type='04';
--利率互换可以选择市值法
delete from SYS_DATADICT_LIST_LINK sdll where sdll.LINKED_DICT_CODE = 'SEC_TYPE'   and sdll.DICT_CODE = 'VALUATION_METHOD' and TRIM(sdll.LINKED_ITEM_CODE) = TRIM('04') and sdll.item_code='01';
insert into SYS_DATADICT_LIST_LINK (DICT_CODE, ITEM_CODE, LINKED_DICT_CODE, LINKED_ITEM_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('VALUATION_METHOD', '01', 'SEC_TYPE', '04', 'yy', null, sysdate, 'yy', sysdate);

delete from SYS_DATADICT_LIST_LINK sdll where sdll.LINKED_DICT_CODE = 'SEC_TYPE'   and sdll.DICT_CODE = 'VALUATION_PRICE' and TRIM(sdll.LINKED_ITEM_CODE) = TRIM('04') and sdll.item_code='SCH';
insert into SYS_DATADICT_LIST_LINK (DICT_CODE, ITEM_CODE, LINKED_DICT_CODE, LINKED_ITEM_CODE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('VALUATION_PRICE', 'SCH', 'SEC_TYPE', '04', 'yy', null, sysdate, 'yy', sysdate);
/**利率互换的估值方案估值模型默认为模型法 end***************************************************************************/
