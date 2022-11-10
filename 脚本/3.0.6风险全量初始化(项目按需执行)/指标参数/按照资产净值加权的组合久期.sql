/**风险指标参数: 按照资产净值加权的组合久期 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='AZZCJZJQDZHJQ';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('AZZCJZJQDZHJQ','按照资产净值加权的组合久期', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='AZZCJZJQDZHJQ';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('AZZCJZJQDZHJQ','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('AZZCJZJQDZHJQ','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('AZZCJZJQDZHJQ','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('AZZCJZJQDZHJQ','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('AZZCJZJQDZHJQ','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'select 1 from dual';

----事后(日间)
V_CONTENT_04 CLOB := 'SELECT sum((TRADE_AMT)*(M_DURATION)) AS RVALUE FROM (
  select
  SUM(A1.TRADE_AMT) TRADE_AMT,
  CASE WHEN A1.FINPROD_TYPE =''F01'' THEN
  (
    select  CASE WHEN nvl(sum(B1.M_DURATION),0)=0 THEN (((A1.mdate)-(date''${valDate}''))/365) ELSE (nvl(sum(B1.M_DURATION),0)) END M_DURATION from  MST_BOND_VALUATION_INFO B1
      inner join
      (select to_char(max(VALUE_DATE),''yyyy-MM-dd'') as VALUE_DATE,SEC_ID from MST_BOND_VALUATION_INFO where VALUE_DATE<=date''${valDate}'' group by sec_id) B2
      on B1.SEC_ID=B2.SEC_ID and B1.VALUE_DATE=B2.VALUE_DATE WHERE B1.SEC_ID=A1.FINPROD_ID
  )
  WHEN (A1.FINPROD_TYPE =''F06'' OR A1.FINPROD_TYPE =''XJ'') THEN (1)/365
  WHEN (A1.FINPROD_TYPE =''F10'' AND A1.MDATE IS NULL) THEN (1)
  WHEN a1.FINPROD_ID =a1.PORTFOLIO_ID
  THEN (1)/365
    ELSE ((nvl((A1.mdate)-(date''${valDate}''),0))/365) END M_DURATION
  from
  (
  select NULL AS FINPROD_ID,NULL AS PORTFOLIO_ID,nvl(sum(AMOUNT),0) as  TRADE_AMT,''XJ'' AS FINPROD_TYPE,NULL AS MDATE   from PTL_CASH_POSITION
   where  PORTFOLIO_ID in ${portfolioCode} and HODING_TYPE=''03'' and CDATE =date''${valDate}''
    UNION ALL
    select
    t1.FINPROD_ID,t1.PORTFOLIO_ID,nvl(t1.DSC_COST_AMT,0)+nvl(t1.TDY_INTINCEXP,0)+nvl(t1.TDY_FLOAT_INGPL,0)+nvl(t1.DELAY_PAY_AMT,0)+nvl(t1.TDY_LOSS_AMT,0) as TRADE_AMT,t2.FINPROD_TYPE2,t2.MDATE
    from PTL_SEC_VALUTION t1
    inner join FIN_PRODUCT t2 on t1.FINPROD_ID = t2.FINPROD_ID
    where t1.PORTFOLIO_ID in ${portfolioCode} and  t1.CDATE = date''${valDate}''-1
     UNION ALL
     select
    t1.FINPROD_ID,t1.PORTFOLIO_ID, nvl(t1.DSC_COST_AMT,0)+nvl(t1.TDY_INTINCEXP,0)+nvl(t1.TDY_FLOAT_INGPL,0)+nvl(t1.DELAY_PAY_AMT,0)+nvl(t1.TDY_LOSS_AMT,0) as TRADE_AMT,t3.FINPROD_TYPE2,t3.MDATE
    from PTL_SEC_VALUTION t1
    inner join FIN_TRADE_PRODUCT t3  on t1.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH=0
    where t1.PORTFOLIO_ID in ${portfolioCode} and  t1.CDATE = date''${valDate}''-1
    and  t3.FINPROD_TYPE2 not in (''F02'', ''F04'', ''F08'', ''F25'')
      UNION ALL
    select
    n.FINPROD_ID ,t4.PORTFOLIO_ID,
     nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T07'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T08'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T09'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T22'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T23'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT,n.FINPROD_TYPE2,nvl(t2.MDATE,t3.MDATE) MDATE
      from TRD_PRODUCT_DEAL n
      left join FIN_PRODUCT t2  on n.FINPROD_ID = t2.FINPROD_ID
      left join FIN_TRADE_PRODUCT t3 on n.FINPROD_ID = t3.FINPROD_ID
      LEFT JOIN TRD_PRODUCT_DEAL_ADD t4 ON n.TRADE_ID=t4.TRADE_ID
      where t4.PORTFOLIO_ID in ${portfolioCode} and  n.TRADE_DATE = date''${valDate}''
      and ((t3.FINPROD_TYPE2 is null and t2.FINPROD_TYPE2 is not null) or (t3.FINPROD_TYPE2 not in (''F02'', ''F04'',''F08'', ''F25'') and t3.BRANCH=0))
      and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T11'',''T52'',''T22'',''T23'') group by t4.PORTFOLIO_ID,n.FINPROD_ID,t2.MDATE,t3.MDATE,n.FINPROD_TYPE2
    UNION ALL
    select
    n.FINPROD_ID ,t4.PORTFOLIO_ID,
    nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T07'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T08'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T09'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T22'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T23'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT,n.FINPROD_TYPE2,nvl(t2.MDATE,t3.MDATE) MDATE
      from TRD_PRODUCT_TEMP_DEAL n
      left join FIN_PRODUCT t2  on n.FINPROD_ID = t2.FINPROD_ID
      left join FIN_TRADE_PRODUCT t3 on n.FINPROD_ID = t3.FINPROD_ID
      LEFT JOIN TRD_PRODUCT_TEMP_DEAL_ADD t4 ON n.TRADE_ID=t4.TRADE_ID
      where t4.PORTFOLIO_ID in ${portfolioCode} and  n.TRADE_DATE = date''${valDate}''
      and ((t3.FINPROD_TYPE2 is null and t2.FINPROD_TYPE2 is not null) or (t3.FINPROD_TYPE2 not in (''F02'', ''F04'',''F08'', ''F25'') and t3.BRANCH=0))
      and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T11'',''T52'',''T22'',''T23'') group by t4.PORTFOLIO_ID,n.FINPROD_ID,t2.MDATE,t3.MDATE,n.FINPROD_TYPE2
    UNION ALL
    select  n.FINPROD_ID ,m.PORTFOLIO_ID,
     nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT,n.FINPROD_TYPE2,nvl(t2.MDATE,t3.MDATE) MDATE
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT T2 on T2.FINPROD_ID=n.FINPROD_ID
    left join FIN_TRADE_PRODUCT t3 on n.FINPROD_ID = t3.FINPROD_ID
      WHERE  m.PORTFOLIO_ID in ${portfolioCode}  AND n.TRADE_DATE=date''${valDate}''
      AND ((t3.FINPROD_TYPE2 is null and t2.FINPROD_TYPE2 is not null) or (t3.FINPROD_TYPE2 not in (''F02'', ''F04'',''F08'', ''F25'') AND t3.BRANCH=0 ))
      and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T52'') group by m.PORTFOLIO_ID,n.FINPROD_ID,t2.MDATE,t3.MDATE,n.FINPROD_TYPE2
    UNION ALL
    select  n.FINPROD_ID ,m.PORTFOLIO_ID,
     nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)  as  TRADE_AMT,n.FINPROD_TYPE2,nvl(t2.MDATE,t3.MDATE) MDATE
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT T2 on T2.FINPROD_ID=n.FINPROD_ID
    left join FIN_TRADE_PRODUCT t3 on n.FINPROD_ID = t3.FINPROD_ID
      WHERE  n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}'' AND n.PS = ''T02''
      AND ((t3.FINPROD_TYPE2 is null and t2.FINPROD_TYPE2 is not null) or (t3.FINPROD_TYPE2 not in (''F02'', ''F04'',''F08'', ''F25'') AND t3.BRANCH=0 ))
    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
  group by m.PORTFOLIO_ID,n.FINPROD_ID,t2.MDATE,t3.MDATE,n.FINPROD_TYPE2

  ) A1 GROUP BY A1.PORTFOLIO_ID,A1.FINPROD_ID,A1.FINPROD_TYPE,A1.MDATE
)';

----事后(日终)
V_CONTENT_05 CLOB := 'SELECT sum((TRADE_AMT)*(M_DURATION)) AS jiuqi FROM (select
  A1.TRADE_AMT,
  A1.mdate,
  CASE WHEN A1.FINPROD_TYPE =''F01'' THEN
  (
    select CASE WHEN nvl(sum(B1.M_DURATION),0)=0 THEN (((A1.mdate)-(date''${valDate}''))/365) ELSE (nvl(sum(B1.M_DURATION),0)) END M_DURATION from  MST_BOND_VALUATION_INFO B1
      inner join
      (select to_char(max(VALUE_DATE),''yyyy-MM-dd'') as VALUE_DATE,SEC_ID from MST_BOND_VALUATION_INFO where VALUE_DATE<=date''${valDate}'' group by sec_id) B2
      on B1.SEC_ID=B2.SEC_ID and B1.VALUE_DATE=B2.VALUE_DATE WHERE B1.SEC_ID=A1.FINPROD_ID
  )
WHEN (A1.FINPROD_TYPE =''F06'' OR A1.FINPROD_TYPE =''XJ'') THEN (1)/365
WHEN a1.FINPROD_ID =a1.PORTFOLIO_ID THEN (1)/365
ELSE ((((A1.mdate)-(date''${valDate}'')))/365) END M_DURATION
from
(
    select NULL AS FINPROD_ID,NULL AS PORTFOLIO_ID,nvl(sum(AMOUNT),0) as  TRADE_AMT,''XJ'' AS FINPROD_TYPE,NULL AS MDATE   from PTL_CASH_POSITION
   where  PORTFOLIO_ID in ${portfolioCode} and HODING_TYPE=''03'' and CDATE =date''${valDate}''
    UNION ALL
  select
  t1.FINPROD_ID,t1.PORTFOLIO_ID,nvl(t1.DSC_COST_AMT,0)+nvl(t1.TDY_INTINCEXP,0)+nvl(t1.TDY_FLOAT_INGPL,0)+nvl(t1.DELAY_PAY_AMT,0)+nvl(t1.TDY_LOSS_AMT,0) as TRADE_AMT,t2.FINPROD_TYPE2,t2.MDATE
  from PTL_SEC_VALUTION t1
   inner join FIN_PRODUCT t2  on t1.FINPROD_ID = t2.FINPROD_ID
  where t1.PORTFOLIO_ID in ${portfolioCode} and  t1.CDATE = date''${valDate}''
    UNION ALL
    select
  t1.FINPROD_ID,t1.PORTFOLIO_ID,nvl(t1.DSC_COST_AMT,0)+nvl(t1.TDY_INTINCEXP,0)+nvl(t1.TDY_FLOAT_INGPL,0)+nvl(t1.DELAY_PAY_AMT,0)+nvl(t1.TDY_LOSS_AMT,0) as TRADE_AMT,t3.FINPROD_TYPE2,t3.MDATE
  from PTL_SEC_VALUTION t1
  inner join FIN_TRADE_PRODUCT t3  on t1.FINPROD_ID = t3.FINPROD_ID AND t3.BRANCH=0
  where t1.PORTFOLIO_ID in ${portfolioCode} and  t1.CDATE = date''${valDate}''
  and  t3.FINPROD_TYPE2 not in (''F02'',''F04'',''F08'', ''F25'')
) A1
)';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'AZZCJZJQDZHJQ' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'AZZCJZJQDZHJQ' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'AZZCJZJQDZHJQ' and PARA_GRANULARITY = '05';
END;
/
