------------------------add by weipeng on 20220121 ：账套科目表改造 start---------------------------
--BOK_SUBJECT表增加字段
ALTER TABLE BOK_SUBJECT ADD  SUBJECT_INTERNAL_CODE VARCHAR2(32);
COMMENT ON COLUMN BOK_SUBJECT.SUBJECT_INTERNAL_CODE IS '内部科目编码';
ALTER TABLE BOK_SUBJECT ADD  SUBJECT_HAND_FEL_CODE VARCHAR2(200);
COMMENT ON COLUMN BOK_SUBJECT.SUBJECT_HAND_FEL_CODE IS '辅助核算表达式';
ALTER TABLE BOK_SUBJECT ADD CCY VARCHAR2(32);
COMMENT ON COLUMN BOK_SUBJECT.CCY IS '币种';
ALTER TABLE BOK_SUBJECT ADD IS_ACC VARCHAR2(32);
COMMENT ON COLUMN BOK_SUBJECT.IS_ACC IS '是否核算项';
------------------------add by weipeng on 20220121：账套科目表改造  end---------------------------

------------------------add by weipeng on 20220301 增加内部科目记账辅助底层代码规则表start---------------------------
DROP TABLE BOK_SUBJECT_INTERNAL_RULE;
CREATE TABLE BOK_SUBJECT_INTERNAL_RULE
(
    BOOKSET_ID  VARCHAR2(50) NOT NULL,
    SUBJECT_INTERNAL_CODE VARCHAR2(32) NOT NULL,
    GEN_DETSUB_RULE VARCHAR2(32) ,
    AUTO_LEVEL   VARCHAR2(10) NOT NULL,
    CREATE_USER   VARCHAR2(20) NOT NULL,
    CREATE_DEPT   VARCHAR2(32),
    CREATE_TIME   TIMESTAMP(6) NOT NULL,
    UPDATE_USER   VARCHAR2(20) NOT NULL,
    UPDATE_TIME   TIMESTAMP(6) NOT NULL
);
-- ADD COMMENTS TO THE TABLE
COMMENT ON TABLE BOK_SUBJECT_INTERNAL_RULE
  IS '内部科目记账辅助底层代码规则';
-- ADD COMMENTS TO THE COLUMNS
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_RULE.BOOKSET_ID
  IS '账套代码';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_RULE.SUBJECT_INTERNAL_CODE
  IS '内部科目编码';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_RULE.GEN_DETSUB_RULE
  IS '底层科目号规则';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_RULE.AUTO_LEVEL
  IS '层级';
COMMENT ON COLUMN BOK_SCENESUBJECT.CREATE_USER
  IS '创建人';
COMMENT ON COLUMN BOK_SCENESUBJECT.CREATE_DEPT
  IS '创建部门';
COMMENT ON COLUMN BOK_SCENESUBJECT.CREATE_TIME
  IS '创建时间';
COMMENT ON COLUMN BOK_SCENESUBJECT.UPDATE_USER
  IS '更新人';
COMMENT ON COLUMN BOK_SCENESUBJECT.UPDATE_TIME
  IS '更新时间';
--增加主键
ALTER TABLE BOK_SUBJECT_INTERNAL_RULE ADD CONSTRAINT PK_BOK_SUBJECT_INTERNAL_RULE PRIMARY KEY (BOOKSET_ID, SUBJECT_INTERNAL_CODE);
------------------------add by weipeng on 20220301 增加内部科目记账辅助底层代码规则表 end---------------------------

