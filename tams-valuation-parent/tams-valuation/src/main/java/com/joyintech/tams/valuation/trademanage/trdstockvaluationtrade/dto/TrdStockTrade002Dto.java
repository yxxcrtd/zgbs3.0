package com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.List;

public class TrdStockTrade002Dto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdStockTrade002Dto> selectedItem;

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
     * 外部成交编号
     */
    private String outTradeId;

    /**
     * 金融产品代码
     */
    @NotEmpty(fieldName = "产品代码")
    private String prodId;

    /**
     * 金融产品名称
     */
    @NotEmpty(fieldName = "产品名称")
    private String prodName;

    /**
     * 债券代码
     */
    @NotEmpty(fieldName = "资产代码")
    private String assetId;

    /**
     * 债券简称
     */
    @NotEmpty(fieldName = "资产简称")
    private String assetAbbr;

    /**
     * 交易方向
     */
    @NotEmpty(fieldName = "交易类型")
    private String ps;

    /**
     * 交易日
     */
    @NotEmpty(fieldName = "交易日")
    private String tradeDate;

    /**
     * 缴款日
     */
    @NotEmpty(fieldName = "缴款日")
    private String paymentDate;

    /**
     * 交割日期
     */
    @NotEmpty(fieldName = "交割日")
    private String settdateDate;

    /**
     * 交收日
     */
    @NotEmpty(fieldName = "交收日")
    private String settleDate;

    /**
     * 是否锁定
     */
    private String isLock;

    /**
     * 锁定期截止日
     */
    @NotEmpty(fieldName = "锁定期截止日")
    private String endDate;

    /**
     * 交易数量
     */
    @NotEmpty(fieldName = "交易数量")
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal dealNum;

    /**
     * 交易价格
     */
    @NotEmpty(fieldName = "交易价格")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

    /**
     * 币种
     */
    @NotEmpty(fieldName = "币种")
    private String ccy;

    /**
     * 印花税
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal stampDutyAmt;

    /**
     * 佣金
     */
    @NotEmpty(fieldName = "佣金")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal commission;

    /**
     * 其他应付费用
     */
    @NotEmpty(fieldName = "其他费用")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal otherAmt;

    /**
     * 结算金额
     */
    @NotEmpty(fieldName = "结算金额")
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    @NotEmpty(fieldName = "资金账户")
    private String myAcctNo;

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
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

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getStampDutyAmt() {
        return stampDutyAmt;
    }

    public void setStampDutyAmt(BigDecimal stampDutyAmt) {
        this.stampDutyAmt = stampDutyAmt;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getSettdateDate() {
        return settdateDate;
    }

    public void setSettdateDate(String settdateDate) {
        this.settdateDate = settdateDate;
    }

    public BigDecimal getDealNum() {
        return dealNum;
    }

    public void setDealNum(BigDecimal dealNum) {
        this.dealNum = dealNum;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getMyAcctNo() {
        return myAcctNo;
    }

    public void setMyAcctNo(String myAcctNo) {
        this.myAcctNo = myAcctNo;
    }

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

    public List<TrdStockTrade002Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdStockTrade002Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getOutTradeId() {
        return outTradeId;
    }

    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getIsLock() {
        return isLock;
    }

    public void setIsLock(String isLock) {
        this.isLock = isLock;
    }
}
