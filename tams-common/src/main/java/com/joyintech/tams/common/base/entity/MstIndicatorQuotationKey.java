package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;
import java.util.Date;

public class MstIndicatorQuotationKey extends BaseEntity {
    /**
     * 股票代码，市场代码
     */
    private String indicatorCode;

    /**
     * 交易日
     */
    private Date dealDate;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 取得 股票代码，市场代码
     * @return 股票代码，市场代码
     */
    public String getIndicatorCode() {
        return indicatorCode;
    }

    /**
     * 设置 股票代码，市场代码
     * @param indicatorCode 股票代码，市场代码
     */
    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode == null ? null : indicatorCode.trim();
    }

    /**
     * 取得 交易日
     * @return 交易日
     */
    public Date getDealDate() {
        return dealDate;
    }

    /**
     * 设置 交易日
     * @param dealDate 交易日
     */
    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    /**
     * 取得 币种
     * @return 币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 币种
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }
}