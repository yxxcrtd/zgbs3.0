package com.joyintech.tams.invest.trade.transfer.sectransfer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * 债券交易拆分
 * @author zcw
 * @version 1.0 2019年11月21日
 * @see SecResolute001Dto
 * @since 1.0
 */
public class SecResolute001Dto extends BaseEntity implements ProcessEntity {
	/**
	 *
	 */
	private static final long serialVersionUID = 9170110132654594943L;

	/**
	 * 投资过度组合
	 */
	private String investPortfolioId;
	/**
	 * 所有相关表，贯穿整个流程
	 */
	private BaseTotalTradeInfoDto baseTotalTradeInfoDto;
	/**
	 * 拆分前交易编号
	 */
	private String tradeId;
	/**
	 * 画面元素
	 */
	private String elementId;
	/**
	 * 通道名称
	 */
	private String chlName;
	/**
	 * 拆分信息列表dtolist
	 */
	private List<SecResoluteDto> secResoluteDtoList;

	/**
	 * 风险试算实体类
	 */
	private List<RiskTrialRetDto> riskTrialRetDto;

	/**
	 * 操作方式
	 */
	private String operateType;
	/**
	 * 交易日期
	 */
	private Date tradeDate;
	/**
	 * 债券一级分类
	 */
	private String windTypeOne;
	/**
	 * 交割日期
	 */
	private Date settleDate;
	/**
	 * 交易方向
	 */
	private String ps;
	/**
	 * 资产代码
	 */
	private String finprodMarketId;
	/**
	 * 资产名称
	 */
	private String finprodAbbr;
	/**
	 * 交易场所
	 */
	private String tradeMarket;
	/**
	 * 净价
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal unitCprice;
	/**
	 * 应计利息
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal unitInt;
	/**
	 * 全价
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal unitFprice;
	/**
	 * 投资目的
	 */
	private String invAim;
	/**
	 * 证券托管户
	 */
	private String secManageAcctId;
	/**
	 * 通道
	 */
	private String chlId;
	/**
	 * 券面总额
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal shareAmt;
	/**
	 * 剩余券面总额
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal undistributedAmount;
	/**
	 * 已拆分金额
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal distributedAmount;
	/**
	 * 拆分信息—交易金额
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal tradeRepAmt;
	/**
	 * 币种
	 */
	private String ccy;

	/**
	 * 金融产品代码
	 */
	private String finprodId;

	/**
	 * 组合代码
	 */
	private String portfolioId;

	/**
	 * 交易手续费
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal procedureAmtS;
	/**
	 * 结算费
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal settleAmtS;
	/**
	 * 佣金
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal commissionAmtS;
	/**
	 * 通道费
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal channelFeeS;
	/**
	 * 交易金额
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal tradeRepAmtS;

	/**
	 * 券面总额
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal tradeAmt;

	/**
	 * 组合可用现金
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal avalibleCash;

	/**
	 * 资产可用持仓
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal assetPosition;

	/**
	 * pageModel
	 */
	private String pageModel;

	public List<PtlFundAllocationDto> ptlFundAllocationDtos;

	public List<PtlFundAllocationDto> getPtlFundAllocationDtos() {
		return ptlFundAllocationDtos;
	}

	public void setPtlFundAllocationDtos(List<PtlFundAllocationDto> ptlFundAllocationDtos) {
		this.ptlFundAllocationDtos = ptlFundAllocationDtos;
	}

	/**
	 * 单位净价
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal cprice;

	/**
	 * 单位应计利息
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal uint;

	/**
	 * 单位全价
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal fprice;

	private String calendarId;

	/**
	 * 通道费
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal chlFee;
	/**
	 * 交易手续费
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal proceFee;
	/**
	 * 结算费
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal settleFee;
	/**
	 * 佣金
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal commFee;

	/**
	 * 已拆分的交易金额
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal tradeRepAmtA;

	/**
	 * 附件上传
	 */
	private List<UploadFile> uploadFileList;

	/**
	 * 实体组合代码
	 */
	private String actPortfolioId;

