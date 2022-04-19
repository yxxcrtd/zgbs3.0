package com.joyintech.tams.cfets.etr.repo.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CfetsEtrIbRepoLibDto  {

    /**
     * 
     */
    private static final long serialVersionUID = 934652225044201805L;
    
    /**
     * pageModel
     */
    private String pageModel; 
    
    private String tradePlace;

    private String pledgeStockId;

    /**
     * 证券账户
     */
    private String secAcctId;


    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    /**
     * 质押券列表信息
     */
    List<CfetsEtrIbRepoLibDto> pledgeBondList = new ArrayList<>();

    /**
     * 可质押券列表信息
     */
    List<CfetsEtrIbRepoLibDto> avaPledgeBondList;

    /**
     * 组合代码
     */
    private String portfolioId;


    /**
     * 分支序号
     */
    private Long branch;

    /**
     * 折算比例
     */
    private BigDecimal scale;

    /**
     * 金融产品代码
     */
    private String finprodId;
    
    private String tradeId;
    
    private String finProdAbbr;
    
    private String pledgeStockName;

    /**
     * 债项评级
     */
    private String gradeResult ;

    /**
     * 登记托管机构
     */
    private String registOrg ;
    
    /**
     * 取得 交易编号
     * @return 交易编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 交易编号
     * @param tradeId 交易编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Long getBranch() {
        return branch;
    }

    public void setBranch(Long branch) {
        this.branch = branch;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getPageModel() {
		return pageModel;
	}

	public void setPageModel(String pageModel) {
		this.pageModel = pageModel;
	}

	public String getTradePlace() {
		return tradePlace;
	}

	public void setTradePlace(String tradePlace) {
		this.tradePlace = tradePlace;
	}

	public String getFinProdAbbr() {
		return finProdAbbr;
	}

	public void setFinProdAbbr(String finProdAbbr) {
		this.finProdAbbr = finProdAbbr;
	}

	public String getPledgeStockId() {
		return pledgeStockId;
	}

	public void setPledgeStockId(String pledgeStockId) {
		this.pledgeStockId = pledgeStockId;
	}

	public String getPledgeStockName() {
		return pledgeStockName;
	}

	public void setPledgeStockName(String pledgeStockName) {
		this.pledgeStockName = pledgeStockName;
	}
    
    public String getRegistOrgStr() {
        if (StringUtil.isEmpty(getRegistOrg())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构_中债, this.getRegistOrg());
    }

    public String getGradeResultStr() {
        if (StringUtil.isEmpty(getGradeResult())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.评级结果, this.getGradeResult());
    }
    private String parentPortfolioId;


    public String getParentPortfolioId() {
        return parentPortfolioId;
    }

    public void setParentPortfolioId(String parentPortfolioId) {
        this.parentPortfolioId = parentPortfolioId;
    }

    public List<CfetsEtrIbRepoLibDto> getPledgeBondList() {
        return pledgeBondList;
    }

    public BigDecimal getScale() {
        return scale;
    }

    public void setScale(BigDecimal scale) {
        this.scale = scale;
    }

    public void setPledgeBondList(List<CfetsEtrIbRepoLibDto> pledgeBondList) {
        this.pledgeBondList = pledgeBondList;
    }

    public List<CfetsEtrIbRepoLibDto> getAvaPledgeBondList() {
        return avaPledgeBondList;
    }

    public void setAvaPledgeBondList(List<CfetsEtrIbRepoLibDto> avaPledgeBondList) {
        this.avaPledgeBondList = avaPledgeBondList;
    }

    public String getGradeResult() {
        return gradeResult;
    }

    public void setGradeResult(String gradeResult) {
        this.gradeResult = gradeResult;
    }

    public String getRegistOrg() {
        return registOrg;
    }

    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }
}
