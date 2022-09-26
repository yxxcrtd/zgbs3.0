package com.joyintech.tams.invest.trade.transfer.trdrolloverpledgedrepo.dto;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.trade.lend.dto.FinTradeProductDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.List;

public class TrdRolloverPledgedRepo001Dto extends TrdProductDeal implements ProcessEntity{

    /**
     * 操作类型
     */
    private String operateType;

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
     * 回购类型：质押式协议回购，质押式竞价回购
     */
    private String repoType;

    /**
     * 期限品种
     */
    private String termTypeReback;

    /**
     * 回购期限
     */
    private Long term;

    /**
     * 实际占款天数
     */
    private int actCapDays;

    /**
     * 计息基础
     */
    private String basis;

    /**
     * 回购利率%
     */
    private BigDecimal rebackPrice;

    /**
     * 证券托管户
     */
    private String secAcctId;

    /**
     * 通道
     */
    private String chlId;

    /**
     * 剩余可转仓本金金额
     */
    private BigDecimal avaRolloverAmt;

    //转仓信息
    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 本金金额
     */
    private BigDecimal rolloverPrinAmt;

    /**
     * 回购利息
     */
    private BigDecimal rolloverRepoIntAmt;

    /**
     *  交易金额
     */
    private BigDecimal rolloverTradeAmt;

    /**
     * 转仓交易汇总券面总额
     */
    private BigDecimal sumRolloverPrinAmt;

    /**
     * 质押券转仓列表
     */
    List<FinDealPledgeDto> pledgedRepoRolloverList;

    /**
     * 列表信息
     */
    List<TrdRolloverPledgedRepo001Dto> pledgedRepoInfoList;
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
     * 待转组合
     */
    private String invTransPortf;

    /**
     * 实际组合代码
     */
    private String actPortfolioId;

    /**
     * 交易日历
     */
    private String calendarId;

    /**
     * 是否保本
     */
    private String isGuarantee;

    /**
     * 已拆分金额
     */
    private BigDecimal splitShareAmt;
    /**
     * 交易员
     */
    private String trader;
    /**
     * 交易员部门
     */
    private String deptCode;

    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**
     * Gets the value of isGuarantee.
     *
     * @return the value of isGuarantee
     */
    public String getIsGuarantee() {
        return isGuarantee;
    }

