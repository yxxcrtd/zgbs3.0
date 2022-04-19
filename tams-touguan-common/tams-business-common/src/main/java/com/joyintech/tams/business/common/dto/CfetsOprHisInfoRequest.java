package com.joyintech.tams.business.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 *
 * 操作记录
 */
@ApiModel(value="操作记录请求参数",description="操作记录请求参数")
public class CfetsOprHisInfoRequest {

    @ApiModelProperty(name="seqNo",value="序号",example="1")
    private String seqNo;

    @ApiModelProperty(name="entrustId",value="序号",example="1")
    private String entrustId;

    @ApiModelProperty(name="operType",value="操作类型",example="1")
    private String operType;

    @ApiModelProperty(name="operDate",value="操作日期",example="1")
    private Date operDate;

    @ApiModelProperty(name="operTime",value="操作时间",example="1")
    private Date operTime;

    @ApiModelProperty(name="busiDes",value="业务说明",example="1")
    private String busiDes;

    @ApiModelProperty(name="userId",value="操作员",example="1")
    private String userId;

    @ApiModelProperty(name="createUser",value="创建人",example="1")
    private String createUser;

    @ApiModelProperty(name="createDept",value="创建部门",example="1")
    private String createDept;

    @ApiModelProperty(name="createTime",value="创建时间",example="1")
    private Date createTime;

    @ApiModelProperty(name="updateUser",value="更新人",example="1")
    private String updateUser;

    @ApiModelProperty(name="updateTime",value="更新时间",example="1")
    private Date updateTime;

    @ApiModelProperty(name="statisticalTimes",value="导出次数")
    private int statisticalTimes;

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    public String getBusiDes() {
        return busiDes;
    }

    public void setBusiDes(String busiDes) {
        this.busiDes = busiDes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateDept() {
        return createDept;
    }

    public void setCreateDept(String createDept) {
        this.createDept = createDept;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public int getStatisticalTimes() {
        return statisticalTimes;
    }

    public void setStatisticalTimes(int statisticalTimes) {
        this.statisticalTimes = statisticalTimes;
    }
}
