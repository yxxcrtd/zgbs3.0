/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.irs.controller;


import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.service.valuation.trademanage.irs.dto.TrdIRSValuationTrade001Dto;
import com.joyintech.service.valuation.trademanage.irs.service.TrdIRSValuationTrade001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 利率互换交易微服务
 *
 * @author sunshuaishuai
 * @version 1.0 2022年01月13日
 * @see TrdIRSValuationTrade001Controller
 * @since 1.0
 */
@RestController(value="serviceTrdIRSValuationTrade001Controller")
@RequestMapping("/TrdIRSValuationTrade001")
public class TrdIRSValuationTrade001Controller {

    @Autowired
    private TrdIRSValuationTrade001Service trdIRSValuationTrade001Service;

    /**
     * 分页查询
     *
     * @return Page<TrdBondInvestTradeDto>泛型的page实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("findPage/{page}/{length}")
    public Page<TrdIRSValuationTrade001Dto> queryForPage(@RequestBody Map<String, Object> param,
                                                         @PathVariable("page") int page, @PathVariable("length") int length)
            throws Exception {
        return trdIRSValuationTrade001Service.selectForPage(page, length, param);
    }

    /**
     * 不分页数据查询
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/queryPageList")
    public List<TrdIRSValuationTrade001Dto> queryPageList(@RequestBody Map<String, Object> param){
        return trdIRSValuationTrade001Service.queryPageList(param);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdIRSValuationTrade001Dto dto){
        return trdIRSValuationTrade001Service.delete(dto);
    }

}
