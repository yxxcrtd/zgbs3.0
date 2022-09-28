package com.joyintech.tams.invest.trade.transfer.fund.dto;

import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.WflPaTrdProductDeal;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.common.base.entity.WflTrdProductDealAdd;
import com.joyintech.tams.common.base.entity.WflTrdProductDealOth;
import com.joyintech.tams.component.business.trade.dto.BussTrdAvailableCashQuery;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.trade.lend.dto.FinTradeProductDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.List;

public class FundAssetTransfer001Dto extends TrdProductDeal implements ProcessEntity {
    /**
     * 操作类型
     */
    private String operateType;
    /**
     * 元素Id
     */
    private String elementId;
    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     *  投资过度组合
     */
    private String invTransPortf;

    /**
     *  交易日历
     */
    private String calendarId;

    /**
     * 工作流代码
     */
    private String workflowId;

    /**
     * 数据状态
     */
    private String status;

    /**
     * 画面模式
     */
    private String pageModel;

    /**
     * 交易份额
     */
    private BigDecimal tradePrin;

    /**
     * 单位净值
     */
    private BigDecimal unitCprice;

    /**
     * 待结转份额
     */
    private BigDecimal carryOverQty;

    /**
     * 交易金额
     */
    private BigDecimal tradeAmt;

    /**
     * 持仓份额
     */
    private BigDecimal tradePrinOrg;

    /**
     * 剩余可转仓份额
     */
    private BigDecimal rollablePrin;

    private BigDecimal portoAvailableAmt;

    /**
     * 组合代码名称
     */
    private String portfolioName;

    /**
     * 实际组合
     */
    private String actPortfolioId;

    /**
     * 资产代码
     */
    private String finprodMarketId;

    /**
     * 资产简称
     */
    private String finprodAbbr;

    /**
     * 金融产品代码
     */
    private String finprodIdOrg;

    /**
     * 资产类型
     */
    private String assetType;

    /**
     * 资产一级类型
     */
    private String fundType;

    /**
     * 原始交易编号
     */
    private String tradeIdOrg;

    /**
     * 转仓列表
     */
    private List<FundAssetTransfer001Dto> funAssetTransferDtoList;

    /**
     * 组合可用持仓和组合可用现金实体类
     */
    private BussTrdAvailableCashQuery bussTrdAvailableCashQuery;
    /**
     * 金融产品交易附属业务流程表
     */
    private WflTrdProductDealAdd wflTrdProductDealAdd;

    /**
     * 金融产品交易扩展业务流程表
     */
    private WflPaTrdProductDeal wflPaTrdProductDeal;

    /**
     * 金融产品交易业务流程信息
     */
    private WflTrdProductDeal wflTrdProductDeal;

    /**
     * 金融产品交易业务流程其他信息
     */
    private WflTrdProductDealOth wflTrdProductDealOth;

    /**
     * 金融产品交易Entity
     */
    private TrdProductDeal trdProductDeal;

    /**
     * 金融产品交易扩展Entity
     */
    private PaTrdProductDeal paTrdProductDeal;

    /**
     * 交易类金融产品
     */
    private FinTradeProductDto finTradeProduct;

    /**
     * 金融产品交易附属表
     */
    private TrdProductDealAdd trdProductDealAdd;
    /**
     * 组合交易
     */
    private List<PtlTrade> ptlTrade;

    /**
     * 划款指令
     */
    private List<BussTrdTransferOrderDto> bussTrdTransferOrder;

    /**
     * 金融产品分类
     */
    private FinProductType finProductType;

    /**
     * 发行机构代码
     */
    private String issuePartyId;

    /**
     * 交易员
     */
    private String trader;

    /**
     * 交易员部门
     */
    private String deptCode;

    /**
     * 对账状态
     */
    private String checkStatus;
    /**
     * 估值状态
     */
    private String valueStatus;
    
    /**
     * 软控是否继续？Y：继续
     */
    private String softSkipMode;


    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**
     * 触发加签flag，风险岗审批
     */
    private String riskFlag;

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


    public String getSoftSkipMode() {
		return softSkipMode;
	}

	public void setSoftSkipMode(String softSkipMode) {
		this.softSkipMode = softSkipMode;
	}

	/**
     * Gets the value of operateType.
     *
     * @return the value of operateType
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * Sets the operateType.
     *
     * <p>You can use getOperateType() to get the value of operateType</p>
     *
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * Gets the value of elementId.
     *
     * @return the value of elementId
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * Sets the elementId.
     *
     * <p>You can use getElementId() to get the value of elementId</p>
     *
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * Gets the value of invTransPortf.
     *
     * @return the value of invTransPortf
     */
    public String getInvTransPortf() {
        return invTransPortf;
    }

