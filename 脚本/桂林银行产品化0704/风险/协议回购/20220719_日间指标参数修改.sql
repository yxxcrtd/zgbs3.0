delete from RSK_ISUP_INDICATOR_PARA_DTL where PARA_CODE='DYZTPJDYAAAJGJRGJGZ' and PARA_GRANULARITY='04';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES ('DYZTPJDYAAAJGJRGJGZ', '04', 'SQL',
 to_clob('WITH finTemp AS (select FINPROD_ID,
                        case FINPROD_TYPE2
                            WHEN ''F01'' THEN ISSUER
                            WHEN ''F06'' THEN manager
                            WHEN ''F07'' THEN ISSUER
                            WHEN ''F10'' THEN ISSUER
                            WHEN ''F18'' THEN financier
                            WHEN ''F17'' THEN manager
                            WHEN ''F32'' THEN ISSUER
                            WHEN ''F37'' THEN ISSUER
                            ELSE null end as ISSUER
                 from FIN_PRODUCT
                 union all
                 SELECT T.FINPROD_ID, M.LINK_ID as ISSUER
                 FROM FIN_TRADE_PRODUCT T
                          INNER JOIN MST_COUNTER_PARTY M ON M.COUNTER_ID = T.COUNTER_ID
                 WHERE T.BRANCH = 0
                   AND T.FINPROD_TYPE2 IN (''F03'', ''F05'')),
     RT AS (SELECT O_CODE, max(GRADE_RESULT) GRADE_RESULT
            FROM (
                     SELECT O_CODE,
                            GRADE_RESULT,
                            rank() over (partition by O_CODE,GRADE_RESULT order by GRADE_DATE desc) rk
                     FROM MST_BOND_CREDIT_RATING
                     where O_TYPE = ''01'')
            where rk = 1
            group by O_CODE)
SELECT SUM(TRADE_AMT) as RVALUE, RKEY, RKEYCN
FROM (
         SELECT NVL(SUM(NVL(VAL.MARKET_VALUE, 0) + nvl(VAL.TDY_INTINCEXP, 0)), 0) TRADE_AMT,
                PRD.ISSUER                                                        RKEY,
                CST.CUSTOMER_NAME                                                 RKEYCN
         FROM PTL_SEC_PIERCE_VALUTION VAL
                  INNER JOIN finTemp PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID
                  INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID
                  left JOIN RT ON RT.O_CODE = PRD.ISSUER
                  LEFT JOIN MST_CUSTOMER_INFO CST ON PRD.ISSUER = CST.CUSTOMER_ID
         WHERE VAL.CDATE = date ''${valDate}'' - 1
           AND VAL.PORTFOLIO_ID IN ${portfolioCode}
           AND (RT.GRADE_RESULT NOT IN (''01'', ''02'') OR RT.GRADE_RESULT IS null)
         GROUP BY PRD.ISSUER, CST.CUSTOMER_NAME
         union all ') || to_clob(
         ' SELECT nvl(sum(decode(T.PS, ''T50'', decode(T.TRADE_STATUS, ''03'', T.prin_amt * -1, T.prin_amt))), 0) TRADE_AMT,
                T2.ISSUER                                                                                   RKEY,
                CST.CUSTOMER_NAME as                                                                        RKEYCN
         FROM TRD_PRODUCT_DEAL T
                  INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  LEFT JOIN RT ON RT.O_CODE = T2.ISSUER
                  LEFT JOIN MST_CUSTOMER_INFO CST ON T2.ISSUER = CST.CUSTOMER_ID
                  INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.PS = ''T50''
           AND T.FINPROD_TYPE2 = ''F01''
           AND T.TRADE_DATE <= date ''${valDate}''
           AND (RT.GRADE_RESULT NOT IN (''01'', ''02'') OR RT.GRADE_RESULT IS null)
           AND NOT EXISTS(
             -- 没有后续申购确认交易
                 SELECT 1
                 FROM TRD_PRODUCT_DEAL TRD
                 WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID
                   AND TRD.TRADE_STATUS = ''01''
                   AND NOT EXISTS(
                         SELECT 1
                         FROM TRD_PRODUCT_DEAL TRD1
                         WHERE TRD.TRADE_ID = TRD1.P_TRADE_ID
                           AND TRD1.TRADE_STATUS = ''03''
                     ))
           AND NOT EXISTS(SELECT 1
                          FROM WFL_TRD_PRODUCT_DEAL TRD
                                   INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
                                   INNER JOIN SYS_WFL_BUSINESS_LIST swbl
                                              on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                          WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID
                            AND TRD.TRADE_STATUS = ''01''
             )
         group by T2.ISSUER, CST.CUSTOMER_NAME
         union all ') || to_clob(
         ' SELECT NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT, T2.ISSUER RKEY, CST.CUSTOMER_NAME RKEYCN
         FROM WFL_TRD_PRODUCT_DEAL T
                  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  LEFT JOIN RT ON RT.O_CODE = T2.ISSUER
                  LEFT JOIN MST_CUSTOMER_INFO CST ON T2.ISSUER = CST.CUSTOMER_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.PS = ''T50''
           AND T.FINPROD_TYPE2 = ''F01''
           AND T.TRADE_DATE = date ''${valDate}''
           AND (RT.GRADE_RESULT NOT IN (''01'', ''02'') OR RT.GRADE_RESULT IS null)
         group by T2.ISSUER, CST.CUSTOMER_NAME
         union all
         --没有撤销记录的申购确认交易
         SELECT NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT, T2.ISSUER RKEY, CST.CUSTOMER_NAME RKEYCN
         FROM TRD_PRODUCT_DEAL T
                  INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  LEFT JOIN RT ON RT.O_CODE = T2.ISSUER
                  LEFT JOIN MST_CUSTOMER_INFO CST ON T2.ISSUER = CST.CUSTOMER_ID
         WHERE T.TRADE_STATUS = ''01''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.PS = ''T05''
           AND T.FINPROD_TYPE2 = ''F01''
           AND (RT.GRADE_RESULT NOT IN (''01'', ''02'') OR RT.GRADE_RESULT IS null)
           AND NOT EXISTS(
                 SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.P_TRADE_ID AND TRD.TRADE_STATUS = ''03'')
         group by T2.ISSUER, CST.CUSTOMER_NAME
                  --没有撤销记录的申购确认交易
         union all
         --申购确认交易(流程中)
         SELECT NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT, T2.ISSUER RKEY, CST.CUSTOMER_NAME RKEYCN
         FROM WFL_TRD_PRODUCT_DEAL T
                  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  LEFT JOIN RT ON RT.O_CODE = T2.ISSUER
                  LEFT JOIN MST_CUSTOMER_INFO CST ON T2.ISSUER = CST.CUSTOMER_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.PS = ''T05''
           AND T.TRADE_DATE = date ''${valDate}''
           AND T.FINPROD_TYPE2 = ''F01''
           AND (RT.GRADE_RESULT NOT IN (''01'', ''02'') OR RT.GRADE_RESULT IS null)
         group by T2.ISSUER, CST.CUSTOMER_NAME
                  --申购确认交易(流程中)
         UNION ALL ') || to_clob(
         ' SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
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
                    - nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T54'', n.TRADE_AMT)),
                          0) AS   TRADE_AMT,
                t2.ISSUER         RKEY,
                CST.CUSTOMER_NAME RKEYCN
         FROM TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                  INNER JOIN finTemp t2 ON t2.FINPROD_ID = n.FINPROD_ID
                  INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                  LEFT JOIN RT ON RT.O_CODE = t2.ISSUER
                  LEFT JOIN MST_CUSTOMER_INFO CST ON t2.ISSUER = CST.CUSTOMER_ID
         WHERE m.PORTFOLIO_ID IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND (RT.GRADE_RESULT NOT IN (''01'', ''02'') OR RT.GRADE_RESULT IS null)
           AND n.PS IN
               (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'', ''T54'')
         GROUP BY t2.ISSUER, CST.CUSTOMER_NAME
         UNION ALL ') || to_clob(
        '  SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''04'', n.TRADE_AMT)), 0)
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
                    - nvl(sum(decode(n.PS, ''T54'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T93'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T95'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T96'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T55'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T56'', n.TRADE_AMT)),
                          0) AS   TRADE_AMT,
                t2.ISSUER         RKEY,
                CST.CUSTOMER_NAME RKEYCN
         FROM TRD_PRODUCT_TEMP_DEAL_ADD m
                  INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID
                  INNER JOIN finTemp t2 ON t2.FINPROD_ID = n.FINPROD_ID
                  INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                  LEFT JOIN RT ON RT.O_CODE = t2.ISSUER
                  LEFT JOIN MST_CUSTOMER_INFO CST ON t2.ISSUER = CST.CUSTOMER_ID
         WHERE m.PORTFOLIO_ID IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND (RT.GRADE_RESULT NOT IN (''01'', ''02'') OR RT.GRADE_RESULT IS null)
           AND n.PS IN
               (''T01'', ''04'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',
                ''T93'', ''T95'', ''T96'', ''T54'', ''T55'', ''T56'')
           AND n.COUNTER_ID IS NOT NULL
         GROUP BY t2.ISSUER, CST.CUSTOMER_NAME
         UNION ALL ') || to_clob(
         ' SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)),
                          0) AS   TRADE_AMT,
                t2.ISSUER         RKEY,
                CST.CUSTOMER_NAME RKEYCN
         FROM WFL_TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                  INNER JOIN finTemp t2 ON t2.FINPROD_ID = n.FINPROD_ID
                  left JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                  LEFT JOIN RT ON RT.O_CODE = t2.ISSUER
                  LEFT JOIN MST_CUSTOMER_INFO CST ON t2.ISSUER = CST.CUSTOMER_ID
         WHERE m.PORTFOLIO_ID IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND (RT.GRADE_RESULT NOT IN (''01'', ''02'') OR RT.GRADE_RESULT IS null)
           AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T52'')
           AND n.TRADE_STATUS = ''01''
           AND n.IS_CANCEL = ''N''
         GROUP BY t2.ISSUER, CST.CUSTOMER_NAME
         UNION ALL
         SELECT nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)),
                    0) AS         TRADE_AMT,
                t2.ISSUER         RKEY,
                CST.CUSTOMER_NAME RKEYCN
         FROM WFL_TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                  INNER JOIN finTemp t2 ON t2.FINPROD_ID = n.FINPROD_ID
                  INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                  LEFT JOIN RT ON RT.O_CODE = t2.ISSUER
                  LEFT JOIN MST_CUSTOMER_INFO CST ON t2.ISSUER = CST.CUSTOMER_ID
         WHERE n.counter_id IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND (RT.GRADE_RESULT NOT IN (''01'', ''02'') OR RT.GRADE_RESULT IS null)
           AND n.PS = ''T02''
           AND EXISTS(SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
         GROUP BY t2.ISSUER, CST.CUSTOMER_NAME
     )
