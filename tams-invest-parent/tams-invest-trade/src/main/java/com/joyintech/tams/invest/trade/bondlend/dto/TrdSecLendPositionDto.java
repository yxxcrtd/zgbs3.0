package com.joyintech.tams.invest.trade.bondlend.dto;

import java.util.Date;
import java.util.List;

public class TrdSecLendPositionDto {

    private String busiType;//业务类型

    private String busiAct;//业务动作

    private String ps;//交易方向

    private String tradePlace;//交易场所

    private String settleSpeed;//清算速度

    private String decisionAct;//决策动作

    private String systemAct;//系统动作

    private String workFlowId;//流程编号

    private String isCashSettle;//是否现金交割

    private Date cdate;//日期

    private String ccy;//币种

    private String totalTradeId;//汇总交易编号

    private String bTotalTradeId;//前次交易编号

    private String flowStatus;//流程状态

    private String modelType;//指令或交易标识

    private String balanceCheck;//是否校验余额

    private String interfaceType;//报文id

    private String userId;//当前用户

    private List<TrdSecLendCashPositionDto> ptlCashList; //组合数据集

    private List<TrdSecLendSecPositionDto> ptlTargetList; //标的券数据集

    private List<TrdSecLendSecPositionDto> ptlSecList; //质押券数据集

    private List<TrdSecLendSecPositionDto> ptlRelieveSecList; //解除质押券数据集

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
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

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getDecisionAct() {
        return decisionAct;
    }

    public void setDecisionAct(String decisionAct) {
        this.decisionAct = decisionAct;
    }

    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }

    public String getWorkFlowId() {
        return workFlowId;
    }

    public void setWorkFlowId(String workFlowId) {
        this.workFlowId = workFlowId;
    }

    public String getIsCashSettle() {
        return isCashSettle;
    }

    public void setIsCashSettle(String isCashSettle) {
        this.isCashSettle = isCashSettle;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getTotalTradeId() {
        return totalTradeId;
    }

    public void setTotalTradeId(String totalTradeId) {
        this.totalTradeId = totalTradeId;
    }

    public String getbTotalTradeId() {
        return bTotalTradeId;
    }

    public void setbTotalTradeId(String bTotalTradeId) {
        this.bTotalTradeId = bTotalTradeId;
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

    public List<TrdSecLendCashPositionDto> getPtlCashList() {
        return ptlCashList;
    }

    public void setPtlCashList(List<TrdSecLendCashPositionDto> ptlCashList) {
        this.ptlCashList = ptlCashList;
    }

    public List<TrdSecLendSecPositionDto> getPtlTargetList() {
        return ptlTargetList;
    }

    public void setPtlTargetList(List<TrdSecLendSecPositionDto> ptlTargetList) {
        this.ptlTargetList = ptlTargetList;
    }

    public List<TrdSecLendSecPositionDto> getPtlSecList() {
        return ptlSecList;
    }

    public void setPtlSecList(List<TrdSecLendSecPositionDto> ptlSecList) {
        this.ptlSecList = ptlSecList;
    }

    public List<TrdSecLendSecPositionDto> getPtlRelieveSecList() {
        return ptlRelieveSecList;
    }

    public void setPtlRelieveSecList(List<TrdSecLendSecPositionDto> ptlRelieveSecList) {
        this.ptlRelieveSecList = ptlRelieveSecList;
    }
}
