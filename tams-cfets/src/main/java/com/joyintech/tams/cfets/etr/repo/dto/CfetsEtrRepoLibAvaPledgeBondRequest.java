package com.joyintech.tams.cfets.etr.repo.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="质押券库查询入参",description="质押券库查询入参")
public class CfetsEtrRepoLibAvaPledgeBondRequest {

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name="portfolioIdList",value="组合代码",example="['1','2']")
    private List<String> portfolioIdList;

    @ApiModelProperty(name="secAcctIdList",value="证券账户",example="['1','2']")
    private List<String> secAcctIdList;

    @ApiModelProperty(name="finprodAbbrList",value="债券简称",example="['1','2']")
    private List<String> finprodAbbrList;

    @ApiModelProperty(name="finprodTypeList",value="债券分类",example="['1','2']")
    private List<String> finprodTypeList;

    @ApiModelProperty(name="gradeResultList",value="评级",example="['1','2']")
    private List<String> gradeResultList;

    @ApiModelProperty(name="idList",value="已质押id",example="['1','2']")
    private List<String> idList;

    @ApiModelProperty(name="zyqk",value="质押券库",example="'2'")
    private List<String> zyqk;

    private String pledgeStockId;

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

    public List<String> getSecAcctIdList() {
        return secAcctIdList;
    }

    public void setSecAcctIdList(List<String> secAcctIdList) {
        this.secAcctIdList = secAcctIdList;
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

    public List<String> getGradeResultList() {
        return gradeResultList;
    }

    public void setGradeResultList(List<String> gradeResultList) {
        this.gradeResultList = gradeResultList;
    }

//    public List<String> getZyqkList() {
//        return zyqkList;
//    }
//
//    public void setZyqkList(List<String> zyqkList) {
//        this.zyqkList = zyqkList;
//    }

    public List<String> getZyqk() {
        return zyqk;
    }

    public void setZyqk(List<String> zyqk) {
        this.zyqk = zyqk;
    }

    public String getPledgeStockId() {
        return pledgeStockId;
    }

    public void setPledgeStockId(String pledgeStockId) {
        this.pledgeStockId = pledgeStockId;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }
}
