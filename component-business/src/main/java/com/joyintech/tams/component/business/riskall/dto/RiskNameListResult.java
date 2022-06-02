package com.joyintech.tams.component.business.riskall.dto;

public class RiskNameListResult {

    /**
     * 资产或机构名称
     */
    private String name;

    /**
     * 关联名单类型
     */
    private String listDetailType;

    /**
     * 关联名单名称
     */
    private String listName;

    /**
     * 名单属性
     */
    private String listType;


    /**
     * 试算结果
     */
    private String rskStatus;

    /**
     * 试算消息
     */
    private String errorMsg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getListDetailType() {
        return listDetailType;
    }

    public void setListDetailType(String listDetailType) {
        this.listDetailType = listDetailType;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public String getRskStatus() {
        return rskStatus;
    }

    public void setRskStatus(String rskStatus) {
        this.rskStatus = rskStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
