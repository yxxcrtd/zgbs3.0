/*
 * 文件名：RskAuthorityManageService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.risk.customerrelationtree.agencydetailsinf.service;

import java.util.List;
import java.util.Map;

import com.joyintech.framework.grid.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.jdbc.datasource.DynamicDataSourceHolder;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.risk.customerrelationtree.agencydetailsinf.dao.AgencyDetailsInf002Mapper;
import com.joyintech.service.risk.customerrelationtree.agencydetailsinf.dto.MstCustomerInfoAddDto;
import com.joyintech.service.risk.customerrelationtree.agencydetailsinf.dto.SeniorExecutiveInfDto;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author qiangchengxi
 * @version 1.0 2018年11月30日
 * @see AgencyDetailsInf002Service
 * @since 1.0
 */
@Service("backAgencyDetailsInf002Service")
public class AgencyDetailsInf002Service extends BaseService<SeniorExecutiveInfDto> {
	@Autowired
	private AgencyDetailsInf002Mapper agencyDetailsInf002Mapper;

	/**
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	public Page<SelectOptionDto> getRelateOrgInf(int pageNo, int length, Map<String, Object> param) {
		DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
		Page<SelectOptionDto> page = super.queryForPageForSelectReturn(pageNo, length, () -> agencyDetailsInf002Mapper.getRelateOrgInf(param));
		DynamicDataSourceHolder.clearDataSource();
		return page;
	}

	/**
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	public List<MstCustomerInfoAddDto> getMstCustomerInfoAdd(Map<String, Object> map) {
		DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
		List<MstCustomerInfoAddDto> resultList = agencyDetailsInf002Mapper.getMstCustomerInfoAdd(map);
		DynamicDataSourceHolder.clearDataSource();
		return resultList;
	}

	/**
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	public List<SeniorExecutiveInfDto> getCorrelationInf(Map<String, Object> map) {
		DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
		List<SeniorExecutiveInfDto> resultList = agencyDetailsInf002Mapper.getCorrelationInf(map);
		DynamicDataSourceHolder.clearDataSource();
		return resultList;
	}

	/**
	 * 插入
	 * 
	 * @param entity
	 * @return
	 * @since 1.0
	 */
	public void insert(SeniorExecutiveInfDto entity) {
		if (null != entity && entity.getCorClass().equals("1")) {//添加高管信息
			List<SeniorExecutiveInfDto> lst = entity.getSelectedItem();
			if (lst != null && lst.size() > 0) {
				for (SeniorExecutiveInfDto e : lst) {
					SeniorExecutiveInfDto temp = entity;
					temp.setExecutiveName(e.getExecutiveName());
					temp.setTitle(e.getTitle());
					agencyDetailsInf002Mapper.insertSelective(entity);
				}
			}
		} else {
			agencyDetailsInf002Mapper.insertSelective(entity);
		}
	}

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return
	 * @since 1.0
	 */
	public void update(SeniorExecutiveInfDto entity) {

		agencyDetailsInf002Mapper.updateByPrimaryKeySelective(entity);
	}

	/**
	 * 删除监督指标
	 * 
	 * @param name
	 */
	public int delete(SeniorExecutiveInfDto dto) {

		return agencyDetailsInf002Mapper.deleteByPrimaryKey(dto.getSeqno());
	}
}
