/**风险指标参数: 非开放式产品高流动性资产(无现金)(穿透后) **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='FKFSCPGLDXZCCTH';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('FKFSCPGLDXZCCTH','非开放式产品高流动性资产(无现金)(穿透后)', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='FKFSCPGLDXZCCTH';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('FKFSCPGLDXZCCTH','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('FKFSCPGLDXZCCTH','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('FKFSCPGLDXZCCTH','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('FKFSCPGLDXZCCTH','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('FKFSCPGLDXZCCTH','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'SELECT  T1.PORTFOLIO_ID AS RKEY,nvl(sum(nvl(T1.DSC_COST_AMT,0)+nvl(T1.TDY_INTINCEXP,0)),0) as  RVALUE 
  FROM PTL_SEC_PIERCE_VALUTION T1
  INNER JOIN (
        select T2.FINPROD_ID from FIN_PRODUCT T2  
        INNER JOIN (SELECT O_CODE FROM (
        SELECT O_CODE,GRADE_RESULT,rank() over (partition by O_CODE order by GRADE_DATE desc) rk
        FROM MST_BOND_CREDIT_RATING where O_TYPE = ''02'' and  GRADE_RESULT =''02'' and GRADE_DATE <=date''${valDate}'') where rk =1 group by O_CODE) RT
        ON  RT.O_CODE=T2.Finprod_Market_Id 
        where T2.FINPROD_TYPE2  =''F01'' AND T2.ISSUE_TYPE =''01''
        UNION 
        select T2.FINPROD_ID from FIN_PRODUCT T2  
        inner JOIN FIN_PRODUCT_TYPE T4 ON T4.FINPROD_ID=T2.FINPROD_ID AND (T4.TYPE_4 IN (''B01'',''B02'',''B03'') OR (T4.TYPE_4 =''B05'' AND T4.TYPE_5=''01''))
        where T2.FINPROD_TYPE2  =''F01''
    
  )T3 ON  T1.FINPROD_ID=T3.FINPROD_ID
    WHERE  T1.CDATE = DATE''${valDate}''-1  AND T1.PORTFOLIO_ID!=T1.FINPROD_ID
 GROUP BY T1.PORTFOLIO_ID';

----事后(日间)
V_CONTENT_04 CLOB := 'SELECT SUM(TRADE_AMT) AS tradeAmt
FROM(
      SELECT  nvl(sum(nvl(T1.DSC_COST_AMT,0)+nvl(T1.TDY_INTINCEXP,0)),0) AS TRADE_AMT
  FROM PTL_SEC_PIERCE_VALUTION T1
  INNER JOIN (
        select T2.FINPROD_ID from FIN_PRODUCT T2  
        INNER JOIN (SELECT O_CODE FROM (
        SELECT O_CODE,GRADE_RESULT,rank() over (partition by O_CODE order by GRADE_DATE desc) rk
        FROM MST_BOND_CREDIT_RATING where O_TYPE = ''02'' and  GRADE_RESULT =''02'' and GRADE_DATE <=date''${valDate}'') where rk =1 group by O_CODE) RT
        ON  RT.O_CODE=T2.Finprod_Market_Id 
        where T2.FINPROD_TYPE2  =''F01'' AND T2.ISSUE_TYPE =''01''
        UNION 
        select T2.FINPROD_ID from FIN_PRODUCT T2  
        inner JOIN FIN_PRODUCT_TYPE T4 ON T4.FINPROD_ID=T2.FINPROD_ID AND (T4.TYPE_4 IN (''B01'',''B02'',''B03'') OR (T4.TYPE_4 =''B05'' AND T4.TYPE_5=''01''))
        where T2.FINPROD_TYPE2  =''F01''
    
  )T3 ON  T1.FINPROD_ID=T3.FINPROD_ID
    WHERE  T1.CDATE = DATE''${valDate}''-1  AND T1.PORTFOLIO_ID!=T1.FINPROD_ID
    UNION ALL
      SELECT
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
        FROM TRD_PRODUCT_DEAL_ADD m
        inner join TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
        INNER JOIN FIN_PRODUCT T2 on T2.FINPROD_ID=n.FINPROD_ID and T2.FINPROD_TYPE =''F01''
        INNER JOIN FIN_PRODUCT_TYPE T3 on n.FINPROD_ID=T3.FINPROD_ID and  (T3.TYPE_4 IN (''B01'',''B02'',''B03'') OR (T3.TYPE_4 =''B05'' AND T3.TYPE_5=''01''))
        where  m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T11'',''T52'',''T22'',''T23'')
    UNION ALL
      SELECT
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
                FROM TRD_PRODUCT_DEAL_ADD m
        inner join TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
        INNER JOIN FIN_PRODUCT T2 on T2.FINPROD_ID=n.FINPROD_ID and T2.FINPROD_TYPE =''F01'' AND  T2.ISSUE_TYPE =''01''
      inner JOIN MST_BOND_CREDIT_RATING T4 ON T4.O_CODE=T2.Finprod_Market_Id AND T4.GRADE_RESULT =''02''
        WHERE  m.PORTFOLIO_ID in ${portfolioCode}
      AND n.TRADE_DATE=date''${valDate}''  and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T11'',''T52'',''T22'',''T23'')
    UNION ALL
      SELECT
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
        FROM TRD_PRODUCT_TEMP_DEAL_ADD m
        inner join TRD_PRODUCT_TEMP_DEAL n on m.TRADE_ID=n.TRADE_ID
        INNER JOIN FIN_PRODUCT T2 on T2.FINPROD_ID=n.FINPROD_ID and T2.FINPROD_TYPE =''F01''
        INNER JOIN FIN_PRODUCT_TYPE T3 on n.FINPROD_ID=T3.FINPROD_ID and  (T3.TYPE_4 IN (''B01'',''B02'',''B03'') OR (T3.TYPE_4 =''B05'' AND T3.TYPE_5=''01''))
        where  m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T11'',''T52'',''T22'',''T23'')
    UNION ALL
      SELECT
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
                FROM TRD_PRODUCT_TEMP_DEAL_ADD m
        inner join TRD_PRODUCT_TEMP_DEAL n on m.TRADE_ID=n.TRADE_ID
        INNER JOIN FIN_PRODUCT T2 on T2.FINPROD_ID=n.FINPROD_ID and T2.FINPROD_TYPE =''F01'' AND  T2.ISSUE_TYPE =''01''
      inner JOIN MST_BOND_CREDIT_RATING T4 ON T4.O_CODE=T2.Finprod_Market_Id AND T4.GRADE_RESULT =''02''
        WHERE  m.PORTFOLIO_ID in ${portfolioCode}
      AND n.TRADE_DATE=date''${valDate}''  and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T11'',''T52'',''T22'',''T23'')
  UNION ALL
      SELECT
       nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT  from WFL_TRD_PRODUCT_DEAL_ADD m
      inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
        INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
      INNER JOIN FIN_PRODUCT T2 on T2.FINPROD_ID=n.FINPROD_ID and T2.FINPROD_TYPE =''F01''
        INNER JOIN FIN_PRODUCT_TYPE T3 on n.FINPROD_ID=T3.FINPROD_ID and  (T3.TYPE_4 IN (''B01'',''B02'',''B03'') OR (T3.TYPE_4 =''B05'' AND T3.TYPE_5=''01''))
      where m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T52'')
  UNION ALL
      SELECT
      nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)  as  TRADE_AMT
      from WFL_TRD_PRODUCT_DEAL_ADD m
      inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
        INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
      INNER JOIN FIN_PRODUCT T2 on T2.FINPROD_ID=n.FINPROD_ID and T2.FINPROD_TYPE =''F01''
        INNER JOIN FIN_PRODUCT_TYPE T3 on n.FINPROD_ID=T3.FINPROD_ID and  (T3.TYPE_4 IN (''B01'',''B02'',''B03'') OR (T3.TYPE_4 =''B05'' AND T3.TYPE_5=''01''))
      where n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}'' and n.PS = ''T02''
      AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
    UNION ALL
      select
       nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
      -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT  from WFL_TRD_PRODUCT_DEAL_ADD m
      inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
        INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
     INNER JOIN FIN_PRODUCT T2 on T2.FINPROD_ID=n.FINPROD_ID and T2.FINPROD_TYPE =''F01'' AND  T2.ISSUE_TYPE =''01''
      inner JOIN MST_BOND_CREDIT_RATING T4 ON T4.O_CODE=T2.Finprod_Market_Id AND T4.GRADE_RESULT =''02''
        WHERE  m.PORTFOLIO_ID in ${portfolioCode}
      AND n.TRADE_DATE=date''${valDate}''  and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T52'')
    UNION ALL
      select
      nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
      from WFL_TRD_PRODUCT_DEAL_ADD m
      inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
        INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
     INNER JOIN FIN_PRODUCT T2 on T2.FINPROD_ID=n.FINPROD_ID and T2.FINPROD_TYPE =''F01'' AND  T2.ISSUE_TYPE =''01''
      inner JOIN MST_BOND_CREDIT_RATING T4 ON T4.O_CODE=T2.Finprod_Market_Id AND T4.GRADE_RESULT =''02''
        WHERE  n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}'' AND n.PS = ''T02''
    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
  )';

----事后(日终)
V_CONTENT_05 CLOB := 'SELECT  nvl(sum(nvl(T1.DSC_COST_AMT,0)+nvl(T1.TDY_INTINCEXP,0)),0)  AS RVALUE
  FROM PTL_SEC_PIERCE_VALUTION T1
  INNER JOIN (
        select T2.FINPROD_ID from FIN_PRODUCT T2  
        INNER JOIN (SELECT O_CODE FROM (
        SELECT O_CODE,GRADE_RESULT,rank() over (partition by O_CODE order by GRADE_DATE desc) rk
        FROM MST_BOND_CREDIT_RATING where O_TYPE = ''02'' and  GRADE_RESULT =''02'' and GRADE_DATE <=date''${valDate}'') where rk =1 group by O_CODE) RT
        ON  RT.O_CODE=T2.Finprod_Market_Id 
        where T2.FINPROD_TYPE2  =''F01'' AND T2.ISSUE_TYPE =''01''
        UNION 
        select T2.FINPROD_ID from FIN_PRODUCT T2  
        inner JOIN FIN_PRODUCT_TYPE T4 ON T4.FINPROD_ID=T2.FINPROD_ID AND (T4.TYPE_4 IN (''B01'',''B02'',''B03'') OR (T4.TYPE_4 =''B05'' AND T4.TYPE_5=''01''))
        where T2.FINPROD_TYPE2  =''F01''
    
  )T3 ON  T1.FINPROD_ID=T3.FINPROD_ID
    WHERE  T1.CDATE = DATE''${valDate}''  AND T1.PORTFOLIO_ID!=T1.FINPROD_ID
   AND T1.PORTFOLIO_ID IN ${portfolioCode}';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'FKFSCPGLDXZCCTH' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'FKFSCPGLDXZCCTH' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'FKFSCPGLDXZCCTH' and PARA_GRANULARITY = '05';
END;
/
