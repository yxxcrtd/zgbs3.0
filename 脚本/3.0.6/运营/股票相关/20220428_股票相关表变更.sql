drop table TRD_TRADE_NEW_STOCK_LOCKED;
-- Create table
create table TRD_TRADE_NEW_STOCK_LOCKED
(
  trade_id            	VARCHAR2(50) not null,
  bookset_id            VARCHAR2(50) not null,
  finprod_id            VARCHAR2(50) not null,
  ccy                   VARCHAR2(50),
  share_amt         	NUMBER(30,2),
  unit_cprice          	NUMBER(30,14),
  start_date        	DATE,
  end_date              DATE,
  total_days       		NUMBER(30,2),
  exp_date              DATE,
  m_trade_id          	VARCHAR2(50),
  gen_type              VARCHAR2(50),
  create_user           VARCHAR2(20) not null,
  create_dept           VARCHAR2(32),
  create_time           TIMESTAMP(6) not null,
  update_user           VARCHAR2(20) not null,
  update_time           TIMESTAMP(6) not null
);
-- Add comments to the table 
comment on table TRD_TRADE_NEW_STOCK_LOCKED
  is '新股锁定业务表';
-- Add comments to the columns 
comment on column TRD_TRADE_NEW_STOCK_LOCKED.TRADE_ID
  is '交易编号';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.bookset_id
  is '账套代码';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.finprod_id
  is '金融产品代码';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.ccy
  is '币种';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.share_amt
  is '锁定数量';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.unit_cprice
  is '单位成本';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.start_date
  is '锁定开始日期';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.end_date
  is '锁定结束日期';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.total_days
  is '锁定天数';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.exp_date
  is '预计上市流通日';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.m_trade_id
  is '内部关联交易编号';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.gen_type
  is '数据来源';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.create_user
  is '创建人';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.create_dept
  is '创建部门';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.create_time
  is '创建时间';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.update_user
  is '更新人';
comment on column TRD_TRADE_NEW_STOCK_LOCKED.update_time
  is '更新时间';
-- Create/Recreate primary, unique and foreign key constraints 
alter table TRD_TRADE_NEW_STOCK_LOCKED add constraint PK_TRD_TRADE_NEW_STOCK_LOCKED primary key (TRADE_ID);


----TRD_PRODUCT_DEAL 表增加字段：发行方式（暂时股票用）
ALTER TABLE TRD_PRODUCT_DEAL ADD  ISSUE_TYPE VARCHAR(50);
comment on column TRD_PRODUCT_DEAL.ISSUE_TYPE is '发行方式(暂时股票用)';

----BOK_BOOKDETAIL_CURRENT 表增加字段：发行方式（暂时股票用）
ALTER TABLE BOK_BOOKDETAIL_CURRENT ADD  ISSUE_TYPE VARCHAR(50);
comment on column BOK_BOOKDETAIL_CURRENT.ISSUE_TYPE is '发行方式(暂时股票用)';