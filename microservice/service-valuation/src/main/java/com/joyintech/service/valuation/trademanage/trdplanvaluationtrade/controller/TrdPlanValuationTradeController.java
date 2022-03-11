/*
 * 文件名：TaxBalanceQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdplanvaluationtrade.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.service.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanTradeResult;
import com.joyintech.service.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanValuationTradeDto;
import com.joyintech.service.valuation.trademanage.trdplanvaluationtrade.service.TrdPlanValuationTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * service 计划类资产交易
 * 
 * @author yuzj
 * @version 1.0 2022年02月24日
 * @see TrdPlanValuationTradeController
 * @since 1.0
 */
@RestController(value="serviceTrdPlanValuationTradeController")
@RequestMapping("/TrdPlanValuationTrade")
public class TrdPlanValuationTradeController extends BaseController {

    @Autowired
    private TrdPlanValuationTradeService trdPlanValuationTradeService;

    /**
     * 查询 支持分页 <功能详细描述>
     *
     * @since 1.0
     */
    @RequestMapping("/selectForPage/{page}/{length}")
    public Page<TrdPlanValuationTradeDto> selectForPage(@RequestBody Map<String, Object> pageParam, @PathVariable("page") int page,
                                                        @PathVariable("length") int length)
            throws Exception {
        Page<TrdPlanValuationTradeDto> page1 = trdPlanValuationTradeService.selectForPage(page, length, pageParam);
        return page1;
    }

    /**
     * 策略代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createPlanTradeId")
    public String createPlanTradeId() {
        return trdPlanValuationTradeService.createPlanTradeId();
    }

    /**
     * 不分页数据查询
     *
     * @return page
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("/findAll")
    public List<TrdPlanValuationTradeDto> findAll(@RequestBody Map<String, Object> param){
        return trdPlanValuationTradeService.findAll(param);
    }

    /**
     * 根据交易编号查询对象
     * @see
     * @since 1.0
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdPlanValuationTradeDto queryTradeInfoByTradeId(@RequestBody Map<String, Object> param) {
        return trdPlanValuationTradeService.queryTradeInfoByTradeId(param);
    }

    /**
     * 赎回到账详情查询
     * @see
     * @since 1.0
     */
    @PostMapping("queryRedInfoByTradeId")
    public TrdPlanValuationTradeDto queryRedInfoByTradeId(@RequestBody String tradeId) {
        return trdPlanValuationTradeService.queryRedInfoByTradeId(tradeId);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdPlanValuationTradeDto dto){
        return trdPlanValuationTradeService.delete(dto);
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
    public void insert(@RequestBody TrdPlanTradeResult dto) {
        trdPlanValuationTradeService.insert(dto);
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
    public void update(@RequestBody TrdPlanTradeResult dto) {
        trdPlanValuationTradeService.update(dto);
    }


}
