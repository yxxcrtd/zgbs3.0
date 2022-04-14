
alter table etr_entrust_add ADD (PRICE_TYPE VARCHAR2(50));

comment on column etr_entrust_add.PRICE_TYPE is '计价方式';