    /**
     * Sets the invTransPortf.
     *
     * <p>You can use getInvTransPortf() to get the value of invTransPortf</p>
     *
     * @param invTransPortf invTransPortf
     */
    public void setInvTransPortf(String invTransPortf) {
        this.invTransPortf = invTransPortf;
    }

    /**
     * Gets the value of calendarId.
     *
     * @return the value of calendarId
     */
    public String getCalendarId() {
        return calendarId;
    }

    /**
     * Sets the calendarId.
     *
     * <p>You can use getCalendarId() to get the value of calendarId</p>
     *
     * @param calendarId calendarId
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * Gets the value of workflowId.
     *
     * @return the value of workflowId
     */
    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * Sets the workflowId.
     *
     * <p>You can use getWorkflowId() to get the value of workflowId</p>
     *
     * @param workflowId workflowId
     */
    @Override
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * Gets the value of status.
     *
     * @return the value of status
     */
    @Override
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * <p>You can use getStatus() to get the value of status</p>
     *
     * @param status status
     */
    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the value of pageModel.
     *
     * @return the value of pageModel
     */
    public String getPageModel() {
        return pageModel;
    }

    /**
     * Sets the pageModel.
     *
     * <p>You can use getPageModel() to get the value of pageModel</p>
     *
     * @param pageModel pageModel
     */
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    /**
     * Gets the value of tradePrin.
     *
     * @return the value of tradePrin
     */
    public BigDecimal getTradePrin() {
        return tradePrin;
    }

    /**
     * Sets the tradePrin.
     *
     * <p>You can use getTradePrin() to get the value of tradePrin</p>
     *
     * @param tradePrin tradePrin
     */
    public void setTradePrin(BigDecimal tradePrin) {
        this.tradePrin = tradePrin;
    }

    /**
     * Gets the value of unitCprice.
     *
     * @return the value of unitCprice
     */
    @Override
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    /**
     * Sets the unitCprice.
     *
     * <p>You can use getUnitCprice() to get the value of unitCprice</p>
     *
     * @param unitCprice unitCprice
     */
    @Override
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    /**
     * Gets the value of carryOverQty.
     *
     * @return the value of carryOverQty
     */
    public BigDecimal getCarryOverQty() {
        return carryOverQty;
    }

    /**
     * Sets the carryOverQty.
     *
     * <p>You can use getCarryOverQty() to get the value of carryOverQty</p>
     *
     * @param carryOverQty carryOverQty
     */
    public void setCarryOverQty(BigDecimal carryOverQty) {
        this.carryOverQty = carryOverQty;
    }

    /**
     * Gets the value of tradeAmt.
     *
     * @return the value of tradeAmt
     */
    @Override
    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    /**
     * Sets the tradeAmt.
     *
     * <p>You can use getTradeAmt() to get the value of tradeAmt</p>
     *
     * @param tradeAmt tradeAmt
     */
    @Override
    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getPortoAvailableAmt() {
        return portoAvailableAmt;
    }

    public void setPortoAvailableAmt(BigDecimal portoAvailableAmt) {
        this.portoAvailableAmt = portoAvailableAmt;
    }

    /**
     * Gets the value of tradePrinOrg.
     *
     * @return the value of tradePrinOrg
     */
    public BigDecimal getTradePrinOrg() {
        return tradePrinOrg;
    }

    /**
     * Sets the tradePrinOrg.
     *
     * <p>You can use getTradePrinOrg() to get the value of tradePrinOrg</p>
     *
     * @param tradePrinOrg tradePrinOrg
     */
    public void setTradePrinOrg(BigDecimal tradePrinOrg) {
        this.tradePrinOrg = tradePrinOrg;
    }

    /**
     * Gets the value of rollablePrin.
     *
     * @return the value of rollablePrin
     */
    public BigDecimal getRollablePrin() {
        return rollablePrin;
    }

    /**
     * Sets the rollablePrin.
     *
     * <p>You can use getRollablePrin() to get the value of rollablePrin</p>
     *
     * @param rollablePrin rollablePrin
     */
    public void setRollablePrin(BigDecimal rollablePrin) {
        this.rollablePrin = rollablePrin;
    }

