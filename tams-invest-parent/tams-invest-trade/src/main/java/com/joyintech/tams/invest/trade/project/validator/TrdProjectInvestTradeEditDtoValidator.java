package com.joyintech.tams.invest.trade.project.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.FinProduct;

import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.trade.project.service.TrdProjectInvestTrade002Service;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.trade.common.dto.EtrInvestInfoDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.common.validator.TradeCommonValidator;
import com.joyintech.tams.invest.trade.project.dto.TrdProjectInvestTrade002Dto;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Description: 撤销校验
 * @date Date : 2018年09月27日 13:57
 */
@Component
public class TrdProjectInvestTradeEditDtoValidator implements TradeCommonValidator {

    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    
    @Autowired
	private TradeCommonService tradeCommonService;
    @Autowired
    private  BussCommonMapperDelegateService bussCommonMapperDelegateService;
    @Autowired
    private TrdProjectInvestTrade002Service trdProjectInvestTrade002Service;
    @Autowired
    private FinProductMapper finProductMapper;

    @Override
    public List<ValidMessage> bussValid(Object value) {
        TrdProjectInvestTrade002Dto dto = (TrdProjectInvestTrade002Dto)value;
        List<ValidMessage> errorList = new ArrayList<>();
        errorList.addAll(bussTrdTransferOrderService.check(dto.getBussTrdTransferOrder()));
//        if (StringUtil.isNotEmpty(dto.getEntrustId())) {
//            if (dto.getPrinAmt() != null && dto.getPrinAmt().compareTo(dto.getEntrustPrinAmt()) > 0) {
//                errorList.add(new ValidMessage(getMessage("TRD_E_0002")));
//            }
//        }
     // 验证流程中是否存在该委托 
        if(StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()){
        	String str = tradeCommonService.check(dto.getEntrustId(), "TrdProjectInvestTrade002");
        	if(StringUtil.isNotEmpty(str)){ 
        		ValidMessage validMessage = new ValidMessage(str);
        		errorList.add(validMessage);
        	}
        	EtrInvestInfoDto etrInvestInfoDto = tradeCommonService.findEtrInfo(dto.getEntrustId());
        	BigDecimal finishAmt = BigDecimal.ZERO;
        	if(etrInvestInfoDto.getEtrFinishStatusList()!=null){
        		for(EtrFinishStatus fin:etrInvestInfoDto.getEtrFinishStatusList()){
        			finishAmt=ArithmeticUtil.doAdd(finishAmt,fin.getFinishAmt());
        		}
        	}
        	 if (dto.getPrinAmt() != null && dto.getPrinAmt().compareTo(ArithmeticUtil.subtract(etrInvestInfoDto.getEtrEntrust().getEntrustAmt(), finishAmt)) > 0) {
                 errorList.add(new ValidMessage(getMessage("交易本金不能大于剩余委托金额")));
             }
        }
        //起息日<=交易日&&交割日小于到期日
        if(bussCommonMapperDelegateService != null && StringUtil.isNotEmpty(dto.getFinprodId())){
            FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, dto.getFinprodId(), FinProduct.class);

//            if(finProduct != null && finProduct.getVdate() != null && (DateUtil.compareDate(DateUtil.parse(dto.getTradeDate()), finProduct.getVdate())<0)){
//            	errorList.add(new ValidMessage("tradeDate",getMessage("BUSS_COM_E_0004",getMessage("TrdPlanRateInvestTrade002.tradeDate"),getMessage("TrdPlanRateInvestTrade002.vdate"))));
//            }
            if(finProduct != null && finProduct.getMdate() != null && (DateUtil.compareDate(DateUtil.parse(dto.getSettleDate()), finProduct.getMdate())>0)){
            	errorList.add(new ValidMessage("settleDate",getMessage("BUSS_COM_E_0003",getMessage("TrdPlanRateInvestTrade002.settleDate"),getMessage("TrdPlanRateInvestTrade002.mdate"))));
            }
            // add BUG:TOUGUAN-1063,系统需要校验交易金额不可大于标的融资金额 start
            if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dto.getPs()) && StringUtil.isNotEmpty(dto.getPrinAmt())
            		&& dto.getPrinAmt().compareTo(finProduct.getIssueAmt()) > 0) {
            	ValidMessage validMessage = new ValidMessage("prinAmt",
                        getMessage("BUSS_COM_E_0003", getMessage("TrdLendInvestTrade002.prin"),
                            getMessage("FinPlanProfitAsset002.issueAmt")));
                    errorList.add(validMessage);
            }
            // // add BUG:TOUGUAN-1063,系统需要校验交易金额不可大于标的融资金额 end
        }

        //add bugTISHKBS-1201 项目类资产交易-资产名称字段为必填项，没有标志必填标识
        if (!StringUtil.isNotEmpty(dto.getFinprodName())) {
            errorList.add(new ValidMessage("finprodName",
                    getMessage("BUSS_COM_E_0038", "资产名称")));
        }

        if (dto.getTrdInvestPersonInfo() == null || StringUtil.isEmpty(dto.getTrdInvestPersonInfo().getCounterId())) {
            errorList.add(new ValidMessage("trdInvestPersonInfo.counterId",getMessage("BUSS_COM_E_0038", getMessage("EtrInvestPersonInfo002.counterId"))));
        }
        // ZGZGST-1232 fuyongsheng 20210820:增加SPPI测试 系统参数开关,Y:需要测试
        if(DictCodeListExt1.是否_是.getValue().equals(CacheUtil.getSysParam("IS_NEED_SPPI_CHECK"))){
            //投资目的
            if(StringUtil.isNotEmpty(dto.getInvAim())) {
                if(StringUtil.equals(dto.getInvAim(), DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue())
                        || StringUtil.equals(dto.getInvAim(), DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue())) {
                    FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
                    if(StringUtil.isNotEmpty(finProduct)) {
                        if(StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_测试不通过.getValue())) {
                            errorList.add(new ValidMessage("invAim",
                                    getMessage("该资产SPPI测试不通过，只可以选择以出售该金融资产为目的！")));
                        }
                        else if(StringUtil.isEmpty(finProduct.getSppiTestStatus())
                            || StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_未测试.getValue())
                                || StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_测试中.getValue())
                                //|| StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_已退回.getValue())
                        ) {
                            errorList.add(new ValidMessage("invAim",
                                    getMessage("选择此投资目的时，对应的资产必须先进行SPPI测试！")));
                        }
                    }
                }
            }
        }


        if ((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())||StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) &&
                StringUtil.equals(dto.getTrdInvestPersonInfo().getIsOutTrade(), DictCodeList.是否_否.getValue())){
            //买入交易为自动生成   不可以页面提交，此处不校验，共通check会校验
            if (DictCodeList.是否_是.getValue().equals(CacheUtil.getSysParam(BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL))&&StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {

            }else{
                if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()) &&
                        StringUtil.isEmpty(dto.getTurnInvAim()) &&
                        DictCodeList.是否_是.getValue().equals(CacheUtil.getSysParam(BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL))){
                    ValidMessage validMessage = new ValidMessage("turnInvAim", "转仓投资目的必填！");
                    errorList.add(validMessage);
                }
            }
        }

        // 项目类资产交易页面，本金金额大于可用持仓时，系统没有提示
        if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), dto.getPs())) {
            if (dto.getPrinAmt() != null && dto.getBussTrdAvailableCashQuery() != null
                && dto.getBussTrdAvailableCashQuery().getAssetPosition() != null
                && dto.getPrinAmt().compareTo(
                    dto.getBussTrdAvailableCashQuery().getAssetPosition()) > 0) {
                ValidMessage validMessage = new ValidMessage(
                    getMessage("BUSS_COM_E_0003", getMessage("TrdLendInvestTrade002.prin"),
                        getMessage("TrdBondInvestTrade002.avaliblePosition")));
                errorList.add(validMessage);
            }
            
            if (CollectionUtils.isNotEmpty(dto.getBussTrdTransfenpeiDto())) {// 多组合模式
            	for (BussTrdTransfenpeiDto fenpeiDto : dto.getBussTrdTransfenpeiDto()) {
            		if (fenpeiDto.getTradeAmt() != null && fenpeiDto.getAssetsavailablepositions() != null
            				&& fenpeiDto.getPrinAmt().compareTo(fenpeiDto.getAssetsavailablepositions()) > 0) {
            			// 卖出校验本金金额不能大于资产可以持仓
            			ValidMessage validMessage = new ValidMessage(
            					getMessage("BUSS_COM_E_0003", getMessage("TrdLendInvestTrade002.prin"),
            							getMessage("TrdBondInvestTrade002.avaliblePosition")));
            			errorList.add(validMessage);
            			break;
            		}
            	}
            }
            if(StringUtil.isEmptyList(dto.getBussTrdTransfenpeiDto())){// 多组合模式下不校验未支付利息是否转让 add by wangcheng 20200121
                //还本不带息的资产才做此校验
                Map<String,Object> param = new HashMap<String,Object>();
                param.put("finprodId",dto.getFinprodId());
                param.put("settleDate",dto.getSettleDate());
                Map<String, Object> result=trdProjectInvestTrade002Service.findRepayFlag(param);
                if(result.get("repayIntFlag")!=null &&result.get("repayIntFlag").equals("N")) {
                    if (dto.getIspayUnpaidint() == null || StringUtil.isEmpty(dto.getIspayUnpaidint())) {
                        errorList.add(new ValidMessage("ispayUnpaidint", getMessage("BUSS_COM_E_0038", getMessage(
                                "TrdPlanRateInvestTrade002.ispayUnpaidint"))));
                    }
                }
            }
        }

        if (StringUtil.isNotEmpty(dto.getBussTrdTransfenpeiDto())){
            Map<String, List<BussTrdTransfenpeiDto>> map = dto.getBussTrdTransfenpeiDto().stream().collect(JoyinCollectors.groupingBy(item->item.getPortfolioId()));
            if (map!=null && map.keySet().size()<dto.getBussTrdTransfenpeiDto().size()){
                errorList.add(new ValidMessage("组合分配不能重复分配给同一组合。"));
            }
        }
        // ZGZGST-1232 fuyongsheng 20210820:增加SPPI测试 系统参数开关,Y:需要测试
        if(DictCodeListExt1.是否_是.getValue().equals(CacheUtil.getSysParam("IS_NEED_SPPI_CHECK"))){
            //投资目的
            if(StringUtil.isNotEmpty(dto.getInvAim())) {
                if(StringUtil.equals(dto.getInvAim(), DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue())
                        || StringUtil.equals(dto.getInvAim(), DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue())) {
                    FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
                    if(StringUtil.isNotEmpty(finProduct)) {
                        if(StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_测试不通过.getValue())) {
                            errorList.add(new ValidMessage("invAim",
                                    getMessage("ETR_ERROR_0059")));
                        }
                        else if(StringUtil.isEmpty(finProduct.getSppiTestStatus())
                                || StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_未测试.getValue())
                                || StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_测试中.getValue())
                            //|| StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_已退回.getValue())
                        ) {
                            errorList.add(new ValidMessage("invAim",
                                    getMessage("选择此投资目的时，对应的资产必须先进行SPPI测试！")));
                        }
                    }
                }
            }
        }
        
        
        return errorList;
    }
}