------------------------add by weipeng on 20220301 增加内部科目表 start---------------------------
DROP TABLE BOK_SUBJECT_INTERNAL;
CREATE TABLE BOK_SUBJECT_INTERNAL
(
    SUBJECT_INTERNAL_CODE VARCHAR2(100) NOT NULL
    , SUBJECT_INTERNAL_NAME VARCHAR2(200) NOT NULL
    , SUBJECT_LEVEL VARCHAR2(20) NOT NULL
    , ACC_FLAG VARCHAR2(20) NOT NULL
    , SUBJECT_TYPE VARCHAR2(20) NOT NULL
    , CREATE_USER VARCHAR2(20)
    , CREATE_DEPT VARCHAR2(20)
    , CREATE_TIME TIMESTAMP
    , UPDATE_USER VARCHAR2(20)
    , UPDATE_TIME TIMESTAMP
    , CONSTRAINT BOK_SUBJECT_INTERNAL_PK PRIMARY KEY
    (
     SUBJECT_INTERNAL_CODE
        )
    ENABLE
);

COMMENT ON TABLE BOK_SUBJECT_INTERNAL IS '内部科目';

COMMENT ON COLUMN BOK_SUBJECT_INTERNAL.SUBJECT_INTERNAL_CODE IS '内部科目编码';

COMMENT ON COLUMN BOK_SUBJECT_INTERNAL.SUBJECT_INTERNAL_NAME IS '内部科目名称';

COMMENT ON COLUMN BOK_SUBJECT_INTERNAL.SUBJECT_LEVEL IS '科目级别';

COMMENT ON COLUMN BOK_SUBJECT_INTERNAL.ACC_FLAG IS '是否核算项';

COMMENT ON COLUMN BOK_SUBJECT_INTERNAL.SUBJECT_TYPE IS '核算属性:资产类
负债类
共同类
所有者权益类
损益类';
------------------------add by weipeng on 20220301 增加内部科目表 end---------------------------

------------------------modify by sunshuaishuai on 20220408 内部科目记账辅助总表start---------------------------
DROP TABLE BOK_SUBJECT_INTERNAL_HAND;
CREATE TABLE BOK_SUBJECT_INTERNAL_HAND
(
	BOOKSET_ID VARCHAR2(50),
    SUBJECT_INTERNAL_CODE VARCHAR2(100),
    HAND_CODE VARCHAR2(50) NOT null,
    STATUS char(1),
    CREATE_USER VARCHAR2(20),
    CREATE_DEPT VARCHAR2(20),
    CREATE_TIME TIMESTAMP,
    UPDATE_USER VARCHAR2(20),
    UPDATE_TIME TIMESTAMP,
    CONSTRAINT "PK_BOK_SUBJECT_INTERNAL_HAND" PRIMARY KEY ("BOOKSET_ID", "SUBJECT_INTERNAL_CODE","HAND_CODE")
);

COMMENT ON TABLE BOK_SUBJECT_INTERNAL_HAND IS '内部科目记账辅助总表';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_HAND.BOOKSET_ID IS '账套代码';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_HAND.SUBJECT_INTERNAL_CODE IS '内部科目编码';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_HAND.HAND_CODE IS '辅助核算项编码';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_HAND.STATUS IS '是否启用:是:Y 否：N';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_HAND.CREATE_USER IS '创建人';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_HAND.CREATE_DEPT IS '创建部门';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_HAND.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_HAND.UPDATE_USER IS '更新人';
COMMENT ON COLUMN BOK_SUBJECT_INTERNAL_HAND.UPDATE_TIME IS '更新时间';

------------------------modify by sunshuaishuai on 20220408 增加内部科目记账辅助底层代码规则表 end---------------------------

------------------------add by weipeng on 20220308 会计元素表字段备注变更 start---------------------------
comment on column BOK_TABLEDICT.dict_type is '元素数据类型: P 下拉框 N 数值型 F 辅助核算项';
------------------------add by weipeng on 20220308 会计元素表字段备注变更 end---------------------------

------------------------add by weipeng on 20220309 场景科目关系配置表主键修改 start---------------------------
delete from BOK_SCENESUBJECT where FORM_ELE is null;
alter table BOK_SCENESUBJECT  DROP PRIMARY KEY ;
alter table BOK_SCENESUBJECT add constraint PK_BOK_SCENESUBJECT primary key (BOOKSET_ID, SCENE_NODE_ID, SUBJECT_NO, FORM_ELE, CD_FLAG, BOK_DETAIL_ID);
------------------------add by weipeng on 20220309 场景科目关系配置表主键修改 end---------------------------

