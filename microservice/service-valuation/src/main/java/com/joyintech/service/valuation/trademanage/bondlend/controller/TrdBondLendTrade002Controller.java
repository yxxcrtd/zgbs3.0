/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.bondlend.controller;


import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.bondlend.dto.TrdBondLendTrade001Dto;
import com.joyintech.service.valuation.trademanage.bondlend.dto.TrdBondLendTradeDto;
import com.joyintech.service.valuation.trademanage.bondlend.dto.TrdBondLendTradeResult;
import com.joyintech.service.valuation.trademanage.bondlend.service.TrdBondLendTrade002Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 债券借贷交易微服务
 *
 * @author sunshuaishuai
 * @version 1.0 2022年01月07日
 * @see TrdBondLendTrade002Controller
 * @since 1.0
 */
@RestController(value="serviceTrdBondLendTrade002Controller")
@RequestMapping("/TrdBondLendTrade002")
public class TrdBondLendTrade002Controller {

    @Autowired
    private TrdBondLendTrade002Service trdBondLendTrade002Service;
    
    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdBondLendTradeDto queryTradeInfoByTradeId(@RequestBody String tradeId) {
        return trdBondLendTrade002Service.queryTradeInfoByTradeId(tradeId);
    }

    /**
     * 通过交易编号查询交易信息（到期）
     *
     * @return 查询结果集
     * @throws Exception
     *             异常
     */
    @PostMapping("queryInfoByTradeId")
    public TrdBondLendTrade001Dto queryInfoByTradeId(@RequestBody String tradeId) {
        return trdBondLendTrade002Service.queryInfoByTradeId(tradeId);
    }

    /**
     *  资金账户下拉框
     *
     * @return List<SelectOptionDto>
     */
    @PostMapping("/getTrdAcctId/{page}/{length}")
    public Page<SelectOptionDto> getTrdAcctId(@RequestBody Map<String, Object> map, @PathVariable("page") int page,
                                            @PathVariable("length") int length){
        return trdBondLendTrade002Service.getTrdAcctId(page,length,map);
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
        int  count = trdBondLendTrade002Service.querySequences();
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
    public int insert(@RequestBody TrdBondLendTradeResult dto)
            throws Exception {
        return trdBondLendTrade002Service.insert(dto);
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
    public int update(@RequestBody TrdBondLendTradeResult dto)
            throws Exception {
        return trdBondLendTrade002Service.update(dto);
    }

}
