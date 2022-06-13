package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

import java.util.Date;

public class AssetDailyInfoKey extends BaseEntity {

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 日期
     */
    private Date cdate;

    /**
     * 取得 金融产品代码
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 日期
     * @return 日期
     */
    public Date getCdate() {
        return cdate;
    }

    /**
     * 设置 日期
     * @param cdate 日期
     */
    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }
}