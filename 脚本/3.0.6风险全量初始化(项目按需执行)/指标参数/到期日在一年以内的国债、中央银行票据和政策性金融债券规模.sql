/**风险指标参数: 到期日在一年以内的国债、中央银行票据和政策性金融债券规模 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='XJGZZYYHPJZCXJRZQGM';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('XJGZZYYHPJZCXJRZQGM','到期日在一年以内的国债、中央银行票据和政策性金融债券规模', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='XJGZZYYHPJZCXJRZQGM';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('XJGZZYYHPJZCXJRZQGM','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('XJGZZYYHPJZCXJRZQGM','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('XJGZZYYHPJZCXJRZQGM','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('XJGZZYYHPJZCXJRZQGM','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('XJGZZYYHPJZCXJRZQGM','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'select y1.RKEY as RKEY,sum(y1.TRADE_AMT)  as RVALUE from
  (
            select m.PORTFOLIO_ID RKEY,nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as  TRADE_AMT from PTL_SEC_VALUTION m
            INNER JOIN FIN_PRODUCT n on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 in (''F01'',''F32'') and n.MDATE>=date''${valDate}'' and n.MDATE<=add_months(date''${valDate}'', 12)
                /**TYPE_8 一级债也存了F01**/
            inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01''))  and t3.TYPE_8 =''F01''
            where  m.CDATE=date''${valDate}''-1  AND m.PORTFOLIO_ID!=m.FINPROD_ID group by m.PORTFOLIO_ID
            union all
                 --债券转托管转出方
                 SELECT  T1.PORTFOLIO_ID,SUM(nvl(T.TRADE_AMT*-1,0)) TRADE_AMT 
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID  and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
                 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
                 WHERE 
                    T.PS  IN ( ''Z01'')
                   AND FPT.TYPE_8 IN (''F01'') and (FPT.TYPE_6 in (''B01'',''B03'') or (FPT.TYPE_6=''B05'' AND FPT.TYPE_7=''01'')) 
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T.TRADE_DATE < DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )
                  GROUP BY T1.PORTFOLIO_ID
                union all
              --债券转托管转入方
                 SELECT   T1.PORTFOLIO_ID RKEY,SUM(nvl(T.PRIN_AMT,0)) TRADE_AMT 
                  FROM TRD_FIN_PRODUCT_BARTER T
                 INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID  and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
                 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T4.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
                 WHERE 
                    T4.PS  IN ( ''Z01'')
                   AND FPT.TYPE_8 IN (''F01'') and (FPT.TYPE_6 in (''B01'',''B03'') or (FPT.TYPE_6=''B05'' AND FPT.TYPE_7=''01'')) 
                   AND T4.TRADE_STATUS = ''01''
                   AND T4.IS_CANCEL = ''N''
                   AND T4.TRADE_DATE < DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )
                  GROUP BY T1.PORTFOLIO_ID
   
    union all
    

             select t.PTL_BAG_ID as RKEY,
             ( select nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as  TRADE_AMT from PTL_SEC_VALUTION m
               INNER JOIN FIN_PRODUCT n on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 in (''F01'',''F32'') and n.MDATE>=date''${valDate}'' and n.MDATE<=add_months(date''${valDate}'', 12)
            inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01''))  and t3.TYPE_8 =''F01''
            where  m.CDATE=date''${valDate}''-1  AND m.PORTFOLIO_ID!=m.FINPROD_ID AND
            m.PORTFOLIO_ID in (   select t1.PORTFOLIO_ID from RSK_PORTFOLIO_PACKAGE_DTL t1  inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}'' where t1.PTL_BAG_ID=t.PTL_BAG_ID)
             )
             from RSK_PORTFOLIO_PACKAGE t
     
              union all
               select s2.PTL_BAG_ID RKEY,NVL(SUM(NVL(s3.RVALUE,0)),0) as TRADE_AMT from （
              --债券转托管转出方
                 SELECT  nvl(T.TRADE_AMT*-1,0) RVALUE ,T1.PORTFOLIO_ID 
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID  and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
                 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
                 WHERE 
                    T.PS  IN ( ''Z01'')
                   AND FPT.TYPE_8 IN (''F01'')  and (FPT.TYPE_6 in (''B01'',''B03'') or (FPT.TYPE_6=''B05'' AND FPT.TYPE_7=''01'')) 
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T.TRADE_DATE < DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )
                union all
              --债券转托管转入方
                 SELECT  nvl(T.PRIN_AMT,0) RVALUE,T1.PORTFOLIO_ID
                  FROM TRD_FIN_PRODUCT_BARTER T
                 INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID  and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
                 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T4.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
                 WHERE 
                    T4.PS  IN ( ''Z01'')
                   AND FPT.TYPE_8 IN (''F01'')  and (FPT.TYPE_6 in (''B01'',''B03'') or (FPT.TYPE_6=''B05'' AND FPT.TYPE_7=''01''))
                   AND T4.TRADE_STATUS = ''01''
                   AND T4.IS_CANCEL = ''N''
                   AND T4.TRADE_DATE < DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )
        ) s3
         INNER JOIN ( select t1.PORTFOLIO_ID,t1.PTL_BAG_ID from RSK_PORTFOLIO_PACKAGE_DTL t1  inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}'')
         s2 on s3.PORTFOLIO_ID=s2.PORTFOLIO_ID  group by s2.PTL_BAG_ID
) y1 group by y1.RKEY';

