-- auto-generated definition
DROP TABLE RSK_PRE_OCCUPY_INFO_HIS;
create table RSK_PRE_OCCUPY_INFO_HIS
(
    UUID          VARCHAR2(32) not null
        constraint PK_RSK_PRE_OCCUPY_INFO_HIS
        primary key,
    EVENT_DATE    DATE         not null,
    EVENT_ID      VARCHAR2(32) not null,
    EVENT_TYPE    VARCHAR2(50),
    OCCUPY_DATE   DATE,
    ENTRUST_CODE  VARCHAR2(32),
    PROD_ID       VARCHAR2(32),
    ASSET_CODE    VARCHAR2(50),
    ASSET_TYPE    VARCHAR2(50),
    ASSET_TYPE2   VARCHAR2(50),
    PS            VARCHAR2(50),
    AMOUNT        NUMBER(30, 14),
    PRICE_AMT     NUMBER(30, 14),
    TRADE_NUM     NUMBER(30, 14),
    NET_PRICE     NUMBER(30, 14),
    ALL_PRICE     NUMBER(30, 14),
    UNIT_NET      NUMBER(30, 14),
    ASSET_RATE    NUMBER(30, 14),
    TRADER        VARCHAR2(600),
    MANAGER       VARCHAR2(600),
    DELIVERY_DATE DATE,
    START_DATE    DATE,
    END_DATE      DATE,
    OCP_STATUS    VARCHAR2(50) not null,
    WFL_STATUS    VARCHAR2(50) not null,
    CREATE_USER   VARCHAR2(20) not null,
    CREATE_DEPT   VARCHAR2(32),
    CREATE_TIME   TIMESTAMP(6) not null,
    UPDATE_USER   VARCHAR2(20) not null,
    UPDATE_TIME   TIMESTAMP(6) not null,
    SEQ_NO        VARCHAR2(64)
)
    ;

comment on table RSK_PRE_OCCUPY_INFO_HIS is '预占信息'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.UUID is '流水号'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.EVENT_DATE is '事件日期'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.EVENT_ID is '预占编号'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.EVENT_TYPE is '事件类型(0：投资委托)'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.OCCUPY_DATE is '预占到期日'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.ENTRUST_CODE is '委托编号'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.PROD_ID is '产品代码'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.ASSET_CODE is '资产代码'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.ASSET_TYPE is '系统资产大类'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.ASSET_TYPE2 is '系统资产细类'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.PS is '交易方向'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.AMOUNT is '金额'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.PRICE_AMT is '价格'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.TRADE_NUM is '数量'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.NET_PRICE is '净价'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.ALL_PRICE is '全价'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.UNIT_NET is '单位净值'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.ASSET_RATE is '资产利率'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.TRADER is '交易员'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.MANAGER is '投资经理'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.DELIVERY_DATE is '交割日'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.START_DATE is '委托开始日'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.END_DATE is '委托结束日'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.OCP_STATUS is '状态，预占、释放'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.WFL_STATUS is '当前流程状态，待审批、审批退回、审批通过、直接通过'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.CREATE_USER is '创建人'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.CREATE_DEPT is '创建部门'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.CREATE_TIME is '创建时间'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.UPDATE_USER is '更新人'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.UPDATE_TIME is '更新时间'
;

comment on column RSK_PRE_OCCUPY_INFO_HIS.SEQ_NO is '报文流水号'
;

