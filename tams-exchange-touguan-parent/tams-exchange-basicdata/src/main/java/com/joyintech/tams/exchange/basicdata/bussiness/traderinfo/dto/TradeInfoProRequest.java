package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value="交易商信息维护请求参数",description="交易商信息维护请求参数")
public class TradeInfoProRequest {

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name="isOur",value="本方标识",example="A1", required = true)
    private String isOur;

    @ApiModelProperty(name="dealer",value="交易商",example="A1", required = true)
    private String dealer;

    @ApiModelProperty(name="dealerNo",value="交易商代码",example="A1", required = true)
    private String dealerNo;

    @ApiModelProperty(name="dealerName",value="交易商名称",example="A1", required = true)
    private String dealerName;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="A1", required = true)
    private String tradeMarket;

    @ApiModelProperty(name="traderNo",value="交易员代码",example="A1", required = true)
    private String traderNo;

    @ApiModelProperty(name="traderName",value="交易员名称",example="A1", required = true)
    private String traderName;

    @ApiModelProperty(name="dealerPartyNo",value="交易主体代码",example="A1", required = true)
    private String dealerPartyNo;

    @ApiModelProperty(name="dealerPartyName",value="交易主体名称",example="A1", required = true)
    private String dealerPartyName;

    @ApiModelProperty(name="acctNo",value="资金账户",example="A1", required = true)
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

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

}
