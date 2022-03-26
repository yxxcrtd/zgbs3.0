--计划类资产 收益类型增加利率型
delete from sys_page_dictlist d where d.page_id='FinPlanAssetC001' and d.dict_id='PROFIT_TYPE' and d.dict_item_val='02';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('FinPlanAssetC001', '999999', 'PROFIT_TYPE', '02', '预期收益型', null, 'system', null, sysdate, 'system', sysdate, 'ALL');

