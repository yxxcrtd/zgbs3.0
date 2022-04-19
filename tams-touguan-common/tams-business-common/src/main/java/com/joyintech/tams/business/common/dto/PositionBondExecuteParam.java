package com.joyintech.tams.business.common.dto;

import java.util.Date;
import java.util.List;

public class PositionBondExecuteParam extends PositionRuleHead{
    
    private String busiAct; // 业务行为 投资指令，交易登记，汇总确认，中标结果维护

    private String deliveryType; // 结算方式

    private String settleSpeed; // 清算速度

    private String purchModle; // 申购方式

    private String tradePlace; // 交易场所

    private String type2; // 证券二级分类

    private String type3; // 证券三级分类

    private String type4; // 证券四级分类

    private String type5; // 证券五极分类

    private Date cdate; // 日期

    private String ccy; // 币种

    private String busiType; // 业务类型 债券一级投标,现券交易,债券行权交易,债券存续期交易

    private String ps; // 交易方向
    
    private String systemAct; // 系统动作
    
    private String finprodId; // 
    
    private List<PositionRulePtlData> ptlList; // 组合数据集

    public String getBusiType() {
        return busiType;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getType3() {
        return type3;
    }

    public void setType3(String type3) {
        this.type3 = type3;
    }

    public String getType4() {
        return type4;
    }

    public void setType4(String type4) {
        this.type4 = type4;
    }

    public String getType5() {
        return type5;
    }

    public void setType5(String type5) {
        this.type5 = type5;
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

    public List<PositionRulePtlData> getPtlList() {
        return ptlList;
    }

    public void setPtlList(List<PositionRulePtlData> ptlList) {
        this.ptlList = ptlList;
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
}
