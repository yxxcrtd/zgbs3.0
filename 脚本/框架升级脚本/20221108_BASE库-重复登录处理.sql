--重复登录处理模式参数设置
DELETE FROM SYS_PARA where PARA_CODE = 'duplicate_logon_type';
insert into SYS_PARA(SYSTEM_CODE,PARA_CODE,PARAM_VALUE,REMARK,CREATE_USER,CREATE_TIME,UPDATE_USER,UPDATE_TIME) values ('SYS','duplicate_logon_type','02','重复登录处理模式，仅在不允许重复登录时有效（01:不提示，直接踢出已登录的用户，02：提示用户是否继续登录，03：直接报错，不允许登录）','system',sysdate, 'system',sysdate);

--在用户信息表中记录已经登录的用户SESSION
ALTER TABLE SYS_USER ADD SESSION_ID VARCHAR2(50);
COMMENT ON COLUMN SYS_USER.SESSION_ID IS '在不允许重复登录的时候会将用户的session id记录到此字段';