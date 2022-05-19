-- 金融产品临时交易汇总附属表
create table TRD_TEMP_DEAL_TOTAL_ADD(
    TOTLE_TRADE_ID         VARCHAR2(32) not null constraint PK_TRD_TEMP_DEAL_TOTAL_ADD primary key,
    ENTRUST_ID             VARCHAR2(32),
    OUR_SHARE_AMT          NUMBER(30, 4),
    LENDIN_SHARE_AMT       NUMBER(30, 4),
    OUR_CPRICE_AMT         NUMBER(30, 2),
    LENDIN_CPRICE_AMT      NUMBER(30, 2),
    OUR_FPRICE_AMT         NUMBER(30, 2),
    LENDIN_FPRICE_AMT      NUMBER(30, 2),
    OUR_FPRICE_AMT2        NUMBER(30, 2),
    LENDIN_FPRICE_AMT2     NUMBER(30, 2),
    PURCH_MODLE            VARCHAR2(50),
    PURCH_TYPE             VARCHAR2(50),
    PURCH_DATE             DATE,
    PUR_PAY_DATE           DATE,
    PUR_CFM_DATE           DATE,
    PUR_AMT                NUMBER(30, 4),
    DEPOSIT_AMT            NUMBER(30, 4),
    FIRST_UNIT_CPRICE3     NUMBER(30, 14),
    EXP_CON_VALUE          NUMBER(30, 2),
    PREB            	   NUMBER(30, 14),
    PREC                   NUMBER(30, 14),
    SPO_SHARE_NUM          NUMBER(30, 4),
    DEVIATION              NUMBER(30, 2),
    CONVERSION_RAT         NUMBER(30, 14),
    BCON_UNIT_CPRICE2      NUMBER(30, 14),
    CONVERSION_TYPE        VARCHAR2(50),
    ASSET_RECOMMAND_ORG    VARCHAR2(32),
    TERMINATE_RATE         NUMBER(30, 14),
    MP_FINPROD_ID          VARCHAR2(32),
    MP_BRANCH              NUMBER(10),
    IS_CASHSET             VARCHAR2(50),
    GEN_TYPE               VARCHAR2(50),
    CREATE_USER            VARCHAR2(20) not null,
    CREATE_DEPT            VARCHAR2(32),
    CREATE_TIME            TIMESTAMP(6) not null,
    UPDATE_USER            VARCHAR2(20) not null,
    UPDATE_TIME            TIMESTAMP(6) not null,
    PENALTY_INTAMT         NUMBER(30, 4),
    INT_SETT_TYPE          VARCHAR2(50),
    CLEAR_REC_AMT          NUMBER(30, 2),
    CLEAR_LOSS_AMT         NUMBER(30, 2),
    OU_PARTY_ID            VARCHAR2(32)
);
comment on table TRD_TEMP_DEAL_TOTAL_ADD is '金融产品临时交易汇总附属表';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.TOTLE_TRADE_ID is '汇总交易编号';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.ENTRUST_ID is '委托代码';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.OUR_SHARE_AMT is '本方券交易份额';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.LENDIN_SHARE_AMT is '融入券交易份额';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.OUR_CPRICE_AMT is '本方券净价总额';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.LENDIN_CPRICE_AMT is '融入券净价总额';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.OUR_FPRICE_AMT is '本方券全价总额';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.LENDIN_FPRICE_AMT is '融入券全价总额';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.OUR_FPRICE_AMT2 is 'OUR_FPRICE_AMT2';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.LENDIN_FPRICE_AMT2 is 'LENDIN_FPRICE_AMT2';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.PURCH_MODLE is '申购方式，网上申购、网下申购';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.PURCH_TYPE is '申购类型，优先配售、普通申购';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.PURCH_DATE is '申购日';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.PUR_PAY_DATE is '预计申购缴款日';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.PUR_CFM_DATE is '预计申购确认日';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.PUR_AMT is '申购申请份额';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.DEPOSIT_AMT is '保证金金额';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.FIRST_UNIT_CPRICE3 is '正股价';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.EXP_CON_VALUE is '预计转股价值';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.PREB is '转股溢价率';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.PREC is '纯债溢价率';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.SPO_SHARE_NUM is '零星股数';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.DEVIATION is '偏离度';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.CONVERSION_RAT is '折算比例';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.BCON_UNIT_CPRICE2 is '折算前单位净值';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.CONVERSION_TYPE is '份额折算类型';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.ASSET_RECOMMAND_ORG is '资产推荐方';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.TERMINATE_RATE is '提前终止利率';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.MP_FINPROD_ID is '主协议金融产品代码';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.MP_BRANCH is '主协议分支序号';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.IS_CASHSET is '是否现金交割';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.GEN_TYPE is '生成方式：接口接入、画面录入、批处理生成';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.CREATE_USER is '创建人';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.CREATE_DEPT is '创建部门';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.CREATE_TIME is '创建时间';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.UPDATE_USER is '更新人';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.UPDATE_TIME is '更新时间';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.PENALTY_INTAMT is '罚息金额';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.INT_SETT_TYPE is '结息方式,适用于活期存放';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.CLEAR_REC_AMT is '清理应收款';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.CLEAR_LOSS_AMT is '清理减值';
comment on column TRD_TEMP_DEAL_TOTAL_ADD.OU_PARTY_ID is '本方';
-- 汇总指令关系表
create table TOTAL_ETR_ENTRUST_REL(
    TOTLE_ID           	     VARCHAR2(32) not null constraint PK_TOTAL_ETR_ENTRUST_REL primary key,
    ENTRUST_ID               VARCHAR2(32),
    TRADER                   VARCHAR2(32),
    CREATE_USER              VARCHAR2(20) not null,
    CREATE_DEPT              VARCHAR2(32),
    CREATE_TIME              TIMESTAMP(6) not null,
    UPDATE_USER              VARCHAR2(20) not null,
    UPDATE_TIME              TIMESTAMP(6) not null
);
comment on table TOTAL_ETR_ENTRUST_REL is '汇总指令关系表';
comment on column TOTAL_ETR_ENTRUST_REL.TOTLE_ID is '汇总代码';
comment on column TOTAL_ETR_ENTRUST_REL.ENTRUST_ID is '委托代码';
comment on column TOTAL_ETR_ENTRUST_REL.TRADER is '交易员';
comment on column TOTAL_ETR_ENTRUST_REL.CREATE_USER is '创建人';
comment on column TOTAL_ETR_ENTRUST_REL.CREATE_DEPT is '创建部门';
comment on column TOTAL_ETR_ENTRUST_REL.CREATE_TIME is '创建时间';
comment on column TOTAL_ETR_ENTRUST_REL.UPDATE_USER is '更新人';
comment on column TOTAL_ETR_ENTRUST_REL.UPDATE_TIME is '更新时间';
-- 投标确认增加字段
alter table BID_ORDER ADD COUNTER_ID VARCHAR(32) ;
COMMENT ON COLUMN BID_ORDER.COUNTER_ID IS '交易对手';
alter table BID_ORDER ADD BID_MODLE VARCHAR2(50) ;
COMMENT ON COLUMN BID_ORDER.BID_MODLE IS '投标方式';
alter table BID_ORDER ADD UNIT_CPRICE NUMBER(30,14) ;
COMMENT ON COLUMN BID_ORDER.UNIT_CPRICE IS '净价(元)';
alter table BID_ORDER ADD UNIT_INT NUMBER(30,4) ;
COMMENT ON COLUMN BID_ORDER.UNIT_INT IS '应计利息';
alter table BID_ORDER ADD TRADE_AMT NUMBER(30,4) ;
COMMENT ON COLUMN BID_ORDER.TRADE_AMT IS '交易金额';
alter table BID_ORDER ADD INT_AMT NUMBER(30,4) ;
COMMENT ON COLUMN BID_ORDER.INT_AMT IS '应计利息总额';
alter table BID_ORDER ADD ASSIGN_REGDATE DATE ;
COMMENT ON COLUMN BID_ORDER.ASSIGN_REGDATE IS '债权债务登记日';
alter table BID_ORDER ADD ONMARKET_DATE DATE ;
COMMENT ON COLUMN BID_ORDER.ONMARKET_DATE IS '上市日';
-- 投标确认明细增加字段
alter table BID_ORDER_BID_PRICE ADD INV_AIM VARCHAR(50) ;
COMMENT ON COLUMN BID_ORDER_BID_PRICE.INV_AIM IS '投资目的';
alter table BID_ORDER_BID_PRICE ADD SEC_ACCT_ID VARCHAR(50) ;
COMMENT ON COLUMN BID_ORDER_BID_PRICE.SEC_ACCT_ID IS '证券账户';
alter table BID_ORDER_BID_PRICE ADD ACCT_NO VARCHAR(50) ;
COMMENT ON COLUMN BID_ORDER_BID_PRICE.ACCT_NO IS '资金账户';
alter table BID_ORDER_BID_PRICE ADD TRADE_AMT NUMBER(30,4) ;
COMMENT ON COLUMN BID_ORDER_BID_PRICE.TRADE_AMT IS '交易金额';
alter table BID_ORDER_BID_PRICE ADD INT_AMT NUMBER(30,4) ;
COMMENT ON COLUMN BID_ORDER_BID_PRICE.INT_AMT IS '应计利息总额';