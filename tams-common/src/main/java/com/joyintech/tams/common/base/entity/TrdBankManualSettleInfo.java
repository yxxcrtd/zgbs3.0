package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述:TRD_BANK_MANUAL_SETTLE_INFO表的实体类
 * @version
 * @author:  Administrator
 * @创建时间: 2021-11-16
 */
public class TrdBankManualSettleInfo extends BaseEntity {
    /**
     * 交收编号
     */
    private String settleSeqNo;

    /**
     * 交收状态
     */
    private String settleStatus;

    /**
     * 原业务编号
     */
    private String tradeId;

    /**
     * 交收日期
     */
    private Date settleDate;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 交割数量
     */
    private BigDecimal settleNum;

    /**
     * 交割金额
     */
    private BigDecimal settleAmt;

    /**
     * 业务类别
     */
    private String busiType;

    /**
     * 交收失败原因
     */
    private String remark;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 类型
     */
    private String finprodType2;
    /**
     * 交收编号
     * @return SETTLE_SEQ_NO 交收编号
     */
    public String getSettleSeqNo() {
        return settleSeqNo;
    }

    /**
     * 交收编号
     * @param settleSeqNo 交收编号
     */
    public void setSettleSeqNo(String settleSeqNo) {
        this.settleSeqNo = settleSeqNo == null ? null : settleSeqNo.trim();
    }

    /**
     * 交收状态
     * @return SETTLE_STATUS 交收状态
     */
    public String getSettleStatus() {
        return settleStatus;
    }

    /**
     * 交收状态
     * @param settleStatus 交收状态
     */
    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus == null ? null : settleStatus.trim();
    }

    /**
     * 原业务编号
     * @return TRADE_ID 原业务编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 原业务编号
     * @param tradeId 原业务编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }

    /**
     * 交收日期
     * @return SETTLE_DATE 交收日期
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 交收日期
     * @param settleDate 交收日期
     */
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * 金融产品代码
     * @return FINPROD_ID 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 金融产品代码
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 交割数量
     * @return SETTLE_NUM 交割数量
     */
    public BigDecimal getSettleNum() {
        return settleNum;
    }

    /**
     * 交割数量
     * @param settleNum 交割数量
     */
    public void setSettleNum(BigDecimal settleNum) {
        this.settleNum = settleNum;
    }

    /**
     * 交割金额
     * @return SETTLE_AMT 交割金额
     */
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    /**
     * 交割金额
     * @param settleAmt 交割金额
     */
    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    /**
     * 业务类别
     * @return BUSI_TYPE 业务类别
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * 业务类别
     * @param busiType 业务类别
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType == null ? null : busiType.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }
}