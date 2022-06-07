package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;
import java.util.Date;

public class BidResultTotal extends BaseEntity {


    private static final long serialVersionUID = 9148764831077077460L;

    /**
     * 中标编号
     */
    private String totalWinId;

    /**
     * 投标编号
     */
    private String bidId;

    /**
     * 中标日期
     */
    private Date winDate;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 是否中标
     */
    private String isWin;

    /**
     * 中标方式
     */
    private String winType;

    /**
     * 中标券面总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal winPrinamt;

    /**
     * 中标标位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal winPrice;

    /**
     * 结算服务费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settFeeAmt;

    /**
     * 缴款日
     */
    private Date payDate;

    /**
     * 债权债务登记日
     */
    private Date creditorDebtDate;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 数据状态
     */
    private String status;

    /**
     * 获取 中标编号
     *
     * @return totalWinId 中标编号
     */
    public String getTotalWinId() {
        return this.totalWinId;
    }

    /**
     * 设置 中标编号
     *
     * @param totalWinId 中标编号
     */
    public void setTotalWinId(String totalWinId) {
        this.totalWinId = totalWinId;
    }

    /**
     * 获取 投标编号
     *
     * @return bidId 投标编号
     */
    public String getBidId() {
        return this.bidId;
    }

    /**
     * 设置 投标编号
     *
     * @param bidId 投标编号
     */
    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    /**
     * 获取 中标日期
     *
     * @return winDate 中标日期
     */
    public Date getWinDate() {
        return this.winDate;
    }

    /**
     * 设置 中标日期
     *
     * @param winDate 中标日期
     */
    public void setWinDate(Date winDate) {
        this.winDate = winDate;
    }

    /**
     * 获取 金融产品代码
     *
     * @return finprodId 金融产品代码
     */
    public String getFinprodId() {
        return this.finprodId;
    }

    /**
     * 设置 金融产品代码
     *
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 获取 是否中标
     *
     * @return isWin 是否中标
     */
    public String getIsWin() {
        return this.isWin;
    }

    /**
     * 设置 是否中标
     *
     * @param isWin 是否中标
     */
    public void setIsWin(String isWin) {
        this.isWin = isWin;
    }

    /**
     * 获取 中标方式
     *
     * @return winType 中标方式
     */
    public String getWinType() {
        return this.winType;
    }

    /**
     * 设置 中标方式
     *
     * @param winType 中标方式
     */
    public void setWinType(String winType) {
        this.winType = winType;
    }

    /**
     * 获取 中标券面总额
     *
     * @return winPrinamt 中标券面总额
     */
    public BigDecimal getWinPrinamt() {
        return this.winPrinamt;
    }

    /**
     * 设置 中标券面总额
     *
     * @param winPrinamt 中标券面总额
     */
    public void setWinPrinamt(BigDecimal winPrinamt) {
        this.winPrinamt = winPrinamt;
    }

    /**
     * 获取 中标标位
     *
     * @return winPrice 中标标位
     */
    public BigDecimal getWinPrice() {
        return this.winPrice;
    }

    /**
     * 设置 中标标位
     *
     * @param winPrice 中标标位
     */
    public void setWinPrice(BigDecimal winPrice) {
        this.winPrice = winPrice;
    }

    /**
     * 获取 结算服务费
     *
     * @return settFeeAmt 结算服务费
     */
    public BigDecimal getSettFeeAmt() {
        return this.settFeeAmt;
    }

    /**
     * 设置 结算服务费
     *
     * @param settFeeAmt 结算服务费
     */
    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    /**
     * 获取 缴款日
     *
     * @return payDate 缴款日
     */
    public Date getPayDate() {
        return this.payDate;
    }

    /**
     * 设置 缴款日
     *
     * @param payDate 缴款日
     */
    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    /**
     * 获取 债权债务登记日
     *
     * @return creditorDebtDate 债权债务登记日
     */
    public Date getCreditorDebtDate() {
        return this.creditorDebtDate;
    }

    /**
     * 设置 债权债务登记日
     *
     * @param creditorDebtDate 债权债务登记日
     */
    public void setCreditorDebtDate(Date creditorDebtDate) {
        this.creditorDebtDate = creditorDebtDate;
    }

    /**
     * 获取 交易对手
     *
     * @return counterId 交易对手
     */
    public String getCounterId() {
        return this.counterId;
    }

    /**
     * 设置 交易对手
     *
     * @param counterId 交易对手
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    /**
     * 获取 备注
     *
     * @return remark 备注
     */
    public String getRemark() {
        return this.remark;
    }

    /**
     * 设置 备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }


    /**
     * 获取 数据状态
     *
     * @return status 数据状态
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * 设置 数据状态
     *
     * @param status 数据状态
     */
    public void setStatus(String status) {
        this.status = status;
    }
}