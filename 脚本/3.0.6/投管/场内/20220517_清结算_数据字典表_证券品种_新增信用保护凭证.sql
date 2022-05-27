delete from SYS_DATADICT_LIST where dict_code = 'SEC_TYPE' and item_code in ('14');

insert into SYS_DATADICT_LIST(DICT_CODE,ITEM_CODE,ITEM_VALUE,ORDER_NO,DEFAULT_VALUE,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,INSTITUTION_CODE)
values ('SEC_TYPE','14','信用保护凭证交易','14','0','system','',sysdate, 'system',sysdate,null);