package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;

public class WflEtrEntrustDetail extends WflEtrEntrustDetailKey {
    /**
     * 分配比例
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal disRate;

    /**
     * 委托金额，交易金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustAmt;

    /**
     * 委托数量，数量或者券面总额/100
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustNum;

    /**
     * 目标类型，金额，数量
     */
    private String targetType;

    /**
     * 工作流代码
     */
    private String workflowId;

    /**
     * 证券托管户
     */
    private String secAcctId;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 资金账户
     */
    private String acctNo;

    /**
     * 取得 分配比例
     * @return 分配比例
     */
    public BigDecimal getDisRate() {
        return disRate;
    }

    /**
     * 设置 分配比例
     * @param disRate 分配比例
     */
    public void setDisRate(BigDecimal disRate) {
        this.disRate = disRate;
    }

    /**
     * 取得 委托金额，交易金额
     * @return 委托金额，交易金额
     */
    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    /**
     * 设置 委托金额，交易金额
     * @param entrustAmt 委托金额，交易金额
     */
    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    /**
     * 取得 委托数量，数量或者券面总额/100
     * @return 委托数量，数量或者券面总额/100
     */
    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    /**
     * 设置 委托数量，数量或者券面总额/100
     * @param entrustNum 委托数量，数量或者券面总额/100
     */
    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    /**
     * 取得 目标类型，金额，数量
     * @return 目标类型，金额，数量
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * 设置 目标类型，金额，数量
     * @param targetType 目标类型，金额，数量
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType == null ? null : targetType.trim();
    }

    /**
     * 取得 工作流代码
     * @return 工作流代码
     */
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * 设置 工作流代码
     * @param workflowId 工作流代码
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId == null ? null : workflowId.trim();
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }
}