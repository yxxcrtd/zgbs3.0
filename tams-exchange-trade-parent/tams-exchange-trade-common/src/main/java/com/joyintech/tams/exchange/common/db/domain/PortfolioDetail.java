package com.joyintech.tams.exchange.common.db.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 交易多组合明细表
 */
@ApiModel(value = "交易多组合明细表")
public class PortfolioDetail implements Serializable, KeyMethodInterface<String> {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "主键ID")
    private String id;

    /**
     * 业务类型：1 下单；2 撤单；3 成交
     */
    @ApiModelProperty(value = "业务类型：1 下单；2 撤单；3 成交")
    private String busiType;

    /**
     * 业务ID
     */
    @ApiModelProperty(value = "业务ID")
    private String busiId;

    /**
     * 业务状态：1 初始；2 确认
     */
    @ApiModelProperty(value = "业务状态：1 初始；2 确认")
    private String busiStatus;

    /**
     * 指令ID
     */
    @ApiModelProperty(value = "指令ID")
    private String instructionId;

    /**
     * 组合ID
     */
    @ApiModelProperty(value = "组合ID")
    private String portfolioId;

    /**
     * 组合名称
     */
    @ApiModelProperty(value = "组合名称")
    private String portfolioName;

    /**
     * 目标金额
     */
    @ApiModelProperty(value = "目标金额")
    private BigDecimal targetAmt;

    /**
     * 目标数量
     */
    @ApiModelProperty(value = "目标数量")
    private BigDecimal targetNum;

    /**
     * 目标类型：01 数量；02 金额
     */
    @ApiModelProperty(value = "目标类型：01 数量；02 金额")
    private String targetType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType == null ? null : busiType.trim();
    }

    public String getBusiId() {
        return busiId;
    }

    public void setBusiId(String busiId) {
        this.busiId = busiId == null ? null : busiId.trim();
    }

    public String getBusiStatus() {
        return busiStatus;
    }

    public void setBusiStatus(String busiStatus) {
        this.busiStatus = busiStatus == null ? null : busiStatus.trim();
    }

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId == null ? null : instructionId.trim();
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName == null ? null : portfolioName.trim();
    }

    public BigDecimal getTargetAmt() {
        return targetAmt;
    }

    public void setTargetAmt(BigDecimal targetAmt) {
        this.targetAmt = targetAmt;
    }

    public BigDecimal getTargetNum() {
        return targetNum;
    }

    public void setTargetNum(BigDecimal targetNum) {
        this.targetNum = targetNum;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType == null ? null : targetType.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", busiType=").append(busiType);
        sb.append(", busiId=").append(busiId);
        sb.append(", busiStatus=").append(busiStatus);
        sb.append(", instructionId=").append(instructionId);
        sb.append(", portfolioId=").append(portfolioId);
        sb.append(", portfolioName=").append(portfolioName);
        sb.append(", targetAmt=").append(targetAmt);
        sb.append(", targetNum=").append(targetNum);
        sb.append(", targetType=").append(targetType);
        sb.append("]");
        return sb.toString();
    }
}