GROUP BY RKEY, RKEYCN'), 'portfolioCode,issuerCode', 'N', 'dzw', '3212413', sysdate, 'dzw', sysdate);

delete  from RSK_ISUP_INDICATOR_PARA_DTL where PARA_CODE='ZCSYCXQX' and PARA_GRANULARITY='04';
INSERT INTO  RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES ('ZCSYCXQX', '04', 'SQL',
to_clob('WITH ASSET AS (
    -- 逆回购交易
    SELECT FINPROD_ID, TO_NUMBER(MDATE - DATE ''${valDate}'') LAST_PERIOD
    FROM FIN_TRADE_PRODUCT
    WHERE FINPROD_TYPE2 IN (''F03'', ''F05'', ''F09'')
    UNION
    -- 活期存款
    SELECT FP.FINPROD_ID, 0 LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F05''
      AND FPT.TYPE_6 = ''D03''
    UNION
    -- 非活期存款
    SELECT FP.FINPROD_ID, TO_NUMBER(FP.MDATE - DATE ''${valDate}'') LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F05''
      AND FPT.TYPE_6 != ''D03''
    UNION
    -- 计划类净值型、计划类利率型
    SELECT FP.FINPROD_ID, 1 LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F04'', ''F09'')
    UNION
    -- 基金
    SELECT FP.FINPROD_ID, DECODE(PLAN.MDATE, NULL, 1, TO_NUMBER(PLAN.MDATE - DATE ''${valDate}'')) LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
             LEFT JOIN (
        -- 下一个开放赎回起始日
        SELECT min(FOP.OPEN_DATE_STR) MDATE, FOP.FINPROD_ID
        FROM FIN_OPEN_PLAN FOP
                 INNER JOIN FIN_PRODUCT_TYPE FPT ON FOP.FINPROD_ID = FPT.FINPROD_ID
        WHERE OPEN_DATE_STR > DATE ''${valDate}''
          AND FPT.TYPE_8 = ''F02''
          AND OPEN_TYPE IN (''02'', ''03'')
        GROUP BY FOP.FINPROD_ID
    ) PLAN ON PLAN.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F02''
    UNION
    -- 其他资产
    SELECT FP.FINPROD_ID, TO_NUMBER(FP.MDATE - DATE ''${valDate}'') LAST_PERIOD
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FP.FINPROD_ID = FPT.FINPROD_ID
    WHERE FPT.TYPE_8 NOT IN (''F02'', ''F05'', ''F04'', ''F09'')
)') || to_clob(
' select NVL(r1.SHARE_AMT - nvl(R2.fee_amt,0),0) RVALUE
from (
         SELECT NVL(SUM(NVL(LAST_PERIOD, 0) * TRADE_AMT), 0) SHARE_AMT, PORTFOLIO_ID
         FROM (
                  SELECT SUM(NVL(TEMP.TRADE_AMT, 0)) TRADE_AMT,
                         TEMP.FINPROD_ID             RKEY,
                         TEMP.LAST_PERIOD,
                         TEMP.PORTFOLIO_ID
                  FROM (
                           SELECT SUM(NVL(PSP.MARKET_VALUE, 0) + NVL(TDY_INTINCEXP, 0)) TRADE_AMT,
                                  PSP.FINPROD_ID,
                                  ASSET.LAST_PERIOD,
                                  PSP.PORTFOLIO_ID
                           FROM PTL_SEC_PIERCE_VALUTION PSP
                                    INNER JOIN ASSET ON ASSET.FINPROD_ID = PSP.FINPROD_ID
                                    LEFT JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = ASSET.FINPROD_ID
                           WHERE PSP.CDATE = DATE''${valDate}'' - 1
                             AND PSP.PORTFOLIO_ID IN ${portfolioCode}
                             AND ((PSP.SUB_CODE IS NULL AND FPT.TYPE_8 IN (''F04'', ''F09'')) OR
                                  FPT.TYPE_8 NOT IN (''F04'', ''F09''))
                             AND ASSET.LAST_PERIOD >= 0
                           GROUP BY PSP.FINPROD_ID, ASSET.LAST_PERIOD, PSP.PORTFOLIO_ID
                           UNION ALL
                           SELECT SUM(CASE
                                          WHEN n.PS IN (''T01'', ''T04'', ''T52'', ''T25'', ''T64'') THEN NVL(n.TRADE_AMT, 0)
                                          WHEN n.PS = ''T49'' THEN NVL(-100 * n.SHARE_AMT, 0)
                                          ELSE NVL(-1 * n.TRADE_AMT, 0)
                               END) TRADE_AMT,
                                  n.FINPROD_ID,
                                  ASSET.LAST_PERIOD,
                                  M.PORTFOLIO_ID
                           FROM TRD_PRODUCT_DEAL_ADD m
                                    INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                                    INNER JOIN ASSET ON ASSET.FINPROD_ID = n.FINPROD_ID
                           WHERE n.TRADE_DATE = DATE''${valDate}''
                             AND m.PORTFOLIO_ID IN ${portfolioCode}
                             AND n.PS IN
                                 (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',
                                  ''T25'', ''T49'', ''T64'', ''T63'', ''T93'', ''T95'', ''T96'', ''T54'', ''T55'', ''T56'')
                             AND N.TRADE_STATUS = ''01''
                             AND N.IS_CANCEL = ''N''
                           GROUP BY n.FINPROD_ID, ASSET.LAST_PERIOD, M.PORTFOLIO_ID
                           UNION ALL ') || to_clob(
                           ' SELECT SUM(CASE
                                          WHEN n.PS IN (''T01'', ''T04'', ''T52'', ''T25'', ''T64'', ''04'')
                                              THEN NVL(n.TRADE_AMT, 0)
                                          WHEN n.PS = ''T49'' THEN NVL(-100 * n.SHARE_AMT, 0)
                                          ELSE NVL(-1 * n.TRADE_AMT, 0)
                               END) TRADE_AMT,
                                  n.FINPROD_ID,
                                  ASSET.LAST_PERIOD,
                                  M.PORTFOLIO_ID
                           FROM TRD_PRODUCT_TEMP_DEAL_ADD m
                                    INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID
                                    INNER JOIN ASSET ON ASSET.FINPROD_ID = n.FINPROD_ID
                           WHERE n.TRADE_DATE = DATE''${valDate}''
                             AND m.PORTFOLIO_ID IN ${portfolioCode}
                             AND n.PS IN
                                 (''T01'', ''04'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'',
                                  ''T39'', ''T25'', ''T49'', ''T64'', ''T63'', ''T93'', ''T95'', ''T96'', ''T54'', ''T55'', ''T56'')
                             AND N.TRADE_STATUS = ''01''
                             AND N.IS_CANCEL = ''N''
                           GROUP BY n.FINPROD_ID, ASSET.LAST_PERIOD, M.PORTFOLIO_ID
                           UNION ALL
                           SELECT SUM(CASE
                                          WHEN n.PS IN (''T01'', ''T04'', ''T52'', ''T64'') THEN NVL(n.TRADE_AMT, 0)
                                          WHEN n.PS = ''T49'' THEN NVL(-1 * n.SHARE_AMT, 0)
                                          ELSE NVL(-1 * n.TRADE_AMT, 0)
                               END) AS TRADE_AMT,
                                  n.FINPROD_ID,
                                  ASSET.LAST_PERIOD,
                                  M.PORTFOLIO_ID
                           FROM WFL_TRD_PRODUCT_DEAL_ADD m
                                    INNER JOIN WFL_TRD_PRODUCT_DEAL n
                                               ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                                    INNER JOIN SYS_WFL_BUSINESS_LIST swbl
                                               on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                                    INNER JOIN ASSET ON ASSET.FINPROD_ID = n.FINPROD_ID
                           WHERE m.PORTFOLIO_ID IN ${portfolioCode}
                             AND n.TRADE_DATE = DATE''${valDate}''
                             AND n.FINPROD_TYPE2 NOT IN (''F02'', ''F04'', ''F08'', ''F25'')
                             AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T52'', ''T49'', ''T64'', ''T63'')
                             AND N.TRADE_STATUS = ''01''
                             AND N.IS_CANCEL = ''N''
                           GROUP BY n.FINPROD_ID, ASSET.LAST_PERIOD, M.PORTFOLIO_ID
                           UNION ALL') || to_clob('
                           ----无标的资产流程中买入标的还没有生成的场景
                           SELECT SUM(CASE
                                          WHEN n.PS IN (''T01'', ''T04'', ''T52'', ''T64'') THEN NVL(n.TRADE_AMT, 0)
                                          WHEN n.PS = ''T49'' THEN NVL(-1 * n.SHARE_AMT, 0)
                                          ELSE NVL(-1 * n.TRADE_AMT, 0)
                               END)                                     TRADE_AMT,
                                  N.FINPROD_ID,
                                  TO_NUMBER(N.MDATE - DATE''${valDate}'') LAST_PERIOD,
                                  M.PORTFOLIO_ID
                           FROM WFL_TRD_PRODUCT_DEAL_ADD M
                                    INNER JOIN WFL_TRD_PRODUCT_DEAL N
                                               ON M.TRADE_ID = N.TRADE_ID AND N.TRADE_STATUS != ''03''
                                    INNER JOIN SYS_WFL_BUSINESS_LIST SWBL
                                               ON N.WORKFLOW_ID = SWBL.SEQ_NO AND SWBL.PROCESS_STATE != ''BACK''
                           WHERE M.PORTFOLIO_ID IN ${portfolioCode}
                             AND N.TRADE_DATE = DATE''${valDate}''
                             AND N.FINPROD_TYPE2 IN (''F03'', ''F05'', ''F09'')
                             AND n.PS IN (''T01'')
                             AND N.TRADE_STATUS = ''01''
                             AND N.IS_CANCEL = ''N''
                           GROUP BY N.FINPROD_ID, TO_NUMBER(N.MDATE - DATE''${valDate}''), M.PORTFOLIO_ID
                           UNION ALL
                           SELECT nvl(sum(decode(n.PS, ''T02'',
                                                 decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))),
                                      0) AS    TRADE_AMT,
                                  n.FINPROD_ID,
                                  ASSET.LAST_PERIOD,
                                  n.counter_id PORTFOLIO_ID
                           FROM WFL_TRD_PRODUCT_DEAL_ADD m
                                    INNER JOIN WFL_TRD_PRODUCT_DEAL n
                                               ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                                    INNER JOIN SYS_WFL_BUSINESS_LIST swbl
                                               on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                                    INNER JOIN ASSET ON ASSET.FINPROD_ID = n.FINPROD_ID
                           WHERE n.counter_id IN ${portfolioCode}
                             AND n.TRADE_DATE = DATE''${valDate}''
                             AND n.PS = ''T02''
                             AND EXISTS(SELECT 1
                                        FROM SYS_PARA
                                        WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
                           GROUP BY n.FINPROD_ID, ASSET.LAST_PERIOD, n.counter_id
                       ) TEMP') || to_clob( '
                  GROUP BY TEMP.FINPROD_ID, TEMP.LAST_PERIOD, TEMP.PORTFOLIO_ID
              )
         group by PORTFOLIO_ID) r1
         left join
     (select NVL(sum(bp.TDY_UNPAID_INTINCEXP * (nvl(fcpp.pay_date, fp.mdate) - DATE''${valDate}'')), 0) as fee_amt,
             bp.FINPROD_ID
      from BOK_POSITION_PRDFEE bp
               left join fin_cash_info fc on bp.finprod_id = fc.FINPROD_ID and bp.FEE_TYPE = fc.CASH_TYPE_2
               left join (SELECT cash_id,
                                 pay_date,
                                 ROW_NUMBER() OVER (PARTITION BY cash_id ORDER BY pay_date asc) AS RN
                          FROM fin_cash_plan fcp
                          WHERE fcp.pay_date > DATE''${valDate}'') fcpp ON fc.cash_id = fcpp.cash_id
               left join fin_product fp on bp.finprod_id = fp.finprod_id
      where fcpp.RN = ''1''
        and bp.cdate = DATE''${valDate}''
      group by bp.FINPROD_ID) r2 on ''F16_'' || r1.PORTFOLIO_ID = r2.FINPROD_ID'), 'portfolioCode', 'N', 'cdj', 'C001', sysdate, 'cdj', sysdate);
