--系统参数增加
DELETE FROM SYS_PARA WHERE PARA_CODE='DETAIL_ASPECT_SWITCH';
insert into SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
values ('VAL', 'DETAIL_ASPECT_SWITCH', '02', '01', '01,02', '详情日志切面开关：01 关闭；02 开启', 'system', null, sysdate, 'system', sysdate, null, '01');

delete from sys_para s where s.PARA_CODE in('BATCHLOG_HOST','BATCHLOG_PORT','BATCHLOG_USERNAME','BATCHLOG_PASSWORD');
insert into sys_para (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
values ('VAL', 'BATCHLOG_HOST', '192.168.10.225', '01', '', '批处理日志-服务器IP', 'system', null, sysdate, 'system', sysdate, null, '01');

insert into sys_para (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
values ('VAL', 'BATCHLOG_PORT', '21', '01', '', '批处理日志-服务器端口', 'system', null, sysdate, 'system', sysdate, null, '01');

insert into sys_para (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
values ('VAL', 'BATCHLOG_USERNAME', 'logftp', '01', '', '批处理日志-服务器登录用户名', 'system', null,sysdate, 'system', sysdate, null, '01');

insert into sys_para (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
values ('VAL', 'BATCHLOG_PASSWORD', '123456', '01', '', '批处理日志-服务器登录密码', 'system', null, sysdate, 'system', sysdate, null, '01');


