/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meifaxin
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.common.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.order.exchange.business.dto.*;
import com.joyintech.background.component.order.exchange.common.constant.UnitEnum;
import com.joyintech.background.component.order.exchange.common.dto.EtrBaseDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrParameterDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrSinglePortfolioDto;
import com.joyintech.background.component.order.exchange.common.service.EtrComponentCommService;
import com.joyintech.background.component.order.exchange.common.service.impl.EtrComponentFactory;
import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.SecPrinSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.AccurIntForSecheduleCalculator;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.*;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.component.business.bond.BusiFinExercisePlanService;
import com.joyintech.tams.component.business.common.dto.FinStockInfoDto;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.exchange.entrust.common.validator.EtrExchangeCommonValidator;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dao.EtrExchangeTradeCommonMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.FinProductOthCodeDto;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.SubEqutityDto;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.*;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeQuotationRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeQuotationResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.position.service.PositionRequestServiceEtrExchangeTradeSaveRequest;
import com.joyintech.tams.invest.common.asset.dto.AssetGenerateEntry;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.asset.util.AssetCommonUtil;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecData;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeCommonervice;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.joyintech.tams.constant.DictCodeList.委托状态_已撤回;
import static com.joyintech.tams.constant.DictCodeList.数据状态_修改中;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_修改;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_新建;
import static com.joyintech.tams.invest.common.asset.service.BondAssetCalcService.*;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 *
 * @author meifaxin
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeCommon001ValidatorService
 * @since 1.0
 */
@Service
public class EtrExchangeTradeCommonValidatorService implements EtrExchangeCommonValidator {

	@Autowired
	private EtrExchangeTradeCommon001Service etrExchangeTradeCommon001Service;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ValidMessage> bussValid(Object value) {
		List<ValidMessage> errorList = new ArrayList<>();
		if(value instanceof EtrExchangeTradeSaveRequest){
			EtrExchangeTradeSaveRequest dto = (EtrExchangeTradeSaveRequest) value;
			if (StringUtil.equals(dto.getCurrentTab(),"gd")) {
				if (dto.getOperateType().equals("save") || dto.getOperateType().equals("submit")) {
//				if (StringUtil.equals(CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL"), "02")) {
					if (!StringUtil.isNotEmpty(dto.getBusinessModel())) {
						errorList.add(new ValidMessage("businessModel", "请输入业务模式"));
					}
//				} else {
//					if (!StringUtil.isNotEmpty(dto.getInvAim())) {
//						errorList.add(new ValidMessage("invAim", this.getMessage("ETR_ERROR_0060")));
//					}
//				}
				}
			}
		}
		return errorList;
	}
}
