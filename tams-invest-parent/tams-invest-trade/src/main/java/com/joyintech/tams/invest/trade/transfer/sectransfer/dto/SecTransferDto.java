package com.joyintech.tams.invest.trade.transfer.sectransfer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.PaTrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
*
* 债券交易转仓
* @author zcw
* @version 1.0 2019年11月21日
* @see SecTransferDto
* @since 1.0
*/
public class SecTransferDto extends TrdBondInvestTradeDto {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5576407185793143146L;
	
	/**
     * 拆分交易编号
     */
    private String tradeId;
    /**
     * 组合代码
     */
    private String portfolioId;
    /**
     * 组合名称
     */
    private String portfolioName;
    /**
     * 资产代码
     */
    private String finprodMarketId;
    /**
     * 交易日期
     */
    private Date tradeDate;
    /**
     * 本金金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal prinAmount;
    /**
     * 券面总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal tradeAmt;
    /**
     * 净价总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal cpriceAmt;
    /**
     * 应计利息总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal intAmt;
    /**
     * 全价总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal fpriceAmt;
    /**
     * 交易手续费
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal procedureAmt;
    /**
     * 结算费
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal settleAmt;
    /**
     * 佣金
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal commissionAmt;
    /**
     * 通道费
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal channelFee;
    /**
     * 交易金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal tradeRepAmt;
    
    /**
     * 附件上传
     */
    private List<UploadFile> uploadFileList;
    
    /**
     * 实体组合代码
     */
    private String actPortfolioId;
    
    /**
     * 操作方式
     */
    private String operateType;
    
	/**
     * 投资过度组合
     */
    private String investPortfolioId;
    
    /**
     * 交易方向
     */
    private String ps;
    
    /**
     * 资产代码
     */
    private String finprodId;
    
    /**
     * 交易场所
     */
    private String tradeMarket;
    
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
     * pageModel
     */
    private String PtradeId;
    
    /**
     * pageModel
     */
    private String pageModel;
    
    private TrdProductDeal trdProductDeal;
    
    /**
     * 划款指令
     */
    private List<StlPayOrdersDto> stlPayOrdersList;
    /**
     *到期收益率
     */
    private BigDecimal exerYield;
    /**
     *行权收益率
     */
    private BigDecimal yield;

	/**
	 * 汇总扩展表
	 */
	private PaTrdFinProductDealTotal paTrdFinProductDealTotal;

	/**
	 * 汇总附属表
	 */
	private TrdDealTotalAdd trdDealTotalAdd;

	/**
	 * 附件编号
	 */
	private String businessId;

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
    
