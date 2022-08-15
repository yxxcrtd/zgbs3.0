package com.joyintech.tams.invest.common.dto.settle.dvporder;

import java.math.BigDecimal;

import com.joyintech.framework.base.BaseEntity;

/**
 * @program: tams-invest-parent
 * @description:
 * @author: 强成西
 * @create: 2019-01-03 10:56
 **/
public class DvpOrdersManagementDto extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -7581496063904255012L;

    /**
     * 业务编号
     */
    private String bizCode;

    /**
     * 流程状态
     */
    private String statusValid;

    /**
     * 平安业务类型
     */
    private String paBussType;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 资产代码
     */
    private String finprodMarketId;

    /**
     * 资产名称
     */
    private String finprodName;

    /**
     * 交易日
     */
    private String tradeDate;

    /**
     * 处理状态
     */
    private String dealStatus;

    /**
     * 是否发送指令
     */
    private String isNeedSend;

    /**
     * 收支方向
     */
    private String ps;

    /**
     * 划款类型
     */
    private String payType;

    /**
     * 账户类型
     */
    private String acctType;

    /**
     * 支付日期/交割日
     */
    private String settleDate;

    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 交易金额
     */
    private BigDecimal tradeAmt;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 对手方资金账户
     */
    private String counterAcctNo;

    /**
     * 本方资金账户
     */
    private String myAcctNo;

    /**
     * 付款账号
     */
    private String payAcctNo;

    /**
     * 付款账号名称
     */
    private String payAcctName;

    /**
     * 收款账号
     */
    private String recAcctNo;

    /**
     * 收款账号名称
     */
    private String recAcctName;

    /**
     * 收款账号网点/支付系统号
     */
    private String largePayNo;

    /**
     * 用途
     */
    private String purpose;

    /**
     * 备注
     */
    private String remark;


    /**
     * 发送状态
     */
    private String sendStatus;

    /**
     * 登记托管机构
     */
    private String registOrg;

    /**
     * 交易场所
     */
    private String tradeMarket;
    
    /**
     * 指令状态，新建，撤销
     */
    private String orderStatus;

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取 sendStatus
     *
     * @return sendStatus
     */
    public String getSendStatus() {
        if (sendStatus == null || "".equals(sendStatus))
            return "";
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
     * 获取 bizCode
     *
     * @return bizCode
     */
    public String getBizCode() {
        if (bizCode == null || "".equals(bizCode))
            return "";
        return bizCode;
    }

    /**
     * 设置 bizCode
     *
     * @param bizCode bizCode
     */
    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    /**
     * 获取 statusValid
     *
     * @return statusValid
     */
    public String getStatusValid() {
        if (statusValid == null || "".equals(statusValid))
            return "";
        return statusValid;
    }

    /**
     * 设置 statusValid
     *
     * @param statusValid statusValid
     */
    public void setStatusValid(String statusValid) {
        this.statusValid = statusValid;
    }

    /**
     * 获取 paBussType
     *
     * @return paBussType
     */
    public String getPaBussType() {
        if (paBussType == null || "".equals(paBussType))
            return "";
        return paBussType;
    }

    /**
     * 设置 paBussType
     *
     * @param paBussType paBussType
     */
    public void setPaBussType(String paBussType) {
        this.paBussType = paBussType;
    }

    /**
     * 获取 portfolioId
     *
     * @return portfolioId
     */
    public String getPortfolioId() {
        if (portfolioId == null || "".equals(portfolioId))
            return "";
        return portfolioId;
    }

    /**
     * 设置 portfolioId
     *
     * @param portfolioId portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取 portfolioName
     *
     * @return portfolioName
     */
    public String getPortfolioName() {
        if (portfolioName == null || "".equals(portfolioName))
            return "";
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
     * 获取 finprodId
     *
     * @return finprodId
     */
    public String getFinprodId() {
        if (finprodId == null || "".equals(finprodId))
            return "";
        return finprodId;
    }

    /**
     * 设置 finprodId
     *
     * @param finprodId finprodId
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 获取 finprodMarketId
     *
     * @return finprodMarketId
     */
    public String getFinprodMarketId() {
        if (finprodMarketId == null || "".equals(finprodMarketId))
            return "";
        return finprodMarketId;
    }

    /**
     * 设置 finprodMarketId
     *
     * @param finprodMarketId finprodMarketId
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    /**
     * 获取 finprodName
     *
     * @return finprodName
     */
    public String getFinprodName() {
        if (finprodName == null || "".equals(finprodName))
            return "";
        return finprodName;
    }

    /**
     * 设置 finprodName
     *
     * @param finprodName finprodName
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    /**
     * 获取 tradeDate
     *
     * @return tradeDate
     */
    public String getTradeDate() {
        if (tradeDate == null || "".equals(tradeDate))
            return "";
        return tradeDate;
    }

    /**
     * 设置 tradeDate
     *
     * @param tradeDate tradeDate
     */
    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    /**
     * 获取 dealStatus
     *
     * @return dealStatus
     */
    public String getDealStatus() {
        if (dealStatus == null || "".equals(dealStatus))
            return "";
        return dealStatus;
    }

    /**
     * 设置 dealStatus
     *
     * @param dealStatus dealStatus
     */
    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    /**
     * 获取 isNeedSend
     *
     * @return isNeedSend
     */
    public String getIsNeedSend() {
        if (isNeedSend == null || "".equals(isNeedSend))
            return "";
        return isNeedSend;
    }

    /**
     * 设置 isNeedSend
     *
     * @param isNeedSend isNeedSend
     */
    public void setIsNeedSend(String isNeedSend) {
        this.isNeedSend = isNeedSend;
    }

    /**
     * 获取 ps
     *
     * @return ps
     */
    public String getPs() {
        if (ps == null || "".equals(ps))
            return "";
        return ps;
    }

    /**
     * 设置 ps
     *
     * @param ps ps
     */
    public void setPs(String ps) {
        this.ps = ps;
    }

    /**
     * 获取 payType
     *
     * @return payType
     */
    public String getPayType() {
        if (payType == null || "".equals(payType))
            return "";
        return payType;
    }

    /**
     * 设置 payType
     *
     * @param payType payType
     */
    public void setPayType(String payType) {
        this.payType = payType;
    }

    /**
     * 获取 acctType
     *
     * @return acctType
     */
    public String getAcctType() {
        if (acctType == null || "".equals(acctType))
            return "";
        return acctType;
    }

    /**
     * 设置 acctType
     *
     * @param acctType acctType
     */
    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }

    /**
     * 获取 settleDate
     *
     * @return settleDate
     */
    public String getSettleDate() {
        if (settleDate == null || "".equals(settleDate))
            return "";
        return settleDate;
    }

    /**
     * 设置 settleDate
     *
     * @param settleDate settleDate
     */
    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * 获取 settleAmt
     *
     * @return settleAmt
     */
    public BigDecimal getSettleAmt() {
        if (settleAmt == null)
            return BigDecimal.ZERO;
        return settleAmt;
    }

    /**
     * 设置 settleAmt
     *
     * @param settleAmt settleAmt
     */
    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    /**
     * 获取 tradeAmt
     *
     * @return tradeAmt
     */
    public BigDecimal getTradeAmt() {
        if (tradeAmt == null)
            return BigDecimal.ZERO;
        return tradeAmt;
    }

    /**
     * 设置 tradeAmt
     *
     * @param tradeAmt tradeAmt
     */
    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    /**
     * 获取 ccy
     *
     * @return ccy
     */
    public String getCcy() {
        if (ccy == null || "".equals(ccy))
            return "";
        return ccy;
    }

    /**
     * 设置 ccy
     *
     * @param ccy ccy
     */
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * 获取 counterAcctNo
     *
     * @return counterAcctNo
     */
    public String getCounterAcctNo() {
        if (counterAcctNo == null || "".equals(counterAcctNo))
            return "";
        return counterAcctNo;
    }

    /**
     * 设置 counterAcctNo
     *
     * @param counterAcctNo counterAcctNo
     */
    public void setCounterAcctNo(String counterAcctNo) {
        this.counterAcctNo = counterAcctNo;
    }

    /**
     * 获取 myAcctNo
     *
     * @return myAcctNo
     */
    public String getMyAcctNo() {
        if (myAcctNo == null || "".equals(myAcctNo))
            return "";
        return myAcctNo;
    }

    /**
     * 设置 myAcctNo
     *
     * @param myAcctNo myAcctNo
     */
    public void setMyAcctNo(String myAcctNo) {
        this.myAcctNo = myAcctNo;
    }

    /**
     * 获取 payAcctNo
     *
     * @return payAcctNo
     */
    public String getPayAcctNo() {
        if (payAcctNo == null || "".equals(payAcctNo))
            return "";
        return payAcctNo;
    }

    /**
     * 设置 payAcctNo
     *
     * @param payAcctNo payAcctNo
     */
    public void setPayAcctNo(String payAcctNo) {
        this.payAcctNo = payAcctNo;
    }

    /**
     * 获取 payAcctName
     *
     * @return payAcctName
     */
    public String getPayAcctName() {
        if (payAcctName == null || "".equals(payAcctName))
            return "";
        return payAcctName;
    }

    /**
     * 设置 payAcctName
     *
     * @param payAcctName payAcctName
     */
    public void setPayAcctName(String payAcctName) {
        this.payAcctName = payAcctName;
    }

    /**
     * 获取 recAcctNo
     *
     * @return recAcctNo
     */
    public String getRecAcctNo() {
        if (recAcctNo == null || "".equals(recAcctNo))
            return "";
        return recAcctNo;
    }

    /**
     * 设置 recAcctNo
     *
     * @param recAcctNo recAcctNo
     */
    public void setRecAcctNo(String recAcctNo) {
        this.recAcctNo = recAcctNo;
    }

    /**
     * 获取 recAcctName
     *
     * @return recAcctName
     */
    public String getRecAcctName() {
        if (recAcctName == null || "".equals(recAcctName))
            return "";
        return recAcctName;
    }

    /**
     * 设置 recAcctName
     *
     * @param recAcctName recAcctName
     */
    public void setRecAcctName(String recAcctName) {
        this.recAcctName = recAcctName;
    }

    /**
     * 获取 largePayNo
     *
     * @return largePayNo
     */
    public String getLargePayNo() {
        if (largePayNo == null || "".equals(largePayNo))
            return "";
        return largePayNo;
    }

    /**
     * 设置 largePayNo
     *
     * @param largePayNo largePayNo
     */
    public void setLargePayNo(String largePayNo) {
        this.largePayNo = largePayNo;
    }

    /**
     * 获取 purpose
     *
     * @return purpose
     */
    public String getPurpose() {
        if (purpose == null || "".equals(purpose))
            return "";
        return purpose;
    }

    /**
     * 设置 purpose
     *
     * @param purpose purpose
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * 获取 registOrg
     *
     * @return registOrg
     */
    public String getRegistOrg() {
        return registOrg;
    }

    /**
     * 设置 registOrg
     *
     * @param registOrg registOrg
     */
    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

    /**
     * 获取 remark (备注)
     *
     * @return remark
     */
    public String getRemark() {
        if (remark == null || "".equals(remark))
            return "";
        return remark;
    }

    /**
     * 设置 remark (备注)
     *
     * @param remark remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }
}
