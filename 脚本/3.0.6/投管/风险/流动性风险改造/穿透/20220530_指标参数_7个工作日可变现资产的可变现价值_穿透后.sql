/**风险指标参数:7个工作日可变现资产的可变现价值(穿透后)**/
DECLARE
----事后(日间)
V_CONTENT_04 CLOB := 'WITH FIN_OPEN_PLAN_AST AS  (
  /**申购开放(这里三个union是为了取的顺序赎回优先,无赎回则取申购)**/
   SELECT
   CASH_ID,FINPROD_ID , OPEN_VDATE
   FROM
    FIN_OPEN_PLAN fop
   WHERE
    fop.FINPROD_ID LIKE ''F17_%'' AND fop.OPEN_TYPE = ''01'' AND NOT EXISTS (
    SELECT 1 FROM FIN_OPEN_PLAN fop2 WHERE fop2.OPEN_TYPE = ''02'' AND fop.CASH_ID =fop2.CASH_ID
    )
  /**赎回开放**/
  UNION ALL
   SELECT
   CASH_ID, FINPROD_ID ,OPEN_VDATE
   FROM
    FIN_OPEN_PLAN fop
   WHERE
    fop.FINPROD_ID LIKE ''F17_%'' AND fop.OPEN_TYPE = ''02''
  /**申赎同频**/
  UNION ALL
  SELECT
   CASH_ID,fop.FINPROD_ID , OPEN_VDATE
  FROM
   FIN_OPEN_PLAN fop
  WHERE
   fop.FINPROD_ID LIKE ''F17_%'' AND fop.OPEN_TYPE = ''03''
)
,FIN_CASH_PLAN_COST AS  (
  /**还本计划**/
   SELECT
   CASH_ID,FINPROD_ID ,PAY_DATE
   FROM
    FIN_CASH_PLAN fop
   WHERE
    fop.FINPROD_TYPE2 IN (''F01'',''F18'') AND EXISTS (
    SELECT 1 FROM FIN_CASH_INFO fop2 WHERE fop2.CASH_TYPE_1 = ''01'' AND fop2.CASH_TYPE_2 =''C01'' AND fop.CASH_ID =fop2.CASH_ID
    )
)
,FIN_CASH_PLAN_RATE AS  (
  /**付息计划**/
   SELECT
   CASH_ID,FINPROD_ID , PAY_DATE
   FROM
    FIN_CASH_PLAN fop
   WHERE
    fop.FINPROD_TYPE2 IN (''F01'',''F18'') AND EXISTS (
    SELECT 1 FROM FIN_CASH_INFO fop2 WHERE fop2.CASH_TYPE_1 = ''02'' AND fop2.CASH_TYPE_2 =''C02'' AND fop.CASH_ID =fop2.CASH_ID
    )
)
,PROD AS (
    -- 资产到期7个交易日以内(含)
    SELECT FINPROD_ID FROM (
        SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN FROM (
            SELECT m.FINPROD_ID,  m.VDATE,m.MDATE, MH.CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO FROM (
                -- 存款类资产,项目类资产,债券资产支持证券(到期)
                SELECT FPT.FINPROD_ID,FPT.VDATE,FPT.MDATE,NVL(FPT.CALENDAR_ID,''01'') CALENDAR_ID FROM FIN_PRODUCT FPT
                INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
                WHERE T1.TYPE_8 IN (''F05'',''F06'') OR (T1.TYPE_8 IN (''F01'',''F17'') AND T1.TYPE_9=''B13'')
        --可赎回日在7个交易日以内计划类资产(高频开放)
        UNION ALL
        select n.FINPROD_ID ,M.VDATE,MDATE,NVL(M.CALENDAR_ID,''01'') CALENDAR_ID
          from FIN_PRODUCT m
          INNER JOIN FIN_PRODUCT_TYPE FPT ON m.FINPROD_ID=FPT.FINPROD_ID and FPT.TYPE_8 IN (''F03'',''F04'',''F09'')
          INNER JOIN FIN_PRODUCT_ADD FPA ON m.FINPROD_ID=FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
          INNER JOIN FIN_CASH_INFO n ON n.FINPROD_ID = m.FINPROD_ID AND n.CASH_TYPE_1 = ''01'' AND n.CASH_TYPE_2 = ''C01''
          INNER JOIN FIN_OPEN_RULE r ON r.CASH_ID = n.CASH_ID AND r.OPEN_MODE = ''02''

        --可赎回日在7个交易日以内计划类资产(非高频开放),赎回确定日刚好是平台日也统计
        UNION ALL
          SELECT FINPROD_ID,VDATE,OPEN_VDATE,CALENDAR_ID FROM (
            SELECT m.FINPROD_ID,M.VDATE,y.OPEN_VDATE,NVL(M.CALENDAR_ID,''01'') CALENDAR_ID ,rank() over(PARTITION BY m.FINPROD_ID ORDER BY y.OPEN_VDATE asc) AS rowno
            from FIN_PRODUCT m
            INNER JOIN  FIN_PRODUCT_TYPE FPT ON m.FINPROD_ID=FPT.FINPROD_ID and FPT.TYPE_8 IN (''F03'',''F04'',''F09'')
            INNER JOIN FIN_PRODUCT_ADD FPA ON m.FINPROD_ID=FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
            INNER JOIN  FIN_CASH_INFO n ON n.FINPROD_ID=m.FINPROD_ID  AND n.CASH_TYPE_1=''01'' AND n.CASH_TYPE_2=''C01''
            INNER JOIN  FIN_OPEN_PLAN_AST y ON y.CASH_ID=n.CASH_ID
            WHERE y.OPEN_VDATE>=date''${valDate}''   ORDER BY y.OPEN_VDATE ASC
          ) WHERE  rowno=1
                UNION ALL
                -- 逆回购(到期)
                SELECT FTP.FINPROD_ID,FTP.VDATE,FTP.MDATE,NVL(FTP.CALENDAR_ID,''01'') CALENDAR_ID FROM FIN_TRADE_PRODUCT FTP
                INNER JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                WHERE FTP.MDATE IS NOT NULL AND T1.TYPE_8 IN (''F07'',''F10'') AND TYPE_7 = ''04'' AND FTP.MDATE >= DATE ''${valDate}'' - 1
                UNION ALL
                -- 拆出(到期)
                SELECT FTP.FINPROD_ID,FTP.VDATE,FTP.MDATE,NVL(FTP.CALENDAR_ID,''01'') CALENDAR_ID FROM FIN_TRADE_PRODUCT FTP
                INNER JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                WHERE FTP.MDATE IS NOT NULL AND T1.TYPE_8 =''F08'' AND TYPE_7 = ''02'' AND FTP.MDATE >= DATE ''${valDate}'' - 1
            ) m
            INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND MH.CDATE>=m.MDATE-15  AND MH.IS_HOLIDAY = ''0''
        ) WHERE cdate>=vdate  and vdate< =DATE ''${valDate}'' and CDATE >= DATE ''${valDate}''
    )  GROUP BY FINPROD_ID HAVING count(1)<=8
    UNION
      --资产还本第7个交易日以内(含)
    SELECT FINPROD_ID FROM (
        SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN FROM (
            SELECT m.FINPROD_ID,  m.VDATE,m.MDATE, MH.CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO FROM (
                -- 债券资产支持证券(还本),平台日当天还本也统计，所以取>=
                SELECT FINPROD_ID,VDATE,PAY_DATE MDATE,CALENDAR_ID FROM (
            SELECT m.FINPROD_ID,M.VDATE,y.PAY_DATE,NVL(M.CALENDAR_ID,''01'') CALENDAR_ID ,rank() over(PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
            from FIN_PRODUCT m
            INNER JOIN FIN_PRODUCT_TYPE FPT ON m.FINPROD_ID=FPT.FINPROD_ID and FPT.TYPE_8 =''F01'' AND TYPE_9 = ''B13''
            INNER JOIN  FIN_CASH_PLAN_COST y ON y.FINPROD_ID=m.FINPROD_ID
            WHERE y.PAY_DATE>=date''${valDate}''   ORDER BY y.PAY_DATE ASC
          ) WHERE  rowno=1
        --项目类(还本)
        UNION ALL
          SELECT FINPROD_ID,VDATE,PAY_DATE MDATE,CALENDAR_ID FROM (
            SELECT m.FINPROD_ID,M.VDATE,y.PAY_DATE,NVL(M.CALENDAR_ID,''01'') CALENDAR_ID ,rank() over(PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
            from FIN_PRODUCT m
            INNER JOIN FIN_PRODUCT_TYPE FPT ON m.FINPROD_ID=FPT.FINPROD_ID and FPT.TYPE_8 =''F06''
            INNER JOIN  FIN_CASH_PLAN_COST y ON y.FINPROD_ID=m.FINPROD_ID
            WHERE y.PAY_DATE>=date''${valDate}''   ORDER BY y.PAY_DATE ASC
          ) WHERE  rowno=1

            ) m
            INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND MH.CDATE>=m.MDATE-15  AND MH.IS_HOLIDAY = ''0''
        ) WHERE cdate>=vdate  and vdate< =DATE ''${valDate}'' and CDATE >= DATE ''${valDate}''
    )  GROUP BY FINPROD_ID HAVING count(1)<=8
    UNION
      --资产付息第7个交易日以内(含)
    SELECT FINPROD_ID FROM (
        SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN FROM (
            SELECT m.FINPROD_ID,  m.VDATE,m.MDATE, MH.CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO FROM (
                -- 债券资产支持证券(付息),平台日当天付息也统计，所以取>=
                SELECT FINPROD_ID,VDATE,PAY_DATE MDATE,CALENDAR_ID FROM (
            SELECT m.FINPROD_ID,M.VDATE,y.PAY_DATE,NVL(M.CALENDAR_ID,''01'') CALENDAR_ID ,rank() over(PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
            from FIN_PRODUCT m
            INNER JOIN FIN_PRODUCT_TYPE FPT ON m.FINPROD_ID=FPT.FINPROD_ID and FPT.TYPE_8 =''F01'' AND TYPE_9 = ''B13''
            INNER JOIN  FIN_CASH_PLAN_RATE y ON y.FINPROD_ID=m.FINPROD_ID
            WHERE y.PAY_DATE>=date''${valDate}''   ORDER BY y.PAY_DATE ASC
          ) WHERE  rowno=1
        --项目类(付息)
        UNION ALL
          SELECT FINPROD_ID,VDATE,PAY_DATE MDATE,CALENDAR_ID FROM (
            SELECT m.FINPROD_ID,M.VDATE,y.PAY_DATE,NVL(M.CALENDAR_ID,''01'') CALENDAR_ID ,rank() over(PARTITION BY m.FINPROD_ID ORDER BY y.PAY_DATE asc) AS rowno
            from FIN_PRODUCT m
            INNER JOIN FIN_PRODUCT_TYPE FPT ON m.FINPROD_ID=FPT.FINPROD_ID and FPT.TYPE_8 =''F06''
            INNER JOIN  FIN_CASH_PLAN_RATE y ON y.FINPROD_ID=m.FINPROD_ID
            WHERE y.PAY_DATE>=date''${valDate}''   ORDER BY y.PAY_DATE ASC
          ) WHERE  rowno=1

            ) m
            INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND MH.CDATE>=m.MDATE-15  AND MH.IS_HOLIDAY = ''0''
        ) WHERE cdate>=vdate  and vdate< =DATE ''${valDate}'' and CDATE >= DATE ''${valDate}''
    )  GROUP BY FINPROD_ID HAVING count(1)<=8
  UNION
  --7日可变现公募基金
  SELECT FPT.FINPROD_ID FROM FIN_PRODUCT FPT
  INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FPT.FINPROD_ID AND FPA.IS_SEVEN_DAY_CASHABLE=''Y''
  INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
  UNION
    -- 计划类资产(每日申赎开放)
    SELECT FP.FINPROD_ID FROM FIN_PRODUCT FP
  INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FP.FINPROD_ID
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F03'',''F04'',''F09'') AND FPA.IS_OPEN_EVERYDAY=''Y''
  UNION
    -- 存款类无条件可提前支取
    SELECT FP.FINPROD_ID FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    INNER JOIN FIN_PRODUCT_ADD FPA ON FPA.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 =''F05'' AND FPA.IS_INADVANCE =''03''
  UNION
    -- 债券
    SELECT FP.FINPROD_ID FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    -- 排除流动性受限,资产支持证券
    WHERE FPT.TYPE_8 IN (''F01'',''F17'') AND FPT.TYPE_9 <> ''B13''
  -- 委外逆回购
  UNION
  SELECT M.FINPROD_ID
  FROM PTL_SEC_PIERCE_VALUTION M
  WHERE M.CDATE = date''${valDate}'' - 1 AND substr(m.FINPROD_ID,0,4) IN (''F03_'',''F05_'')
    UNION
    --上市交易股票股票类型】字段为''普通股''股票行情信息界面中，【交易状态】字段显示为''交易''
    SELECT FP.FINPROD_ID FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    INNER JOIN REP_ASSET_EQUITY RAE ON RAE.REP_FINPROD_ID =FP.FINPROD_ID AND REP_SHARES_TYPE =''01''
    INNER JOIN (
      SELECT DISTINCT m1.SHARES_ID FROM mst_share_price m1 WHERE TRADE_STATUS =''1'' AND NOT EXISTS (
      SELECT 1 FROM mst_share_price m2 WHERE m1.SHARES_ID =m2.SHARES_ID AND m1.DEAL_DATE <m2.DEAL_DATE AND m2.DEAL_DATE <=DATE ''${valDate}''
       )
    )  MSP ON FP.FINPROD_MARKET_ID =MSP.SHARES_ID
    -- 排除流动性受限
    WHERE FPT.TYPE_8 =''F11''
)
SELECT SUM(TRADE_AMT) RVALUE FROM (
    SELECT
        NVL(SUM(NVL(M.MARKET_VALUE ,0)+nvl(m.TDY_INTINCEXP, 0)),0) TRADE_AMT
    FROM PTL_SEC_PIERCE_VALUTION M
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
    - nvl(sum(decode(n.PS, ''T56'', n.TRADE_AMT)), 0) AS TRADE_AMT
    FROM TRD_PRODUCT_DEAL_ADD m
    INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
    INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
    WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T46'', ''T47'', ''T48'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T56'')
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
      - nvl(sum(decode(n.PS, ''T19'', n.TRADE_AMT)), 0) AS TRADE_AMT
    FROM WFL_TRD_PRODUCT_DEAL_ADD m
    INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
    INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T46'', ''T47'', ''T48'', ''T52'')
    UNION ALL
    SELECT
        nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
      + nvl(sum(decode(n.PS, ''04'', n.TRADE_AMT)), 0)
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
    FROM TRD_PRODUCT_TEMP_DEAL_ADD m
    INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
    INNER JOIN PROD t2 ON (t2.FINPROD_ID = n.FINPROD_ID OR t2.FINPROD_ID = n.TRADE_ID)
    WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS IN (''04'',''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T46'', ''T47'', ''T48'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T56'')
)';
BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'QGGZRKBXZCKBXJZ' and PARA_GRANULARITY ='04';
END;
/