	public BigDecimal getExerYield() {
		return exerYield;
	}
	public void setExerYield(BigDecimal exerYield) {
		this.exerYield = exerYield;
	}
	public BigDecimal getYield() {
		return yield;
	}
	public void setYield(BigDecimal yield) {
		this.yield = yield;
	}
	public String getTradeId() {
		return tradeId;
	}
	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}
	public String getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}
	public String getPortfolioName() {
		return portfolioName;
	}
	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}
	public String getFinprodMarketId() {
		return finprodMarketId;
	}
	public void setFinprodMarketId(String finprodMarketId) {
		this.finprodMarketId = finprodMarketId;
	}
	public Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public BigDecimal getPrinAmount() {
		return prinAmount;
	}
	public void setPrinAmount(BigDecimal prinAmount) {
		this.prinAmount = prinAmount;
	}
	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}
	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}
	public BigDecimal getCpriceAmt() {
		return cpriceAmt;
	}
	public void setCpriceAmt(BigDecimal cpriceAmt) {
		this.cpriceAmt = cpriceAmt;
	}
	public BigDecimal getIntAmt() {
		return intAmt;
	}
	public void setIntAmt(BigDecimal intAmt) {
		this.intAmt = intAmt;
	}
	public BigDecimal getFpriceAmt() {
		return fpriceAmt;
	}
	public void setFpriceAmt(BigDecimal fpriceAmt) {
		this.fpriceAmt = fpriceAmt;
	}
	public BigDecimal getProcedureAmt() {
		return procedureAmt;
	}
	public void setProcedureAmt(BigDecimal procedureAmt) {
		this.procedureAmt = procedureAmt;
	}
	public BigDecimal getSettleAmt() {
		return settleAmt;
	}
	public void setSettleAmt(BigDecimal settleAmt) {
		this.settleAmt = settleAmt;
	}
	public BigDecimal getCommissionAmt() {
		return commissionAmt;
	}
	public void setCommissionAmt(BigDecimal commissionAmt) {
		this.commissionAmt = commissionAmt;
	}
	public BigDecimal getChannelFee() {
		return channelFee;
	}
	public void setChannelFee(BigDecimal channelFee) {
		this.channelFee = channelFee;
	}
	public BigDecimal getTradeRepAmt() {
		return tradeRepAmt;
	}
	public void setTradeRepAmt(BigDecimal tradeRepAmt) {
		this.tradeRepAmt = tradeRepAmt;
	}
	public String getActPortfolioId() {
		return actPortfolioId;
	}
	public void setActPortfolioId(String actPortfolioId) {
		this.actPortfolioId = actPortfolioId;
	}
	public List<UploadFile> getUploadFileList() {
		return uploadFileList;
	}
	public void setUploadFileList(List<UploadFile> uploadFileList) {
		this.uploadFileList = uploadFileList;
	}
	public String getOperateType() {
		return operateType;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	public String getInvestPortfolioId() {
		return investPortfolioId;
	}
	public void setInvestPortfolioId(String investPortfolioId) {
		this.investPortfolioId = investPortfolioId;
	}
	public String getPs() {
		return ps;
	}
	public void setPs(String ps) {
		this.ps = ps;
	}
	public String getFinprodId() {
		return finprodId;
	}
	public void setFinprodId(String finprodId) {
		this.finprodId = finprodId;
	}
	public String getTradeMarket() {
		return tradeMarket;
	}
	public void setTradeMarket(String tradeMarket) {
		this.tradeMarket = tradeMarket;
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
	public String getPtradeId() {
		return PtradeId;
	}
	public void setPtradeId(String ptradeId) {
		PtradeId = ptradeId;
	}
	public String getPageModel() {
		return pageModel;
	}
	public void setPageModel(String pageModel) {
		this.pageModel = pageModel;
	}
	public TrdProductDeal getTrdProductDeal() {
		return trdProductDeal;
	}
	public void setTrdProductDeal(TrdProductDeal trdProductDeal) {
		this.trdProductDeal = trdProductDeal;
	}
	public List<StlPayOrdersDto> getStlPayOrdersList() {
		return stlPayOrdersList;
	}
	public void setStlPayOrdersList(List<StlPayOrdersDto> stlPayOrdersList) {
		this.stlPayOrdersList = stlPayOrdersList;
	}
	
	public String getInvAimStr() {
		return SysDatadictUtil.getNameFromDataDict(DictCode.投资目的, this.getInvAim());
	}

	public String getTurnInvAimStr() {
		return SysDatadictUtil.getNameFromDataDict(DictCode.投资目的, this.getTurnInvAim());
	}
	@Override
	public PaTrdFinProductDealTotal getPaTrdFinProductDealTotal() {
		return paTrdFinProductDealTotal;
	}

	@Override
	public void setPaTrdFinProductDealTotal(PaTrdFinProductDealTotal paTrdFinProductDealTotal) {
		this.paTrdFinProductDealTotal = paTrdFinProductDealTotal;
	}

	@Override
	public TrdDealTotalAdd getTrdDealTotalAdd() {
		return trdDealTotalAdd;
	}

	@Override
	public void setTrdDealTotalAdd(TrdDealTotalAdd trdDealTotalAdd) {
		this.trdDealTotalAdd = trdDealTotalAdd;
	}

	private String stlPayId;

	public String getStlPayId() {
		return stlPayId;
	}

	public void setStlPayId(String stlPayId) {
		this.stlPayId = stlPayId;
	}

	/**
	 * 交易金额
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal tradeMoney;

	public BigDecimal getTradeMoney() {
		return tradeMoney;
	}

	public void setTradeMoney(BigDecimal tradeMoney) {
		this.tradeMoney = tradeMoney;
	}

	private String portfolioIdIn;

	public String getPortfolioIdIn() {
		return portfolioIdIn;
	}

	public void setPortfolioIdIn(String portfolioIdIn) {
		this.portfolioIdIn = portfolioIdIn;
	}

	private String fileArrayName;

	public String getFileArrayName() {
		return fileArrayName;
	}

	public void setFileArrayName(String fileArrayName) {
		this.fileArrayName = fileArrayName;
	}

	/**
	 * 交易数据
	 */
	private List<BaseTradeInfoDto> baseTradeInfoDtos;

	public List<BaseTradeInfoDto> getBaseTradeInfoDtos() {
		return baseTradeInfoDtos;
	}

	public void setBaseTradeInfoDtos(List<BaseTradeInfoDto> baseTradeInfoDtos) {
		this.baseTradeInfoDtos = baseTradeInfoDtos;
	}
}
