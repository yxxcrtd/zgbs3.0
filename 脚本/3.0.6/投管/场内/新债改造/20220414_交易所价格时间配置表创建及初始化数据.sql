declare
      num number;
begin
    select count(1) into num from user_tables where table_name = upper('TRD_PRICE_TIME_CONFIG') ;
    if num > 0 then
        execute immediate 'drop table TRD_PRICE_TIME_CONFIG' ;
    end if;
end;
/
create table TRD_PRICE_TIME_CONFIG
(
    CONFIG_NO       int          not null
        primary key,
    ETR_TRAD        varchar2(8)  not null,
    TRADE_MARKET    varchar2(8),
    TRADE_PLAT      varchar2(8),
    ASSET_CLASSIFY1 varchar2(8),
    ASSET_CLASSIFY2 varchar2(8),
    ASSET_CLASSIFY3 varchar2(8),
    DEAL_MODE       varchar2(8),
    PRICE_TIME_TYPE varchar2(8),
    OPEN_TIME       varchar2(32) not null,
    CLOSE_TIME      varchar2(32) not null,
    REMARK          varchar2(64) ,
    CREATE_USER     varchar2(32) not null,
    CREATE_DEPT     varchar2(8),
    CREATE_TIME     timestamp(6) not null,
    UPDATE_USER     varchar2(32) not null,
    UPDATE_TIME     timestamp(6) not null
)
/

comment on table TRD_PRICE_TIME_CONFIG is '交易所价格时间配置表'
/
comment on column TRD_PRICE_TIME_CONFIG.CONFIG_NO is '配置代码'
/
comment on column TRD_PRICE_TIME_CONFIG.ETR_TRAD is '指令/交易'
/
comment on column TRD_PRICE_TIME_CONFIG.TRADE_MARKET is '交易场所'
/
comment on column TRD_PRICE_TIME_CONFIG.TRADE_PLAT is '交易平台'
/
comment on column TRD_PRICE_TIME_CONFIG.ASSET_CLASSIFY1 is '资产分类一'
/
comment on column TRD_PRICE_TIME_CONFIG.ASSET_CLASSIFY2 is '资产分类二'
/
comment on column TRD_PRICE_TIME_CONFIG.ASSET_CLASSIFY3 is '资产分类三'
/
comment on column TRD_PRICE_TIME_CONFIG.DEAL_MODE is '交易方式'
/
comment on column TRD_PRICE_TIME_CONFIG.PRICE_TIME_TYPE is '申报价格范围类型'
/
comment on column TRD_PRICE_TIME_CONFIG.OPEN_TIME is '开始时间'
/
comment on column TRD_PRICE_TIME_CONFIG.CLOSE_TIME is '结束时间'
/
comment on column TRD_PRICE_TIME_CONFIG.REMARK is '备注'
/
comment on column TRD_PRICE_TIME_CONFIG.CREATE_USER is '创建人'
/
comment on column TRD_PRICE_TIME_CONFIG.CREATE_DEPT is '创建部门'
/
comment on column TRD_PRICE_TIME_CONFIG.CREATE_TIME is '创建时间'
/
comment on column TRD_PRICE_TIME_CONFIG.UPDATE_USER is '更新人'
/
comment on column TRD_PRICE_TIME_CONFIG.UPDATE_TIME is '更新时间'
/
