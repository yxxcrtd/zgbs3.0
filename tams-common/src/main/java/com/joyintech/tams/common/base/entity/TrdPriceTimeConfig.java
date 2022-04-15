package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

public class TrdPriceTimeConfig extends BaseEntity {
    /**
     * 配置代码
     */
    private String configNo;

    /**
     * 指令/交易
     */
    private String etrTrad;

    /**
     * 交易场所，上交所、深交所等
     */
    private String tradeMarket;

    /**
     * 交易平台
     */
    private String tradePlat;

    /**
     * 资产分类一
     */
    private String assetClassify1;

    /**
     * 资产分类二
     */
    private String assetClassify2;

    /**
     * 资产分类三
     */
    private String assetClassify3;

    /**
     * 交易方式
     */
    private String dealMode;

    /**
     * 申报价格范围类型
     */
    private String priceTimeType;

    /**
     * 早盘开市时间
     */
    private String openTime;

    /**
     * 早盘收市时间
     */
    private String closeTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 取得 配置代码
     * @return 配置代码
     */
    public String getConfigNo() {
        return configNo;
    }

    /**
     * 设置 配置代码
     * @param configNo 配置代码
     */
    public void setConfigNo(String configNo) {
        this.configNo = configNo == null ? null : configNo.trim();
    }

    /**
     * 取得 指令/交易
     * @return 指令/交易
     */
    public String getEtrTrad() {
        return etrTrad;
    }

    /**
     * 设置 指令/交易
     * @param etrTrad 指令/交易
     */
    public void setEtrTrad(String etrTrad) {
        this.etrTrad = etrTrad == null ? null : etrTrad.trim();
    }

    /**
     * 取得 交易场所，上交所、深交所等
     * @return 交易场所，上交所、深交所等
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易场所，上交所、深交所等
     * @param tradeMarket 交易场所，上交所、深交所等
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    /**
     * 取得 交易平台
     * @return 交易平台
     */
    public String getTradePlat() {
        return tradePlat;
    }

    /**
     * 设置 交易平台
     * @param tradePlat 交易平台
     */
    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat == null ? null : tradePlat.trim();
    }

    /**
     * 取得 资产分类一
     * @return 资产分类一
     */
    public String getAssetClassify1() {
        return assetClassify1;
    }

    /**
     * 设置 资产分类一
     * @param assetClassify1 资产分类一
     */
    public void setAssetClassify1(String assetClassify1) {
        this.assetClassify1 = assetClassify1 == null ? null : assetClassify1.trim();
    }

    /**
     * 取得 资产分类二
     * @return 资产分类二
     */
    public String getAssetClassify2() {
        return assetClassify2;
    }

    /**
     * 设置 资产分类二
     * @param assetClassify2 资产分类二
     */
    public void setAssetClassify2(String assetClassify2) {
        this.assetClassify2 = assetClassify2 == null ? null : assetClassify2.trim();
    }

    /**
     * 取得 资产分类三
     * @return 资产分类三
     */
    public String getAssetClassify3() {
        return assetClassify3;
    }

    /**
     * 设置 资产分类三
     * @param assetClassify3 资产分类三
     */
    public void setAssetClassify3(String assetClassify3) {
        this.assetClassify3 = assetClassify3 == null ? null : assetClassify3.trim();
    }

    /**
     * 取得 交易方式
     * @return 交易方式
     */
    public String getDealMode() {
        return dealMode;
    }

    /**
     * 设置 交易方式
     * @param dealMode 交易方式
     */
    public void setDealMode(String dealMode) {
        this.dealMode = dealMode == null ? null : dealMode.trim();
    }

    /**
     * 取得 申报价格范围类型
     * @return 申报价格范围类型
     */
    public String getPriceTimeType() {
        return priceTimeType;
    }

    /**
     * 设置 申报价格范围类型
     * @param priceTimeType 申报价格范围类型
     */
    public void setPriceTimeType(String priceTimeType) {
        this.priceTimeType = priceTimeType == null ? null : priceTimeType.trim();
    }

    /**
     * 取得 早盘开市时间
     * @return 早盘开市时间
     */
    public String getOpenTime() {
        return openTime;
    }

    /**
     * 设置 早盘开市时间
     * @param openTime 早盘开市时间
     */
    public void setOpenTime(String openTime) {
        this.openTime = openTime == null ? null : openTime.trim();
    }

    /**
     * 取得 早盘收市时间
     * @return 早盘收市时间
     */
    public String getCloseTime() {
        return closeTime;
    }

    /**
     * 设置 早盘收市时间
     * @param closeTime 早盘收市时间
     */
    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime == null ? null : closeTime.trim();
    }

    /**
     * 取得 备注
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public enum ETRTRADEnum {
        ETR("ETR"),
        TRAD("TRAD"),
        ;
        private final String code;

        ETRTRADEnum(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}