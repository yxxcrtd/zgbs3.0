package com.joyintech.tams.invest.batch.common.dao;

import com.joyintech.framework.base.entity.SysBatchJob;
import com.joyintech.tams.common.base.entity.BokBatchTaskLink;
import com.joyintech.tams.common.base.entity.BokBatchTaskTodo;
import com.joyintech.tams.invest.batch.common.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author jinmaodong
 * @version 1.0 2016年12月5日
 * @see BatchDispatchMapper
 * @since 1.0
 */
@Repository
public interface BatchDispatchMapper {

    /**
     * 查询每个账套对应账套日所需跑批的最小发生日
     * 
     * @param params 参数
     * @return 交易信息
     * @see
     * @since 1.0
     */
    List<DealAccountingTodo> selectDealAccounting(Map<String, Object> params);

    /**
     * 查询每个账套对应账套日所需跑批的最小发生日,不考虑倒起息
     *
     * @param params 参数
     * @return 交易信息
     * @see
     * @since 1.0
     */
    List<DealAccountingTodo> selectDealAccountingNoBackValue(Map<String, Object> params);


    /**
     * 
     * <一句话功能简述>
     * <功能详细描述>
     * @param batchSeqNo
     * @return
     * @see
     * @since 1.0
     */
    List<BatchTaskInfo> selectBatchJob(List<String> batchGroupSeqNos);
    
    /**
     * 
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    List<BokBatchTaskLink> selectBatchTaskLink();
    
    /**
     * 
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    List<BokBatchTaskTodo> selectBatchTaskTodo(@Param(value = "seqNo") String seqNo);
    
    List<BatchDimensionConfig> selectBatchDimensionConfig();
    
    void insertDimValue(List<DimensionValue> dimValueList);
    
    void insertBatchGrpPri(List<BatchGrpPri> batchGrpPriList);
    
    void insertBatchJob(List<SysBatchJob> batchJobList);
    
    void insertBokBatchTaskTodo(List<BokBatchTaskTodo> bokBatchTaskTodoList);
    
    void deleteOldData(Date oldDate);
}
