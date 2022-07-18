/**风险指标参数:7个工作日可变现资产的可变现价值**/
----风险监督指标参数

DECLARE
---- 实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'WITH FIN_OPEN_PLAN_AST AS (
    /**申购开放(这里三个union是为了取的顺序赎回优先,无赎回则取申购)**/
    SELECT CASH_ID,
           FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''01''
      AND NOT EXISTS(
            SELECT 1 FROM FIN_OPEN_PLAN fop2 WHERE fop2.OPEN_TYPE = ''02'' AND fop.CASH_ID = fop2.CASH_ID
        )
        /**赎回开放**/
    UNION ALL
    SELECT CASH_ID,
           FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''02''
        /**申赎同频**/
    UNION ALL
    SELECT CASH_ID,
           fop.FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''03''
)
   , FIN_CASH_PLAN_COST AS (
    /**还本计划**/
    SELECT CASH_ID,
           FINPROD_ID,
           PAY_DATE
    FROM FIN_CASH_PLAN fop
    WHERE fop.FINPROD_TYPE2 IN (''F01'', ''F18'')
      AND EXISTS(
            SELECT 1
            FROM FIN_CASH_INFO fop2
            WHERE fop2.CASH_TYPE_1 = ''01'' AND fop2.CASH_TYPE_2 = ''C01'' AND fop.CASH_ID = fop2.CASH_ID
        )
)
   , FIN_CASH_PLAN_RATE AS (
    /**付息计划**/
    SELECT CASH_ID,
           FINPROD_ID,
           PAY_DATE
    FROM FIN_CASH_PLAN fop
    WHERE fop.FINPROD_TYPE2 IN (''F01'', ''F18'')
      AND EXISTS(
            SELECT 1
            FROM FIN_CASH_INFO fop2
            WHERE fop2.CASH_TYPE_1 = ''02'' AND fop2.CASH_TYPE_2 = ''C02'' AND fop.CASH_ID = fop2.CASH_ID
        )
)
   , PROD AS (
    -- 资产到期7个交易日以内(含)
    SELECT FINPROD_ID
    FROM (
             SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN
             FROM (
                      SELECT m.FINPROD_ID,
                             m.VDATE,
                             m.MDATE,
                             MH.CDATE,
                             ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO
                      FROM (
                               -- 存款类资产,项目类资产,债券资产支持证券(到期)
                               SELECT FPT.FINPROD_ID, FPT.VDATE, FPT.MDATE, NVL(FPT.CALENDAR_ID, ''01'') CALENDAR_ID
                               FROM FIN_PRODUCT FPT
                                        INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
                               WHERE T1.TYPE_8 IN (''F05'', ''F06'')
                                  OR (T1.TYPE_8 IN (''F01'', ''F17'') AND T1.TYPE_9 = ''B13'')
                                  --可赎回日在7个交易日以内计划类资产(高频开放)
                               UNION ALL
                               select n.FINPROD_ID, M.VDATE, MDATE, NVL(M.CALENDAR_ID, ''01'') CALENDAR_ID
                               from FIN_PRODUCT m
                                        INNER JOIN FIN_PRODUCT_TYPE FPT
                                                   ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 IN (''F03'', ''F04'', ''F09'')
                                        INNER JOIN FIN_PRODUCT_ADD FPA
                                                   ON m.FINPROD_ID = FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
                                        INNER JOIN FIN_CASH_INFO n
                                                   ON n.FINPROD_ID = m.FINPROD_ID AND n.CASH_TYPE_1 = ''01'' AND
                                                      n.CASH_TYPE_2 = ''C01''
                                        INNER JOIN FIN_OPEN_RULE r ON r.CASH_ID = n.CASH_ID AND r.OPEN_MODE = ''02''
                                    --可赎回日在7个交易日以内计划类资产(非高频开放),赎回确定日刚好是平台日也统计
                               UNION ALL
                               SELECT FINPROD_ID, VDATE, OPEN_VDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.OPEN_VDATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                             CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.OPEN_VDATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 IN (''F03'', ''F04'', ''F09'')
                                                 INNER JOIN FIN_PRODUCT_ADD FPA
                                                            ON m.FINPROD_ID = FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
                                                 INNER JOIN FIN_CASH_INFO n
                                                            ON n.FINPROD_ID = m.FINPROD_ID AND n.CASH_TYPE_1 = ''01'' AND
                                                               n.CASH_TYPE_2 = ''C01''
                                                 INNER JOIN FIN_OPEN_PLAN_AST y ON y.CASH_ID = n.CASH_ID
                                        WHERE y.OPEN_VDATE >= date''${valDate}''
                                        ORDER BY y.OPEN_VDATE ASC
                                    )
                               WHERE rowno = 1
                               UNION ALL
                               -- 逆回购(到期)
                               SELECT FTP.FINPROD_ID, FTP.VDATE, FTP.MDATE, NVL(FTP.CALENDAR_ID, ''01'') CALENDAR_ID
                               FROM FIN_TRADE_PRODUCT FTP
                                        INNER JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                               WHERE FTP.MDATE IS NOT NULL
                                 AND T1.TYPE_8 IN (''F07'', ''F10'')
                                 AND TYPE_7 = ''04''
                                 AND FTP.MDATE >= DATE ''${valDate}'' - 1
                               UNION ALL
                               -- 拆出(到期)
                               SELECT FTP.FINPROD_ID, FTP.VDATE, FTP.MDATE, NVL(FTP.CALENDAR_ID, ''01'') CALENDAR_ID
                               FROM FIN_TRADE_PRODUCT FTP
                                        INNER JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                               WHERE FTP.MDATE IS NOT NULL
                                 AND T1.TYPE_8 = ''F08''
                                 AND TYPE_7 = ''02''
                                 AND FTP.MDATE >= DATE ''${valDate}'' - 1
                           ) m
                               INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND
                                                            MH.CDATE >= m.MDATE - 15 AND MH.IS_HOLIDAY = ''0''
                  )
             WHERE cdate >= vdate
               and vdate < = DATE ''${valDate}''
               and CDATE >= DATE ''${valDate}''
         )
    GROUP BY FINPROD_ID
    HAVING count(1) <= 8
    UNION
    --资产还本第7个交易日以内(含)
    SELECT FINPROD_ID
    FROM (
             SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN
             FROM (
                      SELECT m.FINPROD_ID,
                             m.VDATE,
                             m.MDATE,
                             MH.CDATE,
                             ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO
                      FROM (
                               -- 债券资产支持证券(还本),平台日当天还本也统计
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F01'' AND TYPE_9 = ''B13''
                                                 INNER JOIN FIN_CASH_PLAN_COST y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                                     --项目类(还本)
                               UNION ALL
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F06''
                                                 INNER JOIN FIN_CASH_PLAN_COST y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                           ) m
                               INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND
                                                            MH.CDATE >= m.MDATE - 15 AND MH.IS_HOLIDAY = ''0''
                  )
             WHERE cdate >= vdate
               and vdate < = DATE ''${valDate}''
               and CDATE >= DATE ''${valDate}''
         )
    GROUP BY FINPROD_ID
    HAVING count(1) <= 8
    UNION
    --资产付息第7个交易日以内(含)
    SELECT FINPROD_ID
    FROM (
             SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN
             FROM (
                      SELECT m.FINPROD_ID,
                             m.VDATE,
                             m.MDATE,
                             MH.CDATE,
                             ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO
                      FROM (
                               -- 债券资产支持证券(付息),平台日当天付息也统计
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F01'' AND TYPE_9 = ''B13''
                                                 INNER JOIN FIN_CASH_PLAN_RATE y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                                     --项目类(付息)
                               UNION ALL
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F06''
                                                 INNER JOIN FIN_CASH_PLAN_RATE y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                           ) m
                               INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND
                                                            MH.CDATE >= m.MDATE - 15 AND MH.IS_HOLIDAY = ''0''
                  )
             WHERE cdate >= vdate
               and vdate < = DATE ''${valDate}''
               and CDATE >= DATE ''${valDate}''
         )
    GROUP BY FINPROD_ID
    HAVING count(1) <= 8
    UNION
    --7日可变现公募基金
    SELECT FPT.FINPROD_ID
    FROM FIN_PRODUCT FPT
             INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FPT.FINPROD_ID AND FPA.IS_SEVEN_DAY_CASHABLE = ''Y''
             INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
    UNION
    -- 计划类资产(每日申赎开放)
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FP.FINPROD_ID
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F03'', ''F04'', ''F09'')
      AND FPA.IS_OPEN_EVERYDAY = ''Y''
    UNION
    -- 存款类无条件可提前支取
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
             INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F05''
      AND FPA.IS_INADVANCE = ''03''
    UNION
    -- 债券
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
         -- 排除流动性受限,资产支持证券
    WHERE FPT.TYPE_8 IN (''F01'', ''F17'')
      AND FPT.TYPE_9 <> ''B13''
    UNION
    -- 信用保护凭证
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F37''
      -- 委外逆回购
    UNION
    SELECT M.FINPROD_ID
    FROM PTL_SEC_PIERCE_VALUTION M
    WHERE M.CDATE = date''${valDate}'' - 1
      AND substr(m.FINPROD_ID, 0, 4) IN (''F03_'', ''F05_'')
    UNION
    --上市交易股票股票类型】字段为''普通股''股票行情信息界面中，【交易状态】字段显示为''交易''
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
             INNER JOIN REP_ASSET_EQUITY RAE ON RAE.REP_FINPROD_ID = FP.FINPROD_ID AND REP_SHARES_TYPE = ''01''
             INNER JOIN (
        SELECT DISTINCT m1.SHARES_ID
        FROM mst_share_price m1
        WHERE TRADE_STATUS = ''1''
          AND NOT EXISTS(
                SELECT 1
                FROM mst_share_price m2
                WHERE m1.SHARES_ID = m2.SHARES_ID
                  AND m1.DEAL_DATE < m2.DEAL_DATE
                  AND m2.DEAL_DATE <= DATE ''${valDate}''
            )
    ) MSP ON FP.FINPROD_MARKET_ID = MSP.SHARES_ID
         -- 排除流动性受限
    WHERE FPT.TYPE_8 = ''F11''
)
   , ASSET_WW AS (
---查询委外组合估值表
    SELECT PTL_ID, SUB_CODE, SMV, SECURITY_QUANTITY, VAL_DATE
    FROM (SELECT PTL_ID,
                 SUB_CODE,
                 SMV,
                 SECURITY_QUANTITY,
                 VAL_DATE,
                 rank() OVER (PARTITION BY PTL_ID ORDER BY VAL_DATE desc) AS rowno
          FROM RSK_VALTBL_SUM_ASSET_SUB
          WHERE SECURITY_QUANTITY IS NOT NULL
            AND VAL_DATE <= date''${valDate}''
         )
    WHERE ROWNO = 1
)

