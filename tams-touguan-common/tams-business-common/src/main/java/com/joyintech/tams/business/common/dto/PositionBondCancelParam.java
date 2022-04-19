package com.joyintech.tams.business.common.dto;

public class PositionBondCancelParam extends PositionRuleHead{
    // 头寸引擎根节点标识
    private String interfaceType;

    // 委托或交易编号
    private String totalTradeId;

    // 指令或交易标识 ETR:指令,TRD:交易
    private String modelType;

    @Override
    public String getInterfaceType() {
        return interfaceType;
    }

    @Override
    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public String getTotalTradeId() {
        return totalTradeId;
    }

    @Override
    public void setTotalTradeId(String totalTradeId) {
        this.totalTradeId = totalTradeId;
    }

    @Override
    public String getModelType() {
        return modelType;
    }

    @Override
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
}
