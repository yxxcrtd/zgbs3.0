/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdbondvaluationduration.controller;


import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.service.valuation.component.common.dto.FinCashPlanDto;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationDto;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationResult;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.service.TrdBondValuationDuration002Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 股票交易微服务
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月11日
 * @see TrdBondValuationDuration002Controller
 * @since 1.0
 */
@RestController(value="serviceTrdBondValuationDuration002Controller")
@RequestMapping("/TrdBondValuationDuration002")
public class TrdBondValuationDuration002Controller {

    @Autowired
    private TrdBondValuationDuration002Service trdBondValuationDuration002Service;

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdBondValuationDurationDto queryTradeInfoByTradeId(@RequestBody String tradeId) {
        return trdBondValuationDuration002Service.queryTradeInfoByTradeId(tradeId);
    }

    /**
     *
     * 查询债券付息计划
     *
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("/getFinCashPlanByProductId")
    public List<FinCashPlanDto> getFinCashPlanByProductId(@RequestBody Map<String, Object> param){
        return trdBondValuationDuration002Service.getFinCashPlanByProductId(param);
    }

    /**
     * 插入
     *
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("insert")
    public int insert(@RequestBody TrdBondValuationDurationResult dto)
            throws Exception {
        return trdBondValuationDuration002Service.insert(dto);
    }

    /**
     * 修改
     *
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("update")
    public int update(@RequestBody TrdBondValuationDurationResult dto)
            throws Exception {
        return trdBondValuationDuration002Service.update(dto);
    }

}
