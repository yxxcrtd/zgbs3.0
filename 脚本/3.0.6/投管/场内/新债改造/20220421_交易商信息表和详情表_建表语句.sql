create table MST_DEALER_INFO
(
    CUSTOMER_ID                varchar(32)   not null  ,
    DEALER_NO                  varchar2(50)     not null ,
    TRADE_MARKET               varchar(50)     ,
    CREATE_USER                varchar(20)      ,
    CREATE_DEPT                varchar(32)     ,
    CREATE_TIME                timestamp       ,
    UPDATE_USER                varchar(20)     ,
    UPDATE_TIME                timestamp,
    primary key (CUSTOMER_ID,DEALER_NO)
);
comment on TABLE MST_DEALER_INFO is '交易商信息';
comment on COLUMN MST_DEALER_INFO.CUSTOMER_ID is '客户代码';
comment on COLUMN MST_DEALER_INFO.DEALER_NO is '交易商代码';
comment on COLUMN MST_DEALER_INFO.TRADE_MARKET is '交易场所';


create table MST_DEALER_DETAIL_INFO
(
    SEQ_NO varchar(32)   not null  primary key ,
    CUSTOMER_ID varchar2(32)     not null ,
    DEALER_NO               varchar2(50)     ,
    DEALER_PARTY_NO  varchar2(50)     ,
    DEALER_PARTY_TYPE varchar(50)     ,
    DEALER_PARTY_NAME varchar2(50)      ,
    TRADER_NO varchar(32)      ,
    TRADER_NAME varchar2(50)      ,
    IS_OUR  varchar(50)      ,
    CREATE_USER                varchar(20)      ,
    CREATE_DEPT                varchar(32)     ,
    CREATE_TIME                timestamp       ,
    UPDATE_USER                varchar(20)     ,
    UPDATE_TIME                timestamp
);

comment on TABLE MST_DEALER_DETAIL_INFO is '交易商明细信息';
comment on COLUMN MST_DEALER_DETAIL_INFO.CUSTOMER_ID is '客户代码';
comment on COLUMN MST_DEALER_DETAIL_INFO.DEALER_NO is '交易商代码';
comment on COLUMN MST_DEALER_DETAIL_INFO.DEALER_PARTY_NO is '交易主体代码';
comment on COLUMN MST_DEALER_DETAIL_INFO.DEALER_PARTY_TYPE is '交易主体类型';
comment on COLUMN MST_DEALER_DETAIL_INFO.DEALER_PARTY_NAME is '交易主体名称';
comment on COLUMN MST_DEALER_DETAIL_INFO.TRADER_NO is '交易员代码';
comment on COLUMN MST_DEALER_DETAIL_INFO.TRADER_NAME is '交易员名称';
comment on COLUMN MST_DEALER_DETAIL_INFO.IS_OUR is '本方标识';