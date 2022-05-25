package com.joyintech.service.period.trdprojduration.controller;

import java.util.List;
import java.util.Map;

import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.period.trdprojduration.service.TrdProjectDurationService;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.common.TrdCommonDeleteDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.trdprojduration.TrdProjectDurationDto;

/**
 * 项目类资产 存续期
 *
 * @author : 强成西
 * @version V1.0
 * @Project: microservice
 * @Package com.joyintech.service.product.productduration.addreduce.controller
 * @Description: TODO
 * @date Date : 2018年09月19日 10:02
 */
@RestController("TrdProjectDurationCtr_dtx")
@RequestMapping("/TrdProjectDuration")
public class TrdProjectDurationController {

    @Autowired
    private TrdProjectDurationService trdProjectDurationService;

    /**
     * 查询有效、撤销或待处理的存续期交易数据
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<TrdProjectDurationDto> findPage(@RequestBody Map<String, Object> param, @PathVariable("page") int page,
                                                     @PathVariable("length") int length) {
        return trdProjectDurationService.findPage(page, length, param);
    }

    /**
     * 画面查询方法-交易编号
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("getBusiCode/{page}/{length}")
    public Page<SelectOptionDto> getBusiCode(@RequestBody Map<String, Object> param, @PathVariable("page") int page,
                                             @PathVariable("length") int length) {
        return trdProjectDurationService.getBusiCode(page, length, param);
    }

    /**
     * 查询有效、撤销或待处理的存续期交易数据(不分页)
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("selectAllData")
    public List<TrdProjectDurationDto> selectAllData(@RequestBody Map<String, Object> param) {
        return trdProjectDurationService.selectAllData(param);
    }

    /**
     * 查询有效的存续期交易数据
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findEffectiveData")
    public TrdProjectDurationDto findEffectiveData(@RequestBody Map<String, Object> params) {
        return trdProjectDurationService.findEffectiveData(params);
    }

    /**
     * 查询有效的存续期交易数据
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findEffectiveDeal")
    public List<BaseTradeProductDealDto> findEffectiveDeal(@RequestBody Map<String, Object> params) {
        return trdProjectDurationService.findEffectiveDeal(params);
    }
    /**
     * 查询有效的子组合交易信息
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findChildTradeInfo")
    public List<BaseTradeProductDealDto> findChildTradeInfo(@RequestBody Map<String, Object> params) {
        return trdProjectDurationService.findChildTradeInfo(params);
    }
    /**
     * 查询利息明细信息
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findAssetIntInfo")
    public List<FinAssetIntInfo> findAssetIntInfo(@RequestBody Map<String, Object> params) {
        return trdProjectDurationService.findAssetIntInfo(params);
    }

    /**
     * 插入流程表数据
     *findPage
     * @return 查询结果集
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doInsert")
    public String doInsert(@RequestBody BaseTotalTradeInfoDto baseTotalTradeInfoDto) {
        return trdProjectDurationService.doInsert(baseTotalTradeInfoDto);
    }

    /**
     * 插入数据，并删除流程表数据
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doComplete")
    public String doComplete(@RequestBody TrdProjectDurationDto durationDto) {
        return trdProjectDurationService.doComplete(durationDto);
    }

    /**
     * 撤销交易，插入流程表
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doRevoke")
    public String doRevoke(@RequestBody TrdProjectDurationDto durationDto) {
        return trdProjectDurationService.doRevoke(durationDto);
    }

    /**
     * 作废流程， 并删除流程表数据
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doDelete")
    public String doDelete(@RequestBody TrdCommonDeleteDto trdCommonDeleteDto) {
        return trdProjectDurationService.doDelete(trdCommonDeleteDto);
    }
    //add by liupengfei for多级法人 S
    /**
     * 画面查询方法-市场代码
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("getFinprodMarketId/{page}/{length}")
    public Page<SelectOptionDto> getFinprodMarketId(@RequestBody Map<String, Object> param, @PathVariable("page") int page,
                                                    @PathVariable("length") int length)
    {
        return trdProjectDurationService.getFinprodMarketId(page, length, param);
    }
    //add by liupengfei for多级法人 E

    @PostMapping("getGroupPayByList")
    public List<StlPayOrdersDto> getGroupPayByList(@RequestBody Map<String, Object> params) {
        return trdProjectDurationService.getGroupPayByList(params);
    }

    /**
     * 是否付息日
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("isPayIntDate")
    public boolean isPayIntDate(@RequestBody Map<String,Object> map) {
        return trdProjectDurationService.isPayIntDate(map);
    }
}
