package com.joyintech.tams.cfets.etr.repo.dto;

import com.joyintech.framework.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value="可质押债券数据",description="可质押债券数据")
public class CfetsEtrIbRepoLibAvaPledgeBondResponse extends BaseEntity {

    /**
     * 组合代码
     */
    @ApiModelProperty(name="portfolioId",value="组合代码")
    private String portfolioId;

    /**
     * 组合名称
     */
    @ApiModelProperty(name="portfolioName",value="组合名称")
    private String portfolioName;

    /**
     * 金融产品代码
     */
    @ApiModelProperty(name="finprodId",value="金融产品代码")
    private String finprodId;

    /**
     * 债券简称
     */
    @ApiModelProperty(name="finProdAbbr",value="债券简称")
    private String finProdAbbr;

    /**
     * 市场代码
     */
    @ApiModelProperty(name="finprodMarketId",value="市场代码")
    private String finprodMarketId;
    /**
     * 证券账户
     */
    @ApiModelProperty(name="secAcctId",value="证券账户")
    private String secAcctId;
    /**
     * 证券账户名称
     */
    @ApiModelProperty(name="secAcctStr",value="证券账户名称")
    private String secAcctStr;
    /**
     * 可质押券面总额
     */
    @ApiModelProperty(name="pledStrdPrinAmt",value="可质押券面总额")
    private BigDecimal pledStrdPrinAmt;
    /**
     * 券面总额
     */
    @ApiModelProperty(name="amount",value="券面总额")
    private BigDecimal amount;
    /**
     * 债项评级
     */
    @ApiModelProperty(name="gradeResult",value="债项评级")
    private String gradeResult ;
    /**
     * 到期日
     */
    @ApiModelProperty(name="mdate",value="到期日")
    private Date mdate ;
    /**
     * 登记托管机构
     */
    @ApiModelProperty(name="registOrg",value="登记托管机构")
    private String registOrg ;

    /**
     * 债项评级
     */
    @ApiModelProperty(name="gradeResult",value="债项评级")
    private String gradeResultStr ;
    /**
     * 到期日
     */
    @ApiModelProperty(name="mdate",value="到期日")
    private String mdateStr ;
    /**
     * 登记托管机构
     */
    @ApiModelProperty(name="registOrg",value="登记托管机构")
    private String registOrgStr ;
    /**
     * 分支序号
     */
    @ApiModelProperty(name="branch",value="分支序号")
    private Long branch;

    /**
     * 分支序号
     */
    @ApiModelProperty(name="waitPeriod",value="待偿期")
    private String waitPeriod;

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

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinProdAbbr() {
        return finProdAbbr;
    }

    public void setFinProdAbbr(String finProdAbbr) {
        this.finProdAbbr = finProdAbbr;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getSecAcctStr() {
        return secAcctStr;
    }

    public void setSecAcctStr(String secAcctStr) {
        this.secAcctStr = secAcctStr;
    }

    public BigDecimal getPledStrdPrinAmt() {
        return pledStrdPrinAmt;
    }

    public void setPledStrdPrinAmt(BigDecimal pledStrdPrinAmt) {
        this.pledStrdPrinAmt = pledStrdPrinAmt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getGradeResult() {
        return gradeResult;
    }


    public void setGradeResult(String gradeResult) {
        this.gradeResult = gradeResult;
    }

    public Date getMdate() {
        return mdate;
    }


    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getRegistOrg() {
        return registOrg;
    }


    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }

    public Long getBranch() {
        return branch;
    }

    public void setBranch(Long branch) {
        this.branch = branch;
    }

    public String getWaitPeriod() {
        return waitPeriod;
    }

    public void setWaitPeriod(String waitPeriod) {
        this.waitPeriod = waitPeriod;
    }

    public String getGradeResultStr() {
        return gradeResultStr;
    }

    public void setGradeResultStr(String gradeResultStr) {
        this.gradeResultStr = gradeResultStr;
    }

    public String getMdateStr() {
        return mdateStr;
    }

    public void setMdateStr(String mdateStr) {
        this.mdateStr = mdateStr;
    }

    public String getRegistOrgStr() {
        return registOrgStr;
    }

    public void setRegistOrgStr(String registOrgStr) {
        this.registOrgStr = registOrgStr;
    }
}
