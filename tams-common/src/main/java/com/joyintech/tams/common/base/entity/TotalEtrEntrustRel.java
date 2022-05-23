package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

/**
 * 描述:TOTAL_ETR_ENTRUST_REL表的实体类
 * @version
 * @author:  Administrator
 * @创建时间: 2022-05-23
 */
public class TotalEtrEntrustRel extends BaseEntity {
    /**
     * 汇总代码
     */
    private String totleId;

    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 交易员
     */
    private String trader;



    /**
     * 汇总代码
     * @return TOTLE_ID 汇总代码
     */
    public String getTotleId() {
        return totleId;
    }

    /**
     * 汇总代码
     * @param totleId 汇总代码
     */
    public void setTotleId(String totleId) {
        this.totleId = totleId == null ? null : totleId.trim();
    }

    /**
     * 委托代码
     * @return ENTRUST_ID 委托代码
     */
    public String getEntrustId() {
        return entrustId;
    }

    /**
     * 委托代码
     * @param entrustId 委托代码
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId == null ? null : entrustId.trim();
    }

    /**
     * 交易员
     * @return TRADER 交易员
     */
    public String getTrader() {
        return trader;
    }

    /**
     * 交易员
     * @param trader 交易员
     */
    public void setTrader(String trader) {
        this.trader = trader == null ? null : trader.trim();
    }
}