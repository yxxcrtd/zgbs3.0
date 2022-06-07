package com.joyintech.tams.cfets.trd.bond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;

/**
 * 指令可用额度实体
 * @author zznn
 * @date 2021/12/27
 */
public class EntrustAvaShareAmtDto {

    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 本方券交易份额(已用)
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal usedOurShareAmt;

    /**
     * 融入券交易份额(已用)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal usedLendinShareAmt;

    /**
     * 本方券委托数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourEntNum;

    /**
     * 融入券委托数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinEntNum;

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public BigDecimal getUsedOurShareAmt() {
        return usedOurShareAmt;
    }

    public void setUsedOurShareAmt(BigDecimal usedOurShareAmt) {
        this.usedOurShareAmt = usedOurShareAmt;
    }

    public BigDecimal getUsedLendinShareAmt() {
        return usedLendinShareAmt;
    }

    public void setUsedLendinShareAmt(BigDecimal usedLendinShareAmt) {
        this.usedLendinShareAmt = usedLendinShareAmt;
    }

    public BigDecimal getOurEntNum() {
        return ourEntNum;
    }

    public void setOurEntNum(BigDecimal ourEntNum) {
        this.ourEntNum = ourEntNum;
    }

    public BigDecimal getLendinEntNum() {
        return lendinEntNum;
    }

    public void setLendinEntNum(BigDecimal lendinEntNum) {
        this.lendinEntNum = lendinEntNum;
    }
}

