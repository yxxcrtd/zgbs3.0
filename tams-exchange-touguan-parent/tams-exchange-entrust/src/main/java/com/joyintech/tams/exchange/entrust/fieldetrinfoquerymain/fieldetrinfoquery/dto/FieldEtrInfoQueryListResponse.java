/*
 * 文件名：BussCombDistribtDto.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 场内指令分发
 * 指令列表分页查询DTO
 * @author msk
 * @version 1.0 2021年12月22日
 * @see FieldEtrInfoQueryListResponse
 * @since 1.0
 */
@ApiModel(value="场内指令分发返回数据",description="场内指令分发返回数据")
public class FieldEtrInfoQueryListResponse extends EtrEntrust {

    @ApiModelProperty(name="portfolioName",value="组合名称",example="字符串1")
    private String portfolioName;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="字符串1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="字符串1")
    private String finprodAbbr;

    @ApiModelProperty(name="otherPs",value="交易方向",example="买入")
    private String otherPs;

    @ApiModelProperty(name="targetType",value="目标类型",example="A1")
    private String targetType;

    @ApiModelProperty(name="busiPs",value="回购方向",example="回购/拆借方向")
    private String busiPs;

    @ApiModelProperty(name="finishAmt",value="完成额度",example="30%")
    private BigDecimal finishAmt;

    @ApiModelProperty(name="trader",value="交易员",example="admin")
    private String trader;

