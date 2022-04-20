/*
 * 文件名：TaxBalanceQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade001Dto;

import com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade001Result;
import com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade002Result;
import com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTradeResult;
import com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.service.TrdOutRightRepoValuationTrade001Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * service 买断式回购交易
 * @see TrdOutRightRepoValuationTrade001Controller
 * @since 1.0
 */
@RestController(value="serviceTrdOutRightRepoValuationTrade001Controller")
@RequestMapping("/TrdOutRightRepoValuationTrade001")
public class TrdOutRightRepoValuationTrade001Controller extends BaseController {

    @Autowired
    private TrdOutRightRepoValuationTrade001Service trdOutRightRepoValuationTrade001Service;

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
        return trdOutRightRepoValuationTrade001Service.findPortfolioId(page,length,map);
    }

    /**
     * 资产代码下拉框
     */
    @PostMapping("findFinprodId/{page}/{length}")
    public Page<SelectOptionDto> findFinprodId(@RequestBody Map<String,Object> map,
                                               @PathVariable("page") int page, @PathVariable("length") int length) {
        return trdOutRightRepoValuationTrade001Service.findFinprodId(page,length,map);
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
    public void doComplete(@RequestBody TrdOutRightRepoValuationTrade001Dto dto, @PathVariable("type") String type) {
        dto.setOperateType(type);
        trdOutRightRepoValuationTrade001Service.doComplete(dto);
    }

    /**
     * 查询 支持分页 <功能详细描述>
     *
     * @since 1.0
     */
    @RequestMapping("/findPage/{page}/{length}")
    public Page<TrdOutRightRepoValuationTrade001Dto> selectForPage(@RequestBody Map<String, Object> pageParam, @PathVariable("page") int page,
                                                                   @PathVariable("length") int length)
            throws Exception {
        Page<TrdOutRightRepoValuationTrade001Dto> page1 = trdOutRightRepoValuationTrade001Service.selectForPage(page, length, pageParam);
        return page1;
    }

    /**
     * 不分页数据查询  导出
     */
    @PostMapping("/queryPageList")
    public List<TrdOutRightRepoValuationTrade001Dto> queryPageList(@RequestBody Map<String, Object> param){
        return trdOutRightRepoValuationTrade001Service.queryPageList(param);
    }

    /**
     * 根据交易编号查询对象
     * @see
     * @since 1.0
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdOutRightRepoValuationTrade001Dto queryTradeInfoByTradeId(@RequestBody Map<String, Object> params) {
        return trdOutRightRepoValuationTrade001Service.queryTradeInfoByTradeId(params);
    }


    /**
     * 策略代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createPlanTradeId")
    public String createPlanTradeId() {
        return trdOutRightRepoValuationTrade001Service.createPlanTradeId();
    }


    /**
     * 删除
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdOutRightRepoValuationTrade001Dto dto){
        return trdOutRightRepoValuationTrade001Service.delete(dto);
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
    public int insert(@RequestBody TrdOutRightRepoValuationTradeResult dto)
            throws Exception {
        return trdOutRightRepoValuationTrade001Service.insert(dto);
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
    public int update(@RequestBody TrdOutRightRepoValuationTradeResult dto)
            throws Exception {
        return trdOutRightRepoValuationTrade001Service.update(dto);
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
    public int update01(@RequestBody TrdOutRightRepoValuationTrade001Result dto)
            throws Exception {
        return trdOutRightRepoValuationTrade001Service.update01(dto);
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
    @RequestMapping("update02")
    public int update02(@RequestBody TrdOutRightRepoValuationTrade002Result dto)
            throws Exception {
        return trdOutRightRepoValuationTrade001Service.update02(dto);
    }
    /**
     * 插入 债券回购标的
     *
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("insert01")
    public int insert01(@RequestBody TrdOutRightRepoValuationTrade001Result dto)
            throws Exception {
        return trdOutRightRepoValuationTrade001Service.insert01(dto);
    }


    /**
     * 插入 债券回购质押品信息
     *
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("insert02")
    public int insert02(@RequestBody TrdOutRightRepoValuationTrade002Result dto)
            throws Exception {
        return trdOutRightRepoValuationTrade001Service.insert02(dto);
    }
}
