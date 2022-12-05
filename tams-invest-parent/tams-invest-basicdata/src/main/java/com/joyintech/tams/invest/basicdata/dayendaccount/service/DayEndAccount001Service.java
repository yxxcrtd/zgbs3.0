/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.basicdata.dayendaccount.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.invest.basicdata.dayendaccount.dto.AccountResult;
import com.joyintech.tams.invest.basicdata.dayendaccount.dto.DayEndAccount002Dto;
import com.joyintech.tams.invest.basicdata.dayendaccount.dto.DayEndAccountDto;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 银行间债券指令
 * @author chengzhong
 * @version 1.0 2021年1月19日
 * @see DayEndAccount001Service
 * @since 1.0
 */
@Service
public class DayEndAccount001Service extends CommonExportService<DayEndAccountDto> {

	
	private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 查询日终核对数据
     * <功能详细描述>
     * @param param 查询参数
     * @return 分页结果
     * @see
     * @since 1.0
     */
	public Page<DayEndAccountDto> selectForPage(Map<String, Object> param) {
		try {
			String userId = SecurityUtil.getLoginUser().getUserName();
			if (StringUtils.isNotEmpty(userId)) {
				logger.info("请求用户【{}】", "userId = " + userId);
				param.put("userId", SecurityUtil.getLoginUser().getUserName());
				return restTemplate.postForPage(
						ServiceApplication.BASICDATA_SERVICE_NAME + "/DayEndAccount/selectForPage", param,
						DayEndAccountDto.class);
			}
		} catch (Exception ex) {
			logger.info(ex.getMessage());
		}
		return null;
	}


	/**
	 * 获取资产信息
	 * @param param
	 * @return
	 */
	public Page<SelectOptionDto> queryAssetCode(Map<String, Object> param) {
		return restTemplate.postForPage(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/DayEndAccount/queryAssetCode", param,
				SelectOptionDto.class);
	}

	/**
	 * 对账
	 * @param dtoList
	 * @return
	 */
	public AjaxResult qryAccount(List<DayEndAccountDto> dtoList) {
		AccountResult result = restTemplate.postForObject(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/DayEndAccount/qryAccount", dtoList,
				AccountResult.class);
		AjaxResult ajaxResult = new AjaxResult();
		ajaxResult.setMessage(result.getMessage());
		return ajaxResult;
	}

	/**
	 * 查询成交明细
	 * @param param
	 * @return
	 */
	public DayEndAccount002Dto getReconDealInfo(Map<String, Object> param) {
		return restTemplate.postForObject(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/DayEndAccount/getReconDealInfo", param,
				DayEndAccount002Dto.class);
	}

	/**
	 * 查询成交明细
	 * @param param
	 * @return
	 */
//	public DayEndAccount002Dto getDealDetailInfo(Map<String, Object> param) {
//		return restTemplate.postForObject(
//				ServiceApplication.BASICDATA_SERVICE_NAME + "/DayEndAccount/getDealDetailInfo", param,
//				DayEndAccount002Dto.class);
//	}

	public Page<SelectOptionDto> selectCustomerId(Map<String, Object> param) {
		return restTemplate.postForPage(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/DayEndAccount/selectCustomerId", param,
				SelectOptionDto.class);
	}

	public Page<SelectOptionDto> queryBusiAcctNo(Map<String, Object> param) {
		return restTemplate.postForPage(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/DayEndAccount/queryBusiAcctNo", param,
				SelectOptionDto.class);
	}

	public Page<DayEndAccountDto> selectPage(Map<String, Object> parameters) {
		return restTemplate.postForPage(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/DayEndAccount/selectPage", parameters,
				DayEndAccountDto.class);
	}

	public Page<SelectOptionDto> selectSecAcctId(Map<String, Object> param) {
		return restTemplate.postForPage(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/DayEndAccount/selectSecAcctId", param,
				SelectOptionDto.class);
	}
}
