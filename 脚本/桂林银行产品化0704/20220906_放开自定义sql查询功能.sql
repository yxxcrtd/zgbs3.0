delete from SYS_PAGE where PAGE_ID = 'SystemSqlQuery001';
INSERT INTO SYS_PAGE (PAGE_ID, SYSTEM_CODE, PAGE_URL, PAGE_FILE, MENU_ID, HEIGHT, WIDTH, IS_HP_SELECT, IS_NEED_WORKFLOW, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('SystemSqlQuery001', 'INV', '/touguan/system/systemsqlquery/SystemSqlQuery001', 'touguan/system/systemsqlquery/SystemSqlQuery001', null, null, null, '02', '02', '自定义SQL查询', 'system', null, sysdate, 'system', sysdate);

delete from SYS_MENU where SYS_MENU.MENU_ID = 'SYSTEM_001_004_004' and PAGE_ID = 'SystemSqlQuery001';
INSERT INTO SYS_MENU (MENU_ID, SYSTEM_CODE, ICON, PARENT_MENU_ID, PAGE_ID, ORDER_NO, IS_SPREAD, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('SYSTEM_001_004_004', 'SYS', null, 'SYSTEM_001_004', 'SystemSqlQuery001', 2, '02', '自定义SQL查询', 'system', null, sysdate, 'system', sysdate);
