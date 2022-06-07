package com.joyintech.tams.cfets.etr.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 头寸信息查询，请求参数
 */
@ApiModel(value="新建指令发送头寸请求参数",description="新建指令发送头寸请求参数")
public class CfetsEtrIbPositionRequest {
    @ApiModelProperty(name="interfaceType",value="报文类型",example="1")
    private String interfaceType;

    @ApiModelProperty(name="userId",value="用户名",example="1")
    private String userId;

    @ApiModelProperty(name="computerId",value="应答队列",example="1")
    private String computerId;

    @ApiModelProperty(name="totalTradeId",value="汇总交易编号",example="1")
    private String totalTradeId;

    @ApiModelProperty(name="flowStatus",value="流程状态",example="1")
    private String flowStatus;

    @ApiModelProperty(name="modelType",value="指令或交易标识",example="1")
    private String modelType;

    @ApiModelProperty(name="balanceCheck",value="余额校验",example="1")
    private String balanceCheck;

    @ApiModelProperty(name="busiType",value="业务类型",example="1")
    private String busiType;

    @ApiModelProperty(name="deliveryType",value="结算方式",example="1")
    private String deliveryType;

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="1")
    private String settleSpeed;



    @ApiModelProperty(name="purchModle",value="申购方式",example="1")
    private String purchModle;

    @ApiModelProperty(name="tradePlace",value="交易场所",example="1")
    private String tradePlace;

    @ApiModelProperty(name="busiAct",value="业务动作",example="1")
    private String busiAct;

    @ApiModelProperty(name="workFlowId",value="流程编号",example="01")
    private String workFlowId;

    @ApiModelProperty(name="ps",value="交易方向",example="1")
    private String ps;

    @ApiModelProperty(name="decisionAct",value="决策动作",example="01")
    private String decisionAct;

    @ApiModelProperty(name="systemAct",value="系统动作",example="1")
    private String systemAct;

    @ApiModelProperty(name="cdate",value="日期",example="1")
    private Date cdate;

    @ApiModelProperty(name="ccy",value="日期",example="1")
    private String ccy;

    @ApiModelProperty(name="finprodId",value="债券代码",example="1")
    private String finprodId;


    @ApiModelProperty(name="bidResult",value="投标结果",example="1")
    private String bidResult;


    @ApiModelProperty(name="bTotalTradeId",value="前次交易编号",example="1")
    private String bTotalTradeId;

    @ApiModelProperty(name="ptlList",value="组合代码集合",example="[字符串1,字符串2,...]")
    private List<CfetsEtrIbPosPortfolioRequest> ptlList;


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

    public String getComputerId() {
        return computerId;
    }

    public void setComputerId(String computerId) {
        this.computerId = computerId;
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

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }
    public String getPurchModle() {
        return purchModle;
    }

    public void setPurchModle(String purchModle) {
        this.purchModle = purchModle;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    public String getBusiAct() {
        return busiAct;
    }

    public void setBusiAct(String busiAct) {
        this.busiAct = busiAct;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }



    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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


    public List<CfetsEtrIbPosPortfolioRequest> getPtlList() {
        return ptlList;
    }

    public void setPtlList(List<CfetsEtrIbPosPortfolioRequest> ptlList) {
        this.ptlList = ptlList;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    public String getDecisionAct() {
        return decisionAct;
    }

    public void setDecisionAct(String decisionAct) {
        this.decisionAct = decisionAct;
    }

    public String getWorkFlowId() {
        return workFlowId;
    }

    public void setWorkFlowId(String workFlowId) {
        this.workFlowId = workFlowId;
    }
}
