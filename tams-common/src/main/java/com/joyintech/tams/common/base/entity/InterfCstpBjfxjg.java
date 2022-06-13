package com.joyintech.tams.common.base.entity;

import java.math.BigDecimal;

/**
 * 描述:INTERF_CSTP_BJFXJG表的实体类
 *
 * @author jiangpeng
 * @date 2022/5/6 17:20
 */
public class InterfCstpBjfxjg {
    /**
     * 工具代码
     */
    private String instrumentId;

    /**
     * 工具简称
     */
    private String instrumentShortName;

    /**
     * 工具全称
     */
    private String instrumentFullName;

    /**
     * 发行人
     */
    private String issuer;

    /**
     * 发行人机构 21 位码
     */
    private String issuer21FirmId;

    /**
     * 发行价格（元）
     */
    private BigDecimal issuePrice;

    /**
     * 利差（BP）
     */
    private BigDecimal spread;

    /**
     * 票面利率
     */
    private BigDecimal couponRate;

    /**
     * 实际发行量（亿元）
     */
    private BigDecimal realIssuesize;

    /**
     * 工具状态 1-发行成功 2-发行失败
     */
    private String instrumentStatus;

    /**
     * 工具类型
     * CD-同业存单
     * CFETS/K 2409—2021
     * 265
     * 工具类型 CD_FTA-自贸区同业存单
     * 6-央行票据
     * 0-国债
     * 1-政策性金融债
     * 5-企业债
     * 3-次级债
     * 2-短期融资券
     * 7-国际开发机构债
     * 8-混合资本债
     * 10-中期票据
     * 11-地方政府债
     * 12-财务公司债
     * 13-证券公司债
     * 23-集合票据
     * 27-金融租赁公司金融债
     * 9-资产支持证券
     * 22-政府支持机构债券
     * 19-超短期融资券
     * 21-定向工具
     * 14-公司短期融资券
     * 49-二级资本工具
     * 50-保险公司资本补充债
     * 45-资产管理公司金融债
     */
    private String instrumentType;

    /**
     * 失败原因
     */
    private String failureReasons;

    /**
     * 消息类型
     * 2-发行结果信息（发行人接收）
     * 3-投标结果信息（投资人接收）
     * 发行失败时，消息类型默认为发行结果
     * 信息
     */
    private String messageType;

    /**
     * 发行方式
     */
    private String issueMethod;

    /**
     * 息票类型
     */
    private String couponRateType;

    /**
     * 数据类型标识 0-本方数据 2-关联机构数据
     */
    private String dataCategoryIndicator;

    /**
     * 申购机构
     */
    private String fullChineseNameOfSubscribe;

    /**
     * 申购机构 21 位码
     */
    private String subscribeInstitutionId;

    /**
     * 认购量（亿元）
     */
    private BigDecimal subscriptionAmount;

    /**
     * 应缴纳金额（元）
     */
    private BigDecimal actualFee;

    /**
     * 认购时间 格式：yyyy-mm-dd HH:MM:SS
     */
    private String subscriptionTime;

    /**
     * 提交用户
     */
    private String submitUser;

    /**
     * 币种
     */
    private String currency;

    /**
     * 报价编号(内部)
     */
    private String trdId;

    /**
     * 工具代码
     *
     * @return INSTRUMENT_ID 工具代码
     */
    public String getInstrumentId() {
        return instrumentId;
    }

