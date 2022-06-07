/*
 * 文件名：FinPlanAssetNController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.asset.plan.planNet.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.asset.plan.planNet.dto.BussRepAssetMngProdInfo;
import com.joyintech.service.asset.plan.planNet.dto.BussRepOutInvestAgrInfo;
import com.joyintech.service.asset.plan.planNet.dto.FinPlanNetAssetDto;
import com.joyintech.service.asset.plan.planNet.service.FinPlanNetAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 计划类资产（净值型）controller
 * 
 * @author liuwei
 * @version 1.0 2018年9月10日
 * @see FinPlanNetAssetController
 * @since 1.0
 */
@RestController("FinPlanNetAssetController_dtx")
@RequestMapping("/FinPlanNetAsset")
public class FinPlanNetAssetController {
    
    /**
     * 计划类资产（净值型）Service
     */
    @Autowired
    private FinPlanNetAssetService finPlanNetAssetService;
    
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
        return finPlanNetAssetService.getFinProdMarketId(finprodMarketId);
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
    public Page<FinPlanNetAssetDto> selectAll(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page, @PathVariable("length") int length) throws Exception {
        return finPlanNetAssetService.selectForPage(page, length, param);
    }
    
    /**
     * 删除
     * @param dto 页面选中的数据
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public void delete(@RequestBody FinPlanNetAssetDto dto)
        throws Exception {
        finPlanNetAssetService.delete(dto);
    }

    /**
     * 插入
     * @param dto 页面选中的数据
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("insert")
    public void insert(@RequestBody FinPlanNetAssetDto dto)
            throws Exception {
        finPlanNetAssetService.insert(dto);
    }

    /**
     * 修改
     * @param dto 页面选中的数据
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("update")
    public void update(@RequestBody FinPlanNetAssetDto dto)
            throws Exception {
        finPlanNetAssetService.update(dto);
    }
    /**
     * 出账编号的联想下拉框事件
     *
     * @param map 输入管理人参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @PostMapping("getCmsId")
    public List<SelectOptionDto> getCmsId(@RequestBody Map<String,Object> map){
        return finPlanNetAssetService.getCmsId(map);
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
    public FinPlanNetAssetDto getChange(String cmsId){
        return finPlanNetAssetService.getChange(cmsId);
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
        return finPlanNetAssetService.getIssuer(map);
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
        return finPlanNetAssetService.getTrusteeId(map);
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
        return finPlanNetAssetService.getCalendarId(map);
    }
    /**
     * 修改或双击查看界面
     * @param finprodId 金融产品代码
     */
    @PostMapping("getBasicInfoByFinprodId")
    public FinPlanNetAssetDto getBasicInfoByFinprodId(@RequestBody String finprodId){
        return finPlanNetAssetService.getBasicInfoByFinprodId(finprodId);
    }
    /**
     * 资产管理计划业务组件
     *
     * @param finprodId 金融产品代码
     * @return BussRepAssetMngProdInfo
     * @see
     * @since 1.0
     */
    @GetMapping("getRepAssetMngProdInfoByFinprodId")
    public BussRepAssetMngProdInfo getRepAssetMngProdInfoByFinprodId(String finprodId){
        return finPlanNetAssetService.getRepAssetMngProdInfoByFinprodId(finprodId);
    }
    /**
     * 委外投资-协议方式业务组件
     *
     * @param finprodId 金融产品代码
     * @return BussRepOutInvestAgrInfo
     * @see
     * @since 1.0
     */
    @GetMapping("getRepOutInvestAgrInfoByFinprodId")
    public BussRepOutInvestAgrInfo getRepOutInvestAgrInfoByFinprodId(String finprodId){
        return finPlanNetAssetService.getRepOutInvestAgrInfoByFinprodId(finprodId);
    }
    /**
     * 查询市场代码
     * @param finprodMarketId 市场代码
     */
    @GetMapping("selectFinprodMarketId")
    public int selectFinprodMarketId( String finprodMarketId){
        return finPlanNetAssetService.selectFinprodMarketId(finprodMarketId);
    }
    /**
     * 查询客户名称
     *
     * @param custormId 客户代码
     * @return 客户名称
     * @see
     * @since 1.0
     */
    @GetMapping("getName")
    public String getName(String custormId){
        return finPlanNetAssetService.getName(custormId);
    }
    // 增加查询资产代码 by 赵东东 2019.10.12 start 
    /**
     * 查询资产代码
     * @param finprodId 资产代码
     */
    @GetMapping("getFinprodId")
    public String  getFinprodId( String finprodMarketId){
        return finPlanNetAssetService.getFinprodId(finprodMarketId);
    }
     // 增加查询资产代码 by 赵东东 2019.10.12 end 
}
