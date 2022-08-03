delete from SYS_PAGE_ELEMENT where page_id in('CfetsTrdConfirm','ManualSettleQuery','ExchangeTransMatch001','ManualSettle001');

update SYS_PAGE set REMARK = '银行间成交确认' where PAGE_ID = 'CfetsTrdConfirm';
update SYS_PAGE set REMARK = '场内成交确认' where PAGE_ID = 'ExchangeTransMatch001';
update SYS_PAGE set REMARK = '场外交收' where PAGE_ID = 'ManualSettleQuery';
update SYS_PAGE set REMARK = '场内日间交收' where PAGE_ID = 'ManualSettle001';

-- 银行间成交确认
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('CfetsTrdConfirm', 'CfetsTrdConfirmDealTab','01', '01','', '成交匹配', 'system', '', sysdate, 'system', sysdate, 'C', '');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('CfetsTrdConfirm', 'CfetsTrdConfirmDeal_btn_failed','01', '01','', '发行失败', 'system', '', sysdate, 'system', sysdate, 'C', 'CfetsTrdConfirmDealTab');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('CfetsTrdConfirm', 'CfetsTrdConfirmDeal_btn_confirm','01', '01','', '成交确认', 'system', '', sysdate, 'system', sysdate, 'C', 'CfetsTrdConfirmDealTab');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('CfetsTrdConfirm', 'CfetsTrdConfirmDeal_btn_select','01', '01','', '查询可匹配成交回报', 'system', '', sysdate, 'system', sysdate, 'C', 'CfetsTrdConfirmDealTab');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('CfetsTrdConfirm', 'CfetsTrdConfirmDeal_btn_manual','01', '01','', '手工匹配', 'system', '', sysdate, 'system', sysdate, 'C', 'CfetsTrdConfirmDealTab');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('CfetsTrdConfirm', 'CfetsTrdConfirmDeal_btn_done','01', '01','', '查询可匹配交易执行', 'system', '', sysdate, 'system', sysdate, 'C', 'CfetsTrdConfirmDealTab');

insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('CfetsTrdConfirm', 'CfetsTrdConfirmConfirmTab','01', '01','', '匹配确认', 'system', '', sysdate, 'system', sysdate, 'C', '');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('CfetsTrdConfirm', 'CfetsTrdConfirmConfirm_btn_confirm','01', '01','', '匹配确认', 'system', '', sysdate, 'system', sysdate, 'C', 'CfetsTrdConfirmConfirmTab');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('CfetsTrdConfirm', 'CfetsTrdConfirmConfirm_btn_cancel','01', '01','', '匹配取消', 'system', '', sysdate, 'system', sysdate, 'C', 'CfetsTrdConfirmConfirmTab');

-- 场内成交确认
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ExchangeTransMatch001', 'ExchangeTransMatch001DealTab','01', '01','', '成交匹配', 'system', '', sysdate, 'system', sysdate, 'C', '');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ExchangeTransMatch001', 'ExchangeTransMatch001_deal_btn_match_search','01', '01','', '匹配查询', 'system', '', sysdate, 'system', sysdate, 'C', 'ExchangeTransMatch001DealTab');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ExchangeTransMatch001', 'ExchangeTransMatch001_deal_btn_transaction_macth','01', '01','', '成交匹配', 'system', '', sysdate, 'system', sysdate, 'C', 'ExchangeTransMatch001DealTab');

insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ExchangeTransMatch001', 'ExchangeTransMatch001SelectTab','01', '01','', '匹配查询', 'system', '', sysdate, 'system', sysdate, 'C', '');

-- 场外交收
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ManualSettleQuery', 'ManualSettleQueryCfetsTab','01', '01','', '银行间', 'system', '', sysdate, 'system', sysdate, 'C', '');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ManualSettleQuery', 'ManualSettleQueryCfets_btn_manualSettle','01', '01','', '交收', 'system', '', sysdate, 'system', sysdate, 'C', 'ManualSettleQueryCfetsTab');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ManualSettleQuery', 'ManualSettleQueryCfets_btn_back','01', '01','', '回溯', 'system', '', sysdate, 'system', sysdate, 'C', 'ManualSettleQueryCfetsTab');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ManualSettleQuery', 'ManualSettleQueryCfets_btn_manualSettleFail','01', '01','', '交收失败', 'system', '', sysdate, 'system', sysdate, 'C', 'ManualSettleQueryCfetsTab');

-- 场内日间交收
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ManualSettle001', 'ManualSettle001_btn_settle','01', '01','', '交收', 'system', '', sysdate, 'system', sysdate, 'C', '');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ManualSettle001', 'ManualSettle001_btn_back','01', '01','', '回溯', 'system', '', sysdate, 'system', sysdate, 'C', '');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ManualSettle001', 'ManualSettle001_btn_settledelay','01', '01','', '交收延期', 'system', '', sysdate, 'system', sysdate, 'C', '');
insert into SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID,ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG,PARENT_ELEMENT_ID)
values('ManualSettle001', 'ManualSettle001_btn_settlefail','01', '01','', '交收失败', 'system', '', sysdate, 'system', sysdate, 'C', '');
