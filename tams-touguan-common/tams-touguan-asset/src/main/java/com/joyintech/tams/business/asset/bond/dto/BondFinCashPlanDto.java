/*
 * 文件名：FinCashPlanDto.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：zhangnian 修改时间：2018年10月17日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.business.asset.bond.dto;

import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDetailDto;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 现金流计划DTO 〈功能详细描述〉
 * 
 * @author wangyang1
 * @version 1.0 2022年4月16日
 * @see BondFinCashPlanDto
 * @since 1.0
 */
public class BondFinCashPlanDto extends FinCashPlan {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 调整后的计算开始日
     */
    @ApiModelProperty(name="vdate",value="计息开始日(必填)")
    private Date vdate;

    /**
     * 调整后的计算结束日
     */
    @ApiModelProperty(name="mdate",value="计息截止日(必填)")
    private Date mdate;

    /**
     * 调整后的计划支付日
     */
    @ApiModelProperty(name="payDate",value="支付日(必填)")
    private Date payDate;

    /**
     * 区间净收益率
     */
    @ApiModelProperty(name="rangeYield",value="区间利率(%)(必填)")
    private BigDecimal rangeYield;
    /**
     * 计息本金
     */
    @ApiModelProperty(name="intPrinAmt",value="计息本金(元)(必填)")
    private BigDecimal intPrinAmt;
    /**
     * 应计利息
     */
    @ApiModelProperty(name="intAmt",value="应计利息(元)(必填)")
    private BigDecimal intAmt;

    /**
     * 计息基础
     */
    @ApiModelProperty(name="basis",value="计息基础(必填) ")
    private String basis;

    @ApiModelProperty(name="prinAmt",value="计划支付本金(必填)、还本面值")
    private BigDecimal prinAmt;

    @ApiModelProperty(name="cashAmt",value="计划支付现金流(必填)、本息和")
    private BigDecimal cashAmt;

    /**
     * 还本计划
     */
    @ApiModelProperty(name="lirs",value="还本计划")
    private List<BondFinCashPlanDto> lirs;

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public BigDecimal getRangeYield() {
        return rangeYield;
    }

    public void setRangeYield(BigDecimal rangeYield) {
        this.rangeYield = rangeYield;
    }

    public BigDecimal getIntPrinAmt() {
        return intPrinAmt;
    }

    public void setIntPrinAmt(BigDecimal intPrinAmt) {
        this.intPrinAmt = intPrinAmt;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    private Date maxTradeDate;

    private Date oldMdate;

    public Date getMaxTradeDate() {
        return maxTradeDate;
    }

    public void setMaxTradeDate(Date maxTradeDate) {
        this.maxTradeDate = maxTradeDate;
    }

    public Date getOldMdate() {
        return oldMdate;
    }

    public void setOldMdate(Date oldMdate) {
        this.oldMdate = oldMdate;
    }

    @Override
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    @Override
    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    @Override
    public BigDecimal getCashAmt() {
        return cashAmt;
    }

    @Override
    public void setCashAmt(BigDecimal cashAmt) {
        this.cashAmt = cashAmt;
    }

    public List<BondFinCashPlanDto> getLirs() {
        return lirs;
    }

    public void setLirs(List<BondFinCashPlanDto> lirs) {
        this.lirs = lirs;
    }
}
