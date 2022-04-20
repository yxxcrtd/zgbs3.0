/**辅助核算配置 菜单**/
delete from sys_menu s where s.menu_id='ACCOUTING_001_001_006';
insert into sys_menu (MENU_ID, SYSTEM_CODE, ICON, PARENT_MENU_ID, PAGE_ID, ORDER_NO, IS_SPREAD, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ACCOUTING_001_001_006', 'VAL', null, 'ACCOUTING_001_001', 'ValAssistAccounting001', 6, '1', '辅助核算配置', 'system', null, sysdate, 'system', sysdate);
/**辅助核算配置 查询页面**/
delete from sys_page s where s.page_id='ValAssistAccounting001';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ValAssistAccounting001', 'VAL', '/valuation/accounadjustmanage/assistaccount/ValAssistAccounting001', 'valuation/accounadjustmanage/assistaccount/ValAssistAccounting001', 'ACCOUTING_001_001_006', null, null, '02', '02', '辅助核算配置', 'system', null, sysdate, 'system', sysdate);

/**辅助核算配置 修改页面**/
delete from sys_page s where s.page_id='ValAssistAccounting002';
insert into sys_page (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ValAssistAccounting002', 'VAL', '/valuation/accounadjustmanage/assistaccount/ValAssistAccounting002', 'valuation/accounadjustmanage/assistaccount/ValAssistAccounting002', 'ACCOUTING_001_001_007', null, null, '02', '01', '辅助核算配置编辑', 'system', null, sysdate, 'system', sysdate);


/**辅助核算项编辑工作流配置**/
DELETE from SYS_WFL_BUSINESS_MODULE where PAGE_ID='ValAssistAccounting002';

INSERT INTO "SYS_WFL_BUSINESS_MODULE"("PAGE_ID", "ELEMENT_ID", "MODULE_ID", "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "INSTITUTION_CODE", "IS_SEND_UORG") VALUES ('ValAssistAccounting002', 'ValAssistAccounting002_btn_delete', 'oneApproval', 'system', NULL, sysdate, 'system', sysdate, 'ALL', '02');
INSERT INTO "SYS_WFL_BUSINESS_MODULE"("PAGE_ID", "ELEMENT_ID", "MODULE_ID", "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "INSTITUTION_CODE", "IS_SEND_UORG") VALUES ('ValAssistAccounting002', 'ValAssistAccounting002_btn_save', 'oneApproval', 'system', NULL, sysdate, 'system', sysdate, 'ALL', '02');
INSERT INTO "SYS_WFL_BUSINESS_MODULE"("PAGE_ID", "ELEMENT_ID", "MODULE_ID", "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "INSTITUTION_CODE", "IS_SEND_UORG") VALUES ('ValAssistAccounting002', 'ValAssistAccounting002_btn_submit', 'oneApproval', 'system', NULL, sysdate, 'system', sysdate, 'ALL', '02');


delete from sys_datadict_list d where d.dict_code='COMMON_BUSINESS_TYPE' and d.item_code='BIH01';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('COMMON_BUSINESS_TYPE', 'BIH01', '辅助核算配置', 503, '0', 'system', null, sysdate, 'system', sysdate, null);

/**底层科目规则数据字典**/
delete from sys_datadict d where d.dict_code='GEN_DETSUB_RULE';
insert into sys_datadict (DICT_CODE, SYSTEM_CODE, DICT_CNNAME, DICT_ENNAME, UPDATE_FLG, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GEN_DETSUB_RULE', 'VAL', '底层科目规则', null, '02', 'system', null, sysdate, 'system', sysdate);

delete from sys_datadict_list d where d.dict_code='GEN_DETSUB_RULE';

insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('GEN_DETSUB_RULE', 'FYDM', '费用代码', 0, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('GEN_DETSUB_RULE', 'JGDM', '机构代码', 1, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('GEN_DETSUB_RULE', 'JYDM', '交易代码', 2, '0', 'system', null, sysdate, 'system', sysdate, null);

insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('GEN_DETSUB_RULE', 'ZQDM', '证券代码', 3, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('GEN_DETSUB_RULE', 'ZCDM', '资产代码', 4, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('GEN_DETSUB_RULE', 'ZJZH', '资金账户', 5, '0', 'system', null, sysdate, 'system', sysdate, null);

insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('GEN_DETSUB_RULE', 'ZCPDM', '子产品代码', 6, '0', 'system', null, sysdate, 'system', sysdate, null);

delete from  sys_page_dictlist where page_id='ValAssistAccounting002' and DICT_ID='GEN_DETSUB_RULE';
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('ValAssistAccounting002', '999999', 'GEN_DETSUB_RULE', 'FYDM', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('ValAssistAccounting002', '999999', 'GEN_DETSUB_RULE', 'JGDM', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('ValAssistAccounting002', '999999', 'GEN_DETSUB_RULE', 'JYDM', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('ValAssistAccounting002', '999999', 'GEN_DETSUB_RULE', 'ZQDM', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('ValAssistAccounting002', '999999', 'GEN_DETSUB_RULE', 'ZCDM', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('ValAssistAccounting002', '999999', 'GEN_DETSUB_RULE', 'ZJZH', null, null, 'system', null, sysdate, 'system', sysdate, null);
insert into sys_page_dictlist (PAGE_ID, SUB_PAGE_ID, DICT_ID, DICT_ITEM_VAL, DICT_ITEM_VAL_CNNAME, DICT_ITEM_VAL_DEFAULT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('ValAssistAccounting002', '999999', 'GEN_DETSUB_RULE', 'ZCPDM', null, null, 'system', null, sysdate, 'system', sysdate, null);

/**添加辅助核算项默认模板**/
delete from BOK_BOOKSET BB where BB.BOOKSET_ID='SUBJECT_INTERNAL_001';
insert into BOK_BOOKSET (BOOKSET_ID, BOOKSET_NAME, BOOKSET_FULLNAME, BOOKSET_TYPE, ACCT_MODE, BOK_SPEC_MODE, ACCT_SUBJECT_ID, ACCT_SUBJECT_TYPE, STANDARD_CCY, BOOKSET_ID_TMPL, ACCT_STANDARD, START_DATE, END_DATE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('SUBJECT_INTERNAL_001', '辅助核算默认账套模板', '辅助核算默认账套模板', '01', null, null, null, null, 'CNY', 'SUBJECT_INTERNAL_001', 'QY', sysdate, sysdate, 'system', null, sysdate, 'system', sysdate);


/**添加辅助核算项业务字典**/
delete from sys_datadict d where d.dict_code='BOK_HAND_GROUP';
insert into sys_datadict (DICT_CODE, SYSTEM_CODE, DICT_CNNAME, DICT_ENNAME, UPDATE_FLG, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('BOK_HAND_GROUP', 'VAL', '辅助核算项群组', null, '02', 'system', null, sysdate, 'system', sysdate);


delete from sys_datadict_list d where d.dict_code='BOK_HAND_GROUP';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('BOK_HAND_GROUP', 'SEC_TYPE', '证券大类', 0, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('BOK_HAND_GROUP', 'TYPE_2', '证券分类', 1, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('BOK_HAND_GROUP', 'TRADE_MARKET', '交易市场', 2, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('BOK_HAND_GROUP', 'ISSUE_TYPE', '发行方式', 3, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('BOK_HAND_GROUP', 'COUNTER_TYPE', '交易对手类型', 4, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('BOK_HAND_GROUP', 'ACCT_TYPE', '账户类型', 5, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('BOK_HAND_GROUP', 'FEE_TYPE', '费用类型', 6, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('BOK_HAND_GROUP', 'TRD_PROP', '交易属性', 7, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('BOK_HAND_GROUP', 'TAX_FEE_TYPE', '税费类型', 8, '0', 'system', null, sysdate, 'system', sysdate, null);
--insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
--values ('BOK_HAND_GROUP', 'BOK_CPY', '交易对手', 8, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('BOK_HAND_GROUP', 'CHANNEL', '渠道', 9, '0', 'system', null, sysdate, 'system', sysdate, null);



delete from sys_datadict d where d.dict_code in('TYPE_2','TRD_PROP','TAX_FEE_TYPE');

insert into sys_datadict (DICT_CODE, SYSTEM_CODE, DICT_CNNAME, DICT_ENNAME, UPDATE_FLG, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TYPE_2', 'VAL', '证券分类', null, '01', 'system', null, sysdate, 'system', sysdate);
insert into sys_datadict (DICT_CODE, SYSTEM_CODE, DICT_CNNAME, DICT_ENNAME, UPDATE_FLG, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TRD_PROP', 'VAL', '交易属性', null, '01', 'system', null, sysdate, 'system', sysdate);
insert into sys_datadict (DICT_CODE, SYSTEM_CODE, DICT_CNNAME, DICT_ENNAME, UPDATE_FLG, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TAX_FEE_TYPE', 'VAL', '税费类型', null, '01', 'system', null, sysdate, 'system', sysdate);
--insert into sys_datadict (DICT_CODE, SYSTEM_CODE, DICT_CNNAME, DICT_ENNAME, UPDATE_FLG, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
--values ('BOK_CPY', 'VAL', '交易对手', null, '01', 'system', null, sysdate, 'system', sysdate);




delete from sys_datadict_list d where d.dict_code in('TYPE_2','TRD_PROP','TAX_FEE_TYPE');




--科目性质级别
delete from sys_datadict sd where sd.dict_code='SUBJECT_LEVEL_NEW';
insert into sys_datadict (DICT_CODE, SYSTEM_CODE, DICT_CNNAME, DICT_ENNAME, UPDATE_FLG, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('SUBJECT_LEVEL_NEW', 'VAL', '科目性质级别', null, '02', 'system', null,sysdate, 'system',sysdate);

delete from  sys_datadict_list sd where sd.dict_code='SUBJECT_LEVEL_NEW';
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('SUBJECT_LEVEL_NEW', '1', '总账', 1, '0', 'system', null,sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('SUBJECT_LEVEL_NEW', '2', '明细', 2, '0', 'system', null, sysdate, 'system', sysdate, null);
insert into sys_datadict_list (DICT_CODE, ITEM_CODE, ITEM_VALUE, ORDER_NO, DEFAULT_VALUE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE)
values ('SUBJECT_LEVEL_NEW', '3', '汇总式明细', 3, '0', 'system', null, sysdate, 'system',sysdate, null);


