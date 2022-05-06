/*
 * 文件名：TrdPlanRateInvestTradeDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.trade.trdplanrateinvesttrade.validate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.trade.common.dto.EtrInvestInfoDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.common.validator.TradeCommonValidator;
import com.joyintech.tams.invest.trade.trdplanrateinvesttrade.dto.TrdPlanRateInvestTradeDto;
import com.joyintech.tams.invest.trade.trdplanrateinvesttrade.service.TrdPlanRateInvestTrade002Service;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 计划类（利率型）投资交易
 * @author fanshuanghu
 * @version 1.0 2018年11月22日
 * @see TrdPlanRateInvestTradeDtoValidator
 * @since 1.0
 */
@Transactional
@Component
public class TrdPlanRateInvestTradeDtoValidator implements TradeCommonValidator {
    /**
     * 基础Mapper代理Service
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    @Autowired
    private TrdPlanRateInvestTrade002Service trdPlanRateInvestTrade002Service;
    
    @Autowired
	private TradeCommonService tradeCommonService;
    
    @Autowired
    private FinProductMapper finProductMapper;

    @Value("${flag:false}")
    private boolean flag;

    @Override
    public List<ValidMessage> bussValid(Object value){
        ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
        if(flag) {
        	return errorList;
        }
        TrdPlanRateInvestTradeDto dto = (TrdPlanRateInvestTradeDto)value;
//        if(dto.getOperateType().equals("submit")){
//            if(StringUtil.isEmptyList(dto.getFileArray())){
//                errorList.add(new ValidMessage("fileArray",getMessage("FW_E_0006",getMessage("TrdPlanRateInvestTrade002.fileCommon"))));
//            }
//        }              
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
            // 交易对手必填
            if (StringUtil.isEmpty(dto.getTrdInvestPersonInfo002Dto().getCounterId())) {
                errorList.add(new ValidMessage("counterId",getMessage("BUSS_COM_E_0038",getMessage("EtrInvestPersonInfo002.counterId"))));
            }
        	/*if (dto.getPrinAmt() != null) {
                FinProduct finProduct = trdPlanRateInvestTrade002Service.getFinProductInfo(dto.getFinprodId());
                if (finProduct != null && finProduct.getIssueAmt() != null
                		&& dto.getPrinAmt().compareTo(finProduct.getIssueAmt()) > 0) {
                	errorList.add(new ValidMessage("委托金额超过了计划的融资金额！"));
                }
        	}*/
            // 验证流程中是否存在该委托 
            if(StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()){
            	String str = tradeCommonService.check(dto.getEntrustId(), "TrdPlanRateInvestTrade002");
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
            	BigDecimal balance = BigDecimal.ZERO;
            	if(StringUtil.isNotEmpty(etrInvestInfoDto.getEtrEntrust().getEntrustNum())){
            		balance = ArithmeticUtil.subtract(etrInvestInfoDto.getEtrEntrust().getEntrustNum(), finishAmt);
            	}else{
            		balance = ArithmeticUtil.subtract(etrInvestInfoDto.getEtrEntrust().getEntrustAmt(), finishAmt);
            	}
	        	 if (dto.getPrinAmt() != null && etrInvestInfoDto.getEtrEntrust()!=null&& dto.getPrinAmt().compareTo(balance) > 0) {
	                 errorList.add(new ValidMessage(getMessage("交易本金不能大于剩余委托金额")));
	             }
            }
            if(dto.getBussTrdTransferOrder() == null || dto.getBussTrdTransferOrder().size()==0){
                errorList.add(new ValidMessage("bussTrdTransferOrder",getMessage("FW_E_0006",getMessage("TrdPlanRateInvestTrade002.stlPayInfo"))));
            }
            if(dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size()>0){
                if((dto.getBussTrdTransferOrder().get(0).getPayAcctNo() == null || dto.getBussTrdTransferOrder().get(0).getPayAcctNo().isEmpty()) && dto.getBussTrdTransferOrder().get(0).getOrderId() != null
                && StringUtil.equals( DictCodeList.是否_是.getValue(),dto.getBussTrdTransferOrder().get(0).getIsNeedSend()) ){
                    errorList.add(new ValidMessage("bussTrdTransferOrder",getMessage("FW_E_0006",getMessage("TrdPlanRateInvestTrade002.payAcctNo","或",getMessage("TrdPlanRateInvestTrade002.recAcctNo")))));
                }
            }
//            //买入的时候可用现金必须大于交易本金
//            if(DictCodeList.交易方向_申购.getValue().equals(dto.getPs())){
//                if(dto.getBussTrdAvailableCashQuery().getAvalibleCash() != null && dto.getPrinAmt() != null 
//                    && dto.getBussTrdAvailableCashQuery().getAvalibleCash().compareTo(dto.getPrinAmt()) < 0){
//                    errorList.add(new ValidMessage("prinAmt",getMessage("BUSS_COM_E_0036",getMessage("TrdPlanRateInvestTrade002.accAvailableCashT"),getMessage("TrdPlanRateInvestTrade002.prinAmt"))));
//                }
//            }
            //卖出时持仓必须大于交易金额
         // 一体化对应etliu start
//            if(DictCodeList.交易方向_赎回.getValue().equals(dto.getPs())){
          /*  if(DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())){
             // 一体化对应etliu end
                if(dto.getBussTrdAvailableCashQuery().getAssetPosition() != null && dto.getPrinAmt() != null 
                    && dto.getBussTrdAvailableCashQuery().getAssetPosition().compareTo(dto.getPrinAmt()) < 0){
                    errorList.add(new ValidMessage("prinAmt",getMessage("BUSS_COM_E_0036",getMessage("TrdPlanRateInvestTrade002.bottomAssetAmount"),getMessage("TrdPlanRateInvestTrade002.prinAmt"))));
                }
            }*/
            //起息日<=交易日&&交割日小于到期日
            //到期日改为资产到期日  yzg 20190903 start
            if(bussCommonMapperDelegateService != null && dto.getFinprodId() != null){
                FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, dto.getFinprodId(), FinProduct.class);
                //YUCHUBS-604 新建交易选择的资产起息日大于交易日，给出的提醒不准确 guyunfei 20190906 start
                if(finProduct != null && finProduct.getVdate() != null && DateUtil.compareDate(finProduct.getVdate(), dto.getTradeDate())>0){
                    errorList.add(new ValidMessage("tradeDate",getMessage("BUSS_COM_E_0003",getMessage("TrdPlanRateInvestTrade002.vdate2"),getMessage("TrdPlanRateInvestTrade001.tradeDate"))));
                }
                //YUCHUBS-604 新建交易选择的资产起息日大于交易日，给出的提醒不准确 guyunfei 20190906 end
                if(finProduct != null && finProduct.getMdate() != null && DateUtil.compareDate(finProduct.getMdate(), dto.getSettleDate())<0){
                    errorList.add(new ValidMessage("settleDate",getMessage("BUSS_COM_E_0003",getMessage("TrdPlanRateInvestTrade002.settleDate"),getMessage("TrdPlanRateInvestTrade002.mdate.2"))));
                }
                if(finProduct != null && finProduct.getMdate() != null && (DateUtil.compareDate(dto.getTradeDate(), finProduct.getMdate())>0)){
                	errorList.add(new ValidMessage("tradeDate",getMessage("BUSS_COM_E_0003",getMessage("TrdPlanRateInvestTrade002.tradeDate"),getMessage("TrdPlanRateInvestTrade002.mdate.2"))));
                }
                if(finProduct != null && finProduct.getMdate() != null && (DateUtil.compareDate(dto.getSettleDate(), finProduct.getMdate())>0)){
                	errorList.add(new ValidMessage("settleDate",getMessage("BUSS_COM_E_0003",getMessage("TrdPlanRateInvestTrade002.settleDate"),getMessage("TrdPlanRateInvestTrade002.mdate.2"))));
                }
            }
          //到期日改为资产到期日  yzg 20190903 end
            if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), dto.getPs())) {
                //还本不带息的资产才做此校验
                Map<String,Object> param = new HashMap<String,Object>();
                param.put("finprodId",dto.getFinprodId());
                param.put("settleDate",dto.getSettleDate());
                Map<String, Object> result=trdPlanRateInvestTrade002Service.getFinprodType(param);
                if(result.get("repayIntFlag")!=null &&result.get("repayIntFlag").equals("N")) {
                    if (dto.getIspayUnpaidint() == null || StringUtil.isEmpty(dto.getIspayUnpaidint())) {
                        errorList.add(new ValidMessage("ispayUnpaidint", getMessage("BUSS_COM_E_0038", getMessage(
                                "TrdPlanRateInvestTrade002.ispayUnpaidint"))));
                    }
                }
            }

            if (StringUtil.isNotEmpty(dto.getBussTrdTransfenpeiDto())){
                Map<String, List<BussTrdTransfenpeiDto>> map = dto.getBussTrdTransfenpeiDto().stream().collect(JoyinCollectors.groupingBy(item->item.getPortfolioId()));
                if (map!=null && map.keySet().size()<dto.getBussTrdTransfenpeiDto().size()){
                    errorList.add(new ValidMessage("组合分配不能重复分配给同一组合。"));
                }
            }
            // ZGZGST-1232 fuyongsheng 20210820:增加SPPI测试 系统参数开关
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
        }
        return errorList;
    }

}
