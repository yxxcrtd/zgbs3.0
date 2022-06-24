
declare
     columnExistedCount1 number;
     columnExistedCount2 number;
     columnExistedCount3 number;
     columnExistedCount4 number;
begin
    select  count(1) into columnExistedCount2  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUST')  and t.column_name = upper('PROP_TRADE_NAME');
    select  count(1) into columnExistedCount1  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUST')  and t.column_name = upper('TRADE_NAME');
    select  count(1) into columnExistedCount3  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUSTHISTORY')  and t.column_name = upper('TRADE_NAME');
    select  count(1) into columnExistedCount4  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUSTHISTORY')  and t.column_name = upper('PROP_TRADE_NAME');

    if columnExistedCount1 = 0 then
        execute immediate 'alter table T_INTEGRATED_ENTRUST add TRADE_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_INTEGRATED_ENTRUST.TRADE_NAME is ''本方方交易员名称''';
    end if;
    if columnExistedCount2 = 0 then
        execute immediate 'alter table T_INTEGRATED_ENTRUST add PROP_TRADE_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_INTEGRATED_ENTRUST.PROP_TRADE_NAME is ''对手方交易员名称''';
    end if;

    if columnExistedCount3 = 0 then
        execute immediate 'alter table T_INTEGRATED_ENTRUSTHISTORY add TRADE_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_INTEGRATED_ENTRUSTHISTORY.TRADE_NAME is ''本方方交易员名称''';
    end if;
    if columnExistedCount4 = 0 then
        execute immediate 'alter table T_INTEGRATED_ENTRUSTHISTORY add PROP_TRADE_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_INTEGRATED_ENTRUSTHISTORY.PROP_TRADE_NAME is ''对手方交易员名称''';
    end if;
end;
