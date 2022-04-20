/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdsecovertransfervaluationduration.controller;


import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.service.valuation.trademanage.trdsecovertransfervaluationduration.dto.TrdSecOverTransferValuationDuration001Dto;
import com.joyintech.service.valuation.trademanage.trdsecovertransfervaluationduration.service.TrdSecOverTransferValuationDuration001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 应收款转让管理微服务
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月17日
 * @see TrdSecOverTransferValuationDuration001Controller
 * @since 1.0
 */
@RestController(value="serviceTrdSecOverTransferValuationDuration001Controller")
@RequestMapping("/TrdSecOverTransferValuationDuration001")
public class TrdSecOverTransferValuationDuration001Controller {

    @Autowired
    private TrdSecOverTransferValuationDuration001Service trdSecOverTransferValuationDuration001Service;

    /**
     * 分页查询
     *
     * @return Page<TrdBondInvestTradeDto>泛型的page实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("findPage/{page}/{length}")
    public Page<TrdSecOverTransferValuationDuration001Dto> queryForPage(@RequestBody Map<String, Object> param,
                                                                        @PathVariable("page") int page, @PathVariable("length") int length)
            throws Exception {
        return trdSecOverTransferValuationDuration001Service.selectForPage(page, length, param);
    }

    /**
     * 不分页数据查询
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/queryPageList")
    public List<TrdSecOverTransferValuationDuration001Dto> queryPageList(@RequestBody Map<String, Object> param){
        return trdSecOverTransferValuationDuration001Service.queryPageList(param);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdSecOverTransferValuationDuration001Dto dto){
        return trdSecOverTransferValuationDuration001Service.delete(dto);
    }

}