SELECT NVL(SUM(RVALUE), 0) RVALUE, RKEY
FROM (
         SELECT SUM(nvl(tpd.SHARE_AMT, 0) / bb.SECURITY_QUANTITY * rvas.SMV) AS RVALUE,
                tpd.PORTFOLIO_ID                                                RKEY
         FROM PTL_SEC_VALUTION tpd
                  --自主管理的计划类资产
                  INNER JOIN FIN_PRODUCT_TYPE FTP on tpd.FINPROD_ID = FTP.FINPROD_ID and FTP.TYPE_10 = ''N004''
                  INNER JOIN RSK_VALTBL_ASSET_SUB rvas
                             ON tpd.FINPROD_ID = ''F17_'' || rvas.PTL_ID AND rvas.FINPROD_ID IS NOT NULL AND
                                rvas.SMV IS NOT NULL
                  INNER JOIN ASSET_WW bb
                             on rvas.PTL_ID = bb.PTL_ID and bb.SUB_CODE = ''601'' AND bb.VAL_DATE = rvas.VAL_DATE
                  INNER JOIN PROD N ON rvas.FINPROD_ID = N.FINPROD_ID
         WHERE tpd.CDATE = date''${valDate}'' - 1
         GROUP BY tpd.PORTFOLIO_ID
         UNION ALL
         SELECT NVL(SUM(NVL(M.DSC_COST_AMT, 0) + NVL(M.TDY_INTINCEXP, 0) + NVL(M.TDY_FLOAT_INGPL, 0) +
                        NVL(M.DELAY_PAY_AMT, 0) + NVL(M.TDY_LOSS_AMT, 0)), 0) RVALUE,
                M.PORTFOLIO_ID                                                RKEY
         FROM PTL_SEC_VALUTION M
                  INNER JOIN PROD N ON M.FINPROD_ID = N.FINPROD_ID
                  INNER JOIN FIN_PRODUCT_TYPE FPT ON m.FINPROD_ID = FPT.FINPROD_ID and
                                                     ((FPT.TYPE_8 = ''F04'' AND FPT.TYPE_10 <> ''N004'') OR FPT.TYPE_8 <> ''F04'')
         WHERE M.CDATE = date''${valDate}'' - 1
         GROUP BY M.PORTFOLIO_ID
     )
GROUP BY RKEY';

