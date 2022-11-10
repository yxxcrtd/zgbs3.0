/**风险指标参数: 产品权益类资产规模 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='QYLZCGM';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('QYLZCGM','产品权益类资产规模', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  'SQL' ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='QYLZCGM';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('QYLZCGM','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('QYLZCGM','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('QYLZCGM','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('QYLZCGM','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('QYLZCGM','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'select PORTFOLIO_ID RKEY,nvl(sum(TRADE_AMT),0) as RVALUE from (
select m.PORTFOLIO_ID,nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as TRADE_AMT from PTL_SEC_VALUTION m
INNER JOIN FIN_PRODUCT_ADD PFP
on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
where  m.CDATE =date''${valDate}''-1  AND m.PORTFOLIO_ID!=m.FINPROD_ID group by m.PORTFOLIO_ID
union all
select m.PORTFOLIO_ID,NVL(SUM((nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)) * NVL(GOAL.EQUITY_PER,0)),0) as  TRADE_AMT from PTL_SEC_VALUTION m
INNER JOIN FIN_PRODUCT_ADD PFP
on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
INNER JOIN (
   select * from (
      select FPG.FINPROD_ID,
             FPG.EQUITY_PER,
             row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
      from FIN_PRODUCT_GOAL FPG
      WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
   ) where rn = 1
) GOAL ON GOAL.FINPROD_ID = m.FINPROD_ID
where  m.CDATE =date''${valDate}''-1  AND m.PORTFOLIO_ID!=m.FINPROD_ID group by m.PORTFOLIO_ID
) group by PORTFOLIO_ID';

----事后(日间)
V_CONTENT_04 CLOB := 'select nvl(sum(TRADE_AMT),0) as tradeAmt from
   (
   --估值表（投资性质=权益类）
  select nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as TRADE_AMT
  from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT_ADD PFP
    on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
  where m.PORTFOLIO_ID in ${portfolioCode}  and
  m.CDATE =date''${valDate}''-1   AND m.PORTFOLIO_ID!=m.FINPROD_ID
  union all
   --估值表（投资性质=混合类）
  select NVL(SUM((nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)) * NVL(GOAL.EQUITY_PER,0)),0)  as TRADE_AMT
  from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT_ADD PFP on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.EQUITY_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = m.FINPROD_ID
  where m.PORTFOLIO_ID in ${portfolioCode}  and
  m.CDATE =date''${valDate}''-1   AND m.PORTFOLIO_ID!=m.FINPROD_ID
  union ALL
  --1.1交易表（标的类资产，投资性质=权益类）
    SELECT
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'',''T25'',''T64'') THEN NVL(n.TRADE_AMT,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0)
     ELSE  NVL(-1*n.TRADE_AMT,0)
     END) TRADE_AMT
    from TRD_PRODUCT_DEAL n
    INNER JOIN FIN_PRODUCT_ADD PFP
     on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
     LEFT JOIN TRD_PRODUCT_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and
    n.TRADE_DATE=date''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T25'', ''T49'',''T64'',''T63'',''T93'',''T95'',''T96'',''T54'',''T55'',''T56'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''
  union ALL
  --1.2交易表（标的类资产，投资性质=混合类）
    SELECT
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'',''T25'',''T64'') THEN NVL(n.TRADE_AMT,0)*NVL(GOAL.EQUITY_PER,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0)*NVL(GOAL.EQUITY_PER,0)
     ELSE  NVL(-1*n.TRADE_AMT,0) * NVL(GOAL.EQUITY_PER,0)
     END) TRADE_AMT
    from TRD_PRODUCT_DEAL n
    INNER JOIN FIN_PRODUCT_ADD PFP
     on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
     LEFT JOIN TRD_PRODUCT_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
     INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.EQUITY_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = n.FINPROD_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and
    n.TRADE_DATE=date''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T25'', ''T49'',''T64'',''T63'',''T93'',''T95'',''T96'',''T54'',''T55'',''T56'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''
  union ALL
  --2.1日间交易表（标的类资产，投资性质=权益类）
    SELECT
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'',''T25'',''T64'') THEN NVL(n.TRADE_AMT,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0)
     ELSE  NVL(-1*n.TRADE_AMT,0)
     END) TRADE_AMT
    from TRD_PRODUCT_TEMP_DEAL n
    INNER JOIN FIN_PRODUCT_ADD PFP
     on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
     LEFT JOIN TRD_PRODUCT_TEMP_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and
    n.TRADE_DATE=date''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T25'', ''T49'',''T93'',''T95'',''T96'',''T54'',''T55'',''T56'')
    AND n.TRADE_STATUS = ''01''
   AND n.IS_CANCEL = ''N''
    union ALL
  --2.2日间交易表（标的类资产，投资性质=混合类）
    SELECT
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'',''T25'',''T64'') THEN NVL(n.TRADE_AMT,0) * NVL(GOAL.EQUITY_PER,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0) * NVL(GOAL.EQUITY_PER,0)
     ELSE  NVL(-1*n.TRADE_AMT,0) * NVL(GOAL.EQUITY_PER,0)
     END) TRADE_AMT
    from TRD_PRODUCT_TEMP_DEAL n
    INNER JOIN FIN_PRODUCT_ADD PFP
     on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
     LEFT JOIN TRD_PRODUCT_TEMP_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
     INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.EQUITY_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = n.FINPROD_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and
    n.TRADE_DATE=date''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T25'', ''T49'',''T93'',''T95'',''T96'',''T54'',''T55'',''T56'')
    AND n.TRADE_STATUS = ''01''
   AND n.IS_CANCEL = ''N''
  union ALL
  --3.1流程交易表（标的类资产，投资性质=权益类）
     select
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'') THEN NVL(n.TRADE_AMT,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0)
     ELSE  NVL(-1*n.TRADE_AMT,0)
     END) TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS = ''01''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
    where m.PORTFOLIO_ID in ${portfolioCode} and
    n.TRADE_DATE=date''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'',  ''T52'', ''T49'')
   AND n.IS_CANCEL = ''N''
  union ALL
  --3.2流程交易表（标的类资产，投资性质=混合类）
     select
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'') THEN NVL(n.TRADE_AMT,0) * NVL(GOAL.EQUITY_PER,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0) * NVL(GOAL.EQUITY_PER,0)
     ELSE  NVL(-1*n.TRADE_AMT,0) * NVL(GOAL.EQUITY_PER,0)
     END) TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS = ''01''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.EQUITY_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = n.FINPROD_ID
    where m.PORTFOLIO_ID in ${portfolioCode} and
    n.TRADE_DATE=date''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'',  ''T52'', ''T49'')
   AND n.IS_CANCEL = ''N''
  UNION ALL
  --4.1内部卖出流程中交易表（标的类资产，投资性质=权益类）
    SELECT
     SUM(NVL(n.TRADE_AMT,0)) TRADE_AMT
    FROM WFL_TRD_PRODUCT_DEAL_ADD m
    INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS = ''01''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
    WHERE n.counter_id IN ${portfolioCode} AND
    n.TRADE_DATE = DATE''${valDate}'' AND n.PS = ''T02''
    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
    AND n.IS_CANCEL = ''N''
  UNION ALL
  --4.2内部卖出流程中交易表（标的类资产，投资性质=混合类）
    SELECT
     SUM(NVL(n.TRADE_AMT,0)* NVL(GOAL.EQUITY_PER,0)) TRADE_AMT
    FROM WFL_TRD_PRODUCT_DEAL_ADD m
    INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS = ''01''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.EQUITY_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = n.FINPROD_ID
    WHERE n.counter_id IN ${portfolioCode} AND
    n.TRADE_DATE = DATE''${valDate}'' AND n.PS = ''T02''
    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
    AND n.IS_CANCEL = ''N''
      union all
    --5.可转债网下申购：还未做申购确认（投资性质=权益类）

  SELECT SUM(nvl(T.prin_amt,0)) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
 WHERE T1.PORTFOLIO_ID in ${portfolioCode} AND
     T.PS = ''T50''
   AND T.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
)
  AND NOT EXISTS (SELECT 1 FROM WFL_TRD_PRODUCT_DEAL TRD
    INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
     INNER JOIN SYS_WFL_BUSINESS_LIST swbl on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
     WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL=''N''
  )
 union all
 --6.可转债网下申购：申购交易流程中（投资性质=权益类）
 SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T1.PORTFOLIO_ID in${portfolioCode} AND
    T.PS = ''T50''
   AND T.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
union all
 --7.可转债网下申购：有申购确认交易（投资性质=权益类）
SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.PS = ''T05''
   AND T.FINPROD_TYPE2 = ''F01''
  union all
 --8.可转债网下申购：申购确认交易(流程中)（投资性质=权益类）
SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T1.PORTFOLIO_ID in  ${portfolioCode} AND
    T.PS = ''T05''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T.FINPROD_TYPE2 = ''F01''
  union all
--9.债转股（股票）（投资性质=权益类）
  SELECT SUM(Nvl(T.prin_amt,0)) TRADE_AMT
  FROM TRD_FIN_PRODUCT_BARTER T
  INNER JOIN TRD_PRODUCT_DEAL T3 ON T.TRADE_ID = T3.TRADE_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
 WHERE T1.PORTFOLIO_ID in  ${portfolioCode} AND
    T3.PS = ''T49''
   AND T.FINPROD_TYPE = ''F10''
   AND T3.TRADE_STATUS = ''01''
   AND T3.IS_CANCEL = ''N''
   AND T3.TRADE_DATE = DATE''${valDate}''
      union all
    --5.可转债网下申购：还未做申购确认（投资性质=混合类）

  SELECT SUM(nvl(T.prin_amt,0)* NVL(GOAL.EQUITY_PER,0)) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
    INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.EQUITY_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = T.FINPROD_ID
 WHERE T1.PORTFOLIO_ID in ${portfolioCode} AND
     T.PS = ''T50''
   AND T.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
)
  AND NOT EXISTS (SELECT 1 FROM WFL_TRD_PRODUCT_DEAL TRD
    INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
     INNER JOIN SYS_WFL_BUSINESS_LIST swbl on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
     WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL=''N''
  )
 union all
 --6.可转债网下申购：申购交易流程中（投资性质=混合类）
 SELECT SUM(NVL(T.prin_amt, 0)* NVL(GOAL.EQUITY_PER,0)) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
    INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.EQUITY_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = T.FINPROD_ID
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T1.PORTFOLIO_ID in${portfolioCode} AND
    T.PS = ''T50''
   AND T.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
union all
 --7.可转债网下申购：有申购确认交易（投资性质=混合类）
SELECT SUM(NVL(T.prin_amt, 0)* NVL(GOAL.EQUITY_PER,0)) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
    INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.EQUITY_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = T.FINPROD_ID
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.PS = ''T05''
   AND T.FINPROD_TYPE2 = ''F01''
  union all
 --8.可转债网下申购：申购确认交易(流程中)（投资性质=混合类）
SELECT SUM(NVL(T.prin_amt, 0)* NVL(GOAL.EQUITY_PER,0)) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
    INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.EQUITY_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = T.FINPROD_ID
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T1.PORTFOLIO_ID in  ${portfolioCode} AND
    T.PS = ''T05''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T.FINPROD_TYPE2 = ''F01''
  union all
--9.债转股（股票）（投资性质=混合类）
  SELECT SUM(nvl(T.prin_amt,0)* NVL(GOAL.EQUITY_PER,0)) TRADE_AMT
  FROM TRD_FIN_PRODUCT_BARTER T
  INNER JOIN TRD_PRODUCT_DEAL T3 ON T.TRADE_ID = T3.TRADE_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
    INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.EQUITY_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = T.FINPROD_ID
 WHERE T1.PORTFOLIO_ID in  ${portfolioCode} AND
    T3.PS = ''T49''
   AND T.FINPROD_TYPE = ''F10''
   AND T3.TRADE_STATUS = ''01''
   AND T3.IS_CANCEL = ''N''
   AND T3.TRADE_DATE = DATE''${valDate}''
  )';

----事后(日终)
V_CONTENT_05 CLOB := 'select nvl(sum(TRADE_AMT),0) as tradeAmt from (
select nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as  TRADE_AMT from PTL_SEC_VALUTION m
INNER JOIN FIN_PRODUCT_ADD PFP
on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''02''
where m.PORTFOLIO_ID in ${portfolioCode} and m.CDATE =date''${valDate}'' AND m.PORTFOLIO_ID!=m.FINPROD_ID
union all
select NVL(SUM((nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)) * NVL(GOAL.EQUITY_PER,0)),0) as TRADE_AMT from PTL_SEC_VALUTION m
INNER JOIN FIN_PRODUCT_ADD PFP
on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
INNER JOIN (
   select * from (
      select FPG.FINPROD_ID,
             FPG.EQUITY_PER,
             row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
      from FIN_PRODUCT_GOAL FPG
      WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
   ) where rn = 1
) GOAL ON GOAL.FINPROD_ID = m.FINPROD_ID
where m.PORTFOLIO_ID in ${portfolioCode} and m.CDATE =date''${valDate}'' AND m.PORTFOLIO_ID!=m.FINPROD_ID
)';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'QYLZCGM' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'QYLZCGM' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'QYLZCGM' and PARA_GRANULARITY = '05';
END;
/
