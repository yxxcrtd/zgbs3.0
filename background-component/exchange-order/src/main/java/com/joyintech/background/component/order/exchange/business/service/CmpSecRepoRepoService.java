package com.joyintech.background.component.order.exchange.business.service;

import com.joyintech.background.component.order.exchange.business.dto.EtrSecRepoRepoDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrParameterDto;
import com.joyintech.background.component.order.exchange.common.service.EtrSubComponentService;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

@Component
public class CmpSecRepoRepoService extends EtrSubComponentService<EtrSecRepoRepoDto> {

	public BindingResult entrustCheck(@Validated EtrSecRepoRepoDto dto, BindingResult result) {
		// 交易规则配置
		EtrParameterDto settings = settings();
		return result;
	}
}
