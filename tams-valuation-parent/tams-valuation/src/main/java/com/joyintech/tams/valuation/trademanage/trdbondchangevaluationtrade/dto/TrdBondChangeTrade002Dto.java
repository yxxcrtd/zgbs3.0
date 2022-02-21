package com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.validator.TrdBondChangeTradeValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@PageId("TrdBondChangeTrade002")
@ValidLogic(targetClass = TrdBondChangeTradeValidator.class, groups = { ValidateGroupEdit.class,
        ValidateGroupDelete.class })
public class TrdBondChangeTrade002Dto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdBondChangeTrade002Dto> selectedItem;

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

    @NotEmpty(fieldName = "TrdBondChangeTrade002.prodId", groups = { ValidateGroupEdit.class })
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.ps", groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdBondChangeTrade002.tradeDate", groups = { ValidateGroupEdit.class })
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.settdateDate", groups = { ValidateGroupEdit.class })
    public Date getSettdateDate() {
        return settdateDate;
    }

    public void setSettdateDate(Date settdateDate) {
        this.settdateDate = settdateDate;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.invAim", groups = { ValidateGroupEdit.class })
    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.ccy", groups = { ValidateGroupEdit.class })
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.otherAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.tradeAmt", groups = { ValidateGroupEdit.class })
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

    public List<TrdBondChangeTrade002Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdBondChangeTrade002Dto> selectedItem) {
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

    @NotEmpty(fieldName = "TrdBondChangeTrade002.prodName", groups = { ValidateGroupEdit.class })
    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.settleAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.oAssetId", groups = { ValidateGroupEdit.class })
    public String getoAssetId() {
        return oAssetId;
    }

    public void setoAssetId(String oAssetId) {
        this.oAssetId = oAssetId;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.oAssetAbbr", groups = { ValidateGroupEdit.class })
    public String getoAssetAbbr() {
        return oAssetAbbr;
    }

    public void setoAssetAbbr(String oAssetAbbr) {
        this.oAssetAbbr = oAssetAbbr;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.cAssetId", groups = { ValidateGroupEdit.class })
    public String getcAssetId() {
        return cAssetId;
    }

    public void setcAssetId(String cAssetId) {
        this.cAssetId = cAssetId;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.cAssetAbbr", groups = { ValidateGroupEdit.class })
    public String getcAssetAbbr() {
        return cAssetAbbr;
    }

    public void setcAssetAbbr(String cAssetAbbr) {
        this.cAssetAbbr = cAssetAbbr;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.oTradeNum", groups = { ValidateGroupEdit.class })
    public BigDecimal getoTradeNum() {
        return oTradeNum;
    }

    public void setoTradeNum(BigDecimal oTradeNum) {
        this.oTradeNum = oTradeNum;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.oTradeAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getoTradeAmt() {
        return oTradeAmt;
    }

    public void setoTradeAmt(BigDecimal oTradeAmt) {
        this.oTradeAmt = oTradeAmt;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.cTradeNum", groups = { ValidateGroupEdit.class })
    public BigDecimal getcTradeNum() {
        return cTradeNum;
    }

    public void setcTradeNum(BigDecimal cTradeNum) {
        this.cTradeNum = cTradeNum;
    }

    @NotEmpty(fieldName = "TrdBondChangeTrade002.cTradeAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getcTradeAmt() {
        return cTradeAmt;
    }

    public void setcTradeAmt(BigDecimal cTradeAmt) {
        this.cTradeAmt = cTradeAmt;
    }
}
