
--桂林合并代码 add by yangxin at 20220630 start 增加系统参数 是否自动生成赎回到账
delete from sys_para where PARA_CODE = 'is_auto_redemption';
insert into sys_para (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
values ('TRD', 'is_auto_redemption', 'N', null, null, '是否自动生成赎回到账', 'system', null, sysdate, 'system', sysdate, null, '01');
