/*
 * 文件名：TaxBalanceQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade003Dto;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.service.TrdRepoValuationTrade002Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * service 质押式回购交易
 * 
 * @author yuzj
 * @version 1.0 2021年12月29日
 * @see TrdRepoValuationTrade002Controller
 * @since 1.0
 */
@RestController(value="serviceTrdRepoValuationTrade002Controller")
@RequestMapping("/TrdRepoValuationTrade002")
public class TrdRepoValuationTrade002Controller extends BaseController {

    @Autowired
    private TrdRepoValuationTrade002Service trdRepoValuationTrade002Service;



    /**
     * 回调函数 操作具体 new delete update <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/doComplete/{type}")
    public void doComplete(@RequestBody TrdRepoValuationTrade003Dto dto, @PathVariable("type") String type) {
        dto.setOperateType(type);
        trdRepoValuationTrade002Service.doComplete(dto);
    }



    /**
     * 策略代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createPlanTradeId")
    public String createPlanTradeId() {
        return trdRepoValuationTrade002Service.createPlanTradeId();
    }


    /**
     * 根据交易编号查询对象
     * @see
     * @since 1.0
     */
    @PostMapping("queryTradeInfoByTradeId01")
    public TrdRepoValuationTrade003Dto queryTradeInfoByTradeId01(@RequestBody Map<String, Object> params) {
        return trdRepoValuationTrade002Service.queryTradeInfoByTradeId01(params);
    }


}
