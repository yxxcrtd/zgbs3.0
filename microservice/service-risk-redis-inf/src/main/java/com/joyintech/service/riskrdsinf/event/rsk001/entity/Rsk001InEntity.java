/*
 * 文件名：Rsk001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年8月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.riskrdsinf.event.rsk001.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.RskPreOccupyInfo;

/**
 * 风险试算接口-输入
 *
 * @author zhangrui1
 * @version 1.0 2019年8月13日
 * @see Rsk001InEntity
 * @since 1.0
 */
public class Rsk001InEntity extends RskPreOccupyInfo {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -5083171952675595091L;

    /**
     * 组合代码
     */
    private String portfolioCode;

    /**
     * 市场代码
     */
    private String marketCode;

    /**
     * 交易场所
     */
    private String tradePlace;

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
     * 取得 组合代码
     *
     * @return 组合代码
     */
    @NotEmpty
    @Size(max = 32)
    public String getPortfolioCode() {
        return portfolioCode;
    }

    /**
     * 设置 组合代码
     *
     * @param portfolioCode 组合代码
     */
    public void setPortfolioCode(String portfolioCode) {
        this.portfolioCode = portfolioCode;
    }

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

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty
    @Size(max = 50)
    public String getEventType() {
        return super.getEventType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty
    @Size(max = 50)
    public String getPs() {
        return super.getPs();
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    // @NotNull
    // public Date getOccupyDate() {
    // return super.getOccupyDate();
    // }

    /**
     * {@inheritDoc}
     */
    public void setEventDate(String eventDate) {
        super.setEventDate(DateUtil.parse(eventDate));
    }

    /**
     * {@inheritDoc}
     */
    public void setOccupyDate(String occupyDate) {
        super.setOccupyDate(DateUtil.parse(occupyDate));
    }

    /**
     * 达到预警线是否需要人工确认
     */
    private String isWarnNeedConfirm;

    /**
     * 达到审批线是否需要人工确认
     */
    private String isApprNeedConfirm;

    /**
     * 获取 达到预警线是否需要人工确认
     *
     * @return 达到预警线是否需要人工确认.
     */
    @NotEmpty
    public String getIsWarnNeedConfirm() {
        return isWarnNeedConfirm;
    }

    /**
     * 设置 达到预警线是否需要人工确认
     *
     * @param isWarnNeedConfirm 达到预警线是否需要人工确认
     */
    public void setIsWarnNeedConfirm(String isWarnNeedConfirm) {
        this.isWarnNeedConfirm = isWarnNeedConfirm;
    }

    /**
     * 获取 达到审批线是否需要人工确认
     *
     * @return 达到审批线是否需要人工确认.
     */
    @NotEmpty
    public String getIsApprNeedConfirm() {
        return isApprNeedConfirm;
    }

    /**
     * 设置 达到审批线是否需要人工确认
     *
     * @param isApprNeedConfirm 达到审批线是否需要人工确认
     */
    public void setIsApprNeedConfirm(String isApprNeedConfirm) {
        this.isApprNeedConfirm = isApprNeedConfirm;
    }

	public String getMarketCode() {
		return marketCode;
	}

	public void setMarketCode(String marketCode) {
		this.marketCode = marketCode;
	}

	public String getTradePlace() {
		return tradePlace;
	}

	public void setTradePlace(String tradePlace) {
		this.tradePlace = tradePlace;
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
