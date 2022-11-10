/**风险指标: 单产品债券加权平均久期不超过5年(穿透后) **/

DELETE FROM RSK_ISUP_INDICATOR WHERE IND_ID='10ecfe3a88d4441f975463052031fe4a';
INSERT INTO RSK_ISUP_INDICATOR (IND_ID, IND_NAME, IND_TYPE, EXPR, SUP_TYPE, TIME_POINT, SUP_FREQUENCY, SUP_DIRECTION, WARN_COND, APPROVE_COND, FOUL_COND, IS_ADJUST, ADJUST, IS_ENABLED, MEMO, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, IS_EXCP_WHEN_DIV_ZERO, COMPARE_DAYS, INSTITUTION_CODE, SUP_DISPLAY_CLASSIFY, ASSET_THR_TYPE, INVEST_PROPORTY, IS_DISPLAY_ORIGINAL,SUP_DOC,IND_NUM)
VALUES('10ecfe3a88d4441f975463052031fe4a','单产品债券加权平均久期不超过5年(穿透后)','LIMIT_TERM','${DCPZQJQPJJQCTH}','INTERNAL','03','DAYEND', null , '{"threshold_1":{"op":">","value":"5","unit":"NONE","is_model":"02"}}' , '{"threshold_1":{"op":">","unit":"NONE","is_model":"02"}}'  , '{"threshold_1":{"op":">","unit":"NONE","is_model":"02"}}' , 'N' , null , '01' , null ,'system',null,sysdate,'system',sysdate, '03' , null , null , null  , null , null  , 'Y' , null , 'INT010008' );