----事后(日间)
V_CONTENT_04 CLOB := 'WITH FIN_OPEN_PLAN_AST AS (
    /**申购开放(这里三个union是为了取的顺序赎回优先,无赎回则取申购)**/
    SELECT CASH_ID,
           FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''01''
      AND NOT EXISTS(
            SELECT 1 FROM FIN_OPEN_PLAN fop2 WHERE fop2.OPEN_TYPE = ''02'' AND fop.CASH_ID = fop2.CASH_ID
        )
        /**赎回开放**/
    UNION ALL
    SELECT CASH_ID,
           FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''02''
        /**申赎同频**/
    UNION ALL
    SELECT CASH_ID,
           fop.FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''03''
)
   , FIN_CASH_PLAN_COST AS (
    /**还本计划**/
    SELECT CASH_ID,
           FINPROD_ID,
           PAY_DATE
    FROM FIN_CASH_PLAN fop
    WHERE fop.FINPROD_TYPE2 IN (''F01'', ''F18'')
      AND EXISTS(
            SELECT 1
            FROM FIN_CASH_INFO fop2
            WHERE fop2.CASH_TYPE_1 = ''01'' AND fop2.CASH_TYPE_2 = ''C01'' AND fop.CASH_ID = fop2.CASH_ID
        )
)
   , FIN_CASH_PLAN_RATE AS (
    /**付息计划**/
    SELECT CASH_ID,
           FINPROD_ID,
           PAY_DATE
    FROM FIN_CASH_PLAN fop
    WHERE fop.FINPROD_TYPE2 IN (''F01'', ''F18'')
      AND EXISTS(
            SELECT 1
            FROM FIN_CASH_INFO fop2
            WHERE fop2.CASH_TYPE_1 = ''02'' AND fop2.CASH_TYPE_2 = ''C02'' AND fop.CASH_ID = fop2.CASH_ID
        )
)
   , PROD AS (
    -- 资产到期7个交易日以内(含)
    SELECT FINPROD_ID
    FROM (
             SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN
             FROM (
                      SELECT m.FINPROD_ID,
                             m.VDATE,
                             m.MDATE,
                             MH.CDATE,
                             ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO
                      FROM (
                               -- 存款类资产,项目类资产,债券资产支持证券(到期)
                               SELECT FPT.FINPROD_ID, FPT.VDATE, FPT.MDATE, NVL(FPT.CALENDAR_ID, ''01'') CALENDAR_ID
                               FROM FIN_PRODUCT FPT
                                        INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
                               WHERE T1.TYPE_8 IN (''F05'', ''F06'')
                                  OR (T1.TYPE_8 IN (''F01'', ''F17'') AND T1.TYPE_9 = ''B13'')
                                  --可赎回日在7个交易日以内计划类资产(高频开放)
                               UNION ALL
                               select n.FINPROD_ID, M.VDATE, MDATE, NVL(M.CALENDAR_ID, ''01'') CALENDAR_ID
                               from FIN_PRODUCT m
                                        INNER JOIN FIN_PRODUCT_TYPE FPT
                                                   ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 IN (''F03'', ''F04'', ''F09'')
                                        INNER JOIN FIN_PRODUCT_ADD FPA
                                                   ON m.FINPROD_ID = FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
                                        INNER JOIN FIN_CASH_INFO n
                                                   ON n.FINPROD_ID = m.FINPROD_ID AND n.CASH_TYPE_1 = ''01'' AND
                                                      n.CASH_TYPE_2 = ''C01''
                                        INNER JOIN FIN_OPEN_RULE r ON r.CASH_ID = n.CASH_ID AND r.OPEN_MODE = ''02''
                                    --可赎回日在7个交易日以内计划类资产(非高频开放),赎回确定日刚好是平台日也统计
                               UNION ALL
                               SELECT FINPROD_ID, VDATE, OPEN_VDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.OPEN_VDATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                             CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.OPEN_VDATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 IN (''F03'', ''F04'', ''F09'')
                                                 INNER JOIN FIN_PRODUCT_ADD FPA
                                                            ON m.FINPROD_ID = FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
                                                 INNER JOIN FIN_CASH_INFO n
                                                            ON n.FINPROD_ID = m.FINPROD_ID AND n.CASH_TYPE_1 = ''01'' AND
                                                               n.CASH_TYPE_2 = ''C01''
                                                 INNER JOIN FIN_OPEN_PLAN_AST y ON y.CASH_ID = n.CASH_ID
                                        WHERE y.OPEN_VDATE >= date''${valDate}''
                                        ORDER BY y.OPEN_VDATE ASC
                                    )
                               WHERE rowno = 1
                               UNION ALL
                               -- 逆回购(到期)
                               SELECT FTP.FINPROD_ID, FTP.VDATE, FTP.MDATE, NVL(FTP.CALENDAR_ID, ''01'') CALENDAR_ID
                               FROM FIN_TRADE_PRODUCT FTP
                                        INNER JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                               WHERE FTP.MDATE IS NOT NULL
                                 AND T1.TYPE_8 IN (''F07'', ''F10'')
                                 AND TYPE_7 = ''04''
                                 AND FTP.MDATE >= DATE ''${valDate}'' - 1
                               UNION ALL
                               -- 拆出(到期)
                               SELECT FTP.FINPROD_ID, FTP.VDATE, FTP.MDATE, NVL(FTP.CALENDAR_ID, ''01'') CALENDAR_ID
                               FROM FIN_TRADE_PRODUCT FTP
                                        INNER JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                               WHERE FTP.MDATE IS NOT NULL
                                 AND T1.TYPE_8 = ''F08''
                                 AND TYPE_7 = ''02''
                                 AND FTP.MDATE >= DATE ''${valDate}'' - 1
                           ) m
                               INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND
                                                            MH.CDATE >= m.MDATE - 15 AND MH.IS_HOLIDAY = ''0''
                  )
             WHERE cdate >= vdate
               and vdate < = DATE ''${valDate}''
               and CDATE >= DATE ''${valDate}''
         )
    GROUP BY FINPROD_ID
    HAVING count(1) <= 8
    UNION
    --资产还本第7个交易日以内(含)
    SELECT FINPROD_ID
    FROM (
             SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN
             FROM (
                      SELECT m.FINPROD_ID,
                             m.VDATE,
                             m.MDATE,
                             MH.CDATE,
                             ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO
                      FROM (
                               -- 债券资产支持证券(还本),平台日当天还本也统计
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F01'' AND TYPE_9 = ''B13''
                                                 INNER JOIN FIN_CASH_PLAN_COST y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                                     --项目类(还本)
                               UNION ALL
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F06''
                                                 INNER JOIN FIN_CASH_PLAN_COST y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                           ) m
                               INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND
                                                            MH.CDATE >= m.MDATE - 15 AND MH.IS_HOLIDAY = ''0''
                  )
             WHERE cdate >= vdate
               and vdate < = DATE ''${valDate}''
               and CDATE >= DATE ''${valDate}''
         )
    GROUP BY FINPROD_ID
    HAVING count(1) <= 8
    UNION
    --资产付息第7个交易日以内(含)
    SELECT FINPROD_ID
    FROM (
             SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN
             FROM (
                      SELECT m.FINPROD_ID,
                             m.VDATE,
                             m.MDATE,
                             MH.CDATE,
                             ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO
                      FROM (
                               -- 债券资产支持证券(付息),平台日当天付息也统计
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F01'' AND TYPE_9 = ''B13''
                                                 INNER JOIN FIN_CASH_PLAN_RATE y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                                     --项目类(付息)
                               UNION ALL
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F06''
                                                 INNER JOIN FIN_CASH_PLAN_RATE y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                           ) m
                               INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND
                                                            MH.CDATE >= m.MDATE - 15 AND MH.IS_HOLIDAY = ''0''
                  )
             WHERE cdate >= vdate
               and vdate < = DATE ''${valDate}''
               and CDATE >= DATE ''${valDate}''
         )
    GROUP BY FINPROD_ID
    HAVING count(1) <= 8
    UNION
    --7日可变现公募基金
    SELECT FPT.FINPROD_ID
    FROM FIN_PRODUCT FPT
             INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FPT.FINPROD_ID AND FPA.IS_SEVEN_DAY_CASHABLE = ''Y''
             INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
    UNION
    -- 计划类资产(每日申赎开放)
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FP.FINPROD_ID
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F03'', ''F04'', ''F09'')
      AND FPA.IS_OPEN_EVERYDAY = ''Y''
    UNION
    -- 存款类无条件可提前支取
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
             INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F05''
      AND FPA.IS_INADVANCE = ''03''
    UNION
    -- 债券
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
         -- 排除流动性受限,资产支持证券
    WHERE FPT.TYPE_8 IN (''F01'', ''F17'')
      AND FPT.TYPE_9 <> ''B13''
    UNION
    -- 信用保护凭证
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F37''
    -- 委外逆回购
    UNION
    SELECT M.FINPROD_ID
    FROM PTL_SEC_PIERCE_VALUTION M
    WHERE M.CDATE = date''${valDate}'' - 1
      AND substr(m.FINPROD_ID, 0, 4) IN (''F03_'', ''F05_'')
    UNION
    --上市交易股票股票类型】字段为''普通股''股票行情信息界面中，【交易状态】字段显示为''交易''
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
             INNER JOIN REP_ASSET_EQUITY RAE ON RAE.REP_FINPROD_ID = FP.FINPROD_ID AND REP_SHARES_TYPE = ''01''
             INNER JOIN (
        SELECT DISTINCT m1.SHARES_ID
        FROM mst_share_price m1
        WHERE TRADE_STATUS = ''1''
          AND NOT EXISTS(
                SELECT 1
                FROM mst_share_price m2
                WHERE m1.SHARES_ID = m2.SHARES_ID
                  AND m1.DEAL_DATE < m2.DEAL_DATE
                  AND m2.DEAL_DATE <= DATE ''${valDate}''
            )
    ) MSP ON FP.FINPROD_MARKET_ID = MSP.SHARES_ID
         -- 排除流动性受限
    WHERE FPT.TYPE_8 = ''F11''
)
   , ASSET_WW AS (
---查询委外组合估值表
    SELECT PTL_ID, SUB_CODE, SMV, SECURITY_QUANTITY, VAL_DATE
    FROM (SELECT PTL_ID,
                 SUB_CODE,
                 SMV,
                 SECURITY_QUANTITY,
                 VAL_DATE,
                 rank() OVER (PARTITION BY PTL_ID ORDER BY VAL_DATE desc) AS rowno
          FROM RSK_VALTBL_SUM_ASSET_SUB
          WHERE SECURITY_QUANTITY IS NOT NULL
            AND VAL_DATE <= date''${valDate}''
         )
    WHERE ROWNO = 1
)
SELECT SUM(TRADE_AMT) RVALUE
FROM (
         SELECT NVL(SUM(NVL(M.DSC_COST_AMT, 0) + NVL(M.TDY_INTINCEXP, 0) + NVL(M.TDY_FLOAT_INGPL, 0) +
                        NVL(M.DELAY_PAY_AMT, 0) + NVL(M.TDY_LOSS_AMT, 0)), 0) TRADE_AMT
         FROM PTL_SEC_VALUTION M
                  INNER JOIN PROD N ON M.FINPROD_ID = N.FINPROD_ID
             --排除自主管理的计划类资产
                  INNER JOIN FIN_PRODUCT_TYPE FPT ON m.FINPROD_ID = FPT.FINPROD_ID and
                                                     ((FPT.TYPE_8 = ''F04'' AND FPT.TYPE_10 <> ''N004'') OR FPT.TYPE_8 <> ''F04'')
         WHERE M.CDATE = date''${valDate}'' - 1
           AND M.PORTFOLIO_ID IN ${portfolioCode}
         UNION ALL
         SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
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
                    - nvl(sum(decode(n.PS, ''T56'', n.TRADE_AMT)), 0) AS TRADE_AMT
         FROM TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN TRD_PRODUCT_DEAL n
                             ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL, ''N'') = ''N''
                  INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
         WHERE m.PORTFOLIO_ID IN ${portfolioCode}
           AND n.TRADE_DATE = DATE''${valDate}''
           AND n.PS IN
               (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T46'', ''T47'', ''T48'', ''T52'', ''T22'', ''T23'',
                ''T38'', ''T39'', ''T56'')
         UNION ALL
         SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T46'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T47'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T48'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T19'', n.TRADE_AMT)), 0) AS TRADE_AMT
         FROM WFL_TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN WFL_TRD_PRODUCT_DEAL n
                             ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL, ''N'') = ''N''
                  INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE m.PORTFOLIO_ID IN ${portfolioCode}
           AND n.TRADE_DATE = DATE''${valDate}''
           AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T46'', ''T47'', ''T48'', ''T52'')
         UNION ALL
         SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''04'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T46'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T47'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T48'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T19'', n.TRADE_AMT)), 0) AS TRADE_AMT
         FROM TRD_PRODUCT_TEMP_DEAL_ADD m
                  INNER JOIN TRD_PRODUCT_TEMP_DEAL n
                             ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL, ''N'') = ''N''
                  INNER JOIN PROD t2 ON (t2.FINPROD_ID = n.FINPROD_ID OR t2.FINPROD_ID = n.TRADE_ID)
                  inner join FIN_PRODUCT FP on n.FINPROD_ID = FP.FINPROD_ID AND FP.TRADE_MARKET IN (''03'', ''04'')
         WHERE m.PORTFOLIO_ID IN ${portfolioCode}
           AND n.TRADE_DATE = DATE''${valDate}''
           AND n.PS IN (''04'', ''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T46'', ''T47'', ''T48'', ''T52'')
         UNION ALL
         SELECT SUM(tpd.SHARE_AMT / bb.SECURITY_QUANTITY * rvas.SMV) AS RVALUE
         FROM PTL_SEC_VALUTION tpd
                  --自主管理的计划类资产
                  INNER JOIN FIN_PRODUCT_TYPE FTP on tpd.FINPROD_ID = FTP.FINPROD_ID and FTP.TYPE_10 = ''N004''
                  INNER JOIN RSK_VALTBL_ASSET_SUB rvas
                             ON tpd.FINPROD_ID = ''F17_'' || rvas.PTL_ID AND rvas.FINPROD_ID IS NOT NULL AND
                                rvas.SMV IS NOT NULL
                  INNER JOIN ASSET_WW bb
                             on rvas.PTL_ID = bb.PTL_ID and bb.SUB_CODE = ''601'' AND bb.VAL_DATE = rvas.VAL_DATE
                  INNER JOIN PROD N ON rvas.FINPROD_ID = N.FINPROD_ID
         WHERE tpd.PORTFOLIO_ID IN ${portfolioCode}
           and tpd.CDATE = date''${valDate}'' - 1
     )';

