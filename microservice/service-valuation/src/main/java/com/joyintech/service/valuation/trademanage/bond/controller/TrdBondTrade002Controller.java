/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.bond.controller;


import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.component.common.dto.FinCashPlanDto;
import com.joyintech.service.valuation.trademanage.bond.dto.TrdBondTradeResult;
import com.joyintech.service.valuation.trademanage.bond.dto.TrdBondTradeDto;
import com.joyintech.service.valuation.trademanage.bond.service.TrdBondTrade002Service;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 债券交易微服务
 *
 * @author Zane.Y
 * @version 1.0 2018年9月7日
 * @see TrdBondTrade002Controller
 * @since 1.0
 */
@RestController(value="serviceTrdBondTrade002Controller")
@RequestMapping("/TrdBondTrade002")
public class TrdBondTrade002Controller {

    @Autowired
    private TrdBondTrade002Service trdBondTrade002Service;

    /**
     * 通过资产代码查询资产简称和交易场所
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("selectInfoBySecId")
    public FinProduct selectInfoBySecId(@RequestBody Map<String, Object> param) {
        return trdBondTrade002Service.selectInfoBySecId(param);
    }

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdBondTradeDto queryTradeInfoByTradeId(@RequestBody String tradeId) {
        return trdBondTrade002Service.queryTradeInfoByTradeId(tradeId);
    }

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("getSecManageAcctId")
    public List<SelectOptionDto> getSecManageAcctId(@RequestBody Map<String, Object> map) {
        return trdBondTrade002Service.getSecManageAcctId(map);
    }

    /**
     * 联想查询通道（通道收取方式）
     *
     * @param params 画面参数
     * @return 通道代码：下拉框
     */
    @PostMapping("getChannelId/{page}/{length}")
    public Page<SelectOptionDto> getChannelId(@RequestBody Map<String, Object> params, @PathVariable("page") int page, @PathVariable("length") int length) {
        return trdBondTrade002Service.getChannelId(page, length, params);
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
        int  count = trdBondTrade002Service.querySequences();
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
    public int insert(@RequestBody TrdBondTradeResult dto)
            throws Exception {
        return trdBondTrade002Service.insert(dto);
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
    public int update(@RequestBody TrdBondTradeResult dto)
            throws Exception {
        return trdBondTrade002Service.update(dto);
    }

    /**
     * 查询标的面值
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("getRepaymentInfo")
    public List<FinCashPlanDto> getRepaymentInfo(@RequestBody Map<String, Object> map) {
        return trdBondTrade002Service.getRepaymentInfo(map);
    }

}
