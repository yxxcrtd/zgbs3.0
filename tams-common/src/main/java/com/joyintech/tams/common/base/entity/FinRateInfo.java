package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class FinRateInfo extends FinRateInfoKey {
    /**
     * 利率类型
     */
    private String intType;

    /**
     * 计息基础，A/360，A/365等
     */
    private String basis;

    /**
     * 重置类型，基准利率变动后固定间隔生效、付息后下一计息期生效
     */
    private String resetType;

    /**
     * 重置频率，针对固定频率重置，目前无
     */
    private String resetFreq;

    /**
     * 首次重置日，针对固定频率重置，目前无
     */
    private Date firstResetDate;

    /**
     * 指定重置日期，针对指定日期重置，目前无
     */
    private String resetDate;

    /**
     * 提前观察数量，所有重置类型都有
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal observeBefDay;

    /**
     * 提前观察单位，自然日、工作日、自然月等
     */
    private String observeBefUnit;

    /**
     * 初始利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal rate;

    /**
     * 系数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal coefficient;

    /**
     * 利差
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal spreadRate;

    /**
     * 利率上限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal highestRate;

    /**
     * 利率下限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lowestRate;

    /**
     * 基准编号
     */
    private String benchmarkId;

    /**
     * 基准类型，复合基准、利率行情、指数行情、曲线
     */
    private String benchmarkType;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 分支序号
     */
    private Long branch;

    /**
     * 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType;

    /**
     * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType2;

    /**
     * 备注
     */
    private String remark;

    /**
     * 首期利率确定日
     */
    private Date firstConfirmDate;

    /**
     * 基准利率精度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal benchmarkIdAccuracy;

    /**
     * 取得 利率类型
     * @return 利率类型
     */
    public String getIntType() {
        return intType;
    }

    /**
     * 设置 利率类型
     * @param intType 利率类型
     */
    public void setIntType(String intType) {
        this.intType = intType == null ? null : intType.trim();
    }

    /**
     * 取得 计息基础，A/360，A/365等
     * @return 计息基础，A/360，A/365等
     */
    public String getBasis() {
        return basis;
    }

    /**
     * 设置 计息基础，A/360，A/365等
     * @param basis 计息基础，A/360，A/365等
     */
    public void setBasis(String basis) {
        this.basis = basis == null ? null : basis.trim();
    }

    /**
     * 取得 重置类型，基准利率变动后固定间隔生效、付息后下一计息期生效
     * @return 重置类型，基准利率变动后固定间隔生效、付息后下一计息期生效
     */
    public String getResetType() {
        return resetType;
    }

    /**
     * 设置 重置类型，基准利率变动后固定间隔生效、付息后下一计息期生效
     * @param resetType 重置类型，基准利率变动后固定间隔生效、付息后下一计息期生效
     */
    public void setResetType(String resetType) {
        this.resetType = resetType == null ? null : resetType.trim();
    }

    /**
     * 取得 重置频率，针对固定频率重置，目前无
     * @return 重置频率，针对固定频率重置，目前无
     */
    public String getResetFreq() {
        return resetFreq;
    }

    /**
     * 设置 重置频率，针对固定频率重置，目前无
     * @param resetFreq 重置频率，针对固定频率重置，目前无
     */
    public void setResetFreq(String resetFreq) {
        this.resetFreq = resetFreq == null ? null : resetFreq.trim();
    }

    /**
     * 取得 首次重置日，针对固定频率重置，目前无
     * @return 首次重置日，针对固定频率重置，目前无
     */
    public Date getFirstResetDate() {
        return firstResetDate;
    }

    /**
     * 设置 首次重置日，针对固定频率重置，目前无
     * @param firstResetDate 首次重置日，针对固定频率重置，目前无
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setFirstResetDate(Date firstResetDate) {
        this.firstResetDate = firstResetDate;
    }

    /**
     * 取得 指定重置日期，针对指定日期重置，目前无
     * @return 指定重置日期，针对指定日期重置，目前无
     */
    public String getResetDate() {
        return resetDate;
    }

    /**
     * 设置 指定重置日期，针对指定日期重置，目前无
     * @param resetDate 指定重置日期，针对指定日期重置，目前无
     */
    public void setResetDate(String resetDate) {
        this.resetDate = resetDate == null ? null : resetDate.trim();
    }

    /**
     * 取得 提前观察数量，所有重置类型都有
     * @return 提前观察数量，所有重置类型都有
     */
    public BigDecimal getObserveBefDay() {
        return observeBefDay;
    }

    /**
     * 设置 提前观察数量，所有重置类型都有
     * @param observeBefDay 提前观察数量，所有重置类型都有
     */
    public void setObserveBefDay(BigDecimal observeBefDay) {
        this.observeBefDay = observeBefDay;
    }

    /**
     * 取得 提前观察单位，自然日、工作日、自然月等
     * @return 提前观察单位，自然日、工作日、自然月等
     */
    public String getObserveBefUnit() {
        return observeBefUnit;
    }

    /**
     * 设置 提前观察单位，自然日、工作日、自然月等
     * @param observeBefUnit 提前观察单位，自然日、工作日、自然月等
     */
    public void setObserveBefUnit(String observeBefUnit) {
        this.observeBefUnit = observeBefUnit == null ? null : observeBefUnit.trim();
    }

    /**
     * 取得 初始利率
     * @return 初始利率
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * 设置 初始利率
     * @param rate 初始利率
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * 取得 系数
     * @return 系数
     */
    public BigDecimal getCoefficient() {
        return coefficient;
    }

    /**
     * 设置 系数
     * @param coefficient 系数
     */
    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    /**
     * 取得 利差
     * @return 利差
     */
    public BigDecimal getSpreadRate() {
        return spreadRate;
    }

    /**
     * 设置 利差
     * @param spreadRate 利差
     */
    public void setSpreadRate(BigDecimal spreadRate) {
        this.spreadRate = spreadRate;
    }

    /**
     * 取得 利率上限
     * @return 利率上限
     */
    public BigDecimal getHighestRate() {
        return highestRate;
    }

    /**
     * 设置 利率上限
     * @param highestRate 利率上限
     */
    public void setHighestRate(BigDecimal highestRate) {
        this.highestRate = highestRate;
    }

    /**
     * 取得 利率下限
     * @return 利率下限
     */
    public BigDecimal getLowestRate() {
        return lowestRate;
    }

    /**
     * 设置 利率下限
     * @param lowestRate 利率下限
     */
    public void setLowestRate(BigDecimal lowestRate) {
        this.lowestRate = lowestRate;
    }

    /**
     * 取得 基准编号
     * @return 基准编号
     */
    public String getBenchmarkId() {
        return benchmarkId;
    }

    /**
     * 设置 基准编号
     * @param benchmarkId 基准编号
     */
    public void setBenchmarkId(String benchmarkId) {
        this.benchmarkId = benchmarkId == null ? null : benchmarkId.trim();
    }

    /**
     * 取得 基准类型，复合基准、利率行情、指数行情、曲线
     * @return 基准类型，复合基准、利率行情、指数行情、曲线
     */
    public String getBenchmarkType() {
        return benchmarkType;
    }

    /**
     * 设置 基准类型，复合基准、利率行情、指数行情、曲线
     * @param benchmarkType 基准类型，复合基准、利率行情、指数行情、曲线
     */
    public void setBenchmarkType(String benchmarkType) {
        this.benchmarkType = benchmarkType == null ? null : benchmarkType.trim();
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
     * 取得 首期利率确定日
     * @return 首期利率确定日
     */
    public Date getFirstConfirmDate() {
        return firstConfirmDate;
    }

    /**
     * 设置 首期利率确定日
     * @param firstConfirmDate 首期利率确定日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setFirstConfirmDate(Date firstConfirmDate) {
        this.firstConfirmDate = firstConfirmDate;
    }


    /**
     * 获取 基准利率精度
     *
     * @return benchmarkIdAccuracy 基准利率精度
     */
    public BigDecimal getBenchmarkIdAccuracy() {
        return this.benchmarkIdAccuracy;
    }

    /**
     * 设置 基准利率精度
     *
     * @param benchmarkIdAccuracy 基准利率精度
     */
    public void setBenchmarkIdAccuracy(BigDecimal benchmarkIdAccuracy) {
        this.benchmarkIdAccuracy = benchmarkIdAccuracy;
    }
}