/*
 * 文件名：FieldEtrInfoQueryMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：msk
 * 修改时间：2021年12月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.common.dao;

import com.joyintech.tams.business.common.dto.CommonSearchQueryWorkflowResponse;
import com.joyintech.tams.business.common.dto.CommonSearchRskInfoResponse;
import com.joyintech.tams.business.common.dto.CommonSearchInfoOperateListResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 场内指令查询
 * @author msk
 * @version 1.0 2021年12月22日
 * @see EtrCommonTableInfoQueryMapper
 * @since 1.0
 */
@Repository
public interface EtrCommonTableInfoQueryMapper {

    /**
     * 操作记录查询
     *
     * @param pageParam
     * @return List<FieldEtrInfoOperateListResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    List<CommonSearchInfoOperateListResponse> getCommonSearchTabInfoQueryOperate(Map<String, Object> pageParam);

    /**
     * 风险试算查询
     *
     * @param pageParam
     * @return Page<FieldEtrDispatcherRskInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    List<CommonSearchRskInfoResponse> getCommonSearchTabRskInfo(Map<String, Object> pageParam);

    /**
     * 审批记录分页查询
     * @param pageParam 参数
     * @return List<CfetsManualSettleTraderListRespond>
     */
    List<CommonSearchQueryWorkflowResponse> queryAllWorkFlowInfoList(Map<String, Object> pageParam);

    List<String> queryTotalEntrustIdList(Map<String, Object> pageParam);
}
