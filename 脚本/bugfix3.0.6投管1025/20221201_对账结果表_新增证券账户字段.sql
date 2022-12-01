declare
    secAcctId_count number;
begin
    select count(1) into secAcctId_count from user_tab_columns where UPPER(column_name)='SEC_ACCT_ID' AND TABLE_NAME = 'STL_SEC_RECON';
    if secAcctId_count > 0 then
        EXECUTE IMMEDIATE 'alter table STL_SEC_RECON DROP COLUMN SEC_ACCT_ID';
    end if;
    EXECUTE IMMEDIATE 'ALTER TABLE STL_SEC_RECON ADD SEC_ACCT_ID VARCHAR2(50)';

    EXECUTE IMMEDIATE 'COMMENT ON COLUMN STL_SEC_RECON.SEC_ACCT_ID IS ''证券账户''';
end;
/