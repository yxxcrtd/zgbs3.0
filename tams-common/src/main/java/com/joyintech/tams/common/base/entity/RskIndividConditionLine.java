package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import java.math.BigDecimal;

public class RskIndividConditionLine extends BaseEntity {
    /**
     * 条件设置编号
     */
    private String conditionId;

    /**
     * 组合(包)代码
     */
    private String prtBagId;

    /**
     * 监督指标编号
     */
    private String indId;

    /**
     * 条件项
     */
    private String conditionItem;

    /**
     * 控制类型
     */
    private String controlType;

    /**
     * 数值（起）
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal valueStr;

    /**
     * 数值（末）
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal valueEnd;

    /**
     * 预警线
     */
    private String warnCond;

    /**
     * 审批线
     */
    private String approveCond;

    /**
     * 违规线
     */
    private String foulCond;

    /**
     * 组合包是否计算
     */
    private String isBagCalc;

    /**
     * 处理类型
     */
    private String processModel;

    /**
     * 取得 条件设置编号
     * @return 条件设置编号
     */
    public String getConditionId() {
        return conditionId;
    }

    /**
     * 设置 条件设置编号
     * @param conditionId 条件设置编号
     */
    public void setConditionId(String conditionId) {
        this.conditionId = conditionId == null ? null : conditionId.trim();
    }

    /**
     * 取得 组合(包)代码
     * @return 组合(包)代码
     */
    public String getPrtBagId() {
        return prtBagId;
    }

    /**
     * 设置 组合(包)代码
     * @param prtBagId 组合(包)代码
     */
    public void setPrtBagId(String prtBagId) {
        this.prtBagId = prtBagId == null ? null : prtBagId.trim();
    }

    /**
     * 取得 监督指标编号
     * @return 监督指标编号
     */
    public String getIndId() {
        return indId;
    }

    /**
     * 设置 监督指标编号
     * @param indId 监督指标编号
     */
    public void setIndId(String indId) {
        this.indId = indId == null ? null : indId.trim();
    }

    /**
     * 取得 条件项
     * @return 条件项
     */
    public String getConditionItem() {
        return conditionItem;
    }

    /**
     * 设置 条件项
     * @param conditionItem 条件项
     */
    public void setConditionItem(String conditionItem) {
        this.conditionItem = conditionItem == null ? null : conditionItem.trim();
    }

    /**
     * 取得 控制类型
     * @return 控制类型
     */
    public String getControlType() {
        return controlType;
    }

    /**
     * 设置 控制类型
     * @param controlType 控制类型
     */
    public void setControlType(String controlType) {
        this.controlType = controlType == null ? null : controlType.trim();
    }

    /**
     * 取得 数值（起）
     * @return 数值（起）
     */
    public BigDecimal getValueStr() {
        return valueStr;
    }

    /**
     * 设置 数值（起）
     * @param valueStr 数值（起）
     */
    public void setValueStr(BigDecimal valueStr) {
        this.valueStr = valueStr;
    }

    /**
     * 取得 数值（末）
     * @return 数值（末）
     */
    public BigDecimal getValueEnd() {
        return valueEnd;
    }

    /**
     * 设置 数值（末）
     * @param valueEnd 数值（末）
     */
    public void setValueEnd(BigDecimal valueEnd) {
        this.valueEnd = valueEnd;
    }

    /**
     * 取得 预警线
     * @return 预警线
     */
    public String getWarnCond() {
        return warnCond;
    }

    /**
     * 设置 预警线
     * @param warnCond 预警线
     */
    public void setWarnCond(String warnCond) {
        this.warnCond = warnCond == null ? null : warnCond.trim();
    }

    /**
     * 取得 审批线
     * @return 审批线
     */
    public String getApproveCond() {
        return approveCond;
    }

    /**
     * 设置 审批线
     * @param approveCond 审批线
     */
    public void setApproveCond(String approveCond) {
        this.approveCond = approveCond == null ? null : approveCond.trim();
    }

    /**
     * 取得 违规线
     * @return 违规线
     */
    public String getFoulCond() {
        return foulCond;
    }

    /**
     * 设置 违规线
     * @param foulCond 违规线
     */
    public void setFoulCond(String foulCond) {
        this.foulCond = foulCond == null ? null : foulCond.trim();
    }

    /**
     * 取得 组合包是否计算
     * @return 组合包是否计算
     */
    public String getIsBagCalc() {
        return isBagCalc;
    }

    /**
     * 设置 组合包是否计算
     * @param isBagCalc 组合包是否计算
     */
    public void setIsBagCalc(String isBagCalc) {
        this.isBagCalc = isBagCalc == null ? null : isBagCalc.trim();
    }

    public String getProcessModel() {
        return processModel;
    }

    public void setProcessModel(String processModel) {
        this.processModel = processModel;
    }
}