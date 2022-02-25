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
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeDto;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.service.TrdBondChangeTrade001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 证券转换交易微服务
 *
 * @author sunshuaishuai
 * @version 1.0 2022年01月04日
 * @see TrdBondChangeTrade001Controller
 * @since 1.0
 */
@RestController(value="serviceTrdBondChangeTrade001Controller")
@RequestMapping("/TrdBondChangeTrade001")
public class TrdBondChangeTrade001Controller {

    @Autowired
    private TrdBondChangeTrade001Service trdBondChangTrade001Service;

    /**
     * 分页查询
     *
     * @return Page<TrdBondInvestTradeDto>泛型的page实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("findPage/{page}/{length}")
    public Page<TrdBondChangeTradeDto> queryForPage(@RequestBody Map<String, Object> param,
                                                    @PathVariable("page") int page, @PathVariable("length") int length)
            throws Exception {
        return trdBondChangTrade001Service.selectForPage(page, length, param);
    }

    /**
     * 不分页数据查询
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/queryPageList")
    public List<TrdBondChangeTradeDto> queryPageList(@RequestBody Map<String, Object> param){
        return trdBondChangTrade001Service.queryPageList(param);
    }

    /**
     *
     * 转出证券名称联想下拉框
     * <功能详细描述>
     * @param map 输入参数
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    @PostMapping("/getAssetId/{page}/{length}")
    public Page<SelectOptionDto> getAssetId(@RequestBody Map<String, Object> map, @PathVariable("page") int page,
                                              @PathVariable("length") int length){
        return trdBondChangTrade001Service.getAssetId(page,length,map);
    }

    /**
     *
     * 正股代码联想下拉框
     * <功能详细描述>
     * @param map 输入参数
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    @PostMapping("/getStockId/{page}/{length}")
    public Page<SelectOptionDto> getStockId(@RequestBody Map<String, Object> map,@PathVariable("page") int page,
                                            @PathVariable("length") int length){
        return trdBondChangTrade001Service.getStockId(page,length,map);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdBondChangeTradeDto dto){
        return trdBondChangTrade001Service.delete(dto);
    }

}
