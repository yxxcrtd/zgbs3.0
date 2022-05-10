ALTER TABLE BID_ORDER_BID_PRICE DROP COLUMN dip_Type;
alter table BID_ORDER add (dip_Type varchar2(5));
comment on column BID_ORDER.dip_Type is '分配方式';

ALTER TABLE WFL_BID_ORDER DROP COLUMN dip_Type;
alter table WFL_BID_ORDER add (dip_Type varchar2(5));
comment on column WFL_BID_ORDER.dip_Type is '分配方式';