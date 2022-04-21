/*
 * 文件名：MstMarketInstitutions002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年7月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.basicdata.mstmarketinstitutions.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.mstmarketinstitutions.dto.MstMarketInstitutionsDto;
import com.joyintech.service.basicdata.mstmarketinstitutions.service.MstBondCreditRatingService;
import com.joyintech.service.basicdata.mstmarketinstitutions.service.MstMarketInstitutionsService;
import com.joyintech.tams.common.base.entity.InterfCmsInstitution;
import com.joyintech.tams.common.base.entity.MstBondCreditRating;

/**
 * 市场机构的基本信息管理
 * 显示市场机构信息的基本页面
 * @author Wangzehui
 * @version 1.0 2018年7月23日
 * @see MstMarketInstitutionsController
 * @since 1.0
 * 
 */
@RestController("MstMarketInstitutionsCtr_dtx")
@RequestMapping("/MstMarketInstitutions")
public class MstMarketInstitutionsController {
    
    /**
     * 市场机构管理service注入
     */
    @Autowired
    private MstMarketInstitutionsService mstMarketInstitutionsService;
    /**
     * 
     * {@inheritDoc}
     */
    @Autowired
    private MstBondCreditRatingService mstBondCreditRatingService;
    
    /**
     * 客户代码联想文本框service层实现
      *
      * @param map 检索参数
      * @return SuggestDto
      */
    @PostMapping("/getCustomerId")
    public List<SuggestDto> getCustomerId(@RequestBody Map<String, Object> map){
        return mstMarketInstitutionsService.getCustomerId(map);
    }
    /**
     * 客户名称联想文本框service层实现
      *
      * @param map Map<String, Object> 检索参数
      * @return SuggestDto
      */
    @PostMapping("/getCustomerName")
   public List<SuggestDto> getCustomerName(@RequestBody Map<String, Object> map) {
        return mstMarketInstitutionsService.getCustomerName(map);
    }
    /**
     * 使用给定的检索分页数据。返回类型是Service的泛型。
     *  市场机构代码
     *  市场机构名称
     * @param pageParam 查询方法
     * @return Page类
     * @throws Exception 异常信息
     */
    @PostMapping("/selectForPage/{page}/{length}")
    public Page<MstMarketInstitutionsDto> selectForPage(@RequestBody Map<String, Object> pageParam,@PathVariable("page") int page, @PathVariable("length") int length)
    {
        return mstMarketInstitutionsService.selectForPage(page,length,pageParam);
    }
    /**
     * selectPaRskCustomerPage
     * 根据客户代码查询市场机构详细信息
     * <功能详细描述>
     * @param map map
     * @return MstMarketInstitutionsDto
     * @see
     * @since 1.0
     */
    @PostMapping("/selectDetail")
    public MstMarketInstitutionsDto selectDetail(@RequestBody Map<String, Object> map){
        return mstMarketInstitutionsService.selectDetail(map);
    }
    /**
     * 
     *删除时检查数据是否被使用过
     * <功能详细描述>
     * @param dto MstMarketInstitutionsDto
     * @return int
     * @see
     * @since 1.0
     */
    @PostMapping("/checkCodeIsUse")
    public int checkCodeIsUse(@RequestBody MstMarketInstitutionsDto dto){
        return mstMarketInstitutionsService.checkCodeIsUse(dto);
    }
    /**
     * 
     *批量删除时检查数据是否被使用过
     * <功能详细描述>
     * @param item List<MstMarketInstitutionsDto> 
     * @return int
     * @see
     * @since 1.0
     */
    @PostMapping("/checkAllCodeIsUse")
    public List<MstMarketInstitutionsDto> checkAllCodeIsUse(@RequestBody List<MstMarketInstitutionsDto> item){
        return mstMarketInstitutionsService.checkAllCodeIsUse(item);
    }
    /**
     * 
     * 通过客户代码删除客户评级表信息 
     * <功能详细描述>
     * @param customerId String
     * @return int
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/deleteMstBondCreditRatingByPrimaryKey")
    public int deleteMstBondCreditRatingByPrimaryKey(String customerId){
        return mstMarketInstitutionsService.deleteMstBondCreditRatingByPrimaryKey(customerId);
    }
    /**
     * 
     * 通过客户代码查询客户评级表信息 
     * <功能详细描述>
     * @param customerId String
     * @return List<PaRskCustomerRating>
     * @see
     * @since 1.0
     */
    @PostMapping("/selectMstBondCreditRatingByPrimaryKey")
    public List<MstBondCreditRating> selectMstBondCreditRatingByPrimaryKey(String customerId){
        return mstMarketInstitutionsService.selectMstBondCreditRatingByPrimaryKey(customerId);
    }
    /**
     * 
     * 编辑查询页面客户评级表查询分页
     * <功能详细描述>
     * @param pageParam pageParam
     * @return Page<MstBondCreditRating>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @PostMapping("selectMstBondCreditPage/{page}/{length}")
    public Page<MstBondCreditRating> selectMstBondCreditPage(@RequestBody Map<String, Object> pageParam,@PathVariable("page") int page, @PathVariable("length") int length)
    {
        return mstBondCreditRatingService.selectMstBondCreditPage(page,length,pageParam);
    }
    /**
     * 
     * 工作流完成
     * <功能详细描述>
     * @param map
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doComplete")
    public void doComplete(@RequestBody MstMarketInstitutionsDto entity){
        mstMarketInstitutionsService.doComplete(entity);
    }
    
    /**
     * 
     * 省市县联动
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("/getAddressSelectOption")
    public List<SelectOptionDto> getAddressSelectOption(@RequestBody Map<String, Object> param){
        return mstMarketInstitutionsService.getAddressSelectOption(param);
    }
    
    /**
     * 
     * 查询所有的市场机构代码、名称
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @GetMapping("/customerNameCheck")
    public List<MstMarketInstitutionsDto> customerNameCheck(String customerName){
        return mstMarketInstitutionsService.customerNameCheck(customerName);
    }
    
    /**
     * 
     * 根据becif查询CMS_机构信息
     * <功能详细描述>
     * @param becif String
     * @return InterfCmsInstitution
     * @see
     * @since 1.0
     */
    @GetMapping("/selectInterfCmsInstitution")
    public InterfCmsInstitution selectInterfCmsInstitution(String becif){
        return mstMarketInstitutionsService.selectInterfCmsInstitution(becif);
    }
    /**
     * 获取评级机构
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    @RequestMapping("getGradeOrgId")
    public List<SuggestDto> getGradeOrgId(@RequestBody Map<String, Object> param) {
    	return mstMarketInstitutionsService.getGradeOrgId(param);
    }

    /**
     * 
     * 批量删除时检查数据是否进行过客户性质匹配
     * <功能详细描述>
     * @param item List<MstMarketInstitutionsDto> 
     * @return int
     * @see
     * @since 1.0
     */
    @PostMapping("/deleteCheck")
    public List<MstMarketInstitutionsDto> deleteCheck(@RequestBody List<MstMarketInstitutionsDto> item){
        return mstMarketInstitutionsService.deleteCheck(item);
    }

    /**
     * 根据insitituteCode判断是否需要新增
     * @param insitituteCode
     * @return
     */
    @GetMapping("/autoAddInstitutions")
    public MstMarketInstitutionsDto autoAddInstitutions(@RequestParam("insitituteCode") String insitituteCode) {
        return mstMarketInstitutionsService.autoAddInstitutions(insitituteCode);
    }
}
