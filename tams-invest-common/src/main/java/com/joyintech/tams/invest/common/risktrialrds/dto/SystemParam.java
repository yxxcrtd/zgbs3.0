package com.joyintech.tams.invest.common.risktrialrds.dto;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;

import java.math.BigDecimal;
import java.util.Map;

public class SystemParam {

    private String valDate;

    private String portfolioCode;

    private String assetCode;

    private String tradeCode;

    private String ptlBagId;

    private String supFrequency;

    /**
     * 交易金额
     */
    private BigDecimal tradeAmt;

    /**
     * 交易数量
     */
    private BigDecimal tradeNum;

    /**
     * 资产类型
     */
    private String assetType;

    /**
     * 资产细类
     */
    private String assetSecType;

    /**
     * 到期日
     */
    private String mDate;

    /**
     * 事件类型(委托或交易)
     */
    private String eventType;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 发行人code
     */
    private String issuerCode;

    /**
     * 资产基本信息(现金规则等,以资产ID为维度)
     */
    private Map<String,Object> astCashMap;

    public String getValDate() {
        return valDate;
    }

    public void setValDate(String valDate) {
        this.valDate = valDate;
    }

    public String getPortfolioCode() {
        return portfolioCode;
    }

    public void setPortfolioCode(String portfolioCode) {
        this.portfolioCode = portfolioCode;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getPtlBagId() {
        return ptlBagId;
    }

    public void setPtlBagId(String ptlBagId) {
        this.ptlBagId = ptlBagId;
    }

    public String getSupFrequency() {
        return supFrequency;
    }

    public void setSupFrequency(String supFrequency) {
        this.supFrequency = supFrequency;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(BigDecimal tradeNum) {
        this.tradeNum = tradeNum;
    }

    public String getAssetSecType() {
        return assetSecType;
    }

    public void setAssetSecType(String assetSecType) {
        this.assetSecType = assetSecType;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getIssuerCode() {
        return issuerCode;
    }

    public void setIssuerCode(String issuerCode) {
        this.issuerCode = issuerCode;
    }

    public Map<String, Object> getAstCashMap() {
        return astCashMap;
    }

    public void setAstCashMap(Map<String, Object> astCashMap) {
        this.astCashMap = astCashMap;
    }

    public String replaceForString(String sql) {
        String ptlCode = StringUtil.contains(portfolioCode,
            GlobalConstant.OPEN_PARENTHESIS) ? portfolioCode : "'" + portfolioCode + "'";
        String astCode = StringUtil.contains(assetCode,
            GlobalConstant.OPEN_PARENTHESIS) ? assetCode : "'" + assetCode + "'";
        String ptlBagCode= StringUtil.contains(ptlBagId,
                GlobalConstant.OPEN_PARENTHESIS) ? ptlBagId : "'" + ptlBagId + "'";
        String issuerCode = StringUtil.contains(this.issuerCode,
                GlobalConstant.OPEN_PARENTHESIS) ? this.issuerCode : "'" + this.issuerCode + "'";
        return sql.replaceAll("\\$\\{valDate\\}", valDate).replaceAll("\\$\\{portfolioCode\\}",
            ptlCode).replaceAll("\\$\\{assetCode\\}", astCode).replaceAll("\\$\\{tradeCode\\}",
                tradeCode).replaceAll(
                "\\$\\{assetType\\}", "'" +assetType+ "'").replaceAll("\\$\\{ptlBagId\\}",
                ptlBagCode).replaceAll("\\$\\{issuerCode\\}",
                this.issuerCode);
    }
}