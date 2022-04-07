drop table FIN_PRODUCT_ISSUE cascade constraints;

/*==============================================================*/
/* Table: FIN_PRODUCT_ISSUE                                     */
/*==============================================================*/
create table FIN_PRODUCT_ISSUE
(
    FINPROD_ID           VARCHAR(50)          not null,
    ISSUE_CLASS          VARCHAR(50),
    TENDER_TYPE          VARCHAR(50),
    TENDER_TARGET        VARCHAR(50),
    PLAN_ISSUE_AMT       NUMERIC(30, 14),
    ISSUE_STYLE          VARCHAR(50),
    ADD_BATCH            NUMERIC(10),
    ISSUE_PRICE          NUMERIC(30, 14),
    REFER_YIELD          NUMERIC(30, 14),
    INT_RATE_ID          VARCHAR(50),
    RATE_GRADE           VARCHAR(50),
    BP                   NUMERIC(30, 14),
    ISSUE_DATE           DATE,
    PAY_DATE             DATE,
    FIRST_CONFIRM_DATE   DATE,
    ISSUE_SDATE          DATE,
    ISSUE_EDATE          DATE,
    TENDER_SDATE         DATE,
    TENDER_EDATE         DATE,
    PAY_EDATE            DATE,
    ASSIGN_REGDATE       DATE,
    MIN_PAMT             NUMERIC(30, 14),
    MAX_PAMT             NUMERIC(30, 14),
    MAX_TOTALAMT         NUMERIC(30, 14),
    MIN_PER_TAMT         NUMERIC(30, 14),
    MAX_PER_UTAMT        NUMERIC(30, 14),
    MIN_TAMT             NUMERIC(30, 14),
    MAX_TAMT             NUMERIC(30, 14),
    TENDER_UNIT          NUMERIC(30, 14),
    MAX_TENDER_UNIT      NUMERIC(30, 14),
    MAX_TENDER_PLUS      NUMERIC(30, 14),
    SERI_Y_N             VARCHAR(50),
    MAX_TOTAL_TENDER     NUMERIC(30, 14),
    PAY_CYCLE            VARCHAR(50),
    INT_TYPE             VARCHAR(50),
    CREATE_USER          VARCHAR(20)          not null,
    CREATE_DEPT          VARCHAR(32),
    CREATE_TIME          TIMESTAMP            not null,
    UPDATE_USER          VARCHAR(20)          not null,
    UPDATE_TIME          TIMESTAMP            not null,
    constraint PK_FIN_PRODUCT_ISSUE primary key (FINPROD_ID)
);

comment on table FIN_PRODUCT_ISSUE is
'标的类金融产品发行信息';

comment on column FIN_PRODUCT_ISSUE.FINPROD_ID is
'金融产品代码';

comment on column FIN_PRODUCT_ISSUE.ISSUE_CLASS is
'发行方式，报价发行/招标发行/簿记建档';

comment on column FIN_PRODUCT_ISSUE.TENDER_TYPE is
'招标方式，单一价格招标/数量招标/荷兰式招标/混合式招标';

comment on column FIN_PRODUCT_ISSUE.TENDER_TARGET is
'招标标的，利率(%)/价格(元)/利差(BP)';

comment on column FIN_PRODUCT_ISSUE.PLAN_ISSUE_AMT is
'计划发行量';

comment on column FIN_PRODUCT_ISSUE.ISSUE_STYLE is
'发行类型,首发/增发';

comment on column FIN_PRODUCT_ISSUE.ADD_BATCH is
'增发批次,首发时为0';

comment on column FIN_PRODUCT_ISSUE.ISSUE_PRICE is
'发行价格';

comment on column FIN_PRODUCT_ISSUE.REFER_YIELD is
'收益率，利息贴现为参考收益率，附息为票面利率';

comment on column FIN_PRODUCT_ISSUE.INT_RATE_ID is
'行情代码';

comment on column FIN_PRODUCT_ISSUE.RATE_GRADE is
'基准利率精度';

comment on column FIN_PRODUCT_ISSUE.BP is
'利差';

comment on column FIN_PRODUCT_ISSUE.ISSUE_DATE is
'发行日';

comment on column FIN_PRODUCT_ISSUE.PAY_DATE is
'缴款日';

comment on column FIN_PRODUCT_ISSUE.FIRST_CONFIRM_DATE is
'首次利率确定日';

