package com.joyintech.tams.invest.trade.transfer.deposittransfere.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 存款类资产交易转仓Dto
 *
 * @author hepengli
 * @version 1.0 2019年12月12日
 * @see DepositTransfere001Dto
 * @since 1.0
 */
public class DepositTransfere001Dto extends WflTrdProductDeal implements ProcessEntity {
    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;
    /**
     * 待转仓组合
     */
    private String transferPortfolioId;
    /**
     * 所有相关表，贯穿整个流程
     */
    private BaseTotalTradeInfoDto baseTotalTradeInfoDto;
    /**
     * 金融产品交易实体类
     */
    private FinProductTradeDto finProductTradeDto;
    /**
     * 组合表
     */
    private List<PtlTrade> ptlTrade;
    /**
     * 转仓列表
     */
    private List<DepositTransfere002Dto> depositTransfere002Dto;
    /**
     * 委托代码
     */
    private String entrustId;
    /**
     * 交易编号
     */
    @PrimaryKey(PrimaryKey.Field.PK_1)
    private String tradeId;
    /**
     * 实体组合代码
     */
    private String actPortfolioId;
    /**
     * 金融产品类型
     */
    private String finprodType;
    /**
     * 交易日期
     */
    private Date tradeDate;
    /**
     * 交割日期
     */
    private Date settleDate;
    /**
     * 交易方向
     */
    private String ps;
    /**
     * 生效日
     */
    private Date effDate;
    /**
     * 起息日
     */
    private Date vDate;
    /**
     * 生效利率
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal yield;
    /**
     * 计息基础
     */
    private String basis;
    /**
     * 是否随交易支付
     */
    private String isPayWithTrade;
    /**
     * 资产代码
     */
    private String finprodMarketId;
    /**
     * 资产代码
     */
    private String finprodId;
    /**
     * 资产名称
     */
    private String finprodAbbr;
    /**
     * 币种
     */
    private String ccy;
    /**
     * 操作方式
     */
    private String operateType;
    /**
     * 净投资收益率%
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal loadRate;
    /**
     * 本金金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal prinAmt;
    /**
     * 通道
     */
    private String chlId;
    /**
     * 剩余可拆分金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal restAmt;
    /**
     * 组合代码
     */
    private String portfolioId;
    /**
     * 组合名称
     */
    private String portfolioName;
    /**
     * 拼接组合代码和名称
     */
    private String portfolioIdStr;
    /**
     * 利息总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal intAmt;
    /**
     * 通道费
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal feeAmt;
    /**
     * 交易金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal tradeAmt;
    /**
     * 拆分前交易编号
     */
    private String splitTradeId;
    /**
     * 交易日历编号
     */
    private String calendarId;
    
    /**
     * 转让逾期利息
     */
    private BigDecimal transOverdueIntAmt;
	
    /**
     * 转让逾期本金
     */
    private BigDecimal transOverduePrinAmt;
	
    /**
     * 交割金额
     */
    private BigDecimal deliveryAmount;

    /**
     * 划款指令
     */
    private List<BussTrdTransferOrderDto> bussTrdTransferOrder;

    public List<BussTrdTransferOrderDto> getBussTrdTransferOrder() {
        return bussTrdTransferOrder;
    }

    public void setBussTrdTransferOrder(List<BussTrdTransferOrderDto> bussTrdTransferOrder) {
        this.bussTrdTransferOrder = bussTrdTransferOrder;
    }

    public String getOperateType() {
        return operateType;
    }

    public String getSplitTradeId() {
        return splitTradeId;
    }

    public FinProductTradeDto getFinProductTradeDto() {
        return finProductTradeDto;
    }

    public void setFinProductTradeDto(FinProductTradeDto finProductTradeDto) {
        this.finProductTradeDto = finProductTradeDto;
    }

    public List<DepositTransfere002Dto> getDepositTransfere002Dto() {
        return depositTransfere002Dto;
    }

