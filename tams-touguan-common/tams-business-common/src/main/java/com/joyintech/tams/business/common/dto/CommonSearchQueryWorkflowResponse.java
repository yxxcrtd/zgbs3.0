package com.joyintech.tams.business.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 审批信息查询
 * 指令明细请求参数DTO
 * @author sjj
 * @version 1.0 2021年10月29日
 * @see CommonSearchQueryWorkflowResponse
 * @since 1.0
 */
@ApiModel(value="审批信息返回参数",description="审批信息返回参数")
public class CommonSearchQueryWorkflowResponse {
    @ApiModelProperty(name="seqNo",value="序号",example="A1",required = true)
    private String seqNo;

    @ApiModelProperty(name="operType",value="操作类型",example="A1",required = true)
    private String operType;

    @ApiModelProperty(name="operDate",value="审批日期",example="A1",required = true)
    private Date operDate;

    @ApiModelProperty(name="operTime",value="审批时间",example="A1",required = true)
    private String operTime;

    @ApiModelProperty(name="nodeId",value="审批节点Id",example="A1",required = true)
    private String nodeId;

    @ApiModelProperty(name="nodeName",value="审批节点",example="A1",required = true)
    private String nodeName;

    @ApiModelProperty(name="hiUser",value="审批人",example="A1",required = true)
    private String hiUser;

    @ApiModelProperty(name="operStatus",value="操作状态",example="A1",required = true)
    private String operStatus;

    @ApiModelProperty(name="opinions",value="处理意见",example="A1",required = true)
    private String opinions;

    private byte []msg;

    @ApiModelProperty(name="tradeStatus",value="流程状态",example="A1",required = true)
    private String tradeStatus;

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
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

    public String getOperTime() {
        return operTime;
    }

    public void setOperTime(String operTime) {
        this.operTime = operTime;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getHiUser() {
        return hiUser;
    }

    public void setHiUser(String hiUser) {
        this.hiUser = hiUser;
    }

    public String getOperStatus() {
        return operStatus;
    }

    public void setOperStatus(String operStatus) {
        this.operStatus = operStatus;
    }

    public String getOpinions() {
        if(this.msg != null) {
            return new String(this.msg);
        }
        return opinions;
    }

    public void setOpinions(String opinions) {
        this.opinions = opinions;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }


}

