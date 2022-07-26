/*
 * 文件名：EtrExchangeTradeDto002.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author chendijun
 * @version 1.0 2020年7月2日
 * @see EtrExchangeAgreRepTrade003Dto
 * @since 1.0
 */
@PageId("EtrExchangeAgreRepTrade003Dto")
public class EtrExchangeAgreRepTrade003Dto extends EtrEntrust{


    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -6781233493537627872L;

    /**
     * 执行进度
     */
    private BigDecimal implementPercent;
    /**
     * 成交进度
     */
    private BigDecimal dealPercent;

    /**
     * 组合名称
     */
    private String portfolioName;

    private String finprodAbbr;

    private List<EtrExchangeAgreRepTrade003Dto> selectedItem;

    private String paAssetType;

    /**
     * 市场代码
     */
    private String finprodMarketId;

    /**
     * 席位号
     */
    private String counterSeat;

    /**
     * 约定号
     */
    private String counterConNo;

    /**
     * 交易货币账户
     */
    private String dealCurAcct;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 本方交易员
     */
    private String trader;

    /**
     * 对手方交易商
     */
    private String counterOrg;

    /**
     * 申报类型 [db字段：DEAL_MODE，交易方式]
     */
    private String declaType;

    /**
     *
     */
    private String calMode;

    /**
     * 全价
     */
    private BigDecimal fprice;

    /**
     * 收益率
     */
    private BigDecimal  yield;


    /**
     * 资产类型
     */
    private String assetType;
    /**
     * 回购期限
     */

    private Integer term;
    private Date fristSettleDate;

    private Date expireSettleDate;
    private Integer occupyDays;
    private BigDecimal intAmt;
    private BigDecimal expireSettleAmt;
    private BigDecimal conRatio;
    private BigDecimal repSecNum;
    private BigDecimal printAmt;
    private String counterId;
    private String counterName;
    private String counterTrader;
    private String secAcctId;

    private String fromEtrExchange;

    private String tradeId;
    private String intiTradeId;

    /**
     * 资产类型2
     */
    private String assetType2;

    private String busiPs;

    /**
     * 质押券单位
     */
    private String pledgeUnit;

    /**
     * 业务类型
     */
    private String busiTypeTg;
    /**
     * 业务类型
     */
    private String busiTypeTgStr;

    /**
     * 投资经理
     */
    private String invManagerName;

    /**
     * 本方交易员
     */
    private String homeTrader;

    /**
     * 补充协议
     */
    private String addCla;

    @ApiModelProperty(name="finishAmt",value="已完成额度(元)")
    private String finishAmt;

    @ApiModelProperty(name="avaNum",value="剩余指令额度(元)")
    private String avaNum;

    @ApiModelProperty(name="finishDealAmt",value="已成交额度(元)")
    private String finishDealAmt;

    @ApiModelProperty(name="avaDealAmt",value="剩余成交额度(元)")
    private String avaDealAmt;

    @ApiModelProperty(name="psStr",value="委托方向")
    private String psStr;

    @ApiModelProperty(name="requestId",value="请求编号")
    private String requestId;

    private String initialNum;

    @ApiModelProperty(name="swapOrigFinprodId",value="中途换券当前质押券")
    private String swapOrigFinprodId;
    
    @ApiModelProperty(name="swapOrigFinprodMarketId",value="中途换券当前质押代码")
    private String swapOrigFinprodMarketId;

    @ApiModelProperty(name="swapOrigFinprodAbbr",value="中途换券当前质押券名称")
    private String swapOrigFinprodAbbr;

    @ApiModelProperty(name="swapOrigRepSecNum",value="中途换券当前质押数量")
    private BigDecimal swapOrigRepSecNum;

    @ApiModelProperty(name="swapOutFinprodId",value="中途换券换出质押券")
    private String swapOutFinprodId;

    @ApiModelProperty(name="swapOutFinprodMarketId",value="中途换券换出质押代码")
    private String swapOutFinprodMarketId;

    @ApiModelProperty(name="swapOutFinprodAbbr",value="中途换券换出质押券名称")
    private String swapOutFinprodAbbr;

    @ApiModelProperty(name="swapOutRepSecNum",value="中途换券换出质押数量")
    private BigDecimal swapOutRepSecNum;

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
        if(StringUtil.isNotEmpty(dealPercent)){
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
        if (StringUtil.isNotBlank(this.psStr)) {
            return this.psStr;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, super.getPs());
    }

    public void setPsStr(String psStr) {
        this.psStr = psStr;
    }

    public  String getFinprodType2String(){
        return SysDataUtil.getNameFromDataDict(DictCode.金融产品类型_投管用, super.getFinprodType2());
    }