------------------------add by weipeng on 20220323 修改BOK_ASSET_MARKET_INFO表字段MARKET_CODE长度与FINPROD_ID长度一致，跑批报错 start---------------------------
alter table BOK_ASSET_MARKET_INFO MODIFY MARKET_CODE VARCHAR2(50);
------------------------add by weipeng on 20220323 修改BOK_ASSET_MARKET_INFO表字段MARKET_CODE长度与FINPROD_ID长度一致，跑批报错 end---------------------------

------------------------add by weipeng on 20220323 现金管理类改造，增加资产估值方案信息表 start---------------------------
DROP TABLE BOK_FINPROD_SCHEME_INFO;
create table BOK_FINPROD_SCHEME_INFO
(
    bookset_id  VARCHAR2(50) not null,
    finprod_id        VARCHAR2(50) not null,
    trade_market      VARCHAR2(50),
    sec_type          VARCHAR2(50),
    type1   VARCHAR2(50),
    inv_aim           VARCHAR2(50),
    scheme_detail_id  VARCHAR2(32) not null,
    scheme_id         VARCHAR2(32) not null,
    valuation_method  VARCHAR2(50),
    valuation_price   VARCHAR2(50),
    valuation_price_2 VARCHAR2(50),
    quota_digit       NUMBER(10),
    valuation_date    VARCHAR2(50),
    vdate             DATE,
    mdate             DATE,
    valuation_source  VARCHAR2(50),
    price_tail_mode   VARCHAR2(50),
    price_class       VARCHAR2(50),
    create_user       VARCHAR2(20) not null,
    create_dept       VARCHAR2(32),
    create_time       TIMESTAMP(6) not null,
    update_user       VARCHAR2(20) not null,
    update_time       TIMESTAMP(6) not null
);
-- Add comments to the table
comment on table BOK_FINPROD_SCHEME_INFO
  is '资产估值方案信息';
-- Add comments to the columns
comment on column BOK_FINPROD_SCHEME_INFO.bookset_id
  is '账套代码';
comment on column BOK_FINPROD_SCHEME_INFO.finprod_id
  is '金融产品代码';
comment on column BOK_FINPROD_SCHEME_INFO.trade_market
  is '交易市场';
comment on column BOK_FINPROD_SCHEME_INFO.sec_type
  is '证券品种';
comment on column BOK_FINPROD_SCHEME_INFO.type1
  is '资产类型';
comment on column BOK_FINPROD_SCHEME_INFO.inv_aim
  is '投资目的';
comment on column BOK_FINPROD_SCHEME_INFO.scheme_detail_id
  is '方案明细代码';
comment on column BOK_FINPROD_SCHEME_INFO.scheme_id
  is '方案代码';
comment on column BOK_FINPROD_SCHEME_INFO.valuation_method
  is '估值方法';
comment on column BOK_FINPROD_SCHEME_INFO.valuation_price
  is '估值市价';
comment on column BOK_FINPROD_SCHEME_INFO.valuation_price_2
  is '估值第二市价';
comment on column BOK_FINPROD_SCHEME_INFO.quota_digit
  is '行情位数';
comment on column BOK_FINPROD_SCHEME_INFO.valuation_date
  is '行情日期';
comment on column BOK_FINPROD_SCHEME_INFO.vdate
  is '开始日';
comment on column BOK_FINPROD_SCHEME_INFO.mdate
  is '结束日';
comment on column BOK_FINPROD_SCHEME_INFO.valuation_source
  is '估值来源';
comment on column BOK_FINPROD_SCHEME_INFO.price_tail_mode
  is '行情尾数处理方式';
comment on column BOK_FINPROD_SCHEME_INFO.price_class
  is '价格类型';
comment on column BOK_FINPROD_SCHEME_INFO.create_user
  is '创建人';
