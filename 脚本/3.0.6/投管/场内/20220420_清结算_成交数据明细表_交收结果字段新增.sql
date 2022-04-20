declare
    vc_count number;
begin
    select count(1) into vc_count from user_tab_columns where UPPER(column_name)='DELIVERY_RESULT' AND TABLE_NAME = 'SETTLE_TRANS_DATA';
    if vc_count > 0 then
        EXECUTE IMMEDIATE 'alter table SETTLE_TRANS_DATA DROP COLUMN DELIVERY_RESULT';
    end if;
    EXECUTE IMMEDIATE 'ALTER TABLE SETTLE_TRANS_DATA ADD DELIVERY_RESULT  VARCHAR2(2)';
    EXECUTE IMMEDIATE 'COMMENT ON COLUMN SETTLE_TRANS_DATA.DELIVERY_RESULT IS ''交收结果 01:正常交收 02:部分交收 03:交收失败 04:交收失败''';
end;
/