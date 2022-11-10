/**风险指标参数: 产品总资产(穿透后) **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='CPZZCCTH';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('CPZZCCTH','产品总资产(穿透后)', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  'SQL' ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='CPZZCCTH';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZZCCTH','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZZCCTH','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZZCCTH','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZZCCTH','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZZCCTH','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'WITH  temp  as(
        SELECT M.PORTFOLIO_ID,NVL(M.MARKET_VALUE,0)+nvl(TDY_INTINCEXP, 0) AMOUNT
          FROM PTL_SEC_PIERCE_VALUTION M
          LEFT JOIN FIN_TRADE_PRODUCT  FTP ON M.FINPROD_ID = FTP.FINPROD_ID and FTP.BRANCH = 0
          LEFT JOIN FIN_PRODUCT_TYPE FPT ON M.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH = 0
          WHERE  (FTP.FINPROD_TYPE2 not in (''F02'',''F04'') or FTP.FINPROD_TYPE2 is null) AND M.CDATE = date''${valDate}''-1
   )
         SELECT PORTFOLIO_ID RKEY,sum(AMOUNT) RVALUE FROM temp
         group by PORTFOLIO_ID
         union all
         select m.PTL_BAG_ID as RKEY,
             (
              SELECT nvl(sum(AMOUNT),0) FROM (
               SELECT PORTFOLIO_ID,AMOUNT FROM temp
               ) t1 where t1.PORTFOLIO_ID in (select t1.PORTFOLIO_ID from RSK_PORTFOLIO_PACKAGE_DTL t1
               inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}'' where t1.PTL_BAG_ID=m.PTL_BAG_ID)
             ) AS RVALUE
         from RSK_PORTFOLIO_PACKAGE m';

----事后(日间)
V_CONTENT_04 CLOB := 'select sum(amount) AS RVALUE from
  (
     select sum(amount) as amount
     from
     (
     SELECT
       DISTINCT t2.PORTFOLIO_ID ,
       mujiData.mujiAmt  as AMOUNT
       from  PTL_PORTFOLIO t2
       inner join
       (
       select t1.PORTFOLIO_ID, sum(t1.TRADE_AMT) as mujiAmt from TRD_FIN_PRODUCT_DEAL_TOTAL t1
       WHERE  t1.PS in (''C1'', ''C2'')   and t1.SETTLE_DATE <= date''${valDate}'' AND t1.TRADE_STATUS=''01'' and nvl(t1.IS_CANCEL,''N'') =''N''  AND t1.TRADE_DATE  < date''${valDate}''
       group by t1.PORTFOLIO_ID
       ) mujiData
       on t2.PORTFOLIO_ID = mujiData.PORTFOLIO_ID
       where  t2.VDATE=date''${valDate}'' and t2.PORTFOLIO_ID in ${portfolioCode}
     UNION ALL

    SELECT DISTINCT M.PORTFOLIO_ID RKEY,NVL(M.MARKET_VALUE,0)+nvl(TDY_INTINCEXP, 0) AMOUNT
          FROM PTL_SEC_PIERCE_VALUTION M
          LEFT JOIN FIN_TRADE_PRODUCT  FTP ON M.FINPROD_ID = FTP.FINPROD_ID and FTP.BRANCH = 0
          LEFT JOIN FIN_PRODUCT_TYPE FPT ON M.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH = 0
          WHERE PORTFOLIO_ID IN ${portfolioCode} AND CDATE = date''${valDate}''-1 and ( FTP.FINPROD_TYPE2 not in (''F02'',''F04'') or FTP.FINPROD_TYPE2 is null)

     )
    union all
    --1.产品端交易表
    select
     CASE  WHEN n.PS IN(''C1'',''C2'',''C3'',''C15'',''T98'') THEN NVL(n.TRADE_AMT,0)
     ELSE  NVL(-1*n.TRADE_AMT,0)
     END amount
    from TRD_PRODUCT_DEAL_ADD m
    inner join TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
    where m.PORTFOLIO_ID in ${portfolioCode}  and
    n.TRADE_DATE=date''${valDate}''
    AND  n.PS IN (''C1'', ''C2'', ''C3'', ''C4'', ''C5'', ''C6'', ''C7'', ''C9'', ''C14'', ''C15'', ''C16'', ''C17'',''C18'', ''C19'',''C20'',''T98'',''T99'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''

     union ALL
    --2.回购交易
    select
     CASE  WHEN n.PS =''T01'' THEN NVL(n.TRADE_AMT,0)
     ELSE  NVL(-1*n.TRADE_AMT,0)
     END amount
     from TRD_PRODUCT_DEAL_ADD m
    inner join TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.FINPROD_TYPE2 in (''F02'',''F08'',''F25'',''F04'')
    where  m.PORTFOLIO_ID in ${portfolioCode} and
    n.TRADE_DATE=date''${valDate}'' and n.PS in (''T01'',''T07'',''T54'',''T55'',''T56'',''T22'',''T23'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''

    union all
  --3.日间交易表（场内交易：回购）
    select
     CASE  WHEN n.PS IN(''T01'',''03'') THEN NVL(n.TRADE_AMT,0)
     ELSE  NVL(-1*n.TRADE_AMT,0)
     END amount
    from TRD_PRODUCT_TEMP_DEAL_ADD m
    inner join TRD_PRODUCT_TEMP_DEAL n on m.TRADE_ID=n.TRADE_ID and n.FINPROD_TYPE2 in (''F02'',''F08'',''F25'',''F04'')
    where  m.PORTFOLIO_ID in ${portfolioCode} and
    n.TRADE_DATE=date''${valDate}'' and n.PS in (''03'',''T01'',''T07'',''T22'',''T23'',''T54'',''T55'',''T56'',''T93'',''T95'',''T96'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''

     union ALL
  --4.回购交易流程表
    select
     NVL(n.TRADE_AMT,0) amount
     from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.FINPROD_TYPE2 in (''F02'',''F08'',''F25'',''F04'')
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    where  m.PORTFOLIO_ID in ${portfolioCode} and
    n.TRADE_DATE=date''${valDate}'' and n.PS IN(''T01'',''03'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''
  )';

----事后(日终)
V_CONTENT_05 CLOB := 'SELECT nvl(sum(NVL(M.MARKET_VALUE,0)+nvl(TDY_INTINCEXP, 0)),0) RVALUE
  FROM PTL_SEC_PIERCE_VALUTION M
  LEFT JOIN FIN_TRADE_PRODUCT  FTP ON M.FINPROD_ID = FTP.FINPROD_ID and FTP.BRANCH = 0
  LEFT JOIN FIN_PRODUCT_TYPE FPT ON M.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH = 0
  WHERE PORTFOLIO_ID IN ${portfolioCode} AND CDATE = date''${valDate}'' and ( FTP.FINPROD_TYPE2 not in (''F02'',''F04'') or FTP.FINPROD_TYPE2 is null)';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'CPZZCCTH' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'CPZZCCTH' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'CPZZCCTH' and PARA_GRANULARITY = '05';
END;
/
