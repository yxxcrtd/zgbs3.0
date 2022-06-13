package com.joyintech.tams.business.common.dto;

import java.util.UUID;

public class ManagerDto {
    
    private String id = UUID.randomUUID().toString();
    private boolean isNeedListener = false;  // 是否需要监听，异常撤销时不用监听
    private String exchange; // 交换机路径
    private String routingKey; // 路由   ps：与交换机路径在一起匹配到对应队列QUEUE,类似java中类名，交换机为包名
    private boolean needTimeOutRevoke=true; // mq发送异常是否抛出，包含超时，一般撤销、撤回、关闭等操作时设置
    private int revokeTimes = 0; // 正常撤销发送超时后重发次数
    private PositionBondExecuteParam positionRuleInterDto;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNeedListener() {
        return isNeedListener;
    }

    public int getRevokeTimes() {
        return revokeTimes;
    }

    public void setRevokeTimes(int revokeTimes) {
        this.revokeTimes = revokeTimes;
    }

    public void setNeedListener(boolean needListener) {
        isNeedListener = needListener;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public boolean isNeedTimeOutRevoke() {
        return needTimeOutRevoke;
    }

    public void setNeedTimeOutRevoke(boolean needTimeOutRevoke) {
        this.needTimeOutRevoke = needTimeOutRevoke;
    }

    public PositionBondExecuteParam getPositionRuleInterDto() {
        return positionRuleInterDto;
    }

    public void setPositionRuleInterDto(PositionBondExecuteParam positionRuleInterDto) {
        this.positionRuleInterDto = positionRuleInterDto;
    }
}
