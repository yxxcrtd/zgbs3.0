/**风险指标:开放式理财产品在前一估值日内，产品资产净值中不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产占比，应小于50%**/
 DELETE FROM RSK_ISUP_INDICATOR WHERE IND_ID='D937B14C26C7422DE055000000000001';
 INSERT INTO RSK_ISUP_INDICATOR (IND_ID,IND_NAME,IND_TYPE,EXPR,SUP_TYPE,TIME_POINT,SUP_FREQUENCY,SUP_DIRECTION,WARN_COND,APPROVE_COND,FOUL_COND,IS_ADJUST,ADJUST,IS_ENABLED,MEMO,CREATE_USER,CREATE_DEPT,CREATE_TIME,UPDATE_USER,UPDATE_TIME,IS_EXCP_WHEN_DIV_ZERO,COMPARE_DAYS,IS_DISPLAY_ORIGINAL,IND_NUM)
 VALUES('D937B14C26C7422DE055000000000001','开放式理财产品在前一估值日内，产品资产净值中不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产占比，应小于50%','LIMIT_CONCENT','${BJBHYJYSCBNJLGYJZZCGZ}/${CPJZCJZ}','SUPERVISION','03','DAYTIME,DAYEND','','{"threshold_1":{"op":">=","value":"45","unit":"PERCENT","is_model":"02"}}','{"threshold_1":{"op":">=","unit":"PERCENT","is_model":"02"}}','{"threshold_1":{"op":">=","value":"50","unit":"PERCENT","is_model":"02"}}','N','','01','','system',null,sysdate,'system',sysdate,'02','','Y','SUP102092');

 /**风险指标参数:不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值**/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='BJBHYJYSCBNJLGYJZZCGZ';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('BJBHYJYSCBNJLGYJZZCGZ','不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值','N','','system','C001',sysdate,'system',sysdate,'','','Y','String','REDIS','');

---监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='BJBHYJYSCBNJLGYJZZCGZ';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES('BJBHYJYSCBNJLGYJZZCGZ','01', 'SQL','SELECT 1 FROM DUAL', 'portfolioCode','N','system','C001',sysdate,'system',sysdate);

INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES('BJBHYJYSCBNJLGYJZZCGZ','02', 'SQL','SELECT 1 FROM DUAL', 'portfolioCode','N','system','C001',sysdate,'system',sysdate);

INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES('BJBHYJYSCBNJLGYJZZCGZ','03', 'SQL','SELECT 1 FROM DUAL', 'portfolioCode','N','system','C001',sysdate,'system',sysdate);

INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES('BJBHYJYSCBNJLGYJZZCGZ','04', 'SQL','SELECT 1 FROM DUAL', 'portfolioCode','N','system','C001',sysdate,'system',sysdate);

INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES('BJBHYJYSCBNJLGYJZZCGZ','05', 'SQL','SELECT 1 FROM DUAL', 'portfolioCode','N','system','C001',sysdate,'system',sysdate);

