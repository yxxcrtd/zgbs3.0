package com.joyintech.tams.cfets.etr.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 头寸信息查询，请求参数
 */
@ApiModel(value="债券指令头寸信息查询请求参数",description="债券指令头寸信息查询请求参数")
public class CfetsEtrIbBondPosiInfoRequestDto {

    @ApiModelProperty(name="portfolioIdList",value="组合代码集合",example="[字符串1,字符串2,...]", required = true)
    private List<String> portfolioIdList;

    @ApiModelProperty(name="cdate",value="交易日期",example="2021-10-20", required = true)
    private String cdate;

    @ApiModelProperty(name="finprodId",value="资产代码",example="xxx", required = true)
    private String finprodId;

    @ApiModelProperty(name="acctId",value="账户",example="xxx")
    private String acctId;

    public String getAcctId() {
        return acctId;
    }

    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }

    public List<String> getPortfolioIdList() {
        return portfolioIdList;
    }

    public void setPortfolioIdList(List<String> portfolioIdList) {
        this.portfolioIdList = portfolioIdList;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

}
