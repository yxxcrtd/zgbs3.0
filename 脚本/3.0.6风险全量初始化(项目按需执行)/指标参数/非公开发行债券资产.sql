/**风险指标参数: 非公开发行债券资产 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='FGKFXZQZC';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('FGKFXZQZC','非公开发行债券资产', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='FGKFXZQZC';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('FGKFXZQZC','01', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('FGKFXZQZC','02', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('FGKFXZQZC','03', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('FGKFXZQZC','04', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('FGKFXZQZC','05', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'select
 t1.PORTFOLIO_ID as RKEY,NVL(SUM(nvl(t1.DSC_COST_AMT,0)+nvl(t1.TDY_INTINCEXP,0)+nvl(t1.TDY_FLOAT_INGPL,0)+nvl(t1.DELAY_PAY_AMT,0)+nvl(t1.TDY_LOSS_AMT,0)),0)  as RVALUE
from PTL_SEC_VALUTION t1
inner join FIN_PRODUCT t2  on t1.FINPROD_ID=t2.FINPROD_ID and t2.FINPROD_TYPE2 =''F01'' and t2.ISSUE_TYPE = ''02''
 inner join FIN_PRODUCT_TYPE fpt on t1.FINPROD_ID = fpt.FINPROD_ID and fpt.TYPE_6 != ''B17''
where  t1.CDATE = date''${valDate}''-1
group by t1.PORTFOLIO_ID
UNION all
   select t.PTL_BAG_ID as RKEY,
   ( select NVL(SUM(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as  RVALUE from PTL_SEC_VALUTION m
  inner join FIN_PRODUCT t2  on m.FINPROD_ID=t2.FINPROD_ID and t2.FINPROD_TYPE2 =''F01'' and t2.ISSUE_TYPE = ''02''
    inner join FIN_PRODUCT_TYPE fpt on m.FINPROD_ID = fpt.FINPROD_ID and fpt.TYPE_6 != ''B17''
  where    m.CDATE =date''${valDate}''-1  AND
  m.PORTFOLIO_ID in (   select t1.PORTFOLIO_ID from RSK_PORTFOLIO_PACKAGE_DTL t1  inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}'' where t1.PTL_BAG_ID=t.PTL_BAG_ID)
   )
   from RSK_PORTFOLIO_PACKAGE t';

----事后(日间)
V_CONTENT_04 CLOB := 'select sum(nvl(TRADE_AMT, 0)) as tradeAmt from
 (
 select sum(amount) as TRADE_AMT from
 (
  SELECT
  nvl(t1.DSC_COST_AMT,0)+nvl(t1.TDY_INTINCEXP,0)+nvl(t1.TDY_FLOAT_INGPL,0)+nvl(t1.DELAY_PAY_AMT,0)+nvl(t1.TDY_LOSS_AMT,0)  as  amount
  from PTL_SEC_VALUTION t1
  inner join FIN_PRODUCT fp on t1.FINPROD_ID = fp.FINPROD_ID and fp.FINPROD_TYPE2 = ''F01'' and fp.ISSUE_TYPE = ''02''
  inner join FIN_PRODUCT_TYPE fpt on t1.FINPROD_ID = fpt.FINPROD_ID and fpt.TYPE_6 != ''B17''
  where t1.PORTFOLIO_ID in ${portfolioCode} and  t1.CDATE=date''${valDate}''-1
 )
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
  -nvl(sum(decode(n.PS,''T22'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T23'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
  from TRD_PRODUCT_DEAL_ADD m inner join TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
  inner join FIN_PRODUCT fp on n.FINPROD_ID = fp.FINPROD_ID and fp.FINPROD_TYPE2 = ''F01'' and fp.ISSUE_TYPE = ''02''
  inner join FIN_PRODUCT_TYPE fpt on n.FINPROD_ID = fpt.FINPROD_ID and fpt.TYPE_6 != ''B17''
  where m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T11'',''T52'',''T22'',''T23'')
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
  -nvl(sum(decode(n.PS,''T22'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T23'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
  from TRD_PRODUCT_TEMP_DEAL_ADD m inner join TRD_PRODUCT_TEMP_DEAL n on m.TRADE_ID=n.TRADE_ID
  inner join FIN_PRODUCT fp on n.FINPROD_ID = fp.FINPROD_ID and fp.FINPROD_TYPE2 = ''F01'' and fp.ISSUE_TYPE = ''02''
  inner join FIN_PRODUCT_TYPE fpt on n.FINPROD_ID = fpt.FINPROD_ID and fpt.TYPE_6 != ''B17''
  where m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T11'',''T52'',''T22'',''T23'')
union all
  select
   nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
  from WFL_TRD_PRODUCT_DEAL_ADD m inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
  inner join FIN_PRODUCT fp on n.FINPROD_ID = fp.FINPROD_ID and fp.FINPROD_TYPE2 = ''F01'' and fp.ISSUE_TYPE = ''02''
  inner join FIN_PRODUCT_TYPE fpt on n.FINPROD_ID = fpt.FINPROD_ID and fpt.TYPE_6 != ''B17''
  where m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T52'')
union all
  select
  nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
  from WFL_TRD_PRODUCT_DEAL_ADD m inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
  inner join FIN_PRODUCT fp on n.FINPROD_ID = fp.FINPROD_ID and fp.FINPROD_TYPE2 = ''F01'' and fp.ISSUE_TYPE = ''02''
  inner join FIN_PRODUCT_TYPE fpt on n.FINPROD_ID = fpt.FINPROD_ID and fpt.TYPE_6 != ''B17''
  where n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''  AND n.PS = ''T02''
   AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
)';

----事后(日终)
V_CONTENT_05 CLOB := 'select
nvl(sum(nvl(targetData.VALUE, 0)), 0) as RVALUE
from
(
select
  NVL(SUM(nvl(t1.DSC_COST_AMT,0)+nvl(t1.TDY_INTINCEXP,0)+nvl(t1.TDY_FLOAT_INGPL,0)+nvl(t1.DELAY_PAY_AMT,0)+nvl(t1.TDY_LOSS_AMT,0)),0)  as  VALUE from PTL_SEC_VALUTION t1
inner join FIN_PRODUCT t2  on t1.FINPROD_ID=t2.FINPROD_ID and t2.FINPROD_TYPE2 =''F01'' and t2.ISSUE_TYPE = ''02''
inner join FIN_PRODUCT_TYPE fpt on t1.FINPROD_ID = fpt.FINPROD_ID and fpt.TYPE_6 != ''B17''
where t1.PORTFOLIO_ID in ${portfolioCode} and  t1.CDATE =date''${valDate}''
group by t1.PORTFOLIO_ID
) targetData';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'FGKFXZQZC' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'FGKFXZQZC' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'FGKFXZQZC' and PARA_GRANULARITY = '05';
END;
/
