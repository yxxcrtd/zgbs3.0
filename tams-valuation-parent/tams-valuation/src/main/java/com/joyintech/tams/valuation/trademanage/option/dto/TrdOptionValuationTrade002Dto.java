package com.joyintech.tams.valuation.trademanage.option.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.trademanage.option.validator.TrdOptionValuationTrade002Validator;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 期权交易新增dto
 */
@PageId("TrdOptionValuationTrade003")
@ValidLogic(targetClass = TrdOptionValuationTrade002Validator.class, groups = { ValidateGroupEdit.class,
        ValidateGroupDelete.class })
public class TrdOptionValuationTrade002Dto extends BaseEntity implements ProcessEntity {
    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdOptionValuationTrade002Dto> selectedItem;

    /**
     * 要素id
     */
    private String elementId;
    /**
     * 交易编号
     */
    private String tradeId;
    /**
     * 外部成交编号
     */
    private String outTradeId;
    /**
     * 金融产品代码
     */
    private String prodId;
    /**
     * 金融产品名称
     */
    private String prodName;
    /**
     * 合约标的
     */
    private String targetId;
    /**
     * 合约类型
     */
    private String contractType;
    /**
     * 交易方向
     */
    private String ps;
    /**
     * 交易日期
     */
    private Date tradeDate;
    /**
     * 支付日
     */
    private Date payDate;
    /**
     * 收付方向
     */
    private String payment;
    /**
     * 名义本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal nomCapi;

    /**
     * 行权方式
     */
    private String exerciseType;
    /**
     * 期权费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal optFee;
    /**
     * 币种
     */
    private String ccy;
    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String acctNo;


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

    public List<TrdOptionValuationTrade002Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdOptionValuationTrade002Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.tradeId", groups = { ValidateGroupEdit.class })
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getOutTradeId() {
        return outTradeId;
    }

    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.prodId", groups = { ValidateGroupEdit.class })
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.prodName", groups = { ValidateGroupEdit.class })
    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.targetId", groups = { ValidateGroupEdit.class })
    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.contractType", groups = { ValidateGroupEdit.class })
    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.ps", groups = { ValidateGroupEdit.class })
    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.tradeDate", groups = { ValidateGroupEdit.class })
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.nomCapi", groups = { ValidateGroupEdit.class })
    public BigDecimal getNomCapi() {
        return nomCapi;
    }

    public void setNomCapi(BigDecimal nomCapi) {
        this.nomCapi = nomCapi;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.exerciseType", groups = { ValidateGroupEdit.class })
    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.optFee", groups = { ValidateGroupEdit.class })
    public BigDecimal getOptFee() {
        return optFee;
    }

    public void setOptFee(BigDecimal optFee) {
        this.optFee = optFee;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.ccy", groups = { ValidateGroupEdit.class })
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.settleAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.acctNo", groups = { ValidateGroupEdit.class })
    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade003.payDate", groups = { ValidateGroupEdit.class })
    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade003.payment", groups = { ValidateGroupEdit.class })
    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