----事后(日终)
V_CONTENT_05 CLOB := 'WITH FIN_OPEN_PLAN_AST AS (
    /**申购开放(这里三个union是为了取的顺序赎回优先,无赎回则取申购)**/
    SELECT CASH_ID,
           FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''01''
      AND NOT EXISTS(
            SELECT 1 FROM FIN_OPEN_PLAN fop2 WHERE fop2.OPEN_TYPE = ''02'' AND fop.CASH_ID = fop2.CASH_ID
        )
        /**赎回开放**/
    UNION ALL
    SELECT CASH_ID,
           FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''02''
        /**申赎同频**/
    UNION ALL
    SELECT CASH_ID,
           fop.FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''03''
)
   , FIN_CASH_PLAN_COST AS (
    /**还本计划**/
    SELECT CASH_ID,
           FINPROD_ID,
           PAY_DATE
    FROM FIN_CASH_PLAN fop
    WHERE fop.FINPROD_TYPE2 IN (''F01'', ''F18'')
      AND EXISTS(
            SELECT 1
            FROM FIN_CASH_INFO fop2
            WHERE fop2.CASH_TYPE_1 = ''01'' AND fop2.CASH_TYPE_2 = ''C01'' AND fop.CASH_ID = fop2.CASH_ID
        )
)
   , FIN_CASH_PLAN_RATE AS (
    /**付息计划**/
    SELECT CASH_ID,
           FINPROD_ID,
           PAY_DATE
    FROM FIN_CASH_PLAN fop
    WHERE fop.FINPROD_TYPE2 IN (''F01'', ''F18'')
      AND EXISTS(
            SELECT 1
            FROM FIN_CASH_INFO fop2
            WHERE fop2.CASH_TYPE_1 = ''02'' AND fop2.CASH_TYPE_2 = ''C02'' AND fop.CASH_ID = fop2.CASH_ID
        )
)
   , PROD AS (
    -- 资产到期7个交易日以内(含)
    SELECT FINPROD_ID
    FROM (
             SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN
             FROM (
                      SELECT m.FINPROD_ID,
                             m.VDATE,
                             m.MDATE,
                             MH.CDATE,
                             ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO
                      FROM (
                               -- 存款类资产,项目类资产,债券资产支持证券(到期)
                               SELECT FPT.FINPROD_ID, FPT.VDATE, FPT.MDATE, NVL(FPT.CALENDAR_ID, ''01'') CALENDAR_ID
                               FROM FIN_PRODUCT FPT
                                        INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
                               WHERE T1.TYPE_8 IN (''F05'', ''F06'')
                                  OR (T1.TYPE_8 IN (''F01'', ''F17'') AND T1.TYPE_9 = ''B13'')
                                  --可赎回日在7个交易日以内计划类资产(高频开放)
                               UNION ALL
                               select n.FINPROD_ID, M.VDATE, MDATE, NVL(M.CALENDAR_ID, ''01'') CALENDAR_ID
                               from FIN_PRODUCT m
                                        INNER JOIN FIN_PRODUCT_TYPE FPT
                                                   ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 IN (''F03'', ''F04'', ''F09'')
                                        INNER JOIN FIN_PRODUCT_ADD FPA
                                                   ON m.FINPROD_ID = FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
                                        INNER JOIN FIN_CASH_INFO n
                                                   ON n.FINPROD_ID = m.FINPROD_ID AND n.CASH_TYPE_1 = ''01'' AND
                                                      n.CASH_TYPE_2 = ''C01''
                                        INNER JOIN FIN_OPEN_RULE r ON r.CASH_ID = n.CASH_ID AND r.OPEN_MODE = ''02''
                                    --可赎回日在7个交易日以内计划类资产(非高频开放),赎回确定日刚好是平台日也统计
                               UNION ALL
                               SELECT FINPROD_ID, VDATE, OPEN_VDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.OPEN_VDATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                             CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.OPEN_VDATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 IN (''F03'', ''F04'', ''F09'')
                                                 INNER JOIN FIN_PRODUCT_ADD FPA
                                                            ON m.FINPROD_ID = FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
                                                 INNER JOIN FIN_CASH_INFO n
                                                            ON n.FINPROD_ID = m.FINPROD_ID AND n.CASH_TYPE_1 = ''01'' AND
                                                               n.CASH_TYPE_2 = ''C01''
                                                 INNER JOIN FIN_OPEN_PLAN_AST y ON y.CASH_ID = n.CASH_ID
                                        WHERE y.OPEN_VDATE >= date''${valDate}''
                                        ORDER BY y.OPEN_VDATE ASC
                                    )
                               WHERE rowno = 1
                               UNION ALL
                               -- 逆回购(到期)
                               SELECT FTP.FINPROD_ID, FTP.VDATE, FTP.MDATE, NVL(FTP.CALENDAR_ID, ''01'') CALENDAR_ID
                               FROM FIN_TRADE_PRODUCT FTP
                                        INNER JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                               WHERE FTP.MDATE IS NOT NULL
                                 AND T1.TYPE_8 IN (''F07'', ''F10'')
                                 AND TYPE_7 = ''04''
                                 AND FTP.MDATE >= DATE ''${valDate}'' - 1
                               UNION ALL
                               -- 拆出(到期)
                               SELECT FTP.FINPROD_ID, FTP.VDATE, FTP.MDATE, NVL(FTP.CALENDAR_ID, ''01'') CALENDAR_ID
                               FROM FIN_TRADE_PRODUCT FTP
                                        INNER JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                               WHERE FTP.MDATE IS NOT NULL
                                 AND T1.TYPE_8 = ''F08''
                                 AND TYPE_7 = ''02''
                                 AND FTP.MDATE >= DATE ''${valDate}'' - 1
                           ) m
                               INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND
                                                            MH.CDATE >= m.MDATE - 15 AND MH.IS_HOLIDAY = ''0''
                  )
             WHERE cdate >= vdate
               and vdate < = DATE ''${valDate}''
               and CDATE >= DATE ''${valDate}''
         )
    GROUP BY FINPROD_ID
    HAVING count(1) <= 8
    UNION
    --资产还本第7个交易日以内(含)
    SELECT FINPROD_ID
    FROM (
             SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN
             FROM (
                      SELECT m.FINPROD_ID,
                             m.VDATE,
                             m.MDATE,
                             MH.CDATE,
                             ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO
                      FROM (
                               -- 债券资产支持证券(还本),平台日当天还本也统计
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F01'' AND TYPE_9 = ''B13''
                                                 INNER JOIN FIN_CASH_PLAN_COST y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                                     --项目类(还本)
                               UNION ALL
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F06''
                                                 INNER JOIN FIN_CASH_PLAN_COST y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                           ) m
                               INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND
                                                            MH.CDATE >= m.MDATE - 15 AND MH.IS_HOLIDAY = ''0''
                  )
             WHERE cdate >= vdate
               and vdate < = DATE ''${valDate}''
               and CDATE >= DATE ''${valDate}''
         )
    GROUP BY FINPROD_ID
    HAVING count(1) <= 8
    UNION
    --资产付息第7个交易日以内(含)
    SELECT FINPROD_ID
    FROM (
             SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN
             FROM (
                      SELECT m.FINPROD_ID,
                             m.VDATE,
                             m.MDATE,
                             MH.CDATE,
                             ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO
                      FROM (
                               -- 债券资产支持证券(付息),平台日当天付息也统计
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F01'' AND TYPE_9 = ''B13''
                                                 INNER JOIN FIN_CASH_PLAN_RATE y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                                     --项目类(付息)
                               UNION ALL
                               SELECT FINPROD_ID, VDATE, PAY_DATE MDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.PAY_DATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                           CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE FPT
                                                            ON m.FINPROD_ID = FPT.FINPROD_ID and FPT.TYPE_8 = ''F06''
                                                 INNER JOIN FIN_CASH_PLAN_RATE y ON y.FINPROD_ID = m.FINPROD_ID
                                        WHERE y.PAY_DATE >= date''${valDate}''
                                        ORDER BY y.PAY_DATE ASC
                                    )
                               WHERE rowno = 1
                           ) m
                               INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND
                                                            MH.CDATE >= m.MDATE - 15 AND MH.IS_HOLIDAY = ''0''
                  )
             WHERE cdate >= vdate
               and vdate < = DATE ''${valDate}''
               and CDATE >= DATE ''${valDate}''
         )
    GROUP BY FINPROD_ID
    HAVING count(1) <= 8
    UNION
    --7日可变现公募基金
    SELECT FPT.FINPROD_ID
    FROM FIN_PRODUCT FPT
             INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FPT.FINPROD_ID AND FPA.IS_SEVEN_DAY_CASHABLE = ''Y''
             INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
    UNION
    -- 计划类资产(每日申赎开放)
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FP.FINPROD_ID
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F03'', ''F04'', ''F09'')
      AND FPA.IS_OPEN_EVERYDAY = ''Y''
    UNION
    -- 存款类无条件可提前支取
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
             INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F05''
      AND FPA.IS_INADVANCE = ''03''
    UNION
    -- 债券
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
         -- 排除流动性受限,资产支持证券
    WHERE FPT.TYPE_8 IN (''F01'', ''F17'')
      AND FPT.TYPE_9 <> ''B13''
    UNION
    -- 信用保护凭证
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 = ''F37''
      -- 委外逆回购
    UNION
    SELECT M.FINPROD_ID
    FROM PTL_SEC_PIERCE_VALUTION M
    WHERE M.CDATE = date''${valDate}'' - 1
      AND substr(m.FINPROD_ID, 0, 4) IN (''F03_'', ''F05_'')
    UNION
    --上市交易股票股票类型】字段为''普通股''股票行情信息界面中，【交易状态】字段显示为''交易''
    SELECT FP.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
             INNER JOIN REP_ASSET_EQUITY RAE ON RAE.REP_FINPROD_ID = FP.FINPROD_ID AND REP_SHARES_TYPE = ''01''
             INNER JOIN (
        SELECT DISTINCT m1.SHARES_ID
        FROM mst_share_price m1
        WHERE TRADE_STATUS = ''1''
          AND NOT EXISTS(
                SELECT 1
                FROM mst_share_price m2
                WHERE m1.SHARES_ID = m2.SHARES_ID
                  AND m1.DEAL_DATE < m2.DEAL_DATE
                  AND m2.DEAL_DATE <= DATE ''${valDate}''
            )
    ) MSP ON FP.FINPROD_MARKET_ID = MSP.SHARES_ID
         -- 排除流动性受限
    WHERE FPT.TYPE_8 = ''F11''
)
   , ASSET_WW AS (
---查询委外组合估值表
    SELECT PTL_ID, SUB_CODE, SMV, SECURITY_QUANTITY, VAL_DATE
    FROM (SELECT PTL_ID,
                 SUB_CODE,
                 SMV,
                 SECURITY_QUANTITY,
                 VAL_DATE,
                 rank() OVER (PARTITION BY PTL_ID ORDER BY VAL_DATE desc) AS rowno
          FROM RSK_VALTBL_SUM_ASSET_SUB
          WHERE SECURITY_QUANTITY IS NOT NULL
            AND VAL_DATE <= date''${valDate}''
         )
    WHERE ROWNO = 1
)
SELECT SUM(TRADE_AMT) RVALUE
FROM (
         SELECT NVL(SUM(NVL(M.DSC_COST_AMT, 0) + NVL(M.TDY_INTINCEXP, 0) + NVL(M.TDY_FLOAT_INGPL, 0) +
                        NVL(M.DELAY_PAY_AMT, 0) + NVL(M.TDY_LOSS_AMT, 0)), 0) TRADE_AMT
         FROM PTL_SEC_VALUTION M
                  INNER JOIN PROD N ON M.FINPROD_ID = N.FINPROD_ID
             --排除自主管理的计划类资产
                  INNER JOIN FIN_PRODUCT_TYPE FPT ON m.FINPROD_ID = FPT.FINPROD_ID and
                                                     ((FPT.TYPE_8 = ''F04'' AND FPT.TYPE_10 <> ''N004'') OR FPT.TYPE_8 <> ''F04'')
         WHERE M.CDATE = date''${valDate}''
           AND M.PORTFOLIO_ID IN ${portfolioCode}
         UNION ALL
         SELECT SUM(tpd.SHARE_AMT / bb.SECURITY_QUANTITY * rvas.SMV) AS RVALUE
         FROM PTL_SEC_VALUTION tpd
                  --自主管理的计划类资产
                  INNER JOIN FIN_PRODUCT_TYPE FTP on tpd.FINPROD_ID = FTP.FINPROD_ID and FTP.TYPE_10 = ''N004''
                  INNER JOIN RSK_VALTBL_ASSET_SUB rvas
                             ON tpd.FINPROD_ID = ''F17_'' || rvas.PTL_ID AND rvas.FINPROD_ID IS NOT NULL AND
                                rvas.SMV IS NOT NULL
                  INNER JOIN ASSET_WW bb
                             on rvas.PTL_ID = bb.PTL_ID and bb.SUB_CODE = ''601'' AND bb.VAL_DATE = rvas.VAL_DATE
                  INNER JOIN PROD N ON rvas.FINPROD_ID = N.FINPROD_ID
         WHERE tpd.PORTFOLIO_ID IN ${portfolioCode}
           and tpd.CDATE = date''${valDate}''
     )';
