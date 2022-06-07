package com.joyintech.service.valuation.basicdata.asset.issue.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.FinProductIssue;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 新债券标的信息DTO
 *
 * @author zhuys
 * @version 1.0 2022年4月11日
 * @see FinProductIssueDto
 * @since 1.0
 */
public class FinProductIssueDto extends FinProductIssue {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 发行状态
     */
    private String issueStatus;

    /**
     * 资产代码
     */
    private String finprodMarketId;

    /**
     * 资产简称
     */
    private String finprodAbbr;

    /**
     * 资产名称
     */
    private String finprodName;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 登记托管机构
     */
    private String registOrg;

    /**
     * 发行人
     */
    private String issuer;

    /**
     * 募集方式
     */
    private String issueType;

    /**
     * 发行日/投标日FROM
     */
    private Date sdateForm;

    /**
     * 发行日/投标日to
     */
    private Date sdateTo;

    /**
     * 上市日
     */
    private Date onmarketDate;

    /**
     * 上市代码
     */
    private String appearMarketId;

    /**
     * 委托开始日
     */
    private Date vdate;

    /**
     * 委托结束日
     */
    private Date mdate;

    /**
     * 资产一级分类
     */
    private String type9;

    /**
     * 资产二级分类
     */
    private String type10;

    /**
     * 资产三级分类
     */
    private String type11;

    /**
     * 计息类型
     */
    private String couponSpecies;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 发行价
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal issuePrice;

    /**
     * 备注
     */
    private String remark;


    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getRegistOrg() {
        return registOrg;
    }

    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public Date getSdateForm() {
        return sdateForm;
    }

    public void setSdateForm(Date sdateForm) {
        this.sdateForm = sdateForm;
    }

    public Date getSdateTo() {
        return sdateTo;
    }

    public void setSdateTo(Date sdateTo) {
        this.sdateTo = sdateTo;
    }

    public Date getOnmarketDate() {
        return onmarketDate;
    }

    public void setOnmarketDate(Date onmarketDate) {
        this.onmarketDate = onmarketDate;
    }

    public String getAppearMarketId() {
        return appearMarketId;
    }

    public void setAppearMarketId(String appearMarketId) {
        this.appearMarketId = appearMarketId;
    }

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getType9() {
        return type9;
    }

    public void setType9(String type9) {
        this.type9 = type9;
    }

    public String getType10() {
        return type10;
    }

    public void setType10(String type10) {
        this.type10 = type10;
    }

    public String getType11() {
        return type11;
    }

    public void setType11(String type11) {
        this.type11 = type11;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @Override
    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    @Override
    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