    public  String getAssetTypeString(){
        return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, this.assetType);
    }

    public String getEntrustStatusString(){
        return SysDataUtil.getNameFromDataDict(DictCode.委托状态, super.getEntrustStatus());
    }

    public String getTradeMarketString(){
        return  SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }

    public String getPriceModelString(){
        return  SysDataUtil.getNameFromDataDict(DictCode.价格模式, super.getPriceModel());
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public List<EtrExchangeAgreRepTrade003Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<EtrExchangeAgreRepTrade003Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getEntrustId(){
        return super.getEntrustId();
    }

    public String getPaAssetType() {
        return paAssetType;
    }

    public void setPaAssetType(String paAssetType) {
        this.paAssetType = paAssetType;
    }

    public String getTradePlatString(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易平台, super.getTradePlat());
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

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Date getFristSettleDate() {
        return fristSettleDate;
    }

    public void setFristSettleDate(Date fristSettleDate) {
        this.fristSettleDate = fristSettleDate;
    }

    public Date getExpireSettleDate() {
        return expireSettleDate;
    }

    public void setExpireSettleDate(Date expireSettleDate) {
        this.expireSettleDate = expireSettleDate;
    }

    public Integer getOccupyDays() {
        return occupyDays;
    }

    public void setOccupyDays(Integer occupyDays) {
        this.occupyDays = occupyDays;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getExpireSettleAmt() {
        return expireSettleAmt;
    }

    public void setExpireSettleAmt(BigDecimal expireSettleAmt) {
        this.expireSettleAmt = expireSettleAmt;
    }

    public BigDecimal getConRatio() {
        return conRatio;
    }

    public void setConRatio(BigDecimal conRatio) {
        this.conRatio = conRatio;
    }

    public BigDecimal getRepSecNum() {
        return repSecNum;
    }

    public void setRepSecNum(BigDecimal repSecNum) {
        this.repSecNum = repSecNum;
    }

    public BigDecimal getPrintAmt() {
        return printAmt;
    }

    public void setPrintAmt(BigDecimal printAmt) {
        this.printAmt = printAmt;
    }

    @Override
    public String getCounterId() {
        return counterId;
    }

    @Override
    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    @Override
    public String getCounterTrader() {
        return counterTrader;
    }

    @Override
    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getFromEtrExchange() {
        return fromEtrExchange;
    }

    public void setFromEtrExchange(String fromEtrExchange) {
        this.fromEtrExchange = fromEtrExchange;
    }

    public String getAssetType2() {
        return assetType2;
    }

    public void setAssetType2(String assetType2) {
        this.assetType2 = assetType2;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getIntiTradeId() {
        return intiTradeId;
    }

    public void setIntiTradeId(String intiTradeId) {
        this.intiTradeId = intiTradeId;
    }

    public String getBusiPs() {
        return busiPs;
    }

    public void setBusiPs(String busiPs) {
        this.busiPs = busiPs;
    }

    /**
     * 当前处理人
     */
    private String nextUser;

    public String getNextUser() {
        return nextUser;
    }

    public void setNextUser(String nextUser) {
        this.nextUser = nextUser;
    }

    public String getPledgeUnit() {
        return pledgeUnit;
    }

    public void setPledgeUnit(String pledgeUnit) {
        this.pledgeUnit = pledgeUnit;
    }

    public String getPledgeUnitStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易单位,getPledgeUnit());
    }

    public String getBusiTypeTg() {
        return busiTypeTg;
    }

    public void setBusiTypeTg(String busiTypeTg) {
        this.busiTypeTg = busiTypeTg;
    }

    public String getBusiTypeTgStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.投管业务类型, this.getBusiTypeTg());
    }

    public String getInvManagerName() {
        return invManagerName;
    }

    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    public String getHomeTrader() {
        return homeTrader;
    }

    public void setHomeTrader(String homeTrader) {
        this.homeTrader = homeTrader;
    }

    public String getAddCla() {
        return addCla;
    }

    public void setAddCla(String addCla) {
        this.addCla = addCla;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getInitialNum() {
        return initialNum;
    }

    public void setInitialNum(String initialNum) {
        this.initialNum = initialNum;
    }

    public String getSwapOrigFinprodId() {
        return swapOrigFinprodId;
    }

    public void setSwapOrigFinprodId(String swapOrigFinprodId) {
        this.swapOrigFinprodId = swapOrigFinprodId;
    }

    public String getSwapOrigFinprodMarketId() {
        return swapOrigFinprodMarketId;
    }

    public void setSwapOrigFinprodMarketId(String swapOrigFinprodMarketId) {
        this.swapOrigFinprodMarketId = swapOrigFinprodMarketId;
    }

    public String getSwapOrigFinprodAbbr() {
        return swapOrigFinprodAbbr;
    }

    public void setSwapOrigFinprodAbbr(String swapOrigFinprodAbbr) {
        this.swapOrigFinprodAbbr = swapOrigFinprodAbbr;
    }

    public String getSwapOutFinprodId() {
        return swapOutFinprodId;
    }

    public void setSwapOutFinprodId(String swapOutFinprodId) {
        this.swapOutFinprodId = swapOutFinprodId;
    }

    public String getSwapOutFinprodMarketId() {
        return swapOutFinprodMarketId;
    }

    public void setSwapOutFinprodMarketId(String swapOutFinprodMarketId) {
        this.swapOutFinprodMarketId = swapOutFinprodMarketId;
    }

    public String getSwapOutFinprodAbbr() {
        return swapOutFinprodAbbr;
    }

    public void setSwapOutFinprodAbbr(String swapOutFinprodAbbr) {
        this.swapOutFinprodAbbr = swapOutFinprodAbbr;
    }

    public BigDecimal getSwapOutRepSecNum() {
        return swapOutRepSecNum;
    }

    public void setSwapOutRepSecNum(BigDecimal swapOutRepSecNum) {
        this.swapOutRepSecNum = swapOutRepSecNum;
    }
}
