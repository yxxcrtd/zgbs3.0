
delete from SYS_DATADICT_LIST where dict_code = 'SHARES_TRADE_PLATFORM' and item_code = '06';
insert into SYS_DATADICT_LIST (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE) values ('SHARES_TRADE_PLATFORM','06','新债平台','7','0','system','',to_date('2019-03-05', 'yyyy/mm/dd'), 'system',to_date('2019-03-05', 'yyyy/mm/dd'),'');
