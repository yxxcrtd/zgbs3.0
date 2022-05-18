
update CFG_DATATABLE_SHOW 
   set colname = '成交数量(万元)'
 where PAGECODE = 'FieldEtrDispatcher'
   and DTCODE = 'FieldEtrDispatcherDealInfo'
   and COLNAME = '成交数量';



update CFG_DATATABLE_SHOW
   set colname = '成交价格(元)'
 where PAGECODE = 'FieldEtrDispatcher'
   and DTCODE = 'FieldEtrDispatcherDealInfo'
   and COLNAME = '成交价格';

update CFG_DATATABLE_SHOW
   set colname = '成交金额(元)'
 where PAGECODE = 'FieldEtrDispatcher'
   and DTCODE = 'FieldEtrDispatcherDealInfo'
   and COLNAME = '成交金额';