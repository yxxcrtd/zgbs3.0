delete from SYS_DATADICT_LIST where dict_code = 'COMMON_BUSINESS_TYPE' and item_code = 'ETR54';
insert into SYS_DATADICT_LIST values ('COMMON_BUSINESS_TYPE','ETR54','场内债券现券指令','440','0','system','',sysdate, 'system',sysdate,'');

delete from SYS_PAGE_DICTLIST where PAGE_ID ='MainPage' and DICT_ID = 'COMMON_BUSINESS_TYPE' and DICT_ITEM_VAL = 'ETR54';
INSERT INTO SYS_PAGE_DICTLIST (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE) VALUES ('MainPage', '999999', 'COMMON_BUSINESS_TYPE', 'ETR54', '场内债券现券指令', null, 'system', null, sysdate, 'system',sysdate, null);