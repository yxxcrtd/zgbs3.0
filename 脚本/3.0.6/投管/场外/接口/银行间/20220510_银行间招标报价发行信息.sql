create table INTERF_RDI_RELEASE_INFO
(
    SECURITY_ID                  VARCHAR2(50),
    SYMBOL                       VARCHAR2(110),
    FULL_SYMBOL                  VARCHAR2(300),
    ISSUER                       VARCHAR2(300),
    ISSUER_21_FIRM_ID            VARCHAR2(50),
    ISSUE_METHOD                 CHAR,
    BIDDING_METHOD               CHAR,
    SECURITY_TERM                VARCHAR2(20),
    ISSUE_SIZE                   NUMBER(10, 2),
    FACE_VALUE                   NUMBER(3),
    ISSUE_DATE                   CHAR(8),
    SETTL_DATE                   CHAR(8),
    MATURITY_DATE                CHAR(8),
    PAYMENT_DATE                 CHAR(8),
    COUPON_RATE_TYPE             CHAR,
    COUPON_RATE                  NUMBER(10, 4),
    ISSUE_PX                     NUMBER(10, 4),
    COUPON_PAYMENT_FREQUENCY     CHAR,
    BENCHMARK_CURVE_NAME         VARCHAR2(100),
    DECIMAL_PLACES               CHAR,
    INTEREST_FIX_DATE_ADJUSTMENT VARCHAR2(50),
    SPREAD                       NUMBER(10, 2),
    INTEREST_ACCRUAL_DATE        CHAR(8),
    COUPON_PAYMENT_DATE          CHAR(8),
    DAY_COUNT                    CHAR,
    CREDIT_RATING_AGENCY         VARCHAR2(100),
    CREDIT_RATING                VARCHAR2(20),
    SECURITY_STATUS              CHAR,
    TRADE_PUBLISH_INDICATOR      CHAR,
    STIPULATION_VALUE            NUMBER(10, 4),
    EVENT_DATE                   CHAR(8),
    SECURITY_TYPE                VARCHAR2(20),
    CURRENCY                     VARCHAR2(20),
    MIN_PER_TENDER_AMT           NUMBER(10, 2),
    MAX_PER_TENDER_AMT           NUMBER(10, 2),
    MIN_TENDER_PX_FOR_PLAN       NUMBER(10, 2),
    MIN_TENDER_YIELD             NUMBER(12, 4),
    MAX_TENDER_PX_FOR_PLAN       NUMBER(10, 2),
    MAX_TENDER_YIELD             NUMBER(12, 4),
    TENDER_STEP                  NUMBER(10, 2),
    MEDIAN_MAX_BID_MARKED        NUMBER(4),
    MAX_TENDER_PX_DIFFER         NUMBER(4),
    MAX_TOTAL_TENDER_AMT         NUMBER(10, 2),
    CONTINUOUS_TENDER_LIMIT      CHAR,
    CONTACT_1                    VARCHAR2(30),
    CONTACT_NUMBER_1             VARCHAR2(30),
    FAX_1                        VARCHAR2(30),
    CONTACT_2                    VARCHAR2(30),
    CONTACT_NUMBER_2             VARCHAR2(30),
    FAX_2                        VARCHAR2(30),
    RECEIVING_ACCOUNT_NAME       VARCHAR2(100),
    BENEFICIARY_ACCOUNT          VARCHAR2(100),
    INCOMING_BANK_NAME           VARCHAR2(100),
    SETTLE_BANK_NUMBER           VARCHAR2(100),
    REMARK                       VARCHAR2(300),
    MAX_SUB_AMT                  NUMBER(10, 4),
    SINGLE_MIN_SUB_AMT           NUMBER(10, 4),
    SINGLE_MAX_SUB_AMT           NUMBER(10, 4)
)
/

comment on table INTERF_RDI_RELEASE_INFO is '银行间招标/报价发行信息接口表'
/

comment on column INTERF_RDI_RELEASE_INFO.SECURITY_ID is '工具代码'
/

comment on column INTERF_RDI_RELEASE_INFO.SYMBOL is '工具简称'
/

