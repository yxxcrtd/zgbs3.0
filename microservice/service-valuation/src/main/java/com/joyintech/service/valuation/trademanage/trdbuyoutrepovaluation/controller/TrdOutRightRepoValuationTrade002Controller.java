/*
 * 文件名：TaxBalanceQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade003Dto;
import com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.service.TrdOutRightRepoValuationTrade002Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * service 买断式回购交易
 * @see TrdOutRightRepoValuationTrade002Controller
 * @since 1.0
 */
@RestController(value="serviceTrdOutRightRepoValuationTrade002Controller")
@RequestMapping("/TrdOutRightRepoValuationTrade002")
public class TrdOutRightRepoValuationTrade002Controller extends BaseController {

    @Autowired
    private TrdOutRightRepoValuationTrade002Service trdOutRightRepoValuationTrade002Service;

    /**
     * 产品代码下拉框
     *
     * @return 返回的数据
     * @exception Exception
     *                异常
     * @since 1.0
     */
    @PostMapping("findPortfolioId/{page}/{length}")
    public Page<SelectOptionDto> findPortfolioId(@RequestBody Map<String,Object> map,
                                           @PathVariable("page") int page, @PathVariable("length") int length) {
        return trdOutRightRepoValuationTrade002Service.findPortfolioId(page,length,map);
    }

    /**
     * 资产代码下拉框
     */
    @PostMapping("findFinprodId/{page}/{length}")
    public Page<SelectOptionDto> findFinprodId(@RequestBody Map<String,Object> map,
                                               @PathVariable("page") int page, @PathVariable("length") int length) {
        return trdOutRightRepoValuationTrade002Service.findFinprodId(page,length,map);
    }


    /**
     * 回调函数 操作具体 new delete update <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/doComplete/{type}")
    public void doComplete(@RequestBody TrdOutRightRepoValuationTrade003Dto dto, @PathVariable("type") String type) {
        dto.setOperateType(type);
        trdOutRightRepoValuationTrade002Service.doComplete(dto);
    }



    /**
     * 根据交易编号查询对象  到期确认
     * @see
     * @since 1.0
     */
    @PostMapping("queryTradeInfoByTradeId01")
    public TrdOutRightRepoValuationTrade003Dto queryTradeInfoByTradeId01(@RequestBody Map<String, Object> param) {
        return trdOutRightRepoValuationTrade002Service.queryTradeInfoByTradeId01(param);
    }


    /**
     * 策略代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createPlanTradeId")
    public String createPlanTradeId() {
        return trdOutRightRepoValuationTrade002Service.createPlanTradeId();
    }



}
