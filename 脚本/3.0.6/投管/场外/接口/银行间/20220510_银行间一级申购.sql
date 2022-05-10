create table INTERF_CSTP_BJFXJG
(
    INSTRUMENT_ID                  VARCHAR2(50),
    INSTRUMENT_SHORT_NAME          VARCHAR2(110),
    INSTRUMENT_FULL_NAME           VARCHAR2(300),
    ISSUER                         VARCHAR2(300),
    ISSUER_21_FIRM_ID              VARCHAR2(100),
    ISSUE_PRICE                    NUMBER(10, 4),
    SPREAD                         NUMBER(10, 2),
    COUPON_RATE                    NUMBER(10, 4),
    REAL_ISSUESIZE                 NUMBER(10, 1),
    INSTRUMENT_STATUS              CHAR,
    INSTRUMENT_TYPE                VARCHAR2(10),
    FAILURE_REASONS                VARCHAR2(100),
    MESSAGE_TYPE                   VARCHAR2(100),
    ISSUE_METHOD                   CHAR,
    COUPON_RATE_TYPE               CHAR,
    DATA_CATEGORY_INDICATOR        CHAR,
    FULL_CHINESE_NAME_OF_SUBSCRIBE VARCHAR2(300),
    SUBSCRIBE_INSTITUTION_ID       VARCHAR2(100),
    SUBSCRIPTION_AMOUNT            NUMBER(10, 1),
    ACTUAL_FEE                     NUMBER(10, 2),
    SUBSCRIPTION_TIME              VARCHAR2(20),
    SUBMIT_USER                    VARCHAR2(100),
    CURRENCY                       VARCHAR2(50)
)
;

comment on table INTERF_CSTP_BJFXJG is '报价发行结果接口表（一级申购cstp下行）'
;

comment on column INTERF_CSTP_BJFXJG.INSTRUMENT_ID is '工具代码'
;

comment on column INTERF_CSTP_BJFXJG.INSTRUMENT_SHORT_NAME is '工具简称'
;

comment on column INTERF_CSTP_BJFXJG.INSTRUMENT_FULL_NAME is '工具全称'
;

comment on column INTERF_CSTP_BJFXJG.ISSUER is '发行人'
;

comment on column INTERF_CSTP_BJFXJG.ISSUER_21_FIRM_ID is '发行人机构 21 位码'
;

comment on column INTERF_CSTP_BJFXJG.ISSUE_PRICE is '发行价格（元）'
;

comment on column INTERF_CSTP_BJFXJG.SPREAD is '利差（BP）'
;

comment on column INTERF_CSTP_BJFXJG.COUPON_RATE is '票面利率'
;

comment on column INTERF_CSTP_BJFXJG.REAL_ISSUESIZE is '实际发行量（亿元）'
;

comment on column INTERF_CSTP_BJFXJG.INSTRUMENT_STATUS is '工具状态 1-发行成功 2-发行失败'
;

comment on column INTERF_CSTP_BJFXJG.INSTRUMENT_TYPE is '工具类型
CD-同业存单
CFETS;K 2409—2021
265
工具类型 CD_FTA-自贸区同业存单
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
21-定向工具
14-公司短期融资券
49-二级资本工具
50-保险公司资本补充债
45-资产管理公司金融债'
;

comment on column INTERF_CSTP_BJFXJG.FAILURE_REASONS is '失败原因'
;

comment on column INTERF_CSTP_BJFXJG.MESSAGE_TYPE is '消息类型
2-发行结果信息（发行人接收）
3-投标结果信息（投资人接收）
发行失败时，消息类型默认为发行结果
信息'
;

comment on column INTERF_CSTP_BJFXJG.ISSUE_METHOD is '发行方式'
;

comment on column INTERF_CSTP_BJFXJG.COUPON_RATE_TYPE is '息票类型'
;

comment on column INTERF_CSTP_BJFXJG.DATA_CATEGORY_INDICATOR is '数据类型标识 0-本方数据 2-关联机构数据'
;

comment on column INTERF_CSTP_BJFXJG.FULL_CHINESE_NAME_OF_SUBSCRIBE is '申购机构'
;

comment on column INTERF_CSTP_BJFXJG.SUBSCRIBE_INSTITUTION_ID is '申购机构 21 位码'
;

comment on column INTERF_CSTP_BJFXJG.SUBSCRIPTION_AMOUNT is '认购量（亿元）'
;

comment on column INTERF_CSTP_BJFXJG.ACTUAL_FEE is '应缴纳金额（元）'
;

comment on column INTERF_CSTP_BJFXJG.SUBSCRIPTION_TIME is '认购时间 格式：yyyy-mm-dd HH:MM:SS'
;

comment on column INTERF_CSTP_BJFXJG.SUBMIT_USER is '提交用户'
;

comment on column INTERF_CSTP_BJFXJG.CURRENCY is '币种'
;


