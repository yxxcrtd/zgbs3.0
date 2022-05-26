package com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePositionsSecRepoResponse;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 *  持仓返回
 * 〈功能详细描述〉
 * @author wsz
 * @version 1.0 20220526
 * @see EtrCreditProtectionCertificateHoldResponse
 * @since 1.0
 */
@ApiModel(value="持仓信息查询返回参数",description="持仓信息查询返回参数")
public class EtrCreditProtectionCertificateHoldResponse {


    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="A1")
    private String portfolioName;

    @ApiModelProperty(name="secAcctId",value="证券账户ID",example="A1")
    private String secAcctId;

    @ApiModelProperty(name="secAcctName",value="证券账户名称",example="A1")
    private String secAcctName;

    @ApiModelProperty(name="acctNo",value="资金账户",example="12")
    private String acctNo;

    @ApiModelProperty(name="secId",value="证券代码",example="12")
    private String secId;

    @ApiModelProperty(name="secName",value="证券名称",example="12")
    private String secName;

    @ApiModelProperty(name="tradeMarket",value="交易市场",example="12")
    private String tradeMarket;

    @ApiModelProperty(name="finprodMarketId",value="证券市场代码",example="12")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodId",value="资产代码",example="12")
    private String finprodId;

    @ApiModelProperty(name="finprodName",value="资产名称",example="12")
    private String finprodName;

    @ApiModelProperty(name="finprodType2",value="金融产品类型（投管分类）",example="12")
    private String finprodType2;

    @ApiModelProperty(name="invAim",value="投资目的",example="12")
    private String invAim;

    @ApiModelProperty(name="businessModel",value="业务模式",example="12")
    private String businessModel;



    @ApiModelProperty(name="positionsNum",value="持仓数量",example="12")
    private BigDecimal positionsNum;


    @ApiModelProperty(name="sellNum",value="可卖数量",example="12")
    private BigDecimal sellNum;

    @ApiModelProperty(name="positionsAmt",value="持仓成本",example="12")
    private BigDecimal positionsAmt;

    @ApiModelProperty(name="price",value="最新价",example="12")
    private BigDecimal price;

    @ApiModelProperty(name="floating",value="浮动盈亏",example="12")
    private BigDecimal floating;

    @ApiModelProperty(name="floatingPL",value="盈亏比例",example="12")
    private BigDecimal floatingPL;

    @ApiModelProperty(name="repoNum",value="质押数量",example="12")
    private BigDecimal repoNum;

    @ApiModelProperty(name="secNum",value="标准券数量",example="12")
    private BigDecimal secNum;

    @ApiModelProperty(name="canUseSecNum",value="标准券可用数量",example="12")
    private BigDecimal canUseSecNum;

    @ApiModelProperty(name="PtlPositionsNum",value="组合持仓.数量",example="12")
    private BigDecimal PtlPositionsNum;

    private String dealCurAcctName;

    private String tradePlat;

    public String getDealCurAcctName() {
        return dealCurAcctName;
    }

    public void setDealCurAcctName(String dealCurAcctName) {
        this.dealCurAcctName = dealCurAcctName;
    }

    public BigDecimal getPtlPositionsNum() {
        return PtlPositionsNum;
    }

    public void setPtlPositionsNum(BigDecimal ptlPositionsNum) {
        PtlPositionsNum = ptlPositionsNum;
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

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getSecAcctName() {
        return secAcctName;
    }

    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public BigDecimal getPositionsNum() {
        return positionsNum;
    }

    public void setPositionsNum(BigDecimal positionsNum) {
        this.positionsNum = positionsNum;
    }

    public BigDecimal getSellNum() {
        return sellNum;
    }

    public void setSellNum(BigDecimal sellNum) {
        this.sellNum = sellNum;
    }

    public BigDecimal getPositionsAmt() {
        return positionsAmt;
    }

    public void setPositionsAmt(BigDecimal positionsAmt) {
        this.positionsAmt = positionsAmt;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFloatingPL() {
        return floatingPL;
    }

    public void setFloatingPL(BigDecimal floatingPL) {
        this.floatingPL = floatingPL;
    }

    public BigDecimal getRepoNum() {
        return repoNum;
    }

    public void setRepoNum(BigDecimal repoNum) {
        this.repoNum = repoNum;
    }

    public BigDecimal getSecNum() {
        return secNum;
    }

    public void setSecNum(BigDecimal secNum) {
        this.secNum = secNum;
    }

    public BigDecimal getCanUseSecNum() {
        return canUseSecNum;
    }

    public void setCanUseSecNum(BigDecimal canUseSecNum) {
        this.canUseSecNum = canUseSecNum;
    }

    public BigDecimal getFloating() {
        return floating;
    }

    public void setFloating(BigDecimal floating) {
        this.floating = floating;
    }
    /**
     * 获取交易场所中文名称
     * @return tradeMarketStr
     */
    public String getTradeMarketStr(){
        if(StringUtil.isNotEmpty(getTradeMarket())){
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易场所,getTradeMarket());
        }
        else {
            return "";
        }
    }

    /**
     * 获取投资目的中文名称
     * @return invAimStr
     */
    public String getInvAimStr(){
        if(StringUtil.isNotEmpty(getInvAim())){
            return SysDataUtil.getNameFromDataDict(DictCode.投资目的,getInvAim());
        }
        else {
            return "";
        }
    }

    /**
     * 获取业务模式名称
     * @return businessModelStr
     */
    public String getBusinessModelStr(){
        if(StringUtil.isNotEmpty(getBusinessModel())){
            Set<String> resList = new HashSet<>();
            String[] values = getBusinessModel().split(",");
            for (String value : values) {
                String nameFromDataDict = SysDataUtil.getNameFromDataDict(DictCode.业务模式, value);
                if(null != nameFromDataDict){
                    resList.add(nameFromDataDict);
                }
            }
            return resList.isEmpty() ? "" : String.join(",",resList);
        }
        else {
            return "";
        }
    }


    public  String getFinprodType2Str(){
        return SysDataUtil.getNameFromDataDict(DictCode.金融产品类型_投管用, this.getFinprodType2());
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    private String assetType;

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public  String getAssetTypeStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.资产三级分类, this.getAssetType());
    }


    @ApiModelProperty(name="positionUnitCost",value="持仓成本单位",example="12")
    private BigDecimal positionUnitCost;

    public BigDecimal getPositionUnitCost() {
        return positionUnitCost;
    }

    public void setPositionUnitCost(BigDecimal positionUnitCost) {
        this.positionUnitCost = positionUnitCost;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }
}
