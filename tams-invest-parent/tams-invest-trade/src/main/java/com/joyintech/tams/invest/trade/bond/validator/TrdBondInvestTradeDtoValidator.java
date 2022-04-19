/*
 * 文件名：TrdBondInvestTradeDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2018年11月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.bond.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.InterfTradSecdeal;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.trade.bond.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.common.validator.TradeCommonValidator;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 债券投资交易保存提交check
 *
 * @author etliu
 * @version 1.0 2018年11月29日
 * @see TrdBondInvestTradeDtoValidator
 * @since 1.0
 */
@Component
public class TrdBondInvestTradeDtoValidator implements TradeCommonValidator {

    /**
     * BussTrdTransferOrderService
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    
    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * TrdBondInvestTrade002Service
     */
    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;
    
    @Autowired
	private TradeCommonService tradeCommonService;
    
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired
    private DateCalService dateCalService  ;

    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;

    @Value("${flag:false}")
    private boolean flag;
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        TrdBondInvestTradeDto inputDto = (TrdBondInvestTradeDto)value;
        List<ValidMessage> errorList = new ArrayList<>();
        
        if(flag) {
        	return errorList;
        }

        // 交易所债一级市场时，交易平台非必填 add by renping 20191105 start
        if (!((StringUtil.equals(inputDto.getTradeMarket(),
            DictCodeListExt1.交易场所_上海证券交易所.getValue())
               || StringUtil.equals(inputDto.getTradeMarket(),
                   DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
              && StringUtil.equals(inputDto.getTradeMarketMode(),
                  DictCodeListExt1.交易市场_一级市场.getValue()))) {
            if (StringUtil.isEmpty(inputDto.getTradePlat())) {
                errorList.add(new ValidMessage("tradePlat",
                    getMessage("FW_E_0006", getMessage("TrdBondInvestTrade002.tradePlat"))));
            }
        }
        // 交易所债一级市场时，交易平台非必填 add by renping 20191105 end
        // 到期收益率，买入时必填
        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), inputDto.getPs())) {
            /*if (inputDto.getIntegrateTradeAmt() != null && inputDto.getIntegrateTradeAmt().compareTo(BigDecimal.ZERO) > 0
                && inputDto.getIntegrateTradeAmt().compareTo(inputDto.getAssetIntegrateSaleAvailablePosition()) > 0) {
                errorList.add(new ValidMessage("integrateTradeAmt", "卖空购回券面总额不能大于融入券卖空持仓"));
            }*/
            if (inputDto.getYield() == null) {
                ValidMessage validMessage = new ValidMessage("yield",
                    getMessage("FW_E_0006", getMessage("TrdBondInvestTrade002.yield")));
                errorList.add(validMessage);
            }
        }

        if (!trdBondInvestTrade002Service.isRight(inputDto.getFinprodId())
        		&& (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), inputDto.getPs())
        				|| StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), inputDto.getPs()))) {
            ValidMessage validMessage = new ValidMessage("非行权债不可做行权交易。");
                errorList.add(validMessage);
        }
        if ((StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), inputDto.getPs())
                || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), inputDto.getPs()))) {
            if (inputDto.getTradeAmt() == null || inputDto.getTradeAmt().compareTo(BigDecimal.ZERO) <= 0) {
                errorList.add(new ValidMessage("tradeAmt", "行权时，券面总额必填"));
            }
        }
        // 交易金额 〉 0
        if(CollectionUtils.isEmpty(inputDto.getBussTrdTransfenpeiDto())){
            if (inputDto.getTradeAmt() != null && inputDto.getTradeAmt().compareTo(BigDecimal.ZERO) <= 0) {
                ValidMessage validMessage = new ValidMessage(getMessage("BUSS_COM_E_0036", getMessage("TrdBondInvestTrade002.tradeAmt"), "0"));
                errorList.add(validMessage);
            }

            if (inputDto.getCpriceAmt() != null && inputDto.getUnitCprice().compareTo(BigDecimal.ZERO) <= 0) {
                ValidMessage validMessage = new ValidMessage("unitCprice",
                        getMessage("BUSS_COM_E_0036", getMessage("TrdBondInvestTrade002.unitCprice"), "0"));
                errorList.add(validMessage);
            }
            if (inputDto.getCpriceAmt() != null && inputDto.getCpriceAmt().compareTo(BigDecimal.ZERO) <= 0) {
                ValidMessage validMessage = new ValidMessage("cpriceAmt",
                        getMessage("BUSS_COM_E_0036", getMessage("TrdBondInvestTrade002.cpriceAmt"), "0"));
                errorList.add(validMessage);
            }
            if (inputDto.getFpriceAmt() != null && inputDto.getFpriceAmt().compareTo(BigDecimal.ZERO) <= 0) {
                ValidMessage validMessage = new ValidMessage("fpriceAmt",
                        getMessage("BUSS_COM_E_0036", getMessage("TrdBondInvestTrade002.fpriceAmt"), "0"));
                errorList.add(validMessage);
            }
        }

        // 非行权日，不可以录入行权交易
        // 一体化对应etliu start
