package com.joyintech.tams.exchange.common.bean;

import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModelProperty;

/**
 * @ClassName TrdExchange002
 * @Description TODO
 * @Author swwan
 * @Date 2020/6/28 9:59
 * @Version 1.0
 **/
//@PageId("TrdExchange002")
public class TrdExchangeDto extends IntegratedEntrust implements ProcessEntity {

    @ApiModelProperty(required = true, name = "orderID", value = "订单编号")
    private String orderID;

    @ApiModelProperty(required = true, name = "operateType", value = "操作类型")
    private String operateType;

    @ApiModelProperty(required = true, name = "pageModel", value = "画面模式")
    private String pageModel;

    @ApiModelProperty(required = true, name = "workflowStatus", value = "工作流状态")
    private String workflowStatus;

    @ApiModelProperty(required = true, name = "users", value = "审批人")
    private String users;

    @ApiModelProperty(required = true, name = "roles", value = "审批角色")
    private String roles;

    @ApiModelProperty(required = true, name = "operType", value = "操作类型")
    private String operType;

    @ApiModelProperty(required = true, name = "pageId", value = "页面ID")
    private String pageId;

    @ApiModelProperty(required = true, name = "workflowParameter", value = "工作流参数")
    private String workflowParameter;

    @ApiModelProperty(required = true, name = "elementId", value = "元素ID")
    private String elementId;

    @ApiModelProperty(required = true, name = "submit", value = "提交标识")
    private String submit;


    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public String getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getWorkflowParameter() {
        return workflowParameter;
    }

    @Override
    public void setWorkflowParameter(String workflowParameter) {
        this.workflowParameter = workflowParameter;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getSubmit() {
        return submit;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }

    public String getOffsetFlag() {
        return null;
    }

    public void setOffsetFlag(String offsetFlag) {

    }

    public String getHedgeFlag() {
        return null;
    }

    public void setHedgeFlag(String hedgeFlag) {

    }

}