    /**
     * Gets the value of portfolioName.
     *
     * @return the value of portfolioName
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * Sets the portfolioName.
     *
     * <p>You can use getPortfolioName() to get the value of portfolioName</p>
     *
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * Gets the value of actPortfolioId.
     *
     * @return the value of actPortfolioId
     */
    public String getActPortfolioId() {
        return actPortfolioId;
    }

    /**
     * Sets the actPortfolioId.
     *
     * <p>You can use getActPortfolioId() to get the value of actPortfolioId</p>
     *
     * @param actPortfolioId actPortfolioId
     */
    public void setActPortfolioId(String actPortfolioId) {
        this.actPortfolioId = actPortfolioId;
    }

    /**
     * Gets the value of finprodMarketId.
     *
     * @return the value of finprodMarketId
     */
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    /**
     * Sets the finprodMarketId.
     *
     * <p>You can use getFinprodMarketId() to get the value of finprodMarketId</p>
     *
     * @param finprodMarketId finprodMarketId
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    /**
     * Gets the value of funAssetTransferDtoList.
     *
     * @return the value of funAssetTransferDtoList
     */
    public List<FundAssetTransfer001Dto> getFunAssetTransferDtoList() {
        return funAssetTransferDtoList;
    }

    /**
     * Sets the funAssetTransferDtoList.
     *
     * <p>You can use getFunAssetTransferDtoList() to get the value of funAssetTransferDtoList</p>
     *
     * @param funAssetTransferDtoList funAssetTransferDtoList
     */
    public void setFunAssetTransferDtoList(List<FundAssetTransfer001Dto> funAssetTransferDtoList) {
        this.funAssetTransferDtoList = funAssetTransferDtoList;
    }

    /**
     * Gets the value of finprodAbbr.
     *
     * @return the value of finprodAbbr
     */
    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    /**
     * Sets the finprodAbbr.
     *
     * <p>You can use getFinprodAbbr() to get the value of finprodAbbr</p>
     *
     * @param finprodAbbr finprodAbbr
     */
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    /**
     * Gets the value of finprodIdOrg.
     *
     * @return the value of finprodIdOrg
     */
    public String getFinprodIdOrg() {
        return finprodIdOrg;
    }

    /**
     * Sets the finprodIdOrg.
     *
     * <p>You can use getFinprodIdOrg() to get the value of finprodIdOrg</p>
     *
     * @param finprodIdOrg finprodIdOrg
     */
    public void setFinprodIdOrg(String finprodIdOrg) {
        this.finprodIdOrg = finprodIdOrg;
    }

    /**
     * Gets the value of assetType.
     *
     * @return the value of assetType
     */
    public String getAssetType() {
        return assetType;
    }

    /**
     * Sets the assetType.
     *
     * <p>You can use getAssetType() to get the value of assetType</p>
     *
     * @param assetType assetType
     */
    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    /**
     * Gets the value of portfolioId.
     *
     * @return the value of portfolioId
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * Sets the portfolioId.
     *
     * <p>You can use getPortfolioId() to get the value of portfolioId</p>
     *
     * @param portfolioId portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * Gets the value of bussTrdAvailableCashQuery.
     *
     * @return the value of bussTrdAvailableCashQuery
     */
    public BussTrdAvailableCashQuery getBussTrdAvailableCashQuery() {
        return bussTrdAvailableCashQuery;
    }

    /**
     * Sets the bussTrdAvailableCashQuery.
     *
     * <p>You can use getBussTrdAvailableCashQuery() to get the value of bussTrdAvailableCashQuery</p>
     *
     * @param bussTrdAvailableCashQuery bussTrdAvailableCashQuery
     */
    public void setBussTrdAvailableCashQuery(BussTrdAvailableCashQuery bussTrdAvailableCashQuery) {
        this.bussTrdAvailableCashQuery = bussTrdAvailableCashQuery;
    }

    /**
     * Gets the value of tradeIdOrg.
     *
     * @return the value of tradeIdOrg
     */
    public String getTradeIdOrg() {
        return tradeIdOrg;
    }

    /**
     * Sets the tradeIdOrg.
     *
     * <p>You can use getTradeIdOrg() to get the value of tradeIdOrg</p>
     *
     * @param tradeIdOrg tradeIdOrg
     */
    public void setTradeIdOrg(String tradeIdOrg) {
        this.tradeIdOrg = tradeIdOrg;
    }

    /**
     * Gets the value of wflTrdProductDealAdd.
     *
     * @return the value of wflTrdProductDealAdd
     */
    public WflTrdProductDealAdd getWflTrdProductDealAdd() {
        return wflTrdProductDealAdd;
    }

