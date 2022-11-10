/**风险指标参数: 流动性受限资产的持仓市值 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='LDXSXZCCCSZ';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('LDXSXZCCCSZ','流动性受限资产的持仓市值', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='LDXSXZCCCSZ';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('LDXSXZCCCSZ','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('LDXSXZCCCSZ','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('LDXSXZCCCSZ','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('LDXSXZCCCSZ','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('LDXSXZCCCSZ','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'WITH FIN_OPEN_PLAN_AST AS  (
  /**申购开放(这里三个union是为了取的顺序赎回优先,无赎回则取申购)**/
   SELECT
   CASH_ID,FINPROD_ID , OPEN_VDATE
   FROM
    FIN_OPEN_PLAN fop
   WHERE
    fop.FINPROD_ID LIKE ''F17_%'' AND fop.OPEN_TYPE = ''01'' AND NOT EXISTS (
    SELECT 1 FROM FIN_OPEN_PLAN fop2 WHERE fop2.OPEN_TYPE = ''02'' AND fop.CASH_ID =fop2.CASH_ID
    )
  /**赎回开放**/
  UNION ALL
   SELECT
   CASH_ID, FINPROD_ID ,OPEN_VDATE
   FROM
    FIN_OPEN_PLAN fop
   WHERE
    fop.FINPROD_ID LIKE ''F17_%'' AND fop.OPEN_TYPE = ''02''
  /**申赎同频**/
  UNION ALL
  SELECT
   CASH_ID,fop.FINPROD_ID , OPEN_VDATE
  FROM
   FIN_OPEN_PLAN fop
  WHERE
   fop.FINPROD_ID LIKE ''F17_%'' AND fop.OPEN_TYPE = ''03''
)
,PROD AS (
    -- 资产到期向前第10个交易日
    SELECT FINPROD_ID FROM (
        SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN FROM (
            SELECT m.FINPROD_ID,  m.VDATE,m.MDATE, MH.CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO FROM (
                -- 逆回购
                SELECT FTP.FINPROD_ID,FTP.VDATE,FTP.MDATE,NVL(FTP.CALENDAR_ID,''01'') CALENDAR_ID FROM FIN_TRADE_PRODUCT FTP
                INNER JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                WHERE FTP.MDATE IS NOT NULL AND T1.TYPE_8 IN (''F07'',''F10'') AND TYPE_7 = ''04'' AND FTP.MDATE >= DATE ''${valDate}'' - 1
        UNION ALL
        -- 银行定期存款（不含活期存款和大额存单以及同业借款）
        SELECT FPT.FINPROD_ID,FPT.VDATE,MDATE,NVL(FPT.CALENDAR_ID,''01'') CALENDAR_ID
        FROM FIN_PRODUCT FPT
        INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
        INNER JOIN FIN_PRODUCT_ADD FPA ON FPT.FINPROD_ID = FPA.FINPROD_ID
        -- 这里是有条件可提前支取
        WHERE (FPA.IS_INADVANCE IS NULL OR FPA.IS_INADVANCE IN (''01'',''02''))
        AND T1.TYPE_8 = ''F05'' AND T1.TYPE_6 != ''D03'' AND T1.TYPE_11 not in (''029'',''030'',''031'')
          --距可赎回日在10个交易日以上计划类资产(非高频开放)
          UNION ALL
          SELECT FINPROD_ID,VDATE,OPEN_VDATE,CALENDAR_ID FROM (
            SELECT m.FINPROD_ID,M.VDATE,y.OPEN_VDATE,NVL(M.CALENDAR_ID,''01'') CALENDAR_ID ,rank() over(PARTITION BY m.FINPROD_ID ORDER BY y.OPEN_VDATE asc) AS rowno
            from FIN_PRODUCT m
            INNER JOIN FIN_PRODUCT_TYPE T1 ON m.FINPROD_ID = T1.FINPROD_ID
            INNER JOIN FIN_PRODUCT_ADD FPA ON m.FINPROD_ID=FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
            INNER JOIN  FIN_CASH_INFO n ON n.FINPROD_ID=m.FINPROD_ID  AND n.CASH_TYPE_1=''01'' AND n.CASH_TYPE_2=''C01'' AND M.FINPROD_TYPE2 =''F17''
            INNER JOIN  FIN_OPEN_PLAN_AST y ON y.CASH_ID=n.CASH_ID
            WHERE y.OPEN_VDATE>=date''${valDate}'' AND T1.TYPE_11!=''103'' ORDER BY y.OPEN_VDATE ASC
          ) WHERE  rowno=1
            ) m
            INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND MH.CDATE>=m.MDATE-25  AND MH.IS_HOLIDAY = ''0''
        ) WHERE cdate>=vdate  and vdate< =DATE ''${valDate}'' and CDATE > DATE ''${valDate}''
    ) GROUP BY FINPROD_ID HAVING count(1)>10
    UNION
    -- 资产支持证券
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F01'',''F17'')  AND FPT.TYPE_6 = ''B13''
    UNION
    -- 违约债券资产（不含同业存单）
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    INNER JOIN PTL_RECE_POSITION PRP ON FP.FINPROD_ID = PRP.FINPROD_ID AND PRP.HODING_TYPE = ''03''/*逾期*/
    WHERE FPT.TYPE_8=''F01'' AND type_9!=''01'' AND PRP.PAY_DATE<PRP.CDATE
  UNION
   -- 流动性受限债券,非金融企业债务融资工具或其它
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT_TYPE FPT
    WHERE EXISTS (
        SELECT 1 FROM RSK_SPECIFIC_ASSET_TYPE RSAT WHERE RSAT.IS_FLOW_LIMITED = ''Y'' AND (
        (REGEXP_INSTR(RSAT.TYPE_1||''_''||RSAT.TYPE_2||''_''||RSAT.TYPE_3||''_''||RSAT.TYPE_4 ,
            FPT.TYPE_9||''_''||FPT.TYPE_10||''_''||FPT.TYPE_11||''_''||FPT.TYPE_12) >= 1 AND RSAT.FINPROD_ID IS NULL)
        OR FPT.FINPROD_ID = RSAT.FINPROD_ID)
    )
)
select NVL(SUM(target.RVALUE),0) RVALUE,target.RKEY from (
        SELECT
            NVL(SUM(NVL(M.DSC_COST_AMT,0)+NVL(M.TDY_INTINCEXP,0)+NVL(M.TDY_FLOAT_INGPL,0)+NVL(M.DELAY_PAY_AMT,0)+NVL(M.TDY_LOSS_AMT,0)),0) RVALUE,
            M.PORTFOLIO_ID RKEY
        FROM PTL_SEC_VALUTION M
        INNER JOIN PROD N ON M.FINPROD_ID = N.FINPROD_ID
        WHERE M.CDATE = date''${valDate}'' - 1
        GROUP BY M.PORTFOLIO_ID
        UNION ALL
           ---可转债网下申购，没有申购确认
                   SELECT  nvl(sum(T.prin_amt),0) RVALUE, T1.PORTFOLIO_ID as RKEY
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN PROD N ON N.FINPROD_ID = T2.FINPROD_ID
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
                  group by T1.PORTFOLIO_ID
                     UNION ALL
                        --场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
                         SELECT SUM(case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE, T1.PORTFOLIO_ID as RKEY 
                          FROM TRD_PRODUCT_DEAL T
                         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                         INNER JOIN PROD N ON N.FINPROD_ID = T2.FINPROD_ID
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
                         INNER JOIN PROD N ON N.FINPROD_ID = T2.FINPROD_ID
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
                             union all
                      --债券转托管转出方
                         SELECT  SUM(nvl(T.TRADE_AMT*-1,0)) RVALUE , T1.PORTFOLIO_ID as RKEY 
                          FROM TRD_PRODUCT_DEAL T
                         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                         INNER JOIN PROD N ON N.FINPROD_ID = T2.FINPROD_ID              
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
                          GROUP BY T1.PORTFOLIO_ID 
                        union all
                      --债券转托管转入方
                         SELECT  SUM(nvl(T.PRIN_AMT,0)) RVALUE , T1.PORTFOLIO_ID as RKEY 
                          FROM TRD_FIN_PRODUCT_BARTER T
                         INNER JOIN TRD_PRODUCT_DEAL T4 ON T.trade_id = T4.trade_id
                         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T4.trade_id = T1.trade_id
                         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                         INNER JOIN PROD N ON N.FINPROD_ID = T2.FINPROD_ID            
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
                          GROUP BY T1.PORTFOLIO_ID
) target 
group by target.RKEY';

----事后(日间)
V_CONTENT_04 CLOB := 'WITH FIN_OPEN_PLAN_AST AS (
    /**申购开放(这里三个union是为了取的顺序赎回优先,无赎回则取申购)**/
    SELECT CASH_ID,
           FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''01''
      AND NOT EXISTS(
            SELECT 1 FROM FIN_OPEN_PLAN fop2 WHERE fop2.OPEN_TYPE = ''02'' AND fop.CASH_ID = fop2.CASH_ID
        )
        /**赎回开放**/
    UNION ALL
    SELECT CASH_ID,
           FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''02''
        /**申赎同频**/
    UNION ALL
    SELECT CASH_ID,
           fop.FINPROD_ID,
           OPEN_VDATE
    FROM FIN_OPEN_PLAN fop
    WHERE fop.FINPROD_ID LIKE ''F17_%''
      AND fop.OPEN_TYPE = ''03''
)
   , PROD AS (
    -- 资产到期向前第10个交易日
    SELECT FINPROD_ID
    FROM (
             SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN
             FROM (
                      SELECT m.FINPROD_ID,
                             m.VDATE,
                             m.MDATE,
                             MH.CDATE,
                             ROW_NUMBER() OVER (PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO
                      FROM (
                               -- 逆回购
                               SELECT FTP.FINPROD_ID, FTP.VDATE, FTP.MDATE, NVL(FTP.CALENDAR_ID, ''01'') CALENDAR_ID
                               FROM FIN_TRADE_PRODUCT FTP
                                        INNER JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                               WHERE FTP.MDATE IS NOT NULL
                                 AND T1.TYPE_8 IN (''F07'', ''F10'')
                                 AND TYPE_7 = ''04''
                                 AND FTP.MDATE >= DATE ''${valDate}'' - 1
                               UNION ALL
                               -- 银行定期存款（不含活期存款和大额存单以及同业借款）
                               SELECT FPT.FINPROD_ID, FPT.VDATE, MDATE, NVL(FPT.CALENDAR_ID, ''01'') CALENDAR_ID
                               FROM FIN_PRODUCT FPT
                                        INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
                                        INNER JOIN FIN_PRODUCT_ADD FPA ON FPT.FINPROD_ID = FPA.FINPROD_ID
                                    -- 这里是有条件可提前支取
                               WHERE (FPA.IS_INADVANCE IS NULL OR FPA.IS_INADVANCE IN (''01'', ''02''))
                                 AND T1.TYPE_8 = ''F05''
                                 AND T1.TYPE_6 != ''D03''
                                 AND T1.TYPE_11 not in (''029'', ''030'', ''031'')
                                 --距可赎回日在10个交易日以上计划类资产(非高频开放)
                               UNION ALL
                               SELECT FINPROD_ID, VDATE, OPEN_VDATE, CALENDAR_ID
                               FROM (
                                        SELECT m.FINPROD_ID,
                                               M.VDATE,
                                               y.OPEN_VDATE,
                                               NVL(M.CALENDAR_ID, ''01'')                                             CALENDAR_ID,
                                               rank() over (PARTITION BY m.FINPROD_ID ORDER BY y.OPEN_VDATE asc) AS rowno
                                        from FIN_PRODUCT m
                                                 INNER JOIN FIN_PRODUCT_TYPE T1 ON m.FINPROD_ID = T1.FINPROD_ID
                                                 INNER JOIN FIN_PRODUCT_ADD FPA
                                                            ON m.FINPROD_ID = FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
                                                 INNER JOIN FIN_CASH_INFO n
                                                            ON n.FINPROD_ID = m.FINPROD_ID AND n.CASH_TYPE_1 = ''01'' AND
                                                               n.CASH_TYPE_2 = ''C01'' AND M.FINPROD_TYPE2 = ''F17''
                                                 INNER JOIN FIN_OPEN_PLAN_AST y ON y.CASH_ID = n.CASH_ID
                                        WHERE y.OPEN_VDATE >= date''${valDate}''
                                          AND T1.TYPE_11 != ''103''
                                        ORDER BY y.OPEN_VDATE ASC
                                    )
                               WHERE rowno = 1
                           ) m
                               INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND
                                                            MH.CDATE >= m.MDATE - 25 AND MH.IS_HOLIDAY = ''0''
                  )
             WHERE cdate >= vdate
               and vdate < = DATE ''${valDate}''
               and CDATE > DATE ''${valDate}''
         )
    GROUP BY FINPROD_ID
    HAVING count(1) > 10
    UNION
    -- 资产支持证券
    SELECT FPT.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F01'', ''F17'')
      AND FPT.TYPE_6 = ''B13''
    UNION
    -- 违约债券资产（不含同业存单）
    SELECT FPT.FINPROD_ID
    FROM FIN_PRODUCT FP
             INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
             INNER JOIN PTL_RECE_POSITION PRP ON FP.FINPROD_ID = PRP.FINPROD_ID AND PRP.HODING_TYPE = ''03''/*逾期*/
    WHERE FPT.TYPE_8 = ''F01''
      AND type_9 != ''01''
      AND PRP.PAY_DATE < PRP.CDATE
    UNION
    -- 流动性受限债券,非金融企业债务融资工具或其它
    SELECT FPT.FINPROD_ID
    FROM FIN_PRODUCT_TYPE FPT
    WHERE EXISTS(
                  SELECT 1
                  FROM RSK_SPECIFIC_ASSET_TYPE RSAT
                  WHERE RSAT.IS_FLOW_LIMITED = ''Y''
                    AND (
                          (REGEXP_INSTR(RSAT.TYPE_1 || ''_'' || RSAT.TYPE_2 || ''_'' || RSAT.TYPE_3 || ''_'' || RSAT.TYPE_4,
                                        FPT.TYPE_9 || ''_'' || FPT.TYPE_10 || ''_'' || FPT.TYPE_11 || ''_'' || FPT.TYPE_12) >=
                           1 AND RSAT.FINPROD_ID IS NULL)
                          OR FPT.FINPROD_ID = RSAT.FINPROD_ID)
              )
)
SELECT SUM(TRADE_AMT) RVALUE
FROM (
         SELECT NVL(SUM(NVL(M.DSC_COST_AMT, 0) + NVL(M.TDY_INTINCEXP, 0) + NVL(M.TDY_FLOAT_INGPL, 0) +
                        NVL(M.DELAY_PAY_AMT, 0) + NVL(M.TDY_LOSS_AMT, 0)), 0) TRADE_AMT
         FROM PTL_SEC_VALUTION M
                  INNER JOIN PROD N ON M.FINPROD_ID = N.FINPROD_ID
         WHERE M.CDATE = date ''${valDate}'' - 1
           AND M.PORTFOLIO_ID IN ${portfolioCode}
         UNION ALL
         SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T19'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T07'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T08'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T09'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T11'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T22'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T23'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T38'', n.TRADE_AMT)), 0)
                    -
                nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0) AS TRADE_AMT
         FROM TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                  INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
         WHERE m.PORTFOLIO_ID IN ${portfolioCode}
           and n.TRADE_STATUS = ''01''
           and NVL(n.IS_CANCEL, ''N'') = ''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND n.PS IN
               (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T52'', ''T22'', ''T23'', ''T39'', ''T38'')
         UNION ALL
         SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''04'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T93'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T95'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T96'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T19'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T07'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T08'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T09'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T11'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T22'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T23'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T38'', n.TRADE_AMT)), 0)
                    -
                nvl(sum(decode(n.PS, ''T39'', n.TRADE_AMT)), 0) AS TRADE_AMT
         FROM TRD_PRODUCT_TEMP_DEAL_ADD m
                  INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID
                  INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
         WHERE m.PORTFOLIO_ID IN ${portfolioCode}
           and n.TRADE_STATUS = ''01''
           and NVL(n.IS_CANCEL, ''N'') = ''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND n.PS IN
               (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T07'', ''T08'', ''T09'', ''T11'', ''T52'', ''T22'', ''T23'', ''T39'', ''T38'', ''04'',
                ''T93'', ''T95'', ''T96'')
         UNION ALL
         SELECT nvl(sum(decode(n.PS, ''T01'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T04'', n.TRADE_AMT)), 0)
                    + nvl(sum(decode(n.PS, ''T52'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0)
                    - nvl(sum(decode(n.PS, ''T03'', n.TRADE_AMT)), 0)
                    -
                nvl(sum(decode(n.PS, ''T19'', n.TRADE_AMT)), 0) AS TRADE_AMT
         FROM WFL_TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                  INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE m.PORTFOLIO_ID IN ${portfolioCode}
           and n.TRADE_STATUS = ''01''
           and NVL(n.IS_CANCEL, ''N'') = ''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T19'', ''T52'')
         UNION ALL
         SELECT nvl(sum(decode(n.PS, ''T02'', n.TRADE_AMT)), 0) AS TRADE_AMT
         FROM WFL_TRD_PRODUCT_DEAL_ADD m
                  INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS != ''03''
                  INNER JOIN PROD t2 ON t2.FINPROD_ID = n.FINPROD_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE n.counter_id IN ${portfolioCode}
           and n.TRADE_STATUS = ''01''
           and NVL(n.IS_CANCEL, ''N'') = ''N''
           AND n.TRADE_DATE = DATE ''${valDate}''
           AND n.PS = ''T02''
           AND EXISTS(SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
         union all
--可转债网下申购没有确认
         SELECT nvl(sum(decode(T.PS, ''T50'', decode(T.TRADE_STATUS, ''03'', T.prin_amt * -1, T.prin_amt))), 0) TRADE_AMT
         FROM TRD_PRODUCT_DEAL T
                  INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
                  INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.PS = ''T50''
           AND T.FINPROD_TYPE2 = ''F01''
           AND T.TRADE_STATUS = ''01''
           and T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <= date''${valDate}''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND NOT EXISTS(
             -- 没有后续申购确认交易
                 SELECT 1
                 FROM TRD_PRODUCT_DEAL TRD
                 WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID
                   AND TRD.TRADE_STATUS = ''01''
                   AND NOT EXISTS(
                         SELECT 1
                         FROM TRD_PRODUCT_DEAL TRD1
                         WHERE TRD.TRADE_ID = TRD1.P_TRADE_ID
                           AND TRD1.TRADE_STATUS = ''03''
                     ))
           AND NOT EXISTS(
             ----没有流程中申购确认
                 SELECT 1
                 FROM WFL_TRD_PRODUCT_DEAL TRD
                          INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
                          INNER JOIN SYS_WFL_BUSINESS_LIST swbl
                                     on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                 WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID
             )
         union all
         ---可转债网下申购流程中
         SELECT NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT
         FROM WFL_TRD_PRODUCT_DEAL T
                  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.PS = ''T50''
           AND T.FINPROD_TYPE2 = ''F01''
           AND T.TRADE_DATE = date''${valDate}''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
         union all
         --可转债申购确认交易
         SELECT NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT
         FROM TRD_PRODUCT_DEAL T
                  INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
         WHERE T.TRADE_STATUS = ''01''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.TRADE_DATE = date''${valDate}''
           AND T.PS = ''T05''
           AND T.FINPROD_TYPE2 = ''F01''
           AND T.IS_CANCEL = ''N''

         union all
         --可转债申购确认交易(流程中)
         SELECT NVL(SUM(NVL(T.prin_amt, 0)), 0) TRADE_AMT
         FROM WFL_TRD_PRODUCT_DEAL T
                  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.PS = ''T05''
           AND T.TRADE_DATE = date''${valDate}''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.FINPROD_TYPE2 = ''F01''

         union all
         ---债转股的股票
         SELECT SUM(Nvl(T.prin_amt, 0)) TRADE_AMT
         FROM TRD_FIN_PRODUCT_BARTER T
                  INNER JOIN TRD_PRODUCT_DEAL T3 ON T.TRADE_ID = T3.TRADE_ID
                  INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
         WHERE T1.PORTFOLIO_ID in ${portfolioCode}
           AND T3.PS = ''T49''
           AND T.FINPROD_TYPE = ''F10''
           AND T3.TRADE_STATUS = ''01''
           AND T3.IS_CANCEL = ''N''
           AND T3.TRADE_DATE = DATE''${valDate}''
           AND T1.PORTFOLIO_ID in ${portfolioCode}

         UNION ALL
--11.债券转托管申购没有做确认
         SELECT SUM(nvl(T4.prin_amt, 0)) TRADE_AMT
         FROM TRD_FIN_PRODUCT_BARTER T4
                  INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
                  INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
                  INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.PS = ''Z01''
           AND T2.FINPROD_TYPE2 = ''F01''
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND NOT EXISTS(
             -- 没有后续申购确认交易
                 SELECT 1
                 FROM TRD_PRODUCT_DEAL TRD
                 WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID
                   AND TRD.TRADE_STATUS = ''01''
                   AND TRD.IS_CANCEL = ''N''
             )
           AND NOT EXISTS(SELECT 1
                          FROM WFL_TRD_PRODUCT_DEAL TRD
                                   INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
                                   INNER JOIN SYS_WFL_BUSINESS_LIST swbl
                                              on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                          WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID
                            AND TRD.TRADE_STATUS = ''01''
                            AND TRD.IS_CANCEL = ''N''
             )

         union all
         --12.债券转托管有申购确认交易
         SELECT SUM(NVL(T4.prin_amt, 0)) TRADE_AMT
         FROM TRD_FIN_PRODUCT_BARTER T4
                  INNER JOIN TRD_PRODUCT_DEAL T ON T.trade_id = T4.FINPROD_ID
                  INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T4.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
         WHERE T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE = DATE''${valDate}''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.PS = ''Z02''
           AND T2.FINPROD_TYPE2 = ''F01''

         union all
         --13.债券转托管申购流程中
         SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT
         FROM WFL_TRD_FIN_PRODUCT_BARTER T
                  INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
                  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE T3.PORTFOLIO_ID in ${portfolioCode}
           AND T1.PS = ''Z01''
           AND T1.IS_CANCEL = ''N''
           AND T1.TRADE_DATE = DATE''${valDate}''
           AND T2.FINPROD_TYPE2 = ''F01''

         union all
         --14.债券转托管申购确认交易(流程中)
         SELECT SUM(NVL(T.prin_amt, 0)) TRADE_AMT
         FROM WFL_TRD_FIN_PRODUCT_BARTER T
                  INNER JOIN WFL_TRD_PRODUCT_DEAL T1 ON T.trade_id = T1.trade_id
                  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE T3.PORTFOLIO_ID in ${portfolioCode}
           AND T1.PS = ''Z02''
           AND T1.IS_CANCEL = ''N''
           AND T1.TRADE_DATE = DATE''${valDate}''
           AND T2.FINPROD_TYPE2 = ''F01''


         UNION ALL
         --11.债券转托管（赎回没有做确认--转出方
         SELECT SUM(nvl(T.PRIN_AMT * -1, 0)) TRADE_AMT
         FROM TRD_PRODUCT_DEAL T
                  INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
                  INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.PS = ''Z01''
           AND T2.FINPROD_TYPE2 = ''F01''
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND NOT EXISTS(
             -- 没有后续申购确认交易
                 SELECT 1
                 FROM TRD_PRODUCT_DEAL TRD
                 WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID
                   AND TRD.TRADE_STATUS = ''01''
                   AND TRD.IS_CANCEL = ''N''
             )
           AND NOT EXISTS(SELECT 1
                          FROM WFL_TRD_PRODUCT_DEAL TRD
                                   INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD TRDA ON TRD.trade_id = TRDA.trade_id
                                   INNER JOIN SYS_WFL_BUSINESS_LIST swbl
                                              on TRDA.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                          WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID
                            AND TRD.TRADE_STATUS = ''01''
                            AND TRD.IS_CANCEL = ''N''
             )

         union all
         --12.债券转托管：有赎回确认交易--转出方
         SELECT SUM(NVL(T.prin_amt * -1, 0)) TRADE_AMT
         FROM TRD_PRODUCT_DEAL T
                  INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
         WHERE T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND T.TRADE_DATE = DATE''${valDate}''
           AND T.PS = ''Z02''
           AND T2.FINPROD_TYPE2 = ''F01''

         union all
         --13.债券转托管：赎回流程中--转出方
         SELECT SUM(NVL(T1.prin_amt * -1, 0)) TRADE_AMT
         FROM WFL_TRD_PRODUCT_DEAL T1
                  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T1.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE T3.PORTFOLIO_ID in ${portfolioCode}
           AND T1.PS = ''Z01''
           AND T1.IS_CANCEL = ''N''
           AND T1.TRADE_DATE = DATE''${valDate}''
           AND T2.FINPROD_TYPE2 = ''F01''

         union all
         --14.债券转托管：赎回确认交易(流程中)--转出方
         SELECT SUM(NVL(T1.prin_amt, 0)) TRADE_AMT
         FROM WFL_TRD_PRODUCT_DEAL T1
                  INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T3 ON T3.TRADE_ID = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T1.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
                  INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         WHERE T3.PORTFOLIO_ID in ${portfolioCode}
           AND T1.PS = ''Z02''
           AND T1.IS_CANCEL = ''N''
           AND T1.TRADE_DATE = DATE''${valDate}''
           AND T2.FINPROD_TYPE2 = ''F01''

         UNION ALL
         --场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
         SELECT NVL(SUM(case when ps IN (''T04'') then nvl(T.TRADE_AMT, 0) else nvl(-1 * T.TRADE_AMT, 0) end),
                    0) TRADE_AMT
         FROM TRD_PRODUCT_DEAL T
                  INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
                  INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH = 0
         WHERE T.PS IN (''T04'', ''T06'')
           AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6 = ''F01''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND NOT EXISTS(
             -- 没有后续申购确认交易
                 SELECT 1
                 FROM TRD_PRODUCT_DEAL TRD
                 WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
             )

         UNION ALL
         --场外非货币基金、净值资管计划申购申请未确认的交易金额(+)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（-）
         SELECT NVL(SUM(case
                            when ps IN (''T06'') and T.TRADE_AMT != 0 then nvl(-1 * T.TRADE_AMT, 0)
                            when ps IN (''T06'') and (T.TRADE_AMT = 0 or T.TRADE_AMT is null) then
                                    nvl(T.SHARE_AMT * -1, 0) * (select NVL(fvpp.VALUATION, 1)
                                                                from FIN_PRODUCT_VALUATION fvpp
                                                                         inner JOIN (select fvp.FINPROD_ID, MAX(fvp.VAL_DATE) VAL_DATE
                                                                                     from FIN_PRODUCT_VALUATION fvp
                                                                                     group by fvp.FINPROD_ID) hq
                                                                                    on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE
                                                                where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
                            else nvl(T.TRADE_AMT, 0) end), 0) TRADE_AMT
         FROM TRD_PRODUCT_DEAL T
                  INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                  INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                  INNER JOIN PROD PD ON T2.FINPROD_ID = PD.FINPROD_ID
                  INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH = 0
         WHERE T.PS IN (''T04'', ''T06'')
           AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6 = ''F02''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE <= DATE''${valDate}''
           AND T1.PORTFOLIO_ID in ${portfolioCode}
           AND NOT EXISTS(
             -- 没有后续申购确认交易
                 SELECT 1
                 FROM TRD_PRODUCT_DEAL TRD
                 WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
             )
     )';

----事后(日终)
V_CONTENT_05 CLOB := 'WITH FIN_OPEN_PLAN_AST AS  (
  /**申购开放(这里三个union是为了取的顺序赎回优先,无赎回则取申购)**/
   SELECT
   CASH_ID,FINPROD_ID , OPEN_VDATE
   FROM
    FIN_OPEN_PLAN fop
   WHERE
    fop.FINPROD_ID LIKE ''F17_%'' AND fop.OPEN_TYPE = ''01'' AND NOT EXISTS (
    SELECT 1 FROM FIN_OPEN_PLAN fop2 WHERE fop2.OPEN_TYPE = ''02'' AND fop.CASH_ID =fop2.CASH_ID
    )
  /**赎回开放**/
  UNION ALL
   SELECT
   CASH_ID, FINPROD_ID ,OPEN_VDATE
   FROM
    FIN_OPEN_PLAN fop
   WHERE
    fop.FINPROD_ID LIKE ''F17_%'' AND fop.OPEN_TYPE = ''02''
  /**申赎同频**/
  UNION ALL
  SELECT
   CASH_ID,fop.FINPROD_ID , OPEN_VDATE
  FROM
   FIN_OPEN_PLAN fop
  WHERE
   fop.FINPROD_ID LIKE ''F17_%'' AND fop.OPEN_TYPE = ''03''
)
,PROD AS (
    -- 资产到期向前第10个交易日
    SELECT FINPROD_ID FROM (
        SELECT FINPROD_ID, MDATE, CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE asc) RN FROM (
            SELECT m.FINPROD_ID,  m.VDATE,m.MDATE, MH.CDATE, ROW_NUMBER() OVER(PARTITION BY FINPROD_ID ORDER BY CDATE desc) ROWNO FROM (
                -- 逆回购
                SELECT FTP.FINPROD_ID,FTP.VDATE,FTP.MDATE,NVL(FTP.CALENDAR_ID,''01'') CALENDAR_ID FROM FIN_TRADE_PRODUCT FTP
                INNER JOIN FIN_PRODUCT_TYPE T1 ON FTP.FINPROD_ID = T1.FINPROD_ID
                WHERE FTP.MDATE IS NOT NULL AND T1.TYPE_8 IN (''F07'',''F10'') AND TYPE_7 = ''04'' AND FTP.MDATE >= DATE ''${valDate}'' - 1
        UNION ALL
        -- 银行定期存款（不含活期存款和大额存单以及同业借款）
        SELECT FPT.FINPROD_ID,FPT.VDATE,MDATE,NVL(FPT.CALENDAR_ID,''01'') CALENDAR_ID
        FROM FIN_PRODUCT FPT
        INNER JOIN FIN_PRODUCT_TYPE T1 ON FPT.FINPROD_ID = T1.FINPROD_ID
        INNER JOIN FIN_PRODUCT_ADD FPA ON FPT.FINPROD_ID = FPA.FINPROD_ID
        -- 这里是有条件可提前支取
        WHERE (FPA.IS_INADVANCE IS NULL OR FPA.IS_INADVANCE IN (''01'',''02''))
        AND T1.TYPE_8 = ''F05'' AND T1.TYPE_6 != ''D03'' AND T1.TYPE_11 not in (''029'',''030'',''031'')
          --距可赎回日在10个交易日以上计划类资产(非高频开放)
          UNION ALL
          SELECT FINPROD_ID,VDATE,OPEN_VDATE,CALENDAR_ID FROM (
            SELECT m.FINPROD_ID,M.VDATE,y.OPEN_VDATE,NVL(M.CALENDAR_ID,''01'') CALENDAR_ID ,rank() over(PARTITION BY m.FINPROD_ID ORDER BY y.OPEN_VDATE asc) AS rowno
            from FIN_PRODUCT m
            INNER JOIN FIN_PRODUCT_TYPE T1 ON m.FINPROD_ID = T1.FINPROD_ID
            INNER JOIN FIN_PRODUCT_ADD FPA ON m.FINPROD_ID=FPA.FINPROD_ID and FPA.IS_OPEN_EVERYDAY = ''N''
            INNER JOIN  FIN_CASH_INFO n ON n.FINPROD_ID=m.FINPROD_ID  AND n.CASH_TYPE_1=''01'' AND n.CASH_TYPE_2=''C01'' AND M.FINPROD_TYPE2 =''F17''
            INNER JOIN  FIN_OPEN_PLAN_AST y ON y.CASH_ID=n.CASH_ID
            WHERE y.OPEN_VDATE>=date''${valDate}'' AND T1.TYPE_11!=''103'' ORDER BY y.OPEN_VDATE ASC
          ) WHERE  rowno=1
            ) m
            INNER JOIN MST_HOLIDAY MH ON MH.CALENDAR_ID = m.CALENDAR_ID AND m.MDATE >= MH.CDATE AND MH.CDATE>=m.MDATE-25  AND MH.IS_HOLIDAY = ''0''
        ) WHERE cdate>=vdate  and vdate< =DATE ''${valDate}'' and CDATE > DATE ''${valDate}''
    ) GROUP BY FINPROD_ID HAVING count(1)>10
    UNION
    -- 资产支持证券
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    WHERE FPT.TYPE_8 IN (''F01'',''F17'')  AND FPT.TYPE_6 = ''B13''
    UNION
    -- 违约债券资产（不含同业存单）
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT FP
    INNER JOIN FIN_PRODUCT_TYPE FPT ON FPT.FINPROD_ID = FP.FINPROD_ID
    INNER JOIN PTL_RECE_POSITION PRP ON FP.FINPROD_ID = PRP.FINPROD_ID AND PRP.HODING_TYPE = ''03''/*逾期*/
    WHERE FPT.TYPE_8=''F01'' AND type_9!=''01'' AND PRP.PAY_DATE<PRP.CDATE
  UNION
   -- 流动性受限债券,非金融企业债务融资工具或其它
    SELECT FPT.FINPROD_ID FROM FIN_PRODUCT_TYPE FPT
    WHERE EXISTS (
        SELECT 1 FROM RSK_SPECIFIC_ASSET_TYPE RSAT WHERE RSAT.IS_FLOW_LIMITED = ''Y'' AND (
        (REGEXP_INSTR(RSAT.TYPE_1||''_''||RSAT.TYPE_2||''_''||RSAT.TYPE_3||''_''||RSAT.TYPE_4 ,
            FPT.TYPE_9||''_''||FPT.TYPE_10||''_''||FPT.TYPE_11||''_''||FPT.TYPE_12) >= 1 AND RSAT.FINPROD_ID IS NULL)
        OR FPT.FINPROD_ID = RSAT.FINPROD_ID)
    )
)
select sum(nvl(targetData.TRADE_AMT, 0)) as RVALUE from (
        SELECT
            NVL(SUM(NVL(M.DSC_COST_AMT,0)+NVL(M.TDY_INTINCEXP,0)+NVL(M.TDY_FLOAT_INGPL,0)+NVL(M.DELAY_PAY_AMT,0)+NVL(M.TDY_LOSS_AMT,0)),0) TRADE_AMT
        FROM PTL_SEC_VALUTION M
        INNER JOIN PROD N ON M.FINPROD_ID = N.FINPROD_ID
        WHERE M.CDATE = date''${valDate}'' AND M.PORTFOLIO_ID IN ${portfolioCode}
        union all
       select tmp.TRADE_AMT from 
             (
              --可转债网下申购未确认（+）
                 SELECT nvl(T.TRADE_AMT,0) TRADE_AMT, T.FINPROD_ID
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
                 SELECT  nvl(T.TRADE_AMT*-1,0) TRADE_AMT , T.FINPROD_ID
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
                 SELECT  nvl(T.PRIN_AMT,0) TRADE_AMT , T.FINPROD_ID
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
        )tmp 
               INNER JOIN FIN_PRODUCT T2 on tmp.FINPROD_ID = T2.FINPROD_ID
               INNER JOIN PROD N ON T2.FINPROD_ID = N.FINPROD_ID      
               INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0  AND FPT.TYPE_8 IN (''F01'')
        UNION ALL
                --场外货币基金、货币资管计划申购申请未确认的交易金额(+)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（-）
                 SELECT case when ps IN (''T04'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end  TRADE_AMT
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN PROD N ON T2.FINPROD_ID = N.FINPROD_ID     
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
                 WHERE 
                    T.PS  IN (''T04'',''T06'')
                   AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))
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
                 SELECT case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(-1*T.TRADE_AMT,0)
                  when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT*-1,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
                 inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp where fvp.VAL_DATE<=date''${valDate}'' group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
                 where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
                  else nvl(T.TRADE_AMT,0) end TRADE_AMT
                  FROM TRD_PRODUCT_DEAL T
                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
                 INNER JOIN PROD N ON T2.FINPROD_ID = N.FINPROD_ID          
                 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
                 WHERE 
                    T.PS  IN (''T04'',''T06'')
                   AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
                   AND T.TRADE_STATUS = ''01''
                   AND T.IS_CANCEL = ''N''
                   AND T1.PORTFOLIO_ID in ${portfolioCode} 
                   AND T.TRADE_DATE <= DATE''${valDate}''
                   AND NOT EXISTS (
                      -- 没有后续申购确认交易
                      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
                )
  
   ) targetData';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'LDXSXZCCCSZ' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'LDXSXZCCCSZ' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'LDXSXZCCCSZ' and PARA_GRANULARITY = '05';
END;
/
