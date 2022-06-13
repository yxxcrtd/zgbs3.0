/*
 * 文件名：TrdPlanCcyInvestTradeValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.trade.planccy.validator;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.common.validator.TradeCommonValidator;
import com.joyintech.tams.invest.trade.planccy.dto.TrdPlanCcyInvestTradeDto;
import com.joyintech.tams.invest.trade.planccy.service.TrdPlanCcyInvestTrade001Service;
import com.joyintech.tams.invest.trade.planccy.service.TrdPlanCcyInvestTrade002Service;
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
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2019年1月7日
 * @see TrdPlanCcyInvestTradeValidator
 * @since 1.0
 */
@Component
public class TrdPlanCcyInvestTradeValidator implements TradeCommonValidator {

    @Autowired
    private TrdPlanCcyInvestTrade001Service trdPlanCcyInvestTrade001Service;
    @Autowired
    private TrdPlanCcyInvestTrade002Service trdPlanCcyInvestTrade002Service;

	@Autowired
	private TradeCommonService tradeCommonService;
    /**
     * BussTrdTransferOrderService
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
        TrdPlanCcyInvestTradeDto dto = (TrdPlanCcyInvestTradeDto)value;
        //交易对手必填 add by zhuyongkang
        if (!StringUtil.equals(dto.getOperateType(), "revoke") && !StringUtil.equals(dto.getOperateType(), "confirm")
                && (dto.getTrdInvestPersonInfo() == null || StringUtil.isEmpty(dto.getTrdInvestPersonInfo().getCounterId()))) {
            errorList.add(new ValidMessage("trdInvestPersonInfo.counterId", this.getMessage("BUSS_COM_E_0038", this.getMessage("交易对手"))));
            return errorList;
        }
        //修改bug BSTOT-531 交易对手选择内部交易对手时不可以做买入交易，只能卖出  wangcheng 20200214 start
        if(dto.getTrdInvestPersonInfo() != null){
        	if(StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dto.getPs()) 
        			&& StringUtil.equals(dto.getTrdInvestPersonInfo().getIsOutTrade(), DictCodeList.是否_否.getValue())){
        		errorList.add(new ValidMessage("trdInvestPersonInfo.counterId", this.getMessage("TRD_COUNTER_TYPE_001")));
                return errorList;
        	}
        }
      //修改bug BSTOT-531 交易对手选择内部交易对手时不可以做买入交易，只能卖出  wangcheng 20200214 end

        //修改bug BSTOT-1637 SIT-计划类货币型资产多组合交易卖出提交时没有校验“交易金额不可大于可用持仓 add by daizhiwei 20200222 start
        if (StringUtil.equals(dto.getOperateType(), "save") || StringUtil.equals(dto.getOperateType(), "submit")){
            List<BussTrdTransfenpeiDto> dtoList = dto.getBussTrdTransfenpeiDto();
            if (!StringUtil.isEmptyList(dtoList)
                && (StringUtil.equals(dto.getPs(),DictCodeList.交易方向_赎回.getValue()) || StringUtil.equals(dto.getPs(),DictCodeList.交易方向_卖出.getValue()))){
                dtoList.forEach(item ->{
                    if ((item.getTradeAmt() != null && item.getAssetsavailablepositions() == null)
                        ||(item.getTradeAmt() != null && item.getAssetsavailablepositions() != null
                        && item.getTradeAmt().compareTo(item.getAssetsavailablepositions()) == 1)){
                        errorList.add(new ValidMessage("tradeAmt", this.getMessage("BUSS_COM_E_0003", this.getMessage("TrdFundInvestTrade002.tradeAmt"),this.getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                    }
                });
            }
        }
        //修改bug BSTOT-1637 SIT-计划类货币型资产多组合交易卖出提交时没有校验“交易金额不可大于可用持仓 add by daizhiwei 20200222 start
        if (StringUtil.equals(dto.getOperateType(), "save")) {
            String entrustId = dto.getEntrustId();
            BigDecimal tradeAmt = dto.getTradeAmt();
            BigDecimal shareAmt = dto.getShareAmt();
            String tradeId = dto.getTradeId();
            if(dto.getTrdInvestPersonInfo() != null){
                String counterId = dto.getTrdInvestPersonInfo().getCounterTrader();
                String contactInfo = dto.getTrdInvestPersonInfo().getContactInfo();
//                if(!StringUtil.isNotEmpty(counterId)){
//                    errorList.add(new ValidMessage("trdInvestPersonInfo.counterTrader", this.getMessage("BUSS_COM_E_0038",this.getMessage("EtrInvestPersonInfo002.counterTrader"))));
//                }
              //修改bug BSTOT-512 联系方式必填 add by wangcheng 20200115 
//                if(!StringUtil.isNotEmpty(contactInfo)){
//                    errorList.add(new ValidMessage("trdInvestPersonInfo.contactInfo", this.getMessage("BUSS_COM_E_0038",this.getMessage("TrdOptionTrade002.contact"))));
//                }
            }
            if (StringUtil.isNotEmpty(entrustId) && (tradeAmt != null||shareAmt != null)) {
                Map<String, Object> param = new HashMap<>();
                param.put("entrustId", entrustId);
                param.put("tradeId", tradeId);
                Map<String, Object> resultMap = trdPlanCcyInvestTrade002Service.selectEntrustAmt(param);
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
                    if (tradeAmt != null && tradeAmt.compareTo(entrustAmt) == 1) {
                        errorList.add(new ValidMessage("tradeAmt", this.getMessage("TRD_X_0004") + entrustAmt));
                    }else if(shareAmt != null && shareAmt.compareTo(entrustAmt) == 1){
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("TRD_X_0009") + shareAmt));
                    }
                }
            }
         // 验证流程中是否存在该委托 
            if(StringUtil.isNotEmpty(entrustId) && WorkFlowUtil.isNotFromToDoList()){
            	String str = tradeCommonService.check(entrustId, "TrdPlanCcyInvestTrade002");
            	if(StringUtil.isNotEmpty(str)){ 
            		ValidMessage validMessage = new ValidMessage(str);
            		errorList.add(validMessage);
            	}
            }
            if (StringUtil.isNotEmpty(dto.getPs())) {
                if(!StringUtil.isNotEmpty(dto.getSettleSpreed()) 
                    && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue()))){
                    errorList.add(new ValidMessage("settleSpreed", this.getMessage("BUSS_COM_E_0038",this.getMessage("TrdPlanCcyInvestTrade002.settleSpreed"))));
                }
                BigDecimal zero = new BigDecimal(0);
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
                    if (tradeAmt == null) {
                        errorList.add(new ValidMessage("tradeAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeAmt"))));
                    }
                }
                else if (StringUtil.equals(dto.getPs(),DictCodeList.交易方向_赎回.getValue()) || StringUtil.equals(dto.getPs(),DictCodeList.交易方向_卖出.getValue())) {
                    if (shareAmt == null) {
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeSha"))));
                    }
                }
                if (tradeAmt != null && tradeAmt.compareTo(zero) == -1) {
                    errorList.add(new ValidMessage("tradeAmt", this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeAmt"),"0")));
                }
                if (shareAmt != null && shareAmt.compareTo(zero) == -1) {
                    errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeSha"),"0")));
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                    BigDecimal assetAvailablePosition = dto.getAssetAvailablePosition();
                    if (assetAvailablePosition != null && shareAmt != null
                        && shareAmt.compareTo(assetAvailablePosition) == 1) {
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0003", this.getMessage("TrdFundInvestTrade002.tradeSha"),this.getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                    }
                }
                if ((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))
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
//                ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
//                BigDecimal assetPosition = trdPlanCcyInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
//                //资产可用持仓小于交易份额
//                if(assetPosition.compareTo(dto.getShareAmt())== -1){
//                    errorList.add(new ValidMessage("",
//                        getMessage("BUSS_COM_E_0003",
//                        ptlSecPositionFlowDto.getPortfolioId()+"  "+getMessage("TrdPlanCcyInvestTrade002.tradeSha"),
//                        getMessage("TrdPlanCcyInvestTrade002.assetAvailablePosition"))));
//                }
//            }else
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())){
                Map<String, Object> param = new HashMap<>();
                param.put("portfolioId", dto.getTrdInvestPersonInfo().getCounterId());
                param.put("ccy", dto.getCcy());
                param.put("vdate", DateUtil.format(dto.getTradeDate()));
                param.put("calId", dto.getCalendarId());
                Map<String, Object> para = trdPlanCcyInvestTrade002Service.getProAvailableCash(param);
                BigDecimal accCash = new BigDecimal((String)para.get("cashT2"));
                //交易对手没有足够的现金
                //add wsz ZGCPCN-1127 修改交易验证软控 start
                if(accCash.compareTo(dto.getTradeAmt())<0  && CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH).equals(DictCodeListExt1.是或否_否.getValue())){
                    errorList.add(new ValidMessage("",
                        getMessage("BUSS_COM_E_0003", 
                        param.get("portfolioId")+"  "+getMessage("TrdPlanCcyInvestTrade002.tradeAmt"),
                        getMessage("TrdPlanCcyInvestTrade002.accAvailableCash"))));
                }
                //add wsz ZGCPCN-1127 修改交易验证软控 end
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "submit")) {
            String entrustId = dto.getEntrustId();
            BigDecimal tradeAmt = dto.getTradeAmt();
            BigDecimal shareAmt = dto.getShareAmt();
            String tradeId = dto.getTradeId();
            if(dto.getTrdInvestPersonInfo() != null){
                String counterId = dto.getTrdInvestPersonInfo().getCounterTrader();
                String contactInfo = dto.getTrdInvestPersonInfo().getContactInfo();
//                if(!StringUtil.isNotEmpty(counterId)){
//                    errorList.add(new ValidMessage("trdInvestPersonInfo.counterTrader", this.getMessage("BUSS_COM_E_0038",this.getMessage("EtrInvestPersonInfo002.counterTrader"))));
//                }
                //修改bug BSTOT-512 联系方式必填 add by wangcheng 20200115 
//                if(!StringUtil.isNotEmpty(contactInfo)){
//                    errorList.add(new ValidMessage("trdInvestPersonInfo.contactInfo", this.getMessage("BUSS_COM_E_0038",this.getMessage("TrdOptionTrade002.contact"))));
//                }
            }
            //判断dto.getBussTrdTransfenpeiDto() 为空 为单组合 add by wangcheng 20190116
            if (StringUtil.isNotEmpty(entrustId) && (tradeAmt != null||shareAmt != null) && StringUtil.isEmptyList(dto.getBussTrdTransfenpeiDto())) {
                Map<String, Object> param = new HashMap<>();
                param.put("entrustId", entrustId);
                param.put("tradeId", tradeId);
                Map<String, Object> resultMap = trdPlanCcyInvestTrade002Service.selectEntrustAmt(param);
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
                    if ((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())|| StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()))&& tradeAmt != null && tradeAmt.compareTo(entrustAmt) == 1) {
                        errorList.add(new ValidMessage("tradeAmt", this.getMessage("TRD_X_0004") + entrustAmt));
                    }else if((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())|| StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue()))&&shareAmt != null && shareAmt.compareTo(entrustAmt) == 1){
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("TRD_X_0009") + entrustAmt));
                    }
                }
            }
         // 验证流程中是否存在该委托 
            if(StringUtil.isNotEmpty(entrustId) && WorkFlowUtil.isNotFromToDoList()){
            	String str = tradeCommonService.check(entrustId, "TrdPlanCcyInvestTrade002");
            	if(StringUtil.isNotEmpty(str)){ 
            		ValidMessage validMessage = new ValidMessage(str);
            		errorList.add(validMessage);
            	}
            }
            if (StringUtil.isNotEmpty(dto.getPs())) {
                if(!StringUtil.isNotEmpty(dto.getSettleSpreed()) 
                    && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue()))){
                    errorList.add(new ValidMessage("settleSpreed", this.getMessage("BUSS_COM_E_0038",this.getMessage("TrdPlanCcyInvestTrade002.settleSpreed"))));
                }
                BigDecimal zero = new BigDecimal(0);
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
                    if (tradeAmt == null) {
                        errorList.add(new ValidMessage("tradeAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeAmt"))));
                    }
                }
                else if (StringUtil.equals(dto.getPs(),DictCodeList.交易方向_赎回.getValue()) || StringUtil.equals(dto.getPs(),DictCodeList.交易方向_卖出.getValue())) {
                    if (shareAmt == null) {
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeSha"))));
                    }
                }
                if (tradeAmt != null && tradeAmt.compareTo(zero) == -1) {
                    errorList.add(new ValidMessage("tradeAmt", this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeAmt"),"0")));
                }
                if (shareAmt != null && shareAmt.compareTo(zero) == -1) {
                    errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeSha"),"0")));
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                    BigDecimal assetAvailablePosition = dto.getAssetAvailablePosition();
                    if (assetAvailablePosition != null && shareAmt != null
                        && shareAmt.compareTo(assetAvailablePosition) == 1) {
                        errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0003", this.getMessage("TrdFundInvestTrade002.tradeSha"),this.getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                    }
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
                //内转交易不能超过内部交易对手组合可用持仓
//                if(dto.getTrdInvestPersonInfo().getCounterId()!= null && StringUtil.isNotEmpty(dto.getFinprodId()) && dto.getTradeDate()!=null){
//                    if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()) && dto.getTradeAmt()!=null){
//                        Map<String, Object> param = new HashMap<>();
//                        param.put("vdate", DateUtil.format(dto.getTradeDate()));
//                        param.put("portfolioId", dto.getTrdInvestPersonInfo().getCounterId());
//                        param.put("ccy", dto.getCcy());
//                        Map<String, Object> result = trdPlanCcyInvestTrade002Service.getProAvailableCash(param);
//                        if(StringUtil.isNotEmpty((String)result.get("cashT"))){
//                            BigDecimal cashT = new BigDecimal((String)result.get("cashT"));
//                            if(cashT.compareTo(dto.getTradeAmt()) == -1){
//                                errorList.add(new ValidMessage("tradeAmt", this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdPlanCcyInvestTrade002.proAvailableCashBack"),this.getMessage("TrdPlanCcyInvestTrade002.tradeAmt"))));
//                            }
//                        }
//                    }else if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) && dto.getShareAmt()!=null){
//                        PtlSecPositionFlowDto positiondto = new PtlSecPositionFlowDto();
//                        positiondto.setPortfolioId(dto.getTrdInvestPersonInfo().getCounterId());
//                        positiondto.setFinprodId(dto.getFinprodId());
//                        positiondto.setCdate(dto.getTradeDate());
//                        BigDecimal resultPosition = trdPlanCcyInvestTrade002Service.getPtlSecPosition(positiondto);
//                        if(resultPosition != null && resultPosition.compareTo(dto.getShareAmt()) == -1){
//                            errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdPlanCcyInvestTrade002.assetAvailablePositionBack"),this.getMessage("TrdPlanCcyInvestTrade002.tradeSha"))));
//                        }
//                    }
//                }
            }
            //     附件上传改为非必填 by zhuyongkang 20191010 start
//            if (dto.getFileArray() == null || dto.getFileArray().size() <= 0) {
//                errorList.add(new ValidMessage(
//                    getMessage("BUSS_COM_E_0038", getMessage("TrdFundInvestTrade002.file"))));
//            }
//            if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())){内部交易只能卖出
//                PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
//                ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
//                ptlSecPositionFlowDto.setPortfolioId(dto.getTrdInvestPersonInfo().getCounterId());
//                ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
//                BigDecimal assetPosition = trdPlanCcyInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
//                //资产可用持仓小于交易份额
//                if(assetPosition.compareTo(dto.getShareAmt())== -1){
//                    errorList.add(new ValidMessage("",
//                        getMessage("BUSS_COM_E_0003",
//                        ptlSecPositionFlowDto.getPortfolioId()+"  "+getMessage("TrdPlanCcyInvestTrade002.tradeSha"),
//                        getMessage("TrdPlanCcyInvestTrade002.assetAvailablePosition"))));
//                }
//            }else
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())){
                Map<String, Object> param = new HashMap<>();
                param.put("portfolioId", dto.getTrdInvestPersonInfo().getCounterId());
                param.put("ccy", dto.getCcy());
                param.put("vdate", DateUtil.format(dto.getTradeDate()));
                param.put("calId", dto.getCalendarId());
                Map<String, Object> para = trdPlanCcyInvestTrade002Service.getProAvailableCash(param);
                BigDecimal accCash = new BigDecimal((String)para.get("cashT2"));
                //交易对手没有足够的现金
                //add wsz ZGCPCN-1127 修改交易验证软控 start
                if(accCash.compareTo(dto.getTradeAmt())<0  && CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH).equals(DictCodeListExt1.是或否_否.getValue())){
                    errorList.add(new ValidMessage("",
                        getMessage("BUSS_COM_E_0003", 
                        param.get("portfolioId")+"  "+getMessage("TrdPlanCcyInvestTrade002.tradeAmt"),
                        getMessage("TrdPlanCcyInvestTrade002.accAvailableCash"))));
                }
                //add wsz ZGCPCN-1127 修改交易验证软控 end
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "revoke")) {
            TrdPlanCcyInvestTradeDto dtocopy = dto.getSelectedItem().get(0);
            if (StringUtil.equals(dtocopy.getValidStatus(),
                DictCodeListExt1.交易流程状态_撤销.getValue())
                || StringUtil.equals(dtocopy.getValidStatus(),
                    DictCodeListExt1.交易流程状态_流程中.getValue())) {
                errorList.add(new ValidMessage(this.getMessage("TRD_X_0005")));
            }
            if (StringUtil.equals(dtocopy.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                && trdPlanCcyInvestTrade001Service.selRedemption(dtocopy.getTradeId()) != 0) {
                errorList.add(new ValidMessage(this.getMessage("TRD_X_0006")));
            }
            String ps = dtocopy.getPs();
            if (StringUtil.isNotEmpty(ps)
                && (StringUtil.equals(ps, DictCodeList.交易方向_申购.getValue())
                    || StringUtil.equals(ps, DictCodeList.交易方向_赎回.getValue()))) {
                if (!trdPlanCcyInvestTrade001Service.revokeCheck(dtocopy)) {
                    errorList.add(new ValidMessage(this.getMessage("TRD_X_0007")));
                }
            }
            if (StringUtil.isNotEmpty(ps)
                && StringUtil.equals(ps, DictCodeList.交易方向_赎回到账.getValue())) {
                errorList.add(new ValidMessage(this.getMessage("TRD_X_0008")));
            }
            // if(StringUtil.isNotEmpty(ps) && StringUtil.equals(ps,
            // DictCodeList.交易方向_赎回确认.getValue())){
            // WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            // wflValidInputParameter.setBusinessDataPk1(dtocopy.getTradeId());
            // wflValidInputParameter.setBusinessDataPk2(dtocopy.getBusiType());
            // wflValidInputParameter.setPageId("TrdFundInvestTrade003");
            // boolean flag = mstWorkflowCommonService.isInWflProcess(wflValidInputParameter);
            // if(flag){
            // errorList.add(new ValidMessage("赎回到账交易在流程中不能撤销"));
            // }
            // }
        }

        if (StringUtil.equals(dto.getOperateType(), "save") || StringUtil.equals(dto.getOperateType(), "submit")){
            if (StringUtil.isNotEmpty(dto.getBussTrdTransfenpeiDto())){
                Map<String, List<BussTrdTransfenpeiDto>> map = dto.getBussTrdTransfenpeiDto().stream().collect(JoyinCollectors.groupingBy(item->item.getPortfolioId()));
                if (map!=null && map.keySet().size()<dto.getBussTrdTransfenpeiDto().size()){
                    errorList.add(new ValidMessage("组合分配不能重复分配给同一组合。"));
                }
            }

        }
        return errorList;
    }

}
