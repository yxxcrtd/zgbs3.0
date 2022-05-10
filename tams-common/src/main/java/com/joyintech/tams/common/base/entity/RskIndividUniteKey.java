package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

public class RskIndividUniteKey extends BaseEntity {
    /**
     * 联合设置编号
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
     * 处理类型
     */
    private String processModel;

    /**
     * 取得 联合设置编号
     * @return 联合设置编号
     */
    public String getUniteId() {
        return uniteId;
    }

    /**
     * 设置 联合设置编号
     * @param uniteId 联合设置编号
     */
    public void setUniteId(String uniteId) {
        this.uniteId = uniteId == null ? null : uniteId.trim();
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

    public String getProcessModel() {
        return processModel;
    }

    public void setProcessModel(String processModel) {
        this.processModel = processModel;
    }
}