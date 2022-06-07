package com.joyintech.tams.invest.common.risk.dto;

import com.joyintech.tams.common.base.entity.FinProduct;

public class RiskNamListCommonDto extends FinProduct {

    /**
     * 名单代码
     */
    private String listId;

    /**
     * 客户代码
     */
    private String customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 组织机构代码/统一社会信用代码
     */
    private String insitituteCode;

    /**
     * 名单主体类型，资产代码、托管人、
     */
    private String listDetailIdType;

    /**
     * 名单类型，白名单、黑名单
     */
    private String listType;

    /**
     * 关联名单名称
     */
    private String listName;

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getInsitituteCode() {
        return insitituteCode;
    }

    public void setInsitituteCode(String insitituteCode) {
        this.insitituteCode = insitituteCode;
    }

    public String getListDetailIdType() {
        return listDetailIdType;
    }

    public void setListDetailIdType(String listDetailIdType) {
        this.listDetailIdType = listDetailIdType;
    }
     
    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
