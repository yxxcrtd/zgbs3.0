/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.option.controller;


import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.option.dto.TrdOptionValuationTrade002Dto;
import com.joyintech.service.valuation.trademanage.option.dto.TrdOptionValuationTrade003Dto;
import com.joyintech.service.valuation.trademanage.option.dto.TrdOptionValuationTradeResult;
import com.joyintech.service.valuation.trademanage.option.service.TrdOptionValuationTrade002Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 场外期权交易微服务
 *
 * @author sunshuaishuai
 * @version 1.0 2022年01月07日
 * @see TrdOptionValuationTrade002Controller
 * @since 1.0
 */
@RestController(value="serviceTrdOptionValuationTrade002Controller")
@RequestMapping("/TrdOptionValuationTrade002")
public class TrdOptionValuationTrade002Controller {

    @Autowired
    private TrdOptionValuationTrade002Service trdOptionValuationTrade002Service;
    
    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdOptionValuationTrade002Dto queryTradeInfoByTradeId(@RequestBody String tradeId) {
        return trdOptionValuationTrade002Service.queryTradeInfoByTradeId(tradeId);
    }

    /**
     * 通过交易编号查询交易信息（到期）
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("queryInfoByTradeId")
    public TrdOptionValuationTrade003Dto queryInfoByTradeId(@RequestBody String tradeId) {
        return trdOptionValuationTrade002Service.queryInfoByTradeId(tradeId);
    }

    /**
     * 交易对手查询
     * @param param
     * @return 交易对手下拉框
     */
    @RequestMapping("getCounterId")
    public List<SelectOptionDto> getCounterId(@RequestBody Map<String, Object> param){
        return trdOptionValuationTrade002Service.getCounterId(param);
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
        int  count = trdOptionValuationTrade002Service.querySequences();
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
    public int insert(@RequestBody TrdOptionValuationTradeResult dto)
            throws Exception {
        return trdOptionValuationTrade002Service.insert(dto);
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
    public int update(@RequestBody TrdOptionValuationTradeResult dto)
            throws Exception {
        return trdOptionValuationTrade002Service.update(dto);
    }

}
