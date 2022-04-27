package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;
import java.util.Date;

public class BokStatStockPositionKey extends BaseEntity {
    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 发生日期
     */
    private Date happenDate;

    /**
     * 账务明细代码
     */
    private String bokDetailId;

    /**
     * 股票持仓类型：01新股；02增发；03流通；04限售
     */
    private String stockPositionType;

    /**
     * 取得 账套代码
     * @return 账套代码
     */
    public String getBooksetId() {
        return booksetId;
    }

    /**
     * 设置 账套代码
     * @param booksetId 账套代码
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId == null ? null : booksetId.trim();
    }

    /**
     * 取得 发生日期
     * @return 发生日期
     */
    public Date getHappenDate() {
        return happenDate;
    }

    /**
     * 设置 发生日期
     * @param happenDate 发生日期
     */
    public void setHappenDate(Date happenDate) {
        this.happenDate = happenDate;
    }

    /**
     * 取得 账务明细代码
     * @return 账务明细代码
     */
    public String getBokDetailId() {
        return bokDetailId;
    }

    /**
     * 设置 账务明细代码
     * @param bokDetailId 账务明细代码
     */
    public void setBokDetailId(String bokDetailId) {
        this.bokDetailId = bokDetailId == null ? null : bokDetailId.trim();
    }

    /**
     * 取得 股票持仓类型：01新股；02增发；03流通；04限售
     * @return 股票持仓类型：01新股；02增发；03流通；04限售
     */
    public String getStockPositionType() {
        return stockPositionType;
    }

    /**
     * 设置 股票持仓类型：01新股；02增发；03流通；04限售
     * @param stockPositionType 股票持仓类型：01新股；02增发；03流通；04限售
     */
    public void setStockPositionType(String stockPositionType) {
        this.stockPositionType = stockPositionType == null ? null : stockPositionType.trim();
    }
}