package com.joyintech.service.trade.exchange.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

/**
 * @ClassName TrdExchangeEntrustDto
 * @Description 普通委托复核工作流Dto
 * @Author swwan
 * @Date 2021/02/24 16:11
 * @Version 1.0
 **/
@PageId("TrdExchange002")
public class TrdExchangeEntrustDto extends BaseEntity implements ProcessEntity {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 3680516262295636527L;

    private String eventId;

    private java.util.List<Entrust> entrustList;

    private OrderPlaceBean orderPlaceBean;

    private String portfolioId;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 画面模式
     */
    private String pageModel;

    /**
     * 工作流状态
     */
    private String workflowStatus;

    /**
     * 审批人
     */
    private String users;

    /**
     * 审批角色
     */
    private String roles;

    /**
     * 操作类型
     */
    private String operType;

    /**
     * 页面ID
     */
    private String pageId;

    /**
     * 工作流参数
     */
    private String workflowParameter;

    /**
     * 元素ID
     */
    private String elementId;

    /**
     * 提交标识
     */
    private String submit;

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public java.util.List<Entrust> getEntrustList() {
        return entrustList;
    }

    public void setEntrustList(java.util.List<Entrust> entrustList) {
        this.entrustList = entrustList;
    }

    public OrderPlaceBean getOrderPlaceBean() {
        return orderPlaceBean;
    }

    public void setOrderPlaceBean(OrderPlaceBean orderPlaceBean) {
        this.orderPlaceBean = orderPlaceBean;
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
}