BEGIN
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'QGGZRKBXZCKBXJZFCT' and PARA_GRANULARITY in('01','02','03');

    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'QGGZRKBXZCKBXJZFCT' and PARA_GRANULARITY ='04';

    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'QGGZRKBXZCKBXJZFCT' and PARA_GRANULARITY = '05';
END;
/

----7个工作日可变现资产的可变现价值-买入预占（不算指标）
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_QGGZRKBXZCKBXJZFCT_01';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES  ('EVENT_ID_QGGZRKBXZCKBXJZFCT_01','7个工作日可变现资产的可变现价值-买入预占（不算指标）','N','','system','',sysdate,'system',sysdate);
         

DELETE FROM RSK_EVENT_ITEM_RULE WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_01';

DELETE FROM RSK_EVENT_PARA_REL WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_01';

----7个工作日可变现资产的可变现价值-卖出预占（不算指标）
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_QGGZRKBXZCKBXJZFCT_02';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES  ('EVENT_ID_QGGZRKBXZCKBXJZFCT_02','7个工作日可变现资产的可变现价值-卖出预占（不算指标）','N','','system','',sysdate,'system',sysdate);
         

DELETE FROM RSK_EVENT_ITEM_RULE WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_02';

DELETE FROM RSK_EVENT_PARA_REL WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_02';

----7个工作日可变现资产的可变现价值-买入实占（不算指标）
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_QGGZRKBXZCKBXJZFCT_03';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES  ('EVENT_ID_QGGZRKBXZCKBXJZFCT_03','7个工作日可变现资产的可变现价值-买入实占（不算指标）','N','','system','',sysdate,'system',sysdate);
         

DELETE FROM RSK_EVENT_ITEM_RULE WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_03';

DELETE FROM RSK_EVENT_PARA_REL WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_03';

----7个工作日可变现资产的可变现价值-卖出实占（不算指标）
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_QGGZRKBXZCKBXJZFCT_04';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES  ('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','7个工作日可变现资产的可变现价值-卖出实占（不算指标）','N','','system','',sysdate,'dzw',sysdate);
         

DELETE FROM RSK_EVENT_ITEM_RULE WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_04';
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','0c1f743191704820b7645effd886a9f8','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','type9','0c1f743191704820b7645effd886a9f8','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','0c1f743191704820b7645effd886a9f8','','T07,T08,T09,T38,T39','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','0c1f743191704820b7645effd886a9f8','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','0c1f743191704820b7645effd886a9f8','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','astLastPayDate','0c1f743191704820b7645effd886a9f8','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','217809c2a9b644c292e80d93d6022929','','F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','217809c2a9b644c292e80d93d6022929','','T07,T08,T09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','217809c2a9b644c292e80d93d6022929','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','217809c2a9b644c292e80d93d6022929','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','astLastPayDate','217809c2a9b644c292e80d93d6022929','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','3974d71e0fd048dfb13ab1051bd4bf8e','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','type9','3974d71e0fd048dfb13ab1051bd4bf8e','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','3974d71e0fd048dfb13ab1051bd4bf8e','','T07,T08,T09,T38,T39','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','3974d71e0fd048dfb13ab1051bd4bf8e','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','3974d71e0fd048dfb13ab1051bd4bf8e','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','astLastRebackDate','3974d71e0fd048dfb13ab1051bd4bf8e','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','3b3714f76fa64cd58dcc532510afe831','','F03,F04,F09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','3b3714f76fa64cd58dcc532510afe831','','T07,T08,T09,T11,T12,T13,T38,T39','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isOpenEveryDay','3b3714f76fa64cd58dcc532510afe831','','Y','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','3b3714f76fa64cd58dcc532510afe831','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','3b3714f76fa64cd58dcc532510afe831','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','3bf1a49992924d4486e9f6e01195dad1','','F37','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','3bf1a49992924d4486e9f6e01195dad1','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','3bf1a49992924d4486e9f6e01195dad1','','T07','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','53d52b9268a849bbbe9a8eca47e3861c','','F11','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','stockTradeStatus','53d52b9268a849bbbe9a8eca47e3861c','','1','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','stockShareType','53d52b9268a849bbbe9a8eca47e3861c','','01','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','53d52b9268a849bbbe9a8eca47e3861c','','T07,T11,T12,T13','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','53d52b9268a849bbbe9a8eca47e3861c','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','53d52b9268a849bbbe9a8eca47e3861c','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','828d847f53994c458c75e4d603002bce','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','828d847f53994c458c75e4d603002bce','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','828d847f53994c458c75e4d603002bce','','F05,F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','mdate','828d847f53994c458c75e4d603002bce','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','828d847f53994c458c75e4d603002bce','','T07,T08,T09,T38,T39','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','9d0612a51653492bb717402807f31559','','F03,F04,F09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','9d0612a51653492bb717402807f31559','','T07,T08,T09,T11,T12,T13,T38,T39','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isOpenEveryDay','9d0612a51653492bb717402807f31559','','N','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','9d0612a51653492bb717402807f31559','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','9d0612a51653492bb717402807f31559','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','astLastRedempDate','9d0612a51653492bb717402807f31559','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','9f0021b0565f49c094049db352cbb7b1','','F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','9f0021b0565f49c094049db352cbb7b1','','T07,T08,T09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','9f0021b0565f49c094049db352cbb7b1','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','9f0021b0565f49c094049db352cbb7b1','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','astLastRebackDate','9f0021b0565f49c094049db352cbb7b1','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','b163b5292b2b4fcaa76eb0d45c87174b','','F02','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','b163b5292b2b4fcaa76eb0d45c87174b','','T07,T11,T12,T13','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isSevenDayCashable','b163b5292b2b4fcaa76eb0d45c87174b','','Y','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','b163b5292b2b4fcaa76eb0d45c87174b','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','b163b5292b2b4fcaa76eb0d45c87174b','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','d6811305615543e38d910056f701da58','','F08','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','d6811305615543e38d910056f701da58','','T07,T54,T56','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','mdate','d6811305615543e38d910056f701da58','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','d6811305615543e38d910056f701da58','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType2','d6811305615543e38d910056f701da58','','02','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','d73f880e7d564f2bbcd115f5a46db714','','F07,F10','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','d73f880e7d564f2bbcd115f5a46db714','','T07,T54,T56,T55','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','mdate','d73f880e7d564f2bbcd115f5a46db714','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','d73f880e7d564f2bbcd115f5a46db714','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType2','d73f880e7d564f2bbcd115f5a46db714','','04','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','dcccc772bf9640948a9131793fed0c23','','F05','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','dcccc772bf9640948a9131793fed0c23','','T07,T08,T09,T38,T39','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isInAdvance','dcccc772bf9640948a9131793fed0c23','','03','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','dcccc772bf9640948a9131793fed0c23','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','dcccc772bf9640948a9131793fed0c23','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','e911687f012a4faf86fa8823c1aa8de5','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','e911687f012a4faf86fa8823c1aa8de5','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','e911687f012a4faf86fa8823c1aa8de5','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','e911687f012a4faf86fa8823c1aa8de5','','T07,T08,T09,T38,T39','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','type9','e911687f012a4faf86fa8823c1aa8de5','','B13','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','assetType','eec889bfeb5d483ba532b180b4262087','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','eventType','eec889bfeb5d483ba532b180b4262087','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','isFlowLimited','eec889bfeb5d483ba532b180b4262087','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','mdate','eec889bfeb5d483ba532b180b4262087','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','ps','eec889bfeb5d483ba532b180b4262087','','T07,T08,T09,T38,T39','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_04','type9','eec889bfeb5d483ba532b180b4262087','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);

