/*
 * 文件名：TrdBondLendInvestTradeDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年06月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.bondlend.validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.trade.bondlend.dto.TrdBondLendInvestTradeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.trade.bond.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.common.validator.TradeCommonValidator;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 债券投资交易保存提交check
 *
 * @author 裴家俊
 * @version 2019年06月25日
 * @see TrdBondLendInvestTradeDtoValidator
 * @since 1.0
 */
@Component
public class TrdBondLendInvestTradeDtoValidator implements TradeCommonValidator {

    /**
     * BussTrdTransferOrderService
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * TrdBondInvestTrade002Service
     */
    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;
    
    @Autowired
	private TradeCommonService tradeCommonService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        TrdBondLendInvestTradeDto inputDto = (TrdBondLendInvestTradeDto)value;
        //万元转换 add  by yeming 20210817 start
        if(inputDto.getBussRepoSec() != null){
            if( inputDto.getBussRepoSec().getTotalPrinAmt()  != null && StringUtil.equals(CacheUtil.getSysParam("entrustAmtUnit"),"02")){
                inputDto.getBussRepoSec().setTotalPrinAmt(ArithmeticUtil.multiply(2, RoundingMode.HALF_UP, inputDto.getBussRepoSec().getTotalPrinAmt(),new BigDecimal(10000)));
            }
            for (PledgeBondInfoDto pledgeBond :  inputDto.getBussRepoSec().getPledgeBondList()) {
                if (pledgeBond.getAvaPledgePrin() != null) {
                    pledgeBond.setAvaPledgePrin(ArithmeticUtil.multiply(2, RoundingMode.HALF_UP,pledgeBond.getAvaPledgePrin(), new BigDecimal(10000)));
                }
                if (pledgeBond.getPledgePrin() != null) {
                    pledgeBond.setPledgePrin(ArithmeticUtil.multiply(2, RoundingMode.HALF_UP,pledgeBond.getPledgePrin(), new BigDecimal(10000)));
                }
                if (pledgeBond.getPrin() != null) {
                    pledgeBond.setPrin(ArithmeticUtil.multiply(2, RoundingMode.HALF_UP,pledgeBond.getPrin(), new BigDecimal(10000)));
                }
            }
        }
        //万元转换 add  by yeming 20210817 end
        List<ValidMessage> errorList = new ArrayList<>();

        //中债新增 20200407 start
        //持有目的
        if(StringUtil.isEmpty(inputDto.getHoldPurpose())){
            ValidMessage validMessage = new ValidMessage("holdPurpose",
                    getMessage("FW_E_0006", getMessage("TrdBondLendInvestTrade002.holdPurpose")));
            errorList.add(validMessage);
        }else if(StringUtil.equals(DictCodeListExt1.持有目的_其他.getValue(), inputDto.getHoldPurpose())){
        	if (StringUtil.isEmpty(inputDto.getRemark())) {
        		ValidMessage validMessage = new ValidMessage("remark",
        				getMessage("FW_E_0006", getMessage("TrdBondLendInvestTrade002.remark")));
        		errorList.add(validMessage);
			}
		}
        //中债新增 20200407 end
        // 券面金额 〉 0
        if (inputDto.getShareAmt() != null && inputDto.getShareAmt().compareTo(BigDecimal.ZERO) <= 0) {
            ValidMessage validMessage = new ValidMessage("tradeAmt",
                getMessage("BUSS_COM_E_0036", getMessage("TrdBondLendInvestTrade002.tradeAmt"), "0"));
            errorList.add(validMessage);
        }
        if (inputDto.getmTradeAmt() != null && inputDto.getmTradeAmt().compareTo(BigDecimal.ZERO) <= 0) {
            ValidMessage validMessage = new ValidMessage("unitCprice",
                getMessage("BUSS_COM_E_0036", getMessage("TrdBondLendInvestTrade002.mTradeAmt"), "0"));
            errorList.add(validMessage);
        }
        if (inputDto.getmYield() != null && inputDto.getmYield().compareTo(BigDecimal.ZERO) <= 0) {
            ValidMessage validMessage = new ValidMessage("cpriceAmt",
                getMessage("BUSS_COM_E_0036", getMessage("TrdBondLendInvestTrade002.mYield"), "0"));
            errorList.add(validMessage);
        }

