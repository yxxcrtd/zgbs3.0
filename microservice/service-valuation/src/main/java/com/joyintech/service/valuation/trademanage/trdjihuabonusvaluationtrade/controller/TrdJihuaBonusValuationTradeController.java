/*
 * 文件名：TaxBalanceQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdjihuabonusvaluationtrade.controller;

import java.util.List;
import java.util.Map;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.service.valuation.trademanage.trdjihuabonusvaluationtrade.dto.TrdJihuaBonusTradeResult;
import com.joyintech.service.valuation.trademanage.trdjihuabonusvaluationtrade.dto.TrdJihuaBonusValuationTradeDto;
import com.joyintech.service.valuation.trademanage.trdjihuabonusvaluationtrade.service.TrdJihuaBonusValuationTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;

/**
 * service 基金存续期
 * 
 * @author yuzj
 * @version 1.0 2021年01月22日
 * @see TrdJihuaBonusValuationTradeController
 * @since 1.0
 */
@RestController(value="serviceTrdJihuaBonusValuationTradeController")
@RequestMapping("/TrdJihuaBonusValuationTrade")
public class TrdJihuaBonusValuationTradeController extends BaseController {

    @Autowired
    private TrdJihuaBonusValuationTradeService trdJihuaBonusValuationTradeService;

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
        return trdJihuaBonusValuationTradeService.findPortfolioId(page,length,map);
    }

    /**
     * 资产代码下拉框
     * @since 1.0
     */
    @PostMapping("findFinprodId/{page}/{length}")
    public Page<SelectOptionDto> findFinprodId(@RequestBody Map<String,Object> map,
                                                 @PathVariable("page") int page, @PathVariable("length") int length) {
        return trdJihuaBonusValuationTradeService.findFinprodId(page,length,map);
    }



    /**
     * 查询 支持分页 <功能详细描述>
     *
     * @since 1.0
     */
    @RequestMapping("/selectForPage/{page}/{length}")
    public Page<TrdJihuaBonusValuationTradeDto> selectForPage(@RequestBody Map<String, Object> pageParam, @PathVariable("page") int page,
                                                             @PathVariable("length") int length)
            throws Exception {
        Page<TrdJihuaBonusValuationTradeDto> page1 = trdJihuaBonusValuationTradeService.selectForPage(page, length, pageParam);
        return page1;
    }

    /**
     * 策略代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createJihuaBonusTradeId")
    public String createJihuaBonusTradeId() {
        return trdJihuaBonusValuationTradeService.createJihuaBonusTradeId();
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
    public List<TrdJihuaBonusValuationTradeDto> findAll(@RequestBody Map<String, Object> param){
        return trdJihuaBonusValuationTradeService.findAll(param);
    }

    /**
     * 根据交易编号查询对象
     * @see
     * @since 1.0
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdJihuaBonusValuationTradeDto queryTradeInfoByTradeId(@RequestBody String  tradeId) {
        return trdJihuaBonusValuationTradeService.queryTradeInfoByTradeId(tradeId);
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
    public void insertNet(@RequestBody TrdJihuaBonusTradeResult dto) {
        trdJihuaBonusValuationTradeService.insert(dto);
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
    public void updateNet(@RequestBody TrdJihuaBonusTradeResult dto) {
        trdJihuaBonusValuationTradeService.update(dto);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdJihuaBonusValuationTradeDto dto){
        return trdJihuaBonusValuationTradeService.delete(dto);
    }

}
