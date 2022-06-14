delete from SYS_PARA where PARA_CODE in ('cfetsTrdFirstBidFirm_etrTable_refresh_period','cfetsTrdFirstBidFirm_tender_refresh_period',
                                            'cfetsTrdFirstBidFirm_distribution_refresh_period','cfetsTrdFirstBidFirm_details_refresh_period',
                                            'cfetsTrdBondFirstSub_etrTable_refresh_period','cfetsTrdBondFirstSub_daysub_refresh_period',
                                            'cfetsTrdBondFirstSub_daysubdetails_refresh_period','cfetsTrdBondFirstSub_details_refresh_period');

INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG) 
VALUES ('INV', 'cfetsTrdFirstBidFirm_etrTable_refresh_period', '120000', '01', null, '一级投标确认指令列表画面自动刷新时间(ms)', 'system', null,sysdate, 'system',sysdate, null, '01');

INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG) 
VALUES ('INV', 'cfetsTrdFirstBidFirm_tender_refresh_period', '120000', '01', null, '一级投标确认投标确认列表画面自动刷新时间(ms)', 'system', null,sysdate, 'system',sysdate, null, '01');

INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG) 
VALUES ('INV', 'cfetsTrdFirstBidFirm_distribution_refresh_period', '120000', '01', null, '一级投标确认分销买入列表画面自动刷新时间(ms)', 'system', null,sysdate, 'system',sysdate, null, '01');

INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG) 
VALUES ('INV', 'cfetsTrdFirstBidFirm_details_refresh_period', '120000', '01', null, '一级投标确认投标执行明细列表画面自动刷新时间(ms)', 'system', null,sysdate, 'system',sysdate, null, '01');

INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG) 
VALUES ('INV', 'cfetsTrdBondFirstSub_etrTable_refresh_period', '120000', '01', null, '一级申购指令列表画面自动刷新时间(ms)', 'system', null,sysdate, 'system',sysdate, null, '01');

INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG) 
VALUES ('INV', 'cfetsTrdBondFirstSub_daysub_refresh_period', '120000', '01', null, '一级申购当日申购列表画面自动刷新时间(ms)', 'system', null,sysdate, 'system',sysdate, null, '01');

INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG) 
VALUES ('INV', 'cfetsTrdBondFirstSub_daysubdetails_refresh_period', '120000', '01', null, '一级申购当日申购明细列表画面自动刷新时间(ms)', 'system', null,sysdate, 'system',sysdate, null, '01');

INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG) 
VALUES ('INV', 'cfetsTrdBondFirstSub_details_refresh_period', '120000', '01', null, '一级申购投标执行明细列表画面自动刷新时间(ms)', 'system', null,sysdate, 'system',sysdate, null, '01');