comment on column FIN_PRODUCT_ISSUE.ISSUE_SDATE is
'发行开始日';

comment on column FIN_PRODUCT_ISSUE.ISSUE_EDATE is
'发行结束日';

comment on column FIN_PRODUCT_ISSUE.TENDER_SDATE is
'投标起始日';

comment on column FIN_PRODUCT_ISSUE.TENDER_EDATE is
'投标截止日';

comment on column FIN_PRODUCT_ISSUE.PAY_EDATE is
'缴款截止日';

comment on column FIN_PRODUCT_ISSUE.ASSIGN_REGDATE is
'债权债务登记日';

comment on column FIN_PRODUCT_ISSUE.MIN_PAMT is
'单笔最低认购量';

comment on column FIN_PRODUCT_ISSUE.MAX_PAMT is
'单笔最高认购量';

comment on column FIN_PRODUCT_ISSUE.MAX_TOTALAMT is
'最高认购总量';

comment on column FIN_PRODUCT_ISSUE.MIN_PER_TAMT is
'每标位最低投标量';

comment on column FIN_PRODUCT_ISSUE.MAX_PER_UTAMT is
'每标位最高投标量';

comment on column FIN_PRODUCT_ISSUE.MIN_TAMT is
'最低标位';

comment on column FIN_PRODUCT_ISSUE.MAX_TAMT is
'最高标位';

comment on column FIN_PRODUCT_ISSUE.TENDER_UNIT is
'投标步长';

comment on column FIN_PRODUCT_ISSUE.MAX_TENDER_UNIT is
'最大投标标位数';

comment on column FIN_PRODUCT_ISSUE.MAX_TENDER_PLUS is
'最大投标标位差';

comment on column FIN_PRODUCT_ISSUE.SERI_Y_N is
'连续投标限制';

comment on column FIN_PRODUCT_ISSUE.MAX_TOTAL_TENDER is
'最高投标总量';

comment on column FIN_PRODUCT_ISSUE.PAY_CYCLE is
'付息频率,月付/季付/年付/半年付/到期一次';

comment on column FIN_PRODUCT_ISSUE.INT_TYPE is
'利率类型';

comment on column FIN_PRODUCT_ISSUE.CREATE_USER is
'创建人';

comment on column FIN_PRODUCT_ISSUE.CREATE_DEPT is
'创建部门';

comment on column FIN_PRODUCT_ISSUE.CREATE_TIME is
'创建时间';

comment on column FIN_PRODUCT_ISSUE.UPDATE_USER is
'更新人';

comment on column FIN_PRODUCT_ISSUE.UPDATE_TIME is
'更新时间';

drop table INTERF_CFETS_NEWBOND_ISSUE cascade constraints;

