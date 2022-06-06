
/**指令回购质押品信息表新增字段**/
alter table T_INSTRUCTION_PLEDGE add DISPLACE_DIR VARCHAR2(50);
comment on column T_INSTRUCTION_PLEDGE.DISPLACE_DIR is '置换方向';

/**历史指令回购质押品信息表新增字段**/
alter table T_INSTRUCTION_PLEDGE_HISTORY add DISPLACE_DIR VARCHAR2(50);
comment on column T_INSTRUCTION_PLEDGE_HISTORY.DISPLACE_DIR is '置换方向';