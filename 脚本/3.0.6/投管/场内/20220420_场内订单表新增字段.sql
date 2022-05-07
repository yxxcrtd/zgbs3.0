
/**订单表新增字段**/
alter table T_ORDER add TARGET_TYPE VARCHAR2(50);
comment on column T_ORDER.TARGET_TYPE is '目标类型';

alter table T_ORDER add IS_NO_GUARD VARCHAR2(50);
comment on column T_ORDER.IS_NO_GUARD is '是否非担保';

alter table T_ORDER add IS_FULL_PRICE VARCHAR2(50);
comment on column T_ORDER.IS_FULL_PRICE is '是否全价';


/**历史订单表新增字段**/
alter table T_ORDERHISTORY add TARGET_TYPE VARCHAR2(50);
comment on column T_ORDERHISTORY.TARGET_TYPE is '目标类型';

alter table T_ORDERHISTORY add IS_NO_GUARD VARCHAR2(50);
comment on column T_ORDERHISTORY.IS_NO_GUARD is '是否非担保';

alter table T_ORDERHISTORY add IS_FULL_PRICE VARCHAR2(50);
comment on column T_ORDERHISTORY.IS_FULL_PRICE is '是否全价';