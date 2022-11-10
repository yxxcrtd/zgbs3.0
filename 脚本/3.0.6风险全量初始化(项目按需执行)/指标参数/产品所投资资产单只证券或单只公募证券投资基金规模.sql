/**风险指标参数: 产品所投资资产单只证券或单只公募证券投资基金规模 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='GMZDDDZZQHJJ';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('GMZDDDZZQHJJ','产品所投资资产单只证券或单只公募证券投资基金规模', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='GMZDDDZZQHJJ';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('GMZDDDZZQHJJ','01', 'SQL','select 1 from dual',  'portfolioCode,assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('GMZDDDZZQHJJ','02', 'SQL','select 1 from dual',  'portfolioCode,assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('GMZDDDZZQHJJ','03', 'SQL','select 1 from dual',  'portfolioCode,assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('GMZDDDZZQHJJ','04', 'SQL','select 1 from dual',  'portfolioCode,assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('GMZDDDZZQHJJ','05', 'SQL','select 1 from dual',  'portfolioCode,assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'SELECT nvl(sum(nvl(RVALUE, 0)),0) as RVALUE,s1.RKEY  FROM (
  select
  NVL(SUM(nvl(t1.DSC_COST_AMT,0)+nvl(t1.TDY_INTINCEXP,0)+nvl(t1.TDY_FLOAT_INGPL,0)+nvl(t1.DELAY_PAY_AMT,0)+nvl(t1.TDY_LOSS_AMT,0)),0) as RVALUE, t1.PORTFOLIO_ID||'',''||t1.FINPROD_ID as RKEY
  from PTL_SEC_VALUTION t1
  inner join FIN_PRODUCT t2 on t1.FINPROD_ID = t2.FINPROD_ID and t2.FINPROD_TYPE2  IN (''F01'',''F06'',''F10'')
  inner join FIN_PRODUCT_TYPE t3 on t2.finprod_id = t3.finprod_id
  /*不包含国债、地方政府债券、中央银行票据、政府机构债券、政策性金融债券*/
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
  where  t1.CDATE = date''${valDate}''-1
  group by t1.PORTFOLIO_ID,t1.FINPROD_ID,t2.FINPROD_NAME
  UNION ALL
        ---可转债网下申购，没有申购确认
