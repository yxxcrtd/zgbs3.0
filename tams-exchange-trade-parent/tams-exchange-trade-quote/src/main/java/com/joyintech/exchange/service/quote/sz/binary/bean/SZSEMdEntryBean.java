package com.joyintech.exchange.service.quote.sz.binary.bean;

/**
 * 行情条目
 *
 * @author swwan
 * @className MdEntryBean
 * @date 2021/10/29 16:37
 */
public class SZSEMdEntryBean {
    // yanjianhui 修改于 2022/8/24 10:07  接受数据的字段类型错误，应为double。
    private double mdEntryPx;
    private int mdEntrySize;
    private String mdEntryType;
    private int mdPriceLevel;
    private int noOrders;
    private int numberOfOrders;
    private int orderQty;

    public double getMdEntryPx() {
        return mdEntryPx;
    }

    public void setMdEntryPx(double mdEntryPx) {
        this.mdEntryPx = mdEntryPx;
    }

    public int getMdEntrySize() {
        return mdEntrySize;
    }

    public void setMdEntrySize(int mdEntrySize) {
        this.mdEntrySize = mdEntrySize;
    }

    public String getMdEntryType() {
        return mdEntryType;
    }

    public void setMdEntryType(String mdEntryType) {
        this.mdEntryType = mdEntryType;
    }

    public int getMdPriceLevel() {
        return mdPriceLevel;
    }

    public void setMdPriceLevel(int mdPriceLevel) {
        this.mdPriceLevel = mdPriceLevel;
    }

    public int getNoOrders() {
        return noOrders;
    }

    public void setNoOrders(int noOrders) {
        this.noOrders = noOrders;
    }

    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(int numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }
}
