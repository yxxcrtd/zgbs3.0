package com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class TrdBondValuationDuration001Dto extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 交易编号
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    private String tradeId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 资产代码
     */
    private String assetId;

    /**
     * 资产简称
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
     * 会计分类
     */
    private String invAim;

    /**
     * 实收利息总额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal intAmtReal;

    /**
     * 实收本金
     */
    private BigDecimal actCapi;

    /**
     * 计划支付日
     */
    private Date plandate;

    /**
     * 资金交收日
     */
    private Date fundSettleDate;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 本币交易金额
     */
    private BigDecimal bTradeAmt;

    /**
     * 资金账户
     */
    private String acctNo;

    /**
     * 选中的数据
     */
    private transient List<TrdBondValuationDuration001Dto> selectedItem;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 要素id
     */
    private String elementId;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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

    /**
     * 取得 交易方向str
     *
     * @return 交易方向
     */
    public String getPsStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps))?
                SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps):ps;
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

    public String getInvAimStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.投资目的, invAim))?
                SysDataUtil.getNameFromDataDict(DictCode.投资目的, invAim):invAim;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
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

    public BigDecimal getbTradeAmt() {
        return bTradeAmt;
    }

    public void setbTradeAmt(BigDecimal bTradeAmt) {
        this.bTradeAmt = bTradeAmt;
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

    public List<TrdBondValuationDuration001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdBondValuationDuration001Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public BigDecimal getIntAmtReal() {
        return intAmtReal;
    }

    public void setIntAmtReal(BigDecimal intAmtReal) {
        this.intAmtReal = intAmtReal;
    }

    public BigDecimal getActCapi() {
        return actCapi;
    }

    public void setActCapi(BigDecimal actCapi) {
        this.actCapi = actCapi;
    }

    public Date getPlandate() {
        return plandate;
    }

    public void setPlandate(Date plandate) {
        this.plandate = plandate;
    }

    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }


    public String getBTradeAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getbTradeAmt()==null?"0.00":decimalFormat.format(this.getbTradeAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }
}
