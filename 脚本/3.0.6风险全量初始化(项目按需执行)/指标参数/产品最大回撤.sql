/**风险指标参数: 产品最大回撤 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='CPZDHC';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('CPZDHC','产品最大回撤', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='CPZDHC';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZDHC','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZDHC','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZDHC','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZDHC','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPZDHC','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'select 1 from dual';

----事后(日间)
V_CONTENT_04 CLOB := 'with jzDayData as (
select
max(t1.TDY_UNIT_VALUE_SUM) as jzDayValue
from PTL_PORTFOLIO_VALUATION t1
inner join PTL_PORTFOLIO pp on t1.PORTFOLIO_ID = pp.PORTFOLIO_ID and t1.CDATE = pp.VDATE
where t1.PORTFOLIO_ID in ${portfolioCode}
),
allLjSylData as (
select
t1.CDATE,
(nvl(t1.TDY_UNIT_VALUE_SUM, 0) - nvl(jzDayData.jzDayValue, 0)) / nvl(jzDayData.jzDayValue, 0) as ljSylValue
from PTL_PORTFOLIO_VALUATION t1
inner join jzDayData on 1=1
where t1.PORTFOLIO_ID in ${portfolioCode} and t1.CDATE <= date ''${valDate}''
order by t1.CDATE
)
select
max(
round(allLjSylData1.ljSylValue -
(select min(allLjSylData2.ljSylValue) from allLjSylData allLjSylData2 where allLjSylData2.CDATE <= allLjSylData1.CDATE), 6)
)
as resultValue
from allLjSylData allLjSylData1';

----事后(日终)
V_CONTENT_05 CLOB := 'with jzDayData as (
select
max(t1.TDY_UNIT_VALUE_SUM) as jzDayValue
from PTL_PORTFOLIO_VALUATION t1
inner join PTL_PORTFOLIO pp on t1.PORTFOLIO_ID = pp.PORTFOLIO_ID and t1.CDATE = pp.VDATE
where t1.PORTFOLIO_ID in ${portfolioCode}
),
allLjSylData as (
select
t1.CDATE,
(nvl(t1.TDY_UNIT_VALUE_SUM, 0) - nvl(jzDayData.jzDayValue, 0)) / nvl(jzDayData.jzDayValue, 0) as ljSylValue
from PTL_PORTFOLIO_VALUATION t1
inner join jzDayData on 1=1
where t1.PORTFOLIO_ID in ${portfolioCode} and t1.CDATE <= date ''${valDate}''
order by t1.CDATE
)
select
max(
round(allLjSylData1.ljSylValue -
(select min(allLjSylData2.ljSylValue) from allLjSylData allLjSylData2 where allLjSylData2.CDATE <= allLjSylData1.CDATE), 6)
)
as resultValue
from allLjSylData allLjSylData1';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'CPZDHC' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'CPZDHC' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'CPZDHC' and PARA_GRANULARITY = '05';
END;
/
