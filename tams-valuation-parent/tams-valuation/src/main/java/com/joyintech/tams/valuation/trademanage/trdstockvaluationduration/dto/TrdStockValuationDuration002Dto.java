package com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.List;

public class TrdStockValuationDuration002Dto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdStockValuationDuration002Dto> selectedItem;

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
     * 资产代码
     */
    @NotEmpty(fieldName = "资产代码")
    private String assetId;

    /**
     * 资产简称
     */
    @NotEmpty(fieldName = "资产简称")
    private String assetAbbr;

    /**
     * 证券账户
     */
   // @NotEmpty(fieldName = "证券账户")
    private String secManageAcctId;

    /**
     * 交易类型
     */
    @NotEmpty(fieldName = "交易类型")
    private String ps;

    /**
     * 计划支付日
     */
    @NotEmpty(fieldName = "计划支付日")
    private String planPayDate;

    /**
     * 资金交收日
     */
    @NotEmpty(fieldName = "资金交收日")
    private String fundSettleDate;

    /**
     * 投资目的
     */
    @NotEmpty(fieldName = "会计分类")
    private String invAim;

    /**
     * 实收利息
     */
    @NotEmpty(fieldName = "实收利息")
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal actInt;

    /**
     * 实收本金
     */
    @NotEmpty(fieldName = "实收本金")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal actCapi;

    /**
     * 币种
     */
    @NotEmpty(fieldName = "币种")
    private String ccy;

    /**
     * 其他费用
     */
    @NotEmpty(fieldName = "其他费用")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal otherAmt;

    /**
     * 是否结清
     */
    private String isClean2;

    /**
     * 本币交易金额
     */
    @NotEmpty(fieldName = "本币交易金额")
    private BigDecimal tardeAmt;

    /**
     * 结算金额
     */
    @NotEmpty(fieldName = "结算金额")
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    //@NotEmpty(fieldName = "资金账户")
    private String myAcctNo;

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
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

    public String getFundSettleDate() {
        return fundSettleDate;
    }

    public void setFundSettleDate(String fundSettleDate) {
        this.fundSettleDate = fundSettleDate;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
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

    public List<TrdStockValuationDuration002Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdStockValuationDuration002Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
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

    public String getPlanPayDate() {
        return planPayDate;
    }

    public void setPlanPayDate(String planPayDate) {
        this.planPayDate = planPayDate;
    }

    public BigDecimal getActInt() {
        return actInt;
    }

    public void setActInt(BigDecimal actInt) {
        this.actInt = actInt;
    }

    public BigDecimal getActCapi() {
        return actCapi;
    }

    public void setActCapi(BigDecimal actCapi) {
        this.actCapi = actCapi;
    }

    public String getIsClean2() {
        return isClean2;
    }

    public void setIsClean2(String isClean2) {
        this.isClean2 = isClean2;
    }

    public BigDecimal getTardeAmt() {
        return tardeAmt;
    }

    public void setTardeAmt(BigDecimal tardeAmt) {
        this.tardeAmt = tardeAmt;
    }
}
