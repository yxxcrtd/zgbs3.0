/*
 * 文件名：TrdRepoTrade002DtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.trdrepotrade.validator;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.intf.Page;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.trade.common.dto.EtrInvestInfoDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.common.validator.TradeCommonValidator;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade002Dto;
import com.joyintech.tams.invest.trade.trdrepotrade.service.TrdRepoTrade002Service;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 回购资产交易新建画面：TrdRepoTrade002DtoValidator
 *
 * @author 裴家俊
 * @version 1.0 2018年11月1日
 * @see TrdRepoTrade002DtoValidator
 * @since 1.0
 */
@Component
public class TrdRepoTrade002DtoValidator implements TradeCommonValidator {

    /**
     * TrdRepoTrade002Service
     */
    @Autowired(required = false)
    private TrdRepoTrade002Service trdRepoTrade002Service;

    /**
     * BussTrdTransferOrderService
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    
	@Autowired
	private TradeCommonService tradeCommonService;
    /**
     * 白名单校验正则表达式
     */
    private static final String whitePattern = "^[\\u4e00-\\u9fa5\\ue815-\\ue864\\uff01-\\uff5e\\uffe0-\\uffe5\\u3000-\\u3003\\u3005-\\u3017\\u301d-\\u301e\\u2010\\u2013-\\u2016\\u2018-\\u2019\\u201c-\\u201d\\u2025-\\u2026\\u2030\\u2032-\\u2033\\u2035\\u203b\\u00b7\\u0030-\\u0039\\u0041-\\u005a\\u0061-\\u007a\\u2160-\\u216b\\u0020-\\u0026\\u0028-\\u007B\\u007D\\u007E]{0,}$";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        TrdRepoTrade002Dto inputDto = (TrdRepoTrade002Dto) value;
        List<ValidMessage> errorList = new ArrayList<>();

        // 交易场所为银行间时，银行间成交编号必填
//        if (DictCodeListExt1.交易场所_银行间.getValue().equals(inputDto.getTradeMarket())) {
//            if (!StringUtil.isNotEmpty(inputDto.getfTradeId())) {
//                ValidMessage validMessage = new ValidMessage("fTradeId",
//                        getMessage("BUSS_COM_E_0038",
//                                getMessage("TrdRepoTrade002.fTradeId")));
//                errorList.add(validMessage);
//            }
//        }
        // 划款指令
        if (bussTrdTransferOrderService != null) {
            List<ValidMessage> list = bussTrdTransferOrderService.check(
                    inputDto.getBussTrdTransferOrder());
            errorList.addAll(list);
        }

        // 起息日不能大于到期日
        if (inputDto != null
                && inputDto.getVdate() != null
                && inputDto.getMdate() != null) {
            if (DateUtil.compareDate(inputDto.getVdate(),
                    inputDto.getMdate()) > 0) {
                ValidMessage validMessage = new ValidMessage("vdate",
                        getMessage("FW_E_0009", getMessage("TrdLendInvestTrade002.vdate"),
                                getMessage("TrdLendInvestTrade002.mdate")));
                errorList.add(validMessage);
            }
        }

        // 画面.本金金额 〉 未完成委托额度 时，提示
//        if (inputDto.getPrinAmt() != null && inputDto.getEntrustAmt() != null
//                && inputDto.getPrinAmt().compareTo(inputDto.getEntrustAmt()) > 0) {
//            ValidMessage validMessage = new ValidMessage("prinAmt",
//                    getMessage("TRD_W_0009", getMessage("TrdRepoTrade002.prinAmt")));
//            errorList.add(validMessage);
//        }
        // 画面券面总额大于流程中与已完成额度提示  add by wangpeng 20190906
        
