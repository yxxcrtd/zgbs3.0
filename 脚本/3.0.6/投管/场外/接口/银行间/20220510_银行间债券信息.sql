create table INTERF_RDI_BOND_INFO
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
    FACE_VALUE               NUMBER(3, 2),
    ISSUE_DATE               CHAR(8),
    CIRCULATION_SIZE         NUMBER(10, 2),
    MATURITY_DATE            CHAR(8),
    SECURITY_TERM            VARCHAR2(20),
    TERM_TO_MATURITY         VARCHAR2(20),
    PRINCIPAL                NUMBER(6, 2),
    ACCRUED_INTEREST_AMT     NUMBER(10, 2),
    LIST_DATE                CHAR(8),
    DELIST_DATE              CHAR(8),
    SECURITY_STATUS          CHAR,
    SPECIAL_DEBT             CHAR,
    DEFAULT_BOND_INDICATOR   CHAR,
    EVENT_DATE_START         CHAR(8),
    EVENT_DATE_END           CHAR(8),
    COUPON_RATE_TYPE         CHAR,
    COUPON_PAYMENT_FREQUENCY CHAR,
    INTEREST_ACCRUAL_METHOD  CHAR,
    DAY_COUNT                CHAR,
    SETTL_DATE               CHAR(8),
    COUPON_PAYMENT_DATE      CHAR(8),
    PRICE                    NUMBER(6, 2),
    BENCH_MARK_CURVE_NAME    VARCHAR2(50),
    BENCH_MARK_PRICE         NUMBER(6, 2),
    SPREAD                   NUMBER(6, 2),
    COUPON_CAP_RATE          NUMBER(6, 2),
    COUPON_FLOOR_RATE        NUMBER(6, 2),
    COUPON_RATE              NUMBER(6, 2),
    ISSUER                   VARCHAR2(50),
    CONVERT_DATE             CHAR(8),
    CONVERT_TYPE             CHAR,
    CONVERT_OF_FACE_VALUE    NUMBER(6, 4),
    CONVERT_PX               NUMBER(10, 2),
    PARTY_ID                 VARCHAR2(50),
    NET_SETTLEMENT_INDICATOR CHAR,
    CURRENCY                 VARCHAR2(20),
    TRADE_PUBLISH_INDICATOR  CHAR
)
/

comment on table INTERF_RDI_BOND_INFO is '银行间债券信息接口表'
/

comment on column INTERF_RDI_BOND_INFO.SECURITY_DESC is '债券类型'
/

comment on column INTERF_RDI_BOND_INFO.SYMBOL is '资产支持证券名称'
/

comment on column INTERF_RDI_BOND_INFO.SECURITY_ID is '资产支持证券代码'
/

comment on column INTERF_RDI_BOND_INFO.SECURITY_TYPE_ID is '债券类型代码'
/

comment on column INTERF_RDI_BOND_INFO.TERM_TO_MATURITY_STRING is '标准待偿期'
/

comment on column INTERF_RDI_BOND_INFO.PARTY_SUB_PERSON is '发起人'
/

comment on column INTERF_RDI_BOND_INFO.PARTY_SUB_ID is '发行人代码'
/

comment on column INTERF_RDI_BOND_INFO.ISSUE_SIZE is '发行总量（亿）'
/

comment on column INTERF_RDI_BOND_INFO.ISSUE_PX is '发行价格（元）'
/

comment on column INTERF_RDI_BOND_INFO.FACE_VALUE is '面值（元）'
/

comment on column INTERF_RDI_BOND_INFO.ISSUE_DATE is '发行日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_INFO.CIRCULATION_SIZE is '流通总量（亿）'
/

comment on column INTERF_RDI_BOND_INFO.MATURITY_DATE is '到期日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_INFO.SECURITY_TERM is '债券期限'
/

comment on column INTERF_RDI_BOND_INFO.TERM_TO_MATURITY is '待偿期'
/

comment on column INTERF_RDI_BOND_INFO.ACCRUED_INTEREST_AMT is '每百元应计利息'
/

comment on column INTERF_RDI_BOND_INFO.PRINCIPAL is '每百元本金额（元）'
/

comment on column INTERF_RDI_BOND_INFO.LIST_DATE is '上市交易日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_INFO.DELIST_DATE is '摘牌日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_INFO.SECURITY_STATUS is '状态 枚举值： 1-活动'
/

comment on column INTERF_RDI_BOND_INFO.SPECIAL_DEBT is '特殊债 枚举值： Y-是N-否'
/

comment on column INTERF_RDI_BOND_INFO.DEFAULT_BOND_INDICATOR is '到期违约债 枚举值：Y-是N-否'
/

comment on column INTERF_RDI_BOND_INFO.EVENT_DATE_START is '再次流通起始日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_INFO.EVENT_DATE_END is '再次流通终止日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_INFO.COUPON_RATE_TYPE is '息票类型 枚举值： 1-浮动利率 0-固定利率 2-零息利率3-贴现'
/

comment on column INTERF_RDI_BOND_INFO.COUPON_PAYMENT_FREQUENCY is '付息频率 枚举值： 3-月4-季0-半年 1-年2-到期'
/

comment on column INTERF_RDI_BOND_INFO.INTEREST_ACCRUAL_METHOD is '计算方法 枚举值： 2-频率 3-天数 4-无'
/

comment on column INTERF_RDI_BOND_INFO.DAY_COUNT is '计息基础 枚举值： 3-实际/365 0-实际/实际 1-实际/360 5-实际/365F 2-30/360'
/

comment on column INTERF_RDI_BOND_INFO.SETTL_DATE is '起息日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_INFO.COUPON_PAYMENT_DATE is '首次付息日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_INFO.PRICE is '固定利率（%） 单位：%，精度：6'
/

comment on column INTERF_RDI_BOND_INFO.BENCH_MARK_CURVE_NAME is '基准利率名'
/

comment on column INTERF_RDI_BOND_INFO.BENCH_MARK_PRICE is '基准利率值（%）单位：%，精度：6'
/

comment on column INTERF_RDI_BOND_INFO.SPREAD is '基准利差（基点）（%）单位：%，精度：6'
/

comment on column INTERF_RDI_BOND_INFO.COUPON_CAP_RATE is '利率上限 单位：%，精度：6'
/

comment on column INTERF_RDI_BOND_INFO.COUPON_FLOOR_RATE is '利率下限 单位：%，精度：6'
/

comment on column INTERF_RDI_BOND_INFO.COUPON_RATE is '当前票面利率 单位：%，精度：6'
/

comment on column INTERF_RDI_BOND_INFO.ISSUER is '发行机构简称-中文'
/

comment on column INTERF_RDI_BOND_INFO.CONVERT_DATE is '转换日期 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_INFO.CONVERT_TYPE is '转换类型 枚举值： 0-部分 1-全部'
/

comment on column INTERF_RDI_BOND_INFO.CONVERT_OF_FACE_VALUE is '转换比例'
/

comment on column INTERF_RDI_BOND_INFO.CONVERT_PX is '转换价格（元）'
/

comment on column INTERF_RDI_BOND_INFO.PARTY_ID is '托管机构'
/

comment on column INTERF_RDI_BOND_INFO.NET_SETTLEMENT_INDICATOR is '是否可适用于净额清算 枚举值： Y-是N-否'
/

comment on column INTERF_RDI_BOND_INFO.CURRENCY is '币种'
/

comment on column INTERF_RDI_BOND_INFO.TRADE_PUBLISH_INDICATOR is '流通范围 枚举值： 0-定向 1-公开'
/


