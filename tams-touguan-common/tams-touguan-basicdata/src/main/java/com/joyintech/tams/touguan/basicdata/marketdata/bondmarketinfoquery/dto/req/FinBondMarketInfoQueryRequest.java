package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 债券市场信息查询条件
 *
 * @author zhuys
 * @version 1.0 2022年4月11日
 * @see FinBondMarketInfoQueryRequest
 * @since 1.0
 */
@ApiModel(value = "债券市场信息查询条件", description = "债券市场信息查询条件")
public class FinBondMarketInfoQueryRequest {

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name = "finprodMarketIds", value = "市场代码")
    private List<String> finprodMarketIds;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name = "tradeMarket", value = "交易场所代码")
    private String tradeMarket;

    @ApiModelProperty(name = "tradeMarkets ", value = "交易场所代码集")
    private List<String> tradeMarkets;

    @ApiModelProperty(name = "issuer", value = "发行人")
    private String issuer;

    @ApiModelProperty(name = "issueDateStart", value = "发行日开始")
    private Date issueDateStart;

    @ApiModelProperty(name = "issueDateEnd", value = "发行日结束")
    private Date issueDateEnd;

    @ApiModelProperty(name = "vdateStart", value = "起息日开始")
    private Date vdateStart;

    @ApiModelProperty(name = "vdateEnd", value = "起息日结束")
    private Date vdateEnd;

    @ApiModelProperty(name = "mdateStart", value = "到期日开始")
    private Date mdateStart;

    @ApiModelProperty(name = "mdateEnd", value = "到期日结束")
    private Date mdateEnd;

    @ApiModelProperty(name = "couponSpecies", value = "息票品种")
    private String couponSpecies;

    @ApiModelProperty(name = "intType", value = "利率类型")
    private String intType;

    @ApiModelProperty(name = "payCycle", value = "付息频率")
    private String payCycle;

    @ApiModelProperty(name = "ccy", value = "币种")
    private String ccy;

    @ApiModelProperty(name = "isRight", value = "是否含权债")
    private String isRight;

    @ApiModelProperty(name = "exerciseType", value = "行权方式")
    private String exerciseType;

    @ApiModelProperty(name = "isSus", value = "是否永续")
    private String isSus;

    @ApiModelProperty(name = "exerciseDateStart", value = "理论行权日开始")
    private Date exerciseDateStart;

    @ApiModelProperty(name = "exerciseDateEnd", value = "理论行权日结束")
    private Date exerciseDateEnd;

    @ApiModelProperty(name = "zxGrade", value = "债项评级")
    private String zxGrade;

    @ApiModelProperty(name = "ztGrade", value = "主体评级")
    private String ztGrade;

    @ApiModelProperty(name = "crossMarketCode", value = "跨市场代码")
    private String crossMarketCode;

    @ApiModelProperty(name = "warrantor", value = "担保人")
    private String warrantor;

    @ApiModelProperty(name = "platformDate", value = "平台日")
    private String platformDate;

    @ApiModelProperty(name = "search", value = "关键词")
    private String search;


    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public List<String> getFinprodMarketIds() {
        return finprodMarketIds;
    }

    public void setFinprodMarketIds(List<String> finprodMarketIds) {
        this.finprodMarketIds = finprodMarketIds;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public List<String> getTradeMarkets() {
        return tradeMarkets;
    }

    public void setTradeMarkets(List<String> tradeMarkets) {
        this.tradeMarkets = tradeMarkets;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
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

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getIsSus() {
        return isSus;
    }

    public void setIsSus(String isSus) {
        this.isSus = isSus;
    }

    public Date getExerciseDateStart() {
        return exerciseDateStart;
    }

    public void setExerciseDateStart(Date exerciseDateStart) {
        this.exerciseDateStart = exerciseDateStart;
    }

    public Date getExerciseDateEnd() {
        return exerciseDateEnd;
    }

    public void setExerciseDateEnd(Date exerciseDateEnd) {
        this.exerciseDateEnd = exerciseDateEnd;
    }

    public String getZxGrade() {
        return zxGrade;
    }

    public void setZxGrade(String zxGrade) {
        this.zxGrade = zxGrade;
    }

    public String getZtGrade() {
        return ztGrade;
    }

    public void setZtGrade(String ztGrade) {
        this.ztGrade = ztGrade;
    }

    public String getCrossMarketCode() {
        return crossMarketCode;
    }

    public void setCrossMarketCode(String crossMarketCode) {
        this.crossMarketCode = crossMarketCode;
    }

    public String getWarrantor() {
        return warrantor;
    }

    public void setWarrantor(String warrantor) {
        this.warrantor = warrantor;
    }

    public String getPlatformDate() {
        return platformDate;
    }

    public void setPlatformDate(String platformDate) {
        this.platformDate = platformDate;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}