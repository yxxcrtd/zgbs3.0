package com.joyintech.tams.invest.batch.common.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 批定时任务Mapper
 *
 * @author 王波
 * @version 1.0 2019年2月13日
 * @see ValBatchCronJobMapper
 * @since 1.0
 */
@Repository
public interface ValBatchCronJobMapper {
    /**
     * 通过任务组ID查询所有任务
     * @param taskGrpId 任务组ID
     * @return
     */
    List<String> findAllTaskbyGroup(@Param("taskGrpId")String taskGrpId);

    /**
     * 通过产品组Id 查询所有产品Seqno
     * @param prodGrpId prodGrpId
     * @return
     */
    List<String> findAllProdbyGroup(@Param("prodGrpId")String prodGrpId);

    Date getSysDate();

    Date getCommonSysDate();
}
