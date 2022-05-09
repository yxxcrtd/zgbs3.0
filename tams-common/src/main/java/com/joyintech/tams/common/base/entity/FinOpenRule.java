package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.util.Date;

public class FinOpenRule extends FinOpenRuleKey {
    /**
     * 开放模式，定期开放、高频开放、周期开放
     */
    private String openMode;

    /**
     * 开放天数，每次开放几天
     */
    private Long openDays;

    /**
     * 开放周期，数字
     */
    private Long openCycle;

    /**
     * 开放周期单位，天、周、月等
     */
    private String openCycleUnit;

    /**
     * 日期类型，第几工作日、第几自然日、周几、几号等
     */
    private String dayType;

    /**
     * 日期值，可以有多个数值，逗号分隔
     */
    private String dayValue;

    /**
     * 推算方法，起息日向后、首次开放日等
     */
    private String calcMethod;

    /**
     * 首次开放日
     */
    private Date firstOpenDate;

    /**
     * 营业日准则
     */
    private String tradedayRule;

    /**
     * 节假日是否开放
     */
    private String hdayOpenFlag;

    /**
     * 推算规则，开放周期开始日、开放周期结束日、开放周期起息日
     */
    private String calcRule;

    /**
     * 净值日规则
     */
    private String cpriceRule;

    /**
     * 开放天数类型
     */
    private String openDaysType;

    /**
     * 申购确认速度
     */
    private Long purSpeed;

    /**
     * 赎回确认速度
     */
    private Long redSpeed;

    /**
     * 取得 开放模式，定期开放、高频开放、周期开放
     * @return 开放模式，定期开放、高频开放、周期开放
     */
    public String getOpenMode() {
        return openMode;
    }

    /**
     * 设置 开放模式，定期开放、高频开放、周期开放
     * @param openMode 开放模式，定期开放、高频开放、周期开放
     */
    public void setOpenMode(String openMode) {
        this.openMode = openMode == null ? null : openMode.trim();
    }

    /**
     * 取得 开放天数，每次开放几天
     * @return 开放天数，每次开放几天
     */
    public Long getOpenDays() {
        return openDays;
    }

    /**
     * 设置 开放天数，每次开放几天
     * @param openDays 开放天数，每次开放几天
     */
    public void setOpenDays(Long openDays) {
        this.openDays = openDays;
    }

    /**
     * 取得 开放周期，数字
     * @return 开放周期，数字
     */
    public Long getOpenCycle() {
        return openCycle;
    }

    /**
     * 设置 开放周期，数字
     * @param openCycle 开放周期，数字
     */
    public void setOpenCycle(Long openCycle) {
        this.openCycle = openCycle;
    }

    /**
     * 取得 开放周期单位，天、周、月等
     * @return 开放周期单位，天、周、月等
     */
    public String getOpenCycleUnit() {
        return openCycleUnit;
    }

    /**
     * 设置 开放周期单位，天、周、月等
     * @param openCycleUnit 开放周期单位，天、周、月等
     */
    public void setOpenCycleUnit(String openCycleUnit) {
        this.openCycleUnit = openCycleUnit == null ? null : openCycleUnit.trim();
    }

    /**
     * 取得 日期类型，第几工作日、第几自然日、周几、几号等
     * @return 日期类型，第几工作日、第几自然日、周几、几号等
     */
    public String getDayType() {
        return dayType;
    }

    /**
     * 设置 日期类型，第几工作日、第几自然日、周几、几号等
     * @param dayType 日期类型，第几工作日、第几自然日、周几、几号等
     */
    public void setDayType(String dayType) {
        this.dayType = dayType == null ? null : dayType.trim();
    }

    /**
     * 取得 日期值，可以有多个数值，逗号分隔
     * @return 日期值，可以有多个数值，逗号分隔
     */
    public String getDayValue() {
        return dayValue;
    }