//        if (StringUtil.equals(DictCodeList.交易方向_行权.getValue(), inputDto.getPs())
//            && inputDto.getSettleDate() != null) {
        if ((StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), inputDto.getPs())
        		|| StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), inputDto.getPs()))
            && inputDto.getSettleDate() != null) {
        	// 一体化对应etliu end
            if (!trdBondInvestTrade002Service.isExerciseDate(inputDto.getFinprodId(),
                DateUtil.format(inputDto.getSettleDate()))) {
                ValidMessage validMessage = new ValidMessage("settleDate",
                    getMessage("TRD_E_0001", getMessage("TrdBondInvestTrade002.settleDate")));
                errorList.add(validMessage);
            }
        }



        // ZGZGST-1232 fuyongsheng 20210820:增加SPPI测试 系统参数开关,Y:需要测试
        if(DictCodeListExt1.是否_是.getValue().equals(CacheUtil.getSysParam("IS_NEED_SPPI_CHECK"))){
            //投资目的
            if(StringUtil.isNotEmpty(inputDto.getInvAim())) {
                if(StringUtil.equals(inputDto.getInvAim(), DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue())
                        || StringUtil.equals(inputDto.getInvAim(), DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue())) {
                    FinProduct finProduct = finProductMapper.selectByPrimaryKey(inputDto.getFinprodId());
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

        // 银行间成交编号 条件必填
//        if (StringUtil.equals(DictCodeListExt1.交易场所_银行间.getValue(),
//            inputDto.getTradeMarket())) {
//            if (StringUtil.isBlank(inputDto.getfTradeId())) {
//                ValidMessage validMessage = new ValidMessage("fTradeId",
//                    getMessage("FW_E_0006", getMessage("TrdBondInvestTrade002.fTradeId")));
//                errorList.add(validMessage);
//            }
//        }

        // 划款指令
        if (bussTrdTransferOrderService != null) {
            List<ValidMessage> list = bussTrdTransferOrderService.check(
                inputDto.getBussTrdTransferOrder());
            errorList.addAll(list);
        }

        // 画面.交割日 >= 标的类金融产品.到期日 时，提交报错
        if (inputDto.getSettleDate() != null && inputDto.getMdate() != null) {
            if (DateUtil.compareDate(inputDto.getSettleDate(), inputDto.getMdate()) >= 0) {
                ValidMessage validMessage = new ValidMessage("settleDate",
                    getMessage("BUSS_COM_E_0035", getMessage("TrdBondInvestTrade002.settleDate"),
                        getMessage("TrdBondInvestTrade002.mdate")));
                errorList.add(validMessage);
            }
        }

//         12、 交易场所为银行间时,附件需上传成交单
         /*add by 朱永康 附件上传改为非必填 20190830 start*/
//        if (StringUtil.equals(DictCodeListExt1.交易场所_银行间.getValue(),
//            inputDto.getTradeMarket())) {
//            if (StringUtil.isEmptyList(inputDto.getUploadFileList())) {
//                ValidMessage validMessage = new ValidMessage(
//                    getMessage("FW_E_0006", getMessage("FileCommon")));
//                errorList.add(validMessage);
//            }
//        }
        /*add by 朱永康 附件上传改为非必填 20190830 end*/
        // 交易方向为卖出或行权时，显示可用持仓，券面总额不可大于可用持仓；

        if(CollectionUtils.isNotEmpty(inputDto.getBussTrdTransfenpeiDto())){//多组合模式
            for (BussTrdTransfenpeiDto fenpeiDto:inputDto.getBussTrdTransfenpeiDto()) {
                if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), inputDto.getPs())
                        // 一体化对应etliu start
//            || StringUtil.equals(DictCodeList.交易方向_行权.getValue(), inputDto.getPs())) {
                        || (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), inputDto.getPs())
                        || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), inputDto.getPs()))) {
                    // 一体化对应etliu end
                    if (fenpeiDto.getTradeAmt() != null && fenpeiDto.getAssetsavailablepositions()!= null
                            && fenpeiDto.getTradeAmt().compareTo(fenpeiDto.getAssetsavailablepositions()) > 0) {
                        ValidMessage validMessage = new ValidMessage(
                                getMessage("BUSS_COM_E_0003", getMessage("TrdBondInvestTrade002.tradeAmt"),
                                        getMessage("TrdBondInvestTrade002.avaliblePosition")));
                        errorList.add(validMessage);
                        break;
                    }
                }
                checkSellPositionFenpei(inputDto, fenpeiDto, errorList);
            }
        }else {
            if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), inputDto.getPs())
                    // 一体化对应etliu start
