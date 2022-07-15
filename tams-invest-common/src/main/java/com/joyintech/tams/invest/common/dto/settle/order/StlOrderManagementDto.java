package com.joyintech.tams.invest.common.dto.settle.order;

import com.joyintech.framework.base.BaseEntity;

/**
 * @program: tams-invest-parent
 * @description:paBizTypeString
 * @author: 强成西
 * @create: 2019-01-19 17:31
 **/
public class StlOrderManagementDto extends BaseEntity {

    /**
     * 业务编号
     */
    private String bizCode;
    
    /**
     * 指令编号
     */
    private String orderId;

    /**
     * 平安业务类型
     */
    private String paBizType;

    /**
     * 划款类型
     */
    private String payType;

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
     * 产品收益类型
     */
    private String profitType;

    /**
     * 资产代码
     */
    private String finprodMarketId;

    /**
     * 资产名称
     */
    private String finprodName;

    /**
     * 结算日期
     */
    private String settleDate;

    /**
     * 结算金额
     */
    private String settleAmt;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 是否是DVP划款
     */
    private String isDvp;

    /**
     * 发送状态
     */
    private String sendStatus;

    /**
     * 发送时间
     */
    private String sendDate;
    /**
     * 指令接收状态
     */
    private String orderRecStatus;
    /**
     * 处理状态
     */
    private String orderRetStatus;

    // 查询列表中增加“付款账户”、“付款账户名称”、“收款账户”、“收款账户名称”、“划款用途”、“币种” 20191030 add by renping start
    /**
     * 用途
     */
    private String purpose;

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
     * 获取 purpose
     * 
     * @return purpose.
     */
    public String getPurpose() {
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
     * 获取 payAcctNo
     * 
     * @return payAcctNo.
     */
    public String getPayAcctNo() {
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
     * @return payAcctName.
     */
    public String getPayAcctName() {
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
     * @return recAcctNo.
     */
    public String getRecAcctNo() {
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
     * @return recAcctName.
     */
    public String getRecAcctName() {
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
    // 查询列表中增加“付款账户”、“付款账户名称”、“收款账户”、“收款账户名称”、“划款用途”、“币种” 20191030 add by renping end

    /**
     * 获取 sendDate
     *
     * @return sendDate
     */
    public String getSendDate() {
        return sendDate;
    }

    /**
     * 设置 sendDate
     *
     * @param sendDate sendDate
     */
    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    /**
     * 获取 orderRecStatus
     *
     * @return orderRecStatus
     */
    public String getOrderRecStatus() {
        if (orderRecStatus == null || "".equals(orderRecStatus)) {
            return "";
        }
        return orderRecStatus;
    }

    /**
     * 设置 orderRecStatus
     *
     * @param orderRecStatus orderRecStatus
     */
    public void setOrderRecStatus(String orderRecStatus) {
        this.orderRecStatus = orderRecStatus;
    }

    /**
     * 获取 orderRetStatus
     *
     * @return orderRetStatus
     */
    public String getOrderRetStatus() {
        if (orderRetStatus == null || "".equals(orderRetStatus)) {
            return "";
        }
        return orderRetStatus;
    }

    /**
     * 设置 orderRetStatus
     *
     * @param orderRetStatus orderRetStatus
     */
    public void setOrderRetStatus(String orderRetStatus) {
        this.orderRetStatus = orderRetStatus;
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
     * 获取 paBizType
     *
     * @return paBizType
     */
    public String getPaBizType() {
        if (paBizType == null || "".equals(paBizType))
            return "";
        return paBizType;
    }

    /**
     * 设置 paBizType
     *
     * @param paBizType paBizType
     */
    public void setPaBizType(String paBizType) {
        this.paBizType = paBizType;
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
    public String getSettleAmt() {
        if (settleAmt == null || "".equals(settleAmt))
            return "";
        return settleAmt;
    }

    /**
     * 设置 settleAmt
     *
     * @param settleAmt settleAmt
     */
    public void setSettleAmt(String settleAmt) {
        this.settleAmt = settleAmt;
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
     * 获取 isDvp
     *
     * @return isDvp
     */
    public String getIsDvp() {
        if (isDvp == null || "".equals(isDvp))
            return "";
        return isDvp;
    }

    /**
     * 设置 isDvp
     *
     * @param isDvp isDvp
     */
    public void setIsDvp(String isDvp) {
        this.isDvp = isDvp;
    }

    /**
     * 获取 profitType
     *
     * @return profitType
     */
    public String getProfitType() {
        return profitType;
    }

    /**
     * 设置 profitType
     *
     * @param profitType profitType
     */
    public void setProfitType(String profitType) {
        this.profitType = profitType;
    }

    /**
     * 获取 orderId
     * @return orderId.
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置 orderId
     * @param orderid orderId
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
