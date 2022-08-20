package com.joyintech.tams.invest.basicdata.mstcommissionset.validator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.MstCustomerInfoMapper;
import com.joyintech.tams.common.base.entity.MstCustomerInfo;
import com.joyintech.tams.common.base.entity.TrdFeeConfigDetail;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.basicdata.mstcommissionset.dto.MstCommissionSetDto;
import com.joyintech.tams.invest.basicdata.mstcommissionset.service.MstCommissionSetService;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;

/**
 * 佣金设置验证
 *
 * @author 裴家俊
 * @version 1.0 2019年3月22日
 * @see MstCommissionSetValidator
 * @since 1.0
 */
@Component
public class MstCommissionSetValidator implements BeanValidator {
    public static final String TRADE_TYPE = "tradeType";
    public static final String FEE_TYPE = "feeType";
    public static final String TRADE_MARKET = "tradeMarket";
    public static final String ASSET_CLASSIFY_1 = "assetClassify1";
    public static final String ASSET_CLASSIFY_2 = "assetClassify2";
    public static final String TRADE_MARKET_MODE = "tradeMarketMode";
    public static final String EFF_DATE = "effDate";
    public static final String PS = "ps";
    public static final String SEC_ACCT_ID = "secAcctId";
    public static final String TERM_TYPE = "termType";
    public static final String CUSTOMER_ID = "customerId";

    /**
     * 共通Mapper的通用代理方案
     */

    /**
     * MstCommissionSetService
     */
    @Autowired
    private MstCommissionSetService mstCommissionSetService;

    /**
     * add by hudongyang 2020-08-05
     */
    @Autowired
    private MstCustomerInfoMapper mstCustomerInfoMapper;

