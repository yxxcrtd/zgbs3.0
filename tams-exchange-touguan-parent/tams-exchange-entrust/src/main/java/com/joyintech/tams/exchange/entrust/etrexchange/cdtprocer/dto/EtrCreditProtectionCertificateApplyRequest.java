package com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.validator.EtrExchangeTradeCpc001Validator;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePortfolioSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 *  交易信息 （左边下）
 * @author meifaxin
 * @version 1.0 2020年7月2日
 * @see EtrCreditProtectionCertificateApplyRequest
 * @since 1.0
 */
@PageId("etrCreditProtectionCertificate")
@ValidLogic(targetClass = EtrExchangeTradeCpc001Validator.class , groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
@ApiModel(value="指令下单表单",description="指令下单表单")
public class EtrCreditProtectionCertificateApplyRequest extends EtrEntrust implements ProcessEntity {

    @ApiModelProperty(name="operateType",value="操作类型",example="submit/save", required = true)
    private String operateType;
    @ApiModelProperty(name="elementId",value="按钮id",example="EtrExchangeTradeJj_btn_submit/EtrExchangeTradeJj_btn_save")
    private String elementId;
    @ApiModelProperty(name="pageId",value="d",example="EtrExchangeTradeJj")
    private String pageId;

    @ApiModelProperty(name="acctNo",value="资金账户",example="submit/save", required = true)
    private String acctNo;

    /**
     * 证券账户代码
     */
    private String secAcctId;

    @ApiModelProperty(name="finprodName",value="资产名称",example="", required = true)
    private String finprodName;

    @ApiModelProperty(required = true,name="tradeMarket",value="交易场所",example="A1")
    private String tradeMarket;

    @ApiModelProperty(name="targetType",value="目标类型",example="")
    private String targetType;

    @ApiModelProperty(name="priceModel",value="价格模式Code",example="01")
    private String priceModel;

    @ApiModelProperty(name="price",value="净价",example="")
    private BigDecimal price;

    @ApiModelProperty(name="declaType",value="全价价格",example="")
    private BigDecimal fprice;

    @ApiModelProperty(name="yield",value="收益率",example="")
    private BigDecimal yield;

    @ApiModelProperty(name="entrustPrice",value="指令价格",example="")
    private BigDecimal entrustPrice;


    @ApiModelProperty(name="entrustBussType",value="指令业务类型",example="")
    private String entrustBussType;

    @ApiModelProperty(name="fpriceClone",value="市价时全价价格,前端默认隐藏传值",example="")
    private BigDecimal fpriceClone;

    @ApiModelProperty(name="priceClone",value="市价时净价,前端默认隐藏传值",example="")
    private BigDecimal priceClone;

    @ApiModelProperty(name="yieldClone",value="市价时收益率,前端默认隐藏传值",example="")
    private BigDecimal yieldClone;

    /**
     * 可用金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal accAvailableCash;

    /**
     * 可用持仓
     */
    @ApiModelProperty(name="accAvailablePos",value="可用持仓",example="")
    private BigDecimal accAvailablePos;

    @ApiModelProperty(name="businessTypeName",value="业务类型名称",example="")
    private String businessTypeName;

    @ApiModelProperty(name="standardBondVolume",value="标准券数量",example="")
    private Long standardBondVolume;

    @ApiModelProperty(name="repoAmt",value="回购金额",example="12")
    private BigDecimal repoAmt;

    @ApiModelProperty(name="repoRate",value="回购利率",example="12")
    private BigDecimal repoRate;

    @ApiModelProperty(name="seatId",value="席位号",example="12")
    private String seatId;
    @ApiModelProperty(name="limitUpAmt",value="价格上限（涨停价格）",example="12")
    private BigDecimal limitUpAmt;

    @ApiModelProperty(name="limitDnAmt",value="价格下限（跌停价格）",example="12")
    private BigDecimal limitDnAmt;

    @ApiModelProperty(name="repPs",value="回购类型",example="03")
    private String repPs;

    @ApiModelProperty(name="assetType2",value="资产类型2",example="H01")
    private String assetType2;

    @ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
    private String softSkipMode;
    /**
     * 约定号
     */
    private String agreeOnId;

    /**
     * 对手方交易商
     */
    private String counterOrg;

    /**
     * 本方交易员
     */
    private String trader;


    private String confirmOk;

    public String getConfirmOk() {
        return confirmOk;
    }

    public void setConfirmOk(String confirmOk) {
        this.confirmOk = confirmOk;
    }

    public void setDeclaTypeStr(String declaTypeStr) {
        this.declaTypeStr = declaTypeStr;
    }

    @Override
    public BigDecimal getAccAvailableCash() {
        return accAvailableCash;
    }

    @Override
    public void setAccAvailableCash(BigDecimal accAvailableCash) {
        this.accAvailableCash = accAvailableCash;
    }

    public BigDecimal getAccAvailablePos() {
        return accAvailablePos;
    }

    public void setAccAvailablePos(BigDecimal accAvailablePos) {
        this.accAvailablePos = accAvailablePos;
    }

    /**
     * 回购期限
     */
    private String term;

    /**
     * 委托状态
     */
    private String recStatus;
    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 计算方式
     */
    private String calculateField;

    /**
     * 涨停
     */
    private BigDecimal limitUp;

    /**
     * 跌停
     */
    private BigDecimal limitDown;

    /**
     * 资产类型
     */
    private String assetsType;

    /**
     * 操作类型
     */
    private String pageModel;

    /**
     * 记录操作方式
     */
    private String optType;


    /**
     * 申报类型
     */
    private String declaType;

    /**
     * 可转股数量
     */
    private BigDecimal toShareAvailableNum;

    /**
     * 清算速度
     */
    private String settleSpeed;
    /**
     * 序号
     */
    private String seqNo;
    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    @ApiModelProperty(required = true,name="portfolioList",value="组合列表",example="[{'portfolioId':'qwqw'}]]")
    List<EtrExchangeTradePortfolioSecRepoRequest> portfolioList;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 是否多组合
     */
    private String isMulti;
    /**
     * 申报类型
     */
    private String declaTypeStr;
    /**
     * 风险预占是否来自场内
     */
    private String fromEtrExchange;

    /**
     * 头寸使用序列号
     */
    private Long positionSeqno;


    private BigDecimal disRate;

    public BigDecimal getDisRate() {
        return disRate;
    }

    public void setDisRate(BigDecimal disRate) {
        this.disRate = disRate;
    }

    public Long getPositionSeqno() {
        return positionSeqno;
    }

    public void setPositionSeqno(Long positionSeqno) {
        this.positionSeqno = positionSeqno;
    }

    /**
     * 取得 委托代码
     * @return 委托代码
     */
    @Override
    @PrimaryKey(value = PrimaryKey.Field.PK_1)
    public String getEntrustId() {
        return super.getEntrustId();
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 获取 fprice
     * @return fprice.
     */
    public BigDecimal getFprice() {
        // 现券市价指令模式时，全价取得前端隐藏传值
        if (DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue().equals(getEntrustBussType())
                && StringUtil.equals(getPriceModel(), DictCodeListExt2.价格模式_市价指令.getValue())){
            return getFpriceClone();
        }
        return fprice;
    }


    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 设置 fprice
     * @param fprice fprice
     */
    public void setFprice(BigDecimal fprice) {
        this.fprice = fprice;
    }

    public List<EtrExchangeTradePortfolioSecRepoRequest> getPortfolioList() {
        return portfolioList;
    }

    public void setPortfolioList(List<EtrExchangeTradePortfolioSecRepoRequest> portfolioList) {
        this.portfolioList = portfolioList;
    }

    /**
     * 获取 finprodName
     * @return finprodName.
     */
    public String getFinprodName() {
        return finprodName;
    }

    /**
     * 设置 finprodName
     * @param finprodName finprodName
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getCalculateField() {
        return calculateField;
    }

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }

    public BigDecimal getLimitUp() {
        return limitUp;
    }

    public void setLimitUp(BigDecimal limitUp) {
        this.limitUp = limitUp;
    }

    public BigDecimal getLimitDown() {
        return limitDown;
    }

    public void setLimitDown(BigDecimal limitDown) {
        this.limitDown = limitDown;
    }

    public String getAssetsType() {
        return assetsType;
    }

    public void setAssetsType(String assetsType) {
        this.assetsType = assetsType;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public String getOptType() {
        return optType;
    }

    public void setOptType(String optType) {
        this.optType = optType;
    }

    public String getDeclaType() {
        return declaType;
    }

    public void setDeclaType(String declaType) {
        this.declaType = declaType;
    }

    public BigDecimal getToShareAvailableNum() {
        return toShareAvailableNum;
    }

    public void setToShareAvailableNum(BigDecimal toShareAvailableNum) {
        this.toShareAvailableNum = toShareAvailableNum;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    /**
     * 获取 yield
     * @return yield.
     */
    public BigDecimal getYield() {
        // 现券市价指令模式时，净价取得前端隐藏传值
        if (DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue().equals(getEntrustBussType())
                && StringUtil.equals(getPriceModel(), DictCodeListExt2.价格模式_市价指令.getValue())){
            return getPriceClone();
        }
        return yield;
    }


    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public BigDecimal getLimitUpAmt() {
        return limitUpAmt;
    }

    public void setLimitUpAmt(BigDecimal limitUpAmt) {
        this.limitUpAmt = limitUpAmt;
    }

    public BigDecimal getLimitDnAmt() {
        return limitDnAmt;
    }

    public void setLimitDnAmt(BigDecimal limitDnAmt) {
        this.limitDnAmt = limitDnAmt;
    }

    public String getAgreeOnId() {
        return agreeOnId;
    }

    public void setAgreeOnId(String agreeOnId) {
        this.agreeOnId = agreeOnId;
    }

    public String getCounterOrg() {
        return counterOrg;
    }

    public void setCounterOrg(String counterOrg) {
        this.counterOrg = counterOrg;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getRecStatus() {
        return recStatus;
    }

    public void setRecStatus(String recStatus) {
        this.recStatus = recStatus;
    }

    /**
     * 设置 yield
     * @param yield yield
     */
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    /**
     * 获取 yield
     * @return yield.
     */
    public BigDecimal getEntrustPrice() {
        return entrustPrice;
    }



    /**
     * 设置 yield
     * @param entrustPrice
     */
    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    /**
     * 获取 operateType
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }


    /**
     * 设置 operateType
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }


    public BigDecimal getFpriceClone() {
        return fpriceClone;
    }
    public void setFpriceClone(BigDecimal fpriceClone) {
        this.fpriceClone = fpriceClone;
    }
    public BigDecimal getPriceClone() {
        return priceClone;
    }
    public void setPriceClone(BigDecimal priceClone) {
        this.priceClone = priceClone;
    }
    public BigDecimal getYieldClone() {
        return yieldClone;
    }
    public void setYieldClone(BigDecimal yieldClone) {
        this.yieldClone = yieldClone;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public String getEntrustBussType() {
        return entrustBussType;
    }
    public void setEntrustBussType(String entrustBussType) {
        this.entrustBussType = entrustBussType;
    }

    public String getBusinessTypeName() {
        return businessTypeName;
    }
    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
    }
    public String getTargetType() {
        return targetType;
    }
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    @Override
    public String getPriceModel() {
        return priceModel;
    }

    @Override
    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public Long getStandardBondVolume() {
        return standardBondVolume;
    }

    public void setStandardBondVolume(Long standardBondVolume) {
        this.standardBondVolume = standardBondVolume;
    }

    public BigDecimal getRepoAmt() {
        return repoAmt;
    }

    public void setRepoAmt(BigDecimal repoAmt) {
        this.repoAmt = repoAmt;
    }

    public BigDecimal getRepoRate() {
        return repoRate;
    }

    public void setRepoRate(BigDecimal repoRate) {
        this.repoRate = repoRate;
    }

    /**
     * 获取 getPsStr
     * @return getPsStr.
     */
    public String getPsStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, super.getPs());
    }

    public String getTradeMarketString(){
        return  SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }
    //add by chenjiaxing 投资目的值转化 start
    public String getInvAimStr(){
        return  SysDataUtil.getNameFromDataDict(DictCode.投资目的, super.getInvAim());
    }
    //add by chenjiaxing 投资目的值转化 end

    public String getPriceModelStr(){
        return  SysDataUtil.getNameFromDataDict(DictCode.价格模式, super.getPriceModel());
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    @Override
    public BigDecimal getPrice() {
        // 现券市价指令模式时，净价取得前端隐藏传值
        if (DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue().equals(getEntrustBussType())
                && StringUtil.equals(getPriceModel(), DictCodeListExt2.价格模式_市价指令.getValue())){
            return getPriceClone();
        }
        return price;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    @Override
    public String getTradeMarket() {
        return tradeMarket;
    }

    @Override
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getIsMulti() {
        return isMulti;
    }

    public void setIsMulti(String isMulti) {
        this.isMulti = isMulti;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getDeclaTypeStr(){
        return  SysDataUtil.getNameFromDataDict(DictCode.申报类型, getDeclaType());
    }

    public String getRepPs() {
        return repPs;
    }

    public void setRepPs(String repPs) {
        this.repPs = repPs;
    }

    public String getAssetType2() {
        return assetType2;
    }

    public void setAssetType2(String assetType2) {
        this.assetType2 = assetType2;
    }

    public String getFromEtrExchange() {
        return fromEtrExchange;
    }

    public void setFromEtrExchange(String fromEtrExchange) {
        this.fromEtrExchange = fromEtrExchange;
    }

    public List<EtrExchangeTradeSaveSecRepoRequest> selectedItem;

    public List<EtrExchangeTradeSaveSecRepoRequest> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<EtrExchangeTradeSaveSecRepoRequest> selectedItem) {
        this.selectedItem = selectedItem;
    }

    private String isDoRkRedisFlag;

    public String getIsDoRkRedisFlag() {
        return isDoRkRedisFlag;
    }

    public void setIsDoRkRedisFlag(String isDoRkRedisFlag) {
        this.isDoRkRedisFlag = isDoRkRedisFlag;
    }

    private String currentTab;

    public String getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(String currentTab) {
        this.currentTab = currentTab;
    }

    @ApiModelProperty(name="priceType",value="计价方式",example="01")
    private String priceType;

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    /**
     * 交易方式
     */
    @ApiModelProperty(name="dealMode",value="指定交易方式",example="0")
    private String dealMode;

    public String getDealMode() {
        return dealMode;
    }

    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }
}

