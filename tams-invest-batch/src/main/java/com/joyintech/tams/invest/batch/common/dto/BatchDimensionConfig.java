package com.joyintech.tams.invest.batch.common.dto;

import com.joyintech.framework.base.BaseEntity;

public class BatchDimensionConfig extends BaseEntity {
    
    /**
     * 序列号
     */
    private String seqNo;

    /**
     * 维度名
     */
    private String dimensionName;

    /**
     * 优先级
     */
    private long pri;
    
    /**
     * 备注
     */
    private String remark;

    /**
     * 获取 seqNo
     *
     * @return seqNo
     */
    public String getSeqNo() {
        return seqNo;
    }

    /**
     * 设置 seqNo
     *
     * @param seqNo seqNo
     */
    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    /**
     * 获取 dimensionName
     *
     * @return dimensionName
     */
    public String getDimensionName() {
        return dimensionName;
    }

    /**
     * 设置 dimensionName
     *
     * @param dimensionName dimensionName
     */
    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    /**
     * 获取 pri
     *
     * @return pri
     */
    public long getPri() {
        return pri;
    }

    /**
     * 设置 pri
     *
     * @param pri pri
     */
    public void setPri(long pri) {
        this.pri = pri;
    }

    /**
     * 获取 remark
     *
     * @return remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 remark
     *
     * @param remark remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