	/**
	 * 已拆分的净价总额
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal cpriceAmt;

	/**
	 * 已拆分的全价总额
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal fpriceAmt;

	/**
	 * 已拆分的应计利息
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal intAmt;

	/**
	 * 转仓投资目的
	 */
	private String turnInvAim;


	/**
	 * 获取 转仓投资目的 turnInvAim
	 * @return turnInvAim.
	 */
	public String getTurnInvAim() {
		return turnInvAim;
	}

	/**
	 * 设置 转仓投资目的 turnInvAim
	 * @param turnInvAim
	 */
	public void setTurnInvAim(String turnInvAim) {
		this.turnInvAim = turnInvAim;
	}

	public String getInvestPortfolioId() {
		return investPortfolioId;
	}

	public void setInvestPortfolioId(String investPortfolioId) {
		this.investPortfolioId = investPortfolioId;
	}

	public BaseTotalTradeInfoDto getBaseTotalTradeInfoDto() {
		return baseTotalTradeInfoDto;
	}

	public void setBaseTotalTradeInfoDto(BaseTotalTradeInfoDto baseTotalTradeInfoDto) {
		this.baseTotalTradeInfoDto = baseTotalTradeInfoDto;
	}
	@PrimaryKey(Field.PK_1)
	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public List<SecResoluteDto> getSecResoluteDtoList() {
		return secResoluteDtoList;
	}

	public void setSecResoluteDtoList(List<SecResoluteDto> secResoluteDtoList) {
		this.secResoluteDtoList = secResoluteDtoList;
	}

	public List<RiskTrialRetDto> getRiskTrialRetDto() {
		return riskTrialRetDto;
	}

	public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
		this.riskTrialRetDto = riskTrialRetDto;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	public String getFinprodMarketId() {
		return finprodMarketId;
	}

	public void setFinprodMarketId(String finprodMarketId) {
		this.finprodMarketId = finprodMarketId;
	}

	public String getFinprodAbbr() {
		return finprodAbbr;
	}

	public void setFinprodAbbr(String finprodAbbr) {
		this.finprodAbbr = finprodAbbr;
	}

	public String getTradeMarket() {
		return tradeMarket;
	}

	public void setTradeMarket(String tradeMarket) {
		this.tradeMarket = tradeMarket;
	}

	public BigDecimal getUnitCprice() {
		return unitCprice;
	}

	public void setUnitCprice(BigDecimal unitCprice) {
		this.unitCprice = unitCprice;
	}

	public BigDecimal getUnitInt() {
		return unitInt;
	}

	public void setUnitInt(BigDecimal unitInt) {
		this.unitInt = unitInt;
	}

	public BigDecimal getUnitFprice() {
		return unitFprice;
	}

	public void setUnitFprice(BigDecimal unitFprice) {
		this.unitFprice = unitFprice;
	}

	public String getInvAim() {
		return invAim;
	}

	public void setInvAim(String invAim) {
		this.invAim = invAim;
	}

	public String getSecManageAcctId() {
		return secManageAcctId;
	}

	public void setSecManageAcctId(String secManageAcctId) {
		this.secManageAcctId = secManageAcctId;
	}

	public String getChlId() {
		return chlId;
	}

	public void setChlId(String chlId) {
		this.chlId = chlId;
	}

	public BigDecimal getShareAmt() {
		return shareAmt;
	}

	public void setShareAmt(BigDecimal shareAmt) {
		this.shareAmt = shareAmt;
	}

	public BigDecimal getUndistributedAmount() {
		return undistributedAmount;
	}

	public void setUndistributedAmount(BigDecimal undistributedAmount) {
		this.undistributedAmount = undistributedAmount;
	}

	public BigDecimal getDistributedAmount() {
		return distributedAmount;
	}

	public void setDistributedAmount(BigDecimal distributedAmount) {
		this.distributedAmount = distributedAmount;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public String getFinprodId() {
		return finprodId;
	}

	public void setFinprodId(String finprodId) {
		this.finprodId = finprodId;
	}

	public String getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}

	public BigDecimal getProcedureAmtS() {
		return procedureAmtS;
	}

	public void setProcedureAmtS(BigDecimal procedureAmtS) {
		this.procedureAmtS = procedureAmtS;
	}

