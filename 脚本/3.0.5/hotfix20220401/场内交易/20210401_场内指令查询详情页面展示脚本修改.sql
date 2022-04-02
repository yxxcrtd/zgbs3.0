-- 指令查询 删除以下字段
-- 1债转股
delete from CFG_READONLY_FORM_SHOW where PAGECODE = 'FieldEtrDispatcher' and DTCODE = 'FieldEtrDispatcherInfo' and SPCDCODE = 'EX004' and COLCODE = 'expConValue';

-- 2配债
delete from CFG_READONLY_FORM_SHOW where PAGECODE = 'FieldEtrDispatcher' AND DTCODE = 'FieldEtrDispatcherInfo' AND SPCDCODE = 'EX006'  and COLCODE = 'expConValue';
delete from CFG_READONLY_FORM_SHOW where PAGECODE = 'FieldEtrDispatcher' AND DTCODE = 'FieldEtrDispatcherInfo' AND SPCDCODE = 'EX006'  and COLCODE = 'preb';
delete from CFG_READONLY_FORM_SHOW where PAGECODE = 'FieldEtrDispatcher' AND DTCODE = 'FieldEtrDispatcherInfo' AND SPCDCODE = 'EX006'  and COLCODE = 'prec';

-- 3可转债申购
delete from CFG_READONLY_FORM_SHOW where PAGECODE = 'FieldEtrDispatcher' AND DTCODE = 'FieldEtrDispatcherInfo' AND SPCDCODE = 'EX007' and COLCODE = 'expConValue';
delete from CFG_READONLY_FORM_SHOW where PAGECODE = 'FieldEtrDispatcher' AND DTCODE = 'FieldEtrDispatcherInfo' AND SPCDCODE = 'EX007' and COLCODE = 'preb';
delete from CFG_READONLY_FORM_SHOW where PAGECODE = 'FieldEtrDispatcher' AND DTCODE = 'FieldEtrDispatcherInfo' AND SPCDCODE = 'EX007' and COLCODE = 'prec';