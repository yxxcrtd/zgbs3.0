declare
    sec_acctId_count number;
begin
    select count(1) into sec_acctId_count from user_tab_columns where UPPER(column_name)='SEC_ACCT_ID' AND TABLE_NAME = 'SETTLE_TRANS_DATA_TOTAL_MATCH';
    if sec_acctId_count > 0 then
        EXECUTE IMMEDIATE 'alter table SETTLE_TRANS_DATA_TOTAL_MATCH DROP COLUMN SEC_ACCT_ID';
    end if;
    EXECUTE IMMEDIATE 'ALTER TABLE SETTLE_TRANS_DATA_TOTAL_MATCH ADD SEC_ACCT_ID  VARCHAR2(50)';

    EXECUTE IMMEDIATE 'COMMENT ON COLUMN SETTLE_TRANS_DATA_TOTAL_MATCH.SEC_ACCT_ID IS ''证券账户''';
end;
/