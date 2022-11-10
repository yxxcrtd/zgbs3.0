/**风险指标参数: 同一机构发行的债券及其作为原始权益人的资产支持证券估值合计 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='JGZQGZHJ';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('JGZQGZHJ','同一机构发行的债券及其作为原始权益人的资产支持证券估值合计', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='JGZQGZHJ';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('JGZQGZHJ','01', 'SQL','select 1 from dual',  'portfolioCode,originalOwner' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('JGZQGZHJ','02', 'SQL','select 1 from dual',  'portfolioCode,originalOwner' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('JGZQGZHJ','03', 'SQL','select 1 from dual',  'portfolioCode,originalOwner' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('JGZQGZHJ','04', 'SQL','select 1 from dual',  'portfolioCode,originalOwner' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('JGZQGZHJ','05', 'SQL','select 1 from dual',  'portfolioCode,originalOwner' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'SELECT nvl(sum(nvl(RVALUE, 0)),0) as RVALUE,s1.RKEY  FROM (
        SELECT
            NVL(SUM(nvl(val.DSC_COST_AMT,0)+nvl(val.TDY_INTINCEXP,0)+nvl(val.TDY_FLOAT_INGPL,0)+nvl(val.DELAY_PAY_AMT,0)+nvl(val.TDY_LOSS_AMT,0)),0) RVALUE,
            VAL.PORTFOLIO_ID || '','' || PRD.ISSUER RKEY
        FROM PTL_SEC_VALUTION VAL
      INNER JOIN FIN_PRODUCT PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID AND PRD.FINPROD_TYPE2 in (''F01'',''F32'')
      INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID and TYP.BRANCH= 0 AND (TYP.TYPE_6 NOT IN (''B01'',''B03'',''B17'',''B05'',''B13'')
            OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
        WHERE VAL.CDATE = date''${valDate}'' - 1
        GROUP BY VAL.PORTFOLIO_ID,PRD.ISSUER
    UNION ALL
    SELECT
            NVL(SUM(nvl(val.DSC_COST_AMT,0)+nvl(val.TDY_INTINCEXP,0)+nvl(val.TDY_FLOAT_INGPL,0)+nvl(val.DELAY_PAY_AMT,0)+nvl(val.TDY_LOSS_AMT,0)),0) RVALUE,
            VAL.PORTFOLIO_ID || '','' || A.ORIGINAL_OWNER RKEY
        FROM PTL_SEC_VALUTION VAL
                 INNER JOIN FIN_PRODUCT PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID AND PRD.FINPROD_TYPE2 in (''F01'',''F32'')
                 INNER JOIN FIN_PRODUCT_ADD A ON VAL.FINPROD_ID=A.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID and TYP.BRANCH= 0 AND TYP.TYPE_6=''B13'' AND TYP.TYPE_8 = ''F01''
        WHERE VAL.CDATE = date''${valDate}'' - 1
        GROUP BY VAL.PORTFOLIO_ID,A.ORIGINAL_OWNER
     UNION ALL
     ---（发行人）可转债网下申购，没有申购确认
           SELECT  nvl(sum(T.prin_amt),0) RVALUE, T1.PORTFOLIO_ID||'',''||T2.ISSUER as RKEY
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
        INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND (TYP.TYPE_6 NOT IN (''B01'',''B03'',''B17'',''B05'',''B13'')
            OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
        INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE T.PS = ''T50''
           AND T.FINPROD_TYPE2 = ''F01''
          AND T.TRADE_STATUS=''01'' and T.IS_CANCEL=''N''
           AND T.TRADE_DATE < date''${valDate}''
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
          group by T1.PORTFOLIO_ID,T2.ISSUER
       UNION ALL
     ---(原始权益人)可转债网下申购，没有申购确认
           SELECT  nvl(sum(T.prin_amt),0) RVALUE, T1.PORTFOLIO_ID||'',''||A.ORIGINAL_OWNER as RKEY
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD A ON T.FINPROD_ID=A.FINPROD_ID
        INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND TYP.TYPE_6=''B13'' AND TYP.TYPE_8 = ''F01''
        INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE T.PS = ''T50''
           AND T.FINPROD_TYPE2 = ''F01''
          AND T.TRADE_STATUS=''01'' and T.IS_CANCEL=''N''
           AND T.TRADE_DATE < date''${valDate}''
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
          group by T1.PORTFOLIO_ID,A.ORIGINAL_OWNER
 union all
      --发行人债券转托管转出方
         SELECT  SUM(nvl(T.TRADE_AMT*-1,0)) RVALUE , T1.PORTFOLIO_ID||'',''||T2.ISSUER as RKEY
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND (TYP.TYPE_6 NOT IN (''B01'',''B03'',''B17'',''B05'',''B13'')
           OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE
            T.PS  IN ( ''Z01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
          GROUP BY T1.PORTFOLIO_ID,T2.ISSUER
        union all
      --发行人债券转托管转入方
         SELECT  SUM(nvl(T.PRIN_AMT,0)) RVALUE , T1.PORTFOLIO_ID||'',''||T2.ISSUER as RKEY
          FROM TRD_FIN_PRODUCT_BARTER T
         INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND (TYP.TYPE_6 NOT IN (''B01'',''B03'',''B17'',''B05'',''B13'')
          OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T4.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE
            T4.PS  IN ( ''Z01'')
           AND T4.TRADE_STATUS = ''01''
           AND T4.IS_CANCEL = ''N''
           AND T4.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
          GROUP BY T1.PORTFOLIO_ID,T2.ISSUER
           union all
      --原始权益人债券转托管转出方
         SELECT  SUM(nvl(T.TRADE_AMT*-1,0)) RVALUE , T1.PORTFOLIO_ID||'',''||A.ORIGINAL_OWNER as RKEY
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
        INNER JOIN FIN_PRODUCT_ADD A ON T.FINPROD_ID=A.FINPROD_ID
        INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND TYP.TYPE_6=''B13'' AND TYP.TYPE_8 = ''F01''
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE
            T.PS  IN ( ''Z01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
          GROUP BY T1.PORTFOLIO_ID,A.ORIGINAL_OWNER
        union all
      --原始权益人债券转托管转入方
         SELECT  SUM(nvl(T.PRIN_AMT,0)) RVALUE , T1.PORTFOLIO_ID||'',''||A.ORIGINAL_OWNER as RKEY
          FROM TRD_FIN_PRODUCT_BARTER T
         INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD A ON T.FINPROD_ID=A.FINPROD_ID
        INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND TYP.TYPE_6=''B13'' AND TYP.TYPE_8 = ''F01''
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T4.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE
            T4.PS  IN ( ''Z01'')
           AND T4.TRADE_STATUS = ''01''
           AND T4.IS_CANCEL = ''N''
           AND T4.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
          GROUP BY T1.PORTFOLIO_ID,A.ORIGINAL_OWNER
) s1 group by s1.RKEY';

----事后(日间)
V_CONTENT_04 CLOB := 'SELECT SUM(TRADE_AMT) as RVALUE, RKEY
FROM (
         SELECT SUM(nvl(val.DSC_COST_AMT,0)+nvl(val.TDY_INTINCEXP,0)+nvl(val.TDY_FLOAT_INGPL,0)+nvl(val.DELAY_PAY_AMT,0)+nvl(val.TDY_LOSS_AMT,0)) TRADE_AMT,
           VAL.PORTFOLIO_ID || '','' || PRD.ISSUER RKEY
         FROM PTL_SEC_VALUTION VAL
                  INNER JOIN FIN_PRODUCT PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID AND PRD.FINPROD_TYPE2 in (''F01'',''F32'')
                  INNER JOIN FIN_PRODUCT_TYPE TYP
                             ON TYP.FINPROD_ID = PRD.FINPROD_ID AND (TYP.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'')
                                 OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
         WHERE VAL.CDATE = date''${valDate}'' - 1
           AND VAL.PORTFOLIO_ID IN ${portfolioCode}
         GROUP BY VAL.PORTFOLIO_ID,PRD.ISSUER
         UNION ALL
       SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
   - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
   - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
   + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
   - nvl(sum(decode(n.PS, ''T22'', n.TRADE_AMT)), 0)
   - nvl(sum(decode(n.PS, ''T23'', n.TRADE_AMT)), 0)
   + nvl(sum(decode(n.PS, ''T25'', n.TRADE_AMT)), 0)
   - nvl(sum(decode(n.PS, ''T26'', n.TRADE_AMT)), 0)
   - nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0)
   - nvl(sum(decode(n.PS, ''T07'', n.TRADE_AMT)), 0)
   - nvl(sum(decode(n.PS, ''T08'', n.TRADE_AMT)), 0)
   - nvl(sum(decode(n.PS, ''T09'', n.TRADE_AMT)), 0)
   - nvl(sum(decode(n.PS, ''T49'', n.TRADE_AMT)),
                          0) AS   TRADE_AMT,
           m.PORTFOLIO_ID || '','' || t2.ISSUER RKEY
         FROM TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                  INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 in (''F01'',''F32'')
                  INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
             AND (t3.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (t3.TYPE_6 = ''B05'' AND t3.TYPE_7 != ''01'')) AND
                                                    t3.TYPE_8 = ''F01''
         WHERE m.PORTFOLIO_ID IN ${portfolioCode}  and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
           AND n.TRADE_DATE = DATE''${valDate}''
           AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'', ''T07'', ''T08'', ''T09'')
         GROUP BY m.PORTFOLIO_ID, t2.ISSUER
            UNION  SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
                           + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T22'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T23'', n.TRADE_AMT)), 0)
                           + nvl(sum(decode(n.PS, ''T25'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T26'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T07'', n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T08'', n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T09'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T49'', n.TRADE_AMT)),
                                 0) AS   TRADE_AMT,
           m.PORTFOLIO_ID || '','' || t2.ISSUER RKEY
                FROM TRD_PRODUCT_TEMP_DEAL_ADD m
                         INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID
                         INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 in (''F01'',''F32'') AND t2.TRADE_MARKET IN (''03'',''04'')
                         INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                    AND (t3.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (t3.TYPE_6 = ''B05'' AND t3.TYPE_7 != ''01'')) AND
                                                           t3.TYPE_8 = ''F01''
                WHERE m.PORTFOLIO_ID IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
                  AND n.TRADE_DATE = DATE''${valDate}''
                  AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'', ''T07'', ''T08'', ''T09'')
         GROUP BY m.PORTFOLIO_ID, t2.ISSUER
             UNION ALL SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                            - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
                            - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
                            + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                            - nvl(sum(decode(n.PS, ''T22'', n.TRADE_AMT)), 0)
                            - nvl(sum(decode(n.PS, ''T23'', n.TRADE_AMT)), 0)
                            + nvl(sum(decode(n.PS, ''T25'', n.TRADE_AMT)), 0)
                            - nvl(sum(decode(n.PS, ''T26'', n.TRADE_AMT)), 0)
                            - nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0)
       - nvl(sum(decode(n.PS, ''T07'', n.TRADE_AMT)), 0)
          - nvl(sum(decode(n.PS, ''T08'', n.TRADE_AMT)), 0)
          - nvl(sum(decode(n.PS, ''T09'', n.TRADE_AMT)), 0)
                            - nvl(sum(decode(n.PS, ''T49'', n.TRADE_AMT)),
                                  0) AS   TRADE_AMT,
           m.PORTFOLIO_ID || '','' || t2.ISSUER RKEY
                 FROM WFL_TRD_PRODUCT_DEAL_ADD m
                          INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                          INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                          INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 in (''F01'',''F32'')
                          INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                     AND (t3.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (t3.TYPE_6 = ''B05'' AND t3.TYPE_7 != ''01'')) AND
                                                            t3.TYPE_8 = ''F01''
                 WHERE m.PORTFOLIO_ID IN ${portfolioCode}
                   AND n.TRADE_DATE = DATE''${valDate}''
                   AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'', ''T07'', ''T08'', ''T09'')
                 GROUP BY m.PORTFOLIO_ID, t2.ISSUER
                 UNION  SELECT nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)),
                           0) AS         TRADE_AMT,
           n.counter_id || '','' || t2.ISSUER RKEY
                FROM WFL_TRD_PRODUCT_DEAL_ADD m
                         INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                         INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                         INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 in (''F01'',''F32'')
                         INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0
                    AND (t3.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (t3.TYPE_6 = ''B05'' AND t3.TYPE_7 != ''01'')) AND
                                                           t3.TYPE_8 = ''F01''
               WHERE n.counter_id IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
                  AND n.TRADE_DATE = DATE''${valDate}''
                  AND n.PS = ''T02''
                  AND EXISTS(SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
                 GROUP BY n.counter_id, t2.ISSUER
  union
                --11.债券转托管（投资性质=债权类）申购没有做确认
  SELECT SUM(nvl(T4.prin_amt,0)) TRADE_AMT,  T1.PORTFOLIO_ID || '','' || t2.ISSUER RKEY
  FROM TRD_FIN_PRODUCT_BARTER T4
 INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPY ON FPY.FINPROD_ID = T2.FINPROD_ID AND FPY.BRANCH = 0
                    AND (FPY.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (FPY.TYPE_6 = ''B05'' AND FPY.TYPE_7 != ''01'')) AND
                                                           FPY.TYPE_8 = ''F01''
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 WHERE T1.PORTFOLIO_ID in ${portfolioCode} AND
     T.PS = ''Z01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE <= DATE''${valDate}''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
)
  AND NOT EXISTS (SELECT 1 FROM WFL_TRD_PRODUCT_DEAL TRD
    INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
     INNER JOIN SYS_WFL_BUSINESS_LIST swbl on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
     WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL=''N''
  )
GROUP BY T1.PORTFOLIO_ID,t2.ISSUER
union
 --12.债券转托管（投资性质=债权类）：有申购确认交易
SELECT SUM(NVL(T4.prin_amt, 0)) TRADE_AMT, T1.PORTFOLIO_ID || '','' || t2.ISSUER RKEY
  FROM TRD_FIN_PRODUCT_BARTER T4
 INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
  INNER JOIN FIN_PRODUCT_TYPE FPY ON FPY.FINPROD_ID = T2.FINPROD_ID AND FPY.BRANCH = 0
                    AND (FPY.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (FPY.TYPE_6 = ''B05'' AND FPY.TYPE_7 != ''01'')) AND
                                                           FPY.TYPE_8 = ''F01''
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.PS = ''Z02''
GROUP BY T1.PORTFOLIO_ID,t2.ISSUER
  union
  --13.债券转托管（投资性质=债权类）：申购流程中
  SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT, T3.PORTFOLIO_ID || '','' || t2.ISSUER RKEY
  FROM WFL_TRD_FIN_PRODUCT_BARTER T
 INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPY ON FPY.FINPROD_ID = T2.FINPROD_ID AND FPY.BRANCH = 0
                    AND (FPY.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (FPY.TYPE_6 = ''B05'' AND FPY.TYPE_7 != ''01'')) AND
                                                           FPY.TYPE_8 = ''F01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z01''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
GROUP BY T3.PORTFOLIO_ID,t2.ISSUER
 union
  --14.债券转托管（投资性质=债权类）：申购确认交易(流程中)
SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT, T3.PORTFOLIO_ID || '','' || t2.ISSUER RKEY
  FROM WFL_TRD_FIN_PRODUCT_BARTER T
 INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
INNER JOIN FIN_PRODUCT_TYPE FPY ON FPY.FINPROD_ID = T2.FINPROD_ID AND FPY.BRANCH = 0
                    AND (FPY.TYPE_6 NOT IN (''B01'', ''B03'', ''B17'', ''B05'',''B13'') OR (FPY.TYPE_6 = ''B05'' AND FPY.TYPE_7 != ''01'')) AND
                                                           FPY.TYPE_8 = ''F01''
 LEFT JOIN MST_CUSTOMER_INFO CST ON CST.CUSTOMER_ID = t2.ISSUER
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z02''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
GROUP BY T3.PORTFOLIO_ID,t2.ISSUER
               union
                SELECT SUM(nvl(val.DSC_COST_AMT,0)+nvl(val.TDY_INTINCEXP,0)+nvl(val.TDY_FLOAT_INGPL,0)+nvl(val.DELAY_PAY_AMT,0)+nvl(val.TDY_LOSS_AMT,0)) TRADE_AMT,
                        VAL.PORTFOLIO_ID || '','' || PRD.ORIGINAL_OWNER            RKEY
                       FROM PTL_SEC_VALUTION VAL
                INNER JOIN FIN_PRODUCT FP ON VAL.FINPROD_ID = FP.FINPROD_ID AND FP.FINPROD_TYPE2 in (''F01'',''F32'')
                INNER JOIN FIN_PRODUCT_ADD PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID
                INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID AND TYP.TYPE_6 =''B13''  AND TYP.TYPE_8 = ''F01''
                WHERE VAL.CDATE = date''${valDate}'' - 1 AND VAL.PORTFOLIO_ID IN ${portfolioCode}
                GROUP BY VAL.PORTFOLIO_ID,PRD.ORIGINAL_OWNER
               UNION  SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
                           + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T22'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T23'', n.TRADE_AMT)), 0)
                           + nvl(sum(decode(n.PS, ''T25'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T26'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T07'', n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T08'', n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T09'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T49'', n.TRADE_AMT)),
                                 0) AS   TRADE_AMT,
                       m.PORTFOLIO_ID || '','' ||PRD.ORIGINAL_OWNER         RKEY
                FROM TRD_PRODUCT_DEAL_ADD m
                INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 in (''F01'',''F32'')
                INNER JOIN FIN_PRODUCT_ADD PRD ON t2.FINPROD_ID = PRD.FINPROD_ID
                INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0  AND t3.TYPE_6 =''B13'' and t3.TYPE_8 = ''F01''
                WHERE m.PORTFOLIO_ID IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
                  AND n.TRADE_DATE = DATE''${valDate}''
                  AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'', ''T07'', ''T08'', ''T09'')
                GROUP BY  m.PORTFOLIO_ID,PRD.ORIGINAL_OWNER
               UNION  SELECT nvl(sum(decode(n.PS, ''T01'',n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T02'',n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T03'',n.TRADE_AMT)), 0)
                           + nvl(sum(decode(n.PS, ''T52'',n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T22'',n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T23'',n.TRADE_AMT)), 0)
                           + nvl(sum(decode(n.PS, ''T25'',n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T26'',n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T39'',n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T07'', n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T08'', n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T09'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T49'',n.TRADE_AMT)),
                                 0) AS   TRADE_AMT,
                m.PORTFOLIO_ID || '','' ||PRD.ORIGINAL_OWNER         RKEY
                FROM TRD_PRODUCT_TEMP_DEAL_ADD m
                INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID
                INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 in (''F01'',''F32'') AND t2.TRADE_MARKET IN (''03'',''04'')
                INNER JOIN FIN_PRODUCT_ADD PRD ON t2.FINPROD_ID = PRD.FINPROD_ID
                INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0 and t3.TYPE_6 =''B13'' and  t3.TYPE_8 = ''F01''
                WHERE m.PORTFOLIO_ID IN ${portfolioCode}  and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
                  AND n.TRADE_DATE = DATE''${valDate}''
                  AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'', ''T07'', ''T08'', ''T09'')
                GROUP BY  m.PORTFOLIO_ID,PRD.ORIGINAL_OWNER
               UNION  SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
                           + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T22'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T23'', n.TRADE_AMT)), 0)
                           + nvl(sum(decode(n.PS, ''T25'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T26'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T07'', n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T08'', n.TRADE_AMT)), 0)
         - nvl(sum(decode(n.PS, ''T09'', n.TRADE_AMT)), 0)
                           - nvl(sum(decode(n.PS, ''T49'', n.TRADE_AMT)),
                                 0) AS   TRADE_AMT,
                m.PORTFOLIO_ID || '','' ||PRD.ORIGINAL_OWNER         RKEY
                FROM WFL_TRD_PRODUCT_DEAL_ADD m
                         INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                         INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                         INNER JOIN FIN_PRODUCT t2 ON t2.FINPROD_ID = n.FINPROD_ID AND t2.FINPROD_TYPE2 in (''F01'',''F32'')
                         INNER JOIN FIN_PRODUCT_ADD PRD ON t2.FINPROD_ID = PRD.FINPROD_ID
                         INNER JOIN FIN_PRODUCT_TYPE t3 ON n.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH = 0  AND t3.TYPE_6 =''B13'' and t3.TYPE_8 = ''F01''
                WHERE m.PORTFOLIO_ID IN ${portfolioCode} and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
                  AND n.TRADE_DATE = DATE''${valDate}''
                  AND n.PS IN (''T01'', ''T02'', ''T03'', ''T52'', ''T22'', ''T23'', ''T25'', ''T26'', ''T39'', ''T49'', ''T07'', ''T08'', ''T09'')
                GROUP BY  m.PORTFOLIO_ID,PRD.ORIGINAL_OWNER
                UNION  SELECT nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)),0) AS         TRADE_AMT,
                n.counter_id|| '','' ||PRD.ORIGINAL_OWNER         RKEY
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
                GROUP BY  n.counter_id,PRD.ORIGINAL_OWNER
    union
                --11.债券转托管（投资性质=债权类）申购没有做确认
  SELECT SUM(nvl(T4.prin_amt,0)) TRADE_AMT, T1.PORTFOLIO_ID|| '','' ||PRD.ORIGINAL_OWNER RKEY
  FROM TRD_FIN_PRODUCT_BARTER T4
 INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
INNER JOIN FIN_PRODUCT_ADD PRD ON t2.FINPROD_ID = PRD.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPY ON FPY.FINPROD_ID = T2.FINPROD_ID AND FPY.BRANCH = 0 AND FPY.TYPE_6 =''B13'' and FPY.TYPE_8 = ''F01''
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 WHERE T1.PORTFOLIO_ID in ${portfolioCode} AND
     T.PS = ''Z01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE <= DATE''${valDate}''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
)
  AND NOT EXISTS (SELECT 1 FROM WFL_TRD_PRODUCT_DEAL TRD
    INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
     INNER JOIN SYS_WFL_BUSINESS_LIST swbl on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
     WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL=''N''
  )
 GROUP BY T1.PORTFOLIO_ID,PRD.ORIGINAL_OWNER
union
 --12.债券转托管（投资性质=债权类）：有申购确认交易
SELECT SUM(NVL(T4.prin_amt, 0)) TRADE_AMT, T1.PORTFOLIO_ID|| '','' ||PRD.ORIGINAL_OWNER RKEY
  FROM TRD_FIN_PRODUCT_BARTER T4
 INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PRD ON t2.FINPROD_ID = PRD.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPY ON FPY.FINPROD_ID = T2.FINPROD_ID AND FPY.BRANCH = 0 AND FPY.TYPE_6 =''B13'' and FPY.TYPE_8 = ''F01''
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.PS = ''Z02''
 GROUP BY T1.PORTFOLIO_ID,PRD.ORIGINAL_OWNER
  union
  --13.债券转托管（投资性质=债权类）：申购流程中
  SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT,T3.PORTFOLIO_ID|| '','' ||PRD.ORIGINAL_OWNER RKEY
  FROM WFL_TRD_FIN_PRODUCT_BARTER T
 INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PRD ON t2.FINPROD_ID = PRD.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPY ON FPY.FINPROD_ID = T2.FINPROD_ID AND FPY.BRANCH = 0 AND FPY.TYPE_6 =''B13'' and FPY.TYPE_8 = ''F01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z01''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
 GROUP BY T3.PORTFOLIO_ID,PRD.ORIGINAL_OWNER
 union
  --14.债券转托管（投资性质=债权类）：申购确认交易(流程中)
SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT,T3.PORTFOLIO_ID|| '','' ||PRD.ORIGINAL_OWNER RKEY
  FROM WFL_TRD_FIN_PRODUCT_BARTER T
 INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PRD ON t2.FINPROD_ID = PRD.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPY ON FPY.FINPROD_ID = T2.FINPROD_ID AND FPY.BRANCH = 0 AND FPY.TYPE_6 =''B13'' and FPY.TYPE_8 = ''F01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z02''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
 GROUP BY T3.PORTFOLIO_ID,PRD.ORIGINAL_OWNER
)
GROUP BY RKEY';

----事后(日终)
V_CONTENT_05 CLOB := 'SELECT nvl(sum(nvl(RVALUE, 0)),0) as RVALUE,s1.RKEY  FROM (
SELECT
            NVL(VAL.DSC_COST_AMT,0)+NVL(VAL.TDY_INTINCEXP,0)+NVL(VAL.TDY_FLOAT_INGPL,0)+NVL(VAL.DELAY_PAY_AMT,0)+NVL(VAL.TDY_LOSS_AMT,0) RVALUE,
           VAL.PORTFOLIO_ID || '','' || PRD.ISSUER RKEY
        FROM PTL_SEC_VALUTION VAL
                 INNER JOIN FIN_PRODUCT PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID AND PRD.FINPROD_TYPE2 in (''F01'',''F32'')
                 INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID AND (TYP.TYPE_6 NOT IN (''B01'',''B03'',''B17'',''B05'')
            OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
        WHERE VAL.CDATE = date''${valDate}''  AND VAL.PORTFOLIO_ID IN ${portfolioCode}
        GROUP BY VAL.PORTFOLIO_ID,PRD.ISSUER
    UNION
SELECT
            NVL(VAL.DSC_COST_AMT,0)+NVL(VAL.TDY_INTINCEXP,0)+NVL(VAL.TDY_FLOAT_INGPL,0)+NVL(VAL.DELAY_PAY_AMT,0)+NVL(VAL.TDY_LOSS_AMT,0) RVALUE,
            VAL.PORTFOLIO_ID || '','' || A.ORIGINAL_OWNER RKEY
        FROM PTL_SEC_VALUTION VAL
                 INNER JOIN FIN_PRODUCT PRD ON VAL.FINPROD_ID = PRD.FINPROD_ID AND PRD.FINPROD_TYPE2 in (''F01'',''F32'')
                 INNER JOIN FIN_PRODUCT_ADD A ON VAL.FINPROD_ID=A.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = PRD.FINPROD_ID and TYP.BRANCH= 0 AND TYP.TYPE_6=''B13'' AND TYP.TYPE_8 = ''F01''
        WHERE VAL.CDATE = date''${valDate}'' AND VAL.PORTFOLIO_ID IN ${portfolioCode}
        GROUP BY VAL.PORTFOLIO_ID,A.ORIGINAL_OWNER
      UNION
           ---（发行人）可转债网下申购，没有申购确认
           SELECT  nvl(sum(T.prin_amt),0) RVALUE, T1.PORTFOLIO_ID||'',''||T2.ISSUER as RKEY
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
        INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND (TYP.TYPE_6 NOT IN (''B01'',''B03'',''B17'',''B05'',''B13'')
            OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
        INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE T.PS = ''T50''
           AND T.FINPROD_TYPE2 = ''F01''
          AND T.TRADE_STATUS=''01'' and T.IS_CANCEL=''N''
           AND T.TRADE_DATE <= date''${valDate}'' AND T1.PORTFOLIO_ID IN ${portfolioCode}
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
          group by T1.PORTFOLIO_ID,T2.ISSUER
       UNION
     ---(原始权益人)可转债网下申购，没有申购确认
           SELECT  nvl(sum(T.prin_amt),0) RVALUE, T1.PORTFOLIO_ID||'',''||A.ORIGINAL_OWNER as RKEY
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD A ON T.FINPROD_ID=A.FINPROD_ID
        INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND TYP.TYPE_6=''B13'' AND TYP.TYPE_8 = ''F01''
        INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE T.PS = ''T50''
           AND T.FINPROD_TYPE2 = ''F01''
          AND T.TRADE_STATUS=''01'' and T.IS_CANCEL=''N''
           AND T.TRADE_DATE <= date''${valDate}'' AND T1.PORTFOLIO_ID IN ${portfolioCode}
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
          group by T1.PORTFOLIO_ID,A.ORIGINAL_OWNER
 union
      --发行人债券转托管转出方
         SELECT  SUM(nvl(T.TRADE_AMT*-1,0)) RVALUE , T1.PORTFOLIO_ID||'',''||T2.ISSUER as RKEY
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND (TYP.TYPE_6 NOT IN (''B01'',''B03'',''B17'',''B05'',''B13'')
           OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE
            T.PS  IN ( ''Z01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <= DATE''${valDate}'' AND T1.PORTFOLIO_ID IN ${portfolioCode}
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
          GROUP BY T1.PORTFOLIO_ID,T2.ISSUER
        union
      --发行人债券转托管转入方
         SELECT  SUM(nvl(T.PRIN_AMT,0)) RVALUE , T1.PORTFOLIO_ID||'',''||T2.ISSUER as RKEY
          FROM TRD_FIN_PRODUCT_BARTER T
         INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND (TYP.TYPE_6 NOT IN (''B01'',''B03'',''B17'',''B05'',''B13'')
          OR (TYP.TYPE_6 = ''B05'' AND TYP.TYPE_7 != ''01'')) AND TYP.TYPE_8 = ''F01''
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T4.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE
            T4.PS  IN ( ''Z01'')
           AND T4.TRADE_STATUS = ''01''
           AND T4.IS_CANCEL = ''N''
           AND T4.TRADE_DATE <= DATE''${valDate}'' AND T1.PORTFOLIO_ID IN ${portfolioCode}
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
          GROUP BY T1.PORTFOLIO_ID,T2.ISSUER
           union
      --原始权益人债券转托管转出方
         SELECT  SUM(nvl(T.TRADE_AMT*-1,0)) RVALUE , T1.PORTFOLIO_ID||'',''||A.ORIGINAL_OWNER as RKEY
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
        INNER JOIN FIN_PRODUCT_ADD A ON T.FINPROD_ID=A.FINPROD_ID
        INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND TYP.TYPE_6=''B13'' AND TYP.TYPE_8 = ''F01''
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE
            T.PS  IN ( ''Z01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <= DATE''${valDate}'' AND T1.PORTFOLIO_ID IN ${portfolioCode}
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
          GROUP BY T1.PORTFOLIO_ID,A.ORIGINAL_OWNER
        union all
      --原始权益人债券转托管转入方
         SELECT  SUM(nvl(T.PRIN_AMT,0)) RVALUE , T1.PORTFOLIO_ID||'',''||A.ORIGINAL_OWNER as RKEY
          FROM TRD_FIN_PRODUCT_BARTER T
         INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD A ON T.FINPROD_ID=A.FINPROD_ID
        INNER JOIN FIN_PRODUCT_TYPE TYP ON TYP.FINPROD_ID = T2.FINPROD_ID and TYP.BRANCH= 0 AND TYP.TYPE_6=''B13'' AND TYP.TYPE_8 = ''F01''
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T4.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE
            T4.PS  IN ( ''Z01'')
           AND T4.TRADE_STATUS = ''01''
           AND T4.IS_CANCEL = ''N''
           AND T4.TRADE_DATE <= DATE''${valDate}'' AND T1.PORTFOLIO_ID IN ${portfolioCode}
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
          GROUP BY T1.PORTFOLIO_ID,A.ORIGINAL_OWNER
 ) s1 group by s1.RKEY';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'JGZQGZHJ' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'JGZQGZHJ' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'JGZQGZHJ' and PARA_GRANULARITY = '05';
END;
/
