package com.joyintech.background.component.order.exchange.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.background.component.common.pack.common.json.BigDecimalSerializer;

import java.math.BigDecimal;

public class EtrPledgeDto extends EtrPledgeKeyDto {
    /**
     * 质押面值
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal pledgeFaceValue;

    /**
     * 质押数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pledgeNumber;

    /**
     * 质押率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pledgeRatio;

    /**
     * 质押金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pledgeAmt;
    /**
     * 置换方向
     */
    private String displaceDir;


    /**
     * 取得 质押面值
     * @return 质押面值
     */
    public BigDecimal getPledgeFaceValue() {
        return pledgeFaceValue;
    }

    /**
     * 设置 质押面值
     * @param pledgeFaceValue 质押面值
     */
    public void setPledgeFaceValue(BigDecimal pledgeFaceValue) {
        this.pledgeFaceValue = pledgeFaceValue;
    }

    /**
     * 取得 质押数量
     * @return 质押数量
     */
    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    /**
     * 设置 质押数量
     * @param pledgeNumber 质押数量
     */
    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    /**
     * 取得 质押率
     * @return 质押率
     */
    public BigDecimal getPledgeRatio() {
        return pledgeRatio;
    }

    /**
     * 设置 质押率
     * @param pledgeRatio 质押率
     */
    public void setPledgeRatio(BigDecimal pledgeRatio) {
        this.pledgeRatio = pledgeRatio;
    }

    /**
     * 取得 质押金额
     * @return 质押金额
     */
    public BigDecimal getPledgeAmt() {
        return pledgeAmt;
    }

    /**
     * 设置 质押金额
     * @param pledgeAmt 质押金额
     */
    public void setPledgeAmt(BigDecimal pledgeAmt) {
        this.pledgeAmt = pledgeAmt;
    }

    public String getDisplaceDir() {
        return displaceDir;
    }

    public void setDisplaceDir(String displaceDir) {
        this.displaceDir = displaceDir;
    }
}