package com.joyintech.service.valuation.trademanage.trdstockvaluationduration.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TrdStockValuationDurationDto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdStockValuationDurationDto> selectedItem;

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

    /**
     * 权益登记日
     */
    private Date regDate_sz;

    /**
     * 派息日
     */
    private Date dividDate_sz;

    /**
     * 权益登记日股本
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal amount_sz;

    /**
     * 分红股本
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal devidentAmt_sz;

    /**
     * 资金账户
     */
    private String myAcctNo_sz;

    /**
     * 权益登记日
     */
    private Date regDate_fhsz;

    /**
     * 派息日
     */
    private Date dividDate_fhsz;

    /**
     * 权益登记日股本
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal amount_fhsz;

    /**
     * 分红金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal bonusAmt_fhsz;

    /**
     * 分红股本
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal devidentAmt_fhsz;

    /**
     * 结算金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal settleAmt_fhsz;

    /**
     * 资金账户
     */
    private String myAcctNo_fhsz;

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
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

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getExRightsDate() {
        return exRightsDate;
    }

    public void setExRightsDate(Date exRightsDate) {
        this.exRightsDate = exRightsDate;
    }

    public Date getDividDate() {
        return dividDate;
    }

    public void setDividDate(Date dividDate) {
        this.dividDate = dividDate;
    }

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

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getDevidentAmt() {
        return devidentAmt;
    }

    public void setDevidentAmt(BigDecimal devidentAmt) {
        this.devidentAmt = devidentAmt;
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

    public List<TrdStockValuationDurationDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdStockValuationDurationDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public Date getRegDate_sz() {
        return regDate_sz;
    }

    public void setRegDate_sz(Date regDate_sz) {
        this.regDate_sz = regDate_sz;
    }

    public Date getDividDate_sz() {
        return dividDate_sz;
    }

    public void setDividDate_sz(Date dividDate_sz) {
        this.dividDate_sz = dividDate_sz;
    }

    public BigDecimal getAmount_sz() {
        return amount_sz;
    }

    public void setAmount_sz(BigDecimal amount_sz) {
        this.amount_sz = amount_sz;
    }

    public BigDecimal getDevidentAmt_sz() {
        return devidentAmt_sz;
    }

    public void setDevidentAmt_sz(BigDecimal devidentAmt_sz) {
        this.devidentAmt_sz = devidentAmt_sz;
    }

    public String getMyAcctNo_sz() {
        return myAcctNo_sz;
    }

    public void setMyAcctNo_sz(String myAcctNo_sz) {
        this.myAcctNo_sz = myAcctNo_sz;
    }

    public Date getRegDate_fhsz() {
        return regDate_fhsz;
    }

    public void setRegDate_fhsz(Date regDate_fhsz) {
        this.regDate_fhsz = regDate_fhsz;
    }

    public Date getDividDate_fhsz() {
        return dividDate_fhsz;
    }

    public void setDividDate_fhsz(Date dividDate_fhsz) {
        this.dividDate_fhsz = dividDate_fhsz;
    }

    public BigDecimal getAmount_fhsz() {
        return amount_fhsz;
    }

    public void setAmount_fhsz(BigDecimal amount_fhsz) {
        this.amount_fhsz = amount_fhsz;
    }

    public BigDecimal getBonusAmt_fhsz() {
        return bonusAmt_fhsz;
    }

    public void setBonusAmt_fhsz(BigDecimal bonusAmt_fhsz) {
        this.bonusAmt_fhsz = bonusAmt_fhsz;
    }

    public BigDecimal getDevidentAmt_fhsz() {
        return devidentAmt_fhsz;
    }

    public void setDevidentAmt_fhsz(BigDecimal devidentAmt_fhsz) {
        this.devidentAmt_fhsz = devidentAmt_fhsz;
    }

    public BigDecimal getSettleAmt_fhsz() {
        return settleAmt_fhsz;
    }

    public void setSettleAmt_fhsz(BigDecimal settleAmt_fhsz) {
        this.settleAmt_fhsz = settleAmt_fhsz;
    }

    public String getMyAcctNo_fhsz() {
        return myAcctNo_fhsz;
    }

    public void setMyAcctNo_fhsz(String myAcctNo_fhsz) {
        this.myAcctNo_fhsz = myAcctNo_fhsz;
    }
}
