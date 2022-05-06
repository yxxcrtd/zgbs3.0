/*
 * 文件名：TrdOutRightRepoTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：张亮
 * 修改时间：2019年01月24日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.buyoutrepo.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.joyintech.framework.grid.Page;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.trade.trdrepotrade.service.TrdRepoTrade002Service;

import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.buyoutrepo.dto.TrdOutRightRepoTrade002Dto;
import com.joyintech.tams.invest.trade.buyoutrepo.dto.TrdOutRightRepoTrade004Dto;
import com.joyintech.tams.invest.trade.buyoutrepo.dto.TrdOutRightRepoTradePtl;
import com.joyintech.tams.invest.trade.buyoutrepo.service.TrdOutRightRepoTrade002Service;
import com.joyintech.tams.invest.common.dto.cstp.TrdRepoTradeCSTPDto;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * 买断式回购多组合新建
 * @author zhanggenglin
 * @version 1.0 2019年12月25日
 * @see TrdOutRightRepoTrade003Controller
 * @since 1.0
 */
@Controller
@PageId("TrdOutRightRepoTrade003")
@RequestMapping("/invest/trade/buyoutrepo/TrdOutRightRepoTrade003")
public class TrdOutRightRepoTrade003Controller extends BaseController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * {@linkplain}
	 */
	@Autowired
	private TrdOutRightRepoTrade002Service trdOutRightRepoTrade002Service;

	/**
	 * {@linkplain}
	 */
	@Autowired
	private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

	/**
	 * {@linkplain}
	 */
	@Autowired
	private  BussCommonMapperDelegateService bussCommonMapperDelegateService;
	/**
	 * {@linkplain}
	 */
	@Autowired
	private BaseGetBusinessId baseGetBusinessId;

	@Autowired
	private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

	/**
	 * {@linkplain}
	 */
	@Autowired
	private IPlatformDate iPlatformDate;

	/**
	 * DateCalService
	 */
	@Autowired
	private DateCalService dateCalService;
	@Autowired
	private TrdRepoTrade002Service trdRepoTrade002Service;
	@Override
	public String init(Model model) {
		this.setSelectOptionToModel(model, DictCode.交易场所, DictCode.回购拆借方向, DictCode.清算速度,
				DictCode.计息基础, DictCode.币种, DictCode.结算方式, DictCode.回购期限品种,DictCode.分配方式,DictCode.计算模式);

		model.addAttribute("platDate", iPlatformDate.getSysDateStr());
		model.addAttribute("noBranchNoValue", CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
		model.addAttribute("BACK_LEND_DIRECTION_FORWARD", DictCodeList.回购拆借方向_正回购.getValue());
		model.addAttribute("BACK_LEND_DIRECTION_NEGATIVE", DictCodeList.回购拆借方向_逆回购.getValue());
		model.addAttribute("BASIS_360", DictCodeList.计息基础_A_360.getValue());
		model.addAttribute("BASIS_365", DictCodeList.计息基础_A_365.getValue());
		model.addAttribute("SETTLESPEED_0", DictCodeList.清算速度_T_0.getValue());
		model.addAttribute("SETTLESPEED_1", DictCodeList.清算速度_T_1.getValue());
		model.addAttribute("SETTLESPEED_2", DictCodeListExt2.清算速度_T_2.getValue());
		model.addAttribute("REPO_TYPE_R001", DictCodeList.回购期限品种_R001.getValue());
		model.addAttribute("REPO_TYPE_R007", DictCodeList.回购期限品种_R007.getValue());
		model.addAttribute("REPO_TYPE_R014", DictCodeList.回购期限品种_R014.getValue());
		model.addAttribute("REPO_TYPE_R021", DictCodeList.回购期限品种_R021.getValue());
		model.addAttribute("REPO_TYPE_R1M", DictCodeList.回购期限品种_R1M.getValue());
		model.addAttribute("REPO_TYPE_R2M", DictCodeList.回购期限品种_R2M.getValue());
		model.addAttribute("REPO_TYPE_R3M", DictCodeList.回购期限品种_R3M.getValue());
		model.addAttribute("REPO_TYPE_R4M", DictCodeList.回购期限品种_R4M.getValue());
		model.addAttribute("REPO_TYPE_R6M", DictCodeList.回购期限品种_R6M.getValue());
		model.addAttribute("REPO_TYPE_R9M", DictCodeList.回购期限品种_R9M.getValue());
		model.addAttribute("REPO_TYPE_R1Y", DictCodeList.回购期限品种_R1Y.getValue());
        // add by wangguoxian 费用设置 20190722 start
        // 佣金
        model.addAttribute("FEE_TYPE_05", BussinessDictCodeList.费用类型_佣金.getValue());
        // 交易手续费
        model.addAttribute("FEE_TYPE_06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        // 结算手续费
        model.addAttribute("FEE_TYPE_08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        // 交易场所_银行间
        model.addAttribute("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        // 交易场所_上海证券交易所
        model.addAttribute("TRADE_PLACE_02", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        // 交易场所_深圳证券交易所
        model.addAttribute("TRADE_PLACE_03", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        // 上交所期限品种
        model.addAttribute("TERM_TYPE_REBACK_GC001", BussinessDictCodeList.回购期限品种_GC001.getValue());
        model.addAttribute("TERM_TYPE_REBACK_GC002", BussinessDictCodeList.回购期限品种_GC002.getValue());
        model.addAttribute("TERM_TYPE_REBACK_GC003", BussinessDictCodeList.回购期限品种_GC003.getValue());
        model.addAttribute("TERM_TYPE_REBACK_GC004", BussinessDictCodeList.回购期限品种_GC004.getValue());
        model.addAttribute("TERM_TYPE_REBACK_GC007", BussinessDictCodeList.回购期限品种_GC007.getValue());
        model.addAttribute("TERM_TYPE_REBACK_GC014", BussinessDictCodeList.回购期限品种_GC014.getValue());
        model.addAttribute("TERM_TYPE_REBACK_GC028", BussinessDictCodeList.回购期限品种_GC028.getValue());
        model.addAttribute("TERM_TYPE_REBACK_GC091", BussinessDictCodeList.回购期限品种_GC091.getValue());
        model.addAttribute("TERM_TYPE_REBACK_GC182", BussinessDictCodeList.回购期限品种_GC182.getValue());

        // 深交所期限品种
        model.addAttribute("TERM_TYPE_REBACK_R_001", BussinessDictCodeList.回购期限品种_R_001.getValue());
        model.addAttribute("TERM_TYPE_REBACK_R_002", BussinessDictCodeList.回购期限品种_R_002.getValue());
        model.addAttribute("TERM_TYPE_REBACK_R_003", BussinessDictCodeList.回购期限品种_R_003.getValue());
        model.addAttribute("TERM_TYPE_REBACK_R_004", BussinessDictCodeList.回购期限品种_R_004.getValue());
        model.addAttribute("TERM_TYPE_REBACK_R_007", BussinessDictCodeList.回购期限品种_R_007.getValue());
        model.addAttribute("TERM_TYPE_REBACK_R_014", BussinessDictCodeList.回购期限品种_R_014.getValue());
        model.addAttribute("TERM_TYPE_REBACK_R_028", BussinessDictCodeList.回购期限品种_R_028.getValue());
        model.addAttribute("TERM_TYPE_REBACK_R_091", BussinessDictCodeList.回购期限品种_R_091.getValue());
        model.addAttribute("TERM_TYPE_REBACK_R_182", BussinessDictCodeList.回购期限品种_R_182.getValue());
        model.addAttribute("TERM_TYPE_REBACK_RC001", BussinessDictCodeList.回购期限品种_RC001.getValue());
        model.addAttribute("TERM_TYPE_REBACK_RC003", BussinessDictCodeList.回购期限品种_RC003.getValue());
        model.addAttribute("TERM_TYPE_REBACK_RC007", BussinessDictCodeList.回购期限品种_RC007.getValue());

        // add by wangguoxian 费用设置 20190722 end
		// 交易市场_二级市场
		model.addAttribute("TRADE_PLACE_SEC", DictCodeListExt1.交易市场_二级市场.getValue());
		// 投资目的_摊余成本类
		model.addAttribute("INV_AIM_TC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
		// 回购拆借方向_正回购
		model.addAttribute("BACK_LEND_DIRECTION_03", DictCodeList.回购拆借方向_正回购.getValue());
		// 回购拆借方向_逆回购
		model.addAttribute("BACK_LEND_DIRECTION_04", DictCodeList.回购拆借方向_逆回购.getValue());
		// 回购拆借方向_逆回购
		model.addAttribute("FINPROD_TYPE_TG_F04", DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
		// 回购拆借方向_逆回购
		model.addAttribute("FINPROD_TYPE_TG_F05", DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
		// 交易方向_买入
		model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
		// 是否_否
		model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
		// 监督时分_交易监控
		model.addAttribute("MY_SUP_TIME_TYPE_DEAL", DictCodeListExt1.监督时分_交易监控.getValue());
		// 是否来自代办
		model.addAttribute("formTodoList", !WorkFlowUtil.isNotFromToDoList());

		model.addAttribute("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());

		TrdOutRightRepoTrade002Dto trdOutRightRepoTrade002Dto = new TrdOutRightRepoTrade002Dto();
		
		if (!WorkFlowUtil.isNotFromToDoList() || StringUtil.equals(this.getParam("workflowStatus"), DictCodeListExt1.交易流程状态_流程中.getValue())) {
			ProcessMark pro = trdOutRightRepoTrade002Service.getBusinessInfo(this.getParam(GlobalConstant.WORKFLOW_ID));
			if (pro != null) {
				trdOutRightRepoTrade002Dto = (TrdOutRightRepoTrade002Dto) JsonUtils.json2Object(pro.getBusinessData(),
						TrdOutRightRepoTrade002Dto.class);
				trdOutRightRepoTrade002Dto.setSecTotalPrin(trdOutRightRepoTrade002Dto.getSecTotalPrin().divide(new BigDecimal(10000)));
				model.addAttribute(GlobalConstant.WORKFLOW_ID, this.getParam(GlobalConstant.WORKFLOW_ID));
				if (!StringUtil.isEmptyList(trdOutRightRepoTrade002Dto.getBussTrdTransferOrder())) {
					model.addAttribute("entity_bussTrdTransferOrder",
							JsonUtils.object2Json(trdOutRightRepoTrade002Dto.getBussTrdTransferOrder().get(0)));
				}
				if (StringUtil.isNotEmpty(trdOutRightRepoTrade002Dto.getTrdOutRightRepoPersonInfo())) {
					model.addAttribute("entity_trdInvestPersonInfo",
							JsonUtils.object2Json(trdOutRightRepoTrade002Dto.getTrdOutRightRepoPersonInfo()));
				}
			}
		} // 交易编号不为空时
		else if (StringUtil.isNotEmpty(this.getParam("tradeId"))) {
			trdOutRightRepoTrade002Dto = trdOutRightRepoTrade002Service
					.queryBuyOutRepoByTradeId(this.getParam("tradeId"));
			trdOutRightRepoTrade002Dto.setSecTotalPrin(trdOutRightRepoTrade002Dto.getSecTotalPrin().divide(new BigDecimal(10000)));
			if (StringUtil.isNotEmpty(trdOutRightRepoTrade002Dto.getTrdOutRightRepoPersonInfo())) {
				model.addAttribute("entity_trdInvestPersonInfo",
						JsonUtils.object2Json(trdOutRightRepoTrade002Dto.getTrdOutRightRepoPersonInfo()));
			}
		} // 委托代码不为空时
		else if (StringUtil.isNotEmpty(this.getParam("entrustId"))) {
		    String entrustId=this.getParam("entrustId");
            String entrustAmtTotle=this.getParam("entrustAmtTotle");
            String price=this.getParam("price");
            
		    String[] entrustIdList = entrustId.split("\\,");
		    
			trdOutRightRepoTrade002Dto = trdOutRightRepoTrade002Service.getEntrustInfo(entrustIdList[0]);
			trdOutRightRepoTrade002Dto.setfSettleMethod(DictCodeListExt1.结算方式_券款对付.getValue());
			trdOutRightRepoTrade002Dto.setmSettleMethod(DictCodeListExt1.结算方式_券款对付.getValue());
			if (StringUtil.isNotEmpty(trdOutRightRepoTrade002Dto.getTrdOutRightRepoPersonInfo())) {
				model.addAttribute("entity_trdInvestPersonInfo",
						JsonUtils.object2Json(trdOutRightRepoTrade002Dto.getTrdOutRightRepoPersonInfo()));
			}
			
			Long start=(long)0;
            Long end=(long)200000;
			if(entrustIdList.length>0){
			    for(int i=0;i<entrustIdList.length;i++){
	                TrdOutRightRepoTrade002Dto trrt02 = new TrdOutRightRepoTrade002Dto();
	                trrt02 = trdOutRightRepoTrade002Service.getEntrustInfo(entrustIdList[i]);
	                if(trrt02.getEntrustInfo()!=null&&trrt02.getEntrustInfo().getEntrustTermStart()!=null){
                        if(start<trrt02.getEntrustInfo().getEntrustTermStart()){
                            start=trrt02.getEntrustInfo().getEntrustTermStart();
                        }
	                }
	                
	                if(trrt02.getEntrustInfo()!=null&&trrt02.getEntrustInfo().getEntrustTermEnd()!=null){
                        if(end>trrt02.getEntrustInfo().getEntrustTermStart()){
                            end=trrt02.getEntrustInfo().getEntrustTermEnd();
                        }
                    }
	            }
			    trdOutRightRepoTrade002Dto.getEntrustInfo().setEntrustTermStart(start);
			    trdOutRightRepoTrade002Dto.getEntrustInfo().setEntrustTermEnd(end);
			}
			
			String endDate=this.getParam("endDate");
            model.addAttribute("endDate", endDate);
			
			BigDecimal doEntrustMoney = trdOutRightRepoTrade002Service.selectDoEntrustMoney(entrustId);
			trdOutRightRepoTrade002Dto.setSecTotalPrin(doEntrustMoney.divide(new BigDecimal(10000)));
            if (StringUtil.isNotEmpty(price)) {
                BigDecimal price2 = new BigDecimal(price);
                trdOutRightRepoTrade002Dto.setRepoRate(price2);
            }
            trdOutRightRepoTrade002Dto.setEntrustId(entrustId);
		} else {
			trdOutRightRepoTrade002Dto.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
			trdOutRightRepoTrade002Dto.setSettleSpeed(DictCodeList.清算速度_T_0.getValue());
			trdOutRightRepoTrade002Dto.setfSettleMethod(DictCodeListExt1.结算方式_券款对付.getValue());
			trdOutRightRepoTrade002Dto.setmSettleMethod(DictCodeListExt1.结算方式_券款对付.getValue());

		}

		model.addAttribute("CALC_MODEL_01", DictCodeListExt3.计算模式_计算回购利率.getValue());
        model.addAttribute("CALC_MODEL_02", DictCodeListExt3.计算模式_计算首次净价.getValue());
        model.addAttribute("CALC_MODEL_03", DictCodeListExt3.计算模式_计算到期净价.getValue());
		model.addAttribute(GlobalConstant.ENTITY, trdOutRightRepoTrade002Dto);
		model.addAttribute("TrdOutRightRepoTradePtl", trdOutRightRepoTrade002Dto.getTrdOutRightRepoTradePtl());
		//add by lizhi 是否限制划款指令开关 2020年2月26日  start
        model.addAttribute("GEN_PAY_ORDERS_AUTO",CacheUtil.getSysParam(BussinessConstant.GEN_PAY_ORDERS_AUTO));
        //add by lizhi 是否限制划款指令开关 2020年2月26日  end
		String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
		PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
		pageElementConfigDto.setPageId("BaseRiskMathComm");
		Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
		pageElementConfigDto.setPageId(this.getPageId());
		Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
		isEleShow.putAll(map);
		model.addAttribute("isEleShow", isEleShow);
		if(workflowId!=null){
            return "invest/trade/buyoutrepo/TrdOutRightRepoTrade002";
        }else{
            return super.init(model);
        }
	}

	/**
	 * 查询债券代码下拉框
	 * 
	 *            查询参数
	 * @return 债券代码下拉框
	 */
	@ResponseBody
	@RequestMapping("querySecFinprodIdList")
	public List<SelectOptionDto> querySecFinprodIdList() {
		return trdOutRightRepoTrade002Service.querySecFinprodIdList(RequestUtil.getPageParam());
	}

	/**
	 * 1.7计算按钮点击事件
	 *
	 * @param inputDto
	 *            画面参数
	 * @return 计算结果
	 * @see
	 * @since 1.0
	 */
	@RequestMapping("calculateData")
	@ResponseBody
	public AjaxResult calculateData(@RequestBody TrdOutRightRepoTrade004Dto inputDto) {
		List<ValidMessage> errorList = new ArrayList<ValidMessage>();
		if (!StringUtil.isNotEmpty(inputDto.getRepoVdate())) {
			errorList.add(new ValidMessage("vdate",
					this.getMessage("FW_E_0006", this.getMessage("TrdOutRightRepoTrade002.vdate"))));
		}
		if (!StringUtil.isNotEmpty(inputDto.getRepoMdate())) {
			errorList.add(new ValidMessage("mdate",
					this.getMessage("FW_E_0006", this.getMessage("TrdOutRightRepoTrade002.mdate"))));
		}
		if (!StringUtil.isNotEmpty(inputDto.getRepoRate())) {
			errorList.add(new ValidMessage("repoRate",
					this.getMessage("FW_E_0006", this.getMessage("TrdOutRightRepoTrade002.amtInfo.repoRate"))));
		}
		if (StringUtil.isNotEmpty(inputDto.getFinprodId()) && StringUtil.isNotEmpty(inputDto.getRepoVdate()) && StringUtil.isNotEmpty(inputDto.getRepoMdate())){
			if (trdOutRightRepoTrade002Service.checkDateInPayPlan(inputDto.getFinprodId(), inputDto.getRepoVdate(), inputDto.getRepoMdate()) == 0){
				ValidMessage validMessage = new ValidMessage("finprodId", getMessage("TRD_E_0018"));
				errorList.add(validMessage);
			}
		}
		if (!StringUtil.isNotEmpty(inputDto.getRepoCalculateField())) {
			errorList.add(new ValidMessage(
					getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.calculateField"))));
		} else if (StringUtil.isNotEmpty(inputDto.getFinprodId())){
			FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class,
					inputDto.getFinprodId(), FinProduct.class);
			if (finProduct != null) {
				inputDto.setVdate(finProduct.getVdate());
				inputDto.setMdate(finProduct.getMdate());
			}

			if (StringUtil.equals(inputDto.getRepoCalculateField(), "fCleanPrice")) {
				if (!StringUtil.isNotEmpty(inputDto.getfCleanPrice())){
					errorList.add(new ValidMessage("fCprice", this.getMessage("FW_E_0006",
							this.getMessage("TrdOutRightRepoTrade002.amtInfo.firstCleanPrice"))));
				}
				// 画面.交割日<债券起息日
				if (StringUtil.isNotEmpty(inputDto.getVdate()) && StringUtil.isNotEmpty(inputDto.getSettleDate())) {
					if (DateUtil.compareDate(inputDto.getRepoVdate(), inputDto.getVdate()) < 0) {
						errorList.add(new ValidMessage("vdate",
								this.getMessage("TRD_E_0012")));
					}
				}
			} else if (StringUtil.equals(inputDto.getRepoCalculateField(), "mCleanPrice")) {
				if (!StringUtil.isNotEmpty(inputDto.getmCleanPrice())){
					errorList.add(new ValidMessage("mCprice", this.getMessage("FW_E_0006",
							this.getMessage("TrdOutRightRepoTrade002.amtInfo.matCleanPrice"))));
				}
				// 债券到期日<=交割日时
				if (StringUtil.isNotEmpty(inputDto.getRepoMdate()) && StringUtil.isNotEmpty(inputDto.getMdate())) {
					if (DateUtil.compareDate(inputDto.getMdate(), inputDto.getRepoMdate()) <= 0) {
						errorList.add(new ValidMessage("vdate",
								this.getMessage("TRD_E_0013")));
					}
				}
			}
		}
		//首次净价不能大于委托时候的首次净价区间
		if (StringUtil.isNotEmpty(inputDto.getEntrustId())){
			TrdOutRightRepoTrade002Dto	dto = trdOutRightRepoTrade002Service.getfCpriceBtw(inputDto.getEntrustId());
			if (dto !=null && StringUtil.isNotEmpty(inputDto.getfCleanPrice()) && 
					StringUtil.isNotEmpty(dto.getfCprice()) && StringUtil.isNotEmpty(dto.getfCprice2())){
				if (inputDto.getfCleanPrice().compareTo(dto.getfCprice()) < 0 || inputDto.getfCleanPrice().compareTo(dto.getfCprice2()) > 0){
					ValidMessage validMessage = new ValidMessage("fCprice", "首次净价不在委托的首次净价区间内：" +
							dto.getfCprice() + "-" + dto.getfCprice2());
					errorList.add(validMessage);
				}				
			}
		}
		if (StringUtil.isEmptyList(errorList)) {
			return trdBondInvestTrade002Service.calculateData(inputDto);
		}

		AjaxResult ar = new AjaxResult();
		ar.setErrorList(errorList);

		return ar;
	}
	
	/**
     * 计算按钮check(针对计算回购利率)
     */
    @RequestMapping("calculateDataCheck")
    @ResponseBody
    public AjaxResult calculateDataCheck(@RequestBody TrdOutRightRepoTrade004Dto inputDto) {
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        if (!StringUtil.isNotEmpty(inputDto.getRepoVdate())) {
            errorList.add(new ValidMessage("vdate",
                    this.getMessage("FW_E_0006", this.getMessage("TrdOutRightRepoTrade002.vdate"))));
        }
        if (!StringUtil.isNotEmpty(inputDto.getRepoMdate())) {
            errorList.add(new ValidMessage("mdate",
                    this.getMessage("FW_E_0006", this.getMessage("TrdOutRightRepoTrade002.mdate"))));
        }
        //add by zhangnian 20200106  start
        if (!StringUtil.isNotEmpty(inputDto.getCalendarId())) {
            errorList.add(new ValidMessage("dealCalId",
                    this.getMessage("FW_E_0006", this.getMessage("TrdOutRightRepoTrade002.dealCalId"))));
        }
        //买断式回购交易  计算模式改造 start
        if (StringUtil.isEmpty(inputDto.getCalcModel())) {
            ValidMessage validMessage = new ValidMessage("calcModel", getMessage("请选择计算模式"));
            errorList.add(validMessage);
        } else {
            if (StringUtil.equals(inputDto.getCalcModel(), DictCodeListExt3.计算模式_计算回购利率.getValue())) {
                if (inputDto.getfCleanPrice() == null) {
                    errorList.add(new ValidMessage("fCprice", this.getMessage("FW_E_0006",
                            this.getMessage("TrdOutRightRepoTrade002.amtInfo.firstCleanPrice"))));
                }
                if (inputDto.getmCleanPrice() == null) {
                    errorList.add(new ValidMessage("mCprice", this.getMessage("FW_E_0006",
                            this.getMessage("TrdOutRightRepoTrade002.amtInfo.matCleanPrice"))));
                }
            } else if (StringUtil.equals(inputDto.getCalcModel(), DictCodeListExt3.计算模式_计算首次净价.getValue())) {
                if (inputDto.getRepoRate() == null) {
                    errorList.add(new ValidMessage("repoRate", this.getMessage("FW_E_0006",
                            this.getMessage("TrdOutRightRepoTrade002.amtInfo.repoRate"))));
                }
                if (inputDto.getmCleanPrice() == null) {
                    errorList.add(new ValidMessage("mCprice", this.getMessage("FW_E_0006",
                            this.getMessage("TrdOutRightRepoTrade002.amtInfo.matCleanPrice"))));
                }
            } else if (StringUtil.equals(inputDto.getCalcModel(), DictCodeListExt3.计算模式_计算到期净价.getValue())) {
                if (inputDto.getRepoRate() == null) {
                    errorList.add(new ValidMessage("repoRate", this.getMessage("FW_E_0006",
                            this.getMessage("TrdOutRightRepoTrade002.amtInfo.repoRate"))));
                }
                if (inputDto.getfCleanPrice() == null) {
                    errorList.add(new ValidMessage("fCprice", this.getMessage("FW_E_0006",
                            this.getMessage("TrdOutRightRepoTrade002.amtInfo.firstCleanPrice"))));
                }
            }
        }
        //买断式回购交易  计算模式改造 end
        if (StringUtil.isNotEmpty(inputDto.getFinprodId()) && StringUtil.isNotEmpty(inputDto.getRepoVdate()) && StringUtil.isNotEmpty(inputDto.getRepoMdate())) {
            if (trdOutRightRepoTrade002Service.checkDateInPayPlan(inputDto.getFinprodId(), inputDto.getRepoVdate(), inputDto.getRepoMdate()) == 0) {
                ValidMessage validMessage = new ValidMessage("finprodId", getMessage("TRD_E_0018"));
                errorList.add(validMessage);
            }
        }
        /*if (!StringUtil.isNotEmpty(inputDto.getRepoCalculateField())) {
            errorList.add(new ValidMessage(
                    getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.calculateField"))));
        } else*/
        if (StringUtil.isNotEmpty(inputDto.getFinprodId())) {
            FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class,
                    inputDto.getFinprodId(), FinProduct.class);
            if (finProduct != null) {
                inputDto.setVdate(finProduct.getVdate());
                inputDto.setMdate(finProduct.getMdate());
            }
            if (StringUtil.equals(inputDto.getCalcModel(), DictCodeListExt3.计算模式_计算首次净价.getValue())) {
                /*if (!StringUtil.isNotEmpty(inputDto.getmCleanPrice())){
                    errorList.add(new ValidMessage("mCprice", this.getMessage("FW_E_0006",
                            this.getMessage("TrdOutRightRepoTrade002.amtInfo.matCleanPrice"))));
                }*/
                // 债券到期日<=交割日时
                if (StringUtil.isNotEmpty(inputDto.getRepoMdate()) && StringUtil.isNotEmpty(inputDto.getMdate())) {
                    if (DateUtil.compareDate(inputDto.getMdate(), inputDto.getRepoMdate()) <= 0) {
                        errorList.add(new ValidMessage("vdate",
                                this.getMessage("TRD_E_0013")));
                    }
                }
            } else if (StringUtil.equals(inputDto.getCalcModel(), DictCodeListExt3.计算模式_计算到期净价.getValue())) {
                /*if (!StringUtil.isNotEmpty(inputDto.getfCleanPrice())){
                    errorList.add(new ValidMessage("fCprice", this.getMessage("FW_E_0006",
                            this.getMessage("TrdOutRightRepoTrade002.amtInfo.firstCleanPrice"))));
                }*/
                // 画面.交割日<债券起息日
                if (StringUtil.isNotEmpty(inputDto.getVdate()) && StringUtil.isNotEmpty(inputDto.getSettleDate())) {
                    if (DateUtil.compareDate(inputDto.getRepoVdate(), inputDto.getVdate()) < 0) {
                        errorList.add(new ValidMessage("vdate",
                                this.getMessage("TRD_E_0012")));
                    }
                }
            }
        }
        //首次净价不能大于委托时候的首次净价区间
        if (StringUtil.isNotEmpty(inputDto.getEntrustId())) {
            TrdOutRightRepoTrade002Dto dto = trdOutRightRepoTrade002Service.getfCpriceBtw(inputDto.getEntrustId());
            if (dto != null && StringUtil.isNotEmpty(inputDto.getfCleanPrice()) &&
                    StringUtil.isNotEmpty(dto.getfCprice()) && StringUtil.isNotEmpty(dto.getfCprice2())) {
                if (inputDto.getfCleanPrice().compareTo(dto.getfCprice()) < 0 || inputDto.getfCleanPrice().compareTo(dto.getfCprice2()) > 0) {
                    ValidMessage validMessage = new ValidMessage("fCprice", "首次净价不在委托的首次净价区间内：" +
                            dto.getfCprice() + "-" + dto.getfCprice2());
                    errorList.add(validMessage);
                }
            }
        }
        AjaxResult ar = new AjaxResult();
        ar.setErrorList(errorList);

        return ar;
    }

	/**
	 * 画面check
	 * 
	 * @param dto
	 *            验证对象
	 * @param br
	 *            验证信息
	 * @return 验证结果
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@Valid @RequestBody TrdOutRightRepoTrade002Dto dto, BindingResult br) {
		if (br.hasErrors()) {
			return new AjaxResult(br);
		}

		String confirmMsg = "";
		// 到期日如果是节假日 要提醒
		if (dateCalService.isDealCalendarHoliday(dto.getMdate(), dto.getDealCalId())) {
			confirmMsg = getMessage("BUSS_COM_E_0039", getMessage("TrdRepoTrade002.mdate")) + "<br/>";
		}

		// 逆回购时，本金金额 〉 组合可用剩余现金，提示
		if (DictCodeList.回购拆借方向_逆回购.getValue().equals(dto.getPs())) {
			/*if (StringUtil.isEmpty(dto.getChlId()) && StringUtil.isNotEmpty(dto.getfSettleAmt())
					&& StringUtil.isNotEmpty(dto.getBussTrdOutRightRepoTrade().getAvalibleCash())) {
				if (dto.getfSettleAmt().compareTo(dto.getBussTrdOutRightRepoTrade().getAvalibleCash()) > 0) {
					confirmMsg += getMessage("TRD_C_0002") + "<br/>";
				}
			}
            else if (StringUtil.isNotEmpty(dto.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(dto.getPayType())
                     && dto.getTradeAmtAndFee() != null && dto.getTradeAmtAndFee().compareTo(dto.getBussTrdOutRightRepoTrade().getChannelAvailableCash()) > 0) {
            	confirmMsg += this.getMessage("TRD_C_0004",getMessage("invest.channelId")) + "<br/>";
            }*/
			//edit by xueying 20200730 satrt
			//组合分配的数据进行判断
			List<TrdOutRightRepoTradePtl> trdOutRightRepoTradePtlList=dto.getTrdOutRightRepoTradePtl();
			if(CollectionUtils.isNotEmpty(trdOutRightRepoTradePtlList)){
				for(TrdOutRightRepoTradePtl ptlDto:trdOutRightRepoTradePtlList){
					if (StringUtil.isEmpty(dto.getChlId()) && StringUtil.isNotEmpty(ptlDto.getMatSettleAmt())
							&& StringUtil.isNotEmpty(ptlDto.getBussTrdAvailableCashQueryavalibleCash())) {
						if (ptlDto.getMatSettleAmt().compareTo(ptlDto.getBussTrdAvailableCashQueryavalibleCash()) > 0) {
							confirmMsg += getMessage("TRD_C_0002") + "<br/>";
						}
					}
					else if (StringUtil.isNotEmpty(dto.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(dto.getPayType())
							&& ptlDto.getTradeRepAmt() != null && ptlDto.getTradeRepAmt().compareTo(ptlDto.getChannelAvailableCash()) > 0) {
						confirmMsg += this.getMessage("TRD_C_0004",getMessage("invest.channelId")) + "<br/>";
					}
				}
			}
			//edit by xueying 20200730 end
		}

		if (confirmMsg.length() > 0) {
			AjaxResult wr = new AjaxResult();
			wr.setData(confirmMsg);
			return wr;
		} else {
			return super.commonCheck(br, dto.getOperateType());
		}
	}

	/**
	 * 保存操作
	 *
	 * @param dto
	 *            dto
	 * @return 结果
	 * @throws Exception
	 *             异常
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("save")
	public AjaxResult save(@RequestBody TrdOutRightRepoTrade002Dto dto) throws InvocationTargetException, IllegalAccessException, WorkFlowException, IOException, InstantiationException {
	    
	    List<TrdOutRightRepoTrade002Dto> dtoList = new ArrayList<>();
        List<TrdOutRightRepoTradePtl> fenpeiList = dto.getTrdOutRightRepoTradePtl();//组合分配
        List<BussTrdTransferOrderDto> orderList = dto.getBussTrdTransferOrder();//划款指令
        
        if(fenpeiList != null && fenpeiList.size()>0){
            for (int i = 0; i < fenpeiList.size(); i++) {
				TrdOutRightRepoTradePtl fenpei = fenpeiList.get(i);
				if(fenpei.getTradeAmt()!=null){
				if (fenpei.getTradeAmt().compareTo(new BigDecimal(0)) > 0) {
					TrdOutRightRepoTrade002Dto tempDto = new TrdOutRightRepoTrade002Dto();
					BeanUtil.copyProperties(tempDto, dto);
					List<BussTrdTransferOrderDto> tmpOrderList = new ArrayList<BussTrdTransferOrderDto>();
					tmpOrderList.add(orderList.get(i));
					tempDto.setPortfolioId(fenpei.getPortfolioId());
					tempDto.setBussTrdTransferOrder(tmpOrderList);
					tempDto.setEntrustId(fenpei.getEntrustId());
					tempDto.setSecAcctId(fenpei.getSecManageAcctId());
					tempDto.setSecTotalPrin(fenpei.getTradeAmt());
					tempDto.setRepoRate(fenpei.getRepoRate());
					tempDto.setfIntAmt(fenpei.getFirstIntAmt());
					tempDto.setfSettleAmt(fenpei.getFirstSettleAmt());
					tempDto.setmIntAmt(fenpei.getMatIntAmt());
					tempDto.setmSettleAmt(fenpei.getMatSettleAmt());
					tempDto.setProcedureAmt(fenpei.getProcedureAmt());
					tempDto.setSettleAmt(fenpei.getSettleAmt());
					tempDto.setTradeAmtAndFee(fenpei.getTradeRepAmt());

					if (baseGetBusinessId != null) {
						BussinessCodeParameter codeParameter = new BussinessCodeParameter();
						if (StringUtil.equals(tempDto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
							codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
						} else {
							codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
						}
						tempDto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
					} else {
						tempDto.setTradeId(IDGenerator.generate());
					}
					tempDto.setRiskTrialRetDto(null);
					//多组合交易对风险试算处理逻辑
					if (CollectionUtils.isNotEmpty(dto.getRiskTrialRetDto())) {
						List<RiskTrialRetDto> riskTrialRetlist = new ArrayList<>();
						for (RiskTrialRetDto riskTrialRetDto : dto.getRiskTrialRetDto()) {
							if (StringUtils.equals(riskTrialRetDto.getPortfolioCode(), fenpei.getPortfolioId())) {
								riskTrialRetlist.add(riskTrialRetDto);
							}
							List<String> colist = interfRiskRdsService.queryPortfolioPackageDtlByBagId(riskTrialRetDto.getPortfolioCode());
							if (!CollectionUtils.isEmpty(colist) && colist.contains(fenpei.getPortfolioId())) {
								riskTrialRetlist.add(riskTrialRetDto);
							}
						}
						tempDto.setRiskTrialRetDto(riskTrialRetlist);
					}
					dtoList.add(tempDto);
				} else {
					fenpeiList.remove(i);
					i--;
				}
			}
                }
            }else{
                TrdOutRightRepoTrade002Dto tempDto = new TrdOutRightRepoTrade002Dto();
                BeanUtil.copyProperties(tempDto, dto);
                if (StringUtil.isEmpty(dto.getTradeId())) {
                    if (baseGetBusinessId != null) {
                        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                        if (StringUtil.equals(tempDto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
                            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
                        } else {
                            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
                        }
                        tempDto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
                    } else {
                        tempDto.setTradeId(IDGenerator.generate());
                    }
                }
                dtoList.add(tempDto);
            }
//        }
        //----------add by jjpei 20200114 核风险版本 start----------
        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
            AjaxResult ar =interfRiskRdsService.checkBatchRiskRds(dtoList,new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(), BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(),DictCodeList.平安资产类型_买断式回购.getValue(),
                    (sm,en)-> {
						try {
							return interfRiskRdsService.sendRiskRdsData(sm,en);
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
							throw new IOException();
						} catch (InstantiationException e) {
							logger.error(e.getMessage(), e);
							throw new InstantiationException();
						}
					});
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        //----------add by jjpei 20200114 核风险版本 end----------  
		ProcessParameter<TrdOutRightRepoTrade002Dto> processParameter = new ProcessParameter<TrdOutRightRepoTrade002Dto>();
        WorkFlowParameter<TrdOutRightRepoTrade002Dto> params = new WorkFlowParameter<TrdOutRightRepoTrade002Dto>();
        params.setElementId(dto.getElementId());
        params.setPageId("TrdOutRightRepoTrade002");
		params.setBusinessCode(dto.getTradeId());
		params.setBusinessCode((entity) -> entity.getTradeId());
		params.setBusinessType(DictCodeList.共通业务类型_买断式回购交易.getValue());
		// 设置工作流注解
		params.setComment(entity -> this.getMessage("TrdOutRightRepoTrade002.tradeId") + "(" + entity.getTradeId() + ")"
				+ this.getMessage("TrdOutRightRepoTrade002.repoType") + "("
				+ DictCodeList.回购类型_买断式回购.getCodeName() + ")"
				+ this.getMessage("TrdOutRightRepoTrade002.dealDate") + "(" + DateUtil.format(entity.getTradeDate())
				+ ")");
		processParameter.setWorkFlowParameter(params);
		// 调用工作流事件
		String msg = trdOutRightRepoTrade002Service.start(processParameter, dtoList.toArray(new TrdOutRightRepoTrade002Dto[]{}));
		// 返回工作流错误信息
		return new AjaxResult(msg);
	}

    // add by wangguoxian 费用设置 20190720 start
    /**
     * 计算交易费用，结算费，佣金
     * 
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("calFee")
    @ResponseBody
    public TrdOutRightRepoTrade002Dto calFee(@RequestBody TrdOutRightRepoTrade002Dto dto) {
        TrdOutRightRepoTrade002Dto result = trdOutRightRepoTrade002Service.calFee(dto);
        return result;

}
    // add by wangguoxian 费用设置 20190720 end

	/**
	 * 查询债券cstp交易下拉框
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryRepoCstpInfoList")
	public Page<SelectOptionDto> queryRepoCstpInfoList() {
		Map<String, Object> param = RequestUtil.getPageParam();
		param.put("prodType",BussinessDictCodeList.回购交易_买断式.getValue());
		Page<SelectOptionDto> suggestDtos = trdRepoTrade002Service.queryRepoCstpInfoList(param);
		return suggestDtos;
	}

	/**
	 * 根据成交编号查询cstp交易信息
	 * @param fedealno 成交编号
	 * @return cstp交易信息
	 */
	@ResponseBody
	@RequestMapping("queryRepoCstpInfoByNo")
	public TrdRepoTradeCSTPDto queryRepoCstpInfoByNo(@RequestBody Map<String,Object> map){
		String prodType = BussinessDictCodeList.回购交易_买断式.getValue();
		return trdRepoTrade002Service.queryRepoCstpInfoByNo(map);
	}
	
	/**
     * 获取下一个工作日
     *
     * @param params 画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    @RequestMapping("getNextWorkDay")
    @ResponseBody
    public String getNextWorkDay(@RequestParam Map<String, Object> params) {
    	if (dateCalService != null) {
            Date tradeDate = DateUtil.parse((String) params.get("tradeDate"));
            List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate,
                    DateUtil.addDays(tradeDate, 31), (String) params.get("calendarId"));
            Date tradingDay = dateCalService.calculationTradingDay(tradeDate, 1, holiday, "1");
            return DateUtil.format(tradingDay);
        }
        return null;
    }
    
    /**
     * 检验是否节假日
     * @param map
     * @return
     */
    @RequestMapping("isDealCalendarHoliday")
    @ResponseBody
    public boolean isDealCalendarHoliday(@RequestBody Map<String, Object> map) {
        return dateCalService.isDealCalendarHoliday(DateUtil.parse((String)map.get("mpayDate")),(String)map.get("calendarId"));
    }
    
    // add by zhanggenglin 质押式回购多组合 20190813 start
    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.业务类型_买断式回购预处理);
        String index=baseGetBusinessId.buildBussinessCode(codeParameter);
        return StringUtil.trimHtmlTag(index);
    }
    // add by zhanggenglin 质押式回购多组合 20190813 end
    
    
    /**
     * 查询委托信息
     *
     * @return 委托信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getEntrustInfo")
    public List<TrdOutRightRepoTradePtl> getEntrustInfo() {
        Map<String,Object> pageMap = RequestUtil.getPageParam();
        String entrustId = (String)pageMap.get("entrustId");
        String distribution = (String)pageMap.get("distribution");
        String ps = (String)pageMap.get("ps");
        BaseEtrInvestInfoDto ret =  trdRepoTrade002Service.getEntrustInfo(entrustId);
        
        List<TrdOutRightRepoTradePtl> list = new ArrayList<>();
        if(ret.getBaseEtrInvestInfoDtoList()!=null){
            for(int i=0;i<ret.getBaseEtrInvestInfoDtoList().size();i++){
                if(ret.getBaseEtrInvestInfoDtoList().get(i).getEtrEntrust()!=null){
                    TrdOutRightRepoTradePtl trdOutRightRepoTradePtl = new TrdOutRightRepoTradePtl();
                    BigDecimal doEntrustMoney = trdOutRightRepoTrade002Service.selectDoEntrustMoney(ret.getBaseEtrInvestInfoDtoList().get(i).getEtrEntrust().getEntrustId());
                    trdOutRightRepoTradePtl.setEntrustId(ret.getBaseEtrInvestInfoDtoList().get(i).getEtrEntrust().getEntrustId());
                    trdOutRightRepoTradePtl.setDoEntrustMoney(doEntrustMoney);
                    trdOutRightRepoTradePtl.setTradeAmt(doEntrustMoney);
                    if(ret.getBaseEtrInvestInfoDtoList().get(i).getEtrEntrust().getPrice()==null){
                        trdOutRightRepoTradePtl.setRepoRate(new BigDecimal(0));
                    }else{
                        trdOutRightRepoTradePtl.setRepoRate(ret.getBaseEtrInvestInfoDtoList().get(i).getEtrEntrust().getPrice().multiply(new BigDecimal(100)));
                    }
                    trdOutRightRepoTradePtl.setEntrustDate(ret.getBaseEtrInvestInfoDtoList().get(i).getEtrEntrust().getCreateTime());
                    trdOutRightRepoTradePtl.setPortfolioId(ret.getBaseEtrInvestInfoDtoList().get(i).getEtrEntrust().getPortfolioId());
                    list.add(trdOutRightRepoTradePtl);
                }
            }
            
            if("140".equals(distribution)){//时间优先
                //排序
                Collections.sort(list, new Comparator<TrdOutRightRepoTradePtl>() {
                   @Override
                   public int compare(TrdOutRightRepoTradePtl o1, TrdOutRightRepoTradePtl o2) {
                       // TODO Auto-generated method stub
                       return o1.getEntrustDate().compareTo(o2.getEntrustDate());
                   }
                 });
            }else if("141".equals(distribution)){//数量优先
              //排序
              Collections.sort(list, new Comparator<TrdOutRightRepoTradePtl>() {
                 @Override
                 public int compare(TrdOutRightRepoTradePtl o1, TrdOutRightRepoTradePtl o2) {
                     // TODO Auto-generated method stub
                     return o2.getTradeAmt().compareTo(o1.getTradeAmt());
                 }
               });
            }else if("142".equals(distribution)){//价格优先
                Collections.sort(list, new Comparator<TrdOutRightRepoTradePtl>() {
                    @Override
                    public int compare(TrdOutRightRepoTradePtl o1, TrdOutRightRepoTradePtl o2) {
                        // TODO Auto-generated method stub
                        return o1.getTradeAmt().compareTo(o2.getTradeAmt());
                    }
                  });
                //排序
                if("03".equals(ps)){
                    Collections.sort(list, new Comparator<TrdOutRightRepoTradePtl>() {
                        @Override
                        public int compare(TrdOutRightRepoTradePtl o1, TrdOutRightRepoTradePtl o2) {
                            // TODO Auto-generated method stub
                            return o2.getRepoRate().compareTo(o1.getRepoRate());
                        }
                      });
                }else if("04".equals(ps)){
                    Collections.sort(list, new Comparator<TrdOutRightRepoTradePtl>() {
                        @Override
                        public int compare(TrdOutRightRepoTradePtl o1, TrdOutRightRepoTradePtl o2) {
                            // TODO Auto-generated method stub
                            return o1.getRepoRate().compareTo(o2.getRepoRate());
                        }
                      });
                }
                
            }
        } else if(ret.getEtrEntrust() != null){
            TrdOutRightRepoTradePtl trdOutRightRepoTradePtl = new TrdOutRightRepoTradePtl();
            BigDecimal doEntrustMoney = trdOutRightRepoTrade002Service.selectDoEntrustMoney(
                ret.getEtrEntrust().getEntrustId());
            trdOutRightRepoTradePtl.setEntrustId(ret.getEtrEntrust().getEntrustId());
            trdOutRightRepoTradePtl.setDoEntrustMoney(doEntrustMoney);
            trdOutRightRepoTradePtl.setTradeAmt(doEntrustMoney);
            if (ret.getEtrEntrust().getPrice() == null) {
                trdOutRightRepoTradePtl.setRepoRate(new BigDecimal(0));
            }
            else {
                trdOutRightRepoTradePtl.setRepoRate(
                    ret.getEtrEntrust().getPrice().multiply(new BigDecimal(100)));
            }
            trdOutRightRepoTradePtl.setEntrustDate(ret.getEtrEntrust().getCreateTime());
            trdOutRightRepoTradePtl.setPortfolioId(ret.getEtrEntrust().getPortfolioId());
            list.add(trdOutRightRepoTradePtl);
        }
        return list;
    }
}
