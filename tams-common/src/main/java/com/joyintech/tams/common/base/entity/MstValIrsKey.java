package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;
import java.util.Date;

public class MstValIrsKey extends BaseEntity {
    /**
     * 金融产品代码（市场代码）
     */
    private String finprodId2;

    /**
     * 数据来源
     */
    private String datasourceType;

    /**
     * 估值日期
     */
    private Date valDate;

    /**
     * 估值模型
     */
    private String valmodelId;

    public String getValmodelId() {
        return valmodelId;
    }

    public void setValmodelId(String valmodelId) {
        this.valmodelId = valmodelId;
    }

    /**
     * 取得 金融产品代码（市场代码）
     * @return 金融产品代码（市场代码）
     */
    public String getFinprodId2() {
        return finprodId2;
    }

    /**
     * 设置 金融产品代码（市场代码）
     * @param finprodId2 金融产品代码（市场代码）
     */
    public void setFinprodId2(String finprodId2) {
        this.finprodId2 = finprodId2 == null ? null : finprodId2.trim();
    }

    /**
     * 取得 数据来源
     * @return 数据来源
     */
    public String getDatasourceType() {
        return datasourceType;
    }

    /**
     * 设置 数据来源
     * @param datasourceType 数据来源
     */
    public void setDatasourceType(String datasourceType) {
        this.datasourceType = datasourceType == null ? null : datasourceType.trim();
    }

    /**
     * 取得 估值日期
     * @return 估值日期
     */
    public Date getValDate() {
        return valDate;
    }

    /**
     * 设置 估值日期
     * @param valDate 估值日期
     */
    public void setValDate(Date valDate) {
        this.valDate = valDate;
    }
}