	public BigDecimal getSettleAmtS() {
		return settleAmtS;
	}

	public void setSettleAmtS(BigDecimal settleAmtS) {
		this.settleAmtS = settleAmtS;
	}

	public BigDecimal getCommissionAmtS() {
		return commissionAmtS;
	}

	public void setCommissionAmtS(BigDecimal commissionAmtS) {
		this.commissionAmtS = commissionAmtS;
	}

	public BigDecimal getChannelFeeS() {
		return channelFeeS;
	}

	public void setChannelFeeS(BigDecimal channelFeeS) {
		this.channelFeeS = channelFeeS;
	}

	public BigDecimal getTradeRepAmtS() {
		return tradeRepAmtS;
	}

	public void setTradeRepAmtS(BigDecimal tradeRepAmtS) {
		this.tradeRepAmtS = tradeRepAmtS;
	}

	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public BigDecimal getAvalibleCash() {
		return avalibleCash;
	}

	public void setAvalibleCash(BigDecimal avalibleCash) {
		this.avalibleCash = avalibleCash;
	}

	public BigDecimal getAssetPosition() {
		return assetPosition;
	}

	public void setAssetPosition(BigDecimal assetPosition) {
		this.assetPosition = assetPosition;
	}

	public String getPageModel() {
		return pageModel;
	}

	public void setPageModel(String pageModel) {
		this.pageModel = pageModel;
	}

