/**风险指标参数: 产品封闭期 **/

----风险监督指标参数
DELETE FROM RSK_ISUP_INDICATOR_PARA WHERE PARA_CODE ='CPFBQ';
INSERT INTO RSK_ISUP_INDICATOR_PARA (PARA_CODE, PARA_NAME, IS_FORBID, FORBID_ENUM, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, MODEL_CODE, MODEL_VALUE, IS_UP_REDIS, PARAM_DATA_TYPE, PARA_TYPE, PARAM_MATH_TYPE, PARAM_VALUE_NONE)
VALUES('CPFBQ','产品封闭期', 'N' , '' ,'system','1',sysdate,'system',sysdate, null , null , 'Y' , 'String' ,  null ,  'REDIS'  ,  null );

----监督指标参数明细
DELETE FROM RSK_ISUP_INDICATOR_PARA_DTL WHERE PARA_CODE ='CPFBQ';
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPFBQ','01', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPFBQ','02', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPFBQ','03', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPFBQ','04', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);
INSERT INTO RSK_ISUP_INDICATOR_PARA_DTL (PARA_CODE, PARA_GRANULARITY, PARA_DISPLAY_TYPE, CONTENT, REDIS_FIELD, IS_FRESH_CAN_TRD, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME)
VALUES('CPFBQ','05', 'SQL','select 1 from dual',  'portfolioCode' , 'N' ,'system','1',sysdate,'system',sysdate);

DECLARE
----实时、买入预占、卖出预占
V_CONTENT_01_02_03 CLOB := 'select 1 from dual';

----事后(日间)
V_CONTENT_04 CLOB := 'SELECT DISTINCT RVALUE
  FROM (
        ----开放式产品
        SELECT distinct RVALUE FROM (SELECT case
                                           when t1.OPEN_CYCLE_UNIT = ''01'' then
                                            round(t1.OPEN_CYCLE / 365, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''02'' then
                                            round(t1.OPEN_CYCLE * 7 / 365, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''03'' then
                                            round(t1.OPEN_CYCLE / 12, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''05'' then
                                            t1.OPEN_CYCLE
                                           else
                                            0
                                         end as RVALUE
                                    FROM FIN_OPEN_RULE t1
                                   inner join FIN_CASH_INFO t2
                                      on t1.CASH_ID = t2.CASH_ID
                                   inner join ptl_portfolio t3
                                      on t3.portfolio_id = t2.finprod_id
                                     and t3.portfolio_type = ''01''
                                   inner join PA_PTL_PORTFOLIO t4
                                      on t3.portfolio_id = t4.portfolio_id
                                     and t4.open_type = ''02''
                                   WHERE t4.portfolio_id in ${portfolioCode}
                                     and t2.CASH_TYPE_1 = ''01''
                                  union all
                                  SELECT case
                                           when t1.OPEN_CYCLE_UNIT = ''01'' then
                                            round(t1.OPEN_CYCLE / 365, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''02'' then
                                            round(t1.OPEN_CYCLE * 7 / 365, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''03'' then
                                            round(t1.OPEN_CYCLE / 12, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''05'' then
                                            t1.OPEN_CYCLE
                                           else
                                            0
                                         end as RVALUE
                                    FROM FIN_OPEN_RULE t1
                                   inner join FIN_CASH_INFO t2
                                      on t1.CASH_ID = t2.CASH_ID
                                   inner join FIN_PRODUCT_ADD F
                                      ON T2.FINPROD_ID = F.FINPROD_ID
                                   WHERE F.portfolio_id in ${portfolioCode}
                                     and t2.CASH_TYPE_1 = ''01'') union all
                         ----封闭式产品
                          select round(decode((t5.mdate - t5.vdate), null, 0,(t5.mdate - t5.vdate)) / 365, 2) RVALUE
          from ptl_portfolio t5
         inner join PA_PTL_PORTFOLIO t6
            on t6.portfolio_id = t5.portfolio_id
           and t6.open_type = ''01''
         where t5.portfolio_type = ''01''
           and t5.portfolio_id in ${portfolioCode})
 WHERE RVALUE > 0.5';

----事后(日终)
V_CONTENT_05 CLOB := 'SELECT DISTINCT RVALUE
  FROM (
        ----开放式产品
        SELECT distinct RVALUE FROM (SELECT case
                                           when t1.OPEN_CYCLE_UNIT = ''01'' then
                                            round(t1.OPEN_CYCLE / 365, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''02'' then
                                            round(t1.OPEN_CYCLE * 7 / 365, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''03'' then
                                            round(t1.OPEN_CYCLE / 12, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''05'' then
                                            t1.OPEN_CYCLE
                                           else
                                            0
                                         end as RVALUE
                                    FROM FIN_OPEN_RULE t1
                                   inner join FIN_CASH_INFO t2
                                      on t1.CASH_ID = t2.CASH_ID
                                   inner join ptl_portfolio t3
                                      on t3.portfolio_id = t2.finprod_id
                                     and t3.portfolio_type = ''01''
                                   inner join PA_PTL_PORTFOLIO t4
                                      on t3.portfolio_id = t4.portfolio_id
                                     and t4.open_type = ''02''
                                   WHERE t4.portfolio_id in ${portfolioCode}
                                     and t2.CASH_TYPE_1 = ''01''
                                  union all
                                  SELECT case
                                           when t1.OPEN_CYCLE_UNIT = ''01'' then
                                            round(t1.OPEN_CYCLE / 365, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''02'' then
                                            round(t1.OPEN_CYCLE * 7 / 365, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''03'' then
                                            round(t1.OPEN_CYCLE / 12, 2)
                                           when t1.OPEN_CYCLE_UNIT = ''05'' then
                                            t1.OPEN_CYCLE
                                           else
                                            0
                                         end as RVALUE
                                    FROM FIN_OPEN_RULE t1
                                   inner join FIN_CASH_INFO t2
                                      on t1.CASH_ID = t2.CASH_ID
                                   inner join FIN_PRODUCT_ADD F
                                      ON T2.FINPROD_ID = F.FINPROD_ID
                                   WHERE F.portfolio_id in ${portfolioCode}
                                     and t2.CASH_TYPE_1 = ''01'') union all
                         ----封闭式产品
                          select round(decode((t5.mdate - t5.vdate), null, 0,(t5.mdate - t5.vdate)) / 365, 2) RVALUE
          from ptl_portfolio t5
         inner join PA_PTL_PORTFOLIO t6
            on t6.portfolio_id = t5.portfolio_id
           and t6.open_type = ''01''
         where t5.portfolio_type = ''01''
           and t5.portfolio_id in ${portfolioCode})
 WHERE RVALUE > 0.5';

BEGIN
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01_02_03 WHERE PARA_CODE = 'CPFBQ' and PARA_GRANULARITY in('01','02','03');
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'CPFBQ' and PARA_GRANULARITY = '04';
UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'CPFBQ' and PARA_GRANULARITY = '05';
END;
/
