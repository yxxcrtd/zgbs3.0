/*
 * 文件名：FinBondTargetAssetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 中标结果维护信息DTO
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see CfetsPrimaryBidManageInsertRequest
 * @since 1.0
 */
@ApiModel(value="中标结果维护查询参数",description="中标结果维护查询参数")
public class CfetsPrimaryBidManageInsertRequest {

    @ApiModelProperty(name="bidId",value="投标编号(方法:getInitPageParams(编辑必填)、getBasicInfo(必填))")
    private String bidId;

    @ApiModelProperty(name="bidDate",value="中标日期(方法:getInitPageParams(编辑必填)、getBasicInfo(必填))")
    private String bidDate;

    @ApiModelProperty(name="workflowId",value="流程Id(方法:getInitPageParams(流程必填))")
    private String workflowId;

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getBidDate() {
        return bidDate;
    }

    public void setBidDate(String bidDate) {
        this.bidDate = bidDate;
    }
}