    /**
     * Sets the isGuarantee.
     *
     * <p>You can use getIsGuarantee() to get the value of isGuarantee</p>
     *
     * @param isGuarantee isGuarantee
     */
    public void setIsGuarantee(String isGuarantee) {
        this.isGuarantee = isGuarantee;
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
     * Gets the value of repoType.
     *
     * @return the value of repoType
     */
    public String getRepoType() {
        return repoType;
    }

    /**
     * Sets the repoType.
     *
     * <p>You can use getRepoType() to get the value of repoType</p>
     *
     * @param repoType repoType
     */
    public void setRepoType(String repoType) {
        this.repoType = repoType;
    }

    /**
     * Gets the value of termTypeReback.
     *
     * @return the value of termTypeReback
     */
    public String getTermTypeReback() {
        return termTypeReback;
    }

    /**
     * Sets the termTypeReback.
     *
     * <p>You can use getTermTypeReback() to get the value of termTypeReback</p>
     *
     * @param termTypeReback termTypeReback
     */
    public void setTermTypeReback(String termTypeReback) {
        this.termTypeReback = termTypeReback;
    }

    /**
     * Gets the value of term.
     *
     * @return the value of term
     */
    public Long getTerm() {
        return term;
    }

    /**
     * Sets the term.
     *
     * <p>You can use getTerm() to get the value of term</p>
     *
     * @param term term
     */
    public void setTerm(Long term) {
        this.term = term;
    }

    /**
     * Gets the value of actCapDays.
     *
     * @return the value of actCapDays
     */
    public int getActCapDays() {
        return actCapDays;
    }

    /**
     * Sets the actCapDays.
     *
     * <p>You can use getActCapDays() to get the value of actCapDays</p>
     *
     * @param actCapDays actCapDays
     */
    public void setActCapDays(int actCapDays) {
        this.actCapDays = actCapDays;
    }

    /**
     * Gets the value of basis.
     *
     * @return the value of basis
     */
    public String getBasis() {
        return basis;
    }

    /**
     * Sets the basis.
     *
     * <p>You can use getBasis() to get the value of basis</p>
     *
     * @param basis basis
     */
    public void setBasis(String basis) {
        this.basis = basis;
    }

    /**
     * Gets the value of rebackPrice.
     *
     * @return the value of rebackPrice
     */
    public BigDecimal getRebackPrice() {
        return rebackPrice;
    }

    /**
     * Sets the rebackPrice.
     *
     * <p>You can use getRebackPrice() to get the value of rebackPrice</p>
     *
     * @param rebackPrice rebackPrice
     */
    public void setRebackPrice(BigDecimal rebackPrice) {
        this.rebackPrice = rebackPrice;
    }

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
     * Gets the value of chlId.
     *
     * @return the value of chlId
     */
    @Override
    public String getChlId() {
        return chlId;
    }

    /**
     * Sets the chlId.
     *
     * <p>You can use getChlId() to get the value of chlId</p>
     *
     * @param chlId chlId
     */
    @Override
    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    /**
     * Gets the value of avaRolloverAmt.
     *
     * @return the value of avaRolloverAmt
     */
    public BigDecimal getAvaRolloverAmt() {
        return avaRolloverAmt;
    }

    /**
     * Sets the avaRolloverAmt.
     *
     * <p>You can use getAvaRolloverAmt() to get the value of avaRolloverAmt</p>
     *
     * @param avaRolloverAmt avaRolloverAmt
     */
    public void setAvaRolloverAmt(BigDecimal avaRolloverAmt) {
        this.avaRolloverAmt = avaRolloverAmt;
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
     * Gets the value of rolloverPrinAmt.
     *
     * @return the value of rolloverPrinAmt
     */
    public BigDecimal getRolloverPrinAmt() {
        return rolloverPrinAmt;
    }

    /**
     * Sets the rolloverPrinAmt.
     *
     * <p>You can use getRolloverPrinAmt() to get the value of rolloverPrinAmt</p>
     *
     * @param rolloverPrinAmt rolloverPrinAmt
     */
    public void setRolloverPrinAmt(BigDecimal rolloverPrinAmt) {
        this.rolloverPrinAmt = rolloverPrinAmt;
    }

    /**
     * Gets the value of rolloverRepoIntAmt.
     *
     * @return the value of rolloverRepoIntAmt
     */
    public BigDecimal getRolloverRepoIntAmt() {
        return rolloverRepoIntAmt;
    }

    /**
     * Sets the rolloverRepoIntAmt.
     *
     * <p>You can use getRolloverRepoIntAmt() to get the value of rolloverRepoIntAmt</p>
     *
     * @param rolloverRepoIntAmt rolloverRepoIntAmt
     */
    public void setRolloverRepoIntAmt(BigDecimal rolloverRepoIntAmt) {
        this.rolloverRepoIntAmt = rolloverRepoIntAmt;
    }

    /**
     * Gets the value of rolloverTradeAmt.
     *
     * @return the value of rolloverTradeAmt
     */
    public BigDecimal getRolloverTradeAmt() {
        return rolloverTradeAmt;
    }

    /**
     * Sets the rolloverTradeAmt.
     *
     * <p>You can use getRolloverTradeAmt() to get the value of rolloverTradeAmt</p>
     *
     * @param rolloverTradeAmt rolloverTradeAmt
     */
    public void setRolloverTradeAmt(BigDecimal rolloverTradeAmt) {
        this.rolloverTradeAmt = rolloverTradeAmt;
    }

    /**
     * Gets the value of sumRolloverPrinAmt.
     *
     * @return the value of sumRolloverPrinAmt
     */
    public BigDecimal getSumRolloverPrinAmt() {
        return sumRolloverPrinAmt;
    }

    /**
     * Sets the sumRolloverPrinAmt.
     *
     * <p>You can use getSumRolloverPrinAmt() to get the value of sumRolloverPrinAmt</p>
     *
     * @param sumRolloverPrinAmt sumRolloverPrinAmt
     */
    public void setSumRolloverPrinAmt(BigDecimal sumRolloverPrinAmt) {
        this.sumRolloverPrinAmt = sumRolloverPrinAmt;
    }


    /**
     * Gets the value of pledgedRepoRolloverList.
     *
     * @return the value of pledgedRepoRolloverList
     */
    public List<FinDealPledgeDto> getPledgedRepoRolloverList() {
        return pledgedRepoRolloverList;
    }

    /**
     * Sets the pledgedRepoRolloverList.
     *
     * <p>You can use getPledgedRepoRolloverList() to get the value of pledgedRepoRolloverList</p>
     *
     * @param pledgedRepoRolloverList pledgedRepoRolloverList
     */
    public void setPledgedRepoRolloverList(List<FinDealPledgeDto> pledgedRepoRolloverList) {
        this.pledgedRepoRolloverList = pledgedRepoRolloverList;
    }

    /**
     * Gets the value of pledgedRepoInfoList.
     *
     * @return the value of pledgedRepoInfoList
     */
    public List<TrdRolloverPledgedRepo001Dto> getPledgedRepoInfoList() {
        return pledgedRepoInfoList;
    }

    /**
     * Sets the pledgedRepoInfoList.
     *
     * <p>You can use getPledgedRepoInfoList() to get the value of pledgedRepoInfoList</p>
     *
     * @param pledgedRepoInfoList pledgedRepoInfoList
     */
    public void setPledgedRepoInfoList(List<TrdRolloverPledgedRepo001Dto> pledgedRepoInfoList) {
        this.pledgedRepoInfoList = pledgedRepoInfoList;
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
     * Gets the value of splitShareAmt.
     *
     * @return the value of splitShareAmt
     */
    public BigDecimal getSplitShareAmt() {
        return splitShareAmt;
    }

    /**
     * Sets the splitShareAmt.
     *
     * <p>You can use getSplitShareAmt() to get the value of splitShareAmt</p>
     *
     * @param splitShareAmt splitShareAmt
     */
    public void setSplitShareAmt(BigDecimal splitShareAmt) {
        this.splitShareAmt = splitShareAmt;
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
}
