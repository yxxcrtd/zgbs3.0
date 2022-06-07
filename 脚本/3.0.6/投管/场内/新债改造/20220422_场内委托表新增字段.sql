
/**综合委托表新增字段**/
alter table T_INTEGRATED_ENTRUST add PROP_OWNER_TYPE VARCHAR2(32);
comment on column T_INTEGRATED_ENTRUST.PROP_OWNER_TYPE is '对手方报价所有者类型';

/**历史综合委托表新增字段**/
alter table T_INTEGRATED_ENTRUSTHISTORY add PROP_OWNER_TYPE VARCHAR2(32);
comment on column T_INTEGRATED_ENTRUSTHISTORY.PROP_OWNER_TYPE is '对手方报价所有者类型';