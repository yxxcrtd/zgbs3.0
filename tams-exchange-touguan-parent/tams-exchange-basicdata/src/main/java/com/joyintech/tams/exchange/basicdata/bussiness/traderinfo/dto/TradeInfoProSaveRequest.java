package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto;


import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.validator.TradeInfoPro001Validator;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;



public class TradeInfoProSaveRequest extends BaseEntity implements ProcessEntity{

    @ApiModelProperty(name="operateType",value="操作类型",example="submit/save", required = true)
    private String operateType;
    @ApiModelProperty(name="elementId",value="按钮id",example="TradeInfoPro001_btn_submit/TradeInfoPro001_btn_save")
    private String elementId;
    @ApiModelProperty(name="pageId",value="d",example="TradeInfoPro001")
    private String pageId;

    @ApiModelProperty(name="seqNo",value="流水号",example="A1", required = true)
    private String seqNo;
    @ApiModelProperty(name="customerId",value="客户代码",example="A1", required = true)
    private String customerId;
    @ApiModelProperty(name="dealer",value="交易商",example="A1", required = true)
    private String dealer;
    @ApiModelProperty(name="dealerName",value="交易商名称",example="A1", required = true)
    private String dealerName;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="A1", required = true)
    private String tradeMarket;

    @ApiModelProperty(name="dealerNo",value="交易商代码",example="A1", required = true)
    private String dealerNo;

    @ApiModelProperty(name="dealNoPre",value="修改前交易商代码",example="A1")
    private String dealNoPre;

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

    @ApiModelProperty(name="selectedItem",value="选中数据",example="A1,A2..", required = true)
    private List<TradeInfoProSaveRequest> selectedItem;
    @ApiModelProperty(name="dealerPartyNoPre",value="修改前交易主体代码",example="A1")
    private String dealerPartyNoPre;
    @ApiModelProperty(name="traderNoPre",value="修改前交易对手代码",example="A1")
    private String traderNoPre;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
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

    public void setDealerPartyType(String dealerPartyType) {
        this.dealerPartyType = dealerPartyType;
    }

    public String getDealerPartyName() {
        return dealerPartyName;
    }

    public void setDealerPartyName(String dealerPartyName) {
        this.dealerPartyName = dealerPartyName;
    }

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

    public List<TradeInfoProSaveRequest> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TradeInfoProSaveRequest> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDealerPartyNo() {
        return dealerPartyNo;
    }

    public void setDealerPartyNo(String dealerPartyNo) {
        this.dealerPartyNo = dealerPartyNo;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getDealNoPre() {
        return dealNoPre;
    }

    public void setDealNoPre(String dealNoPre) {
        this.dealNoPre = dealNoPre;
    }

    public String getDealerPartyNoPre() {
        return dealerPartyNoPre;
    }

    public void setDealerPartyNoPre(String dealerPartyNoPre) {
        this.dealerPartyNoPre = dealerPartyNoPre;
    }

    public String getTraderNoPre() {
        return traderNoPre;
    }

    public void setTraderNoPre(String traderNoPre) {
        this.traderNoPre = traderNoPre;
    }
}
