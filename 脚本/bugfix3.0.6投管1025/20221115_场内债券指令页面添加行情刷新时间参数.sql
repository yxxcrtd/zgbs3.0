delete from SYS_PARA where PARA_CODE in ('etr_ex_sec_qut_ref_time','etr_ex_repo_qut_ref_time');

INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
VALUES ('ETR', 'etr_ex_sec_qut_ref_time', '600000', null, null, '场内债券指令页面债券行情刷新时间间隔(单位:毫秒)', 'system', null, sysdate, 'system', sysdate, null, '01');

INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
VALUES ('ETR', 'etr_ex_repo_qut_ref_time', '600000', null, null, '场内债券指令页面回购行情刷新时间间隔(单位:毫秒)', 'system', null, sysdate, 'system', sysdate, null, '01');