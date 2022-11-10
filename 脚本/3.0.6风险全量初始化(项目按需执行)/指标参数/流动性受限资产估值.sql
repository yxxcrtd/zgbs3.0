/**风险指标参数: 流动性受限资产估值 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='LDXSXZCGZ';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('LDXSXZCGZ','流动性受限资产估值', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='LDXSXZCGZ';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('LDXSXZCGZ','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('LDXSXZCGZ','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('LDXSXZCGZ','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('LDXSXZCGZ','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('LDXSXZCGZ','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'WITH PROD AS (select distinct FINPROD_ID from (
    -- 资产到期向前第10个交易日
    SELECT FINPROD_ID FROM (
        SELECT FINPROD_ID, CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN FROM (
            SELECT m.FINPROD_ID, MH.CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO FROM (
                -- 存款类资产
                SELECT FPT.FINPROD_ID,FPT.MDATE,NVL(FPT.CALENDAR_ID,''01'') CALENDAR_ID FROM FIN_PRODUCT FPT
                LEFT JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
				INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FPT.FINPROD_ID
                WHERE FPT.MDATE IS NOT NULL
				-- 是否可提前支取条件
				AND FPA.IS_INADVANCE in (''01'',''02'')
				AND T1.TYPE_8 = ''F05'' AND FPT.MDATE >= DATE ''${valDate}'' - 1
                UNION ALL
                -- 逆回购
                SELECT FTP.FINPROD_ID,FTP.MDATE,NVL(FTP.CALENDAR_ID,''01'') CALENDAR_ID FROM FIN_TRADE_PRODUCT FTP
                LEFT JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                WHERE FTP.MDATE IS NOT NULL AND T1.TYPE_8 IN (''F07'',''F10'') AND TYPE_7 = ''04'' AND FTP.MDATE >= DATE ''${valDate}'' - 1
            ) m
            INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE  AND MH.IS_HOLIDAY = ''0''
        ) WHERE ROWNO <= 10
    ) WHERE RN = 1 AND CDATE > DATE ''${valDate}''
  UNION ALL
    -- 资产支持证券
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT FPT
    LEFT JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
    WHERE FPT.MDATE IS NOT NULL AND T1.TYPE_8 = ''F01'' AND TYPE_6 = ''B13'' AND FPT.MDATE >= DATE ''${valDate}'' - 1
  UNION ALL
    -- 流动性受限债券
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT_TYPE FPT
    WHERE EXISTS (
        SELECT 1 FROM RSK_SPECIFIC_ASSET_TYPE RSAT WHERE RSAT.IS_FLOW_LIMITED = ''Y'' AND (
        REGEXP_INSTR(RSAT.TYPE_1||''_''||RSAT.TYPE_2||''_''||RSAT.TYPE_3||''_''||RSAT.TYPE_4 ,
            FPT.TYPE_9||''_''||FPT.TYPE_10||''_''||FPT.TYPE_11||''_''||FPT.TYPE_12) >= 1
        OR FPT.FINPROD_ID = RSAT.FINPROD_ID)
    )
))
SELECT
    NVL(SUM(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) RVALUE,
    M.PORTFOLIO_ID RKEY
FROM PTL_SEC_VALUTION M
INNER JOIN PROD N ON M.FINPROD_ID = N.FINPROD_ID
WHERE M.CDATE = date''${valDate}'' - 1
GROUP BY M.PORTFOLIO_ID';

----事后(日间)
V_CONTENT_04 CLOB := 'WITH PROD AS (
    SELECT DISTINCT FINPROD_ID
    FROM (
             -- 资产到期向前第10个交易日
             SELECT FINPROD_ID
             FROM (
                      SELECT FINPROD_ID, CDATE, ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN
                      FROM (
                               SELECT m.FINPROD_ID,
                                      MH.CDATE,
                                      ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO
                               FROM (
                                        -- 存款类资产
                                        SELECT FPT.FINPROD_ID, FPT.MDATE, NVL(FPT.CALENDAR_ID, ''01'') CALENDAR_ID
                                        FROM FIN_PRODUCT FPT
                                                 LEFT JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
                                                 INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FPT.FINPROD_ID
                                        WHERE FPT.MDATE IS NOT NULL
                                          AND T1.TYPE_8 = ''F05''
                                          -- 是否可提前支取条件
                                          AND FPA.IS_INADVANCE in (''01'', ''02'')
                                          AND FPT.MDATE >= DATE ''${valDate}'' - 1
                                        UNION ALL
                                        -- 逆回购
                                        SELECT FTP.FINPROD_ID, FTP.MDATE, NVL(FTP.CALENDAR_ID, ''01'') CALENDAR_ID
                                        FROM FIN_TRADE_PRODUCT FTP
                                                 LEFT JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                                        WHERE FTP.MDATE IS NOT NULL
                                          AND T1.TYPE_8 IN (''F07'', ''F10'')
                                          AND TYPE_7 = ''04''
                                          AND FTP.MDATE >= DATE ''${valDate}'' - 1
                                    ) m
                                        INNER JOIN MST_HOLIDAY MH
                                                   ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND
                                                      MH.IS_HOLIDAY = ''0''
                           )
                      WHERE ROWNO <= 10
                  )
             WHERE RN = 1
               AND CDATE > DATE ''${valDate}''
             UNION ALL -- 资产支持证券
             SELECT FPT.FINPROD_ID
             FROM FIN_PRODUCT FPT
                      LEFT JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
             WHERE FPT.MDATE IS NOT NULL
               AND T1.TYPE_8 = ''F01''
               AND TYPE_6 = ''B13''
               AND FPT.MDATE >= DATE ''${valDate}'' - 1
             UNION ALL
             -- 流动性受限债券
             SELECT FPT.FINPROD_ID
             FROM FIN_PRODUCT_TYPE FPT
             WHERE EXISTS(
                           SELECT 1
                           FROM RSK_SPECIFIC_ASSET_TYPE RSAT
                           WHERE RSAT.IS_FLOW_LIMITED = ''Y''
                             AND (
                                       REGEXP_INSTR(RSAT.TYPE_1 || ''_'' || RSAT.TYPE_2 || ''_'' || RSAT.TYPE_3 || ''_'' ||
                                                    RSAT.TYPE_4,
                                                    FPT.TYPE_9 || ''_'' || FPT.TYPE_10 || ''_'' || FPT.TYPE_11 || ''_'' ||
                                                    FPT.TYPE_12) >= 1
                                   OR FPT.FINPROD_ID = RSAT.FINPROD_ID)
                       )
         ))
SELECT SUM(TRADE_AMT) RVALUE
FROM (
         SELECT NVL(SUM(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)), 0) TRADE_AMT
         FROM PTL_SEC_VALUTION M
                  INNER JOIN PROD N ON M.FINPROD_ID = N.FINPROD_ID
         WHERE M.CDATE = date ''${valDate}'' - 1
           AND M.PORTFOLIO_ID IN ${portfolioCode}
         UNION ALL
         SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
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
                    -
                nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0) AS TRADE_AMT
         FROM TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                  INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
         WHERE m.PORTFOLIO_ID IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND n.PS IN
               (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T52'', ''T22'', ''T23'', ''T39'', ''T38'')
         UNION ALL  SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''04'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T93'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T95'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T96'', n.TRADE_AMT)), 0)
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
                    -
                nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0) AS TRADE_AMT
         FROM TRD_PRODUCT_TEMP_DEAL_ADD m
                  INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID
                  INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
         WHERE m.PORTFOLIO_ID IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND n.PS IN
               (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T52'', ''T22'', ''T23'', ''T39'', ''T38'', ''04'',
                ''T93'', ''T95'', ''T96'')
         UNION ALL
         SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
                    -
                nvl(sum(decode(n.PS, ''T19'', n.TRADE_AMT)), 0) AS TRADE_AMT
         FROM WFL_TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                  INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE m.PORTFOLIO_ID IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T52'')
         UNION ALL SELECT nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0) AS TRADE_AMT
         FROM WFL_TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                  INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE n.counter_id IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND n.PS = ''T02''
           AND EXISTS(SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
     )';

----事后(日终)
V_CONTENT_05 CLOB := 'WITH PROD AS (select distinct FINPROD_ID from (
    -- 资产到期向前第10个交易日
    SELECT FINPROD_ID FROM (
        SELECT FINPROD_ID, CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN FROM (
            SELECT m.FINPROD_ID, MH.CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO FROM (
                -- 存款类资产
                SELECT FPT.FINPROD_ID,FPT.MDATE,NVL(FPT.CALENDAR_ID,''01'') CALENDAR_ID FROM FIN_PRODUCT FPT
                LEFT JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
				INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FPT.FINPROD_ID
                WHERE FPT.MDATE IS NOT NULL AND T1.TYPE_8 = ''F05''
				-- 是否可提前支取条件
				AND FPA.IS_INADVANCE in (''01'',''02'')
				AND FPT.MDATE >= DATE ''${valDate}'' - 1
                UNION ALL
                -- 逆回购
                SELECT FTP.FINPROD_ID,FTP.MDATE,NVL(FTP.CALENDAR_ID,''01'') CALENDAR_ID FROM FIN_TRADE_PRODUCT FTP
                LEFT JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                WHERE FTP.MDATE IS NOT NULL AND T1.TYPE_8 IN (''F07'',''F10'') AND TYPE_7 = ''04'' AND FTP.MDATE >= DATE ''${valDate}'' - 1
            ) m
            INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE  AND MH.IS_HOLIDAY = ''0''
        ) WHERE ROWNO <= 10
    ) WHERE RN = 1 AND CDATE > DATE ''${valDate}''
  UNION ALL
    -- 资产支持证券
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT FPT
    LEFT JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
    WHERE FPT.MDATE IS NOT NULL AND T1.TYPE_8 = ''F01'' AND TYPE_6 = ''B13'' AND FPT.MDATE >= DATE ''${valDate}'' - 1
    UNION ALL
    -- 流动性受限债券
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT_TYPE FPT
     WHERE EXISTS (
        SELECT 1 FROM RSK_SPECIFIC_ASSET_TYPE RSAT WHERE RSAT.IS_FLOW_LIMITED = ''Y'' AND (
        REGEXP_INSTR(RSAT.TYPE_1||''_''||RSAT.TYPE_2||''_''||RSAT.TYPE_3||''_''||RSAT.TYPE_4 ,
            FPT.TYPE_9||''_''||FPT.TYPE_10||''_''||FPT.TYPE_11||''_''||FPT.TYPE_12) >= 1
        OR FPT.FINPROD_ID = RSAT.FINPROD_ID)
    )
))
SELECT
    NVL(SUM(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) RVALUE
FROM PTL_SEC_VALUTION M
INNER JOIN PROD N ON M.FINPROD_ID = N.FINPROD_ID
WHERE M.CDATE = date''${valDate}'' AND M.PORTFOLIO_ID IN ${portfolioCode}';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'LDXSXZCGZ' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'LDXSXZCGZ' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'LDXSXZCGZ' and PARA_GRANULARITY = '05';
END;
/
