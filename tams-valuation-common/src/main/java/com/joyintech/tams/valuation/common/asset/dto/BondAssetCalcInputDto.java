package com.joyintech.tams.valuation.common.asset.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: 2.1.2
 * @description:
 * @author: 强成西
 * @create: 2019-12-28 15:53
 **/
public class BondAssetCalcInputDto {

    private String finprodId;
    private String tradeMarket;
    private String ps;
    private BigDecimal unitInt;
    private Date settleDate;

    /**
     * 获取 finprodId
     *
     * @return finprodId
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 finprodId
     *
     * @param finprodId finprodId
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 获取 tradeMarket
     *
     * @return tradeMarket
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 tradeMarket
     *
     * @param tradeMarket tradeMarket
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    /**
     * 获取 ps
     *
     * @return ps
     */
    public String getPs() {
        return ps;
    }

    /**
     * 设置 ps
     *
     * @param ps ps
     */
    public void setPs(String ps) {
        this.ps = ps;
    }

    /**
     * 获取 unitInt
     *
     * @return unitInt
     */
    public BigDecimal getUnitInt() {
        return unitInt;
    }

    /**
     * 设置 unitInt
     *
     * @param unitInt unitInt
     */
    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    /**
     * 获取 settleDate
     *
     * @return settleDate
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 settleDate
     *
     * @param settleDate settleDate
     */
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }
}
