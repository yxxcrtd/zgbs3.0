package com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.validator.TrdStockTradeValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TrdStockTradeAddDto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 比例
     */
    private BigDecimal ratio;

    /**
     * 锁定数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lockedNum;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 预计上市流通日
     */
    private Date circulateDate;

    /**
     * 期限(天)
     */
    private BigDecimal lockedDays;

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    public BigDecimal getLockedNum() {
        return lockedNum;
    }

    public void setLockedNum(BigDecimal lockedNum) {
        this.lockedNum = lockedNum;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getLockedDays() {
        return lockedDays;
    }

    public void setLockedDays(BigDecimal lockedDays) {
        this.lockedDays = lockedDays;
    }
}
