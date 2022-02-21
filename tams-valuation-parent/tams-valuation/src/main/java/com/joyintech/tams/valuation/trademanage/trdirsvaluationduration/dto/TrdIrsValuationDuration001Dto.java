package com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class TrdIrsValuationDuration001Dto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 交易编号
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    private String tradeId;

    /**
     * 原始交易编号
     */
    private String pTradeId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 产品名称
     */
    private String finprodName;

    /**
     * 交易类型
     */
    private String ps;

    /**
     * 交易日
     */
    private Date tradeDate;

    /**
     * 收付息类型
     */
    private String irsPsIntType;

    /**
     * 固定利息支付日
     */
    private Date fixPayDate;

    /**
     * 固定利息
     */
    private BigDecimal fixIntAmt;

    /**
     * 浮动利息支付日
     */
    private Date floatPayDate;

    /**
     * 浮动利息
     */
    private BigDecimal floatIntAmt;

    /**
     * 提前终止收付方向
     */
    private String beforeEndPs;

    /**
     * 提前终止名义本金
     */
    private BigDecimal beforeEndPrin;

    /**
     * 提前终止收付金额
     */
    private BigDecimal beforeEndAmt;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String acctNo;

    /**
     * 选中的数据
     */
    private transient List<TrdIrsValuationDuration001Dto> selectedItem;

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
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.利率互换存续期类型, ps))?
                SysDataUtil.getNameFromDataDict(DictCode.利率互换存续期类型, ps):ps;
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

    public List<TrdIrsValuationDuration001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdIrsValuationDuration001Dto> selectedItem) {
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

    public String getpTradeId() {
        return pTradeId;
    }

    public void setpTradeId(String pTradeId) {
        this.pTradeId = pTradeId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getIrsPsIntType() {
        return irsPsIntType;
    }

    public void setIrsPsIntType(String irsPsIntType) {
        this.irsPsIntType = irsPsIntType;
    }

    public Date getFixPayDate() {
        return fixPayDate;
    }

    public void setFixPayDate(Date fixPayDate) {
        this.fixPayDate = fixPayDate;
    }

    public String getFixPayDateStr() {
        return DateUtil.format(this.getFixPayDate());
    }

    public BigDecimal getFixIntAmt() {
        return fixIntAmt;
    }

    public void setFixIntAmt(BigDecimal fixIntAmt) {
        this.fixIntAmt = fixIntAmt;
    }

    public Date getFloatPayDate() {
        return floatPayDate;
    }

    public void setFloatPayDate(Date floatPayDate) {
        this.floatPayDate = floatPayDate;
    }

    public String getFloatPayDateStr() {
        return DateUtil.format(this.getFloatPayDate());
    }

    public BigDecimal getFloatIntAmt() {
        return floatIntAmt;
    }

    public void setFloatIntAmt(BigDecimal floatIntAmt) {
        this.floatIntAmt = floatIntAmt;
    }

    public String getBeforeEndPs() {
        return beforeEndPs;
    }

    public void setBeforeEndPs(String beforeEndPs) {
        this.beforeEndPs = beforeEndPs;
    }

    public BigDecimal getBeforeEndPrin() {
        return beforeEndPrin;
    }

    public void setBeforeEndPrin(BigDecimal beforeEndPrin) {
        this.beforeEndPrin = beforeEndPrin;
    }

    public BigDecimal getBeforeEndAmt() {
        return beforeEndAmt;
    }

    public void setBeforeEndAmt(BigDecimal beforeEndAmt) {
        this.beforeEndAmt = beforeEndAmt;
    }

    public String getFixIntAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getFixIntAmt()==null?"0.00":decimalFormat.format(this.getFixIntAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getFloatIntAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getFloatIntAmt()==null?"0.00":decimalFormat.format(this.getFloatIntAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getBeforeEndPrinStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getBeforeEndPrin()==null?"0.00":decimalFormat.format(this.getBeforeEndPrin().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getBeforeEndAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getBeforeEndAmt()==null?"0.00":decimalFormat.format(this.getBeforeEndAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

}
