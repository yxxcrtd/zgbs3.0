package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.util.Date;

public class RskIndividAssetDel extends BaseEntity implements IColBaseCond {
    /**
     * 剔除编号
     */
    private String delId;

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
     * 资产代码
     */
    private String finprodId;

    /**
     * 处理类型
     */
    private String processModel;

    /**
     * 取得 剔除编号
     * @return 剔除编号
     */
    public String getDelId() {
        return delId;
    }

    /**
     * 设置 剔除编号
     * @param delId 剔除编号
     */
    public void setDelId(String delId) {
        this.delId = delId == null ? null : delId.trim();
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

    public String getProcessModel() {
        return processModel;
    }

    public void setProcessModel(String processModel) {
        this.processModel = processModel;
    }
}