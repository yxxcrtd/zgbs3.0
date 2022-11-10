/**风险指标参数: 不同产品5交易日证券反向交易控制(买) **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='BTCPFIVEZQJYKZBUY';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('BTCPFIVEZQJYKZBUY','不同产品5交易日证券反向交易控制(买)', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'N' , 'String' ,  null ,  'DATABASE'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='BTCPFIVEZQJYKZBUY';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('BTCPFIVEZQJYKZBUY','01', 'SQL','select 1 from dual',  null , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('BTCPFIVEZQJYKZBUY','02', 'SQL','select 1 from dual',  null , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('BTCPFIVEZQJYKZBUY','03', 'SQL','select 1 from dual',  null , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('BTCPFIVEZQJYKZBUY','04', 'SQL','select 1 from dual',  null , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('BTCPFIVEZQJYKZBUY','05', 'SQL','select 1 from dual',  null , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'WITH temp AS (SELECT s.FINPROD_ID,s.CDATE
FROM( SELECT m.FINPROD_ID, n.CDATE, ROW_NUMBER() OVER(PARTITION BY m.FINPROD_ID ORDER BY n.CDATE DESC) num
    FROM(SELECT FINPROD_ID,
                CASE WHEN CALENDAR_ID IS NULL THEN ''01''
                     ELSE CALENDAR_ID END CALENDAR_ID
         FROM FIN_PRODUCT WHERE  FINPROD_ID=${assetCode}) m
            INNER JOIN MST_HOLIDAY n
                       ON m.CALENDAR_ID=n.CALENDAR_ID
                           AND n.CDATE<=DATE''${valDate}''
                           AND n.CDATE>=DATE''${valDate}''-10
                           AND n.IS_HOLIDAY=0 ) s
WHERE s.num<=5)
SELECT DISTINCT  nvl(max(RVALUE),0) RVALUE FROM (
--1.1交易表
SELECT t2.UNIT_CPRICE RVALUE
FROM TRD_PRODUCT_DEAL_ADD t1
         INNER JOIN TRD_PRODUCT_DEAL t2
                    ON t1.TRADE_ID=t2.TRADE_ID AND t2.TRADE_STATUS=''01''AND NVL(t2.IS_CANCEL,''N'') =''N''
         INNER JOIN FIN_PRODUCT m
                    ON  m.FINPROD_ID=t2.FINPROD_ID
         INNER JOIN temp p
                    ON t2.FINPROD_ID = p.FINPROD_ID
                        AND t2.TRADE_DATE=p.CDATE
WHERE (
        (t2.PS IN (''T01'',''T46'',''T47'',''T48'',''T04'',''T50'') AND m.FINPROD_TYPE2 IN (''F01'',''F32'',''F10''))OR
        (t2.PS =''T49''AND m.FINPROD_TYPE2 =''F10'')
    )
  AND t1.PORTFOLIO_ID IN ${portfolioCode} and t2.FINPROD_ID=${assetCode}
UNION all
--2.1日间交易表
SELECT t2.UNIT_CPRICE RVALUE
FROM TRD_PRODUCT_TEMP_DEAL_ADD t1
         INNER JOIN TRD_PRODUCT_TEMP_DEAL t2
                    ON t1.TRADE_ID=t2.TRADE_ID AND t2.TRADE_STATUS=''01''AND NVL(t2.IS_CANCEL,''N'') =''N''
         INNER JOIN FIN_PRODUCT m
                    ON  m.FINPROD_ID=t2.FINPROD_ID
         INNER JOIN temp p
                    ON t2.FINPROD_ID = p.FINPROD_ID
                        AND t2.TRADE_DATE=p.CDATE
WHERE (
        (t2.PS IN (''T01'',''T46'',''T47'',''T48'',''T04'',''T50'') AND m.FINPROD_TYPE2 IN (''F01'',''F32'',''F10''))OR
        (t2.PS =''T49''AND m.FINPROD_TYPE2 =''F10'')
    )
  AND t1.PORTFOLIO_ID IN ${portfolioCode} and t2.FINPROD_ID=${assetCode}
UNION all
--3.1流程交易表
SELECT t2.UNIT_CPRICE RVALUE
FROM WFL_TRD_PRODUCT_DEAL_ADD t1
         INNER JOIN WFL_TRD_PRODUCT_DEAL t2
                    ON t1.TRADE_ID=t2.TRADE_ID AND t2.TRADE_STATUS=''01''AND NVL(t2.IS_CANCEL,''N'') =''N''
         INNER JOIN SYS_WFL_BUSINESS_LIST swbl on t2.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
         INNER JOIN FIN_PRODUCT m
                    ON  m.FINPROD_ID=t2.FINPROD_ID
         INNER JOIN temp p
                    ON t2.FINPROD_ID = p.FINPROD_ID
                        AND t2.TRADE_DATE=p.CDATE
WHERE (
        (t2.PS IN (''T01'',''T46'',''T47'',''T48'',''T04'',''T50'') AND m.FINPROD_TYPE2 IN (''F01'',''F32'',''F10''))OR
        (t2.PS =''T49''AND m.FINPROD_TYPE2 =''F10'')
    )
  AND t1.PORTFOLIO_ID IN ${portfolioCode} and t2.FINPROD_ID=${assetCode}
)';

----事后(日间)
V_CONTENT_04 CLOB := 'WITH TEMP AS (SELECT s.FINPROD_ID, s.CDATE
                           FROM ( SELECT m.FINPROD_ID, n.CDATE, ROW_NUMBER() OVER(PARTITION BY m.FINPROD_ID ORDER BY n.CDATE DESC) num
                                   FROM( SELECT FINPROD_ID,CASE  WHEN CALENDAR_ID IS NULL THEN ''01'' ELSE CALENDAR_ID END CALENDAR_ID
                                          FROM FIN_PRODUCT WHERE FINPROD_TYPE2 IN (''F01'',''F32'',''F10'')) m
                                          INNER JOIN  MST_HOLIDAY n
                                          ON m.CALENDAR_ID=n.CALENDAR_ID
                                          AND n.CDATE<=DATE''${valDate}''
                      AND n.CDATE>=DATE''${valDate}''-10
                                          AND n.IS_HOLIDAY=0) s
                         WHERE s.num<=5)
SELECT COUNT(1)AS RVALUE, FINPROD_ID   AS RKEY, FINPROD_NAME AS RKEYCN
FROM( SELECT B.FINPROD_ID, A.FINPROD_NAME,A.MAX_BUY,B.MIN_SELL
        FROM(SELECT FINPROD_ID , FINPROD_NAME, MAX(UNIT_CPRICE) AS MAX_BUY
              FROM (
          --交易表
          SELECT t1.PORTFOLIO_ID, t2.FINPROD_ID , m.FINPROD_NAME,t2.UNIT_CPRICE
          FROM TRD_PRODUCT_DEAL_ADD t1
          INNER JOIN TRD_PRODUCT_DEAL t2
          ON t1.TRADE_ID=t2.TRADE_ID
          INNER JOIN FIN_PRODUCT m
          ON m.FINPROD_ID=t2.FINPROD_ID
          INNER JOIN TEMP p
          ON t2.FINPROD_ID = p.FINPROD_ID
          AND t2.TRADE_DATE=p.CDATE
          WHERE (
          (t2.PS IN (''T01'',''T46'',''T47'',''T48'',''T04'',''T50'') AND m.FINPROD_TYPE2 IN (''F01'',''F32'',''F10''))OR
          (t2.PS =''T49''AND m.FINPROD_TYPE2 =''F10'')
          )
          AND t2.TRADE_STATUS=''01''
                    AND NVL(t2.IS_CANCEL,''N'') =''N''
          AND t1.PORTFOLIO_ID IN ${portfolioCode}
          UNION ALL
          --日间交易表（场内交易）
          SELECT t1.PORTFOLIO_ID, t2.FINPROD_ID , m.FINPROD_NAME,t2.UNIT_CPRICE
          FROM TRD_PRODUCT_TEMP_DEAL_ADD t1
          INNER JOIN TRD_PRODUCT_TEMP_DEAL t2
          ON t1.TRADE_ID=t2.TRADE_ID
          INNER JOIN FIN_PRODUCT m
          ON m.FINPROD_ID=t2.FINPROD_ID
          INNER JOIN TEMP p
          ON t2.FINPROD_ID = p.FINPROD_ID
          AND t2.TRADE_DATE=p.CDATE
          WHERE (
          (t2.PS IN (''T01'',''T46'',''T47'',''T48'',''T04'',''T50'') AND m.FINPROD_TYPE2 IN (''F01'',''F32'',''F10''))OR
          (t2.PS =''T49''AND m.FINPROD_TYPE2 =''F10'')
          )
          AND t2.TRADE_STATUS=''01''
                    AND NVL(t2.IS_CANCEL,''N'') =''N''
          AND t1.PORTFOLIO_ID IN ${portfolioCode}
          UNION ALL
          --交易流程表
          SELECT t1.PORTFOLIO_ID, t2.FINPROD_ID , m.FINPROD_NAME,t2.UNIT_CPRICE
          FROM WFL_TRD_PRODUCT_DEAL_ADD t1
          INNER JOIN WFL_TRD_PRODUCT_DEAL t2
          ON t1.TRADE_ID=t2.TRADE_ID
          AND t2.TRADE_STATUS= ''01''
          INNER JOIN SYS_WFL_BUSINESS_LIST swbl on t2.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
          INNER JOIN FIN_PRODUCT m
          ON m.FINPROD_ID=t2.FINPROD_ID
          INNER JOIN TEMP p
          ON t2.FINPROD_ID = p.FINPROD_ID
          AND t2.TRADE_DATE=p.CDATE
          WHERE (
          (t2.PS IN (''T01'',''T46'',''T47'',''T48'',''T04'',''T50'') AND m.FINPROD_TYPE2 IN (''F01'',''F32'',''F10''))OR
          (t2.PS =''T49''AND m.FINPROD_TYPE2 =''F10'')
          )
          AND t1.PORTFOLIO_ID IN ${portfolioCode}
          )
                GROUP BY FINPROD_ID,FINPROD_NAME) A
        INNER JOIN
            (SELECT FINPROD_ID , FINPROD_NAME, MIN(UNIT_CPRICE) AS MIN_SELL
              FROM(SELECT t2.FINPROD_ID , m.FINPROD_NAME,t2.UNIT_CPRICE
           FROM TRD_PRODUCT_DEAL_ADD t1
           INNER JOIN TRD_PRODUCT_DEAL t2
           ON t1.TRADE_ID=t2.TRADE_ID
           INNER JOIN FIN_PRODUCT m
           ON m.FINPROD_ID=t2.FINPROD_ID
           INNER JOIN TEMP p
          ON t2.FINPROD_ID = p.FINPROD_ID
          AND t2.TRADE_DATE=p.CDATE
          WHERE  (
          (t2.PS IN (''T02'',''T03'') AND m.FINPROD_TYPE2 IN (''F01'',''F32'',''F10''))OR
          (t2.PS =''T49''AND m.FINPROD_TYPE2 =''F01'')
          )
          AND t2.TRADE_STATUS=''01''
                    AND NVL(t2.IS_CANCEL,''N'') =''N''
          AND t1.PORTFOLIO_ID IN ${portfolioCode}
          UNION ALL
          SELECT t2.FINPROD_ID , m.FINPROD_NAME,t2.UNIT_CPRICE
            FROM TRD_PRODUCT_TEMP_DEAL_ADD t1
            INNER JOIN TRD_PRODUCT_TEMP_DEAL t2
            ON t1.TRADE_ID=t2.TRADE_ID
            INNER JOIN FIN_PRODUCT m
            ON m.FINPROD_ID=t2.FINPROD_ID
            INNER JOIN TEMP p
          ON t2.FINPROD_ID = p.FINPROD_ID
          AND t2.TRADE_DATE=p.CDATE
          WHERE   (
          (t2.PS IN (''T02'',''T03'') AND m.FINPROD_TYPE2 IN (''F01'',''F32'',''F10''))OR
          (t2.PS =''T49''AND m.FINPROD_TYPE2 =''F01'')
          )
          AND t2.TRADE_STATUS=''01''
                    AND NVL(t2.IS_CANCEL,''N'') =''N''
          AND t1.PORTFOLIO_ID IN ${portfolioCode}
          UNION ALL
          SELECT t2.FINPROD_ID , m.FINPROD_NAME,t2.UNIT_CPRICE
            FROM WFL_TRD_PRODUCT_DEAL_ADD t1
            INNER JOIN WFL_TRD_PRODUCT_DEAL t2
            ON t1.TRADE_ID=t2.TRADE_ID
          AND t2.TRADE_STATUS = ''01''
          INNER JOIN SYS_WFL_BUSINESS_LIST swbl on t2.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
            INNER JOIN FIN_PRODUCT m
            ON m.FINPROD_ID=t2.FINPROD_ID
            INNER JOIN TEMP p
          ON t2.FINPROD_ID = p.FINPROD_ID
          AND t2.TRADE_DATE=p.CDATE
          WHERE   (
          (t2.PS IN (''T02'',''T03'') AND m.FINPROD_TYPE2 IN (''F01'',''F32'',''F10''))OR
          (t2.PS =''T49''AND m.FINPROD_TYPE2 =''F01'')
          )
          AND t1.PORTFOLIO_ID IN ${portfolioCode}
          )
          GROUP BY FINPROD_ID ,FINPROD_NAME) B
        ON  A.FINPROD_ID =B.FINPROD_ID
        AND A.MAX_BUY>B.MIN_SELL )
GROUP BY FINPROD_ID,FINPROD_NAME';

----事后(日终)
V_CONTENT_05 CLOB := 'WITH TEMP AS (SELECT s.FINPROD_ID, s.CDATE
                           FROM ( SELECT m.FINPROD_ID, n.CDATE, ROW_NUMBER() OVER(PARTITION BY m.FINPROD_ID ORDER BY n.CDATE DESC) num
                                   FROM( SELECT FINPROD_ID,CASE  WHEN CALENDAR_ID IS NULL THEN ''01'' ELSE CALENDAR_ID END CALENDAR_ID
                                          FROM FIN_PRODUCT WHERE FINPROD_TYPE2 IN (''F01'',''F32'',''F10'')) m
                                          INNER JOIN  MST_HOLIDAY n
                                          ON m.CALENDAR_ID=n.CALENDAR_ID
                                          AND n.CDATE<=DATE''${valDate}''
                      AND n.CDATE>=DATE''${valDate}''-10
                                          AND n.IS_HOLIDAY=0) s
                         WHERE s.num<=5)
SELECT COUNT(1) AS RVALUE, FINPROD_ID   AS RKEY, FINPROD_NAME AS RKEYCN
FROM( SELECT B.FINPROD_ID, A.FINPROD_NAME,A.MAX_BUY,B.MIN_SELL
        FROM(SELECT FINPROD_ID , FINPROD_NAME, MAX(UNIT_CPRICE) AS MAX_BUY
              FROM (SELECT t1.PORTFOLIO_ID, t2.FINPROD_ID , m.FINPROD_NAME,t2.UNIT_CPRICE
          FROM TRD_PRODUCT_DEAL_ADD t1
          INNER JOIN TRD_PRODUCT_DEAL t2
          ON t1.TRADE_ID=t2.TRADE_ID
          INNER JOIN FIN_PRODUCT m
          ON m.FINPROD_ID=t2.FINPROD_ID
          INNER JOIN TEMP p
          ON t2.FINPROD_ID = p.FINPROD_ID
          AND t2.TRADE_DATE=p.CDATE
          WHERE (
          (t2.PS IN (''T01'',''T46'',''T47'',''T48'',''T04'',''T50'') AND m.FINPROD_TYPE2 IN (''F01'',''F32'',''F10''))OR
          (t2.PS =''T49''AND m.FINPROD_TYPE2 =''F10'')
          )
          AND t2.TRADE_STATUS=''01''
                    AND NVL(t2.IS_CANCEL,''N'') =''N''
          AND t1.PORTFOLIO_ID IN ${portfolioCode})
                GROUP BY FINPROD_ID, FINPROD_NAME) A
        INNER JOIN
            (SELECT FINPROD_ID , FINPROD_NAME, MIN(UNIT_CPRICE) AS MIN_SELL
              FROM(SELECT t2.FINPROD_ID , m.FINPROD_NAME,t2.UNIT_CPRICE
           FROM TRD_PRODUCT_DEAL_ADD t1
           INNER JOIN TRD_PRODUCT_DEAL t2
          ON t1.TRADE_ID=t2.TRADE_ID
          INNER JOIN FIN_PRODUCT m
          ON m.FINPROD_ID=t2.FINPROD_ID
          INNER JOIN TEMP p
          ON t2.FINPROD_ID = p.FINPROD_ID
          AND t2.TRADE_DATE=p.CDATE
          WHERE (
          (t2.PS IN (''T02'',''T03'') AND m.FINPROD_TYPE2 IN (''F01'',''F32'',''F10''))OR
          (t2.PS =''T49''AND m.FINPROD_TYPE2 =''F01'')
          )
          AND t2.TRADE_STATUS=''01''
                    AND NVL(t2.IS_CANCEL,''N'') =''N''
          AND t1.PORTFOLIO_ID IN ${portfolioCode})
                GROUP BY FINPROD_ID ,FINPROD_NAME) B
        ON  A.FINPROD_ID =B.FINPROD_ID
        AND A.MAX_BUY>B.MIN_SELL )
GROUP BY FINPROD_ID,FINPROD_NAME';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'BTCPFIVEZQJYKZBUY' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'BTCPFIVEZQJYKZBUY' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'BTCPFIVEZQJYKZBUY' and PARA_GRANULARITY = '05';
END;
/
