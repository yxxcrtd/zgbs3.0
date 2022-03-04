/*在存续期管理下添加【基金存续期】菜单*/
delete from sys_menu where MENU_ID='DEAL_001_003_004';
insert into sys_menu (MENU_ID, SYSTEM_CODE, ICON, PARENT_MENU_ID, PAGE_ID, ORDER_NO, IS_SPREAD, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DEAL_001_003_004', 'VAL', null, 'DEAL_001_003', 'TrdFundBonusValuationTrade001', 4, '2', '基金存续期', 'system', null,sysdate, 'system', sysdate);

/*基金存续期-查询及新建页面*/
delete from sys_page where page_id='TrdFundBonusValuationTrade001';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TrdFundBonusValuationTrade001', 'TRD', '/valuation/trademanage/trdfundbonusvaluationtrade/TrdFundBonusValuationTrade001', 'valuation/trademanage/trdfundbonusvaluationtrade/TrdFundBonusValuationTrade001', 'DEAL_001_003_004', null, null, '02', '01', '基金存续期', 'system', null, sysdate, 'system', sysdate);
delete from sys_page where page_id='TrdFundBonusValuationTrade002';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TrdFundBonusValuationTrade002', 'TRD', '/valuation/trademanage/trdfundbonusvaluationtrade/TrdFundBonusValuationTrade002', 'valuation/trademanage/trdfundbonusvaluationtrade/TrdFundBonusValuationTrade002', null, null, null, '02', '01', '基金存续期新建', 'system', null, sysdate, 'system', sysdate);

/*基金存续期-新建页面-交易类型下拉框*/
delete from  sys_page_dictlist where page_id='TrdFundBonusValuationTrade002' and DICT_ID='PS';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade002', '999999', 'PS', 'T11', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade002', '999999', 'PS', 'T12', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade002', '999999', 'PS', 'C3', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade002', '999999', 'PS', 'C6', null, null, 'system', null, sysdate, 'system', sysdate, null);


/*基金存续期-查询页面-交易类型下拉框*/
delete from  sys_page_dictlist where page_id='TrdFundBonusValuationTrade001' and DICT_ID='PS';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade001', '999999', 'PS', 'T11', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade001', '999999', 'PS', 'T12', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade001', '999999', 'PS', 'C3', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade001', '999999', 'PS', 'C6', null, null, 'system', null, sysdate, 'system', sysdate, null);

/*基金存续期-新建页面-交易场所下拉框*/
delete from  sys_page_dictlist where page_id='TrdFundBonusValuationTrade002' and DICT_ID='TRADE_PLACE';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade002', '999999', 'TRADE_PLACE', '03', null, null, 'system', null, sysdate, 'system', sysdate, null);

insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade002', '999999', 'TRADE_PLACE', '04', null, null, 'system', null, sysdate, 'system', sysdate, null);

insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade002', '999999', 'TRADE_PLACE', '09', null, null, 'system', null, sysdate, 'system', sysdate, null);

/*基金存续期-查询页面-交易场所下拉框*/
delete from  sys_page_dictlist where page_id='TrdFundBonusValuationTrade001' and DICT_ID='TRADE_PLACE';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade001', '999999', 'TRADE_PLACE', '03', null, null, 'system', null, sysdate, 'system', sysdate, null);

insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade001', '999999', 'TRADE_PLACE', '04', null, null, 'system', null, sysdate, 'system', sysdate, null);

insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('TrdFundBonusValuationTrade001', '999999', 'TRADE_PLACE', '09', null, null, 'system', null, sysdate, 'system', sysdate, null);

/*基金存续期-工作流配置*/
delete from sys_wfl_business_module where page_id='TrdFundBonusValuationTrade002';
insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('TrdFundBonusValuationTrade002', 'TrdFundBonusValuationTrade002_btn_save', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('TrdFundBonusValuationTrade002', 'TrdFundBonusValuationTrade002_btn_submit', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

insert into sys_wfl_business_module (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('TrdFundBonusValuationTrade002', 'TrdFundBonusValuationTrade001_btn_delete', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');




/*基金存续期-4位数的交易编号，序列*/
DROP SEQUENCE FUNDBONUSTRADEID_SEQUENCE;
-- Create sequence
create sequence FUNDBONUSTRADEID_SEQUENCE
minvalue 1
maxvalue 9999
start with 1
increment by 1
cache 20;