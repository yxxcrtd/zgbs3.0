package com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.validator.TrdBondValuationDurationValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@PageId("TrdBondValuationDuration002")
@ValidLogic(targetClass = TrdBondValuationDurationValidator.class, groups = { ValidateGroupEdit.class,
        ValidateGroupDelete.class })
public class TrdBondValuationDuration002Dto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdBondValuationDuration002Dto> selectedItem;

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
    private String prodId;

    /**
     * 金融产品名称
     */
    private String prodName;

    /**
     * 资产代码
     */
    private String assetId;

    /**
     * 资产简称
     */
    private String assetAbbr;

    /**
     * 证券账户
     */
    private String secManageAcctId;

    /**
     * 交易类型
     */
    private String ps;

    /**
     * 计划支付日
     */
    private Date planPayDate;

    /**
     * 资金交收日
     */
    private Date fundSettleDate;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 实收利息
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal actInt;

    /**
     * 实收本金
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal actCapi;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 其他费用
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal otherAmt;

    /**
     * 是否结清
     */
    private String isClean2;

    /**
     * 本币交易金额
     */
    private BigDecimal tardeAmt;

    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String myAcctNo;

    @NotEmpty(fieldName = "TrdBondValuationDuration002.prodId", groups = { ValidateGroupEdit.class })
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.secManageAcctId", groups = { ValidateGroupEdit.class })
    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.ps", groups = { ValidateGroupEdit.class })
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

    public Date getFundSettleDate() {
        return fundSettleDate;
    }

    public void setFundSettleDate(Date fundSettleDate) {
        this.fundSettleDate = fundSettleDate;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.invAim", groups = { ValidateGroupEdit.class })
    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.ccy", groups = { ValidateGroupEdit.class })
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.otherAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.myAcctNo", groups = { ValidateGroupEdit.class })
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

    public List<TrdBondValuationDuration002Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdBondValuationDuration002Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.prodName", groups = { ValidateGroupEdit.class })
    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.settleAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.assetId", groups = { ValidateGroupEdit.class })
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.assetAbbr", groups = { ValidateGroupEdit.class })
    public String getAssetAbbr() {
        return assetAbbr;
    }

    public void setAssetAbbr(String assetAbbr) {
        this.assetAbbr = assetAbbr;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.planPayDate", groups = { ValidateGroupEdit.class })
    public Date getPlanPayDate() {
        return planPayDate;
    }

    public void setPlanPayDate(Date planPayDate) {
        this.planPayDate = planPayDate;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.actInt", groups = { ValidateGroupEdit.class })
    public BigDecimal getActInt() {
        return actInt;
    }

    public void setActInt(BigDecimal actInt) {
        this.actInt = actInt;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.actCapi", groups = { ValidateGroupEdit.class })
    public BigDecimal getActCapi() {
        return actCapi;
    }

    public void setActCapi(BigDecimal actCapi) {
        this.actCapi = actCapi;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.isClean2", groups = { ValidateGroupEdit.class })
    public String getIsClean2() {
        return isClean2;
    }

    public void setIsClean2(String isClean2) {
        this.isClean2 = isClean2;
    }

    @NotEmpty(fieldName = "TrdBondValuationDuration002.tardeAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getTardeAmt() {
        return tardeAmt;
    }

    public void setTardeAmt(BigDecimal tardeAmt) {
        this.tardeAmt = tardeAmt;
    }
}
