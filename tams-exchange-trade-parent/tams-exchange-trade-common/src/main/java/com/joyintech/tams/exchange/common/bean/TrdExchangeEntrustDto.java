package com.joyintech.tams.exchange.common.bean;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.util.List;

/**
 * @ClassName TrdExchangeApproveDto
 * @Description TODO
 * @Author swwan
 * @Date 2020/8/3 16:11
 * @Version 1.0
 **/
@PageId("TrdExchange002")
public class TrdExchangeEntrustDto extends BaseEntity implements ProcessEntity {

    private String eventId;

    private List<Entrust> entrustList;

    private OrderPlaceBean orderPlaceBean;

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
    private OperationType operType;

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

    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public List<Entrust> getEntrustList() {
        return entrustList;
    }

    public void setEntrustList(List<Entrust> entrustList) {
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

    public OperationType getOperType() {
        return operType;
    }

    public void setOperType(OperationType operType) {
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
