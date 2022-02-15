/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdstockvaluationduration.controller;


import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.service.valuation.trademanage.trdstockvaluationduration.dto.TrdStockValuationDuration002Dto;
import com.joyintech.service.valuation.trademanage.trdstockvaluationduration.dto.TrdStockValuationDurationResult;
import com.joyintech.service.valuation.trademanage.trdstockvaluationduration.service.TrdStockValuationDuration002Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 股票交易微服务
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月15日
 * @see TrdStockValuationDuration002Controller
 * @since 1.0
 */
@RestController(value="serviceTrdStockValuationDuration002Controller")
@RequestMapping("/TrdStockValuationDuration002")
public class TrdStockValuationDuration002Controller {

    @Autowired
    private TrdStockValuationDuration002Service trdStockValuationDuration002Service;

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdStockValuationDuration002Dto queryTradeInfoByTradeId(@RequestBody String tradeId) {
        return trdStockValuationDuration002Service.queryTradeInfoByTradeId(tradeId);
    }

    /**
     *
     * 编号查询
     *
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("/querySequences")
    public int querySequences(){
        int  count = trdStockValuationDuration002Service.querySequences();
        return count;
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
    public int insert(@RequestBody TrdStockValuationDurationResult dto)
            throws Exception {
        return trdStockValuationDuration002Service.insert(dto);
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
    public int update(@RequestBody TrdStockValuationDurationResult dto)
            throws Exception {
        return trdStockValuationDuration002Service.update(dto);
    }

}
