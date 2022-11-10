/**风险指标: 开放式理财产品不得直接或间接投资于未上市企业股权及其收（受）益权(穿透后) **/

DELETE FROM RSK_ISUP_INDICATOR WHERE IND_ID='16edbd55cd2544dea3bc4d67dc7ea729';
INSERT INTO RSK_ISUP_INDICATOR (IND_ID, IND_NAME, IND_TYPE, EXPR, SUP_TYPE, TIME_POINT, SUP_FREQUENCY, SUP_DIRECTION, WARN_COND, APPROVE_COND, FOUL_COND, IS_ADJUST, ADJUST, IS_ENABLED, MEMO, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, IS_EXCP_WHEN_DIV_ZERO, COMPARE_DAYS, INSTITUTION_CODE, SUP_DISPLAY_CLASSIFY, ASSET_THR_TYPE, INVEST_PROPORTY, IS_DISPLAY_ORIGINAL,SUP_DOC,IND_NUM)
VALUES('16edbd55cd2544dea3bc4d67dc7ea729','开放式理财产品不得直接或间接投资于未上市企业股权及其收（受）益权(穿透后)','FORBID_FUND','${FSMCPBDTZWSSQYGQCTH}','SUPERVISION','01,02,03','DAYTIME,DAYEND', null , null , null  , '{"threshold_1":{"value":"02","value_cn":"开放式理财产品投资未上市企业股权","fb_model":"Y"}}' , 'N' , null , '01' , null ,'system',null,sysdate,'system',sysdate, '03' , null , null , null  , null , null  , 'N' , null , 'SUP052042' );
