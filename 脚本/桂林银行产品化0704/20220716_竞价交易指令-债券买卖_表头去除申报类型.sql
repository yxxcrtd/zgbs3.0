DELETE FROM CFG_READONLY_FORM_SHOW t
 where t.PAGECODE = 'FieldEtrDispatcher'
   and t.DTCODE = 'FieldEtrDispatcherInfo'
   and t.SPCDCODE = 'F01EX001'
   and t.colcode = 'declaTypeStr';