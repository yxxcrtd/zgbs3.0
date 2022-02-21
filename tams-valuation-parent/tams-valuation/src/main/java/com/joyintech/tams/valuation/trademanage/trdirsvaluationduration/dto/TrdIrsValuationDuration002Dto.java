package com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.validator.TrdIrsValuationDurationValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@PageId("TrdIrsValuationDuration002")
@ValidLogic(targetClass = TrdIrsValuationDurationValidator.class, groups = { ValidateGroupEdit.class,
        ValidateGroupDelete.class })
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
    private String prodId;

    /**
     * 金融产品名称
     */
    private String prodName;

    /**
     * 交易类型
     */
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
    private Date tradeDate;

    /**
     * 固定利息支付日
     */
    private Date fixPayDate;

    /**
     * 浮动利息支付日
     */
    private Date floatPayDate;

    /**
     * 提前终止支付日
     */
    private Date beforeEndPayDate;

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
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal otherAmt;

    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String myAcctNo;

    @NotEmpty(fieldName = "TrdIrsValuationDuration002.prodId", groups = { ValidateGroupEdit.class })
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @NotEmpty(fieldName = "TrdIrsValuationDuration002.ps", groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdIrsValuationDuration002.ccy", groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdIrsValuationDuration002.myAcctNo", groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdIrsValuationDuration002.prodName", groups = { ValidateGroupEdit.class })
    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    @NotEmpty(fieldName = "TrdIrsValuationDuration002.settleAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdIrsValuationDuration002.pTradeId", groups = { ValidateGroupEdit.class })
    public String getpTradeId() {
        return pTradeId;
    }

    public void setpTradeId(String pTradeId) {
        this.pTradeId = pTradeId;
    }

    @NotEmpty(fieldName = "TrdIrsValuationDuration002.counterId", groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdIrsValuationDuration002.tradeDate", groups = { ValidateGroupEdit.class })
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getFixPayDate() {
        return fixPayDate;
    }

    public void setFixPayDate(Date fixPayDate) {
        this.fixPayDate = fixPayDate;
    }

    public Date getFloatPayDate() {
        return floatPayDate;
    }

    public void setFloatPayDate(Date floatPayDate) {
        this.floatPayDate = floatPayDate;
    }

    public Date getBeforeEndPayDate() {
        return beforeEndPayDate;
    }

    public void setBeforeEndPayDate(Date beforeEndPayDate) {
        this.beforeEndPayDate = beforeEndPayDate;
    }

    public String getBeforeEndPs() {
        return beforeEndPs;
    }

    public void setBeforeEndPs(String beforeEndPs) {
        this.beforeEndPs = beforeEndPs;
    }

    @NotEmpty(fieldName = "TrdIrsValuationDuration002.prin", groups = { ValidateGroupEdit.class })
    public BigDecimal getPrin() {
        return prin;
    }

    public void setPrin(BigDecimal prin) {
        this.prin = prin;
    }

    @NotEmpty(fieldName = "TrdIrsValuationDuration002.remainPrin", groups = { ValidateGroupEdit.class })
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
