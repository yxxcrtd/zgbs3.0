package com.joyintech.tams.cfets.manualsettle.dto;

import com.joyintech.tams.business.common.dto.PositionRuleHead;
import com.joyintech.tams.business.common.dto.PositionRulePtlData;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 *发送头寸报文共通属性
 */
public class CfetsManualSettlePositionDto extends PositionRuleHead {

    @ApiModelProperty(name="ptlList",value="组合代码集合",example="[字符串1,字符串2,...]")
    private List<PositionRulePtlData> ptlList;

    @ApiModelProperty(name="busiType",value="业务类型",example="1")
    private String busiType;

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="1")
    private String settleSpeed;

    @ApiModelProperty(name="tradePlace",value="交易场所",example="1")
    private String tradePlace;

    @ApiModelProperty(name="busiAct",value="业务动作",example="1")
    private String busiAct;

    @ApiModelProperty(name="ps",value="交易方向",example="1")
    private String ps;

    @ApiModelProperty(name="systemAct",value="系统动作",example="1")
    private String systemAct;

    @ApiModelProperty(name="cdate",value="日期",example="1")
    private Date cdate;

    @ApiModelProperty(name="ccy",value="日期",example="1")
    private String ccy;

    @ApiModelProperty(name="finprodId",value="债券代码",example="1")
    private String finprodId;

    @ApiModelProperty(name="bTotalTradeId",value="前次交易编号",example="1")
    private String bTotalTradeId;

    public String getWorkFlowId() {
        return workFlowId;
    }

    public void setWorkFlowId(String workFlowId) {
        this.workFlowId = workFlowId;
    }

    @ApiModelProperty(name="workFlowId",value="流程编号",example="01")
    private String workFlowId;

    public String getDecisionAct() {
        return decisionAct;
    }

    public void setDecisionAct(String decisionAct) {
        this.decisionAct = decisionAct;
    }

    @ApiModelProperty(name="decisionAct",value="决策动作",example="01")
    private String decisionAct;

    public List<PositionRulePtlData> getPtlList() {
        return ptlList;
    }

    public void setPtlList(List<PositionRulePtlData> ptlList) {
        this.ptlList = ptlList;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
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

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getbTotalTradeId() {
        return bTotalTradeId;
    }

    public void setbTotalTradeId(String bTotalTradeId) {
        this.bTotalTradeId = bTotalTradeId;
    }
}
