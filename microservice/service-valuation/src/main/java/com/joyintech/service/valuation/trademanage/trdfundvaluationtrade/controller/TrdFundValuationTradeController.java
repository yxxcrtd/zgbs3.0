/*
 * 文件名：TaxBalanceQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdfundvaluationtrade.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.service.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundTradeResult;
import com.joyintech.service.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundValuationTradeDto;
import com.joyintech.service.valuation.trademanage.trdfundvaluationtrade.service.TrdFundValuationTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * service 基金交易
 * 
 * @author yuzj
 * @version 1.0 2021年01月04日
 * @see TrdFundValuationTradeController
 * @since 1.0
 */
@RestController(value="serviceTrdFundValuationTradeController")
@RequestMapping("/TrdFundValuationTrade")
public class TrdFundValuationTradeController extends BaseController {

    @Autowired
    private TrdFundValuationTradeService trdFundValuationTradeService;

    /**
     * 查询 支持分页 <功能详细描述>
     *
     * @since 1.0
     */
    @RequestMapping("/selectForPage/{page}/{length}")
    public Page<TrdFundValuationTradeDto> selectForPage(@RequestBody Map<String, Object> pageParam, @PathVariable("page") int page,
                                                        @PathVariable("length") int length)
            throws Exception {
        Page<TrdFundValuationTradeDto> page1 = trdFundValuationTradeService.selectForPage(page, length, pageParam);
        return page1;
    }

    /**
     * 策略代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createFundTradeId")
    public String createPlanTradeId() {
        return trdFundValuationTradeService.createPlanTradeId();
    }

    /**
     * 分页数据查询
     *
     * @return page
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("/findAll")
    public List<TrdFundValuationTradeDto> findAll(@RequestBody Map<String, Object> param){
        return trdFundValuationTradeService.findAll(param);
    }

    /**
     * 根据交易编号查询对象
     * @see
     * @since 1.0
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdFundValuationTradeDto queryTradeInfoByTradeId(@RequestBody Map<String, Object> param) {
        return trdFundValuationTradeService.queryTradeInfoByTradeId(param);
    }

    /**
     * 赎回到账详情查询
     * @see
     * @since 1.0
     */
    @PostMapping("queryRedInfoByTradeId")
    public TrdFundValuationTradeDto queryRedInfoByTradeId(@RequestBody String tradeId) {
        return trdFundValuationTradeService.queryRedInfoByTradeId(tradeId);
    }

    /**
     * 赎回到账双击明细页面
     * @see
     * @since 1.0
     */
    @PostMapping("queryRedInfoByOutTradeId")
    public TrdFundValuationTradeDto queryRedInfoByOutTradeId(@RequestBody String tradeId) {
        return trdFundValuationTradeService.queryRedInfoByOutTradeId(tradeId);
    }


    /**
     * 回调函数 操作具体 new
     *
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("insert")
    public void insertNet(@RequestBody TrdFundTradeResult dto) {
        trdFundValuationTradeService.insert(dto);
    }

    /**
     * 回调函数 操作具体 update <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("update")
    public void updateNet(@RequestBody TrdFundTradeResult dto) {
        trdFundValuationTradeService.update(dto);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdFundValuationTradeDto dto){
        return trdFundValuationTradeService.delete(dto);
    }

}
