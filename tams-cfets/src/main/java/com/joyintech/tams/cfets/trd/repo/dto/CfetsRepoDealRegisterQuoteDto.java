/*
 * 文件名：CfetsBondDealDialogueQuoteDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.repo.dto;

import com.joyintech.background.component.common.pack.common.util.ArithmeticUtil;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.tams.common.base.entity.RegFinProductDealTotal;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 对话报价(登记)
 */
public class CfetsRepoDealRegisterQuoteDto extends RegFinProductDealTotal implements ProcessEntity {

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

    private String ouTraderName;

    private String counterName;

    private String counterTraderName;
    /**
     * 回购方式
     */
    private String repoMethod;
    /**
     * 清算速度
     */
    private String settleSpreedCode;
    /**
     * 回购利率%
     */
    private BigDecimal price;
    /**
     * 质押券折算总额(元)
     */
    private BigDecimal convertAmt;

    /**
     * 首次结算方式
     */
    private String deliverytype;

    /**
     * 清算类型
     */
    private String settleType;
    /**
     * 交易手续费
     */
    private BigDecimal dealFeeAmt;

    /**
     * 结算服务费
     */
    private BigDecimal settFeeAmt;

    /**
     * 组合分配方式
     */
    private String distribution;

    /**
     * 是否软控 跳过校验
     */
    private String softSkipMode;

    /**
     * 组合分配信息
     */
    private List<RepoPtlAllocationDto> ptlAllocations;

    /**
     * 质押券信息
     */
    private List<RepoEntrustPledgeDto> repoEntrustPledgeDtos;

    /**
     * 首次结算日
     */
    private Date settlementStartDate;

    /**
     * 到期结算日
     */
    private Date settlementEndDate;

    /**
     * 到期结算金额
     */
    private BigDecimal mtradeAmt;

    /**
     * 实际占款天数(天)
     */
    private Long actCapDays;
    /**
     * 决策动作
     */
    private String systemAct;

    private String shareAmtStr;


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

    public String getOuTraderName() {
        return ouTraderName;
    }

    public void setOuTraderName(String ouTraderName) {
        this.ouTraderName = ouTraderName;
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

    public String getRepoMethod() {
        return repoMethod;
    }

    public void setRepoMethod(String repoMethod) {
        this.repoMethod = repoMethod;
    }

    public String getSettleSpreedCode() {
        return settleSpreedCode;
    }

    public void setSettleSpreedCode(String settleSpreedCode) {
        this.settleSpreedCode = settleSpreedCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public BigDecimal getConvertAmt() {
        return convertAmt;
    }

    public void setConvertAmt(BigDecimal convertAmt) {
        this.convertAmt = convertAmt;
    }

    public String getDeliverytype() {
        return deliverytype;
    }

    public void setDeliverytype(String deliverytype) {
        this.deliverytype = deliverytype;
    }

    public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
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

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public List<RepoPtlAllocationDto> getPtlAllocations() {
        return ptlAllocations;
    }

    public void setPtlAllocations(List<RepoPtlAllocationDto> ptlAllocations) {
        this.ptlAllocations = ptlAllocations;
    }

    public List<RepoEntrustPledgeDto> getRepoEntrustPledgeDtos() {
        return repoEntrustPledgeDtos;
    }

    public void setRepoEntrustPledgeDtos(List<RepoEntrustPledgeDto> repoEntrustPledgeDtos) {
        this.repoEntrustPledgeDtos = repoEntrustPledgeDtos;
    }

    public Date getSettlementStartDate() {
        return settlementStartDate;
    }

    public void setSettlementStartDate(Date settlementStartDate) {
        this.settlementStartDate = settlementStartDate;
    }

    public Date getSettlementEndDate() {
        return settlementEndDate;
    }

    public void setSettlementEndDate(Date settlementEndDate) {
        this.settlementEndDate = settlementEndDate;
    }


    public BigDecimal getMtradeAmt() {
        return mtradeAmt;
    }

    public void setMtradeAmt(BigDecimal mtradeAmt) {
        this.mtradeAmt = mtradeAmt;
    }

    public Long getActCapDays() {
        return actCapDays;
    }

    public void setActCapDays(Long actCapDays) {
        this.actCapDays = actCapDays;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }

    public String getShareAmtStr() {
        if (StringUtil.isNotEmpty(this.getShareAmt())) {
            return ArithmeticUtil.divide(this.getShareAmt(), new BigDecimal(10000), 2, RoundingMode.HALF_UP).toPlainString();
        }
        return shareAmtStr;
    }

    public void setShareAmtStr(String shareAmtStr) {
        this.shareAmtStr = shareAmtStr;
    }
}
