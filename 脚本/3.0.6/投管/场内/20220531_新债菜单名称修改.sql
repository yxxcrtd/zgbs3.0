update SYS_MENU
set REMARK = '场内债券指令'
where PAGE_ID = 'etrExchangeTradeSecRepo';

UPDATE SYS_DATADICT_LIST
SET "ITEM_VALUE" = '场内债券指令'
WHERE "DICT_CODE" = 'COMMON_BUSINESS_TYPE'
  AND "ITEM_CODE" = 'ETR54';

UPDATE SYS_PAGE
SET "REMARK" = '场内债券指令'
WHERE "PAGE_ID" = 'etrExchangeTradeSecRepo';

