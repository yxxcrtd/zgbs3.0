/*
 * 文件名：TrdFundInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.valuation.trademanage.trdfundbonusvaluationtrade.dto;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:基金存续期
 * @auther: yuzj
 * @date: 2022/02/11
 * @param:
 * @return:
 */
public class TrdFundBonusValuationTradeDto extends BaseEntity implements ProcessEntity{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 5309538894816479429L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdFundBonusValuationTradeDto> selectedItem;

    /**
     * 要素id
     */
    private String elementId;

    /**
     * 交易编号
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    private String tradeId;

    /**
     * 产品代码
     */
    private String prodId;

    /**
     * 产品名称
     */
    private String prodName;

    /**
     * 资产代码
     */
    private String assetId;

    /**
     * 资产名称
     */
    private String assetAbbr;

    /**
     * 交易类型
     */
    private String ps;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 权益登记日
     */
    private Date mdate;

    /**
     * 资金到账日
     */
    private Date settleDate;

    /**
     * 分红金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal bonusAmt;

    /**
     * 结算金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String myAcctNo;

    /**
     * 交易日期
     */
    private Date tradeDateReInvest;

    /**
     * 权益登记日
     */
    private Date mdateInvest;

    /**
     * 分红金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal bonusAmtReInvest;

    /**
     * 交易份额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal dealNumReInvest;

    /**
     * 单位分红净值
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal unitBonusUnitValue;

    /**
     * 结算金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal settleAmtReInvest;

    /**
     * 资金账户
     */
    private String myAcctNoReInvest;

    /**
     * 交易日期
     */
    private Date tradeDateStrengthen;

    /**
     * 交易份额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal dealNumStrengthen;

    /**
     * 资金账户
     */
    private String myAcctNoStrengthen;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public List<TrdFundBonusValuationTradeDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdFundBonusValuationTradeDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    
    public String getTradeId() {
        return tradeId;
    }

    
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetAbbr() {
        return assetAbbr;
    }

    public void setAssetAbbr(String assetAbbr) {
        this.assetAbbr = assetAbbr;
    }

    
    public String getPs() {
        return ps;
    }

    
    public void setPs(String ps) {
        this.ps = ps;
    }

    
    public String getTradeMarket() {
        return tradeMarket;
    }

    
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    
    public String getCcy() {
        return ccy;
    }

    
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    
    public Date getTradeDate() {
        return tradeDate;
    }

    
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getBonusAmt() {
        return bonusAmt;
    }

    public void setBonusAmt(BigDecimal bonusAmt) {
        this.bonusAmt = bonusAmt;
    }

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getMyAcctNo() {
        return myAcctNo;
    }

    public void setMyAcctNo(String myAcctNo) {
        this.myAcctNo = myAcctNo;
    }

    public Date getTradeDateReInvest() {
        return tradeDateReInvest;
    }

    public void setTradeDateReInvest(Date tradeDateReInvest) {
        this.tradeDateReInvest = tradeDateReInvest;
    }

    public Date getMdateInvest() {
        return mdateInvest;
    }

    public void setMdateInvest(Date mdateInvest) {
        this.mdateInvest = mdateInvest;
    }

    public BigDecimal getBonusAmtReInvest() {
        return bonusAmtReInvest;
    }

    public void setBonusAmtReInvest(BigDecimal bonusAmtReInvest) {
        this.bonusAmtReInvest = bonusAmtReInvest;
    }

    public BigDecimal getDealNumReInvest() {
        return dealNumReInvest;
    }

    public void setDealNumReInvest(BigDecimal dealNumReInvest) {
        this.dealNumReInvest = dealNumReInvest;
    }

    public BigDecimal getUnitBonusUnitValue() {
        return unitBonusUnitValue;
    }

    public void setUnitBonusUnitValue(BigDecimal unitBonusUnitValue) {
        this.unitBonusUnitValue = unitBonusUnitValue;
    }

    public BigDecimal getSettleAmtReInvest() {
        return settleAmtReInvest;
    }

    public void setSettleAmtReInvest(BigDecimal settleAmtReInvest) {
        this.settleAmtReInvest = settleAmtReInvest;
    }

    public String getMyAcctNoReInvest() {
        return myAcctNoReInvest;
    }

    public void setMyAcctNoReInvest(String myAcctNoReInvest) {
        this.myAcctNoReInvest = myAcctNoReInvest;
    }

    public Date getTradeDateStrengthen() {
        return tradeDateStrengthen;
    }

    public void setTradeDateStrengthen(Date tradeDateStrengthen) {
        this.tradeDateStrengthen = tradeDateStrengthen;
    }

    public BigDecimal getDealNumStrengthen() {
        return dealNumStrengthen;
    }

    public void setDealNumStrengthen(BigDecimal dealNumStrengthen) {
        this.dealNumStrengthen = dealNumStrengthen;
    }

    public String getMyAcctNoStrengthen() {
        return myAcctNoStrengthen;
    }

    public void setMyAcctNoStrengthen(String myAcctNoStrengthen) {
        this.myAcctNoStrengthen = myAcctNoStrengthen;
    }
}

