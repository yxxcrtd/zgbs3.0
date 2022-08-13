UPDATE CFG_READONLY_FORM_SHOW
SET "COLNAME"       = '质押数量',
    "DICTCODE"      = NULL,
    "SEQNO"         = '22',
    "EXTENDCFG"     = '{"colAlign": "left","dataType":"amount","precision":"2","unit":"wanYuan"}',
    "STATUS"        = '1',
    "REMARK1"       = '0',
    "REMARK2"       = NULL,
    "REMARK3"       = NULL,
    "SPCDCODE_DESC" = '999999'
WHERE "PAGECODE" = 'CfetsEtrDispatcher'
  AND "DTCODE" = 'CfetsEtrDispatcherInfo'
  AND "SPCDCODE" = 'EX012'
  AND "COLCODE" = 'pledgeNumber';
