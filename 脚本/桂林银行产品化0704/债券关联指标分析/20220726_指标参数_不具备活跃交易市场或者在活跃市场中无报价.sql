/**风险指标参数:不具备活跃交易市场或者在活跃市场中无报价**/

DECLARE
----事后(日间)
V_CONTENT_04 CLOB := 'WITH PROD AS (
    --不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT_TYPE FPT
    WHERE EXISTS (
        SELECT 1 FROM RSK_SPECIFIC_ASSET_TYPE RSAT WHERE RSAT.IS_LIQUIDITY2 = ''Y'' AND (
        (REGEXP_INSTR(RSAT.TYPE_1||''_''||RSAT.TYPE_2||''_''||RSAT.TYPE_3||''_''||RSAT.TYPE_4 ,
            FPT.TYPE_9||''_''||FPT.TYPE_10||''_''||FPT.TYPE_11||''_''||FPT.TYPE_12) >= 1 AND RSAT.FINPROD_ID IS NULL)
        OR FPT.FINPROD_ID = RSAT.FINPROD_ID)
    )
)
SELECT SUM(TRADE_AMT) RVALUE FROM (
    SELECT
        NVL(SUM(NVL(M.DSC_COST_AMT,0)+NVL(M.TDY_INTINCEXP,0)+NVL(M.TDY_FLOAT_INGPL,0)+NVL(M.DELAY_PAY_AMT,0)+NVL(M.TDY_LOSS_AMT,0)),0) TRADE_AMT
    FROM PTL_SEC_VALUTION M
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
	  - nvl(sum(decode(n.PS, ''T100'', n.TRADE_AMT)), 0)
	  - nvl(sum(decode(n.PS, ''T13'', n.TRADE_AMT)), 0) AS TRADE_AMT
    FROM TRD_PRODUCT_DEAL_ADD m
    INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
    INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
    WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T46'', ''T47'', ''T48'', ''T52'', ''T22'', ''T23'', ''T39'', ''T38'', ''T100'', ''T13'')
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
	  - nvl(sum(decode(n.PS, ''T100'', n.TRADE_AMT)), 0)
	  - nvl(sum(decode(n.PS, ''T13'', n.TRADE_AMT)), 0) AS TRADE_AMT
    FROM WFL_TRD_PRODUCT_DEAL_ADD m
    INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
    INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T46'', ''T47'', ''T48'', ''T52'', ''T100'', ''T13'')
  UNION ALL
	select
        nvl(sum(decode(n.PS,''T02'',n.TRADE_AMT)),0) as  TRADE_AMT
	from WFL_TRD_PRODUCT_DEAL_ADD m
	inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS = ''01''
	INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
	INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
	where n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}'' AND n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
	AND n.PS = ''T02''
	AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
)';
BEGIN
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'BJBHYJYSCBNJLGYJZZCGZ' and PARA_GRANULARITY ='04';
END;
/
