/*
 * 文件名：AssetCommonInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.asset.dto;

import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinRateInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  资产信息共通Dto
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月16日
 * @see AssetCommonInfoDto
 * @since 1.0
 */
public class AssetCommonInfoDto extends FinProduct{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 现金流ID
     */
    private String cashId;

    /**
     * 计息基础
     */
    private String basis;
    
    /**
     * 利率类型
     */
    private String intType;
    
    /**
     * 票面利率
     */
    private BigDecimal rate;
    
    /**
     * 币种
     */
    private String ccy;
    
    
    /**
     * 本金
     */
    private BigDecimal amount;
    
    /**
     * 交易日历
     */
    private String calendarId;
    
    /**
     * 利息分配方式  
     */
    private String intpayRule;
    
    /**
     * 付息计划
     */
    private List<FinCashPlanDto> invList;
    
    /**
     * 还本计划
     */
    private List<FinCashPlanDto> invRepayList;
    
    /**
     * 利率信息
     */
    private FinRateInfo finRateInfo;

    /**
     * 平台日
     */
    private Date sysDate;

    /**
     * 获取 basis
     * @return basis.
     */
    public String getBasis() {
        return basis;
    }

    
    /**
     * 设置 basis
     * @param basis basis
     */
    public void setBasis(String basis) {
        this.basis = basis;
    }
    
    /**
     * 获取 intType
     * @return intType.
     */
    public String getIntType() {
        return intType;
    }
    
    /**
     * 设置 intType
     * @param intType intType
     */
    public void setIntType(String intType) {
        this.intType = intType;
    }

    
    /**
     * 获取 rate
     * @return rate.
     */
    public BigDecimal getRate() {
        return rate;
    }

    
    /**
     * 设置 rate
     * @param rate rate
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    
    /**
     * 获取 ccy
     * @return ccy.
     */
    public String getCcy() {
        return ccy;
    }

    
    /**
     * 设置 ccy
     * @param ccy ccy
     */
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    
    /**
     * 获取 amount
     * @return amount.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    
    /**
     * 设置 amount
     * @param amount amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    
    /**
     * 获取 calendarId
     * @return calendarId.
     */
    public String getCalendarId() {
        return calendarId;
    }

    
    /**
     * 设置 calendarId
     * @param calendarId calendarId
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    
    /**
     * 获取 intpayRule
     * @return intpayRule.
     */
    public String getIntpayRule() {
        return intpayRule;
    }
    
    
    
    /**
     * 获取 cashId
     * @return cashId.
     */
    public String getCashId() {
        return cashId;
    }


    
    /**
     * 设置 cashId
     * @param cashId cashId
     */
    public void setCashId(String cashId) {
        this.cashId = cashId;
    }


    /**
     * 设置 intpayRule
     * @param intpayRule intpayRule
     */
    public void setIntpayRule(String intpayRule) {
        this.intpayRule = intpayRule;
    }

    
    


    
    
    /**
     * 获取 invList
     * @return invList.
     */
    public List<FinCashPlanDto> getInvList() {
        return invList;
    }


    
    /**
     * 设置 invList
     * @param invList invList
     */
    public void setInvList(List<FinCashPlanDto> invList) {
        this.invList = invList;
    }


    
    /**
     * 获取 invRepayList
     * @return invRepayList.
     */
    public List<FinCashPlanDto> getInvRepayList() {
        return invRepayList;
    }


    
    /**
     * 设置 invRepayList
     * @param invRepayList invRepayList
     */
    public void setInvRepayList(List<FinCashPlanDto> invRepayList) {
        this.invRepayList = invRepayList;
    }


    
    /**
     * 获取 finRateInfo
     * @return finRateInfo.
     */
    public FinRateInfo getFinRateInfo() {
        return finRateInfo;
    }


    
    /**
     * 设置 finRateInfo
     * @param finRateInfo finRateInfo
     */
    public void setFinRateInfo(FinRateInfo finRateInfo) {
        this.finRateInfo = finRateInfo;
    }


    
    /**
     * 获取 sysDate
     * @return sysDate.
     */
    public Date getSysDate() {
        return sysDate;
    }


    
    /**
     * 设置 sysDate
     * @param sysDate sysDate
     */
    public void setSysDate(Date sysDate) {
        this.sysDate = sysDate;
    }
    
    
    
}
