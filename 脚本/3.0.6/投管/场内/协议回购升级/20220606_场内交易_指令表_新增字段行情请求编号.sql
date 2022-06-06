
/**指令表新增字段**/
alter table T_INSTRUCTION add QUOTE_REQUEST_ID VARCHAR2(50);
comment on column T_INSTRUCTION.QUOTE_REQUEST_ID is '行情请求编号';

/**历史指令表新增字段**/
alter table T_INSTRUCTION_HISTORY add QUOTE_REQUEST_ID VARCHAR2(50);
comment on column T_INSTRUCTION_HISTORY.QUOTE_REQUEST_ID is '行情请求编号';