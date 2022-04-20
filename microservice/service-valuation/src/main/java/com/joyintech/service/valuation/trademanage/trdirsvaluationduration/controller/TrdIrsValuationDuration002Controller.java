/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdirsvaluationduration.controller;


import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationResult;
import com.joyintech.service.valuation.trademanage.trdirsvaluationduration.dto.TrdIrsValuationDuration002Dto;
import com.joyintech.service.valuation.trademanage.trdirsvaluationduration.dto.TrdIrsValuationDuration003Dto;
import com.joyintech.service.valuation.trademanage.trdirsvaluationduration.service.TrdIrsValuationDuration002Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 利率互换交易微服务
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月16日
 * @see TrdIrsValuationDuration002Controller
 * @since 1.0
 */
@RestController(value="serviceTrdIrsValuationDuration002Controller")
@RequestMapping("/TrdIrsValuationDuration002")
public class TrdIrsValuationDuration002Controller {

    @Autowired
    private TrdIrsValuationDuration002Service trdIrsValuationDuration002Service;

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdIrsValuationDuration002Dto queryTradeInfoByTradeId(@RequestBody String tradeId) {
        return trdIrsValuationDuration002Service.queryTradeInfoByTradeId(tradeId);
    }

    /**
     * 原始交易编号下拉框
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("getTradeId")
    public List<SelectOptionDto> getTradeId(@RequestBody Map<String, Object> param) {
        return trdIrsValuationDuration002Service.getTradeId(param);
    }

    /**
     * 根据原始交易编查询交易信息
     *
     * @param pTradeId 参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @PostMapping("findInfoByTradeId")
    public TrdIrsValuationDuration003Dto findInfoByTradeId(@RequestBody String pTradeId) {
        return trdIrsValuationDuration002Service.findInfoByTradeId(pTradeId);
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
        int  count = trdIrsValuationDuration002Service.querySequences();
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
    public int insert(@RequestBody TrdBondValuationDurationResult dto)
            throws Exception {
        return trdIrsValuationDuration002Service.insert(dto);
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
        return trdIrsValuationDuration002Service.update(dto);
    }

}
