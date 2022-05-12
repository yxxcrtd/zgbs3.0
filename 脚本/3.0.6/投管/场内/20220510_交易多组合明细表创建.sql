declare
    num number;
begin
    select count(1) into num from user_tables where table_name = upper('T_PORTFOLIO_DETAIL');
    if num > 0 then
        execute immediate 'drop table T_PORTFOLIO_DETAIL' ;
    end if;
end;
/
create table T_PORTFOLIO_DETAIL
(
    ID             VARCHAR2(48) not null
        primary key,
    BUSI_TYPE      varchar2(8)  not null,
    BUSI_ID        varchar2(32) not null,
    BUSI_STATUS    varchar2(8)  not null,
    INSTRUCTION_ID varchar2(64) not null,
    PORTFOLIO_ID   varchar2(64) not null,
    PORTFOLIO_NAME varchar2(64) not null,
    TARGET_AMT     NUMBER(30, 2) default 0,
    TARGET_NUM     NUMBER(30, 2) default 0,
    TARGET_TYPE    varchar2(8)  not null
)
/

comment on table T_PORTFOLIO_DETAIL is '交易多组合明细表'
/
comment on column T_PORTFOLIO_DETAIL.BUSI_TYPE is '业务类型：1 下单；2 撤单；3 成交'
/
comment on column T_PORTFOLIO_DETAIL.BUSI_ID is '业务ID'
/
comment on column T_PORTFOLIO_DETAIL.BUSI_STATUS is '业务状态：1 初始；2 确认'
/
comment on column T_PORTFOLIO_DETAIL.INSTRUCTION_ID is '指令ID'
/
comment on column T_PORTFOLIO_DETAIL.PORTFOLIO_ID is '组合ID'
/
comment on column T_PORTFOLIO_DETAIL.PORTFOLIO_NAME is '组合名称'
/
comment on column T_PORTFOLIO_DETAIL.TARGET_AMT is '目标金额'
/
comment on column T_PORTFOLIO_DETAIL.TARGET_NUM is '目标数量'
/
comment on column T_PORTFOLIO_DETAIL.TARGET_TYPE is '目标类型：01 数量；02 金额'
/

/*交易多组合明细表序列*/
drop sequence T_PORTFOLIO_DETAIL_ID_SEQ;
create sequence T_PORTFOLIO_DETAIL_ID_SEQ
    minvalue 1
    maxvalue 9999999999999999999999999999
    start with 1
    increment by 1
    nocache;
/