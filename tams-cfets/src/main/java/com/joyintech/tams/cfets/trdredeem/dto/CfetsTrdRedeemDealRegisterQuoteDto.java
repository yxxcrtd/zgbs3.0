/*
 * 文件名：CfetsBondDealDialogueQuoteDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.trdredeem.dto;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.common.base.entity.RegFinProductDealTotal;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 对话报价(登记)
 */
public class CfetsTrdRedeemDealRegisterQuoteDto extends RegFinProductDealTotal implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -6517441367865558290L;
    /**
     * 操作类型
     */
    private String operatorType;

    /**
     * 要素id
     */
    private String elementId;

    /**
     * 汇总登记编号
     */
    @PrimaryKey(PrimaryKey.Field.PK_1)
    private String totleTradeId;

    /**
     *实际行权日
     */
    private Date actSetteDate;

    /**
     * 交易手续费
     */
    private BigDecimal  dealFeeAmt;

    /**
     * 结算服务费
     */
    private BigDecimal  settFeeAmt;

    private BigDecimal ourFpriceAmt;

    private BigDecimal lendinFpriceAmt;

    private BigDecimal saleinFpriceAmt;

    private BigDecimal ourIntAmt;

    private BigDecimal lendinIntAmt;

    private BigDecimal saleinIntAmt;

    private BigDecimal lendinShareAmt;

    private BigDecimal sellAbleNum;


    private Date theoreticalExerciseDate;

    /**
     * 报价方式(登记)
     */
    private String quoteStyle;

    private String calculateField;

    private String finprodAbbr;

    /**
     * 本方
     */
    private String mySide;

    private String mySideAbbr;
    /**
     * 本方交易员
     */
    private String mySideTrader;

    private String counterName;

    private String counterTraderName;


    private String  settleMethod;

    private String  settleType;

    private BigDecimal price;

    private BigDecimal matureYield;

    private String settleSpreedCode;

    /**
     * 组合分配方式
     */
    private String distribution;
    /**
     * 组合分配信息
     */
    private List<PtlAllocationDto> ptlAllocatios;

    /**
     * 市场代码
     */
    private String finprodMarketId;
    /**
     * 行权净价
     */
    private BigDecimal exercisePrice;
    /**
     * 赎回比例
     */
    private BigDecimal redeemRate;
    /**
     * 软控是否继续？Y：继续
     */
    private String softSkipMode;
    /**
     * 金融产品代码
     */
    @PrimaryKey(PrimaryKey.Field.PK_2)
    private String finprodId;
    /**
     * 撤销原因
     */
    private String revokeReason;

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    @Override
    public String getTotleTradeId() {
        return totleTradeId;
    }

    @Override
    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId;
    }

    public Date getActSetteDate() {
        return actSetteDate;
    }

    public void setActSetteDate(Date actSetteDate) {
        this.actSetteDate = actSetteDate;
    }

    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public List<PtlAllocationDto> getPtlAllocatios() {
        return ptlAllocatios;
    }

    public void setPtlAllocatios(List<PtlAllocationDto> ptlAllocatios) {
        this.ptlAllocatios = ptlAllocatios;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public BigDecimal getOurFpriceAmt() {
        return ourFpriceAmt;
    }

    public void setOurFpriceAmt(BigDecimal ourFpriceAmt) {
        this.ourFpriceAmt = ourFpriceAmt;
    }

    public BigDecimal getLendinFpriceAmt() {
        return lendinFpriceAmt;
    }

    public void setLendinFpriceAmt(BigDecimal lendinFpriceAmt) {
        this.lendinFpriceAmt = lendinFpriceAmt;
    }

    public BigDecimal getSaleinFpriceAmt() {
        return saleinFpriceAmt;
    }

    public void setSaleinFpriceAmt(BigDecimal saleinFpriceAmt) {
        this.saleinFpriceAmt = saleinFpriceAmt;
    }

    public BigDecimal getOurIntAmt() {
        return ourIntAmt;
    }

    public void setOurIntAmt(BigDecimal ourIntAmt) {
        this.ourIntAmt = ourIntAmt;
    }

    public BigDecimal getLendinIntAmt() {
        return lendinIntAmt;
    }

    public void setLendinIntAmt(BigDecimal lendinIntAmt) {
        this.lendinIntAmt = lendinIntAmt;
    }

    public BigDecimal getSaleinIntAmt() {
        return saleinIntAmt;
    }

    public void setSaleinIntAmt(BigDecimal saleinIntAmt) {
        this.saleinIntAmt = saleinIntAmt;
    }

    public Date getTheoreticalExerciseDate() {
        return theoreticalExerciseDate;
    }

    public void setTheoreticalExerciseDate(Date theoreticalExerciseDate) {
        this.theoreticalExerciseDate = theoreticalExerciseDate;
    }

    public BigDecimal getSellAbleNum() {
        return sellAbleNum;
    }

    public void setSellAbleNum(BigDecimal sellAbleNum) {
        this.sellAbleNum = sellAbleNum;
    }

    public String getQuoteStyle() {
        return quoteStyle;
    }

    public void setQuoteStyle(String quoteStyle) {
        this.quoteStyle = quoteStyle;
    }

    public String getCalculateField() {
        return calculateField;
    }

    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getMySide() {
        return mySide;
    }

    public void setMySide(String mySide) {
        this.mySide = mySide;
    }

    public String getMySideAbbr() {
        return mySideAbbr;
    }

    public void setMySideAbbr(String mySideAbbr) {
        this.mySideAbbr = mySideAbbr;
    }

    public String getMySideTrader() {
        return mySideTrader;
    }

    public void setMySideTrader(String mySideTrader) {
        this.mySideTrader = mySideTrader;
    }

    public String getSettleMethod() {
        return settleMethod;
    }

    public void setSettleMethod(String settleMethod) {
        this.settleMethod = settleMethod;
    }

    public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getMatureYield() {
        return matureYield;
    }

    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    public String getSettleSpreedCode() {
        return settleSpreedCode;
    }

    public void setSettleSpreedCode(String settleSpreedCode) {
        this.settleSpreedCode = settleSpreedCode;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public BigDecimal getLendinShareAmt() {
        return lendinShareAmt;
    }

    public void setLendinShareAmt(BigDecimal lendinShareAmt) {
        this.lendinShareAmt = lendinShareAmt;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getCounterTraderName() {
        return counterTraderName;
    }

    public void setCounterTraderName(String counterTraderName) {
        this.counterTraderName = counterTraderName;
    }

    public BigDecimal getExercisePrice() {
        return exercisePrice;
    }

    public void setExercisePrice(BigDecimal exercisePrice) {
        this.exercisePrice = exercisePrice;
    }

    public BigDecimal getRedeemRate() {
        return redeemRate;
    }

    public void setRedeemRate(BigDecimal redeemRate) {
        this.redeemRate = redeemRate;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    @Override
    public String getFinprodId() {
        return finprodId;
    }

    @Override
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    @PrimaryKey(PrimaryKey.Field.PK_3)
    public String getTheoreticalExerciseDateStr() {
        if(StringUtil.isNotEmpty(this.getTheoreticalExerciseDate())){
            return DateUtil.format(this.getTheoreticalExerciseDate());
        }
        return null;
    }

    public String getRevokeReason() {
        return revokeReason;
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }
}
