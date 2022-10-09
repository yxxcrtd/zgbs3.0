-- 银行间系统参数表 
drop table BANK_SYS_PARA;
create table BANK_SYS_PARA
(
    PARA_CODE        VARCHAR2(50)               not null
        constraint PK_BANK_SYS_PARA
            primary key,
    PARAM_VALUE      VARCHAR2(2000)             not null,
    PARA_NAME        VARCHAR2(2000)             not null,
    DATA_TYPE        VARCHAR2(50),
    DATA_SCOPE       VARCHAR2(100),
    REMARK           VARCHAR2(600),
    IS_HAVE_SUB      VARCHAR2(50) default 'N'  not null,
    CREATE_USER      VARCHAR2(20)               not null,
    CREATE_DEPT      VARCHAR2(32),
    CREATE_TIME      TIMESTAMP(6)               not null,
    UPDATE_USER      VARCHAR2(20)               not null,
    UPDATE_TIME      TIMESTAMP(6)               not null
);
comment on table BANK_SYS_PARA is '银行间系统参数表';
comment on column BANK_SYS_PARA.PARA_CODE is '参数代码,唯一值';
comment on column BANK_SYS_PARA.PARA_NAME is '参数名称';
comment on column BANK_SYS_PARA.PARAM_VALUE is '参数值';
comment on column BANK_SYS_PARA.DATA_TYPE is '数据类型(01:文字列,02:下拉框,03:日期)';
comment on column BANK_SYS_PARA.DATA_SCOPE is '选择范围';
comment on column BANK_SYS_PARA.REMARK is '备注';
comment on column BANK_SYS_PARA.IS_HAVE_SUB is '是否存在子表';
comment on column BANK_SYS_PARA.CREATE_USER is '创建人';
comment on column BANK_SYS_PARA.CREATE_DEPT is '创建部门';
comment on column BANK_SYS_PARA.CREATE_TIME is '创建时间';
comment on column BANK_SYS_PARA.UPDATE_USER is '更新人';
comment on column BANK_SYS_PARA.UPDATE_TIME is '更新时间';

-- 银行间系统参数子表 
drop table BANK_SYS_PARA_SUB;
create table BANK_SYS_PARA_SUB
(
    PARA_CODE        VARCHAR2(50)               not null,
    SUB_PARA_CODE    VARCHAR2(50)               not null,
    SUB_PARA_NAME    VARCHAR2(50)               not null,
    PARAM_VALUE1     VARCHAR2(2000)             not null,
    PARAM_VALUE2     VARCHAR2(2000)             not null,
    PARAM_VALUE3     VARCHAR2(2000)             not null,
    PARAM_VALUE4     VARCHAR2(2000)             not null,
    DATA_TYPE        VARCHAR2(50),
    DATA_SCOPE       VARCHAR2(120),
    REMARK           VARCHAR2(600),
    CREATE_USER      VARCHAR2(20)               not null,
    CREATE_DEPT      VARCHAR2(32),
    CREATE_TIME      TIMESTAMP(6)               not null,
    UPDATE_USER      VARCHAR2(20)               not null,
    UPDATE_TIME      TIMESTAMP(6)               not null
);
create unique index UI_BANK_SYS_PARA_SUB
    on BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE);
comment on table BANK_SYS_PARA_SUB is '银行间系统参数表';
comment on column BANK_SYS_PARA_SUB.PARA_CODE is '参数代码,对应主表';
comment on column BANK_SYS_PARA_SUB.SUB_PARA_CODE is '参数代码';
comment on column BANK_SYS_PARA_SUB.SUB_PARA_NAME is '参数名称';
comment on column BANK_SYS_PARA_SUB.PARAM_VALUE1 is '参数值1';
comment on column BANK_SYS_PARA_SUB.PARAM_VALUE2 is '参数值2';
comment on column BANK_SYS_PARA_SUB.PARAM_VALUE3 is '参数值3';
comment on column BANK_SYS_PARA_SUB.PARAM_VALUE4 is '参数值4';
comment on column BANK_SYS_PARA_SUB.DATA_TYPE is '数据类型(01:文字列,02:下拉框,03:日期)';
comment on column BANK_SYS_PARA_SUB.DATA_SCOPE is '选择范围';
comment on column BANK_SYS_PARA_SUB.REMARK is '备注';
comment on column BANK_SYS_PARA_SUB.CREATE_USER is '创建人';
comment on column BANK_SYS_PARA_SUB.CREATE_DEPT is '创建部门';
comment on column BANK_SYS_PARA_SUB.CREATE_TIME is '创建时间';
comment on column BANK_SYS_PARA_SUB.UPDATE_USER is '更新人';
comment on column BANK_SYS_PARA_SUB.UPDATE_TIME is '更新时间';