    /**
     * Sets the wflTrdProductDealAdd.
     *
     * <p>You can use getWflTrdProductDealAdd() to get the value of wflTrdProductDealAdd</p>
     *
     * @param wflTrdProductDealAdd wflTrdProductDealAdd
     */
    public void setWflTrdProductDealAdd(WflTrdProductDealAdd wflTrdProductDealAdd) {
        this.wflTrdProductDealAdd = wflTrdProductDealAdd;
    }

    /**
     * Gets the value of wflPaTrdProductDeal.
     *
     * @return the value of wflPaTrdProductDeal
     */
    public WflPaTrdProductDeal getWflPaTrdProductDeal() {
        return wflPaTrdProductDeal;
    }

    /**
     * Sets the wflPaTrdProductDeal.
     *
     * <p>You can use getWflPaTrdProductDeal() to get the value of wflPaTrdProductDeal</p>
     *
     * @param wflPaTrdProductDeal wflPaTrdProductDeal
     */
    public void setWflPaTrdProductDeal(WflPaTrdProductDeal wflPaTrdProductDeal) {
        this.wflPaTrdProductDeal = wflPaTrdProductDeal;
    }

    /**
     * Gets the value of wflTrdProductDeal.
     *
     * @return the value of wflTrdProductDeal
     */
    public WflTrdProductDeal getWflTrdProductDeal() {
        return wflTrdProductDeal;
    }

    /**
     * Sets the wflTrdProductDeal.
     *
     * <p>You can use getWflTrdProductDeal() to get the value of wflTrdProductDeal</p>
     *
     * @param wflTrdProductDeal wflTrdProductDeal
     */
    public void setWflTrdProductDeal(WflTrdProductDeal wflTrdProductDeal) {
        this.wflTrdProductDeal = wflTrdProductDeal;
    }

    /**
     * Gets the value of wflTrdProductDealOth.
     *
     * @return the value of wflTrdProductDealOth
     */
    public WflTrdProductDealOth getWflTrdProductDealOth() {
        return wflTrdProductDealOth;
    }

    /**
     * Sets the wflTrdProductDealOth.
     *
     * <p>You can use getWflTrdProductDealOth() to get the value of wflTrdProductDealOth</p>
     *
     * @param wflTrdProductDealOth wflTrdProductDealOth
     */
    public void setWflTrdProductDealOth(WflTrdProductDealOth wflTrdProductDealOth) {
        this.wflTrdProductDealOth = wflTrdProductDealOth;
    }

    /**
     * Gets the value of trdProductDeal.
     *
     * @return the value of trdProductDeal
     */
    public TrdProductDeal getTrdProductDeal() {
        return trdProductDeal;
    }

    /**
     * Sets the trdProductDeal.
     *
     * <p>You can use getTrdProductDeal() to get the value of trdProductDeal</p>
     *
     * @param trdProductDeal trdProductDeal
     */
    public void setTrdProductDeal(TrdProductDeal trdProductDeal) {
        this.trdProductDeal = trdProductDeal;
    }

    /**
     * Gets the value of finTradeProduct.
     *
     * @return the value of finTradeProduct
     */
    public FinTradeProductDto getFinTradeProduct() {
        return finTradeProduct;
    }

    /**
     * Sets the finTradeProduct.
     *
     * <p>You can use getFinTradeProduct() to get the value of finTradeProduct</p>
     *
     * @param finTradeProduct finTradeProduct
     */
    public void setFinTradeProduct(FinTradeProductDto finTradeProduct) {
        this.finTradeProduct = finTradeProduct;
    }

    /**
     * Gets the value of trdProductDealAdd.
     *
     * @return the value of trdProductDealAdd
     */
    public TrdProductDealAdd getTrdProductDealAdd() {
        return trdProductDealAdd;
    }

    /**
     * Sets the trdProductDealAdd.
     *
     * <p>You can use getTrdProductDealAdd() to get the value of trdProductDealAdd</p>
     *
     * @param trdProductDealAdd trdProductDealAdd
     */
    public void setTrdProductDealAdd(TrdProductDealAdd trdProductDealAdd) {
        this.trdProductDealAdd = trdProductDealAdd;
    }

    /**
     * Gets the value of ptlTrade.
     *
     * @return the value of ptlTrade
     */
    public List<PtlTrade> getPtlTrade() {
        return ptlTrade;
    }

