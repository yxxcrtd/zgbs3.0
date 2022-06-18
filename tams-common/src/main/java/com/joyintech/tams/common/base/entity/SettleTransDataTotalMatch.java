package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import java.math.BigDecimal;

public class SettleTransDataTotalMatch extends SettleTransDataTotalMatchKey {
    /**
     * 目标类型
     */
    private String matchType;

    /**
     * 已匹配份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal marchQty;

    /**
     * 已匹配金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal marchAmt;

    /**
     * 指令已匹配份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal marchQtyEn;

    /**
     * 指令已匹配金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal marchAmtEn;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 取得 目标类型
     * @return 目标类型
     */
    public String getMatchType() {
        return matchType;
    }

    /**
     * 设置 目标类型
     * @param matchType 目标类型
     */
    public void setMatchType(String matchType) {
        this.matchType = matchType == null ? null : matchType.trim();
    }

    /**
     * 取得 已匹配份额
     * @return 已匹配份额
     */
    public BigDecimal getMarchQty() {
        return marchQty;
    }

    /**
     * 设置 已匹配份额
     * @param marchQty 已匹配份额
     */
    public void setMarchQty(BigDecimal marchQty) {
        this.marchQty = marchQty;
    }

    /**
     * 取得 已匹配金额
     * @return 已匹配金额
     */
    public BigDecimal getMarchAmt() {
        return marchAmt;
    }

    /**
     * 设置 已匹配金额
     * @param marchAmt 已匹配金额
     */
    public void setMarchAmt(BigDecimal marchAmt) {
        this.marchAmt = marchAmt;
    }

    /**
     * 取得 指令已匹配份额
     * @return 指令已匹配份额
     */
    public BigDecimal getMarchQtyEn() {
        return marchQtyEn;
    }

    /**
     * 设置 指令已匹配份额
     * @param marchQtyEn 指令已匹配份额
     */
    public void setMarchQtyEn(BigDecimal marchQtyEn) {
        this.marchQtyEn = marchQtyEn;
    }

    /**
     * 取得 指令已匹配金额
     * @return 指令已匹配金额
     */
    public BigDecimal getMarchAmtEn() {
        return marchAmtEn;
    }

    /**
     * 设置 指令已匹配金额
     * @param marchAmtEn 指令已匹配金额
     */
    public void setMarchAmtEn(BigDecimal marchAmtEn) {
        this.marchAmtEn = marchAmtEn;
    }

    /**
     * 取得 币种
     * @return 币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 币种
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 证券账户
     */
    private String secAcctId;

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }
}