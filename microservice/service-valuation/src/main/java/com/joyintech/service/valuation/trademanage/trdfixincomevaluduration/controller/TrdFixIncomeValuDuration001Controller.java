/*
 * 文件名：TaxBalanceQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdfixincomevaluduration.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.component.common.dto.FinCashPlanDto;
import com.joyintech.service.valuation.trademanage.trdfixincomevaluduration.dto.TrdFixIncomeValuDuration001Dto;
import com.joyintech.service.valuation.trademanage.trdfixincomevaluduration.dto.TrdFixIncomeValuDurationResult;
import com.joyintech.service.valuation.trademanage.trdfixincomevaluduration.service.TrdFixIncomeValuDuration001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * service 固收类存续期
 * 
 * @author yuzj
 * @version 1.0 2021年12月29日
 * @see TrdFixIncomeValuDuration001Controller
 * @since 1.0
 */
@RestController(value="serviceTrdFixIncomeValuDuration001Controller")
@RequestMapping("/TrdFixIncomeValuDuration001")
public class TrdFixIncomeValuDuration001Controller extends BaseController {

    @Autowired
    private TrdFixIncomeValuDuration001Service trdFixIncomeValuDuration001Service;

    /**
     * 查询 支持分页 <功能详细描述>
     *
     * @since 1.0
     */
    @RequestMapping("/findPage/{page}/{length}")
    public Page<TrdFixIncomeValuDuration001Dto> selectForPage(@RequestBody Map<String, Object> param, @PathVariable("page") int page,
                                                           @PathVariable("length") int length)
            throws Exception {
        return trdFixIncomeValuDuration001Service.selectForPage(page, length, param);
    }

    /**
     * 根据交易编号查询对象

     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdFixIncomeValuDuration001Dto queryTradeInfoByTradeId(@RequestBody Map<String, Object> params) {
        return trdFixIncomeValuDuration001Service.queryTradeInfoByTradeId(params);
    }

    /**
     * 删除
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdFixIncomeValuDuration001Dto dto){
        return trdFixIncomeValuDuration001Service.delete(dto);
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
    public int insert(@RequestBody TrdFixIncomeValuDurationResult dto)
            throws Exception {
        return trdFixIncomeValuDuration001Service.insert(dto);
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
    public int update(@RequestBody TrdFixIncomeValuDurationResult dto)
            throws Exception {
        return trdFixIncomeValuDuration001Service.update(dto);
    }

    /**
     *
     * 查询计划类资产付息计划
     *
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("/getFinCashPlanByProductId")
    public List<FinCashPlanDto> getFinCashPlanByProductId(@RequestBody Map<String, Object> param){
        return trdFixIncomeValuDuration001Service.getFinCashPlanByProductId(param);
    }
}
