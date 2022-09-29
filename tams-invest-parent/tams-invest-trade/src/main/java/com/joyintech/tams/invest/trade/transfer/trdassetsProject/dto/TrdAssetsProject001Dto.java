package com.joyintech.tams.invest.trade.transfer.trdassetsProject.dto;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.List;

public class TrdAssetsProject001Dto extends TrdProductDeal implements ProcessEntity {

	private static final long serialVersionUID = 1L;
	/**
     * 操作类型
     */
    private String operateType;
    /**
     * 元素Id
     */
    private String elementId;

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
     * 单位应计利息
     */
    private BigDecimal unitIntAmt;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 资产一级分类
     */
    private String projectType;

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
     * 资产名称
     */
    private String finprodName;

    /**
     * 剩余未拆分本金金额
     */
    private BigDecimal unSplitPrinAmt;

    /**
     * 净投资收益率
     */
    private BigDecimal investRate;
    /**
     * 借款利率
     */
    private BigDecimal loadRate;

    //拆分信息
    /**
     * 本金金额
     */
    private BigDecimal splitPrinAmt;

    /**
     * 净价总额
     */
    private BigDecimal splitCprice;

    /**
     * 应计利息总额
     */
    private BigDecimal splitIntAmt;

    /**
     * 实付金额
     */
    private BigDecimal splitTradeAmt;

    /**
     * 通道费
     */
    private BigDecimal splitChannelFee;

    /**
     * 手续费
     */
    private BigDecimal splitProcedureAmt;
    /**
     * 交易数据
     */
    private List<BaseTradeInfoDto> baseTradeInfoDtos;
    /**
     * 列表信息
     */
    List<TrdAssetsProject001Dto> projectListInfoList;

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
     * 已拆分应计利息总额
     */
    private BigDecimal alSplitIntAmt;

    /**
     * 已拆分净价总额
     */
    private BigDecimal alSplitCprice;

    /**
     * 已拆交易份额
     */
    private BigDecimal alSplitPrinAmt;

    /**
     * 已拆分实付金额
     */
    private BigDecimal alSplitTradeAmt;

    /**
     * 已拆分通道费
     */
    private BigDecimal alSplitChannelFee;

    /**
     * 已拆分交易手续费
     */
    private BigDecimal alSplitProcedureAmt;

    /**
     * 原始交易手续费
     */
    private BigDecimal procedureAmt;
    /**
     * 原始交易通道费
     */
    private BigDecimal channelFee;
    /**
     * 原始交易手续费
     */
    private BigDecimal commissionAmt;
    
    /**
     * 转让逾期利息
     */
    private BigDecimal transOverdueIntAmt;
	
    /**
     * 转让逾期本金
     */
    private BigDecimal transOverduePrinAmt;
	
    /**
     * 交割金额
     */
    private BigDecimal deliveryAmount;

    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;



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
     * Gets the value of splitPrinAmt.
     *
     * @return the value of splitPrinAmt
     */
    public BigDecimal getSplitPrinAmt() {
        return splitPrinAmt;
    }

    /**
     * Sets the splitPrinAmt.
     *
     * <p>You can use getSplitPrinAmt() to get the value of splitPrinAmt</p>
     *
     * @param splitPrinAmt splitPrinAmt
     */
    public void setSplitPrinAmt(BigDecimal splitPrinAmt) {
        this.splitPrinAmt = splitPrinAmt;
    }

    /**
     * Gets the value of splitCprice.
     *
     * @return the value of splitCprice
     */
    public BigDecimal getSplitCprice() {
        return splitCprice;
    }

    /**
     * Sets the splitCprice.
     *
     * <p>You can use getSplitCprice() to get the value of splitCprice</p>
     *
     * @param splitCprice splitCprice
     */
    public void setSplitCprice(BigDecimal splitCprice) {
        this.splitCprice = splitCprice;
    }

    /**
     * Gets the value of splitIntAmt.
     *
     * @return the value of splitIntAmt
     */
    public BigDecimal getSplitIntAmt() {
        return splitIntAmt;
    }

    /**
     * Sets the splitIntAmt.
     *
     * <p>You can use getSplitIntAmt() to get the value of splitIntAmt</p>
     *
     * @param splitIntAmt splitIntAmt
     */
    public void setSplitIntAmt(BigDecimal splitIntAmt) {
        this.splitIntAmt = splitIntAmt;
    }

