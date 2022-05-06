/*
 * 文件名：TrdPlanNetInvestTradeValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年12月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.plannet.validator;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.common.validator.TradeCommonValidator;
import com.joyintech.tams.invest.trade.plannet.dto.TrdPlanNetInvestTradeDto;
import com.joyintech.tams.invest.trade.plannet.service.TrdPlanNetInvestTrade001Service;
import com.joyintech.tams.invest.trade.plannet.service.TrdPlanNetInvestTrade002Service;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author xiexinting
 * @version 1.0 2018年12月2日
 * @see TrdPlanNetInvestTradeValidator
 * @since 1.0
 */
@Component
public class TrdPlanNetInvestTradeValidator implements TradeCommonValidator {

    @Autowired
    private TrdPlanNetInvestTrade001Service trdPlanNetInvestTrade001Service;
    
    @Autowired
    private TrdPlanNetInvestTrade002Service trdPlanNetInvestTrade002Service;

    /**
     * BussTrdTransferOrderService
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    
	@Autowired
	private TradeCommonService tradeCommonService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
        TrdPlanNetInvestTradeDto dto = (TrdPlanNetInvestTradeDto)value;
        //交易对手必填
        if (!StringUtil.equals(dto.getOperateType(), "revoke") && !StringUtil.equals(dto.getOperateType(), "confirm")
                && (dto.getTrdInvestPersonInfo() == null || StringUtil.isEmpty(dto.getTrdInvestPersonInfo().getCounterId()))) {
            errorList.add(new ValidMessage("trdInvestPersonInfo.counterId", this.getMessage("BUSS_COM_E_0038", this.getMessage("交易对手"))));
            return errorList;
        }
        if (StringUtil.equals(dto.getOperateType(), "save")) {
            if (StringUtil.isNotEmpty(dto.getPs())) {
                if((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                        ||StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue()))){
                    if(null==dto.getPurCfmDate()){
                        errorList.add(new ValidMessage("purCfmDate",
                                getMessage("COMMON_E_0057", getMessage("TrdFundInvestTrade002.purCfmDate"))));
                    }
                }
                BigDecimal shareAmt = dto.getShareAmt();
                BigDecimal tradeAmt = dto.getTradeAmt();
                String entrustId = dto.getEntrustId();
                String tradeId =
                        dto.getTradeId();
                if (StringUtil.isNotEmpty(entrustId) && tradeAmt != null) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("entrustId", entrustId);
                    param.put("tradeId", tradeId);
                    Map<String, Object> resultMap = trdPlanNetInvestTrade002Service.selectEntrustAmt(param);
                    if (resultMap != null) {
                        BigDecimal entrustAmt = new BigDecimal((String)resultMap.get("ENTRUST_AMT"));
                        BigDecimal finishAmt = new BigDecimal((String)resultMap.get("FINISH_AMT"));
                        BigDecimal prinAmt = new BigDecimal((String)resultMap.get("PRIN_AMT"));
                        if (finishAmt != null) {
                            entrustAmt = entrustAmt.subtract(finishAmt);
                        }
                        if (prinAmt != null) {
                            entrustAmt = entrustAmt.subtract(prinAmt);
                        }
                        if (tradeAmt.compareTo(entrustAmt) == 1) {
                            errorList.add(new ValidMessage("tradeAmt", this.getMessage("TRD_X_0004") + entrustAmt));
                        }
                    }
                }
             // 验证流程中是否存在该委托 
                if(StringUtil.isNotEmpty(entrustId) && WorkFlowUtil.isNotFromToDoList()){
                	String str = tradeCommonService.check(entrustId, "TrdPlanNetInvestTrade002");
                	if(StringUtil.isNotEmpty(str)){ 
                		ValidMessage validMessage = new ValidMessage(str);
                		errorList.add(validMessage);
                	}
                }
                BigDecimal zero = new BigDecimal(0);
                
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
                    if (tradeAmt == null) {
                        errorList.add(new ValidMessage("tradeAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeAmt"))));
                    }
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    if (shareAmt == null && tradeAmt == null) {
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdPlanNetInvestTrade003.tradeAmtSha"))));
                    }
                }
                // TISHKS-776 交易份额提交申请时未作必填校验，为空时提交，进入审批报系统异常 modify by huwei1 20220505 start
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
                    if (null == shareAmt) {
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdPlanNetInvestTrade003.tradeSha"))));
                    }
                }
                // TISHKS-776 交易份额提交申请时未作必填校验，为空时提交，进入审批报系统异常 modify by huwei1 20220505 end
                //dto内已经有过校验，无需重复校验
//                if (tradeAmt != null && tradeAmt.compareTo(zero) == -1) {
//                    errorList.add(new ValidMessage("tradeAmt", this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeAmt"),"0")));
//                }
//                if (shareAmt != null && shareAmt.compareTo(zero) == -1) {
//                    errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeSha"),"0")));
//                }

                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    BigDecimal assetAvailablePosition = dto.getAssetAvailablePosition();
                    if (assetAvailablePosition != null && shareAmt != null
                        && shareAmt.compareTo(assetAvailablePosition) == 1) {
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0003", this.getMessage("TrdFundInvestTrade002.tradeSha"),this.getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                    }
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                    && StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                    errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0038",
                        getMessage("TrdFundInvestTrade002.transOrder"))));
                }
            }
            //     附件上传改为非必填 by zhuyongkang 20191010 start
//            if (dto.getFileArray() == null || dto.getFileArray().size() <= 0) {
//                errorList.add(new ValidMessage(
//                    getMessage("BUSS_COM_E_0038", getMessage("TrdFundInvestTrade002.file"))));
//            }
            
//            if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())){ 内部交易只能卖出
//                PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
//                ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
//                ptlSecPositionFlowDto.setPortfolioId(dto.getTrdInvestPersonInfo().getCounterId());
//                ptlSecPositionFlowDto.setSecAcctId(dto.getChlId());
//                ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
//                BigDecimal assetPosition = trdPlanNetInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
//                //资产可用持仓小于交易份额
//                if (StringUtil.isNotEmpty(dto.getShareAmt())) {
//                    if (assetPosition.compareTo(dto.getShareAmt()) == -1) {
//                        errorList.add(new ValidMessage("",
//                                getMessage("BUSS_COM_E_0003",
//                                        ptlSecPositionFlowDto.getPortfolioId() + "  " + getMessage("TrdPlanNetInvestTrade001.shareAmt"),
//                                        getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
//                    }
//                }
//
//                //同时验证买入方是否有足够的现金
//                Map<String, Object> param = new HashMap<>();
//                param.put("portfolioId", dto.getPortfolioId());
//                param.put("ccy", dto.getCcy());
//                param.put("vdate", DateUtil.format(dto.getTradeDate()));
//                param.put("calId", dto.getCalendarId());
//                param.put("chlId", dto.getChlId());
//                Map<String, Object> para = trdPlanNetInvestTrade002Service.getProAvailableCash(param);
//                BigDecimal accCash = new BigDecimal((String)para.get("cashT2"));
//                if(accCash.compareTo(dto.getTradeAmt())==-1){
//                    errorList.add(new ValidMessage("",
//                        getMessage("BUSS_COM_E_0003",
//                        param.get("portfolioId")+"  "+getMessage("TrdPlanNetInvestTrade002.tradeAmt"),
//                        getMessage("TrdFundInvestTrade002.accAvailableCash"))));
//                }
//            }else
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())){
                if (dto.getShareAmt() == null) {
                    errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdPlanNetInvestTrade003.tradeSha"))));
                }
                Map<String, Object> param = new HashMap<>();
                param.put("portfolioId", dto.getTrdInvestPersonInfo().getCounterId());
                param.put("ccy", dto.getCcy());
                param.put("vdate", DateUtil.format(dto.getTradeDate()));
                param.put("calId", dto.getCalendarId());
                param.put("chlId", dto.getChlId());
                Map<String, Object> para = trdPlanNetInvestTrade002Service.getProAvailableCash(param);
                BigDecimal accCash = new BigDecimal((String)para.get("cashT2"));
                //交易对手没有足够的现金 
                if(accCash.compareTo(dto.getTradeAmt())==-1){
                    errorList.add(new ValidMessage("",
                        getMessage("BUSS_COM_E_0003", 
                        param.get("portfolioId")+"  "+getMessage("TrdPlanNetInvestTrade002.tradeAmt"),
                        getMessage("TrdFundInvestTrade002.accAvailableCash"))));
                }
                
                //同时验证卖出方是否有足够的持仓
                PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
                ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
                ptlSecPositionFlowDto.setPortfolioId(dto.getPortfolioId());
                ptlSecPositionFlowDto.setSecAcctId(dto.getChlId());
                ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
                BigDecimal assetPosition = trdPlanNetInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
                if (StringUtil.isNotEmpty(dto.getShareAmt())) {
                    if (assetPosition.compareTo(dto.getShareAmt()) == -1) {
                        errorList.add(new ValidMessage("",
                                getMessage("BUSS_COM_E_0003",
                                        ptlSecPositionFlowDto.getPortfolioId() + "  " + getMessage("TrdPlanNetInvestTrade001.shareAmt"),
                                        getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                    }
                }
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "submit")) {
            if (StringUtil.isNotEmpty(dto.getPs())) {
                BigDecimal shareAmt = dto.getShareAmt();
                BigDecimal tradeAmt = dto.getTradeAmt();
                String entrustId = dto.getEntrustId();
                String tradeId = dto.getTradeId();
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    if (!StringUtil.isNotEmpty(dto.getSettleSpreed())) {
                        errorList.add(new ValidMessage("settleSpreed", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.settleSpreed"))));
                    }
                }
                if (StringUtil.isNotEmpty(entrustId) && (tradeAmt != null || shareAmt != null) && !StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("entrustId", entrustId);
                    param.put("tradeId", tradeId);
                    Map<String, Object> resultMap = trdPlanNetInvestTrade002Service.selectEntrustAmt(param);
                    if (resultMap != null) {
                        BigDecimal entrustAmt = new BigDecimal((String) resultMap.get("ENTRUST_AMT"));
                        BigDecimal entrustNum = new BigDecimal((String) resultMap.get("ENTRUST_NUM"));
                        BigDecimal finishAmt = new BigDecimal((String) resultMap.get("FINISH_AMT"));
                        BigDecimal prinAmt = new BigDecimal((String) resultMap.get("PRIN_AMT"));
                        if(entrustAmt != null && entrustAmt.compareTo(BigDecimal.ZERO)>0){
                            entrustAmt = entrustAmt.subtract(finishAmt);//委托金额减去完成金额
                            entrustAmt = entrustAmt.subtract(prinAmt);
                            if (tradeAmt != null && tradeAmt.compareTo(entrustAmt) == 1) {
                                errorList.add(new ValidMessage("tradeAmt", this.getMessage("TRD_X_0004") + entrustAmt));
                            }
                        }
                        if(entrustNum != null && entrustNum.compareTo(BigDecimal.ZERO)>0){
                            entrustNum = entrustNum.subtract(finishAmt);//委托份额减去完成份额
                            entrustNum = entrustNum.subtract(prinAmt);
                            if(shareAmt != null && shareAmt.compareTo(entrustNum) == 1){
                                errorList.add(new ValidMessage("shareAmt", this.getMessage("TRD_X_0009") + entrustNum));
                            }
                        }
                    }
                }
             // 验证流程中是否存在该委托 
                if(StringUtil.isNotEmpty(entrustId) && WorkFlowUtil.isNotFromToDoList()){
                	String str = tradeCommonService.check(entrustId, "TrdPlanNetInvestTrade002");
                	if(StringUtil.isNotEmpty(str)){ 
                		ValidMessage validMessage = new ValidMessage(str);
                		errorList.add(validMessage);
                	}
                }
                BigDecimal zero = new BigDecimal(0);
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
                    if (tradeAmt == null) {
                        errorList.add(new ValidMessage("tradeAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeAmt"))));
                    }
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    if (shareAmt == null && tradeAmt == null) {
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdPlanNetInvestTrade003.tradeAmtSha"))));
                    }
                }
                // TISHKS-776 交易份额提交申请时未作必填校验，为空时提交，进入审批报系统异常 modify by huwei1 20220505 start
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
                    if (null == shareAmt) {
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdPlanNetInvestTrade003.tradeSha"))));
                    }
                }
                // TISHKS-776 交易份额提交申请时未作必填校验，为空时提交，进入审批报系统异常 modify by huwei1 20220505 end

                //解决点击提交提示语有重复的问题
                //dto内已经有了校验，无需重复进行校验
//                if (tradeAmt != null && tradeAmt.compareTo(zero) == -1) {
//                    errorList.add(new ValidMessage("tradeAmt", this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeAmt"),"0")));
//                }
//                if (shareAmt != null && shareAmt.compareTo(zero) == -1) {
//                    errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeSha"),"0")));
//                }

                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    BigDecimal assetAvailablePosition = dto.getAssetAvailablePosition();
                    if (assetAvailablePosition != null && shareAmt != null
                            && shareAmt.compareTo(assetAvailablePosition) == 1) {
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0003", this.getMessage("TrdFundInvestTrade002.tradeSha"), this.getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                    }
//                    if (assetAvailablePosition != null && tradeAmt != null && tradeAmt.compareTo(assetAvailablePosition) == 1) {
//                        errorList.add(new ValidMessage("tradeAmt", this.getMessage("BUSS_COM_E_0003", this.getMessage("TrdFundInvestTrade002.tradeAmt"), this.getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
//                    } // 不用金额与持仓做check
                }
                if ((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))
                        && StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                    // 划款指令
                    if (bussTrdTransferOrderService != null) {
                        List<ValidMessage> list = bussTrdTransferOrderService.check(
                                dto.getBussTrdTransferOrder());
                        errorList.addAll(list);
                    }
                }
                if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()) && trdPlanNetInvestTrade002Service.checkIssueAmt(dto)){
                    errorList.add(new ValidMessage("tradeAmt", this.getMessage("该净值型计划的持有金额与交易金额之和不能超过该资产标的的发行金额")));
                }
            }
            //     附件上传改为非必填 by zhuyongkang 20191010 start
//            if (dto.getFileArray() == null || dto.getFileArray().size() <= 0) {
//                errorList.add(new ValidMessage(
//                        getMessage("BUSS_COM_E_0038", getMessage("TrdFundInvestTrade002.file"))));
//            }

//            if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())){ 内部交易只能卖出
//                PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
//                ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
//                ptlSecPositionFlowDto.setPortfolioId(dto.getTrdInvestPersonInfo().getCounterId());
//                ptlSecPositionFlowDto.setSecAcctId(dto.getChlId());
//                ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
//                BigDecimal assetPosition = trdPlanNetInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
//                //资产可用持仓小于交易份额
//                if (StringUtil.isNotEmpty(dto.getShareAmt())){
//                    if(assetPosition.compareTo(dto.getShareAmt())== -1){
//                        errorList.add(new ValidMessage("",
//                                getMessage("BUSS_COM_E_0003",
//                                        ptlSecPositionFlowDto.getPortfolioId()+"  "+getMessage("TrdPlanNetInvestTrade001.shareAmt"),
//                                        getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
//                    }
//                }
//
//
//                //同时验证买入方是否有足够的现金
//                Map<String, Object> param = new HashMap<>();
//                param.put("portfolioId", dto.getPortfolioId());
//                param.put("ccy", dto.getCcy());
//                param.put("vdate", DateUtil.format(dto.getTradeDate()));
//                param.put("calId", dto.getCalendarId());
//                param.put("chlId", dto.getChlId());
//                Map<String, Object> para = trdPlanNetInvestTrade002Service.getProAvailableCash(param);
//                BigDecimal accCash = new BigDecimal((String)para.get("cashT2"));
//                if(accCash.compareTo(dto.getTradeAmt())==-1){
//                    errorList.add(new ValidMessage("",
//                        getMessage("BUSS_COM_E_0003",
//                        param.get("portfolioId")+"  "+getMessage("TrdPlanNetInvestTrade002.tradeAmt"),
//                        getMessage("TrdFundInvestTrade002.accAvailableCash"))));
//                }
//            }else
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                if (dto.getShareAmt() == null) {
                    errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdPlanNetInvestTrade003.tradeSha"))));
                }
                Map<String, Object> param = new HashMap<>();
                param.put("portfolioId", dto.getTrdInvestPersonInfo().getCounterId());
                param.put("ccy", dto.getCcy());
                param.put("vdate", DateUtil.format(dto.getTradeDate()));
                param.put("calId", dto.getCalendarId());
                param.put("chlId", dto.getChlId());
                Map<String, Object> para = trdPlanNetInvestTrade002Service.getProAvailableCash(param);
                BigDecimal accCash = new BigDecimal((String) para.get("cashT2"));
                //交易对手没有足够的现金 
                if (accCash.compareTo(dto.getTradeAmt()) == -1) {
                    errorList.add(new ValidMessage("",
                            getMessage("BUSS_COM_E_0003",
                                    param.get("portfolioId") + "  " + getMessage("TrdPlanNetInvestTrade002.tradeAmt"),
                                    getMessage("TrdFundInvestTrade002.accAvailableCash"))));
                }
                if(StringUtil.isEmptyList(dto.getBussTrdTransfenpeiDto())) {
                    //同时验证卖出方是否有足够的持仓
                    PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
                    ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
                    ptlSecPositionFlowDto.setPortfolioId(dto.getPortfolioId());
                    ptlSecPositionFlowDto.setSecAcctId(dto.getChlId());
                    ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
                    BigDecimal assetPosition = trdPlanNetInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
                    if (StringUtil.isNotEmpty(dto.getShareAmt())) {
                        if (assetPosition.compareTo(dto.getShareAmt()) == -1) {
                            errorList.add(new ValidMessage("",
                                    getMessage("BUSS_COM_E_0003",
                                            ptlSecPositionFlowDto.getPortfolioId() + "  " + getMessage("TrdPlanNetInvestTrade001.shareAmt"),
                                            getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                        }
                    }
                }
                //多组合卖出校验持仓
                else{
                    for(BussTrdTransfenpeiDto fenpei:dto.getBussTrdTransfenpeiDto()){
                        //同时验证卖出方是否有足够的持仓
                        PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
                        ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
                        ptlSecPositionFlowDto.setPortfolioId(fenpei.getPortfolioId());
//                        ptlSecPositionFlowDto.setSecAcctId(fenpei.get());
                        ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
                        BigDecimal assetPosition = trdPlanNetInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
                        if (StringUtil.isNotEmpty(fenpei.getShareAmt())) {
                            if (assetPosition.compareTo(fenpei.getShareAmt()) == -1) {
                                errorList.add(new ValidMessage("",
                                        getMessage("BUSS_COM_E_0003",
                                                ptlSecPositionFlowDto.getPortfolioId() + "  " + getMessage("TrdPlanNetInvestTrade001.shareAmt"),
                                                getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                            }
                        }
                    }
                }
//                if (dto.getShareAmt() != null) {
//                    //同时验证卖出方是否有足够的持仓
//                    PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
//                    ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
//                    ptlSecPositionFlowDto.setPortfolioId(dto.getPortfolioId());
//                    ptlSecPositionFlowDto.setSecAcctId(dto.getChlId());
//                    ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
//                    BigDecimal assetPosition = trdPlanNetInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
//                    if (StringUtil.isNotEmpty(dto.getShareAmt())) {
//                        if (assetPosition.compareTo(dto.getShareAmt()) == -1) {
//                            errorList.add(new ValidMessage("",
//                                    getMessage("BUSS_COM_E_0003",
//                                            ptlSecPositionFlowDto.getPortfolioId() + "  " + getMessage("TrdPlanNetInvestTrade001.shareAmt"),
//                                            getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
//                        }
//                    } else {
//                        errorList.add(new ValidMessage("shareAmt",
//                                getMessage("FW_E_0006", "交易份额")));
//                    }
//                }
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "revoke")) {
            TrdPlanNetInvestTradeDto dtocopy = dto.getSelectedItem().get(0);
            if (StringUtil.equals(dtocopy.getValidStatus(),
                DictCodeListExt1.交易流程状态_撤销.getValue())
                || StringUtil.equals(dtocopy.getValidStatus(),
                    DictCodeListExt1.交易流程状态_流程中.getValue())) {
                errorList.add(new ValidMessage(this.getMessage("TRD_X_0005")));
            }
            if (StringUtil.equals(dtocopy.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                && trdPlanNetInvestTrade001Service.selRedemption(dtocopy.getTradeId()) != 0) {
                errorList.add(new ValidMessage(this.getMessage("TRD_X_0006")));
            }
            String ps = dtocopy.getPs();
            if (StringUtil.isNotEmpty(ps)
                && (StringUtil.equals(ps, DictCodeList.交易方向_申购.getValue())
                    || StringUtil.equals(ps, DictCodeList.交易方向_赎回.getValue()))) {
                if (!trdPlanNetInvestTrade001Service.revokeCheck(dtocopy)) {
                    errorList.add(new ValidMessage(this.getMessage("TRD_X_0007")));
                }
            }
            if (StringUtil.isNotEmpty(ps)
                && StringUtil.equals(ps, DictCodeList.交易方向_赎回到账.getValue())) {
                errorList.add(new ValidMessage(this.getMessage("TRD_X_0008")));
            }
        }

        if (StringUtil.equals(dto.getOperateType(), "save") || StringUtil.equals(dto.getOperateType(), "submit")) {
            if (StringUtil.isNotEmpty(dto.getBussTrdTransfenpeiDto())){
                Map<String, List<BussTrdTransfenpeiDto>> map = dto.getBussTrdTransfenpeiDto().stream().collect(JoyinCollectors.groupingBy(item -> item.getPortfolioId()));
                if (map!=null && map.keySet().size()<dto.getBussTrdTransfenpeiDto().size()){
                    errorList.add(new ValidMessage("组合分配不能重复分配给同一组合。"));
                }
            }
        }

        return errorList;
    }

}
