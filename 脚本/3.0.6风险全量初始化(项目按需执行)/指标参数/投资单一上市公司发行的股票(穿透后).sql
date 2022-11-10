/**风险指标参数: 投资单一上市公司发行的股票(穿透后) **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='TZDYSSGSFXGPCTH';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('TZDYSSGSFXGPCTH','投资单一上市公司发行的股票(穿透后)', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='TZDYSSGSFXGPCTH';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TZDYSSGSFXGPCTH','01', 'SQL','select 1 from dual',  'portfolioCode,assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TZDYSSGSFXGPCTH','02', 'SQL','select 1 from dual',  'portfolioCode,assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TZDYSSGSFXGPCTH','03', 'SQL','select 1 from dual',  'portfolioCode,assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TZDYSSGSFXGPCTH','04', 'SQL','select 1 from dual',  'portfolioCode,assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('TZDYSSGSFXGPCTH','05', 'SQL','select 1 from dual',  'portfolioCode,assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'SELECT nvl(sum(nvl(RVALUE, 0)),0) as RVALUE,s1.RKEY  FROM (
select
 sum(nvl(SHARE_AMT,0)) as RVALUE, t1.PORTFOLIO_ID||'',''||t1.FINPROD_ID as RKEY
from PTL_SEC_PIERCE_VALUTION t1
inner join FIN_PRODUCT t2 on t1.FINPROD_ID = t2.FINPROD_ID
inner join FIN_PRODUCT_TYPE fpt on t1.FINPROD_ID=fpt.FINPROD_ID AND fpt.BRANCH=0 and (fpt.TYPE_8 =''F11''  OR (fpt.TYPE_8 =''F01'' and fpt.TYPE_6 =''B17''))
where  t1.CDATE = date''${valDate}''-1
group by t1.PORTFOLIO_ID,t1.FINPROD_ID,t2.FINPROD_NAME
UNION ALL
  select sum(nvl(SHARE_AMT,0)) as RVALUE, s2.PTL_BAG_ID||'',''||s1.FINPROD_ID as RKEY
 from (select t1.*
from PTL_SEC_PIERCE_VALUTION t1
inner join FIN_PRODUCT t2 on t1.FINPROD_ID = t2.FINPROD_ID
inner join FIN_PRODUCT_TYPE fpt on t1.FINPROD_ID=fpt.FINPROD_ID AND fpt.BRANCH=0 and (fpt.TYPE_8 =''F11''  OR (fpt.TYPE_8 =''F01'' and fpt.TYPE_6 =''B17''))
where  t1.CDATE = date''${valDate}''-1
) s1,( select t1.PORTFOLIO_ID,t1.PTL_BAG_ID from RSK_PORTFOLIO_PACKAGE_DTL t1  inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}''-1 )
s2 where s1.PORTFOLIO_ID=s2.PORTFOLIO_ID  group by s2.PTL_BAG_ID,s1.FINPROD_ID
 ) s1 group by s1.RKEY';

----事后(日间)
V_CONTENT_04 CLOB := 'select nvl(sum(nvl(TRADE_AMT, 0)),0) as RVALUE ,s1.FINPROD_ID as RKEY,s1.FINPROD_NAME RKEYCN from

(

  select t1.FINPROD_ID ,n.FINPROD_NAME,nvl(sum(nvl(SHARE_AMT,0)),0) as  TRADE_AMT from PTL_SEC_PIERCE_VALUTION t1

  INNER  JOIN FIN_PRODUCT n on t1.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 =''F10''

  where t1.PORTFOLIO_ID in ${portfolioCode}  and  t1.CDATE=date''${valDate}''-1

  group by t1.FINPROD_ID,n.FINPROD_NAME

union all

   select t2.FINPROD_ID ,m.FINPROD_NAME,

  nvl(sum(decode(t2.PS,''T01'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  + nvl(sum(decode(t2.PS,''T46'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  + nvl(sum(decode(t2.PS,''T47'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  + nvl(sum(decode(t2.PS,''T48'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  - nvl(sum(decode(t2.PS,''T11'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  - nvl(sum(decode(t2.PS,''T02'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  from TRD_PRODUCT_DEAL_ADD t1

  inner join TRD_PRODUCT_DEAL t2 on t1.TRADE_ID=t2.TRADE_ID

   INNER  JOIN FIN_PRODUCT m on m.FINPROD_ID=t2.FINPROD_ID and m.FINPROD_TYPE2 =''F10''

  where t1.PORTFOLIO_ID in ${portfolioCode} and  t2.TRADE_DATE=date''${valDate}'' and t2.PS in (''T01'',''T02'',''T46'',''T47'',''T48'',''T11'')

   group by t2.FINPROD_ID,m.FINPROD_NAME

union all

   select t2.FINPROD_ID ,m.FINPROD_NAME,

  nvl(sum(decode(t2.PS,''T01'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  + nvl(sum(decode(t2.PS,''T46'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  + nvl(sum(decode(t2.PS,''T47'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  + nvl(sum(decode(t2.PS,''T48'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  - nvl(sum(decode(t2.PS,''T11'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  - nvl(sum(decode(t2.PS,''T02'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  from TRD_PRODUCT_TEMP_DEAL_ADD t1

  inner join TRD_PRODUCT_TEMP_DEAL t2 on t1.TRADE_ID=t2.TRADE_ID

   INNER  JOIN FIN_PRODUCT m on m.FINPROD_ID=t2.FINPROD_ID and m.FINPROD_TYPE2 =''F10''

  where t1.PORTFOLIO_ID in ${portfolioCode} and  t2.TRADE_DATE=date''${valDate}'' and t2.PS in (''T01'',''T02'',''T46'',''T47'',''T48'',''T11'')

   group by t2.FINPROD_ID,m.FINPROD_NAME

union all

   select   t2.FINPROD_ID ,s.FINPROD_NAME,

  nvl(sum(decode(t2.PS,''T01'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  + nvl(sum(decode(t2.PS,''T46'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  + nvl(sum(decode(t2.PS,''T47'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  + nvl(sum(decode(t2.PS,''T48'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  - nvl(sum(decode(t2.PS,''T11'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  - nvl(sum(decode(t2.PS,''T02'',decode(TRADE_STATUS,''03'',t2.TRADE_AMT*-1,t2.TRADE_AMT))),0)

  from WFL_TRD_PRODUCT_DEAL_ADD t1

  inner join WFL_TRD_PRODUCT_DEAL t2 on t1.TRADE_ID=t2.TRADE_ID   and t2.TRADE_STATUS != ''03''

  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on t2.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''

  INNER  JOIN FIN_PRODUCT s on t2.FINPROD_ID=s.FINPROD_ID and s.FINPROD_TYPE2 =''F10''

  where t1.PORTFOLIO_ID in ${portfolioCode}  and t2.TRADE_DATE=date''${valDate}'' and t2.PS in (''T01'',''T02'',''T46'',''T47'',''T48'',''T11'')

   group by t2.FINPROD_ID,s.FINPROD_NAME

   

  --增加流程中转仓交易考虑

   SELECT

       FP.FINPROD_ID ,FP.FINPROD_NAME,  

       nvl(sum(decode(n.PS, ''T02'', decode(TRADE_STATUS, ''03'', n.TRADE_AMT*-1, n.TRADE_AMT))), 0)

    FROM WFL_TRD_PRODUCT_DEAL_ADD m

    INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''

    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''

    INNER JOIN FIN_PRODUCT FP ON FP.FINPROD_ID = n.FINPROD_ID  and FP.FINPROD_TYPE2 =''F10''

    WHERE n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''

    AND n.PS =''T02''

    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')

    GROUP BY FP.FINPROD_ID ,FP.FINPROD_NAME

   

) s1  group by s1.FINPROD_ID,s1.FINPROD_NAME';

----事后(日终)
V_CONTENT_05 CLOB := 'select nvl(sum(nvl(TRADE_AMT, 0)),0) as RVALUE ,s1.FINPROD_ID as RKEY,s1.FINPROD_NAME RKEYCN from (
  select t1.FINPROD_ID ,n.FINPROD_NAME,nvl(sum(nvl(SHARE_AMT,0)),0) as  TRADE_AMT from PTL_SEC_PIERCE_VALUTION t1
  INNER  JOIN FIN_PRODUCT n on t1.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 =''F10''
  where t1.PORTFOLIO_ID in ${portfolioCode}  and  t1.CDATE=date''${valDate}''-1
  group by t1.FINPROD_ID,n.FINPROD_NAME
) s1  group by s1.FINPROD_ID,s1.FINPROD_NAME';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'TZDYSSGSFXGPCTH' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'TZDYSSGSFXGPCTH' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'TZDYSSGSFXGPCTH' and PARA_GRANULARITY = '05';
END;
/