DELETE FROM RSK_EVENT_PARA_REL WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_04';
INSERT INTO RSK_EVENT_PARA_REL (PARA_CODE, EVENT_CODE, PARA_GRANULARITY, REL_TYPE, REL_ITEM, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CUSTOM_VAL, FORMULA_VAL) VALUES('QGGZRKBXZCKBXJZFCT','EVENT_ID_QGGZRKBXZCKBXJZFCT_04','01','-','amount','','system','',SYSDATE,'system',SYSDATE,'','');
INSERT INTO RSK_EVENT_PARA_REL (PARA_CODE, EVENT_CODE, PARA_GRANULARITY, REL_TYPE, REL_ITEM, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CUSTOM_VAL, FORMULA_VAL) VALUES('QGGZRKBXZCKBXJZFCT','EVENT_ID_QGGZRKBXZCKBXJZFCT_04','02','-','amount','','system','',SYSDATE,'system',SYSDATE,'','');
INSERT INTO RSK_EVENT_PARA_REL (PARA_CODE, EVENT_CODE, PARA_GRANULARITY, REL_TYPE, REL_ITEM, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CUSTOM_VAL, FORMULA_VAL) VALUES('QGGZRKBXZCKBXJZFCT','EVENT_ID_QGGZRKBXZCKBXJZFCT_04','03','-','amount','','system','',SYSDATE,'system',SYSDATE,'','');

----7个工作日可变现资产的可变现价值-买入预占
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_QGGZRKBXZCKBXJZFCT_05';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES  ('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','7个工作日可变现资产的可变现价值-买入预占','Y','','system','',sysdate,'dzw',sysdate);
         

DELETE FROM RSK_EVENT_ITEM_RULE WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_05';
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','1b9ff40115964a90abc8f5dfef9c2fe3','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','type9','1b9ff40115964a90abc8f5dfef9c2fe3','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','1b9ff40115964a90abc8f5dfef9c2fe3','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','mdate','1b9ff40115964a90abc8f5dfef9c2fe3','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','1b9ff40115964a90abc8f5dfef9c2fe3','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','1b9ff40115964a90abc8f5dfef9c2fe3','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','23056974324e40fca6558868e7a193c9','','F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','23056974324e40fca6558868e7a193c9','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','23056974324e40fca6558868e7a193c9','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','23056974324e40fca6558868e7a193c9','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','astLastRebackDate','23056974324e40fca6558868e7a193c9','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','30c086c9c3ca4783aeb3e27f34e184bb','','F05','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','30c086c9c3ca4783aeb3e27f34e184bb','','T01','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isInAdvance','30c086c9c3ca4783aeb3e27f34e184bb','','03','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','30c086c9c3ca4783aeb3e27f34e184bb','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','30c086c9c3ca4783aeb3e27f34e184bb','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','59ddbf04a0fc48a1823c3a30252e0277','','F02','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','59ddbf04a0fc48a1823c3a30252e0277','','T01,T04,T52','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isSevenDayCashable','59ddbf04a0fc48a1823c3a30252e0277','','Y','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','59ddbf04a0fc48a1823c3a30252e0277','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','59ddbf04a0fc48a1823c3a30252e0277','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','69941fdd08fe4d88bd59afbf7be3cc0f','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','type9','69941fdd08fe4d88bd59afbf7be3cc0f','','B13','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','69941fdd08fe4d88bd59afbf7be3cc0f','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','69941fdd08fe4d88bd59afbf7be3cc0f','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','69941fdd08fe4d88bd59afbf7be3cc0f','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','7e2fc67f86534a8eabb7ec352a699188','','F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','7e2fc67f86534a8eabb7ec352a699188','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','7e2fc67f86534a8eabb7ec352a699188','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','7e2fc67f86534a8eabb7ec352a699188','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','astLastPayDate','7e2fc67f86534a8eabb7ec352a699188','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','ac208f5f5a284cfaac63b2bac81bbc62','','F08','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','ac208f5f5a284cfaac63b2bac81bbc62','','T01','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','mdate','ac208f5f5a284cfaac63b2bac81bbc62','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','ac208f5f5a284cfaac63b2bac81bbc62','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType2','ac208f5f5a284cfaac63b2bac81bbc62','','02','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','be7799a452ed40f8b83707a6f61af1c0','','F37','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','be7799a452ed40f8b83707a6f61af1c0','','T01,T04','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','be7799a452ed40f8b83707a6f61af1c0','','0','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','c1dd4bea1ff6474cb618f00d0718b12c','','F11','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','stockTradeStatus','c1dd4bea1ff6474cb618f00d0718b12c','','1','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','stockShareType','c1dd4bea1ff6474cb618f00d0718b12c','','01','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','c1dd4bea1ff6474cb618f00d0718b12c','','T01,T46,T47,T48,T04','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','c1dd4bea1ff6474cb618f00d0718b12c','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','c1dd4bea1ff6474cb618f00d0718b12c','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','c6bb112e6c964ba3840ea2a6c9a96f34','','F05,F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','c6bb112e6c964ba3840ea2a6c9a96f34','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','mdate','c6bb112e6c964ba3840ea2a6c9a96f34','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','c6bb112e6c964ba3840ea2a6c9a96f34','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','c6bb112e6c964ba3840ea2a6c9a96f34','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','type9','e24e9431f6c6416fb59f2a5c2db271e6','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','e24e9431f6c6416fb59f2a5c2db271e6','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','e24e9431f6c6416fb59f2a5c2db271e6','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','e24e9431f6c6416fb59f2a5c2db271e6','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','astLastRebackDate','e24e9431f6c6416fb59f2a5c2db271e6','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','e24e9431f6c6416fb59f2a5c2db271e6','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','e6226a7647ac416c9d7df24af73513a7','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','astLastPayDate','e6226a7647ac416c9d7df24af73513a7','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','e6226a7647ac416c9d7df24af73513a7','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','e6226a7647ac416c9d7df24af73513a7','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','e6226a7647ac416c9d7df24af73513a7','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','type9','e6226a7647ac416c9d7df24af73513a7','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','ebdfea816cf44ce88cc03b24f9e84961','','F03,F04,F09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','ebdfea816cf44ce88cc03b24f9e84961','','T01,T04,T52','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isOpenEveryDay','ebdfea816cf44ce88cc03b24f9e84961','','Y','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','ebdfea816cf44ce88cc03b24f9e84961','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','ebdfea816cf44ce88cc03b24f9e84961','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','f3510ad690e04b12a646e2d67c189cde','','F03,F04,F09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','astLastRedempDate','f3510ad690e04b12a646e2d67c189cde','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','f3510ad690e04b12a646e2d67c189cde','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isFlowLimited','f3510ad690e04b12a646e2d67c189cde','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','isOpenEveryDay','f3510ad690e04b12a646e2d67c189cde','','N','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','f3510ad690e04b12a646e2d67c189cde','','T01,T04,T52','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType','fd85601195f14bd7b061c7549680cb04','','F07,F10','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','assetType2','fd85601195f14bd7b061c7549680cb04','','04','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','eventType','fd85601195f14bd7b061c7549680cb04','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','mdate','fd85601195f14bd7b061c7549680cb04','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_05','ps','fd85601195f14bd7b061c7549680cb04','','T01','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);

DELETE FROM RSK_EVENT_PARA_REL WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_05';
INSERT INTO RSK_EVENT_PARA_REL (PARA_CODE, EVENT_CODE, PARA_GRANULARITY, REL_TYPE, REL_ITEM, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CUSTOM_VAL, FORMULA_VAL) VALUES('QGGZRKBXZCKBXJZFCT','EVENT_ID_QGGZRKBXZCKBXJZFCT_05','02','+','amount','','system','',SYSDATE,'system',SYSDATE,'','');

----7个工作日可变现资产的可变现价值-卖出预占
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_QGGZRKBXZCKBXJZFCT_06';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES  ('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','7个工作日可变现资产的可变现价值-卖出预占','Y','','system','',sysdate,'dzw',sysdate);
         

