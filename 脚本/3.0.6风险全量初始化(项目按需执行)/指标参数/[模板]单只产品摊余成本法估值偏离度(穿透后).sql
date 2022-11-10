/**风险指标参数: [模板]单只产品摊余成本法估值偏离度(穿透后) **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='MBDZCPTYCBFGZPLDCTH';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('MBDZCPTYCBFGZPLDCTH','[模板]单只产品摊余成本法估值偏离度(穿透后)', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'N' , 'String' ,  null ,  'PLUGIN'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='MBDZCPTYCBFGZPLDCTH';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('MBDZCPTYCBFGZPLDCTH','01', 'SQL','select 1 from dual',  null , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('MBDZCPTYCBFGZPLDCTH','02', 'SQL','select 1 from dual',  null , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('MBDZCPTYCBFGZPLDCTH','03', 'SQL','select 1 from dual',  null , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('MBDZCPTYCBFGZPLDCTH','04', 'SQL','select 1 from dual',  null , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('MBDZCPTYCBFGZPLDCTH','05', 'SQL','select 1 from dual',  null , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'WITH BOND AS (
  SELECT SEC_ID,nvl(PRICE,100) PRICE,VALUE_SOURCE FROM (SELECT SEC_ID,PRICE,VALUE_SOURCE, RANK() OVER(PARTITION BY SEC_ID ORDER BY VALUE_DATE DESC ) AS rowno
    FROM MST_BOND_VALUATION_INFO WHERE VALUE_DATE<=date''${valDate}'') WHERE rowno=1
), BONDGZ AS (   SELECT FINPROD_ID, CBJJ FROM
  (SELECT   M.FINPROD_ID, DECODE(tpd.UNIT_FPRICE , NULL, DECODE(SHARE_AMT, NULL, 100, 0, 100, tpd.TRADE_AMT /SHARE_AMT), tpd.UNIT_FPRICE ) AS CBJJ ,    RANK() OVER(PARTITION BY M.FINPROD_ID ORDER BY M.CDATE DESC ) AS rowno
  FROM    PTL_SEC_POSITION_FLOW M INNER JOIN FIN_PRODUCT_TYPE N ON    M.FINPROD_ID = N.FINPROD_ID
  INNER JOIN TRD_PRODUCT_DEAL tpd ON tpd.TOTLE_TRADE_ID = M.TRADE_ID  WHERE tpd.is_cancel = ''N'' and tpd.trade_status != ''03'' and
  CDATE = DATE ''${valDate}''   AND PORTFOLIO_ID IN ${portfolioCode}) WHERE rowno = 1
), temp as ( select SHARE_AMT,(DSC_COST_AMT+TDY_INTINCEXP) as DSC_COST_AMT,nvl(p.PRICE,100) AS market_price
                from  PTL_SEC_PIERCE_VALUTION  m inner join FIN_PRODUCT  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.FINPROD_TYPE2 =''F01''
        LEFT JOIN BOND p ON fpt.FINPROD_MARKET_ID = p.SEC_ID AND CASE WHEN fpt.TRADE_MARKET in(''03'',''04'') THEN ''CSD'' ELSE ''CBC'' END =p.VALUE_SOURCE
          WHERE m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''-1
      UNION ALL
        select SHARE_AMT,(DSC_COST_AMT+TDY_INTINCEXP) as DSC_COST_AMT,(DSC_COST_AMT+TDY_INTINCEXP)/DSC_COST_AMT AS market_price
                from  PTL_SEC_PIERCE_VALUTION  m inner join FIN_PRODUCT_TYPE  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.TYPE_8 in (''F05'',''F06'') AND fpt.BRANCH =0
          WHERE
           m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''-1
       UNION ALL
        select SHARE_AMT,(DSC_COST_AMT+TDY_INTINCEXP) as DSC_COST_AMT,(DSC_COST_AMT+TDY_INTINCEXP)/DSC_COST_AMT AS market_price
                from  PTL_SEC_PIERCE_VALUTION  m inner join FIN_TRADE_PRODUCT  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.FINPROD_TYPE2 in (''F03'',''F05'') AND fpt.BRANCH =0
          WHERE
           m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''-1  AND m.DSC_COST_AMT >0
        UNION ALL
         SELECT
           m.AMOUNT AS SHARE_AMT,s.CBJJ*m.AMOUNT AS DSC_COST_AMT,nvl(p.PRICE,100) AS market_price
                  from PTL_SEC_POSITION_FLOW  m
                inner join FIN_PRODUCT  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.FINPROD_TYPE2 =''F01''
        LEFT JOIN BOND p ON fpt.FINPROD_MARKET_ID = p.SEC_ID AND CASE WHEN fpt.TRADE_MARKET in(''03'',''04'') THEN ''CSD'' ELSE ''CBC'' END =p.VALUE_SOURCE
        LEFT JOIN BONDGZ s ON s.FINPROD_ID=fpt.FINPROD_ID
          WHERE
          m.INV_AIM IN (''AC'') AND m.HODING_TYPE IN (''01'',''03'')
          AND m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''
        UNION ALL
         SELECT
           m.AMOUNT AS SHARE_AMT,m.AMOUNT AS  DSC_COST_AMT,1 AS market_price
                  from PTL_SEC_POSITION_FLOW  m
              inner join FIN_PRODUCT_TYPE  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.TYPE_8 in (''F05'',''F06'') AND fpt.BRANCH =0
          WHERE
           m.HODING_TYPE IN (''01'',''03'')
          AND m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''
       UNION ALL
           SELECT
           m.AMOUNT AS SHARE_AMT,0 AS DSC_COST_AMT,0 AS market_price
                  from PTL_SEC_POSITION_FLOW  m
              inner join FIN_TRADE_PRODUCT  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.FINPROD_TYPE2 in (''F03'',''F05'') AND fpt.BRANCH =0
          WHERE
           m.HODING_TYPE IN (''01'',''03'')
          AND m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''
      UNION ALL
               select nvl(sum(AMOUNT),0) AS SHARE_AMT,nvl(sum(AMOUNT),0) AS DSC_COST_AMT,1 AS market_price from PTL_CASH_POSITION
          where  PORTFOLIO_ID in ${portfolioCode} and HODING_TYPE=''03'' and CDATE =date''${valDate}'' AND
          ACCT_ID in ( SELECT ACCT_NO from PTL_CAPACCT_INFO where PORTFOLIO_ID in ${portfolioCode}  )
      UNION ALL
               select nvl(sum(AMOUNT),0) AS SHARE_AMT,nvl(sum(AMOUNT),0) AS DSC_COST_AMT,1 AS market_price from PTL_CASH_POSITION_FLOW
          where  PORTFOLIO_ID in ${portfolioCode} and HODING_TYPE=''01'' and CDATE =date''${valDate}'' AND
          ACCT_ID in ( SELECT ACCT_NO from PTL_CAPACCT_INFO where PORTFOLIO_ID in ${portfolioCode}  )
)
 SELECT
            SUM(NVL(m.DSC_COST_AMT, 0)) FENZI,
            SUM(NVL(m.SHARE_AMT, 0)*NVL(m.market_price, 100)) FENZITWO
        FROM
            temp m';

----事后(日间)
V_CONTENT_04 CLOB := 'WITH BOND AS (
  SELECT SEC_ID,nvl(PRICE,100) PRICE,VALUE_SOURCE FROM (SELECT SEC_ID,PRICE,VALUE_SOURCE, RANK() OVER(PARTITION BY SEC_ID ORDER BY VALUE_DATE DESC ) AS rowno
    FROM MST_BOND_VALUATION_INFO WHERE VALUE_DATE<=date''${valDate}'') WHERE rowno=1
), BONDGZ AS (   SELECT	FINPROD_ID,	TOTLE_TRADE_ID,CBJJ FROM
	(SELECT		M.FINPROD_ID, tpd.TOTLE_TRADE_ID, DECODE(tpd.UNIT_FPRICE , NULL, DECODE(SHARE_AMT, NULL, 100, 0, 100, tpd.TRADE_AMT /SHARE_AMT), tpd.UNIT_FPRICE ) AS CBJJ , 		RANK() OVER(PARTITION BY M.FINPROD_ID ORDER BY M.CDATE DESC ) AS rowno
	FROM		PTL_SEC_POSITION_FLOW M	INNER JOIN FIN_PRODUCT_TYPE N ON		M.FINPROD_ID = N.FINPROD_ID
	INNER JOIN TRD_PRODUCT_DEAL tpd ON tpd.TOTLE_TRADE_ID = M.TRADE_ID 	WHERE tpd.is_cancel = ''N'' and tpd.trade_status != ''03'' and
	CDATE = DATE ''${valDate}''		AND PORTFOLIO_ID IN ${portfolioCode}) WHERE	rowno = 1
), temp as ( select SHARE_AMT,(DSC_COST_AMT+TDY_INTINCEXP) as DSC_COST_AMT,nvl(p.PRICE,100) AS market_price
                from  PTL_SEC_PIERCE_VALUTION  m inner join FIN_PRODUCT  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.FINPROD_TYPE2 =''F01''
        LEFT JOIN BOND p ON fpt.FINPROD_MARKET_ID = p.SEC_ID AND CASE WHEN fpt.TRADE_MARKET in(''03'',''04'') THEN ''CSD'' ELSE ''CBC'' END =p.VALUE_SOURCE
          WHERE m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''-1
      UNION ALL
        select SHARE_AMT,(DSC_COST_AMT+TDY_INTINCEXP) as DSC_COST_AMT,(DSC_COST_AMT+TDY_INTINCEXP)/DSC_COST_AMT AS market_price
                from  PTL_SEC_PIERCE_VALUTION  m inner join FIN_PRODUCT_TYPE  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.TYPE_8 in (''F05'',''F06'') AND fpt.BRANCH =0
          WHERE
           m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''-1
       UNION ALL
        select SHARE_AMT,(DSC_COST_AMT+TDY_INTINCEXP) as DSC_COST_AMT,(DSC_COST_AMT+TDY_INTINCEXP)/DSC_COST_AMT AS market_price
                from  PTL_SEC_PIERCE_VALUTION  m inner join FIN_TRADE_PRODUCT  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.FINPROD_TYPE2 in (''F03'',''F05'') AND fpt.BRANCH =0
          WHERE
           m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''-1  AND m.DSC_COST_AMT >0
       UNION ALL
         SELECT
           m.AMOUNT AS SHARE_AMT,s.CBJJ*m.AMOUNT AS DSC_COST_AMT,nvl(p.PRICE,100) AS market_price
                  from PTL_SEC_POSITION_FLOW  m
                inner join FIN_PRODUCT  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.FINPROD_TYPE2 =''F01''
        LEFT JOIN BOND p ON fpt.FINPROD_MARKET_ID = p.SEC_ID AND CASE WHEN fpt.TRADE_MARKET in(''03'',''04'') THEN ''CSD'' ELSE ''CBC'' END =p.VALUE_SOURCE
        LEFT JOIN BONDGZ s ON s.FINPROD_ID=fpt.FINPROD_ID and s.TOTLE_TRADE_ID = m.TRADE_ID
          WHERE
          m.INV_AIM IN (''AC'') AND m.HODING_TYPE IN (''01'',''03'')
          AND m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''
        UNION ALL
         SELECT
           m.AMOUNT AS SHARE_AMT,m.AMOUNT AS  DSC_COST_AMT,1 AS market_price
                  from PTL_SEC_POSITION_FLOW  m
              inner join FIN_PRODUCT_TYPE  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.TYPE_8 in (''F05'',''F06'') AND fpt.BRANCH =0
          WHERE
           m.HODING_TYPE IN (''01'',''03'')
          AND m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''
       UNION ALL
           SELECT
           m.AMOUNT AS SHARE_AMT,0 AS DSC_COST_AMT,0 AS market_price
                  from PTL_SEC_POSITION_FLOW  m
              inner join FIN_TRADE_PRODUCT  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.FINPROD_TYPE2 in (''F03'',''F05'') AND fpt.BRANCH =0
          WHERE
           m.HODING_TYPE IN (''01'',''03'')
          AND m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''
      UNION ALL
               select nvl(sum(AMOUNT),0) AS SHARE_AMT,nvl(sum(AMOUNT),0) AS DSC_COST_AMT,1 AS market_price from PTL_CASH_POSITION
          where  PORTFOLIO_ID in ${portfolioCode} and HODING_TYPE=''03'' and CDATE =date''${valDate}'' AND
          ACCT_ID in ( SELECT ACCT_NO from PTL_CAPACCT_INFO where PORTFOLIO_ID in ${portfolioCode}  )
      UNION ALL
               select nvl(sum(AMOUNT),0) AS SHARE_AMT,nvl(sum(AMOUNT),0) AS DSC_COST_AMT,1 AS market_price from PTL_CASH_POSITION_FLOW
          where  PORTFOLIO_ID in ${portfolioCode} and HODING_TYPE=''01'' and CDATE =date''${valDate}'' AND
          ACCT_ID in ( SELECT ACCT_NO from PTL_CAPACCT_INFO where PORTFOLIO_ID in ${portfolioCode}  )
)
select DEV_AMT AS RVALUE FROM(SELECT
    decode(fenzitwo,NULL,0,0,0,ABS(fenzi-fenzitwo)/ABS(fenzitwo)) AS DEV_AMT
FROM
    (
        SELECT
            CASE
                WHEN SUM(NVL(m.SHARE_AMT, 0)) =0
                THEN 0
                ELSE SUM(NVL(m.DSC_COST_AMT, 0))/SUM(NVL(m.SHARE_AMT, 0))
            END fenzi,
            CASE
                WHEN SUM(NVL(m.SHARE_AMT, 0))=0
                THEN 0
                ELSE SUM(NVL(m.SHARE_AMT, 0)*NVL(m.market_price, 100))/SUM(NVL(m.SHARE_AMT, 0))
            END fenzitwo
        FROM
            temp m))';

----事后(日终)
V_CONTENT_05 CLOB := 'WITH BOND AS (
  SELECT SEC_ID,nvl(PRICE,100) PRICE,VALUE_SOURCE FROM (SELECT SEC_ID,PRICE, VALUE_SOURCE,RANK() OVER(PARTITION BY SEC_ID ORDER BY VALUE_DATE DESC ) AS rowno
    FROM MST_BOND_VALUATION_INFO WHERE VALUE_DATE<=date''${valDate}'') WHERE rowno=1
), temp as ( select SHARE_AMT,(DSC_COST_AMT+TDY_INTINCEXP) as DSC_COST_AMT,p.PRICE AS market_price
                from  PTL_SEC_PIERCE_VALUTION  m inner join FIN_PRODUCT  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.FINPROD_TYPE2 =''F01''
        LEFT JOIN BOND p ON fpt.FINPROD_MARKET_ID = p.SEC_ID AND CASE WHEN fpt.TRADE_MARKET in(''03'',''04'') THEN ''CSD'' ELSE ''CBC'' END =p.VALUE_SOURCE
          WHERE m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''
      UNION ALL
        select SHARE_AMT,(DSC_COST_AMT+TDY_INTINCEXP)  AS DSC_COST_AMT,(DSC_COST_AMT+TDY_INTINCEXP)/DSC_COST_AMT AS market_price
                from  PTL_SEC_PIERCE_VALUTION  m INNER JOIN FIN_PRODUCT_TYPE  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.TYPE_8 in (''F05'',''F06'') AND fpt.BRANCH =0
          WHERE
           m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}''
      UNION ALL
        select SHARE_AMT,(DSC_COST_AMT+TDY_INTINCEXP)  AS DSC_COST_AMT,(DSC_COST_AMT+TDY_INTINCEXP)/DSC_COST_AMT AS market_price
                from  PTL_SEC_PIERCE_VALUTION  m INNER JOIN FIN_TRADE_PRODUCT  fpt  ON   m.FINPROD_ID=fpt.FINPROD_ID
          AND fpt.FINPROD_TYPE2 in (''F05'',''F03'') AND fpt.BRANCH =0
          WHERE
           m.PORTFOLIO_ID IN ${portfolioCode}
          AND m.CDATE =DATE''${valDate}'' AND m.DSC_COST_AMT >0
      UNION ALL
               select nvl(sum(cash),0) AS SHARE_AMT,nvl(sum(cash),0) AS DSC_COST_AMT,1 AS market_price from (SELECT  SUM(m.DSC_COST_AMT+nvl(m.TDY_INTINCEXP,0)) as cash FROM PTL_SEC_PIERCE_VALUTION m where
 SUBSTR(m.FINPROD_ID,instr(m.FINPROD_ID,'':'',1,1)+1,length(m.FINPROD_ID)) in ( SELECT SUBSTR(ACCT_NO,instr(ACCT_NO,'':'',1,1)+1,length(ACCT_NO)) from PTL_CAPACCT_INFO where PORTFOLIO_ID in ${portfolioCode}  AND ACCT_TYPE <> ''A06'')
 AND m.PORTFOLIO_ID in ${portfolioCode} AND m.CDATE =DATE''${valDate}''
 UNION ALL
 SELECT SUM(n.DSC_COST_AMT+nvl(n.TDY_INTINCEXP,0))  as cash FROM  PTL_SEC_PIERCE_VALUTION n WHERE n.FINPROD_ID in ${portfolioCode} AND n.CDATE =DATE''${valDate}'')
)
SELECT DEV_AMT AS RVALUE FROM(SELECT
    decode(fenzitwo,NULL,0,0,0,ABS(fenzi-fenzitwo)/ABS(fenzitwo)) AS DEV_AMT
FROM
    (
        SELECT
            CASE
                WHEN SUM(NVL(m.SHARE_AMT, 0)) =0
                THEN 0
                ELSE SUM(NVL(m.DSC_COST_AMT, 0))/SUM(NVL(m.SHARE_AMT, 0))
            END fenzi,
            CASE
                WHEN SUM(NVL(m.SHARE_AMT, 0))=0
                THEN 0
                ELSE SUM(NVL(m.SHARE_AMT, 0)*NVL(m.market_price, 100))/SUM(NVL(m.SHARE_AMT, 0))
            END fenzitwo
        FROM
            temp m))';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'MBDZCPTYCBFGZPLDCTH' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'MBDZCPTYCBFGZPLDCTH' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'MBDZCPTYCBFGZPLDCTH' and PARA_GRANULARITY = '05';
END;
/
