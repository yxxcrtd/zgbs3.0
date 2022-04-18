/*
 * 文件名：TrdDepositInvestTradeController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trddepositvaluationtrade.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
//import com.joyintech.service.trade.trddepositinvesttrade.dto.TrdDepositInvestTrade001Dto;
//import com.joyintech.service.trade.trddepositinvesttrade.service.TrdDepositInvestTrade001Service;
//import com.joyintech.tams.common.base.entity.InterfSendOpics;
//import com.joyintech.tams.common.base.entity.StlPayOrders;
//import com.joyintech.tams.constant.DictCodeListExt1;
//import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
//import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.service.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTrade001Dto;
import com.joyintech.service.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTradeResult;
import com.joyintech.service.valuation.trademanage.trddepositvaluationtrade.service.TrdDepositValuationTrade001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 存款类投资交易查询
 * @author fanshuanghu
 * @version 1.0 2018年10月10日
 * @see TrdDepositValuationTrade001Controller
 * @since 1.0
 */
@RestController("TrdDepositValuationTrade001Ctr_dtx")
@RequestMapping("/TrdDepositValuationTrade001")
public class TrdDepositValuationTrade001Controller {
    /**
     * 注入存款类投资交易Service
     */
    @Autowired
    private TrdDepositValuationTrade001Service trdDepositValuationTrade001Service;

    /**
     * 交易对手下拉框

     */
    @PostMapping("getCounterId/{page}/{length}")
    public Page<SelectOptionDto> getCounterId(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page,
                                              @PathVariable("length") int length){
        return trdDepositValuationTrade001Service.getCounterId(page, length, param);
    }

    @RequestMapping("findPage/{page}/{length}")
    public Page<TrdDepositValuationTrade001Dto> queryForPage(@RequestBody Map<String, Object> param,
                                                 @PathVariable("page") int page, @PathVariable("length") int length)
            throws Exception {
        return trdDepositValuationTrade001Service.selectForPage(page, length, param);
    }

    /**
     * 根据交易编号查询对象
     * @see
     * @since 1.0
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdDepositValuationTrade001Dto queryTradeInfoByTradeId(@RequestBody Map<String, Object> param) {
        return trdDepositValuationTrade001Service.queryTradeInfoByTradeId(param);
    }

    /**
     * 删除
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdDepositValuationTrade001Dto dto){
        return trdDepositValuationTrade001Service.delete(dto);
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
    public int insert(@RequestBody TrdDepositValuationTradeResult dto)
            throws Exception {
        return trdDepositValuationTrade001Service.insert(dto);
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
    public int update(@RequestBody TrdDepositValuationTradeResult dto)
            throws Exception {
        return trdDepositValuationTrade001Service.update(dto);
    }
}