//            || StringUtil.equals(DictCodeList.交易方向_行权.getValue(), inputDto.getPs())) {
                    || (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), inputDto.getPs())
                    || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), inputDto.getPs()))) {
                // 一体化对应etliu end
                if (inputDto.getTradeAmt() != null && inputDto.getAssetPosition() != null
                        && inputDto.getTradeAmt().compareTo(inputDto.getAssetPosition()) > 0) {
                    ValidMessage validMessage = new ValidMessage(
                            getMessage("BUSS_COM_E_0003", getMessage("TrdBondInvestTrade002.tradeAmt"),
                                    getMessage("TrdBondInvestTrade002.avaliblePosition")));
                    errorList.add(validMessage);
                }
            }

            checkSellPosition(inputDto, errorList);
        }

        // 委托场合相关check
        if (StringUtil.isNotBlank(inputDto.getEntrustId())) {
            // 券面总额超过委托时的券面总额
            if (inputDto.getTradeAmt() != null) {
                // 查询未完成的委托额度
//                BaseEtrInvestInfoDto entrustInfo = trdBondInvestTrade002Service.getEntrustInfo(inputDto.getEntrustId());
//                if (entrustInfo != null && entrustInfo.getEtrEntrust() != null
//                    && entrustInfo.getEtrEntrust().getEntrustNum() != null
//                    && inputDto.getTradeAmt().compareTo(
//                        entrustInfo.getEtrEntrust().getEntrustNum()) > 0) {
//                    ValidMessage validMessage = new ValidMessage("tradeAmt",
//                        getMessage("TRD_W_0009", getMessage("TrdBondInvestTrade002.tradeAmt")));
//                    errorList.add(validMessage);
//                }
            	

                if(CollectionUtils.isNotEmpty(inputDto.getBussTrdTransfenpeiDto())){//多组合模式
                    List<BaseEtrInvestInfoDto> entrustInfos = trdBondInvestTrade002Service.getEntrustInfos(inputDto.getEntrustId());
                    //特殊处理中标结果中批量缴款 start
                    if (StringUtil.isNotBlank(inputDto.getBondFirstSign())){
                        entrustInfos.stream().forEach(e ->{
                            //数量*100，再转换成万元
                            e.getEtrEntrust().setEntrustNum(com.joyintech.framework.util.ArithmeticUtil.multiply(e.getEtrEntrust().getEntrustNum(),new BigDecimal("1000000")));
                            e.setDoEntrustMoney(com.joyintech.framework.util.ArithmeticUtil.multiply(e.getDoEntrustMoney(),new BigDecimal("1000000")));
                        });
                    }
                    //特殊处理中标结果中批量缴款 end
                    List<BussTrdTransfenpeiDto> bussTrdTransfenpeiDto = inputDto.getBussTrdTransfenpeiDto();
                    BigDecimal entrustNum_;
                    for (BussTrdTransfenpeiDto fenpeiDto:bussTrdTransfenpeiDto) {
                        for (BaseEtrInvestInfoDto entrustInfo:entrustInfos) {
                            entrustNum_ = entrustInfo.getEtrEntrust().getEntrustNum();
                            if(StringUtils.equals("02",CacheUtil.getSysParam("entrustAmtUnit"))){
                                entrustNum_ = entrustNum_.multiply(new BigDecimal(10000));
                            }
                            if (StringUtils.equals(fenpeiDto.getPortfolioId(),entrustInfo.getEtrEntrust().getPortfolioId())
                            		&& StringUtils.equals(fenpeiDto.getEntrustId(),entrustInfo.getEtrEntrust().getEntrustId())
                                    &&entrustInfo != null && entrustInfo.getEtrEntrust() != null
                                    && entrustInfo.getEtrEntrust().getEntrustNum() != null) {
                                if(fenpeiDto.getTradeAmt().compareTo(entrustNum_)>0){
                                    ValidMessage validMessage = new ValidMessage(
                                            getMessage("TRD_W_0009", getMessage("TrdBondInvestTrade002.tradeAmt")));
                                    errorList.add(validMessage);
                                    break;
                                }
                            }
                        }
                    }
                }else{
				//委托查询流程中与已完成额度  不得大于当前券面总额 add by wangpeng 20190906
	            	BigDecimal entrustNum = tradeCommonService.getEntrustAmtByEntrustId(inputDto.getEntrustId());
	                BigDecimal wflPrinAmt = tradeCommonService.getWflPrinAmt(inputDto.getEntrustId(), inputDto.getPortfolioId(), null);
	                if(StringUtil.isNotEmpty(inputDto.getWorkflowId()) && !WorkFlowUtil.isNotFromToDoList()){
//	            		EtrInvestInfoDto etrInvestInfoDto = tradeCommonService.findEtrInfo(inputDto.getEntrustId());
	            		TrdBondInvestTradeDto dto = trdBondInvestTrade002Service.getBussinessEntityByWorkflowId(
	            				inputDto.getWorkflowId());
	            		wflPrinAmt = ArithmeticUtil.subtract(wflPrinAmt, dto.getShareAmt());
	                }
	                //如果委托数量 < 流程中委托数量 + 当前代办委托数量  
	                if(entrustNum.compareTo(wflPrinAmt.add(ArithmeticUtil.divide(inputDto.getEntrustNum(),new BigDecimal(100)))) < 0) {
	                	 ValidMessage validMessage = new ValidMessage("tradeAmt",
	                			 "可用委托份额不足,当前最多可委托份额为:"+ entrustNum.subtract(wflPrinAmt).multiply(new BigDecimal(100)));
	                         errorList.add(validMessage);
	
	                //add by wangpeng 20190906 end
                     }
                }
            }

            BaseEtrInvestInfoDto resultDto = trdBondInvestTrade002Service.getEntrustInfo(inputDto.getEntrustId());
            //根据价格模式验证委托
            //限价模式
            if(StringUtil.equals(DictCodeListExt2.价格模式_限价指令.getValue(), resultDto.getEtrEntrust().getPriceModel())){
                if( !StringUtil.isNotEmpty(inputDto.getCalculateField())){
                    inputDto.setCalculateField("price");
                }
            	// 委托场合时且交易方向为买入
                if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), inputDto.getPs())) {
                    // 净价超过委托时的净价;
                    if (StringUtil.equals(inputDto.getCalculateField(),"price") && inputDto.getUnitCprice() != null && inputDto.getEntrustPrice() != null
                        && inputDto.getUnitCprice().compareTo(inputDto.getEntrustPrice()) > 0) {
                        ValidMessage validMessage = new ValidMessage("unitCprice",
                            getMessage("TRD_W_0007", getMessage("TrdBondInvestTrade002.unitCprice"),
                                getMessage("TrdBondInvestTrade002.unitCprice"),
                                getMessage("TrdLendInvestTrade002.morethan")));
                        errorList.add(validMessage);
                    }

                    // 到期收益率低于委托时的到期收益率;
                    if (StringUtil.equals(inputDto.getCalculateField(),"yield") && inputDto.getYield() != null && inputDto.getEntrustYield() != null
                        && inputDto.getYield().compareTo(inputDto.getEntrustYield()) < 0) {
                        ValidMessage validMessage = new ValidMessage("yield",
                            getMessage("TRD_W_0007", getMessage("TrdBondInvestTrade002.yield"),
                                getMessage("TrdBondInvestTrade002.yield"),
                                getMessage("TrdLendInvestTrade002.lessthan")));
                        errorList.add(validMessage);
                    }

                    // 行权收益率低于委托时的行权收益率
                    if (StringUtil.equals(inputDto.getCalculateField(),"exerYield") && inputDto.getExerYield() != null && inputDto.getEntrustExerYield() != null
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
                    if (StringUtil.equals(inputDto.getCalculateField(),"price") && inputDto.getUnitCprice() != null && inputDto.getEntrustPrice() != null
                        && inputDto.getUnitCprice().compareTo(inputDto.getEntrustPrice()) < 0) {
                        ValidMessage validMessage = new ValidMessage("unitCprice",
                            getMessage("TRD_W_0007", getMessage("TrdBondInvestTrade002.unitCprice"),
                                getMessage("TrdBondInvestTrade002.unitCprice"),
                                getMessage("TrdLendInvestTrade002.lessthan")));
                        errorList.add(validMessage);
                    }
                    
                    // 到期收益率超过委托时的到期收益率;
                    if (StringUtil.equals(inputDto.getCalculateField(),"yield") && inputDto.getYield() != null && inputDto.getEntrustYield() != null
                        && inputDto.getYield().compareTo(inputDto.getEntrustYield()) > 0) {
                        ValidMessage validMessage = new ValidMessage("yield",
                            getMessage("TRD_W_0007", getMessage("TrdBondInvestTrade002.yield"),
                                getMessage("TrdBondInvestTrade002.yield"),
                                getMessage("TrdLendInvestTrade002.morethan")));
                        errorList.add(validMessage);
                    }

                    // 行权收益率超过委托时的行权收益率
                    if (StringUtil.equals(inputDto.getCalculateField(),"exerYield") && inputDto.getExerYield() != null && inputDto.getEntrustExerYield() != null
                        && inputDto.getExerYield().compareTo(inputDto.getEntrustExerYield()) > 0) {
                        ValidMessage validMessage = new ValidMessage("exerYield",
                            getMessage("TRD_W_0007", getMessage("TrdBondInvestTrade002.exerYield"),
                                getMessage("TrdBondInvestTrade002.exerYield"),
                                getMessage("TrdLendInvestTrade002.morethan")));
                        errorList.add(validMessage);
                    }
                }
              //指定价格模式软校验
            }
            else if(StringUtil.equals(DictCodeListExt2.价格模式_指定价格.getValue(), resultDto.getEtrEntrust().getPriceModel())){
            		// 净价等于委托时的净价;
            		if (inputDto.getUnitCprice() != null && inputDto.getEntrustPrice() != null
            				&& inputDto.getUnitCprice().compareTo(inputDto.getEntrustPrice()) != 0) {
            			ValidMessage validMessage = new ValidMessage("unitCprice",
                                "价格模式为指定价格，交易均需要按照委托指定的净价交易!");
                            errorList.add(validMessage);
            		}
            	
            }
            //市价模式不验证
            
        }
        // 净价总额+应计利息总额=全价总额的check追加
