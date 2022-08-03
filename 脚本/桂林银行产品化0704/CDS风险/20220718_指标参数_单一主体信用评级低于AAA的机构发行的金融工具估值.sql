/**风险指标参数:单一主体信用评级低于AAA的机构发行的金融工具估值**/
----风险监督指标参数

DECLARE
---- 实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'WITH finTemp AS (select FINPROD_ID ,case  FINPROD_TYPE2 WHEN  ''F01'' THEN ISSUER
                WHEN ''F06'' THEN manager
                WHEN ''F07'' THEN ISSUER
				WHEN ''F10'' THEN ISSUER
				WHEN ''F18'' THEN financier
				WHEN ''F17'' THEN manager
				WHEN ''F32'' THEN ISSUER
				WHEN ''F37'' THEN ISSUER
				ELSE null end as ISSUER from  FIN_PRODUCT
          union all
          SELECT FINPROD_ID,COUNTER_ID as ISSUER
          FROM FIN_TRADE_PRODUCT
          WHERE BRANCH=0 AND FINPROD_TYPE2 IN(''F03'',''F05'')),
       RT AS (SELECT O_CODE,max(GRADE_RESULT) GRADE_RESULT FROM (
             SELECT O_CODE,GRADE_RESULT,rank() over (partition by O_CODE,GRADE_RESULT order by GRADE_DATE desc) rk
             FROM MST_BOND_CREDIT_RATING where O_TYPE = ''01'') where rk =1 group by O_CODE)
        SELECT nvl(sum(nvl(RVALUE, 0)),0) as RVALUE,RKEY  FROM (
        SELECT
            NVL(SUM(NVL(VAL.MARKET_VALUE,0)+nvl(VAL.TDY_INTINCEXP, 0)),0) RVALUE,
            VAL.PORTFOLIO_ID || '','' || PRD.ISSUER RKEY
        FROM PTL_SEC_PIERCE_VALUTION VAL
        INNER JOIN finTemp PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID
        INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID
        INNER JOIN  RT ON RT.O_CODE = PRD.ISSUER
        WHERE VAL.CDATE = date''${valDate}'' - 1 AND RT.GRADE_RESULT NOT IN (''01'',''02'')
        GROUP BY VAL.PORTFOLIO_ID,PRD.ISSUER
        UNION ALL
        SELECT nvl(sum(decode(T.PS,''T50'',decode(T.TRADE_STATUS,''03'',T.prin_amt*-1,T.prin_amt))),0) RVALUE,
           T1.PORTFOLIO_ID||'',''||T2.ISSUER as RKEY
        FROM TRD_PRODUCT_DEAL T
        INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
        INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
        INNER JOIN  RT ON RT.O_CODE = T2.ISSUER
        INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
        WHERE T.PS = ''T50''
        AND T.FINPROD_TYPE2 = ''F01''
        AND T.TRADE_DATE < date''${valDate}''
        AND RT.GRADE_RESULT NOT IN (''01'',''02'')
        AND NOT EXISTS (
          SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01''
          AND NOT EXISTS (
           SELECT 1 FROM TRD_PRODUCT_DEAL TRD1
          WHERE TRD.TRADE_ID = TRD1.P_TRADE_ID
            AND TRD1.TRADE_STATUS = ''03''
        ))
        AND NOT EXISTS (SELECT 1 FROM WFL_TRD_PRODUCT_DEAL TRD
        INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
         INNER JOIN SYS_WFL_BUSINESS_LIST swbl on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'')
        group by T1.PORTFOLIO_ID,T2.ISSUER)group by RKEY';

