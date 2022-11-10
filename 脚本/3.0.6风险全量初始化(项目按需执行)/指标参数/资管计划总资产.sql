/**风险指标参数: 资管计划总资产 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='ZGJHZZC';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('ZGJHZZC','资管计划总资产', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='ZGJHZZC';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('ZGJHZZC','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('ZGJHZZC','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('ZGJHZZC','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('ZGJHZZC','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('ZGJHZZC','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'WITH  temp  as(
    SELECT
       DISTINCT t2.PORTFOLIO_ID ,
       mujiData.mujiAmt  as AMOUNT
       from  PTL_PORTFOLIO t2
       left join
       (
       select t2.FINPROD_MARKET_ID, sum(CASE  WHEN t1.PS IN(''T01'',''T04'') THEN NVL(t1.TRADE_AMT,0)
     ELSE  NVL(-1*t1.TRADE_AMT,0)
     END) as mujiAmt from TRD_PRODUCT_DEAL t1
      LEFT JOIN FIN_PRODUCT t2 ON t1.FINPROD_ID = t2.FINPROD_ID
       WHERE
           t1.PS in (''T01'',''T02'',''T04'',''T24'') AND t1.FINPROD_TYPE2 =''F17''
           and t1.SETTLE_DATE = date''${valDate}'' and t1.TRADE_STATUS=''01'' and nvl(t1.IS_CANCEL,''N'') =''N''
       group by t2.FINPROD_MARKET_ID
       ) mujiData
       on t2.PORTFOLIO_ID = mujiData.FINPROD_MARKET_ID
       where  mujiData.mujiAmt>0
   UNION ALL
    SELECT
  rvsas.PTL_ID PORTFOLIO_ID,
  sum(rvsas.SMV) AMOUNT
    FROM RSK_VALTBL_SUM_ASSET_SUB rvsas
    LEFT JOIN FIN_PRODUCT fp ON fp.FINPROD_MARKET_ID = rvsas.PTL_ID
    WHERE
      rvsas.SUB_CODE = ''604''
    AND rvsas.VAL_DATE = date''${valDate}''-1
    AND fp.FINPROD_TYPE2 =''F17''
    GROUP BY rvsas.PTL_ID
   )
 SELECT PORTFOLIO_ID RKEY,sum(AMOUNT) RVALUE FROM temp
 group by PORTFOLIO_ID
  union all
    select m.PTL_BAG_ID as RKEY,
     (
      SELECT sum(AMOUNT) FROM (
       SELECT PORTFOLIO_ID,AMOUNT FROM temp
       ) t1 where t1.PORTFOLIO_ID in (select t1.PORTFOLIO_ID from RSK_PORTFOLIO_PACKAGE_DTL t1
       inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}'' where t1.PTL_BAG_ID=m.PTL_BAG_ID)
     ) AS RVALUE
     from RSK_PORTFOLIO_PACKAGE m';

----事后(日间)
V_CONTENT_04 CLOB := 'select nvl(sum(amount),0) AS amount from
  (
     select sum(amount) as amount
     from
     (
    SELECT
	    rvsas.PTL_ID PORTFOLIO_ID,
	    sum(rvsas.SMV) AMOUNT
    FROM RSK_VALTBL_SUM_ASSET_SUB rvsas
    LEFT JOIN FIN_PRODUCT fp ON fp.FINPROD_MARKET_ID = rvsas.PTL_ID
    WHERE rvsas.SUB_CODE = ''604''
    AND rvsas.VAL_DATE = date''${valDate}''-1
    AND fp.FINPROD_TYPE2 =''F17'' AND fp.FINPROD_MARKET_ID  in ${portfolioCode}
    GROUP BY rvsas.PTL_ID
        )
    union all
    --1.计划类资产交易
    select
     CASE  WHEN m.PS IN(''T01'',''T04'') THEN NVL(m.TRADE_AMT,0)
     ELSE  NVL(-1*m.TRADE_AMT,0)
     END amount
    from TRD_PRODUCT_DEAL m
    LEFT JOIN FIN_PRODUCT n ON m.FINPROD_ID = n.FINPROD_ID
    where n.FINPROD_MARKET_ID in ${portfolioCode}  and
    m.TRADE_DATE=date''${valDate}''
    AND  m.PS IN (''T01'',''T02'',''T04'',''T24'')/*买入卖出申购赎回到账*/
    AND m.TRADE_STATUS = ''01''
    AND m.IS_CANCEL = ''N''
    union all
    --2.自主专户委托正回购指令
    SELECT sum(nvl(m.ENTRUST_AMT,0)) amount
  FROM ETR_ENTRUST_WW m
  WHERE m.PS=''03''
  AND m.PORTFOLIO_ID in ${portfolioCode}
  AND m.ENTRUST_DATE = date''${valDate}''
  AND m.ENTRUST_STATUS !=''06''
  )';

----事后(日终)
V_CONTENT_05 CLOB := 'select nvl(sum(amount),0) as amount
  from (SELECT
	    rvsas.PTL_ID PORTFOLIO_ID,
	    sum(rvsas.SMV) AMOUNT
    FROM RSK_VALTBL_SUM_ASSET_SUB rvsas
    LEFT JOIN FIN_PRODUCT fp ON fp.FINPROD_MARKET_ID = rvsas.PTL_ID
    WHERE rvsas.SUB_CODE = ''604''
    AND rvsas.VAL_DATE = date''${valDate}''
    AND fp.FINPROD_TYPE2 =''F17'' AND fp.FINPROD_MARKET_ID  in ${portfolioCode}
    GROUP BY rvsas.PTL_ID)';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'ZGJHZZC' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'ZGJHZZC' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'ZGJHZZC' and PARA_GRANULARITY = '05';
END;
/
