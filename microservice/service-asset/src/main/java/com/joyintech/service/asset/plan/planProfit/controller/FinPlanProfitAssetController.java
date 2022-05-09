/*
 * 文件名：FinPlanAssetRController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年9月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.asset.plan.planProfit.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.asset.plan.planProfit.dto.FinPlanProfitAssetDto;
import com.joyintech.service.asset.plan.planProfit.service.FinPlanProfitAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 计划类资产（利率型）controller
 * 
 * @author liuwei
 * @version 1.0 2018年9月14日
 * @see FinPlanProfitAssetController
 * @since 1.0
 */
@RestController("FinPlanProfitAsset_dtx")
@RequestMapping("/FinPlanProfitAsset")
public class FinPlanProfitAssetController {
    /**
     * 计划类资产（利率型）Service
     */
    @Autowired
    private FinPlanProfitAssetService finPlanProfitAssetService;
    
    /**
     * 资产代码的联想文本框事件
     * 
     * @param finprodMarketId 资产代码（市场代码）
     * @return 查询的资产代码和资产名称列表
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @GetMapping("getFinProdMarketId")
    public List<SuggestDto> getFinProdMarketId(String finprodMarketId)
        throws Exception {
        return finPlanProfitAssetService.getFinProdMarketId(finprodMarketId);
    }
    /**
     * 画面查询方法
     * 
     * @param param 画面参数
     * @param page 页码数
     * @param length 页面长度
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<FinPlanProfitAssetDto> selectAll(@RequestBody Map<String, Object> param,
                                                 @PathVariable("page") int page, @PathVariable("length") int length) throws Exception {
        return finPlanProfitAssetService.selectForPage(page, length, param);
    }
    
    /**
     * 删除
     * @param dto 页面选中的数据
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public void delete(@RequestBody FinPlanProfitAssetDto dto)
        throws Exception {
        finPlanProfitAssetService.delete(dto);
    }
    /**
     * 插入
     * @param dto 页面选中的数据
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("insert")
    public void insert(@RequestBody FinPlanProfitAssetDto dto)
            throws Exception {
        finPlanProfitAssetService.insert(dto);
    }

    /**
     * 修改
     * @param dto 页面选中的数据
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("update")
    public void update(@RequestBody FinPlanProfitAssetDto dto)
            throws Exception {
        finPlanProfitAssetService.update(dto);
    }
    /**
     * 出账编号的联想下拉框事件
     *
     * @param map 输入出账编号
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @PostMapping("getCmsId")
    public List<SelectOptionDto> getCmsId(@RequestBody Map<String,Object> map){
        return finPlanProfitAssetService.getCmsId(map);
    }
    /**
     * 出账编号的联动事件
     *
     * @param cmsId 输入出账编号
     * @return 查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @GetMapping("getChange")
    public FinPlanProfitAssetDto getChange(String cmsId){
        return finPlanProfitAssetService.getChange(cmsId);
    }
    /**
     * 管理人的联想下拉框事件
     * 
     * @param map 输入管理人参数
     * @return 查询的管理人代码和管理人名称列表
     * @see
     * @since 1.0
     */
    @PostMapping("getIssuer")
    public List<SelectOptionDto> getIssuer(@RequestBody Map<String,Object> map){
        return finPlanProfitAssetService.getIssuer(map);
    }
    /**
     * 托管人的联想下拉框事件
     * 
     * @param map 输入托管人参数
     * @return 查询的托管人代码和托管人名称列表
     * @see
     * @since 1.0
     */
    @PostMapping("getTrusteeId")
    public List<SelectOptionDto> getTrusteeId(@RequestBody Map<String,Object> map){
        return finPlanProfitAssetService.getTrusteeId(map);
    }
    /**
     * 交易日历的联想下拉框事件
     * 
     * @param map 输入交易日历参数
     * @return 查询的交易日历代码和交易日历名称列表
     * @see
     * @since 1.0
     */
    @PostMapping("getCalendarId")
    public List<SelectOptionDto> getCalendarId(@RequestBody Map<String,Object> map){
        return finPlanProfitAssetService.getCalendarId(map);
    }
    /**
     * 放款行的联想下拉框事件
     * 
     * @param map 输入放款行参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @PostMapping("getBank")
    public List<SelectOptionDto> getBank(@RequestBody Map<String,Object> map){
        return finPlanProfitAssetService.getBank(map);
    }
    /**
     * 资产提供行的多选下拉框事件
     * 
     * @param map 查询参数
     * @return 查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("getBankList")
    public List<SelectOptionDto> getBankList(Map<String,Object> map)
    {
        return finPlanProfitAssetService.getBankList(map);
    }
    /**
     * 修改或双击查看界面
     * @param finprodId 金融产品代码
     */
    @PostMapping("getBasicInfoByFinprodId")
    public FinPlanProfitAssetDto getBasicInfoByFinprodId(@RequestBody String finprodId){
        return finPlanProfitAssetService.getBasicInfoByFinprodId(finprodId);
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 start*/
    @GetMapping("getFinprodId")
    public String getFinprodId(String finprodMarketId){
        return finPlanProfitAssetService.getFinprodId(finprodMarketId);
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 end*/
    /**
     * 查询市场代码
     * @param finprodMarketId 市场代码
     */
    @GetMapping("selectFinprodMarketId")
    public int selectFinprodMarketId( String finprodMarketId){
        return finPlanProfitAssetService.selectFinprodMarketId(finprodMarketId);
    }

    /**
     * 查询客户名称
     *
     * @param custormerId 客户代码
     * @return 客户名称
     * @see
     * @since 1.0
     */
    @GetMapping("getName")
    public String getName(String custormerId){
        return finPlanProfitAssetService.getName(custormerId);
    }

    /**
     * 资产是否存在交易
     *
     * @param finprodId 金融产品代码
     * @return 交易数
     * @see
     * @since 1.0
     */
//    @GetMapping("isTransInAsset")
//    public int isTransInAsset(String finprodId){
//        return finPlanProfitAssetService.isTransInAsset(finprodId);
//    }
}
