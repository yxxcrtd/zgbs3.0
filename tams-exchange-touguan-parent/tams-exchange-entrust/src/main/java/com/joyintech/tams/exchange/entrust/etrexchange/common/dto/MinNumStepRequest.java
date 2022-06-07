package com.joyintech.tams.exchange.entrust.etrexchange.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="查询最小交易单元请求参数",description="查询最小交易单元请求参数")
public class MinNumStepRequest {

    /**
     * 资产代码
     */
    @ApiModelProperty(name="finprodId",value="资产代码",example="A1")
    private String finprodId;

    /**
     * 交易平台
     */
    @ApiModelProperty(name="currentTab",value="交易平台:竞价交易:jj,大宗交易指令:dz,协议现券指令:gd (场内现券指令传'指定交易方式'- 0:匹配成交,F19:协商成交)",example="A1")
    private String currentTab;

    /**
     * 市场代码
     */
    @ApiModelProperty(name="tradeMarket",value="资产代码",example="A1")
    private String tradeMarket;

    /**
     * 资产代码
     */
    @ApiModelProperty(name="finprodType2",value="资产类型",example="A1")
    private String finprodType2;

    /**
     * 资产代码
     */
    @ApiModelProperty(name="ps",value="委托方向",example="A1")
    private String ps;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(String currentTab) {
        this.currentTab = currentTab;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }
}
