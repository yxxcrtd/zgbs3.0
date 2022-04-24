/*
 * 文件名：FinCashCalcRuleDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.asset.dto;

import com.joyintech.tams.common.base.entity.FinCashCalcRule;

import java.util.Date;

/**
 *  现金流计算规则
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月21日
 * @see FinCashCalcRuleDto
 * @since 1.0
 */
public class FinCashCalcRuleDto extends FinCashCalcRule{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    /**
     * 推算方式
     */
    private String calcRule;

    /**
     * 首次付息日
     */
    private Date firstPayDate;

    /**
     * 获取 calcRule
     *
     * @return calcRule
     */
    public String getCalcRule() {
        return calcRule;
    }

    /**
     * 设置 calcRule
     *
     * @param calcRule calcRule
     */
    public void setCalcRule(String calcRule) {
        this.calcRule = calcRule;
    }

    /**
     * 获取 firstPayDate
     *
     * @return firstPayDate
     */
    public Date getFirstPayDate() {
        return firstPayDate;
    }

    /**
     * 设置 firstPayDate
     *
     * @param firstPayDate firstPayDate
     */
    public void setFirstPayDate(Date firstPayDate) {
        this.firstPayDate = firstPayDate;
    }
}
