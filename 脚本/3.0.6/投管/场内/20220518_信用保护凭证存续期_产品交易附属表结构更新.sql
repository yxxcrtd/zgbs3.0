declare
    columnExistedCount1 number;
    columnExistedCount2 number;
    columnExistedCount3 number;
    columnExistedCount4 number;
    columnExistedCount5 number;
    columnExistedCount6 number;
begin

    select count(1) into columnExistedCount1 from user_tab_columns t where t.table_name = upper('TRD_DEAL_TOTAL_ADD')  and t.column_name = upper('CREDIT_EVENT_HAPPENED_DATE');
    select count(1) into columnExistedCount2 from user_tab_columns t where t.table_name = upper('TRD_DEAL_TOTAL_ADD')  and t.column_name = upper('CREDIT_EVENT_RESOLVE_DATE');
    select count(1) into columnExistedCount3 from user_tab_columns t where t.table_name = upper('TRD_DEAL_TOTAL_ADD')  and t.column_name = upper('DELIVER_BOND');
    select count(1) into columnExistedCount4 from user_tab_columns t where t.table_name = upper('WFL_TRD_DEAL_TOTAL_ADD')  and t.column_name = upper('CREDIT_EVENT_HAPPENED_DATE');
    select count(1) into columnExistedCount5 from user_tab_columns t where t.table_name = upper('WFL_TRD_DEAL_TOTAL_ADD')  and t.column_name = upper('CREDIT_EVENT_RESOLVE_DATE');
    select count(1) into columnExistedCount6 from user_tab_columns t where t.table_name = upper('WFL_TRD_DEAL_TOTAL_ADD')  and t.column_name = upper('DELIVER_BOND');
    if columnExistedCount1 = 0 then
        execute immediate
            'alter table TRD_DEAL_TOTAL_ADD add CREDIT_EVENT_HAPPENED_DATE DATE';
    end if;
    if columnExistedCount2 = 0 then
        execute immediate
            'alter table TRD_DEAL_TOTAL_ADD add CREDIT_EVENT_RESOLVE_DATE DATE';
    end if;
    if columnExistedCount3 = 0 then
        execute immediate
            'alter table TRD_DEAL_TOTAL_ADD add DELIVER_BOND VARCHAR2(50)';
    end if;
    if columnExistedCount4 = 0 then
        execute immediate
            'alter table WFL_TRD_DEAL_TOTAL_ADD add CREDIT_EVENT_HAPPENED_DATE DATE';
    end if;
    if columnExistedCount5 = 0 then
        execute immediate
            'alter table WFL_TRD_DEAL_TOTAL_ADD add CREDIT_EVENT_RESOLVE_DATE DATE';
    end if;
    if columnExistedCount6 = 0 then
        execute immediate
            'alter table WFL_TRD_DEAL_TOTAL_ADD add DELIVER_BOND VARCHAR2(50)';
    end if;
    execute immediate 'comment on column TRD_DEAL_TOTAL_ADD.CREDIT_EVENT_HAPPENED_DATE is ''信用事件发生时间''';

    execute immediate 'comment on column TRD_DEAL_TOTAL_ADD.CREDIT_EVENT_RESOLVE_DATE is ''信用事件决定日''';

    execute immediate 'comment on column TRD_DEAL_TOTAL_ADD.DELIVER_BOND is ''交付债券''';

    execute immediate 'comment on column WFL_TRD_DEAL_TOTAL_ADD.CREDIT_EVENT_HAPPENED_DATE is ''信用事件发生时间''';

    execute immediate 'comment on column WFL_TRD_DEAL_TOTAL_ADD.CREDIT_EVENT_RESOLVE_DATE is ''信用事件决定日''';

    execute immediate 'comment on column WFL_TRD_DEAL_TOTAL_ADD.DELIVER_BOND is ''交付债券''';
end;

