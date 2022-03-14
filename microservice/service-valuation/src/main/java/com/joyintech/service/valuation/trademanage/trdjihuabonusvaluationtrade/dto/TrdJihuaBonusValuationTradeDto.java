/*
 * 文件名：TrdJihuaBonusTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.valuation.trademanage.trdjihuabonusvaluationtrade.dto;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:计划类资产存续期
 * @auther: yuzj
 * @date: 2022/02/11
 * @param:
 * @return:
 */
public class TrdJihuaBonusValuationTradeDto extends BaseEntity implements ProcessEntity {
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
    private List<TrdJihuaBonusValuationTradeDto> selectedItem;

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
    private Date tradeDate_hlzt;

    /**
     * 分红金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal bonusAmt_hlzt;

    /**
     * 交易份额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal dealNum_hlzt;

    /**
     * 单位分红净值
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal unitBonusUnitValue;

    /**
     * 结算金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal settleAmt_hlzt;

    /**
     * 资金账户
     */
    private String myAcctNo_hlzt;

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

    public List<TrdJihuaBonusValuationTradeDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdJihuaBonusValuationTradeDto> selectedItem) {
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

    public Date getTradeDate_hlzt() {
        return tradeDate_hlzt;
    }

    public void setTradeDate_hlzt(Date tradeDate_hlzt) {
        this.tradeDate_hlzt = tradeDate_hlzt;
    }

    public BigDecimal getBonusAmt_hlzt() {
        return bonusAmt_hlzt;
    }

    public void setBonusAmt_hlzt(BigDecimal bonusAmt_hlzt) {
        this.bonusAmt_hlzt = bonusAmt_hlzt;
    }

    public BigDecimal getDealNum_hlzt() {
        return dealNum_hlzt;
    }

    public void setDealNum_hlzt(BigDecimal dealNum_hlzt) {
        this.dealNum_hlzt = dealNum_hlzt;
    }

    public BigDecimal getUnitBonusUnitValue() {
        return unitBonusUnitValue;
    }

    public void setUnitBonusUnitValue(BigDecimal unitBonusUnitValue) {
        this.unitBonusUnitValue = unitBonusUnitValue;
    }

    public BigDecimal getSettleAmt_hlzt() {
        return settleAmt_hlzt;
    }

    public void setSettleAmt_hlzt(BigDecimal settleAmt_hlzt) {
        this.settleAmt_hlzt = settleAmt_hlzt;
    }

    public String getMyAcctNo_hlzt() {
        return myAcctNo_hlzt;
    }

    public void setMyAcctNo_hlzt(String myAcctNo_hlzt) {
        this.myAcctNo_hlzt = myAcctNo_hlzt;
    }
}

