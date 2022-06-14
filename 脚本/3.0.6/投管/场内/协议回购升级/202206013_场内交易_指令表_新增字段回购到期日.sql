
/**指令表新增字段**/
alter table T_INSTRUCTION add REPO_MDATE DATE;
comment on column T_INSTRUCTION.REPO_MDATE is '回购到期日';

/**历史指令表新增字段**/
alter table T_INSTRUCTION_HISTORY add REPO_MDATE DATE;
comment on column T_INSTRUCTION_HISTORY.REPO_MDATE is '回购到期日';