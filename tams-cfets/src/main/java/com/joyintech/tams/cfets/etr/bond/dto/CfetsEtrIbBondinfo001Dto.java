package com.joyintech.tams.cfets.etr.bond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 银行间债券指令
 * @author chengzhong
 * @version 1.0 2021年1月19日
 * @see CfetsEtrIbBondinfo001Dto
 * @since 1.0
 */
@PageId("EtrIbBondinfo001")
public class CfetsEtrIbBondinfo001Dto extends BaseEntity{


    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;
    
    /**
     * 持仓数量
     */
    private BigDecimal amount;
    
    /**
     * 单位成本
     */
    private BigDecimal unitCost;
    
    /**
     * 投资目的
     */
    private String invAim;


    @ApiModelProperty(name="portfolioName",value="组合名称",example="名称A1")
    private String portfolioName;

    /**
     * 金融产品代码
     */
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="secType1",value="债券一级分类Code",example="分类1Code")
    private String secType1;

    /**
     * 持仓券面总额
     */
    private BigDecimal shareAmt;

    /**
     * 质押券面总额
     */
    private BigDecimal avaPledgePrin;

    /**
     * 质押券面总额
     */
    private BigDecimal avaShareAmt;

    /**
     * 持仓成本
     */
    private BigDecimal positionsAmt;

    /**
     * 最新价
     */
    private BigDecimal price;

    /**
     * 持仓市值
     */
    private BigDecimal positionsPrice;

    /**
     * 浮动盈亏
     */
    private BigDecimal floatingPl;


    @ApiModelProperty(name="entrustId",value="指令编号",example="A1")
    private String entrustId;

    /**
     * 有效状态
     */
    @ApiModelProperty(name="entrustStatus",value="指令状态Code",example="A1")
    private String entrustStatus;


    @ApiModelProperty(name="invManagerId",value="投资经理",example="A1")
    private String invManagerId;


    @ApiModelProperty(name="ps",value="交易方向Code",example="T01")
    private String ps;


    @ApiModelProperty(name="shareNum",value="券面总额",example="11")
    private BigDecimal shareNum;


    @ApiModelProperty(name="priceModel",value="价格模式Code",example="01")
    private String priceModel;


    @ApiModelProperty(name="unitPrice",value="净价",example="1")
    private BigDecimal unitPrice;

    @ApiModelProperty(name="allPrice",value="全价",example="1")
    private BigDecimal allPrice;


    @ApiModelProperty(name="matureYield",value="到期收益率",example="1")
    private BigDecimal matureYield;


    @ApiModelProperty(name="exerYield",value="行权收益率",example="1")
    private BigDecimal exerYield;


    @ApiModelProperty(name="settleSpreed",value="清算速度Code",example="1")
    private String settleSpreed;

    /**
     * 指令开始日
     */
    private Date vdate;

    /**
     * 指令结束日
     */
    private Date mdate;


    @ApiModelProperty(name="finishAmt",value="已完成额度",example="1")
    private BigDecimal finishAmt;

    /**
     * 交易完成度
     */
    private BigDecimal transactioncom;


    @ApiModelProperty(name="remark",value="备注",example="A1")
    private String remark;

    /**
     * 持仓单位成本(综合成本法)
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal costPosition;

    /**
     * 持仓数量(综合成本法)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal costPositionNum;

    /**
     * 通道代码
     */
    private String channelId;

    /**
     * 交易对手
     */
    private String counterId;

    @ApiModelProperty(name="effectiveStatus",value="有效状态Code",example="A1")
    private String effectiveStatus;

    @ApiModelProperty(name="effectiveStatusName",value="有效状态",example="A1")
    private String effectiveStatusName;


    @ApiModelProperty(name="entrustStatusName",value="指令状态",example="A1")
    private String entrustStatusName;

    @ApiModelProperty(name="execProcess",value="执行进度%",example="A1")
    private BigDecimal execProcess;

    @ApiModelProperty(name="transProcess",value="成交进度%",example="A1")
    private BigDecimal transProcess;

    @ApiModelProperty(name="secType1Name",value="债券一级分类",example="分类1名称")
    private String secType1Name;

    @ApiModelProperty(name="psName",value="交易方向",example="T01")
    private String psName;

    @ApiModelProperty(name="priceModelName",value="价格模式",example="01")
    private String priceModelName;

    @ApiModelProperty(name="settleSpreedName",value="清算速度",example="1")
    private String settleSpreedName;

    @ApiModelProperty(name="etrDate",value="指令日期",example="2021-07-06")
    private Date etrDate;

    @ApiModelProperty(name="approver",value="审批人",example="1")
    private String approver;

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;


    @ApiModelProperty(name="execFinishAmt",value="已完成指令额度",example="1")
    private BigDecimal execFinishAmt;

    @ApiModelProperty(name="execRemainderAmt",value="剩余指令额度",example="1")
    private BigDecimal execRemainderAmt;

    @ApiModelProperty(name="transFinishAmt",value="已成交额度",example="1")
    private BigDecimal transFinishAmt;

    @ApiModelProperty(name="transRemainderAmt",value="剩余成交额度",example="1")
    private BigDecimal transRemainderAmt;

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

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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

    public String getSecType1() {
        return secType1;
    }

    public void setSecType1(String secType1) {
        this.secType1 = secType1;
    }

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public BigDecimal getAvaPledgePrin() {
        return avaPledgePrin;
    }

    public void setAvaPledgePrin(BigDecimal avaPledgePrin) {
        this.avaPledgePrin = avaPledgePrin;
    }

    public BigDecimal getAvaShareAmt() {
        return avaShareAmt;
    }

    public void setAvaShareAmt(BigDecimal avaShareAmt) {
        this.avaShareAmt = avaShareAmt;
    }

    public BigDecimal getPositionsAmt() {
        return positionsAmt;
    }

    public void setPositionsAmt(BigDecimal positionsAmt) {
        this.positionsAmt = positionsAmt;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPositionsPrice() {
        return positionsPrice;
    }

    public void setPositionsPrice(BigDecimal positionsPrice) {
        this.positionsPrice = positionsPrice;
    }

    public BigDecimal getFloatingPl() {
        return floatingPl;
    }

    public void setFloatingPl(BigDecimal floatingPl) {
        this.floatingPl = floatingPl;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public BigDecimal getShareNum() {
        return shareNum;
    }

    public void setShareNum(BigDecimal shareNum) {
        this.shareNum = shareNum;
    }

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(BigDecimal allPrice) {
        this.allPrice = allPrice;
    }

    public BigDecimal getMatureYield() {
        return matureYield;
    }

    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    public BigDecimal getExerYield() {
        return exerYield;
    }

    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    public String getSettleSpreed() {
        return settleSpreed;
    }

    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
    }

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public BigDecimal getFinishAmt() {
        return finishAmt;
    }

    public void setFinishAmt(BigDecimal finishAmt) {
        this.finishAmt = finishAmt;
    }

    public BigDecimal getTransactioncom() {
        return transactioncom;
    }

    public void setTransactioncom(BigDecimal transactioncom) {
        this.transactioncom = transactioncom;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 取得 持仓单位成本(综合成本法)
     * @return 持仓单位成本(综合成本法)
     */
    public BigDecimal getCostPosition() {
        return costPosition;
    }

    /**
     * 设置 持仓单位成本(综合成本法)
     * @param costPosition 持仓单位成本(综合成本法)
     */
    public void setCostPosition(BigDecimal costPosition) {
        this.costPosition = costPosition;
    }

    /**
     * 取得 持仓数量(综合成本法)
     * @return 持仓数量(综合成本法)
     */
    public BigDecimal getCostPositionNum() {
        return costPositionNum;
    }

    /**
     * 设置 持仓数量(综合成本法)
     * @param costPositionNum 持仓数量(综合成本法)
     */
    public void setCostPositionNum(BigDecimal costPositionNum) {
        this.costPositionNum = costPositionNum;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    private String parentPortfolioId;


    public String getParentPortfolioId() {
        return parentPortfolioId;
    }

    public void setParentPortfolioId(String parentPortfolioId) {
        this.parentPortfolioId = parentPortfolioId;
    }

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
	}

	public String getInvAim() {
		return invAim;
	}

	public void setInvAim(String invAim) {
		this.invAim = invAim;
	}

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getEntrustStatusName(){
        if (StringUtil.isEmpty(getEntrustStatus())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.委托状态, getEntrustStatus());
    }

    public String getEffectiveStatusName() {
        return effectiveStatusName;
    }

    public void setEffectiveStatusName(String effectiveStatusName) {
        this.effectiveStatusName = effectiveStatusName;
    }

    public BigDecimal getExecProcess() {
        return execProcess;
    }

    public void setExecProcess(BigDecimal execProcess) {
        this.execProcess = execProcess;
    }

    public BigDecimal getTransProcess() {
        return transProcess;
    }

    public void setTransProcess(BigDecimal transProcess) {
        this.transProcess = transProcess;
    }

    public String getSecType1Name() {
        return secType1Name;
    }

    public void setSecType1Name(String secType1Name) {
        this.secType1Name = secType1Name;
    }

    public String getPriceModelName() {
        return priceModelName;
    }

    public void setPriceModelName(String priceModelName) {
        this.priceModelName = priceModelName;
    }

    public String getPsName() {
        return psName;
    }

    public void setPsName(String psName) {
        this.psName = psName;
    }

    public String getSettleSpreedName() {
        return settleSpreedName;
    }

    public void setSettleSpreedName(String settleSpreedName) {
        this.settleSpreedName = settleSpreedName;
    }

    public Date getEtrDate() {
        return etrDate;
    }

    public void setEtrDate(Date etrDate) {
        this.etrDate = etrDate;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public BigDecimal getExecFinishAmt() {
        return execFinishAmt;
    }

    public void setExecFinishAmt(BigDecimal execFinishAmt) {
        this.execFinishAmt = execFinishAmt;
    }

    public BigDecimal getExecRemainderAmt() {
        return execRemainderAmt;
    }

    public void setExecRemainderAmt(BigDecimal execRemainderAmt) {
        this.execRemainderAmt = execRemainderAmt;
    }

    public BigDecimal getTransFinishAmt() {
        return transFinishAmt;
    }

    public void setTransFinishAmt(BigDecimal transFinishAmt) {
        this.transFinishAmt = transFinishAmt;
    }

    public BigDecimal getTransRemainderAmt() {
        return transRemainderAmt;
    }

    public void setTransRemainderAmt(BigDecimal transRemainderAmt) {
        this.transRemainderAmt = transRemainderAmt;
    }
}