        if(inputDto.getPrinAmt() != null &&StringUtil.isNotEmpty(inputDto.getEntrustId())){
			BigDecimal entrustNum = tradeCommonService.getEntrustAmtByEntrustId(inputDto.getEntrustId());
	        BigDecimal wflPrinAmt = tradeCommonService.getWflPrinAmt(inputDto.getEntrustId(), inputDto.getPortfolioId(), null);
	        if(StringUtil.isNotEmpty(inputDto.getWorkflowId()) && !WorkFlowUtil.isNotFromToDoList()){
	        	TrdRepoTrade002Dto dto = trdRepoTrade002Service.getBussinessEntityByWorkflowId(
        				inputDto.getWorkflowId());
        		wflPrinAmt = ArithmeticUtil.subtract(wflPrinAmt, dto.getPrinAmt());
            }
	        if(entrustNum.compareTo(wflPrinAmt.add(inputDto.getPrinAmt())) == -1) {
	        	 ValidMessage validMessage = new ValidMessage("prinAmt",
	        			 "可用委托份额不足,当前最多可委托份额为:"+ entrustNum.subtract(wflPrinAmt));
	                 errorList.add(validMessage);
	        }
		}
        //add by wangpeng 20190906 end 

        if(StringUtil.isNotEmpty(inputDto.getEntrustId())){
        	EtrInvestInfoDto  etrInvestInfoDto = tradeCommonService.findEtrInfo(inputDto.getEntrustId());
        	//价格模式为限价模式
        	//市价模式不校验，指定价格模式为软校验
        	if(StringUtil.equals(DictCodeListExt2.价格模式_限价指令.getValue(), etrInvestInfoDto.getEtrEntrust().getPriceModel())){
        		// 2) 交易方向为逆回购时，修改后的回购利率不能低于委托的回购利率；
                if (inputDto != null
                        && StringUtil.equals(inputDto.getPs(),
                        DictCodeList.回购拆借方向_逆回购.getValue())) {
                    if (inputDto.getRebackPrice() != null && inputDto.getEntrustPrice() != null
                            && inputDto.getRebackPrice().compareTo(inputDto.getEntrustPrice()) < 0) {
                        ValidMessage validMessage = new ValidMessage("rebackPrice",
                                getMessage("TRD_W_0007", getMessage("TrdRepoTrade002.entrustRebackPrice"),
                                        getMessage("TrdRepoTrade002.entrustRebackPrice"),
                                        getMessage("TrdRepoTrade002.lessthan")));
                        errorList.add(validMessage);
                    }
                }
                // 3) 交易方向为正回购时，修改后的回购利率不能高于委托的回购利率；
                else if (inputDto != null
                        && StringUtil.equals(inputDto.getPs(),
                        DictCodeList.回购拆借方向_正回购.getValue())) {
                    if (inputDto.getRebackPrice() != null && inputDto.getEntrustPrice() != null
                            && inputDto.getRebackPrice().compareTo(inputDto.getEntrustPrice()) > 0) {
                        ValidMessage validMessage = new ValidMessage("rebackPrice",
                                getMessage("TRD_W_0007", getMessage("TrdRepoTrade002.entrustRebackPrice"),
                                        getMessage("TrdRepoTrade002.entrustRebackPrice"),
                                        getMessage("TrdRepoTrade002.morethan")));
                        errorList.add(validMessage);
                    }
                }
        	}
        	
        	else if(StringUtil.equals(DictCodeListExt2.价格模式_指定价格.getValue(), etrInvestInfoDto.getEtrEntrust().getPriceModel())){
        		// 2) 交易方向为逆回购时，修改后的回购利率不能低于委托的回购利率；
                if (inputDto != null) {
                    if (inputDto.getRebackPrice() != null && inputDto.getEntrustPrice() != null
                            && inputDto.getRebackPrice().compareTo(inputDto.getEntrustPrice()) != 0) {
                    	ValidMessage validMessage = new ValidMessage("rebackPrice",
                                "价格模式为指定价格，交易时均按照委托的回购利率进行交易");
                        errorList.add(validMessage);
                    }
                }
        	}
        }
        
        // 进行验证：交易中回购期限应等于委托中回购期限，或在委托期限范围内，
        // 若不满足，最终无法提交，给出相应提示：“请输入合理的起息日和到期日”
        if (inputDto != null && inputDto.getTerm() != null) {
            if ((inputDto.getEntrustTermFrom() != null && inputDto.getTerm() > inputDto.getEntrustTermFrom())) {
                ValidMessage validMessage = new ValidMessage("term",
                        getMessage("TRD_E_0006"));
                errorList.add(validMessage);
            }
        }

