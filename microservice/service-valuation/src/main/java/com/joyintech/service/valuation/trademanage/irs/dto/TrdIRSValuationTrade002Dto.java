package com.joyintech.service.valuation.trademanage.irs.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class TrdIRSValuationTrade002Dto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdIRSValuationTrade002Dto> selectedItem;

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
     * 产品名称
     */
    private String prodName;

    /**
     * 交易类型
     */
    private String ps;

    /**
     * 交易日
     */
    private Date tradeDate;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 名义本金
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal prin;

    /**
     * 固定利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yield;

    /**
     * 固定计息基础
     */
    private String fixBasis;

    /**
     *固定利率付息频率
     */
    private String fixPayCycle;

    /**
     * 固定利率首期起息日
     */
    private Date firstPayDateR;

    /**
     * 挂钩利率
     */
    private String intType;

    /**
     * 利差(BP)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal spreadRate;

    /**
     * 浮动计息基础
     */
    private String floatBasis;

    /**
     *浮动利率付息频率
     */
    private String floatPayCycle;

    /**
     * 浮动利率首期起息日
     */
    private Date firstPayDateF;

    /**
     * 首期利率确定日
     */
    private Date firstResetDate;

    /**
     * 利率重置频率
     */
    private String resetFreq;

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
     * 币种
     */
    private String ccy;

    /**
     * 结算金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String acctNo;

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.ps", groups = { ValidateGroupEdit.class })
    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.tradeId", groups = { ValidateGroupEdit.class })
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.tradeDate", groups = { ValidateGroupEdit.class })
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.ccy", groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.acctNo", groups = { ValidateGroupEdit.class })
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

    public List<TrdIRSValuationTrade002Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdIRSValuationTrade002Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getOutTradeId() {
        return outTradeId;
    }

    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.settleAmt", groups = { ValidateGroupEdit.class })
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

    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.vdate", groups = { ValidateGroupEdit.class })
    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.mdate", groups = { ValidateGroupEdit.class })
    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.prin", groups = { ValidateGroupEdit.class })
    public BigDecimal getPrin() {
        return prin;
    }

    public void setPrin(BigDecimal prin) {
        this.prin = prin;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.yield", groups = { ValidateGroupEdit.class })
    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.fixPayCycle", groups = { ValidateGroupEdit.class })
    public String getFixPayCycle() {
        return fixPayCycle;
    }

    public void setFixPayCycle(String fixPayCycle) {
        this.fixPayCycle = fixPayCycle;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.firstPayDateR", groups = { ValidateGroupEdit.class })
    public Date getFirstPayDateR() {
        return firstPayDateR;
    }

    public void setFirstPayDateR(Date firstPayDateR) {
        this.firstPayDateR = firstPayDateR;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.intType", groups = { ValidateGroupEdit.class })
    public String getIntType() {
        return intType;
    }

    public void setIntType(String intType) {
        this.intType = intType;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.spreadRate", groups = { ValidateGroupEdit.class })
    public BigDecimal getSpreadRate() {
        return spreadRate;
    }

    public void setSpreadRate(BigDecimal spreadRate) {
        this.spreadRate = spreadRate;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.floatPayCycle", groups = { ValidateGroupEdit.class })
    public String getFloatPayCycle() {
        return floatPayCycle;
    }

    public void setFloatPayCycle(String floatPayCycle) {
        this.floatPayCycle = floatPayCycle;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.firstPayDateF", groups = { ValidateGroupEdit.class })
    public Date getFirstPayDateF() {
        return firstPayDateF;
    }

    public void setFirstPayDateF(Date firstPayDateF) {
        this.firstPayDateF = firstPayDateF;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.prodId", groups = { ValidateGroupEdit.class })
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.prodName", groups = { ValidateGroupEdit.class })
    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.fixBasis", groups = { ValidateGroupEdit.class })
    public String getFixBasis() {
        return fixBasis;
    }

    public void setFixBasis(String fixBasis) {
        this.fixBasis = fixBasis;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.floatBasis", groups = { ValidateGroupEdit.class })
    public String getFloatBasis() {
        return floatBasis;
    }

    public void setFloatBasis(String floatBasis) {
        this.floatBasis = floatBasis;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.firstResetDate", groups = { ValidateGroupEdit.class })
    public Date getFirstResetDate() {
        return firstResetDate;
    }

    public void setFirstResetDate(Date firstResetDate) {
        this.firstResetDate = firstResetDate;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.resetFreq", groups = { ValidateGroupEdit.class })
    public String getResetFreq() {
        return resetFreq;
    }

    public void setResetFreq(String resetFreq) {
        this.resetFreq = resetFreq;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.dealFeeAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    @NotEmpty(fieldName = "TrdIRSValuationTrade002.stlFeeAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getStlFeeAmt() {
        return stlFeeAmt;
    }

    public void setStlFeeAmt(BigDecimal stlFeeAmt) {
        this.stlFeeAmt = stlFeeAmt;
    }
}