SELECT nvl(sum(decode(T.PS,''T50'',decode(T.TRADE_STATUS,''03'',T.prin_amt*-1,T.prin_amt))),0) RVALUE,T1.PORTFOLIO_ID||'',''||T.FINPROD_ID as RKEY
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 and FPT.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 WHERE T.PS = ''T50''
   AND T.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_DATE < date''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01''
      AND NOT EXISTS (
          SELECT 1 FROM TRD_PRODUCT_DEAL TRD1
         WHERE TRD.TRADE_ID = TRD1.P_TRADE_ID
           AND TRD1.TRADE_STATUS = ''03''
  ))
  AND NOT EXISTS (SELECT 1 FROM WFL_TRD_PRODUCT_DEAL TRD
    INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
     INNER JOIN SYS_WFL_BUSINESS_LIST swbl on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
     WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01''
  )
   GROUP BY T1.PORTFOLIO_ID,T.FINPROD_ID
      UNION ALL
                --场外货币基金申购申请未确认的交易金额(+)、（场外货币基金）赎回申请未确认的交易金额（-）
                 SELECT SUM(case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE, T1.PORTFOLIO_ID||'',''||T.FINPROD_ID as RKEY
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0
                 WHERE
                    T.PS  IN (''T04'',''T06'')
                   AND  (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01'')
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T.TRADE_DATE < DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )
                GROUP BY T1.PORTFOLIO_ID,T.FINPROD_ID
                UNION ALL
               --场外非货币基金购申请未确认的交易金额(+)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（-）
                 SELECT SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT,0)
                  when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp
                 inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE
                 where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
                  else nvl(T.TRADE_AMT,0) end ) RVALUE , T1.PORTFOLIO_ID||'',''||T.FINPROD_ID as RKEY
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0
                 WHERE
                    T.PS  IN (''T04'',''T06'')
                   AND  (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02'')
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T.TRADE_DATE <DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )
                  GROUP BY T1.PORTFOLIO_ID,T.FINPROD_ID
                     union all
              --债券转托管转出方
                 SELECT  SUM(nvl(T.TRADE_AMT*-1,0)) RVALUE , T1.PORTFOLIO_ID||'',''||T.FINPROD_ID as RKEY
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 and FPT.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
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
                  GROUP BY T1.PORTFOLIO_ID,T.FINPROD_ID
                union all
              --债券转托管转入方
                 SELECT  SUM(nvl(T.PRIN_AMT,0)) RVALUE , T1.PORTFOLIO_ID||'',''||T.FINPROD_ID as RKEY
                  FROM TRD_FIN_PRODUCT_BARTER T
                 INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 and FPT.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
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
                  GROUP BY T1.PORTFOLIO_ID,T.FINPROD_ID
                UNION ALL
                  select NVL(SUM(nvl(s1.DSC_COST_AMT,0)+nvl(s1.TDY_INTINCEXP,0)+nvl(s1.TDY_FLOAT_INGPL,0)+nvl(s1.DELAY_PAY_AMT,0)+nvl(s1.TDY_LOSS_AMT,0)),0) as RVALUE, s2.PTL_BAG_ID||'',''||s1.FINPROD_ID as RKEY
                  from (select t1.*
                  from PTL_SEC_VALUTION t1
                  inner join FIN_PRODUCT t2 on t1.FINPROD_ID = t2.FINPROD_ID and t2.FINPROD_TYPE2  IN (''F01'',''F06'',''F10'')
                  inner join FIN_PRODUCT_TYPE t3 on t2.finprod_id = t3.finprod_id
                  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
                  where  t1.CDATE = date''${valDate}''-1
                  ) s1,( select t1.PORTFOLIO_ID,t1.PTL_BAG_ID from RSK_PORTFOLIO_PACKAGE_DTL t1  inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}'' )
                  s2 where s1.PORTFOLIO_ID=s2.PORTFOLIO_ID  group by s2.PTL_BAG_ID,s1.FINPROD_ID
             UNION ALL
                select NVL(SUM(NVL(s3.RVALUE,0)),0) as RVALUE, s2.PTL_BAG_ID||'',''||s3.FINPROD_ID as RKEY from (
                  SELECT  T.prin_amt RVALUE,T1.PORTFOLIO_ID,T2.FINPROD_ID
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 and FPT.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
                 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
                 WHERE T.PS = ''T50''
                   AND T.FINPROD_TYPE2 = ''F01''
                  AND T.TRADE_STATUS=''01'' and T.IS_CANCEL=''N''
                   AND T.TRADE_DATE < date''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01''
                      AND NOT EXISTS (
                          SELECT 1 FROM TRD_PRODUCT_DEAL TRD1
                         WHERE TRD.TRADE_ID = TRD1.P_TRADE_ID
                           AND TRD1.TRADE_STATUS = ''03''
                  ))
                  AND NOT EXISTS (SELECT 1 FROM WFL_TRD_PRODUCT_DEAL TRD
                    INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
                     INNER JOIN SYS_WFL_BUSINESS_LIST swbl on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                     WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01''
                  )

             UNION ALL
                --场外货币基金申购申请未确认的交易金额(+)、（场外货币基金）赎回申请未确认的交易金额（-）
                 SELECT case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end RVALUE,T1.PORTFOLIO_ID,T2.FINPROD_ID
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0
                 WHERE
                    T.PS  IN (''T04'',''T06'')
                   AND  (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01'')
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T.TRADE_DATE < DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )

                UNION ALL
               --场外非货币基金申购申请未确认的交易金额(+)、（场外非货币基金赎回申请未确认的交易金额（-）
                 SELECT case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT,0)
                  when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp
                 inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE
                 where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
                  else nvl(T.TRADE_AMT,0) end  RVALUE ,T1.PORTFOLIO_ID,T2.FINPROD_ID
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0
                 WHERE
                    T.PS  IN (''T04'',''T06'')
                   AND  (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02'')
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T.TRADE_DATE <DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )
                     union all
              --债券转托管转出方
                 SELECT  nvl(T.TRADE_AMT*-1,0) RVALUE ,T1.PORTFOLIO_ID,T2.FINPROD_ID
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 and FPT.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
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
                union all
              --债券转托管转入方
                 SELECT  nvl(T.PRIN_AMT,0) RVALUE,T1.PORTFOLIO_ID,T2.FINPROD_ID
                  FROM TRD_FIN_PRODUCT_BARTER T
                 INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0  and FPT.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
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
        ) s3
         INNER JOIN ( select t1.PORTFOLIO_ID,t1.PTL_BAG_ID from RSK_PORTFOLIO_PACKAGE_DTL t1  inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}'')
         s2 on s3.PORTFOLIO_ID=s2.PORTFOLIO_ID  group by s2.PTL_BAG_ID,s3.FINPROD_ID
  ) s1 group by s1.RKEY';

