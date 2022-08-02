/**风险指标参数:组合资产剩余存续期限**/
----风险监督指标参数
DECLARE
----事后(日间)
V_CONTENT_04 CLOB := 'WITH ASSET AS (
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
)
select NVL(r1.SHARE_AMT - R2.fee_amt,0) RVALUE
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
                           UNION ALL
                           SELECT SUM(CASE
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
                           UNION ALL
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
                       ) TEMP
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
      group by bp.FINPROD_ID) r2 on ''F16_'' || r1.PORTFOLIO_ID = r2.FINPROD_ID';

----事后(日终)
V_CONTENT_05 CLOB := 'WITH ASSET AS (
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
)
select NVL(r1.SHARE_AMT - R2.fee_amt,0) RVALUE
from (
         SELECT NVL(SUM(SHARE_AMT * NVL(LAST_PERIOD, 0)), 0) SHARE_AMT, PORTFOLIO_ID
         FROM (
                  SELECT SUM(NVL(PSP.MARKET_VALUE, 0) + NVL(TDY_INTINCEXP, 0)) SHARE_AMT,
                         PSP.FINPROD_ID,
                         ASSET.LAST_PERIOD,
                         PSP.PORTFOLIO_ID
                  FROM PTL_SEC_PIERCE_VALUTION PSP
                           INNER JOIN ASSET ON ASSET.FINPROD_ID = PSP.FINPROD_ID
                           LEFT JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = ASSET.FINPROD_ID
                  WHERE PSP.CDATE = DATE ''${valDate}''
                    AND PSP.PORTFOLIO_ID IN ${portfolioCode}
                    AND ((PSP.SUB_CODE IS NULL AND FPT.TYPE_8 IN (''F04'', ''F09'')) OR FPT.TYPE_8 NOT IN (''F04'', ''F09''))
                    AND ASSET.LAST_PERIOD >= 0
                  GROUP BY PSP.FINPROD_ID, ASSET.LAST_PERIOD, PSP.PORTFOLIO_ID
              )
         group by PORTFOLIO_ID
     ) r1
         left join
     -- 负债类合计
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
            and bp.cdate = DATE ''${valDate}''
          group by bp.FINPROD_ID) r2 on ''F16_'' || r1.PORTFOLIO_ID = r2.FINPROD_ID';

BEGIN
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'ZCSYCXQX' and PARA_GRANULARITY ='04';
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'ZCSYCXQX' and PARA_GRANULARITY = '05';
END;
/
