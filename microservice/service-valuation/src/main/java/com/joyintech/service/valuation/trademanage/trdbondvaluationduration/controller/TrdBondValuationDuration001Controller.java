/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdbondvaluationduration.controller;


import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationDto;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.service.TrdBondValuationDuration001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 债券存续期管理微服务
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月11日
 * @see TrdBondValuationDuration001Controller
 * @since 1.0
 */
@RestController(value="serviceTrdBondValuationDuration001Controller")
@RequestMapping("/TrdBondValuationDuration001")
public class TrdBondValuationDuration001Controller {

    @Autowired
    private TrdBondValuationDuration001Service trdBondValuationDuration001Service;

    /**
     * 分页查询
     *
     * @return Page<TrdBondInvestTradeDto>泛型的page实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("findPage/{page}/{length}")
    public Page<TrdBondValuationDurationDto> queryForPage(@RequestBody Map<String, Object> param,
                                                          @PathVariable("page") int page, @PathVariable("length") int length)
            throws Exception {
        return trdBondValuationDuration001Service.selectForPage(page, length, param);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdBondValuationDurationDto dto){
        return trdBondValuationDuration001Service.delete(dto);
    }

}
