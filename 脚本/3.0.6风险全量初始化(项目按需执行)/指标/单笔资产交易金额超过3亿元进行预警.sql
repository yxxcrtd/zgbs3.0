/**风险指标: 单笔资产交易金额超过3亿元进行预警 **/

DELETE FROM RSK_ISUP_INDICATOR WHERE IND_ID='b311e1023c734402a43f3023467a5379';
INSERT INTO RSK_ISUP_INDICATOR (IND_ID, IND_NAME, IND_TYPE, EXPR, SUP_TYPE, TIME_POINT, SUP_FREQUENCY, SUP_DIRECTION, WARN_COND, APPROVE_COND, FOUL_COND, IS_ADJUST, ADJUST, IS_ENABLED, MEMO, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, IS_EXCP_WHEN_DIV_ZERO, COMPARE_DAYS, INSTITUTION_CODE, SUP_DISPLAY_CLASSIFY, ASSET_THR_TYPE, INVEST_PROPORTY, IS_DISPLAY_ORIGINAL,SUP_DOC,IND_NUM)
VALUES('b311e1023c734402a43f3023467a5379','单笔资产交易金额超过3亿元进行预警','LIMIT_TRAN','${FV202001010001}','INTERNAL','01,02','', null , '{"threshold_1":{"op":">=","value":"30000","unit":"WRMB","is_model":"02"}}' , '{"threshold_1":{"op":">=","unit":"WRMB","is_model":"02"}}'  , '{"threshold_1":{"op":">=","unit":"WRMB","is_model":"02"}}' , 'N' , null , '01' , null ,'system',null,sysdate,'system',sysdate, '03' , null , null , null  , null , null  , 'Y' , null , 'INT011009' );
