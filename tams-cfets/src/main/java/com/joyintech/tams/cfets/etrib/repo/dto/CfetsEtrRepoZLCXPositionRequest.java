package com.joyintech.tams.cfets.etrib.repo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="质押券库查询入参",description="质押券库查询入参")
public class CfetsEtrRepoZLCXPositionRequest {

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name="portfolioIdList",value="组合代码",example="['1','2']")
    private List<String> portfolioIdList;

    @ApiModelProperty(name="zyqk",value="质押券库",example="03")
    private String zyqk;

    @ApiModelProperty(name="secAcctIdList",value="证券账户",example="['1','2']")
    private List<String> secAcctIdList;

    @ApiModelProperty(name="registOrgList",value="登记托管机构",example="CBC")
    private List<String> registOrgList;

    @ApiModelProperty(name="finprodAbbrList",value="债券简称",example="CBC")
    private List<String> finprodAbbrList;

    @ApiModelProperty(name="finprodTypeList",value="债券分类",example="CBC")
    private List<String> finprodTypeList;

    @ApiModelProperty(name="search",value="关键字",example="CBC")
    private String search;

    /**
     * 未来行权日待偿期
     */
    @ApiModelProperty(name="exPendingPeriod",value="未来行权日待偿期")
    private BigDecimal exPendingPeriod;

    /**
     * 未来行权日待偿期单位（D/Y）
     */
    @ApiModelProperty(name="exPendingPeriodUnit",value="未来行权日待偿期单位")
    private String exPendingPeriodUnit;

    /**
     * 到期日待偿期
     */
    @ApiModelProperty(name="mPendingPeriod",value="到期日待偿期")
    private String mPendingPeriod;

    /**
     * 到期日待偿期单位（D/Y）
     */
    @ApiModelProperty(name="mPendingPeriodUnit",value="到期日待偿期单位")
    private String mPendingPeriodUnit;

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public List<String> getPortfolioIdList() {
        return portfolioIdList;
    }

    public void setPortfolioIdList(List<String> portfolioIdList) {
        this.portfolioIdList = portfolioIdList;
    }

    public String getZyqk() {
        return zyqk;
    }

    public void setZyqk(String zyqk) {
        this.zyqk = zyqk;
    }

    public List<String> getSecAcctIdList() {
        return secAcctIdList;
    }

    public void setSecAcctIdList(List<String> secAcctIdList) {
        this.secAcctIdList = secAcctIdList;
    }

    public List<String> getRegistOrgList() {
        return registOrgList;
    }

    public void setRegistOrgList(List<String> registOrgList) {
        this.registOrgList = registOrgList;
    }

    public List<String> getFinprodAbbrList() {
        return finprodAbbrList;
    }

    public void setFinprodAbbrList(List<String> finprodAbbrList) {
        this.finprodAbbrList = finprodAbbrList;
    }

    public List<String> getFinprodTypeList() {
        return finprodTypeList;
    }

    public void setFinprodTypeList(List<String> finprodTypeList) {
        this.finprodTypeList = finprodTypeList;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public BigDecimal getExPendingPeriod() {
        return exPendingPeriod;
    }

    public void setExPendingPeriod(BigDecimal exPendingPeriod) {
        this.exPendingPeriod = exPendingPeriod;
    }

    public String getExPendingPeriodUnit() {
        return exPendingPeriodUnit;
    }

    public void setExPendingPeriodUnit(String exPendingPeriodUnit) {
        this.exPendingPeriodUnit = exPendingPeriodUnit;
    }

    public String getmPendingPeriod() {
        return mPendingPeriod;
    }

    public void setmPendingPeriod(String mPendingPeriod) {
        this.mPendingPeriod = mPendingPeriod;
    }

    public String getmPendingPeriodUnit() {
        return mPendingPeriodUnit;
    }

    public void setmPendingPeriodUnit(String mPendingPeriodUnit) {
        this.mPendingPeriodUnit = mPendingPeriodUnit;
    }
}
