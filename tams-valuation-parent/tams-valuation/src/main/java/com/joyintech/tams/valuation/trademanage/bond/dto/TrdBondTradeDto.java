package com.joyintech.tams.valuation.trademanage.bond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.trademanage.bond.validator.TrdBondTradeValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@PageId("TrdBondTrade002")
@ValidLogic(targetClass = TrdBondTradeValidator.class, groups = { ValidateGroupEdit.class,
        ValidateGroupDelete.class })
public class TrdBondTradeDto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdBondTradeDto> selectedItem;

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
     * 应付佣金
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal commissionPay;

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
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String myAcctNo;

    /**
     * 交易日
     */
    private Date exchangeDate;

    /**
     * 申购日期
     */
    private Date purchDate;

    /**
     * 结算日期
     */
    private Date settleDate;

    /**
     * 申购金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal entrustAmt;

    /**
     * 资金账户
     */
    private String myAcctNo_sq;

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
    private BigDecimal refundAmt;

    /**
     * 资金账户
     */
    private String myAcctNo_qr;

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @NotEmpty(fieldName = "TrdBondTrade.prodId", groups = { ValidateGroupEdit.class })
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @NotEmpty(fieldName = "TrdBondTrade.secId", groups = { ValidateGroupEdit.class })
    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    @NotEmpty(fieldName = "TrdBondTrade.secAbbr" , groups = { ValidateGroupEdit.class })
    public String getSecAbbr() {
        return secAbbr;
    }

    public void setSecAbbr(String secAbbr) {
        this.secAbbr = secAbbr;
    }

    @NotEmpty(fieldName = "TrdBondTrade.tradeMarket" , groups = { ValidateGroupEdit.class })
    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    @NotEmpty(fieldName = "TrdBondTrade.secManageAcctId" , groups = { ValidateGroupEdit.class })
    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    @NotEmpty(fieldName = "TrdBondTrade.ps" , groups = { ValidateGroupEdit.class })
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

    public String getTradeDateStr() {
        return DateUtil.format(this.getTradeDate());
    }

    public Date getSettdateDate() {
        return settdateDate;
    }

    public void setSettdateDate(Date settdateDate) {
        this.settdateDate = settdateDate;
    }

    public String getSettdateDateStr() {
        return DateUtil.format(this.getSettdateDate());
    }

    public Date getFundSettleDate() {
        return fundSettleDate;
    }

    public void setFundSettleDate(Date fundSettleDate) {
        this.fundSettleDate = fundSettleDate;
    }

    public String getFundSettleDateStr() {
        return DateUtil.format(this.getFundSettleDate());
    }

    @NotEmpty(fieldName = "TrdBondTrade.invAim" , groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdBondTrade.ccy" , groups = { ValidateGroupEdit.class })
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

    public BigDecimal getCommissionPay() {
        return commissionPay;
    }

    public void setCommissionPay(BigDecimal commissionPay) {
        this.commissionPay = commissionPay;
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

    public List<TrdBondTradeDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdBondTradeDto> selectedItem) {
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

    @NotEmpty(fieldName = "TrdBondTrade.prodName", groups = { ValidateGroupEdit.class })
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

    public Date getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(Date exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public Date getPurchDate() {
        return purchDate;
    }

    public void setPurchDate(Date purchDate) {
        this.purchDate = purchDate;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    public String getMyAcctNo_sq() {
        return myAcctNo_sq;
    }

    public void setMyAcctNo_sq(String myAcctNo_sq) {
        this.myAcctNo_sq = myAcctNo_sq;
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

    public BigDecimal getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(BigDecimal refundAmt) {
        this.refundAmt = refundAmt;
    }

    public String getMyAcctNo_qr() {
        return myAcctNo_qr;
    }

    public void setMyAcctNo_qr(String myAcctNo_qr) {
        this.myAcctNo_qr = myAcctNo_qr;
    }

    /**
     * 取得 交易场所str
     *
     * @return 交易场所
     */
    public String getTradeMarketStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易场所, tradeMarket))?
                SysDataUtil.getNameFromDataDict(DictCode.交易场所, tradeMarket):tradeMarket;
    }

    /**
     * 取得 交易方向str
     *
     * @return 交易方向
     */
    public String getPsStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps))?
                SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps):ps;
    }

    public String getInvAimStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.投资目的, invAim))?
                SysDataUtil.getNameFromDataDict(DictCode.投资目的, invAim):invAim;
    }

    /**
     * 取得 币种Fmt
     *
     * @return 币种
     */
    public String getCcyFmt() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.币种, ccy))?
                SysDataUtil.getNameFromDataDict(DictCode.币种, ccy):ccy;
    }

    public String getDealNumStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getDealNum()==null?"0.00":decimalFormat.format(this.getDealNum().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getFpriceAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getFpriceAmt()==null?"0.00":decimalFormat.format(this.getFpriceAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getIntAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getIntAmt()==null?"0.00":decimalFormat.format(this.getIntAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }


    public String getTradeAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getTradeAmt()==null?"0.00":decimalFormat.format(this.getTradeAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    /**
     * 取得 数据来源str
     *
     * @return 交易方向
     */
    public String getDataSourceStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.生成方式, dataSource))?
                SysDataUtil.getNameFromDataDict(DictCode.生成方式, dataSource):dataSource;
    }
}
