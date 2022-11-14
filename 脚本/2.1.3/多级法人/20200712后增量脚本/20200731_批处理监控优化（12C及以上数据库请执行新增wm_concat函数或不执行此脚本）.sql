CREATE OR REPLACE VIEW SYS_VW_BATCH_EXEC_DETAIL AS
select 
    aa.SEQ_NO,
    aa.BATCH_SEQ_NO,
    aa.PRD_GROUP_SEQ_NO,
    aa.DETAIL_NAME,
    aa.START_DATE,
    aa.END_DATE,
    aa.CREATE_USER,
    aa.CREATE_TIME,
    aa.UPDATE_TIME,
    -- del by xr 用xmlagg加上日期查询批处理监控还是挺慢的，更新成wm_concat函数后效率有大幅度提升，但11G以上数据库版本需执行新增wm_concat函数
    --或注释wm_concat韩式 使用xmlagg
   -- TRIM(',' FROM xmlagg(xmlparse(content aa.PRODUCT_ID||',' wellformed) ORDER BY aa.PRODUCT_ID
  --  ).getclobval()) PRODUCT_ID,
    to_char(wm_concat(aa.PRODUCT_ID)) as  PRODUCT_ID,
    aa.PROCESS_TIME,
    aa.STATUS,
    aa.EXEC_COUNT,
    aa.PERCENT,
    aa.REAL_NAME
from(
  SELECT
    A.SEQ_NO,
    A.BATCH_SEQ_NO,
    A.PRD_GROUP_SEQ_NO,
    A.DETAIL_NAME,
    A.START_DATE,
    A.END_DATE,
    A.CREATE_USER,
    A.CREATE_TIME,
    A.UPDATE_TIME,
    SBGP.PRODUCT_ID,
--    TRIM(',' FROM xmlagg(xmlparse(content SBGP.PRODUCT_ID||',' wellformed) ORDER BY SBGP.PRODUCT_ID
--    ).getclobval()) PRODUCT_ID,
    /* listagg(SBGP.PRODUCT_ID, ',')  within group ( order by SBGP.PRODUCT_ID ) PRODUCT_ID,*/
    (extract(DAY FROM a.update_time-a.create_time) * 86400+ extract(hour FROM a.update_time-
    a.create_time) * 3600 + extract(minute FROM a.update_time-a.create_time) * 60+ extract(second
    FROM a.update_time-a.create_time)) * 1000 PROCESS_TIME,
    A.STATUS,
    COUNT(B.SEQ_NO)                       EXEC_COUNT,
    ROUND(endNumber.num/allNumber.num, 2) PERCENT,
    NVL(s.REAL_NAME, '自动批处理')             REAL_NAME
FROM
    SYS_BATCHGRP_EXEC_INFO A
LEFT JOIN
    SYS_BATCH_EXEC_INFO B
ON
    A.SEQ_NO = B.BATCH_EXEDETAIL_SEQ_NO
AND B.TASK_STATUS = 'EXP'
LEFT JOIN
    sys_user s
ON
    a.CREATE_USER = s.USER_NAME
LEFT JOIN
    SYS_BATCH_GRP_PRDLINK SBGP
ON
    A.PRD_GROUP_SEQ_NO = SBGP.GROUP_SEQ
LEFT JOIN
    (
        SELECT
            BATCH_EXEDETAIL_SEQ_NO exeSeqNo,
            COUNT(1)               num
        FROM
            SYS_BATCH_EXEC_INFO
        WHERE
            (
                TASK_STATUS = 'END'
            OR  TASK_STATUS='EXP'
            OR  TASK_STATUS='BRK')
        GROUP BY
            BATCH_EXEDETAIL_SEQ_NO) endNumber
ON
    A.SEQ_NO = endNumber.exeSeqNo
LEFT JOIN
    (
        SELECT
            BATCH_EXEDETAIL_SEQ_NO exeSeqNo,
            COUNT(1)               num
        FROM
            SYS_BATCH_EXEC_INFO
        GROUP BY
            BATCH_EXEDETAIL_SEQ_NO) allNumber
ON
    A.SEQ_NO = allNumber.exeSeqNo
GROUP BY
    A.SEQ_NO,
    A.BATCH_SEQ_NO,
    A.PRD_GROUP_SEQ_NO,
    A.DETAIL_NAME,
    A.START_DATE,
    A.END_DATE,
    A.CREATE_USER,
    A.CREATE_TIME,
    A.UPDATE_TIME,
    endNumber.num,
    allNumber.num,
    s.REAL_NAME,
    A.STATUS,
    SBGP.PRODUCT_ID
) aa
GROUP BY
    aa.SEQ_NO,
    aa.BATCH_SEQ_NO,
    aa.PRD_GROUP_SEQ_NO,
    aa.DETAIL_NAME,
    aa.START_DATE,
    aa.END_DATE,
    aa.CREATE_USER,
    aa.CREATE_TIME,
    aa.UPDATE_TIME,
    aa.PROCESS_TIME,
    aa.STATUS,
    aa.EXEC_COUNT,
    aa.PERCENT,
    aa.REAL_NAME
ORDER BY
    aa.CREATE_TIME DESC
;