    /**
     * Gets the value of splitTradeAmt.
     *
     * @return the value of splitTradeAmt
     */
    public BigDecimal getSplitTradeAmt() {
        return splitTradeAmt;
    }

    /**
     * Sets the splitTradeAmt.
     *
     * <p>You can use getSplitTradeAmt() to get the value of splitTradeAmt</p>
     *
     * @param splitTradeAmt splitTradeAmt
     */
    public void setSplitTradeAmt(BigDecimal splitTradeAmt) {
        this.splitTradeAmt = splitTradeAmt;
    }

    /**
     * Gets the value of splitChannelFee.
     *
     * @return the value of splitChannelFee
     */
    public BigDecimal getSplitChannelFee() {
        return splitChannelFee;
    }

    /**
     * Sets the splitChannelFee.
     *
     * <p>You can use getSplitChannelFee() to get the value of splitChannelFee</p>
     *
     * @param splitChannelFee splitChannelFee
     */
    public void setSplitChannelFee(BigDecimal splitChannelFee) {
        this.splitChannelFee = splitChannelFee;
    }

    /**
     * Gets the value of splitProcedureAmt.
     *
     * @return the value of splitProcedureAmt
     */
    public BigDecimal getSplitProcedureAmt() {
        return splitProcedureAmt;
    }

    /**
     * Sets the splitProcedureAmt.
     *
     * <p>You can use getSplitProcedureAmt() to get the value of splitProcedureAmt</p>
     *
     * @param splitProcedureAmt splitProcedureAmt
     */
    public void setSplitProcedureAmt(BigDecimal splitProcedureAmt) {
        this.splitProcedureAmt = splitProcedureAmt;
    }

    /**
     * Gets the value of projectListInfoList.
     *
     * @return the value of projectListInfoList
     */
    public List<TrdAssetsProject001Dto> getProjectListInfoList() {
        return projectListInfoList;
    }

