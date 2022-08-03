/**风险指标参数:标准化债权类资产以及上市交易的股票持仓市值(穿透后)**/
----风险监督指标参数

DECLARE
----事后(日间)
V_CONTENT_04 CLOB := 'WITH PROD AS (
    SELECT FPT.FINPROD_ID
    FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    --债券,新发债券,公募基金中债券基金
    WHERE ((FPT.TYPE_8 =''F02'') OR (FPT.TYPE_8 IN (''F01'',''F17'')))
    UNION ALL
    --上市交易股票股票类型】字段为''普通股''股票行情信息界面中，【交易状态】字段显示为''交易''
    SELECT FP.FINPROD_ID FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    INNER JOIN REP_ASSET_EQUITY RAE ON RAE.REP_FINPROD_ID =FP.FINPROD_ID AND REP_SHARES_TYPE =''01''
    INNER JOIN (
      SELECT DISTINCT m1.SHARES_ID FROM mst_share_price m1 WHERE TRADE_STATUS =''1'' AND NOT EXISTS (
      SELECT 1 FROM mst_share_price m2 WHERE m1.SHARES_ID =m2.SHARES_ID AND m1.DEAL_DATE <m2.DEAL_DATE AND m2.DEAL_DATE <=DATE ''${valDate}''
       )
    )  MSP ON FP.FINPROD_MARKET_ID =MSP.SHARES_ID
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
    - nvl(sum(decode(n.PS, ''T100'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T19'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T07'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T08'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T09'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T11'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T22'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T23'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T38'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0) AS TRADE_AMT
    FROM TRD_PRODUCT_DEAL_ADD m
    INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
    INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
    WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T46'', ''T47'', ''T48'', ''T52'', ''T22'', ''T23'', ''T39'', ''T38'', ''T100'')
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
    - nvl(sum(decode(n.PS, ''T100'', n.TRADE_AMT)), 0)
      - nvl(sum(decode(n.PS, ''T19'', n.TRADE_AMT)), 0) AS TRADE_AMT
    FROM WFL_TRD_PRODUCT_DEAL_ADD m
    INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
    INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T46'', ''T47'', ''T48'', ''T52'', ''T100'')
	union ALL
	select
        nvl(sum(decode(n.PS,''T02'',n.TRADE_AMT)),0) as  TRADE_AMT
	from WFL_TRD_PRODUCT_DEAL_ADD m
	inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
	INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
	INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
	where n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}'' AND n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
	AND n.PS = ''T02''
	AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
)';
BEGIN
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'BZHZQLZCYJSSJYGPCCSZ' and PARA_GRANULARITY ='04';
END;
/