        // 3) 当回购类型为质押式竞价回购，交易方向为正回购时，
        // 本次回购交易金额<=可使用标准券额度，严格限制，若不满足，则交易无法提交；
        if (inputDto != null && DictCodeList.回购类型_质押式竞价回购.getValue().equals(inputDto.getRepoType())
                && DictCodeList.回购拆借方向_正回购.getValue().equals(inputDto.getPs())) {
        	//BSTOT-1872 update by yzg 标准券可用额度页面未带入 20200305
        	Map<String, Object> params = new HashMap<>();
            params.put("secAcctId", inputDto.getSecAcctId());
            params.put("tradeDate", DateUtil.format(inputDto.getTradeDate()));
            params.put("vdate", DateUtil.format(inputDto.getTradeDate()));
            params.put("ccy", inputDto.getCcy());
            params.put("mdate", DateUtil.format(inputDto.getMdate()));
            params.put("portfolioId", inputDto.getPortfolioId());
            BigDecimal 标准券面总额 = trdRepoTrade002Service.getAvaStandardStockPosition(params);
            if(!StringUtil.isNotEmpty(标准券面总额)){
            	标准券面总额 = BigDecimal.ZERO;
            }
            if (inputDto.getPrinAmt() != null && inputDto.getPrinAmt().compareTo(标准券面总额) > 0) {
                ValidMessage validMessage = new ValidMessage("prinAmt",
                        getMessage("TRD_E_0005"));
                errorList.add(validMessage);
            }
        }
        // 保存质押券时提示质押券登记托管机构不同，不允许保存；（子画面已作校验，此处不再处理）

        // 3) 正回购本金金额不能大于质押券信息中汇总券面总额，若大于，则提示：“正回购委托金额不能大于质押券汇总券面总额”（除11的其他正回购）
        if (DictCodeList.回购拆借方向_正回购.getValue().equals(inputDto.getPs())
                && (DictCodeList.回购类型_质押式协议回购.getValue().equals(inputDto.getRepoType())
                    ||DictCodeList.回购类型_质押式回购.getValue().equals(inputDto.getRepoType()))
                && inputDto.getPrinAmt() != null) {
        	if (inputDto.getBussRepoSec() == null) {
                ValidMessage validMessage = new ValidMessage("prinAmt",
                        getMessage("TRD_E_00121"));
                errorList.add(validMessage);
        	} else {
	        	if (inputDto.getBussRepoSec().getTotalPrinAmt() == null
	        			&& !StringUtil.isEmptyList(inputDto.getBussRepoSec().getPledgeBondList())) {
	        		BigDecimal totalPrinAmt = BigDecimal.ZERO;
	        		for (PledgeBondInfoDto pbi : inputDto.getBussRepoSec().getPledgeBondList()) {
	        			totalPrinAmt = ArithmeticUtil.doAdd(totalPrinAmt, pbi.getPledgePrin());
	        		}
	        		inputDto.getBussRepoSec().setTotalPrinAmt(totalPrinAmt);
	        	}
                //汇总券面总额转换 edit by yeming 20210809 start
                BigDecimal totalPrinAmt = inputDto.getBussRepoSec().getTotalPrinAmt();
                if(StringUtil.equals(CacheUtil.getSysParam("entrustAmtUnit"),"02")){
                    totalPrinAmt = totalPrinAmt.multiply(new BigDecimal(10000));//万元转成元
                }
                //汇总券面总额转换 edit by yeming 20210809 end
	        	if (inputDto.getBussRepoSec().getTotalPrinAmt() == null
	                    || inputDto.getPrinAmt().compareTo(totalPrinAmt) > 0) {
	                ValidMessage validMessage = new ValidMessage("prinAmt",
	                        getMessage("TRD_E_00121"));
	                errorList.add(validMessage);
	        	}
        	}

        }

