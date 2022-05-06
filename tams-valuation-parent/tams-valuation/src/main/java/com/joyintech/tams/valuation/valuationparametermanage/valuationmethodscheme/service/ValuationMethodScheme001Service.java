/*
 * 文件名：ValuationMethodScheme001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年8月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.dto.ValuationMethodScheme001Dto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * 
 * 估值方法方案
 * @author zhangwei
 * @version 1.0 2018年8月27日
 * @see ValuationMethodScheme001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ValuationMethodScheme001Service extends CommonExportService<ValuationMethodScheme001Dto>{
	
	
	/**
     * 获取估值方案 - 下拉框
     * @return
     * @throws Exception
     */
	public List<SelectOptionDto> findScheme(Map<String, Object> parameters) {
		return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
                + "/ValuationMethodScheme001/findScheme",
                parameters, SelectOptionDto.class);
		
    }
	
	/**
     * 获取估值方案 - 分页
     * @return
     * @throws Exception
     */
	public Page<ValuationMethodScheme001Dto> findSchemeByPage(Map<String, Object> parameters) {
    	return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME
                + "/ValuationMethodScheme001/findSchemeByPage",
                parameters, ValuationMethodScheme001Dto.class);
    }

	 /**
     * 获取估值方案详情 - 分页
     * @return
     * @throws Exception
     */
	public Page<ValuationMethodScheme001Dto> findSchemeDetailByPage(Map<String, Object> parameters){
		return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME
                + "/ValuationMethodScheme001/findSchemeDetailByPage",
                parameters, ValuationMethodScheme001Dto.class);
    }
	
	/**
     * 获取估值方案详情 - 分页 : 特殊资产估值
     * @return
     * @throws Exception
     */
	public Page<ValuationMethodScheme001Dto> findSpecialAssetsByPage(Map<String, Object> parameters) {
		return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME
                + "/ValuationMethodScheme001/findSpecialAssetsByPage",
                parameters, ValuationMethodScheme001Dto.class);
    }
	
	
	/**
     * 获取估值方案详情 - 不分页
     * @return
     * @throws Exception
     */
	public List<ValuationMethodScheme001Dto> findSchemeDetail(Map<String, Object> parameters) {
		return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
                + "/ValuationMethodScheme001/findSchemeDetail",
                parameters, ValuationMethodScheme001Dto.class);
    }
	
	/**
     * 获取估值方案详情 - 不分页 : 特殊资产估值
     * @return
     * @throws Exception
     */
	public List<ValuationMethodScheme001Dto> findSpecialAssets(Map<String, Object> parameters) {
		return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
                + "/ValuationMethodScheme001/findSpecialAssets",
                parameters, ValuationMethodScheme001Dto.class);
    }
	
	/**
     * 获取产品代码 - 特殊资产查询下拉框
     * @return
     * @throws Exception
     */
	public List<SelectOptionDto> findFinprod(Map<String, Object> parameters) {
		return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
                + "/ValuationMethodScheme001/findFinprod",
                parameters, SelectOptionDto.class);
    }
	

//    /**
//     * 组装方案简述
//     */
//    private void assembleSchDesc(List<ValuationMethodScheme001Dto> list) {
//    	if(CollectionUtils.isNotEmpty(list)) {
//    		for (ValuationMethodScheme001Dto dto : list) {
//    			String schemeDesc = dto.getSchemeName() ;
//				dto.setScheme(schemeDesc);
//			}
//    	}
//    }
    
    /**
     * 通用联动查询
     * @param param : dict_code_parent,dict_code_child,item_code_parent
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.OTHER)
	public List<SelectOptionDto> getLinkSelectOption(Map<String, Object> param) {
    	return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
                + "/ValuationMethodScheme001/getLinkSelectOption",
                param, SelectOptionDto.class);
	}
	
	
	/**
     * 是否存在默认估值方案
     * @return
     * @throws Exception
     */
	public boolean getIsdefault() {
		return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME
                + "/ValuationMethodScheme001/getIsdefault", null, boolean.class);
    }



	/**
	 * @Description:根据投资目的+估值方法过滤  估值市价_专家法
	 * @auther: yuzj
	 * @date: 2021/8/12
	 * @param:
	 * @return:
	 */
	public List<SelectOptionDto> getResultByInvAimAndValuationMethod(List<SelectOptionDto> selectOptionDtoList,Map<String, Object> param){
//当投资目的为[收取合同现金流量及出售为目的],估值方法为[市值法]时,估值第一、第二市价枚举值删除专家法
		//当投资目的为出售资产为目的,选择市值法或市值成本孰低法时，选择市值法或市值成本孰低法时，估值第一市价改为市价来源、市价来源枚举值删除专家法；
		String invAim = (String) param.get("invAim");//投资目的
		String valuationMethod = (String) param.get("valuationMethod");//估值方法
		if(StringUtils.isNotEmpty(invAim) && StringUtils.isNotEmpty(valuationMethod)
				&& (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(invAim)
				&& DictCodeListExt1.估值方法_市值法.getValue().equals(valuationMethod)) ||
				(DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(invAim) &&(DictCodeListExt1.估值方法_市值法.getValue().equals(valuationMethod)
						|| DictCodeListExt1.估值方法_市值成本孰低法.getValue().equals(valuationMethod)))
		){
			for (int i = 0; i < selectOptionDtoList.size(); i++) {
				if (DictCodeList.估值市价_专家法.getValue().equals(selectOptionDtoList.get(i).getId())) {
					selectOptionDtoList.remove(i--);
				}
			}
		}
		return  selectOptionDtoList;
	}



}
