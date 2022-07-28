/**风险指标参数:到期日在一年以内的国债、中央银行票据和政策性金融债券规模 添加一级债**/

DECLARE
----实时
V_CONTENT_01_02_03 CLOB := 'select y1.PORTFOLIO_ID as RKEY,sum(TRADE_AMT)  as RVALUE from
  (
    select m.PORTFOLIO_ID,nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as  TRADE_AMT from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT n on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 in (''F01'',''F32'') and n.MDATE>=date''${valDate}'' and n.MDATE<=add_months(date''${valDate}'', 12)
	/**TYPE_8 一级债也存了F01**/
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01''))  and t3.TYPE_8 =''F01''
    where  m.CDATE=date''${valDate}''-1  AND m.PORTFOLIO_ID!=m.FINPROD_ID group by m.PORTFOLIO_ID

    union all
     select t.PTL_BAG_ID as RKEY,
     ( select nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as  TRADE_AMT from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT n on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 in (''F01'',''F32'') and n.MDATE>=date''${valDate}'' and n.MDATE<=add_months(date''${valDate}'', 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01''))  and t3.TYPE_8 =''F01''
    where  m.CDATE=date''${valDate}''-1  AND m.PORTFOLIO_ID!=m.FINPROD_ID AND
    m.PORTFOLIO_ID in (   select t1.PORTFOLIO_ID from RSK_PORTFOLIO_PACKAGE_DTL t1  inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}'' where t1.PTL_BAG_ID=t.PTL_BAG_ID)
     )
     from RSK_PORTFOLIO_PACKAGE t

  ) y1 group by y1.PORTFOLIO_ID';

----事后(日间)
V_CONTENT_04 CLOB := 'select sum(TRADE_AMT) as tradeAmt from
  (
    select nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as  TRADE_AMT from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT n on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 IN (''F01'',''F32'') and n.MDATE>=sysdate and n.MDATE<=add_months(sysdate, 12)
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
	-nvl(sum(decode(n.PS,''T39'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT  from TRD_PRODUCT_DEAL_ADD m
    inner join TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 IN (''F01'',''F32'') and t2.MDATE>=sysdate and t2.MDATE<=add_months(sysdate, 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01'')) and t3.TYPE_8 =''F01''
    where n.TRADE_STATUS = ''01'' and n.IS_CANCEL = ''N'' AND
	m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T39'',''T11'',''T52'')
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
	-nvl(sum(decode(n.PS,''T39'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT  from TRD_PRODUCT_TEMP_DEAL_ADD m
    inner join TRD_PRODUCT_TEMP_DEAL n on m.TRADE_ID=n.TRADE_ID
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 IN (''F01'',''F32'') and t2.MDATE>=sysdate and t2.MDATE<=add_months(sysdate, 12)
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
	-nvl(sum(decode(n.PS,''T07'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T08'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T09'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
	-nvl(sum(decode(n.PS,''T39'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)   as  TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 IN (''F01'',''F32'') and t2.MDATE>=sysdate and t2.MDATE<=add_months(sysdate, 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01'')) and t3.TYPE_8 =''F01''
    where n.TRADE_STATUS = ''01'' and n.IS_CANCEL = ''N'' AND
	m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T39'',''T11'',''T52'')
	union ALL
    select
    nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)  as  TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 IN (''F01'',''F32'') and t2.MDATE>=sysdate and t2.MDATE<=add_months(sysdate, 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01'')) and t3.TYPE_8 =''F01''
    where  n.TRADE_STATUS = ''01'' and n.IS_CANCEL = ''N'' AND
	n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''  AND n.PS = ''T02''
    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
  )';
 
----事后(日终)
V_CONTENT_05 CLOB := 'select sum(TRADE_AMT) as RVALUE from
  (
    select nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as  TRADE_AMT from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT n on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 IN (''F01'',''F32'') and n.MDATE>=date''${valDate}'' and n.MDATE<=add_months(date''${valDate}'', 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01''))  and t3.TYPE_8 =''F01''
    where  m.PORTFOLIO_ID in ${portfolioCode}  and m.CDATE=date''${valDate}''
  )';
  
BEGIN
     UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'XJGZZYYHPJZCXJRZQGM' and PARA_GRANULARITY in('01','02','03');

    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'XJGZZYYHPJZCXJRZQGM' and PARA_GRANULARITY ='04';

    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'XJGZZYYHPJZCXJRZQGM' and PARA_GRANULARITY = '05';
END;
/