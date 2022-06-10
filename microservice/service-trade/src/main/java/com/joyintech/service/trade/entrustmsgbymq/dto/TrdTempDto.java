/*
 * 文件名：TrdTempDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：tanxuan
 * 修改时间：2020年5月26日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.entrustmsgbymq.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.position.dto.PositionBaseData;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 成交消息预清算
 *
 * @author tanxuan
 * @version 1.0 2020年5月26日
 *
 *
 */
public class TrdTempDto extends TrdProductDeal{

	/**
	 * <意义，目的和功能，以及可能被用到的地方>
	 */
	private static final long serialVersionUID = -7438510730454849159L;

	/**
	 * 是否有锁定期
	 */
	private String withLockPeriod;

	/**
	 * 组合分配
	 */
	private List<BussTrdTransfenpeiDto> bussTrdTransfenpeiDto;

	/**
	 * 锁定截止日
	 */
	private Date lockMdate;

	/**
	 * 有效状态
	 */
	private String validStatus;

	/**
	 * 委托代码
	 */
	private String entrustId;

	/**
	 * 资产名称
	 */
	private String finprodAbbr;

	/**
	 * 组合代码
	 */
	private String portfolioId;

	/**
	 * 实体组合代码
	 */
	private String actPortfolioId;

	/**
	 * 组合名称
	 */
	private String portfolioName;

	/**
	 * 资产名称
	 */
	private String finprodName;

	/**
	 * 交易日历
	 */
	private String calendarId;

	/**
	 * 操作类型
	 */
	private String operateType;

	/**
	 * 画面模式
	 */
	private String pageModel;

	/**
	 * 工作流状态
	 */
	private String workflowStatus;

	/**
	 * 计算类型
	 */
	private String calculateField;

	/**
	 * 金融产品交易
	 */
	private TrdProductTempDeal trdProductTempDeal;

	/**
	 * 金融产品交易附属
	 */
	private TrdProductTempDealAdd trdProductTempDealAdd;

	/**
	 * 金融产品交易,到期续作的的购回交易
	 */
	private TrdProductTempDeal trdProductTempDealB;

	/**
	 * 金融产品交易附属，到期续作的的购回交易
	 */
	private TrdProductTempDealAdd trdProductTempDealAddB;

	/**
	 * 回购质押品
	 */
	private FinDealPledge finDealPledge;

	/**
	 * 回购质押品列表
	 */
	private List<FinDealPledge> finDealPledgeList;

	/**
	 * 交易类金融产品
	 */
	private FinTradeProduct finTradeProduct;

	/**
	 * 金融产品分类
	 */
	private FinProductType finProductType;

	/**
	 * 组合交易
	 */
	private List<PtlTrade> ptlTrade;

	/**
	 * 金融产品交易扩展
	 */
	private PaTrdProductDeal paTrdProductDeal;

	/**
	 * 划款指令
	 */
	private List<BussTrdTransferOrderDto> bussTrdTransferOrder;

	/**
	 * 人员信息
	 */
	@Valid
	private TrdInvestPersonInfo002Dto trdInvestPersonInfo;

	/**
	 * 持仓信息的基础数据
	 */
	private PositionBaseData positionBaseData;

	/**
	 * 撤销说明
	 */
	private String revokeReason;

	/**
	 * 金融产品代码（系统自动生成）
	 */
	private String systemFinprodId;

	/**
	 * 委托数量
	 */
	private BigDecimal entrustAmt;

	/**
	 * 委托额度
	 */
	private BigDecimal entrustNum;

	/**
	 * 委托单价
	 */
	private BigDecimal price;

	/**
	 * 完成数量
	 */
	private BigDecimal finishAmt;

	/**
	 * 通道性质
	 */
	private String payType;

	/**
	 * 金额信息
	 */
	private String amtInfo;

	/**
	 * 证券托管户
	 */
	private String secAcctId;

	/**
	 * 费用明细表
	 */
	private TrdTradeFeeDetail trdTradeFeeDetail;

