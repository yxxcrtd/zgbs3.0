

DELETE FROM SYS_PAGE T WHERE T.PAGE_ID = 'CurrencyTransfer001';
DELETE FROM SYS_WFL_BUSINESS_MODULE T WHERE T.PAGE_ID = 'CurrencyTransfer001';

insert into SYS_PAGE (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
values ('CurrencyTransfer001', 'REP', '/invest/trade/transfer/currencytransfer/CurrencyTransfer001', 'invest/trade/transfer/currencytransfer/CurrencyTransfer001', null, null, null, '02', '01', '货币型计划资产转仓', 'system', null, sysdate, 'system', sysdate);


insert into SYS_WFL_BUSINESS_MODULE (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('CurrencyTransfer001', 'CurrencyTransfer001_btn_save', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');

insert into SYS_WFL_BUSINESS_MODULE (PAGE_ID, ELEMENT_ID, MODULE_ID, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, IS_SEND_UORG)
values ('CurrencyTransfer001', 'CurrencyTransfer001_btn_submit', 'oneApproval', 'system', null, sysdate, 'system', sysdate, 'ALL', '02');
