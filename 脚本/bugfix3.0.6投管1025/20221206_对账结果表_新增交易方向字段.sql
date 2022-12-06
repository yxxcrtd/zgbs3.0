declare
    ps_count number;
begin
    select count(1) into ps_count from user_tab_columns where UPPER(column_name)='PS' AND TABLE_NAME = 'STL_SEC_RECON';
    if ps_count > 0 then
        EXECUTE IMMEDIATE 'alter table STL_SEC_RECON DROP COLUMN PS';
    end if;
    EXECUTE IMMEDIATE 'ALTER TABLE STL_SEC_RECON ADD PS VARCHAR2(50)';

    EXECUTE IMMEDIATE 'COMMENT ON COLUMN STL_SEC_RECON.PS IS ''交易方向''';
end;
/