drop table MST_REPO_PRODUCT cascade constraints;

/*==============================================================*/
/* Table: MST_REPO_PRODUCT                                      */
/*==============================================================*/
create table MST_REPO_PRODUCT
(
    SEQ_NO               VARCHAR2(32)         not null,
    TRADE_MARKET         VARCHAR(50),
    MARKET_ID             VARCHAR2(50),
    TERM_TYPE            VARCHAR(50),
    REMARK               VARCHAR2(1000),
    CREATE_USER          VARCHAR2(20)         not null,
    CREATE_DEPT          VARCHAR2(32),
    CREATE_TIME          TIMESTAMP            not null,
    UPDATE_USER          VARCHAR2(20)         not null,
    UPDATE_TIME          TIMESTAMP            not null,
    constraint PK_REPO_PRODUCT_CONFIG primary key (SEQ_NO)
);

comment on table MST_REPO_PRODUCT is
    '回购市场标的';

comment on column MST_REPO_PRODUCT.SEQ_NO is
    '流水号';

comment on column MST_REPO_PRODUCT.TRADE_MARKET is
    '交易场所，上交所、深交所等';

comment on column MST_REPO_PRODUCT.MARKET_ID is
    '市场代码';

comment on column MST_REPO_PRODUCT.TERM_TYPE is
    '期限品种';

comment on column MST_REPO_PRODUCT.REMARK is
    '备注';

comment on column MST_REPO_PRODUCT.CREATE_USER is
    '创建人';

comment on column MST_REPO_PRODUCT.CREATE_DEPT is
    '创建部门';

comment on column MST_REPO_PRODUCT.CREATE_TIME is
    '创建时间';

comment on column MST_REPO_PRODUCT.UPDATE_USER is
    '更新人';

comment on column MST_REPO_PRODUCT.UPDATE_TIME is
    '更新时间';


insert into MST_REPO_PRODUCT values ('1','03','204001.SH','GC001','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('2','03','204002.SH','GC002','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('3','03','204003.SH','GC003','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('4','03','204004.SH','GC004','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('5','03','204007.SH','GC007','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('6','03','204014.SH','GC014','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('7','03','204028.SH','GC028','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('8','03','204091.SH','GC091','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('9','03','204182.SH','GC182','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('10','04','131810.SZ','R-001','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('11','04','131811.SZ','R-002','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('12','04','131800.SZ','R-003','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('13','04','131809.SZ','R-004','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('14','04','131801.SZ','R-007','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('15','04','131802.SZ','R-014','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('16','04','131803.SZ','R-028','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('17','04','131805.SZ','R-091','','system','',sysdate, 'system',sysdate);
insert into MST_REPO_PRODUCT values ('18','04','131806.SZ','R-182','','system','',sysdate, 'system',sysdate);
