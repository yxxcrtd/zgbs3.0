-- 汇总指令关系表修改主键
alter table TOTAL_ETR_ENTRUST_REL MODIFY ENTRUST_ID VARCHAR(32) not null ;
alter table TOTAL_ETR_ENTRUST_REL drop constraint PK_TOTAL_ETR_ENTRUST_REL CASCADE DROP index;
alter table TOTAL_ETR_ENTRUST_REL add constraint PK_TOTAL_ETR_ENTRUST_REL primary key (ENTRUST_ID) enable validate;