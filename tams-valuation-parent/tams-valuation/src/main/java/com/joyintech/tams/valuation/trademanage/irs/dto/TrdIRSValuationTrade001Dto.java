package com.joyintech.tams.valuation.trademanage.irs.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
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

public class TrdIRSValuationTrade001Dto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

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
     *浮动利率付息频率
     */
    private String floatPayCycle;

    /**
     * 浮动利率首期起息日
     */
    private Date firstPayDateF;

    /**
     * 交易对手
     */
    private String counterId;

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

    /**
     * 选中的数据
     */
    private transient List<TrdIRSValuationTrade001Dto> selectedItem;

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
        String psName="";
        return psName;
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

    public List<TrdIRSValuationTrade001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdIRSValuationTrade001Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getOutTradeId() {
        return outTradeId;
    }

    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
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

    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public String getVdateStr() {
        return DateUtil.format(this.getVdate());
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getMdateStr() {
        return DateUtil.format(this.getMdate());
    }

    public BigDecimal getPrin() {
        return prin;
    }

    public void setPrin(BigDecimal prin) {
        this.prin = prin;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public String getFixPayCycle() {
        return fixPayCycle;
    }

    public void setFixPayCycle(String fixPayCycle) {
        this.fixPayCycle = fixPayCycle;
    }

    public Date getFirstPayDateR() {
        return firstPayDateR;
    }

    public void setFirstPayDateR(Date firstPayDateR) {
        this.firstPayDateR = firstPayDateR;
    }

    public String getFirstPayDateRStr() {
        return DateUtil.format(this.getFirstPayDateR());
    }

    public String getIntType() {
        return intType;
    }

    public void setIntType(String intType) {
        this.intType = intType;
    }

    public BigDecimal getSpreadRate() {
        return spreadRate;
    }

    public void setSpreadRate(BigDecimal spreadRate) {
        this.spreadRate = spreadRate;
    }

    public String getFloatPayCycle() {
        return floatPayCycle;
    }

    public void setFloatPayCycle(String floatPayCycle) {
        this.floatPayCycle = floatPayCycle;
    }

    public Date getFirstPayDateF() {
        return firstPayDateF;
    }

    public void setFirstPayDateF(Date firstPayDateF) {
        this.firstPayDateF = firstPayDateF;
    }

    public String getFirstPayDateFStr() {
        return DateUtil.format(this.getFirstPayDateF());
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }
}
