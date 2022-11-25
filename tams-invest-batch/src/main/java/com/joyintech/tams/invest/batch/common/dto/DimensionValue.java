package com.joyintech.tams.invest.batch.common.dto;

import com.joyintech.framework.base.BaseEntity;

public class DimensionValue extends BaseEntity {
    /**
     * 批处理任务组实例ID
     */
    private String groupInsSeqNo;
    /**
     * 维度配置ID
     */
    private String dimensionSeqNo;
    /**
     * 纬度值
     */
    private String dimensionValue;

    /**
     * 
     * @param groupInsSeqNo
     * @param dimensionSeqNo
     * @param dimensionValue
     */
    public DimensionValue(String groupInsSeqNo,String dimensionSeqNo,String dimensionValue){
        super();
        this.groupInsSeqNo = groupInsSeqNo;
        this.dimensionSeqNo = dimensionSeqNo;
        this.dimensionValue = dimensionValue;
    }
    /**
     *  取得 批处理任务组实例ID
     *  @return 批处理任务组实例ID
     */
    public String getGroupInsSeqNo() {
       return groupInsSeqNo;
    }

    /**
     *  设置 批处理任务组实例ID
     *  @param groupInsSeqNo 批处理任务组实例ID
     */
    public void setGroupInsSeqNo( String groupInsSeqNo)  {
       this.groupInsSeqNo = groupInsSeqNo;
    }

    /**
     *  取得 维度配置ID
     *  @return 维度配置ID
     */
    public String getDimensionSeqNo() {
       return dimensionSeqNo;
    }

    /**
     *  设置 维度配置ID
     *  @param dimensionSeqNo 维度配置ID
     */
    public void setDimensionSeqNo( String dimensionSeqNo)  {
       this.dimensionSeqNo = dimensionSeqNo;
    }

    /**
     *  取得 纬度值
     *  @return 纬度值
     */
    public String getDimensionValue() {
       return dimensionValue;
    }

    /**
     *  设置 纬度值
     *  @param dimensionValue 纬度值
     */
    public void setDimensionValue( String dimensionValue)  {
       this.dimensionValue = dimensionValue;
    }

}
