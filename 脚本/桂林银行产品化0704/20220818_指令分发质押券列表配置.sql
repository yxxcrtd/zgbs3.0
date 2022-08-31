-- 更新债券代码字段code
UPDATE CFG_DATATABLE_SHOW
SET COLCODE = 'finprodMarketId'
WHERE PAGECODE = 'CfetsEtrDispatcher'
  AND DTCODE = 'CfetsEtrDispatcherInfo'
  AND SPCDCODE = '05,02,03'
  and COLCODE = 'bondCode';

UPDATE CFG_DATATABLE_SHOW SET SEQNO = 3 WHERE PAGECODE = 'CfetsEtrDispatcher' AND DTCODE = 'CfetsEtrDispatcherInfo' AND SPCDCODE = '05,02,03' AND COLCODE = 'finprodAbbr' ;
UPDATE CFG_DATATABLE_SHOW SET SEQNO = 2 WHERE PAGECODE = 'CfetsEtrDispatcher' AND DTCODE = 'CfetsEtrDispatcherInfo' AND SPCDCODE = '05,02,03' AND COLCODE = 'finprodMarketId' ;
UPDATE CFG_DATATABLE_SHOW SET SEQNO = 7 WHERE PAGECODE = 'CfetsEtrDispatcher' AND DTCODE = 'CfetsEtrDispatcherInfo' AND SPCDCODE = '05,02,03' AND COLCODE = 'firstNetPrice' ;
UPDATE CFG_DATATABLE_SHOW SET SEQNO = 6 WHERE PAGECODE = 'CfetsEtrDispatcher' AND DTCODE = 'CfetsEtrDispatcherInfo' AND SPCDCODE = '05,02,03' AND COLCODE = 'pledStrdPrinAmt' ;
UPDATE CFG_DATATABLE_SHOW SET SEQNO = 4 WHERE PAGECODE = 'CfetsEtrDispatcher' AND DTCODE = 'CfetsEtrDispatcherInfo' AND SPCDCODE = '05,02,03' AND COLCODE = 'pledgePrin' ;
UPDATE CFG_DATATABLE_SHOW SET SEQNO = 5 WHERE PAGECODE = 'CfetsEtrDispatcher' AND DTCODE = 'CfetsEtrDispatcherInfo' AND SPCDCODE = '05,02,03' AND COLCODE = 'pledgeRatio' ;
UPDATE CFG_DATATABLE_SHOW SET SEQNO = 8 WHERE PAGECODE = 'CfetsEtrDispatcher' AND DTCODE = 'CfetsEtrDispatcherInfo' AND SPCDCODE = '05,02,03' AND COLCODE = 'repRegistOrg' ;