comment on column BOK_FINPROD_SCHEME_INFO.create_dept
  is '创建部门';
comment on column BOK_FINPROD_SCHEME_INFO.create_time
  is '创建时间';
comment on column BOK_FINPROD_SCHEME_INFO.update_user
  is '更新人';
comment on column BOK_FINPROD_SCHEME_INFO.update_time
  is '更新时间';

--增加主键
alter table BOK_FINPROD_SCHEME_INFO add constraint PK_BOK_FINPROD_SCHEME_INFO primary key (BOOKSET_ID,FINPROD_ID);
------------------------add by weipeng on 20220323 现金管理类改造，增加资产估值方案信息表 end---------------------------

--------------------add by sunshuaishuai 20220407 交易表中添加资金账户字段 start-------------------------------------
/*交易表添加资金账户字段*/
ALTER TABLE TRD_PRODUCT_DEAL ADD MY_ACCT_NO VARCHAR2(50);
COMMENT ON COLUMN TRD_PRODUCT_DEAL.MY_ACCT_NO IS '资金账户';
--------------------add by sunshuaishuai 20220407 交易表中添加资金账户字段 end---------------------------------------

--------------------add by sunshuaishuai 20220408 修改账套科目表中辅助核算表达式字段长度 start-------------------------------------
ALTER TABLE BOK_SUBJECT MODIFY SUBJECT_HAND_FEL_CODE VARCHAR2(500);
--------------------add by sunshuaishuai 20220408 修改账套科目表中辅助核算表达式字段长度 end---------------------------------------
DROP TABLE BOK_SUBJECT_HAND_VALUE;
 CREATE TABLE BOK_SUBJECT_HAND_VALUE
(
  BOOKSET_ID VARCHAR2(50)
, SUBJECT_NO VARCHAR2(32)
, SUBJECT_INTERNAL_CODE VARCHAR2(32)
, HAND_CODE VARCHAR2(32)
, HAND_CODE_VALUE VARCHAR2(32)
, CREATE_USER VARCHAR2(20)
, CREATE_DEPT VARCHAR2(20)
, CREATE_TIME TIMESTAMP
, UPDATE_USER VARCHAR2(20)
, UPDATE_TIME TIMESTAMP
);

COMMENT ON TABLE BOK_SUBJECT_HAND_VALUE IS '科目辅助配置';
COMMENT ON COLUMN BOK_SUBJECT_HAND_VALUE.BOOKSET_ID IS '账套代码';
COMMENT ON COLUMN BOK_SUBJECT_HAND_VALUE.SUBJECT_INTERNAL_CODE IS '内部科目编码';
COMMENT ON COLUMN BOK_SUBJECT_HAND_VALUE.HAND_CODE IS '辅助核算项';
COMMENT ON COLUMN BOK_SUBJECT_HAND_VALUE.HAND_CODE_VALUE IS '辅助核算项值';
COMMENT ON COLUMN BOK_SUBJECT_HAND_VALUE.SUBJECT_NO IS '科目号';
COMMENT ON COLUMN BOK_SUBJECT_HAND_VALUE.CREATE_USER
  IS '创建人';
COMMENT ON COLUMN BOK_SUBJECT_HAND_VALUE.CREATE_DEPT
  IS '创建部门';
COMMENT ON COLUMN BOK_SUBJECT_HAND_VALUE.CREATE_TIME
  IS '创建时间';
COMMENT ON COLUMN BOK_SUBJECT_HAND_VALUE.UPDATE_USER
  IS '更新人';
COMMENT ON COLUMN BOK_SUBJECT_HAND_VALUE.UPDATE_TIME
  IS '更新时间';

ALTER TABLE BOK_SUBJECT_HAND_VALUE
ADD CONSTRAINT BOK_SUBJECT_HAND_VALUE_PK PRIMARY KEY (BOOKSET_ID,SUBJECT_NO,SUBJECT_INTERNAL_CODE,HAND_CODE,HAND_CODE_VALUE);
