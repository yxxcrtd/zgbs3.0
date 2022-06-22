-- 设置授权为硬控
update SYS_PARA set PARAM_VALUE='false' where PARA_CODE='IS_AUTHORIZATION_SOFTCONTROL';

update SYS_PARA set REMARK='是否启用指令授权(01:是,02:否)' where PARA_CODE='etrlimit_switch';
update SYS_PARA set REMARK='是否启用交易授权(01:是,02:否)',PARAM_VALUE='02' where PARA_CODE='AUTHORIZE_LIMIT';
