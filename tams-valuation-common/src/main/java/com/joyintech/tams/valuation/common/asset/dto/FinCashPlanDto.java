/*
 * 文件名：FinCashPlanDto.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：zhangnian 修改时间：2018年10月17日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.asset.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.FinCashPlan;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 现金流计划DTO 〈功能详细描述〉
 * 
 * @author zhangnian
 * @version 1.0 2018年10月17日
 * @see FinCashPlanDto
 * @since 1.0
 */
public class FinCashPlanDto extends FinCashPlan {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    private BigDecimal yield;

    /**
     * 还本金额
     */
    private BigDecimal repayAmt;
    
    /**
     * 是否可编辑
     */
    private String updateFlg;
    
    /**
     * 是否重新计算
     */
    private boolean isReset;

    /**
     * 是否已支付
     */
    private String isPayed;
    
    /**
     * 当前本金
     */
    private BigDecimal curPrin;
    
    /**
     * 最大交易日
     */
    private Date maxTradeDate;

    /**
     * 付息计划
     */
    private List<? extends FinCashPlanDetailDto> lis;

    /**
     * 还本计划
     */
    private List<? extends FinCashPlanDetailDto> lirs;

    private String cashType1;

    /**
     * 获取 cashType1
     *
     * @return cashType1
     */
    public String getCashType1() {
        return cashType1;
    }

    /**
     * 设置 cashType1
     *
     * @param cashType1 cashType1
     */
    public void setCashType1(String cashType1) {
        this.cashType1 = cashType1;
    }

    /**
     * 当期最新利率
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal newRate;
    
    /**
     * old mdate
     */
    private Date oldMdate;
    
    /**
     * old vdate
     */
    private Date oldVdate;

    /**
     * 是否还本不带息
     */
    private String isPayIntFlag;
    /**
     * 获取 repayAmt
     * 
     * @return repayAmt.
     */
    public BigDecimal getRepayAmt() {
        return repayAmt;
    }

    /**
     * 设置 repayAmt
     * 
     * @param repayAmt repayAmt
     */
    public void setRepayAmt(BigDecimal repayAmt) {
        this.repayAmt = repayAmt;
    }

    /**
     * 获取 newRate
     * 
     * @return newRate.
     */
    public BigDecimal getNewRate() {
        return newRate;
    }

    /**
     * 设置 newRate
     * 
     * @param newRate newRate
     */
    public void setNewRate(BigDecimal newRate) {
        this.newRate = newRate;
    }

    /**
     * 获取 lis
     * 
     * @return lis.
     */
    public List<? extends FinCashPlanDetailDto> getLis() {
        return lis;
    }

    /**
     * 设置 lis
     * 
     * @param lis lis
     */
    public void setLis(List<? extends FinCashPlanDetailDto> lis) {
        this.lis = lis;
    }

    /**
     * 获取 lirs
     * 
     * @return lirs.
     */
    public List<? extends FinCashPlanDetailDto> getLirs() {
        return lirs;
    }

    /**
     * 设置 lirs
     * 
     * @param lirs lirs
     */
    public void setLirs(List<? extends FinCashPlanDetailDto> lirs) {
        this.lirs = lirs;
    }

    /**
     * 获取 yield
     * 
     * @return yield.
     */
    public BigDecimal getYield() {
        return yield;
    }

    /**
     * 设置 yield
     * 
     * @param yield yield
     */
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    /**
     * 获取 isPayed
     *
     * @return isPayed
     */
    public String getIsPayed() {
        return isPayed;
    }

    /**
     * 设置 isPayed
     *
     * @param isPayed isPayed
     */
    public void setIsPayed(String isPayed) {
        this.isPayed = isPayed;
    }

    
    /**
     * 获取 isReset
     * @return isReset.
     */
    public boolean isReset() {
        return isReset;
    }

    
    /**
     * 设置 isReset
     * @param isReset isReset
     */
    public void setReset(boolean isReset) {
        this.isReset = isReset;
    }

    
    /**
     * 获取 curPrin
     * @return curPrin.
     */
    public BigDecimal getCurPrin() {
        return curPrin;
    }

    
    /**
     * 设置 curPrin
     * @param curPrin curPrin
     */
    public void setCurPrin(BigDecimal curPrin) {
        this.curPrin = curPrin;
    }

    
    /**
     * 获取 updateFlg
     * @return updateFlg.
     */
    public String getUpdateFlg() {
        return updateFlg;
    }

    
    /**
     * 设置 updateFlg
     * @param updateFlg updateFlg
     */
    public void setUpdateFlg(String updateFlg) {
        this.updateFlg = updateFlg;
    }

    
    /**
     * 获取 maxTradeDate
     * @return maxTradeDate.
     */
    public Date getMaxTradeDate() {
        return maxTradeDate;
    }

    
    /**
     * 设置 maxTradeDate
     * @param maxTradeDate maxTradeDate
     */
    public void setMaxTradeDate(Date maxTradeDate) {
        this.maxTradeDate = maxTradeDate;
    }

    
    /**
     * 获取 oldMdate
     * @return oldMdate.
     */
    public Date getOldMdate() {
        return oldMdate;
    }

    
    /**
     * 设置 oldMdate
     * @param oldMdate oldMdate
     */
    public void setOldMdate(Date oldMdate) {
        this.oldMdate = oldMdate;
    }

    
    /**
     * 获取 oldVdate
     * @return oldVdate.
     */
    public Date getOldVdate() {
        return oldVdate;
    }

    
    /**
     * 设置 oldVdate
     * @param oldVdate oldVdate
     */
    public void setOldVdate(Date oldVdate) {
        this.oldVdate = oldVdate;
    }

    /**
     * 获取 isPayIntFlag
     *
     * @return isPayIntFlag
     */
    @Override
    public String getIsPayIntFlag() {
        return isPayIntFlag;
    }

    /**
     * 设置 isPayIntFlag
     *
     * @param isPayIntFlag isPayIntFlag
     */
    @Override
    public void setIsPayIntFlag(String isPayIntFlag) {
        this.isPayIntFlag = isPayIntFlag;
    }

}
