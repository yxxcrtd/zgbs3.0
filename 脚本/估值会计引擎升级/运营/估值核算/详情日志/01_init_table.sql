-- Create table
drop table SYS_BATCH_DETAIL_LOG;
create table SYS_BATCH_DETAIL_LOG
(
  exe_seqno      VARCHAR2(32) not null,
  group_id       VARCHAR2(32) not null,
  task_id        VARCHAR2(32) not null,
  task_bean_name VARCHAR2(200) not null,
  excute_date    DATE not null,
  class_name     VARCHAR2(200),
  method_name    VARCHAR2(200),
  args           CLOB,
  bookset_id     VARCHAR2(50),
  finprod_id     VARCHAR2(50),
  busi_id        VARCHAR2(2000),
  result_info    CLOB,
  detail_ex      CLOB,
  create_user    VARCHAR2(20) not null,
  create_time    TIMESTAMP(6) not null,
  update_user    VARCHAR2(20) not null,
  update_time    TIMESTAMP(6) not null
);
comment on  table SYS_BATCH_DETAIL_LOG
  is '批处理详情日志';

-- Add comments to the columns
comment on column SYS_BATCH_DETAIL_LOG.exe_seqno
  is '批处理任务执行信息ID';
comment on column SYS_BATCH_DETAIL_LOG.group_id
  is '批处理任务组ID';
comment on column SYS_BATCH_DETAIL_LOG.task_id
  is '批处理任务ID';
comment on column SYS_BATCH_DETAIL_LOG.task_bean_name
  is '批处理任务BEAN';
comment on column SYS_BATCH_DETAIL_LOG.excute_date
  is '执行日期';
comment on column SYS_BATCH_DETAIL_LOG.class_name
  is '执行类名';
comment on column SYS_BATCH_DETAIL_LOG.method_name
  is '执行方法';
comment on column SYS_BATCH_DETAIL_LOG.args
  is '执行参数';
comment on column SYS_BATCH_DETAIL_LOG.bookset_id
  is '账套ID';
comment on column SYS_BATCH_DETAIL_LOG.finprod_id
  is '金融产品ID';
comment on column SYS_BATCH_DETAIL_LOG.busi_id
  is '业务代码';
comment on column SYS_BATCH_DETAIL_LOG.result_info
  is '执行结果';
comment on column SYS_BATCH_DETAIL_LOG.detail_ex
  is '扩展明细信息';
comment on column SYS_BATCH_DETAIL_LOG.create_user
  is '创建人';
comment on column SYS_BATCH_DETAIL_LOG.create_time
  is '创建时间';
comment on column SYS_BATCH_DETAIL_LOG.update_user
  is '更新人';
comment on column SYS_BATCH_DETAIL_LOG.update_time
  is '更新时间';
-- Create/Recreate primary, unique and foreign key constraints
alter table SYS_BATCH_DETAIL_LOG
  add constraint PK_SYS_BATCH_DETAIL_LOG primary key (EXE_SEQNO);
