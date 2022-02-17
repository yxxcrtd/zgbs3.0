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
//
//    @Autowired
//    private BaseTradeProductDealService baseTradeProductDealService;
//
//

    /**
     * 策略代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createPlanTradeId")
    public String createPlanTradeId() {
        return trdDepositValuationTrade001Service.createPlanTradeId();
    }

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
        return trdDepositValuationTrade001Service.findPortfolioId(page,length,map);
    }


    /**
     * 资产代码下拉框
     *
     * @return 返回的数据
     * @exception Exception
     *                异常
     * @since 1.0
     */
    @PostMapping("findFinprodId/{page}/{length}")
    public Page<SelectOptionDto> findFinprodId(@RequestBody Map<String,Object> map,
                                                 @PathVariable("page") int page, @PathVariable("length") int length) {
        return trdDepositValuationTrade001Service.findFinprodId(page,length,map);
    }

    /**
     * 交易对手下拉框

     */
    @PostMapping("getCounterId/{page}/{length}")
    public Page<SelectOptionDto> getCounterId(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page,
                                              @PathVariable("length") int length){
        return trdDepositValuationTrade001Service.getCounterId(page, length, param);
    }


    /**
     * 查询 支持分页 <功能详细描述>
     *
     * @since 1.0
     */
//    @RequestMapping("/selectForPage/{page}/{length}")
//    public Page<TrdDepositValuationTrade001Dto> selectForPage(@RequestBody Map<String, Object> pageParam, @PathVariable("page") int page,
//                                                              @PathVariable("length") int length)
//            throws Exception {
//        Page<TrdDepositValuationTrade001Dto> page1 = trdDepositValuationTrade001Service.selectForPage(page, length, pageParam);
//        return page1;
//    }

    @RequestMapping("findPage/{page}/{length}")
    public Page<TrdDepositValuationTrade001Dto> queryForPage(@RequestBody Map<String, Object> param,
                                                 @PathVariable("page") int page, @PathVariable("length") int length)
            throws Exception {
        return trdDepositValuationTrade001Service.selectForPage(page, length, param);
    }


    /**
     * 不分页数据查询  导出
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/queryPageList")
    public List<TrdDepositValuationTrade001Dto> queryPageList(@RequestBody Map<String, Object> param){
        return trdDepositValuationTrade001Service.queryPageList(param);
    }

    /**
     * 根据交易编号查询对象
     * @see
     * @since 1.0
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdDepositValuationTrade001Dto queryTradeInfoByTradeId(@RequestBody String  tradeId) {
        return trdDepositValuationTrade001Service.queryTradeInfoByTradeId(tradeId);
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
