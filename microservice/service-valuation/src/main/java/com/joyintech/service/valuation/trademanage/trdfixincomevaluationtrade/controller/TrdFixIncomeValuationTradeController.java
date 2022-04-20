/*
 * 文件名：TaxBalanceQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdfixincomevaluationtrade.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.service.valuation.trademanage.trdfixincomevaluationtrade.dto.TrdFixIncomeTradeResult;
import com.joyintech.service.valuation.trademanage.trdfixincomevaluationtrade.dto.TrdFixIncomeValuationTradeDto;
import com.joyintech.service.valuation.trademanage.trdfixincomevaluationtrade.service.TrdFixIncomeValuationTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * service 固收型交易
 * 
 * @author yuzj
 * @version 1.0 2021年12月29日
 * @see TrdFixIncomeValuationTradeController
 * @since 1.0
 */
@RestController(value="serviceTrdFixIncomeValuationTradeController")
@RequestMapping("/TrdFixIncomeValuationTrade")
public class TrdFixIncomeValuationTradeController extends BaseController {

    @Autowired
    private TrdFixIncomeValuationTradeService trdFixIncomeValuationTradeService;

    /**
     * 查询 支持分页 <功能详细描述>
     *
     * @since 1.0
     */
    @RequestMapping("/selectForPage/{page}/{length}")
    public Page<TrdFixIncomeValuationTradeDto> selectForPage(@RequestBody Map<String, Object> pageParam, @PathVariable("page") int page,
                                                        @PathVariable("length") int length)
            throws Exception {
        Page<TrdFixIncomeValuationTradeDto> page1 = trdFixIncomeValuationTradeService.selectForPage(page, length, pageParam);
        return page1;
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
    public int insert(@RequestBody TrdFixIncomeTradeResult dto)
            throws Exception {
        return trdFixIncomeValuationTradeService.insert(dto);
    }

    /**
     * 回调函数 操作具体 update <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("update")
    public void update(@RequestBody TrdFixIncomeTradeResult dto) {
        trdFixIncomeValuationTradeService.update(dto);
    }


    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdFixIncomeValuationTradeDto dto){
        return trdFixIncomeValuationTradeService.delete(dto);
    }

    /**
     * 根据交易编号查询对象
     * @see
     * @since 1.0
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdFixIncomeValuationTradeDto queryTradeInfoByTradeId(@RequestBody String  tradeId) {
        return trdFixIncomeValuationTradeService.queryTradeInfoByTradeId(tradeId);
    }

}
