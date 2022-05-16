----新增 下单成交组合明细表：INF_CONTRACT_PORTFOLIO_DETAIL
-- Create table
--/
declare
    num number;
begin
    select count(1) into num from user_tables where table_name = upper('INF_CONTRACT_PORTFOLIO_DETAIL');
    if num > 0 then
        execute immediate 'drop table INF_CONTRACT_PORTFOLIO_DETAIL';
    end if;
end;
/
create table INF_CONTRACT_PORTFOLIO_DETAIL
(
  seqno             VARCHAR2(32) not null,
  portfolio_id      VARCHAR2(32) not null,
  portfolio_name    VARCHAR2(200),
  share_amt         NUMBER(30,4),
  trade_amt         NUMBER(30,2),
  entrust_num       NUMBER(30,4),
  entrust_amt       NUMBER(30,2),
  target_type       VARCHAR2(50),
  create_user       VARCHAR2(20) not null,
  create_dept       VARCHAR2(32),
  create_time       TIMESTAMP(6) not null,
  update_user       VARCHAR2(20) not null,
  update_time       TIMESTAMP(6) not null,
  constraint PK_PORTFOLIO_SEQNO_PORTFOLIO primary key (seqno,portfolio_id)
);
-- Add comments to the table 
comment on table INF_CONTRACT_PORTFOLIO_DETAIL is '下单成交组合明细表';
-- Add comments to the columns 
comment on column INF_CONTRACT_PORTFOLIO_DETAIL.seqno is '流水号';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.portfolio_id is '组合代码';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.portfolio_name is '组合名称';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.share_amt is '成交数量';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.trade_amt is '成交金额';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.entrust_num is '委托数量';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.entrust_amt is '委托金额';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.target_type is '目标类型';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.create_user is '创建人';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.create_dept is '创建部门';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.create_time is '创建时间';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.update_user is '更新人';

comment on column INF_CONTRACT_PORTFOLIO_DETAIL.update_time is '更新时间';
  