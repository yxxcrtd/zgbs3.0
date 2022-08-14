update CFG_DATATABLE_SHOW set EXTENDCFG = '{"colAlign": "left","dataType":"amount","precision":"2"}' where PAGECODE = 'FieldEtrDispatcher'
and DTCODE = 'FieldEtrDispatcherDealInfo'
and SPCDCODE in ('EX011','EX012','EX013','EX014','EX015') and COLCODE = 'tradedVolume';


update CFG_DATATABLE_SHOW set EXTENDCFG = '{"colAlign": "left","dataType":"amount","precision":"2"}' where PAGECODE = 'CfetsEtrDispatcher'
and DTCODE = 'CfetsEtrDispatcherDealInfo'
and SPCDCODE in ('EX011','EX012','EX013','EX014','EX015') and COLCODE = 'tradedVolume';


update  CFG_DATATABLE_SHOW set COLCODE = 'entrustId' where PAGECODE = 'FieldEtrDispatcher'
and DTCODE = 'FieldEtrDispatcherDealInfo'
and SPCDCODE in ('EX012')
and COLCODE = 'brokerEntrustId';