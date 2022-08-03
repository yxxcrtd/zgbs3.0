package com.joyintech.tams.exchange.common.bean;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "质押式协议回购行情质押券返回报文", description = "质押式协议回购行情质押券返回报文")
public class IntegratedRepurchaseQuotePledgeBean {

    @ApiModelProperty(name = "execid", value = "执行编号")
    private String execid;
    @ApiModelProperty(name = "market", value = "市场代码")
    private String market;
    @ApiModelProperty(name = "stkcode", value = "质押券代码")
    private String stkcode;
    @ApiModelProperty(name = "orderqty", value = "质押券数量")
    private String orderqty;
    @ApiModelProperty(name = "deliverySide", value = "质押方向")
    private String deliverySide;
    @ApiModelProperty(name = "stkprop", value = "股份性质")
    private String stkprop;
	public String getExecid() {
		return execid;
	}
	public void setExecid(String execid) {
		this.execid = execid;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public String getStkcode() {
		return stkcode;
	}
	public void setStkcode(String stkcode) {
		this.stkcode = stkcode;
	}
	public String getOrderqty() {
		return orderqty;
	}
	public void setOrderqty(String orderqty) {
		this.orderqty = orderqty;
	}
	public String getDeliverySide() {
		return deliverySide;
	}
	public void setDeliverySide(String deliverySide) {
		this.deliverySide = deliverySide;
	}
	public String getStkprop() {
		return stkprop;
	}
	public void setStkprop(String stkprop) {
		this.stkprop = stkprop;
	}

}
