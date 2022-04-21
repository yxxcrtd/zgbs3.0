package com.joyintech.service.basicdata.systrdconfig.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;



public class TradeInfoProRequest {


    private String isOur;


    private String dealer;


    private String dealerNo;


    private String dealerName;


    private String tradePlace;


    private String traderNo;


    private String traderName;


    private String dealerPartyNo;


    private String dealerPartyName;


    private String acctNo;

    public String getIsOur() {
        return isOur;
    }

    public void setIsOur(String isOur) {
        this.isOur = isOur;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    public String getDealerNo() {
        return dealerNo;
    }

    public void setDealerNo(String dealerNo) {
        this.dealerNo = dealerNo;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getTraderNo() {
        return traderNo;
    }

    public void setTraderNo(String traderNo) {
        this.traderNo = traderNo;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public String getDealerPartyNo() {
        return dealerPartyNo;
    }

    public void setDealerPartyNo(String dealerPartyNo) {
        this.dealerPartyNo = dealerPartyNo;
    }

    public String getDealerPartyName() {
        return dealerPartyName;
    }

    public void setDealerPartyName(String dealerPartyName) {
        this.dealerPartyName = dealerPartyName;
    }
}
