DROP TABLE SZD_SJSFW;

create table SZD_SJSFW
(
   SEQNO                VARCHAR2(32)                   not null,
   IMPORT_DATE          DATE                           null,
   DATA_FILE            VARCHAR2(256)                  null,
   IS_SETTLE_TARGET     VARCHAR2(50)                   null,
   FWSJLB               VARCHAR2(2)                    null,
   FWJSZH               VARCHAR2(6)                    null,
   FWBFZH               VARCHAR2(25)                   null,
   FWTGDY               VARCHAR2(6)                    null,
   FWZQDM               VARCHAR2(8)                    null,
   FWZQZH               VARCHAR2(20)                   null,
   FWGFXZ               VARCHAR2(2)                    null,
   FWLTLX               VARCHAR2(1)                    null,
   FWMRJE               NUMBER(17,2)                   null,
   FWMCJE               NUMBER(17,2)                   null,
   FWZJYE               NUMBER(17,2)                   null,
   FWSL1                NUMBER(17,2)                   null,
   FWSL2                NUMBER(17,2)                   null,
   FWCLRQ               VARCHAR2(8)                    null,
   FWFSRQ               VARCHAR2(8)                    null,
   FWZYSM               VARCHAR2(100)                  null,
   FWBZSM               VARCHAR2(120)                  null,
   FWBYBZ               VARCHAR2(1)                    null,
   CREATE_USER          VARCHAR2(20)                   not null,
   CREATE_DEPT          VARCHAR2(32)                   null,
   CREATE_TIME          TIMESTAMP                      not null,
   UPDATE_USER          VARCHAR2(20)                   not null,
   UPDATE_TIME          TIMESTAMP                      not null,
   constraint PK_SZD_SJSFW primary key (SEQNO)
);

comment on table SZD_SJSFW is '深中登SJSFW';
comment on column SZD_SJSFW.SEQNO is '流水号';
comment on column SZD_SJSFW.IMPORT_DATE is '导入日期';
comment on column SZD_SJSFW.DATA_FILE is '数据文件';
comment on column SZD_SJSFW.IS_SETTLE_TARGET is '是否本次数据清算对象';
comment on column SZD_SJSFW.FWSJLB is '数据类别';
comment on column SZD_SJSFW.FWJSZH is '结算账号';
comment on column SZD_SJSFW.FWBFZH is '结算账号';
comment on column SZD_SJSFW.FWTGDY is '托管单元';
comment on column SZD_SJSFW.FWZQDM is '证券代码';
comment on column SZD_SJSFW.FWZQZH is '证券账户';
comment on column SZD_SJSFW.FWGFXZ is '股份性质';
comment on column SZD_SJSFW.FWLTLX is '流通类型';
comment on column SZD_SJSFW.FWMRJE is '买入金额';
comment on column SZD_SJSFW.FWMCJE is '卖出金额';
comment on column SZD_SJSFW.FWZJYE is '资金余额';
comment on column SZD_SJSFW.FWSL1 is '数量1';
comment on column SZD_SJSFW.FWSL2 is '数量2';
comment on column SZD_SJSFW.FWCLRQ is '处理日期';
comment on column SZD_SJSFW.FWFSRQ is '发送日期';
comment on column SZD_SJSFW.FWZYSM is '摘要说明';
comment on column SZD_SJSFW.FWBZSM is '备注说明';
comment on column SZD_SJSFW.FWBYBZ is '备用标志';
comment on column SZD_SJSFW.CREATE_USER is '创建人';
comment on column SZD_SJSFW.CREATE_DEPT is '创建部门';
comment on column SZD_SJSFW.CREATE_TIME is '创建时间';
comment on column SZD_SJSFW.UPDATE_USER is '更新人';
comment on column SZD_SJSFW.UPDATE_TIME is '更新时间';
