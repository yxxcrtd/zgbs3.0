package com.joyintech.tams.cfets.etr.repo.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value="质押券库查询结果",description="质押券库查询结果")
public class CfetsEtrRepoZLCXPositionResponse {

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
     * 证券账户/通道代码
     */
    @ApiModelProperty(name="secAcctId",value="证券账户/通道代码")
    private String secAcctId;

    @ApiModelProperty(name="secAcctIdStr",value="证券账户名称")
    private String secAcctIdStr;


    /**
     * 金融产品Id
     */
    @ApiModelProperty(name="finprodId",value="金融产品Id")
    private String finprodId;

    /**
     * 资产简称
     */
    @ApiModelProperty(name="finprodAbbr",value="资产简称")
    private String finprodAbbr;

    /**
     * 资产分类
     */
    @ApiModelProperty(name="finProdType",value="资产分类")
    private String finProdType;

    /**
     * 可质押券面总额
     */
    @ApiModelProperty(name="pledStrdPrinAmt",value="可质押券面总额")
    private BigDecimal pledStrdPrinAmt;
    /**
     * 持仓券面总额
     */
    @ApiModelProperty(name="pledgeAmount",value="已质押券面总额")
    private BigDecimal pledgeAmount;

    /**
     * 债项评级
     */
    @ApiModelProperty(name="gradeResult",value="债项评级")
    private String gradeResult ;

    /**
     * 到期日
     */
    @ApiModelProperty(name="mdate",value="到期日")
    private Date mdate;
    /**
     * 到期日
     */
    @ApiModelProperty(name="mdate",value="到期日")
    private String mdateStr;

    /**
     * 折算比例
     */
    @ApiModelProperty(name="scale",value="折算比例")
    private BigDecimal scale;

    /**
     * 首次净价
     */
    @ApiModelProperty(name="firstNetPrice",value="首次净价")
    private BigDecimal firstNetPrice;

    /**
     * 登记托管机构
     */
    @ApiModelProperty(name="registOrg",value="登记托管机构")
    private String registOrg ;

    /**
     * 质押券库
     */
    @ApiModelProperty(name="pledgeStockName",value="质押券库")
    private String pledgeStockName;
    
    /**
     * 质押券库Id
     */
    @ApiModelProperty(name="pledgeStockId",value="质押券库Id")
    private String pledgeStockId;
    
    /**
     * 市场代码
     */
    @ApiModelProperty(name="finprodMarketId",value="市场代码")
    private String finprodMarketId;

    @ApiModelProperty(name="waitPeriod",value="待偿期")
    private String waitPeriod;
    
    
    private String issuer;
    
    private long branch;
    

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

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getFinProdType() {
        return finProdType;
    }

    public void setFinProdType(String finProdType) {
        this.finProdType = finProdType;
    }

    public BigDecimal getPledStrdPrinAmt() {
        return pledStrdPrinAmt;
    }

    public void setPledStrdPrinAmt(BigDecimal pledStrdPrinAmt) {
        this.pledStrdPrinAmt = pledStrdPrinAmt;
    }

    public BigDecimal getPledgeAmount() {
        return pledgeAmount;
    }

    public void setPledgeAmount(BigDecimal pledgeAmount) {
        this.pledgeAmount = pledgeAmount;
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

    public BigDecimal getScale() {
        return scale;
    }

    public void setScale(BigDecimal scale) {
        this.scale = scale;
    }

    public BigDecimal getFirstNetPrice() {
        return firstNetPrice;
    }

    public void setFirstNetPrice(BigDecimal firstNetPrice) {
        this.firstNetPrice = firstNetPrice;
    }

    public String getRegistOrg() {
        return registOrg;
    }

    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }

    public String getPledgeStockName() {
        return pledgeStockName;
    }

    public void setPledgeStockName(String pledgeStockName) {
        this.pledgeStockName = pledgeStockName;
    }

	public String getFinprodId() {
		return finprodId;
	}

	public void setFinprodId(String finprodId) {
		this.finprodId = finprodId;
	}

	public String getPledgeStockId() {
		return pledgeStockId;
	}

	public void setPledgeStockId(String pledgeStockId) {
		this.pledgeStockId = pledgeStockId;
	}

	public String getFinprodAbbr() {
		return finprodAbbr;
	}

	public void setFinprodAbbr(String finprodAbbr) {
		this.finprodAbbr = finprodAbbr;
	}

	public String getFinprodMarketId() {
		return finprodMarketId;
	}

	public void setFinprodMarketId(String finprodMarketId) {
		this.finprodMarketId = finprodMarketId;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public long getBranch() {
		return branch;
	}

	public void setBranch(long branch) {
		this.branch = branch;
	}

    public String getGradeResultStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级结果, this.getGradeResult());
    }

    public String getRegistOrgStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构, this.getRegistOrg());
    }

    public String getFinProdTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.资产四级分类, this.getFinProdType());
    }

    public String getSecAcctIdStr() {
        return secAcctIdStr;
    }

    public void setSecAcctIdStr(String secAcctIdStr) {
        this.secAcctIdStr = secAcctIdStr;
    }

    public String getMdateStr() {
        return mdateStr;
    }

    public void setMdateStr(String mdateStr) {
        this.mdateStr = mdateStr;
    }

    public String getWaitPeriod() {
        return waitPeriod;
    }

    public void setWaitPeriod(String waitPeriod) {
        this.waitPeriod = waitPeriod;
    }
}
