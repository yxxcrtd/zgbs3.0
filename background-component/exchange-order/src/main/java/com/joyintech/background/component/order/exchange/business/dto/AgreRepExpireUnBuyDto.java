/*
 * 文件名：EtrStockRightDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.background.component.order.exchange.business.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.joyintech.background.component.common.pack.common.validation.annotation.Length;
import com.joyintech.background.component.order.exchange.common.constant.DictCodeList;
import com.joyintech.background.component.order.exchange.common.dto.EtrSinglePortfolioDto;


/**
 * 配股Dto
 * 
 * @author lihuadong
 * @version 1.0 2020年7月8日
 * @see AgreRepExpireUnBuyDto
 * @since 1.0
 */
public class AgreRepExpireUnBuyDto extends EtrSinglePortfolioDto {



	/**
	 * 
	 */
	private static final long serialVersionUID = -4887849005580621438L;

	/**
     * Constructor
     */
    public AgreRepExpireUnBuyDto() {
        super(DictCodeList.指令业务类型_协议回购到期续做指令.getValue());
    }
    /**
     * 首次交收日
     */
    private Date vpayDate;

	/**
     * 到期交收日
     */
    private Date mpayDate;
    /**
     * 到期应计利息
     */
    private BigDecimal mIntAmt;
    /**
     * 到期结算金额
     */
    private BigDecimal mTradeAmt;
    /**
     * 占款天数
     */
    private BigDecimal capDays;
    /**
     * 成交编号/初始交易编号
     */
    private String tradeId;
    /**
     * 请求号
     */
    private String requestId;

    /**
     * 质押面值
     */
    private BigDecimal pledgeFaceValue;
    
    /**
     * 质押数量
     */
    private BigDecimal pledgeNumber;
    /**
     * 质押率
     */
    private BigDecimal pledgeRatio;
    /**
     * 质押金额
     */
    private BigDecimal pledgeAmt;
    /**
     * 期限天数，到期日-起息日
     */
    private Long termDays;
    
    /**
     * 交易对手
     */
    private String counterId;
    
    /**
     * 对手方交易员
     */
    private String counterTrader;

    /**
     * 本方交易员
     */
    private String trader;
    /**
     * 约定号
     */
    private String counterConNo;
    /**
     * 席位号
     */
    private String counterSeat;
    /**
     * 备注
     */
    private String remark;
    /**
     * 回购方向
     */
    private String busiPs;
    /**
     * 质押券代码
     */
    private String repSecId;

	/**
	 * 续做类型
	 */
	private String redoType;
	private String transCode;
	/**
	 * 个性分类三
     */
    private String assetType10;
    public Date getVpayDate() {
		return vpayDate;
	}
	public void setVpayDate(Date vpayDate) {
		this.vpayDate = vpayDate;
	}
	public Date getMpayDate() {
		return mpayDate;
	}
	public void setMpayDate(Date mpayDate) {
		this.mpayDate = mpayDate;
	}
	public BigDecimal getmIntAmt() {
		return mIntAmt;
	}
	public void setmIntAmt(BigDecimal mIntAmt) {
		this.mIntAmt = mIntAmt;
	}
	public BigDecimal getmTradeAmt() {
		return mTradeAmt;
	}
	public void setmTradeAmt(BigDecimal mTradeAmt) {
		this.mTradeAmt = mTradeAmt;
	}
	public BigDecimal getCapDays() {
		return capDays;
	}
	public void setCapDays(BigDecimal capDays) {
		this.capDays = capDays;
	}
	public String getTradeId() {
		return tradeId;
	}
	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public BigDecimal getPledgeFaceValue() {
		return pledgeFaceValue;
	}
	public void setPledgeFaceValue(BigDecimal pledgeFaceValue) {
		this.pledgeFaceValue = pledgeFaceValue;
	}
	public BigDecimal getPledgeNumber() {
		return pledgeNumber;
	}
	public void setPledgeNumber(BigDecimal pledgeNumber) {
		this.pledgeNumber = pledgeNumber;
	}
	public BigDecimal getPledgeRatio() {
		return pledgeRatio;
	}
	public void setPledgeRatio(BigDecimal pledgeRatio) {
		this.pledgeRatio = pledgeRatio;
	}
	public BigDecimal getPledgeAmt() {
		return pledgeAmt;
	}
	public void setPledgeAmt(BigDecimal pledgeAmt) {
		this.pledgeAmt = pledgeAmt;
	}
	public Long getTermDays() {
		return termDays;
	}
	public void setTermDays(Long termDays) {
		this.termDays = termDays;
	}
	public String getCounterId() {
		return counterId;
	}
	public void setCounterId(String counterId) {
		this.counterId = counterId;
	}
	public String getCounterTrader() {
		return counterTrader;
	}
	public void setCounterTrader(String counterTrader) {
		this.counterTrader = counterTrader;
	}
	public String getTrader() {
		return trader;
	}
	public void setTrader(String trader) {
		this.trader = trader;
	}
	public String getCounterConNo() {
		return counterConNo;
	}
	public void setCounterConNo(String counterConNo) {
		this.counterConNo = counterConNo;
	}
	@Length(max = 6, fieldName = "EtrComponent.counterSeat")
	public String getCounterSeat() {
		return counterSeat;
	}
	public void setCounterSeat(String counterSeat) {
		this.counterSeat = counterSeat;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getBusiPs() {
		return busiPs;
	}
	public void setBusiPs(String busiPs) {
		this.busiPs = busiPs;
	}
	public String getRepSecId() {
		return repSecId;
	}
	public void setRepSecId(String repSecId) {
		this.repSecId = repSecId;
	}
	public String getAssetType10() {
		return assetType10;
	}
	public void setAssetType10(String assetType10) {
		this.assetType10 = assetType10;
	}

	public String getRedoType() {
		return redoType;
	}

	public void setRedoType(String redoType) {
		this.redoType = redoType;
	}
	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}
}
