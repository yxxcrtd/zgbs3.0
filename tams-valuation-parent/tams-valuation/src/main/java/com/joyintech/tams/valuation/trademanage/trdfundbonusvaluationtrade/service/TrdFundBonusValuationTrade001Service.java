/*
 * 文件名：TrdPlanRateInvestTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.dto.TrdFundBonusValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundValuationTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 计划类资产投资交易查询
 * @author fanshuanghu
 * @version 1.0 2018年11月22日
 * @see TrdFundBonusValuationTrade001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class TrdFundBonusValuationTrade001Service extends CommonExportService<TrdFundBonusValuationTradeDto> implements BaseProcessService<TrdFundBonusValuationTradeDto> {
    /**
     * 查询
     * @param pageParam Map
     * @return Page类
     * @throws Exception 异常信息
     */
    public Page<TrdFundBonusValuationTradeDto> selectForPage(Map<String, Object> pageParam){
        return restTemplate.postForPage(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundBonusValuationTrade/selectForPage",
                pageParam, TrdFundBonusValuationTradeDto.class);
    }

    /**
     * 工作流（留痕处理进行的操作）
     */
    @Override
    public void doComplete(TrdFundBonusValuationTradeDto entity, OperationType type) {
        String action = "delete";
        simpleRestTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdFundBonusValuationTrade/" + action, entity, Integer.class);
    }


    /**
     * 根据条件查询所有
     *
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    public List<TrdFundBonusValuationTradeDto> findAll(Map<String, Object> params) {
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundBonusValuationTrade/findAll",
                params, TrdFundBonusValuationTradeDto.class);

    }





}
