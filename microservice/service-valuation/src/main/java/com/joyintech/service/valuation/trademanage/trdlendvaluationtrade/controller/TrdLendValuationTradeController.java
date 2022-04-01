/*
 * 文件名：TaxBalanceQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdlendvaluationtrade.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trdlendvaluationtrade.dto.TrdLendValuationTradeDto;
import com.joyintech.service.valuation.trademanage.trdlendvaluationtrade.dto.TrdLendValuationTradeResult;
import com.joyintech.service.valuation.trademanage.trdlendvaluationtrade.service.TrdLendValuationTradeService;
import com.joyintech.service.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanValuationTradeDto;
import com.joyintech.service.valuation.trademanage.trdplanvaluationtrade.service.TrdPlanValuationTradeService;
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
 * @see TrdLendValuationTradeController
 * @since 1.0
 */
@RestController(value="serviceTrdLendValuationTradeController")
@RequestMapping("/TrdLendValuationTrade")
public class TrdLendValuationTradeController extends BaseController {

    @Autowired
    private TrdLendValuationTradeService trdLendValuationTradeService;

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
        return trdLendValuationTradeService.findPortfolioId(page,length,map);
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
    public void doComplete(@RequestBody TrdLendValuationTradeDto dto, @PathVariable("type") String type) {
        dto.setOperateType(type);
        trdLendValuationTradeService.doComplete(dto);
    }

    /**
     * 查询 支持分页 <功能详细描述>
     *
     * @since 1.0
     */
    @RequestMapping("/findPage/{page}/{length}")
    public Page<TrdLendValuationTradeDto> selectForPage(@RequestBody Map<String, Object> pageParam, @PathVariable("page") int page,
                                                        @PathVariable("length") int length)
            throws Exception {
        Page<TrdLendValuationTradeDto> page1 = trdLendValuationTradeService.selectForPage(page, length, pageParam);
        return page1;
    }

    /**
     * 策略代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createPlanTradeId")
    public String createPlanTradeId() {
        return trdLendValuationTradeService.createPlanTradeId();
    }


    /**
     * 根据交易编号查询对象
     * @see
     * @since 1.0
     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdLendValuationTradeDto queryTradeInfoByTradeId(@RequestBody String  tradeId) {
        return trdLendValuationTradeService.queryTradeInfoByTradeId(tradeId);
    }

    /**
     * 删除
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody TrdLendValuationTradeDto dto){
        return trdLendValuationTradeService.delete(dto);
    }


    /**
     * 不分页数据查询  导出
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/queryPageList")
    public List<TrdLendValuationTradeDto> queryPageList(@RequestBody Map<String, Object> param){
        return trdLendValuationTradeService.queryPageList(param);
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
    public int insert(@RequestBody TrdLendValuationTradeResult dto)
            throws Exception {
        return trdLendValuationTradeService.insert(dto);
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
    public int update(@RequestBody TrdLendValuationTradeResult dto)
            throws Exception {
        return trdLendValuationTradeService.update(dto);
    }
}
