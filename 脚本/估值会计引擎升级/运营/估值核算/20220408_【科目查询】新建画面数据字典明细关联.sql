--默认下拉框只显示是
delete from  sys_page_dictlist where page_id='ValSubjectSet002' and DICT_ID='YES_OR_NO';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('ValSubjectSet002', '999999', 'YES_OR_NO', 'Y', null, null, 'system', null, sysdate, 'system', sysdate, null);
