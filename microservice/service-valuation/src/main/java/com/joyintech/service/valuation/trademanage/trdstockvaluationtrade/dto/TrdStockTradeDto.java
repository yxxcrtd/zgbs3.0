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
    private String tradeDate;

    /**
     * 缴款日
     */
    private String paymentDate;

    /**
     * 交割日期
     */
    private String settdateDate;

    /**
     * 交收日
     */
    private String settleDate;

    /**
     * 是否锁定
     */
    private String isLock;

    /**
     * 锁定期截止日
     */
    private String endDate;

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

    /**
     * 成交日期
     */
    private Date dealDate;

    /**
     * 资金交收日期
     */
    private Date fundSettleDate;

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
     * 汇率日期
     */
    private Date exchangeDate;

    /**
     * 配售代码
     */
    private String saleCode;

    /**
     * 结算日期
     */
    private Date settleDate_pg;

    /**
     * 资金交收日期
     */
    private Date fundSettleDate_pg;

    /**
     * 成交数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal dealNum_pg;

    /**
     * 成交价格
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradedPrice;

    /**
     * 缴款金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal paymentAmt_pg;

    /**
     * 资金账户
     */
    private String myAcctNo_pg;

    /**
     * 申购日期
     */
    private Date purchDate;

    /**
     * 结算日期
     */
    private Date settleDate_sg;

    /**
     * 申购金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal entrustAmt;

    /**
     * 资金账户
     */
    private String myAcctNo_sg;

    /**
     * 确认日期
     */
    private Date confirmDate;

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
     * 资金账户
     */
    private String myAcctNo_qr;

    /**
     * 缴款金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal paymentAmt;

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

    public Date getDealDate() {
        return dealDate;
    }

    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    public Date getFundSettleDate() {
        return fundSettleDate;
    }

    public void setFundSettleDate(Date fundSettleDate) {
        this.fundSettleDate = fundSettleDate;
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

    public Date getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(Date exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public Date getSettleDate_pg() {
        return settleDate_pg;
    }

    public void setSettleDate_pg(Date settleDate_pg) {
        this.settleDate_pg = settleDate_pg;
    }

    public Date getFundSettleDate_pg() {
        return fundSettleDate_pg;
    }

    public void setFundSettleDate_pg(Date fundSettleDate_pg) {
        this.fundSettleDate_pg = fundSettleDate_pg;
    }

    public BigDecimal getDealNum_pg() {
        return dealNum_pg;
    }

    public void setDealNum_pg(BigDecimal dealNum_pg) {
        this.dealNum_pg = dealNum_pg;
    }

    public BigDecimal getTradedPrice() {
        return tradedPrice;
    }

    public void setTradedPrice(BigDecimal tradedPrice) {
        this.tradedPrice = tradedPrice;
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

    public Date getPurchDate() {
        return purchDate;
    }

    public void setPurchDate(Date purchDate) {
        this.purchDate = purchDate;
    }

    public Date getSettleDate_sg() {
        return settleDate_sg;
    }

    public void setSettleDate_sg(Date settleDate_sg) {
        this.settleDate_sg = settleDate_sg;
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

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
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

    public String getMyAcctNo_qr() {
        return myAcctNo_qr;
    }

    public void setMyAcctNo_qr(String myAcctNo_qr) {
        this.myAcctNo_qr = myAcctNo_qr;
    }

    public BigDecimal getPaymentAmt() {
        return paymentAmt;
    }

    public void setPaymentAmt(BigDecimal paymentAmt) {
        this.paymentAmt = paymentAmt;
    }
}
