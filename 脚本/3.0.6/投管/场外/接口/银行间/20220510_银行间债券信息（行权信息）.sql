create table INTERF_RDI_BOND_EXERCISE
(
    SECURITY_ID       VARCHAR2(20),
    EVENT_TYPE        VARCHAR2(10),
    EVENT_DATE        CHAR(8),
    EVENT_PX          NUMBER(10, 2),
    EVENT_QTY         NUMBER(10, 2),
    STIPULATION_VALUE NUMBER(6, 2)
)
/

comment on table INTERF_RDI_BOND_EXERCISE is '银行间债券行权接口表'
/

comment on column INTERF_RDI_BOND_EXERCISE.SECURITY_ID is '债券代码'
/

comment on column INTERF_RDI_BOND_EXERCISE.EVENT_TYPE is '行权类型 2-Call 表示可提前兑付，1-Put 表示可提前赎回'
/

comment on column INTERF_RDI_BOND_EXERCISE.EVENT_DATE is '行权日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_EXERCISE.EVENT_PX is '行权价格（元）'
/

comment on column INTERF_RDI_BOND_EXERCISE.EVENT_QTY is '行权量（亿）'
/

comment on column INTERF_RDI_BOND_EXERCISE.STIPULATION_VALUE is '行权后利率/基准利差（%）单位：%，精度：6 浮动时即为利率，固定为利差'
/


