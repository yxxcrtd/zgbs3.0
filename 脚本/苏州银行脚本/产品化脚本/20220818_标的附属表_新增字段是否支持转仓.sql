declare
    vc_count number;
begin
    select count(1) into vc_count from user_tab_columns where UPPER(column_name)='IS_INNER_ADV' AND TABLE_NAME = 'FIN_PRODUCT_ADD';
    if vc_count > 0 then
        EXECUTE IMMEDIATE 'ALTER TABLE FIN_PRODUCT_ADD DROP COLUMN IS_INNER_ADV';
    end if;
    EXECUTE IMMEDIATE 'ALTER TABLE FIN_PRODUCT_ADD ADD IS_INNER_ADV VARCHAR2(2)';
    EXECUTE IMMEDIATE 'COMMENT ON COLUMN FIN_PRODUCT_ADD.IS_INNER_ADV IS ''是否支持转仓(Y/N)''';
end;

/