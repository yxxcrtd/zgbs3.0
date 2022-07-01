package com.joyintech.tams.business.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 备注信息实体类
 */
@ApiModel(value = "备注信息实体类", description = "备注信息实体类")
public class EtrEntrustOperHisDto {
    @ApiModelProperty(name = "entrustId", value = "序号", example = "1")
    private String entrustId;

    @ApiModelProperty(name = "operType", value = "操作类型", example = "1")
    private String operType;

    @ApiModelProperty(name = "operDate", value = "操作日期", example = "1")
    private String operDate;

    @ApiModelProperty(name = "operTime", value = "操作时间", example = "1")
    private String operTime;

    @ApiModelProperty(name = "busiDes", value = "业务说明", example = "1")
    private String busiDes;

    @ApiModelProperty(name = "userId", value = "操作员", example = "1")
    private String userId;

    @ApiModelProperty(name = "userName", value = "操作员名称", example = "1")
    private String userName;

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

    public String getOperDate() {
        return operDate;
    }

    public void setOperDate(String operDate) {
        this.operDate = operDate;
    }

    public String getOperTime() {
        return operTime;
    }

    public void setOperTime(String operTime) {
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
