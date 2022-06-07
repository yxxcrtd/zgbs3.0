package com.joyintech.tams.cfets.etr.bond.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 银行间债券指令，持仓信息
 * @author chengzhong
 * @version 1.0 2021年1月19日
 * @see CfetsEtrIbBondinfo001Dto
 * @since 1.0
 */
@ApiModel(value="债券指令持仓信息返回数据",description="债券指令持仓信息返回数据")
public class CfetsEtrIbBondPosinfo001Dto{


    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="名称A1")
    private String portfolioName;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="secType1",value="债券分类Code",example="分类1Code")
    private String secType1;

    @ApiModelProperty(name="secType1Name",value="债券分类",example="分类1名称")
    private String secType1Name;

    @ApiModelProperty(name="busiModel",value="业务模式Code",example="业务模式1Code")
    private String busiModel;

    @ApiModelProperty(name="busiModelStr",value="业务模式",example="业务模式1名称")
    private String busiModelStr;

    @ApiModelProperty(name="etrPledgePrin",value="指令可卖出持仓",example="10000.99")
    private BigDecimal etrPledgePrin;

    @ApiModelProperty(name="etrAvaPledgePrin",value="指令可质押持仓",example="10000.99")
    private BigDecimal etrAvaPledgePrin;

    @ApiModelProperty(name="etrAlrPledgePrin",value="指令已质押持仓",example="10000.99")
    private BigDecimal etrAlrPledgePrin;

    @ApiModelProperty(name="trdAvaShareAmt",value="交易可用券面总额",example="10000.99")
    private BigDecimal trdAvaShareAmt;

    @ApiModelProperty(name="trdIntransShareAmt",value="交易在途券面总额",example="10000.99")
    private BigDecimal trdIntransShareAmt;

    @ApiModelProperty(name="positionsAmt",value="持仓成本（万元）",example="10000.99")
    private BigDecimal positionsAmt;

    @ApiModelProperty(name="unitCost",value="持仓成本价",example="99.9999")
    private BigDecimal unitCost;

    @ApiModelProperty(name="price",value="最新净价",example="99.9999")
    private BigDecimal price;

    @ApiModelProperty(name="positionsPrice",value="持仓市值（万元）",example="99.99")
    private BigDecimal positionsPrice;

    @ApiModelProperty(name="floatingPl",value="浮动盈亏（万元）",example="99.99")
    private BigDecimal floatingPl;

    @ApiModelProperty(name="finprodId",value="资产代码id",example="ZC1")
    private String finprodId;

    @ApiModelProperty(name="accountPosition",value="账户持仓",example="99.99")
    private BigDecimal accountPosition;

    public BigDecimal getAccountPosition() {
        return accountPosition;
    }

    public void setAccountPosition(BigDecimal accountPosition) {
        this.accountPosition = accountPosition;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getSecType1() {
        return secType1;
    }

    public void setSecType1(String secType1) {
        this.secType1 = secType1;
    }

    public String getSecType1Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, getSecType1());        
    }

    public void setSecType1Name(String secType1Name) {
        this.secType1Name = secType1Name;
    }

    public String getBusiModel() {
        return busiModel;
    }

    public void setBusiModel(String busiModel) {
        this.busiModel = busiModel;
    }
    
    public String getBusiModelStr() {
        String str = getBusiModel();
        if (StringUtil.isNotEmpty(str)) {
            if (StringUtil.contains(str, ",")){
                String[] arr = str.split(",");
                for (int i=0; i<arr.length; i++){
                    String each = arr[i];
                    String rsStr = SysDataUtil.getNameFromDataDict(DictCode.业务模式, each);
                    str = str.replace(each, rsStr);
                }
            }else{
                return SysDataUtil.getNameFromDataDict(DictCode.业务模式, getBusiModel());
            }
        }
        return str;
    }

    public void setBusiModelStr(String busiModelStr) {
        this.busiModelStr = busiModelStr;
    }

    public BigDecimal getEtrPledgePrin() {
        return etrPledgePrin;
    }

    public void setEtrPledgePrin(BigDecimal etrPledgePrin) {
        this.etrPledgePrin = etrPledgePrin;
    }

    public BigDecimal getEtrAvaPledgePrin() {
        return etrAvaPledgePrin;
    }

    public void setEtrAvaPledgePrin(BigDecimal etrAvaPledgePrin) {
        this.etrAvaPledgePrin = etrAvaPledgePrin;
    }

    public BigDecimal getEtrAlrPledgePrin() {
        return etrAlrPledgePrin;
    }

    public void setEtrAlrPledgePrin(BigDecimal etrAlrPledgePrin) {
        this.etrAlrPledgePrin = etrAlrPledgePrin;
    }

    public BigDecimal getTrdAvaShareAmt() {
        return trdAvaShareAmt;
    }

    public void setTrdAvaShareAmt(BigDecimal trdAvaShareAmt) {
        this.trdAvaShareAmt = trdAvaShareAmt;
    }

    public BigDecimal getTrdIntransShareAmt() {
        return trdIntransShareAmt;
    }

    public void setTrdIntransShareAmt(BigDecimal trdIntransShareAmt) {
        this.trdIntransShareAmt = trdIntransShareAmt;
    }

    public BigDecimal getPositionsAmt() {
        return positionsAmt;
    }

    public void setPositionsAmt(BigDecimal positionsAmt) {
        this.positionsAmt = positionsAmt;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPositionsPrice() {
        return positionsPrice;
    }

    public void setPositionsPrice(BigDecimal positionsPrice) {
        this.positionsPrice = positionsPrice;
    }

    public BigDecimal getFloatingPl() {
        return floatingPl;
    }

    public void setFloatingPl(BigDecimal floatingPl) {
        this.floatingPl = floatingPl;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }
}
