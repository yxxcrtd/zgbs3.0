-- 委托投标标位表修改主键
update  ETR_ENTRUST_BID_PRICE  set PORTFOLIO_ID = rownum where PORTFOLIO_ID is null;
alter table ETR_ENTRUST_BID_PRICE MODIFY PORTFOLIO_ID VARCHAR(32) not null ;
alter table ETR_ENTRUST_BID_PRICE drop constraint PK_ETR_ENTRUST_BID_PRICE CASCADE DROP index;
alter table ETR_ENTRUST_BID_PRICE add constraint PK_ETR_ENTRUST_BID_PRICE primary key (ENTRUST_ID, BID_PRICE, PORTFOLIO_ID) enable validate;
-- 委托投标标位流程表修改主键
update  WFL_ETR_ENTRUST_BID_PRICE  set PORTFOLIO_ID = rownum where PORTFOLIO_ID is null;
alter table WFL_ETR_ENTRUST_BID_PRICE MODIFY PORTFOLIO_ID VARCHAR(32) not null ;
alter table WFL_ETR_ENTRUST_BID_PRICE drop constraint PK_WFL_ETR_ENTRUST_BID_PRICE CASCADE DROP index;
alter table WFL_ETR_ENTRUST_BID_PRICE add constraint PK_WFL_ETR_ENTRUST_BID_PRICE primary key (ENTRUST_ID, BID_PRICE, PORTFOLIO_ID) enable validate;