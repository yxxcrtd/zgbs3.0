package com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.validator.TrdStockTradeValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@PageId("TrdStockTrade002")
@ValidLogic(targetClass = TrdStockTradeValidator.class, groups = { ValidateGroupEdit.class,
        ValidateGroupDelete.class })
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
    private String prodId;

    /**
     * 金融产品名称
     */
    private String prodName;

    /**
     * 债券代码
     */
    private String assetId;

    /**
     * 债券简称
     */
    private String assetAbbr;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 交易日
     */
    private Date tradeDate;

    /**
     * 缴款日
     */
    private Date paymentDate;

    /**
     * 交割日期
     */
    private Date settdateDate;

    /**
     * 交收日
     */
    private Date settleDate;

    /**
     * 是否锁定
     */
    private String isLock;

    /**
     * 锁定期截止日
     */
    private Date endDate;

    /**
     * 交易数量
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal dealNum;

    /**
     * 交易价格
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 印花税
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal stampDutyAmt;

    /**
     * 佣金
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal commission;

    /**
     * 其他应付费用
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

    @NotEmpty(fieldName = "TrdStockTrade002.prodId" , groups = { ValidateGroupEdit.class })
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.assetId" , groups = { ValidateGroupEdit.class })
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.assetAbbr" , groups = { ValidateGroupEdit.class })
    public String getAssetAbbr() {
        return assetAbbr;
    }

    public void setAssetAbbr(String assetAbbr) {
        this.assetAbbr = assetAbbr;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.paymentDate", groups = { ValidateGroupEdit.class })
    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.settleDate", groups = { ValidateGroupEdit.class })
    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getStampDutyAmt() {
        return stampDutyAmt;
    }

    public void setStampDutyAmt(BigDecimal stampDutyAmt) {
        this.stampDutyAmt = stampDutyAmt;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.ps" , groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdStockTrade002.tradeDate" , groups = { ValidateGroupEdit.class })
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.settdateDate", groups = { ValidateGroupEdit.class })
    public Date getSettdateDate() {
        return settdateDate;
    }

    public void setSettdateDate(Date settdateDate) {
        this.settdateDate = settdateDate;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.dealNum", groups = { ValidateGroupEdit.class })
    public BigDecimal getDealNum() {
        return dealNum;
    }

    public void setDealNum(BigDecimal dealNum) {
        this.dealNum = dealNum;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.ccy", groups = { ValidateGroupEdit.class })
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.commission", groups = { ValidateGroupEdit.class })
    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.otherAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.tradeAmt" , groups = { ValidateGroupEdit.class })
    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.myAcctNo" , groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdStockTrade002.prodName" , groups = { ValidateGroupEdit.class })
    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    @NotEmpty(fieldName = "TrdStockTrade002.settleAmt", groups = { ValidateGroupEdit.class })
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
