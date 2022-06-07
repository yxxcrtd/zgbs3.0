package com.joyintech.tams.cfets.trd.bond.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @author zznn
 * @date 2021/11/11
 */
@ApiModel(value="组合分配",description="组合分配")
public class PtlAllocationDto {

    @ApiModelProperty(name="entrustId",value="指令代码")
    private String entrustId;

    @ApiModelProperty(name="portfolioId",value="组合代码")
    private String portfolioId;

    @ApiModelProperty(name="portfolioName",value="组合名称")
    private String portfolioName;


    @ApiModelProperty(name="avaNum",value="剩余指令额度(万元)")
    private BigDecimal avaNum;

    @ApiModelProperty(name="avaNumold",value="剩余指令额度(万元)(备份)")
    private BigDecimal avaNumOld;

    @ApiModelProperty(name="shareAmt",value="券面总额(万元)")
    private BigDecimal shareAmt;

    @ApiModelProperty(name="outPartySec",value="本方券券面总额(万元)")
    private BigDecimal ourEntNum;

    @ApiModelProperty(name="inSec",value="融入券券面总额(万元)")
    private BigDecimal lendinEntNum;

    @ApiModelProperty(name="lendinShareAmt",value="融入券券面")
    private BigDecimal lendinShareAmt;

    @ApiModelProperty(name="ourShareAmt",value="本方券券面")
    private BigDecimal ourShareAmt;

    @ApiModelProperty(name="sellRepurchaseSec",value="卖空购回券面总额(万元)")
    private BigDecimal sellAbleNum;

    @ApiModelProperty(name="dealFeeAmt",value="交易手续费(元)")
    private BigDecimal dealFeeAmt;

    @ApiModelProperty(name="settFeeAmt",value="结算服务费(元)")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name="cpriceAmt",value="净价总额(元)")
    private BigDecimal cpriceAmt;

    @ApiModelProperty(name="intAmt",value="应计利息总额(元)")
    private BigDecimal intAmt;

    @ApiModelProperty(name="fpriceAmt",value="全价总额(元)")
    private BigDecimal fpriceAmt;

    @ApiModelProperty(name="priceModel",value="价格模式")
    private String priceModel;

    @ApiModelProperty(name="businessModel",value="业务模式")
    private String businessModel;

    @ApiModelProperty(name="secManageAcctId",value="证券账户")
    private String secManageAcctId;

    @ApiModelProperty(required = false,name="priceModelStr",value="价格模式名称")
    private String priceModelStr;

    @ApiModelProperty(required = false,name="businessModelStr",value="业务模式名称")
    private String businessModelStr;

    @ApiModelProperty(required = false,name="secAcctName",value="证券账户")
    private String secAcctName;

    @ApiModelProperty(name="price",value="净价(元)")
    private BigDecimal price;

    @ApiModelProperty(name="unitFprice",value="全价(元)")
    private BigDecimal unitFprice;

    @ApiModelProperty(name="matureYield",value="到期收益率(%)")
    private BigDecimal matureYield;

    @ApiModelProperty(name="exerYield",value="行权收益率(%)")
    private BigDecimal exerYield;

    @ApiModelProperty(name="secManageAcctIdStr",value="证券账户名称")
    private String secManageAcctIdStr;

    @ApiModelProperty(name="partyId",value="交易成员ID，21位编号")
    private String partyId;

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

    public BigDecimal getAvaNum() {
        return avaNum;
    }

    public void setAvaNum(BigDecimal avaNum) {
        this.avaNum = avaNum;
    }

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public BigDecimal getOurEntNum() {
        return ourEntNum;
    }

    public void setOurEntNum(BigDecimal ourEntNum) {
        this.ourEntNum = ourEntNum;
    }

    public BigDecimal getLendinEntNum() {
        return lendinEntNum;
    }

    public void setLendinEntNum(BigDecimal lendinEntNum) {
        this.lendinEntNum = lendinEntNum;
    }

    public BigDecimal getSellAbleNum() {
        return sellAbleNum;
    }

    public void setSellAbleNum(BigDecimal sellAbleNum) {
        this.sellAbleNum = sellAbleNum;
    }

    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public BigDecimal getCpriceAmt() {
        return cpriceAmt;
    }

    public void setCpriceAmt(BigDecimal cpriceAmt) {
        this.cpriceAmt = cpriceAmt;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getMatureYield() {
        return matureYield;
    }

    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    public BigDecimal getExerYield() {
        return exerYield;
    }

    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    public BigDecimal getAvaNumOld() {
        return avaNumOld;
    }

    public void setAvaNumOld(BigDecimal avaNumOld) {
        this.avaNumOld = avaNumOld;
    }

    public String getPriceModelStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.价格模式, getPriceModel());
    }

    public void setPriceModelStr(String priceModelStr) {
        this.priceModelStr = priceModelStr;
    }

    public String getBusinessModelStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, getBusinessModel());
    }

    public void setBusinessModelStr(String businessModelStr) {
        this.businessModelStr = businessModelStr;
    }

    public String getSecAcctName() {
        return secAcctName;
    }

    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
    }

    public String getSecManageAcctIdStr() {
        return secManageAcctIdStr;
    }

    public void setSecManageAcctIdStr(String secManageAcctIdStr) {
        this.secManageAcctIdStr = secManageAcctIdStr;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public BigDecimal getLendinShareAmt() {
        return lendinShareAmt;
    }

    public void setLendinShareAmt(BigDecimal lendinShareAmt) {
        this.lendinShareAmt = lendinShareAmt;
    }

    public BigDecimal getOurShareAmt() {
        return ourShareAmt;
    }

    public void setOurShareAmt(BigDecimal ourShareAmt) {
        this.ourShareAmt = ourShareAmt;
    }
}

