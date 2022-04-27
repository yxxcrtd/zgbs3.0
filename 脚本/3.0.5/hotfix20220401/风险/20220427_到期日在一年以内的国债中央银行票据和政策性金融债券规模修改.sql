delete from RSK_ISUP_INDICATOR_PARA_DTL   where PARA_CODE = 'XJGZZYYHPJZCXJRZQGM' and PARA_GRANULARITY='04';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES ('XJGZZYYHPJZCXJRZQGM', '04', 'SQL', 'select sum(TRADE_AMT) as tradeAmt from
  (
    select NVL(SUM(NVL(m.MARKET_VALUE,0)+nvl(m.TDY_INTINCEXP, 0)),0) as  TRADE_AMT from PTL_SEC_PIERCE_VALUTION m
    INNER JOIN FIN_PRODUCT n on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 =''F01'' and n.MDATE>=sysdate and n.MDATE<add_months(sysdate, 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01''))  and t3.TYPE_8 =''F01''
    where  m.PORTFOLIO_ID in ${portfolioCode}  and m.CDATE=date''${valDate}'' -1
    union ALL
    select
    nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T07'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T08'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T09'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT  from TRD_PRODUCT_DEAL_ADD m
    inner join TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 =''F01'' and t2.MDATE>=sysdate and t2.MDATE<add_months(sysdate, 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01'')) and t3.TYPE_8 =''F01''
    where n.TRADE_STATUS = ''01'' and n.IS_CANCEL = ''N'' AND 
	m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T39'',''T11'',''T52'') 
	', 'portfolioCode', 'N', 'wxy', 'YHZGB', TIMESTAMP '2022-04-27 14:10:30', 'wxy', TIMESTAMP '2022-04-27 14:10:30');
  update RSK_ISUP_INDICATOR_PARA_DTL set content = content||'
  union ALL
    select
    nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T07'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T08'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T09'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT  from TRD_PRODUCT_TEMP_DEAL_ADD m
    inner join TRD_PRODUCT_TEMP_DEAL n on m.TRADE_ID=n.TRADE_ID
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 =''F01'' and t2.MDATE>=sysdate and t2.MDATE<add_months(sysdate, 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01'')) and t3.TYPE_8 =''F01''
    where (n.TRADE_STATUS = ''01'' OR n.TRADE_STATUS IS NULL) and n.IS_CANCEL = ''N'' AND 
  	m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T39'',''T11'',''T52'')
   union ALL
    select
     nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)   as  TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 =''F01'' and t2.MDATE>=sysdate and t2.MDATE<add_months(sysdate, 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01'')) and t3.TYPE_8 =''F01''
    where n.TRADE_STATUS = ''01'' and n.IS_CANCEL = ''N'' AND 
	m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T39'',''T11'',''T52'') 
    '  where PARA_CODE = 'XJGZZYYHPJZCXJRZQGM' and PARA_GRANULARITY='04';
    update RSK_ISUP_INDICATOR_PARA_DTL set content = content||'
	union ALL
    select
    nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)  as  TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 =''F01'' and t2.MDATE>=sysdate and t2.MDATE<add_months(sysdate, 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01'')) and t3.TYPE_8 =''F01''
    where  n.TRADE_STATUS = ''01'' and n.IS_CANCEL = ''N'' AND 
	n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''  AND n.PS = ''T02''
    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
  ) '  where PARA_CODE = 'XJGZZYYHPJZCXJRZQGM' and PARA_GRANULARITY='04';