/*==============================================================*/
/* Table: INTERF_CFETS_NEWBOND_ISSUE                            */
/*==============================================================*/
create table INTERF_CFETS_NEWBOND_ISSUE
(
    MARKET_ID            VARCHAR(50)          not null,
    FINPROD_ABBR         VARCHAR(200),
    FINPROD_NAME         VARCHAR(200),
    ISSUER               VARCHAR(200),
    ISSUER_CODE          VARCHAR(21),
    ISSUE_CLASS          VARCHAR(50),
    TENDER_TYPE          VARCHAR(50),
    TENDER_TARGET        VARCHAR(50),
    PERIOD               VARCHAR(50),
    PLAN_ISSUE_AMT       NUMERIC(30, 14),
    PAR_VALUE            NUMERIC(30, 14),
    ISSUE_DATE           DATE,
    VDATE                DATE,
    MDATE                DATE,
    PDATE                DATE,
    COUPON_SPECIES       VARCHAR(50),
    YIELD                NUMERIC(30, 14),
    ISSUE_PRICE          NUMERIC(30, 14),
    PAY_CYCLE            VARCHAR(50),
    INT_RATE_ID          VARCHAR(50),
    RATE_GRADE           VARCHAR(50),
    INT_RATE_CONFTYPE    VARCHAR(50),
    BP                   NUMERIC(30, 14),
    FIRST_CONFIRM_DATE   DATE,
    FIRST_INT_PAYDATE    DATE,
    BASIS                VARCHAR(50),
    ORG_ID               VARCHAR(50),
    ORG_GRADE            VARCHAR(50),
    REFER_YIELD          NUMERIC(30, 14),
    ISSUE_STATUS         VARCHAR(50),
    FINPROD_TYPE         VARCHAR(50),
    RANGE_CIRCLE         VARCHAR(200),
    PAY_DATE             DATE,
    CCY                  VARCHAR(50),
    MIN_PER_TAMT         NUMERIC(30, 14),
    MAX_PER_UTAMT        NUMERIC(30, 14),
    MIN_TAMT             NUMERIC(30, 14),
    MIN_T_REFER_YILED    NUMERIC(30, 14),
    MAX_TAMT             NUMERIC(30, 14),
    MAX_T_REFER_YILED    NUMERIC(30, 14),
    TENDER_UNIT          NUMERIC(30, 14),
    MAX_TENDER_UNIT      NUMERIC(30, 14),
    MAX_TENDER_PLUS      NUMERIC(30, 14),
    SERI_Y_N             VARCHAR(50),
    MAX_TOTAL_TENDER     NUMERIC(30, 14),
    MIN_PAMT             NUMERIC(30, 14),
    MAX_PAMT             NUMERIC(30, 14),
    MAX_TOTALAMT         NUMERIC(30, 14),
    CONTACT_NAME1        VARCHAR(50),
    CONTACT_PHONE1       VARCHAR(50),
    FAX1                 VARCHAR(50),
    CONTACT_NAME2        VARCHAR(50),
    CONTACT_PHONE2       VARCHAR(50),
    FAX2                 VARCHAR(50),
    RE_ACCOUNT_NAME      VARCHAR(200),
    RE_ACCOUNT_NO        VARCHAR(50),
    BANK_NAME            VARCHAR(50),
    PAY_NO               VARCHAR(50),
    REMARK               VARCHAR(200),
    CREATE_USER          VARCHAR(20)          not null,
    CREATE_DEPT          VARCHAR(32),
    CREATE_TIME          TIMESTAMP            not null,
    UPDATE_USER          VARCHAR(20)          not null,
    UPDATE_TIME          TIMESTAMP            not null,
    constraint PK_INTERF_CFETS_NEWBOND_ISSUE primary key (MARKET_ID)
);

comment on table INTERF_CFETS_NEWBOND_ISSUE is
'CFETS新债标的发行信息';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MARKET_ID is
'工具代码';

comment on column INTERF_CFETS_NEWBOND_ISSUE.FINPROD_ABBR is
'工具简称';

comment on column INTERF_CFETS_NEWBOND_ISSUE.FINPROD_NAME is
'工具全称';

comment on column INTERF_CFETS_NEWBOND_ISSUE.ISSUER is
'发行人';

comment on column INTERF_CFETS_NEWBOND_ISSUE.ISSUER_CODE is
'发行人21位码';

comment on column INTERF_CFETS_NEWBOND_ISSUE.ISSUE_CLASS is
'发行方式，报价发行/招标发行/簿记建档';

comment on column INTERF_CFETS_NEWBOND_ISSUE.TENDER_TYPE is
'招标方式，单一价格招标/数量招标/荷兰式招标/混合式招标';

comment on column INTERF_CFETS_NEWBOND_ISSUE.TENDER_TARGET is
'招标标的，利率(%)/价格(元)/利差(BP)';

comment on column INTERF_CFETS_NEWBOND_ISSUE.PERIOD is
'期限';

comment on column INTERF_CFETS_NEWBOND_ISSUE.PLAN_ISSUE_AMT is
'计划发行量';

comment on column INTERF_CFETS_NEWBOND_ISSUE.PAR_VALUE is
'面值';

comment on column INTERF_CFETS_NEWBOND_ISSUE.ISSUE_DATE is
'发行日';

comment on column INTERF_CFETS_NEWBOND_ISSUE.VDATE is
'起息日';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MDATE is
'到期日';

comment on column INTERF_CFETS_NEWBOND_ISSUE.PDATE is
'兑付日';

comment on column INTERF_CFETS_NEWBOND_ISSUE.COUPON_SPECIES is
'息票类型';

comment on column INTERF_CFETS_NEWBOND_ISSUE.YIELD is
'票面利率';

comment on column INTERF_CFETS_NEWBOND_ISSUE.ISSUE_PRICE is
'发行价格';

comment on column INTERF_CFETS_NEWBOND_ISSUE.PAY_CYCLE is
'付息频率,月付/季付/年付/半年付/到期一次';

comment on column INTERF_CFETS_NEWBOND_ISSUE.INT_RATE_ID is
'行情代码';

