comment on table BID_ORDER is '投标确认';

alter table BID_ORDER drop COLUMN OU_PARTY_ID;
alter table BID_ORDER add OU_PARTY_ID VARCHAR2(50);
comment on column BID_ORDER.OU_PARTY_ID is '本方';

alter table BID_ORDER drop COLUMN  OUR_TRADER;
alter table BID_ORDER add OUR_TRADER VARCHAR2(50);
comment on column BID_ORDER.OUR_TRADER is '本方交易员';

alter table BID_ORDER drop COLUMN  SUBSCRIPTION_AMOUNT;
alter table BID_ORDER add SUBSCRIPTION_AMOUNT NUMBER(30, 4);
comment on column BID_ORDER.SUBSCRIPTION_AMOUNT is '认购量';

alter table BID_ORDER drop COLUMN  PAYABLE_AMOUNT;
alter table BID_ORDER add PAYABLE_AMOUNT NUMBER(30, 4);
comment on column BID_ORDER.PAYABLE_AMOUNT is '应缴款金额';

alter table BID_ORDER drop COLUMN  SETT_FEE_AMT;
alter table BID_ORDER add SETT_FEE_AMT NUMBER(30, 4);
comment on column BID_ORDER.SETT_FEE_AMT is '结算服务费';

alter table BID_ORDER drop COLUMN  P_BID_ID;
alter table BID_ORDER add P_BID_ID VARCHAR2(32);
comment on column BID_ORDER.P_BID_ID is '前置投标编';

alter table BID_ORDER drop COLUMN  IS_CANCEL;
alter table BID_ORDER add IS_CANCEL VARCHAR2(50);
comment on column BID_ORDER.IS_CANCEL is '是否撤销';

alter table BID_ORDER drop COLUMN  TRADE_STATUS;
alter table BID_ORDER add TRADE_STATUS VARCHAR2(50);
comment on column BID_ORDER.TRADE_STATUS is '交易状态';

comment on table BID_ORDER_BID_PRICE is '投标确认明细';

alter table BID_ORDER_BID_PRICE drop COLUMN  SUBSCRIPTION_AMOUNT;
alter table BID_ORDER_BID_PRICE add SUBSCRIPTION_AMOUNT NUMBER(30, 4);
comment on column BID_ORDER_BID_PRICE.SUBSCRIPTION_AMOUNT is '认购量';

alter table BID_ORDER_BID_PRICE drop COLUMN PAYABLE_AMOUNT;
alter table BID_ORDER_BID_PRICE add PAYABLE_AMOUNT NUMBER(30, 4);
comment on column BID_ORDER_BID_PRICE.PAYABLE_AMOUNT is '应缴款金额';

alter table BID_ORDER_BID_PRICE drop COLUMN  SETT_FEE_AMT;
alter table BID_ORDER_BID_PRICE add SETT_FEE_AMT NUMBER(30, 4);
comment on column BID_ORDER_BID_PRICE.SETT_FEE_AMT is '结算服务费';

alter table BID_ORDER_BID_PRICE drop COLUMN  P_BID_ID;
alter table BID_ORDER_BID_PRICE add P_BID_ID VARCHAR2(32);
comment on column BID_ORDER_BID_PRICE.P_BID_ID is '前置投标编';

alter table BID_ORDER_BID_PRICE drop COLUMN  IS_CANCEL;
alter table BID_ORDER_BID_PRICE add IS_CANCEL VARCHAR2(50);
comment on column BID_ORDER_BID_PRICE.IS_CANCEL is '是否撤销';

alter table BID_ORDER_BID_PRICE drop COLUMN  TRADE_STATUS;
alter table BID_ORDER_BID_PRICE add TRADE_STATUS VARCHAR2(50);
comment on column BID_ORDER_BID_PRICE.TRADE_STATUS is '交易状态';

ALTER TABLE BID_ORDER_BID_PRICE DROP CONSTRAINT PK_BID_ORDER_BID_PRICE;
drop index PK_BID_ORDER_BID_PRICE;

alter table BID_ORDER_BID_PRICE drop COLUMN  PORTFOLIO_ID;
alter table BID_ORDER_BID_PRICE add PORTFOLIO_ID VARCHAR2(32);
comment on column BID_ORDER_BID_PRICE.PORTFOLIO_ID is '组合代码';

