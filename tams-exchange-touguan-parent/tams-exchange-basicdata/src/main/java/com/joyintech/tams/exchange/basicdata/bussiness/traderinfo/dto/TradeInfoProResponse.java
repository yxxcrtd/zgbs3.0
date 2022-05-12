package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto;


import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.util.SysDataUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value="交易商信息维护返回参数",description="交易商信息维护返回参数")
public class TradeInfoProResponse {

    @ApiModelProperty(name="dealer",value="交易商",example="A1", required = true)
    private String dealer;

    @ApiModelProperty(name="seqNo",value="流水号",example="A1", required = true)
    private String seqNo;

    @ApiModelProperty(name="customerId",value="客户代码",example="A1", required = true)
    private String customerId;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="A1", required = true)
    private String tradeMarket;

    @ApiModelProperty(name="dealerNo",value="交易商代码",example="A1", required = true)
    private String dealerNo;

    @ApiModelProperty(name="dealerName",value="交易商名称",example="A1", required = true)
    private String dealerName;

    @ApiModelProperty(name="dealerPartyNo",value="交易主体代码",example="A1", required = true)
    private String dealerPartyNo;

    @ApiModelProperty(name="dealerPartyType",value="交易主体类型",example="A1", required = true)
    private String dealerPartyType;

    @ApiModelProperty(name="dealerPartyName",value="交易主体名称",example="A1", required = true)
    private String dealerPartyName;

    @ApiModelProperty(name="trader",value="交易员",example="A1", required = true)
    private String trader;

    @ApiModelProperty(name="traderNo",value="交易员代码",example="A1", required = true)
    private String traderNo;

    @ApiModelProperty(name="isOur",value="本方标识",example="A1", required = true)
    private String isOur;

    @ApiModelProperty(name="traderList",value="交易主体信息")
    private List<TraderInfoDto> traderInfoDtoList;


    public String getTradeMarketStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易流通场所, this.getTradeMarket());
    }
    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getDealerNo() {
        return dealerNo;
    }

    public void setDealerNo(String dealerNo) {
        this.dealerNo = dealerNo;
    }

    public String getDealerPartyType() {
        return dealerPartyType;
    }

    public String getDealerPartyTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易主体类型, this.getDealerPartyType());
    }

    public void setDealerPartyType(String dealerPartyType) {
        this.dealerPartyType = dealerPartyType;
    }

    public String getDealerPartyName() {
        return dealerPartyName;
    }

    public void setDealerPartyName(String dealerPartyName) {
        this.dealerPartyName = dealerPartyName;
    }

    @Length(fieldName = "交易员名称", max = 15, groups = {ValidateGroupEdit.class})
    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getTraderNo() {
        return traderNo;
    }

    public void setTraderNo(String traderNo) {
        this.traderNo = traderNo;
    }

    public String getIsOur() {
        return isOur;
    }

    public void setIsOur(String isOur) {
        this.isOur = isOur;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerPartyNo() {
        return dealerPartyNo;
    }

    public void setDealerPartyNo(String dealerPartyNo) {
        this.dealerPartyNo = dealerPartyNo;
    }

    public List<TraderInfoDto> getTraderInfoDtoList() {
        return traderInfoDtoList;
    }

    public void setTraderInfoDtoList(List<TraderInfoDto> traderInfoDtoList) {
        this.traderInfoDtoList = traderInfoDtoList;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }
}
