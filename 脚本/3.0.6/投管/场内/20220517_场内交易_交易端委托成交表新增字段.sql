
/**综合委托表新增字段**/
alter table T_INTEGRATED_ENTRUST add COUNTER_NAME VARCHAR2(128);
comment on column T_INTEGRATED_ENTRUST.COUNTER_NAME is '交易对手名称';

alter table T_INTEGRATED_ENTRUST add PROP_ACCT_NO VARCHAR2(50);
comment on column T_INTEGRATED_ENTRUST.PROP_ACCT_NO is '对手资金账户';

alter table T_INTEGRATED_ENTRUST add PROP_ACCT_NAME VARCHAR2(128);
comment on column T_INTEGRATED_ENTRUST.PROP_ACCT_NAME is '对手资金账户名称';

/**历史综合委托表新增字段**/
alter table T_INTEGRATED_ENTRUSTHISTORY add COUNTER_NAME VARCHAR2(128);
comment on column T_INTEGRATED_ENTRUSTHISTORY.COUNTER_NAME is '交易对手名称';

alter table T_INTEGRATED_ENTRUSTHISTORY add PROP_ACCT_NO VARCHAR2(50);
comment on column T_INTEGRATED_ENTRUSTHISTORY.PROP_ACCT_NO is '对手资金账户';

alter table T_INTEGRATED_ENTRUSTHISTORY add PROP_ACCT_NAME VARCHAR2(128);
comment on column T_INTEGRATED_ENTRUSTHISTORY.PROP_ACCT_NAME is '对手资金账户名称';

/**成交表新增字段**/
alter table T_TRADE add COUNTER_ID VARCHAR2(50);
comment on column T_TRADE.COUNTER_ID is '交易对手';

alter table T_TRADE add COUNTER_NAME VARCHAR2(128);
comment on column T_TRADE.COUNTER_NAME is '交易对手名称';

alter table T_TRADE add PROP_ACCT_NO VARCHAR2(50);
comment on column T_TRADE.PROP_ACCT_NO is '对手资金账户';

alter table T_TRADE add PROP_ACCT_NAME VARCHAR2(128);
comment on column T_TRADE.PROP_ACCT_NAME is '对手资金账户名称';

/**历史成交表新增字段**/
alter table T_TRADEHISTORY add COUNTER_ID VARCHAR2(50);
comment on column T_TRADEHISTORY.COUNTER_ID is '交易对手';

alter table T_TRADEHISTORY add COUNTER_NAME VARCHAR2(128);
comment on column T_TRADEHISTORY.COUNTER_NAME is '交易对手名称';

alter table T_TRADEHISTORY add PROP_ACCT_NO VARCHAR2(50);
comment on column T_TRADEHISTORY.PROP_ACCT_NO is '对手资金账户';

alter table T_TRADEHISTORY add PROP_ACCT_NAME VARCHAR2(128);
comment on column T_TRADEHISTORY.PROP_ACCT_NAME is '对手资金账户名称';