----事后(日间)
V_CONTENT_04 CLOB := 'select nvl(sum(nvl(TRADE_AMT, 0)),0) as RVALUE,s1.FINPROD_ID as RKEY,s1.FINPROD_NAME RKEYCN from
 (
 select m.FINPROD_ID ,n.FINPROD_NAME,NVL(SUM(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as TRADE_AMT from PTL_SEC_VALUTION m
  INNER  JOIN FIN_PRODUCT n on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2  IN (''F01'',''F06'',''F10'')
  inner join FIN_PRODUCT_TYPE t3 on n.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
  where m.PORTFOLIO_ID in ${portfolioCode}  and  m.CDATE =date''${valDate}'' -1 group by m.FINPROD_ID,n.FINPROD_NAME
union all
--债券保证金划款，后续没有申购确认的交易
SELECT T.FINPROD_ID,T2.FINPROD_NAME,nvl(sum(decode(T.PS,''T50'',decode(T.TRADE_STATUS,''03'',T.prin_amt*-1,T.prin_amt))),0) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 WHERE T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.PS = ''T50''
   AND T.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_DATE <= date''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01''
      AND NOT EXISTS (
          SELECT 1 FROM TRD_PRODUCT_DEAL TRD1
         WHERE TRD.TRADE_ID = TRD1.P_TRADE_ID
           AND TRD1.TRADE_STATUS = ''03''
  ))
  AND NOT EXISTS (SELECT 1 FROM WFL_TRD_PRODUCT_DEAL TRD
    INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
     INNER JOIN SYS_WFL_BUSINESS_LIST swbl on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
     WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01''
  )
  group by T.FINPROD_ID,T2.FINPROD_NAME
  --流程中的保证金划款
   union all
SELECT T.FINPROD_ID,T2.FINPROD_NAME,NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.PS = ''T50''
   AND T.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_DATE = date''${valDate}''
  group by T.FINPROD_ID,T2.FINPROD_NAME
union all
 --没有撤销记录的申购确认交易
SELECT T.FINPROD_ID,T2.FINPROD_NAME,NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 WHERE T.TRADE_STATUS = ''01''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.PS = ''T05''
   AND T.FINPROD_TYPE2 = ''F01''
   AND NOT EXISTS ( SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.P_TRADE_ID AND TRD.TRADE_STATUS = ''03'')
 group by T.FINPROD_ID, T2.FINPROD_NAME
   union all
 --申购确认交易(流程中)
SELECT T.FINPROD_ID,T2.FINPROD_NAME,NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.PS = ''T05''
   AND T.TRADE_DATE = date''${valDate}''
   AND T.FINPROD_TYPE2 = ''F01''
   group by T.FINPROD_ID,T2.FINPROD_NAME
 UNION ALL
--11.债券转托管（投资性质=债权类）申购没有做确认
  SELECT T4.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME,SUM(nvl(T4.prin_amt,0)) TRADE_AMT
  FROM TRD_FIN_PRODUCT_BARTER T4
 INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
 inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 WHERE T1.PORTFOLIO_ID in ${portfolioCode} AND
     T.PS = ''Z01''
   AND T2.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE <= DATE''${valDate}''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
)
  AND NOT EXISTS (SELECT 1 FROM WFL_TRD_PRODUCT_DEAL TRD
    INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
     INNER JOIN SYS_WFL_BUSINESS_LIST swbl on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
     WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL=''N''
  )
group by T4.FINPROD_ID,T2.FINPROD_NAME
union all
 --12.债券转托管（投资性质=债权类）：有申购确认交易
SELECT T4.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME,SUM(NVL(T4.prin_amt, 0)) TRADE_AMT
  FROM TRD_FIN_PRODUCT_BARTER T4
 INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
 inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.PS = ''Z02''
   AND T2.FINPROD_TYPE2 = ''F01''
   group by T4.FINPROD_ID,T2.FINPROD_NAME
  union all
  --13.债券转托管（投资性质=债权类）：申购流程中
  SELECT T.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME,SUM(NVL(T.prin_amt, 0)) TRADE_AMT
  FROM WFL_TRD_FIN_PRODUCT_BARTER T
 INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z01''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''
 group by T.FINPROD_ID,T2.FINPROD_NAME
 union all
 --14.债券转托管（投资性质=债权类）：申购确认交易(流程中)
SELECT T.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME,SUM(NVL(T.prin_amt, 0)) TRADE_AMT
  FROM WFL_TRD_FIN_PRODUCT_BARTER T
 INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z02''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''
 group by T.FINPROD_ID,T2.FINPROD_NAME

    UNION ALL
 --11.债券转托管（投资性质=债权类）赎回没有做确认--转出方
  SELECT T.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME,SUM(nvl(T.PRIN_AMT*-1,0)) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
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
 group by T.FINPROD_ID,T2.FINPROD_NAME
union all
 --12.债券转托管：有赎回确认交易（投资性质=债权类）--转出方
SELECT T.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME,SUM(NVL(T.prin_amt*-1, 0)) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T.PS = ''Z02''
   AND T2.FINPROD_TYPE2 = ''F01''
  group by T.FINPROD_ID,T2.FINPROD_NAME
  union all
  --13.债券转托管：赎回流程中（投资性质=债权类） --转出方
  SELECT T1.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME,SUM(NVL(T1.prin_amt*-1, 0)) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T1
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T1.FINPROD_ID = T2.FINPROD_ID
 inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z01''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''
  group by T1.FINPROD_ID,T2.FINPROD_NAME
 union all
 --14.债券转托管：赎回确认交易(流程中)（投资性质=债权类）--转出方
SELECT T1.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME,SUM(NVL(T1.prin_amt, 0)) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T1
  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T1.FINPROD_ID = T2.FINPROD_ID
 inner join FIN_PRODUCT_TYPE t3 on T2.finprod_id = t3.finprod_id
  and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z02''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''
 group by T1.FINPROD_ID,T2.FINPROD_NAME
     UNION ALL
        --场外货币基金申购申请未确认的交易金额(+)、（场外货币基金）赎回申请未确认的交易金额（-）
         SELECT  T.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME,NVL(SUM(case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end),0)  TRADE_AMT
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0
         WHERE
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND T1.PORTFOLIO_ID in   ${portfolioCode}
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
      group by T.FINPROD_ID,T2.FINPROD_NAME
        UNION ALL
       --场外非货币基金申购申请未确认的交易金额(+)、（场外非货币基金赎回申请未确认的交易金额（-）
         SELECT  T.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME,NVL(SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT,0)
          when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp
         inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE
         where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
          else nvl(T.TRADE_AMT,0) end) ,0)  TRADE_AMT
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0
         WHERE
            T.PS  IN (''T04'',''T06'')
           AND  (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <=DATE''${valDate}''
           AND T1.PORTFOLIO_ID in   ${portfolioCode}
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
 group by T.FINPROD_ID,T2.FINPROD_NAME
   --其他交易方向
union ALL
    select n.FINPROD_ID ,m.FINPROD_NAME,
   nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T22'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T23'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  +nvl(sum(decode(n.PS,''T25'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T26'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T39'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T49'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
    from  TRD_PRODUCT_DEAL n
   INNER  JOIN FIN_PRODUCT m on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 IN (''F01'',''F06'',''F10'')
   inner join FIN_PRODUCT_TYPE t3 on m.finprod_id = t3.finprod_id and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
      LEFT JOIN TRD_PRODUCT_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    where  n1.PORTFOLIO_ID in ${portfolioCode}  and n.TRADE_DATE=date''${valDate}''
    and n.PS in (''T01'',''T02'',''T03'',''T04'',''T52'',''T11'',''T19'',''T22'',''T23'',''T25'',''T26'',''T39'',''T49'')
    group by n.FINPROD_ID,m.FINPROD_NAME
union ALL
    select n.FINPROD_ID ,m.FINPROD_NAME,
   nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T22'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T23'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  +nvl(sum(decode(n.PS,''T25'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T26'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T39'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T49'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
    from  TRD_PRODUCT_TEMP_DEAL n
   INNER  JOIN FIN_PRODUCT m on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 IN (''F01'',''F06'',''F10'')
   inner join FIN_PRODUCT_TYPE t3 on m.finprod_id = t3.finprod_id and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
      LEFT JOIN TRD_PRODUCT_TEMP_DEAL_ADD n1 ON n.TRADE_ID =n1.TRADE_ID
    where  n1.PORTFOLIO_ID in ${portfolioCode}  and n.TRADE_DATE=date''${valDate}''
    and n.PS in (''T01'',''T02'',''T03'',''T04'',''T52'',''T11'',''T19'',''T22'',''T23'',''T25'',''T26'',''T39'',''T49'')
    group by n.FINPROD_ID,m.FINPROD_NAME

union ALL
   select   n.FINPROD_ID ,s.FINPROD_NAME,
   nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T22'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T23'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  +nvl(sum(decode(n.PS,''T25'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T26'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T39'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
  -nvl(sum(decode(n.PS,''T49'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
  from WFL_TRD_PRODUCT_DEAL_ADD m
  inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
  INNER  JOIN FIN_PRODUCT s on n.FINPROD_ID=s.FINPROD_ID and s.FINPROD_TYPE2 IN (''F01'',''F06'',''F10'')
  inner join FIN_PRODUCT_TYPE t3 on s.finprod_id = t3.finprod_id and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
  where m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}''
  and n.PS in (''T01'',''T02'',''T03'',''T04'',''T52'',''T11'',''T19'',''T22'',''T23'',''T25'',''T26'',''T39'',''T49'')
  group by n.FINPROD_ID,s.FINPROD_NAME
union ALL
   select   n.FINPROD_ID ,s.FINPROD_NAME,
   nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT
  from WFL_TRD_PRODUCT_DEAL_ADD m
  inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
  INNER  JOIN FIN_PRODUCT s on n.FINPROD_ID=s.FINPROD_ID and s.FINPROD_TYPE2 IN (''F01'',''F06'',''F10'')
  inner join FIN_PRODUCT_TYPE t3 on s.finprod_id = t3.finprod_id
and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
  where n.counter_id in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS =''T02''
  AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
  group by n.FINPROD_ID,s.FINPROD_NAME
 ) s1  group by s1.FINPROD_ID,s1.FINPROD_NAME';

----事后(日终)
V_CONTENT_05 CLOB := 'SELECT nvl(sum(nvl(TRADE_AMT, 0)),0) as RVALUE,s1.FINPROD_ID as RKEY,s1.FINPROD_NAME RKEYCN FROM (
 select nvl(sum(nvl(DSC_COST_AMT,0)+nvl(TDY_FLOAT_INGPL,0)+nvl(TDY_INTINCEXP,0)+ nvl(DELAY_PAY_AMT,0)),0) as TRADE_AMT,m.FINPROD_ID ,n.FINPROD_NAME  from PTL_SEC_VALUTION m
 INNER JOIN FIN_PRODUCT n on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 IN (''F01'',''F06'',''F10'')
 inner join FIN_PRODUCT_TYPE t3 on n.finprod_id = t3.finprod_id and t3.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
 where m.PORTFOLIO_ID in ${portfolioCode}  and  m.CDATE =date''${valDate}'' group by m.FINPROD_ID,n.FINPROD_NAME

  union all
      --可转债网下申购未确认（+）
         SELECT nvl(T.TRADE_AMT,0) TRADE_AMT,t.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 and FPT.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE
            T.PS  IN ( ''T50'')
           AND FPT.TYPE_8 IN (''F01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )

UNION ALL
        --场外货币基金申请未确认的交易金额(+)、（场外货币基金）赎回申请未确认的交易金额（-）
         SELECT case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end  TRADE_AMT,t.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0
         WHERE
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )

UNION ALL
       --场外非货币基金申购申请未确认的交易金额(+)、（场外非货币基金赎回申请未确认的交易金额（-）
         SELECT case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT,0)
          when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp
         inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE
         where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
          else nvl(T.TRADE_AMT,0) end TRADE_AMT,t.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0
         WHERE
            T.PS  IN (''T04'',''T06'')
           AND  (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
        union all
      --债券转托管转出方  申购
         SELECT  nvl(T.TRADE_AMT*-1,0) TRADE_AMT ,t.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 and FPT.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE
            T.PS  IN ( ''Z01'')
           AND FPT.TYPE_8 IN (''F01'')
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
         SELECT  nvl(T.PRIN_AMT,0) TRADE_AMT ,t.FINPROD_ID as ASSETCODE, t2.FINPROD_NAME as ASSETNAME
          FROM TRD_FIN_PRODUCT_BARTER T
         INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0  and FPT.type_6 not in (''B01'',''B02'',''B03'',''B12'',''B05'')
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T4.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE
            T4.PS  IN ( ''Z01'')
           AND FPT.TYPE_8 IN (''F01'')
           AND T4.TRADE_STATUS = ''01''
           AND T4.IS_CANCEL = ''N''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T4.TRADE_DATE <= DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
 ) s1 group by s1.FINPROD_ID,s1.FINPROD_NAME';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'GMZDDDZZQHJJ' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'GMZDDDZZQHJJ' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'GMZDDDZZQHJJ' and PARA_GRANULARITY = '05';
END;
/
