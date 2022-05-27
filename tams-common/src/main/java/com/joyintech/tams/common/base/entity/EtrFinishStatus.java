package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class EtrFinishStatus extends EtrFinishStatusKey {
    /**
     * 完成日期
     */
    private Date finishDate;

    /**
     * 交易完成额度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal finishAmt;

    /**
     * 交易员
     */
    private String trader;

    /**
     * 委托完成额度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entFinishAmt;

    /**
     * 订单完成额度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal orderFinishAmt;


    /**
     * 转二级交易额度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tranFinishAmt;

    /**
     * 取得 完成日期
     * @return 完成日期
     */
    public Date getFinishDate() {
        return finishDate;
    }

    /**
     * 设置 完成日期
     * @param finishDate 完成日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    /**
     * 取得 交易完成额度
     * @return 交易完成额度
     */
    public BigDecimal getFinishAmt() {
        return finishAmt;
    }

    /**
     * 设置 交易完成额度
     * @param finishAmt 交易完成额度
     */
    public void setFinishAmt(BigDecimal finishAmt) {
        this.finishAmt = finishAmt;
    }

    /**
     * 取得 交易员
     * @return 交易员
     */
    public String getTrader() {
        return trader;
    }

    /**
     * 设置 交易员
     * @param trader 交易员
     */
    public void setTrader(String trader) {
        this.trader = trader == null ? null : trader.trim();
    }

    /**
     * 取得 委托完成额度
     * @return 委托完成额度
     */
    public BigDecimal getEntFinishAmt() {
        return entFinishAmt;
    }

    /**
     * 设置 委托完成额度
     * @param entFinishAmt 委托完成额度
     */
    public void setEntFinishAmt(BigDecimal entFinishAmt) {
        this.entFinishAmt = entFinishAmt;
    }

    /**
     * 取得 订单完成额度
     * @return 订单完成额度
     */
    public BigDecimal getOrderFinishAmt() {
        return orderFinishAmt;
    }

    /**
     * 设置 订单完成额度
     * @param orderFinishAmt 订单完成额度
     */
    public void setOrderFinishAmt(BigDecimal orderFinishAmt) {
        this.orderFinishAmt = orderFinishAmt;
    }


    /**
     * 获取 转二级交易额度
     *
     * @return tranFinishAmt 转二级交易额度
     */
    public BigDecimal getTranFinishAmt() {
        return this.tranFinishAmt;
    }

    /**
     * 设置 转二级交易额度
     *
     * @param tranFinishAmt 转二级交易额度
     */
    public void setTranFinishAmt(BigDecimal tranFinishAmt) {
        this.tranFinishAmt = tranFinishAmt;
    }
}