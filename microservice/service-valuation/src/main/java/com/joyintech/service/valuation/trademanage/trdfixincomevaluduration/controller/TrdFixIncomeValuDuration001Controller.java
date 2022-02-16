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
        return trdFixIncomeValuDuration001Service.findPortfolioId(page,length,map);
    }

    /**
     * 资产代码下拉框
     */
    @PostMapping("findFinprodId/{page}/{length}")
    public Page<SelectOptionDto> findFinprodId(@RequestBody Map<String,Object> map,
                                               @PathVariable("page") int page, @PathVariable("length") int length) {
        return trdFixIncomeValuDuration001Service.findFinprodId(page,length,map);
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
    public void doComplete(@RequestBody TrdFixIncomeValuDuration001Dto dto, @PathVariable("type") String type) {
        dto.setOperateType(type);
        trdFixIncomeValuDuration001Service.doComplete(dto);
    }



    /**
     * 策略代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createPlanTradeId")
    public String createPlanTradeId() {
        return trdFixIncomeValuDuration001Service.createPlanTradeId();
    }


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
     * 不分页数据查询  导出
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/queryPageList")
    public List<TrdFixIncomeValuDuration001Dto> queryPageList(@RequestBody Map<String, Object> param){
        return trdFixIncomeValuDuration001Service.queryPageList(param);
    }


    /**
     * 根据交易编号查询对象

     */
    @PostMapping("queryTradeInfoByTradeId")
    public TrdFixIncomeValuDuration001Dto queryTradeInfoByTradeId(@RequestBody String  tradeId) {
        return trdFixIncomeValuDuration001Service.queryTradeInfoByTradeId(tradeId);
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
}
