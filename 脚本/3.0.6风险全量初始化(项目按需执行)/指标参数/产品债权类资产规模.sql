/**风险指标参数: 产品债权类资产规模 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='CPZCLZCGM';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('CPZCLZCGM','产品债权类资产规模', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='CPZCLZCGM';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZCLZCGM','01', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZCLZCGM','02', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZCLZCGM','03', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZCLZCGM','04', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZCLZCGM','05', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'select nvl(sum(TRADE_AMT),0) as RVALUE,PORTFOLIO_ID RKEY from
   (
   ---投资性质为固定收益类型
  select m.PORTFOLIO_ID,nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as TRADE_AMT from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT_ADD PFP on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
  where   m.CDATE =date''${valDate}''-1  group by m.PORTFOLIO_ID
  union all
     ---投资性质为混合类
  select m.PORTFOLIO_ID,nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as TRADE_AMT from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT_ADD PFP on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.CREDITOR_RIGHTS_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = m.FINPROD_ID
  where   m.CDATE =date''${valDate}''-1 group by m.PORTFOLIO_ID
    union all
  ---买断式逆回购、质押式逆回购、拆出
  select  m.PORTFOLIO_ID,
    nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as  TRADE_AMT from PTL_SEC_VALUTION m
  INNER JOIN FIN_TRADE_PRODUCT n on m.FINPROD_ID = n.FINPROD_ID and n.FINPROD_TYPE2 in (''F03'',''F05'',''F09'') and n.BRANCH=0
  where  m.CDATE =date''${valDate}''-1  group by m.PORTFOLIO_ID
  union all
      --可转债网下申购未确认（+）
         SELECT  T1.PORTFOLIO_ID,SUM(nvl(T.TRADE_AMT,0)) TRADE_AMT 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE 
            T.PS  IN ( ''T50'')
           AND FPT.TYPE_8 IN (''F01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
        group by T1.PORTFOLIO_ID
        UNION ALL
        --(投资性质为固定收益)场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
         SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        GROUP BY T1.PORTFOLIO_ID
        UNION ALL
       --(投资性质为固定收益)场外非货币基金、净值资管计划申购申请未确认的交易金额(+)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（-）
         SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT,0)
          when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
         inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
         where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
          else nvl(T.TRADE_AMT,0) end ) RVALUE 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        GROUP BY T1.PORTFOLIO_ID
           UNION ALL
        --(投资性质为混合类)场外货币基金、货币资管计划申购申请未确认的交易金额*债权比例(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额*债权比例（-）
         SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T04'') then nvl(T.TRADE_AMT * GOAL.CREDITOR_RIGHTS_PER,0)  else nvl(-1*T.TRADE_AMT* GOAL.CREDITOR_RIGHTS_PER,0) end ) RVALUE 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         INNER JOIN (
               select * from (
                  select FPG.FINPROD_ID,
                         FPG.CREDITOR_RIGHTS_PER,
                         row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
                  from FIN_PRODUCT_GOAL FPG
                  WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
                  ) where rn = 1
                 ) GOAL ON GOAL.FINPROD_ID = T2.FINPROD_ID
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        GROUP BY T1.PORTFOLIO_ID
        UNION ALL
       --(投资性质为混合类)场外非货币基金、净值资管计划申购申请未确认的交易金额*债权比例(-)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额*债权比例（+）
         SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(T.TRADE_AMT* GOAL.CREDITOR_RIGHTS_PER*-1,0)
          when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT* GOAL.CREDITOR_RIGHTS_PER*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
         inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
         where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
          else nvl(T.TRADE_AMT,0) end ) RVALUE 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         INNER JOIN (
               select * from (
                  select FPG.FINPROD_ID,
                         FPG.CREDITOR_RIGHTS_PER,
                         row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
                  from FIN_PRODUCT_GOAL FPG
                  WHERE FPG.GOAL_DATE <to_date(''${valDate}'', ''yyyy-MM-dd'')
                  ) where rn = 1
                 ) GOAL ON GOAL.FINPROD_ID = T2.FINPROD_ID
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        GROUP BY T1.PORTFOLIO_ID
     union all
      --债券转托管转出方
         SELECT  T1.PORTFOLIO_ID,SUM(nvl(T.TRADE_AMT*-1,0)) TRADE_AMT 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE 
            T.PS  IN ( ''Z01'')
           AND FPT.TYPE_8 IN (''F01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
        group by T1.PORTFOLIO_ID    
        union all
      --债券转托管转入方
         SELECT  T1.PORTFOLIO_ID,SUM(nvl(T.PRIN_AMT,0)) TRADE_AMT 
          FROM TRD_FIN_PRODUCT_BARTER T
         INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T4.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE 
            T4.PS  IN ( ''Z01'')
           AND FPT.TYPE_8 IN (''F01'')
           AND T4.TRADE_STATUS = ''01''
           AND T4.IS_CANCEL = ''N''
           AND T4.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
        group by T1.PORTFOLIO_ID      
  ) group by PORTFOLIO_ID';

----事后(日间)
V_CONTENT_04 CLOB := 'select nvl(sum(TRADE_AMT),0) as RVALUE from
   (
   --估值表（投资性质=债权类）
  select nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as TRADE_AMT 
  from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT_ADD PFP
    on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
  where m.PORTFOLIO_ID in ${portfolioCode}  and  
  m.CDATE =date''${valDate}''-1   AND m.PORTFOLIO_ID!=m.FINPROD_ID
  union all
   --估值表（投资性质=混合类）
  select NVL(SUM((nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)) * NVL(GOAL.CREDITOR_RIGHTS_PER,0)),0) as TRADE_AMT 
  from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT_ADD PFP on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.CREDITOR_RIGHTS_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = m.FINPROD_ID
  where m.PORTFOLIO_ID in ${portfolioCode}  and  
  m.CDATE =date''${valDate}''-1   AND m.PORTFOLIO_ID!=m.FINPROD_ID
  union all
   --估值表（逆回购、拆出）
  select
     nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0)  as  TRADE_AMT from PTL_SEC_VALUTION m
  INNER JOIN FIN_TRADE_PRODUCT n on m.FINPROD_ID = n.FINPROD_ID and n.FINPROD_TYPE2 in (''F03'',''F05'',''F09'') AND n.BRANCH=0
  where m.PORTFOLIO_ID in ${portfolioCode}  and  
  m.CDATE =date''${valDate}''-1
  union ALL
  --1.1交易表（标的类资产，投资性质=债权类）
    SELECT
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'',''T25'',''T64'') THEN NVL(n.TRADE_AMT,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0)
     ELSE  NVL(-1*n.TRADE_AMT,0)
     END) TRADE_AMT
    from TRD_PRODUCT_DEAL n
    INNER JOIN FIN_PRODUCT_ADD PFP
     on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
     LEFT JOIN TRD_PRODUCT_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and 
    n.TRADE_DATE=date''${valDate}'' 
    ---买入、卖出、回售、申购、到期、现金分红、赎回确认、收息到账、还本到账、应收款转入、提前支取、行权-赎回、债转股、认购、提前终止、逾期返售、到期续作、转换申购确认、转换赎回确认、解除质押、到期购回、提前购回
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T07'', ''T11'', ''T19'', ''T22'', ''T23'',''T25'', ''T38'', ''T39'', ''T49'',''T52'',''T54'',''T55'',''T56'',''T64'',''T63'',''T93'',''T95'',''T96'',''Z01'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''
  union ALL
  --1.2交易表（标的类资产，投资性质=混合类）
    SELECT
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'',''T25'',''T64'') THEN NVL(n.TRADE_AMT,0)*NVL(GOAL.CREDITOR_RIGHTS_PER,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0)*NVL(GOAL.CREDITOR_RIGHTS_PER,0)
     ELSE  NVL(-1*n.TRADE_AMT,0) * NVL(GOAL.CREDITOR_RIGHTS_PER,0)
     END) TRADE_AMT
    from TRD_PRODUCT_DEAL n
    INNER JOIN FIN_PRODUCT_ADD PFP
     on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
     LEFT JOIN TRD_PRODUCT_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
     INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.CREDITOR_RIGHTS_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = n.FINPROD_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and 
    n.TRADE_DATE=date''${valDate}'' 
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T25'', ''T49'',''T64'',''T63'',''T93'',''T95'',''T96'',''T54'',''T55'',''T56'',''Z01'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''  
union ALL
--1.3交易表（逆回购、拆出）
    select
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'',''T25'',''T64'') THEN NVL(n.TRADE_AMT,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0)
     ELSE  NVL(-1*n.TRADE_AMT,0)
     END) TRADE_AMT
    from  TRD_PRODUCT_DEAL n
    INNER JOIN FIN_TRADE_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 in (''F03'',''F05'',''F09'') AND t2.BRANCH=0
    LEFT JOIN TRD_PRODUCT_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and 
    n.TRADE_DATE=date''${valDate}'' 
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T25'', ''T49'',''T64'',''T63'',''T93'',''T95'',''T96'',''T54'',''T55'',''T56'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''
  union ALL
  --2.1日间交易表（标的类资产，投资性质=债权类）
    SELECT
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'',''T25'',''T64'') THEN NVL(n.TRADE_AMT,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0)
     ELSE  NVL(-1*n.TRADE_AMT,0)
     END) TRADE_AMT
    from TRD_PRODUCT_TEMP_DEAL n
    INNER JOIN FIN_PRODUCT_ADD PFP
     on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
     LEFT JOIN TRD_PRODUCT_TEMP_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and 
    n.TRADE_DATE=date''${valDate}'' 
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T25'', ''T49'',''T93'',''T95'',''T96'',''T54'',''T55'',''T56'')
    AND n.TRADE_STATUS = ''01''
   AND n.IS_CANCEL = ''N''
    union ALL
  --2.2日间交易表（标的类资产，投资性质=混合类）
    SELECT
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'',''T25'',''T64'') THEN NVL(n.TRADE_AMT,0) * NVL(GOAL.CREDITOR_RIGHTS_PER,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0) * NVL(GOAL.CREDITOR_RIGHTS_PER,0)
     ELSE  NVL(-1*n.TRADE_AMT,0) * NVL(GOAL.CREDITOR_RIGHTS_PER,0)
     END) TRADE_AMT
    from TRD_PRODUCT_TEMP_DEAL n
    INNER JOIN FIN_PRODUCT_ADD PFP
     on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
     LEFT JOIN TRD_PRODUCT_TEMP_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
     INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.CREDITOR_RIGHTS_PER,
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
  --2.3日间交易表（逆回购、拆出）
    select
     SUM(CASE  WHEN n.PS IN(''T01'',''04'',''T04'',''T52'',''T25'',''T64'') THEN NVL(n.TRADE_AMT,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0)
     ELSE  NVL(-1*n.TRADE_AMT,0)
     END) TRADE_AMT
    from  TRD_PRODUCT_TEMP_DEAL n
    INNER JOIN FIN_TRADE_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 in (''F03'',''F05'',''F09'') AND t2.BRANCH=0
    LEFT JOIN TRD_PRODUCT_TEMP_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and 
    n.TRADE_DATE=date''${valDate}'' 
    AND n.PS IN (''T01'',''04'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T11'', ''T52'', ''T22'', ''T23'', ''T38'', ''T39'',''T25'', ''T49'',''T64'',''T63'',''T93'',''T95'',''T96'',''T54'',''T55'',''T56'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''
  union ALL
  --3.1流程交易表（标的类资产，投资性质=债权类）
     select
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'') THEN NVL(n.TRADE_AMT,0)
               WHEN n.PS IN(''T49'',''Z01'') THEN NVL(n.SHARE_AMT*100,0)
     ELSE  NVL(-1*n.TRADE_AMT,0)
     END) TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS = ''01''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
    where m.PORTFOLIO_ID in ${portfolioCode} and 
    n.TRADE_DATE=date''${valDate}'' 
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'',  ''T52'', ''T49'',''Z01'')
   AND n.IS_CANCEL = ''N''
  union ALL
  --3.2流程交易表（标的类资产，投资性质=混合类）
     select
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T52'') THEN NVL(n.TRADE_AMT,0) * NVL(GOAL.CREDITOR_RIGHTS_PER,0)
               WHEN n.PS =''T49'' THEN NVL(n.SHARE_AMT,0) * NVL(GOAL.CREDITOR_RIGHTS_PER,0)
     ELSE  NVL(-1*n.TRADE_AMT,0) * NVL(GOAL.CREDITOR_RIGHTS_PER,0)
     END) TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS = ''01''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.CREDITOR_RIGHTS_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = n.FINPROD_ID
    where m.PORTFOLIO_ID in ${portfolioCode} and 
    n.TRADE_DATE=date''${valDate}'' 
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'',  ''T52'', ''T49'',''Z01'')
   AND n.IS_CANCEL = ''N''
  union ALL
  --3.3流程交易表（逆回购、拆出）
     select
     SUM(NVL(n.TRADE_AMT,0)) TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS = ''01''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_TRADE_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 in (''F03'',''F05'',''F09'') AND t2.BRANCH=0
    where m.PORTFOLIO_ID in ${portfolioCode} and 
    n.TRADE_DATE=date''${valDate}'' 
    AND n.PS IN(''T01'',''04''）
   AND n.IS_CANCEL = ''N''
  UNION ALL
  --4.1内部卖出流程中交易表（标的类资产，投资性质=债权类）
    SELECT
     SUM(NVL(n.TRADE_AMT,0)) TRADE_AMT
    FROM WFL_TRD_PRODUCT_DEAL_ADD m
    INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS = ''01''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
    WHERE n.counter_id IN ${portfolioCode} AND 
    n.TRADE_DATE = DATE''${valDate}'' AND n.PS = ''T02''
    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
    AND n.IS_CANCEL = ''N''
  UNION ALL
  --4.2内部卖出流程中交易表（标的类资产，投资性质=混合类）
    SELECT
     SUM(NVL(n.TRADE_AMT,0)* NVL(GOAL.CREDITOR_RIGHTS_PER,0)) TRADE_AMT
    FROM WFL_TRD_PRODUCT_DEAL_ADD m
    INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS = ''01''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.CREDITOR_RIGHTS_PER,
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
    --5.可转债网下申购：还未做申购确认（投资性质=债权类）

  SELECT SUM(nvl(T.prin_amt,0)) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 WHERE T1.PORTFOLIO_ID in ${portfolioCode} AND
     T.PS = ''T50''
   AND T.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE <= DATE''${valDate}''
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
 --6.可转债网下申购：申购交易流程中（投资性质=债权类）
 SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T1.PORTFOLIO_ID in${portfolioCode} AND
    T.PS = ''T50''
   AND T.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
union all
 --7.可转债网下申购：有申购确认交易（投资性质=债权类）
SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T.PS = ''T05''
   AND T.FINPROD_TYPE2 = ''F01''
  union all
 --8.可转债网下申购：申购确认交易(流程中)（投资性质=债权类）
SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T1.PORTFOLIO_ID in  ${portfolioCode} AND
    T.PS = ''T05''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T.FINPROD_TYPE2 = ''F01''
  union all 
--9.债转股（股票）（投资性质=债权类）
  SELECT SUM(Nvl(T.prin_amt,0)) TRADE_AMT
  FROM TRD_FIN_PRODUCT_BARTER T 
  INNER JOIN TRD_PRODUCT_DEAL T3 ON T.TRADE_ID = T3.TRADE_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 WHERE T1.PORTFOLIO_ID in  ${portfolioCode} AND
    T3.PS = ''T49''
   AND T.FINPROD_TYPE = ''F10''
   AND T3.TRADE_STATUS = ''01''
   AND T3.IS_CANCEL = ''N''
   AND T3.TRADE_DATE = DATE''${valDate}''
   union all 
 --10.债转股（股票）（投资性质=混合类）
  SELECT SUM(nvl(T.prin_amt,0)* NVL(GOAL.CREDITOR_RIGHTS_PER,0)) TRADE_AMT
  FROM TRD_FIN_PRODUCT_BARTER T 
  INNER JOIN TRD_PRODUCT_DEAL T3 ON T.TRADE_ID = T3.TRADE_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
    INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.CREDITOR_RIGHTS_PER,
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
   union all  
--11.债券转托管（投资性质=债权类）申购没有做确认-转入方
  SELECT SUM(nvl(T4.prin_amt,0)) TRADE_AMT
  FROM TRD_FIN_PRODUCT_BARTER T4
 INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T4.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 WHERE T1.PORTFOLIO_ID in ${portfolioCode} AND
     T.PS = ''Z01''
   AND T2.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE <= DATE''${valDate}''
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
 --12.债券转托管（投资性质=债权类）：有申购确认交易（投资性质=债权类）-转入方
SELECT SUM(NVL(T4.prin_amt, 0)) TRADE_AMT
  FROM TRD_FIN_PRODUCT_BARTER T4
 INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T4.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T.PS = ''Z02''
   AND T2.FINPROD_TYPE2 = ''F01''
  union all
  --13.债券转托管（投资性质=债权类）：申购流程中（投资性质=债权类）-转入方 
  SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT
  FROM WFL_TRD_FIN_PRODUCT_BARTER T
 INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z01''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''
 union all
 --14.债券转托管（投资性质=债权类）：申购确认交易(流程中)（投资性质=债权类）-转入方
SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT
  FROM WFL_TRD_FIN_PRODUCT_BARTER T
 INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z02''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''
   UNION ALL
 --11.债券转托管（投资性质=债权类）赎回没有做确认--转出方
  SELECT SUM(nvl(T.PRIN_AMT*-1,0)) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 WHERE T1.PORTFOLIO_ID in ${portfolioCode} AND
     T.PS = ''Z01''
   AND T2.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE <= DATE''${valDate}''
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
 --12.债券转托管（投资性质=债权类）：有赎回确认交易（投资性质=债权类）--转出方
SELECT SUM(NVL(T.prin_amt*-1, 0)) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T.PS = ''Z02''
   AND T2.FINPROD_TYPE2 = ''F01''
  union all
  --13.债券转托管（投资性质=债权类）：赎回流程中（投资性质=债权类） --转出方
  SELECT SUM(NVL(T1.prin_amt*-1, 0)) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T1 
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T1.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z01''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''
 union all
 --14.债券转托管（投资性质=债权类）：赎回确认交易(流程中)（投资性质=债权类）--转出方
SELECT SUM(NVL(T1.prin_amt, 0)) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T1 
  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T1.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_ADD PFP on T1.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z02''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''

     UNION ALL
        --场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
         SELECT case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end  TRADE_AMT
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
          INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND T1.PORTFOLIO_ID in   ${portfolioCode}
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        UNION ALL
       --场外非货币基金、净值资管计划申购申请未确认的交易金额(+)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（-）
         SELECT case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT,0)
          when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
         inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
         where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
          else nvl(T.TRADE_AMT,0) end  TRADE_AMT 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <=DATE''${valDate}''
           AND T1.PORTFOLIO_ID in   ${portfolioCode}           
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        ) 
           UNION ALL
        --场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
         SELECT case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end  TRADE_AMT
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
          INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND T1.PORTFOLIO_ID in   ${portfolioCode}
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        UNION ALL
       --场外非货币基金、净值资管计划申购申请未确认的交易金额(+)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（-）
         SELECT case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT,0)
          when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
         inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
         where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
          else nvl(T.TRADE_AMT,0) end  TRADE_AMT 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <=DATE''${valDate}''
           AND T1.PORTFOLIO_ID in   ${portfolioCode}           
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        ) 
   
  )';

----事后(日终)
V_CONTENT_05 CLOB := 'select nvl(sum(TRADE_AMT),0) as RVALUE from
   (
   --固定收益类
  select (nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)) as TRADE_AMT from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT_ADD PFP
    on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
  where m.PORTFOLIO_ID in ${portfolioCode}  and  m.CDATE =date''${valDate}''
   union all
   --回购
  select
    (nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0))  as  TRADE_AMT from PTL_SEC_VALUTION m
  INNER JOIN FIN_TRADE_PRODUCT n on m.FINPROD_ID = n.FINPROD_ID and n.FINPROD_TYPE2 in (''F03'',''F05'',''F09'') AND n.BRANCH=0
  where m.PORTFOLIO_ID in ${portfolioCode}  and  m.CDATE =date''${valDate}''
  union all
  --混合类的占比计算
  select (nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0))*NVL(GOAL.CREDITOR_RIGHTS_PER,0) as TRADE_AMT from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT_ADD PFP on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.CREDITOR_RIGHTS_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = m.FINPROD_ID
  where m.PORTFOLIO_ID in ${portfolioCode}  and  m.CDATE =date''${valDate}''

union all
   select zq.TRADE_AMT  from (
              --可转债网下申购未确认（+）
                 SELECT  nvl(T.TRADE_AMT,0) TRADE_AMT,T.FINPROD_ID
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
                 WHERE
                    T.PS  IN ( ''T50'')
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T1.PORTFOLIO_ID in ${portfolioCode}
                   AND T.TRADE_DATE <= DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                 )
                union all
              --债券转托管转出方  申购
                 SELECT  nvl(T.TRADE_AMT*-1,0) TRADE_AMT,T.FINPROD_ID
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
                 WHERE
                    T.PS  IN ( ''Z01'')
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T1.PORTFOLIO_ID in ${portfolioCode}
                   AND T.TRADE_DATE <= DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )
                union all
              --债券转托管转入方申购
                 SELECT  nvl(T.PRIN_AMT,0) TRADE_AMT,T.FINPROD_ID
                  FROM TRD_FIN_PRODUCT_BARTER T
                 INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
                 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T4.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
                 WHERE
                    T4.PS  IN ( ''Z01'')
                   AND T4.TRADE_STATUS = ''01''
                   AND T4.IS_CANCEL = ''N''
                   AND T1.PORTFOLIO_ID in ${portfolioCode}
                   AND T4.TRADE_DATE <= DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )
     )zq
        INNER JOIN FIN_PRODUCT T2 on zq.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0  AND FPT.TYPE_8 IN (''F01'')

UNION ALL
   select nvl(hj.TRADE_AMT,0)  from (
        --(投资性质为固定收益)场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
         SELECT case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end  TRADE_AMT ,T.FINPROD_ID
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
         WHERE
            T.PS  IN (''T04'',''T06'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
      UNION ALL
        --(投资性质为混合类)场外货币基金、货币资管计划申购申请未确认的交易金额*债权比例(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额*债权比例（-）
         SELECT case when ps IN (''T04'') then nvl(T.TRADE_AMT * GOAL.CREDITOR_RIGHTS_PER,0)  else nvl(-1*T.TRADE_AMT* GOAL.CREDITOR_RIGHTS_PER,0) end  TRADE_AMT ,T.FINPROD_ID
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
         INNER JOIN (
               select * from (
                  select FPG.FINPROD_ID,
                         FPG.CREDITOR_RIGHTS_PER,
                         row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
                  from FIN_PRODUCT_GOAL FPG
                  WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
                  ) where rn = 1
                 ) GOAL ON GOAL.FINPROD_ID = T.FINPROD_ID
         WHERE
            T.PS  IN (''T04'',''T06'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
)hj
        INNER JOIN FIN_PRODUCT T2 on hj.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0
         where   (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))

UNION ALL
  select case when fhj.ps IN (''T06'') and fhj.TRADE_AMT !=0  then nvl(-1*fhj.TRADE_AMT,0)
           when fhj.ps IN (''T06'') and (fhj.TRADE_AMT =0 or fhj.TRADE_AMT is null)  then nvl(fhj.SHARE_AMT*-1,0)* NVL(hq.VALUATION,1)
          else nvl(fhj.TRADE_AMT,0) end  TRADE_AMT
          from (
       --(投资性质为固定收益)场外非货币基金、净值资管计划申购申请未确认的交易金额(+)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（-）
         SELECT
         T.PS,
         case when ps IN (''T06'') and T.TRADE_AMT !=0  then T.TRADE_AMT
                when ps IN (''T04'') then T.TRADE_AMT
                else NVL(T.TRADE_AMT,0)  end TRADE_AMT,
          case when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT,0)
          else NVL(T.SHARE_AMT,0) end SHARE_AMT,T.FINPROD_ID
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
          INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''01''
         WHERE
            T.PS  IN (''T04'',''T06'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
        UNION ALL
       --(投资性质为混合类)场外非货币基金、净值资管计划申购申请未确认的交易金额*债权比例(-)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额*债权比例（+）
       SELECT
         T.PS,
          case when ps IN (''T06'') and T.TRADE_AMT !=0  then T.TRADE_AMT* NVL(GOAL.CREDITOR_RIGHTS_PER,0)
                when ps IN (''T04'') then T.TRADE_AMT* NVL(GOAL.CREDITOR_RIGHTS_PER,0)
                else NVL(T.TRADE_AMT,0)*NVL(GOAL.CREDITOR_RIGHTS_PER,0)  end TRADE_AMT,
          case when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT,0)* NVL(GOAL.CREDITOR_RIGHTS_PER,0)
          else NVL(T.SHARE_AMT,0)*NVL(GOAL.CREDITOR_RIGHTS_PER,0) end SHARE_AMT,T.FINPROD_ID
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id

         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
         INNER JOIN (
               select * from (
                  select FPG.FINPROD_ID,
                         FPG.CREDITOR_RIGHTS_PER,
                         row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
                  from FIN_PRODUCT_GOAL FPG
                  WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
                  ) where rn = 1
                 ) GOAL ON GOAL.FINPROD_ID = T.FINPROD_ID
         WHERE
            T.PS  IN (''T04'',''T06'')
           AND T.TRADE_STATUS = ''01''
            AND T.IS_CANCEL = ''N''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
  )fhj
          INNER JOIN FIN_PRODUCT T2 on fhj.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0
         LEFT  JOIN (SELECT FINPROD_ID,MAX(VALUATION) VALUATION FROM (
                        SELECT FINPROD_ID,VALUATION,rank() over (partition by FINPROD_ID order by VAL_DATE desc) rk
                        FROM FIN_PRODUCT_VALUATION where VAL_DATE <=date''${valDate}'') where rk =1 group by FINPROD_ID
                        )hq
                        ON T2.FINPROD_MARKET_ID = hq.FINPROD_ID
         where (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))

  )';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'CPZCLZCGM' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'CPZCLZCGM' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'CPZCLZCGM' and PARA_GRANULARITY = '05';
END;
/