	/**
	 * 交易金额
	 */
	private BigDecimal dealAmt;

	/**
	 * 资产可用持仓
	 */
	private BigDecimal bottomAssetAmount;

	/**
	 * 托管户剩余现金
	 */
	private BigDecimal accAvailableCash;

	/**
	 * 资金账户可用现金
	 */
	private BigDecimal tradeCash;

	/**
	 * 组合T日可用现金
	 */
	private BigDecimal avalibleCash;
	/**
	 * 交易方式
	 */
	private String dealMode;

	/**
	 * 印花税
	 */
	private BigDecimal stampDuty;

	/**
	 * 佣金
	 */
	private BigDecimal commission;

	/**
	 * 印花税是否随交易支付
	 */
	private String stampDutyIsPayWithTrade;

	/**
	 * 佣金是否随交易支付
	 */
	private String commissionIsPayWithTrade;

	/**
	 * 是否手工交收
	 */
	private String isManualSettle;

	/**
	 * 交易保证金
	 */
	private BigDecimal dealMargin;

	/**
	 * 质押券置换临时表
	 */
	private FinPledgeDispTemp finPledgeDispTemp;

	/**
	 * 质押券置换明细临时表
	 */
	private List<FinPledgeDispDetailTemp> finPledgeDispDetailTempList;

	/**
	 * 是否指令完成
	 */
	private String isFinish;

	/**
	 * 有无指令编号
	 */
	private String isEntrustId;

	/**
	 * 资产类型
	 */
	private String assetType;

	/**
	 * 数据参数
	 */
	private Object data;
	/**
	 * 续做类型
	 */
	private String redoType;

	/**
	 * 划款指令编号
	 */
	private String orderId;

	// add by etliu 20220116 start
	// GZYHTZJY-1145: 【场内对账清结算1231】深交所协议回购首期交易，日终头寸更新有误【解决日期：2022-01-14】】
	/**
	 * 投资目的
	 */
	private String pledgeInvAim;
	/**
	 * 投资目的
	 */
	private String newPledgeInvAim;
	/**
	 * 投资目的
	 */
	private String finDealPledgePledgeInvAim;
	/**
	 * 投资目的
	 */
	private String dataPledgeInvAim;
	/**
	 * 投资目的
	 */
	private String finPledgeDispDetailPledgeInvAim;

	public String getPledgeInvAim() {
		return pledgeInvAim;
	}

	public void setPledgeInvAim(String pledgeInvAim) {
		this.pledgeInvAim = pledgeInvAim;
	}

	public String getNewPledgeInvAim() {
		return newPledgeInvAim;
	}

	public void setNewPledgeInvAim(String newPledgeInvAim) {
		this.newPledgeInvAim = newPledgeInvAim;
	}

	public String getFinDealPledgePledgeInvAim() {
		return finDealPledgePledgeInvAim;
	}

	public void setFinDealPledgePledgeInvAim(String finDealPledgePledgeInvAim) {
		this.finDealPledgePledgeInvAim = finDealPledgePledgeInvAim;
	}

	public String getDataPledgeInvAim() {
		return dataPledgeInvAim;
	}

	public void setDataPledgeInvAim(String dataPledgeInvAim) {
		this.dataPledgeInvAim = dataPledgeInvAim;
	}

	public String getFinPledgeDispDetailPledgeInvAim() {
		return finPledgeDispDetailPledgeInvAim;
	}

	public void setFinPledgeDispDetailPledgeInvAim(String finPledgeDispDetailPledgeInvAim) {
		this.finPledgeDispDetailPledgeInvAim = finPledgeDispDetailPledgeInvAim;
	}
	// add by etliu 20220116 end

	public String getIsFinish() {
		return isFinish;
	}

	public void setIsFinish(String isFinish) {
		this.isFinish = isFinish;
	}

	public String getIsEntrustId() {
		return isEntrustId;
	}

