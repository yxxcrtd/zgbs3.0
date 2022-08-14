/*
 * 文件名：TrdSecLendDurationMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.period.trdseclendduration.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.period.trdseclendduration.TrdSecLendDurationDto;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.TrdRepoDurationDto;

/**
 * 债券借贷存续期管理
 *
 * @author 裴家俊
 * @version 1.0 2019年7月1日
 * @see TrdSecLendDurationMapper
 * @since 1.0
 */
@Repository
public interface TrdSecLendDurationMapper {

    List<SelectOptionDto> getPortfolioId(Map<String, Object> param);

    List<TrdSecLendDurationDto> findPage(Map<String, Object> param);

    BigDecimal getShowHandNum(Map<String, Object> param);



    TrdSecLendDurationDto findEffectiveData(Map<String, Object> param);

    List<RepoInfo> findRepoInfo(Map<String, Object> params);

    List<FinCashPlan> findFinCashInfo(String finprodId);

    WflTrdFinProductDealTotal findTotalByWorkflowId(String workflowId);

    List<WflStlPayOrders> findOrdersByWorkflowId(String workflowId);

    WflPaTrdFinDealTotal findPaTotalByWorkflowId(String workflowId);

    List<WflTrdProductDeal> findDealByWorkflowId(String workflowId);

    String findPrdRegCode(Map<String, Object> params);

    List<SelectOptionDto> getBusiCode(Map<String, Object> param);

    List<WflPaTrdProductDeal> findPaDealByWorkflowId(String workflowId);

    List<WflTrdProductDealAdd> findAddDealByWorkflowId(String workflowId);

    void insertSelect04SecFlow(Map<String, Object> params);

    void delete04Flow(Map<String, Object> params);

    PtlSecPositionFlow selectSecFlow(Map<String, Object> params);

    void insertSelect04Sec(PtlSecPositionFlow flow);
    
    String selectCounterIdByTotleTradeId(String totleTradeId);
    
    String findCeTradeIdByWorkflowId(String workflowId);
}
