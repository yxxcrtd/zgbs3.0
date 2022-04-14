drop table TRD_PRICE_TIME_CONFIG cascade constraints;

/*==============================================================*/
/* Table: TRD_PRICE_TIME_CONFIG                                 */
/*==============================================================*/
create table TRD_PRICE_TIME_CONFIG 
(
   CONFIG_NO            VARCHAR2(32)         not null,
   ETR_TRAD             VARCHAR(50),
   TRADE_MARKET         VARCHAR(50),
   TRADE_PLAT           VARCHAR2(50),
   ASSET_CLASSIFY1      VARCHAR2(50),
   ASSET_CLASSIFY2      VARCHAR2(50),
   ASSET_CLASSIFY3      VARCHAR2(50),
   PRICE_TIME_TYPE      VARCHAR2(50),
   OPEN_TIME            VARCHAR2(50),
   CLOSE_TIME           VARCHAR2(50),
   REMARK               VARCHAR2(1000),
   CREATE_USER          VARCHAR2(20)         not null,
   CREATE_DEPT          VARCHAR2(32),
   CREATE_TIME          TIMESTAMP            not null,
   UPDATE_USER          VARCHAR2(20)         not null,
   UPDATE_TIME          TIMESTAMP            not null,
   constraint PK_TRD_PRICE_TIME_CONFIG primary key (CONFIG_NO)
);

comment on table TRD_PRICE_TIME_CONFIG is
'交易所价格时间配置';

comment on column TRD_PRICE_TIME_CONFIG.CONFIG_NO is
'配置代码';

comment on column TRD_PRICE_TIME_CONFIG.ETR_TRAD is
'指令/交易';

comment on column TRD_PRICE_TIME_CONFIG.TRADE_MARKET is
'交易场所，上交所、深交所等';

comment on column TRD_PRICE_TIME_CONFIG.TRADE_PLAT is
'交易平台';

comment on column TRD_PRICE_TIME_CONFIG.ASSET_CLASSIFY1 is
'资产分类一';

comment on column TRD_PRICE_TIME_CONFIG.ASSET_CLASSIFY2 is
'资产分类二';

comment on column TRD_PRICE_TIME_CONFIG.ASSET_CLASSIFY3 is
'资产分类三';

comment on column TRD_PRICE_TIME_CONFIG.PRICE_TIME_TYPE is
'申报价格范围类型';

comment on column TRD_PRICE_TIME_CONFIG.OPEN_TIME is
'早盘开市时间';

comment on column TRD_PRICE_TIME_CONFIG.CLOSE_TIME is
'早盘收市时间';

comment on column TRD_PRICE_TIME_CONFIG.REMARK is
'备注';

comment on column TRD_PRICE_TIME_CONFIG.CREATE_USER is
'创建人';

comment on column TRD_PRICE_TIME_CONFIG.CREATE_DEPT is
'创建部门';

comment on column TRD_PRICE_TIME_CONFIG.CREATE_TIME is
'创建时间';

comment on column TRD_PRICE_TIME_CONFIG.UPDATE_USER is
'更新人';

comment on column TRD_PRICE_TIME_CONFIG.UPDATE_TIME is
'更新时间';
