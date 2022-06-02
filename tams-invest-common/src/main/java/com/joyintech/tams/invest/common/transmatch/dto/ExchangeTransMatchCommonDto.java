package com.joyintech.tams.invest.common.transmatch.dto;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransDataTotal;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalAdd;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalPledge;
import com.joyintech.tams.constant.DictCodeList;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeTransMatchCommonDto extends SettleTransDataTotal {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 8844629119926341373L;

    /**
     * 匹配类型
     */
    private String matchType;


    /**
     * 剩余成交金额
     */
    private BigDecimal remainAmount;

    /**
     * 剩余成交数量
     */
    private BigDecimal remainNum;

    /**
     * 随交易费用
     */
    private BigDecimal totalFeeAmtPay;

    /**
     * 不随交易费用
     */
    private BigDecimal totalUnFeeAmtPay;

    /**
     * 关联的指令编号list
     */
    private List<String> entrustIdList;

    /**
     * 汇总成交数据附属表
     */
    private SettleTransDataTotalAdd settleTransDataTotalAdd;

    /**
     * 汇总成交质押品信息表(质押式协议回购时使用)
     */
    private List<SettleTransDataTotalPledge> settleTransDataTotalPledgeList;

    /**
     *  转换资产代码/债转股代码（基金转换/债转股）
     */
    private String rFinprodType;

    /**
     * 本方交易员
     */
    private String trader;

    /**
     * 对手方交易员
     */
    private String counterTrader;

    /**
     * 本方交易商
     */
    private String dealerNo;

    /**
     * 对手方交易商
     */
    private String outDealerNo;

    /**
     * 本方交易主体
     */
    private String dealerPartyNo;

    /**
     * 对手方交易主体
     */
    private String outDealerPartyNo;

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getCounterTrader() {
        return counterTrader;
    }

    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }

    public String getDealerNo() {
        return dealerNo;
    }

    public void setDealerNo(String dealerNo) {
        this.dealerNo = dealerNo;
    }

    public String getOutDealerNo() {
        return outDealerNo;
    }

    public void setOutDealerNo(String outDealerNo) {
        this.outDealerNo = outDealerNo;
    }

    public String getDealerPartyNo() {
        return dealerPartyNo;
    }

    public void setDealerPartyNo(String dealerPartyNo) {
        this.dealerPartyNo = dealerPartyNo;
    }

    public String getOutDealerPartyNo() {
        return outDealerPartyNo;
    }

    public void setOutDealerPartyNo(String outDealerPartyNo) {
        this.outDealerPartyNo = outDealerPartyNo;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public List<String> getEntrustIdList() {
        return entrustIdList;
    }

    public void setEntrustIdList(List<String> entrustIdList) {
        this.entrustIdList = entrustIdList;
    }

    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }

    public BigDecimal getRemainNum() {
        return remainNum;
    }

    public void setRemainNum(BigDecimal remainNum) {
        this.remainNum = remainNum;
    }

    public BigDecimal getTotalFeeAmtPay() {
        if(StringUtil.equals(getIsPayWithTrade(), DictCodeList.是否_是.getValue())) {
            return ArithmeticUtil.doAdd(getStampFee(),getJsfAmt(),getGhfAmt(),getZgfAmt(),getSxfAmt(),getSettleFee(),getCommissionAmt());
        }
        else {
            return ArithmeticUtil.doAdd(getStampFee(),getJsfAmt(),getGhfAmt(),getZgfAmt(),getSxfAmt(),getSettleFee());
        }

    }

    public void setTotalFeeAmtPay(BigDecimal totalFeeAmtPay) {
        this.totalFeeAmtPay = totalFeeAmtPay;
    }

    public BigDecimal getTotalUnFeeAmtPay() {
        if(StringUtil.equals(getIsPayWithTrade(), DictCodeList.是否_是.getValue())) {
            return getCommissionAmt();
        }
        else {
            return BigDecimal.ZERO;
        }
    }

    public void setTotalUnFeeAmtPay(BigDecimal totalUnFeeAmtPay) {
        this.totalUnFeeAmtPay = totalUnFeeAmtPay;
    }

    public SettleTransDataTotalAdd getSettleTransDataTotalAdd() {
        return settleTransDataTotalAdd;
    }

    public void setSettleTransDataTotalAdd(SettleTransDataTotalAdd settleTransDataTotalAdd) {
        this.settleTransDataTotalAdd = settleTransDataTotalAdd;
    }

    public String getrFinprodType() {
        return rFinprodType;
    }

    public void setrFinprodType(String rFinprodType) {
        this.rFinprodType = rFinprodType;
    }

    public List<SettleTransDataTotalPledge> getSettleTransDataTotalPledgeList() {
        return settleTransDataTotalPledgeList;
    }

    public void setSettleTransDataTotalPledgeList(List<SettleTransDataTotalPledge> settleTransDataTotalPledgeList) {
        this.settleTransDataTotalPledgeList = settleTransDataTotalPledgeList;
    }
}
