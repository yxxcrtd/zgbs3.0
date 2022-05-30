/*
 * 文件名：EtrBaseDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.etr.bondfirstbid.dto;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;
import com.joyintech.background.component.common.pack.common.validation.annotation.NotEmpty;
import com.joyintech.background.component.order.exchange.common.dto.EtrDealDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrExecuteDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrRecDto;

import java.util.Date;
import java.util.List;

/**
 * 投资指令Dto
 * 
 * @author zhangrui1
 * @version 1.0 2020年7月8日
 * @see EtrBaseDto
 * @since 1.0
 */
public class EtrBaseDto extends BaseEntity {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 3258253187839963460L;

    /**
     * 指令编号
     */
    private String entrustId;

    /**
     * 指令日期
     */
    private Date entrustDate;

    /**
     * 指令开始日期
     */
    private Date vdate;

    /**
     * 指令结束日期
     */
    private Date mdate;

    /**
     * 指令状态
     */
    private String entrustStatus;

    /**
     * 提交人
     */
    private String invManagerId;

    /**
     * 指令类型
     */
    private String entrustClass;

    /**
     * 指令业务类型
     */
    private String entrustType;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 投资指令接收信息
     */
    private List<EtrRecDto> orderRecList;

    /**
     * 指令执行明细
     */
    private List<EtrExecuteDto> etrExecuteList;

    /**
     * 指令成交明细
     */
    private List<EtrDealDto> etrDealList;

    /**
     * 原更新时间
     */
    private Date oldUpdateTime;

    /**
     * 附属表到期日
     */
    private Date addMdate;

    public EtrBaseDto() {
        // nothing
    }

    /**
     * @param entrustType 指令业务类型
     * @param entrustClass 指令类型
     */
    protected EtrBaseDto(String entrustType, String entrustClass) {
        setEntrustType(entrustType);
        setEntrustClass(entrustClass);
    }

    /**
     * 取得 指令编号
     * 
     * @return 指令编号
     */
    public String getEntrustId() {
        return this.entrustId;
    }

    /**
     * 设置 指令编号
     * 
     * @param entrustId 指令编号
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    /**
     * 取得 指令开始日期
     * 
     * @return 指令开始日期
     */
    @NotEmpty(fieldName = "EtrComponent.vdate")
    public Date getVdate() {
        return this.vdate;
    }

    /**
     * 设置 指令开始日期
     * 
     * @param vdate 指令开始日期
     */
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 指令结束日期
     * 
     * @return 指令结束日期
     */
    @NotEmpty(fieldName = "EtrComponent.vdate")
    public Date getMdate() {
        return this.mdate;
    }

    /**
     * 设置 指令结束日期
     * 
     * @param mdate 指令结束日期
     */
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 指令状态
     * 
     * @return 指令状态
     */
    public String getEntrustStatus() {
        return this.entrustStatus;
    }

    /**
     * 设置 指令状态
     * 
     * @param entrustStatus 指令状态
     */
    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    /**
     * 取得 提交人
     * 
     * @return 提交人
     */
    public String getInvManagerId() {
        return this.invManagerId;
    }

    /**
     * 设置 提交人
     * 
     * @param invManagerId 提交人
     */
    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    /**
     * 获取 指令类型
     * 
     * @return 指令类型.
     */
    public String getEntrustClass() {
        return entrustClass;
    }

    /**
     * 设置 指令类型
     * 
     * @param orderType 指令类型
     */
    public void setEntrustClass(String orderType) {
        this.entrustClass = orderType;
    }

    /**
     * 获取 指令业务类型
     * 
     * @return 指令业务类型.
     */
    public String getEntrustType() {
        return entrustType;
    }

    /**
     * 设置 指令业务类型
     * 
     * @param entrustType 指令业务类型
     */
    public void setEntrustType(String entrustType) {
        this.entrustType = entrustType;
    }

    /**
     * 获取 投资指令接收信息
     * 
     * @return 投资指令接收信息.
     */
    public List<EtrRecDto> getOrderRecList() {
        return orderRecList;
    }

    /**
     * 设置 投资指令接收信息
     * 
     * @param orderRecList 投资指令接收信息
     */
    public void setOrderRecList(List<EtrRecDto> orderRecList) {
        this.orderRecList = orderRecList;
    }

    /**
     * 获取 指令执行明细
     * 
     * @return 指令执行明细.
     */
    public List<EtrExecuteDto> getEtrExecuteList() {
        return etrExecuteList;
    }

    /**
     * 设置 指令执行明细
     * 
     * @param etrExecuteList 指令执行明细
     */
    public void setEtrExecuteList(List<EtrExecuteDto> etrExecuteList) {
        this.etrExecuteList = etrExecuteList;
    }

    /**
     * 获取 指令成交明细
     * 
     * @return 指令成交明细.
     */
    public List<EtrDealDto> getEtrDealList() {
        return etrDealList;
    }

    /**
     * 设置 指令成交明细
     * 
     * @param etrDealList 指令成交明细
     */
    public void setEtrDealList(List<EtrDealDto> etrDealList) {
        this.etrDealList = etrDealList;
    }

    /**
     * 获取 原更新时间
     * 
     * @return 原更新时间.
     */
    public Date getOldUpdateTime() {
        return oldUpdateTime;
    }

    /**
     * 设置 原更新时间
     * 
     * @param oldUpdateTime 原更新时间
     */
    public void setOldUpdateTime(Date oldUpdateTime) {
        this.oldUpdateTime = oldUpdateTime;
    }

    /**
     * 获取 指令日期
     * 
     * @return 指令日期.
     */
    public Date getEntrustDate() {
        return entrustDate;
    }

    /**
     * 设置 指令日期
     * 
     * @param entrustDate 指令日期
     */
    public void setEntrustDate(Date entrustDate) {
        this.entrustDate = entrustDate;
    }

    /**
     * 获取 目标类型
     * 
     * @return 目标类型.
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * 设置 目标类型
     * 
     * @param targetType 目标类型
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Date getAddMdate() {
        return addMdate;
    }

    public void setAddMdate(Date addMdate) {
        this.addMdate = addMdate;
    }
}
