package com.joyintech.tams.cfets.etr.bond.dto;


import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value="债券指令查询返回参数",description="债券指令查询返回参数")
public class CfetsEtrIbfindEtrinfoResponse {

    @ApiModelProperty(name="effectiveStatus",value="有效状态Code",example="A1")
    private String effectiveStatus;

    @ApiModelProperty(name="effectiveStatusName",value="有效状态",example="A1")
    private String effectiveStatusName;

    @ApiModelProperty(name="entrustId",value="指令编号",example="A1")
    private String entrustId;

    @ApiModelProperty(name="entrustStatus",value="指令状态Code",example="A1")
    private String entrustStatus;

    @ApiModelProperty(name="entrustStatusName",value="指令状态",example="A1")
    private String entrustStatusName;

    @ApiModelProperty(name="execProcess",value="执行进度%",example="A1")
    private BigDecimal execProcess;

    @ApiModelProperty(name="transProcess",value="成交进度%",example="A1")
    private BigDecimal transProcess;

    @ApiModelProperty(name="invManagerId",value="投资经理",example="A1")
    private String invManagerId;

    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="名称A1")
    private String portfolioName;

    @ApiModelProperty(name="finprodId",value="资产代码Id",example="ZC1")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="secType1",value="债券一级分类Code",example="分类1Code")
    private String secType1;

    @ApiModelProperty(name="secType1Name",value="债券一级分类",example="分类1名称")
    private String secType1Name;

    @ApiModelProperty(name="psName",value="交易方向",example="T01")
    private String psName;

    @ApiModelProperty(name="ps",value="交易方向Code",example="T01")
    private String ps;

    @ApiModelProperty(name="shareNum",value="券面总额",example="11")
    private BigDecimal shareNum;

    @ApiModelProperty(name="priceModel",value="价格模式Code",example="01")
    private String priceModel;

    @ApiModelProperty(name="priceModelName",value="价格模式",example="01")
    private String priceModelName;

    @ApiModelProperty(name="busiModel",value="业务模式Code",example="01")
    private String busiModel;

    @ApiModelProperty(name="busiModelStr",value="业务模式",example="01")
    private String busiModelStr;

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

    @ApiModelProperty(name="settleSpreedName",value="清算速度",example="1")
    private String settleSpreedName;

    @ApiModelProperty(name="etrDate",value="指令日期",example="2021-07-06")
    private String etrDate;

    @ApiModelProperty(name="approver",value="审批人",example="1")
    private String approver;

    @ApiModelProperty(name="remark",value="备注",example="A1")
    private String remark;

    @ApiModelProperty(name="execFinishAmt",value="已完成指令额度",example="1")
    private BigDecimal execFinishAmt;

    @ApiModelProperty(name="execRemainderAmt",value="剩余指令额度",example="1")
    private BigDecimal execRemainderAmt;

    @ApiModelProperty(name="transFinishAmt",value="已成交额度",example="1")
    private BigDecimal transFinishAmt;

    @ApiModelProperty(name="transRemainderAmt",value="剩余成交额度",example="1")
    private BigDecimal transRemainderAmt;

    @ApiModelProperty(name="entrustOwnNum",value="本方券券面总额",example="1")
    private BigDecimal entrustOwnNum;

    @ApiModelProperty(name="entrustIntegrateNum",value="卖空购回券券面总额",example="1")
    private BigDecimal entrustIntegrateNum;

    @ApiModelProperty(name="calculateField",value="计算方式",example="price")
    private String  calculateField;

    @ApiModelProperty(name="wflExist",value="是否流程中数据",example="Y")
    private String  wflExist;


    public String getEffectiveStatus() {
        return effectiveStatus;
    }

    public void setEffectiveStatus(String effectiveStatus) {
        this.effectiveStatus = effectiveStatus;
    }

    public String getEffectiveStatusName() {
        return effectiveStatusName;
    }

    public void setEffectiveStatusName(String effectiveStatusName) {
        this.effectiveStatusName = effectiveStatusName;
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

    public String getEntrustStatusName() {
        if (StringUtil.isEmpty(getEntrustStatus())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.委托状态, getEntrustStatus());
    }

    public void setEntrustStatusName(String entrustStatusName) {
        this.entrustStatusName = entrustStatusName;
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

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
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

    public String getSecType1Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, getSecType1());
    }

    public void setSecType1Name(String secType1Name) {
        this.secType1Name = secType1Name;
    }

    public String getPsName() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, getPs());
    }

    public void setPsName(String psName) {
        this.psName = psName;
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

    public String getPriceModelName() {
        if (StringUtil.isEmpty(getPriceModel())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.价格模式, getPriceModel());
    }

    public void setPriceModelName(String priceModelName) {
        this.priceModelName = priceModelName;
    }

    public String getBusiModel() {
        return busiModel;
    }

    public void setBusiModel(String busiModel) {
        this.busiModel = busiModel;
    }

    public String getBusiModelStr() {
        if (StringUtil.isEmpty(getBusiModel())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, getBusiModel());
    }

    public void setBusiModelStr(String busiModelStr) {
        this.busiModelStr = busiModelStr;
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

    public String getSettleSpreedName() {
        if(StringUtil.isNotEmpty(this.getSettleSpreed())){
            return "T+" +this.getSettleSpreed();
        }
        return null;
    }

    public void setSettleSpreedName(String settleSpreedName) {
        this.settleSpreedName = settleSpreedName;
    }



    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public BigDecimal getEntrustOwnNum() {
        return entrustOwnNum;
    }

    public void setEntrustOwnNum(BigDecimal entrustOwnNum) {
        this.entrustOwnNum = entrustOwnNum;
    }

    public BigDecimal getEntrustIntegrateNum() {
        return entrustIntegrateNum;
    }

    public void setEntrustIntegrateNum(BigDecimal entrustIntegrateNum) {
        this.entrustIntegrateNum = entrustIntegrateNum;
    }


    public String getCalculateField() {
        return calculateField;
    }

    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }

    public String getEtrDate() {
        return etrDate;
    }

    public void setEtrDate(String etrDate) {
        this.etrDate = etrDate;
    }

    public String getWflExist() {
        return wflExist;
    }

    public void setWflExist(String wflExist) {
        this.wflExist = wflExist;
    }
}
