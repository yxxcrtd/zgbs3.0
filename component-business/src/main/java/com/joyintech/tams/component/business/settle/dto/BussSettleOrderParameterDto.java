/*
 * 文件名：BussSettleOrderParameterDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年09月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.component.business.settle.dto;

import java.util.List;

import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;

public class BussSettleOrderParameterDto {
    /**
     * 是否汇总交易
     */
    private String isTotalTrade;
    /**
     * 页面id
     */
    private String repPageId;
    /**
     * 拆借类型
     */
    private String bankLendingType;
    /**
     * 组合代码
     */
    private String portfolioId;
    /**
     * 投资目的
     */
    private String invAim;
    /**
     * 金融产品交易Entity
     */
    private TrdProductDeal deal;

    /**
     * 债券质押回购多组合新增
     * 张庚林
     * 20190723
     */
    private int mun;
    
    /**
     * 债券质押回购多组合新增
     * 张庚林
     * 20190723
     */
    private List<TrdProductDeal> dealList;
    
    /**
     * 交易方式
     * wangpeng 20190724
     */
    private String dealMode;
    /**
     * 是否多组合
     */
    private String isDzh;

    private String jiaoyiType;
    
    /**
     * 收支方向
     * add by wangpeng 20191111
     */
    private String ieFlag;

    /**
     * 获取 isTotalTrade
     *
     * @return isTotalTrade
     */
    public String getIsTotalTrade() {
        return isTotalTrade;
    }

    /**
     * 设置 isTotalTrade
     *
     * @param isTotalTrade isTotalTrade
     */
    public void setIsTotalTrade(String isTotalTrade) {
        this.isTotalTrade = isTotalTrade;
    }

    /**
     * 获取 bankLendingType
     *
     * @return bankLendingType
     */
    public String getBankLendingType() {
        return bankLendingType;
    }

    /**
     * 设置 bankLendingType
     *
     * @param bankLendingType bankLendingType
     */
    public void setBankLendingType(String bankLendingType) {
        this.bankLendingType = bankLendingType;
    }

    /**
     * 获取 portfolioId
     *
     * @return portfolioId
     */
    public String getPortfolioId() {
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
     * 获取 deal
     *
     * @return deal
     */
    public TrdProductDeal getDeal() {
        return deal;
    }

    /**
     * 设置 deal
     *
     * @param deal deal
     */
    public void setDeal(TrdProductDeal deal) {
        this.deal = deal;
    }

    
    /**
     * 获取 dealList
     * @return dealList.
     */
    public List<TrdProductDeal> getDealList() {
        return dealList;
    }

    
    /**
     * 设置 dealList
     * @param dealList dealList
     */
    public void setDealList(List<TrdProductDeal> dealList) {
        this.dealList = dealList;
    }

    
    /**
     * 获取 mun
     * @return mun.
     */
    public int getMun() {
        return mun;
    }

    
    /**
     * 设置 mun
     * @param mun mun
     */
    public void setMun(int mun) {
        this.mun = mun;
    }

    


    /**
     * 取得 repPageId
     * @return repPageId
     */
    public String getRepPageId() {
        return repPageId;
    }

    /**
     * 设置 repPageId
     * @param repPageId null
     */
    public void setRepPageId(String repPageId) {
        this.repPageId = repPageId;
    }

    /**
     * 获取 invAim
     *
     * @return invAim
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 invAim
     *
     * @param invAim invAim
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

	public String getDealMode() {
		return dealMode;
	}

	public void setDealMode(String dealMode) {
		this.dealMode = dealMode;
	}

    public String getIsDzh() {
        return isDzh;
    }

    public void setIsDzh(String isDzh) {
        this.isDzh = isDzh;
    }

    public String getJiaoyiType() {
        return jiaoyiType;
    }

    public void setJiaoyiType(String jiaoyiType) {
        this.jiaoyiType = jiaoyiType;
    }

	public String getIeFlag() {
		return ieFlag;
	}

	public void setIeFlag(String ieFlag) {
		this.ieFlag = ieFlag;
	}

    private String secManageAcctId;

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }
}
