/**风险指标参数:流动性受限资产的持仓市值**/
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
,PROD AS (
    -- 资产到期向前第10个交易日
    SELECT FINPROD_ID FROM (
        SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN FROM (
            SELECT m.FINPROD_ID,  m.VDATE,m.MDATE, MH.CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO FROM (
                -- 逆回购
                SELECT FTP.FINPROD_ID,FTP.VDATE,FTP.MDATE,NVL(FTP.CALENDAR_ID,''01'') CALENDAR_ID FROM FIN_TRADE_PRODUCT FTP
                WHERE FTP.MDATE IS NOT NULL AND FTP.FINPROD_TYPE2 IN (''F03'',''F05'') AND FTP.MDATE >= DATE ''${valDate}'' - 1
				UNION ALL
				-- 银行定期存款
				SELECT FPT.FINPROD_ID,FPT.VDATE,MDATE,NVL(FPT.CALENDAR_ID,''01'') CALENDAR_ID
				FROM FIN_PRODUCT FPT
				INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
				INNER JOIN FIN_PRODUCT_ADD FPA ON FPT.FINPROD_ID = FPA.FINPROD_ID
				-- 这里是有条件可提前支取
				WHERE (FPA.IS_INADVANCE IS NULL OR FPA.IS_INADVANCE IN (''01'',''02''))
				AND T1.TYPE_8 = ''F05''
			    --距可赎回日在10个交易日以上计划类资产(非高频开放)
			    UNION ALL
				  SELECT FINPROD_ID,VDATE,OPEN_VDATE,CALENDAR_ID FROM (
					  SELECT m.FINPROD_ID,M.VDATE,y.OPEN_VDATE,NVL(M.CALENDAR_ID,''01'') CALENDAR_ID ,rank() over(PARTITION BY m.FINPROD_ID ORDER BY y.OPEN_VDATE asc) AS rowno
					  from FIN_PRODUCT m
					  INNER JOIN FIN_PRODUCT_ADD FPA ON m.FINPROD_ID=FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
					  INNER JOIN  FIN_CASH_INFO n ON n.FINPROD_ID=m.FINPROD_ID  AND n.CASH_TYPE_1=''01'' AND n.CASH_TYPE_2=''C01'' AND M.FINPROD_TYPE2 =''F17''
					  INNER JOIN  FIN_OPEN_PLAN_AST y ON y.CASH_ID=n.CASH_ID
					  WHERE y.OPEN_VDATE>=date''${valDate}''  ORDER BY y.OPEN_VDATE ASC
				  ) WHERE  rowno=1
            ) m
            INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND MH.CDATE>=m.MDATE-25  AND MH.IS_HOLIDAY = ''0''
        ) WHERE cdate>=vdate  and vdate< =DATE ''${valDate}'' and CDATE > DATE ''${valDate}''
    ) GROUP BY FINPROD_ID HAVING count(1)>10
    UNION
    -- 停牌股票(股票行情界面交易状态显示停牌)
    SELECT FP.FINPROD_ID FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    INNER JOIN (
      SELECT DISTINCT m1.SHARES_ID FROM mst_share_price m1 WHERE TRADE_STATUS =''0'' AND NOT EXISTS (
      SELECT 1 FROM mst_share_price m2 WHERE m1.SHARES_ID =m2.SHARES_ID AND m1.DEAL_DATE <m2.DEAL_DATE AND m2.DEAL_DATE <=DATE ''${valDate}''
       )
    )  MSP ON FP.FINPROD_MARKET_ID =MSP.SHARES_ID
    WHERE FPT.TYPE_8 =''F11''
    UNION
    -- 资产支持证券
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F01'',''F17'')  AND FPT.TYPE_6 = ''B13''
  UNION
   -- 流动性受限债券,非金融企业债务融资工具或其它
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT_TYPE FPT
    WHERE EXISTS (
        SELECT 1 FROM RSK_SPECIFIC_ASSET_TYPE RSAT WHERE RSAT.IS_FLOW_LIMITED = ''Y'' AND (
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
	  - nvl(sum(decode(n.PS, ''T100'', n.TRADE_AMT)), 0)
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
	  - nvl(sum(decode(n.PS, ''T56'', n.TRADE_AMT)), 0) AS TRADE_AMT
    FROM TRD_PRODUCT_DEAL_ADD m
    INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID and n.TRADE_STATUS = ''01'' and NVL(n.IS_CANCEL,''N'') =''N''
    INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
    WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T46'', ''T47'', ''T48'', ''T52'', ''T22'', ''T23'', ''T39'', ''T38'',''T54'',''T100'')
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
  	    - nvl(sum(decode(n.PS, ''T100'', n.TRADE_AMT)), 0)
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
  	    - nvl(sum(decode(n.PS, ''T56'', n.TRADE_AMT)), 0) AS TRADE_AMT
      FROM TRD_PRODUCT_TEMP_DEAL_ADD m
      INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID and (n.TRADE_STATUS = ''01'' OR n.TRADE_STATUS IS NULL) and NVL(n.IS_CANCEL,''N'') =''N''
      INNER JOIN PROD t2 ON t2.FINPROD_ID = n.TRADE_ID
      WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
      AND n.PS IN (''04'',''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T46'', ''T47'', ''T48'', ''T52'', ''T22'', ''T23'', ''T39'', ''T38'',''T54'',''T100'')
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
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T46'', ''T47'', ''T48'', ''T52'',''T100'')
)';
BEGIN
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'LDXSXZCCCSZFCT' and PARA_GRANULARITY ='04';
END;
/
