/**风险指标参数: 资产组合的久期 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='ZCZHJQ';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('ZCZHJQ','资产组合的久期', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='ZCZHJQ';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('ZCZHJQ','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('ZCZHJQ','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('ZCZHJQ','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('ZCZHJQ','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('ZCZHJQ','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'select 1 from dual';

----事后(日间)
V_CONTENT_04 CLOB := 'WITH ASSET AS (
    -- 逆回购、拆出交易
    SELECT FINPROD_ID, TO_NUMBER(MDATE - DATE''${valDate}'') / 365 LAST_PERIOD
    FROM FIN_TRADE_PRODUCT
    WHERE FINPROD_TYPE2 IN (''F03'', ''F05'', ''F09'')
    UNION
    -- 活期存放
    SELECT FP.FINPROD_ID, 0 LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F05''
      AND FPT.TYPE_6 = ''D03''
    UNION
    -- 非活期存放
    SELECT FP.FINPROD_ID, TO_NUMBER(FP.MDATE - DATE''${valDate}'') / 365 LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F05''
      AND FPT.TYPE_6 != ''D03''
    UNION
    -- 债券（优先股、永续债（非含权））
    SELECT FP.FINPROD_ID,
           1 / (SELECT NVL(FCCR.YIELD, FRT.RATE)
                FROM FIN_CASH_INFO CASH
                         LEFT JOIN FIN_RATE_INFO FRT ON FRT.CASH_ID = CASH.CASH_ID
                         LEFT JOIN (SELECT FINPROD_ID, MAX(EFF_DATE) AS EFF_DATE
                                    FROM FIN_CASH_CALC_RULE
                                    WHERE EFF_DATE <= DATE''${valDate}''
                                    GROUP BY FINPROD_ID) TEMPT8
                                   ON CASH.FINPROD_ID = TEMPT8.FINPROD_ID
                         LEFT JOIN FIN_CASH_CALC_RULE FCCR
                                   ON FCCR.FINPROD_ID = CASH.FINPROD_ID AND FCCR.CASH_ID = CASH.CASH_ID AND
                                      FCCR.EFF_DATE = TEMPT8.EFF_DATE
                WHERE NVL(FCCR.YIELD, FRT.RATE) IS NOT NULL
                  AND CASH.FINPROD_ID = FP.FINPROD_ID
           ) LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F01'', ''F17'')
      AND (FPT.TYPE_6 IN (''B17'') OR (FP.IS_SUS = ''Y'' AND FP.IS_RIGHT = ''N''))
    UNION
    -- 债券（永续债（含权））
    SELECT FP.FINPROD_ID,
           CASE
               WHEN T1.MDATE IS NOT NULL THEN TO_NUMBER(T1.MDATE - DATE''${valDate}'') / 365
               ELSE 1 / (
                   SELECT NVL(FCCR.YIELD, FRT.RATE)
                   FROM FIN_CASH_INFO CASH
                            LEFT JOIN FIN_RATE_INFO FRT ON FRT.CASH_ID = CASH.CASH_ID
                            LEFT JOIN (SELECT FINPROD_ID, MAX(EFF_DATE) AS EFF_DATE
                                       FROM FIN_CASH_CALC_RULE
                                       WHERE EFF_DATE <= DATE''${valDate}''
                                       GROUP BY FINPROD_ID) TEMPT8
                                      ON CASH.FINPROD_ID = TEMPT8.FINPROD_ID
                            LEFT JOIN FIN_CASH_CALC_RULE FCCR
                                      ON FCCR.FINPROD_ID = CASH.FINPROD_ID AND FCCR.CASH_ID = CASH.CASH_ID AND
                                         FCCR.EFF_DATE = TEMPT8.EFF_DATE
                   WHERE NVL(FCCR.YIELD, FRT.RATE) IS NOT NULL
                     AND CASH.FINPROD_ID = FP.FINPROD_ID
               ) END LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
             LEFT JOIN (
        SELECT FINPROD_ID, MIN(EXERCISE_DATE) MDATE
        FROM FIN_EXERCISE_PLAN
        WHERE EXERCISE_DATE > DATE''${valDate}''
        GROUP BY FINPROD_ID
    ) T1 ON T1.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F01'', ''F17'')
      AND FP.IS_SUS = ''Y''
      AND FP.IS_RIGHT = ''Y''
    UNION
    -- 债券（非含权）
    SELECT FP.FINPROD_ID,
           CASE  -- 贴现、零息、利随本清
               WHEN FP.COUPON_SPECIES IN (''02'', ''03'', ''04'') THEN TO_NUMBER(FP.MDATE - DATE''${valDate}'') / 365
               ELSE TO_NUMBER(NVL(FXZ.MDATE, FP.MDATE) - DATE''${valDate}'') / 365 END LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
             LEFT JOIN (
        SELECT FCC.FINPROD_ID, MIN(FCC.EFF_DATE) MDATE
        FROM FIN_CASH_CALC_RULE FCC
                 INNER JOIN FIN_PRODUCT T2 ON FCC.FINPROD_ID = T2.FINPROD_ID
                 LEFT JOIN FIN_CASH_INFO T5 ON T5.FINPROD_ID = FCC.FINPROD_ID AND T5.CASH_TYPE_2 = ''C02''
                 LEFT JOIN FIN_RATE_INFO T6 ON T6.CASH_ID = T5.CASH_ID
            AND T6.EFF_DATE = T2.VDATE
        WHERE FCC.EFF_DATE > DATE''${valDate}''
        GROUP BY FCC.FINPROD_ID
    ) FXZ ON FXZ.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F01'', ''F17'')
      AND FP.IS_RIGHT = ''N''
      AND FP.IS_SUS = ''N''
    UNION
    -- 债券（含权）
    SELECT FP.FINPROD_ID,
           CASE -- 贴现、零息、利随本清
               WHEN FP.COUPON_SPECIES IN (''02'', ''03'', ''04'') THEN TO_NUMBER(FP.MDATE - DATE''${valDate}'') / 365
               WHEN NVL(FXZ.MDATE, T1.MDATE) < T1.MDATE
                   THEN TO_NUMBER(NVL(FXZ.MDATE, FP.MDATE) - DATE''${valDate}'') / 365
               ELSE TO_NUMBER(NVL(T1.MDATE, FP.MDATE) - DATE''${valDate}'') / 365 END LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
        AND FPT.TYPE_8 IN (''F01'', ''F17'')
             LEFT JOIN (
        SELECT FCC.FINPROD_ID, MIN(FCC.EFF_DATE) MDATE
        FROM FIN_CASH_CALC_RULE FCC
                 INNER JOIN FIN_PRODUCT T2 ON FCC.FINPROD_ID = T2.FINPROD_ID
                 LEFT JOIN FIN_CASH_INFO T5 ON T5.FINPROD_ID = FCC.FINPROD_ID AND T5.CASH_TYPE_2 = ''C02''
                 LEFT JOIN FIN_RATE_INFO T6 ON T6.CASH_ID = T5.CASH_ID
            AND T6.EFF_DATE = T2.VDATE
        WHERE FCC.EFF_DATE > DATE''${valDate}''
        GROUP BY FCC.FINPROD_ID
    ) FXZ ON FXZ.FINPROD_ID = FP.FINPROD_ID
             LEFT JOIN (
        SELECT FINPROD_ID, MIN(EXERCISE_DATE) MDATE
        FROM FIN_EXERCISE_PLAN
        WHERE EXERCISE_DATE > DATE''${valDate}''
          AND EXERCISE_TYPE = ''02''
        GROUP BY FINPROD_ID
    ) T1 ON T1.FINPROD_ID = FP.FINPROD_ID
    WHERE FP.IS_RIGHT = ''Y''
      AND FP.IS_SUS = ''N''
    UNION
    -- 非标债权
    SELECT FP.FINPROD_ID,
           TO_NUMBER(NVL(FXZ.MDATE, FP.MDATE) - DATE''${valDate}'') / 365 LAST_PERIOD -- 下一个利率调整日-计算日
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
             LEFT JOIN (
        SELECT FCC.FINPROD_ID, MIN(FCC.EFF_DATE) MDATE
        FROM FIN_CASH_CALC_RULE FCC
        INNER JOIN FIN_PRODUCT T2 ON FCC.FINPROD_ID = T2.FINPROD_ID
        LEFT JOIN FIN_CASH_INFO T5 ON T5.FINPROD_ID = FCC.FINPROD_ID AND T5.CASH_TYPE_2 = ''C02''
        LEFT JOIN FIN_RATE_INFO T6 ON T6.CASH_ID = T5.CASH_ID AND T6.EFF_DATE = T2.VDATE
        WHERE FCC.EFF_DATE > DATE''${valDate}''
        GROUP BY FCC.FINPROD_ID
    ) FXZ ON FXZ.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F06''
    UNION
    -- 计划类净值型、计划类货币型
    SELECT FP.FINPROD_ID, 1 / 365 LAST_PERIOD
    FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F04'', ''F09'')
    UNION
    -- 基金
    SELECT FP.FINPROD_ID, DECODE(PLAN.MDATE, NULL, 1 / 365, TO_NUMBER(PLAN.MDATE - DATE''${valDate}'') / 365) LAST_PERIOD
    FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    LEFT JOIN (
        -- 下一个开放赎回起始日
        SELECT MIN(FOP.OPEN_DATE_STR) MDATE, FOP.FINPROD_ID
        FROM FIN_OPEN_PLAN FOP
        INNER JOIN FIN_PRODUCT_TYPE FPT ON FOP.FINPROD_ID = FPT.FINPROD_ID
        WHERE OPEN_DATE_STR > DATE''${valDate}''
          AND FPT.TYPE_8 = ''F02''
          AND OPEN_TYPE IN (''02'', ''03'')
        GROUP BY FOP.FINPROD_ID
    ) PLAN ON PLAN.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F02''
    UNION
    -- 信用保护凭证
    SELECT FP.FINPROD_ID, 0 LAST_PERIOD
    FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F37''
    UNION
    -- 其他资产
    SELECT FP.FINPROD_ID, TO_NUMBER(FP.MDATE - DATE''${valDate}'') / 365 LAST_PERIOD
    FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 NOT IN (''F01'', ''F02'', ''F04'', ''F05'', ''F06'', ''F09'', ''F17'', ''F37'')
),
BOND_VALUTION AS (
 SELECT D.SEC_ID, MAX(D.VALUE_DATE) VALUE_DATE
 FROM MST_BOND_VALUATION_INFO D
          INNER JOIN FIN_PRODUCT B ON B.FINPROD_MARKET_ID = D.SEC_ID
 WHERE VALUE_DATE <= DATE''${valDate}''
   AND ((B.TRADE_MARKET != ''01'' AND D.VALUE_SOURCE = ''CSD'') OR
        (B.TRADE_MARKET = ''01'' AND D.VALUE_SOURCE = ''CBC''))
 GROUP BY SEC_ID
),
ASSETJQ AS (
 -- 有标的资产
 SELECT CASE
            WHEN B.FINPROD_TYPE2 = ''F01'' AND T6.INT_TYPE = ''02''
                AND (D.SDURATION IS NOT NULL OR D.INTEREST_DURATION IS NOT NULL)
                THEN (NVL(D.SDURATION, 0) + NVL(D.INTEREST_DURATION, 0))
            WHEN B.FINPROD_TYPE2 = ''F01'' AND D.M_DURATION IS NOT NULL THEN D.M_DURATION
            ELSE ASSET.LAST_PERIOD
            END REMAINPERIOD,
        B.FINPROD_ID,
        B.FINPROD_TYPE2,
        NULL    PRIN
 FROM FIN_PRODUCT B
 INNER JOIN ASSET ON B.FINPROD_ID = ASSET.FINPROD_ID
 LEFT JOIN FIN_CASH_INFO T5 ON T5.FINPROD_ID = B.FINPROD_ID AND T5.CASH_TYPE_2 = ''C02''
 LEFT JOIN FIN_RATE_INFO T6 ON T6.CASH_ID = T5.CASH_ID AND T6.EFF_DATE = B.VDATE
 LEFT JOIN BOND_VALUTION TEMP ON TEMP.SEC_ID = B.FINPROD_MARKET_ID
 LEFT JOIN MST_BOND_VALUATION_INFO D
            ON B.FINPROD_MARKET_ID = D.SEC_ID AND D.VALUE_DATE = TEMP.VALUE_DATE AND
               ((B.TRADE_MARKET != ''01'' AND D.VALUE_SOURCE = ''CSD'') OR
                (B.TRADE_MARKET = ''01'' AND D.VALUE_SOURCE = ''CBC''))
 WHERE B.FINPROD_TYPE2 != ''F16''
   AND ASSET.LAST_PERIOD >= 0
   AND (B.MDATE >= DATE''${valDate}'' OR B.MDATE IS NULL)
 UNION ALL
 -- 无标的资产
 SELECT ASSET.LAST_PERIOD REMAINPERIOD, E.FINPROD_ID, E.FINPROD_TYPE2, E.PRIN
 FROM FIN_TRADE_PRODUCT E
 INNER JOIN ASSET ON E.FINPROD_ID = ASSET.FINPROD_ID
 WHERE E.BRANCH = 0
   AND (E.FINPROD_TYPE2 IS NULL OR E.FINPROD_TYPE2 IN (''F03'', ''F05'', ''F09''))
   AND ASSET.LAST_PERIOD >= 0
)
SELECT NVL(SUM(
       CASE WHEN TARGETDATA.REMAINPERIOD is null then 0 else NVL(TARGETDATA.REMAINPERIOD, 0) end
       * NVL(TARGETDATA.JIAQUANGUIZE, 0) / DATATWO.AMOUNT), 0) AS RVALUE
FROM (
         SELECT nvl(PSV.DSC_COST_AMT,0)+nvl(PSV.TDY_INTINCEXP,0)+nvl(PSV.TDY_FLOAT_INGPL,0)+nvl(PSV.DELAY_PAY_AMT,0)+nvl(PSV.TDY_LOSS_AMT,0) JIAQUANGUIZE,
                PSV.FINPROD_ID,
                JQ.REMAINPERIOD
         FROM PTL_SEC_VALUTION PSV
         INNER JOIN ASSETJQ JQ ON JQ.FINPROD_ID = PSV.FINPROD_ID
         LEFT JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = JQ.FINPROD_ID
         WHERE PSV.PORTFOLIO_ID IN ${portfolioCode}
           AND PSV.CDATE = DATE''${valDate}'' - 1
           AND ((FPT.TYPE_8 IN (''F04'', ''F09'')) OR FPT.TYPE_8 NOT IN (''F04'', ''F09''))
         UNION ALL
         SELECT T2.TRADE_AMT AS JIAQUANGUIZE, T2.FINPROD_ID, T2.REMAINPERIOD
         FROM (
                  SELECT SUM(CASE
                             WHEN n.PS IN (''T01'', ''T04'', ''T52'', ''T25'', ''T64'') THEN NVL(n.TRADE_AMT, 0)
                             WHEN n.PS = ''T49'' THEN NVL(-100 * n.SHARE_AMT, 0)
                             ELSE NVL(-1 * n.TRADE_AMT, 0)
                      END) TRADE_AMT,
                         T2.FINPROD_ID,
                         T2.REMAINPERIOD
                  FROM TRD_PRODUCT_DEAL N
                           INNER JOIN TRD_PRODUCT_DEAL_ADD T4 ON N.TRADE_ID = T4.TRADE_ID
                           INNER JOIN ASSETJQ T2 ON N.FINPROD_ID = T2.FINPROD_ID
                  WHERE T4.PORTFOLIO_ID IN ${portfolioCode}
                    AND N.TRADE_DATE = DATE''${valDate}''
                    AND n.PS IN
                        (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'', ''T25'',
                         ''T49'', ''T64'', ''T63'', ''T93'', ''T95'', ''T96'', ''T54'', ''T55'', ''T56'')
                    AND N.TRADE_STATUS = ''01''
                    AND N.IS_CANCEL = ''N''
                  GROUP BY T2.FINPROD_ID, T2.REMAINPERIOD
                  UNION ALL
                  SELECT SUM(CASE
                             WHEN n.PS IN (''T01'', ''T04'', ''T52'', ''T25'', ''T64'', ''04'') THEN NVL(n.TRADE_AMT, 0)
                             WHEN n.PS = ''T49'' THEN NVL(-100 * n.SHARE_AMT, 0)
                             ELSE NVL(-1 * n.TRADE_AMT, 0)
                      END) TRADE_AM,
                         T2.FINPROD_ID,
                         T2.REMAINPERIOD
                  FROM TRD_PRODUCT_TEMP_DEAL N
                  INNER JOIN TRD_PRODUCT_TEMP_DEAL_ADD T4 ON N.TRADE_ID = T4.TRADE_ID
                  INNER JOIN ASSETJQ T2 ON N.FINPROD_ID = T2.FINPROD_ID
                  WHERE T4.PORTFOLIO_ID IN ${portfolioCode}
                    AND N.TRADE_DATE = DATE''${valDate}''
                    AND n.PS IN
                        (''T01'', ''04'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',
                         ''T25'', ''T49'', ''T64'', ''T63'', ''T93'', ''T95'', ''T96'', ''T54'', ''T55'', ''T56'')
                    AND N.TRADE_STATUS = ''01''
                    AND N.IS_CANCEL = ''N''
                  GROUP BY T2.FINPROD_ID, T2.REMAINPERIOD
                  UNION ALL
                  SELECT SUM(CASE
                             WHEN n.PS IN (''T01'', ''T04'', ''T52'', ''T64'') THEN NVL(n.TRADE_AMT, 0)
                             WHEN n.PS = ''T49'' THEN NVL(-1 * n.SHARE_AMT, 0)
                             ELSE NVL(-1 * n.TRADE_AMT, 0)
                      END) TRADE_AM,
                         T2.FINPROD_ID,
                         T2.REMAINPERIOD
                  FROM WFL_TRD_PRODUCT_DEAL_ADD M
                  INNER JOIN WFL_TRD_PRODUCT_DEAL N ON M.TRADE_ID = N.TRADE_ID AND N.TRADE_STATUS != ''03''
                  INNER JOIN SYS_WFL_BUSINESS_LIST SWBL ON N.WORKFLOW_ID = SWBL.SEQ_NO AND SWBL.PROCESS_STATE != ''BACK''
                  INNER JOIN ASSETJQ T2 ON N.FINPROD_ID = T2.FINPROD_ID
                  WHERE M.PORTFOLIO_ID IN ${portfolioCode}
                    AND N.TRADE_DATE = DATE''${valDate}''
                    AND T2.FINPROD_TYPE2 NOT IN (''F02'', ''F04'', ''F08'', ''F25'')
                    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T52'', ''T49'', ''T64'', ''T63'')
                    AND N.TRADE_STATUS = ''01''
                    AND N.IS_CANCEL = ''N''
                  GROUP BY T2.FINPROD_ID, T2.REMAINPERIOD
                  UNION ALL
                  ----无标的资产流程中买入标的还没有生成的场景
                  SELECT SUM(CASE
                             WHEN n.PS IN (''T01'', ''T04'', ''T52'', ''T64'') THEN NVL(n.TRADE_AMT, 0)
                             WHEN n.PS = ''T49'' THEN NVL(-1 * n.SHARE_AMT, 0)
                             ELSE NVL(-1 * n.TRADE_AMT, 0) END) TRADE_AM,
                         N.FINPROD_ID,
                         case
                             when (TO_NUMBER(MDATE - DATE''${valDate}'') / 365 < 0) then 0
                             else TO_NUMBER(MDATE - DATE''${valDate}'') / 365 end REMAINPERIOD
                  FROM WFL_TRD_PRODUCT_DEAL_ADD M
                  INNER JOIN WFL_TRD_PRODUCT_DEAL N ON M.TRADE_ID = N.TRADE_ID AND N.TRADE_STATUS != ''03''
                  INNER JOIN SYS_WFL_BUSINESS_LIST SWBL
                  ON N.WORKFLOW_ID = SWBL.SEQ_NO AND SWBL.PROCESS_STATE != ''BACK''
                  WHERE M.PORTFOLIO_ID IN ${portfolioCode}
                    AND N.TRADE_DATE = DATE''${valDate}''
                    AND N.FINPROD_TYPE2 IN (''F03'', ''F05'', ''F09'')
                    AND n.PS IN (''T01'')
                    AND N.TRADE_STATUS = ''01''
                    AND N.IS_CANCEL = ''N''
                  GROUP BY N.FINPROD_ID, MDATE
                  UNION ALL
                  SELECT SUM(NVL(N.TRADE_AMT, 0)) AS TRADE_AMT, T2.FINPROD_ID, T2.REMAINPERIOD
                  FROM WFL_TRD_PRODUCT_DEAL_ADD M
                  INNER JOIN WFL_TRD_PRODUCT_DEAL N ON M.TRADE_ID = N.TRADE_ID AND N.TRADE_STATUS != ''03''
                  INNER JOIN SYS_WFL_BUSINESS_LIST SWBL ON N.WORKFLOW_ID = SWBL.SEQ_NO AND SWBL.PROCESS_STATE != ''BACK''
                  INNER JOIN ASSETJQ T2 ON N.FINPROD_ID = T2.FINPROD_ID
                  WHERE N.COUNTER_ID IN ${portfolioCode}
                    AND N.TRADE_DATE = DATE''${valDate}''
                    AND N.PS = ''T02''
                    AND N.TRADE_STATUS = ''01''
                    AND N.IS_CANCEL = ''N''
                    AND EXISTS(
                          SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''IS_AUTO_GENERATE_NEGATE_DEAL'' AND PARAM_VALUE = ''Y'')
                  GROUP BY T2.FINPROD_ID, T2.REMAINPERIOD
              ) T2
         UNION ALL
         SELECT A.AMOUNT AS JIAQUANGUIZE, A.ACCT_ID AS FINPROD_ID, 0 AS REMAINPERIOD
         FROM PTL_CASH_POSITION A
         WHERE A.PORTFOLIO_ID IN ${portfolioCode}
           AND A.CDATE = DATE''${valDate}''
           AND A.AMOUNT > 0
           AND A.HODING_TYPE = ''03''
     ) TARGETDATA,
     (
         SELECT SUM(AMOUNT) AS AMOUNT
         FROM (
                  SELECT SUM(NVL(TDY_ASSET_NETVALUE, 0)) AS AMOUNT
                  FROM PTL_PORTFOLIO_VALUATION T1
                  WHERE T1.CDATE = DATE''${valDate}'' - 1
                    AND T1.PORTFOLIO_ID IN ${portfolioCode}
                  GROUP BY T1.PORTFOLIO_ID

                  union all
                  --1.产品端交易表
                  select SUM(
                             CASE
                             WHEN n.PS IN (''C1'', ''C2'', ''C3'', ''C10'', ''C15'', ''T98'')
                                 THEN NVL(n.TRADE_AMT, 0)
                             ELSE NVL(-1 * n.TRADE_AMT, 0)
                             END) amount
                  from TRD_PRODUCT_DEAL_ADD m
                           inner join TRD_PRODUCT_DEAL n on m.TRADE_ID = n.TRADE_ID
                  where m.PORTFOLIO_ID in ${portfolioCode}
                    and n.TRADE_DATE = date''${valDate}''
                    AND n.PS IN (''C1'',''C2'',''C3'',''C4'',''C5'',''C6'',''C7'',''C9'',''C10'',''C14'',''C15'',''C16'',''C17'',''C18'',''C19'',''C20'',''T98'',''T99'')
                    AND n.TRADE_STATUS = ''01''
                    AND n.IS_CANCEL = ''N''
                  GROUP BY m.PORTFOLIO_ID
                  union ALL
                  --2.回购交易
                  select SUM(
                         CASE
                         WHEN n.PS = ''T01''
                             THEN NVL(n.TRADE_AMT, 0)
                         ELSE NVL(-1 * n.TRADE_AMT, 0)
                         END) amount
                  from TRD_PRODUCT_DEAL_ADD m
                  inner join TRD_PRODUCT_DEAL n
                  on m.TRADE_ID = n.TRADE_ID and n.FINPROD_TYPE2 in (''F02'', ''F08'', ''F25'', ''F04'')
                  where m.PORTFOLIO_ID in ${portfolioCode}
                    and n.TRADE_DATE = date''${valDate}''
                    and n.PS in (''T01'', ''T07'', ''T54'', ''T55'', ''T56'')
                    AND n.TRADE_STATUS = ''01''
                    AND n.IS_CANCEL = ''N''
                  GROUP BY m.PORTFOLIO_ID
                  union all
                  --3.日间交易表（场内交易：回购）
                  select SUM(
                         CASE
                         WHEN n.PS IN (''T01'', ''03'')
                             THEN NVL(n.TRADE_AMT, 0)
                         ELSE NVL(-1 * n.TRADE_AMT, 0)
                         END) amount
                  from TRD_PRODUCT_TEMP_DEAL_ADD m
                  inner join TRD_PRODUCT_TEMP_DEAL n on m.TRADE_ID = n.TRADE_ID and n.FINPROD_TYPE2 in (''F02'', ''F08'', ''F25'', ''F04'')
                  where m.PORTFOLIO_ID in ${portfolioCode}
                    and n.TRADE_DATE = date''${valDate}''
                    and n.PS in (''03'',''T01'',''T07'',''T22'',''T23'',''T54'',''T55'', ''T56'',''T93'',''T95'',''T96'')
                    AND n.TRADE_STATUS = ''01''
                    AND n.IS_CANCEL = ''N''
                  GROUP BY m.PORTFOLIO_ID
                  union ALL
                  --4.回购交易流程表
                  select SUM(NVL(n.TRADE_AMT, 0)) amount
                  from WFL_TRD_PRODUCT_DEAL_ADD m
                  inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID = n.TRADE_ID and n.FINPROD_TYPE2 in (''F02'', ''F08'', ''F25'', ''F04'')
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl
                  on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                  where m.PORTFOLIO_ID in ${portfolioCode}
                    and n.TRADE_DATE = date''${valDate}''
                    and n.PS IN (''T01'', ''03'')
                    AND n.TRADE_STATUS = ''01''
                    AND n.IS_CANCEL = ''N''
                  GROUP BY m.PORTFOLIO_ID
              )) DATATWO';

----事后(日终)
V_CONTENT_05 CLOB := 'WITH ASSET AS (
    -- 逆回购、拆出交易
    SELECT FINPROD_ID,TO_NUMBER(MDATE - DATE''${valDate}'' ) / 365 LAST_PERIOD FROM FIN_TRADE_PRODUCT
    WHERE FINPROD_TYPE2 IN (''F03'',''F05'',''F09'')
    UNION
    -- 活期存放
    SELECT FP.FINPROD_ID,0 LAST_PERIOD FROM FIN_PRODUCT FP
                                                INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F05'' AND FPT.TYPE_6 = ''D03''
    UNION
    -- 非活期存放
    SELECT FP.FINPROD_ID,TO_NUMBER(FP.MDATE - DATE''${valDate}'' ) / 365 LAST_PERIOD FROM FIN_PRODUCT FP
                                                                                            INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F05'' AND FPT.TYPE_6 != ''D03''
    UNION
    -- 债券（优先股、永续债（非含权））
    SELECT FP.FINPROD_ID, 1/( SELECT NVL(FCCR.YIELD, FRT.RATE) FROM FIN_CASH_INFO CASH
                                                                        LEFT JOIN FIN_RATE_INFO FRT ON FRT.CASH_ID = CASH.CASH_ID
                                                                        LEFT JOIN (SELECT FINPROD_ID, MAX(EFF_DATE) AS EFF_DATE FROM FIN_CASH_CALC_RULE WHERE EFF_DATE <= DATE''${valDate}''  GROUP BY FINPROD_ID ) TEMPT8
                                                                                  ON CASH.FINPROD_ID = TEMPT8.FINPROD_ID
                                                                        LEFT JOIN FIN_CASH_CALC_RULE FCCR ON FCCR.FINPROD_ID=CASH.FINPROD_ID AND FCCR.CASH_ID = CASH.CASH_ID AND FCCR.EFF_DATE = TEMPT8.EFF_DATE
                              WHERE NVL(FCCR.YIELD, FRT.RATE) IS NOT NULL AND CASH.FINPROD_ID = FP.FINPROD_ID
    ) LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F01'',''F17'') AND (FPT.TYPE_6 IN (''B17'') OR (FP.IS_SUS = ''Y'' AND FP.IS_RIGHT = ''N''))
    UNION
    -- 债券（永续债（含权））
    SELECT FP.FINPROD_ID,
           CASE WHEN T1.MDATE IS NOT NULL THEN TO_NUMBER(T1.MDATE - DATE''${valDate}'' ) / 365
                ELSE 1/(
                    SELECT NVL(FCCR.YIELD, FRT.RATE) FROM FIN_CASH_INFO CASH
                                                              LEFT JOIN FIN_RATE_INFO FRT ON FRT.CASH_ID = CASH.CASH_ID
                                                              LEFT JOIN (SELECT FINPROD_ID, MAX(EFF_DATE) AS EFF_DATE FROM FIN_CASH_CALC_RULE WHERE EFF_DATE <= DATE''${valDate}''  GROUP BY FINPROD_ID ) TEMPT8
                                                                        ON CASH.FINPROD_ID = TEMPT8.FINPROD_ID
                                                              LEFT JOIN FIN_CASH_CALC_RULE FCCR ON FCCR.FINPROD_ID=CASH.FINPROD_ID AND FCCR.CASH_ID = CASH.CASH_ID AND FCCR.EFF_DATE = TEMPT8.EFF_DATE
                    WHERE NVL(FCCR.YIELD, FRT.RATE) IS NOT NULL AND CASH.FINPROD_ID = FP.FINPROD_ID
                ) END LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
             LEFT JOIN (
        SELECT FINPROD_ID,MIN(EXERCISE_DATE) MDATE FROM FIN_EXERCISE_PLAN
        WHERE EXERCISE_DATE > DATE''${valDate}''
        GROUP BY FINPROD_ID
    ) T1 ON T1.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F01'',''F17'') AND FP.IS_SUS = ''Y'' AND FP.IS_RIGHT = ''Y''
    UNION
    -- 债券（非含权）
    SELECT FP.FINPROD_ID,
           -- 贴现、零息、利随本清
           CASE WHEN FP.COUPON_SPECIES IN (''02'',''03'',''04'') THEN TO_NUMBER(FP.MDATE - DATE''${valDate}'' ) / 365
                ELSE TO_NUMBER(NVL(FXZ.MDATE, FP.MDATE) - DATE''${valDate}'' ) / 365 END LAST_PERIOD FROM FIN_PRODUCT FP
                                                                                                            INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
                                                                                                            LEFT JOIN (
        SELECT FCC.FINPROD_ID,MIN(FCC.EFF_DATE) MDATE FROM FIN_CASH_CALC_RULE FCC
                                                               INNER JOIN FIN_PRODUCT T2 ON FCC.FINPROD_ID = T2.FINPROD_ID
                                                               LEFT JOIN FIN_CASH_INFO T5 ON T5.FINPROD_ID = FCC.FINPROD_ID AND T5.CASH_TYPE_2 = ''C02''
                                                               LEFT JOIN FIN_RATE_INFO T6 ON T6.CASH_ID = T5.CASH_ID
            AND T6.EFF_DATE = T2.VDATE
        WHERE FCC.EFF_DATE > DATE''${valDate}''
        GROUP BY FCC.FINPROD_ID
    ) FXZ ON FXZ.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F01'',''F17'') AND FP.IS_RIGHT = ''N'' AND FP.IS_SUS = ''N''
    UNION
    -- 债券（含权）
    SELECT FP.FINPROD_ID,
           -- 贴现、零息、利随本清
           CASE WHEN FP.COUPON_SPECIES IN (''02'',''03'',''04'') THEN TO_NUMBER(FP.MDATE - DATE''${valDate}'' ) / 365
                WHEN NVL(FXZ.MDATE, T1.MDATE) < T1.MDATE THEN TO_NUMBER(NVL(FXZ.MDATE,FP.MDATE) - DATE''${valDate}'' ) / 365
                ELSE TO_NUMBER(NVL(T1.MDATE,FP.MDATE) - DATE''${valDate}'' ) / 365 END LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
        AND FPT.TYPE_8 IN (''F01'',''F17'')
             LEFT JOIN (
        SELECT FCC.FINPROD_ID,MIN(FCC.EFF_DATE) MDATE FROM FIN_CASH_CALC_RULE FCC
                                                               INNER JOIN FIN_PRODUCT T2 ON FCC.FINPROD_ID = T2.FINPROD_ID
                                                               LEFT JOIN FIN_CASH_INFO T5 ON T5.FINPROD_ID = FCC.FINPROD_ID AND T5.CASH_TYPE_2 = ''C02''
                                                               LEFT JOIN FIN_RATE_INFO T6 ON T6.CASH_ID = T5.CASH_ID
            AND T6.EFF_DATE = T2.VDATE
        WHERE FCC.EFF_DATE > DATE''${valDate}''
        GROUP BY FCC.FINPROD_ID
    ) FXZ ON FXZ.FINPROD_ID = FP.FINPROD_ID
             LEFT JOIN (
        SELECT FINPROD_ID,MIN(EXERCISE_DATE) MDATE FROM FIN_EXERCISE_PLAN
        WHERE EXERCISE_DATE > DATE''${valDate}''  AND EXERCISE_TYPE = ''02''
        GROUP BY FINPROD_ID
    ) T1 ON T1.FINPROD_ID = FP.FINPROD_ID
    WHERE FP.IS_RIGHT = ''Y'' AND FP.IS_SUS = ''N''
    UNION
    -- 非标债权
    SELECT FP.FINPROD_ID,
           -- 下一个利率调整日-计算日
           TO_NUMBER(NVL(FXZ.MDATE, FP.MDATE) - DATE''${valDate}'' ) / 365 LAST_PERIOD FROM FIN_PRODUCT FP
                                                                                              INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
                                                                                              LEFT JOIN (
        SELECT FCC.FINPROD_ID,MIN(FCC.EFF_DATE) MDATE FROM FIN_CASH_CALC_RULE FCC
                                                               INNER JOIN FIN_PRODUCT T2 ON FCC.FINPROD_ID = T2.FINPROD_ID
                                                               LEFT JOIN FIN_CASH_INFO T5 ON T5.FINPROD_ID = FCC.FINPROD_ID AND T5.CASH_TYPE_2 = ''C02''
                                                               LEFT JOIN FIN_RATE_INFO T6 ON T6.CASH_ID = T5.CASH_ID
            AND T6.EFF_DATE = T2.VDATE
        WHERE FCC.EFF_DATE > DATE''${valDate}''
        GROUP BY FCC.FINPROD_ID
    ) FXZ ON FXZ.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F06''
    UNION
    -- 计划类净值型、计划类利率型
    SELECT FP.FINPROD_ID,1 / 365 LAST_PERIOD FROM FIN_PRODUCT FP
                                                      INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F04'',''F09'')
    UNION
    -- 基金
    SELECT FP.FINPROD_ID, DECODE(PLAN.MDATE, NULL, 1 / 365, TO_NUMBER(PLAN.MDATE - DATE''${valDate}'' ) / 365) LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
             LEFT JOIN (
        -- 下一个开放赎回起始日
        SELECT MIN(FOP.OPEN_DATE_STR) MDATE, FOP.FINPROD_ID FROM FIN_OPEN_PLAN FOP
                                                                     INNER JOIN FIN_PRODUCT_TYPE FPT ON FOP.FINPROD_ID = FPT.FINPROD_ID
        WHERE OPEN_DATE_STR > DATE''${valDate}''  AND FPT.TYPE_8 = ''F02''
          AND OPEN_TYPE IN (''02'',''03'')
        GROUP BY FOP.FINPROD_ID
    ) PLAN ON PLAN.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F02''
    UNION
    -- 信用保护凭证
    SELECT FP.FINPROD_ID,0 LAST_PERIOD FROM FIN_PRODUCT FP
                                                INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F37''
    UNION
    -- 其他资产
    SELECT FP.FINPROD_ID,TO_NUMBER(FP.MDATE - DATE''${valDate}'' ) / 365 LAST_PERIOD FROM FIN_PRODUCT FP
                                                                                            INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 NOT IN (''F01'',''F02'',''F04'',''F05'',''F06'',''F09'',''F17'',''F37'')
),
     BOND_VALUTION AS (
         SELECT D.SEC_ID, MAX(D.VALUE_DATE) VALUE_DATE FROM MST_BOND_VALUATION_INFO D
                                                                INNER JOIN FIN_PRODUCT B ON B.FINPROD_MARKET_ID = D.SEC_ID
         WHERE VALUE_DATE <= DATE''${valDate}''
           AND ((B.TRADE_MARKET != ''01'' AND D.VALUE_SOURCE = ''CSD'') OR (B.TRADE_MARKET = ''01'' AND D.VALUE_SOURCE = ''CBC''))
         GROUP BY SEC_ID
     ),
     ASSETJQ AS (
         -- 有标的资产
         SELECT
             CASE WHEN B.FINPROD_TYPE2 = ''F01'' AND T6.INT_TYPE = ''02''
                 AND (D.SDURATION IS NOT NULL OR D.INTEREST_DURATION IS NOT NULL)
                      THEN (NVL(D.SDURATION, 0) + NVL(D.INTEREST_DURATION, 0))
                  WHEN B.FINPROD_TYPE2 = ''F01'' AND D.M_DURATION IS NOT NULL THEN D.M_DURATION
                  ELSE ASSET.LAST_PERIOD
                 END REMAINPERIOD, B.FINPROD_ID, B.FINPROD_TYPE2, NULL PRIN
         FROM FIN_PRODUCT B
                  INNER JOIN ASSET ON B.FINPROD_ID = ASSET.FINPROD_ID
                  LEFT JOIN FIN_CASH_INFO T5 ON T5.FINPROD_ID = B.FINPROD_ID AND T5.CASH_TYPE_2 = ''C02''
                  LEFT JOIN FIN_RATE_INFO T6 ON T6.CASH_ID = T5.CASH_ID AND T6.EFF_DATE = B.VDATE
                  LEFT JOIN BOND_VALUTION TEMP ON TEMP.SEC_ID = B.FINPROD_MARKET_ID
                  LEFT JOIN MST_BOND_VALUATION_INFO D ON B.FINPROD_MARKET_ID = D.SEC_ID AND D.VALUE_DATE = TEMP.VALUE_DATE AND ((B.TRADE_MARKET != ''01'' AND D.VALUE_SOURCE = ''CSD'') OR (B.TRADE_MARKET = ''01'' AND D.VALUE_SOURCE = ''CBC''))
         WHERE B.FINPROD_TYPE2 != ''F16'' AND ASSET.LAST_PERIOD >= 0 AND (B.MDATE >= DATE''${valDate}''  OR B.MDATE IS NULL)
         UNION ALL
         -- 无标的资产
         SELECT ASSET.LAST_PERIOD REMAINPERIOD,E.FINPROD_ID,E.FINPROD_TYPE2,E.PRIN
         FROM FIN_TRADE_PRODUCT E
                  INNER JOIN ASSET ON E.FINPROD_ID = ASSET.FINPROD_ID
         WHERE E.BRANCH = 0 AND (E.FINPROD_TYPE2 IS NULL OR E.FINPROD_TYPE2 NOT IN (''F03'', ''F05'', ''F09''))
           AND ASSET.LAST_PERIOD >= 0
     )
SELECT NVL(sum(CASE WHEN targetData.remainPeriod is null then 0 else NVL(targetData.remainPeriod,0) end * CASE WHEN dataTwo.amount = 0 THEN 0 ELSE NVL(targetData.jiaQuanGuiZe, 0)/dataTwo.amount END),0) as RVALUE FROM (
     SELECT
             nvl(PSV.DSC_COST_AMT,0)+nvl(PSV.TDY_INTINCEXP,0)+nvl(PSV.TDY_FLOAT_INGPL,0)+nvl(PSV.DELAY_PAY_AMT,0)+nvl(PSV.TDY_LOSS_AMT,0) jiaQuanGuiZe, PSV.FINPROD_ID,JQ.remainPeriod
     FROM PTL_SEC_VALUTION PSV
              INNER JOIN ASSETJQ JQ ON JQ.FINPROD_ID = PSV.FINPROD_ID
              LEFT JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = JQ.FINPROD_ID
     WHERE PSV.PORTFOLIO_ID in  ${portfolioCode} and PSV.CDATE = DATE''${valDate}''
       AND ((FPT.TYPE_8 IN (''F04'',''F09'')) OR FPT.TYPE_8 NOT IN (''F04'',''F09''))
     UNION ALL
     SELECT 0 as remainPeriod, A.ACCT_ID AS FINPROD_ID, A.AMOUNT as jiaQuanGuiZe
     FROM PTL_CASH_POSITION A
     WHERE A.PORTFOLIO_ID in  ${portfolioCode} AND A.CDATE = DATE''${valDate}'' AND A.AMOUNT > 0 AND A.HODING_TYPE = ''03''
 ) targetData, (
     SELECT NVL(SUM(TDY_ASSET_NETVALUE),0) AS amount
     FROM PTL_PORTFOLIO_VALUATION
     WHERE PORTFOLIO_ID IN  ${portfolioCode} AND CDATE = DATE''${valDate}''
 ) dataTwo';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'ZCZHJQ' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'ZCZHJQ' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'ZCZHJQ' and PARA_GRANULARITY = '05';
END;
/
