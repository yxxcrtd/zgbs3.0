------前台树形结构
DELETE FROM BOK_BATCH_TASK_GRP WHERE task_grp_id IN ('ValStockLiquidDiscountInfo','ValStockIndicatorQuotationInfo');
insert into BOK_BATCH_TASK_GRP  (task_grp_id, task_grp_name, task_grp_type, p_task_grp_id, task_id, order_no, create_user, create_time, update_user, update_time, create_dept) values ('ValStockLiquidDiscountInfo','股票流动性折扣信息','InterfData','3051','ValStockLiquidDiscountInfo',100304,'admin',to_date('2018-09-17','yyyy-MM-dd'),'admin',to_date('2018-09-17','yyyy-MM-dd'),'');
insert into BOK_BATCH_TASK_GRP  (task_grp_id, task_grp_name, task_grp_type, p_task_grp_id, task_id, order_no, create_user, create_time, update_user, update_time, create_dept) values ('ValStockIndicatorQuotationInfo','股票指数行情信息','InterfData','3051','ValStockIndicatorQuotationInfo',100305,'admin',to_date('2018-09-17','yyyy-MM-dd'),'admin',to_date('2018-09-17','yyyy-MM-dd'),'');

------接口定义
DELETE FROM VAL_INTERF_DEFINE WHERE INTERF_ID IN ('100301','100302','100303');
insert into VAL_INTERF_DEFINE values ('100304','股票流动性折扣信息','ValStockLiquidDiscountInfo','3051','system','',sysdate,'system',sysdate);
insert into VAL_INTERF_DEFINE values ('100305','股票指数行情信息','ValStockIndicatorQuotationInfo','3051','system','',sysdate,'system',sysdate);

------接口模板名称
DELETE FROM VAL_INTERF_TEMPLATE WHERE TMP_E_NAME IN ('ValStockLiquidDiscountInfo','ValStockIndicatorQuotationInfo');
insert into VAL_INTERF_TEMPLATE values ('股票流动性折扣信息','ValStockLiquidDiscountInfo','ValStockLiquidDiscountInfo','100219','01','ZX','system','',sysdate,'system',sysdate);
insert into VAL_INTERF_TEMPLATE values ('股票指数行情信息','ValStockIndicatorQuotationInfo','ValStockIndicatorQuotationInfo','100220','01','ZX','system','',sysdate,'system',sysdate);

------模板字段校验信息配置表 
--股票存续期交易
delete from VAL_TEMP_COL_CHECK_INFO where TMP_E_NAME='ValStockLiquidDiscountInfo';
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','股票代码','sharesId','1','Y','32','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','估值日期','valDate','2','Y','10','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','预计上市流通日','expCircDate','2','Y','10','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','剩余年限','terms','3','N','30','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','标的股票最后交易日','lastTradeDate','2','N','10','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','参照标的','referObject','1','N','50','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','预期波动率%','expVolatility','3','N','30','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','预期股利收益率%','expYield','3','N','30','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','标的股票公允价值','objFairValue','3','N','30','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','流动性折扣系数%','liquidDiscFactor','3','Y','30','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','限售股估值（元）','limitValue','3','N','30','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票流通性折扣行情信息','ValStockLiquidDiscountInfo','行情来源','dataSource','1','N','50','','1');

--股票流通性折扣行情信息
delete from VAL_TEMP_COL_CHECK_INFO where TMP_E_NAME='ValStockLiquidDiscountInfo';
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票指数行情信息','ValStockIndicatorQuotationInfo','指数代码','indicator_code','1','Y','32','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票指数行情信息','ValStockIndicatorQuotationInfo','交易日','dealDate','2','Y','10','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票指数行情信息','ValStockIndicatorQuotationInfo','币种','ccy','1','Y','50','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票指数行情信息','ValStockIndicatorQuotationInfo','前收盘价','preClosePrice','3','N','30','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票指数行情信息','ValStockIndicatorQuotationInfo','开盘价','openPrice','3','N','30','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票指数行情信息','ValStockIndicatorQuotationInfo','最高价','highPrice','3','N','30','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票指数行情信息','ValStockIndicatorQuotationInfo','最低价','lowPrice','3','N','30','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票指数行情信息','ValStockIndicatorQuotationInfo','收盘价','closePrice','3','Y','30','','1');
insert into VAL_TEMP_COL_CHECK_INFO (TMP_C_NAME,TMP_E_NAME,COL_C_NAME,COL_E_NAME,COL_TYPE,IS_NULL,MAX_LENGTH,DICT_CODE,IS_EXIST) values ('股票指数行情信息','ValStockIndicatorQuotationInfo','行情来源','dataSource','3','N','30','','1');
