update CFG_DATATABLE_SHOW set COLCODE = 'entrustPrice', EXTENDCFG= '{"colAlign": "left","dataType":"amount","precision":"2"}' where PAGECODE = 'FieldEtrDispatcher' and DTCODE = 'FieldEtrDispatcherExeInfo' and SPCDCODE = 'EX010' and COLCODE = 'fullPrice';


update CFG_DATATABLE_SHOW set COLCODE = 'entrustPrice', EXTENDCFG= '{"colAlign": "left","dataType":"amount","precision":"2"}' where PAGECODE = 'FieldEtrDispatcher' and DTCODE = 'FieldEtrDispatcherExeInfo' and SPCDCODE = 'EX010' and COLCODE = 'entrustPrice';