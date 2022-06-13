package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.math.BigDecimal;
import java.util.Date;

public class ValStockCirculate extends BaseEntity {
    /**
     * 产品代码
     */
    private String prodId;

    /**
     * 资产代码
     */
    private String assetId;

    /**
     * 资产代码
     */
    private String assetAbbr;

    /**
     * 新增、修改、删除
     */
    private String dataActionSign;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 流通日期
     */
    private Date dealDate;

    /**
     * 交割日
     */
    private Date settdateDate;

    /**
     * 交易发行方式
     */
    private String dealIssueType;

    /**
     * 流通数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal dealNum;

    /**
     * 流通价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitCprice;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 预期上市流通日
     */
    private Date expCirculateDate;

    /**
     * 取得 产品代码
     * @return 产品代码
     */
    public String getProdId() {
        return prodId;
    }

    /**
     * 设置 产品代码
     * @param prodId 产品代码
     */
    public void setProdId(String prodId) {
        this.prodId = prodId == null ? null : prodId.trim();
    }

    /**
     * 取得 资产代码
     * @return 资产代码
     */
    public String getAssetId() {
        return assetId;
    }

    /**
     * 设置 资产代码
     * @param assetId 资产代码
     */
    public void setAssetId(String assetId) {
        this.assetId = assetId == null ? null : assetId.trim();
    }

    /**
     * 取得 新增、修改、删除
     * @return 新增、修改、删除
     */
    public String getDataActionSign() {
        return dataActionSign;
    }

    /**
     * 设置 新增、修改、删除
     * @param dataActionSign 新增、修改、删除
     */
    public void setDataActionSign(String dataActionSign) {
        this.dataActionSign = dataActionSign == null ? null : dataActionSign.trim();
    }

    /**
     * 取得 交易场所
     * @return 交易场所
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易场所
     * @param tradeMarket 交易场所
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
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
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    /**
     * 取得 交割日
     * @return 交割日
     */
    public Date getSettdateDate() {
        return settdateDate;
    }

    /**
     * 设置 交割日
     * @param settdateDate 交割日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setSettdateDate(Date settdateDate) {
        this.settdateDate = settdateDate;
    }

    /**
     * 取得 成交数量
     * @return 成交数量
     */
    public BigDecimal getDealNum() {
        return dealNum;
    }

    /**
     * 设置 成交数量
     * @param dealNum 成交数量
     */
    public void setDealNum(BigDecimal dealNum) {
        this.dealNum = dealNum;
    }

    /**
     * 取得 成交价格
     * @return 成交价格
     */
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    /**
     * 设置 成交价格
     * @param unitCprice 成交价格
     */
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
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

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDealIssueType() {
        return dealIssueType;
    }

    public void setDealIssueType(String dealIssueType) {
        this.dealIssueType = dealIssueType;
    }

    public Date getExpCirculateDate() {
        return expCirculateDate;
    }

    public void setExpCirculateDate(Date expCirculateDate) {
        this.expCirculateDate = expCirculateDate;
    }

    public String getAssetAbbr() {
        return assetAbbr;
    }

    public void setAssetAbbr(String assetAbbr) {
        this.assetAbbr = assetAbbr;
    }

}