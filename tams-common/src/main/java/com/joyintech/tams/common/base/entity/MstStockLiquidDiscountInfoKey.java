package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;
import java.util.Date;

public class MstStockLiquidDiscountInfoKey extends BaseEntity {
    /**
     * 股票代码【市场代码】
     */
    private String sharesId;

    /**
     * 行情日期
     */
    private Date valDate;

    /**
     * 预计上市流通日
     */
    private Date expCircDate;

    /**
     * 取得 股票代码【市场代码】
     * @return 股票代码【市场代码】
     */
    public String getSharesId() {
        return sharesId;
    }

    /**
     * 设置 股票代码【市场代码】
     * @param sharesId 股票代码【市场代码】
     */
    public void setSharesId(String sharesId) {
        this.sharesId = sharesId == null ? null : sharesId.trim();
    }

    /**
     * 取得 行情日期
     * @return 行情日期
     */
    public Date getValDate() {
        return valDate;
    }

    /**
     * 设置 行情日期
     * @param valDate 行情日期
     */
    public void setValDate(Date valDate) {
        this.valDate = valDate;
    }

    /**
     * 取得 预计上市流通日
     * @return 预计上市流通日
     */
    public Date getExpCircDate() {
        return expCircDate;
    }

    /**
     * 设置 预计上市流通日
     * @param expCircDate 预计上市流通日
     */
    public void setExpCircDate(Date expCircDate) {
        this.expCircDate = expCircDate;
    }
}