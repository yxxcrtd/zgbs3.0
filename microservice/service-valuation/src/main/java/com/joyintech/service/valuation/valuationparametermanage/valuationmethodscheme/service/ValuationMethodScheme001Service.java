/*
 * 文件名：ValuationMethodScheme001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年8月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.valuationparametermanage.valuationmethodscheme.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.valuationparametermanage.valuationmethodscheme.dao.ValuationMethodScheme001Mapper;
import com.joyintech.service.valuation.valuationparametermanage.valuationmethodscheme.dto.ValuationMethodScheme001Dto;
import com.joyintech.tams.constant.DictCodeList;

/**
 * 
 * 估值方法方案
 * 
 * @author zhangwei
 * @version 1.0 2018年8月27日
 * @see ValuationMethodScheme001Service
 * @since 1.0
 */
@Service(value="serviceValuationMethodScheme001Service")
@Transactional(rollbackFor = Exception.class)
public class ValuationMethodScheme001Service extends CommonExportService<ValuationMethodScheme001Dto> {

	@Autowired
	private ValuationMethodScheme001Mapper valuationMethodScheme001Mapper;

	/**
	 * 获取估值方案 - 下拉框
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SelectOptionDto> findScheme(Map<String, Object> parameters) {
		List<ValuationMethodScheme001Dto> resultList = valuationMethodScheme001Mapper.selectScheme(parameters);
		List<SelectOptionDto> selectOptionList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(resultList)) {
			for (ValuationMethodScheme001Dto single : resultList) {
				SelectOptionDto selectOptionDto = new SelectOptionDto();
				selectOptionDto.setId(single.getSchemeId());
				selectOptionDto.setText(single.getSchemeName());
				selectOptionList.add(selectOptionDto);
			}
		}
		return selectOptionList;
	}

	/**
	 * 获取估值方案 - 分页
	 * 
	 * @return
	 * @throws Exception
	 */
	public Page<ValuationMethodScheme001Dto> findSchemeByPage(int page, int length, Map<String, Object> parameters){
		Page<ValuationMethodScheme001Dto> pages = super.queryForPage(page, length,
				() -> valuationMethodScheme001Mapper.selectScheme(parameters));
		List<ValuationMethodScheme001Dto> list = pages.getRoot();
		assembleSchDesc(list);
		return pages;
	}

	/**
	 * 获取估值方案详情 - 分页
	 * 
	 * @return
	 * @throws Exception
	 */
	public Page<ValuationMethodScheme001Dto> findSchemeDetailByPage(int page, int length,
			Map<String, Object> parameters){
		return super.queryForPage(page, length, () -> valuationMethodScheme001Mapper.selectSchemeDetail(parameters));
	}

	/**
	 * 获取估值方案详情 - 分页 : 特殊资产估值
	 * 
	 * @return
	 * @throws Exception
	 */
	public Page<ValuationMethodScheme001Dto> findSpecialAssetsByPage(int page, int length,
			Map<String, Object> parameters) {
		return super.queryForPage(page, length, () -> valuationMethodScheme001Mapper.selectSpecialAssets(parameters));
	}

	/**
	 * 获取估值方案详情 - 不分页
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ValuationMethodScheme001Dto> findSchemeDetail(Map<String, Object> parameters) {
		return valuationMethodScheme001Mapper.selectSchemeDetail(parameters);
	}

	/**
	 * 获取估值方案详情 - 不分页 : 特殊资产估值
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ValuationMethodScheme001Dto> findSpecialAssets(Map<String, Object> parameters) {
		return valuationMethodScheme001Mapper.selectSpecialAssets(parameters);
	}

	/**
	 * 获取产品代码 - 特殊资产查询下拉框
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SelectOptionDto> findFinprod(Map<String, Object> parameters) {
		List<ValuationMethodScheme001Dto> resultList = super.queryForPage(
				() -> valuationMethodScheme001Mapper.selectFinprod(parameters)).getRoot();
		List<SelectOptionDto> selectOptionList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(resultList)) {
			for (ValuationMethodScheme001Dto single : resultList) {
				SelectOptionDto selectOptionDto = new SelectOptionDto();
				selectOptionDto.setId(single.getFinprodId());
				selectOptionDto.setText(single.getFinprodName() + "(" + single.getFinprodId() + ")");
				selectOptionList.add(selectOptionDto);
			}
		}
		return selectOptionList;
	}

	/**
	 * 组装方案简述
	 */
	private void assembleSchDesc(List<ValuationMethodScheme001Dto> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			for (ValuationMethodScheme001Dto dto : list) {
				String schemeDesc = dto.getSchemeName();
				dto.setScheme(schemeDesc);
			}
		}
	}

	/**
	 * 通用联动查询
	 * 
	 * @param param
	 *            : dict_code_parent,dict_code_child,item_code_parent
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@DataSource(DataSourceType.OTHER)
	public List<SelectOptionDto> getLinkSelectOption(Map<String, Object> param) {
		return valuationMethodScheme001Mapper.getLinkSelectOption(param);
	}

	/**
	 * 是否存在默认估值方案
	 * 
	 * @return
	 */
	public boolean getIsdefault() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("isdefault", DictCodeList.是否_是.getValue());
		Integer count = valuationMethodScheme001Mapper.getIsdefault(params);
		return count > 0 ? true : false;
	}
	
}
