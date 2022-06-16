update SYS_MENU set SYSTEM_CODE = 'ETR' where menu_id = 'SETTLE_MANAGEMENT_001_001_006' and  sys_menu.REMARK = '一级投标指令';
delete from sys_menu where sys_menu.MENU_ID = 'ASSET_MANAGEMENT_001_002_006' and sys_menu.REMARK = '债券标的';
delete from sys_menu where sys_menu.MENU_ID = 'ASSET_MANAGEMENT_001_002_010' and sys_menu.REMARK = '新发行债券标的';
delete from sys_menu where sys_menu.MENU_ID = 'DEAL_MANAGEMENT_001_002_001' and sys_menu.REMARK = '银行间债券一级投标';
delete from sys_menu where sys_menu.MENU_ID = 'DEAL_MANAGEMENT_001_002_002' and sys_menu.REMARK = '银行间债券中标缴款';
delete from sys_menu where sys_menu.MENU_ID = 'DEAL_MANAGEMENT_001_002_008' and sys_menu.REMARK = '银行间债券上市流通';
delete from sys_menu where sys_menu.MENU_ID = 'DEAL_MANAGEMENT_001_003_001' and sys_menu.REMARK = '场内债券一级投标';
delete from sys_menu where sys_menu.MENU_ID = 'DEAL_MANAGEMENT_001_003_002' and sys_menu.REMARK = '场内债券中标缴款';
delete from sys_menu where sys_menu.MENU_ID = 'DEAL_MANAGEMENT_001_003_009' and sys_menu.REMARK = '场内债券上市流通';