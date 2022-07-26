declare
    prinAmt_count number;
begin
    select count(1) into prinAmt_count from user_tab_columns where UPPER(column_name)='PRIN_AMT' AND TABLE_NAME = 'INF_CONTRACT_DETAILFILE';
    if prinAmt_count > 0 then
        EXECUTE IMMEDIATE 'alter table INF_CONTRACT_DETAILFILE DROP COLUMN PRIN_AMT';
    end if;
    EXECUTE IMMEDIATE 'ALTER TABLE INF_CONTRACT_DETAILFILE ADD PRIN_AMT NUMBER(30,14)';

    EXECUTE IMMEDIATE 'COMMENT ON COLUMN INF_CONTRACT_DETAILFILE.PRIN_AMT IS ''回购金额''';
end;
/