alter table ETR_PLEDGE drop column CLOST_NUMBER ;
alter table ETR_PLEDGE add CLOST_NUMBER NUMBER(30, 2);
comment on column ETR_PLEDGE.CLOST_NUMBER is '关闭额度';