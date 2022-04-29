DROP TABLE MST_STOCK_LIQUID_DISCOUNT_INFO;
-- Create table
create table MST_STOCK_LIQUID_DISCOUNT_INFO
(
  SHARES_ID          	VARCHAR2(50) not null,
  VAL_DATE           	DATE not null,
  EXP_CIRC_DATE       	DATE not null,
  TERMS         		NUMBER(30,2),
  LAST_TRADE_DATE		DATE,
  REFER_OBJECT			VARCHAR2(50),
  EXP_VOLATILITY     	NUMBER(30,14),
  EXP_YIELD         	NUMBER(30,14),
  OBJ_FAIR_VALUE      	NUMBER(30,14),
  LIQUID_DISC_FACTOR    NUMBER(30,14),
  LIMIT_VALUE        	NUMBER(30,14),
  DATA_SOURCE        	VARCHAR2(50),
  CREATE_USER       	VARCHAR2(20) not null,
  CREATE_DEPT       	VARCHAR2(32),
  CREATE_TIME       	TIMESTAMP(6) not null,
  UPDATE_USER       	VARCHAR2(20) not null,
  UPDATE_TIME       	TIMESTAMP(6) not null
);
-- Add comments to the table 
comment on table MST_STOCK_LIQUID_DISCOUNT_INFO
  is '股票流动性折扣信息表';
-- Add comments to the columns 
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.SHARES_ID
  is '股票代码【市场代码】'; 
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.VAL_DATE
  is '行情日期'; 
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.EXP_CIRC_DATE
  is '预计上市流通日'; 
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.TERMS
  is '剩余年限'; 
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.LAST_TRADE_DATE
  is '标的股票最后交易日'; 
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.REFER_OBJECT
  is '参照标的'; 
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.EXP_VOLATILITY
  is '预期波动率%'; 
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.EXP_YIELD
  is '预期股利收益率%'; 
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.OBJ_FAIR_VALUE
  is '标的股票公允价值'; 
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.LIQUID_DISC_FACTOR
  is '流动性折扣系数%';
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.LIMIT_VALUE
  is '限售股估值（元）';
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.DATA_SOURCE
  is '数据来源';
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.CREATE_USER
  is '创建人';
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.CREATE_DEPT
  is '创建部门';
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.CREATE_TIME
  is '创建时间';
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.UPDATE_USER
  is '更新人';
comment on column MST_STOCK_LIQUID_DISCOUNT_INFO.UPDATE_TIME
  is '更新时间';
-- Create/Recreate primary, unique and foreign key constraints 
alter table MST_STOCK_LIQUID_DISCOUNT_INFO add constraint PK_LIQUID_DISCOUNT_INFO primary key (SHARES_ID, VAL_DATE, EXP_CIRC_DATE);

DROP TABLE MST_INDICATOR_QUOTATION;
-- Create table
create table MST_INDICATOR_QUOTATION 
(
  indicator_code      VARCHAR2(32) not null,
  deal_date           DATE not null,
  ccy                 VARCHAR2(50),
  pre_close_price     NUMBER(30,14),
  open_price          NUMBER(30,14),
  high_price          NUMBER(30,14),
  low_price           NUMBER(30,14),
  close_price         NUMBER(30,14),
  data_source         VARCHAR2(50),
  create_user         VARCHAR2(20) not null,
  create_dept         VARCHAR2(32),
  create_time         TIMESTAMP(6) not null,
  update_user         VARCHAR2(20) not null,
  update_time         TIMESTAMP(6) not null
  
);
-- Add comments to the table 
comment on table MST_INDICATOR_QUOTATION
  is '指数行情';
-- Add comments to the columns 
comment on column MST_INDICATOR_QUOTATION.indicator_code
  is '股票代码，市场代码';
comment on column MST_INDICATOR_QUOTATION.deal_date
  is '交易日';
comment on column MST_INDICATOR_QUOTATION.ccy
  is '币种';
comment on column MST_INDICATOR_QUOTATION.pre_close_price
  is '前收盘价';
comment on column MST_INDICATOR_QUOTATION.open_price
  is '开盘价';
comment on column MST_INDICATOR_QUOTATION.high_price
  is '最高价';
comment on column MST_INDICATOR_QUOTATION.low_price
  is '最低价';
comment on column MST_INDICATOR_QUOTATION.close_price
  is '收盘价';
comment on column MST_INDICATOR_QUOTATION.data_source
  is '数据来源';
comment on column MST_INDICATOR_QUOTATION.create_user
  is '创建人';
comment on column MST_INDICATOR_QUOTATION.create_dept
  is '创建部门';
comment on column MST_INDICATOR_QUOTATION.create_time
  is '创建时间';
comment on column MST_INDICATOR_QUOTATION.update_user
  is '更新人';
comment on column MST_INDICATOR_QUOTATION.update_time
  is '更新时间';

-- 增加主键
alter table MST_INDICATOR_QUOTATION add constraint PK_MST_INDICATOR_QUOTATION primary key (indicator_code, deal_date, ccy);