DECLARE
----事后(日间)
V_CONTENT_04 CLOB := '
WITH PROD AS (
    --不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT_TYPE FPT
    WHERE EXISTS (
        SELECT 1 FROM RSK_SPECIFIC_ASSET_TYPE RSAT WHERE RSAT.IS_LIQUIDITY2 = ''Y'' AND (
        (REGEXP_INSTR(RSAT.TYPE_1||''_''||RSAT.TYPE_2||''_''||RSAT.TYPE_3||''_''||RSAT.TYPE_4 ,
            FPT.TYPE_9||''_''||FPT.TYPE_10||''_''||FPT.TYPE_11||''_''||FPT.TYPE_12) >= 1 AND RSAT.FINPROD_ID IS NULL)
        OR FPT.FINPROD_ID = RSAT.FINPROD_ID)
    )
)
SELECT SUM(TRADE_AMT) RVALUE FROM (
    SELECT
        NVL(SUM(NVL(M.DSC_COST_AMT,0)+NVL(M.TDY_INTINCEXP,0)+NVL(M.TDY_FLOAT_INGPL,0)+NVL(M.DELAY_PAY_AMT,0)+NVL(M.TDY_LOSS_AMT,0)),0) TRADE_AMT
    FROM PTL_SEC_VALUTION M
    INNER JOIN PROD N ON M.FINPROD_ID = N.FINPROD_ID 
    WHERE M.CDATE = date''${valDate}'' - 1 AND  M.PORTFOLIO_ID IN ${portfolioCode}
  UNION ALL
    SELECT
        nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
      + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
      + nvl(sum(decode(n.PS, ''T46'', n.TRADE_AMT)), 0)
      + nvl(sum(decode(n.PS, ''T47'', n.TRADE_AMT)), 0)
      + nvl(sum(decode(n.PS, ''T48'', n.TRADE_AMT)), 0)
      + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T19'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T07'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T08'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T09'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T11'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T22'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T23'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T38'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0)
	  - nvl(sum(decode(n.PS, ''T100'', n.TRADE_AMT)), 0)
	  - nvl(sum(decode(n.PS, ''T13'', n.TRADE_AMT)), 0) AS TRADE_AMT
    FROM TRD_PRODUCT_DEAL_ADD m
    INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
    INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID 
    WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T46'', ''T47'', ''T48'', ''T52'', ''T22'', ''T23'', ''T39'', ''T38'', ''T100'', ''T13'')
  UNION ALL
    SELECT
        nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
      + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
      + nvl(sum(decode(n.PS, ''T46'', n.TRADE_AMT)), 0)
      + nvl(sum(decode(n.PS, ''T47'', n.TRADE_AMT)), 0)
      + nvl(sum(decode(n.PS, ''T48'', n.TRADE_AMT)), 0)
      + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T19'', n.TRADE_AMT)), 0) 
	  - nvl(sum(decode(n.PS, ''T100'', n.TRADE_AMT)), 0)
	  - nvl(sum(decode(n.PS, ''T13'', n.TRADE_AMT)), 0) AS TRADE_AMT
    FROM WFL_TRD_PRODUCT_DEAL_ADD m
    INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
    INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID 
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T46'', ''T47'', ''T48'', ''T52'', ''T100'', ''T13'')
)';

----事后(日终)
V_CONTENT_05 CLOB := 'WITH PROD AS (
    -- 不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT_TYPE FPT
    WHERE EXISTS (
        SELECT 1 FROM RSK_SPECIFIC_ASSET_TYPE RSAT WHERE RSAT.IS_LIQUIDITY2 = ''Y'' AND (
        (REGEXP_INSTR(RSAT.TYPE_1||''_''||RSAT.TYPE_2||''_''||RSAT.TYPE_3||''_''||RSAT.TYPE_4 ,
            FPT.TYPE_9||''_''||FPT.TYPE_10||''_''||FPT.TYPE_11||''_''||FPT.TYPE_12) >= 1 AND RSAT.FINPROD_ID IS NULL)
        OR FPT.FINPROD_ID = RSAT.FINPROD_ID)
    )
)

SELECT NVL(SUM(NVL(M.DSC_COST_AMT,0)+NVL(M.TDY_INTINCEXP,0)+NVL(M.TDY_FLOAT_INGPL,0)+NVL(M.DELAY_PAY_AMT,0)+NVL(M.TDY_LOSS_AMT,0)),0) RVALUE
FROM PTL_SEC_VALUTION M
INNER JOIN PROD N ON M.FINPROD_ID = N.FINPROD_ID 
WHERE M.CDATE = date''${valDate}'' AND  M.PORTFOLIO_ID IN ${portfolioCode}
';
BEGIN
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'BJBHYJYSCBNJLGYJZZCGZ' and PARA_GRANULARITY ='04';

    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'BJBHYJYSCBNJLGYJZZCGZ' and PARA_GRANULARITY = '05';
END;
/

----不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-买入预占（不算指标）
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_01';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_01','不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-买入预占（不算指标）','N','','system','',sysdate,'system',sysdate);

----不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-卖出预占（不算指标）
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_02';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_02','不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-卖出预占（不算指标）','N','','system','',sysdate,'system',sysdate);

----不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-买入实占（不算指标）
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_03';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_03','不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-买入实占（不算指标）','N','','system','',sysdate,'system',sysdate);

----不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-卖出实占（不算指标）
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_04';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_04','不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-卖出实占（不算指标）','N','','system','',sysdate,'system',sysdate);

----不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-买入预占
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_05';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_05','不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-买入预占','Y','','system','',sysdate,'system',sysdate);

----不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-卖出预占
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_06';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_06','不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-卖出预占','Y','','system','',sysdate,'system',sysdate);

----不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-买入实占
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_07';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_07','不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-买入实占','Y','','system','',sysdate,'system',sysdate);

----不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-卖出实占
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_08';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('EVENT_ID_BJBHYJYSCBNJLGYJZZCGZ_08','不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的资产估值-卖出实占','Y','','system','',sysdate,'system',sysdate);