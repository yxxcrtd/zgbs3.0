/*
 * 文件名：RskAuthorityManageService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.service;

import java.util.List;
import java.util.Map;

import com.joyintech.framework.grid.Page;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.dto.MstCustomerInfoAddDto;
import com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.dto.SeniorExecutiveInfDto;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.constant.OperationType;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author wangzehui
 * @version 1.0 2018年11月30日
 * @see AgencyDetailsInf002Service
 * @since 1.0
 */
@Service
public class AgencyDetailsInf002Service extends CommonExportService<SeniorExecutiveInfDto>
		implements BaseProcessService<SeniorExecutiveInfDto> {

	/**
	 * {@inheritDoc}
	 */
	public boolean save(SeniorExecutiveInfDto entity) {
		String action = StringUtil.EMPTY;
		if (entity.getOperateType().equals("delete")) {
			action = "delete";
		} else if (entity.getOperateType().equals("new")) {
			action = "insert";
		} else {
			action = "update";
		}
		restTemplate.postForObject(ServiceApplication.RISK_SERVICE_NAME + "/AgencyDetailsInf002/" + action, entity,
				Integer.class);
		return true;
	}

	/**
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @param pageParam
	 * @return
	 * @see
	 * @since 1.0
	 */
	public Page<SelectOptionDto> getRelateOrgInf(Map<String, Object> pageParam) {
		return restTemplate.postForPage(ServiceApplication.RISK_SERVICE_NAME + "/AgencyDetailsInf002/getRelateOrgInf",
				pageParam, SelectOptionDto.class);
	}

	public List<MstCustomerInfoAddDto> getMstCustomerInfoAdd(Map<String, Object> map) {
		return restTemplate.postForList(
				ServiceApplication.RISK_SERVICE_NAME + "/AgencyDetailsInf002/getMstCustomerInfoAdd", map,
				MstCustomerInfoAddDto.class);
	}

	public List<SeniorExecutiveInfDto> getCorrelationInf(Map<String, Object> map) {
		return restTemplate.postForList(
				ServiceApplication.RISK_SERVICE_NAME + "/AgencyDetailsInf002/getCorrelationInf", map,
				SeniorExecutiveInfDto.class);
	}

	@Override
	public void doComplete(SeniorExecutiveInfDto entity, OperationType type) {
		// TODO Auto-generated method stub
		
	}
}
