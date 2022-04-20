/*
 * 文件名：StayDrawSubprdEntity.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：dongjun
 * 修改时间：2018年9月28日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.accounting.entity;

import com.joyintech.tams.common.base.entity.BokSubject;

import java.math.BigDecimal;

/**
 * 待计提子产品现金流查询 - 实体类
 * 
 * @author dongjun
 * @version 1.0 2018年9月28日
 * @see StayDrawSubprdEntity
 * @since 1.0
 */
public class StayDrawSubprdEntity extends PrdCashEntity {

    /**
     * 交易场所
     */
    private String tradeMarket;
    /**
     * 分层类别
     */
    private String layeringType;
    /**
     * 分层代码
     */
    private String layeringId;
    /**
     * 分层代码
     */
    private String layeringName;
    /**
     * 上层代码代码
     */
    private String fLayeringId;
    
    /**
     * 业务明细类型
     */
    private String  busiDetailType;
    /**
     * 业务明细代码
     */
    private String  busiId;
    /**
     * 分支序号
     */
    private Long  branch;
    /**
     * 级别序号
     */
    private String  lev;
    /**
     * 当前级别
     */
    private String  curLev;
    /**
     * 下级最新序号
     */
    private String  sBranch;
    /**
     * 特殊核算模型
     */
    private String  bokSpecMode;
    
    /**
     * 实收资本
     */
    private BigDecimal capital;

    /**
     * 费用科目信息
     */
    private BokSubject feeSubjectNo;
    
    
    /**
     * 获取 tradeMarket
     * 
     * @return tradeMarket.
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 tradeMarket
     * 
     * @param tradeMarket tradeMarket
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    
    /**
     * 获取 layeringType
     * @return layeringType.
     */
    public String getLayeringType() {
        return layeringType;
    }

    
    /**
     * 设置 layeringType
     * @param layeringType layeringType
     */
    public void setLayeringType(String layeringType) {
        this.layeringType = layeringType;
    }

    
    /**
     * 获取 layeringId
     * @return layeringId.
     */
    public String getLayeringId() {
        return layeringId;
    }

    
    /**
     * 设置 layeringId
     * @param layeringId layeringId
     */
    public void setLayeringId(String layeringId) {
        this.layeringId = layeringId;
    }

    
    /**
     * 获取 fLayeringId
     * @return fLayeringId.
     */
    public String getfLayeringId() {
        return fLayeringId;
    }

    
    /**
     * 设置 fLayeringId
     * @param fLayeringId fLayeringId
     */
    public void setfLayeringId(String fLayeringId) {
        this.fLayeringId = fLayeringId;
    }

    
    /**
     * 获取 busiDetailType
     * @return busiDetailType.
     */
    public String getBusiDetailType() {
        return busiDetailType;
    }

    
    /**
     * 设置 busiDetailType
     * @param busiDetailType busiDetailType
     */
    public void setBusiDetailType(String busiDetailType) {
        this.busiDetailType = busiDetailType;
    }

    
    /**
     * 获取 busiId
     * @return busiId.
     */
    public String getBusiId() {
        return busiId;
    }

    
    /**
     * 设置 busiId
     * @param busiId busiId
     */
    public void setBusiId(String busiId) {
        this.busiId = busiId;
    }

    
    /**
     * 获取 branch
     * @return branch.
     */
    public Long getBranch() {
        return branch;
    }

    
    /**
     * 设置 branch
     * @param branch branch
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    
    /**
     * 获取 lev
     * @return lev.
     */
    public String getLev() {
        return lev;
    }

    
    /**
     * 设置 lev
     * @param lev lev
     */
    public void setLev(String lev) {
        this.lev = lev;
    }

    
    /**
     * 获取 curLev
     * @return curLev.
     */
    public String getCurLev() {
        return curLev;
    }

    
    /**
     * 设置 curLev
     * @param curLev curLev
     */
    public void setCurLev(String curLev) {
        this.curLev = curLev;
    }

    
    /**
     * 获取 sBranch
     * @return sBranch.
     */
    public String getsBranch() {
        return sBranch;
    }

    
    /**
     * 设置 sBranch
     * @param sBranch sBranch
     */
    public void setsBranch(String sBranch) {
        this.sBranch = sBranch;
    }

    
    /**
     * 获取 layeringName
     * @return layeringName.
     */
    public String getLayeringName() {
        return layeringName;
    }

    
    /**
     * 设置 layeringName
     * @param layeringName layeringName
     */
    public void setLayeringName(String layeringName) {
        this.layeringName = layeringName;
    }

    
    /**
     * 获取 特殊核算模型
     * @return bokSpecMode.
     */
    public String getBokSpecMode() {
        return bokSpecMode;
    }

    
    /**
     * 设置 特殊核算模型
     * @param bokSpecMode bokSpecMode
     */
    public void setBokSpecMode(String bokSpecMode) {
        this.bokSpecMode = bokSpecMode;
    }

    /**
     * 获取 实收资本
     * @return 实收资本
     */
    public BigDecimal getCapital() {
        return capital;
    }
    
    /**
     * 设置 实收资本
     * @param capital 实收资本
     */
    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    /**
     * 获取 费用科目信息
     * @return 费用科目信息
     */
    public BokSubject getFeeSubjectNo() {
        return feeSubjectNo;
    }

    /**
     * 设置 费用科目信息
     * @param feeSubjectNo 费用科目信息
     */
    public void setFeeSubjectNo(BokSubject feeSubjectNo) {
        this.feeSubjectNo = feeSubjectNo;
    }
}
