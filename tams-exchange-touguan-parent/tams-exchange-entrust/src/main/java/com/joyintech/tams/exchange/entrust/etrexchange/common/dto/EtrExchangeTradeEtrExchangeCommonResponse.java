/*
 * 文件名：EtrExchangeTradeDto002.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeJj003Dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  查询指令列表分页数据
 * 〈功能详细描述〉
 * @author meifaxin
 * @version 1.0 2020年7月2日
 * @see EtrExchangeTradeEtrExchangeCommonResponse
 * @since 1.0
 */
@ApiModel(value="指令列表查询返回参数",description="指令列表查询返回参数")
public class EtrExchangeTradeEtrExchangeCommonResponse {


    @ApiModelProperty(name="entrustId",value="指令编号",example="A1")
    private String entrustId;

    @ApiModelProperty(name="entrustStatus",value="指令状态Code",example="A1")
    private String entrustStatus;

    @ApiModelProperty(name="entrustStatusString",value="指令状态名称",example="A1")
    private String entrustStatusString;


    /**
     * 执行进度
     */
    @ApiModelProperty(name="implementPercent",value="执行进度",example="A1")
    private BigDecimal implementPercent;

    /**
     * 成交进度
     */
    @ApiModelProperty(name="dealPercent",value="成交进度",example="A1")
    private BigDecimal dealPercent;
    /**
     * 组合名称
     */
    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;

    /**
     * 组合名称
     */
    @ApiModelProperty(name="portfolioName",value="组合名称",example="A1")
    private String portfolioName;

    /**
     * 资产名称
     */
    @ApiModelProperty(name="finprodId",value="资产代码",example="A1")
    private String finprodId;
    /**
     * 资产名称
     */
    @ApiModelProperty(name="finprodAbbr",value="资产名称",example="A1")
    private String finprodAbbr;

    private List<EtrExchangeTradeJj003Dto> selectedItem;

    @ApiModelProperty(name="paAssetType",value="平安资产类型",example="A1")
    private String paAssetType;

    /**
     * 市场代码
     */
    @ApiModelProperty(name="finprodMarketId",value="市场代码",example="A1")
    private String finprodMarketId;

    /**
     * 席位号
     */
    @ApiModelProperty(name="counterSeat",value="席位号",example="A1")
    private String counterSeat;

    /**
     * 约定号
     */
    @ApiModelProperty(name="counterConNo",value="约定号",example="A1")
    private String counterConNo;

    /**
     * 交易货币账户
     */
    @ApiModelProperty(name="dealCurAcct",value="交易货币账户",example="A1")
    private String dealCurAcct;

    /**
     * 目标类型
     */
    @ApiModelProperty(name="targetType",value="目标类型",example="A1")
    private String targetType;

    /**
     * 本方交易员
     */
    @ApiModelProperty(name="trader",value="本方交易员",example="A1")
    private String trader;

    /**
     * 本方交易员
     */
    @ApiModelProperty(name="counterTrader ",value="对本方交易员",example="A1")
    private String counterTrader ;

    /**
     * 对手方交易商
     */
    @ApiModelProperty(name="counterOrg",value="对手方交易商",example="A1")
    private String counterOrg;

    /**
     * 申报类型 [db字段：DEAL_MODE，交易方式]
     */
    @ApiModelProperty(name="declaType",value="申报类型",example="A1")
    private String declaType;

    /**
     * 计算方式
     */
    @ApiModelProperty(name="calMode",value="计算方式",example="A1")
    private String calMode;

    @ApiModelProperty(name="price",value="净价",example="")
    private BigDecimal price;

    /**
     * 全价
     */
    @ApiModelProperty(name="fprice",value="全价",example="A1")
    private BigDecimal fprice;

    /**
     * 收益率
     */
    @ApiModelProperty(name="yield",value="收益率",example="A1")
    private BigDecimal  yield;


    /**
     * 资产类型
     */
    @ApiModelProperty(name="assetType",value="资产类型",example="A1")
    private String assetType;

    /**
     * 交易方向
     */
    @ApiModelProperty(name="ps",value="交易方向Code",example="A1")
    private String ps;

    /**
     * 交易方向
     */
    @ApiModelProperty(name="psStr",value="交易方向",example="A1")
    private String psStr;

    /**
     * 金融产品类型Code
     */
    @ApiModelProperty(name="finprodType2",value="金融产品类型Code",example="A1")
    private String finprodType2;

    /**
     * 金融产品类型
     */
    @ApiModelProperty(name="finprodType2String",value="金融产品类型",example="A1")
    private String finprodType2String;

