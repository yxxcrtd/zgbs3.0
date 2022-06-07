/*
 * 文件名：CfetsBondDealEntrustOrderDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.repo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;

import java.math.BigDecimal;
import java.util.List;

/**
 * 委托指令 本币业务交易-质押式回购交易
 */
public class CfetsRepoDealEntrustOrderDto extends EtrEntrust {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -1913728031560090029L;

    /**
     * 剩余额度(万元)
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal avaNum;

    /**
     * 委托剩余额度(万元)
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal entrustAvaNum;

    /**
     * 已完成额度(万元)
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishAmt;

    /**
     * 执行进度(%)
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transactioncom;

    /**
     * 已成交金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishDealAmt;

    /**
     * 剩余成交额度
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal avaDealAmt;

    /**
     * 成交进度(%)
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeFinishPercent;

    /*指令额度(万元) 继承*/
    /*组合代码 继承*/
    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 清算速度
     */
    private String settleSpreed;
    /*交易方向 继承*/
    /*价格模式 继承*/
    private String priceModelStr;

    /**
     * 回购期限(天)
     */
    private int repoTerm;

    /**
     * 期限品种
     */
    private String termType;

    /**
     * 回购利率(%)
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeYield;

    /**
     * 质押债券
     */
    private String finprodIds;

    /**
     * 质押债券
     */
    private String finprodMarketNames;

    /**
     * 质押券库
     */
    private String pledgeStockId;

    /**
     * 质押券库名称
     */
    private String pledgeStockName;

    /**
     * 报价方式
     */
    private String quoteType;

    /**
     * 本方
     */
    private String mySide;

    /**
     * 本方简称
     */
    private String mySideAbbr;

    /**
     * 本方交易员
     */
    private String mySideTrader;

    /**
     * 本方交易员名称
     */
    private String mySideTraderName;

    /**
     * 上次 查询时间
     */
    private String lastQueryTime;

    /**
     * 结算币种
     */
    private String settleCcy;

    private Integer rowNumber;

    /**
     * 投资经理姓名
     */
    private String invManagerName;

    /**
     * 交易对手简称
     */
    private String counterName;

    /**
     * 对手方交易员名称
     */
    private String counterTraderName;

    /**
     * 账户来源
     */
    private String secAcctSource;

    /**
     * 证券账户
     */
    private String secManageAcctId;

    /**
     * 证券账户-中债登
     */
    private String secManageAcctIdZZ;

    /**
     * 证券账户-上清所
     */
    private String secManageAcctIdSQ;

    /**
     * 组合系列
     */
    private String portfolioSeries;

    /**
     * 交易成员ID，21位编号
     */
    private String partyId;

    /**
     * 交易对手ID，21位编号
     */
    private String counterPartyId;

    /**
     * 指令下达时间
     */
    private String transmitTime;

    /**
     * 汇总质押券折算总额
     */
    private BigDecimal sumShareAmt;

    //质押券信息
    private List<RepoEntrustPledgeDto> repoEntrustPledgeDtos;

    public String getPortfolioSeries() {
        return portfolioSeries;
    }

    public void setPortfolioSeries(String portfolioSeries) {
        this.portfolioSeries = portfolioSeries;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    /**
     * 获取 invManagerName
     *
     * @return invManagerName.
     */
    public String getInvManagerName() {
        return invManagerName;
    }

    /**
     * 设置 invManagerName
     *
     * @param invManagerName invManagerName
     */
    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    /**
     * 获取 counterName
     *
     * @return counterName.
     */
    public String getCounterName() {
        return counterName;
    }

    /**
     * 设置 counterName
     *
     * @param counterName counterName
     */
    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }


    /**
     * 获取 transactioncom
     *
     * @return transactioncom.
     */
    public BigDecimal getTransactioncom() {
        return transactioncom;
    }

    /**
     * 设置 transactioncom
     *
     * @param transactioncom transactioncom
     */
    public void setTransactioncom(BigDecimal transactioncom) {
        this.transactioncom = transactioncom;
    }

    /**
     * 获取 portfolioName
     *
     * @return portfolioName.
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     *
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 获取 settleSpreed
     *
     * @return settleSpreed.
     */
    public String getSettleSpreed() {
        return settleSpreed;
    }

