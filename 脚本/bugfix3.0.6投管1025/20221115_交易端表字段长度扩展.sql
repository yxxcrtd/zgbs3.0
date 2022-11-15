
--订单表-资产名称字段长度扩展
alter table T_ORDER modify STOCK_NAME VARCHAR2(200) ;
alter table T_ORDERHISTORY modify STOCK_NAME VARCHAR2(200) ;

alter table T_ORDER modify ACCOUNT_NAME VARCHAR2(200) ;
alter table T_ORDERHISTORY modify ACCOUNT_NAME VARCHAR2(200) ;

--委托表-资产名称字段长度扩展
alter table T_ENTRUST modify STOCK_NAME VARCHAR2(200) ;
alter table T_ENTRUSTHISTORY modify STOCK_NAME VARCHAR2(200) ;

alter table T_INTEGRATED_ENTRUST modify STOCK_NAME VARCHAR2(200) ;
alter table T_INTEGRATED_ENTRUSTHISTORY modify STOCK_NAME VARCHAR2(200) ;

--成交表-资产名称字段长度扩展
alter table T_TRADE modify STOCK_NAME VARCHAR2(200) ;
alter table T_TRADEHISTORY modify STOCK_NAME VARCHAR2(200) ;
