/*
 * 文件名：TaxBalanceQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTrade001Dto;
import com.joyintech.service.valuation.trademanage.trdlendvaluationtrade.dto.TrdLendValuationTradeDto;
import com.joyintech.service.valuation.trademanage.trdlendvaluationtrade.service.TrdLendValuationTradeService;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade001Dto;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade001Result;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTradeResult;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.service.TrdRepoValuationTrade001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * service 拆借交易
 * 
 * @author yuzj
 * @version 1.0 2021年12月29日
 * @see TrdRepoValuationTrade001Controller
 * @since 1.0
 */
@RestController(value="serviceTrdRepoValuationTrade001Controller")
@RequestMapping("/TrdRepoValuationTrade001")
public class TrdRepoValuationTrade001Controller extends BaseController {

    @Autowired
    private TrdRepoValuationTrade001Service trdRepoValuationTrade001Service;

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
        return trdRepoValuationTrade001Service.findPortfolioId(page,length,map);
    }



    /**
     * 回调函数 操作具体 new delete update <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/doComplete/{type}")
    public void doComplete(@RequestBody TrdRepoValuationTrade001Dto dto, @PathVariable("type") String type) {
        dto.setOperateType(type);
        trdRepoValuationTrade001Service.doComplete(dto);
    }



    /**
     * 策略代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createPlanTradeId")
    public String createPlanTradeId() {
        return trdRepoValuationTrade001Service.createPlanTradeId();
    }


    /**
     * 查询 支持分页 <功能详细描述>
     *
     * @since 1.0
     */
    @RequestMapping("/findPage/{page}/{length}")
    public Page<TrdRepoValuationTrade001Dto> selectForPage(@RequestBody Map<String, Object> param, @PathVariable("page") int page,
                                                           @PathVariable("length") int length)
            throws Exception {
//        Page<TrdRepoValuationTrade001Dto> page1 = trdRepoValuationTrade001Service.selectForPage(page, length, pageParam);
//        return page1;
        return trdRepoValuationTrade001Service.selectForPage(page, length, param);
    }


    /**
     * 不分页数据查询  导出
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/queryPageList")
    public List<TrdRepoValuationTrade001Dto> queryPageList(@RequestBody Map<String, Object> param){
        return trdRepoValuationTrade001Service.queryPageList(param);
    }


    /**
     * 根据交易编号查询对象
     * @see
     * @since 1.0
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdRepoValuationTrade001Dto queryTradeInfoByTradeId(@RequestBody String  tradeId) {
        return trdRepoValuationTrade001Service.queryTradeInfoByTradeId(tradeId);
    }

    /**
     * 删除
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdRepoValuationTrade001Dto dto){
        return trdRepoValuationTrade001Service.delete(dto);
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
    public int insert(@RequestBody TrdRepoValuationTradeResult dto)
            throws Exception {
        return trdRepoValuationTrade001Service.insert(dto);
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
    @RequestMapping("insert01")
    public int insert01(@RequestBody TrdRepoValuationTrade001Result dto)
            throws Exception {
        return trdRepoValuationTrade001Service.insert01(dto);
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
    public int update(@RequestBody TrdRepoValuationTradeResult dto)
            throws Exception {
        return trdRepoValuationTrade001Service.update(dto);
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
    @RequestMapping("update01")
    public int update01(@RequestBody TrdRepoValuationTrade001Result dto)
            throws Exception {
        return trdRepoValuationTrade001Service.update01(dto);
    }

}
