/*
 * 文件名：EtrBondBiddingDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.background.component.order.exchange.business.dto;

import com.joyintech.background.component.order.exchange.common.constant.DictCodeList;
import com.joyintech.background.component.order.exchange.common.dto.EtrSinglePortfolioDto;

import java.math.BigDecimal;

/**
 * 场内现券指令-通用质押式回购
 */
public class EtrSecRepoRepoDto extends EtrSinglePortfolioDto {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1489935673702361141L;

    public EtrSecRepoRepoDto() {
        super(DictCodeList.指令业务类型_质押回购竞价.getValue());
    }


    /**
     * 交易方式
     */
    private String dealMode;

    public String getDealMode() {
        return dealMode;
    }

    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }
}

