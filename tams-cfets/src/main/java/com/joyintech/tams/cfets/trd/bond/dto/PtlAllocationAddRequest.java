package com.joyintech.tams.cfets.trd.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zznn
 * @date 2021/11/12
 */
@ApiModel(value="组合分配添加请求参数",description="组合分配添加请求参数")
public class PtlAllocationAddRequest {

    @ApiModelProperty(required = true,name="finprodId",value="资产代码")
    private String finprodId;

    @ApiModelProperty(name="mySide",value="本方")
    private String mySide;

    @ApiModelProperty(required = true,name="secManageAcctId",value="证券账户")
    private String secManageAcctId;

    @ApiModelProperty(required = true,name="ps",value="交易方向")
    private String ps;

    @ApiModelProperty(required = true,name="settleSpreedCode",value="清算速度")
    private String settleSpreedCode;

    @ApiModelProperty(required = true,name="entrustIdList",value="排除已经选择的指令")
    private List<String> entrustIdList;

    @ApiModelProperty(name="portfolioIds",value="组合名称",example="[A,B]")
    private List<String> portfolioIds;

    @ApiModelProperty(name="finprodMarketIds",value="市场代码",example="[A,B]")
    private List<String> finprodMarketIds;

    @ApiModelProperty(name="priceModels",value="价格模式代码集合")
    private List<String> priceModels;

    @ApiModelProperty(name="invManagerIds ",value="投资经理集合",example="[01,02]")
    private List<String> invManagerIds ;

    @ApiModelProperty(name="entrustIds ",value="指令代码集合")
    private List<String> entrustIds ;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getMySide() {
        return mySide;
    }

    public void setMySide(String mySide) {
        this.mySide = mySide;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getSettleSpreedCode() {
        return settleSpreedCode;
    }

    public void setSettleSpreedCode(String settleSpreedCode) {
        this.settleSpreedCode = settleSpreedCode;
    }

    public List<String> getEntrustIdList() {
        return entrustIdList;
    }

    public void setEntrustIdList(List<String> entrustIdList) {
        this.entrustIdList = entrustIdList;
    }

    public List<String> getPortfolioIds() {
        return portfolioIds;
    }

    public void setPortfolioIds(List<String> portfolioIds) {
        this.portfolioIds = portfolioIds;
    }

    public List<String> getFinprodMarketIds() {
        return finprodMarketIds;
    }

    public void setFinprodMarketIds(List<String> finprodMarketIds) {
        this.finprodMarketIds = finprodMarketIds;
    }

    public List<String> getPriceModels() {
        return priceModels;
    }

    public void setPriceModels(List<String> priceModels) {
        this.priceModels = priceModels;
    }

    public List<String> getInvManagerIds() {
        return invManagerIds;
    }

    public void setInvManagerIds(List<String> invManagerIds) {
        this.invManagerIds = invManagerIds;
    }

    public List<String> getEntrustIds() {
        return entrustIds;
    }

    public void setEntrustIds(List<String> entrustIds) {
        this.entrustIds = entrustIds;
    }
}

