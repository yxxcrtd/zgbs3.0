/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meifaxin
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.service;

import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.exchange.entrust.common.validator.EtrExchangeCommonValidator;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.controller.EtrExchangeTradeSecRepo001Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 场内债券指令（现券+通用质押式回购）
 * @author qmh
 * @version 1.0 2022年4月7日
 * @see EtrExchangeTradeSecRepoValidatorService
 * @since 1.0
 */
@Service
public class EtrExchangeTradeSecRepoValidatorService implements EtrExchangeCommonValidator {

	@Autowired
	private EtrExchangeTradeCommon001Service etrExchangeTradeCommon001Service;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ValidMessage> bussValid(Object value) {
		List<ValidMessage> errorList = new ArrayList<>();
		return errorList;
	}
}
