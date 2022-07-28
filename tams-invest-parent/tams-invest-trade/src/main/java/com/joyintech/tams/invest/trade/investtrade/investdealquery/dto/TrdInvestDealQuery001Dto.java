/*
 * 文件名：TrdInvestDealQuery001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.investtrade.investdealquery.dto;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.constant.*;

import java.math.BigDecimal;

/**
 * 交易查询dto
 * 〈功能详细描述〉
 *
 * @author wangkang
 * @version 1.0 2018年9月29日
 * @see TrdInvestDealQuery001Dto
 * @since 1.0
 */
public class TrdInvestDealQuery001Dto extends WflTrdProductDeal {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 有效状态
     */
    private String validStatus;
    /**
     * 委托代码
     */
    private String entrustId;
    /**
     * 投资组合代码
     */
    private String portfolioId;
    /**
     * 组合名称
     */
    private String portfolioName;
    /**
     * 市场代码
     */
    private String finprodMarketId;
    /**
     * 金融产品简称
     */
    private String finprodAbbr;
    /**
     * 回购拆借方向
     */
    private String backLendDirection;

    //add by 张年  2019/8/07 start
    /**
     * 交易对手名称
     */
    private String counterName;
    //add by 张年  2019/8/07 end

    // add by caozhenming 20210730 start
    /**
     * 交易价格
     */
    private BigDecimal tradePrice;

    /**
     * 利率
     */
    private BigDecimal rate;
    // add by caozhenming 20210730 end

    /**
     * 申购类型：01-网上申购；02-网下申购
     */
    private String purchModle;

    /**
     * 类型：H01-协议回购；H02-质押式回购
     */
    private String type6;

    private String finType2;

    /**
     * 得到有效状态
     *
     * @return
     */
    public String getValidStatus() {
        return validStatus;
    }

    /**
     * 有效状态转换
     *
     * @return
     */
    public String getValidStatusVal() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易流程状态, this.getValidStatus());
    }

    /**
     * 设置有效状态
     *
     * @param validStatus
     */
    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    /**
     * 得到委托代码
     *
     * @return
     */
    public String getEntrustId() {
        return entrustId;
    }

    /**
     * 设置委托代码
     *
     * @param entrustId
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    /**
     * 得到投资组合代码
     *
     * @return
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置投资组合代码
     *
     * @param portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 得到组合名称
     *
     * @return
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置组合名称
     *
     * @param portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 得到市场代码
     *
     * @return
     */
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    /**
     * 设置市场代码
     *
     * @param finprodMarketId
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    /**
     * 得到金融产品简称
     *
     * @return
     */
    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    /**
     * 设置金融产品简称
     *
     * @param finprodAbbr
     */
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    /**
     * 金融产品类型转换
     *
     * @return
     */
    public String getFinprodType2Val() {
        return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, super.getFinprodType2());
    }

    /**
     * 交易场所转换
     *
     * @return
     */
    public String getTradeMarketVal() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }

    /**
     * 交易方向转换
     *
     * @return
     */
    public String getPsVal() {
        if (StringUtil.equals(type6, "H01")) {
            //委托方向
            String psStr = SysDataUtil.getNameFromDataDict(DictCode.金融产品类型_投管用, finType2);
            if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                psStr = "正回购";
            } else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                psStr = "逆回购";
            } else {
                psStr = SysDataUtil.getNameFromDataDict(DictCode.金融产品类型_投管用, finType2);
            }
            //业务类型
            String bizType;
            if (StringUtil.equals(super.getPs(), DictCodeListExt3.交易方向_投资端_提前终止.getValue())) {
                bizType = SysDataUtil.getNameFromDataDict(DictCode.交易方向, DictCodeListExt3.交易方向_提前购回.getValue());
            } else if (StringUtil.equals(super.getPs(), DictCodeList.交易方向_到期.getValue())) {
                bizType = SysDataUtil.getNameFromDataDict(DictCode.交易方向, DictCodeListExt3.交易方向_到期购回.getValue());
            } else if (StringUtil.equals(super.getPs(), "T01") || StringUtil.equals(super.getPs(), "T02")) {
                bizType = "首期";
            } else {
                bizType = SysDataUtil.getNameFromDataDict(DictCode.交易方向, super.getPs());
            }
            return psStr + bizType;
        }
        if (StringUtil.equals(super.getPs(), DictCodeList.回购拆借方向_拆入.getValue())
                || StringUtil.equals(super.getPs(), DictCodeList.回购拆借方向_拆出.getValue())
                || StringUtil.equals(super.getPs(), DictCodeList.回购拆借方向_正回购.getValue())
                || StringUtil.equals(super.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())
                || StringUtil.equals(super.getPs(), DictCodeList.回购拆借方向_融入.getValue())
                || StringUtil.equals(super.getPs(), DictCodeList.回购拆借方向_融出.getValue())
                || StringUtil.equals(super.getPs(), DictCodeListExt2.互换方式_收取固定.getValue())
                || StringUtil.equals(super.getPs(), DictCodeListExt2.互换方式_收取浮动.getValue())) {
            if (StringUtil.equals(super.getFinprodType2(), DictCodeListExt2.平安资产类型_利率互换.getValue())) {
                return SysDataUtil.getNameFromDataDict(DictCode.互换方式, super.getPs());
            } else {
                return SysDataUtil.getNameFromDataDict(DictCode.回购拆借方向, super.getPs());
            }
        } else {
            return SysDataUtil.getNameFromDataDict(DictCode.交易方向, super.getPs());
        }
    }

    /**
     * 交易市场转换
     *
     * @return
     */
    public String getTradeMarketModeVal() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易市场, super.getTradeMarketMode());
    }

    /**
     * 币种转换
     *
     * @return
     */
    public String getCcyVal() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());
    }

    /**
     * 交易日期str
     *
     * @return
     */
    public String getTradeDateVal() {
        return DateUtil.format(super.getTradeDate());
    }

    /**
     * 交割日期str
     *
     * @return
     */
    public String getSettleDateVal() {
        return DateUtil.format(super.getSettleDate());
    }

    /**
     * 得到回购拆借方向
     *
     * @return
     */
    public String getBackLendDirection() {
        return backLendDirection;
    }

    /**
     * 设置回购拆借方向
     *
     * @param backLendDirection
     */
    public void setBackLendDirection(String backLendDirection) {
        this.backLendDirection = backLendDirection;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getIsOutTradeStr() {
        if (StringUtil.equals(DictCodeList.是否_是.getValue(), super.getIsOutTrade())) {
            return DictCodeList.是否_是.getCodeName();
        } else {
            return DictCodeList.是否_否.getCodeName();
        }
    }

    public BigDecimal getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(BigDecimal tradePrice) {
        this.tradePrice = tradePrice;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getPurchModle() {
        return purchModle;
    }

    public void setPurchModle(String purchModle) {
        this.purchModle = purchModle;
    }

    public String getType6() {
        return type6;
    }

    public void setType6(String type6) {
        this.type6 = type6;
    }

    public String getFinType2() {
        return finType2;
    }

    public void setFinType2(String finType2) {
        this.finType2 = finType2;
    }
}