comment on column INTERF_RDI_RELEASE_INFO.FULL_SYMBOL is '工具全称'
/

comment on column INTERF_RDI_RELEASE_INFO.ISSUER is '发行人'
/

comment on column INTERF_RDI_RELEASE_INFO.ISSUER_21_FIRM_ID is '发行人 21 位码'
/

comment on column INTERF_RDI_RELEASE_INFO.ISSUE_METHOD is '发行方式 0-单一价格招标发行 5-数量招标发行 6-报价发行'
/

comment on column INTERF_RDI_RELEASE_INFO.BIDDING_METHOD is '招标方式 0-单一价格招标发行 5-数量招标发行'
/

comment on column INTERF_RDI_RELEASE_INFO.SECURITY_TERM is '期限 枚举值：1M、3M、6M、9M、1Y、2Y、3Y 
10286 取值以 YYMMMDDD 的格式
具体期限转换规则详见 4.3 章节'
/

comment on column INTERF_RDI_RELEASE_INFO.ISSUE_SIZE is '计划发行量（亿元）'
/

comment on column INTERF_RDI_RELEASE_INFO.FACE_VALUE is '面值（元）'
/

comment on column INTERF_RDI_RELEASE_INFO.ISSUE_DATE is '发行日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_RELEASE_INFO.SETTL_DATE is '起息日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_RELEASE_INFO.MATURITY_DATE is '到期日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_RELEASE_INFO.PAYMENT_DATE is '兑付日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_RELEASE_INFO.COUPON_RATE_TYPE is '息票类型 枚举值：1-浮息；2-零息/3-贴现；0-
固息'
/

comment on column INTERF_RDI_RELEASE_INFO.COUPON_RATE is '票面利率 最长 10 位，精度 4，单位%'
/

comment on column INTERF_RDI_RELEASE_INFO.ISSUE_PX is '发行价格（元）最长 10 位；精度：4，单位：元'
/

comment on column INTERF_RDI_RELEASE_INFO.COUPON_PAYMENT_FREQUENCY is '付息频率 0-半年、1-年、2-期满、3-月、4-季'
/

comment on column INTERF_RDI_RELEASE_INFO.BENCHMARK_CURVE_NAME is '基准利率'
/

comment on column INTERF_RDI_RELEASE_INFO.DECIMAL_PLACES is '基准利率精度 枚举值：1、2、3、4'
/

comment on column INTERF_RDI_RELEASE_INFO.INTEREST_FIX_DATE_ADJUSTMENT is '基准利率确定方式-N'
/

comment on column INTERF_RDI_RELEASE_INFO.SPREAD is '利差（BP）最长 10 位；精度：2；单位：BP'
/

comment on column INTERF_RDI_RELEASE_INFO.INTEREST_ACCRUAL_DATE is '首次利率确定日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_RELEASE_INFO.COUPON_PAYMENT_DATE is '首次付息日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_RELEASE_INFO.DAY_COUNT is '计息基准 枚举值：A/360，A/365，A/A
1-A/360
3-A/365
0-A/A'
/

comment on column INTERF_RDI_RELEASE_INFO.CREDIT_RATING_AGENCY is '主体评级机构'
/

comment on column INTERF_RDI_RELEASE_INFO.CREDIT_RATING is '主体评级等级'
/

comment on column INTERF_RDI_RELEASE_INFO.SECURITY_STATUS is '工具状态 A-待发行
3-已撤销'
/

comment on column INTERF_RDI_RELEASE_INFO.TRADE_PUBLISH_INDICATOR is '流通范围 1-公开'
/

comment on column INTERF_RDI_RELEASE_INFO.STIPULATION_VALUE is '参考收益率 最
长 10 位，精度 4，单位%；'
/

comment on column INTERF_RDI_RELEASE_INFO.EVENT_DATE is '缴款日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_RELEASE_INFO.SECURITY_TYPE is '工具类型 CD-同业存单
6-央行票据
0-国债
1-政策性金融债
5-企业债
3-次级债
2-短期融资券
7-国际开发机构债
8-混合资本债
10-中期票据
11-地方政府债
12-财务公司债
13-证券公司债
23-集合票据
27-金融租赁公司金融债
9-资产支持证券
22-政府支持机构债券
19-超短期融资券
22-政府支持债券
14-证券公司短期融资券
49-二级资本工具
50-保险公司资本补充债
45-资产管理公司金融债'
/

