package com.joyintech.tams.cfets.etr.bond.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 持仓列表查询，请求参数
 */
@ApiModel(value="债券指令持仓信息请求参数",description="债券指令持仓信息请求参数")
public class CfetsEtrIbBondPosiPage001RequestDto {

    @ApiModelProperty(name="portfolioId",value="组合代码",example="xxx")
    private String portfolioId;

    @ApiModelProperty(name="cdate",value="交易日期",example="2021-09-13", required = true)
    private String cdate;

    @ApiModelProperty(name="finprodId",value="资产代码",example="xxx")
    private String finprodId;

    @ApiModelProperty(name="search",value="模糊查询使用",example="xxx")
    private String search;
    
    @ApiModelProperty(name="secType1",value="债券一级分类",example="F01")
    private String secType1;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
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

    public String getSecType1() {
        return secType1;
    }

    public void setSecType1(String secType1) {
        this.secType1 = secType1;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    @ApiModelProperty(name="从pageMode获取",value="从pageMode获取")
    public int getPageNo() {
        return pageMode.getPageNo();
    }
    @ApiModelProperty(name="从pageMode获取",value="从pageMode获取")
    public int getPageSize() {
        return pageMode.getPageSize();
    }

    /**
     * 从pageMode中获取排序规则
     * @return
     */
    @ApiModelProperty(name="从pageMode获取",value="从pageMode获取")
    public String getSort() {
        return pageMode.getSort();
    }
    @ApiModelProperty(name="从pageMode获取",value="从pageMode获取")
    public String getDirection() {
        return pageMode.getDirection();
    }

}
