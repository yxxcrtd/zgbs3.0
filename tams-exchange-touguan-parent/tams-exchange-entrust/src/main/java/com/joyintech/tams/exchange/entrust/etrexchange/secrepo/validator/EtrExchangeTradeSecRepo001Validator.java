/*
 * 文件名：EtrExchangeTrade001Validator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * @author meifaxin
 * @version 1.0 2021年12月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.validator;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 场内现券指令（除可转债外债券）
 * 
 * @author meifaxin
 * @version 1.0 2021年12月21日
 * @see EtrExchangeTradeSecRepo001Validator
 * @since 1.0
 */
@Component
public class EtrExchangeTradeSecRepo001Validator implements EtrCommonValidator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ValidMessage> bussValid(Object value) {
		List<ValidMessage> errorList = new ArrayList<>();
		// TODO Auto-generated method stub
		EtrExchangeTradeSaveSecRepoRequest dto = (EtrExchangeTradeSaveSecRepoRequest) value;
		if (StringUtil.isNotEmpty(dto.getOperateType())&&dto.getOperateType().equals("submit")) {

		}
		return errorList;
	}
}
