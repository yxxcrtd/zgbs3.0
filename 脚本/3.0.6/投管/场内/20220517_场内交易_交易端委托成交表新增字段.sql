
declare
    columnExistedCount1 number;
    columnExistedCount2 number;
    columnExistedCount3 number;
    columnExistedCount4 number;
    columnExistedCount5 number;
    columnExistedCount6 number;
    columnExistedCount7 number;
    columnExistedCount8 number;
    columnExistedCount9 number;
    columnExistedCount10 number;
    columnExistedCount11 number;
    columnExistedCount12 number;
    columnExistedCount13 number;
    columnExistedCount14 number;
begin
    select  count(1) into columnExistedCount1  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUST')  and t.column_name = upper('COUNTER_NAME');
    select  count(1) into columnExistedCount2  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUST')  and t.column_name = upper('PROP_ACCT_NO');
    select  count(1) into columnExistedCount3  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUST')  and t.column_name = upper('PROP_ACCT_NAME');
    select  count(1) into columnExistedCount4  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUSTHISTORY')  and t.column_name = upper('COUNTER_NAME');
    select  count(1) into columnExistedCount5  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUSTHISTORY')  and t.column_name = upper('PROP_ACCT_NO');
    select  count(1) into columnExistedCount6  from user_tab_columns t where t.table_name = upper('T_INTEGRATED_ENTRUSTHISTORY')  and t.column_name = upper('PROP_ACCT_NAME');
    select  count(1) into columnExistedCount7  from user_tab_columns t where t.table_name = upper('T_TRADE')  and t.column_name = upper('COUNTER_ID');
    select  count(1) into columnExistedCount8  from user_tab_columns t where t.table_name = upper('T_TRADE')  and t.column_name = upper('COUNTER_NAME');
    select  count(1) into columnExistedCount9  from user_tab_columns t where t.table_name = upper('T_TRADE')  and t.column_name = upper('PROP_ACCT_NO');
    select  count(1) into columnExistedCount10  from user_tab_columns t where t.table_name = upper('T_TRADE')  and t.column_name = upper('PROP_ACCT_NAME');
    select  count(1) into columnExistedCount11  from user_tab_columns t where t.table_name = upper('T_TRADEHISTORY')  and t.column_name = upper('COUNTER_ID');
    select  count(1) into columnExistedCount12  from user_tab_columns t where t.table_name = upper('T_TRADEHISTORY')  and t.column_name = upper('COUNTER_NAME');
    select  count(1) into columnExistedCount13  from user_tab_columns t where t.table_name = upper('T_TRADEHISTORY')  and t.column_name = upper('PROP_ACCT_NO');
    select  count(1) into columnExistedCount14  from user_tab_columns t where t.table_name = upper('T_TRADEHISTORY')  and t.column_name = upper('PROP_ACCT_NAME');

    if columnExistedCount1 = 0 then
        execute immediate 'alter table T_INTEGRATED_ENTRUST add COUNTER_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_INTEGRATED_ENTRUST.COUNTER_NAME is ''交易对手名称''';
    end if;
    if columnExistedCount2 = 0 then
        execute immediate 'alter table T_INTEGRATED_ENTRUST add PROP_ACCT_NO VARCHAR2(50)';
        execute immediate 'comment on column T_INTEGRATED_ENTRUST.PROP_ACCT_NO is ''对手资金账户''';
    end if;

    if columnExistedCount3 = 0 then
        execute immediate 'alter table T_INTEGRATED_ENTRUST add PROP_ACCT_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_INTEGRATED_ENTRUST.PROP_ACCT_NAME is ''对手资金账户名称''';
    end if;
    if columnExistedCount4 = 0 then
        execute immediate 'alter table T_INTEGRATED_ENTRUSTHISTORY add COUNTER_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_INTEGRATED_ENTRUSTHISTORY.COUNTER_NAME is ''交易对手名称''';
    end if;
    if columnExistedCount5 = 0 then
        execute immediate 'alter table T_INTEGRATED_ENTRUSTHISTORY add PROP_ACCT_NO VARCHAR2(50)';
        execute immediate 'comment on column T_INTEGRATED_ENTRUSTHISTORY.PROP_ACCT_NO is ''对手资金账户''';
    end if;
    if columnExistedCount6 = 0 then
        execute immediate 'alter table T_INTEGRATED_ENTRUSTHISTORY add PROP_ACCT_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_INTEGRATED_ENTRUSTHISTORY.PROP_ACCT_NAME is ''对手资金账户名称''';
    end if;
    if columnExistedCount7 = 0 then
        execute immediate 'alter table T_TRADE add COUNTER_ID VARCHAR2(50)';
        execute immediate 'comment on column T_TRADE.COUNTER_ID is ''交易对手''';
    end if;
    if columnExistedCount8 = 0 then
        execute immediate 'alter table T_TRADE add COUNTER_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_TRADE.COUNTER_NAME is ''交易对手名称''';
    end if;
    if columnExistedCount9 = 0 then
        execute immediate 'alter table T_TRADE add PROP_ACCT_NO VARCHAR2(50)';
        execute immediate 'comment on column T_TRADE.PROP_ACCT_NO is ''对手资金账户''';
    end if;
    if columnExistedCount10 = 0 then
        execute immediate 'alter table T_TRADE add PROP_ACCT_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_TRADE.PROP_ACCT_NAME is ''对手资金账户名称''';
    end if;
    if columnExistedCount11 = 0 then
        execute immediate 'alter table T_TRADEHISTORY add COUNTER_ID VARCHAR2(50)';
        execute immediate 'comment on column T_TRADEHISTORY.COUNTER_ID is ''交易对手''';
    end if;
    if columnExistedCount12 = 0 then
        execute immediate 'alter table T_TRADEHISTORY add COUNTER_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_TRADEHISTORY.COUNTER_NAME is ''交易对手名称''';
    end if;
    if columnExistedCount13 = 0 then
        execute immediate 'alter table T_TRADEHISTORY add PROP_ACCT_NO VARCHAR2(50)';
        execute immediate 'comment on column T_TRADEHISTORY.PROP_ACCT_NO is ''对手资金账户''';
    end if;
    if columnExistedCount14 = 0 then
        execute immediate 'alter table T_TRADEHISTORY add PROP_ACCT_NAME VARCHAR2(128)';
        execute immediate 'comment on column T_TRADEHISTORY.PROP_ACCT_NAME is ''对手资金账户名称''';
    end if;
end;
