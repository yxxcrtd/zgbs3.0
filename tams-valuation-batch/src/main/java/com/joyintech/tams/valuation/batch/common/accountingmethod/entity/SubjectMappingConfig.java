package com.joyintech.tams.valuation.batch.common.accountingmethod.entity;

/**
 * 科目映射配置
 */
public class SubjectMappingConfig {

    /**
     * 账务明细代码
     */
    private String bokDetailId;

    /**
     * 证券类型
     */
    private String secType;

    /**
     * 证券分类
     */
    private String type2;

    /**
     * 交易市场
     */
    private String tradeMarket;

    /**
     * 发行方式
     */
    private String issueType;

    /**
     * 交易对手类型
     */
    private String counterType;

    /**
     * 账户类型
     */
    private String accountType;

    /**
     * 费用类型
     */
    private String feeType;

    /**
     * 交易属性
     */
    private String tradeAttribute;

    /**
     *  税费类型
     */
    private String taxFeeType;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 返回 账务明细代码
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
        this.bokDetailId = bokDetailId;
    }

    /**
     * @return 证券类型
     */
    public String getSecType() {
        return secType;
    }

    /**
     * 设置 证券类型
     *
     * @param secType 证券类型
     */
    public void setSecType(String secType) {
        this.secType = secType;
    }

    /**
     * @return 证券分类
     */
    public String getType2() {
        return type2;
    }

    /**
     * 设置 证券分类
     *
     * @param type2 证券分类
     */
    public void setType2(String type2) {
        this.type2 = type2;
    }

    /**
     * @return 交易市场
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易市场
     *
     * @param tradeMarket 交易市场
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    /**
     * @return 发行方式
     */
    public String getIssueType() {
        return issueType;
    }

    /**
     * 设置 发行方式
     *
     * @param issueType 发行方式
     */
    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    /**
     * @return 交易对手类型
     */
    public String getCounterType() {
        return counterType;
    }

    /**
     * 设置 交易对手类型
     *
     * @param counterType 交易对手类型
     */
    public void setCounterType(String counterType) {
        this.counterType = counterType;
    }

    /**
     * @return 账户类型
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * 设置 账户类型
     *
     * @param accountType 账户类型
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /**
     * @return 费用类型
     */
    public String getFeeType() {
        return feeType;
    }

    /**
     * 设置 费用类型
     *
     * @param feeType 费用类型
     */
    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    /**
     * @return 交易属性
     */
    public String getTradeAttribute() {
        return tradeAttribute;
    }

    /**
     * 设置 交易属性
     *
     * @param tradeAttribute 交易属性
     */
    public void setTradeAttribute(String tradeAttribute) {
        this.tradeAttribute = tradeAttribute;
    }

    /**
     * @return 税费类型
     */
    public String getTaxFeeType() {
        return taxFeeType;
    }

    /**
     * 设置 税费类型
     *
     * @param taxFeeType 税费类型
     */
    public void setTaxFeeType(String taxFeeType) {
        this.taxFeeType = taxFeeType;
    }

    /**
     * @return 币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 币种
     *
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * 交易对手
     * @return 交易对手
     */
    public String getCounterId() {
        return counterId;
    }

    /**
     * 设置 交易对手
     * @param counterId 交易对手
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }
}