update BID_ORDER_BID_PRICE set PORTFOLIO_ID = 'INIT';
alter table BID_ORDER_BID_PRICE modify PORTFOLIO_ID not null;
alter table BID_ORDER_BID_PRICE add constraint PK_BID_ORDER_BID_PRICE primary key(BID_ID, ENTRUST_ID, BID_PRICE,PORTFOLIO_ID);

drop table WFL_BID_ORDER cascade constraints;

/*==============================================================*/
/* Table: WFL_BID_ORDER                                         */
/*==============================================================*/
create table WFL_BID_ORDER
(
   BID_ID               VARCHAR2(32)         not null,
   FINPROD_ID           VARCHAR2(50),
   BID_DATE             DATE,
   PAY_TYPE             VARCHAR2(50),
   PAY_DATE             DATE,
   IS_WIN               VARCHAR2(50),
   WIN_PRIN_AMT         NUMBER(30,2),
   WIN_PRICE            NUMBER(30,14),
   WIN_TYPE             VARCHAR2(50),
   REMARK               VARCHAR2(1000),
   CREATE_USER          VARCHAR2(20)         not null,
   CREATE_DEPT          VARCHAR2(32),
   CREATE_TIME          TIMESTAMP            not null,
   UPDATE_USER          VARCHAR2(20)         not null,
   UPDATE_TIME          TIMESTAMP            not null,
   OU_PARTY_ID          VARCHAR2(50),
   OUR_TRADER           VARCHAR2(50),
   SUBSCRIPTION_AMOUNT  NUMBER(30, 4),
   P_BID_ID             VARCHAR2(32),
   IS_CANCEL            VARCHAR2(50),
   TRADE_STATUS         VARCHAR2(50),
   WORKFLOW_ID          VARCHAR2(32),
   WORKFLOW_STATUS      VARCHAR2(10),
   constraint PK_WFL_BID_ORDER primary key (BID_ID)
);

comment on table WFL_BID_ORDER is
'流程中投标确认';

comment on column WFL_BID_ORDER.BID_ID is
'投标编号';

comment on column WFL_BID_ORDER.FINPROD_ID is
'金融产品代码';

comment on column WFL_BID_ORDER.BID_DATE is
'投标日期';

comment on column WFL_BID_ORDER.PAY_TYPE is
'缴款方式，一级分销、一级缴款、二级交易';

comment on column WFL_BID_ORDER.PAY_DATE is
'缴款日';

comment on column WFL_BID_ORDER.IS_WIN is
'是否中标';

comment on column WFL_BID_ORDER.WIN_PRIN_AMT is
'中标券面总额';

comment on column WFL_BID_ORDER.WIN_PRICE is
'中标标位';

comment on column WFL_BID_ORDER.WIN_TYPE is
'中标方式，单标位、多标位';

comment on column WFL_BID_ORDER.REMARK is
'备注';

comment on column WFL_BID_ORDER.CREATE_USER is
'创建人';

comment on column WFL_BID_ORDER.CREATE_DEPT is
'创建部门';

comment on column WFL_BID_ORDER.CREATE_TIME is
'创建时间';

comment on column WFL_BID_ORDER.UPDATE_USER is
'更新人';

comment on column WFL_BID_ORDER.UPDATE_TIME is
'更新时间';

comment on column WFL_BID_ORDER.OU_PARTY_ID is
'本方';

comment on column WFL_BID_ORDER.OUR_TRADER is
'本方交易员';

comment on column WFL_BID_ORDER.SUBSCRIPTION_AMOUNT is
'认购量';

comment on column WFL_BID_ORDER.P_BID_ID is
'前置投标编';

comment on column WFL_BID_ORDER.IS_CANCEL is
'是否撤销';

comment on column WFL_BID_ORDER.TRADE_STATUS is
'交易状态';

comment on column WFL_BID_ORDER.WORKFLOW_ID is
'流程id';

comment on column WFL_BID_ORDER.WORKFLOW_STATUS is
'数据状态';


drop table WFL_BID_ORDER_BID_PRICE cascade constraints;

