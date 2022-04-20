/*在交易管理下添加【固收类交易】菜单*/
delete from sys_menu where MENU_ID='DEAL_001_001_015';
insert into sys_menu (MENU_ID, SYSTEM_CODE, ICON, PARENT_MENU_ID, PAGE_ID, ORDER_NO, IS_SPREAD, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DEAL_001_001_015', 'VAL', null, 'DEAL_001_001', 'TrdFixIncomeValuationTrade001', 15, '2', '固收类交易', 'system', null,sysdate, 'system', sysdate);

/*固收类交易-查询及新建页面*/
delete from sys_page where page_id='TrdFixIncomeValuationTrade001';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TrdFixIncomeValuationTrade001', 'VAL', '/valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade001', 'valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade001', 'DEAL_001_001_015', null, null, '02', '01', '固收类交易', 'system', null, sysdate, 'system', sysdate);

delete from sys_page where page_id='TrdFixIncomeValuationTrade002';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TrdFixIncomeValuationTrade002', 'VAL', '/valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade002', 'valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade002', null, null, null, '02', '01', '固收类交易新建', 'system', null, sysdate, 'system', sysdate);

/*固收类交易-新建页面-交易类型下拉框*/
delete from  sys_page_dictlist where page_id='TrdFixIncomeValuationTrade002' and DICT_ID='PS' ;
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFixIncomeValuationTrade002', '999999', 'PS', 'T01', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFixIncomeValuationTrade002', '999999', 'PS', 'T02', null, null, 'system', null, sysdate, 'system', sysdate, null);


/*固收类交易-查询页面-交易类型下拉框*/
delete from  sys_page_dictlist where page_id='TrdFixIncomeValuationTrade001' and DICT_ID='PS';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFixIncomeValuationTrade001', '999999', 'PS', 'T01', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFixIncomeValuationTrade001', '999999', 'PS', 'T02', null, null, 'system', null, sysdate, 'system', sysdate, null);

/*固收类交易-新建页面-会计分类下拉框*/
delete from  sys_page_dictlist where page_id='TrdFixIncomeValuationTrade002' and DICT_ID='INV_AIM';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFixIncomeValuationTrade002', '999999', 'INV_AIM', 'AC', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFixIncomeValuationTrade002', '999999', 'INV_AIM', 'FVOCI', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFixIncomeValuationTrade002', '999999', 'INV_AIM', 'FVTPL', null, null, 'system', null, sysdate, 'system', sysdate, null);

/*固收类交易-查询页面-会计分类下拉框*/
delete from  sys_page_dictlist where page_id='TrdFixIncomeValuationTrade001' and DICT_ID='INV_AIM';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFixIncomeValuationTrade001', '999999', 'INV_AIM', 'AC', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFixIncomeValuationTrade001', '999999', 'INV_AIM', 'FVOCI', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFixIncomeValuationTrade001', '999999', 'INV_AIM', 'FVTPL', null, null, 'system', null, sysdate, 'system', sysdate, null);

-- 生成方式  画面数据字典明细关联
DELETE FROM SYS_PAGE_DICTLIST WHERE PAGE_ID = 'TrdFixIncomeValuationTrade001' AND DICT_ID='CREATE_TYPE';
INSERT INTO SYS_PAGE_DICTLIST
(PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
VALUES('TrdFixIncomeValuationTrade001', '999999', 'CREATE_TYPE', '01', '接口接入', NULL, 'system', NULL, sysdate, 'system', sysdate, NULL);
INSERT INTO SYS_PAGE_DICTLIST
(PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
VALUES('TrdFixIncomeValuationTrade001', '999999', 'CREATE_TYPE', '02', '画面录入', NULL, 'system', NULL, sysdate, 'system', sysdate, NULL);



/*固收类交易-工作流配置*/
delete from sys_wfl_business_module where page_id='TrdFixIncomeValuationTrade002';
insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('TrdFixIncomeValuationTrade002', 'TrdFixIncomeValuationTrade002_btn_save', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('TrdFixIncomeValuationTrade002', 'TrdFixIncomeValuationTrade002_btn_submit', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('TrdFixIncomeValuationTrade002', 'TrdFixIncomeValuationTrade001_btn_delete', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');


/*共通业务类型_固收类资产交易*/
delete  from  sys_datadict_list where DICT_CODE='COMMON_BUSINESS_TYPE' and ITEM_CODE='TRD85';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('COMMON_BUSINESS_TYPE', 'TRD85', '固收类资产交易', 577, '0', 'system', null, sysdate, 'system', sysdate, null);
