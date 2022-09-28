package com.joyintech.tams.invest.trade.transfer.deposittransfere.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: HXB2.1.0
 * @description:
 * @author: hepengli
 * @create: 2019-12-12 17:18
 **/
public class DepositTransfere002Dto extends BaseEntity {
    /**
     * 金融产品交易实体类
     */
    private FinProductTradeDto finProductTradeDto;
    /**
     * 组合表
     */
    private List<PtlTrade> ptlTrade;
    /**
     * 金融产品交易扩展
     */
    private List<PaTrdProductDeal> paTrdProductDeals;
    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;
    /**
     * 实体组合代码
     */
    private String actPortfolioId;
    /**
     * 交易编号
     */
    private String tradeId;
    /**
     * 转出交易编号
     */
    private String tradeIdOut;
    /**
     * 组合代码
     */
    private String portfolioId;
    /**
     * 组合名称
     */
    private String portfolioName;
    /**
     * 资产代码
     */
    private String finprodId;
    /**
     * 交易日期
     */
    private Date tradeDate;
    /**
     * 交割日期
     */
    private Date settleDate;
    /**
     * 本金金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal prinAmt;
    /**
     * 借款利率
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal loadRate;
    /**
     * 应计利息总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal intAmt;
    /**
     * 交易金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal transferTradeAmt;

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

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    public String getActPortfolioId() {
        return actPortfolioId;
    }

    public void setActPortfolioId(String actPortfolioId) {
        this.actPortfolioId = actPortfolioId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeIdOut() {
        return tradeIdOut;
    }

    public FinProductTradeDto getFinProductTradeDto() {
        return finProductTradeDto;
    }

    public void setFinProductTradeDto(FinProductTradeDto finProductTradeDto) {
        this.finProductTradeDto = finProductTradeDto;
    }

    public List<PtlTrade> getPtlTrade() {
        return ptlTrade;
    }

    public void setPtlTrade(List<PtlTrade> ptlTrade) {
        this.ptlTrade = ptlTrade;
    }

    public List<PaTrdProductDeal> getPaTrdProductDeals() {
        return paTrdProductDeals;
    }

    public void setPaTrdProductDeals(List<PaTrdProductDeal> paTrdProductDeals) {
        this.paTrdProductDeals = paTrdProductDeals;
    }

    public void setTradeIdOut(String tradeIdOut) {
        this.tradeIdOut = tradeIdOut;
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

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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

    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    public BigDecimal getLoadRate() {
        return loadRate;
    }

    public void setLoadRate(BigDecimal loadRate) {
        this.loadRate = loadRate;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getTransferTradeAmt() {
        return transferTradeAmt;
    }

    public void setTransferTradeAmt(BigDecimal transferTradeAmt) {
        this.transferTradeAmt = transferTradeAmt;
    }

    private String stlPayId;

    public String getStlPayId() {
        return stlPayId;
    }

    public void setStlPayId(String stlPayId) {
        this.stlPayId = stlPayId;
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
}
