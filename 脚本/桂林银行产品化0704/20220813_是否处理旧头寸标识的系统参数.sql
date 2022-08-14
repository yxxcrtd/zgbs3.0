delete from sys_para where para_code='IS_HANDLE_OLDPOS_FLAG';
insert into sys_para (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
values ('COM', 'IS_HANDLE_OLDPOS_FLAG', 'Y', '01', null, '是否处理旧头寸标识(Y:是，N:否)', 'system', null, sysdate, 'system', sysdate, null, '01');
