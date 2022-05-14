package com.joyintech.tams.exchange.common.bean;

import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.Order;

import com.joyintech.tams.exchange.common.vo.EntrustVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel(value="订单和委托报文",description="订单和委托报文")
public class OrderAndEntrustQryResult implements Serializable {

    private static final long serialVersionUID = -1701098381926217047L;
    @ApiModelProperty(name = "orderList", value = "订单列表")
    private List<Order> orderList;

    @ApiModelProperty(name = "entrustList", value = "委托列表")
    private List<EntrustVo> entrustList;

    public OrderAndEntrustQryResult() {
    }

    public OrderAndEntrustQryResult(List<Order> orderList, List<EntrustVo> entrustList) {
        this.orderList = orderList;
        this.entrustList = entrustList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<EntrustVo> getEntrustList() {
        return entrustList;
    }

    public void setEntrustList(List<EntrustVo> entrustList) {
        this.entrustList = entrustList;
    }
}
