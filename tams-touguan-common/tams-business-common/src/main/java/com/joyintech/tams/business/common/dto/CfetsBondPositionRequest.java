package com.joyintech.tams.business.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 发送债券头寸信息，参数
 */
@ApiModel(value = "新建银行间现券发送头寸请求参数", description = "新建银行间现券发送头寸请求参数")
public class CfetsBondPositionRequest {

    @ApiModelProperty(name = "ps", value = "交易方向 必填,参数字典 PS", example = "03")
    private String ps;

    @ApiModelProperty(name = "purchModle", value = "申购方式,参数字典 PURCH_MODLE", example = "03")
    private String purchModle;

    @ApiModelProperty(name = "totalTradeId", value = "汇总交易编号 必填", example = "1234567")
    private String totalTradeId;

    @ApiModelProperty(name = "interfaceType", value = "报文类型 必填", example = "BOND001")
    private String interfaceType;

    @ApiModelProperty(name = "payType", value = "缴款方式,参数字典 PAY_TYPE", example = "01")
    private String payType;

    @ApiModelProperty(name = "bidResult", value = "投标结果,参数字典 YES_OR_NO", example = "Y")
    private String bidResult;

    @ApiModelProperty(name = "bTotalTradeId", value = "前次交易编号 必填", example = "1")
    private String bTotalTradeId;

    @ApiModelProperty(name = "ccy", value = "币种 必填,参数字典 CCY", example = "CNY")
    private String ccy;

    @ApiModelProperty(name = "busiType", value = "业务类型 必填,参数字典 BUSI_TYPE_POSITION", example = "01")
    private String busiType;

    @ApiModelProperty(name = "balanceCheck", value = "余额校验 必填,参数字典 YES_OR_NO", example = "Y")
    private String balanceCheck;

    @ApiModelProperty(name = "decisionAct", value = "决策动作 必填,参数字典 SYSTEM_ACT", example = "01")
    private String decisionAct;

    @ApiModelProperty(name = "ptlList", value = "组合数据集")
    private List<CfetsBondPortfolioCashRequest> ptlList;

    @ApiModelProperty(name = "busiAct", value = "业务动作 必填,参数字典 BUSI_ACT", example = "01")
    private String busiAct;

    @ApiModelProperty(name = "modelType", value = "指令或交易标识 必填", example = "ETR")
    private String modelType;

    @ApiModelProperty(name = "userId", value = "用户名 必填", example = "wp")
    private String userId;

    @ApiModelProperty(name = "systemAct", value = "系统动作  必填,参数字典 SYSTEM_ACT", example = "01")
    private String systemAct;

    @ApiModelProperty(name = "deliveryType", value = "结算方式,参数字典 DELIVERY_TYPE", example = "01")
    private String deliveryType;

    @ApiModelProperty(name = "computerId", value = "应答队列  必填", example = "1")
    private String computerId;

    @ApiModelProperty(name = "cdate", value = "日期  必填", example = "2021-10-10")
    private Date cdate;

    @ApiModelProperty(name = "tradePlace", value = "交易场所,参数字典 TRADE_PLACE", example = "01")
    private String tradePlace;

    @ApiModelProperty(name = "sharesTradePlatform", value = "交易系统,参数字典 SHARES_TRADE_SYSTEM", example = "01")
    private String sharesTradePlatform;

    @ApiModelProperty(name = "finprodId", value = "债券代码  必填", example = "F01_001.IB")
    private String finprodId;

    @ApiModelProperty(name = "flowStatus", value = "流程状态  必填,参数字典 FLOW_STATUS", example = "01")
    private String flowStatus;

    @ApiModelProperty(name = "settleSpeed", value = "清算速度,参数字典 ACCOUNT_SPEED", example = "01")
    private String settleSpeed;

    @ApiModelProperty(name = "workFlowId", value = "流程编号", example = "01")
    private String workFlowId;

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPurchModle() {
        return purchModle;
    }

    public void setPurchModle(String purchModle) {
        this.purchModle = purchModle;
    }

    public String getTotalTradeId() {
        return totalTradeId;
    }

    public void setTotalTradeId(String totalTradeId) {
        this.totalTradeId = totalTradeId;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getBidResult() {
        return bidResult;
    }

    public void setBidResult(String bidResult) {
        this.bidResult = bidResult;
    }

    public String getbTotalTradeId() {
        return bTotalTradeId;
    }

    public void setbTotalTradeId(String bTotalTradeId) {
        this.bTotalTradeId = bTotalTradeId;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getBalanceCheck() {
        return balanceCheck;
    }

    public void setBalanceCheck(String balanceCheck) {
        this.balanceCheck = balanceCheck;
    }

    public String getDecisionAct() {
        return decisionAct;
    }

    public void setDecisionAct(String decisionAct) {
        this.decisionAct = decisionAct;
    }

    public List<CfetsBondPortfolioCashRequest> getPtlList() {
        return ptlList;
    }

    public void setPtlList(List<CfetsBondPortfolioCashRequest> ptlList) {
        this.ptlList = ptlList;
    }

    public String getBusiAct() {
        return busiAct;
    }

    public void setBusiAct(String busiAct) {
        this.busiAct = busiAct;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getComputerId() {
        return computerId;
    }

    public void setComputerId(String computerId) {
        this.computerId = computerId;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    public String getSharesTradePlatform() {
        return sharesTradePlatform;
    }

    public void setSharesTradePlatform(String sharesTradePlatform) {
        this.sharesTradePlatform = sharesTradePlatform;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFlowStatus() {
        return flowStatus;
    }

    public void setFlowStatus(String flowStatus) {
        this.flowStatus = flowStatus;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getWorkFlowId() {
        return workFlowId;
    }

    public void setWorkFlowId(String workFlowId) {
        this.workFlowId = workFlowId;
    }
}