    /**
     * Sets the ptlTrade.
     *
     * <p>You can use getPtlTrade() to get the value of ptlTrade</p>
     *
     * @param ptlTrade ptlTrade
     */
    public void setPtlTrade(List<PtlTrade> ptlTrade) {
        this.ptlTrade = ptlTrade;
    }

    /**
     * Gets the value of bussTrdTransferOrder.
     *
     * @return the value of bussTrdTransferOrder
     */
    public List<BussTrdTransferOrderDto> getBussTrdTransferOrder() {
        return bussTrdTransferOrder;
    }

    /**
     * Sets the bussTrdTransferOrder.
     *
     * <p>You can use getBussTrdTransferOrder() to get the value of bussTrdTransferOrder</p>
     *
     * @param bussTrdTransferOrder bussTrdTransferOrder
     */
    public void setBussTrdTransferOrder(List<BussTrdTransferOrderDto> bussTrdTransferOrder) {
        this.bussTrdTransferOrder = bussTrdTransferOrder;
    }

    /**
     * Gets the value of finProductType.
     *
     * @return the value of finProductType
     */
    public FinProductType getFinProductType() {
        return finProductType;
    }

    /**
     * Sets the finProductType.
     *
     * <p>You can use getFinProductType() to get the value of finProductType</p>
     *
     * @param finProductType finProductType
     */
    public void setFinProductType(FinProductType finProductType) {
        this.finProductType = finProductType;
    }

    /**
     * Gets the value of paTrdProductDeal.
     *
     * @return the value of paTrdProductDeal
     */
    public PaTrdProductDeal getPaTrdProductDeal() {
        return paTrdProductDeal;
    }

    /**
     * Sets the paTrdProductDeal.
     *
     * <p>You can use getPaTrdProductDeal() to get the value of paTrdProductDeal</p>
     *
     * @param paTrdProductDeal paTrdProductDeal
     */
    public void setPaTrdProductDeal(PaTrdProductDeal paTrdProductDeal) {
        this.paTrdProductDeal = paTrdProductDeal;
    }

    /**
     * Gets the value of issuePartyId.
     *
     * @return the value of issuePartyId
     */
    public String getIssuePartyId() {
        return issuePartyId;
    }

    /**
     * Sets the issuePartyId.
     *
     * <p>You can use getIssuePartyId() to get the value of issuePartyId</p>
     *
     * @param issuePartyId issuePartyId
     */
    public void setIssuePartyId(String issuePartyId) {
        this.issuePartyId = issuePartyId;
    }

    /**
     * Gets the value of trader.
     *
     * @return the value of trader
     */
    @Override
    public String getTrader() {
        return trader;
    }

    /**
     * Sets the trader.
     *
     * <p>You can use getTrader() to get the value of trader</p>
     *
     * @param trader trader
     */
    @Override
    public void setTrader(String trader) {
        this.trader = trader;
    }

    /**
     * Gets the value of deptCode.
     *
     * @return the value of deptCode
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * Sets the deptCode.
     *
     * <p>You can use getDeptCode() to get the value of deptCode</p>
     *
     * @param deptCode deptCode
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }
    /**
     * 取得 交易编号
     *
     * @return 交易编号
     */
    @Override
    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getTradeId() {
        return super.getTradeId();
    }

    /**
     * 取得 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     *
     * @return 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    @Override
    @PrimaryKey(PrimaryKey.Field.PK_2)
    public String getFinprodType2() {
        return super.getFinprodType2();
    }

    /**
     * Gets the value of fundType.
     *
     * @return the value of fundType
     */
    public String getFundType() {
        return fundType;
    }

    /**
     * Sets the fundType.
     *
     * <p>You can use getFundType() to get the value of fundType</p>
     *
     * @param fundType fundType
     */
    public void setFundType(String fundType) {
        this.fundType = fundType;
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

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getValueStatus() {
        return valueStatus;
    }

    public void setValueStatus(String valueStatus) {
        this.valueStatus = valueStatus;
    }

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    private String fileArrayName;

    public String getFileArrayName() {
        return fileArrayName;
    }

    public void setFileArrayName(String fileArrayName) {
        this.fileArrayName = fileArrayName;
    }

    public String getRiskFlag() {
        return riskFlag;
    }

    public void setRiskFlag(String riskFlag) {
        this.riskFlag = riskFlag;
    }

    /**
     * 前置交易编号，撤销新头寸
     */
    private String bTotalTradeId;

    public String getbTotalTradeId() {
        return bTotalTradeId;
    }

    public void setbTotalTradeId(String bTotalTradeId) {
        this.bTotalTradeId = bTotalTradeId;
    }
}
