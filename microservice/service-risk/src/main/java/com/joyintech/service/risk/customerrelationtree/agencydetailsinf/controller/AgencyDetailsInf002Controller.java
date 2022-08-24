/*
 * 文件名：RskAuthorityManageController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.risk.customerrelationtree.agencydetailsinf.controller;

import java.util.List;
import java.util.Map;

import com.joyintech.framework.annotation.DTXTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.risk.creditmanagement.ceassetgroup.dto.CreditLinesAstGrpDto;
import com.joyintech.service.risk.customerrelationtree.agencydetailsinf.dto.MstCustomerInfoAddDto;
import com.joyintech.service.risk.customerrelationtree.agencydetailsinf.dto.SeniorExecutiveInfDto;
import com.joyintech.service.risk.customerrelationtree.agencydetailsinf.service.AgencyDetailsInf002Service;
import com.joyintech.service.risk.rskauthoritymanage.dto.RskAuthorityManageDto;
import com.joyintech.service.risk.rskauthoritymanage.service.RskAuthorityManageService;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;

/**
 * 风险授权管理
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年11月30日
 * @see AgencyDetailsInf002Controller
 * @since 1.0
 */
@RestController("agencyDetailsInf002Ctl_dtx")
@RequestMapping("/AgencyDetailsInf002")
public class AgencyDetailsInf002Controller {
    /**
     * AgencyDetailsInf002Service
     */
    @Autowired
    private AgencyDetailsInf002Service agencyDetailsInf002Service;
    /**
     * 用户下拉框
     * @param param
     * @return
     */
    @PostMapping("/getRelateOrgInf/{page}/{length}")
    public Page<SelectOptionDto> getRelateOrgInf(@RequestBody Map<String, Object> param,
                                                 @PathVariable("page") int page, @PathVariable("length") int length) {
        return agencyDetailsInf002Service.getRelateOrgInf(page, length, param);
    }
    
    /**
     * 用户下拉框
     * @param param
     * @return
     */
    @PostMapping("/getMstCustomerInfoAdd")
    public List<MstCustomerInfoAddDto> getMstCustomerInfoAdd(@RequestBody Map<String, Object> param) {
        return agencyDetailsInf002Service.getMstCustomerInfoAdd(param);
    }
    
    /**
     * 获取高管信息
     * @param param
     * @return
     */
    @PostMapping("/getCorrelationInf")
    public List<SeniorExecutiveInfDto> getCorrelationInf(@RequestBody Map<String, Object> param) {
        return agencyDetailsInf002Service.getCorrelationInf(param);
    }
    
    /**
     * 插入
     * @param dto
     * @throws Exception
     * @since 1.0
     */
	 @Transactional
	 @DTXTransaction
    @PostMapping("insert")
    public void insert(@RequestBody SeniorExecutiveInfDto dto) {
		 agencyDetailsInf002Service.insert(dto);
    }
    
    /**
     * 更新
     * @param dto
     * @throws Exception
     * @since 1.0
     */
    @PostMapping("update")
    public void update(@RequestBody SeniorExecutiveInfDto dto) {
    	agencyDetailsInf002Service.update(dto);
    }
	    
	/**
     * 删除
     * @param 页面选中的数据
     * @return 结果
     * @throws Exception
     */
    @PostMapping("delete")
    public int delete(@RequestBody SeniorExecutiveInfDto dto) {
        return agencyDetailsInf002Service.delete(dto);
    }
}