//        if (StringUtil.isNotEmpty(inputDto.getIntAmt()) && StringUtil.isNotEmpty(inputDto.getCpriceAmt()) && StringUtil.isNotEmpty(inputDto.getFpriceAmt())) {
//	        if (!((inputDto.getIntAmt().add(inputDto.getCpriceAmt())).equals(inputDto.getFpriceAmt()))) {
//	            ValidMessage validMessage = new ValidMessage("fpriceAmt",
//	                getMessage("TRD_E_0004", getMessage("TrdBondInvestTrade002.cpriceAmt"),
//	                    getMessage("TrdBondInvestTrade002.intAmt"),
//	                    getMessage("TrdBondInvestTrade002.fpriceAmt")));
//	            errorList.add(validMessage);
//	        }
//        }

        if(inputDto.getZhilingtype()!= null && "02".equals(inputDto.getZhilingtype())){
        	// 组合分配
        	 List<ValidMessage> ret = new ArrayList<>();
             if (StringUtil.isEmptyList(inputDto.getBussTrdTransfenpeiDto())) {
                 ret.add(new ValidMessage("bussTrdTransfenpeiDto",
                     getMessage("FW_E_0006", "组合分配")));
                 errorList.addAll(ret);
             }else{
            	 for (BussTrdTransfenpeiDto dataDto : inputDto.getBussTrdTransfenpeiDto()) {
            		 // 组合代码
                     if (StringUtil.isBlank(dataDto.getPortfolioId())) {
                    	 ValidMessage validMessage = new ValidMessage("bussTrdTransfenpei_div", getMessage("FW_E_0006",getMessage("TrdInvestFenPeiInfo.busiId")));
                    	 errorList.add(validMessage);
                     }
                     // 券面总额
                     /*if (dataDto.getTradeAmt() != null && dataDto.getTradeAmt().compareTo(BigDecimal.ZERO) <= 0) {
                         ValidMessage validMessage = new ValidMessage(
                                 getMessage("BUSS_COM_E_0036", getMessage("TrdInvestFenPeiInfo.tradeAmt"), "0"));
                             errorList.add(validMessage);
                     }*/
            	 }
             }
        }else{
        	// 净价总额+应计利息总额=全价总额的check追加
        	if(inputDto.getIntAmt() != null && inputDto.getCpriceAmt() != null && inputDto.getFpriceAmt() != null){
        		if (!inputDto.getIntAmt().add(inputDto.getCpriceAmt()).equals(inputDto.getFpriceAmt())) {
                    ValidMessage validMessage = new ValidMessage("fpriceAmt",
                        getMessage("TRD_E_0004", getMessage("TrdBondInvestTrade002.cpriceAmt"),
                            getMessage("TrdBondInvestTrade002.intAmt"),
                            getMessage("TrdBondInvestTrade002.fpriceAmt")));
                    errorList.add(validMessage);
                }
        	}
        }
        //证券托管户 与通道二选一
        if((StringUtil.isBlank(inputDto.getBondFirstSign())) && (inputDto.getChlId()==null||"".equals(inputDto.getChlId()))&&(inputDto.getSecManageAcctId()==null||"".equals(inputDto.getSecManageAcctId()))){
        	 ValidMessage validMessage = new ValidMessage(getMessage("TRD_E_0014_2"));
        	
        	errorList.add(validMessage);
        }

        //add by msk TISHKBS-1038 新增一次性划款通道交易前是否划款判断 start
        if(inputDto.getChlId()!=null&&!"".equals(inputDto.getChlId())){
            Integer count;
            Map<String,Object> sMap= new HashMap<String ,Object>();

            if(StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_买入.getValue())){
                sMap.put("chlId",inputDto.getChlId());
                sMap.put("finprodId",inputDto.getPortfolioId());
                count = trdBondInvestTrade002Service.selectchlTrade(sMap);
                if(count <= 0){
                    ValidMessage validMessage = new ValidMessage(getMessage("通过一次性通道投资请先进行划款！",getMessage("invest.channelId")));
                    errorList.add(validMessage);
                }

            }else if(StringUtil.equals(inputDto.getTrdInvestPersonInfo().getIsOutTrade(), DictCodeList.是否_否.getValue()) &&
                        (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_卖出.getValue()) || StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_行权_回售.getValue()))){
                sMap.put("chlId",inputDto.getChlId());
                sMap.put("counterId",inputDto.getCounterId());
                count = trdBondInvestTrade002Service.selectchlTrade(sMap);
                if(count <= 0){
                    ValidMessage validMessage = new ValidMessage(getMessage("交易对手通过一次性通道投资请先进行划款！",getMessage("invest.channelId"),getMessage("invest.counterId")));
                    errorList.add(validMessage);
                }
            }
        }
        //add by msk TISHKBS-1038 新增一次性划款通道交易前是否划款判断 end

        // add by 裴家俊 2019-05-23--start----
        if(StringUtil.equals(inputDto.getOperateType(), "save") || StringUtil.equals(inputDto.getOperateType(), "submit")){
            if(inputDto.getTrdInvestPersonInfo() != null && StringUtil.isEmpty(inputDto.getTrdInvestPersonInfo().getCounterId()) && !StringUtil.equals(DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue(), inputDto.getWindtypeone())){
                ValidMessage validMessage = new ValidMessage("trdInvestPersonInfo.counterId",getMessage("FW_E_0006", getMessage("TrdDepositInvestTrade002.cno")));
                errorList.add(validMessage);
            }
        }
        // add by 裴家俊 2019-05-23--end----

        //卖出交易内部交易对手要生成反向买入交易，所以内部交易对手的证券账户要和卖出交易的组合的证券账户一致
        if (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_卖出.getValue()) && 
                StringUtil.equals(inputDto.getTrdInvestPersonInfo().getIsOutTrade(), DictCodeList.是否_否.getValue())){
            Map<String, Object> param = new HashMap<>();
            param.put("portfolioId", inputDto.getTrdInvestPersonInfo().getCounterId());
            param.put("zhilingtype", inputDto.getZhilingtype());   
            param.put("search", inputDto.getSecManageAcctId());            ;
           if (StringUtil.isEmpty(inputDto.getChlId()) && StringUtil.isEmptyList(trdBondInvestTrade002Service.getSecManageAcctId(param))){
               ValidMessage validMessage = new ValidMessage("trdInvestPersonInfo.counterId", "卖出交易的内部交易对手证券托管户不存在:" + inputDto.getSecManageAcctId());
               errorList.add(validMessage);
           }
        }
        
      //内转交易  内转投资目的必填。
        if ((StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_卖出.getValue())||StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_买入.getValue())) && 
                StringUtil.equals(inputDto.getTrdInvestPersonInfo().getIsOutTrade(), DictCodeList.是否_否.getValue())){
        	//买入交易为自动生成   不可以页面提交，此处不校验，共通check会校验 
        	if (DictCodeList.是否_是.getValue().equals(CacheUtil.getSysParam(BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL))&&StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_买入.getValue())) {
             	
        	}else{
        		if(StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_卖出.getValue()) &&
                        StringUtil.isEmpty(inputDto.getTurnInvAim()) &&
                        DictCodeList.是否_是.getValue().equals(CacheUtil.getSysParam(BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL))){
             		ValidMessage validMessage = new ValidMessage("turnInvAim", "转仓投资目的必填！");
                    errorList.add(validMessage);
             	}
        	}
        }

        if (StringUtil.isNotEmpty(inputDto.getBussTrdTransfenpeiDto())){
            Map<String, List<BussTrdTransfenpeiDto>> map = inputDto.getBussTrdTransfenpeiDto().stream().collect(JoyinCollectors.groupingBy(item->item.getPortfolioId()));
            if (map!=null && map.keySet().size()<inputDto.getBussTrdTransfenpeiDto().size()){
                errorList.add(new ValidMessage("组合分配不能重复分配给同一组合。"));
            }
        }

		if(StringUtil.isNotEmpty(inputDto.getfTradeId())){//成交编号不为空 校验
        	Map<String,Object> map = new HashMap<>();
        	map.put("fTradeId", inputDto.getfTradeId());
        	//入库时的费用
        	InterfTradSecdeal interfTradSecdeal = trdBondInvestTrade002Service.getInterfTradSecdeal(map);
        	BigDecimal dealFeeAmt  = StringUtil.isNotEmpty(interfTradSecdeal.getDealFeeamt())?interfTradSecdeal.getDealFeeamt(): BigDecimal.ZERO;
        	BigDecimal settFeeAmt  = StringUtil.isNotEmpty(interfTradSecdeal.getSettFeeamt())?interfTradSecdeal.getSettFeeamt(): BigDecimal.ZERO;
        	
        	//所有已经使用的费用
        	List<TrdTradeFeeDetail> allfee = trdBondInvestTrade002Service.getAllFeeByFTradeId(map);
        	BigDecimal dealFeeAmtSum  = BigDecimal.ZERO;
        	BigDecimal settFeeAmtSum  = BigDecimal.ZERO;
        	for(TrdTradeFeeDetail trdTradeFeeDetail:allfee){
        		if(StringUtil.equals(trdTradeFeeDetail.getFeeType(), BussinessDictCodeList.费用类型_交易手续费.getValue())){
        			dealFeeAmtSum =ArithmeticUtil.doAdd(trdTradeFeeDetail.getFeeAmt(),dealFeeAmtSum);
            	}
        		if(StringUtil.equals(trdTradeFeeDetail.getFeeType(), BussinessDictCodeList.费用类型_结算手续费.getValue())){
        			settFeeAmtSum = ArithmeticUtil.doAdd(trdTradeFeeDetail.getFeeAmt(),settFeeAmtSum);
            	}
        	}
        	if(ArithmeticUtil.doAdd(dealFeeAmtSum,inputDto.getProcedureAmt()).compareTo(dealFeeAmt)>0){
    			ValidMessage validMessage = new ValidMessage("procedureAmt", "当前交易交易手续费与已完成交易已超过当前下行成交数据交易手续费！");
                errorList.add(validMessage);
    		}
        	if(ArithmeticUtil.doAdd(settFeeAmtSum,inputDto.getSettleAmt()).compareTo(settFeeAmt)>0){
				ValidMessage validMessage = new ValidMessage("settleAmt", "当前交易结算手续费与已完成交易已超过当前下行成交数据结算手续费！");
                errorList.add(validMessage);
    		}
        }
        if(StringUtil.isNotEmpty(inputDto.getfTradeId())&&null!=inputDto.getTradeAmt()){
            Map<String,Object> map = new HashMap<>();
            map.put("fTradeId",inputDto.getfTradeId());
            map.put("tradeAmt",inputDto.getTradeAmt());
            map.put("workflowId",inputDto.getWorkflowId());
            if(!trdBondInvestTrade002Service.isAllTradeAmtByFTradeId(map)){
                ValidMessage validMessage = new ValidMessage("tradeAmt", "券面总额不可大于该成交数据中未录入交易的券面总额！");
                errorList.add(validMessage);
            }
        }
        return errorList;
    }

    private void checkSellPositionFenpei(TrdBondInvestTradeDto inputDto, BussTrdTransfenpeiDto fenpeiDto,
                                         List<ValidMessage> errorList) {
        if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), inputDto.getPs())) {
            // 交易方向为卖出时，券面总额不大于资产可用持仓，本方券券面总额不大于本方券可用持仓，融入券券面总额不大于融入券可用持仓
            if (fenpeiDto.getOwnTradeAmt() != null && fenpeiDto.getAssetOwnAvailablePosition() != null
                && fenpeiDto.getOwnTradeAmt().compareTo(fenpeiDto.getAssetOwnAvailablePosition()) > 0) {
                errorList.add(new ValidMessage("本方券券面总额不大于本方券可用持仓"));
            }
            if (fenpeiDto.getIntegrateTradeAmt() != null && fenpeiDto.getAssetIntegrateAvailablePosition() != null
                && fenpeiDto.getIntegrateTradeAmt().compareTo(fenpeiDto.getAssetIntegrateAvailablePosition()) > 0) {
                errorList.add(new ValidMessage("融入券券面总额不大于融入券可用持仓"));
            }
        }
        else if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), inputDto.getPs())) {
            // 交易方向为买入时，卖空购回券面总额不能大于融入券卖空可用持仓；
            if (fenpeiDto.getIntegrateTradeAmt() != null && fenpeiDto.getAssetIntegrateSaleAvailablePosition() != null
                && fenpeiDto.getIntegrateTradeAmt().compareTo(fenpeiDto.getAssetIntegrateSaleAvailablePosition()) > 0) {
                errorList.add(new ValidMessage("卖空购回券面总额不能大于融入券卖空可用持仓"));
            }
        }
    }

    private void checkSellPosition(TrdBondInvestTradeDto inputDto, List<ValidMessage> errorList) {
        if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), inputDto.getPs())) {
            // 交易方向为卖出时，券面总额不大于资产可用持仓，本方券券面总额不大于本方券可用持仓，融入券券面总额不大于融入券可用持仓
            if (inputDto.getOwnTradeAmt() != null && inputDto.getAssetOwnAvailablePosition() != null
                && inputDto.getOwnTradeAmt().compareTo(inputDto.getAssetOwnAvailablePosition()) > 0) {
                errorList.add(new ValidMessage("本方券券面总额不大于本方券可用持仓"));
            }
            if (inputDto.getIntegrateTradeAmt() != null && inputDto.getAssetIntegrateAvailablePosition() != null
                && inputDto.getIntegrateTradeAmt().compareTo(inputDto.getAssetIntegrateAvailablePosition()) > 0) {
                errorList.add(new ValidMessage("融入券券面总额不大于融入券可用持仓"));
            }
        }
        else if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), inputDto.getPs())) {
            // 交易方向为买入时，卖空购回券面总额不能大于融入券卖空可用持仓；
            if (inputDto.getIntegrateTradeAmt() != null && inputDto.getAssetIntegrateSaleAvailablePosition() != null
                && inputDto.getIntegrateTradeAmt().compareTo(inputDto.getAssetIntegrateSaleAvailablePosition()) > 0) {
                errorList.add(new ValidMessage("卖空购回券面总额不能大于融入券卖空可用持仓"));
            }
        }
    }

}
