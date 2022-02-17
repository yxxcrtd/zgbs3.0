package com.joyintech.service.valuation.trademanage.bond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TrdBondTrade002Dto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdBondTrade002Dto> selectedItem;

    /**
     * 要素id
     */
    private String elementId;

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
    private String secId;

    /**
     * 债券简称
     */
    private String secAbbr;

    /**
     * 交易场所，银行间、上交所、深交所、柜面等
     */
    private String tradeMarket;

    /**
     * 证券账户
     */
    private String secManageAcctId;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 交易日
     */
    private Date tradeDate;

    /**
     * 交割日期
     */
    private Date settdateDate;

    /**
     * 资金交收日
     */
    private Date fundSettleDate;

    /**
     * 结算方式
     */
    //private String invAim;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 通道
     */
    private String chlId;

    /**
     * 单位面值
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal parValue;

    /**
     * 成交数量
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal dealNum;

    /**
     * 全价总额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal fpriceAmt;

    /**
     * 应收利息总额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal intAmt;

    /**
     * 净价总额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal cpriceAmt;

    /**
     * 净价
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal cprice;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 印花税
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal stampDuty;

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
     * 结算费
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal stlAmt;

    /**
     * 佣金
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal commission;

    /**
     * 交易手续费
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal dealFeeAmt;

    /**
     * 结算手续费
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal stlFeeAmt;

    /**
     * 其他应付费用
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal otherAmt;

    /**
     * 通道费
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal chlFeeAmt;

    /**
     * 正股代码
     */
    private String sharesCode;

    /**
     * 正股类型
     */
    private String sharesType;

    /**
     * 转股数
     */
    private BigDecimal sharesNum;

    /**
     * 正股交易本金
     */
    private BigDecimal expConValue;

    /**
     * 保证金
     */
    private BigDecimal bondAmt;

    /**
     * 交易金额
     */
    private BigDecimal tradeAmt;

    /**
     * 汇率
     */
    private BigDecimal rate;

    /**
     * 本币交易金额
     */
    private BigDecimal bTradeAmt;

    /**
     * 本币交易金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String myAcctNo;

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getSecAbbr() {
        return secAbbr;
    }

    public void setSecAbbr(String secAbbr) {
        this.secAbbr = secAbbr;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
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

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getSettdateDate() {
        return settdateDate;
    }

    public void setSettdateDate(Date settdateDate) {
        this.settdateDate = settdateDate;
    }

    public Date getFundSettleDate() {
        return fundSettleDate;
    }

    public void setFundSettleDate(Date fundSettleDate) {
        this.fundSettleDate = fundSettleDate;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getChlId() {
        return chlId;
    }

    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    public BigDecimal getParValue() {
        return parValue;
    }

    public void setParValue(BigDecimal parValue) {
        this.parValue = parValue;
    }

    public BigDecimal getDealNum() {
        return dealNum;
    }

    public void setDealNum(BigDecimal dealNum) {
        this.dealNum = dealNum;
    }

    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getCpriceAmt() {
        return cpriceAmt;
    }

    public void setCpriceAmt(BigDecimal cpriceAmt) {
        this.cpriceAmt = cpriceAmt;
    }

    public BigDecimal getCprice() {
        return cprice;
    }

    public void setCprice(BigDecimal cprice) {
        this.cprice = cprice;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
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

    public BigDecimal getStlAmt() {
        return stlAmt;
    }

    public void setStlAmt(BigDecimal stlAmt) {
        this.stlAmt = stlAmt;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    public BigDecimal getStlFeeAmt() {
        return stlFeeAmt;
    }

    public void setStlFeeAmt(BigDecimal stlFeeAmt) {
        this.stlFeeAmt = stlFeeAmt;
    }

    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    public BigDecimal getChlFeeAmt() {
        return chlFeeAmt;
    }

    public void setChlFeeAmt(BigDecimal chlFeeAmt) {
        this.chlFeeAmt = chlFeeAmt;
    }

    public String getSharesCode() {
        return sharesCode;
    }

    public void setSharesCode(String sharesCode) {
        this.sharesCode = sharesCode;
    }

    public String getSharesType() {
        return sharesType;
    }

    public void setSharesType(String sharesType) {
        this.sharesType = sharesType;
    }

    public BigDecimal getSharesNum() {
        return sharesNum;
    }

    public void setSharesNum(BigDecimal sharesNum) {
        this.sharesNum = sharesNum;
    }

    public BigDecimal getExpConValue() {
        return expConValue;
    }

    public void setExpConValue(BigDecimal expConValue) {
        this.expConValue = expConValue;
    }

    public BigDecimal getBondAmt() {
        return bondAmt;
    }

    public void setBondAmt(BigDecimal bondAmt) {
        this.bondAmt = bondAmt;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getbTradeAmt() {
        return bTradeAmt;
    }

    public void setbTradeAmt(BigDecimal bTradeAmt) {
        this.bTradeAmt = bTradeAmt;
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

    public List<TrdBondTrade002Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdBondTrade002Dto> selectedItem) {
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
}