    @ApiModelProperty(name="secAcctId",value="证券账户",example="XXX证券账户")
    private String secAcctId;

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="字符串1")
    private String settleSpeed;

    @ApiModelProperty(name="settleSpeed",value="期限（天）",example="30")
    private String termDays;

    @ApiModelProperty(name="effectFlag",value="有效状态code",example="01")
    private String effectFlag;

    @ApiModelProperty(name="etrPrice",value="指令价格",example="99")
    private String etrPrice;

    @ApiModelProperty(name="exeProgress",value="执行进度%",example="50%")
    private BigDecimal exeProgress;

    @ApiModelProperty(name="dealProgress",value="成交进度%",example="50%")
    private BigDecimal dealProgress;

    @ApiModelProperty(name="busiTypePosition",value="头寸类型",example="01")
    private String busiTypePosition;

    @ApiModelProperty(name="execFinishAmt",value="已完成指令额度",example="1")
    private BigDecimal execFinishAmt;

    @ApiModelProperty(name="execRemainderAmt",value="剩余指令额度",example="1")
    private BigDecimal execRemainderAmt;

    @ApiModelProperty(name="transFinishAmt",value="已成交额度",example="1")
    private BigDecimal transFinishAmt;

    @ApiModelProperty(name="transRemainderAmt",value="剩余成交额度",example="1")
    private BigDecimal transRemainderAmt;

    @ApiModelProperty(name="transmitTime",value="下达时间",example="HH:MM:SS")
    private String transmitTime;

    @ApiModelProperty(name="invManagerName",value="投资经理",example="A1")
    private String invManagerName;

    @ApiModelProperty(name="calculateField",value="计算方式",example="计算方式供前台判断使用")
    private String calculateField;

    @ApiModelProperty(name="trdAcctId",value="资金账户",example="XXX资金账户")
    private String trdAcctId;

    @ApiModelProperty(name="assetType",value="平安资产类型",example="债券,质押式回购")
    private String assetType;

    @ApiModelProperty(name="assetType1",value="资产一级类型",example="质押式协议回购")
    private String assetType1;

    @ApiModelProperty(name = "bidModle", value = "投标方式")
    private String bidModle;

    @ApiModelProperty(name = "isWin", value = "中标结果")
    private String isWin;


    /**
     * 获取 otherEntrustStatus
     * @return otherEntrustStatus.
     */
    public String getOtherEntrustStatus() {
        return SysDataUtil.getNameFromDataDict(DictCode.委托状态, super.getEntrustStatus());
    }

    public String getOtherEntrustDate() {
        return DateUtil.format(super.getEntrustDate());
    }

    /**
     * 获取 otherSettleSpeed
     * @return otherSettleSpeed.
     */
    public String getOtherSettleSpeed() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, this.getSettleSpeed());
    }

    /**
     * 获取资产类型 otherFinprodType2
     * @return otherFinprodType2 资产类型对应name
     */
    public String getOtherFinprodType2() {
        if (StringUtil.equals(super.getFinprodType2(), BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue())){
            return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, BussinessDictCodeList.平安资产类型_债券.getValue());
        }
        else if (StringUtil.equals(getAssetType(), BussinessDictCodeList.平安资产类型_回购.getValue())
                && StringUtil.equals(getAssetType1(), DictCodeList.回购类型_质押式协议回购.getValue())) {
            return BussinessConstant.NEGOTIATED_REPURCHASE_TEXT;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, getAssetType());
    }

    /**
     * 获取 otherTradeMarket
     * @return otherTradeMarket.
     */
    public String getOtherTradeMarket() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }

    /**
     * 获取 otherTradeMarket
     * @return otherTradeMarket.
     */
    public String getOtherEffectFlag() {
        return SysDataUtil.getNameFromDataDict(DictCode.有效状态, this.getEffectFlag());
    }

    /**
     * 获取 otherCcy
     * @return otherCcy.
     */
    public String getOtherCcy() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());
    }

    /**
     * 获取 priceModel
     * @return priceModel.
     */
    public String getOtherPriceModel() {
        if (StringUtil.equals(this.getBusiTypePosition(),DictCodeListExt3.投管业务类型_质押券出入库.getValue())){
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.价格模式, super.getPriceModel());
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getOtherPs() {
        if(StringUtils.equals(DictCodeListExt3.交易方向_提前购回.getValue(),this.getPs())
                ||StringUtils.equals(DictCodeListExt3.交易方向_到期购回.getValue(),this.getPs())
                ||StringUtils.equals(DictCodeListExt3.交易方向_中途换券.getValue(),this.getPs())
                ||StringUtils.equals(DictCodeListExt3.交易方向_解除质押.getValue(),this.getPs())
                ||StringUtils.equals(DictCodeListExt3.交易方向_到期续做.getValue(),this.getPs())
        ){
            if(StringUtils.equals(DictCodeList.回购拆借方向_正回购.getValue(),this.getBusiPs())){
                return DictCodeList.回购拆借方向_正回购.getCodeName();
            }
            if(StringUtils.equals(DictCodeList.回购拆借方向_逆回购.getValue(),this.getBusiPs())){
                return DictCodeList.回购拆借方向_逆回购.getCodeName();
            }
        }
        return otherPs;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     * 取得 委托额度，交易金额
     * @return 委托额度，交易金额
     */
    public BigDecimal getEntrustAmt() {
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_质押券出入库.getValue())){
            return null;
        }
        return super.getEntrustAmt();
    }

    public String getEntrustAmtStr() {
        if (getEntrustAmt() == null) {
            return "";
        }
        String tips = "指令金额:";
        NumberFormat numberInstance = NumberFormat.getNumberInstance(Locale.getDefault());
        numberInstance.setMinimumFractionDigits(2);
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_质押券出入库.getValue())){
            return null;
        }
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_通用式质押回购.getValue())){
            return tips + numberInstance.format(ArithmeticUtil.divide(getEntrustAmt(),new BigDecimal(10000),2)) + "(万元)";
        }
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级投标.getValue()) ||
                StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级申购.getValue())){
            return tips + numberInstance.format(ArithmeticUtil.divide(getEntrustAmt(),new BigDecimal(100),2)) + "(万元)";
        }
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_现券交易.getValue())){
            return tips + numberInstance.format(getEntrustAmt().setScale(2,BigDecimal.ROUND_HALF_UP)) + "(元)";
        }else {
            return tips + numberInstance.format(getEntrustAmt().setScale(2,BigDecimal.ROUND_HALF_UP)) + "(元)";
        }
    }
    public BigDecimal getEntrustNum() {
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_通用式质押回购.getValue())){
            return null;
        }
        return super.getEntrustNum();
    }
    public String getEntrustNumStr() {
        if (getEntrustNum() == null) {
            return "";
        }
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_通用式质押回购.getValue())){
            return null;
        }
        String tips = "指令数量:";
        NumberFormat numberInstance = NumberFormat.getNumberInstance(Locale.getDefault());
        numberInstance.setMinimumFractionDigits(2);
        // 指令数量入库时已经 除以 100了
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_质押券出入库.getValue())){
            return tips + numberInstance.format(ArithmeticUtil.divide(super.getEntrustNum(),new BigDecimal("100"),2)) + "(万元)";
        }
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_现券交易.getValue())){
            return tips + numberInstance.format(getEntrustNum().setScale(2, RoundingMode.HALF_UP)) + "(万元)";
        }
        if (StringUtil.equals(getAssetType(),DictCodeList.平安资产类型_债券.getValue())
            || StringUtil.equals(getAssetType(),DictCodeList.平安资产类型_质押式回购.getValue())){
            return tips + numberInstance.format(super.getEntrustNum().setScale(0, RoundingMode.HALF_UP)) + "(张)";
        }
        if (StringUtil.equals(getAssetType(),DictCodeList.平安资产类型_基金.getValue())){
            return tips + numberInstance.format(super.getEntrustNum().setScale(0, RoundingMode.HALF_UP)) + "(份)";
        }
        if (StringUtil.equals(getAssetType(), DictCodeListExt2.平安资产类型_股票.getValue())){
            return tips + numberInstance.format(super.getEntrustNum().setScale(0, RoundingMode.HALF_UP)) + "(股)";
        }
        return tips + numberInstance.format(super.getEntrustNum().setScale(0, RoundingMode.HALF_UP));
    }
    public void setOtherPs(String otherPs) {
        this.otherPs = otherPs;
    }

    public String getBusiPs() {
        return busiPs;
    }

    public void setBusiPs(String busiPs) {
        this.busiPs = busiPs;
    }

    public BigDecimal getFinishAmt() {
        return finishAmt;
    }

    public void setFinishAmt(BigDecimal finishAmt) {
        this.finishAmt = finishAmt;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getTermDays() {
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_通用式质押回购.getValue())){
            return convertTermDays(getFinprodAbbr());
        }
        return termDays;
    }

    public void setTermDays(String termDays) {
        this.termDays = termDays;
    }

    public String getEffectFlag() {
        return effectFlag;
    }

    public void setEffectFlag(String effectFlag) {
        this.effectFlag = effectFlag;
    }

    public BigDecimal getExeProgress() {
        return exeProgress;
    }

    public void setExeProgress(BigDecimal exeProgress) {
        this.exeProgress = exeProgress;
    }

    public BigDecimal getDealProgress() {
        return dealProgress;
    }

    public void setDealProgress(BigDecimal dealProgress) {
        this.dealProgress = dealProgress;
    }

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }

    public String getOtherBusiTypePosition() {
        String busiTypePosition = SysDataUtil.getNameFromDataDict(DictCode.投管业务类型, this.getBusiTypePosition());
        if (StringUtil.isEmpty(busiTypePosition)) {
            return SysDataUtil.getNameFromDataDict(DictCode.头寸业务类型, this.getBusiTypePosition());
        }
        return busiTypePosition;
    }