/*==============================================================*/
/* Table: WFL_BID_ORDER_BID_PRICE                               */
/*==============================================================*/
create table WFL_BID_ORDER_BID_PRICE
(
   BID_ID               VARCHAR2(32)         not null,
   ENTRUST_ID           VARCHAR2(32)         not null,
   BID_PRICE            NUMBER(30,14)        not null,
   IS_WIN               VARCHAR2(50),
   WIN_PRICE            NUMBER(30,14),
   WIN_CPRICE           NUMBER(30,14),
   THE_WIN_PRIN_AMT     NUMBER(30,2),
   WIN_PRIN_AMT         NUMBER(30,2),
   TRADE_ID             VARCHAR2(32),
   CREATE_USER          VARCHAR2(20)         not null,
   CREATE_DEPT          VARCHAR2(32),
   CREATE_TIME          TIMESTAMP            not null,
   UPDATE_USER          VARCHAR2(20)         not null,
   UPDATE_TIME          TIMESTAMP            not null,
   IS_FROZEN_POSITION   VARCHAR2(2),
   OU_PARTY_ID          VARCHAR2(50),
   OUR_TRADER           VARCHAR2(50),
   SUBSCRIPTION_AMOUNT  NUMBER(30, 4),
   P_BID_ID             VARCHAR2(32),
   IS_CANCEL            VARCHAR2(50),
   TRADE_STATUS         VARCHAR2(50),
   WORKFLOW_ID          VARCHAR2(32),
   WORKFLOW_STATUS      VARCHAR2(10),
   PORTFOLIO_ID         VARCHAR2(32),
   constraint PK_WFL_BID_ORDER_BID_PRICE primary key (BID_ID, ENTRUST_ID, BID_PRICE,PORTFOLIO_ID)
);

comment on table WFL_BID_ORDER_BID_PRICE is
'流程中投标确认明细';

comment on column WFL_BID_ORDER_BID_PRICE.BID_ID is
'投标编号';

comment on column WFL_BID_ORDER_BID_PRICE.ENTRUST_ID is
'委托代码';

comment on column WFL_BID_ORDER_BID_PRICE.BID_PRICE is
'投标标位';

comment on column WFL_BID_ORDER_BID_PRICE.IS_WIN is
'是否中标';

comment on column WFL_BID_ORDER_BID_PRICE.WIN_PRICE is
'中标标位';

comment on column WFL_BID_ORDER_BID_PRICE.WIN_CPRICE is
'中标净价';

comment on column WFL_BID_ORDER_BID_PRICE.THE_WIN_PRIN_AMT is
'理论中标券面总额';

comment on column WFL_BID_ORDER_BID_PRICE.WIN_PRIN_AMT is
'实际中标券面总额';

comment on column WFL_BID_ORDER_BID_PRICE.TRADE_ID is
'交易编号';

comment on column WFL_BID_ORDER_BID_PRICE.CREATE_USER is
'创建人';

comment on column WFL_BID_ORDER_BID_PRICE.CREATE_DEPT is
'创建部门';

comment on column WFL_BID_ORDER_BID_PRICE.CREATE_TIME is
'创建时间';

comment on column WFL_BID_ORDER_BID_PRICE.UPDATE_USER is
'更新人';

comment on column WFL_BID_ORDER_BID_PRICE.UPDATE_TIME is
'更新时间';

comment on column WFL_BID_ORDER_BID_PRICE.IS_FROZEN_POSITION is
'是否头寸冻结';

comment on column WFL_BID_ORDER_BID_PRICE.OU_PARTY_ID is
'本方';

comment on column WFL_BID_ORDER_BID_PRICE.OUR_TRADER is
'本方交易员';

comment on column WFL_BID_ORDER_BID_PRICE.SUBSCRIPTION_AMOUNT is
'认购量';

comment on column WFL_BID_ORDER_BID_PRICE.P_BID_ID is
'前置投标编';

comment on column WFL_BID_ORDER_BID_PRICE.IS_CANCEL is
'是否撤销';

comment on column WFL_BID_ORDER_BID_PRICE.TRADE_STATUS is
'交易状态';

comment on column WFL_BID_ORDER_BID_PRICE.WORKFLOW_ID is
'流程id';

comment on column WFL_BID_ORDER_BID_PRICE.WORKFLOW_STATUS is
'数据状态';

comment on column WFL_BID_ORDER_BID_PRICE.PORTFOLIO_ID is
'组合代码';
