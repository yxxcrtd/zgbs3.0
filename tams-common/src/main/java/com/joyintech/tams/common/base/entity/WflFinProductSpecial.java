package com.joyintech.tams.common.base.entity;

import java.util.Date;

/**
 * 描述:WFL_FIN_PRODUCT_SPECIAL表的实体类
 *
 * @author: zhuys
 * @创建时间: 2022-05-09
 */
public class WflFinProductSpecial {

    /**
     * 债券代码
     */
    private String finprodMarketId;

    /**
     * 是否资产支持证券
     */
    private String isAbs;

    /**
     * 是否特殊债
     */
    private String isSpecialDebt;

    /**
     * 是否到期违约债
     */
    private String isDefaultBondIndicator;

    /**
     * 特殊债标识录入方式
     */
    private String inputType;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建部门
     */
    private String createDept;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * null
     */
    private String updateUser;

    /**
     * null
     */
    private Date updateTime;

    /**
     * 工作流代码
     */
    private String workflowId;

    /**
     * 数据状态
     */
    private String workflowStatus;

    /**
     * 债券代码
     *
     * @return FINPROD_MARKET_ID 债券代码
     */
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    /**
     * 债券代码
     *
     * @param finprodMarketId 债券代码
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId == null ? null : finprodMarketId.trim();
    }

    /**
     * 是否资产支持证券
     *
     * @return IS_ABS 是否资产支持证券
     */
    public String getIsAbs() {
        return isAbs;
    }

    /**
     * 是否资产支持证券
     *
     * @param isAbs 是否资产支持证券
     */
    public void setIsAbs(String isAbs) {
        this.isAbs = isAbs == null ? null : isAbs.trim();
    }

    /**
     * 是否特殊债
     *
     * @return IS_SPECIAL_DEBT 是否特殊债
     */
    public String getIsSpecialDebt() {
        return isSpecialDebt;
    }

    /**
     * 是否特殊债
     *
     * @param isSpecialDebt 是否特殊债
     */
    public void setIsSpecialDebt(String isSpecialDebt) {
        this.isSpecialDebt = isSpecialDebt == null ? null : isSpecialDebt.trim();
    }

    /**
     * 是否到期违约债
     *
     * @return IS_DEFAULT_BOND_INDICATOR 是否到期违约债
     */
    public String getIsDefaultBondIndicator() {
        return isDefaultBondIndicator;
    }

    /**
     * 是否到期违约债
     *
     * @param isDefaultBondIndicator 是否到期违约债
     */
    public void setIsDefaultBondIndicator(String isDefaultBondIndicator) {
        this.isDefaultBondIndicator = isDefaultBondIndicator == null ? null : isDefaultBondIndicator.trim();
    }

    /**
     * 特殊债标识录入方式
     *
     * @return INPUT_TYPE 特殊债标识录入方式
     */
    public String getInputType() {
        return inputType;
    }

    /**
     * 特殊债标识录入方式
     *
     * @param inputType 特殊债标识录入方式
     */
    public void setInputType(String inputType) {
        this.inputType = inputType == null ? null : inputType.trim();
    }

    /**
     * 创建人
     *
     * @return CREATE_USER 创建人
     */
    public String getCreateUser() {
        return createUser;
    }

    /**
     * 创建人
     *
     * @param createUser 创建人
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    /**
     * 创建部门
     *
     * @return CREATE_DEPT 创建部门
     */
    public String getCreateDept() {
        return createDept;
    }

    /**
     * 创建部门
     *
     * @param createDept 创建部门
     */
    public void setCreateDept(String createDept) {
        this.createDept = createDept == null ? null : createDept.trim();
    }

    /**
     * 创建时间
     *
     * @return CREATE_TIME 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * null
     *
     * @return UPDATE_USER null
     */
    public String getUpdateUser() {
        return updateUser;
    }

    /**
     * null
     *
     * @param updateUser null
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    /**
     * null
     *
     * @return UPDATE_TIME null
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * null
     *
     * @param updateTime null
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 工作流代码
     *
     * @return WORKFLOW_ID 工作流代码
     */
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * 工作流代码
     *
     * @param workflowId 工作流代码
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId == null ? null : workflowId.trim();
    }

    /**
     * 数据状态
     *
     * @return WORKFLOW_STATUS 数据状态
     */
    public String getWorkflowStatus() {
        return workflowStatus;
    }

    /**
     * 数据状态
     *
     * @param workflowStatus 数据状态
     */
    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus == null ? null : workflowStatus.trim();
    }
}