//    public BigDecimal getEtrPrice() {
//        if (etrPrice == null){
//            return null;
//        }
//        if(StringUtil.equals(DictCodeListExt3.投管业务类型_通用式质押回购.getValue(), this.getBusiTypePosition()) &&
//                StringUtil.equals(DictCodeListExt2.价格模式_市价指令.getValue(), this.getPriceModel())){
//            return null;
//        }
//        if (StringUtil.equals(this.getBusiTypePosition(),DictCodeListExt3.投管业务类型_质押券出入库.getValue())){
//            return null;
//        }
//        if(StringUtils.equals(DictCodeList.平安资产类型_基金.getValue(),this.getFinprodType2())
//            && StringUtils.equals("PRM03",this.getPriceModel())){
//            return new BigDecimal("0.00");
//        }
//        if(StringUtils.equals(getBusiTypePosition(), DictCodeListExt3.投管业务类型_通用式质押回购.getValue())){
//            return etrPrice.setScale(3,BigDecimal.ROUND_HALF_UP);
//        }
//        return etrPrice.setScale(4, RoundingMode.HALF_UP);
//    }


    public String getEtrPrice() {
        return etrPrice;
    }

    public void setEtrPrice(String etrPrice) {
        this.etrPrice = etrPrice;
    }

    public String getEtrPriceStr() {
        if (getEtrPrice() == null){
            return null;
        }
        String tips = "指令价格:";
        if(StringUtils.equals(DictCodeList.平安资产类型_基金.getValue(),this.getFinprodType2())
                && StringUtils.equals("PRM03",this.getPriceModel())){
            return tips + "0.00";
        }
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_通用式质押回购.getValue())){
            return tips + getEtrPrice() + "%";
        }
        return tips + getEtrPrice();
    }

