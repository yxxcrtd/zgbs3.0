package com.joyintech.tams.business.asset.tenderbond.dto;


import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 新债标的列表查询条件
 *
 * @author gwl
 * @version 1.0 2022年4月11日
 * @see FinTenderBondAssetSearch
 * @since 1.0
 */
@ApiModel(value="FinTenderBondAssetSearch",description="新债标的(输入输出共用)")
public class FinTenderBondAssetSearch {

    @ApiModelProperty(name="issueStage",value="发行阶段",required = true)
    private List<String> issueStage;

    @ApiModelProperty(name="finprodMarketId",value="资产代码")
    private String finprodMarketId;

    @ApiModelProperty(name="tradeMarket",value="交易场所")
    private String tradePlace;

    @ApiModelProperty(name="registOrg",value="登记托管机构")
    private String registOrg;

    @ApiModelProperty(name="issuer",value="发行人")
    private String issuer;

    @ApiModelProperty(name="issueType",value="募集方式")
    private String issueType;

    @ApiModelProperty(name="issueClass",value="发行方式")
    private String issueClass;

    @ApiModelProperty(name="tenderType",value="招标方式")
    private String tenderType;

    @ApiModelProperty(name="tenderTarget",value="招标标的")
    private String tenderTarget;

    @ApiModelProperty(name="issueStyle",value="发行类型")
    private String issueStyle;

    @ApiModelProperty(name="issueDateStart",value="发行日开始")
    private Date issueDateStart;

    @ApiModelProperty(name="issueDateEnd",value="发行日结束")
    private Date issueDateEnd;

    @ApiModelProperty(name="payEdateStart",value="缴款截止日开始")
    private Date payEdateStart;

    @ApiModelProperty(name="payEdateEnd",value="缴款截止日结束")
    private Date payEdateEnd;

    @ApiModelProperty(name="listingDateStart",value="上市日期开始")
    private Date listingDateStart;

    @ApiModelProperty(name="listingDateEnd",value="上市日期结束")
    private Date listingDateEnd;

    @ApiModelProperty(name="vdateStart",value="起息日开始")
    private Date vdateStart;

    @ApiModelProperty(name="vdateEnd",value="起息日结束")
    private Date vdateEnd;

    @ApiModelProperty(name="mdateStart",value="到期日开始")
    private Date mdateStart;

    @ApiModelProperty(name="mdateEnd",value="到期日结束")
    private Date mdateEnd;

    @ApiModelProperty(name="type",value="资产分类")
    private List<List<String>> type;

    @ApiModelProperty(name="couponSpecies",value="息票品种")
    private String couponSpecies;

    @ApiModelProperty(name="intType",value="利率类型")
    private String intType;

    @ApiModelProperty(name="payCycle",value="付息频率")
    private String payCycle;

    @ApiModelProperty(name="ccy",value="币种")
    private String ccy;

    @ApiModelProperty(name = "appearMarketId", value = "上市代码")
    private String appearMarketId;

    @ApiModelProperty(name = "issueStatus", value = "发行状态")
    private String issueStatus;

    @ApiModelProperty(name = "finprodType2", value = "金融产品类型（投管分类）")
    private String finprodType2;

    @ApiModelProperty(name="pageMode",value="分页对象",required = true)
    private PageMode pageMode;

    @ApiModelProperty(name="search",value="关键字",example="12")
    private String search;

    @ApiModelProperty(name="platformDate",value="平台日",example="12")
    private String platformDate;

    private List<String> type9List;
    private List<String> type10List;
    private List<String> type11List;


    public List<String> getIssueStage() {
        return issueStage;
    }

    public void setIssueStage(List<String> issueStage) {
        this.issueStage = issueStage;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
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

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getTenderType() {
        return tenderType;
    }

    public void setTenderType(String tenderType) {
        this.tenderType = tenderType;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public String getIssueStyle() {
        return issueStyle;
    }

    public void setIssueStyle(String issueStyle) {
        this.issueStyle = issueStyle;
    }

    public Date getIssueDateStart() {
        return issueDateStart;
    }

    public void setIssueDateStart(Date issueDateStart) {
        this.issueDateStart = issueDateStart;
    }

    public Date getIssueDateEnd() {
        return issueDateEnd;
    }

    public void setIssueDateEnd(Date issueDateEnd) {
        this.issueDateEnd = issueDateEnd;
    }

    public Date getPayEdateStart() {
        return payEdateStart;
    }

    public void setPayEdateStart(Date payEdateStart) {
        this.payEdateStart = payEdateStart;
    }

    public Date getPayEdateEnd() {
        return payEdateEnd;
    }

    public void setPayEdateEnd(Date payEdateEnd) {
        this.payEdateEnd = payEdateEnd;
    }

    public Date getListingDateStart() {
        return listingDateStart;
    }

    public void setListingDateStart(Date listingDateStart) {
        this.listingDateStart = listingDateStart;
    }

    public Date getListingDateEnd() {
        return listingDateEnd;
    }

    public void setListingDateEnd(Date listingDateEnd) {
        this.listingDateEnd = listingDateEnd;
    }

    public Date getVdateStart() {
        return vdateStart;
    }

    public void setVdateStart(Date vdateStart) {
        this.vdateStart = vdateStart;
    }

    public Date getVdateEnd() {
        return vdateEnd;
    }

    public void setVdateEnd(Date vdateEnd) {
        this.vdateEnd = vdateEnd;
    }

    public Date getMdateStart() {
        return mdateStart;
    }

    public void setMdateStart(Date mdateStart) {
        this.mdateStart = mdateStart;
    }

    public Date getMdateEnd() {
        return mdateEnd;
    }

    public void setMdateEnd(Date mdateEnd) {
        this.mdateEnd = mdateEnd;
    }

    public List<List<String>> getType() {
        return type;
    }

    public void setType(List<List<String>> type) {
        this.type = type;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getIntType() {
        return intType;
    }

    public void setIntType(String intType) {
        this.intType = intType;
    }

    public String getPayCycle() {
        return payCycle;
    }

    public void setPayCycle(String payCycle) {
        this.payCycle = payCycle;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getAppearMarketId() {
        return appearMarketId;
    }

    public void setAppearMarketId(String appearMarketId) {
        this.appearMarketId = appearMarketId;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getPlatformDate() {
        return platformDate;
    }

    public void setPlatformDate(String platformDate) {
        this.platformDate = platformDate;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public List<String> getType9List() {
        return type9List;
    }

    public void setType9List(List<String> type9List) {
        this.type9List = type9List;
    }

    public List<String> getType10List() {
        return type10List;
    }

    public void setType10List(List<String> type10List) {
        this.type10List = type10List;
    }

    public List<String> getType11List() {
        return type11List;
    }

    public void setType11List(List<String> type11List) {
        this.type11List = type11List;
    }
}