        // 4)5) 本金金额高于副总裁/总裁助理相应资产的授权额度，则交易无法提交。
        Map<String, Object> params = new HashMap<>();
        params.put("ROLE_POST_01", DictCodeList.角色岗位_领导.getValue());
        params.put("PINAN_ASSET_TYPE_F07", DictCodeList.平安资产类型_质押式回购.getValue());
        params.put("assetSecType", inputDto.getRepoType());
        params.put("tradeMarket", inputDto.getTradeMarket());
        params.put("tradeDirection", inputDto.getPs());
        params.put("loginUserName", LoginUserUtil.getUserName());
        BigDecimal vpOrPAuthAmount = this.trdRepoTrade002Service.getVPOrPAuthAmount(params);
        if (vpOrPAuthAmount != null && inputDto.getPrinAmt() != null
                && inputDto.getPrinAmt().compareTo(vpOrPAuthAmount) > 0) {
            ValidMessage validMessage = new ValidMessage("prinAmt",
                    getMessage("TRD_W_0008", getMessage("TrdRepoTrade002.prinAmt")));
            errorList.add(validMessage);
        }

        // 若正回购的委托信息中有到期日，需要检查所选任意一只质押券到期日是否都大于到期日；
        // 若不满足，给出提示：“所选质押券到期日不可小于等于回购到期日！”；若满足，则检查通过；（此处需求上为，不可小于回购到期日，经与金强确认，质押券到期日必须为回购到期日之后，回购交易的到期日当天也不行）
        if (DictCodeList.回购拆借方向_正回购.getValue().equals(inputDto.getPs())
                && inputDto.getBussRepoSec() != null) {
            if (!StringUtil.isEmptyList(inputDto.getBussRepoSec().getPledgeBondList())) {
                boolean isHaveExpiredBond = false;
                for (PledgeBondInfoDto pbi : inputDto.getBussRepoSec().getPledgeBondList()) {
                	Map<String, Object> paramSec = new HashMap<>();
                	paramSec.put("FINPROD_MARKET_ID", pbi.getBondCode());
                    paramSec.put("FINPROD_TYPE2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                	FinProduct finProduct = trdRepoTrade002Service.querySecinfo(paramSec);
                	if (finProduct != null) {
                		pbi.setMdate(finProduct.getMdate());
                	}
                    if (inputDto.getMdate() != null
                            && (pbi.getMdate() == null
                            || pbi.getMdate().compareTo(inputDto.getMdate()) <= 0)) {
                        isHaveExpiredBond = true;
                        break;
                    }
                }
                if (isHaveExpiredBond) {
                    ValidMessage validMessage = new ValidMessage("mdate",
                            getMessage("TRD_E_0009"));
                    errorList.add(validMessage);
                }
            }
        }

        if (!StringUtil.isEmptyList(inputDto.getBussRepoSec().getPledgeBondList())
                && DictCodeList.回购拆借方向_正回购.getValue().equals(inputDto.getPs())) {
            // 可质押券信息（含投资目的）
            Map<String, List<PledgeBondInfoDto>> avaMap = trdRepoTrade002Service.selectAvaPledgeWithInvAim(inputDto)
                    .stream().collect(JoyinCollectors.groupingBy(x -> x.getBondCode()));
            avaMap.entrySet().stream().forEach(entry -> {
                Collections.sort(entry.getValue(), (o1, o2) -> o1.getInvAimSort().compareTo(o2.getInvAimSort()));
            });
            PtlSecPositionFlowDto resultDto;
            List<String> 无数量可用的质押券 = new ArrayList<>();
            for (PledgeBondInfoDto pledgeBond : inputDto.getBussRepoSec().getPledgeBondList()) {
                // 待分配投资目的的质押券面总额
                BigDecimal pledgePrin = pledgeBond.getPledgePrin();
                if (avaMap.get(pledgeBond.getBondCode()) == null) {
                    无数量可用的质押券.add(pledgeBond.getBondCode() + "：无持仓。");
                }
                else {
                    BigDecimal total = BigDecimal.ZERO;
                    for (PledgeBondInfoDto avaBond : avaMap.get(pledgeBond.getBondCode())) {
                        // 当前投资目的可分配券面总额
                        total = ArithmeticUtil.doAdd(total, avaBond.getAvaPledgePrin());
                    }
                    if (pledgePrin.compareTo(total) > 0) {
                        无数量可用的质押券.add(pledgeBond.getBondCode() + "：持仓不足，当前持仓：" + total.toPlainString());
                    }
                }
            }
            if (!StringUtil.isEmptyList(无数量可用的质押券)) {
                errorList.add(new ValidMessage("chlId","添加的质押券已无数量可用："));
            }
        }
        //修改不显示 通道 by wsz 20220607 start
        Page page = CacheUtil.getPage("TrdRepoTrade002");
        if(!Objects.equals(page,null) && ((null != page.getElementcontrol("chlId") && page.getElementcontrol("chlId").getIsShow())
                || page.getElementcontrol("chlId") == null)){
            if (StringUtil.isEmpty(inputDto.getChlId()) && StringUtil.isEmpty(inputDto.getSecAcctId())) {
                errorList.add(new ValidMessage("chlId","通道代码和证券账户不能同时为空。"));
            }
        }
        //修改不显示 通道 by wsz 20220607 start
        // 1、  交易场所：银行间，必须上传成交单
//        if (DictCodeListExt1.交易场所_银行间.getValue().equals(inputDto.getTradeMarket())
////                && StringUtil.isEmptyList(inputDto.getUploadFileList())) {
////            ValidMessage validMessage = new ValidMessage(getMessage("FW_E_0006", getMessage("TrdRepoTrade002.fileCommon")));
////            errorList.add(validMessage);
////        }
        if (bussTrdTransferOrderService != null) {
            if (StringUtil.equals(inputDto.getIsGuarantee(), DictCodeList.是否_否.getValue())) {
                List<ValidMessage> list = bussTrdTransferOrderService.check(
                        inputDto.getBussTrdTransferOrder());
                errorList.addAll(list);
            }
        }
        // add by 裴家俊 2019-05-23--start----
        if(StringUtil.equals(inputDto.getOperateType(), "save") || StringUtil.equals(inputDto.getOperateType(), "submit")){
            if(inputDto.getTrdInvestPersonInfo() != null && StringUtil.isEmpty(inputDto.getTrdInvestPersonInfo().getCounterId())){
                ValidMessage validMessage = new ValidMessage("trdInvestPersonInfo.counterId",getMessage("FW_E_0006", getMessage("TrdDepositInvestTrade002.cno")));
                errorList.add(validMessage);
            }
            if(inputDto.getCalendarId() == null && StringUtil.isEmpty(inputDto.getCalendarId())){
                ValidMessage validMessage = new ValidMessage("TrdRepoTrade002.calendarId",getMessage("BUSS_COM_E_0038", getMessage("TrdRepoTrade002.calendarId")));
                errorList.add(validMessage);
            }
        }
        // add by 裴家俊 2019-05-23--end----
        // modify by 马数凯 2021-01-25 ZGBSS-360 start
        // add by 张年 2019-08-25--start----
        if ( StringUtil.equals(DictCodeList.回购拆借方向_正回购.getValue() , inputDto.getPs()) && (DictCodeList.回购类型_质押式协议回购.getValue().equals(inputDto.getRepoType()) || DictCodeList.回购类型_质押式回购.getValue().equals(inputDto.getRepoType()))
                && inputDto.getBussRepoSec() != null) {
            if (StringUtil.isEmptyList(inputDto.getBussRepoSec().getPledgeBondList())) {
                errorList.add(new ValidMessage("","请输入质押券信息！"));
            }
        }
        // add by 张年 2019-08-25--end----
        // modify by 马数凯 2021-01-25 ZGBSS-360 end
        
        // add by wangpeng 2019-08-28--start----
        
        if (!(StringUtil.equals(DictCodeList.回购类型_质押式协议回购.getValue(),inputDto.getRepoType())
                && StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(),inputDto.getTradeMarket()))) {
        	//非深交所  协议式 期限品质必填   TrdRepoTrade002.termTypeReback
        	if(StringUtil.isEmpty(inputDto.getTermTypeReback())){
        		ValidMessage validMessage = new ValidMessage("termTypeReback",getMessage("请输入期限品种。"));
                errorList.add(validMessage);
        	}
        }
        // add by wangpeng 2019-08-28--end----
        
