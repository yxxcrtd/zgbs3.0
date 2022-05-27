package com.joyintech.service.basicdata.standardcouponreconversionrate.dto;

import com.joyintech.tams.common.base.entity.StdBondMoreConvrateInfo;

import java.util.List;

public class StandardCouponReConversionRateDto extends StdBondMoreConvrateInfo {

    private transient List<StandardCouponReConversionRateDto> selectedItem;


    private String brokerName; //券商

    private String accountType; //资金账号


    private String accountTypeStr;

    private String tandardRate;//标准券再折算率

    private String acctNoStr;

    private String finprodIdStr;

    public String getFinprodIdStr() {
        return finprodIdStr;
    }

    public void setFinprodIdStr(String finprodIdStr) {
        this.finprodIdStr = finprodIdStr;
    }

    public String getAcctNoStr() {
        return acctNoStr;
    }

    public void setAcctNoStr(String acctNoStr) {
        this.acctNoStr = acctNoStr;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getTandardRate() {
        return tandardRate;
    }

    public void setTandardRate(String tandardRate) {
        this.tandardRate = tandardRate;
    }


    public List<StandardCouponReConversionRateDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<StandardCouponReConversionRateDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getAccountTypeStr() {
        return accountTypeStr;
    }

    public void setAccountTypeStr(String accountTypeStr) {
        this.accountTypeStr = accountTypeStr;
    }

    /**
     * 金融产品简称
     */
    private String finprodAbbr;

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    private String finprodMarketId;

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }
}
