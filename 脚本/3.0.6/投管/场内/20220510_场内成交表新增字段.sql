
/**成交表新增字段**/
alter table T_TRADE add IS_FULL_PRICE VARCHAR2(50);
comment on column T_TRADE.IS_FULL_PRICE is '是否全价（Y:是 ， N：否）';

alter table T_TRADE add NET_PRICE NUMBER(20,4);
comment on column T_TRADE.NET_PRICE is '净价';

alter table T_TRADE add NET_BALANCE NUMBER(20,4);
comment on column T_TRADE.NET_BALANCE is '净价金额';


/**历史成交表新增字段**/
alter table T_TRADEHISTORY add IS_FULL_PRICE VARCHAR2(50);
comment on column T_TRADEHISTORY.IS_FULL_PRICE is '是否全价（Y:是 ， N：否）';

alter table T_TRADEHISTORY add NET_PRICE NUMBER(20,4);
comment on column T_TRADEHISTORY.NET_PRICE is '净价';

alter table T_TRADEHISTORY add NET_BALANCE NUMBER(20,4);
comment on column T_TRADEHISTORY.NET_BALANCE is '净价金额';