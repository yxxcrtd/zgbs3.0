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

public class FuturesInstrument implements Serializable, KeyMethodInterface<String>, InstrumentInterface {
    private static final long serialVersionUID = 9022995628556797389L;
    @NotNull
    @Size(max = 32)
    @Id
    private String innerCode;

    @Size(max = 32)
    private String instrumentId;

    @Size(max = 8)
    private String exchangeId;

    @Size(max = 32)
    private String instrumentName;

    @Size(max = 32)
    private String exchangeInstId;

    @Size(max = 32)
    private String productId;

    @Size(max = 8)
    private String productClass;

    @Digits(integer = 8, fraction = 0)
    private int deliveryYear;

    @Digits(integer = 8, fraction = 0)
    private int deliveryMonth;

    @Digits(integer = 16, fraction = 0)
    private long maxMarketOrderVolume;

    @Digits(integer = 8, fraction = 0)
    private int minMarketOrderVolume;

    @Digits(integer = 16, fraction = 0)
    private long maxLimitOrderVolume;

    @Digits(integer = 8, fraction = 0)
    private int minLimitOrderVolume;

    @Digits(integer = 8, fraction = 0)
    private int volumeMultiple;

    @Digits(integer = 12, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal priceTick = new BigDecimal("0.0000");

    @Size(max = 10)
    private String createDate;

    @Size(max = 10)
    private String openDate;

    @Size(max = 10)
    private String expireDate;

    @Size(max = 10)
    private String startDelivDate;

    @Size(max = 10)
    private String endDelivDate;

    @Size(max = 8)
    private String instLifePhase;

    @Digits(integer = 1, fraction = 0)
    private int isTrading;

    @Size(max = 8)
    private String positionType;

    @Size(max = 8)
    private String positionDateType;

    @Digits(integer = 12, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal longMarginRatio = new BigDecimal("0.0000");

    @Digits(integer = 12, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal shortMarginRatio = new BigDecimal("0.0000");

    @Size(max = 8)
    private String maxMarginSIdeAlgorithm;

    @Size(max = 32)
    private String underlyingInstrId;

    @Digits(integer = 30, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal strikePrice = new BigDecimal("0.0000");

    @Size(max = 8)
    private String optionsType;

    @Digits(integer = 12, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal underlyingMultiple;

    @Size(max = 8)
    private String combinationType;

    @Digits(integer = 8, fraction = 0)
    private int buyUnit;

    private Date createTime;

    private Date updateTime;

    public int count;

    @Id
    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode == null ? null : innerCode.trim();
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId == null ? null : instrumentId.trim();
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId == null ? null : exchangeId.trim();
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName == null ? null : instrumentName.trim();
    }

    public String getExchangeInstId() {
        return exchangeInstId;
    }

    public void setExchangeInstId(String exchangeInstId) {
        this.exchangeInstId = exchangeInstId == null ? null : exchangeInstId.trim();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId == null ? null : productId.trim();
    }

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass == null ? null : productClass.trim();
    }

    public int getDeliveryYear() {
        return deliveryYear;
    }

    public void setDeliveryYear(int deliveryYear) {
        this.deliveryYear = deliveryYear;
    }

    public int getDeliveryMonth() {
        return deliveryMonth;
    }

    public void setDeliveryMonth(int deliveryMonth) {
        this.deliveryMonth = deliveryMonth;
    }

    public long getMaxMarketOrderVolume() {
        return maxMarketOrderVolume;
    }

    public void setMaxMarketOrderVolume(long maxMarketOrderVolume) {
        this.maxMarketOrderVolume = maxMarketOrderVolume;
    }

    public int getMinMarketOrderVolume() {
        return minMarketOrderVolume;
    }

    public void setMinMarketOrderVolume(int minMarketOrderVolume) {
        this.minMarketOrderVolume = minMarketOrderVolume;
    }

    public long getMaxLimitOrderVolume() {
        return maxLimitOrderVolume;
    }

    public void setMaxLimitOrderVolume(long maxLimitOrderVolume) {
        this.maxLimitOrderVolume = maxLimitOrderVolume;
    }

    public int getMinLimitOrderVolume() {
        return minLimitOrderVolume;
    }

    public void setMinLimitOrderVolume(int minLimitOrderVolume) {
        this.minLimitOrderVolume = minLimitOrderVolume;
    }

    public int getVolumeMultiple() {
        return volumeMultiple;
    }

    public void setVolumeMultiple(int volumeMultiple) {
        this.volumeMultiple = volumeMultiple;
    }

    public BigDecimal getPriceTick() {
        return priceTick;
    }

    public void setPriceTick(BigDecimal priceTick) {
        this.priceTick = priceTick;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate == null ? null : createDate.trim();
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate == null ? null : openDate.trim();
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate == null ? null : expireDate.trim();
    }

    public String getStartDelivDate() {
        return startDelivDate;
    }

    public void setStartDelivDate(String startDelivDate) {
        this.startDelivDate = startDelivDate == null ? null : startDelivDate.trim();
    }

    public String getEndDelivDate() {
        return endDelivDate;
    }

    public void setEndDelivDate(String endDelivDate) {
        this.endDelivDate = endDelivDate == null ? null : endDelivDate.trim();
    }

    public String getInstLifePhase() {
        return instLifePhase;
    }

    public void setInstLifePhase(String instLifePhase) {
        this.instLifePhase = instLifePhase == null ? null : instLifePhase.trim();
    }

    public int getIsTrading() {
        return isTrading;
    }

    public void setIsTrading(int isTrading) {
        this.isTrading = isTrading;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType == null ? null : positionType.trim();
    }

    public String getPositionDateType() {
        return positionDateType;
    }

    public void setPositionDateType(String positionDateType) {
        this.positionDateType = positionDateType == null ? null : positionDateType.trim();
    }

    public BigDecimal getLongMarginRatio() {
        return longMarginRatio;
    }

    public void setLongMarginRatio(BigDecimal longMarginRatio) {
        this.longMarginRatio = longMarginRatio;
    }

    public BigDecimal getShortMarginRatio() {
        return shortMarginRatio;
    }

    public void setShortMarginRatio(BigDecimal shortMarginRatio) {
        this.shortMarginRatio = shortMarginRatio;
    }

    public String getMaxMarginSIdeAlgorithm() {
        return maxMarginSIdeAlgorithm;
    }

    public void setMaxMarginSIdeAlgorithm(String maxMarginSIdeAlgorithm) {
        this.maxMarginSIdeAlgorithm = maxMarginSIdeAlgorithm == null ? null : maxMarginSIdeAlgorithm.trim();
    }

    public String getUnderlyingInstrId() {
        return underlyingInstrId;
    }

    public void setUnderlyingInstrId(String underlyingInstrId) {
        this.underlyingInstrId = underlyingInstrId == null ? null : underlyingInstrId.trim();
    }

    public BigDecimal getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(BigDecimal strikePrice) {
        this.strikePrice = strikePrice;
    }

    public String getOptionsType() {
        return optionsType;
    }

    public void setOptionsType(String optionsType) {
        this.optionsType = optionsType == null ? null : optionsType.trim();
    }

    public BigDecimal getUnderlyingMultiple() {
        return underlyingMultiple;
    }

    public void setUnderlyingMultiple(BigDecimal underlyingMultiple) {
        this.underlyingMultiple = underlyingMultiple;
    }

    public String getCombinationType() {
        return combinationType;
    }

    public void setCombinationType(String combinationType) {
        this.combinationType = combinationType == null ? null : combinationType.trim();
    }

    @Override
    public String getMarket() {
        return exchangeId;
    }

    @Override
    public String getStockCode() {
        return innerCode;
    }

    @Override
    public String getStockName() {
        return instrumentName;
    }

    public int getBuyUnit() {
        return buyUnit;
    }

    @Override
    public BigDecimal getPriceStep() {
        return priceTick;
    }

    @Override
    public Long getHighVolume() {
        return (long) maxLimitOrderVolume;
    }

    @Override
    public Integer getLowVolume() {
        return minLimitOrderVolume;
    }

    @Override
    public String getAssetType() {
        return null;
    }

    @Override
    public BigDecimal getLowBalance() {
        return null;
    }

    @Override
    public BigDecimal getHighBalance() {
        return null;
    }

    @Override
    public Integer getMarketLowVolume() {
        return minMarketOrderVolume;

    }

    @Override
    public Long getMarketHighVolume() {
        return maxMarketOrderVolume;
    }

    public void setBuyUnit(int buyUnit) {
        this.buyUnit = buyUnit;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String getId() {
        return innerCode;
    }

    @Override
    public void setId(String id) {
        innerCode = id;
    }
}