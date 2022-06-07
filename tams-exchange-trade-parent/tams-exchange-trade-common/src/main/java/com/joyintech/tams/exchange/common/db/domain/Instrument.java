package com.joyintech.tams.exchange.common.db.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.tams.exchange.common.utils.DoubleSerializerUtils;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Instrument implements Serializable, KeyMethodInterface<String>, InstrumentInterface {
    private static final long serialVersionUID = 1387055817436821859L;
    @NotNull
    @Size(max = 32)
    @Id
    private String innerCode;//合约代码

    @NotNull
    @Size(max = 8)
    private String market;//市场

    @NotNull
    @Size(max = 32)
    private String stockCode;//合约代码

    @NotNull
    @Size(max = 32)
    private String stockName;//合约名称

    @NotNull
    @Digits(integer = 8, fraction = 0)
    private Integer buyUnit = 0;//最小买卖单位

    @Digits(integer = 8, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal priceStep = new BigDecimal("0.00");//最小价差

    @Digits(integer = 8, fraction = 0)
    private Integer storeUnit = 0;//库存单位

    @Digits(integer = 12, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal upPrice = new BigDecimal("0.00");//上限价

    @Digits(integer = 12, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal downPrice = new BigDecimal("0.00");//下限价

    @Digits(integer = 18, fraction = 0)
    private Long highVolume = 0L;//限价单最大可交易数量

    @Digits(integer = 8, fraction = 0)
    private Integer lowVolume = 0;//限价单最小可交易数量

    @Size(max = 3)
    private String assetType;//资产类型

    @Digits(integer = 8, fraction = 2)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal parValue = new BigDecimal("0.00");//面值

    @Size(max = 1)
    private String stbtransType;//转让类型

    @Size(max = 1)
    private String stbtransStatus;//转让状态

    @Size(max = 4)
    private String subStockType;//证券子类

    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal lowBalance = new BigDecimal("0.00");//交易最少金额

    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal highBalance = new BigDecimal("0.00");//交易最大金额

    @Size(max = 1)
    private String exdividendFlag;//除权除息标志

    @Size(max = 1)
    private String bondinvFlag;//债券投资标志

    @Size(max = 1)
    private String stkcodeStatus;//证券状态

    @Size(max = 1)
    private String stbtransFlag;//转让标志

    @Digits(integer = 8, fraction = 0)
    private Integer marketLowVolume = 0;//市价单最小可交易数量

    @Digits(integer = 16, fraction = 0)
    private Long marketHighVolume = 0L;//市价单最大可交易数量

    private Date updateTime;

    private String postStr;

    @Size(max = 3)
    private String isFullPrice;//Y:是 ， N：否

    public int count;

    @Size(max = 64)
    private String tradePlat;//允许交易的平台
    private String netPricFlag;//净价交易标志

    @Size(max = 3)
    private String assetType1;//资产二级分类

    private int contractMultiplier;//合约乘数
    @Size(max = 8)
    private String trmFundType;//交易所基金类型
    @Size(max = 8)
    private String etfType;//ETF类型

    /**
     * ETF 申赎代码
     *
     * @author swwan
     * @date 2020/12/2
     */
    private String purRedId;

    /**
     * ETF 申赎简称
     *
     * @author swwan
     * @date 2020/12/2
     */
    private String purRedAbbr;

    @Id
    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode == null ? null : innerCode.trim();
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market == null ? null : market.trim();
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode == null ? null : stockCode.trim();
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName == null ? null : stockName.trim();
    }

    public int getBuyUnit() {
        return buyUnit;
    }

    public void setBuyUnit(Integer buyUnit) {
        this.buyUnit = buyUnit;
    }

    public BigDecimal getPriceStep() {
        return priceStep;
    }

    public void setPriceStep(BigDecimal priceStep) {
        this.priceStep = priceStep;
    }

    public Integer getStoreUnit() {
        return storeUnit;
    }

    public void setStoreUnit(Integer storeUnit) {
        this.storeUnit = storeUnit;
    }

    public BigDecimal getUpPrice() {
        return upPrice;
    }

    public void setUpPrice(BigDecimal upPrice) {
        this.upPrice = upPrice;
    }

    public BigDecimal getDownPrice() {
        return downPrice;
    }

    public void setDownPrice(BigDecimal downPrice) {
        this.downPrice = downPrice;
    }

    public Long getHighVolume() {
        return highVolume;
    }

    public void setHighVolume(Long highVolume) {
        this.highVolume = highVolume;
    }

    public Integer getLowVolume() {
        return lowVolume;
    }

    public void setLowVolume(Integer lowVolume) {
        this.lowVolume = lowVolume;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType == null ? null : assetType.trim();
    }

    public BigDecimal getParValue() {
        return parValue;
    }

    public void setParValue(BigDecimal parValue) {
        this.parValue = parValue;
    }

    public String getStbtransType() {
        return stbtransType;
    }

    public void setStbtransType(String stbtransType) {
        this.stbtransType = stbtransType == null ? null : stbtransType.trim();
    }

    public String getStbtransStatus() {
        return stbtransStatus;
    }

    public void setStbtransStatus(String stbtransStatus) {
        this.stbtransStatus = stbtransStatus == null ? null : stbtransStatus.trim();
    }

    public String getSubStockType() {
        return subStockType;
    }

    public void setSubStockType(String subStockType) {
        this.subStockType = subStockType == null ? null : subStockType.trim();
    }

    public BigDecimal getLowBalance() {
        return lowBalance;
    }

    public void setLowBalance(BigDecimal lowBalance) {
        this.lowBalance = lowBalance;
    }

    public BigDecimal getHighBalance() {
        return highBalance;
    }

    public void setHighBalance(BigDecimal highBalance) {
        this.highBalance = highBalance;
    }

    public String getExdividendFlag() {
        return exdividendFlag;
    }

    public void setExdividendFlag(String exdividendFlag) {
        this.exdividendFlag = exdividendFlag == null ? null : exdividendFlag.trim();
    }

    public String getBondinvFlag() {
        return bondinvFlag;
    }

    public void setBondinvFlag(String bondinvFlag) {
        this.bondinvFlag = bondinvFlag == null ? null : bondinvFlag.trim();
    }

    public String getStkcodeStatus() {
        return stkcodeStatus;
    }

    public void setStkcodeStatus(String stkcodeStatus) {
        this.stkcodeStatus = stkcodeStatus == null ? null : stkcodeStatus.trim();
    }

    public String getStbtransFlag() {
        return stbtransFlag;
    }

    public void setStbtransFlag(String stbtransFlag) {
        this.stbtransFlag = stbtransFlag == null ? null : stbtransFlag.trim();
    }

    public Integer getMarketLowVolume() {
        return marketLowVolume;
    }

    public void setMarketLowVolume(Integer marketLowVolume) {
        this.marketLowVolume = marketLowVolume;
    }

    public Long getMarketHighVolume() {
        return marketHighVolume;
    }

    public void setMarketHighVolume(Long marketHighVolume) {
        this.marketHighVolume = marketHighVolume;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getPostStr() {
        return postStr;
    }

    public void setPostStr(String postStr) {
        this.postStr = postStr;
    }

    public String getIsFullPrice() {
        return isFullPrice;
    }

    public void setIsFullPrice(String isFullPrice) {
        this.isFullPrice = isFullPrice;
    }

    @Override
    public String getId() {
        return innerCode;
    }

    @Override
    public void setId(String id) {
        innerCode = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getNetPricFlag() {
        return netPricFlag;
    }

    public void setNetPricFlag(String netPricFlag) {
        this.netPricFlag = netPricFlag;
    }

    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public int getContractMultiplier() {
        return contractMultiplier;
    }

    public void setContractMultiplier(int contractMultiplier) {
        this.contractMultiplier = contractMultiplier;
    }

    public String getTrmFundType() {
        return trmFundType;
    }

    public void setTrmFundType(String trmFundType) {
        this.trmFundType = trmFundType;
    }

    public String getEtfType() {
        return etfType;
    }

    public void setEtfType(String etfType) {
        this.etfType = etfType;
    }

    public String getPurRedId() {
        return purRedId;
    }

    public void setPurRedId(String purRedId) {
        this.purRedId = purRedId;
    }

    public String getPurRedAbbr() {
        return purRedAbbr;
    }

    public void setPurRedAbbr(String purRedAbbr) {
        this.purRedAbbr = purRedAbbr;
    }
}