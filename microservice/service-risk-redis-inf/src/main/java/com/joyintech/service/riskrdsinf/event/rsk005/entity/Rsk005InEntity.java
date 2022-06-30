/*
 * 文件名：Rsk005InEntity.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年8月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.riskrdsinf.event.rsk005.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.RskPreOccupyInfo;

/**
 * RSK005接口-输入
 *
 * @author zhangrui1
 * @version 1.0 2019年8月27日
 * @see Rsk005InEntity
 * @since 1.0
 */
public class Rsk005InEntity extends RskPreOccupyInfo {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -96667748860193249L;

    /**
     * 金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal amountOld;

    /**
     * 数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeNumOld;

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Date getEventDate() {
        return super.getEventDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty
    @Size(max = 128)
    public String getEventId() {
        return super.getEventId();
    }

	public BigDecimal getAmountOld() {
		return amountOld;
	}

	public void setAmountOld(BigDecimal amountOld) {
		this.amountOld = amountOld;
	}

	public BigDecimal getTradeNumOld() {
		return tradeNumOld;
	}

	public void setTradeNumOld(BigDecimal tradeNumOld) {
		this.tradeNumOld = tradeNumOld;
	}
}
