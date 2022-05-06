-- 更新债券代码字段code
UPDATE CFG_DATATABLE_SHOW
SET COLCODE = 'finprodMarketId'
WHERE PAGECODE = 'CfetsEtrDispatcher'
  AND DTCODE = 'CfetsEtrDispatcherInfo'
  AND SPCDCODE = '05,02,03'
  and COLCODE = 'bondCode';