
/**成交数据明细表新增字段**/

alter table SETTLE_TRANS_DATA add NET_COMMISSION_AMT NUMBER(30, 2);
comment on column SETTLE_TRANS_DATA.NET_COMMISSION_AMT is '净佣金';