    /**
     * Sets the projectListInfoList.
     *
     * <p>You can use getProjectListInfoList() to get the value of projectListInfoList</p>
     *
     * @param projectListInfoList projectListInfoList
     */
    public void setProjectListInfoList(List<TrdAssetsProject001Dto> projectListInfoList) {
        this.projectListInfoList = projectListInfoList;
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
     * Gets the value of alSplitIntAmt.
     *
     * @return the value of alSplitIntAmt
     */
    public BigDecimal getAlSplitIntAmt() {
        return alSplitIntAmt;
    }

    /**
     * Sets the alSplitIntAmt.
     *
     * <p>You can use getAlSplitIntAmt() to get the value of alSplitIntAmt</p>
     *
     * @param alSplitIntAmt alSplitIntAmt
     */
    public void setAlSplitIntAmt(BigDecimal alSplitIntAmt) {
        this.alSplitIntAmt = alSplitIntAmt;
    }

    /**
     * Gets the value of alSplitCprice.
     *
     * @return the value of alSplitCprice
     */
    public BigDecimal getAlSplitCprice() {
        return alSplitCprice;
    }

    /**
     * Sets the alSplitCprice.
     *
     * <p>You can use getAlSplitCprice() to get the value of alSplitCprice</p>
     *
     * @param alSplitCprice alSplitCprice
     */
    public void setAlSplitCprice(BigDecimal alSplitCprice) {
        this.alSplitCprice = alSplitCprice;
    }

    /**
     * Gets the value of alSplitPrinAmt.
     *
     * @return the value of alSplitPrinAmt
     */
    public BigDecimal getAlSplitPrinAmt() {
        return alSplitPrinAmt;
    }

    /**
     * Sets the alSplitPrinAmt.
     *
     * <p>You can use getAlSplitPrinAmt() to get the value of alSplitPrinAmt</p>
     *
     * @param alSplitPrinAmt alSplitPrinAmt
     */
    public void setAlSplitPrinAmt(BigDecimal alSplitPrinAmt) {
        this.alSplitPrinAmt = alSplitPrinAmt;
    }

    /**
     * Gets the value of alSplitTradeAmt.
     *
     * @return the value of alSplitTradeAmt
     */
    public BigDecimal getAlSplitTradeAmt() {
        return alSplitTradeAmt;
    }

    /**
     * Sets the alSplitTradeAmt.
     *
     * <p>You can use getAlSplitTradeAmt() to get the value of alSplitTradeAmt</p>
     *
     * @param alSplitTradeAmt alSplitTradeAmt
     */
    public void setAlSplitTradeAmt(BigDecimal alSplitTradeAmt) {
        this.alSplitTradeAmt = alSplitTradeAmt;
    }

    /**
     * Gets the value of alSplitChannelFee.
     *
     * @return the value of alSplitChannelFee
     */
    public BigDecimal getAlSplitChannelFee() {
        return alSplitChannelFee;
    }

    /**
     * Sets the alSplitChannelFee.
     *
     * <p>You can use getAlSplitChannelFee() to get the value of alSplitChannelFee</p>
     *
     * @param alSplitChannelFee alSplitChannelFee
     */
    public void setAlSplitChannelFee(BigDecimal alSplitChannelFee) {
        this.alSplitChannelFee = alSplitChannelFee;
    }

    /**
     * Gets the value of alSplitProcedureAmt.
     *
     * @return the value of alSplitProcedureAmt
     */
    public BigDecimal getAlSplitProcedureAmt() {
        return alSplitProcedureAmt;
    }

    /**
     * Sets the alSplitProcedureAmt.
     *
     * <p>You can use getAlSplitProcedureAmt() to get the value of alSplitProcedureAmt</p>
     *
     * @param alSplitProcedureAmt alSplitProcedureAmt
     */
    public void setAlSplitProcedureAmt(BigDecimal alSplitProcedureAmt) {
        this.alSplitProcedureAmt = alSplitProcedureAmt;
    }

    /**
     * Gets the value of unSplitPrinAmt.
     *
     * @return the value of unSplitPrinAmt
     */
    public BigDecimal getUnSplitPrinAmt() {
        return unSplitPrinAmt;
    }

    /**
     * Sets the unSplitPrinAmt.
     *
     * <p>You can use getUnSplitPrinAmt() to get the value of unSplitPrinAmt</p>
     *
     * @param unSplitPrinAmt unSplitPrinAmt
     */
    public void setUnSplitPrinAmt(BigDecimal unSplitPrinAmt) {
        this.unSplitPrinAmt = unSplitPrinAmt;
    }

    /**
     * Gets the value of investRate.
     *
     * @return the value of investRate
     */
    public BigDecimal getInvestRate() {
        return investRate;
    }

    /**
     * Sets the investRate.
     *
     * <p>You can use getInvestRate() to get the value of investRate</p>
     *
     * @param investRate investRate
     */
    public void setInvestRate(BigDecimal investRate) {
        this.investRate = investRate;
    }

    /**
     * Gets the value of procedureAmt.
     *
     * @return the value of procedureAmt
     */
    public BigDecimal getProcedureAmt() {
        return procedureAmt;
    }

    /**
     * Sets the procedureAmt.
     *
     * <p>You can use getProcedureAmt() to get the value of procedureAmt</p>
     *
     * @param procedureAmt procedureAmt
     */
    public void setProcedureAmt(BigDecimal procedureAmt) {
        this.procedureAmt = procedureAmt;
    }

    /**
     * Gets the value of channelFee.
     *
     * @return the value of channelFee
     */
    public BigDecimal getChannelFee() {
        return channelFee;
    }

    /**
     * Sets the channelFee.
     *
     * <p>You can use getChannelFee() to get the value of channelFee</p>
     *
     * @param channelFee channelFee
     */
    public void setChannelFee(BigDecimal channelFee) {
        this.channelFee = channelFee;
    }

    /**
     * Gets the value of projectType.
     *
     * @return the value of projectType
     */
    public String getProjectType() {
        return projectType;
    }

    /**
     * Sets the projectType.
     *
     * <p>You can use getProjectType() to get the value of projectType</p>
     *
     * @param projectType projectType
     */
    public void setProjectType(String projectType) {
        this.projectType = projectType;
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
     * Gets the value of finprodName.
     *
     * @return the value of finprodName
     */
    public String getFinprodName() {
        return finprodName;
    }

    /**
     * Sets the finprodName.
     *
     * <p>You can use getFinprodName() to get the value of finprodName</p>
     *
     * @param finprodName finprodName
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    /**
     * Gets the value of baseTradeInfoDtos.
     *
     * @return the value of baseTradeInfoDtos
     */
    public List<BaseTradeInfoDto> getBaseTradeInfoDtos() {
        return baseTradeInfoDtos;
    }

    /**
     * Sets the baseTradeInfoDtos.
     *
     * <p>You can use getBaseTradeInfoDtos() to get the value of baseTradeInfoDtos</p>
     *
     * @param baseTradeInfoDtos baseTradeInfoDtos
     */
    public void setBaseTradeInfoDtos(List<BaseTradeInfoDto> baseTradeInfoDtos) {
        this.baseTradeInfoDtos = baseTradeInfoDtos;
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
     * Gets the value of unitIntAmt.
     *
     * @return the value of unitIntAmt
     */
    public BigDecimal getUnitIntAmt() {
        return unitIntAmt;
    }

    /**
     * Sets the unitIntAmt.
     *
     * <p>You can use getUnitIntAmt() to get the value of unitIntAmt</p>
     *
     * @param unitIntAmt unitIntAmt
     */
    public void setUnitIntAmt(BigDecimal unitIntAmt) {
        this.unitIntAmt = unitIntAmt;
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
     * Gets the value of commissionAmt.
     *
     * @return the value of commissionAmt
     */
    public BigDecimal getCommissionAmt() {
        return commissionAmt;
    }

    /**
     * Sets the commissionAmt.
     *
     * <p>You can use getCommissionAmt() to get the value of commissionAmt</p>
     *
     * @param commissionAmt commissionAmt
     */
    public void setCommissionAmt(BigDecimal commissionAmt) {
        this.commissionAmt = commissionAmt;
    }

	public BigDecimal getLoadRate() {
		return loadRate;
	}

	public void setLoadRate(BigDecimal loadRate) {
		this.loadRate = loadRate;
	}
    /**
     * 计息基础
     */
    private String basis;


	public String getBasis() {
		return basis;
	}

	public void setBasis(String basis) {
		this.basis = basis;
	}

	public String secAcctId;

    /**
     * Gets the value of secAcctId.
     *
     * @return the value of secAcctId
     */
    public String getSecAcctId() {
        return secAcctId;
    }

    /**
     * Sets the secAcctId.
     *
     * <p>You can use getSecAcctId() to get the value of secAcctId</p>
     *
     * @param secAcctId secAcctId
     */
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    /**
     * 获取转让逾期利息 transOverdueIntAmt
     * @return
     */
	public BigDecimal getTransOverdueIntAmt() {
		return transOverdueIntAmt;
	}

	/**
	 * 设置转让逾期利息 transOverdueIntAmt
	 * @param transOverdueIntAmt
	 */
	public void setTransOverdueIntAmt(BigDecimal transOverdueIntAmt) {
		this.transOverdueIntAmt = transOverdueIntAmt;
	}

	/**
	 * 获取转让逾期本金 transOverduePrinAmt
	 * @return
	 */
	public BigDecimal getTransOverduePrinAmt() {
		return transOverduePrinAmt;
	}

	/**
	 * 设置转让逾期本金 transOverduePrinAmt
	 * @param transOverduePrinAmt
	 */
	public void setTransOverduePrinAmt(BigDecimal transOverduePrinAmt) {
		this.transOverduePrinAmt = transOverduePrinAmt;
	}

	/**
	 * 获取交割金额 deliveryAmount
	 * @return
	 */
	public BigDecimal getDeliveryAmount() {
		return deliveryAmount;
	}

	/**
	 * 设置交割金额 deliveryAmount
	 * @param deliveryAmount
	 */
	public void setDeliveryAmount(BigDecimal deliveryAmount) {
		this.deliveryAmount = deliveryAmount;
	}

    private boolean isTransfer;

    public boolean isTransfer() {
        return isTransfer;
    }

    public void setTransfer(boolean transfer) {
        isTransfer = transfer;
    }

    /**
     * 汇总业务编号
     */
    private String bizCode;

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
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

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    /**
     * 转仓 转出的业务模式
     */
    private String turnInvAim;

    public String getTurnInvAim() {
        return turnInvAim;
    }

    public void setTurnInvAim(String turnInvAim) {
        this.turnInvAim = turnInvAim;
    }

    private String fileArrayName;

    public String getFileArrayName() {
        return fileArrayName;
    }

    public void setFileArrayName(String fileArrayName) {
        this.fileArrayName = fileArrayName;
    }

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
}