    /**
     * 设置 日期值，可以有多个数值，逗号分隔
     * @param dayValue 日期值，可以有多个数值，逗号分隔
     */
    public void setDayValue(String dayValue) {
        this.dayValue = dayValue == null ? null : dayValue.trim();
    }

    /**
     * 取得 推算方法，起息日向后、首次开放日等
     * @return 推算方法，起息日向后、首次开放日等
     */
    public String getCalcMethod() {
        return calcMethod;
    }

    /**
     * 设置 推算方法，起息日向后、首次开放日等
     * @param calcMethod 推算方法，起息日向后、首次开放日等
     */
    public void setCalcMethod(String calcMethod) {
        this.calcMethod = calcMethod == null ? null : calcMethod.trim();
    }

    /**
     * 取得 首次开放日
     * @return 首次开放日
     */
    public Date getFirstOpenDate() {
        return firstOpenDate;
    }

    /**
     * 设置 首次开放日
     * @param firstOpenDate 首次开放日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setFirstOpenDate(Date firstOpenDate) {
        this.firstOpenDate = firstOpenDate;
    }

    /**
     * 取得 营业日准则
     * @return 营业日准则
     */
    public String getTradedayRule() {
        return tradedayRule;
    }

    /**
     * 设置 营业日准则
     * @param tradedayRule 营业日准则
     */
    public void setTradedayRule(String tradedayRule) {
        this.tradedayRule = tradedayRule == null ? null : tradedayRule.trim();
    }

    /**
     * 取得 节假日是否开放
     * @return 节假日是否开放
     */
    public String getHdayOpenFlag() {
        return hdayOpenFlag;
    }

    /**
     * 设置 节假日是否开放
     * @param hdayOpenFlag 节假日是否开放
     */
    public void setHdayOpenFlag(String hdayOpenFlag) {
        this.hdayOpenFlag = hdayOpenFlag == null ? null : hdayOpenFlag.trim();
    }

    /**
     * 取得 推算规则，开放周期开始日、开放周期结束日、开放周期起息日
     * @return 推算规则，开放周期开始日、开放周期结束日、开放周期起息日
     */
    public String getCalcRule() {
        return calcRule;
    }

    /**
     * 设置 推算规则，开放周期开始日、开放周期结束日、开放周期起息日
     * @param calcRule 推算规则，开放周期开始日、开放周期结束日、开放周期起息日
     */
    public void setCalcRule(String calcRule) {
        this.calcRule = calcRule == null ? null : calcRule.trim();
    }

    /**
     * 取得 净值日规则
     * @return 净值日规则
     */
    public String getCpriceRule() {
        return cpriceRule;
    }

    /**
     * 设置 净值日规则
     * @param cpriceRule 净值日规则
     */
    public void setCpriceRule(String cpriceRule) {
        this.cpriceRule = cpriceRule == null ? null : cpriceRule.trim();
    }

    /**
     * 取得 开放天数类型
     * @return 开放天数类型
     */
    public String getOpenDaysType() {
        return openDaysType;
    }

    /**
     * 设置 开放天数类型
     * @param openDaysType 开放天数类型
     */
    public void setOpenDaysType(String openDaysType) {
        this.openDaysType = openDaysType == null ? null : openDaysType.trim();
    }

    /**
     * 取得 申购确认速度
     * @return 申购确认速度
     */
    public Long getPurSpeed() {
        return purSpeed;
    }

    /**
     * 设置 申购确认速度
     * @param purSpeed 申购确认速度
     */
    public void setPurSpeed(Long purSpeed) {
        this.purSpeed = purSpeed;
    }

    /**
     * 取得 赎回确认速度
     * @return 赎回确认速度
     */
    public Long getRedSpeed() {
        return redSpeed;
    }

    /**
     * 设置 赎回确认速度
     * @param redSpeed 赎回确认速度
     */
    public void setRedSpeed(Long redSpeed) {
        this.redSpeed = redSpeed;
    }
}