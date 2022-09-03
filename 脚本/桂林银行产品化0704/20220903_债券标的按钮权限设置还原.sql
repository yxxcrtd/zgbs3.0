update SYS_MENU set page_id = 'FinBondAsset001' where SYS_MENU.REMARK = '债券标的' and menu_id = 'ASSET_MANAGEMENT_001_002_014';
update SYS_PAGE set MENU_ID = 'ASSET_MANAGEMENT_001_002_014' where SYS_PAGE.PAGE_ID = 'FinBondAsset001';
update SYS_PAGE set MENU_ID = 'ASSET_MANAGEMENT_001_002_006' where SYS_PAGE.PAGE_ID = 'FinBondTargetAsset001';
