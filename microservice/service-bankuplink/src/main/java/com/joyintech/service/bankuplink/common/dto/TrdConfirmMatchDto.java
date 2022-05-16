/*
 * 文件名：CfetsTrdConfirmRegInfoListResponse.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.service.bankuplink.common.dto;



import java.util.Date;

/**
 * 银行间交易成交确认
 * 下行成交匹配关系Dto
 * @author 1
 * @version 1.0 2021年10月26日
 * @see TrdConfirmMatchDto
 * @since 1.0
 */
public class TrdConfirmMatchDto {
    /**
     * 流水号
     */
    private String seqno;
    /**
     * 下行交易编号
     */
    private String cstpTradeId;
    /**
     * 成交编号
     */
    private String exeId;
    /**
     * 报价编号/登记编号
     */
    private String preTradeId;
    /**
     * 匹配方式，手工匹配，自动匹配
     */
    private String matchType;
    /**
     * 匹配状态，已匹配
     */
    private String matchStatus;
    /**
     * 成交确认状态
     */
    private String trdConfStatus;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建部门
     */
    private String createDept;
    /**
     * 修改人
     */
    private String updateUser;
    /**
     * 修改时间
     */
    private Date updateTime;

    public String getSeqno() {
        return seqno;
    }

    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }

    public String getCstpTradeId() {
        return cstpTradeId;
    }

    public void setCstpTradeId(String cstpTradeId) {
        this.cstpTradeId = cstpTradeId;
    }

    public String getExeId() {
        return exeId;
    }

    public void setExeId(String exeId) {
        this.exeId = exeId;
    }

    public String getPreTradeId() {
        return preTradeId;
    }

    public void setPreTradeId(String preTradeId) {
        this.preTradeId = preTradeId;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getTrdConfStatus() {
        return trdConfStatus;
    }

    public void setTrdConfStatus(String trdConfStatus) {
        this.trdConfStatus = trdConfStatus;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateDept() {
        return createDept;
    }

    public void setCreateDept(String createDept) {
        this.createDept = createDept;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
