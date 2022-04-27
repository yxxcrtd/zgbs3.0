package com.joyintech.tams.cfets.etr.bondfirstbid.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: kchen
 * @Date: 2022/4/26 20:04
 */
@ApiModel(value="组合关联信息",description="组合关联信息")
public class CfetsEtrPortfolioInfoDto {

    @ApiModelProperty(name="finprodId",value="资产代码",example="xxx", required = true)
    private String finprodId;

    @ApiModelProperty(name="portfolioId",value="组合代码",example="xxx", required = true)
    private String portfolioId;

    @ApiModelProperty(name="secAcctId",value="证券账户代码",example="xxx", required = true)
    private String secAcctId;


    @ApiModelProperty(name="businessModel",value="业务模式",example="xxx", required = true)
    private String businessModel;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }
}
