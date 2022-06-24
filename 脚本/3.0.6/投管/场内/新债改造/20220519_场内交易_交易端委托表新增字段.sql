
/**综合委托表新增字段**/
declare columnExistedCount number;
begin
    select  count(1) into columnExistedCount  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUST')  and t.column_name = upper('TRADE_NAME');
    if columnExistedCount = 0 then
        execute immediate
            'alter table T_INTEGRATED_ENTRUST add TRADE_NAME VARCHAR2(128)';
    end if;
end;

declare columnExistedCount number;
begin
    select  count(1) into columnExistedCount  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUST')  and t.column_name = upper('PROP_TRADE_NAME');
    if columnExistedCount = 0 then
        execute immediate
            'alter table T_INTEGRATED_ENTRUST add PROP_TRADE_NAME VARCHAR2(128)';
    end if;
end;

/**历史综合委托表新增字段**/
declare columnExistedCount number;
begin
    select  count(1) into columnExistedCount  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUSTHISTORY')  and t.column_name = upper('TRADE_NAME');
    if columnExistedCount = 0 then
        execute immediate
            'alter table T_INTEGRATED_ENTRUSTHISTORY add TRADE_NAME VARCHAR2(128)';
    end if;
end;

declare columnExistedCount number;
begin
    select  count(1) into columnExistedCount  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUSTHISTORY')  and t.column_name = upper('PROP_TRADE_NAME');
    if columnExistedCount = 0 then
        execute immediate
            'alter table T_INTEGRATED_ENTRUSTHISTORY add PROP_TRADE_NAME VARCHAR2(128)';
    end if;
end;


comment on column T_INTEGRATED_ENTRUST.TRADE_NAME is '本方方交易员名称';
comment on column T_INTEGRATED_ENTRUST.PROP_TRADE_NAME is '对手方交易员名称';
comment on column T_INTEGRATED_ENTRUSTHISTORY.TRADE_NAME is '本方方交易员名称';
comment on column T_INTEGRATED_ENTRUSTHISTORY.PROP_TRADE_NAME is '对手方交易员名称';