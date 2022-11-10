/**风险指标参数: 产品商品及金融衍生品类资产规模 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='SPJJRYSPLZCGM';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('SPJJRYSPLZCGM','产品商品及金融衍生品类资产规模', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='SPJJRYSPLZCGM';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('SPJJRYSPLZCGM','01', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('SPJJRYSPLZCGM','02', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('SPJJRYSPLZCGM','03', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('SPJJRYSPLZCGM','04', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('SPJJRYSPLZCGM','05', 'SQL','select 1 from dual',  'portfolioCode' , 'Y' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'select nvl(sum(RVALUE),0) as RVALUE,RKEY from (
select nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as RVALUE ,m.PORTFOLIO_ID RKEY from PTL_SEC_VALUTION m
  INNER JOIN FIN_PRODUCT_ADD PFP
  on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''03''
  where  m.CDATE =date''${valDate}''-1  AND m.PORTFOLIO_ID!=m.FINPROD_ID group by m.PORTFOLIO_ID
union all
select NVL(SUM((nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)) * NVL(GOAL.COMMODITY_DERV_PER,0)),0)  as RVALUE,m.PORTFOLIO_ID RKEY from PTL_SEC_VALUTION m
INNER JOIN FIN_PRODUCT_ADD PFP
on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
INNER JOIN (
   select * from (
      select FPG.FINPROD_ID,
             FPG.COMMODITY_DERV_PER,
             row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
      from FIN_PRODUCT_GOAL FPG
      WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
   ) where rn = 1
) GOAL ON GOAL.FINPROD_ID = m.FINPROD_ID
where  m.CDATE =date''${valDate}''-1  AND m.PORTFOLIO_ID!=m.FINPROD_ID group by m.PORTFOLIO_ID
     UNION ALL
                --场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
                 SELECT SUM(case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE, T1.PORTFOLIO_ID as RKEY 
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                INNER JOIN FIN_PRODUCT_ADD PFP  on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''03''
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
               --场外非货币基金、净值资管计划申购申请未确认的交易金额(+)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（-）
                 SELECT SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT,0)
                  when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
                 inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
                 where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
                  else nvl(T.TRADE_AMT,0) end ) RVALUE , T1.PORTFOLIO_ID as RKEY 
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_ADD PFP  on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''03''                
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
                 WHERE 
                    T.PS  IN (''T04'',''T06'')
                   AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T.TRADE_DATE <DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
                )
                  GROUP BY T1.PORTFOLIO_ID
                     UNION ALL
                --场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
                 SELECT SUM(case when ps IN (''T04'') then nvl(T.TRADE_AMT * GOAL.COMMODITY_DERV_PER,0)  else nvl(-1*T.TRADE_AMT * GOAL.COMMODITY_DERV_PER,0) end ) RVALUE, T1.PORTFOLIO_ID as RKEY 
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                INNER JOIN FIN_PRODUCT_ADD PFP  on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
                 INNER JOIN (
                   select * from (
                      select FPG.FINPROD_ID,
                             FPG.COMMODITY_DERV_PER,
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
               --场外非货币基金、净值资管计划申购申请未确认的交易金额(+)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（-）
                 SELECT SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT * GOAL.COMMODITY_DERV_PER,0)
                  when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT * GOAL.COMMODITY_DERV_PER*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
                 inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
                 where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
                  else nvl(T.TRADE_AMT * GOAL.COMMODITY_DERV_PER,0) end ) RVALUE , T1.PORTFOLIO_ID as RKEY 
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_ADD PFP  on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''                
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
                 INNER JOIN (
                   select * from (
                      select FPG.FINPROD_ID,
                             FPG.COMMODITY_DERV_PER,
                             row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
                      from FIN_PRODUCT_GOAL FPG
                      WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
                   ) where rn = 1
                ) GOAL ON GOAL.FINPROD_ID = T2.FINPROD_ID
                 WHERE 
                    T.PS  IN (''T04'',''T06'')
                   AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T.TRADE_DATE <DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
                )
                  GROUP BY T1.PORTFOLIO_ID
) group by RKEY';

----事后(日间)
V_CONTENT_04 CLOB := 'select nvl(sum(TRADE_AMT),0) as tradeAmt from
   (
  select nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as TRADE_AMT from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT_ADD PFP
    on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''03''
  where m.PORTFOLIO_ID in ${portfolioCode}  and  m.CDATE =date''${valDate}''-1  AND m.PORTFOLIO_ID!=m.FINPROD_ID
  union all
  select NVL(SUM((nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)) * NVL(GOAL.COMMODITY_DERV_PER,0)),0) as TRADE_AMT from PTL_SEC_VALUTION m
  INNER JOIN FIN_PRODUCT_ADD PFP
  on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
  INNER JOIN (
     select * from (
        select FPG.FINPROD_ID,
               FPG.COMMODITY_DERV_PER,
               row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
        from FIN_PRODUCT_GOAL FPG
        WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
     ) where rn = 1
  ) GOAL ON GOAL.FINPROD_ID = m.FINPROD_ID
  where m.PORTFOLIO_ID in ${portfolioCode} and m.CDATE =date''${valDate}''-1  AND m.PORTFOLIO_ID!=m.FINPROD_ID
  union ALL
  select
   nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T20'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T07'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T08'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T09'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T22'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T23'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T21'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T59'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T60'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T61'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
    from  TRD_PRODUCT_DEAL n
    inner join FIN_PRODUCT_ADD FPA on n.FINPROD_ID = FPA.FINPROD_ID  and FPA.INVEST_NATURE =''03''
    INNER JOIN TRD_PRODUCT_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T20'',''T21''
    ,''T52'',''T59'',''T60'',''T61'',''T22'',''T23'')
 union ALL
  select
   nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    +nvl(sum(decode(n.PS,''T20'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T07'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T08'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T09'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T22'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T23'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T21'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T59'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T60'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T61'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0) as  TRADE_AMT
    from  TRD_PRODUCT_DEAL n
    inner join FIN_PRODUCT_ADD FPA on n.FINPROD_ID = FPA.FINPROD_ID  and FPA.INVEST_NATURE =''04''
    INNER JOIN TRD_PRODUCT_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.COMMODITY_DERV_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = n.FINPROD_ID
  where n1.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T20'',''T21''
    ,''T52'',''T59'',''T60'',''T61'',''T22'',''T23'')
 UNION all
  select
   nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T20'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T07'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T08'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T09'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T22'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T23'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T21'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T59'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T60'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T61'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
    from  TRD_PRODUCT_TEMP_DEAL n
    inner join FIN_PRODUCT_ADD FPA on n.FINPROD_ID = FPA.FINPROD_ID  and FPA.INVEST_NATURE =''03''
    INNER JOIN TRD_PRODUCT_TEMP_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T20'',''T21''
    ,''T52'',''T59'',''T60'',''T61'',''T22'',''T23'')
 UNION all
  select
   nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    +nvl(sum(decode(n.PS,''T20'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T07'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T08'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T09'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T22'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T23'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T21'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T59'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T60'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T61'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0) as  TRADE_AMT
    from  TRD_PRODUCT_TEMP_DEAL n
    inner join FIN_PRODUCT_ADD FPA on n.FINPROD_ID = FPA.FINPROD_ID  and FPA.INVEST_NATURE =''04''
    INNER JOIN TRD_PRODUCT_TEMP_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.COMMODITY_DERV_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = n.FINPROD_ID
    where n1.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T20'',''T21''
    ,''T52'',''T59'',''T60'',''T61'',''T22'',''T23'')
     union ALL
     select
    nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T20'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T21'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''03''
    where m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T20'',''T21'',''T52'')
  union ALL
     select
    nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    +nvl(sum(decode(n.PS,''T20'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0)
    -nvl(sum(decode(n.PS,''T21'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0) as  TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.COMMODITY_DERV_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = n.FINPROD_ID
    where m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T20'',''T21'',''T52'')
  union ALL
     select
    nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''03''
    where n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}'' AND n.PS = ''T02''
    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
  union ALL
     select
    nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT) * NVL(GOAL.COMMODITY_DERV_PER,0))),0) as  TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT_ADD PFP on n.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
    INNER JOIN (
       select * from (
          select FPG.FINPROD_ID,
                 FPG.COMMODITY_DERV_PER,
                 row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
          from FIN_PRODUCT_GOAL FPG
          WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
       ) where rn = 1
    ) GOAL ON GOAL.FINPROD_ID = n.FINPROD_ID
    where n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}'' AND n.PS = ''T02''
    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
    
    UNION ALL
        --场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
         SELECT  NVL(SUM(case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end),0)  TRADE_AMT
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''03''
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
         SELECT NVL(SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT,0)
          when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then  nvl(T.SHARE_AMT*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
         inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
         where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
          else nvl(T.TRADE_AMT,0) end) ,0)  TRADE_AMT 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''03''
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
         SELECT  NVL(SUM(case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)* NVL(GOAL.COMMODITY_DERV_PER,0)  else nvl(-1*T.TRADE_AMT,0)* NVL(GOAL.COMMODITY_DERV_PER,0) end),0)  TRADE_AMT
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
        INNER JOIN (
               select * from (
                  select FPG.FINPROD_ID,
                         FPG.COMMODITY_DERV_PER,
                         row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
                  from FIN_PRODUCT_GOAL FPG
                  WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
               ) where rn = 1
        ) GOAL ON GOAL.FINPROD_ID = T2.FINPROD_ID
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
         SELECT NVL(SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT,0)* NVL(GOAL.COMMODITY_DERV_PER,0)
          when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT*-1,0)* NVL(GOAL.COMMODITY_DERV_PER,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
         inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
         where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
          else nvl(T.TRADE_AMT,0)* NVL(GOAL.COMMODITY_DERV_PER,0) end) ,0)  TRADE_AMT 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN (
               select * from (
                  select FPG.FINPROD_ID,
                         FPG.COMMODITY_DERV_PER,
                         row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
                  from FIN_PRODUCT_GOAL FPG
                  WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
               ) where rn = 1
        ) GOAL ON GOAL.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_ADD PFP on T2.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
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
V_CONTENT_05 CLOB := 'select  nvl(sum(TRADE_AMT) ,0) as TRADE_AMT from (
---商品衍生品
select nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0) as  TRADE_AMT from PTL_SEC_VALUTION m
  INNER JOIN FIN_PRODUCT_ADD PFP
  on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''03''
  where m.PORTFOLIO_ID in ${portfolioCode}  and  m.CDATE =date''${valDate}'' AND m.PORTFOLIO_ID!=m.FINPROD_ID
union all
--混合
select (nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)) * NVL(GOAL.COMMODITY_DERV_PER, 0) as TRADE_AMT from PTL_SEC_VALUTION m
INNER JOIN FIN_PRODUCT_ADD PFP
on m.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
INNER JOIN (
   select * from (
      select FPG.FINPROD_ID,
             FPG.COMMODITY_DERV_PER,
             row_number() over(partition by FPG.FINPROD_ID order by FPG.GOAL_DATE desc) rn
      from FIN_PRODUCT_GOAL FPG
      WHERE FPG.GOAL_DATE <= to_date(''${valDate}'', ''yyyy-MM-dd'')
   ) where rn = 1
) GOAL ON GOAL.FINPROD_ID = m.FINPROD_ID
where m.PORTFOLIO_ID in ${portfolioCode} and m.CDATE =date''${valDate}'' AND m.PORTFOLIO_ID!=m.FINPROD_ID 

UNION ALL
   select nvl(hj.TRADE_AMT,0)  from (
        --场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
         SELECT case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end  TRADE_AMT,T.FINPROD_ID
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''03''
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
        --场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
         SELECT case when ps IN (''T04'') then nvl(T.TRADE_AMT* NVL(GOAL.COMMODITY_DERV_PER,0),0)  else nvl(-1*T.TRADE_AMT* NVL(GOAL.COMMODITY_DERV_PER,0),0) end  TRADE_AMT,T.FINPROD_ID
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
        INNER JOIN (
           select * from (
              select FPG.FINPROD_ID,
                     FPG.COMMODITY_DERV_PER,
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
       --场外非货币基金、净值资管计划申购申请未确认的交易金额(+)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（-）
         SELECT 
         T.PS, case when ps IN (''T06'') and T.TRADE_AMT !=0  then T.TRADE_AMT
                when ps IN (''T04'') then T.TRADE_AMT
                else NVL(T.TRADE_AMT,0)  end TRADE_AMT,
          case when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT,0) 
          else NVL(T.SHARE_AMT,0) end SHARE_AMT,T.FINPROD_ID
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''03''
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
       --场外非货币基金、净值资管计划申购申请未确认的交易金额(+)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（-）
         SELECT          
          T.PS,
          case when ps IN (''T06'') and T.TRADE_AMT !=0  then T.TRADE_AMT* NVL(GOAL.COMMODITY_DERV_PER,0) 
                when ps IN (''T04'') then T.TRADE_AMT* NVL(GOAL.COMMODITY_DERV_PER,0) 
                else NVL(T.TRADE_AMT,0)*NVL(GOAL.COMMODITY_DERV_PER,0)  end TRADE_AMT,
          case when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT,0)* NVL(GOAL.COMMODITY_DERV_PER,0)
          else NVL(T.SHARE_AMT,0)*NVL(GOAL.COMMODITY_DERV_PER,0) end SHARE_AMT,T.FINPROD_ID
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT_ADD PFP on T.FINPROD_ID = PFP.FINPROD_ID and PFP.INVEST_NATURE =''04''
         INNER JOIN (
           select * from (
              select FPG.FINPROD_ID,
                     FPG.COMMODITY_DERV_PER,
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
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'SPJJRYSPLZCGM' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'SPJJRYSPLZCGM' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'SPJJRYSPLZCGM' and PARA_GRANULARITY = '05';
END;
/
