/*在交易管理下添加【基金交易】菜单*/
delete from sys_menu where MENU_ID='DEAL_001_001_017';
insert into sys_menu (MENU_ID, SYSTEM_CODE, ICON, PARENT_MENU_ID, PAGE_ID, ORDER_NO, IS_SPREAD, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DEAL_001_001_017', 'VAL', null, 'DEAL_001_001', 'TrdFundValuationTrade001', 17, '2', '基金交易', 'system', null,sysdate, 'system', sysdate);

/*基金交易-查询及新建页面*/
delete from sys_page where page_id='TrdFundValuationTrade001';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TrdFundValuationTrade001', 'VAL', '/valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade001', 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade001', 'DEAL_001_001_017', null, null, '02', '01', '基金交易', 'system', null, sysdate, 'system', sysdate);

delete from sys_page where page_id='TrdFundValuationTrade002';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TrdFundValuationTrade002', 'VAL', '/valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade002', 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade002', null, null, null, '02', '01', '基金交易新建', 'system', null, sysdate, 'system', sysdate);

delete from sys_page where page_id='TrdFundValuationTrade003';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TrdFundValuationTrade003', 'VAL', '/valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade003', 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade003', null, null, null, '02', '01', '赎回到账', 'system', null, sysdate, 'system', sysdate);


/*基金交易-新建页面-交易类型下拉框*/
delete from  sys_page_dictlist where page_id='TrdFundValuationTrade002' and DICT_ID='PS';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade002', '999999', 'PS', 'T01', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade002', '999999', 'PS', 'T02', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade002', '999999', 'PS', 'T52', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade002', '999999', 'PS', 'T04', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade002', '999999', 'PS', 'T18', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade002', '999999', 'PS', 'T05', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade002', '999999', 'PS', 'T19', null, null, 'system', null, sysdate, 'system', sysdate, null);

/*基金交易-查询页面-交易类型下拉框*/
delete from  sys_page_dictlist where page_id='TrdFundValuationTrade001' and DICT_ID='PS';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade001', '999999', 'PS', 'T01', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade001', '999999', 'PS', 'T02', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade001', '999999', 'PS', 'T52', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade001', '999999', 'PS', 'T04', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade001', '999999', 'PS', 'T18', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade001', '999999', 'PS', 'T05', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade001', '999999', 'PS', 'T19', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade001', '999999', 'PS', 'T24', null, null, 'system', null, sysdate, 'system', sysdate, null);


/*基金交易-新建页面-交易场所下拉框*/
delete from  sys_page_dictlist where page_id='TrdFundValuationTrade002' and DICT_ID='TRADE_PLACE';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade002', '999999', 'TRADE_PLACE', '03', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade002', '999999', 'TRADE_PLACE', '04', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade002', '999999', 'TRADE_PLACE', '09', null, null, 'system', null, sysdate, 'system', sysdate, null);

/*基金交易-查询页面-交易场所下拉框*/
delete from  sys_page_dictlist where page_id='TrdFundValuationTrade001' and DICT_ID='TRADE_PLACE';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade001', '999999', 'TRADE_PLACE', '03', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade001', '999999', 'TRADE_PLACE', '04', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundValuationTrade001', '999999', 'TRADE_PLACE', '09', null, null, 'system', null, sysdate, 'system', sysdate, null);



/*基金交易-工作流配置*/
delete from sys_wfl_business_module where page_id='TrdFundValuationTrade002';
insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('TrdFundValuationTrade002', 'TrdFundValuationTrade002_btn_save', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('TrdFundValuationTrade002', 'TrdFundValuationTrade002_btn_submit', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('TrdFundValuationTrade002', 'TrdFundValuationTrade001_btn_delete', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

/*基金交易（赎回到账）-工作流配置*/
delete from sys_wfl_business_module where page_id='TrdFundValuationTrade003';
insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('TrdFundValuationTrade003', 'TrdFundValuationTrade003_btn_save', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('TrdFundValuationTrade003', 'TrdFundValuationTrade003_btn_submit', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

