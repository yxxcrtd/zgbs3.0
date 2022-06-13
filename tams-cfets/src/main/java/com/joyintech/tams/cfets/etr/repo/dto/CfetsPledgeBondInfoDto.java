/*
 * 文件名：EtrInvestPersonInfo002Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.etr.repo.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 添加质押券Dto
 * 〈功能详细描述〉
 *
 * @author chenzhili
 * @version 1.0 2019年01月12日
 * @see CfetsPledgeBondInfoDto
 * @since 1.0
 */
public class CfetsPledgeBondInfoDto extends BaseEntity {

    /**
     * 债券代码
     */
    @ApiModelProperty(name = "bondCode",value="债券代码")
    private String bondCode;

    /**
     * 债券代码
     */
    @ApiModelProperty(name = "finprodAbbr",value="债券名称")
    private String finprodAbbr;


    /**
     * 债券金融产品代码
     */
    @ApiModelProperty(name = "finprodId",value="债券金融产品代码")
    private String finprodId;

    /**
     * 债券金融产品代码
     */
    @ApiModelProperty(name = "finprodMarketId",value="债券金融产品代码")
    private String finprodMarketId;
    
    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 组合代码
     */
    private String portfolioId;
    
    /**
     * 组合名称
     */
    private String portfolioName;
    
    /**
     * 到期日
     */
    private Date mdate;

    @ApiModelProperty(name="totalPrinAmt",value="汇总券面总额",example="10000")
    private BigDecimal totalPrinAmt;

    /**
     * 可质押券面总额
     */
    private BigDecimal pledStrdPrinAmt;

    /**
     * 质押券面额
     */
    private BigDecimal pledgePrin;

    /**
     * 质押券面额
     */
    private BigDecimal pledgeAmount;

    /**
     * 首次竞价
     */
    private BigDecimal firstNetPrice;


    /**
     * 质押券折算总额
     */
    private BigDecimal totalAmount;
    /**
     * 质押比例%
     */
    private BigDecimal pledgeRatio;

    /**
     * 质押比例%
     */
    private BigDecimal scale;

    /**
     * 登记托管机构
     */
    private String repRegistOrg;
    /**
     *
     */
    private String registOrg;

    public String getBondCode() {
        return bondCode;
    }

    public void setBondCode(String bondCode) {
        this.bondCode = bondCode;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
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

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public BigDecimal getPledStrdPrinAmt() {
        return pledStrdPrinAmt;
    }

    public void setPledStrdPrinAmt(BigDecimal pledStrdPrinAmt) {
        this.pledStrdPrinAmt = pledStrdPrinAmt;
    }

    public BigDecimal getPledgePrin() {
        return pledgePrin;
    }

    public void setPledgePrin(BigDecimal pledgePrin) {
        this.pledgePrin = pledgePrin;
    }

    public BigDecimal getFirstNetPrice() {
        return firstNetPrice;
    }

    public void setFirstNetPrice(BigDecimal firstNetPrice) {
        this.firstNetPrice = firstNetPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPledgeRatio() {
        return pledgeRatio;
    }

    public void setPledgeRatio(BigDecimal pledgeRatio) {
        this.pledgeRatio = pledgeRatio;
    }

    public BigDecimal getScale() {
        return scale;
    }

    public void setScale(BigDecimal scale) {
        this.scale = scale;
    }

    public String getRepRegistOrg() {
        return repRegistOrg;
    }

    public void setRepRegistOrg(String repRegistOrg) {
        this.repRegistOrg = repRegistOrg;
    }

    public String getRegistOrg() {
        return registOrg;
    }

    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }

    public String getRegistOrgStr() {
        return SysDatadictUtil.getNameFromDataDict(DictCode.登记托管机构,this.getRegistOrg());
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public BigDecimal getPledgeAmount() {
        return pledgeAmount;
    }

    public void setPledgeAmount(BigDecimal pledgeAmount) {
        this.pledgeAmount = pledgeAmount;
    }

    public BigDecimal getTotalPrinAmt() {
        return totalPrinAmt;
    }

    public void setTotalPrinAmt(BigDecimal totalPrinAmt) {
        this.totalPrinAmt = totalPrinAmt;
    }
}
