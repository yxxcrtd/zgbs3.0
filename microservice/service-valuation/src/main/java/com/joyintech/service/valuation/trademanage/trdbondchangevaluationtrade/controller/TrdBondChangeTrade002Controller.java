/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.controller;


import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTrade002Dto;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeFundResult;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeResult;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.service.TrdBondChangeTrade002Service;
import com.joyintech.tams.common.base.entity.FinProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 证券转换交易微服务
 *
 * @author sunshuaishuai
 * @version 1.0 2022年01月04日
 * @see TrdBondChangeTrade002Controller
 * @since 1.0
 */
@RestController(value="serviceTrdBondChangeTrade002Controller")
@RequestMapping("/TrdBondChangeTrade002")
public class TrdBondChangeTrade002Controller {

    @Autowired
    private TrdBondChangeTrade002Service trdBondChangeTrade002Service;

    /**
     * 通过资产代码查询资产简称和交易场所
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("selectInfoBySecId")
    public FinProduct selectInfoBySecId(@RequestBody Map<String, Object> param) {
        return trdBondChangeTrade002Service.selectInfoBySecId(param);
    }

    /*
    * 转出证券代码下拉框显示可转债资产
    *
    */
    @PostMapping("/getSecExgId/{page}/{length}")
    public Page<SelectOptionDto> getSecExgId(@RequestBody Map<String, Object> map, @PathVariable("page") int page,
                                            @PathVariable("length") int length){
        return trdBondChangeTrade002Service.getSecExgId(page,length,map);
    }

    /*
     * 转出证券代码下拉框显示基金
     *
     */
    @PostMapping("/getFundId/{page}/{length}")
    public Page<SelectOptionDto> getFundId(@RequestBody Map<String, Object> map, @PathVariable("page") int page,
                                             @PathVariable("length") int length){
        return trdBondChangeTrade002Service.getFundId(page,length,map);
    }

    /*
     * 展示股票代码和基金
     *
     */
    @PostMapping("/getCAssetId/{page}/{length}")
    public Page<SelectOptionDto> getCAssetId(@RequestBody Map<String, Object> map, @PathVariable("page") int page,
                                           @PathVariable("length") int length){
        return trdBondChangeTrade002Service.getCAssetId(page,length,map);
    }

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdBondChangeTrade002Dto queryTradeInfoByTradeId(@RequestBody String tradeId) {
        return trdBondChangeTrade002Service.queryTradeInfoByTradeId(tradeId);
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
        int  count = trdBondChangeTrade002Service.querySequences();
        return count;
    }

    /**
     * 插入（债转股）
     *
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("insert")
    public int insert(@RequestBody TrdBondChangeTradeResult dto)
            throws Exception {
        return trdBondChangeTrade002Service.insert(dto);
    }

    /**
     * 修改（债转股）
     *
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("update")
    public int update(@RequestBody TrdBondChangeTradeResult dto)
            throws Exception {
        return trdBondChangeTrade002Service.update(dto);
    }

    /**
     * 插入（基金）
     *
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("insertFund")
    public int insertFund(@RequestBody TrdBondChangeTradeFundResult dto)
            throws Exception {
        return trdBondChangeTrade002Service.insertFund(dto);
    }

    /**
     * 修改（基金）
     *
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("updateFund")
    public int updateFund(@RequestBody TrdBondChangeTradeFundResult dto)
            throws Exception {
        return trdBondChangeTrade002Service.updateFund(dto);
    }

}
