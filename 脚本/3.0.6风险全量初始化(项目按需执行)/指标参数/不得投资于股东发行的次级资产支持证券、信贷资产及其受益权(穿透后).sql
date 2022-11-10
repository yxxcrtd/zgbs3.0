/**风险指标参数: 不得投资于股东发行的次级资产支持证券、信贷资产及其受益权(穿透后) **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='BDTZCJZDZXDJQSYQCTH';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('BDTZCJZDZXDJQSYQCTH','不得投资于股东发行的次级资产支持证券、信贷资产及其受益权(穿透后)', 'Y' , 'SELECT ''01'' ID ,''次级资产支持证券'' TEXT FROM DUAL
  UNION ALL
  SELECT ''02'' ID ,''非次级资产支持证券'' TEXT FROM DUAL
  UNION all
  SELECT ''03'' ID ,''信贷资产及其收受益权'' TEXT FROM DUAL
  UNION all
  SELECT ''04'' ID ,''非信贷资产及其收受益权'' TEXT FROM DUAL' ,'system','1',sysdate,'system',sysdate, null , null , 'N' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='BDTZCJZDZXDJQSYQCTH';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('BDTZCJZDZXDJQSYQCTH','01', 'SQL','select 1 from dual',  'assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('BDTZCJZDZXDJQSYQCTH','02', 'SQL','select 1 from dual',  'assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('BDTZCJZDZXDJQSYQCTH','03', 'SQL','select 1 from dual',  'assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('BDTZCJZDZXDJQSYQCTH','04', 'SQL','select 1 from dual',  'assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('BDTZCJZDZXDJQSYQCTH','05', 'SQL','select 1 from dual',  'assetCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'SELECT m.FINPROD_ID RKEY, DECODE(IS_SEC_BOND, ''Y'', ''01||次级资产支持证券'', ''02||非次级资产支持证券'') RVALUE
FROM FIN_PRODUCT m
INNER JOIN FIN_PRODUCT_TYPE n ON m.FINPROD_ID = n.FINPROD_ID AND n.BRANCH = 0
INNER JOIN FIN_PRODUCT_ADD  s ON m.FINPROD_ID = s.FINPROD_ID
INNER JOIN MST_CUSTOMER_INFO_ADD A ON A.CUSTOMER_ID = m.Issuer
WHERE n.TYPE_8 = ''F01'' AND TYPE_6 = ''B13'' AND TYPE_7 IN (''16'', ''17'') AND A.INSITITUTE_CODE = (select i.insititute_code from sys_institution i where i.institution_type = ''01'')
UNION ALL
--1.特定资产类型设置中是否信贷资产为是的债券和项目类资产
  SELECT m.FINPROD_ID  RKEY, ''03||信贷资产及其收受益权'' RVALUE
  FROM FIN_PRODUCT m
  INNER JOIN MST_CUSTOMER_INFO_ADD A ON  (A.CUSTOMER_ID = m.Issuer and  m.finprod_type2 IN (''F01'',''F32''))
    OR (A.CUSTOMER_ID = m.Financier and  m.finprod_type2 = ''F18'')
  INNER JOIN FIN_PRODUCT_TYPE n ON m.FINPROD_ID = n.FINPROD_ID AND n.BRANCH = 0
  INNER JOIN RSK_SPECIFIC_ASSET_TYPE r ON n.FINPROD_ID = r.FINPROD_ID
  where r.Is_Credit_Asset = ''Y''AND A.INSITITUTE_CODE = (select i.insititute_code from sys_institution i where i.institution_type = ''01'')
UNION ALL
--2.没有进行特定资产设置或是否信贷资产为否  且 type_7为1204和1205的项目类资产
  SELECT m.FINPROD_ID  RKEY, ''03||信贷资产及其收受益权'' RVALUE
  FROM FIN_PRODUCT m
  INNER JOIN MST_CUSTOMER_INFO_ADD A ON  A.CUSTOMER_ID = m.Financier and  m.finprod_type2 = ''F18''
  INNER JOIN FIN_PRODUCT_TYPE n ON m.FINPROD_ID = n.FINPROD_ID AND n.BRANCH = 0
  LEFT JOIN RSK_SPECIFIC_ASSET_TYPE r ON n.FINPROD_ID = r.FINPROD_ID and r.Is_Credit_Asset=''N''
  where n.type_8 =''F06''AND n.type_7 in (''1204'',''1205'') AND A.INSITITUTE_CODE = (select i.insititute_code from sys_institution i where i.institution_type = ''01'')
UNION ALL
  SELECT m.FINPROD_ID  RKEY, ''04||非信贷资产及其收受益权'' RVALUE
  FROM FIN_PRODUCT m
  INNER JOIN MST_CUSTOMER_INFO_ADD A ON (A.CUSTOMER_ID = m.Issuer and  m.finprod_type2 IN (''F01'',''F32''))
    OR (A.CUSTOMER_ID = m.Financier and  m.finprod_type2 = ''F18'')
  INNER JOIN FIN_PRODUCT_TYPE n ON m.FINPROD_ID = n.FINPROD_ID AND n.BRANCH = 0
  LEFT JOIN RSK_SPECIFIC_ASSET_TYPE r ON n.FINPROD_ID = r.FINPROD_ID and r.Is_Credit_Asset=''N''
  where n.type_8 =''F06''AND n.type_7 not in (''1204'',''1205'') AND A.INSITITUTE_CODE = (select i.insititute_code from sys_institution i where i.institution_type = ''01'')';

----事后(日间)
V_CONTENT_04 CLOB := 'WITH ASSET AS (
    SELECT m.FINPROD_ID RKEY, DECODE(IS_SEC_BOND, ''Y'', ''01||次级资产支持证券'', ''02||非次级资产支持证券'') RVALUE
FROM FIN_PRODUCT m
INNER JOIN FIN_PRODUCT_TYPE n ON m.FINPROD_ID = n.FINPROD_ID AND n.BRANCH = 0
INNER JOIN FIN_PRODUCT_ADD  s ON m.FINPROD_ID = s.FINPROD_ID
INNER JOIN MST_CUSTOMER_INFO_ADD A ON A.CUSTOMER_ID = m.Issuer
WHERE n.TYPE_8 = ''F01'' AND TYPE_6 = ''B13'' AND TYPE_7 IN (''16'', ''17'') AND A.INSITITUTE_CODE = (select i.insititute_code from sys_institution i where i.institution_type = ''01'')
UNION ALL
--1.特定资产类型设置中是否信贷资产为是的债券和项目类资产
  SELECT m.FINPROD_ID  RKEY, ''03||信贷资产及其收受益权'' RVALUE
  FROM FIN_PRODUCT m
  INNER JOIN MST_CUSTOMER_INFO_ADD A ON  (A.CUSTOMER_ID = m.Issuer and  m.finprod_type2 IN (''F01'',''F32''))
    OR (A.CUSTOMER_ID = m.Financier and  m.finprod_type2 = ''F18'')
  INNER JOIN FIN_PRODUCT_TYPE n ON m.FINPROD_ID = n.FINPROD_ID AND n.BRANCH = 0
  INNER JOIN RSK_SPECIFIC_ASSET_TYPE r ON n.FINPROD_ID = r.FINPROD_ID
  where r.Is_Credit_Asset = ''Y''AND A.INSITITUTE_CODE = (select i.insititute_code from sys_institution i where i.institution_type = ''01'')
UNION ALL
--2.没有进行特定资产设置或是否信贷资产为否  且 type_7为1204和1205的项目类资产
  SELECT m.FINPROD_ID  RKEY, ''03||信贷资产及其收受益权'' RVALUE
  FROM FIN_PRODUCT m
  INNER JOIN MST_CUSTOMER_INFO_ADD A ON  A.CUSTOMER_ID = m.Financier and  m.finprod_type2 = ''F18''
  INNER JOIN FIN_PRODUCT_TYPE n ON m.FINPROD_ID = n.FINPROD_ID AND n.BRANCH = 0
  LEFT JOIN RSK_SPECIFIC_ASSET_TYPE r ON n.FINPROD_ID = r.FINPROD_ID and r.Is_Credit_Asset=''N''
  where n.type_8 =''F06''AND n.type_7 in (''1204'',''1205'') AND A.INSITITUTE_CODE = (select i.insititute_code from sys_institution i where i.institution_type = ''01'')
UNION ALL
  SELECT m.FINPROD_ID  RKEY, ''04||非信贷资产及其收受益权'' RVALUE
  FROM FIN_PRODUCT m
  INNER JOIN MST_CUSTOMER_INFO_ADD A ON (A.CUSTOMER_ID = m.Issuer and  m.finprod_type2 IN (''F01'',''F32''))
    OR (A.CUSTOMER_ID = m.Financier and  m.finprod_type2 = ''F18'')
  INNER JOIN FIN_PRODUCT_TYPE n ON m.FINPROD_ID = n.FINPROD_ID AND n.BRANCH = 0
  LEFT JOIN RSK_SPECIFIC_ASSET_TYPE r ON n.FINPROD_ID = r.FINPROD_ID and r.Is_Credit_Asset=''N''
  where n.type_8 =''F06''AND n.type_7 not in (''1204'',''1205'') AND A.INSITITUTE_CODE = (select i.insititute_code from sys_institution i where i.institution_type = ''01'')
)
SELECT RKEY, RVALUE, RKEYCN FROM (
SELECT SUM(NVL(SHARE_AMT,0)) SHARE_AMT, RKEY, RVALUE, FINPROD_NAME RKEYCN FROM (
    SELECT SUM(NVL(PSP.AMOUNT,0)) SHARE_AMT, ASSET.RKEY, ASSET.RVALUE, FP.FINPROD_NAME
    FROM PTL_SEC_PIERCE_VALUTION PSP
    INNER JOIN FIN_PRODUCT FP ON FP.FINPROD_ID = PSP.FINPROD_ID
    INNER JOIN ASSET ON ASSET.RKEY = FP.FINPROD_ID
    WHERE PSP.CDATE = DATE ''${valDate}'' - 1 AND PSP.PORTFOLIO_ID IN ${portfolioCode}
    GROUP BY ASSET.RKEY, ASSET.RVALUE, FP.FINPROD_NAME
  UNION ALL
    SELECT
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T25'') THEN NVL(n.SHARE_AMT,0)
               WHEN n.PS =''T49'' THEN NVL(-1*n.SHARE_AMT*100,0)
     ELSE  NVL(-1*n.SHARE_AMT,0)
     END) AS SHARE_AMT,
        ASSET.RKEY, ASSET.RVALUE, FP.FINPROD_NAME
    FROM TRD_PRODUCT_DEAL_ADD m
    INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
    INNER JOIN FIN_PRODUCT FP ON FP.FINPROD_ID = n.FINPROD_ID
    INNER JOIN ASSET ON ASSET.RKEY = n.FINPROD_ID
    WHERE n.TRADE_DATE = DATE''${valDate}'' AND m.PORTFOLIO_ID IN ${portfolioCode}
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T07'', ''T22'', ''T23'', ''T39'',''T25'', ''T49'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''
    GROUP BY ASSET.RKEY, ASSET.RVALUE, FP.FINPROD_NAME
  UNION ALL
    SELECT
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'',''T25'') THEN NVL(n.SHARE_AMT,0)
               WHEN n.PS =''T49'' THEN NVL(-1*n.SHARE_AMT*100,0)
     ELSE  NVL(-1*n.SHARE_AMT,0)
     END) AS SHARE_AMT,
        ASSET.RKEY, ASSET.RVALUE, FP.FINPROD_NAME
    FROM TRD_PRODUCT_TEMP_DEAL_ADD m
    INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID
    INNER JOIN FIN_PRODUCT FP ON FP.FINPROD_ID = n.FINPROD_ID
    INNER JOIN ASSET ON ASSET.RKEY = n.FINPROD_ID
    WHERE n.TRADE_DATE = DATE''${valDate}'' AND m.PORTFOLIO_ID IN ${portfolioCode}
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T07'', ''T22'', ''T23'', ''T39'',''T25'', ''T49'')
    AND n.TRADE_STATUS = ''01''
    AND n.IS_CANCEL = ''N''
    GROUP BY ASSET.RKEY, ASSET.RVALUE, FP.FINPROD_NAME
  UNION ALL
    SELECT
     SUM(CASE  WHEN n.PS IN(''T01'',''T04'') THEN NVL(n.SHARE_AMT,0)
               WHEN n.PS =''T49'' THEN NVL(-1*n.SHARE_AMT*100,0)
     ELSE  NVL(-1*n.SHARE_AMT,0)
     END) AS SHARE_AMT,
        ASSET.RKEY, ASSET.RVALUE, FP.FINPROD_NAME
    FROM WFL_TRD_PRODUCT_DEAL_ADD m
    INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS = ''01''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT FP ON FP.FINPROD_ID = n.FINPROD_ID
    INNER JOIN ASSET ON ASSET.RKEY = n.FINPROD_ID
    WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T49'')
    AND n.IS_CANCEL = ''N''
    GROUP BY ASSET.RKEY, ASSET.RVALUE, FP.FINPROD_NAME
  UNION ALL
    SELECT
        SUM(nvl( n.SHARE_AMT, 0))   AS SHARE_AMT,
    ASSET.RKEY, ASSET.RVALUE, FP.FINPROD_NAME
    FROM WFL_TRD_PRODUCT_DEAL_ADD m
    INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS = ''01''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    INNER JOIN FIN_PRODUCT FP ON FP.FINPROD_ID = n.FINPROD_ID
    INNER JOIN ASSET ON ASSET.RKEY = n.FINPROD_ID
    WHERE n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
    AND n.PS =''T02''
    AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
    AND n.IS_CANCEL = ''N''
    GROUP BY ASSET.RKEY, ASSET.RVALUE, FP.FINPROD_NAME
    UNION ALL
    --5.可转债网下申购：还未做申购确认
  SELECT SUM(nvl(T.prin_amt,0)) SHARE_AMT,
    ASSET.RKEY, ASSET.RVALUE, T2.FINPROD_NAME
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 INNER JOIN ASSET ON ASSET.RKEY = T.FINPROD_ID
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
    GROUP BY ASSET.RKEY, ASSET.RVALUE, T2.FINPROD_NAME
 union all
 --6.可转债网下申购：申购交易流程中
 SELECT SUM(NVL(T.prin_amt, 0)) SHARE_AMT,
    ASSET.RKEY, ASSET.RVALUE, T2.FINPROD_NAME
  FROM WFL_TRD_PRODUCT_DEAL T
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN ASSET ON ASSET.RKEY = T.FINPROD_ID
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T1.PORTFOLIO_ID in${portfolioCode} AND
    T.PS = ''T50''
   AND T.FINPROD_TYPE2 = ''F01''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
    GROUP BY ASSET.RKEY, ASSET.RVALUE, T2.FINPROD_NAME
union all
 --7.可转债网下申购：有申购确认交易
SELECT SUM(NVL(T.prin_amt, 0)) SHARE_AMT,
    ASSET.RKEY, ASSET.RVALUE, T2.FINPROD_NAME
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN ASSET ON ASSET.RKEY = T.FINPROD_ID
 WHERE T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T1.PORTFOLIO_ID in ${portfolioCode}
   AND T.PS = ''T05''
   AND T.FINPROD_TYPE2 = ''F01''
    GROUP BY ASSET.RKEY, ASSET.RVALUE, T2.FINPROD_NAME
  union all
 --8.可转债网下申购：申购确认交易(流程中)
SELECT SUM(NVL(T.prin_amt, 0)) SHARE_AMT,
    ASSET.RKEY, ASSET.RVALUE, T2.FINPROD_NAME
  FROM WFL_TRD_PRODUCT_DEAL T
 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN ASSET ON ASSET.RKEY = T.FINPROD_ID
 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
 WHERE T1.PORTFOLIO_ID in  ${portfolioCode} AND
    T.PS = ''T05''
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE = DATE''${valDate}''
   AND T.FINPROD_TYPE2 = ''F01''
    GROUP BY ASSET.RKEY, ASSET.RVALUE, T2.FINPROD_NAME
  union all
--9.债转股（股票）
  SELECT SUM(nvl(T.prin_amt,0)) SHARE_AMT,
    ASSET.RKEY, ASSET.RVALUE, T2.FINPROD_NAME
  FROM TRD_FIN_PRODUCT_BARTER T
  INNER JOIN TRD_PRODUCT_DEAL T3 ON T.TRADE_ID = T3.TRADE_ID
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN ASSET ON ASSET.RKEY = T.FINPROD_ID
 WHERE T1.PORTFOLIO_ID in  ${portfolioCode} AND
    T3.PS = ''T49''
   AND T.FINPROD_TYPE = ''F10''
   AND T3.TRADE_STATUS = ''01''
   AND T3.IS_CANCEL = ''N''
   AND T3.TRADE_DATE = DATE''${valDate}''
    GROUP BY ASSET.RKEY, ASSET.RVALUE, T2.FINPROD_NAME
) GROUP BY RKEY, RVALUE, FINPROD_NAME
) WHERE SHARE_AMT > 0';

----事后(日终)
V_CONTENT_05 CLOB := 'WITH ASSET AS (
SELECT m.FINPROD_ID RKEY, DECODE(fa.IS_SEC_BOND, ''Y'', ''01||次级资产支持证券'', ''02||非次级资产支持证券'') RVALUE
FROM FIN_PRODUCT m
INNER JOIN FIN_PRODUCT_ADD fa on m.FINPROD_ID = fa.FINPROD_ID
INNER JOIN FIN_PRODUCT_TYPE n ON m.FINPROD_ID = n.FINPROD_ID AND n.BRANCH = 0
INNER JOIN MST_CUSTOMER_INFO_ADD A ON A.CUSTOMER_ID = m.Issuer
WHERE n.TYPE_8 = ''F01'' AND TYPE_6 = ''B13'' AND TYPE_7 IN (''16'', ''17'') AND A.INSITITUTE_CODE = (select i.institution_code insititute_code from sys_institution i where i.institution_type = ''01'')
UNION ALL
--1.特定资产类型设置中是否信贷资产为是的债券和项目类资产
  SELECT m.FINPROD_ID  RKEY, ''03||信贷资产及其收受益权'' RVALUE
  FROM FIN_PRODUCT m
  INNER JOIN MST_CUSTOMER_INFO_ADD A ON  (A.CUSTOMER_ID = m.Issuer and  m.finprod_type2 IN (''F01'',''F32''))
    OR (A.CUSTOMER_ID = m.Financier and  m.finprod_type2 = ''F18'')
  INNER JOIN RSK_SPECIFIC_ASSET_TYPE r ON m.FINPROD_ID = r.FINPROD_ID
  where r.Is_Credit_Asset = ''Y''AND A.INSITITUTE_CODE = (select i.institution_code insititute_code from sys_institution i where i.institution_type = ''01'')
UNION ALL
--2.没有进行特定资产设置或是否信贷资产为否  且 type_7为1204和1205的项目类资产
  SELECT m.FINPROD_ID  RKEY, ''03||信贷资产及其收受益权'' RVALUE
  FROM FIN_PRODUCT m
  INNER JOIN MST_CUSTOMER_INFO_ADD A ON  A.CUSTOMER_ID = m.Financier and  m.finprod_type2 = ''F18''
  INNER JOIN FIN_PRODUCT_TYPE n ON m.FINPROD_ID = n.FINPROD_ID AND n.BRANCH = 0
  LEFT JOIN RSK_SPECIFIC_ASSET_TYPE r ON n.FINPROD_ID = r.FINPROD_ID and r.Is_Credit_Asset=''N''
  where n.type_8 =''F06''AND n.type_7 in (''1204'',''1205'') AND A.INSITITUTE_CODE = (select i.institution_code insititute_code from sys_institution i where i.institution_type = ''01'')
UNION ALL
  SELECT m.FINPROD_ID  RKEY, ''04||非信贷资产及其收受益权'' RVALUE
  FROM FIN_PRODUCT m
  INNER JOIN MST_CUSTOMER_INFO_ADD A ON (A.CUSTOMER_ID = m.Issuer and  m.finprod_type2 IN (''F01'',''F32''))
    OR (A.CUSTOMER_ID = m.Financier and  m.finprod_type2 = ''F18'')
  INNER JOIN FIN_PRODUCT_TYPE n ON m.FINPROD_ID = n.FINPROD_ID AND n.BRANCH = 0
  LEFT JOIN RSK_SPECIFIC_ASSET_TYPE r ON n.FINPROD_ID = r.FINPROD_ID and r.Is_Credit_Asset=''N''
  where n.type_8 =''F06''AND n.type_7 not in (''1204'',''1205'') AND A.INSITITUTE_CODE = (select i.institution_code insititute_code from sys_institution i where i.institution_type = ''01'')
)
SELECT RKEY, RVALUE, RKEYCN FROM (
        SELECT SUM(NVL(target.SHARE_AMT,0)) SHARE_AMT, target.RKEY, ASSET.RVALUE, T2.FINPROD_NAME RKEYCN FROM (
                            SELECT NVL(PSP.SHARE_AMT,0) SHARE_AMT, PSP.FINPROD_ID RKEY
                            FROM PTL_SEC_PIERCE_VALUTION PSP
                            WHERE PSP.CDATE = DATE ''${valDate}'' - 1 AND PSP.PORTFOLIO_ID IN ${portfolioCode}
                             UNION ALL
                           --白天的交易
                            SELECT
                            CASE  WHEN n.PS IN(''T01'',''T04'',''T25'') THEN NVL(n.SHARE_AMT,0)
                                       WHEN n.PS =''T49'' THEN NVL(-1*n.SHARE_AMT*100,0)
                             ELSE  NVL(-1*n.SHARE_AMT,0)
                             END AS SHARE_AMT,
                                n.FINPROD_ID
                            FROM TRD_PRODUCT_DEAL_ADD m
                            INNER JOIN TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID
                            WHERE n.TRADE_DATE = DATE''${valDate}'' AND m.PORTFOLIO_ID IN ${portfolioCode}
                            AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T07'', ''T22'', ''T23'', ''T39'',''T25'', ''T49'')
                            AND n.TRADE_STATUS = ''01''
                            AND n.IS_CANCEL = ''N''
                          UNION ALL
                          --场内日间交易
                            SELECT
                             CASE  WHEN n.PS IN(''T01'',''T04'',''T25'') THEN NVL(n.SHARE_AMT,0)
                                       WHEN n.PS =''T49'' THEN NVL(-1*n.SHARE_AMT*100,0)
                             ELSE  NVL(-1*n.SHARE_AMT,0)
                             END AS SHARE_AMT,
                             n.FINPROD_ID
                            FROM TRD_PRODUCT_TEMP_DEAL_ADD m
                            INNER JOIN TRD_PRODUCT_TEMP_DEAL n ON m.TRADE_ID = n.TRADE_ID
                            WHERE n.TRADE_DATE = DATE''${valDate}'' AND m.PORTFOLIO_ID IN ${portfolioCode}
                            AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T07'', ''T22'', ''T23'', ''T39'',''T25'', ''T49'')
                            AND n.TRADE_STATUS = ''01''
                            AND n.IS_CANCEL = ''N''
                          UNION ALL
                          --流程中交易
                            SELECT
                             CASE  WHEN n.PS IN(''T01'',''T04'') THEN NVL(n.SHARE_AMT,0)
                                       WHEN n.PS =''T49'' THEN NVL(-1*n.SHARE_AMT*100,0)
                             ELSE  NVL(-1*n.SHARE_AMT,0)
                             END AS SHARE_AMT,
                             n.FINPROD_ID
                            FROM WFL_TRD_PRODUCT_DEAL_ADD m
                            INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS = ''01''
                            INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                            WHERE m.PORTFOLIO_ID IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
                            AND n.PS IN (''T01'', ''T02'', ''T03'', ''T04'', ''T49'')
                            AND n.IS_CANCEL = ''N''
                          UNION ALL
                          ---转仓交易
                            SELECT
                             nvl( n.SHARE_AMT, 0)  AS SHARE_AMT,
                             n.FINPROD_ID
                            FROM WFL_TRD_PRODUCT_DEAL_ADD m
                            INNER JOIN WFL_TRD_PRODUCT_DEAL n ON m.TRADE_ID = n.TRADE_ID AND n.TRADE_STATUS = ''01''
                            INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                            WHERE n.counter_id IN ${portfolioCode} AND n.TRADE_DATE = DATE''${valDate}''
                            AND n.PS =''T02''
                            AND EXISTS (SELECT 1 FROM SYS_PARA WHERE PARA_CODE = ''is_auto_generate_negate_deal'' AND PARAM_VALUE = ''Y'')
                            AND n.IS_CANCEL = ''N''
                           UNION ALL
                            --5.可转债网下申购：还未做申购确认
                                  SELECT nvl(T.prin_amt,0) SHARE_AMT,
                                    T.FINPROD_ID AS RKEY
                                  FROM TRD_PRODUCT_DEAL T
                                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                                 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
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
                                 --6.可转债网下申购：申购交易流程中
                                 SELECT NVL(T.prin_amt, 0) SHARE_AMT,
                                     T.FINPROD_ID AS RKEY
                                  FROM WFL_TRD_PRODUCT_DEAL T
                                 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                                 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                                 WHERE T1.PORTFOLIO_ID in ${portfolioCode} AND
                                    T.PS = ''T50''
                                   AND T.FINPROD_TYPE2 = ''F01''
                                   AND T.TRADE_STATUS = ''01''
                                   AND T.IS_CANCEL = ''N''
                                   AND T.TRADE_DATE = DATE''${valDate}''
                                union all
                                 --7.可转债网下申购：有申购确认交易
                                SELECT NVL(T.prin_amt, 0) SHARE_AMT,
                                   T.FINPROD_ID AS RKEY
                                  FROM TRD_PRODUCT_DEAL T
                                 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                                 WHERE T.TRADE_STATUS = ''01''
                                   AND T.IS_CANCEL = ''N''
                                   AND T1.PORTFOLIO_ID in ${portfolioCode}
                                   AND T.PS = ''T05''
                                   AND T.FINPROD_TYPE2 = ''F01''
                                  union all
                                 --8.可转债网下申购：申购确认交易(流程中)
                                SELECT NVL(T.prin_amt, 0) SHARE_AMT,
                                    T.FINPROD_ID AS RKEY
                                  FROM WFL_TRD_PRODUCT_DEAL T
                                 INNER JOIN WFL_TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
                                 INNER JOIN SYS_WFL_BUSINESS_LIST swbl on T1.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
                                 WHERE T1.PORTFOLIO_ID in  ${portfolioCode} AND
                                    T.PS = ''T05''
                                   AND T.TRADE_STATUS = ''01''
                                   AND T.IS_CANCEL = ''N''
                                   AND T.TRADE_DATE = DATE''${valDate}''
                                   AND T.FINPROD_TYPE2 = ''F01''
                       union all
                        --9.债转股（股票）
                          SELECT nvl(T.prin_amt,0) SHARE_AMT,
                             T.FINPROD_ID AS RKEY
                          FROM TRD_FIN_PRODUCT_BARTER T
                          INNER JOIN TRD_PRODUCT_DEAL T3 ON T.TRADE_ID = T3.TRADE_ID
                         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id               
                         WHERE T1.PORTFOLIO_ID in  ${portfolioCode} AND
                            T3.PS = ''T49''
                           AND T.FINPROD_TYPE = ''F10''
                           AND T3.TRADE_STATUS = ''01''
                           AND T3.IS_CANCEL = ''N''
                           AND T3.TRADE_DATE = DATE''${valDate}''
                           
                ) target                 
                 INNER JOIN FIN_PRODUCT T2 on target.RKEY = T2.FINPROD_ID      
                 INNER JOIN ASSET ON ASSET.RKEY = target.RKEY 
                GROUP BY target.RKEY, ASSET.RVALUE, T2.FINPROD_NAME
) WHERE SHARE_AMT > 0';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'BDTZCJZDZXDJQSYQCTH' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'BDTZCJZDZXDJQSYQCTH' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'BDTZCJZDZXDJQSYQCTH' and PARA_GRANULARITY = '05';
END;
/
