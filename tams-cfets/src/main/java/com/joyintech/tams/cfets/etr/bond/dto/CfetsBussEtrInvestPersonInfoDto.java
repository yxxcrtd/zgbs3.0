/*
 * 文件名：EtrInvestPersonInfo002Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.etr.bond.dto;


/**
 * 投资人员信息DTO
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年9月19日
 * @see CfetsBussEtrInvestPersonInfoDto
 * @since 1.0
 */
public class CfetsBussEtrInvestPersonInfoDto {


    /**
     * 投资处室
     */
    private String room;
    
    /**
     * 指定交易员
     */
    private String trader;
    
    /**
     * 交易对手
     */
    private String counterId;
    /**
     * 对手方交易员
     */
    private String counterTrader;
    /**
     * 对手方联系方式
     */
    private String contactInfo;

    /**
     * 投资经理
     */
    private String invManagerId;

    
    /**
     * 获取 room
     * @return room.
     */
    public String getRoom() {
        return room;
    }

    
    /**
     * 设置 room
     * @param room room
     */
    public void setRoom(String room) {
        this.room = room;
    }

    
    /**
     * 获取 trader
     * @return trader.
     */
    public String getTrader() {
        return trader;
    }

    
    /**
     * 设置 trader
     * @param trader trader
     */
    public void setTrader(String trader) {
        this.trader = trader;
    }

    
    /**
     * 获取 counterId
     * @return counterId.
     */
    public String getCounterId() {
        return counterId;
    }

    
    /**
     * 设置 counterId
     * @param counterId counterId
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    
    /**
     * 获取 counterTrader
     * @return counterTrader.
     */
    public String getCounterTrader() {
        return counterTrader;
    }

    
    /**
     * 设置 counterTrader
     * @param counterTrader counterTrader
     */
    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }

    
    /**
     * 获取 contactInfo
     * @return contactInfo.
     */
    public String getContactInfo() {
        return contactInfo;
    }

    
    /**
     * 设置 contactInfo
     * @param contactInfo contactInfo
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    
    /**
     * 获取 invManagerId
     * @return invManagerId.
     */
    public String getInvManagerId() {
        return invManagerId;
    }

    
    /**
     * 设置 invManagerId
     * @param invManagerId invManagerId
     */
    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }
    
    
}
