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
values ('SUBJECT_INTERNAL_001', '辅助核算默认账套模板', '辅助核算默认账套模板', '01', null, null, null, null, 'CNY', null, 'QY', sysdate, sysdate, 'system', null, sysdate, 'system', sysdate);


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


/**增加辅助核算项默认账套**/
delete from bok_bookset b where b.bookset_id='SUBJECT_INTERNAL_001';
insert into bok_bookset (BOOKSET_ID, BOOKSET_NAME, BOOKSET_FULLNAME, BOOKSET_TYPE, ACCT_MODE, BOK_SPEC_MODE, ACCT_SUBJECT_ID, ACCT_SUBJECT_TYPE, STANDARD_CCY, BOOKSET_ID_TMPL, ACCT_STANDARD, START_DATE, END_DATE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('SUBJECT_INTERNAL_001', '辅助核算默认账套模板', '辅助核算默认账套模板', '01', null, null, null, null, 'CNY', null, 'QY', sysdate, sysdate, 'system', null, sysdate, 'system',sysdate);



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

--初始化内部科目性质表
delete from bok_subject_internal;
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('HQCK_Z','活期存款','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('HQCK_BJ','活期存款-本金','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('HQCK_YJLX','活期存款-应计利息','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JSBFJ_Z','结算备付金','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JSBFJ_BJ','结算备付金-本金','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JSBFJ_YJLX','结算备付金-应计利息','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CCBZJ_Z','存出保证金','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CCBZJ_BJ','存出保证金-本金','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CCBZJ_YJLX','存出保证金-应计利息','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DQCK_Z','定期存款','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DQCK_BJ','定期存款-本金','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DQCK_YJLX','定期存款-应计利息','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DQCK_JZCB','定期存款-减值准备','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JYXJRZC_Z','交易性金融资产','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GYJZSYL_CB','公允价值05-成本','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GYJZSYLYJLX','公允价值05-应计利息','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GYJZSYL_GYJZBD','公允价值05-公允价值变动','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JYXJRZC_ZDCB','交易性金融资产-指定类','3','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GYJZSYL_ZDCB_CB','公允价值05-指定类-成本','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GYJZSYL_ZDCB_YJLX','公允价值05-指定类-应计利息','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GYJZSYL_ZDCB_GRJZBD','公允价值05-指定类-公允价值变动','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('MRFSJRZC_Z','买入返售金融资产','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('MRFSJRZC_CB','买入返售金融资产-成本','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('MRFSJRZC_YJLX','买入返售金融资产-应计利息','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('MRFSJRZC_JZZB_Z','买入返售金融资产-减值准备','1','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YSGL_Z','应收股利','1','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YSLX_Z','应收利息','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YSLX_ZJ','应收利息-资金','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YSLX_ZQ','应收利息-证券','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YSSGK_Z','应收申购款','1','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YSSGK_ZRK','应收申购款-转入款','1','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTYSK_Z','其他应收款','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTYSK_FY','其他应收款-费用','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTYSK_QT','其他应收款-其他','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('HZZB_Z','坏账准备','1','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DK_Z','贷款','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DK_BJ','贷款-本金','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DK_YJLX','贷款-应计利息','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DK_JZZB_Z','贷款-减值准备','1','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TYCBLTZ_Z','摊余成本类投资','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TYCBLCB','摊余成本类-成本','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TYCBLLXTZ','摊余成本类-利息调整','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TYCBLZQYJLX','摊余成本类-应计利息','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TYCBLTZ_JZZB_Z','摊余成本类-资产减值','1','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ZHSYL_Z','综合收益类','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ZHSYL_CB','综合收益类-成本','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ZHSYL_LXTZ','综合收益类-利息调整','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ZHSYL_YJLX','综合收益类-应计利息','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ZHSYL_GYJZBD','综合收益类-公允价值变动','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CQGQTZ_Z','长期股权投资','1','N','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CQGQTZ_CB','长期股权投资-成本','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CQGQTZ_GZ','长期股权投资-估值','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CQGQTZ_SYTZ','长期股权投资-损益调整','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CQGQTZ_QTZHSY','长期股权投资-其他综合收益','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CQGQTZ_QTQYBD','长期股权投资-其他权益变动','2','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CQGQTZ_JZZB','长期股权投资-减值准备','1','Y','01', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DQJK_Z','短期借款','1','N','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DQJK_CB','短期借款-成本','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('DQJK_YJLX','短期借款-应计利息','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JYXJRFZ_Z','交易性金融负债','1','N','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JRFZCB','交易性金融负债-成本','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JYXJRFZ_YJLX','交易性金融负债-应计利息','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JYXJRFZ_GYJZBD','交易性金融负债-公允价值变动','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JYXJRFZ_ZDL','交易性金融负债-指定类','3','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JYXJRFZ_ZDL_CB','交易性金融负债-指定类-成本','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JYXJRFZ_ZDL_YJLX','交易性金融负债-指定类-应计利息','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JYXJRFZ_ZDL_GYJZBD','交易性金融负债-指定类-公允价值变动','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('MCHGJRZC_Z','卖出回购金融资产','1','N','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CB','卖出回购金融资产-成本','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YJLX','卖出回购金融资产-应计利息','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFSHK_Z','应付赎回款','1','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFSHK_ZCK_Z','应付赎回款-转出款','1','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFSHF_Z','应付赎回费','1','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFSHF_ZCF_Z','应付赎回费-转出费','1','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFGLRBC_Z','应付管理人报酬','1','N','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFGLRBC','应付管理人报酬-管理费','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFCEGLF','应付管理人报酬-业绩报酬','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFTGF_Z','应付托管费','1','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFXSFWF_Z','应付销售服务费','1','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFTZGWF_Z','应付投资顾问费','1','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YJSF_Z','应交税费','1','N','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YJSF_YJZZS_Z','应交税费-应交增值税','3','N','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LXSRS','应交税费-应交增值税-利息','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSYS','应交税费-应交增值税-价差','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YJSF_WJZZS','应交税费-未交增值税','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YJSF_YGXXS_Z','应交税费-预估销项税','3','N','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YGLXSRS','应交税费-预估销项税-利息','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YGTZSYS','应交税费-预估销项税-价差','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YJZZSFJCJS','应交税费-应交附加税','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YGCJSFJ','应交税费-预估附加税','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFLX_Z','应付利息','1','N','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFLX_MX','应付利息','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFLR_Z','应付利润','1','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFYJ','应付交易费用-佣金','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YFJYFY','应付交易费用-费用','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YTFY','预提费用','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTYFK_Z','其他应付款','1','N','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTYFK_BJ','其他应付款-本金','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTYFK_LX','其他应付款-利息','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTYFK','其他应付款-其他费用','2','Y','02', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QSKZJWL_Z','清算资金往来','1','Y','03', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YSGJ_Z','衍生工具','1','N','03', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YSGJ_CB','衍生工具-成本','2','Y','03', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YSGJ_CBCD','衍生工具-成本冲抵','2','Y','03', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YSGJ_GYJZ','衍生工具-公允价值','2','Y','03', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('SSZB_Z','实收资本','1','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ZBGJ_Z','资本公积','1','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('SYPZJ_Z','损益平准金','1','N','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('SYPZJ_YSX','损益平准金-已实现','2','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('SYPZJ_WSX','损益平准金-未实现','2','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTZHSY_Z','其他综合收益','1','N','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTZHSY_ZQ','其他综合收益-证券','2','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTZHSY_ZCJZ','其他综合收益-资产减值','2','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTZHSY_SH','其他综合收益-申赎','2','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('BQLY_Z','本期利润','1','N','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('BQLR_YSX','本期利润-已实现','2','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('BQLR_WSX','本期利润-未实现','2','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LYFP_Z','利润分配','1','N','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LYFP_WFPLY_Z','利润分配-未分配利润','3','N','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('WFPLR_YSX','利润分配-未分配利润-已实现','2','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('WFPLR_WSX','利润分配-未分配利润-未实现','2','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LRFP_YFLR','利润分配-应付利润','2','Y','04', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LXSR_Z','利息收入','1','N','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LXSR_AC_ZQ','利息收入-AC类-证券','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LXSR_FVOCI_ZQ','利息收入-FVOCI类-证券','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LXSR_ZZSBD','利息收入-增值税备抵','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LXSR_HBLJY','利息收入-货币类交易','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LXSR_ZJ','利息收入-资金','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTYWSR_Z','其他业务收入','1','N','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTYWSR_SY','其他业务收入-收益','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTYWSR_ZZSBD','其他业务收入-增值税备抵','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('HDSY_Z','汇兑损益','1','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GYJZBDSY_Z','公允价值变动损益','1','N','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GYJZBDSY_ZC','公允价值变动损益-资产','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GYJZBDSY_FZ','公允价值变动损益-负债','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GYJZBDSY_ZZSBD','公允价值变动损益-增值税备抵','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GYJZBDSY_FJSBD','公允价值变动损益-附加税备抵','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_Z','投资收益','1','N','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_AC_JCSR','投资收益-AC类-价差收入','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_FVTPL_JCSR','投资收益-FVTPL类-价差收入','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_FVTPL_GLSR','投资收益-FVTPL类-股利收入','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_FVTPL_LXSR','投资收益-FVTPL类-利息收入','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_FVOCI_JCSR','投资收益-FVOCI类-价差收入','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_FVOCI_GLSR','投资收益-FVOCI类-股利收入','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_LXSR_ZZSBD','投资收益-利息收入-增值税备抵','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_JCSR_ZZSBD','投资收益-价差收入-增值税备抵','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_CQGQ_JCSR','投资收益-长期股权-价差收入','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_CQGQ_GLSR','投资收益-长期股权-股利收入','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_FZ_JCSR','投资收益-负债-价差收入','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_FZ_LXZC','投资收益-负债-利息支出','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_YSGJ','投资收益-衍生工具','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TZSY_FYZC','投资收益-费用支出','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('SJJFJ_Z','税金及附加','1','N','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('ZZSZC_Z','增值税支出','1','N','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CJSZC','附加税支出','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YGCJSZC','预估附加税支出','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GLRBC_Z','管理人报酬','1','N','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GLRBC','管理人报酬-管理费','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CEGLF','管理人报酬-业绩报酬','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('JYFY','交易费用','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('TGF_Z','托管费','1','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('XSFWF_Z','销售服务费','1','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('GZGWF_Z','投资顾问费','1','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LXZC_Z','利息支出','1','N','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('LXZC','利息支出-证券','2','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTZC_Z','其他支出','1','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('QTFY_Z','其他费用','1','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('XYJZSS_Z','信用减值损失','1','Y','05', 'test', null, sysdate, 'system', sysdate);
insert into bok_subject_internal (SUBJECT_INTERNAL_CODE, SUBJECT_INTERNAL_NAME, SUBJECT_LEVEL, ACC_FLAG, SUBJECT_TYPE, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('YQNDSYTZ_Z','以前年度损益调整','1','Y','05', 'test', null, sysdate, 'system', sysdate);