        // 交易日 <=首次结算日 < 到期结算日 < 标的债券到期日，提交报错
        if (inputDto.getTradeDate() != null && inputDto.getMdate() != null) {
            Map<String,Object> finParam=new HashMap<String,Object>();
            finParam.put("initValue",inputDto.getFinprodId());
            Page<SelectOptionDto> curFin = trdBondInvestTrade002Service.getFinprodId(finParam);
            Map<String,Object> curMap=(Map<String,Object>)curFin.getRoot().get(0).getObject();
            if (DateUtil.compareDate(inputDto.getTradeDate(), DateUtil.parse(curMap.get("mdate").toString())) >= 0) {
                ValidMessage validMessage = new ValidMessage("tradeDate",
                    getMessage("BUSS_COM_E_0035", getMessage("TrdBondLendInvestTrade002.tradeDate"),
                        getMessage("TrdBondLendInvestTrade002.finMdate")));
                errorList.add(validMessage);
            }
            if (DateUtil.compareDate(inputDto.getMdate(), DateUtil.parse(curMap.get("mdate").toString())) >= 0) {
                ValidMessage validMessage = new ValidMessage("mdate",
                    getMessage("BUSS_COM_E_0035", getMessage("TrdBondLendInvestTrade002.mdate"),
                        getMessage("TrdBondLendInvestTrade002.finMdate")));
                errorList.add(validMessage);
            }
        }

        // 交易场所为银行间时,附件需上传成交单
//        if (StringUtil.equals(DictCodeListExt1.交易场所_银行间.getValue(),
//            inputDto.getTradeMarket())) {
//            if (StringUtil.isEmptyList(inputDto.getUploadFileList())) {
//                ValidMessage validMessage = new ValidMessage(
//                    getMessage("FW_E_0006", getMessage("FileCommon")));
//                errorList.add(validMessage);
//            }
//        }
        // 交易方向为融入时；
        if (StringUtil.equals(DictCodeList.回购拆借方向_融入.getValue(), inputDto.getPs())) {
            if(inputDto.getBussRepoSec()==null){
                ValidMessage validMessage = new ValidMessage(
                    getMessage("FW_E_0006", getMessage("TrdRepoTrade001.manage")));
                errorList.add(validMessage);
            }
            else{
                if(StringUtil.isEmptyList(inputDto.getBussRepoSec().getPledgeBondList())){
                    ValidMessage validMessage = new ValidMessage(
                        getMessage("FW_E_0006", getMessage("TrdRepoTrade001.manage")));
                    errorList.add(validMessage);
                }
            }
        }
        // 交易方向为融出时，显示可用持仓，券面总额不可大于可用持仓；
//        if (StringUtil.equals(DictCodeList.回购拆借方向_融出.getValue(), inputDto.getPs())) {
//            if (inputDto.getShareAmt() != null && inputDto.getAssetPosition() != null
//                && inputDto.getShareAmt().compareTo(inputDto.getAssetPosition()) > 0) {
//                ValidMessage validMessage = new ValidMessage(
//                    getMessage("BUSS_COM_E_0003", getMessage("TrdBondLendInvestTrade002.tradeAmt"),
//                        getMessage("TrdBondLendInvestTrade002.avaliblePosition")));
//                errorList.add(validMessage);
//            }
//        }
        if (StringUtil.equals(DictCodeList.回购拆借方向_融入.getValue(), inputDto.getPs())) {
            if (inputDto.getShareAmt() != null && inputDto.getBussRepoSec() != null && inputDto.getBussRepoSec().getTotalPrinAmt() !=null
                    && inputDto.getShareAmt().compareTo(inputDto.getBussRepoSec().getTotalPrinAmt()) > 0) {
                ValidMessage validMessage = new ValidMessage(
                        getMessage("BUSS_COM_E_0003", getMessage("TrdBondLendInvestTrade002.tradeAmt"),
                                getMessage("BussRepoSec.totalPrinAmt")));
                errorList.add(validMessage);
            }
        }

