package com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TrdBondChangeTradeDto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdBondChangeTradeDto> selectedItem;

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
    private String oAssetId;

    /**
     * 债券简称
     */
    private String oAssetAbbr;

    /**
     * 转入证券代码
     */
    private String cAssetId;

    /**
     * 转入证券名称
     */
    private String cAssetAbbr;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 交易编号
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
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
     * 投资目的
     */
    private String invAim;

    /**
     * 转出证券数量
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal oTradeNum;

    /**
     * 转出证券金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal oTradeAmt;

    /**
     * 转入证券数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal cTradeNum;

    /**
     * 转入证券金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal cTradeAmt;

    /**
     * 币种
     */
    private String ccy;


    /**
     * 交易费用
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

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
    private String acctNo;

    /**
     * 转入补差款
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal cDifferenceAmt;

    /**
     * 转出费用
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal feeAmt;

    /**
     * 结算日期
     */
    private Date settleDate;

    /**
     * 转出补差款
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal oDifferenceAmt;

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

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
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

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
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

    public List<TrdBondChangeTradeDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdBondChangeTradeDto> selectedItem) {
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

    public String getoAssetId() {
        return oAssetId;
    }

    public void setoAssetId(String oAssetId) {
        this.oAssetId = oAssetId;
    }

    public String getoAssetAbbr() {
        return oAssetAbbr;
    }

    public void setoAssetAbbr(String oAssetAbbr) {
        this.oAssetAbbr = oAssetAbbr;
    }

    public String getcAssetId() {
        return cAssetId;
    }

    public void setcAssetId(String cAssetId) {
        this.cAssetId = cAssetId;
    }

    public String getcAssetAbbr() {
        return cAssetAbbr;
    }

    public void setcAssetAbbr(String cAssetAbbr) {
        this.cAssetAbbr = cAssetAbbr;
    }

    public BigDecimal getoTradeNum() {
        return oTradeNum;
    }

    public void setoTradeNum(BigDecimal oTradeNum) {
        this.oTradeNum = oTradeNum;
    }

    public BigDecimal getoTradeAmt() {
        return oTradeAmt;
    }

    public void setoTradeAmt(BigDecimal oTradeAmt) {
        this.oTradeAmt = oTradeAmt;
    }

    public BigDecimal getcTradeNum() {
        return cTradeNum;
    }

    public void setcTradeNum(BigDecimal cTradeNum) {
        this.cTradeNum = cTradeNum;
    }

    public BigDecimal getcTradeAmt() {
        return cTradeAmt;
    }

    public void setcTradeAmt(BigDecimal cTradeAmt) {
        this.cTradeAmt = cTradeAmt;
    }

    public BigDecimal getcDifferenceAmt() {
        return cDifferenceAmt;
    }

    public void setcDifferenceAmt(BigDecimal cDifferenceAmt) {
        this.cDifferenceAmt = cDifferenceAmt;
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getoDifferenceAmt() {
        return oDifferenceAmt;
    }

    public void setoDifferenceAmt(BigDecimal oDifferenceAmt) {
        this.oDifferenceAmt = oDifferenceAmt;
    }
}