	    // add by chenlei1 逆回购质押券面额必填 2020-03-17--end----
        if (!StringUtil.isEmptyList(inputDto.getBussRepoSec().getPledgeBondList())
                && DictCodeList.回购拆借方向_逆回购.getValue().equals(inputDto.getPs())) {
	        for (PledgeBondInfoDto pledgeBond : inputDto.getBussRepoSec().getPledgeBondList()) {
	            BigDecimal pledgePrin = pledgeBond.getPledgePrin();
	            if (pledgeBond.getBondCode()!=null && pledgePrin == null) {
	        		ValidMessage validMessage = new ValidMessage("",getMessage("质押券信息列表请输入质押券面额"));
	                errorList.add(validMessage);
	                break;
	            }	
	        }
        }
	    // add by chenlei1 2020-03-17--end----
        // MODIFY BY WJ 20200818
        if (  StringUtil.equals(DictCodeList.回购拆借方向_逆回购.getValue() , inputDto.getPs())
        && ( StringUtil.equals(inputDto.getTradeMarket(), DictCodeList.交易流通场所_银行间债券市场.getValue())
                || (  StringUtil.equals(inputDto.getTradeMarket(), DictCodeList.交易流通场所_上海证券交易所.getValue()) && StringUtil.equals(inputDto.getRepoType(), DictCodeList.回购类型_质押式协议回购.getValue()))
        ) ) {

            if(inputDto.getBussRepoSec() == null || inputDto.getBussRepoSec().getTotalPrinAmt()==null || StringUtil.isEmptyList(inputDto.getBussRepoSec().getPledgeBondList())){
                //如果是下行成交数据  逆回购质押券可为空
                if(!StringUtil.isNotEmpty(inputDto.getfTradeId())){
                    errorList.add(new ValidMessage("","请输入质押券信息！"));
                }
            }else {

                BigDecimal prinAmt = inputDto.getPrinAmt()==null ? BigDecimal.ZERO :inputDto.getPrinAmt() ;
                BigDecimal totalPrinAmt = inputDto.getBussRepoSec() == null ? BigDecimal.ZERO : inputDto.getBussRepoSec().getTotalPrinAmt();
                //汇总券面总额转换 edit by yeming 20210809 start
                if(StringUtil.equals(CacheUtil.getSysParam("entrustAmtUnit"),"02")){
                    totalPrinAmt = totalPrinAmt.multiply(new BigDecimal(10000));//万元转成元
                }
                //汇总券面总额转换 edit by yeming 20210809 end
                if (totalPrinAmt.compareTo(prinAmt) < 0) {
                    ValidMessage validMessage = new ValidMessage(getMessage("TRD_E_00122"));
                    errorList.add(validMessage);
                }
            }

        }
        // end
        //add by msk TISHKBS-1038 新增一次性划款通道交易前是否划款判断 start
        //修改不显示 通道 by wsz 20220607 start
        if(!Objects.equals(page,null) && ((null != page.getElementcontrol("chlId") && page.getElementcontrol("chlId").getIsShow())
                || page.getElementcontrol("chlId") == null)) {
            if (!"".equals(inputDto.getChlId()) && inputDto.getChlId() != null) {
                Map<String, Object> sMap = new HashMap<String, Object>();
                sMap.put("chlId", inputDto.getChlId());
                sMap.put("finprodId", inputDto.getPortfolioId());
                Integer count = trdRepoTrade002Service.selectchlTrade(sMap);
                if (count <= 0) {
                    ValidMessage validMessage = new ValidMessage(getMessage("通过一次性通道投资请先进行划款！", getMessage("invest.channelId")));
                    errorList.add(validMessage);
                }
            }
        }
        //修改不显示 通道 by wsz 20220607 end
        //add by msk TISHKBS-1038 新增一次性划款通道交易前是否划款判断 end
        if (null!=inputDto.getRemark() && !inputDto.getRemark().matches(whitePattern)){
            ValidMessage validMessage = new ValidMessage("",getMessage("备注必须填写白名单中的文字"));
            errorList.add(validMessage);
        }
        return errorList;
    }
}