comment on column INTERF_RDI_RELEASE_INFO.CURRENCY is '币种'
/

comment on column INTERF_RDI_RELEASE_INFO.MIN_PER_TENDER_AMT is '每标位最低投标量（亿元）最长 10 位，精
度 1，单位：亿元'
/

comment on column INTERF_RDI_RELEASE_INFO.MAX_PER_TENDER_AMT is '每标位最高投标量（亿元）最长 10 位，精
度 1，'
/

comment on column INTERF_RDI_RELEASE_INFO.MIN_TENDER_PX_FOR_PLAN is '最低标位 最长 10 位'
/

comment on column INTERF_RDI_RELEASE_INFO.MIN_TENDER_YIELD is '最低标位参考收益率 最长12 位，精度 4，单位%，例如 3.1234%'
/

comment on column INTERF_RDI_RELEASE_INFO.MAX_TENDER_PX_FOR_PLAN is '最高标位 最长 10 位'
/

comment on column INTERF_RDI_RELEASE_INFO.MAX_TENDER_YIELD is '最高标位参考收益率 最长
12 位，精度 4，单位%，例如 3.1234%'
/

comment on column INTERF_RDI_RELEASE_INFO.TENDER_STEP is '投标步长 最长 10 位'
/

comment on column INTERF_RDI_RELEASE_INFO.MEDIAN_MAX_BID_MARKED is '最大投标标位数 整数；最长 4
位，单位：个'
/

comment on column INTERF_RDI_RELEASE_INFO.MAX_TENDER_PX_DIFFER is '最大投标标位差（个）整数；最长 4
位，单位：个'
/

comment on column INTERF_RDI_RELEASE_INFO.MAX_TOTAL_TENDER_AMT is '最高投标总量（亿元）最长 4 位；精
度：1，单位：亿元'
/

comment on column INTERF_RDI_RELEASE_INFO.CONTINUOUS_TENDER_LIMIT is '连续投标限制 Y-是N-否'
/

comment on column INTERF_RDI_RELEASE_INFO.CONTACT_1 is '联系人 1'
/

comment on column INTERF_RDI_RELEASE_INFO.CONTACT_NUMBER_1 is '联系电话 1'
/

comment on column INTERF_RDI_RELEASE_INFO.FAX_1 is '传真 1'
/

comment on column INTERF_RDI_RELEASE_INFO.CONTACT_2 is '联系人 2'
/

comment on column INTERF_RDI_RELEASE_INFO.CONTACT_NUMBER_2 is '联系电话 2'
/

comment on column INTERF_RDI_RELEASE_INFO.FAX_2 is '传真 2'
/

comment on column INTERF_RDI_RELEASE_INFO.RECEIVING_ACCOUNT_NAME is '收款账户名称'
/

comment on column INTERF_RDI_RELEASE_INFO.BENEFICIARY_ACCOUNT is '收款人账号'
/

comment on column INTERF_RDI_RELEASE_INFO.INCOMING_BANK_NAME is '汇入行名称'
/

comment on column INTERF_RDI_RELEASE_INFO.SETTLE_BANK_NUMBER is '支付系统清算行行号'
/

comment on column INTERF_RDI_RELEASE_INFO.REMARK is '备注'
/

comment on column INTERF_RDI_RELEASE_INFO.MAX_SUB_AMT is '最高认购总量（亿元）单位：亿元，最长
10 位，精度：1'
/

comment on column INTERF_RDI_RELEASE_INFO.SINGLE_MIN_SUB_AMT is '单笔最低认购量（亿元）单位：亿元，最长
10 位，精度：1'
/

comment on column INTERF_RDI_RELEASE_INFO.SINGLE_MAX_SUB_AMT is '单笔最高认购量（亿元）单位：亿元，最长
10 位，精度：1'
/

