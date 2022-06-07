
delete from sys_para where system_code = 'ETR' and para_code = 'PRICE_SCOPE_CONTROLL_ETR';
insert into sys_para(SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG)
values ('ETR','PRICE_SCOPE_CONTROLL_ETR','01','02','01,02,03'
,'超过有效价格申报范围控制参数-指令:01禁止02仅提示03允许','system',null,sysdate,'system',sysdate,null,'01');