----事后(日间)
V_CONTENT_04 CLOB := 'WITH finTemp AS (select FINPROD_ID ,case  FINPROD_TYPE2 WHEN  ''F01'' THEN ISSUER
                WHEN ''F06'' THEN manager
                WHEN ''F07'' THEN ISSUER
                WHEN ''F10'' THEN ISSUER
                WHEN ''F18'' THEN financier
                WHEN ''F17'' THEN manager
                WHEN ''F32'' THEN ISSUER
				WHEN ''F37'' THEN ISSUER
                ELSE null end as ISSUER from  FIN_PRODUCT
            union all
            SELECT T.FINPROD_ID,M.LINK_ID as ISSUER
                FROM FIN_TRADE_PRODUCT T
                INNER JOIN  MST_COUNTER_PARTY M ON M.COUNTER_ID=T.COUNTER_ID
                WHERE T.BRANCH=0 AND T.FINPROD_TYPE2 IN(''F03'',''F05'')),
                RT AS (SELECT O_CODE,max(GRADE_RESULT) GRADE_RESULT FROM (
                     SELECT O_CODE,GRADE_RESULT,rank() over (partition by O_CODE,GRADE_RESULT order by GRADE_DATE desc) rk
                     FROM MST_BOND_CREDIT_RATING where O_TYPE = ''01'') where rk =1 group by O_CODE)
             SELECT SUM(TRADE_AMT) as RVALUE, RKEY, RKEYCN FROM (
                SELECT
                    NVL(SUM(NVL(VAL.MARKET_VALUE,0)+nvl(VAL.TDY_INTINCEXP, 0)),0) TRADE_AMT, PRD.ISSUER RKEY, CST.CUSTOMER_NAME RKEYCN
                FROM PTL_SEC_PIERCE_VALUTION VAL
                INNER JOIN finTemp PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID
                INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID
                left JOIN RT ON RT.O_CODE = PRD.ISSUER
                LEFT JOIN MST_CUSTOMER_INFO CST ON PRD.ISSUER = CST.CUSTOMER_ID
                WHERE VAL.CDATE = date''${valDate}'' - 1 AND VAL.PORTFOLIO_ID IN ${portfolioCode} AND (RT.GRADE_RESULT NOT IN (''01'',''02'') OR RT.GRADE_RESULT IS null)
                GROUP BY PRD.ISSUER,CST.CUSTOMER_NAME
             union all
             SELECT nvl(sum(decode(T.PS,''T50'',decode(T.TRADE_STATUS,''03'',T.prin_amt*-1,T.prin_amt))),0) TRADE_AMT,
             T2.ISSUER RKEY, CST.CUSTOMER_NAME as RKEYCN
              FROM TRD_PRODUCT_DEAL T
             INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
             INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
             INNER JOIN  RT ON RT.O_CODE = T2.ISSUER
             LEFT JOIN MST_CUSTOMER_INFO CST ON T2.ISSUER = CST.CUSTOMER_ID
             INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
             WHERE T1.PORTFOLIO_ID in ${portfolioCode}
               AND T.PS = ''T50''
               AND T.FINPROD_TYPE2 = ''F01''
               AND T.TRADE_DATE <= date''${valDate}''
               AND (RT.GRADE_RESULT NOT IN (''01'',''02'') OR RT.GRADE_RESULT IS null)
               AND NOT EXISTS (
                  -- 没有后续申购确认交易
                  SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01''
                  AND NOT EXISTS (
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD1
                     WHERE TRD.TRADE_ID = TRD1.P_TRADE_ID
                       AND TRD1.TRADE_STATUS = ''03''
              ))
              AND NOT EXISTS (SELECT 1 FROM WFL_TRD_PRODUCT_DEAL TRD
                INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
                 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                 WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01''
              )
              group by T2.ISSUER,CST.CUSTOMER_NAME
               union all
            SELECT NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT,T2.ISSUER RKEY,CST.CUSTOMER_NAME RKEYCN
              FROM WFL_TRD_PRODUCT_DEAL T
             INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
             INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
             INNER JOIN  RT ON RT.O_CODE = T2.ISSUER
             LEFT JOIN MST_CUSTOMER_INFO CST ON T2.ISSUER = CST.CUSTOMER_ID
             INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
             WHERE T1.PORTFOLIO_ID in ${portfolioCode}
               AND T.PS = ''T50''
               AND T.FINPROD_TYPE2 = ''F01''
               AND T.TRADE_DATE = date''${valDate}''
               AND (RT.GRADE_RESULT NOT IN (''01'',''02'') OR RT.GRADE_RESULT IS null)
              group by T2.ISSUER,CST.CUSTOMER_NAME
            union all
             --没有撤销记录的申购确认交易
            SELECT NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT,T2.ISSUER RKEY,CST.CUSTOMER_NAME RKEYCN
              FROM TRD_PRODUCT_DEAL T
             INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
             INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
             INNER JOIN  RT ON RT.O_CODE = T2.ISSUER
             LEFT JOIN MST_CUSTOMER_INFO CST ON T2.ISSUER = CST.CUSTOMER_ID
             WHERE T.TRADE_STATUS = ''01''
               AND T1.PORTFOLIO_ID in ${portfolioCode}
               AND T.PS = ''T05''
               AND T.FINPROD_TYPE2 = ''F01''
               AND (RT.GRADE_RESULT NOT IN (''01'',''02'') OR RT.GRADE_RESULT IS null)
               AND NOT EXISTS ( SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.P_TRADE_ID AND TRD.TRADE_STATUS = ''03'')
             group by T2.ISSUER,CST.CUSTOMER_NAME
              --没有撤销记录的申购确认交易
               union all
             --申购确认交易(流程中)
            SELECT NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT,T2.ISSUER RKEY,CST.CUSTOMER_NAME RKEYCN
              FROM WFL_TRD_PRODUCT_DEAL T
             INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
             INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
             INNER JOIN  RT ON RT.O_CODE = T2.ISSUER
             LEFT JOIN MST_CUSTOMER_INFO CST ON T2.ISSUER = CST.CUSTOMER_ID
             INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
             WHERE T1.PORTFOLIO_ID in ${portfolioCode}
               AND T.PS = ''T05''
               AND T.TRADE_DATE = date''${valDate}''
               AND T.FINPROD_TYPE2 = ''F01''
               AND (RT.GRADE_RESULT NOT IN (''01'',''02'') OR RT.GRADE_RESULT IS null)
               group by T2.ISSUER ,CST.CUSTOMER_NAME
               --申购确认交易(流程中)
              UNION ALL
                SELECT
                    nvl(sum(decode(n.PS, ''T01'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  + nvl(sum(decode(n.PS, ''T04'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  + nvl(sum(decode(n.PS, ''T52'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T03'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T19'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T07'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T08'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T09'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T11'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T22'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T23'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T38'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T39'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T54'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0) AS TRADE_AMT, t2.ISSUER RKEY, CST.CUSTOMER_NAME RKEYCN
                FROM TRD_PRODUCT_DEAL_ADD m
                INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                INNER JOIN finTemp t2 ON t2.FINPROD_ID = n.FINPROD_ID
                INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                INNER JOIN  RT ON RT.O_CODE = t2.ISSUER
                LEFT JOIN MST_CUSTOMER_INFO CST ON t2.ISSUER = CST.CUSTOMER_ID
                WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}'' AND (RT.GRADE_RESULT NOT IN (''01'',''02'') OR RT.GRADE_RESULT IS null)
                AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T54'')
                GROUP BY t2.ISSUER,CST.CUSTOMER_NAME
              UNION ALL
                SELECT
                    nvl(sum(decode(n.PS, ''T01'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  + nvl(sum(decode(n.PS, ''T04'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  + nvl(sum(decode(n.PS, ''04'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  + nvl(sum(decode(n.PS, ''T52'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T03'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T19'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T07'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T08'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T09'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T11'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T22'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T23'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T38'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T39'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T54'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T93'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T95'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T96'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T55'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T56'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)   AS TRADE_AMT, t2.ISSUER RKEY, CST.CUSTOMER_NAME RKEYCN
                FROM TRD_PRODUCT_TEMP_DEAL_ADD m
                INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID
                INNER JOIN finTemp t2 ON t2.FINPROD_ID = n.FINPROD_ID
                INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                INNER JOIN  RT ON RT.O_CODE = t2.ISSUER
                LEFT JOIN MST_CUSTOMER_INFO CST ON t2.ISSUER = CST.CUSTOMER_ID
                WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}'' AND (RT.GRADE_RESULT NOT IN (''01'',''02'') OR RT.GRADE_RESULT IS null)
                AND n.PS IN (''T01'',''04'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T93'',''T95'',''T96'',''T54'',''T55'',''T56'')
                AND n.COUNTER_ID IS  NOT  NULL
                GROUP BY t2.ISSUER,CST.CUSTOMER_NAME
              UNION ALL
                SELECT
                    nvl(sum(decode(n.PS, ''T01'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  + nvl(sum(decode(n.PS, ''T04'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  + nvl(sum(decode(n.PS, ''T52'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)
                  - nvl(sum(decode(n.PS, ''T03'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)  AS TRADE_AMT, t2.ISSUER RKEY, CST.CUSTOMER_NAME RKEYCN
                FROM WFL_TRD_PRODUCT_DEAL_ADD m
                INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                INNER JOIN finTemp t2 ON t2.FINPROD_ID = n.FINPROD_ID
                left JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                INNER JOIN  RT ON RT.O_CODE = t2.ISSUER
                LEFT JOIN MST_CUSTOMER_INFO CST ON t2.ISSUER = CST.CUSTOMER_ID
                WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}'' AND (RT.GRADE_RESULT NOT IN (''01'',''02'') OR RT.GRADE_RESULT IS null)
                AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T52'')
               AND n.TRADE_STATUS = ''01''
               AND n.IS_CANCEL = ''N''
                GROUP BY t2.ISSUER,CST.CUSTOMER_NAME
              UNION ALL
                SELECT
                    nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0) AS TRADE_AMT,t2.ISSUER RKEY, CST.CUSTOMER_NAME RKEYCN
                FROM WFL_TRD_PRODUCT_DEAL_ADD m
                INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                INNER JOIN finTemp t2 ON t2.FINPROD_ID = n.FINPROD_ID
                INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                INNER JOIN  RT ON RT.O_CODE = t2.ISSUER
                LEFT JOIN MST_CUSTOMER_INFO CST ON t2.ISSUER = CST.CUSTOMER_ID
                WHERE n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}'' AND (RT.GRADE_RESULT NOT IN (''01'',''02'') OR RT.GRADE_RESULT IS null)
                AND n.PS =''T02''
                AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
                GROUP BY t2.ISSUER,CST.CUSTOMER_NAME
            )  GROUP BY RKEY, RKEYCN';

----事后(日终)
V_CONTENT_05 CLOB := 'WITH finTemp AS (select FINPROD_ID ,case  FINPROD_TYPE2 WHEN  ''F01'' THEN ISSUER
                WHEN ''F06'' THEN manager
                WHEN ''F07'' THEN ISSUER
                WHEN ''F10'' THEN ISSUER
                WHEN ''F18'' THEN financier
                WHEN ''F17'' THEN manager
                WHEN ''F32'' THEN ISSUER
				WHEN ''F37'' THEN ISSUER
                ELSE null end as ISSUER from  FIN_PRODUCT
              union all
              SELECT FINPROD_ID,COUNTER_ID as ISSUER
              FROM FIN_TRADE_PRODUCT
              WHERE BRANCH=0 AND FINPROD_TYPE2 IN(''F03'',''F05'')),
          RT AS (SELECT O_CODE,max(GRADE_RESULT) GRADE_RESULT FROM (
                 SELECT O_CODE,GRADE_RESULT,rank() over (partition by O_CODE,GRADE_RESULT order by GRADE_DATE desc) rk
                 FROM MST_BOND_CREDIT_RATING where O_TYPE = ''01'') where rk =1 group by O_CODE)
        select
            sum(NVL(RVALUE, 0)) as RVALUE,RKEY,RKEYCN
        from (
        SELECT
            NVL(SUM(NVL(VAL.MARKET_VALUE,0)+nvl(VAL.TDY_INTINCEXP, 0)),0) RVALUE, PRD.ISSUER RKEY,
         CST.CUSTOMER_NAME RKEYCN
        FROM PTL_SEC_PIERCE_VALUTION VAL
        INNER JOIN finTemp PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID
        INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID
        left JOIN  RT ON RT.O_CODE = PRD.ISSUER
        LEFT JOIN MST_CUSTOMER_INFO CST ON PRD.ISSUER = CST.CUSTOMER_ID
        WHERE VAL.CDATE = date''${valDate}'' AND VAL.PORTFOLIO_ID IN ${portfolioCode} AND (RT.GRADE_RESULT NOT IN (''01'',''02'') OR RT.GRADE_RESULT IS null)
        GROUP BY PRD.ISSUER,CST.CUSTOMER_NAME
        union all
        SELECT nvl(sum(decode(T.PS,''T50'',decode(T.TRADE_STATUS,''03'',T.prin_amt*-1,T.prin_amt))),0) RVALUE,T2.ISSUER RKEY,CST.CUSTOMER_NAME RKEYCN
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN  RT ON RT.O_CODE = T2.ISSUER
         LEFT JOIN MST_CUSTOMER_INFO CST ON T2.ISSUER = CST.CUSTOMER_ID
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.PS = ''T50''
           AND T.FINPROD_TYPE2 = ''F01''
           AND T.TRADE_DATE <= date''${valDate}''
           AND (RT.GRADE_RESULT NOT IN (''01'',''02'') OR RT.GRADE_RESULT IS null)
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01''
              AND NOT EXISTS (
                  SELECT 1 FROM TRD_PRODUCT_DEAL TRD1
                 WHERE TRD.TRADE_ID = TRD1.P_TRADE_ID
                   AND TRD1.TRADE_STATUS = ''03''
          ))
          AND NOT EXISTS (SELECT 1 FROM WFL_TRD_PRODUCT_DEAL TRD
            INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
             INNER JOIN SYS_WFL_BUSINESS_LIST swbl on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
             WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01''
          ) group by T2.ISSUER,CST.CUSTOMER_NAME
        ) group by RKEY, RKEYCN';
BEGIN
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'DYZTPJDYAAAJGJRGJGZ' and PARA_GRANULARITY in('01','02','03');

    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'DYZTPJDYAAAJGJRGJGZ' and PARA_GRANULARITY ='04';

    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'DYZTPJDYAAAJGJRGJGZ' and PARA_GRANULARITY = '05';
END;
/
