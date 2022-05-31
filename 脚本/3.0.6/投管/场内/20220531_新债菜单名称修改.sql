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

update SYS_PAGE_DICTLIST
set DICT_ITEM_VAL_CNNAME = null
where PAGE_ID = 'MainPage'
  and SUB_PAGE_ID = '999999'
  and DICT_ID = 'COMMON_BUSINESS_TYPE'
  and DICT_ITEM_VAL = 'ETR54';


