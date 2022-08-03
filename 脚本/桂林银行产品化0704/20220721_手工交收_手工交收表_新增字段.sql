declare
    entrustOrderId_count number;
begin
    select count(1) into entrustOrderId_count from user_tab_columns where UPPER(column_name)='ENTRUST_ORDER_ID' AND TABLE_NAME = 'TRD_MANUAL_SETTLE_INFO';
    if entrustOrderId_count > 0 then
        EXECUTE IMMEDIATE 'alter table TRD_MANUAL_SETTLE_INFO DROP COLUMN ENTRUST_ORDER_ID';
    end if;
    EXECUTE IMMEDIATE 'ALTER TABLE TRD_MANUAL_SETTLE_INFO ADD ENTRUST_ORDER_ID  VARCHAR2(50)';

    EXECUTE IMMEDIATE 'COMMENT ON COLUMN TRD_MANUAL_SETTLE_INFO.ENTRUST_ORDER_ID IS ''指令下单Id''';
end;
/