//    public void setEtrPrice(BigDecimal etrPrice) {
//        this.etrPrice = etrPrice;
//    }

    public BigDecimal getExecFinishAmt() {
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_质押券出入库.getValue())){
            return ArithmeticUtil.divide(execFinishAmt,new BigDecimal("100"),2);
        }
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_通用式质押回购.getValue())){
            return ArithmeticUtil.divide(execFinishAmt,new BigDecimal("10000"),2);
        }
        return execFinishAmt;
    }

    public void setExecFinishAmt(BigDecimal execFinishAmt) {
        this.execFinishAmt = execFinishAmt;
    }

    public BigDecimal getExecRemainderAmt() {
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_质押券出入库.getValue())){
            return ArithmeticUtil.divide(execRemainderAmt,new BigDecimal("100"),2);
        }
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_通用式质押回购.getValue())){
            return ArithmeticUtil.divide(execRemainderAmt,new BigDecimal("10000"),2);
        }
        return execRemainderAmt;
    }

    public void setExecRemainderAmt(BigDecimal execRemainderAmt) {
        this.execRemainderAmt = execRemainderAmt;
    }

    public BigDecimal getTransFinishAmt() {
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_质押券出入库.getValue())){
            return ArithmeticUtil.divide(transFinishAmt,new BigDecimal("100"),2);
        }
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_通用式质押回购.getValue())){
            return ArithmeticUtil.divide(transFinishAmt,new BigDecimal("10000"),2);
        }
        return transFinishAmt;
    }

    public void setTransFinishAmt(BigDecimal transFinishAmt) {
        this.transFinishAmt = transFinishAmt;
    }

    public BigDecimal getTransRemainderAmt() {
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_质押券出入库.getValue())){
            return ArithmeticUtil.divide(transRemainderAmt,new BigDecimal("100"),2);
        }
        if(StringUtils.equals(this.getBusiTypePosition(), DictCodeListExt3.投管业务类型_通用式质押回购.getValue())){
            return ArithmeticUtil.divide(transRemainderAmt,new BigDecimal("10000"),2);
        }
        return transRemainderAmt;
    }

    public void setTransRemainderAmt(BigDecimal transRemainderAmt) {
        this.transRemainderAmt = transRemainderAmt;
    }

    public String getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(String transmitTime) {
        this.transmitTime = transmitTime;
    }

    public String getInvManagerName() {
        return invManagerName;
    }

    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    public String getCalculateField() {
        return calculateField;
    }

    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }

    public String getTrdAcctId() {
        return trdAcctId;
    }

    public void setTrdAcctId(String trdAcctId) {
        this.trdAcctId = trdAcctId;
    }

    /**
     * 取得 币种
     * @return 币种
     */
    public String getCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());
    }

    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    /**
     * 期限品种映射期限天数
     * @param termType
     * @return
     */
    private String convertTermDays(String termType){
        String termDays = "";
        switch (termType){
            case "GC001":
                termDays = "1";
                break;
            case "R-001":
                termDays = "1";
                break;
            case "GC002":
                termDays = "2";
                break;
            case "R-002":
                termDays = "2";
                break;
            case "GC003":
                termDays = "3";
                break;
            case "R-003":
                termDays = "3";
                break;
            case "GC004":
                termDays = "4";
                break;
            case "R-004":
                termDays = "4";
                break;
            case "GC007":
                termDays = "7";
                break;
            case "R-007":
                termDays = "7";
                break;
            case "GC014":
                termDays = "14";
                break;
            case "R-014":
                termDays = "14";
                break;
            case "GC028":
                termDays = "28";
                break;
            case "R-028":
                termDays = "28";
                break;
            case "GC091":
                termDays = "91";
                break;
            case "R-091":
                termDays = "91";
                break;
            case "GC182":
                termDays = "182";
                break;
            case "R-182":
                termDays = "182";
                break;
            default:
                break;
        }
        return termDays;
    }

    public String getBidModle() {
        return bidModle;
    }

    public void setBidModle(String bidModle) {
        this.bidModle = bidModle;
    }

    public String getIsWin() {
        return isWin;
    }

    public void setIsWin(String isWin) {
        this.isWin = isWin;
    }
}
