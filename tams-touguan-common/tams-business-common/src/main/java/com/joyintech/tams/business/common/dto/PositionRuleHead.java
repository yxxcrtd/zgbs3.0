package com.joyintech.tams.business.common.dto;

public class PositionRuleHead {
    
    // 异常代码 出现超时或其它异常时，撤销 "err001   投管处理异常
    // "err001   投管处理异常     err002   超时异常"
    private String errorCode;
    
    // 头寸引擎根节点标识
    private String interfaceType;
    
    // 当前操作用户
    private String userId;
    
    // 委托或交易编号
    private String totalTradeId;

    // 流程状态
    private String flowStatus;
    
    // 指令或交易标识 ETR:指令,TRD:交易
    private String modelType;
    
    // 余额校验 Y：需校验   N：不需校验
    private String balanceCheck;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTotalTradeId() {
        return totalTradeId;
    }

    public void setTotalTradeId(String totalTradeId) {
        this.totalTradeId = totalTradeId;
    }

    public String getFlowStatus() {
        return flowStatus;
    }

    public void setFlowStatus(String flowStatus) {
        this.flowStatus = flowStatus;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getBalanceCheck() {
        return balanceCheck;
    }

    public void setBalanceCheck(String balanceCheck) {
        this.balanceCheck = balanceCheck;
    }
    
}
