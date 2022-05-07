alter table wfl_etr_entrust_add drop column BUSI_TYPE_POSITION;
alter table wfl_etr_entrust_add add BUSI_TYPE_POSITION VARCHAR2(10);
comment on column wfl_etr_entrust_add.BUSI_TYPE_POSITION is '投管业务类型';

comment on column etr_entrust_add.BUSI_TYPE_POSITION is '投管业务类型';