-- auto-generated definition
DROP TABLE RSK_OCCUPY_INFO_HIS;
create table RSK_OCCUPY_INFO_HIS
(
    UUID          VARCHAR2(32)  not null
        constraint PK_RSK_OCCUPY_INFO_HIS
        primary key,
    EVENT_DATE    DATE          not null,
    EVENT_ID      VARCHAR2(128) not null,
    EVENT_TYPE    VARCHAR2(50),
    ENTRUST_CODE  VARCHAR2(32),
    OCCUPY_CODE   VARCHAR2(32),
    PROD_ID       VARCHAR2(32),
    ASSET_CODE    VARCHAR2(50),
    ASSET_TYPE    VARCHAR2(50),
    ASSET_TYPE2   VARCHAR2(50),
    PS            VARCHAR2(50),
    AMOUNT        NUMBER(30, 14),
    PRICE_AMT     NUMBER(30, 14),
    TRADE_NUM     NUMBER(30, 14),
    NET_PRICE     NUMBER(30, 14),
    ALL_PRICE     NUMBER(30, 14),
    UNIT_NET      NUMBER(30, 14),
    ASSET_RATE    NUMBER(30, 14),
    TRADER        VARCHAR2(600),
    MANAGER       VARCHAR2(600),
    DELIVERY_DATE DATE,
    START_DATE    DATE,
    END_DATE      DATE,
    OCP_STATUS    VARCHAR2(50)  not null,
    WFL_STATUS    VARCHAR2(50)  not null,
    CREATE_USER   VARCHAR2(20)  not null,
    CREATE_DEPT   VARCHAR2(32),
    CREATE_TIME   TIMESTAMP(6)  not null,
    UPDATE_USER   VARCHAR2(20)  not null,
    UPDATE_TIME   TIMESTAMP(6)  not null,
    SEQ_NO        VARCHAR2(64)
)
    ;

comment on table RSK_OCCUPY_INFO_HIS is '实占信息'
;

comment on column RSK_OCCUPY_INFO_HIS.UUID is '流水号'
;

comment on column RSK_OCCUPY_INFO_HIS.EVENT_DATE is '事件日期'
;

comment on column RSK_OCCUPY_INFO_HIS.EVENT_ID is '实占编号（交易代码）'
;

comment on column RSK_OCCUPY_INFO_HIS.EVENT_TYPE is '事件类型(1：投资交易、2：交易拆分)'
;

comment on column RSK_OCCUPY_INFO_HIS.ENTRUST_CODE is '委托编号'
;

comment on column RSK_OCCUPY_INFO_HIS.OCCUPY_CODE is '预占编号'
;

comment on column RSK_OCCUPY_INFO_HIS.PROD_ID is '产品代码'
;

comment on column RSK_OCCUPY_INFO_HIS.ASSET_CODE is '资产代码'
;

comment on column RSK_OCCUPY_INFO_HIS.ASSET_TYPE is '系统资产大类'
;

comment on column RSK_OCCUPY_INFO_HIS.ASSET_TYPE2 is '系统资产细类'
;

comment on column RSK_OCCUPY_INFO_HIS.PS is '交易方向'
;

comment on column RSK_OCCUPY_INFO_HIS.AMOUNT is '金额'
;

comment on column RSK_OCCUPY_INFO_HIS.PRICE_AMT is '价格'
;

comment on column RSK_OCCUPY_INFO_HIS.TRADE_NUM is '数量'
;

comment on column RSK_OCCUPY_INFO_HIS.NET_PRICE is '净价'
;

comment on column RSK_OCCUPY_INFO_HIS.ALL_PRICE is '全价'
;

comment on column RSK_OCCUPY_INFO_HIS.UNIT_NET is '单位净值'
;

comment on column RSK_OCCUPY_INFO_HIS.ASSET_RATE is '资产利率'
;

comment on column RSK_OCCUPY_INFO_HIS.TRADER is '交易员'
;

comment on column RSK_OCCUPY_INFO_HIS.MANAGER is '投资经理'
;

comment on column RSK_OCCUPY_INFO_HIS.DELIVERY_DATE is '交割日'
;

comment on column RSK_OCCUPY_INFO_HIS.START_DATE is '委托开始日'
;

comment on column RSK_OCCUPY_INFO_HIS.END_DATE is '委托结束日'
;

comment on column RSK_OCCUPY_INFO_HIS.OCP_STATUS is '状态，预占、释放'
;

comment on column RSK_OCCUPY_INFO_HIS.WFL_STATUS is '当前流程状态，待审批、审批退回、审批通过、直接通过'
;

comment on column RSK_OCCUPY_INFO_HIS.CREATE_USER is '创建人'
;

comment on column RSK_OCCUPY_INFO_HIS.CREATE_DEPT is '创建部门'
;

comment on column RSK_OCCUPY_INFO_HIS.CREATE_TIME is '创建时间'
;

comment on column RSK_OCCUPY_INFO_HIS.UPDATE_USER is '更新人'
;

comment on column RSK_OCCUPY_INFO_HIS.UPDATE_TIME is '更新时间'
;

comment on column RSK_OCCUPY_INFO_HIS.SEQ_NO is '报文流水号'
;