    /**
     * 设置 settleSpreed
     *
     * @param settleSpreed settleSpreed
     */
    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
    }

    /**
     * 获取 avaNum
     *
     * @return avaNum.
     */
    public BigDecimal getAvaNum() {
        return avaNum;
    }

    /**
     * 设置 avaNum
     *
     * @param avaNum avaNum
     */
    public void setAvaNum(BigDecimal avaNum) {
        this.avaNum = avaNum;
    }


    /**
     * 获取 finishAmt
     *
     * @return finishAmt.
     */
    public BigDecimal getFinishAmt() {
        return finishAmt;
    }


    /**
     * 设置 finishAmt
     *
     * @param finishAmt finishAmt
     */
    public void setFinishAmt(BigDecimal finishAmt) {
        this.finishAmt = finishAmt;
    }

    /**
     * 获取 quoteType
     *
     * @return quoteType.
     */
    public String getQuoteType() {
        return quoteType;
    }

    /**
     * 设置 quoteType
     *
     * @param quoteType quoteType
     */
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    /**
     * 获取 mySide
     *
     * @return mySide.
     */
    public String getMySide() {
        return mySide;
    }

    /**
     * 设置 mySide
     *
     * @param mySide mySide
     */
    public void setMySide(String mySide) {
        this.mySide = mySide;
    }


    /**
     * 获取 mySideAbbr
     *
     * @return mySideAbbr.
     */
    public String getMySideAbbr() {
        return mySideAbbr;
    }


    /**
     * 设置 mySideAbbr
     *
     * @param mySideAbbr mySideAbbr
     */
    public void setMySideAbbr(String mySideAbbr) {
        this.mySideAbbr = mySideAbbr;
    }


    /**
     * 获取 mySideTraderName
     *
     * @return mySideTraderName.
     */
    public String getMySideTraderName() {
        return mySideTraderName;
    }


    /**
     * 设置 mySideTraderName
     *
     * @param mySideTraderName mySideTraderName
     */
    public void setMySideTraderName(String mySideTraderName) {
        this.mySideTraderName = mySideTraderName;
    }


    /**
     * 获取 counterTraderName
     *
     * @return counterTraderName.
     */
    public String getCounterTraderName() {
        return counterTraderName;
    }


    /**
     * 设置 counterTraderName
     *
     * @param counterTraderName counterTraderName
     */
    public void setCounterTraderName(String counterTraderName) {
        this.counterTraderName = counterTraderName;
    }

    /**
     * 获取 mySideTrader
     *
     * @return mySideTrader.
     */
    public String getMySideTrader() {
        return mySideTrader;
    }

    /**
     * 设置 mySideTrader
     *
     * @param mySideTrader mySideTrader
     */
    public void setMySideTrader(String mySideTrader) {
        this.mySideTrader = mySideTrader;
    }

    public String getSecAcctSource() {
        return secAcctSource;
    }

    public void setSecAcctSource(String secAcctSource) {
        this.secAcctSource = secAcctSource;
    }

    /**
     * 获取 lastQueryTime
     *
     * @return lastQueryTime.
     */
    public String getLastQueryTime() {
        return lastQueryTime;
    }

    /**
     * 设置 lastQueryTime
     *
     * @param lastQueryTime lastQueryTime
     */
    public void setLastQueryTime(String lastQueryTime) {
        this.lastQueryTime = lastQueryTime;
    }

    public String getEntrustStatusStr() {
        if (StringUtil.isNotEmpty(super.getEntrustStatus())) {
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.委托状态,
                    super.getEntrustStatus());
        }
        return "";
    }

    public String getQuoteTypeStr() {
        if (StringUtil.isNotEmpty(this.getQuoteType())) {
            String quoteTypeStr = SysDataUtil.getNameFromDataDict(BussinessDictCode.报价方式,
                    this.getQuoteType());
            if (quoteTypeStr == null) {
                return this.quoteType;
            }
            return quoteTypeStr;
        }
        return "";
    }

    public String getPsStr() {
        if (StringUtil.isNotEmpty(super.getPs())) {
            String psStr = SysDataUtil.getNameFromDataDict(BussinessDictCode.交易方向, super.getPs());
            if (psStr == null) {
                return super.getPs();
            }
            return psStr;
        }
        return "";
    }

    public String getCcyStr() {
        if (StringUtil.isNotEmpty(super.getCcy())) {
            String ccyStr = SysDataUtil.getNameFromDataDict(BussinessDictCode.币种, super.getCcy());
            if (ccyStr == null) {
                return super.getCcy();
            }
            return ccyStr;
        }
        return "";
    }

    public String getSettleSpreedStr() {
        if (StringUtil.equals(this.getSettleSpreed(), "0")
                || StringUtil.equals(this.getSettleSpreed(), BussinessDictCodeList.清算速度_T加0.getValue())) {
            return BussinessDictCodeList.清算速度_T加0.getCodeName();
        } else if (StringUtil.equals(this.getSettleSpreed(), "1")
                || StringUtil.equals(this.getSettleSpreed(), BussinessDictCodeList.清算速度_T加1.getValue())) {
            return BussinessDictCodeList.清算速度_T加1.getCodeName();
        } else if (StringUtil.equals(this.getSettleSpreed(), "2")
                || StringUtil.equals(this.getSettleSpreed(), BussinessDictCodeList.清算速度_T加2.getValue())) {
            return BussinessDictCodeList.清算速度_T加2.getCodeName();
        } else {
            return this.getSettleSpreed();
        }
    }

    public String getAvaNumStr() {
        if (StringUtil.isNotEmpty(this.getAvaNum())) {
            return this.getAvaNum().toPlainString();
        }
        return null;
    }

    public String getSettleMethodStr() {
        if (StringUtil.equals(this.getSettleMethod(),
                BussinessDictCodeList.结算方式_见券付款.getValue())) {
            return BussinessDictCodeList.结算方式_见券付款.getCodeName();
        } else if (StringUtil.equals(this.getSettleMethod(),
                BussinessDictCodeList.结算方式_见款付券.getValue())) {
            return BussinessDictCodeList.结算方式_见款付券.getCodeName();
        } else if (StringUtil.equals(this.getSettleMethod(),
                BussinessDictCodeList.结算方式_券款对付.getValue())) {
            return BussinessDictCodeList.结算方式_券款对付.getCodeName();
        } else {
            return this.getSettleMethod();
        }
    }

    public String getSettleMethodDefault() {
        return BussinessDictCodeList.结算方式_券款对付.getCodeName();
    }

    public String getSettleTypeStr() {
        if (StringUtil.equals(this.getSettleType(), BussinessDictCodeList.清算类型_净额清算.getValue())) {
            return BussinessDictCodeList.清算类型_净额清算.getCodeName();
        } else if (StringUtil.equals(this.getSettleType(),
                BussinessDictCodeList.清算类型_全额清算.getValue())) {
            return BussinessDictCodeList.清算类型_全额清算.getCodeName();
        } else {
            return this.getSettleType();
        }
    }

    public String getSettleTypeDefault() {
        return BussinessDictCodeList.清算类型_全额清算.getCodeName();
    }

    /**
     * 获取 settleCcy
     *
     * @return settleCcy.
     */
    public String getSettleCcy() {
        return settleCcy;
    }

    /**
     * 设置 settleCcy
     *
     * @param settleCcy settleCcy
     */
    public void setSettleCcy(String settleCcy) {
        this.settleCcy = settleCcy;
    }

    /**
     * 本方角色
     */
    private String quoteRoleType;

    /**
     * 报价状态，新增中/已新增/修改中/已修改/撤回中/已撤回/成交中/已成交/终止中/已终止/待回复/已撤销/
     */
    private String tradeStatus;

    /**
     * 发送状态，待发送/成功/失败/发送中
     */
    private String sendStatus;

    /**
     * 结算方式
     */
    private String settleMethod;

    /**
     * 清算类型
     */
    private String settleType;

    /**
     * 报价变化
     */
    private String quoteId;


    /**
     * 获取 quoteId
     *
     * @return quoteId.
     */
    public String getQuoteId() {
        return quoteId;
    }


    /**
     * 设置 quoteId
     *
     * @param quoteId quoteId
     */
    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    /**
     * 获取 quoteRoleType
     *
     * @return quoteRoleType.
     */
    public String getQuoteRoleType() {
        return quoteRoleType;
    }

    /**
     * 设置 quoteRoleType
     *
     * @param quoteRoleType quoteRoleType
     */
    public void setQuoteRoleType(String quoteRoleType) {
        this.quoteRoleType = quoteRoleType;
    }

    /**
     * 获取 tradeStatus
     *
     * @return tradeStatus.
     */
    public String getTradeStatus() {
        return tradeStatus;
    }

    /**
     * 设置 tradeStatus
     *
     * @param tradeStatus tradeStatus
     */
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    /**
     * 获取 sendStatus
     *
     * @return sendStatus.
     */
    public String getSendStatus() {
        return sendStatus;
    }

    /**
     * 设置 sendStatus
     *
     * @param sendStatus sendStatus
     */
    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    /**
     * 获取 settleMethod
     *
     * @return settleMethod.
     */
    public String getSettleMethod() {
        return settleMethod;
    }

    /**
     * 设置 settleMethod
     *
     * @param settleMethod settleMethod
     */
    public void setSettleMethod(String settleMethod) {
        this.settleMethod = settleMethod;
    }

    /**
     * 获取 settleType
     *
     * @return settleType.
     */
    public String getSettleType() {
        return settleType;
    }

    /**
     * 设置 settleType
     *
     * @param settleType settleType
     */
    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    /**
     * 获取 rowNumber
     *
     * @return rowNumber.
     */
    public Integer getRowNumber() {
        return rowNumber;
    }

    /**
     * 设置 rowNumber
     *
     * @param rowNumber rowNumber
     */
    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getSendStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行发送状态, this.getSendStatus());
    }

    public BigDecimal getTradeFinishPercent() {
        return tradeFinishPercent;
    }

    public void setTradeFinishPercent(BigDecimal tradeFinishPercent) {
        this.tradeFinishPercent = tradeFinishPercent;
    }

    public String getTradeYieldStr() {
        if (this.getTradeYield() != null) {
            return this.getTradeYield().setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString();
        }
        return "";
    }

    public BigDecimal getEntrustAvaNum() {
        return entrustAvaNum;
    }

    public void setEntrustAvaNum(BigDecimal entrustAvaNum) {
        this.entrustAvaNum = entrustAvaNum;
    }

    public String getVdateStr() {
        if (this.getVdate() != null) {
            return DateUtil.format(this.getVdate());
        }
        return "";
    }


    public String getMdateStr() {
        if (this.getMdate() != null) {
            return DateUtil.format(this.getMdate());
        }
        return "";
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }


    public String getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(String transmitTime) {
        this.transmitTime = transmitTime;
    }

    public String getTradeMarketStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, getTradeMarket());
    }


    public BigDecimal getFinishDealAmt() {
        return finishDealAmt;
    }

    public void setFinishDealAmt(BigDecimal finishDealAmt) {
        this.finishDealAmt = finishDealAmt;
    }

    public void setPriceModelStr(String priceModelStr) {
        this.priceModelStr = priceModelStr;
    }


    public String getPriceModelStr() {
        if (StringUtil.isEmpty(this.getPriceModel())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.价格模式, getPriceModel());
    }

    public BigDecimal getAvaDealAmt() {
        return avaDealAmt;
    }

    public void setAvaDealAmt(BigDecimal avaDealAmt) {
        this.avaDealAmt = avaDealAmt;
    }

    public int getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(int repoTerm) {
        this.repoTerm = repoTerm;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public BigDecimal getTradeYield() {
        return tradeYield;
    }

    public void setTradeYield(BigDecimal tradeYield) {
        this.tradeYield = tradeYield;
    }

    public String getFinprodIds() {
        return finprodIds;
    }

    public void setFinprodIds(String finprodIds) {
        this.finprodIds = finprodIds;
    }

    public String getFinprodMarketNames() {
        return finprodMarketNames;
    }

    public void setFinprodMarketNames(String finprodMarketNames) {
        this.finprodMarketNames = finprodMarketNames;
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

    public String getEntrustDateStr() {
        return DateUtil.format(this.getEntrustDate());
    }

    public List<RepoEntrustPledgeDto> getRepoEntrustPledgeDtos() {
        return repoEntrustPledgeDtos;
    }

    public void setRepoEntrustPledgeDtos(List<RepoEntrustPledgeDto> repoEntrustPledgeDtos) {
        this.repoEntrustPledgeDtos = repoEntrustPledgeDtos;
    }

    public String getCounterPartyId() {
        return counterPartyId;
    }

    public void setCounterPartyId(String counterPartyId) {
        this.counterPartyId = counterPartyId;
    }

    public String getSecManageAcctIdZZ() {
        return secManageAcctIdZZ;
    }

    public void setSecManageAcctIdZZ(String secManageAcctIdZZ) {
        this.secManageAcctIdZZ = secManageAcctIdZZ;
    }

    public String getSecManageAcctIdSQ() {
        return secManageAcctIdSQ;
    }

    public void setSecManageAcctIdSQ(String secManageAcctIdSQ) {
        this.secManageAcctIdSQ = secManageAcctIdSQ;
    }

    public BigDecimal getSumShareAmt() {
        return sumShareAmt;
    }

    public void setSumShareAmt(BigDecimal sumShareAmt) {
        this.sumShareAmt = sumShareAmt;
    }
}