    public void setDepositTransfere002Dto(List<DepositTransfere002Dto> depositTransfere002Dto) {
        this.depositTransfere002Dto = depositTransfere002Dto;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public List<PtlTrade> getPtlTrade() {
        return ptlTrade;
    }

    public void setPtlTrade(List<PtlTrade> ptlTrade) {
        this.ptlTrade = ptlTrade;
    }

    public void setSplitTradeId(String splitTradeId) {
        this.splitTradeId = splitTradeId;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getTransferPortfolioId() {
        return transferPortfolioId;
    }

    public void setTransferPortfolioId(String investPortfolioId) {
        this.transferPortfolioId = investPortfolioId;
    }

    public BaseTotalTradeInfoDto getBaseTotalTradeInfoDto() {
        return baseTotalTradeInfoDto;
    }

    public void setBaseTotalTradeInfoDto(BaseTotalTradeInfoDto baseTotalTradeInfoDto) {
        this.baseTotalTradeInfoDto = baseTotalTradeInfoDto;
    }

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    public String getIsPayWithTrade() {
        return isPayWithTrade;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public Date getvDate() {
        return vDate;
    }

    public void setvDate(Date vDate) {
        this.vDate = vDate;
    }

    @Override
    public BigDecimal getYield() {
        return yield;
    }

    @Override
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public void setIsPayWithTrade(String isPayWithTrade) {
        this.isPayWithTrade = isPayWithTrade;
    }

    public String getActPortfolioId() {
        return actPortfolioId;
    }

    public void setActPortfolioId(String actPortfolioId) {
        this.actPortfolioId = actPortfolioId;
    }

    public String getFinprodType() {
        return finprodType;
    }

    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public String getPortfolioIdStr() {
        return portfolioIdStr;
    }

    public void setPortfolioIdStr(String portfolioIdStr) {
        this.portfolioIdStr = portfolioIdStr;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getLoadRate() {
        return loadRate;
    }

    public void setLoadRate(BigDecimal loadRate) {
        this.loadRate = loadRate;
    }

    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    public String getChlId() {
        return chlId;
    }

    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    public BigDecimal getRestAmt() {
        return restAmt;
    }

    public void setRestAmt(BigDecimal restAmt) {
        this.restAmt = restAmt;
    }

    /**
     * 获取 起息日
     *
     * @return vDate 起息日
     */
    public Date getVDate() {
        return this.vDate;
    }

    /**
     * 设置 起息日
     *
     * @param vDate 起息日
     */
    public void setVDate(Date vDate) {
        this.vDate = vDate;
    }

    /**
     * 获取 交易日历编号
     *
     * @return calendarId 交易日历编号
     */
    public String getCalendarId() {
        return this.calendarId;
    }

    /**
     * 设置 交易日历编号
     *
     * @param calendarId 交易日历编号
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }
    
    /**
     * 获取转让逾期利息 transOverdueIntAmt
     * @return
     */
	public BigDecimal getTransOverdueIntAmt() {
		return transOverdueIntAmt;
	}

	/**
	 * 设置转让逾期利息 transOverdueIntAmt
	 * @param transOverdueIntAmt
	 */
	public void setTransOverdueIntAmt(BigDecimal transOverdueIntAmt) {
		this.transOverdueIntAmt = transOverdueIntAmt;
	}

	/**
	 * 获取转让逾期本金 transOverduePrinAmt
	 * @return
	 */
	public BigDecimal getTransOverduePrinAmt() {
		return transOverduePrinAmt;
	}

	/**
	 * 设置转让逾期本金 transOverduePrinAmt
	 * @param transOverduePrinAmt
	 */
	public void setTransOverduePrinAmt(BigDecimal transOverduePrinAmt) {
		this.transOverduePrinAmt = transOverduePrinAmt;
	}

	/**
	 * 获取交割金额 deliveryAmount
	 * @return
	 */
	public BigDecimal getDeliveryAmount() {
		return deliveryAmount;
	}

	/**
	 * 设置交割金额 deliveryAmount
	 * @param deliveryAmount
	 */
	public void setDeliveryAmount(BigDecimal deliveryAmount) {
		this.deliveryAmount = deliveryAmount;
	}

    /**
     * 转仓编号
     */
    private String zcId;

    public String getZcId() {
        return zcId;
    }

    public void setZcId(String zcId) {
        this.zcId = zcId;
    }

    /**
     * 附件编号
     */
    private String businessId;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    private String fileArrayName;

    public String getFileArrayName() {
        return fileArrayName;
    }

    public void setFileArrayName(String fileArrayName) {
        this.fileArrayName = fileArrayName;
    }
}
