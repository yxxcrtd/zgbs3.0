/*
 * 文件名：TrdCommandDispatcher001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2018年9月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.investtrade.investdealquery.dto;

import com.joyintech.tams.common.base.entity.WflTrdProductDeal;

public class TrdInvestDealQuery001Dto extends WflTrdProductDeal{

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
	 * @return
	 */
	public String getValidStatus() {
		return validStatus;
	}
	/**
	 * 设置有效状态
	 * @param validStatus
	 */
	public void setValidStatus(String validStatus) {
		this.validStatus = validStatus;
	}
	/**
	 * 得到委托代码
	 * @return
	 */
	public String getEntrustId() {
		return entrustId;
	}
	/**
	 * 设置委托代码
	 * @param entrustId
	 */
	public void setEntrustId(String entrustId) {
		this.entrustId = entrustId;
	}
	/**
	 * 得到投资组合代码
	 * @return
	 */
	public String getPortfolioId() {
		return portfolioId;
	}
	/**
	 * 设置投资组合代码
	 * @param portfolioId
	 */
	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}
	/**
	 * 得到组合名称
	 * @return
	 */
	public String getPortfolioName() {
		return portfolioName;
	}
	/**
	 * 设置组合名称
	 * @param portfolioName
	 */
	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}
	/**
	 * 得到市场代码
	 * @return
	 */
	public String getFinprodMarketId() {
		return finprodMarketId;
	}
	/**
	 * 设置市场代码
	 * @param finprodMarketId
	 */
	public void setFinprodMarketId(String finprodMarketId) {
		this.finprodMarketId = finprodMarketId;
	}
	/**
	 * 得到金融产品简称
	 * @return
	 */
	public String getFinprodAbbr() {
		return finprodAbbr;
	}
	/**
	 * 设置金融产品简称
	 * @param finprodAbbr
	 */
	public void setFinprodAbbr(String finprodAbbr) {
		this.finprodAbbr = finprodAbbr;
	}
	/**
     * 得到回购拆借方向
     * @return
     */
    public String getBackLendDirection() {
        return backLendDirection;
    }
    /**
     * 设置回购拆借方向
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
