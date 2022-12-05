/**风险指标参数: 月末摊余成本法计量的产品净资产 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='YMTYCBFCPJZC';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('YMTYCBFCPJZC','月末摊余成本法计量的产品净资产', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'N' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='YMTYCBFCPJZC';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('YMTYCBFCPJZC','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('YMTYCBFCPJZC','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('YMTYCBFCPJZC','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('YMTYCBFCPJZC','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('YMTYCBFCPJZC','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'SELECT 1 FROM DUAL';

----事后(日间)
V_CONTENT_04 CLOB := 'select DECODE(date''${valDate}'', Last_day(date''${valDate}''), NVL(sum(amount),0), NULL) AS amount from
(
    select sum(amount) as amount
    from(
        SELECT
            DISTINCT t1.PORTFOLIO_ID PORTFOLIO_ID,
                     nvl(TDY_ASSET_NETVALUE,0) as AMOUNT
        from PTL_PORTFOLIO_VALUATION t1
        left join FIN_PRODUCT_ADD t2  on t2.FINPROD_ID =  ''F16_'' || t1.portfolio_id
        where  t1.CDATE=date''${valDate}''-1 and t1.PORTFOLIO_ID in ${portfolioCode} AND t2.IS_CASH_MANAGE = ''Y''
    )
    union all
    select
        nvl(sum(decode(n.PS,''C1'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        +nvl(sum(decode(n.PS,''C2'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        +nvl(sum(decode(n.PS,''C3'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C4'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C5'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C6'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C7'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C9'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C14'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C16'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C17'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C18'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C15'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C20'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as amount
    from TRD_PRODUCT_DEAL_ADD m
    inner join TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID
    left join  FIN_PRODUCT_ADD t2  on t2.FINPROD_ID = ''F16_'' || m.portfolio_id
    where m.PORTFOLIO_ID in ${portfolioCode}  and n.TRADE_DATE=date''${valDate}'' AND t2.IS_CASH_MANAGE = ''Y''
    group by m.PORTFOLIO_ID
    union all
    select
        nvl(sum(decode(n.PS,''C1'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        +nvl(sum(decode(n.PS,''C2'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        +nvl(sum(decode(n.PS,''C3'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C4'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C5'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C6'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C7'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C9'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C14'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C16'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C17'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C18'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C19'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C15'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0)
        -nvl(sum(decode(n.PS,''C20'',decode(TRADE_STATUS,''03'',n.TRADE_AMT*-1,n.TRADE_AMT))),0) as amount
    from WFL_TRD_PRODUCT_DEAL_ADD m
    inner join WFL_TRD_PRODUCT_DEAL n on m.TRADE_ID=n.TRADE_ID and n.TRADE_STATUS != ''03''
    INNER JOIN SYS_WFL_BUSINESS_LIST swbl on n.WORKFLOW_ID = swbl.SEQ_NO and swbl.PROCESS_STATE != ''BACK''
    left join FIN_PRODUCT_ADD t2  on t2.FINPROD_ID =  ''F16_'' || m.portfolio_id  AND t2.IS_CASH_MANAGE = ''Y''
    where m.PORTFOLIO_ID in ${portfolioCode}  and n.TRADE_DATE=date''${valDate}'' group by m.PORTFOLIO_ID
)';

----事后(日终)
V_CONTENT_05 CLOB := 'SELECT DECODE(date''${valDate}'', Last_day(date''${valDate}''), NVL(SUM(TDY_ASSET_NETVALUE),0), NULL) AS amount
FROM PTL_PORTFOLIO_VALUATION V
left join FIN_PRODUCT_ADD A on ''F16_'' || V.portfolio_id =  A.FINPROD_ID
WHERE V.PORTFOLIO_ID IN ${portfolioCode} AND V.CDATE = date''${valDate}'' AND A.IS_CASH_MANAGE = ''Y''';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'YMTYCBFCPJZC' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'YMTYCBFCPJZC' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'YMTYCBFCPJZC' and PARA_GRANULARITY = '05';
END;
/