DELETE FROM RSK_EVENT_ITEM_RULE WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_06';
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','139a0dbf330645ddbb22d939ab7ea989','','F05,F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','139a0dbf330645ddbb22d939ab7ea989','','T02','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','mdate','139a0dbf330645ddbb22d939ab7ea989','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','139a0dbf330645ddbb22d939ab7ea989','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','139a0dbf330645ddbb22d939ab7ea989','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','2c4ce28502dd4836813d35e215875d24','','F11','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','stockTradeStatus','2c4ce28502dd4836813d35e215875d24','','1','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','stockShareType','2c4ce28502dd4836813d35e215875d24','','01','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','2c4ce28502dd4836813d35e215875d24','','T02','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','2c4ce28502dd4836813d35e215875d24','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','2c4ce28502dd4836813d35e215875d24','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','37453302a05b4e36b6a0795f9aa66abe','','F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','37453302a05b4e36b6a0795f9aa66abe','','T02','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','37453302a05b4e36b6a0795f9aa66abe','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','37453302a05b4e36b6a0795f9aa66abe','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','astLastPayDate','37453302a05b4e36b6a0795f9aa66abe','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','93a34728fc0145cc937ceb441eeb35e0','','F05','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','93a34728fc0145cc937ceb441eeb35e0','','T02','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isInAdvance','93a34728fc0145cc937ceb441eeb35e0','','03','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','93a34728fc0145cc937ceb441eeb35e0','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','93a34728fc0145cc937ceb441eeb35e0','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','a552e85288c04d14a787d61a42fae7d2','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','type9','a552e85288c04d14a787d61a42fae7d2','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','a552e85288c04d14a787d61a42fae7d2','','T02,T03','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','a552e85288c04d14a787d61a42fae7d2','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','a552e85288c04d14a787d61a42fae7d2','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','astLastPayDate','a552e85288c04d14a787d61a42fae7d2','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','ac552af006da448bb3395521a879e699','','F03,F04,F09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','ac552af006da448bb3395521a879e699','','T02,T06,T19','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isOpenEveryDay','ac552af006da448bb3395521a879e699','','Y','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','ac552af006da448bb3395521a879e699','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','ac552af006da448bb3395521a879e699','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','b0e5af9d78e0484d837341459ef869c3','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','b0e5af9d78e0484d837341459ef869c3','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','type9','b0e5af9d78e0484d837341459ef869c3','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','b0e5af9d78e0484d837341459ef869c3','','T02,T03','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','mdate','b0e5af9d78e0484d837341459ef869c3','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','b0e5af9d78e0484d837341459ef869c3','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','b12916ea996e4a8a889157ecb5384d35','','F02','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','b12916ea996e4a8a889157ecb5384d35','','T02,T06,T19','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isSevenDayCashable','b12916ea996e4a8a889157ecb5384d35','','Y','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','b12916ea996e4a8a889157ecb5384d35','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','b12916ea996e4a8a889157ecb5384d35','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','b402c85d7b0040dd8c9c77633512df9c','','F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','b402c85d7b0040dd8c9c77633512df9c','','T02','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','b402c85d7b0040dd8c9c77633512df9c','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','b402c85d7b0040dd8c9c77633512df9c','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','astLastRebackDate','b402c85d7b0040dd8c9c77633512df9c','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','d39b50764e664e88ac621477907249eb','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','type9','d39b50764e664e88ac621477907249eb','','B13','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','d39b50764e664e88ac621477907249eb','','T02,T03','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','d39b50764e664e88ac621477907249eb','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','d39b50764e664e88ac621477907249eb','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','d3ae09dcb386469baeb23f787a70ab90','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','type9','d3ae09dcb386469baeb23f787a70ab90','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','d3ae09dcb386469baeb23f787a70ab90','','T02,T03','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','d3ae09dcb386469baeb23f787a70ab90','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','d3ae09dcb386469baeb23f787a70ab90','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','astLastRebackDate','d3ae09dcb386469baeb23f787a70ab90','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','dbd0cba02824449b95557b0a65c3029c','','F37','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','dbd0cba02824449b95557b0a65c3029c','','T02','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','dbd0cba02824449b95557b0a65c3029c','','0','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isOpenEveryDay','f2c9efb472da429bbcc5d3e42b1565c5','','N','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','ps','f2c9efb472da429bbcc5d3e42b1565c5','','T02,T06,T19','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','assetType','f2c9efb472da429bbcc5d3e42b1565c5','','F03,F04,F09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','astLastRedempDate','f2c9efb472da429bbcc5d3e42b1565c5','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','eventType','f2c9efb472da429bbcc5d3e42b1565c5','','0','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_06','isFlowLimited','f2c9efb472da429bbcc5d3e42b1565c5','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);

DELETE FROM RSK_EVENT_PARA_REL WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_06';
INSERT INTO RSK_EVENT_PARA_REL (PARA_CODE, EVENT_CODE, PARA_GRANULARITY, REL_TYPE, REL_ITEM, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CUSTOM_VAL, FORMULA_VAL) VALUES('QGGZRKBXZCKBXJZFCT','EVENT_ID_QGGZRKBXZCKBXJZFCT_06','03','-','amount','','system','',SYSDATE,'system',SYSDATE,'','');

----7个工作日可变现资产的可变现价值-买入实占
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_QGGZRKBXZCKBXJZFCT_07';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES  ('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','7个工作日可变现资产的可变现价值-买入实占','Y','','system','',sysdate,'dzw',sysdate);
         

DELETE FROM RSK_EVENT_ITEM_RULE WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_07';
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','019f07bb4a334abbb9768d701378ba84','','F37','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','019f07bb4a334abbb9768d701378ba84','','T01,T04','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','019f07bb4a334abbb9768d701378ba84','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','526a8c8f24334ee88c28b1a2943654ef','','F05,F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','526a8c8f24334ee88c28b1a2943654ef','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','mdate','526a8c8f24334ee88c28b1a2943654ef','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','526a8c8f24334ee88c28b1a2943654ef','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','526a8c8f24334ee88c28b1a2943654ef','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','61b01067b3f94e2a9296d69ae66922d0','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','type9','61b01067b3f94e2a9296d69ae66922d0','','B13','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','61b01067b3f94e2a9296d69ae66922d0','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','61b01067b3f94e2a9296d69ae66922d0','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','61b01067b3f94e2a9296d69ae66922d0','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','76f596823cf043f78a05c99106ffb441','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','type9','76f596823cf043f78a05c99106ffb441','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','76f596823cf043f78a05c99106ffb441','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','mdate','76f596823cf043f78a05c99106ffb441','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','76f596823cf043f78a05c99106ffb441','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','76f596823cf043f78a05c99106ffb441','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','7774cde1496f4640af25e9aa8f7e8b73','','T01','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','mdate','7774cde1496f4640af25e9aa8f7e8b73','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','7774cde1496f4640af25e9aa8f7e8b73','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType2','7774cde1496f4640af25e9aa8f7e8b73','','04','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','7774cde1496f4640af25e9aa8f7e8b73','','F07,F10','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','879800f628f142ac90eb6d0ed604c842','','F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','879800f628f142ac90eb6d0ed604c842','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','879800f628f142ac90eb6d0ed604c842','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','879800f628f142ac90eb6d0ed604c842','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','astLastPayDate','879800f628f142ac90eb6d0ed604c842','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','88c1b64afb2f49a09a02b4d4512e1b6e','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','type9','88c1b64afb2f49a09a02b4d4512e1b6e','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','88c1b64afb2f49a09a02b4d4512e1b6e','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','88c1b64afb2f49a09a02b4d4512e1b6e','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','88c1b64afb2f49a09a02b4d4512e1b6e','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','astLastPayDate','88c1b64afb2f49a09a02b4d4512e1b6e','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','8beebe079b6d493eabbd3a2aa3a1f1c6','','F11','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','stockTradeStatus','8beebe079b6d493eabbd3a2aa3a1f1c6','','1','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','stockShareType','8beebe079b6d493eabbd3a2aa3a1f1c6','','01','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','8beebe079b6d493eabbd3a2aa3a1f1c6','','T01,T46,T47,T48,T04','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','8beebe079b6d493eabbd3a2aa3a1f1c6','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','8beebe079b6d493eabbd3a2aa3a1f1c6','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','96d1f32a6bd14c688282f8f73f697c25','','F08','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType2','96d1f32a6bd14c688282f8f73f697c25','','02','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','96d1f32a6bd14c688282f8f73f697c25','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','mdate','96d1f32a6bd14c688282f8f73f697c25','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','96d1f32a6bd14c688282f8f73f697c25','','T01','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','9d91a222a09e4b73b10e202406019c54','','F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','9d91a222a09e4b73b10e202406019c54','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','9d91a222a09e4b73b10e202406019c54','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','9d91a222a09e4b73b10e202406019c54','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','astLastRebackDate','9d91a222a09e4b73b10e202406019c54','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','a24c73acf3bb41b9bc755f39bd2e8213','','F03,F04,F09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','a24c73acf3bb41b9bc755f39bd2e8213','','T01,T04,T52','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isOpenEveryDay','a24c73acf3bb41b9bc755f39bd2e8213','','Y','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','a24c73acf3bb41b9bc755f39bd2e8213','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','a24c73acf3bb41b9bc755f39bd2e8213','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','a5c01a420dec4eb2bcbfa75b0c0b1d1d','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','type9','a5c01a420dec4eb2bcbfa75b0c0b1d1d','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','a5c01a420dec4eb2bcbfa75b0c0b1d1d','','T01','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','a5c01a420dec4eb2bcbfa75b0c0b1d1d','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','a5c01a420dec4eb2bcbfa75b0c0b1d1d','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','astLastRebackDate','a5c01a420dec4eb2bcbfa75b0c0b1d1d','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isOpenEveryDay','b72c59722bc8494f8019a72d15d33e11','','N','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','b72c59722bc8494f8019a72d15d33e11','','T01,T04,T52','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','b72c59722bc8494f8019a72d15d33e11','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','b72c59722bc8494f8019a72d15d33e11','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','astLastRedempDate','b72c59722bc8494f8019a72d15d33e11','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','b72c59722bc8494f8019a72d15d33e11','','F03,F04,F09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','c889fd764e39443192b38a2cccc5240d','','F05','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','c889fd764e39443192b38a2cccc5240d','','T01','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isInAdvance','c889fd764e39443192b38a2cccc5240d','','03','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','c889fd764e39443192b38a2cccc5240d','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','c889fd764e39443192b38a2cccc5240d','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','assetType','d95e3c8bd5f844de86791219b4281d15','','F02','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','eventType','d95e3c8bd5f844de86791219b4281d15','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isFlowLimited','d95e3c8bd5f844de86791219b4281d15','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','ps','d95e3c8bd5f844de86791219b4281d15','','T01,T04,T52','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_07','isSevenDayCashable','d95e3c8bd5f844de86791219b4281d15','','Y','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);

