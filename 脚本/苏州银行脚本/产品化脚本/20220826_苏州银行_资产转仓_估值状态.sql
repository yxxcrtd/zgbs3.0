
declare
    vc_count number;
    vc_count1 number;

begin
    select count(1) into vc_count from user_tab_columns where UPPER(column_name)='CHECK_STAUTS' AND TABLE_NAME = 'PTL_PORTFOLIO_VALUATION';
    if vc_count = 0 then
        EXECUTE IMMEDIATE 'ALTER TABLE PTL_PORTFOLIO_VALUATION ADD CHECK_STAUTS VARCHAR2(1)';
        EXECUTE IMMEDIATE 'COMMENT ON COLUMN PTL_PORTFOLIO_VALUATION.CHECK_STAUTS IS ''对账状态:0- 未对账、1- 无需对账、2- 对账一致、3- 对账不一致''';
    end if;

    select count(1) into vc_count1 from user_tab_columns where UPPER(column_name)='VALUATION_TABLE_STATUS' AND TABLE_NAME = 'PTL_PORTFOLIO_VALUATION';
    if vc_count1 = 0 then
        EXECUTE IMMEDIATE 'ALTER TABLE PTL_PORTFOLIO_VALUATION ADD VALUATION_TABLE_STATUS VARCHAR2(1)';
        EXECUTE IMMEDIATE 'COMMENT ON COLUMN PTL_PORTFOLIO_VALUATION.VALUATION_TABLE_STATUS IS ''估值表状态：0-未确认、1-已确认	''';
    end if;

end;