    /**
     * 委托状态
     */
    @ApiModelProperty(name="tradeMarket",value="交易场所Code",example="A1")
    private String tradeMarket;

    /**
     * 交易场所
     */
    @ApiModelProperty(name="tradeMarketString",value="交易场所",example="A1")
    private String tradeMarketString;

    /**
     * 交易场所Code
     */
    @ApiModelProperty(name="priceModel",value="委托状态",example="A1")
    private String priceModel;

    /**
     * 交易场所
     */
    @ApiModelProperty(name="entrustStatusString",value="委托状态",example="A1")
    private String priceModelString;

    /**
     * 交易平台
     */
    @ApiModelProperty(name="tradePlat",value="交易平台",example="A1")
    private String tradePlat;

    /**
     * 交易平台
     */
    @ApiModelProperty(name="tradePlatString",value="委托状态",example="A1")
    private String tradePlatString;

    /**
     * 投资目的
     */
    @ApiModelProperty(name="invAim",value="委托状态",example="A1")
    private String invAim;

    @ApiModelProperty(name = "entrustDate", value = "指令日期")
    private Date entrustDate;




    @ApiModelProperty(name="entrustAmt",value="指令金额",example="12")
    private BigDecimal entrustAmt;

    @ApiModelProperty(name="entrustNum",value="指令数量",example="12")
    private BigDecimal entrustNum;

    @ApiModelProperty(name="entrustType",value="指令类型",example="12")
    private String entrustType;

    @ApiModelProperty(name="acctNo",value="资金账户")
    private String acctNo;

    @ApiModelProperty(name="finishAmt",value="已完成额度(元)")
    private String finishAmt;

    @ApiModelProperty(name="avaNum",value="剩余指令额度(元)")
    private String avaNum;

    @ApiModelProperty(name="finishDealAmt",value="已成交额度(元)")
    private String finishDealAmt;

    @ApiModelProperty(name="avaDealAmt",value="剩余成交额度(元)")
    private String avaDealAmt;

    @ApiModelProperty(name="businessModel",value="业务模式")
    private String businessModel;

    @ApiModelProperty(name="stockId",value="stockId")
    private String stockId;
    @ApiModelProperty(name="expConNum",value="预计转换股数")
    private BigDecimal expConNum;
    @ApiModelProperty(name="standardBondVolume",value="标准券数量",example="")
    private BigDecimal standardBondVolume;
    @ApiModelProperty(name="discountRate",value="折算率",example="")
    private BigDecimal discountRate;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 获取 implementPercent
     * @return implementPercent.
     */
    public BigDecimal getImplementPercent() {
        return implementPercent;
    }

    /**
     * 设置 implementPercent
     * @param implementPercent implementPercent
     */
    public void setImplementPercent(BigDecimal implementPercent) {
        this.implementPercent = implementPercent;
    }

    /**
     * 获取执行百分比
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getImplementPercentAge(){
        if(StringUtil.isNotEmpty(implementPercent)){
            return implementPercent.multiply(new BigDecimal(100));
        }else{
            return new BigDecimal(0);
        }
    }

    /**
     * 获取 dealPercent
     * @return dealPercent.
     */
    public BigDecimal getDealPercent() {
        return dealPercent;
    }

    /**
     * 获取成交百分比
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getDealPercentAge(){
        if(StringUtil.isNotEmpty(dealPercent)){
            return dealPercent.multiply(new BigDecimal(100));
        }else{
            return new BigDecimal(0);
        }
    }

    /**
     * 设置 dealPercent
     * @param dealPercent dealPercent
     */
    public void setDealPercent(BigDecimal dealPercent) {
        this.dealPercent = dealPercent;
    }

