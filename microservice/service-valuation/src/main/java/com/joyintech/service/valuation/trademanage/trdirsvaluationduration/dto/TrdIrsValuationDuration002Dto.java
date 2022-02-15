package com.joyintech.service.valuation.trademanage.trdirsvaluationduration.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.List;

public class TrdIrsValuationDuration002Dto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdIrsValuationDuration002Dto> selectedItem;

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
     * 原始交易编号
     */
    private String pTradeId;

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
     * 交易类型
     */
    @NotEmpty(fieldName = "交易类型")
    private String ps;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 收付息类型
     */
    private String irsPsIntType;

    /**
     * 交易日
     */
    private String tradeDate;

    /**
     * 固定利息支付日
     */
    private String fixPayDate;

    /**
     * 浮动利息支付日
     */
    private String floatPayDate;

    /**
     * 提前终止支付日
     */
    private String beforeEndPayDate;

    /**
     * 提前终止收付方向
     */
    private String beforeEndPs;

    /**
     * 名义本金
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal prin;

    /**
     * 剩余名义本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal remainPrin;

    /**
     * 币种
     */
    @NotEmpty(fieldName = "币种")
    private String ccy;

    /**
     * 固定利息
     */
    private BigDecimal fixIntAmt;

    /**
     * 浮动利息
     */
    private BigDecimal floatIntAmt;

    /**
     * 提前终止名义本金
     */
    private BigDecimal beforeEndPrin;

    /**
     * 提前终止收付金额
     */
    private BigDecimal beforeEndAmt;

    /**
     * 其他费用
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
    //@NotEmpty(fieldName = "资金账户")
    private String myAcctNo;

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
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

    public List<TrdIrsValuationDuration002Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdIrsValuationDuration002Dto> selectedItem) {
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

    public String getpTradeId() {
        return pTradeId;
    }

    public void setpTradeId(String pTradeId) {
        this.pTradeId = pTradeId;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getIrsPsIntType() {
        return irsPsIntType;
    }

    public void setIrsPsIntType(String irsPsIntType) {
        this.irsPsIntType = irsPsIntType;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getFixPayDate() {
        return fixPayDate;
    }

    public void setFixPayDate(String fixPayDate) {
        this.fixPayDate = fixPayDate;
    }

    public String getFloatPayDate() {
        return floatPayDate;
    }

    public void setFloatPayDate(String floatPayDate) {
        this.floatPayDate = floatPayDate;
    }

    public String getBeforeEndPayDate() {
        return beforeEndPayDate;
    }

    public void setBeforeEndPayDate(String beforeEndPayDate) {
        this.beforeEndPayDate = beforeEndPayDate;
    }

    public String getBeforeEndPs() {
        return beforeEndPs;
    }

    public void setBeforeEndPs(String beforeEndPs) {
        this.beforeEndPs = beforeEndPs;
    }

    public BigDecimal getPrin() {
        return prin;
    }

    public void setPrin(BigDecimal prin) {
        this.prin = prin;
    }

    public BigDecimal getRemainPrin() {
        return remainPrin;
    }

    public void setRemainPrin(BigDecimal remainPrin) {
        this.remainPrin = remainPrin;
    }

    public BigDecimal getFixIntAmt() {
        return fixIntAmt;
    }

    public void setFixIntAmt(BigDecimal fixIntAmt) {
        this.fixIntAmt = fixIntAmt;
    }

    public BigDecimal getFloatIntAmt() {
        return floatIntAmt;
    }

    public void setFloatIntAmt(BigDecimal floatIntAmt) {
        this.floatIntAmt = floatIntAmt;
    }

    public BigDecimal getBeforeEndPrin() {
        return beforeEndPrin;
    }

    public void setBeforeEndPrin(BigDecimal beforeEndPrin) {
        this.beforeEndPrin = beforeEndPrin;
    }

    public BigDecimal getBeforeEndAmt() {
        return beforeEndAmt;
    }

    public void setBeforeEndAmt(BigDecimal beforeEndAmt) {
        this.beforeEndAmt = beforeEndAmt;
    }
}
