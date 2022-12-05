/*
 * 文件名：CommisionDetail.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年7月13日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */
 
package com.joyintech.tams.settle.common.dto;

import java.math.BigDecimal;

/**
 * 佣金明细
 * @description 
 * @author 王波
 * @version 1.0 2020年7月13日
 * @see CommisionDetail
 * @since 1.0
 */
public class CommisionDetail {
    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 费用类型
     */
    private String feeType;

    /**
     * 是否随交易支付
     */
    private String isPayWithTrade;

    /**
     * 费用金额
     */
    private BigDecimal feeAmt;

    /**
     * 净费用金额
     */
    private BigDecimal netFeeAmt;

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getIsPayWithTrade() {
        return isPayWithTrade;
    }

    public void setIsPayWithTrade(String isPayWithTrade) {
        this.isPayWithTrade = isPayWithTrade;
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public BigDecimal getNetFeeAmt() {
        return netFeeAmt;
    }

    public void setNetFeeAmt(BigDecimal netFeeAmt) {
        this.netFeeAmt = netFeeAmt;
    }
}
