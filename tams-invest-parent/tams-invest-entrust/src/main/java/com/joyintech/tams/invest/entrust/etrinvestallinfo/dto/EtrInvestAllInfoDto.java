/*
 * 文件名：EtrInvestAllInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrinvestallinfo.dto;

import java.math.BigDecimal;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 所有投资委托查询dto 〈功能详细描述〉
 *
 * @author xiexinting
 * @version 1.0 2018年9月7日
 * @see EtrInvestAllInfoDto
 * @since 1.0
 */
public class EtrInvestAllInfoDto extends EtrEntrust {

	/**
	 * <意义，目的和功能，以及可能被用到的地方>
	 */
	private static final long serialVersionUID = 2542342953449761138L;
	/**
	 * 回购拆借方向
	 */
	private String busiPs;
	/**
	 * 资产类型
	 */
	private String assetType;
	/**
	 * 市场代码
	 */
	private String finprodMarketId;

	/**
	 * 组合名称
	 */
	private String portfolioName;

	/**
	 * 金融产品全称
	 */
	private String finprodName;

	/**
	 * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
	 */
	private String finprodType2;

	/**
	 * 清算速度
	 */
	private Long settleSpreed;

	/**
	 * 交易方向文字列
	 */
	private String psStr;

	/**
	 * 证券账户名称
	 */
	private String secAcctName;
	/**
	 * PB是否已导出
	 */
	private String pbExportstatus;

	/**
	 * 指令创建来源
	 */
	private String entrustCreateSource;

	private String busiTypePosition;

	/**
	 * <功能详细描述>
	 * @return
	 */
	public String getVdateStr(){
		return DateUtil.format(super.getVdate());
	}
	/**
	 * <功能详细描述>
	 * @return
	 */
	public String getMdateStr(){
		return DateUtil.format(super.getMdate());
	}
	/**
	 * 获取 settleSpreed
	 *
	 * @return settleSpreed.
	 */
	public Long getSettleSpreed() {
		return settleSpreed;
	}

	/**
	 * 设置 settleSpreed
	 *
	 * @param settleSpreed
	 *            settleSpreed
	 */
	public void setSettleSpreed(Long settleSpreed) {
		this.settleSpreed = settleSpreed;
	}

	/**
	 * 获取 busiPs
	 *
	 * @return busiPs.
	 */
	public String getBusiPs() {
		return busiPs;
	}

	/**
	 * 设置 busiPs
	 *
	 * @param busiPs
	 *            busiPs
	 */
	public void setBusiPs(String busiPs) {
		this.busiPs = busiPs;
	}

	/**
	 * 获取 assetType
	 *
	 * @return assetType.
	 */
	public String getAssetType() {
		return assetType;
	}

	public String getAssetTypeStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, this.getAssetType());
	}

	/**
	 * 设置 assetType
	 *
	 * @param assetType
	 *            assetType
	 */
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	/**
	 * 获取 finprodMarketId
	 *
	 * @return finprodMarketId.
	 */
	public String getFinprodMarketId() {
		return finprodMarketId;
	}

	/**
	 * 设置 finprodMarketId
	 *
	 * @param finprodMarketId
	 *            finprodMarketId
	 */
	public void setFinprodMarketId(String finprodMarketId) {
		this.finprodMarketId = finprodMarketId;
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
	 * @param portfolioName
	 *            portfolioName
	 */
	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}

	/**
	 * 获取 finprodName
	 *
	 * @return finprodName.
	 */
	public String getFinprodName() {
		return finprodName;
	}

	/**
	 * 设置 finprodName
	 *
	 * @param finprodName
	 *            finprodName
	 */
	public void setFinprodName(String finprodName) {
		this.finprodName = finprodName;
	}

	/**
	 * 获取 finprodType2
	 *
	 * @return finprodType2.
	 */
	public String getFinprodType2() {
		return finprodType2;
	}

	/**
	 * 设置 finprodType2
	 *
	 * @param finprodType2
	 *            finprodType2
	 */
	public void setFinprodType2(String finprodType2) {
		this.finprodType2 = finprodType2;
	}

	public String getEntrustStatusStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.委托状态, super.getEntrustStatus());
	}

	public String getTradeMarketStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
	}

	public String getTradePlatStr() {
		if (this.getSettleSpreed() == null) {
			return null;
		}
		return this.getSettleSpreed() == 0 ? DictCodeList.清算速度_T_0.getCodeName()
				: DictCodeList.清算速度_T_1.getCodeName();
	}

	public String getCcyStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());
	}

	public BigDecimal getEntrustAmtStr() {
		return super.getEntrustAmt() == null
				? (DictCodeList.平安资产类型_债券.getValue().equals(this.getAssetType())
				? super.getEntrustNum().multiply(new BigDecimal(100)) : super.getEntrustNum())
				: super.getEntrustAmt();
	}

	public String getPsStr() {
		return psStr;
	}

	public void setPsStr(String psStr) {
		this.psStr = psStr;
	}

	/**
	 * 获取 secAcctName
	 *
	 * @return secAcctName
	 */
	public String getSecAcctName() {
		return secAcctName;
	}

	/**
	 * 设置 secAcctName
	 *
	 * @param secAcctName String
	 */
	public void setSecAcctName(String secAcctName) {
		this.secAcctName = secAcctName;
	}

	/**
	 * 获取 PB是否已导出
	 * @return pbExportstatus
	 */
	public String getPbExportstatus() {
		return pbExportstatus;
	}
	/**
	 * 设置 PB是否已导出
	 * @param pbExportstatus
	 */
	public void setPbExportstatus(String pbExportstatus) {
		this.pbExportstatus = pbExportstatus;
	}
	/**
	 * 获取 PB是否已导出
	 * @return pbExportstatus
	 */
	public String getPbExportstatusStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.是否,
				this.getPbExportstatus() == null ? DictCodeList.是否_否.getValue() : this.getPbExportstatus());
	}

	/**
	 * 取得 指令创建来源
	 * @return 指令创建来源
	 */
	public String getEntrustCreateSource() {
		return entrustCreateSource;
	}

	/**
	 * 设置 指令创建来源
	 * 01：场内簿记
	 * 02：场内实时
	 * 03：银行间簿记
	 * 04：银行间实时
	 * @param entrustCreateSource 指令创建来源
	 */
	public void setEntrustCreateSource(String entrustCreateSource) {
		this.entrustCreateSource = entrustCreateSource == null ? null : entrustCreateSource.trim();
	}

	public String getBusiTypePosition() {
		return busiTypePosition;
	}

	public void setBusiTypePosition(String busiTypePosition) {
		this.busiTypePosition = busiTypePosition;
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
}
