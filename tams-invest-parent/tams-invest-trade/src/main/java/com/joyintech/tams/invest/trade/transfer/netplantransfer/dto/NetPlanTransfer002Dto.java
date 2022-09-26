package com.joyintech.tams.invest.trade.transfer.netplantransfer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * 净值型计划资产转仓转仓列表dto
 * @author liuwei
 * @version 1.0 2019年12月23日
 * @see NetPlanTransfer002Dto
 * @since 1.0
 */
public class NetPlanTransfer002Dto extends BaseEntity {
    /**
     * 组合代码
     */
    private String portfolioId;
    /**
     * 实际组合代码
     */
    private String actPortfolioId;
    /**
     * 组合名称
     */
    private String portfolioName;
    /**
     * 资产代码
     */
    private String finprodMarketId;
    /**
     * 交易日期
     */
    private Date tradeDate;
    /**
     * 交易份额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal tradeAmt;
    /**
     * 单位净值
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal unitValue;
    /**
     * 交易金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal tradeMoney;
    /**
     * 转仓交易dto
     */
    private FinProductTradeDto finProductTradeDto;
    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**
     * 获取 组合代码
     *
     * @return portfolioId 组合代码
     */
    public String getPortfolioId() {
        return this.portfolioId;
    }

    /**
     * 设置 组合代码
     *
     * @param portfolioId 组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取 组合名称
     *
     * @return portfolioName 组合名称
     */
    public String getPortfolioName() {
        return this.portfolioName;
    }

    /**
     * 设置 组合名称
     *
     * @param portfolioName 组合名称
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 获取 资产代码
     *
     * @return finprodMarketId 资产代码
     */
    public String getFinprodMarketId() {
        return this.finprodMarketId;
    }

    /**
     * 设置 资产代码
     *
     * @param finprodMarketId 资产代码
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    /**
     * 获取 交易日期
     *
     * @return tradeDate 交易日期
     */
    public Date getTradeDate() {
        return this.tradeDate;
    }

    /**
     * 设置 交易日期
     *
     * @param tradeDate 交易日期
     */
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    /**
     * 获取 交易份额
     *
     * @return tradeAmt 交易份额
     */
    public BigDecimal getTradeAmt() {
        return this.tradeAmt;
    }

    /**
     * 设置 交易份额
     *
     * @param tradeAmt 交易份额
     */
    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    /**
     * 获取 单位净值
     *
     * @return unitValue 单位净值
     */
    public BigDecimal getUnitValue() {
        return this.unitValue;
    }

    /**
     * 设置 单位净值
     *
     * @param unitValue 单位净值
     */
    public void setUnitValue(BigDecimal unitValue) {
        this.unitValue = unitValue;
    }

    /**
     * 获取 单位净值
     *
     * @return tradeMoney 单位净值
     */
    public BigDecimal getTradeMoney() {
        return this.tradeMoney;
    }

    /**
     * 设置 单位净值
     *
     * @param tradeMoney 单位净值
     */
    public void setTradeMoney(BigDecimal tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    /**
     * 获取 转仓交易dto
     *
     * @return finProductTradeDto 转仓交易dto
     */
    public FinProductTradeDto getFinProductTradeDto() {
        return this.finProductTradeDto;
    }

    /**
     * 设置 转仓交易dto
     *
     * @param finProductTradeDto 转仓交易dto
     */
    public void setFinProductTradeDto(FinProductTradeDto finProductTradeDto) {
        this.finProductTradeDto = finProductTradeDto;
    }

    /**
     * 获取 实际组合代码
     *
     * @return actPortfolioId 实际组合代码
     */
    public String getActPortfolioId() {
        return this.actPortfolioId;
    }

    /**
     * 设置 实际组合代码
     *
     * @param actPortfolioId 实际组合代码
     */
    public void setActPortfolioId(String actPortfolioId) {
        this.actPortfolioId = actPortfolioId;
    }

    /**
     * 获取 风险试算实体类
     *
     * @return riskTrialRetDto 风险试算实体类
     */
    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return this.riskTrialRetDto;
    }

    /**
     * 设置 风险试算实体类
     *
     * @param riskTrialRetDto 风险试算实体类
     */
    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    /**
     * 划款指令
     */
    private List<StlPayOrdersDto> stlPayOrdersList;

    public List<StlPayOrdersDto> getStlPayOrdersList() {
        return stlPayOrdersList;
    }

    public void setStlPayOrdersList(List<StlPayOrdersDto> stlPayOrdersList) {
        this.stlPayOrdersList = stlPayOrdersList;
    }
    private String fileArrayName;

    public String getFileArrayName() {
        return fileArrayName;
    }

    public void setFileArrayName(String fileArrayName) {
        this.fileArrayName = fileArrayName;
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

    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal carryOverQty;

    public BigDecimal getCarryOverQty() {
        return carryOverQty;
    }

    public void setCarryOverQty(BigDecimal carryOverQty) {
        this.carryOverQty = carryOverQty;
    }
}
