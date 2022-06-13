package com.joyintech.tams.exchange.service.trade.server.agent.kcbp;

import java.util.Map;

public class KCBPAccount {
    private String accountId;
    private String agentType;
    /**
     * 股东代码
     */
    private Map<String, String> secuIds;
    /**
     * 客户代码
     */
    private String custId;
    /**
     * 缺省资金帐户
     */
    private String fundId;
    /**
     * 机构编码
     */
    private String orgId;
    /**
     * 客户证书
     */
    private String custert;
    private Map<String, String> headMessages;

    private String tradePositionStr;
    private String protocolBack_SZ_PositionStr_trade;
    private String protocolBack_SH_PositionStr_trade;
    private String fixedIncome_PositionStr_trade;
    private String entrustPositionStr;
    private String protocolBack_SZ_PositionStr_entrust;
    private String protocolBack_SH_PositionStr_entrust;
    private String fixedIncome_PositionStr_entrust;
    private String RTGS_PositionStr;
    private String bondNegotiate_SZ_PositionStr_Trade;
    private String bondNegotiate_SZ_PositionStr_Entrust;

    public String getEntrustPositionStr() {
        return entrustPositionStr == null ? "" : entrustPositionStr;
    }

    public void setEntrustPositionStr(String entrustPositionStr) {
        this.entrustPositionStr = entrustPositionStr;
    }

    public String getTradePositionStr() {
        return tradePositionStr == null ? "" : tradePositionStr;
    }

    public void setTradePositionStr(String tradePositionStr) {
        this.tradePositionStr = tradePositionStr;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getFundId() {
        return fundId;
    }

    public void setFundId(String fundId) {
        this.fundId = fundId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getCustert() {
        return custert;
    }

    public void setCustert(String custert) {
        this.custert = custert;
    }

    public Map<String, String> getSecuIds() {
        return secuIds;
    }

    public void setSecuIds(Map<String, String> secuIds) {
        this.secuIds = secuIds;
    }

    public Map<String, String> getHeadMessages() {
        return headMessages;
    }

    public void setHeadMessages(Map<String, String> headMessages) {
        this.headMessages = headMessages;
    }

    public String getProtocolBack_SZ_PositionStr_trade() {
        return protocolBack_SZ_PositionStr_trade == null ? "" : protocolBack_SZ_PositionStr_trade;
    }

    public void setProtocolBack_SZ_PositionStr_trade(String protocolBack_SZ_PositionStr_trade) {
        this.protocolBack_SZ_PositionStr_trade = protocolBack_SZ_PositionStr_trade;
    }

    public String getProtocolBack_SH_PositionStr_trade() {
        return protocolBack_SH_PositionStr_trade == null ? "" : protocolBack_SH_PositionStr_trade;
    }

    public void setProtocolBack_SH_PositionStr_trade(String protocolBack_SH_PositionStr_trade) {
        this.protocolBack_SH_PositionStr_trade = protocolBack_SH_PositionStr_trade;
    }

    public String getFixedIncome_PositionStr_trade() {
        return fixedIncome_PositionStr_trade == null ? "" : fixedIncome_PositionStr_trade;
    }

    public void setFixedIncome_PositionStr_trade(String fixedIncome_PositionStr_trade) {
        this.fixedIncome_PositionStr_trade = fixedIncome_PositionStr_trade;
    }

    public String getProtocolBack_SZ_PositionStr_entrust() {
        return protocolBack_SZ_PositionStr_entrust == null ? "" : protocolBack_SZ_PositionStr_entrust;
    }

    public void setProtocolBack_SZ_PositionStr_entrust(String protocolBack_SZ_PositionStr_entrust) {
        this.protocolBack_SZ_PositionStr_entrust = protocolBack_SZ_PositionStr_entrust;
    }

    public String getProtocolBack_SH_PositionStr_entrust() {
        return protocolBack_SH_PositionStr_entrust == null ? "" : protocolBack_SH_PositionStr_entrust;
    }

    public void setProtocolBack_SH_PositionStr_entrust(String protocolBack_SH_PositionStr_entrust) {
        this.protocolBack_SH_PositionStr_entrust = protocolBack_SH_PositionStr_entrust;
    }

    public String getFixedIncome_PositionStr_entrust() {
        return fixedIncome_PositionStr_entrust == null ? "" : fixedIncome_PositionStr_entrust;
    }

    public void setFixedIncome_PositionStr_entrust(String fixedIncome_PositionStr_entrust) {
        this.fixedIncome_PositionStr_entrust = fixedIncome_PositionStr_entrust;
    }

    public String getRTGS_PositionStr() {
        return RTGS_PositionStr == null ? "" : RTGS_PositionStr;
    }

    public void setRTGS_PositionStr(String RTGS_PositionStr) {
        this.RTGS_PositionStr = RTGS_PositionStr;
    }

	public String getBondNegotiate_SZ_PositionStr_Trade() {
		return bondNegotiate_SZ_PositionStr_Trade;
	}

	public void setBondNegotiate_SZ_PositionStr_Trade(String bondNegotiate_SZ_PositionStr_Trade) {
		this.bondNegotiate_SZ_PositionStr_Trade = bondNegotiate_SZ_PositionStr_Trade;
	}

	public String getBondNegotiate_SZ_PositionStr_Entrust() {
		return bondNegotiate_SZ_PositionStr_Entrust;
	}

	public void setBondNegotiate_SZ_PositionStr_Entrust(String bondNegotiate_SZ_PositionStr_Entrust) {
		this.bondNegotiate_SZ_PositionStr_Entrust = bondNegotiate_SZ_PositionStr_Entrust;
	}
}
