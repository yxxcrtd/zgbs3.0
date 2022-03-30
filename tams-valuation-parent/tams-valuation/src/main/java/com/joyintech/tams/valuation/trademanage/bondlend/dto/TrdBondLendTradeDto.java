package com.joyintech.tams.valuation.trademanage.bondlend.dto;

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
import com.joyintech.tams.valuation.trademanage.bondlend.validator.TrdBondLendTrade001Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@PageId("TrdBondLendTrade002")
@ValidLogic(targetClass = TrdBondLendTrade001Validator.class, groups = { ValidateGroupEdit.class,
        ValidateGroupDelete.class })
public class TrdBondLendTradeDto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdBondLendTradeDto> selectedItem;

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
     * 借贷期限
     */
    private Long term;

    /**
     * 金融产品代码
     */
    private String prodId;

    /**
     * 金融产品名称
     */
    private String prodName;

    private String finprodType;

    /**
     * 债券代码
     */
    private String secId;

    /**
     * 债券简称
     */
    private String secAbbr;

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
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 券面总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal prinAmt;

    /**
     * 借贷费率
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal feeRate;

    /**
     * 借贷费用
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal fee;

    /**
     * 币种
     */
    private String ccy;

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
     * 现金交割金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal settAmt;

    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String acctNo;

    @NotEmpty(fieldName = "TrdBondLendTrade.prodId", groups = { ValidateGroupEdit.class })
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getFinprodType() {
        return finprodType;
    }

    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

    @NotEmpty(fieldName = "TrdBondLendTrade.secId", groups = { ValidateGroupEdit.class })
    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    @NotEmpty(fieldName = "TrdBondLendTrade.secAbbr", groups = { ValidateGroupEdit.class })
    public String getSecAbbr() {
        return secAbbr;
    }

    public void setSecAbbr(String secAbbr) {
        this.secAbbr = secAbbr;
    }

    @NotEmpty(fieldName = "TrdBondLendTrade.ps", groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdBondLendTrade.tradeDate", groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdBondLendTrade.ccy", groups = { ValidateGroupEdit.class })
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    @NotEmpty(fieldName = "TrdBondLendTrade.stlFeeAmt", groups = { ValidateGroupEdit.class })
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

    public List<TrdBondLendTradeDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdBondLendTradeDto> selectedItem) {
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

    @NotEmpty(fieldName = "TrdBondLendTrade.prodName", groups = { ValidateGroupEdit.class })
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

    @NotEmpty(fieldName = "TrdBondLendTrade.vdate", groups = { ValidateGroupEdit.class })
    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    @NotEmpty(fieldName = "TrdBondLendTrade.mdate", groups = { ValidateGroupEdit.class })
    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    @NotEmpty(fieldName = "TrdBondLendTrade.prinAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    @NotEmpty(fieldName = "TrdBondLendTrade.feeRate", groups = { ValidateGroupEdit.class })
    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    @NotEmpty(fieldName = "TrdBondLendTrade.fee", groups = { ValidateGroupEdit.class })
    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    @NotEmpty(fieldName = "TrdBondLendTrade.acctNo", groups = { ValidateGroupEdit.class })
    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    @NotEmpty(fieldName = "TrdBondLendTrade.term", groups = { ValidateGroupEdit.class })
    public Long getTerm() {
        return term;
    }

    public void setTerm(Long term) {
        this.term = term;
    }

    public BigDecimal getSettAmt() {
        return settAmt;
    }

    public void setSettAmt(BigDecimal settAmt) {
        this.settAmt = settAmt;
    }

    /**
     * 取得 交易方向str
     *
     * @return 交易方向
     */
    public String getPsStr() {
        String psName="";
        if(StringUtil.isNotEmpty(ps)){
            if(ps.equals("01")){
                psName = "融入";
            }else if(ps.equals("02")){
                psName = "融出";
            }else if(ps.equals("03")){
                psName = "到期";
            }
        }
        return psName;
    }

    public String getTradeDateStr() {
        return DateUtil.format(this.getTradeDate());
    }

    public String getSettdateDateStr() {
        return DateUtil.format(this.getSettdateDate());
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

    public String getPrinAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getPrinAmt()==null?"0.00":decimalFormat.format(this.getPrinAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getFeeStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getFee()==null?"0.00":decimalFormat.format(this.getFee().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getFeeRateStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0000#");
        return this.getFeeRate()==null?"0.0000":decimalFormat.format(this.getFeeRate().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getVdateStr() {
        return DateUtil.format(this.getVdate());
    }

    public String getMdateStr() {
        return DateUtil.format(this.getMdate());
    }
}
