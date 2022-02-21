package com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.validator.TrdStockValuationDurationValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@PageId("TrdStockValuationDuration002")
@ValidLogic(targetClass = TrdStockValuationDurationValidator.class, groups = { ValidateGroupEdit.class,
        ValidateGroupDelete.class })
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
     * 交易类型
     */
    private String ps;

    /**
     * 权益登记日
     */
    private Date regDate;

    /**
     * 除权（除息）日
     */
    private Date exRightsDate;

    /**
     * 派息日
     */
    private Date dividDate;

    /**
     * 权益登记日股本
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal amount;

    /**
     * 分红金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal bonusAmt;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 分红股本
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal devidentAmt;

    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String myAcctNo;

    @NotEmpty(fieldName = "TrdStockValuationDuration002.prodId", groups = { ValidateGroupEdit.class })
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @NotEmpty(fieldName = "TrdStockValuationDuration002.ps", groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdStockValuationDuration002.regDate", groups = { ValidateGroupEdit.class })
    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    @NotEmpty(fieldName = "TrdStockValuationDuration002.exRightsDate", groups = { ValidateGroupEdit.class })
    public Date getExRightsDate() {
        return exRightsDate;
    }

    public void setExRightsDate(Date exRightsDate) {
        this.exRightsDate = exRightsDate;
    }

    @NotEmpty(fieldName = "TrdStockValuationDuration002.dividDate", groups = { ValidateGroupEdit.class })
    public Date getDividDate() {
        return dividDate;
    }

    public void setDividDate(Date dividDate) {
        this.dividDate = dividDate;
    }

    @NotEmpty(fieldName = "TrdStockValuationDuration002.amount", groups = { ValidateGroupEdit.class })
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBonusAmt() {
        return bonusAmt;
    }

    public void setBonusAmt(BigDecimal bonusAmt) {
        this.bonusAmt = bonusAmt;
    }

    public BigDecimal getDevidentAmt() {
        return devidentAmt;
    }

    public void setDevidentAmt(BigDecimal devidentAmt) {
        this.devidentAmt = devidentAmt;
    }

    @NotEmpty(fieldName = "TrdStockValuationDuration002.ccy", groups = { ValidateGroupEdit.class })
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @NotEmpty(fieldName = "TrdStockValuationDuration002.myAcctNo", groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdStockValuationDuration002.prodName", groups = { ValidateGroupEdit.class })
    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    @NotEmpty(fieldName = "TrdStockValuationDuration002.settleAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdStockValuationDuration002.assetId", groups = { ValidateGroupEdit.class })
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @NotEmpty(fieldName = "TrdStockValuationDuration002.assetAbbr", groups = { ValidateGroupEdit.class })
    public String getAssetAbbr() {
        return assetAbbr;
    }

    public void setAssetAbbr(String assetAbbr) {
        this.assetAbbr = assetAbbr;
    }

}