comment on column INTERF_CFETS_NEWBOND_ISSUE.RATE_GRADE is
'基准利率精度';

comment on column INTERF_CFETS_NEWBOND_ISSUE.INT_RATE_CONFTYPE is
'基础利率确定方式';

comment on column INTERF_CFETS_NEWBOND_ISSUE.BP is
'利差';

comment on column INTERF_CFETS_NEWBOND_ISSUE.FIRST_CONFIRM_DATE is
'首次利率确定日';

comment on column INTERF_CFETS_NEWBOND_ISSUE.FIRST_INT_PAYDATE is
'首次付息日';

comment on column INTERF_CFETS_NEWBOND_ISSUE.BASIS is
'计息基准';

comment on column INTERF_CFETS_NEWBOND_ISSUE.ORG_ID is
'主体评级机构';

comment on column INTERF_CFETS_NEWBOND_ISSUE.ORG_GRADE is
'主体评级等级';

comment on column INTERF_CFETS_NEWBOND_ISSUE.REFER_YIELD is
'参考收益率';

comment on column INTERF_CFETS_NEWBOND_ISSUE.ISSUE_STATUS is
'工具状态';

comment on column INTERF_CFETS_NEWBOND_ISSUE.FINPROD_TYPE is
'工具类型';

comment on column INTERF_CFETS_NEWBOND_ISSUE.RANGE_CIRCLE is
'流通范围';

comment on column INTERF_CFETS_NEWBOND_ISSUE.PAY_DATE is
'缴款日';

comment on column INTERF_CFETS_NEWBOND_ISSUE.CCY is
'币种';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MIN_PER_TAMT is
'每标位最低投标量';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MAX_PER_UTAMT is
'每标位最高投标量';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MIN_TAMT is
'最低标位';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MIN_T_REFER_YILED is
'最低标位参考收益率';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MAX_TAMT is
'最高标位';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MAX_T_REFER_YILED is
'最低标位参考收益率';

comment on column INTERF_CFETS_NEWBOND_ISSUE.TENDER_UNIT is
'投标步长';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MAX_TENDER_UNIT is
'最大投标标位数';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MAX_TENDER_PLUS is
'最大投标标位差';

comment on column INTERF_CFETS_NEWBOND_ISSUE.SERI_Y_N is
'连续投标限制';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MAX_TOTAL_TENDER is
'最高投标总量';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MIN_PAMT is
'单笔最低认购量';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MAX_PAMT is
'单笔最高认购量';

comment on column INTERF_CFETS_NEWBOND_ISSUE.MAX_TOTALAMT is
'最高认购总量';

comment on column INTERF_CFETS_NEWBOND_ISSUE.CONTACT_NAME1 is
'联系人1';

comment on column INTERF_CFETS_NEWBOND_ISSUE.CONTACT_PHONE1 is
'联系电话1';

comment on column INTERF_CFETS_NEWBOND_ISSUE.FAX1 is
'传真1';

comment on column INTERF_CFETS_NEWBOND_ISSUE.CONTACT_NAME2 is
'联系人1';

comment on column INTERF_CFETS_NEWBOND_ISSUE.CONTACT_PHONE2 is
'联系电话1';

comment on column INTERF_CFETS_NEWBOND_ISSUE.FAX2 is
'传真1';

comment on column INTERF_CFETS_NEWBOND_ISSUE.RE_ACCOUNT_NAME is
'收款账户名称';

comment on column INTERF_CFETS_NEWBOND_ISSUE.RE_ACCOUNT_NO is
'收款账户';

comment on column INTERF_CFETS_NEWBOND_ISSUE.BANK_NAME is
'汇入行名称';

comment on column INTERF_CFETS_NEWBOND_ISSUE.PAY_NO is
'支付系统清算行行号';

comment on column INTERF_CFETS_NEWBOND_ISSUE.REMARK is
'备注';

comment on column INTERF_CFETS_NEWBOND_ISSUE.CREATE_USER is
'创建人';

comment on column INTERF_CFETS_NEWBOND_ISSUE.CREATE_DEPT is
'创建部门';

comment on column INTERF_CFETS_NEWBOND_ISSUE.CREATE_TIME is
'创建时间';

comment on column INTERF_CFETS_NEWBOND_ISSUE.UPDATE_USER is
'更新人';

comment on column INTERF_CFETS_NEWBOND_ISSUE.UPDATE_TIME is
'更新时间';
