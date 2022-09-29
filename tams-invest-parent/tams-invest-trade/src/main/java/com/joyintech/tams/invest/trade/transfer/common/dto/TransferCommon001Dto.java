package com.joyintech.tams.invest.trade.transfer.common.dto;

import com.joyintech.framework.util.DateUtil;

import java.math.BigDecimal;
import java.util.Date;

public class TransferCommon001Dto {
    /**
     * 组合代码
     */
    private String portfolioId;
    /**
     * 组合名称
     */
    private String portfolioName;
    /**
     * 起息日
     */
    private Date vdate;
    /**
     * 到期日
     */
    private Date mdate;
    /**
     * 组合可用现金
     */
    private BigDecimal availableCash;
    /**
     * 账户账号
     */
    private String acctNo;
    /**
     * 币种
     */
    private String ccy;
    /**
     * 交易场所
     */
    private String tradePlace;

    /**
     * Gets the value of portfolioId.
     *
     * @return the value of portfolioId
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * Sets the portfolioId.
     *
     * <p>You can use getPortfolioId() to get the value of portfolioId</p>
     *
     * @param portfolioId portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * Gets the value of portfolioName.
     *
     * @return the value of portfolioName
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * Sets the portfolioName.
     *
     * <p>You can use getPortfolioName() to get the value of portfolioName</p>
     *
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * Gets the value of vdate.
     *
     * @return the value of vdate
     */
    public Date getVdate() {
        return vdate;
    }

    public String getVdateStr() {
        return DateUtil.format(vdate);
    }

    public String getMdateStr() {
        return DateUtil.format(mdate);
    }

    /**
     * Sets the vdate.
     *
     * <p>You can use getVdate() to get the value of vdate</p>
     *
     * @param vdate vdate
     */
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * Gets the value of mdate.
     *
     * @return the value of mdate
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * Sets the mdate.
     *
     * <p>You can use getMdate() to get the value of mdate</p>
     *
     * @param mdate mdate
     */
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * Gets the value of availableCash.
     *
     * @return the value of availableCash
     */
    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    /**
     * Sets the availableCash.
     *
     * <p>You can use getAvailableCash() to get the value of availableCash</p>
     *
     * @param availableCash availableCash
     */
    public void setAvailableCash(BigDecimal availableCash) {
        this.availableCash = availableCash;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }
}
