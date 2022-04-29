package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class TrdTradeNewStockLocked extends BaseEntity {
    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 锁定数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal shareAmt;

    /**
     * 单位成本
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitCprice;

    /**
     * 锁定开始日期
     */
    private Date startDate;

    /**
     * 锁定结束日期
     */
    private Date endDate;

    /**
     * 锁定天数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal totalDays;

    /**
     * 预计上市流通日
     */
    private Date expDate;

    /**
     * 内部关联交易编号
     */
    private String mTradeId;

    /**
     * 数据来源
     */
    private String genType;

    /**
     * 取得 交易编号
     * @return 交易编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 交易编号
     * @param tradeId 交易编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }

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

    /**
     * 取得 锁定数量
     * @return 锁定数量
     */
    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    /**
     * 设置 锁定数量
     * @param shareAmt 锁定数量
     */
    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    /**
     * 取得 单位成本
     * @return 单位成本
     */
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    /**
     * 设置 单位成本
     * @param unitCprice 单位成本
     */
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    /**
     * 取得 锁定开始日期
     * @return 锁定开始日期
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * 设置 锁定开始日期
     * @param startDate 锁定开始日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * 取得 锁定结束日期
     * @return 锁定结束日期
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * 设置 锁定结束日期
     * @param endDate 锁定结束日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * 取得 锁定天数
     * @return 锁定天数
     */
    public BigDecimal getTotalDays() {
        return totalDays;
    }

    /**
     * 设置 锁定天数
     * @param totalDays 锁定天数
     */
    public void setTotalDays(BigDecimal totalDays) {
        this.totalDays = totalDays;
    }

    /**
     * 取得 预计上市流通日
     * @return 预计上市流通日
     */
    public Date getExpDate() {
        return expDate;
    }

    /**
     * 设置 预计上市流通日
     * @param expDate 预计上市流通日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    /**
     * 取得 内部关联交易编号
     * @return 内部关联交易编号
     */
    public String getmTradeId() {
        return mTradeId;
    }

    /**
     * 设置 内部关联交易编号
     * @param mTradeId 内部关联交易编号
     */
    public void setmTradeId(String mTradeId) {
        this.mTradeId = mTradeId == null ? null : mTradeId.trim();
    }

    /**
     * 取得 数据来源
     * @return 数据来源
     */
    public String getGenType() {
        return genType;
    }

    /**
     * 设置 数据来源
     * @param genType 数据来源
     */
    public void setGenType(String genType) {
        this.genType = genType == null ? null : genType.trim();
    }
}