        // 委托场合相关check
        if (StringUtil.isNotBlank(inputDto.getEntrustId())) {
            // 券面总额超过委托时的券面总额
            if (inputDto.getShareAmt() != null) {
                // 查询未完成的委托额度
                BaseEtrInvestInfoDto entrustInfo = trdBondInvestTrade002Service.getEntrustInfo(inputDto.getEntrustId());
                BigDecimal finishAmt = BigDecimal.ZERO;
                if(entrustInfo!=null&&entrustInfo.getEtrFinishStatusList()!=null){
                	for(EtrFinishStatus fin:entrustInfo.getEtrFinishStatusList()){
                		finishAmt = ArithmeticUtil.doAdd(finishAmt,fin.getFinishAmt());
                	}
                }
                //券面总额在js中已经转成元，故此处比较不再乘10000
                if (entrustInfo != null && entrustInfo.getEtrEntrust() != null
                    && entrustInfo.getEtrEntrust().getEntrustAmt() != null
                    && inputDto.getShareAmt().compareTo(
                        ArithmeticUtil.subtract(entrustInfo.getEtrEntrust().getEntrustAmt(), finishAmt)) > 0) {
                    ValidMessage validMessage = new ValidMessage("tradeAmt",
                        getMessage("TRD_W_0009", getMessage("TrdBondLendInvestTrade002.tradeAmt")));
                    errorList.add(validMessage);
                }
            }
            // 验证流程中是否存在该委托 
            if(StringUtil.isNotEmpty(inputDto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()){
            	String str = tradeCommonService.check(inputDto.getEntrustId(), "TrdBondLendInvestTrade002");
            	if(StringUtil.isNotEmpty(str)){ 
            		ValidMessage validMessage = new ValidMessage(str);
            		errorList.add(validMessage);
            	}
            }

            // 委托场合时且交易方向为买入
            if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), inputDto.getPs())) {
                // 净价超过委托时的净价;
                if (inputDto.getUnitCprice() != null && inputDto.getEntrustPrice() != null
                    && inputDto.getUnitCprice().compareTo(inputDto.getEntrustPrice()) > 0) {
                    ValidMessage validMessage = new ValidMessage("unitCprice",
                        getMessage("TRD_W_0007", getMessage("TrdBondInvestTrade002.unitCprice"),
                            getMessage("TrdBondInvestTrade002.unitCprice"),
                            getMessage("TrdLendInvestTrade002.morethan")));
                    errorList.add(validMessage);
                }

                // 到期收益率低于委托时的到期收益率;
                if (inputDto.getYield() != null && inputDto.getEntrustYield() != null
                    && inputDto.getYield().compareTo(inputDto.getEntrustYield()) < 0) {
                    ValidMessage validMessage = new ValidMessage("yield",
                        getMessage("TRD_W_0007", getMessage("TrdBondInvestTrade002.yield"),
                            getMessage("TrdBondInvestTrade002.yield"),
                            getMessage("TrdLendInvestTrade002.lessthan")));
                    errorList.add(validMessage);
                }

                // 行权收益率低于委托时的行权收益率
                if (inputDto.getExerYield() != null && inputDto.getEntrustExerYield() != null
                    && inputDto.getExerYield().compareTo(inputDto.getEntrustExerYield()) < 0) {
                    ValidMessage validMessage = new ValidMessage("exerYield",
                        getMessage("TRD_W_0007", getMessage("TrdBondInvestTrade002.exerYield"),
                            getMessage("TrdBondInvestTrade002.exerYield"),
                            getMessage("TrdLendInvestTrade002.lessthan")));
                    errorList.add(validMessage);
                }
            }
            // 委托场合时且交易方向为卖出：
            else {
                // 净价低于委托时的净价;
                if (inputDto.getUnitCprice() != null && inputDto.getEntrustPrice() != null
                    && inputDto.getUnitCprice().compareTo(inputDto.getEntrustPrice()) < 0) {
                    ValidMessage validMessage = new ValidMessage("unitCprice",
                        getMessage("TRD_W_0007", getMessage("TrdBondInvestTrade002.unitCprice"),
                            getMessage("TrdBondInvestTrade002.unitCprice"),
                            getMessage("TrdLendInvestTrade002.lessthan")));
                    errorList.add(validMessage);
                }
            }
        }


    //证券托管户
        if(inputDto.getSecManageAcctId()==null||"".equals(inputDto.getSecManageAcctId())){
             ValidMessage validMessage =  new ValidMessage("secManageAcctId",getMessage("TRD_E_0014_1"));
        	
        	errorList.add(validMessage);
        }

        // add by 裴家俊 2019-05-23--start----
        if(StringUtil.equals(inputDto.getOperateType(), "save") || StringUtil.equals(inputDto.getOperateType(), "submit")){
            if(inputDto.getTrdInvestPersonInfo() != null && StringUtil.isEmpty(inputDto.getTrdInvestPersonInfo().getCounterId())){
                ValidMessage validMessage = new ValidMessage("trdInvestPersonInfo.counterId",getMessage("FW_E_0006", getMessage("TrdDepositInvestTrade002.cno")));
                errorList.add(validMessage);
            }
        }
        // add by 裴家俊 2019-05-23--end----
        
        if(StringUtil.equals(inputDto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)||StringUtil.equals(inputDto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
            if(inputDto.getBussRepoSec()!=null&&!StringUtil.isNotEmpty(inputDto.getBussRepoSec().getExchangePledge())) {
           	 	ValidMessage validMessage = new ValidMessage("bussRepoSec.exchangePledge",getMessage("TRD_BONDLEND_E_001"));
           	 	errorList.add(validMessage);
           }	
        }

        return errorList;
    }

}