    /**
     * 获取 portfolioName
     * @return portfolioName.
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }


    /**
     * 获取 getPsStr
     * @return getPsStr.
     */
    public String getPsStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向,  getPs());
    }

    public  String getFinprodType2String(){
        return SysDataUtil.getNameFromDataDict(DictCode.金融产品类型_投管用, getFinprodType2());
    }

    public  String getAssetTypeString(){
        return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, this.assetType);
    }

    public String getEntrustStatusString(){
        return SysDataUtil.getNameFromDataDict(DictCode.委托状态, getEntrustStatus());
    }

    public String getTradeMarketString(){
        return  SysDataUtil.getNameFromDataDict(DictCode.交易场所, getTradeMarket());
    }

    public String getPriceModelString(){
        return  SysDataUtil.getNameFromDataDict(DictCode.价格模式, getPriceModel());
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public void setPsStr(String psStr) {
        this.psStr = psStr;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public void setFinprodType2String(String finprodType2String) {
        this.finprodType2String = finprodType2String;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public void setTradeMarketString(String tradeMarketString) {
        this.tradeMarketString = tradeMarketString;
    }

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public void setPriceModelString(String priceModelString) {
        this.priceModelString = priceModelString;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public List<EtrExchangeTradeJj003Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<EtrExchangeTradeJj003Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }


    public String getPaAssetType() {
        return paAssetType;
    }

    public void setPaAssetType(String paAssetType) {
        this.paAssetType = paAssetType;
    }

    public String getTradePlatString(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易平台, getTradePlat());
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public void setTradePlatString(String tradePlatString) {
        this.tradePlatString = tradePlatString;
    }

    public String getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public void setEntrustStatusString(String entrustStatusString) {
        this.entrustStatusString = entrustStatusString;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getCounterSeat() {
        return counterSeat;
    }

    public void setCounterSeat(String counterSeat) {
        this.counterSeat = counterSeat;
    }

    public String getCounterConNo() {
        return counterConNo;
    }

    public void setCounterConNo(String counterConNo) {
        this.counterConNo = counterConNo;
    }

    public String getDealCurAcct() {
        return dealCurAcct;
    }

    public void setDealCurAcct(String dealCurAcct) {
        this.dealCurAcct = dealCurAcct;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     * 获取 trader
     *
     * @return trader
     */
    public String getTrader() {
        return trader;
    }

    /**
     * 设置 trader
     *
     * @param trader String
     */
    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getCounterTrader() {
        return counterTrader;
    }

    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }

    /**
     * 获取 counterOrg
     *
     * @return counterOrg
     */
    public String getCounterOrg() {
        return counterOrg;
    }

    /**
     * 设置 counterOrg
     *
     * @param counterOrg String
     */
    public void setCounterOrg(String counterOrg) {
        this.counterOrg = counterOrg;
    }

    public String getDeclaType() {
        return declaType;
    }

    public void setDeclaType(String declaType) {
        this.declaType = declaType;
    }

    public String getCalMode() {
        return calMode;
    }

    public void setCalMode(String calMode) {
        this.calMode = calMode;
    }

    public BigDecimal getFprice() {
        return fprice;
    }

    public void setFprice(BigDecimal fprice) {
        this.fprice = fprice;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    //add by chenjiaxing 20210824 start
    /**
     * 证券账户代码
     */
    private String secAcctId;

    /**
     * 证券账户名称
     */
    private String secAcctName;

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
    /**
     * 交易货币账户名称
     */
    private String dealCurAcctName;

    public String getDealCurAcctName() {
        return dealCurAcctName;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    public void setDealCurAcctName(String dealCurAcctName) {
        this.dealCurAcctName = dealCurAcctName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getEntrustDate() {
        return entrustDate;
    }

    public void setEntrustDate(Date entrustDate) {
        this.entrustDate = entrustDate;
    }
    //add by chenjiaxing 20210824 end

    public String getEntrustType() {
        return entrustType;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public void setEntrustType(String entrustType) {
        this.entrustType = entrustType;
    }

    public String getDeclaTypeStr(){
        return  SysDataUtil.getNameFromDataDict(DictCode.申报类型, getDeclaType());
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getFinishAmt() {
        return finishAmt;
    }

    public void setFinishAmt(String finishAmt) {
        this.finishAmt = finishAmt;
    }

    public String getAvaNum() {
        return avaNum;
    }

    public void setAvaNum(String avaNum) {
        this.avaNum = avaNum;
    }

    public String getFinishDealAmt() {
        return finishDealAmt;
    }

    public void setFinishDealAmt(String finishDealAmt) {
        this.finishDealAmt = finishDealAmt;
    }

    public String getAvaDealAmt() {
        return avaDealAmt;
    }

    public void setAvaDealAmt(String avaDealAmt) {
        this.avaDealAmt = avaDealAmt;
    }


    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    @ApiModelProperty(name="counterId",value="交易对手")
    private String counterId;

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public BigDecimal getExpConNum() {
        return expConNum;
    }

    public void setExpConNum(BigDecimal expConNum) {
        this.expConNum = expConNum;
    }

    public BigDecimal getStandardBondVolume() {
        return standardBondVolume;
    }

    public void setStandardBondVolume(BigDecimal standardBondVolume) {
        this.standardBondVolume = standardBondVolume;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }
}
