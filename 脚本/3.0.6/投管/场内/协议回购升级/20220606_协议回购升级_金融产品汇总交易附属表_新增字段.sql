declare
    dealer_no_count number;
	out_dealer_no_count number;
	dealer_party_no_count number;
	out_dealer_party_no number;
begin
    select count(1) into dealer_no_count from user_tab_columns where UPPER(column_name)='DEALER_NO' AND TABLE_NAME = 'TRD_DEAL_TOTAL_ADD';
	select count(1) into out_dealer_no_count from user_tab_columns where UPPER(column_name)='OUT_DEALER_NO' AND TABLE_NAME = 'TRD_DEAL_TOTAL_ADD';
    select count(1) into dealer_party_no_count from user_tab_columns where UPPER(column_name)='DEALER_PARTY_NO' AND TABLE_NAME = 'TRD_DEAL_TOTAL_ADD';
    select count(1) into out_dealer_party_no from user_tab_columns where UPPER(column_name)='OUT_DEALER_PARTY_NO' AND TABLE_NAME = 'TRD_DEAL_TOTAL_ADD';
    if dealer_no_count > 0 then
        EXECUTE IMMEDIATE 'alter table TRD_DEAL_TOTAL_ADD DROP COLUMN DEALER_NO';
    end if;
	if out_dealer_no_count > 0 then
        EXECUTE IMMEDIATE 'alter table TRD_DEAL_TOTAL_ADD DROP COLUMN OUT_DEALER_NO';
    end if;
	if dealer_party_no_count > 0 then
        EXECUTE IMMEDIATE 'alter table TRD_DEAL_TOTAL_ADD DROP COLUMN DEALER_PARTY_NO';
    end if;
	if out_dealer_party_no > 0 then
        EXECUTE IMMEDIATE 'alter table TRD_DEAL_TOTAL_ADD DROP COLUMN OUT_DEALER_PARTY_NO';
    end if;
    EXECUTE IMMEDIATE 'ALTER TABLE TRD_DEAL_TOTAL_ADD ADD DEALER_NO  VARCHAR2(50)';
	EXECUTE IMMEDIATE 'ALTER TABLE TRD_DEAL_TOTAL_ADD ADD OUT_DEALER_NO  VARCHAR2(50)';
    EXECUTE IMMEDIATE 'ALTER TABLE TRD_DEAL_TOTAL_ADD ADD DEALER_PARTY_NO  VARCHAR2(50)';
    EXECUTE IMMEDIATE 'ALTER TABLE TRD_DEAL_TOTAL_ADD ADD OUT_DEALER_PARTY_NO  VARCHAR2(50)';

    EXECUTE IMMEDIATE 'COMMENT ON COLUMN TRD_DEAL_TOTAL_ADD.DEALER_NO IS ''本方交易商''';
	EXECUTE IMMEDIATE 'COMMENT ON COLUMN TRD_DEAL_TOTAL_ADD.OUT_DEALER_NO IS ''对手方交易商''';
	EXECUTE IMMEDIATE 'COMMENT ON COLUMN TRD_DEAL_TOTAL_ADD.DEALER_PARTY_NO IS ''本方交易主体''';
    EXECUTE IMMEDIATE 'COMMENT ON COLUMN TRD_DEAL_TOTAL_ADD.OUT_DEALER_PARTY_NO IS ''对手方交易主体''';
end;
/