    @Override
    public List<ValidMessage> valid(Object value) {
        MstCommissionSetDto dto = (MstCommissionSetDto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            if (CollectionUtils.isNotEmpty(dto.getSelectedItem())) {
                dto.getSelectedItem().forEach(item -> {
                    if (mstCommissionSetService.checkBusinessDataInFlow(item)) {
                        resultList.add(new ValidMessage(
                                this.getMessage("COMMON_E_0012", item.getBatchSeq())));
                    }
                    if (!StringUtil.isNotEmpty(
                            mstCommissionSetService.getBussinessEntityBySeqNo(item.getBatchSeq()))) {
                        resultList.add(new ValidMessage(this.getMessage("COMMON_E_0001")));
                    }
                });
                return resultList;
            }

        }
        else {
            if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)
                    || StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)) {
                //add by hudongyang 2020-08-04 start
                String assetClassify1 = dto.getAssetClassify1();
                if(StringUtil.isBlank(assetClassify1)){
                    ValidMessage validMessage = new ValidMessage("assetClassify1",
                            getMessage("FW_E_0006", getMessage("MstCommissionSet002.assetClassify1")));
                    resultList.add(validMessage);
                }
                String assetClassify2 = dto.getAssetClassify2();
                if(StringUtil.isBlank(assetClassify2)){
                    ValidMessage validMessage = new ValidMessage("assetClassify2",
                            getMessage("FW_E_0006", getMessage("MstCommissionSet002.assetClassify2")));
                    resultList.add(validMessage);
                }
                if(dto.getRetentionBit() == null){
                    ValidMessage validMessage = new ValidMessage("retentionBit",
                            getMessage("FW_E_0006", getMessage("MstCommissionSet002.retentionBit")));
                    resultList.add(validMessage);
                }
                if (StringUtil.isBlank(dto.getChargeBasis())) {
                    ValidMessage validMessage = new ValidMessage("chargeBasis", getMessage(
                            "FW_E_0006", getMessage("MstCommissionSet002.chargeBasis")));
                    resultList.add(validMessage);
                }
                if (StringUtil.isEmpty(dto.getRemark())) {
                    ValidMessage validMessage = new ValidMessage("remark", getMessage(
                            "FW_E_0006", getMessage("MstCommissionSet002.remark")));
                    resultList.add(validMessage);
                }
                //add by hudongyang 2020-08-04 end
                // add by wangguoxian 当费用类型选择为佣金时，证券账户必填 20190717 start
                //delete by hudongyang 2020-08-04 start
//                if (StringUtil.equals(BussinessDictCodeList.费用类型_佣金.getValue(), dto.getFeeType())
//                        && StringUtil.isBlank(dto.getSecAcctId())) {
//                    ValidMessage validMessage = new ValidMessage("secAcctId",
//                            getMessage("FW_E_0006", getMessage("MstCommissionSet002.secAcct")));
//                    resultList.add(validMessage);
//                }
                //delete by hudongyang 2020-08-04 end
                // add by wangguoxian 当费用类型选择为佣金时，证券账户必填 20190717 end
                // add by wangguoxian 当费用类型选择为佣金时，是否随交易支付必填 20190717 start
                if (StringUtil.equals(BussinessDictCodeList.费用类型_佣金.getValue(), dto.getFeeType())
                        && StringUtil.isBlank(dto.getIsPayWithTrade())) {
                    ValidMessage validMessage = new ValidMessage("isPayWithTrade",
                            getMessage("FW_E_0006", getMessage("MstCommissionSet002.isPayWithTrade")));
                    resultList.add(validMessage);
                }
                // add by wangguoxian 当费用类型选择为佣金时，是否随交易支付必填 20190717 end
                // 交易品种选择质押式回购和买断式回购时，期限品种必填
                String tradeType = dto.getTradeType();
                String termType = dto.getTermType();
                if (StringUtil.equals(BussinessDictCodeList.平安资产类型_回购.getValue(),
                        tradeType)) {
                    if (StringUtil.isBlank(termType)) {
                        ValidMessage validMessage = new ValidMessage("termType",
                                getMessage("FW_E_0006", getMessage("MstCommissionSet002.termType")));
                        resultList.add(validMessage);
                    }
                }

                 //计费类型选择固定费率时，计数基数必输
                if(StringUtil.equals(BussinessDictCodeList.是否_否.getValue(),dto.getIsGrade())){
                    if (StringUtil.equals(BussinessDictCodeList.计费类型_固定费率.getValue(),
                            dto.getChargeType())) {
                        if (!StringUtil.isNotEmpty(dto.getChargeRate())) {
                            ValidMessage validMessage = new ValidMessage("chargeRate",
                                    getMessage("FW_E_0006", getMessage("MstCommissionSet002.chargeRate")));
                            resultList.add(validMessage);
                        }
                    }
                    else {
                        if (!StringUtil.isNotEmpty(dto.getChargeValue())) {
                            ValidMessage validMessage = new ValidMessage("chargeValue", getMessage(
                                    "FW_E_0006", getMessage("MstCommissionSet002.chargeValue")));
                            resultList.add(validMessage);
                        }
                    }
                    if (null != dto.getChargeValue()
                            && dto.getChargeValue().compareTo(BigDecimal.ZERO) == -1) {
                        resultList.add(
                                new ValidMessage("chargeValue", this.getMessage("BUSS_COM_E_0004",
                                        this.getMessage("MstCommissionSet002.chargeValue"), "0")));
                    }
                    if (null != dto.getChargeRate()
                            && dto.getChargeRate().compareTo(BigDecimal.ZERO) == -1) {
                        resultList.add(
                                new ValidMessage("chargeRate", this.getMessage("BUSS_COM_E_0004",
                                        this.getMessage("MstCommissionSet002.chargeRate"), "0")));
                    }
                    if (null != dto.getFeeMini()
                            && dto.getFeeMini().compareTo(BigDecimal.ZERO) == -1) {
                        resultList.add(new ValidMessage("feeMini", this.getMessage("BUSS_COM_E_0004",
                                this.getMessage("MstCommissionSet002.feeMini"), "0")));
                    }
                    if (null != dto.getFeeMaxi()
                            && dto.getFeeMaxi().compareTo(BigDecimal.ZERO) == -1) {
                        resultList.add(new ValidMessage("feeMaxi", this.getMessage("BUSS_COM_E_0004",
                                this.getMessage("MstCommissionSet002.feeMaxi"), "0")));
                    }
                    // 判断最小金额不能大于最大金额
                    if (StringUtil.isNotEmpty(dto.getFeeMini())
                            && StringUtil.isNotEmpty(dto.getFeeMaxi())) {
                        if (ArithmeticUtil.doSub(dto.getFeeMini(), dto.getFeeMaxi()).compareTo(
                                BigDecimal.ZERO) > 0) {
                            resultList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0003",
                                    getMessage("MstCommissionSet002.feeMini"),
                                    getMessage("MstCommissionSet002.feeMaxi"))));
                        }

                    }
                }

                // modify by swwan 2022-06-27 start TISHKS-1448
                // 同一生效日、同一个费用类型、证券账户（佣金）、交易场所、交易市场、交易方向、期限品种（若有）、交易品种、资产分类（若有）的费用规则不能重复，
                // 若存在，则不允许提交，并给出提示：
                // “XX（交易场所）XX（证券账户）XX（交易市场）XX（交易品种）XX（资产分类，若有）XX（期限品种，若有）XX（交易方向）**(生效日)交易费用已设置，请重新选择！
                //add by hudongyang 2020-08-05 start
                /*Map<String,Object> map = new HashMap<>();
                map.put("batchSeq",StringUtil.equals(dto.getPageModel(),
                        GlobalConstant.DETAIL_PAGE_MODEL_NEW) ? StringUtil.EMPTY : dto.getBatchSeq());
                map.put("tradeType",dto.getTradeType());//交易品种
                map.put("feeType",dto.getFeeType());//交易费用
                map.put("tradeMarket",dto.getTradeMarket());//交易场所
                MstCustomerInfo mstCustomerInfo = mstCustomerInfoMapper.selectByPrimaryKey(dto.getCustomerId());
                map.put("customerId",dto.getCustomerId());//券商代码
                map.put("assetClassify1",dto.getAssetClassify1());//资产分类一
                map.put("assetClassify2",dto.getAssetClassify2());//资产分类二
                map.put("tradePlat",dto.getTradePlat());//交易平台
                String effDateStr = DateUtil.format(dto.getEffDate(),"yyyy-MM-dd HH:mm:ss");
                map.put("effDate", effDateStr);
                map.put("ps", dto.getPs());
                int count = mstCommissionSetService.searchRepeatBySecPs(map);
                if (count > 0) {
                    String psStr = getPsStr(dto.getPs());
                    resultList.add(new ValidMessage(getMessage("MST_INFO_E_0009",
                            StringUtil.isNotEmpty(mstCustomerInfo) ? mstCustomerInfo.getCustomerName() : dto.getCustomerId(),
                            SysDataUtil.getNameFromDataDict(DictCode.授信资产类型,dto.getTradeType()),//交易品种
                            SysDataUtil.getNameFromDataDict(BussinessDictCode.费用类型,dto.getFeeType()),//交易费用
                            psStr,//交易方式
                            SysDataUtil.getNameFromDataDict(BussinessDictCode.交易场所,dto.getTradeMarket()),//交易场所
                            SysDataUtil.getNameFromDataDict(DictCode.授信资产二级分类,dto.getAssetClassify1()),//资产分类一
                            SysDataUtil.getNameFromDataDict(DictCode.授信资产三级分类,dto.getAssetClassify2()),//资产分类二
                            SysDataUtil.getNameFromDataDict(DictCode.交易平台,dto.getTradePlat()),//交易平台
                            effDateStr)));
                }*/
                HashMap<String, Object> map = Maps.newHashMap();
                map.put("batchSeq",StringUtil.equals(dto.getPageModel(),
                        GlobalConstant.DETAIL_PAGE_MODEL_NEW) ? StringUtil.EMPTY : dto.getBatchSeq());
                // 交易品种
                map.put(TRADE_TYPE, tradeType);
                // 费用类型
                String feeType = dto.getFeeType();
                map.put(FEE_TYPE, feeType);
                // 交易场所
                String tradeMarket = dto.getTradeMarket();
                map.put(TRADE_MARKET, tradeMarket);
                // 资产分类
                map.put(ASSET_CLASSIFY_1, assetClassify1);
                map.put(ASSET_CLASSIFY_2, assetClassify2);
                // 交易市场
                String tradeMarketMode = dto.getTradeMarketMode();
                map.put(TRADE_MARKET_MODE, tradeMarketMode);
                // 生效日
                String effDate = DateUtil.format(dto.getEffDate(), "yyyy-MM-dd HH:mm:ss");
                map.put(EFF_DATE, effDate);
                // 交易方向
                String ps = dto.getPs();
                map.put(PS, ps);
                // 证券账户
                String secAcctId = dto.getSecAcctId();
                map.put(SEC_ACCT_ID, secAcctId);
                // 期限品种
                map.put(TERM_TYPE, termType);
                // 券商代码
                String customerId = dto.getCustomerId();
                map.put(CUSTOMER_ID, customerId);
                int count = mstCommissionSetService.searchRepeatBySecPs(map);
                if (count > 0) {
                    StringBuffer sb = new StringBuffer();
                    if (StringUtil.isNotEmpty(tradeMarket) && StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(BussinessDictCode.交易场所, tradeMarket))) {
                        sb.append(SysDataUtil.getNameFromDataDict(BussinessDictCode.交易场所, tradeMarket)).append("(交易场所)");
                    }
                    if (StringUtil.isNotEmpty(secAcctId)) {
                        sb.append(secAcctId).append("(证券账户)");
                    }
                    if (StringUtil.isNotEmpty(tradeMarketMode) && StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易市场, tradeMarketMode))) {
                        sb.append(StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易市场, tradeMarketMode))).append("(交易市场)");
                    }
                    sb.append(SysDataUtil.getNameFromDataDict(DictCode.授信资产类型, tradeType)).append("(交易品种)");
                    if (StringUtil.isNotEmpty(assetClassify1) && StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易所费用资产分类一, assetClassify1))) {
                        sb.append(SysDataUtil.getNameFromDataDict(DictCode.交易所费用资产分类一, assetClassify1)).append("(资产分类一)");
                    }
                    if (StringUtil.isNotEmpty(assetClassify2) && StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易所费用资产分类二, assetClassify2))) {
                        sb.append(SysDataUtil.getNameFromDataDict(DictCode.交易所费用资产分类二, assetClassify2)).append("(资产分类二)");
                    }
                    if (StringUtil.isNotEmpty(termType) && StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.回购期限品种, termType))) {
                        sb.append(SysDataUtil.getNameFromDataDict(DictCode.回购期限品种, termType)).append("(期限品种)");
                    }
                    if (StringUtil.isNotEmpty(customerId)) {
                        sb.append(customerId).append("(券商代码)");
                    }
                    sb.append(getPsStr(ps)).append("(交易方向)");
                    sb.append(DateUtil.format(dto.getEffDate())).append("(生效日)");
                    resultList.add(new ValidMessage(getMessage("MST_INFO_E_0010", sb.toString())));
                }
                //add by hudongyang 2020-08-05 end
                // modify by swwan 2022-06-27 end
                
                // 费用分档check
                if(StringUtil.equals(BussinessDictCodeList.是否_是.getValue(),dto.getIsGrade())) {
                	List<TrdFeeConfigDetail> trdFeeConfigDetailList = dto.getTrdFeeConfigDetailList();
                	if (trdFeeConfigDetailList != null && trdFeeConfigDetailList.size() > 0) {
                		for (TrdFeeConfigDetail trdFeeConfigDetail : trdFeeConfigDetailList) {
                			// 分档条件未输入报错
                			if (!StringUtil.isNotEmpty(trdFeeConfigDetail.getGradeCondition())) {
                				resultList.add(new ValidMessage(this.getMessage("FW_E_0006",
                                        getMessage("MstCommissionSet002.gradeCondition"))));
                			}
                			// 分档金额未输入报错
                			if (!StringUtil.isNotEmpty(trdFeeConfigDetail.getGradeAmt())) {
                				resultList.add(new ValidMessage(this.getMessage("FW_E_0006",
                                        getMessage("MstCommissionSet002.gradeAmt"))));
                			}
                			// 固定金额且费用值未输入或者费用值输入位数大于10位报错
                			if (StringUtil.equals(BussinessDictCodeList.计费类型_固定金额.getValue(), dto.getChargeType())) {
                				if (!StringUtil.isNotEmpty(trdFeeConfigDetail.getChargeValue())) {
	                				resultList.add(new ValidMessage(this.getMessage("FW_E_0006",
	                                        getMessage("MstCommissionSet002.chargeValue"))));
                				}
                				// 费用值输入位数大于10位报错
                    			if (trdFeeConfigDetail.getChargeValue() != null && String.valueOf(trdFeeConfigDetail.getChargeValue()).length() > 10) {
                    				resultList.add(new ValidMessage(this.getMessage("FW_E_0007",
                                            getMessage("MstCommissionSet002.chargeValue"),
                                            "10")));
                    			}
                    			if (StringUtil.isNotEmpty(trdFeeConfigDetail.getChargeRate())) {
	                				resultList.add(new ValidMessage(this.getMessage("MST_COMMISSION_SET_E002")));
                				}
                			}
                			// 固定费率且费用率未输入或者费率输入位数大于10位报错
                			if (StringUtil.equals(BussinessDictCodeList.计费类型_固定费率.getValue(), dto.getChargeType())) {
                				if (!StringUtil.isNotEmpty(trdFeeConfigDetail.getChargeRate())) {
	                				resultList.add(new ValidMessage(this.getMessage("FW_E_0006",
	                                        getMessage("MstCommissionSet002.chargeRate"))));
                				}
                				// 费率输入位数大于10位报错
                    			if (trdFeeConfigDetail.getChargeRate() != null && String.valueOf(trdFeeConfigDetail.getChargeRate()).length() > 10) {
                    				resultList.add(new ValidMessage(this.getMessage("FW_E_0007",
                                            getMessage("MstCommissionSet002.chargeRate"),
                                            "10")));
                    			}
                    			if (StringUtil.isNotEmpty(trdFeeConfigDetail.getChargeValue())) {
	                				resultList.add(new ValidMessage(this.getMessage("MST_COMMISSION_SET_E003")));
                				}
                			}
                			// 分档金额输入位数大于10位报错
                			if (trdFeeConfigDetail.getGradeAmt() != null && String.valueOf(trdFeeConfigDetail.getGradeAmt()).length() > 10) {
                				resultList.add(new ValidMessage(this.getMessage("FW_E_0007",
                                        getMessage("MstCommissionSet002.gradeAmt"),
                                        "10")));
                			}
                			
                			// 最大值输入位数大于10位报错
                			if (trdFeeConfigDetail.getFeeMaxi() != null && String.valueOf(trdFeeConfigDetail.getFeeMaxi()).length() > 10) {
                				resultList.add(new ValidMessage(this.getMessage("FW_E_0007",
                                        getMessage("MstCommissionSet002.feeMaxi"),
                                        "10")));
                			}
                			// 最小值输入位数大于10位报错
                			if (trdFeeConfigDetail.getFeeMini() != null && String.valueOf(trdFeeConfigDetail.getFeeMini()).length() > 10) {
                				resultList.add(new ValidMessage(this.getMessage("FW_E_0007",
                                        getMessage("MstCommissionSet002.feeMini"),
                                        "10")));
                			}
                			// 最小值大于最大值时报错
                			if (trdFeeConfigDetail.getFeeMaxi() != null && trdFeeConfigDetail.getFeeMini() != null && 
                					trdFeeConfigDetail.getFeeMaxi().compareTo(trdFeeConfigDetail.getFeeMini()) < 0) {
                				resultList.add(new ValidMessage(this.getMessage("FW_E_0009",
                                        getMessage("MstCommissionSet002.feeMini"),
                                        getMessage("MstCommissionSet002.feeMaxi"))));
                			}
                		}
                	}
                }
                
                // 费用分档连续性check
                if(StringUtil.equals(BussinessDictCodeList.是否_是.getValue(),dto.getIsGrade())) {
                	List<TrdFeeConfigDetail> trdFeeConfigDetailList = dto.getTrdFeeConfigDetailList();
                	if (trdFeeConfigDetailList != null && trdFeeConfigDetailList.size() > 0) {
                		for (int i = 0; i < trdFeeConfigDetailList.size(); i++) {
                			// 不是最后一行并且分档条件为大于报错
                			if (i != trdFeeConfigDetailList.size() - 1 && StringUtil.equals(trdFeeConfigDetailList.get(i).getGradeCondition(), 
                					DictCodeListExt3.分档条件_大于.getValue())) {
                				resultList.add(new ValidMessage(this.getMessage("MST_COMMISSION_SET_E001")));
                				break;
                			}
                			// 是最后一行并且分档条件不是大于报错
                			if (i == trdFeeConfigDetailList.size() - 1 && !StringUtil.equals(trdFeeConfigDetailList.get(i).getGradeCondition(), 
                					DictCodeListExt3.分档条件_大于.getValue())) {
                				resultList.add(new ValidMessage(this.getMessage("MST_COMMISSION_SET_E001")));
                				break;
                			}
                			// 比下一行分档金额大报错
                			if ((i + 1) < (trdFeeConfigDetailList.size() - 1)) {
	                			if (trdFeeConfigDetailList.get(i).getGradeAmt() != null && trdFeeConfigDetailList.get(i + 1).getGradeAmt() != null &&
	                					trdFeeConfigDetailList.get(i).getGradeAmt().compareTo(trdFeeConfigDetailList.get(i + 1).getGradeAmt()) >= 0) {
	                				resultList.add(new ValidMessage(this.getMessage("MST_COMMISSION_SET_E001")));
	                				break;
	                			}
                			}
                			else {
                				if (i < trdFeeConfigDetailList.size() - 1) {
	                				if (trdFeeConfigDetailList.get(i).getGradeAmt() != null && trdFeeConfigDetailList.get(i + 1).getGradeAmt() != null &&
		                					trdFeeConfigDetailList.get(i).getGradeAmt().compareTo(trdFeeConfigDetailList.get(i + 1).getGradeAmt()) != 0) {
		                				resultList.add(new ValidMessage(this.getMessage("MST_COMMISSION_SET_E001")));
		                				break;
		                			}
                				}
                			}
                		}
                	}
                }
                
                // 交易费用为佣金时佣金计算方式必须输入
                if(StringUtil.equals(BussinessDictCodeList.费用类型_佣金.getValue(),dto.getFeeType())) {
                	if (!StringUtil.isNotEmpty(dto.getCommCalMethod())) {
                        ValidMessage validMessage = new ValidMessage("commCalMethod",
                                getMessage("FW_E_0006", getMessage("MstCommissionSet002.commCalMethod")));
                        resultList.add(validMessage);
                    }
                }
                
                // 交易品种为质押式回购时，佣金计算方式为按证券代码汇总的话，报错
                if (StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), tradeType) &&
                	StringUtil.equals(DictCodeListExt3.佣金计算方式_按证券代码汇总.getValue(), dto.getCommCalMethod())) {
                	ValidMessage validMessage = new ValidMessage("commCalMethod",
                            getMessage("MST_COMMISSION_SET_E004"));
                    resultList.add(validMessage);
                }
            }
        }
        return resultList;
    }

    // add by wangguoxian 20190718 start
    /**
     *
     * 获取交易方向名称
     * @param ps 交易方向
     * @return 交易方向名称
     * @see
     * @since 1.0
     */
    private String getPsStr(String ps) {
        String psStr = StringUtil.EMPTY;
        if (ps.startsWith("T")) {
            psStr = SysDataUtil.getNameFromDataDict(BussinessDictCode.交易方向,
                    ps);
        }
        else {
            psStr = SysDataUtil.getNameFromDataDict(
                    BussinessDictCode.回购拆借方向, ps);
        }
        return psStr;
    }
    // add by wangguoxian 20190718 end

}
