package com.joyintech.service.valuation.trademanage.option.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 期权交易新增dto
 */
//@ValidLogic(targetClass = TrdOptionTrade002DtoValidator.class)
public class TrdOptionValuationTrade003Dto extends BaseEntity implements ProcessEntity {
    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdOptionValuationTrade003Dto> selectedItem;

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
     * 执行价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal exePrice;

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

    public List<TrdOptionValuationTrade003Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdOptionValuationTrade003Dto> selectedItem) {
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

    public String getOutTradeId() {
        return outTradeId;
    }

    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId;
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

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BigDecimal getNomCapi() {
        return nomCapi;
    }

    public void setNomCapi(BigDecimal nomCapi) {
        this.nomCapi = nomCapi;
    }

    public BigDecimal getExePrice() {
        return exePrice;
    }

    public void setExePrice(BigDecimal exePrice) {
        this.exePrice = exePrice;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public BigDecimal getOptFee() {
        return optFee;
    }

    public void setOptFee(BigDecimal optFee) {
        this.optFee = optFee;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
