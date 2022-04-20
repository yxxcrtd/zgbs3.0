package com.joyintech.service.valuation.trademanage.trdstockvaluationtrade.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TrdStockTradeDto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdStockTradeDto> selectedItem;

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
     * 数据来源
     */
    private String dataSource;

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
     * 币种
     */
    private String ccy;

    /**
     * 证券账户
     */
    private String secManageAcctId;

    /**
     * 交易日
     */
    private Date tradeDate;

    //清算信息
    /**
     * 资金交收日期
     */
    private Date fundSettleDate;

    /**
     * 交易数量
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal dealNum;

    /**
     * 成交金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

    /**
     * 成交价格
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal price;

    /**
     * 印花税
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal stampDutyAmt;

    /**
     * 经手费
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal brokerage;

    /**
     * 证管费
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal certfFee;

    /**
     * 过户费
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal ghfAmt;

    /**
     * 结算费
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal stlAmt;

    /**
     * 交易费
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal feeAmt;

    /**
     * 证券其它费
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal secOtherAmt;

    /**
     * 净佣金
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal commission;

    /**
     * 应付佣金
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal commissionPay;

    /**
     * 汇率日期
     */
    private Date exchangeDate;

    /**
     * 清算金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String myAcctNo;

    //配股信息
    /**
     * 缴款日期
     */
    private Date paymentDate_pg;

    /**
     * 成交数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal dealNum_pg;

    /**
     * 成交价格
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal price_pg;

    /**
     * 缴款金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal paymentAmt_pg;

    /**
     * 资金账户
     */
    private String myAcctNo_pg;

    //申购信息
    /**
     * 申购金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal entrustAmt;

    /**
     * 资金账户
     */
    private String myAcctNo_sg;

    //确认信息
    /**
     * 中签数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal confirmNum;

    /**
     * 中签价格
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal confirmPrice;

    /**
     * 中签金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal confirmAmt;

    /**
     * 补款到账日期
     */
    private Date receiptArriveDate;

    /**
     * 补款金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal receiptAmt;

    /**
     * 返款到账日期
     */
    private Date refundArriveDate;

    /**
     * 返款金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal refundAmount;

    /**
     * 资金账户
     */
    private String myAcctNo_qr;

    /**
     * 净佣金
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal commission_qr;

    /**
     * 应付佣金
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal commissionPay_qr;

    /**
     * 清算金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal settleAmt_qr;

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

    public List<TrdStockTradeDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdStockTradeDto> selectedItem) {
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

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
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

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getFundSettleDate() {
        return fundSettleDate;
    }

    public void setFundSettleDate(Date fundSettleDate) {
        this.fundSettleDate = fundSettleDate;
    }

    public BigDecimal getDealNum() {
        return dealNum;
    }

    public void setDealNum(BigDecimal dealNum) {
        this.dealNum = dealNum;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getStampDutyAmt() {
        return stampDutyAmt;
    }

    public void setStampDutyAmt(BigDecimal stampDutyAmt) {
        this.stampDutyAmt = stampDutyAmt;
    }

    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }

    public BigDecimal getCertfFee() {
        return certfFee;
    }

    public void setCertfFee(BigDecimal certfFee) {
        this.certfFee = certfFee;
    }

    public BigDecimal getGhfAmt() {
        return ghfAmt;
    }

    public void setGhfAmt(BigDecimal ghfAmt) {
        this.ghfAmt = ghfAmt;
    }

    public BigDecimal getStlAmt() {
        return stlAmt;
    }

    public void setStlAmt(BigDecimal stlAmt) {
        this.stlAmt = stlAmt;
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public BigDecimal getSecOtherAmt() {
        return secOtherAmt;
    }

    public void setSecOtherAmt(BigDecimal secOtherAmt) {
        this.secOtherAmt = secOtherAmt;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getCommissionPay() {
        return commissionPay;
    }

    public void setCommissionPay(BigDecimal commissionPay) {
        this.commissionPay = commissionPay;
    }

    public Date getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(Date exchangeDate) {
        this.exchangeDate = exchangeDate;
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

    public Date getPaymentDate_pg() {
        return paymentDate_pg;
    }

    public void setPaymentDate_pg(Date paymentDate_pg) {
        this.paymentDate_pg = paymentDate_pg;
    }

    public BigDecimal getDealNum_pg() {
        return dealNum_pg;
    }

    public void setDealNum_pg(BigDecimal dealNum_pg) {
        this.dealNum_pg = dealNum_pg;
    }

    public BigDecimal getPrice_pg() {
        return price_pg;
    }

    public void setPrice_pg(BigDecimal price_pg) {
        this.price_pg = price_pg;
    }

    public BigDecimal getPaymentAmt_pg() {
        return paymentAmt_pg;
    }

    public void setPaymentAmt_pg(BigDecimal paymentAmt_pg) {
        this.paymentAmt_pg = paymentAmt_pg;
    }

    public String getMyAcctNo_pg() {
        return myAcctNo_pg;
    }

    public void setMyAcctNo_pg(String myAcctNo_pg) {
        this.myAcctNo_pg = myAcctNo_pg;
    }

    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    public String getMyAcctNo_sg() {
        return myAcctNo_sg;
    }

    public void setMyAcctNo_sg(String myAcctNo_sg) {
        this.myAcctNo_sg = myAcctNo_sg;
    }

    public BigDecimal getConfirmNum() {
        return confirmNum;
    }

    public void setConfirmNum(BigDecimal confirmNum) {
        this.confirmNum = confirmNum;
    }

    public BigDecimal getConfirmPrice() {
        return confirmPrice;
    }

    public void setConfirmPrice(BigDecimal confirmPrice) {
        this.confirmPrice = confirmPrice;
    }

    public BigDecimal getConfirmAmt() {
        return confirmAmt;
    }

    public void setConfirmAmt(BigDecimal confirmAmt) {
        this.confirmAmt = confirmAmt;
    }

    public Date getReceiptArriveDate() {
        return receiptArriveDate;
    }

    public void setReceiptArriveDate(Date receiptArriveDate) {
        this.receiptArriveDate = receiptArriveDate;
    }

    public BigDecimal getReceiptAmt() {
        return receiptAmt;
    }

    public void setReceiptAmt(BigDecimal receiptAmt) {
        this.receiptAmt = receiptAmt;
    }

    public Date getRefundArriveDate() {
        return refundArriveDate;
    }

    public void setRefundArriveDate(Date refundArriveDate) {
        this.refundArriveDate = refundArriveDate;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getMyAcctNo_qr() {
        return myAcctNo_qr;
    }

    public void setMyAcctNo_qr(String myAcctNo_qr) {
        this.myAcctNo_qr = myAcctNo_qr;
    }

    public BigDecimal getCommission_qr() {
        return commission_qr;
    }

    public void setCommission_qr(BigDecimal commission_qr) {
        this.commission_qr = commission_qr;
    }

    public BigDecimal getCommissionPay_qr() {
        return commissionPay_qr;
    }

    public void setCommissionPay_qr(BigDecimal commissionPay_qr) {
        this.commissionPay_qr = commissionPay_qr;
    }

    public BigDecimal getSettleAmt_qr() {
        return settleAmt_qr;
    }

    public void setSettleAmt_qr(BigDecimal settleAmt_qr) {
        this.settleAmt_qr = settleAmt_qr;
    }
}