	public void setIsEntrustId(String isEntrustId) {
		this.isEntrustId = isEntrustId;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getIsManualSettle() {
		return isManualSettle;
	}

	public void setIsManualSettle(String isManualSettle) {
		this.isManualSettle = isManualSettle;
	}

	/**
	 * 获取锁定期
	 *
	 * @return
	 */
	public String getWithLockPeriod() {
		return withLockPeriod;
	}

	/**
	 * 设置锁定期
	 *
	 * @param withLockPeriod
	 */

	public void setWithLockPeriod(String withLockPeriod) {
		this.withLockPeriod = withLockPeriod;
	}

	public List<BussTrdTransfenpeiDto> getBussTrdTransfenpeiDto() {
		return bussTrdTransfenpeiDto;
	}

	public void setBussTrdTransfenpeiDto(List<BussTrdTransfenpeiDto> bussTrdTransfenpeiDto) {
		this.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
	}

	/**
	 * 获取资产名称
	 *
	 * @return
	 */
	public String getFinprodAbbr() {
		return finprodAbbr;
	}

	/**
	 * 设置资产名称
	 *
	 * @param finprodAbbr
	 */
	public void setFinprodAbbr(String finprodAbbr) {
		this.finprodAbbr = finprodAbbr;
	}

	/**
	 * 获取 payType
	 *
	 * @return payType
	 */
	public String getPayType() {
		return payType;
	}

	/**
	 * 获取金额信息
	 *
	 * @return
	 */
	public String getAmtInfo() {
		return amtInfo;
	}

	/**
	 * 设置金额信息
	 *
	 * @param amtInfo
	 */
	public void setAmtInfo(String amtInfo) {
		this.amtInfo = amtInfo;
	}

	/**
	 * 设置 payType
	 *
	 * @param payType
	 *            payType
	 */
	public void setPayType(String payType) {
		this.payType = payType;
	}

	/**
	 * 获取证券托管户
	 *
	 * @return
	 */
	public String getSecAcctId() {
		return secAcctId;
	}

	/**
	 * 设置证券托管户
	 *
	 * @param secAcctId
	 */
	public void setSecAcctId(String secAcctId) {
		this.secAcctId = secAcctId;
	}

	/**
	 * 获取 bottomAssetAmount
	 *
	 * @return bottomAssetAmount.
	 */
	public BigDecimal getBottomAssetAmount() {
		return bottomAssetAmount;
	}

	/**
	 * 设置 bottomAssetAmount
	 *
	 * @param bottomAssetAmount
	 *            bottomAssetAmount
	 */
	public void setBottomAssetAmount(BigDecimal bottomAssetAmount) {
		this.bottomAssetAmount = bottomAssetAmount;
	}

	/**
	 * 获取交易方式
	 *
	 * @return
	 */
	public String getDealMode() {
		return dealMode;
	}

	/**
	 * 设置交易方式
	 *
	 * @param dealMode
	 */
	public void setDealMode(String dealMode) {
		this.dealMode = dealMode;
	}

	/**
	 * 获取 bussTrdTransferOrder
	 *
	 * @return bussTrdTransferOrder
	 */
	public List<BussTrdTransferOrderDto> getBussTrdTransferOrder() {
		return bussTrdTransferOrder;
	}

	/**
	 * 设置 bussTrdTransferOrder
	 *
	 * @param bussTrdTransferOrder
	 *            bussTrdTransferOrder
	 */
	public void setBussTrdTransferOrder(List<BussTrdTransferOrderDto> bussTrdTransferOrder) {
		this.bussTrdTransferOrder = bussTrdTransferOrder;
	}

	/**
	 * 获取 trdInvestPersonInfo
	 *
	 * @return trdInvestPersonInfo
	 */
	public TrdInvestPersonInfo002Dto getTrdInvestPersonInfo() {
		return trdInvestPersonInfo;
	}

	/**
	 * 设置 trdInvestPersonInfo
	 *
	 * @param trdInvestPersonInfo
	 *            trdInvestPersonInfo
	 */
	public void setTrdInvestPersonInfo(TrdInvestPersonInfo002Dto trdInvestPersonInfo) {
		this.trdInvestPersonInfo = trdInvestPersonInfo;
	}

	/**
	 * 获取 validStatus
	 *
	 * @return validStatus
	 */
	public String getValidStatus() {
		return validStatus;
	}

	/**
	 * 设置 validStatus
	 *
	 * @param validStatus
	 *            validStatus
	 */
	public void setValidStatus(String validStatus) {
		this.validStatus = validStatus;
	}

	/**
	 * 获取 entrustId
	 *
	 * @return entrustId
	 */
	public String getEntrustId() {
		return entrustId;
	}

	/**
	 * 设置 entrustId
	 *
	 * @param entrustId
	 *            entrustId
	 */
	public void setEntrustId(String entrustId) {
		this.entrustId = entrustId;
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
	 * @param portfolioId
	 *            portfolioId
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
	 * @return finprodName
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
	 * 获取 calendarId
	 *
	 * @return calendarId
	 */
	public String getCalendarId() {
		return calendarId;
	}

	/**
	 * 设置 calendarId
	 *
	 * @param calendarId
	 *            calendarId
	 */
	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}

	/**
	 * 获取 operateType
	 *
	 * @return operateType
	 */
	public String getOperateType() {
		return operateType;
	}

	/**
	 * 设置 operateType
	 *
	 * @param operateType
	 *            operateType
	 */
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	/**
	 * 获取 pageModel
	 *
	 * @return pageModel
	 */
	public String getPageModel() {
		return pageModel;
	}

	/**
	 * 设置 pageModel
	 *
	 * @param pageModel
	 *            pageModel
	 */
	public void setPageModel(String pageModel) {
		this.pageModel = pageModel;
	}

	/**
	 * 获取 trdProductTempDeal
	 *
	 * @return trdProductTempDeal
	 */
	public TrdProductTempDeal getTrdProductTempDeal() {
		return trdProductTempDeal;
	}

	/**
	 * 设置 trdProductTempDeal
	 *
	 * @param trdProductTempDeal
	 *            trdProductTempDeal
	 */
	public void setTrdProductTempDeal(TrdProductTempDeal trdProductTempDeal) {
		this.trdProductTempDeal = trdProductTempDeal;
	}

	/**
	 * 获取 trdProductTempDealAdd
	 *
	 * @return trdProductTempDealAdd
	 */
	public TrdProductTempDealAdd getTrdProductTempDealAdd() {
		return trdProductTempDealAdd;
	}

	/**
	 * 设置 trdProductTempDealAdd
	 *
	 * @param trdProductTempDealAdd
	 *            trdProductTempDealAdd
	 */
	public void setTrdProductTempDealAdd(TrdProductTempDealAdd trdProductTempDealAdd) {
		this.trdProductTempDealAdd = trdProductTempDealAdd;
	}

	public TrdProductTempDeal getTrdProductTempDealB() {
		return trdProductTempDealB;
	}

	public void setTrdProductTempDealB(TrdProductTempDeal trdProductTempDealB) {
		this.trdProductTempDealB = trdProductTempDealB;
	}

	public TrdProductTempDealAdd getTrdProductTempDealAddB() {
		return trdProductTempDealAddB;
	}

	public void setTrdProductTempDealAddB(TrdProductTempDealAdd trdProductTempDealAddB) {
		this.trdProductTempDealAddB = trdProductTempDealAddB;
	}

	/**
	 * 获取 finDealPledge
	 *
	 * @return finDealPledge
	 */
	public FinDealPledge getFinDealPledge() {
		return finDealPledge;
	}

	/**
	 * 设置 finDealPledge
	 *
	 * @param finDealPledge
	 *            finDealPledge
	 */
	public void setFinDealPledge(FinDealPledge finDealPledge) {
		this.finDealPledge = finDealPledge;
	}

	/**
	 * 获取 finTradeProduct
	 *
	 * @return finTradeProduct
	 */
	public FinTradeProduct getFinTradeProduct() {
		return finTradeProduct;
	}

	/**
	 * 设置 finTradeProduct
	 *
	 * @param finTradeProduct
	 *            finTradeProduct
	 */
	public void setFinTradeProduct(FinTradeProduct finTradeProduct) {
		this.finTradeProduct = finTradeProduct;
	}

	public PositionBaseData getPositionBaseData() {
		return positionBaseData;
	}

	public void setPositionBaseData(PositionBaseData positionBaseData) {
		this.positionBaseData = positionBaseData;
	}

	/**
	 * 获取 finProductType
	 *
	 * @return finProductType
	 */
	public FinProductType getFinProductType() {
		return finProductType;
	}

	/**
	 * 设置 finProductType
	 *
	 * @param finProductType
	 *            finProductType
	 */
	public void setFinProductType(FinProductType finProductType) {
		this.finProductType = finProductType;
	}

	/**
	 * 获取 ptlTrade
	 *
	 * @return ptlTrade
	 */
	public List<PtlTrade> getPtlTrade() {
		return ptlTrade;
	}

	/**
	 * 设置 ptlTrade
	 *
	 * @param ptlTrade
	 *            ptlTrade
	 */
	public void setPtlTrade(List<PtlTrade> ptlTrade) {
		this.ptlTrade = ptlTrade;
	}

	/**
	 * 获取 paTrdProductDeal
	 *
	 * @return paTrdProductDeal
	 */
	public PaTrdProductDeal getPaTrdProductDeal() {
		return paTrdProductDeal;
	}

	/**
	 * 设置 paTrdProductDeal
	 *
	 * @param paTrdProductDeal
	 *            paTrdProductDeal
	 */
	public void setPaTrdProductDeal(PaTrdProductDeal paTrdProductDeal) {
		this.paTrdProductDeal = paTrdProductDeal;
	}

	/**
	 * 获取 calculateField
	 *
	 * @return calculateField
	 */
	public String getCalculateField() {
		return calculateField;
	}

	/**
	 * 设置 calculateField
	 *
	 * @param calculateField
	 *            calculateField
	 */
	public void setCalculateField(String calculateField) {
		this.calculateField = calculateField;
	}

	/**
	 * 获取 workflowStatus
	 *
	 * @return workflowStatus.
	 */
	public String getWorkflowStatus() {
		return workflowStatus;
	}

	/**
	 * 设置 workflowStatus
	 *
	 * @param workflowStatus
	 *            workflowStatus
	 */
	public void setWorkflowStatus(String workflowStatus) {
		this.workflowStatus = workflowStatus;
	}

	/**
	 * 获取 revokeReason
	 *
	 * @return revokeReason.
	 */
	public String getRevokeReason() {
		return revokeReason;
	}

	/**
	 * 设置 revokeReason
	 *
	 * @param revokeReason
	 *            revokeReason
	 */
	public void setRevokeReason(String revokeReason) {
		this.revokeReason = revokeReason;
	}

	/**
	 * 获取 systemFinprodId
	 *
	 * @return systemFinprodId.
	 */
	public String getSystemFinprodId() {
		return systemFinprodId;
	}

	/**
	 * 设置 systemFinprodId
	 *
	 * @param systemFinprodId
	 *            systemFinprodId
	 */
	public void setSystemFinprodId(String systemFinprodId) {
		this.systemFinprodId = systemFinprodId;
	}

	public BigDecimal getDealAmt() {
		return dealAmt;
	}

	public void setDealAmt(BigDecimal dealAmt) {
		this.dealAmt = dealAmt;
	}

	public TrdTradeFeeDetail getTrdTradeFeeDetail() {
		return trdTradeFeeDetail;
	}

	public void setTrdTradeFeeDetail(TrdTradeFeeDetail trdTradeFeeDetail) {
		this.trdTradeFeeDetail = trdTradeFeeDetail;
	}

	public BigDecimal getStampDuty() {
		return stampDuty;
	}

	public void setStampDuty(BigDecimal stampDuty) {
		this.stampDuty = stampDuty;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getAccAvailableCash() {
		return accAvailableCash;
	}

	public void setAccAvailableCash(BigDecimal accAvailableCash) {
		this.accAvailableCash = accAvailableCash;
	}

	public BigDecimal getTradeCash() {
		return tradeCash;
	}

	public void setTradeCash(BigDecimal tradeCash) {
		this.tradeCash = tradeCash;
	}

	public BigDecimal getAvalibleCash() {
		return avalibleCash;
	}

	public void setAvalibleCash(BigDecimal avalibleCash) {
		this.avalibleCash = avalibleCash;
	}

	public Date getLockMdate() {
		return lockMdate;
	}

	public void setLockMdate(Date lockMdate) {
		this.lockMdate = lockMdate;
	}

	public String getStampDutyIsPayWithTrade() {
		return stampDutyIsPayWithTrade;
	}

	public void setStampDutyIsPayWithTrade(String stampDutyIsPayWithTrade) {
		this.stampDutyIsPayWithTrade = stampDutyIsPayWithTrade;
	}

	public String getCommissionIsPayWithTrade() {
		return commissionIsPayWithTrade;
	}

	public void setCommissionIsPayWithTrade(String commissionIsPayWithTrade) {
		this.commissionIsPayWithTrade = commissionIsPayWithTrade;
	}

	public BigDecimal getEntrustAmt() {
		return entrustAmt;
	}

	public void setEntrustAmt(BigDecimal entrustAmt) {
		this.entrustAmt = entrustAmt;
	}

	public BigDecimal getEntrustNum() {
		return entrustNum;
	}

	public void setEntrustNum(BigDecimal entrustNum) {
		this.entrustNum = entrustNum;
	}

	public BigDecimal getFinishAmt() {
		return finishAmt;
	}

	public void setFinishAmt(BigDecimal finishAmt) {
		this.finishAmt = finishAmt;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}


	/**
	 * 获取 actPortfolioId
	 *
	 * @return actPortfolioId
	 */
	public String getActPortfolioId() {
		return actPortfolioId;
	}

	/**
	 * 设置 actPortfolioId
	 *
	 * @param actPortfolioId actPortfolioId
	 */
	public void setActPortfolioId(String actPortfolioId) {
		this.actPortfolioId = actPortfolioId;
	}

	public BigDecimal getDealMargin() {
		return dealMargin;
	}

	public void setDealMargin(BigDecimal dealMargin) {
		this.dealMargin = dealMargin;
	}

	public FinPledgeDispTemp getFinPledgeDispTemp() {
		return finPledgeDispTemp;
	}

	public void setFinPledgeDispTemp(FinPledgeDispTemp finPledgeDispTemp) {
		this.finPledgeDispTemp = finPledgeDispTemp;
	}

	public List<FinPledgeDispDetailTemp> getFinPledgeDispDetailTempList() {
		return finPledgeDispDetailTempList;
	}

	public void setFinPledgeDispDetailTempList(List<FinPledgeDispDetailTemp> finPledgeDispDetailTempList) {
		this.finPledgeDispDetailTempList = finPledgeDispDetailTempList;
	}

	public String getRedoType() {
		return redoType;
	}

	public void setRedoType(String redoType) {
		this.redoType = redoType;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public List<FinDealPledge> getFinDealPledgeList() {
		return finDealPledgeList;
	}

	public void setFinDealPledgeList(List<FinDealPledge> finDealPledgeList) {
		this.finDealPledgeList = finDealPledgeList;
	}

	@ApiModelProperty(required = false, name = "entrustBussType", value = "指令业务类型")
	private String entrustBussType;

	public String getEntrustBussType() {
		return entrustBussType;
	}

	public void setEntrustBussType(String entrustBussType) {
		this.entrustBussType = entrustBussType;
	}
}