	public String getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}

	public List<UploadFile> getUploadFileList() {
		return uploadFileList;
	}

	public void setUploadFileList(List<UploadFile> uploadFileList) {
		this.uploadFileList = uploadFileList;
	}

	public String getActPortfolioId() {
		return actPortfolioId;
	}

	public void setActPortfolioId(String actPortfolioId) {
		this.actPortfolioId = actPortfolioId;
	}

	/**
	 * 获取 通道名称
	 *
	 * @return chlName 通道名称
	 */
	public String getChlName() {
		return this.chlName;
	}

	/**
	 * 设置 通道名称
	 *
	 * @param chlName 通道名称
	 */
	public void setChlName(String chlName) {
		this.chlName = chlName;
	}

	/**
	 * 获取 债券一级分类
	 *
	 * @return windTypeOne 债券一级分类
	 */
	public String getWindTypeOne() {
		return this.windTypeOne;
	}

	/**
	 * 设置 债券一级分类
	 *
	 * @param windTypeOne 债券一级分类
	 */
	public void setWindTypeOne(String windTypeOne) {
		this.windTypeOne = windTypeOne;
	}

	/**
	 * 获取 单位净价
	 *
	 * @return cprice 单位净价
	 */
	public BigDecimal getCprice() {
		return this.cprice;
	}

	/**
	 * 设置 单位净价
	 *
	 * @param cprice 单位净价
	 */
	public void setCprice(BigDecimal cprice) {
		this.cprice = cprice;
	}

	/**
	 * 获取 单位应计利息
	 *
	 * @return uint 单位应计利息
	 */
	public BigDecimal getUint() {
		return this.uint;
	}

	/**
	 * 设置 单位应计利息
	 *
	 * @param uint 单位应计利息
	 */
	public void setUint(BigDecimal uint) {
		this.uint = uint;
	}

	/**
	 * 获取 单位全价
	 *
	 * @return fprice 单位全价
	 */
	public BigDecimal getFprice() {
		return this.fprice;
	}

	/**
	 * 设置 单位全价
	 *
	 * @param fprice 单位全价
	 */
	public void setFprice(BigDecimal fprice) {
		this.fprice = fprice;
	}

	/**
	 * 获取 拆分信息—交易金额
	 *
	 * @return tradeRepAmt 拆分信息—交易金额
	 */
	public BigDecimal getTradeRepAmt() {
		return this.tradeRepAmt;
	}

	/**
	 * 设置 拆分信息—交易金额
	 *
	 * @param tradeRepAmt 拆分信息—交易金额
	 */
	public void setTradeRepAmt(BigDecimal tradeRepAmt) {
		this.tradeRepAmt = tradeRepAmt;
	}

	/**
	 * 获取 通道费
	 *
	 * @return chlFee 通道费
	 */
	public BigDecimal getChlFee() {
		return this.chlFee;
	}

	/**
	 * 设置 通道费
	 *
	 * @param chlFee 通道费
	 */
	public void setChlFee(BigDecimal chlFee) {
		this.chlFee = chlFee;
	}

	/**
	 * 获取 交易手续费
	 *
	 * @return proceFee 交易手续费
	 */
	public BigDecimal getProceFee() {
		return this.proceFee;
	}

	/**
	 * 设置 交易手续费
	 *
	 * @param proceFee 交易手续费
	 */
	public void setProceFee(BigDecimal proceFee) {
		this.proceFee = proceFee;
	}

	/**
	 * 获取 结算费
	 *
	 * @return settleFee 结算费
	 */
	public BigDecimal getSettleFee() {
		return this.settleFee;
	}

	/**
	 * 设置 结算费
	 *
	 * @param settleFee 结算费
	 */
	public void setSettleFee(BigDecimal settleFee) {
		this.settleFee = settleFee;
	}

	/**
	 * 获取 佣金
	 *
	 * @return commFee 佣金
	 */
	public BigDecimal getCommFee() {
		return this.commFee;
	}

	/**
	 * 设置 佣金
	 *
	 * @param commFee 佣金
	 */
	public void setCommFee(BigDecimal commFee) {
		this.commFee = commFee;
	}

	/**
	 * 获取 已拆分的净价总额
	 *
	 * @return cpriceAmt 已拆分的净价总额
	 */
	public BigDecimal getCpriceAmt() {
		return this.cpriceAmt;
	}

	/**
	 * 设置 已拆分的净价总额
	 *
	 * @param cpriceAmt 已拆分的净价总额
	 */
	public void setCpriceAmt(BigDecimal cpriceAmt) {
		this.cpriceAmt = cpriceAmt;
	}

	/**
	 * 获取 已拆分的全价总额
	 *
	 * @return fpriceAmt 已拆分的全价总额
	 */
	public BigDecimal getFpriceAmt() {
		return this.fpriceAmt;
	}

	/**
	 * 设置 已拆分的全价总额
	 *
	 * @param fpriceAmt 已拆分的全价总额
	 */
	public void setFpriceAmt(BigDecimal fpriceAmt) {
		this.fpriceAmt = fpriceAmt;
	}

	/**
	 * 获取 已拆分的应计利息
	 *
	 * @return intAmt 已拆分的应计利息
	 */
	public BigDecimal getIntAmt() {
		return this.intAmt;
	}

	/**
	 * 设置 已拆分的应计利息
	 *
	 * @param intAmt 已拆分的应计利息
	 */
	public void setIntAmt(BigDecimal intAmt) {
		this.intAmt = intAmt;
	}

	/**
	 * 获取 已拆分的交易金额
	 *
	 * @return tradeRepAmtA 已拆分的交易金额
	 */
	public BigDecimal getTradeRepAmtA() {
		return this.tradeRepAmtA;
	}

	/**
	 * 设置 已拆分的交易金额
	 *
	 * @param tradeRepAmtA 已拆分的交易金额
	 */
	public void setTradeRepAmtA(BigDecimal tradeRepAmtA) {
		this.tradeRepAmtA = tradeRepAmtA;
	}

	private String isNeedAuto;

	public String getIsNeedAuto() {
		return isNeedAuto;
	}

	public void setIsNeedAuto(String isNeedAuto) {
		this.isNeedAuto = isNeedAuto;
	}

	/**
	 * 转仓编号
	 */
	private String zcId;

	public String getZcId() {
		return zcId;
	}

	public void setZcId(String zcId) {
		this.zcId = zcId;
	}

	private List<BussTrdTransferOrderDto> bussTrdTransferOrder;

	public List<BussTrdTransferOrderDto> getBussTrdTransferOrder() {
		return bussTrdTransferOrder;
	}

	public void setBussTrdTransferOrder(List<BussTrdTransferOrderDto> bussTrdTransferOrder) {
		this.bussTrdTransferOrder = bussTrdTransferOrder;
	}
}
