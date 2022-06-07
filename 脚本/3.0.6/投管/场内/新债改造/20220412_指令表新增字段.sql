
/**指令表新增字段**/
alter table T_INSTRUCTION add TARGET_TYPE VARCHAR2(50);
comment on column T_INSTRUCTION.TARGET_TYPE is '目标类型';

alter table T_INSTRUCTION add IS_NO_GUARD VARCHAR2(50);
comment on column T_INSTRUCTION.IS_NO_GUARD is '是否非担保';

alter table T_INSTRUCTION add IS_NET_PRICE VARCHAR2(50);
comment on column T_INSTRUCTION.IS_NET_PRICE is '是否净价交易';


/**历史指令表新增字段**/
alter table T_INSTRUCTION_HISTORY add TARGET_TYPE VARCHAR2(50);
comment on column T_INSTRUCTION_HISTORY.TARGET_TYPE is '目标类型';

alter table T_INSTRUCTION_HISTORY add IS_NO_GUARD VARCHAR2(50);
comment on column T_INSTRUCTION_HISTORY.IS_NO_GUARD is '是否非担保';

alter table T_INSTRUCTION_HISTORY add IS_NET_PRICE VARCHAR2(50);
comment on column T_INSTRUCTION_HISTORY.IS_NET_PRICE is '是否净价交易';