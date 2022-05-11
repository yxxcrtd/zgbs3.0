create table INTERF_WIND_RELEASE_INFO
(
    SEC_ID                  VARCHAR2(50),
    ISSUING_METHOD          VARCHAR2(20),
    BIDDING_METHOD          VARCHAR2(20),
    TENDER_TARGET           VARCHAR2(50),
    ISSUE_SIZE              NUMBER(14, 2),
    RELEASE_TYPE            VARCHAR2(20),
    ADDITIONAL_BATCHES      int,
    ISSUE_PX                NUMBER(14, 4),
    STIPULATION_VALUE       NUMBER(14, 4),
    COUPON_RATE             NUMBER(14, 4),
    BENCHMARK_CURVE_NAME    NUMBER(14, 4),
    DECIMAL_PLACES          char,
    SPREAD                  NUMBER(14, 2),
    ISSUE_DATE              DATE,
    EVENT_DATE              DATE,
    INTEREST_ACCRUAL_DATE   DATE,
    ISSUE_START_TIME        TIMESTAMP,
    ISSUE_END_TIME          TIMESTAMP,
    BID_DATE                DATE,
    ADDITIONAL_TENDER_DATE  DATE,
    PAYMENT_DEADLINE        DATE,
    DEBT_REGISTRATION_DATE  DATE,
    LISTING_DATE            DATE,
    LISTING_CODE            VARCHAR2(20),
    SINGLE_MIN_SUB_AMT      NUMBER(14, 2),
    SINGLE_MAX_SUB_AMT      NUMBER(14, 2),
    MAX_SUB_AMT             NUMBER(14, 2),
    MIN_PER_TENDER_AMT      NUMBER(14, 2),
    MAX_PER_TENDER_AMT      NUMBER(14, 2),
    MIN_TENDER_PX_FOR_PLAN  NUMBER(14, 4),
    MAX_TENDER_PX_FOR_PLAN  NUMBER(14, 4),
    TENDER_STEP             NUMBER(14, 4),
    MEDIAN_MAX_BID_MARKED   int,
    MAX_TENDER_PX_DIFFER    NUMBER(14, 4),
    CONTINUOUS_TENDER_LIMIT VARCHAR2(20),
    MAX_TOTAL_TENDER_AMT    NUMBER(14, 2)
);
comment on table INTERF_WIND_RELEASE_INFO is 'wind一级未发行债券发行信息接口表';
comment on column INTERF_WIND_RELEASE_INFO.SEC_ID is '债券ID';
comment on column INTERF_WIND_RELEASE_INFO.ISSUING_METHOD is '发行方式';
comment on column INTERF_WIND_RELEASE_INFO.BIDDING_METHOD is '招标方式';
comment on column INTERF_WIND_RELEASE_INFO.TENDER_TARGET is '招标标的';
comment on column INTERF_WIND_RELEASE_INFO.ISSUE_SIZE is '计划发行量';
comment on column INTERF_WIND_RELEASE_INFO.RELEASE_TYPE is '发行类型';
comment on column INTERF_WIND_RELEASE_INFO.ADDITIONAL_BATCHES is '增发批次';
comment on column INTERF_WIND_RELEASE_INFO.ISSUE_PX is '发行价格';
comment on column INTERF_WIND_RELEASE_INFO.STIPULATION_VALUE is '参考收益率';
comment on column INTERF_WIND_RELEASE_INFO.COUPON_RATE is '票面利率';
comment on column INTERF_WIND_RELEASE_INFO.BENCHMARK_CURVE_NAME is '基准利率';
comment on column INTERF_WIND_RELEASE_INFO.DECIMAL_PLACES is '基准利率精度';
comment on column INTERF_WIND_RELEASE_INFO.SPREAD is '利差';
comment on column INTERF_WIND_RELEASE_INFO.ISSUE_DATE is '发行日';
comment on column INTERF_WIND_RELEASE_INFO.EVENT_DATE is '缴款日';
comment on column INTERF_WIND_RELEASE_INFO.INTEREST_ACCRUAL_DATE is '首次利率确定日';
comment on column INTERF_WIND_RELEASE_INFO.ISSUE_START_TIME is '发行开始时间';
comment on column INTERF_WIND_RELEASE_INFO.ISSUE_END_TIME is '发行结束时间';
comment on column INTERF_WIND_RELEASE_INFO.BID_DATE is '投标日';
comment on column INTERF_WIND_RELEASE_INFO.ADDITIONAL_TENDER_DATE is '增发投标日';
comment on column INTERF_WIND_RELEASE_INFO.PAYMENT_DEADLINE is '缴款截止日';
comment on column INTERF_WIND_RELEASE_INFO.DEBT_REGISTRATION_DATE is '债权债务登记日';
comment on column INTERF_WIND_RELEASE_INFO.LISTING_DATE is '上市日';
comment on column INTERF_WIND_RELEASE_INFO.LISTING_CODE is '上市代码';
comment on column INTERF_WIND_RELEASE_INFO.SINGLE_MIN_SUB_AMT is '单笔最低认购量';
comment on column INTERF_WIND_RELEASE_INFO.SINGLE_MAX_SUB_AMT is '单笔最高认购量';
comment on column INTERF_WIND_RELEASE_INFO.MAX_SUB_AMT is '最高认购总量';
comment on column INTERF_WIND_RELEASE_INFO.MIN_PER_TENDER_AMT is '每标位最低投标量';
comment on column INTERF_WIND_RELEASE_INFO.MAX_PER_TENDER_AMT is '每标位最高投标量';
comment on column INTERF_WIND_RELEASE_INFO.MIN_TENDER_PX_FOR_PLAN is '最低标位';
comment on column INTERF_WIND_RELEASE_INFO.MAX_TENDER_PX_FOR_PLAN is '最高标位';
comment on column INTERF_WIND_RELEASE_INFO.TENDER_STEP is '投标步长';
comment on column INTERF_WIND_RELEASE_INFO.MEDIAN_MAX_BID_MARKED is '最大投标标位数';
comment on column INTERF_WIND_RELEASE_INFO.MAX_TENDER_PX_DIFFER is '最大投标标位差';
comment on column INTERF_WIND_RELEASE_INFO.CONTINUOUS_TENDER_LIMIT is '连续投标限制';
comment on column INTERF_WIND_RELEASE_INFO.MAX_TOTAL_TENDER_AMT is '最高投标总量';
alter table INTERF_WIND_RELEASE_INFO MODIFY BENCHMARK_CURVE_NAME VARCHAR2(20);











