----事后(日间)
V_CONTENT_04 CLOB := 'select sum(TRADE_AMT) as tradeAmt from
  (
    select nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as  TRADE_AMT from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT n on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 IN (''F01'',''F32'') and n.MDATE>=date''${valDate}'' and n.MDATE<=add_months(date''${valDate}'', 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01''))  and t3.TYPE_8 =''F01''
    where  m.PORTFOLIO_ID in ${portfolioCode}  and m.CDATE=date''${valDate}'' -1
    union ALL
    select
    nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T49'',decode(TRADE_STATUS,''03'',n.SHARE_AMT*-1,n.SHARE_AMT))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T07'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T08'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T09'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
 -nvl(sum(decode(n.PS,''T39'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT  from TRD_PRODUCT_DEAL_ADD m
    inner join TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 IN (''F01'',''F32'') and t2.MDATE>=date''${valDate}'' and t2.MDATE<=add_months(date''${valDate}'', 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01'')) and t3.TYPE_8 =''F01''
    where n.TRADE_STATUS = ''01'' and n.IS_CANCEL = ''N'' AND
 m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T39'',''T11'',''T52'',''T49'')
    union ALL
    select
    nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T49'',decode(TRADE_STATUS,''03'',n.SHARE_AMT*-1,n.SHARE_AMT))),0)
    -nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T03'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T07'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T08'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T09'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
 -nvl(sum(decode(n.PS,''T39'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T11'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as  TRADE_AMT  from TRD_PRODUCT_TEMP_DEAL_ADD m
    inner join TRD_PRODUCT_TEMP_DEAL n on m.TRADE_ID=n.TRADE_ID
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 IN (''F01'',''F32'') and t2.MDATE>=date''${valDate}'' and t2.MDATE<=add_months(date''${valDate}'', 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01'')) and t3.TYPE_8 =''F01''
    where (n.TRADE_STATUS = ''01'' OR n.TRADE_STATUS IS NULL) and n.IS_CANCEL = ''N'' AND
   m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T39'',''T11'',''T52'',''T49'')

   union ALL
    select
     nvl(sum(decode(n.PS,''T01'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T04'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    +nvl(sum(decode(n.PS,''T52'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
    -nvl(sum(decode(n.PS,''T49'',decode(TRADE_STATUS,''03'',n.SHARE_AMT*-1,n.SHARE_AMT))),0)
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
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 IN (''F01'',''F32'') and t2.MDATE>=date''${valDate}'' and t2.MDATE<=add_months(date''${valDate}'', 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01'')) and t3.TYPE_8 =''F01''
    where n.TRADE_STATUS = ''01'' and n.IS_CANCEL = ''N'' AND
 m.PORTFOLIO_ID in ${portfolioCode} and n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T02'',''T03'',''T04'',''T19'',''T07'',''T08'',''T09'',''T39'',''T11'',''T52'',''T49'')
 union ALL
    select
    nvl(sum(decode(n.PS,''T02'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)  as  TRADE_AMT
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT t2 on t2.FINPROD_ID=n.FINPROD_ID and t2.FINPROD_TYPE2 IN (''F01'',''F32'') and t2.MDATE>=date''${valDate}'' and t2.MDATE<=add_months(date''${valDate}'', 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01'')) and t3.TYPE_8 =''F01''
    where  n.TRADE_STATUS = ''01'' and n.IS_CANCEL = ''N'' AND
 n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''  AND n.PS = ''T02''
    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')

UNION ALL
--11.债券转托管（投资性质=债权类）申购没有做确认
  SELECT nvl(T4.prin_amt,0) TRADE_AMT
  FROM TRD_FIN_PRODUCT_BARTER T4
 INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
 and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
inner join FIN_PRODUCT_TYPE T6 on T4.FINPROD_ID=T6.FINPROD_ID and (T6.TYPE_6 in (''B01'',''B03'') or (T6.TYPE_6=''B05'' AND T6.TYPE_7=''01'')) and T6.TYPE_8 =''F01''
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
union all
 --12.债券转托管：有申购确认交易
SELECT NVL(T4.prin_amt, 0) TRADE_AMT
  FROM TRD_FIN_PRODUCT_BARTER T4
 INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
 and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
inner join FIN_PRODUCT_TYPE T6 on T4.FINPROD_ID=T6.FINPROD_ID and (T6.TYPE_6 in (''B01'',''B03'') or (T6.TYPE_6=''B05'' AND T6.TYPE_7=''01'')) and T6.TYPE_8 =''F01''
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.PS = ''Z02''
   AND T2.FINPROD_TYPE2 = ''F01''
  union all
  --13.债券转托管：申购流程中
  SELECT NVL(T.prin_amt, 0) TRADE_AMT
  FROM WFL_TRD_FIN_PRODUCT_BARTER T
 INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=sysdate and T2.MDATE<=add_months(sysdate, 12)
inner join FIN_PRODUCT_TYPE T6 on T2.FINPROD_ID=T6.FINPROD_ID and (T6.TYPE_6 in (''B01'',''B03'') or (T6.TYPE_6=''B05'' AND T6.TYPE_7=''01'')) and T6.TYPE_8 =''F01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z01''
   AND T1.IS_CANCEL = ''N'' 
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''
 union all
 --14.债券转托管：申购确认交易(流程中)
SELECT NVL(T.prin_amt, 0) TRADE_AMT
  FROM WFL_TRD_FIN_PRODUCT_BARTER T
 INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
inner join FIN_PRODUCT_TYPE T6 on T2.FINPROD_ID=T6.FINPROD_ID and (T6.TYPE_6 in (''B01'',''B03'') or (T6.TYPE_6=''B05'' AND T6.TYPE_7=''01'')) and T6.TYPE_8 =''F01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z02''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''
 
    UNION ALL
 --11.债券转托管:赎回没有做确认--转出方
  SELECT nvl(T.PRIN_AMT*-1,0) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
inner join FIN_PRODUCT_TYPE T6 on T2.FINPROD_ID=T6.FINPROD_ID and (T6.TYPE_6 in (''B01'',''B03'') or (T6.TYPE_6=''B05'' AND T6.TYPE_7=''01'')) and T6.TYPE_8 =''F01''
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

union all
 --12.债券转托管：有赎回确认交易--转出方
SELECT NVL(T.prin_amt*-1, 0) TRADE_AMT
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
inner join FIN_PRODUCT_TYPE T6 on T2.FINPROD_ID=T6.FINPROD_ID and (T6.TYPE_6 in (''B01'',''B03'') or (T6.TYPE_6=''B05'' AND T6.TYPE_7=''01'')) and T6.TYPE_8 =''F01''
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T.PS = ''Z02''
   AND T2.FINPROD_TYPE2 = ''F01''

  union all
  --13.债券转托管：赎回流程中 --转出方
  SELECT NVL(T1.prin_amt*-1, 0) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T1 
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T1.FINPROD_ID = T2.FINPROD_ID
 and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
inner join FIN_PRODUCT_TYPE T6 on T2.FINPROD_ID=T6.FINPROD_ID and (T6.TYPE_6 in (''B01'',''B03'') or (T6.TYPE_6=''B05'' AND T6.TYPE_7=''01'')) and T6.TYPE_8 =''F01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z01''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''

 union all
 --14.债券转托管：赎回确认交易(流程中)--转出方
SELECT NVL(T1.prin_amt, 0) TRADE_AMT
  FROM WFL_TRD_PRODUCT_DEAL T1 
  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T1.FINPROD_ID = T2.FINPROD_ID
 and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
inner join FIN_PRODUCT_TYPE T6 on T2.FINPROD_ID=T6.FINPROD_ID and (T6.TYPE_6 in (''B01'',''B03'') or (T6.TYPE_6=''B05'' AND T6.TYPE_7=''01'')) and T6.TYPE_8 =''F01''
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T3.PORTFOLIO_ID in  ${portfolioCode} AND
    T1.PS = ''Z02''
   AND T1.IS_CANCEL = ''N''
   AND T1.TRADE_DATE = DATE''${valDate}''
   AND T2.FINPROD_TYPE2 = ''F01''
    
  )';

----事后(日终)
V_CONTENT_05 CLOB := 'select sum(TRADE_AMT) as RVALUE from
  (
    select nvl(sum(nvl(m.DSC_COST_AMT,0)+nvl(m.TDY_INTINCEXP,0)+nvl(m.TDY_FLOAT_INGPL,0)+nvl(m.DELAY_PAY_AMT,0)+nvl(m.TDY_LOSS_AMT,0)),0) as  TRADE_AMT from PTL_SEC_VALUTION m
    INNER JOIN FIN_PRODUCT n on m.FINPROD_ID=n.FINPROD_ID and n.FINPROD_TYPE2 IN (''F01'',''F32'') and n.MDATE>=date''${valDate}'' and n.MDATE<=add_months(date''${valDate}'', 12)
    inner join FIN_PRODUCT_TYPE t3 on n.FINPROD_ID=t3.FINPROD_ID and (t3.TYPE_6 in (''B01'',''B03'') or (t3.TYPE_6=''B05'' AND t3.TYPE_7=''01''))  and t3.TYPE_8 =''F01''
    where  m.PORTFOLIO_ID in ${portfolioCode}  and m.CDATE=date''${valDate}''
     union all
                 --债券转托管转出方
                 SELECT SUM(nvl(T.TRADE_AMT*-1,0)) TRADE_AMT 
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID  and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
                 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
                 WHERE 
                    T.PS  IN ( ''Z01'')
                   AND FPT.TYPE_8 IN (''F01'') and (FPT.TYPE_6 in (''B01'',''B03'') or (FPT.TYPE_6=''B05'' AND FPT.TYPE_7=''01'')) 
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T.TRADE_DATE <= DATE''${valDate}''
                   AND T1.PORTFOLIO_ID IN ${portfolioCode}                         
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )
                  GROUP BY T1.PORTFOLIO_ID
                union all
              --债券转托管转入方
                 SELECT  SUM(nvl(T.PRIN_AMT,0)) TRADE_AMT 
                  FROM TRD_FIN_PRODUCT_BARTER T
                 INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID  and T2.FINPROD_TYPE2 IN (''F01'',''F32'') and T2.MDATE>=date''${valDate}'' and T2.MDATE<=add_months(date''${valDate}'', 12)
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
                 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T4.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
                 WHERE 
                    T4.PS  IN ( ''Z01'')
                   AND FPT.TYPE_8 IN (''F01'') and (FPT.TYPE_6 in (''B01'',''B03'') or (FPT.TYPE_6=''B05'' AND FPT.TYPE_7=''01'')) 
                   AND T4.TRADE_STATUS = ''01''
                   AND T4.IS_CANCEL = ''N''
                   AND T4.TRADE_DATE <= DATE''${valDate}''
                   AND T1.PORTFOLIO_ID IN ${portfolioCode}                    
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
                )
   
  )';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'XJGZZYYHPJZCXJRZQGM' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'XJGZZYYHPJZCXJRZQGM' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'XJGZZYYHPJZCXJRZQGM' and PARA_GRANULARITY = '05';
END;
/
