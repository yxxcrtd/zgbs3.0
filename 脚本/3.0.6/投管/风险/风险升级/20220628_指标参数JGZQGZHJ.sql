DECLARE
    V_CONTENT_01_02_03 CLOB :='SELECT
            NVL(SUM(NVL(VAL.MARKET_VALUE,0)+nvl(VAL.TDY_INTINCEXP, 0)),0) RVALUE,
            VAL.PORTFOLIO_ID || '','' || PRD.ISSUER RKEY
        FROM PTL_SEC_PIERCE_VALUTION VAL
	    INNER JOIN FIN_PRODUCT PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID AND PRD.FINPROD_TYPE2 = ''F01''
	    INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID AND (TYP.TYPE_6 NOT IN (''B01'',''B03'',''B17'',''B05'',''B13'')
            OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
        WHERE VAL.CDATE = date''${valDate}'' - 1
        GROUP BY VAL.PORTFOLIO_ID,PRD.ISSUER
		UNION ALL
		SELECT
            NVL(SUM(NVL(VAL.MARKET_VALUE,0)+nvl(VAL.TDY_INTINCEXP, 0)),0) RVALUE,
            VAL.PORTFOLIO_ID || '','' || A.ORIGINAL_OWNER RKEY
        FROM PTL_SEC_PIERCE_VALUTION VAL
                 INNER JOIN FIN_PRODUCT PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID AND PRD.FINPROD_TYPE2 = ''F01''
                 INNER JOIN FIN_PRODUCT_ADD A ON VAL.FINPROD_ID=A.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID AND TYP.TYPE_6=''B13'' AND TYP.TYPE_8 = ''F01''
        WHERE VAL.CDATE = date''${valDate}'' - 1
        GROUP BY VAL.PORTFOLIO_ID,A.ORIGINAL_OWNER';
    V_CONTENT_04 CLOB := 'SELECT SUM(TRADE_AMT) as RVALUE, RKEY, RKEYCN
FROM (
         SELECT SUM(NVL(VAL.MARKET_VALUE, 0)) TRADE_AMT,
                PRD.ISSUER                    RKEY,
                CST.CUSTOMER_NAME             RKEYCN
         FROM PTL_SEC_PIERCE_VALUTION VAL
                  INNER JOIN FIN_PRODUCT PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID AND PRD.FINPROD_TYPE2 = ''F01''
                  INNER JOIN FIN_PRODUCT_TYPE TYP
                             ON TYP.FINPROD_ID = PRD.FINPROD_ID AND (TYP.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'')
                                 OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
                  LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = PRD.ISSUER
         WHERE VAL.CDATE = date''${valDate}'' - 1
           AND VAL.PORTFOLIO_ID IN ${portfolioCode}
         GROUP BY PRD.ISSUER, CST.CUSTOMER_NAME
         UNION ALL
         SELECT nvl(sum(decode(n.PS, ''T01'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                    - nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                    - nvl(sum(decode(n.PS, ''T03'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                    + nvl(sum(decode(n.PS, ''T52'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                    - nvl(sum(decode(n.PS, ''T22'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                    - nvl(sum(decode(n.PS, ''T23'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                    + nvl(sum(decode(n.PS, ''T25'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                    - nvl(sum(decode(n.PS, ''T26'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                    - nvl(sum(decode(n.PS, ''T39'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                    - nvl(sum(decode(n.PS, ''T49'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))),
                          0) AS   TRADE_AMT,
                t2.ISSUER         RKEY,
                CST.CUSTOMER_NAME RKEYCN
         FROM TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                  INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 = ''F01''
                  INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
             AND (t3.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (t3.TYPE_6 = ''B05'' AND t3.TYPE_7 != ''01'')) AND
                                                    t3.TYPE_8 = ''F01''
                  LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = t2.ISSUER
         WHERE m.PORTFOLIO_ID IN ${portfolioCode}
           AND n.TRADE_DATE = DATE''${valDate}''
           AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'')
         GROUP BY t2.ISSUER, CST.CUSTOMER_NAME
         UNION ALL SELECT nvl(sum(decode(n.PS, ''T01'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T03'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           + nvl(sum(decode(n.PS, ''T52'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T22'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T23'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           + nvl(sum(decode(n.PS, ''T25'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T26'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T39'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T49'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))),
                                 0) AS   TRADE_AMT,
                       t2.ISSUER         RKEY,
                       CST.CUSTOMER_NAME RKEYCN
                FROM TRD_PRODUCT_TEMP_DEAL_ADD m
                         INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID
                         INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 = ''F01'' AND t2.TRADE_MARKET IN (''03'',''04'')
                         INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                    AND (t3.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (t3.TYPE_6 = ''B05'' AND t3.TYPE_7 != ''01'')) AND
                                                           t3.TYPE_8 = ''F01''
                         LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = t2.ISSUER
                WHERE m.PORTFOLIO_ID IN ${portfolioCode}
                  AND n.TRADE_DATE = DATE''${valDate}''
                  AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'')
                GROUP BY t2.ISSUER, CST.CUSTOMER_NAME
                UNION ALL SELECT nvl(sum(decode(n.PS, ''T01'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                            - nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                            - nvl(sum(decode(n.PS, ''T03'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                            + nvl(sum(decode(n.PS, ''T52'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                            - nvl(sum(decode(n.PS, ''T22'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                            - nvl(sum(decode(n.PS, ''T23'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                            + nvl(sum(decode(n.PS, ''T25'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                            - nvl(sum(decode(n.PS, ''T26'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                            - nvl(sum(decode(n.PS, ''T39'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                            - nvl(sum(decode(n.PS, ''T49'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))),
                                  0) AS   TRADE_AMT,
                        t2.ISSUER         RKEY,
                        CST.CUSTOMER_NAME RKEYCN
                 FROM WFL_TRD_PRODUCT_DEAL_ADD m
                          INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                          INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                          INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 = ''F01''
                          INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                     AND (t3.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (t3.TYPE_6 = ''B05'' AND t3.TYPE_7 != ''01'')) AND
                                                            t3.TYPE_8 = ''F01''
                          LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = t2.ISSUER
                 WHERE m.PORTFOLIO_ID IN ${portfolioCode}
                   AND n.TRADE_DATE = DATE''${valDate}''
                   AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'')
                 GROUP BY t2.ISSUER, CST.CUSTOMER_NAME
                 UNION ALL SELECT nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))),
                           0) AS         TRADE_AMT,
                       t2.ISSUER         RKEY,
                       CST.CUSTOMER_NAME RKEYCN
                FROM WFL_TRD_PRODUCT_DEAL_ADD m
                         INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                         INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                         INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 = ''F01''
                         INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                    AND (t3.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (t3.TYPE_6 = ''B05'' AND t3.TYPE_7 != ''01'')) AND
                                                           t3.TYPE_8 = ''F01''
                         LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = t2.ISSUER
                WHERE n.counter_id IN ${portfolioCode}
                  AND n.TRADE_DATE = DATE''${valDate}''
                  AND n.PS = ''T02''
                  AND EXISTS(SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
                GROUP BY t2.ISSUER, CST.CUSTOMER_NAME
               union
                SELECT SUM(NVL(VAL.MARKET_VALUE, 0)) TRADE_AMT,
                       PRD.ORIGINAL_OWNER            RKEY,
                       CST.CUSTOMER_NAME             RKEYCN
                FROM PTL_SEC_PIERCE_VALUTION VAL
                INNER JOIN FIN_PRODUCT FP ON VAL.FINPROD_ID = FP.FINPROD_ID AND FP.FINPROD_TYPE2 = ''F01''
                INNER JOIN FIN_PRODUCT_ADD PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID
                INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID AND TYP.TYPE_6 =''B13''  AND TYP.TYPE_8 = ''F01''
                LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = PRD.ORIGINAL_OWNER
                WHERE VAL.CDATE = date''${valDate}'' - 1 AND VAL.PORTFOLIO_ID IN ${portfolioCode}
                GROUP BY PRD.ORIGINAL_OWNER, CST.CUSTOMER_NAME
                UNION  SELECT nvl(sum(decode(n.PS, ''T01'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T03'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           + nvl(sum(decode(n.PS, ''T52'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T22'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T23'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           + nvl(sum(decode(n.PS, ''T25'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T26'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T39'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T49'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))),
                                 0) AS   TRADE_AMT,
                       PRD.ORIGINAL_OWNER         RKEY,
                       CST.CUSTOMER_NAME RKEYCN
                FROM TRD_PRODUCT_DEAL_ADD m
                INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 = ''F01''
                INNER JOIN FIN_PRODUCT_ADD PRD ON t2.FINPROD_ID = PRD.FINPROD_ID
                INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0  AND t3.TYPE_6 =''B13'' and t3.TYPE_8 = ''F01''
                LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = PRD.ORIGINAL_OWNER
                WHERE m.PORTFOLIO_ID IN ${portfolioCode}
                  AND n.TRADE_DATE = DATE''${valDate}''
                  AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'')
                GROUP BY PRD.ORIGINAL_OWNER, CST.CUSTOMER_NAME
                UNION  SELECT nvl(sum(decode(n.PS, ''T01'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T03'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           + nvl(sum(decode(n.PS, ''T52'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T22'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T23'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           + nvl(sum(decode(n.PS, ''T25'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T26'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T39'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T49'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))),
                                 0) AS   TRADE_AMT,
                       PRD.ORIGINAL_OWNER         RKEY,
                       CST.CUSTOMER_NAME RKEYCN
                FROM TRD_PRODUCT_TEMP_DEAL_ADD m
                INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID
                INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 = ''F01'' AND t2.TRADE_MARKET IN (''03'',''04'')
                INNER JOIN FIN_PRODUCT_ADD PRD ON t2.FINPROD_ID = PRD.FINPROD_ID
                INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0 and t3.TYPE_6 =''B13'' and  t3.TYPE_8 = ''F01''
                LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = PRD.ORIGINAL_OWNER
                WHERE m.PORTFOLIO_ID IN ${portfolioCode}
                  AND n.TRADE_DATE = DATE''${valDate}''
                  AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'')
                GROUP BY PRD.ORIGINAL_OWNER, CST.CUSTOMER_NAME
                UNION  SELECT nvl(sum(decode(n.PS, ''T01'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T03'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           + nvl(sum(decode(n.PS, ''T52'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T22'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T23'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           + nvl(sum(decode(n.PS, ''T25'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T26'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T39'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))), 0)
                           - nvl(sum(decode(n.PS, ''T49'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))),
                                 0) AS   TRADE_AMT,
                       PRD.ORIGINAL_OWNER         RKEY,
                       CST.CUSTOMER_NAME RKEYCN
                FROM WFL_TRD_PRODUCT_DEAL_ADD m
                         INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                         INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                         INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 = ''F01''
                         INNER JOIN FIN_PRODUCT_ADD PRD ON t2.FINPROD_ID = PRD.FINPROD_ID
                         INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0  AND t3.TYPE_6 =''B13'' and t3.TYPE_8 = ''F01''
                LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = PRD.ORIGINAL_OWNER
                WHERE m.PORTFOLIO_ID IN ${portfolioCode}
                  AND n.TRADE_DATE = DATE''${valDate}''
                  AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'')
                GROUP BY PRD.ORIGINAL_OWNER, CST.CUSTOMER_NAME
                UNION  SELECT nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT * -1, n.TRADE_AMT))),0) AS         TRADE_AMT,
                       PRD.ORIGINAL_OWNER         RKEY,
                       CST.CUSTOMER_NAME RKEYCN
                FROM WFL_TRD_PRODUCT_DEAL_ADD m
                INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 = ''F01''
                INNER JOIN FIN_PRODUCT_ADD PRD ON t2.FINPROD_ID = PRD.FINPROD_ID
                INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0  AND t3.TYPE_6 =''B13'' and t3.TYPE_8 = ''F01''
                LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = PRD.ORIGINAL_OWNER
                WHERE n.counter_id IN ${portfolioCode}
                  AND n.TRADE_DATE = DATE''${valDate}''
                  AND n.PS = ''T02''
                  AND EXISTS(SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
                GROUP BY PRD.ORIGINAL_OWNER, CST.CUSTOMER_NAME
            )
       GROUP BY RKEY, RKEYCN';
    V_CONTENT_05 CLOB := 'SELECT
            SUM(NVL(VAL.TDY_FLOAT_INGPL,0)+NVL(VAL.DSC_COST_AMT,0)+NVL(VAL.TDY_INTINCEXP,0)) RVALUE,
            PRD.ISSUER RKEY,
            CST.CUSTOMER_NAME RKEYCN
        FROM PTL_SEC_VALUTION VAL
                 INNER JOIN FIN_PRODUCT PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID AND PRD.FINPROD_TYPE2 = ''F01''
                 INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID AND (TYP.TYPE_6 NOT IN (''B01'',''B03'',''B17'',''B05'')
            OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
                 LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = PRD.ISSUER
        WHERE VAL.CDATE = date''${valDate}''  AND VAL.PORTFOLIO_ID IN ${portfolioCode}
        GROUP BY PRD.ISSUER,CST.CUSTOMER_NAME';
BEGIN
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'JGZQGZHJ' and PARA_GRANULARITY IN ('01','02','03');
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'JGZQGZHJ' and PARA_GRANULARITY ='04';
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'JGZQGZHJ' and PARA_GRANULARITY ='05';
END ;