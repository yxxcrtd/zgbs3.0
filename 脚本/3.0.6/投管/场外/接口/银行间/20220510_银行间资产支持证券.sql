create table INTERF_RDI_ASSET_BACK_SECURITY
(
    SECURITY_DESC            VARCHAR2(20),
    SYMBOL                   VARCHAR2(100),
    SECURITY_ID              VARCHAR2(20),
    SECURITY_TYPE_ID         VARCHAR2(20),
    TERM_TO_MATURITY_STRING  VARCHAR2(20),
    PARTY_SUB_PERSON         VARCHAR2(20),
    PARTY_SUB_ID             VARCHAR2(20),
    ISSUE_SIZE               NUMBER(10, 2),
    ISSUE_PX                 NUMBER(6, 2),
    ISSUE_DATE               CHAR(8),
    CIRCULATION_SIZE         NUMBER(10, 2),
    EXPECTED_MATURITY_DATE   CHAR(8),
    MATURITY_DATE            CHAR(8),
    PRINCIPAL                NUMBER(6, 2),
    LIST_DATE                CHAR(8),
    DELIST_DATE              CHAR(8),
    SECURITY_STATUS          CHAR,
    SPECIAL_DEBT             CHAR,
    COUPON_RATE_TYPE         CHAR,
    COUPON_PAYMENT_FREQUENCY CHAR,
    DAY_COUNT                CHAR,
    PAYMENT_DATE             CHAR(8),
    BENCH_MARK_CURVE_NAME    VARCHAR2(50),
    BENCH_MARK_PRICE         NUMBER(6, 2),
    SPREAD                   NUMBER(6, 2),
    PARTY_ID                 VARCHAR2(50),
    CURRENCY                 VARCHAR2(20),
    PRINCIPAL_PAY_FREQUENCY  CHAR,
    EVENT_DATE               CHAR(8)
)
;

comment on table INTERF_RDI_ASSET_BACK_SECURITY is '银行间资产支持证券接口表'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.SECURITY_DESC is '债券类型'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.SYMBOL is '资产支持证券名称'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.SECURITY_ID is '资产支持证券代码'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.SECURITY_TYPE_ID is '债券类型代码'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.TERM_TO_MATURITY_STRING is '标准待偿期'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.PARTY_SUB_PERSON is '发起人代码'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.PARTY_SUB_ID is '发行机构'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.ISSUE_SIZE is '发行总量（亿）'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.ISSUE_PX is '发行价（元）'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.ISSUE_DATE is '发行日 格式：YYYYMMDD'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.CIRCULATION_SIZE is '流通总量（亿）'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.EXPECTED_MATURITY_DATE is '预期到期日 格式：YYYYMMDD'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.MATURITY_DATE is '到期日 格式：YYYYMMDD'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.PRINCIPAL is '每百元本金额（元）'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.LIST_DATE is '上市交易日 格式：YYYYMMDD'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.DELIST_DATE is '摘牌日 格式：YYYYMMDD'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.SECURITY_STATUS is '状态 枚举值： 1-活动 G-非活动'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.SPECIAL_DEBT is '特殊债 枚举值： Y-是N-否'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.COUPON_RATE_TYPE is '利率类型 枚举值： 1-浮动利率 0-固定利率 2-零息利率3-贴现'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.COUPON_PAYMENT_FREQUENCY is '付息频率 枚举值： 3-月4-季0-半年 1-年2-到期'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.DAY_COUNT is '计息基础 枚举值： 3-实际;365 0-实际;实际 1-实际;360 5-实际;365F 2-30;360'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.PAYMENT_DATE is '下一付本日 格式：YYYYMMDD'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.BENCH_MARK_CURVE_NAME is '基准利率'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.BENCH_MARK_PRICE is '当前利率（%）单位：%，精度：6'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.SPREAD is '基本利差（基点）（%）单位：%，精度：6'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.PARTY_ID is '托管机构'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.CURRENCY is '币种'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.PRINCIPAL_PAY_FREQUENCY is '本金支付频率 枚举值： 0-半年1-年3-月4-季2-到期5-两周6-周7-天'
;

comment on column INTERF_RDI_ASSET_BACK_SECURITY.EVENT_DATE is '下一本金除权日 格式：YYYYMMDD'
;