DELETE FROM RSK_EVENT_PARA_REL WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_07';
INSERT INTO RSK_EVENT_PARA_REL (PARA_CODE, EVENT_CODE, PARA_GRANULARITY, REL_TYPE, REL_ITEM, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CUSTOM_VAL, FORMULA_VAL) VALUES('QGGZRKBXZCKBXJZFCT','EVENT_ID_QGGZRKBXZCKBXJZFCT_07','01','+','amount','','system','',SYSDATE,'system',SYSDATE,'','');
INSERT INTO RSK_EVENT_PARA_REL (PARA_CODE, EVENT_CODE, PARA_GRANULARITY, REL_TYPE, REL_ITEM, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CUSTOM_VAL, FORMULA_VAL) VALUES('QGGZRKBXZCKBXJZFCT','EVENT_ID_QGGZRKBXZCKBXJZFCT_07','02','+','amount','','system','',SYSDATE,'system',SYSDATE,'','');
INSERT INTO RSK_EVENT_PARA_REL (PARA_CODE, EVENT_CODE, PARA_GRANULARITY, REL_TYPE, REL_ITEM, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CUSTOM_VAL, FORMULA_VAL) VALUES('QGGZRKBXZCKBXJZFCT','EVENT_ID_QGGZRKBXZCKBXJZFCT_07','03','+','amount','','system','',SYSDATE,'system',SYSDATE,'','');

----7个工作日可变现资产的可变现价值-卖出实占
DELETE FROM RSK_EVENT WHERE EVENT_CODE ='EVENT_ID_QGGZRKBXZCKBXJZFCT_08';
INSERT INTO RSK_EVENT (EVENT_CODE, EVENT_NAME, IS_CALC_INDICATOR, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES  ('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','7个工作日可变现资产的可变现价值-卖出实占','Y','','system','',sysdate,'dzw',sysdate);
         

DELETE FROM RSK_EVENT_ITEM_RULE WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_08';
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','291ab2b590544aecb6099cc93ad9a8f5','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','type9','291ab2b590544aecb6099cc93ad9a8f5','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','291ab2b590544aecb6099cc93ad9a8f5','','T02,T03','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','291ab2b590544aecb6099cc93ad9a8f5','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','291ab2b590544aecb6099cc93ad9a8f5','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','astLastPayDate','291ab2b590544aecb6099cc93ad9a8f5','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','331a6da41f504f9f9b385245937826d4','','F03,F04,F09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','331a6da41f504f9f9b385245937826d4','','T02,T06,T19','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isOpenEveryDay','331a6da41f504f9f9b385245937826d4','','Y','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','331a6da41f504f9f9b385245937826d4','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','331a6da41f504f9f9b385245937826d4','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','51720d5365d04911ab598f2b10e0e0ad','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','type9','51720d5365d04911ab598f2b10e0e0ad','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','51720d5365d04911ab598f2b10e0e0ad','','T02,T03','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','51720d5365d04911ab598f2b10e0e0ad','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','51720d5365d04911ab598f2b10e0e0ad','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','astLastRebackDate','51720d5365d04911ab598f2b10e0e0ad','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','795fccef0e5746b1868d2a954f49e14d','','F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','795fccef0e5746b1868d2a954f49e14d','','T02','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','795fccef0e5746b1868d2a954f49e14d','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','795fccef0e5746b1868d2a954f49e14d','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','astLastPayDate','795fccef0e5746b1868d2a954f49e14d','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','7b879432c919427a8cf75272520039c1','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','7b879432c919427a8cf75272520039c1','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','7b879432c919427a8cf75272520039c1','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','7b879432c919427a8cf75272520039c1','','T02,T03','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','type9','7b879432c919427a8cf75272520039c1','','B13','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','8466f138b3f34330a64822d1464c925a','','F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','astLastRebackDate','8466f138b3f34330a64822d1464c925a','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','8466f138b3f34330a64822d1464c925a','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','8466f138b3f34330a64822d1464c925a','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','8466f138b3f34330a64822d1464c925a','','T02','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','910d794f57824481bb95958046b9101e','','F05,F06','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','910d794f57824481bb95958046b9101e','','T02','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','mdate','910d794f57824481bb95958046b9101e','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','910d794f57824481bb95958046b9101e','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','910d794f57824481bb95958046b9101e','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','b86792c85149483fb28aed65e01c76d2','','F05','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','b86792c85149483fb28aed65e01c76d2','','T02','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isInAdvance','b86792c85149483fb28aed65e01c76d2','','03','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','b86792c85149483fb28aed65e01c76d2','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','b86792c85149483fb28aed65e01c76d2','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','be3bf7cf9d6644cea177755f5697bde3','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','be3bf7cf9d6644cea177755f5697bde3','','F11','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','be3bf7cf9d6644cea177755f5697bde3','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','be3bf7cf9d6644cea177755f5697bde3','','T02','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','stockShareType','be3bf7cf9d6644cea177755f5697bde3','','01','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','stockTradeStatus','be3bf7cf9d6644cea177755f5697bde3','','1','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','c6b8b647bcdb44bc9fed822a71da8c44','','F02','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','c6b8b647bcdb44bc9fed822a71da8c44','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','c6b8b647bcdb44bc9fed822a71da8c44','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isSevenDayCashable','c6b8b647bcdb44bc9fed822a71da8c44','','Y','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','c6b8b647bcdb44bc9fed822a71da8c44','','T02,T06,T19','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','cf6229be674345c0bbad9428b75f3923','','F37','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','cf6229be674345c0bbad9428b75f3923','','T02','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','cf6229be674345c0bbad9428b75f3923','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','ff7299bd27874e4bbc0a43134296bf6f','','F03,F04,F09','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','ff7299bd27874e4bbc0a43134296bf6f','','T02,T06,T19','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isOpenEveryDay','ff7299bd27874e4bbc0a43134296bf6f','','N','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','ff7299bd27874e4bbc0a43134296bf6f','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','ff7299bd27874e4bbc0a43134296bf6f','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','astLastRedempDate','ff7299bd27874e4bbc0a43134296bf6f','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','assetType','ff821346e90541b9821e07e734516596','','F01,F17','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','eventType','ff821346e90541b9821e07e734516596','','1,2','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','isFlowLimited','ff821346e90541b9821e07e734516596','','Y','<>','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','mdate','ff821346e90541b9821e07e734516596','','${sysDate}','<=','7','+','01','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','ps','ff821346e90541b9821e07e734516596','','T02,T03','IN','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);
INSERT INTO RSK_EVENT_ITEM_RULE (EVENT_CODE, ITEM_CODE, GROUP_ID, ITEM_ANO_NAME, ITEM_VALUE, MATCH_TYPE, OFFSET_VAL, OFFSET_TYPE, OFFSET_UNIT, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES('EVENT_ID_QGGZRKBXZCKBXJZFCT_08','type9','ff821346e90541b9821e07e734516596','','B13','=','','','','dzw','YHZGB',SYSDATE,'dzw_ld',SYSDATE);

DELETE FROM RSK_EVENT_PARA_REL WHERE EVENT_CODE = 'EVENT_ID_QGGZRKBXZCKBXJZFCT_08';
INSERT INTO RSK_EVENT_PARA_REL (PARA_CODE, EVENT_CODE, PARA_GRANULARITY, REL_TYPE, REL_ITEM, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CUSTOM_VAL, FORMULA_VAL) VALUES('QGGZRKBXZCKBXJZFCT','EVENT_ID_QGGZRKBXZCKBXJZFCT_08','01','-','amount','','system','',SYSDATE,'system',SYSDATE,'','');
INSERT INTO RSK_EVENT_PARA_REL (PARA_CODE, EVENT_CODE, PARA_GRANULARITY, REL_TYPE, REL_ITEM, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CUSTOM_VAL, FORMULA_VAL) VALUES('QGGZRKBXZCKBXJZFCT','EVENT_ID_QGGZRKBXZCKBXJZFCT_08','02','-','amount','','system','',SYSDATE,'system',SYSDATE,'','');
INSERT INTO RSK_EVENT_PARA_REL (PARA_CODE, EVENT_CODE, PARA_GRANULARITY, REL_TYPE, REL_ITEM, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, CUSTOM_VAL, FORMULA_VAL) VALUES('QGGZRKBXZCKBXJZFCT','EVENT_ID_QGGZRKBXZCKBXJZFCT_08','03','-','amount','','system','',SYSDATE,'system',SYSDATE,'','');

