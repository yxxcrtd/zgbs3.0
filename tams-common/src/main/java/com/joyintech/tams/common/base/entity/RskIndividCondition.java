package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import java.math.BigDecimal;

public class RskIndividCondition extends BaseEntity {
    /**
     * 条件设置编号
     */
    private String conditionId;

    /**
     * 组合(包)代码
     */
    private String prtBagId;

    /**
     * 指标编号
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
     * 数值(起)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal valueStr;

    /**
     * 数值(未)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal valueEnd;

    /**
     * 日期选项(起)
     */
    private String dateStr;

    /**
     * 日期选项(末)
     */
    private String dateEnd;

    /**
     * 日期单位(起)
     */
    private String dateUnitStr;

    /**
     * 日期单位(末)
     */
    private String dateUnitEnd;

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
     * 取得 指标编号
     * @return 指标编号
     */
    public String getIndId() {
        return indId;
    }

    /**
     * 设置 指标编号
     * @param indId 指标编号
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
     * 取得 数值(起)
     * @return 数值(起)
     */
    public BigDecimal getValueStr() {
        return valueStr;
    }

    /**
     * 设置 数值(起)
     * @param valueStr 数值(起)
     */
    public void setValueStr(BigDecimal valueStr) {
        this.valueStr = valueStr;
    }

    /**
     * 取得 数值(未)
     * @return 数值(未)
     */
    public BigDecimal getValueEnd() {
        return valueEnd;
    }

    /**
     * 设置 数值(未)
     * @param valueEnd 数值(未)
     */
    public void setValueEnd(BigDecimal valueEnd) {
        this.valueEnd = valueEnd;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getDateUnitStr() {
        return dateUnitStr;
    }

    public void setDateUnitStr(String dateUnitStr) {
        this.dateUnitStr = dateUnitStr;
    }

    public String getDateUnitEnd() {
        return dateUnitEnd;
    }

    public void setDateUnitEnd(String dateUnitEnd) {
        this.dateUnitEnd = dateUnitEnd;
    }

    public String getProcessModel() {
        return processModel;
    }

    public void setProcessModel(String processModel) {
        this.processModel = processModel;
    }
}