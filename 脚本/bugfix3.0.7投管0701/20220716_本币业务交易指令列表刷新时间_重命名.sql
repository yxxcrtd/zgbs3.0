DELETE FROM SYS_PARA WHERE PARA_CODE  = 'ibuQuoteBond_etrTable_refresh_period';
INSERT INTO SYS_PARA (SYSTEM_CODE ,PARA_CODE ,PARAM_VALUE ,DATA_TYPE ,DATA_SCOPE ,REMARK ,CREATE_USER ,CREATE_DEPT ,CREATE_TIME ,UPDATE_USER ,UPDATE_TIME ,INSTITUTION_CODE ,UPDATE_FLG )
VALUES ('SYS','ibuQuoteBond_etrTable_refresh_period','120000','01','','本币业务交易指令列表刷新时间','system','',sysdate,'system',sysdate,'','01');