DELETE FROM SYS_MENU T
 WHERE T.PARENT_MENU_ID = 'SETTLE_MANAGEMENT_001_003'
   AND T.PAGE_ID IN ('EtrInvestBondInfoEx001',
                     'EtrBondToStock001',
                     'EtrStock001',
                     'EtrFundEx001',
                     'EtrRepoEx001');