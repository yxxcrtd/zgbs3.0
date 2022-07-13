/*
 * 文件名：FinProductModule002Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年8月31日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.period.trdrepoduration.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.TrdRepoDurationDto;

/**
 * 债券存续期管理
 *
 * @author 强成西
 * @version 1.0 2018年8月31日
 * @see TrdRepoDurationMapper
 * @since 1.0
 */
@Repository
public interface TrdRepoDurationMapper {

    List<SelectOptionDto> getPortfolioId(Map<String, Object> param);

    List<TrdRepoDurationDto> findPage(Map<String, Object> param);

    List<TrdRepoDurationDto> selectAllData(Map<String, Object> param);

    TrdRepoDurationDto findEffectiveData(Map<String, Object> param);

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

    void insertSelect04SecFlowByDate(Map<String, Object> params);

    void delete04Flow(Map<String, Object> params);

    List<PtlSecPositionFlow> selectSecFlow(Map<String, Object> params);

    void insertSelect04Sec(PtlSecPositionFlow flow);
    /**
     * 查询交易场所，到期日
     *findTrdMarketByFinprodId
     */
    TrdRepoDurationDto findTrdMarketByFinprodId(Map<String, Object> param);
    /**
     * 根据汇总表的主键查询续做信息
     *findTrdMarketByFinprodId
     */
    TrdRepoDurationDto findTrdDealInfoByBusiCode(Map<String, Object> params);
    /**
     * 根据汇总表的主键查询续做信息新生成首期交易编号
     *findFinprodIdByBusiCode
     */
    String findFinprodIdByTotleTradeId(String totleTradeId);
    

    Map<String, Object> selectTradeReleaseStatus(String tradeid);

	int updateReleaseStatus(String tradeid);
	
	int updateReleaseStatusRevoke(String tradeid);

	/**
     * 撤销时根据汇总表的主键查询授信交易编号
     *findFinprodIdByBusiCode
     */
	String findCeTradeIdByTotleTradeId(String totleTradeId);
	
	List<String> findCeTradeIdListByTotleTradeId(String totleTradeId);
}
