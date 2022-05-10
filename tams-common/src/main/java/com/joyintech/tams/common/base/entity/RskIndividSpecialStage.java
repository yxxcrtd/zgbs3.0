package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.util.Date;

public class RskIndividSpecialStage extends BaseEntity implements IColBaseCond {
    /**
     * 特殊阶段编号
     */
    private String uniteId;

    /**
     * 组合(包)代码
     */
    private String prtBagId;

    /**
     * 指标编号
     */
    private String indId;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 阶段类型
     */
    private String uniteType;

    /**
     * 资产代码
     */
    private String finprodId;

    /**
     * 预警条件
     */
    private String warnCond;

    /**
     * 审批条件
     */
    private String approveCond;

    /**
     * 违规条件
     */
    private String foulCond;

    /**
     * 处理类型
     */
    private String processModel;

    /**
     * 取得 特殊阶段编号
     * @return 特殊阶段编号
     */
    public String getUniteId() {
        return uniteId;
    }

    /**
     * 设置 特殊阶段编号
     * @param uniteId 特殊阶段编号
     */
    public void setUniteId(String uniteId) {
        this.uniteId = uniteId == null ? null : uniteId.trim();
    }

    /**
     * 取得 组合(包)代码
     * @return 组合(包)代码
     */
    @Override
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
    @Override
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
     * 取得 开始日期
     * @return 开始日期
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * 设置 开始日期
     * @param startDate 开始日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * 取得 结束日期
     * @return 结束日期
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * 设置 结束日期
     * @param endDate 结束日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * 取得 阶段类型
     * @return 阶段类型
     */
    public String getUniteType() {
        return uniteType;
    }

    /**
     * 设置 阶段类型
     * @param uniteType 阶段类型
     */
    public void setUniteType(String uniteType) {
        this.uniteType = uniteType == null ? null : uniteType.trim();
    }

    /**
     * 取得 资产代码
     * @return 资产代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 资产代码
     * @param finprodId 资产代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 预警条件
     * @return 预警条件
     */
    public String getWarnCond() {
        return warnCond;
    }

    /**
     * 设置 预警条件
     * @param warnCond 预警条件
     */
    public void setWarnCond(String warnCond) {
        this.warnCond = warnCond == null ? null : warnCond.trim();
    }

    /**
     * 取得 审批条件
     * @return 审批条件
     */
    public String getApproveCond() {
        return approveCond;
    }

    /**
     * 设置 审批条件
     * @param approveCond 审批条件
     */
    public void setApproveCond(String approveCond) {
        this.approveCond = approveCond == null ? null : approveCond.trim();
    }

    /**
     * 取得 违规条件
     * @return 违规条件
     */
    public String getFoulCond() {
        return foulCond;
    }

    /**
     * 设置 违规条件
     * @param foulCond 违规条件
     */
    public void setFoulCond(String foulCond) {
        this.foulCond = foulCond == null ? null : foulCond.trim();
    }

    public String getProcessModel() {
        return processModel;
    }

    public void setProcessModel(String processModel) {
        this.processModel = processModel;
    }
}