/*
 * 文件名：CommissionCalcDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年7月14日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */
 
package com.joyintech.tams.settle.common.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 佣金计算输出DTO
 * @description 
 * @author 王波
 * @version 1.0 2020年7月14日
 * @see CommissionCalcDto
 * @since 1.0
 */
public class CommissionCalcDto {
    // 佣金金额
    private BigDecimal commisionAmount;

    // 净佣金
    private BigDecimal netCommisionAmount;

    // 佣金计算过程明细列表
    private List<CommissionCalcDetail> commissionCalcDetailList;

    // 佣金明细列表
    private List<CommisionDetail> commisionDetailList;

    public BigDecimal getCommisionAmount() {
        return commisionAmount;
    }

    public void setCommisionAmount(BigDecimal commisionAmount) {
        this.commisionAmount = commisionAmount;
    }

    public BigDecimal getNetCommisionAmount() {
        return netCommisionAmount;
    }

    public void setNetCommisionAmount(BigDecimal netCommisionAmount) {
        this.netCommisionAmount = netCommisionAmount;
    }

    public List<CommissionCalcDetail> getCommissionCalcDetailList() {
        return commissionCalcDetailList;
    }

    public void setCommissionCalcDetailList(List<CommissionCalcDetail> commissionCalcDetailList) {
        this.commissionCalcDetailList = commissionCalcDetailList;
    }

    public List<CommisionDetail> getCommisionDetailList() {
        return commisionDetailList;
    }

    public void setCommisionDetailList(List<CommisionDetail> commisionDetailList) {
        this.commisionDetailList = commisionDetailList;
    }
}
