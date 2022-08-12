--GLLCUAT-86 【监控值查询】现金持仓规模参数取值为0问题 start
DECLARE
----事后(日终)
V_CONTENT_05 CLOB := 'SELECT sum(RVALUE) FROM (
    SELECT nvl(sum(nvl(pspv.DSC_COST_AMT,0)+nvl(pspv.TDY_INTINCEXP,0)+nvl(pspv.TDY_FLOAT_INGPL,0)+nvl(pspv.DELAY_PAY_AMT,0)+nvl(pspv.TDY_LOSS_AMT,0)),0) as RVALUE from PTL_SEC_VALUTION pspv
    WHERE pspv.PORTFOLIO_ID in ${portfolioCode} AND pspv.FINPROD_ID in ( SELECT distinct ACCT_NO from PTL_CAPACCT_INFO where PORTFOLIO_ID in ${portfolioCode}  AND ACCT_TYPE <> ''A06'')
	and pspv.CDATE =date''${valDate}''
  UNION ALL
  select nvl(sum(AMOUNT),0) as  RVALUE from PTL_CASH_POSITION_FLOW
    where  PORTFOLIO_ID in ${portfolioCode} and HODING_TYPE=''01'' and CDATE >=date''${valDate}'' AND
    ACCT_ID in ( SELECT ACCT_NO from PTL_CAPACCT_INFO where PORTFOLIO_ID in ${portfolioCode}  AND ACCT_TYPE <> ''A06'')
  UNION ALL
  select nvl(sum(AMOUNT),0) as  RVALUE from PTL_CASH_POSITION_FLOW
    where PORTFOLIO_ID in ${portfolioCode} and HODING_TYPE=''03'' and CDATE >date''${valDate}'' AND
    ACCT_ID in ( SELECT ACCT_NO from PTL_CAPACCT_INFO where PORTFOLIO_ID in ${portfolioCode}  AND ACCT_TYPE <> ''A06'')
  )';
BEGIN
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'XJGM' and PARA_GRANULARITY = '05';
END;
/
--GLLCUAT-86 【监控值查询】现金持仓规模参数取值为0问题 end