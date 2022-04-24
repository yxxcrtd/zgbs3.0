/*
 * 文件名：FinBondTargetAssetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 债券标的信息DTO
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see FinBondAssetRequest
 * @since 1.0
 */
@ApiModel(value="债券标的信息查询参数",description="债券标的信息查询参数")
public class FinBondAssetRequest {

    @ApiModelProperty(name="search",value="关键字",example="A1")
    private String search;

    @ApiModelProperty(name="finprodId",value="资产简称",example="A1")
    private String finprodId;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    @ApiModelProperty(name="tradeMarket",value="交易场所")
    private String tradeMarket;

    private String tradeMarketId;

    @ApiModelProperty(name="registOrg",value="登记托管机构")
    private String registOrg;

    @ApiModelProperty(name="issuer",value="发行人")
    private String issuer;

    @ApiModelProperty(name="assetType",value="资产分类")
    private List<List<String>> assetType;

    private String type9;

    private String type10;

    private String type11;

    @ApiModelProperty(name="idate",value="发行日",example="2021-01-01")
    private List<String> idate;

    @ApiModelProperty(name="idateEnd",value="发行日结束日",example="2021-01-01")
    private String idateEnd;

    @ApiModelProperty(name="idateStart",value="发行日开始日",example="2021-01-01")
    private String idateStart;

    @ApiModelProperty(name="vdate",value="起息日",example="2021-01-01")
    private List<String> vdate;

    @ApiModelProperty(name="vdateEnd",value="起息日结束日",example="2021-01-01")
    private String vdateEnd;

    @ApiModelProperty(name="vdateStart",value="起息日开始日",example="2021-01-01")
    private String vdateStart;

    @ApiModelProperty(name="mdate",value="到期日")
    private  List<String>  mdate;

    @ApiModelProperty(name="mdateEnd",value="到期日结束日",example="2021-01-01")
    private String mdateEnd;

    @ApiModelProperty(name="mdateStart",value="到期日开始日",example="2021-01-01")
    private String mdateStart;

    @ApiModelProperty(name="couponSpecies",value="息票品种")
    private String couponSpecies;

    @ApiModelProperty(name="rateType",value="利率类型")
    private String rateType;

    @ApiModelProperty(name="payCycle",value="付息频率")
    private String payCycle;

    @ApiModelProperty(name="ccy",value="币种")
    private String ccy;

    @ApiModelProperty(name="isSus",value="是否永续")
    private String isSus;

    @ApiModelProperty(name="isRight",value="是否含权债")
    private String isRight;

    @ApiModelProperty(name="exerciseType",value="行权方式")
    private String exerciseType;

    @ApiModelProperty(name="exerciseDate",value="理论行权日")
    private List<String> exerciseDate;

    @ApiModelProperty(name="exerciseDateEnd",value="理论行权日结束日",example="2021-01-01")
    private String exerciseDateEnd;

    @ApiModelProperty(name="exerciseDateStart",value="理论行权日开始日",example="2021-01-01")
    private String exerciseDateStart;

    @ApiModelProperty(name="mGradeResult",value="主体评级")
    private String mGradeResult;

    @ApiModelProperty(name="bGradeResult",value="债项评级")
    private String bGradeResult;

    @ApiModelProperty(name="crossMarketCode",value="跨市场代码")
    private String crossMarketCode;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
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



    public String getIdateEnd() {
        return idateEnd;
    }

    public void setIdateEnd(String idateEnd) {
        this.idateEnd = idateEnd;
    }

    public String getIdateStart() {
        return idateStart;
    }

    public void setIdateStart(String idateStart) {
        this.idateStart = idateStart;
    }


    public String getVdateEnd() {
        return vdateEnd;
    }

    public void setVdateEnd(String vdateEnd) {
        this.vdateEnd = vdateEnd;
    }

    public String getVdateStart() {
        return vdateStart;
    }

    public void setVdateStart(String vdateStart) {
        this.vdateStart = vdateStart;
    }

    public String getMdateEnd() {
        return mdateEnd;
    }

    public void setMdateEnd(String mdateEnd) {
        this.mdateEnd = mdateEnd;
    }

    public String getMdateStart() {
        return mdateStart;
    }

    public void setMdateStart(String mdateStart) {
        this.mdateStart = mdateStart;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
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

    public String getIsSus() {
        return isSus;
    }

    public void setIsSus(String isSus) {
        this.isSus = isSus;
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

    public List<String> getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(List<String> exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public String getExerciseDateEnd() {
        return exerciseDateEnd;
    }

    public void setExerciseDateEnd(String exerciseDateEnd) {
        this.exerciseDateEnd = exerciseDateEnd;
    }

    public String getExerciseDateStart() {
        return exerciseDateStart;
    }

    public void setExerciseDateStart(String exerciseDateStart) {
        this.exerciseDateStart = exerciseDateStart;
    }

    public String getmGradeResult() {
        return mGradeResult;
    }

    public void setmGradeResult(String mGradeResult) {
        this.mGradeResult = mGradeResult;
    }

    public String getbGradeResult() {
        return bGradeResult;
    }

    public void setbGradeResult(String bGradeResult) {
        this.bGradeResult = bGradeResult;
    }

    public String getCrossMarketCode() {
        return crossMarketCode;
    }

    public void setCrossMarketCode(String crossMarketCode) {
        this.crossMarketCode = crossMarketCode;
    }

    public List<List<String>> getAssetType() {
        return assetType;
    }

    public void setAssetType(List<List<String>> assetType) {
        this.assetType = assetType;
    }

    public List<String> getIdate() {
        return idate;
    }

    public void setIdate(List<String> idate) {
        this.idate = idate;
    }

    public List<String> getVdate() {
        return vdate;
    }

    public void setVdate(List<String> vdate) {
        this.vdate = vdate;
    }

    public List<String> getMdate() {
        return mdate;
    }

    public void setMdate(List<String> mdate) {
        this.mdate = mdate;
    }

    public String getTradeMarketId() {
        return tradeMarketId;
    }

    public void setTradeMarketId(String tradeMarketId) {
        this.tradeMarketId = tradeMarketId;
    }
}
