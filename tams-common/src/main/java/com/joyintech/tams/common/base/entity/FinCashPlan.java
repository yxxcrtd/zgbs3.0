package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class FinCashPlan extends FinCashPlanKey {
    /**
     * 现金流序号，从1开始
     */
    private Long cashNum;

    /**
     * 现金流类型，中间还本、中间收息、到期还本、到期收息、费用支付等
     */
    private String cashType;

    /**
     * 未调整的计算开始日
     */
    private Date vdateUnadjust;

    /**
     * 未调整的计算结束日
     */
    private Date mdateUnadjust;

    /**
     * 未调整的计划支付日
     */
    private Date payDateUnadjust;

    /**
     * 调整后的计算开始日
     */
    private Date vdate;

    /**
     * 调整后的计划支付日
     */
    private Date payDate;

    /**
     * 计算周期天数
     */
    private Long termdays;

    /**
     * 理论计息年度开始日，A/A_BOND适用
     */
    private Date vdateY;

    /**
     * 完整周期开始日
     */
    private Date vdateTerm;

    /**
     * 完整周期结束日
     */
    private Date mdateTerm;

    /**
     * 完整周期天数
     */
    private Long termdaysTerm;

    /**
     * 计划支付本金，针对现金流类型为本金的
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal prinAmt;

    /**
     * 计息本金，存计划支付利息对应的本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intPrinAmt;

    /**
     * 计划支付利息，还本计息区间对应的利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intAmt;

    /**
     * 是否支付利息，针对现金流类型为本金的
     */
    private String isPayInt;

    /**
     * 计划支付现金流，如果支付利息：计划支付本金+计划支付利息。如果不支付利息：计划支付本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cashAmt;

    /**
     * 现金流基数，存100或者总金额的具体数值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cashBaseamt;

    /**
     * 现金流单位，单位、百元、 总金额
     */
    private String cashUnitType;

    /**
     * 计息算法，分摊法、累计法，目前无
     */
    private String calcFunction;

    /**
     * 年付息次数
     */
    private Long frequency;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType;

    /**
     * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType2;

    /**
     * 分支序号
     */
    private Long branch;

    /**
     * 支付方式，现金、红利再投等
     */
    private String payType;

    /**
     * 区间净收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal rangeYield;

    /**
     * 备注
     */
    private String remark;

    /**
     * 计息基础
     */
    private String basis;

    /**
     * 取得 现金流序号，从1开始
     * @return 现金流序号，从1开始
     */
    public Long getCashNum() {
        return cashNum;
    }

    /**
     * 设置 现金流序号，从1开始
     * @param cashNum 现金流序号，从1开始
     */
    public void setCashNum(Long cashNum) {
        this.cashNum = cashNum;
    }

    /**
     * 取得 现金流类型，中间还本、中间收息、到期还本、到期收息、费用支付等
     * @return 现金流类型，中间还本、中间收息、到期还本、到期收息、费用支付等
     */
    public String getCashType() {
        return cashType;
    }

    /**
     * 设置 现金流类型，中间还本、中间收息、到期还本、到期收息、费用支付等
     * @param cashType 现金流类型，中间还本、中间收息、到期还本、到期收息、费用支付等
     */
    public void setCashType(String cashType) {
        this.cashType = cashType == null ? null : cashType.trim();
    }

    /**
     * 取得 未调整的计算开始日
     * @return 未调整的计算开始日
     */
    public Date getVdateUnadjust() {
        return vdateUnadjust;
    }

    /**
     * 设置 未调整的计算开始日
     * @param vdateUnadjust 未调整的计算开始日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdateUnadjust(Date vdateUnadjust) {
        this.vdateUnadjust = vdateUnadjust;
    }

    /**
     * 取得 未调整的计算结束日
     * @return 未调整的计算结束日
     */
    public Date getMdateUnadjust() {
        return mdateUnadjust;
    }

    /**
     * 设置 未调整的计算结束日
     * @param mdateUnadjust 未调整的计算结束日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdateUnadjust(Date mdateUnadjust) {
        this.mdateUnadjust = mdateUnadjust;
    }

    /**
     * 取得 未调整的计划支付日
     * @return 未调整的计划支付日
     */
    public Date getPayDateUnadjust() {
        return payDateUnadjust;
    }

    /**
     * 设置 未调整的计划支付日
     * @param payDateUnadjust 未调整的计划支付日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPayDateUnadjust(Date payDateUnadjust) {
        this.payDateUnadjust = payDateUnadjust;
    }

    /**
     * 取得 调整后的计算开始日
     * @return 调整后的计算开始日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 调整后的计算开始日
     * @param vdate 调整后的计算开始日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 调整后的计划支付日
     * @return 调整后的计划支付日
     */
    public Date getPayDate() {
        return payDate;
    }

    /**
     * 设置 调整后的计划支付日
     * @param payDate 调整后的计划支付日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    /**
     * 取得 计算周期天数
     * @return 计算周期天数
     */
    public Long getTermdays() {
        return termdays;
    }

    /**
     * 设置 计算周期天数
     * @param termdays 计算周期天数
     */
    public void setTermdays(Long termdays) {
        this.termdays = termdays;
    }

    /**
     * 取得 理论计息年度开始日，A/A_BOND适用
     * @return 理论计息年度开始日，A/A_BOND适用
     */
    public Date getVdateY() {
        return vdateY;
    }

    /**
     * 设置 理论计息年度开始日，A/A_BOND适用
     * @param vdateY 理论计息年度开始日，A/A_BOND适用
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdateY(Date vdateY) {
        this.vdateY = vdateY;
    }

    /**
     * 取得 完整周期开始日
     * @return 完整周期开始日
     */
    public Date getVdateTerm() {
        return vdateTerm;
    }

    /**
     * 设置 完整周期开始日
     * @param vdateTerm 完整周期开始日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdateTerm(Date vdateTerm) {
        this.vdateTerm = vdateTerm;
    }

    /**
     * 取得 完整周期结束日
     * @return 完整周期结束日
     */
    public Date getMdateTerm() {
        return mdateTerm;
    }

    /**
     * 设置 完整周期结束日
     * @param mdateTerm 完整周期结束日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdateTerm(Date mdateTerm) {
        this.mdateTerm = mdateTerm;
    }

    /**
     * 取得 完整周期天数
     * @return 完整周期天数
     */
    public Long getTermdaysTerm() {
        return termdaysTerm;
    }

    /**
     * 设置 完整周期天数
     * @param termdaysTerm 完整周期天数
     */
    public void setTermdaysTerm(Long termdaysTerm) {
        this.termdaysTerm = termdaysTerm;
    }

    /**
     * 取得 计划支付本金，针对现金流类型为本金的
     * @return 计划支付本金，针对现金流类型为本金的
     */
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    /**
     * 设置 计划支付本金，针对现金流类型为本金的
     * @param prinAmt 计划支付本金，针对现金流类型为本金的
     */
    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    /**
     * 取得 计息本金，存计划支付利息对应的本金
     * @return 计息本金，存计划支付利息对应的本金
     */
    public BigDecimal getIntPrinAmt() {
        return intPrinAmt;
    }

    /**
     * 设置 计息本金，存计划支付利息对应的本金
     * @param intPrinAmt 计息本金，存计划支付利息对应的本金
     */
    public void setIntPrinAmt(BigDecimal intPrinAmt) {
        this.intPrinAmt = intPrinAmt;
    }

    /**
     * 取得 计划支付利息，还本计息区间对应的利息
     * @return 计划支付利息，还本计息区间对应的利息
     */
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    /**
     * 设置 计划支付利息，还本计息区间对应的利息
     * @param intAmt 计划支付利息，还本计息区间对应的利息
     */
    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    /**
     * 取得 是否支付利息，针对现金流类型为本金的
     * @return 是否支付利息，针对现金流类型为本金的
     */
    public String getIsPayInt() {
        return isPayInt;
    }

    /**
     * 设置 是否支付利息，针对现金流类型为本金的
     * @param isPayInt 是否支付利息，针对现金流类型为本金的
     */
    public void setIsPayInt(String isPayInt) {
        this.isPayInt = isPayInt == null ? null : isPayInt.trim();
    }

    /**
     * 取得 计划支付现金流，如果支付利息：计划支付本金+计划支付利息。如果不支付利息：计划支付本金
     * @return 计划支付现金流，如果支付利息：计划支付本金+计划支付利息。如果不支付利息：计划支付本金
     */
    public BigDecimal getCashAmt() {
        return cashAmt;
    }

    /**
     * 设置 计划支付现金流，如果支付利息：计划支付本金+计划支付利息。如果不支付利息：计划支付本金
     * @param cashAmt 计划支付现金流，如果支付利息：计划支付本金+计划支付利息。如果不支付利息：计划支付本金
     */
    public void setCashAmt(BigDecimal cashAmt) {
        this.cashAmt = cashAmt;
    }

    /**
     * 取得 现金流基数，存100或者总金额的具体数值
     * @return 现金流基数，存100或者总金额的具体数值
     */
    public BigDecimal getCashBaseamt() {
        return cashBaseamt;
    }

    /**
     * 设置 现金流基数，存100或者总金额的具体数值
     * @param cashBaseamt 现金流基数，存100或者总金额的具体数值
     */
    public void setCashBaseamt(BigDecimal cashBaseamt) {
        this.cashBaseamt = cashBaseamt;
    }

    /**
     * 取得 现金流单位，单位、百元、 总金额
     * @return 现金流单位，单位、百元、 总金额
     */
    public String getCashUnitType() {
        return cashUnitType;
    }

    /**
     * 设置 现金流单位，单位、百元、 总金额
     * @param cashUnitType 现金流单位，单位、百元、 总金额
     */
    public void setCashUnitType(String cashUnitType) {
        this.cashUnitType = cashUnitType == null ? null : cashUnitType.trim();
    }

    /**
     * 取得 计息算法，分摊法、累计法，目前无
     * @return 计息算法，分摊法、累计法，目前无
     */
    public String getCalcFunction() {
        return calcFunction;
    }

    /**
     * 设置 计息算法，分摊法、累计法，目前无
     * @param calcFunction 计息算法，分摊法、累计法，目前无
     */
    public void setCalcFunction(String calcFunction) {
        this.calcFunction = calcFunction == null ? null : calcFunction.trim();
    }

    /**
     * 取得 年付息次数
     * @return 年付息次数
     */
    public Long getFrequency() {
        return frequency;
    }

    /**
     * 设置 年付息次数
     * @param frequency 年付息次数
     */
    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

    /**
     * 取得 金融产品代码
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     * @return 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     * @param finprodType 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType == null ? null : finprodType.trim();
    }

    /**
     * 取得 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @return 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @param finprodType2 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2 == null ? null : finprodType2.trim();
    }

    /**
     * 取得 分支序号
     * @return 分支序号
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 设置 分支序号
     * @param branch 分支序号
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    /**
     * 取得 支付方式，现金、红利再投等
     * @return 支付方式，现金、红利再投等
     */
    public String getPayType() {
        return payType;
    }

    /**
     * 设置 支付方式，现金、红利再投等
     * @param payType 支付方式，现金、红利再投等
     */
    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }

    /**
     * 取得 区间净收益率
     * @return 区间净收益率
     */
    public BigDecimal getRangeYield() {
        return rangeYield;
    }

    /**
     * 设置 区间净收益率
     * @param rangeYield 区间净收益率
     */
    public void setRangeYield(BigDecimal rangeYield) {
        this.rangeYield = rangeYield;
    }

    /**
     * 取得 备注
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
    /**
     * 是否还本不带息场景标志
     */
    private String isPayIntFlag;

    /**
     * 还本不带息的利息金额
     */
    private BigDecimal repayWithoutInt;

    /**
     * 获取 repayWithoutInt
     *
     * @return repayWithoutInt
     */
    public BigDecimal getRepayWithoutInt() {
        return repayWithoutInt;
    }

    /**
     * 设置 repayWithoutInt
     *
     * @param repayWithoutInt repayWithoutInt
     */
    public void setRepayWithoutInt(BigDecimal repayWithoutInt) {
        this.repayWithoutInt = repayWithoutInt;
    }

    /**
     * 获取 isPayIntFlag
     *
     * @return isPayIntFlag
     */
    public String getIsPayIntFlag() {
        return isPayIntFlag;
    }

    /**
     * 设置 isPayIntFlag
     *
     * @param isPayIntFlag isPayIntFlag
     */
    public void setIsPayIntFlag(String isPayIntFlag) {
        this.isPayIntFlag = isPayIntFlag;
    }


    /**
     * 获取 计息基础
     *
     * @return basis 计息基础
     */
    public String getBasis() {
        return this.basis;
    }

    /**
     * 设置 计息基础
     *
     * @param basis 计息基础
     */
    public void setBasis(String basis) {
        this.basis = basis;
    }
}