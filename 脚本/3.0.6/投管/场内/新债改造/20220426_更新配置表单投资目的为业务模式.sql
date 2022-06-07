update CFG_DATATABLE_SHOW set COLNAME = '业务模式' where COLNAME = '投资目的';
update CFG_READONLY_FORM_SHOW set COLNAME = '业务模式' where COLNAME = '投资目的';
delete from CFG_DATATABLE_SHOW where COLNAME = '业务模式' and COLCODE like '%invAim%';
delete from CFG_READONLY_FORM_SHOW where COLNAME = '业务模式' and COLCODE like '%invAim%';