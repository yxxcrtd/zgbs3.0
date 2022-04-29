package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

public class FinCashInfo extends BaseEntity {
    /**
     * 现金流代码，MD5(业务代码+(char)256+业务类型+(char)256+分支序号+(char)256+现金流二级类型)
     */
    private String cashId;

    /**
     * 现金流一级类型，本金、利息、费用、税额
     */
    private String cashType1;

    /**
     * 现金流二级类型，本金、利息、托管费、固定管理费、浮动管理费、税额、通道托管费、通道管理费等
     */
    private String cashType2;

    /**
     * 币种，本金、利息币种可能不一致。
     */
    private String ccy;

    /**
     * 业务代码，可以是金融产品代码、资金账号代码等。
     */
    private String finprodId;

    /**
     * 业务类型，金融产品、资金账号等。
     */
    private String finprodType;

    /**
     * 分支序号
     */
    private Long branch;

    /**
     * 费用品种代码
     */
    private String feeId;

    /**
     * 通道金融产品代码，如果是资产挂通道费时存储
     */
    private String chlFinprodId;

    /**
     * 通道层级，如果是资产挂通道费时存储
     */
    private Long chlLevel;

    /**
     * 是否参与计算
     */
    private String isCalc;

    /**
     * 收取方式
     */
    private String collectType;

    /**
     * 利率类型:不入表，只为了判断利率互换的现金流
     */
    private String rateType;

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    /**
     * 取得 现金流代码，MD5(业务代码+(char)256+业务类型+(char)256+分支序号+(char)256+现金流二级类型)
     * @return 现金流代码，MD5(业务代码+(char)256+业务类型+(char)256+分支序号+(char)256+现金流二级类型)
     */
    public String getCashId() {
        return cashId;
    }

    /**
     * 设置 现金流代码，MD5(业务代码+(char)256+业务类型+(char)256+分支序号+(char)256+现金流二级类型)
     * @param cashId 现金流代码，MD5(业务代码+(char)256+业务类型+(char)256+分支序号+(char)256+现金流二级类型)
     */
    public void setCashId(String cashId) {
        this.cashId = cashId == null ? null : cashId.trim();
    }

    /**
     * 取得 现金流一级类型，本金、利息、费用、税额
     * @return 现金流一级类型，本金、利息、费用、税额
     */
    public String getCashType1() {
        return cashType1;
    }

    /**
     * 设置 现金流一级类型，本金、利息、费用、税额
     * @param cashType1 现金流一级类型，本金、利息、费用、税额
     */
    public void setCashType1(String cashType1) {
        this.cashType1 = cashType1 == null ? null : cashType1.trim();
    }

    /**
     * 取得 现金流二级类型，本金、利息、托管费、固定管理费、浮动管理费、税额、通道托管费、通道管理费等
     * @return 现金流二级类型，本金、利息、托管费、固定管理费、浮动管理费、税额、通道托管费、通道管理费等
     */
    public String getCashType2() {
        return cashType2;
    }

    /**
     * 设置 现金流二级类型，本金、利息、托管费、固定管理费、浮动管理费、税额、通道托管费、通道管理费等
     * @param cashType2 现金流二级类型，本金、利息、托管费、固定管理费、浮动管理费、税额、通道托管费、通道管理费等
     */
    public void setCashType2(String cashType2) {
        this.cashType2 = cashType2 == null ? null : cashType2.trim();
    }

    /**
     * 取得 币种，本金、利息币种可能不一致。
     * @return 币种，本金、利息币种可能不一致。
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 币种，本金、利息币种可能不一致。
     * @param ccy 币种，本金、利息币种可能不一致。
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 业务代码，可以是金融产品代码、资金账号代码等。
     * @return 业务代码，可以是金融产品代码、资金账号代码等。
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 业务代码，可以是金融产品代码、资金账号代码等。
     * @param finprodId 业务代码，可以是金融产品代码、资金账号代码等。
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 业务类型，金融产品、资金账号等。
     * @return 业务类型，金融产品、资金账号等。
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 业务类型，金融产品、资金账号等。
     * @param finprodType 业务类型，金融产品、资金账号等。
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType == null ? null : finprodType.trim();
    }

    /**
     * 取得 分支序号
     * @return 分支序号
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 设置 分支序号
     * @param branch 分支序号
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    /**
     * 取得 费用品种代码
     * @return 费用品种代码
     */
    public String getFeeId() {
        return feeId;
    }

    /**
     * 设置 费用品种代码
     * @param feeId 费用品种代码
     */
    public void setFeeId(String feeId) {
        this.feeId = feeId == null ? null : feeId.trim();
    }

    /**
     * 取得 通道金融产品代码，如果是资产挂通道费时存储
     * @return 通道金融产品代码，如果是资产挂通道费时存储
     */
    public String getChlFinprodId() {
        return chlFinprodId;
    }

    /**
     * 设置 通道金融产品代码，如果是资产挂通道费时存储
     * @param chlFinprodId 通道金融产品代码，如果是资产挂通道费时存储
     */
    public void setChlFinprodId(String chlFinprodId) {
        this.chlFinprodId = chlFinprodId == null ? null : chlFinprodId.trim();
    }

    /**
     * 取得 通道层级，如果是资产挂通道费时存储
     * @return 通道层级，如果是资产挂通道费时存储
     */
    public Long getChlLevel() {
        return chlLevel;
    }

    /**
     * 设置 通道层级，如果是资产挂通道费时存储
     * @param chlLevel 通道层级，如果是资产挂通道费时存储
     */
    public void setChlLevel(Long chlLevel) {
        this.chlLevel = chlLevel;
    }

    /**
     * 取得 是否参与计算
     * @return 是否参与计算
     */
    public String getIsCalc() {
        return isCalc;
    }

    /**
     * 设置 是否参与计算
     * @param isCalc 是否参与计算
     */
    public void setIsCalc(String isCalc) {
        this.isCalc = isCalc == null ? null : isCalc.trim();
    }

    /**
     * 取得 收取方式
     * @return 收取方式
     */
    public String getCollectType() {
        return collectType;
    }

    /**
     * 设置 收取方式
     * @param collectType 收取方式
     */
    public void setCollectType(String collectType) {
        this.collectType = collectType == null ? null : collectType.trim();
    }
}