    /**
     * 工具代码
     *
     * @param instrumentId 工具代码
     */
    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId == null ? null : instrumentId.trim();
    }

    /**
     * 工具简称
     *
     * @return INSTRUMENT_SHORT_NAME 工具简称
     */
    public String getInstrumentShortName() {
        return instrumentShortName;
    }

    /**
     * 工具简称
     *
     * @param instrumentShortName 工具简称
     */
    public void setInstrumentShortName(String instrumentShortName) {
        this.instrumentShortName = instrumentShortName == null ? null : instrumentShortName.trim();
    }

    /**
     * 工具全称
     *
     * @return INSTRUMENT_FULL_NAME 工具全称
     */
    public String getInstrumentFullName() {
        return instrumentFullName;
    }

    /**
     * 工具全称
     *
     * @param instrumentFullName 工具全称
     */
    public void setInstrumentFullName(String instrumentFullName) {
        this.instrumentFullName = instrumentFullName == null ? null : instrumentFullName.trim();
    }

    /**
     * 发行人
     *
     * @return ISSUER 发行人
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * 发行人
     *
     * @param issuer 发行人
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer == null ? null : issuer.trim();
    }

    /**
     * 发行人机构 21 位码
     *
     * @return ISSUER_21_FIRM_ID 发行人机构 21 位码
     */
    public String getIssuer21FirmId() {
        return issuer21FirmId;
    }

    /**
     * 发行人机构 21 位码
     *
     * @param issuer21FirmId 发行人机构 21 位码
     */
    public void setIssuer21FirmId(String issuer21FirmId) {
        this.issuer21FirmId = issuer21FirmId == null ? null : issuer21FirmId.trim();
    }

    /**
     * 发行价格（元）
     *
     * @return ISSUE_PRICE 发行价格（元）
     */
    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    /**
     * 发行价格（元）
     *
     * @param issuePrice 发行价格（元）
     */
    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    /**
     * 利差（BP）
     *
     * @return SPREAD 利差（BP）
     */
    public BigDecimal getSpread() {
        return spread;
    }

    /**
     * 利差（BP）
     *
     * @param spread 利差（BP）
     */
    public void setSpread(BigDecimal spread) {
        this.spread = spread;
    }

    /**
     * 票面利率
     *
     * @return COUPON_RATE 票面利率
     */
    public BigDecimal getCouponRate() {
        return couponRate;
    }

    /**
     * 票面利率
     *
     * @param couponRate 票面利率
     */
    public void setCouponRate(BigDecimal couponRate) {
        this.couponRate = couponRate;
    }

    /**
     * 实际发行量（亿元）
     *
     * @return REAL_ISSUESIZE 实际发行量（亿元）
     */
    public BigDecimal getRealIssuesize() {
        return realIssuesize;
    }

    /**
     * 实际发行量（亿元）
     *
     * @param realIssuesize 实际发行量（亿元）
     */
    public void setRealIssuesize(BigDecimal realIssuesize) {
        this.realIssuesize = realIssuesize;
    }

    /**
     * 工具状态 1-发行成功 2-发行失败
     *
     * @return INSTRUMENT_STATUS 工具状态 1-发行成功 2-发行失败
     */
    public String getInstrumentStatus() {
        return instrumentStatus;
    }

    /**
     * 工具状态 1-发行成功 2-发行失败
     *
     * @param instrumentStatus 工具状态 1-发行成功 2-发行失败
     */
    public void setInstrumentStatus(String instrumentStatus) {
        this.instrumentStatus = instrumentStatus == null ? null : instrumentStatus.trim();
    }

    /**
     * 工具类型
     * CD-同业存单
     * CFETS/K 2409—2021
     * 265
     * 工具类型 CD_FTA-自贸区同业存单
     * 6-央行票据
     * 0-国债
     * 1-政策性金融债
     * 5-企业债
     * 3-次级债
     * 2-短期融资券
     * 7-国际开发机构债
     * 8-混合资本债
     * 10-中期票据
     * 11-地方政府债
     * 12-财务公司债
     * 13-证券公司债
     * 23-集合票据
     * 27-金融租赁公司金融债
     * 9-资产支持证券
     * 22-政府支持机构债券
     * 19-超短期融资券
     * 21-定向工具
     * 14-公司短期融资券
     * 49-二级资本工具
     * 50-保险公司资本补充债
     * 45-资产管理公司金融债
     *
     * @return INSTRUMENT_TYPE 工具类型
     * CD-同业存单
     * CFETS/K 2409—2021
     * 265
     * 工具类型 CD_FTA-自贸区同业存单
     * 6-央行票据
     * 0-国债
     * 1-政策性金融债
     * 5-企业债
     * 3-次级债
     * 2-短期融资券
     * 7-国际开发机构债
     * 8-混合资本债
     * 10-中期票据
     * 11-地方政府债
     * 12-财务公司债
     * 13-证券公司债
     * 23-集合票据
     * 27-金融租赁公司金融债
     * 9-资产支持证券
     * 22-政府支持机构债券
     * 19-超短期融资券
     * 21-定向工具
     * 14-公司短期融资券
     * 49-二级资本工具
     * 50-保险公司资本补充债
     * 45-资产管理公司金融债
     */
    public String getInstrumentType() {
        return instrumentType;
    }

    /**
     * 工具类型
     * CD-同业存单
     * CFETS/K 2409—2021
     * 265
     * 工具类型 CD_FTA-自贸区同业存单
     * 6-央行票据
     * 0-国债
     * 1-政策性金融债
     * 5-企业债
     * 3-次级债
     * 2-短期融资券
     * 7-国际开发机构债
     * 8-混合资本债
     * 10-中期票据
     * 11-地方政府债
     * 12-财务公司债
     * 13-证券公司债
     * 23-集合票据
     * 27-金融租赁公司金融债
     * 9-资产支持证券
     * 22-政府支持机构债券
     * 19-超短期融资券
     * 21-定向工具
     * 14-公司短期融资券
     * 49-二级资本工具
     * 50-保险公司资本补充债
     * 45-资产管理公司金融债
     *
     * @param instrumentType 工具类型
     *                       CD-同业存单
     *                       CFETS/K 2409—2021
     *                       265
     *                       工具类型 CD_FTA-自贸区同业存单
     *                       6-央行票据
     *                       0-国债
     *                       1-政策性金融债
     *                       5-企业债
     *                       3-次级债
     *                       2-短期融资券
     *                       7-国际开发机构债
     *                       8-混合资本债
     *                       10-中期票据
     *                       11-地方政府债
     *                       12-财务公司债
     *                       13-证券公司债
     *                       23-集合票据
     *                       27-金融租赁公司金融债
     *                       9-资产支持证券
     *                       22-政府支持机构债券
     *                       19-超短期融资券
     *                       21-定向工具
     *                       14-公司短期融资券
     *                       49-二级资本工具
     *                       50-保险公司资本补充债
     *                       45-资产管理公司金融债
     */
    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType == null ? null : instrumentType.trim();
    }

    /**
     * 失败原因
     *
     * @return FAILURE_REASONS 失败原因
     */
    public String getFailureReasons() {
        return failureReasons;
    }

    /**
     * 失败原因
     *
     * @param failureReasons 失败原因
     */
    public void setFailureReasons(String failureReasons) {
        this.failureReasons = failureReasons == null ? null : failureReasons.trim();
    }

    /**
     * 消息类型
     * 2-发行结果信息（发行人接收）
     * 3-投标结果信息（投资人接收）
     * 发行失败时，消息类型默认为发行结果
     * 信息
     *
     * @return MESSAGE_TYPE 消息类型
     * 2-发行结果信息（发行人接收）
     * 3-投标结果信息（投资人接收）
     * 发行失败时，消息类型默认为发行结果
     * 信息
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * 消息类型
     * 2-发行结果信息（发行人接收）
     * 3-投标结果信息（投资人接收）
     * 发行失败时，消息类型默认为发行结果
     * 信息
     *
     * @param messageType 消息类型
     *                    2-发行结果信息（发行人接收）
     *                    3-投标结果信息（投资人接收）
     *                    发行失败时，消息类型默认为发行结果
     *                    信息
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType == null ? null : messageType.trim();
    }

    /**
     * 发行方式
     *
     * @return ISSUE_METHOD 发行方式
     */
    public String getIssueMethod() {
        return issueMethod;
    }

    /**
     * 发行方式
     *
     * @param issueMethod 发行方式
     */
    public void setIssueMethod(String issueMethod) {
        this.issueMethod = issueMethod == null ? null : issueMethod.trim();
    }

    /**
     * 息票类型
     *
     * @return COUPON_RATE_TYPE 息票类型
     */
    public String getCouponRateType() {
        return couponRateType;
    }

    /**
     * 息票类型
     *
     * @param couponRateType 息票类型
     */
    public void setCouponRateType(String couponRateType) {
        this.couponRateType = couponRateType == null ? null : couponRateType.trim();
    }

    /**
     * 数据类型标识 0-本方数据 2-关联机构数据
     *
     * @return DATA_CATEGORY_INDICATOR 数据类型标识 0-本方数据 2-关联机构数据
     */
    public String getDataCategoryIndicator() {
        return dataCategoryIndicator;
    }

    /**
     * 数据类型标识 0-本方数据 2-关联机构数据
     *
     * @param dataCategoryIndicator 数据类型标识 0-本方数据 2-关联机构数据
     */
    public void setDataCategoryIndicator(String dataCategoryIndicator) {
        this.dataCategoryIndicator = dataCategoryIndicator == null ? null : dataCategoryIndicator.trim();
    }

    /**
     * 申购机构
     *
     * @return FULL_CHINESE_NAME_OF_SUBSCRIBE 申购机构
     */
    public String getFullChineseNameOfSubscribe() {
        return fullChineseNameOfSubscribe;
    }

    /**
     * 申购机构
     *
     * @param fullChineseNameOfSubscribe 申购机构
     */
    public void setFullChineseNameOfSubscribe(String fullChineseNameOfSubscribe) {
        this.fullChineseNameOfSubscribe = fullChineseNameOfSubscribe == null ? null : fullChineseNameOfSubscribe.trim();
    }

    /**
     * 申购机构 21 位码
     *
     * @return SUBSCRIBE_INSTITUTION_ID 申购机构 21 位码
     */
    public String getSubscribeInstitutionId() {
        return subscribeInstitutionId;
    }

    /**
     * 申购机构 21 位码
     *
     * @param subscribeInstitutionId 申购机构 21 位码
     */
    public void setSubscribeInstitutionId(String subscribeInstitutionId) {
        this.subscribeInstitutionId = subscribeInstitutionId == null ? null : subscribeInstitutionId.trim();
    }

    /**
     * 认购量（亿元）
     *
     * @return SUBSCRIPTION_AMOUNT 认购量（亿元）
     */
    public BigDecimal getSubscriptionAmount() {
        return subscriptionAmount;
    }

    /**
     * 认购量（亿元）
     *
     * @param subscriptionAmount 认购量（亿元）
     */
    public void setSubscriptionAmount(BigDecimal subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
    }

    /**
     * 应缴纳金额（元）
     *
     * @return ACTUAL_FEE 应缴纳金额（元）
     */
    public BigDecimal getActualFee() {
        return actualFee;
    }

    /**
     * 应缴纳金额（元）
     *
     * @param actualFee 应缴纳金额（元）
     */
    public void setActualFee(BigDecimal actualFee) {
        this.actualFee = actualFee;
    }

    /**
     * 认购时间 格式：yyyy-mm-dd HH:MM:SS
     *
     * @return SUBSCRIPTION_TIME 认购时间 格式：yyyy-mm-dd HH:MM:SS
     */
    public String getSubscriptionTime() {
        return subscriptionTime;
    }

    /**
     * 认购时间 格式：yyyy-mm-dd HH:MM:SS
     *
     * @param subscriptionTime 认购时间 格式：yyyy-mm-dd HH:MM:SS
     */
    public void setSubscriptionTime(String subscriptionTime) {
        this.subscriptionTime = subscriptionTime == null ? null : subscriptionTime.trim();
    }

    /**
     * 提交用户
     *
     * @return SUBMIT_USER 提交用户
     */
    public String getSubmitUser() {
        return submitUser;
    }

    /**
     * 提交用户
     *
     * @param submitUser 提交用户
     */
    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser == null ? null : submitUser.trim();
    }

    /**
     * 币种
     *
     * @return CURRENCY 币种
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * 币种
     *
     * @param currency 币种
     */
    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    /**
     * 报价编号(内部)
     *
     * @return TRD_ID 报价编号(内部)
     */
    public String getTrdId() {
        return trdId;
    }

    /**
     * 报价编号(内部)
     *
     * @param trdId 报价编号(内部)
     */
    public void setTrdId(String trdId) {
        this.trdId = trdId == null ? null : trdId.trim();
    }
}