package com.joyintech.tams.cfets.etrdispatcher.dto;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsPledgeBondInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 场外指令分发
 * 指令成交返回数据
 *
 * @author sjj
 * @version 1.0 2021年11月4日
 * @see CfetsEtrDispatcherDealListInfoResponse
 * @since 1.0
 */
@ApiModel(value = "指令成交返回数据", description = "指令成交返回数据")
public class CfetsEtrDispatcherDealListInfoResponse extends CfetsEtrDispatcherBaseInfo {

    @ApiModelProperty(name = "effectFlag", value = "有效状态code", example = "01")
    private String effectFlag;

    @ApiModelProperty(name = "totleTradeId", value = "汇总交易编号", example = "A1")
    private String totleTradeId;

    @ApiModelProperty(name = "tradeId", value = "业务编号", example = "A1")
    private String tradeId;

    @ApiModelProperty(name = "secAcctId", value = "证券账户code", example = "XXX证券账户")
    private String secAcctId;

    @ApiModelProperty(name = "busiTypePosition", value = "业务类型", example = "A1")
    private String busiTypePosition;

    @ApiModelProperty(name = "secAcctIdStr", value = "证券账户", example = "XXX证券账户")
    private String secAcctIdStr;

    @ApiModelProperty(name = "tradeDate", value = "成交日期", example = "2021-01-01")
    private String tradeDate;

    @ApiModelProperty(name = "purCfmDate", value = "确认日期", example = "2021-01-01")
    private String purCfmDate;

    @ApiModelProperty(name = "settleSpeed", value = "清算速度code", example = "T+0")
    private String settleSpeed;

    @ApiModelProperty(name = "settleSpeedStr", value = "清算速度", example = "T+0")
    private String settleSpeedStr;

    @ApiModelProperty(name = "settleDate", value = "结算日期", example = "2021-01-01")
    private String settleDate;

    @ApiModelProperty(name = "unitPrice", value = "净价", example = "100")
    private BigDecimal unitPrice;

    @ApiModelProperty(name = "matureYield", value = "到期收益率", example = "1.11")
    private BigDecimal matureYield;

    @ApiModelProperty(name = "exerYield", value = "行权收益率", example = "1.11")
    private BigDecimal exerYield;

    @ApiModelProperty(name = "shareNum", value = "券面总额", example = "1000000")
    private BigDecimal shareNum;

    @ApiModelProperty(name = "unitInt", value = "应计利息（元）", example = "100")
    private BigDecimal unitInt;

    @ApiModelProperty(name = "allPrice", value = "全价", example = "100")
    private BigDecimal allPrice;

    @ApiModelProperty(name = "cpriceAmt", value = "净价总额", example = "100")
    private BigDecimal cpriceAmt;

    @ApiModelProperty(name = "intAmt", value = "应计利息总额（元）", example = "100")
    private BigDecimal intAmt;

    @ApiModelProperty(name = "fpriceAmt", value = "全价总额", example = "1000000")
    private BigDecimal fpriceAmt;

    @ApiModelProperty(name = "jyFeeAmt", value = "交易手续费", example = "1000000")
    private BigDecimal jyFeeAmt;

    @ApiModelProperty(name = "jsFeeAmt", value = "结算手续费", example = "1000000")
    private BigDecimal jsFeeAmt;

    @ApiModelProperty(name = "counterName", value = "交易对手", example = "A1")
    private String counterName;

    @ApiModelProperty(name = "remark", value = "备注", example = "A1")
    private String remark;

    @ApiModelProperty(name = "termType", value = "期限", example = "1Y/1M")
    private String termType;

    @ApiModelProperty(name = "repoTerm", value = "回购期限(天)", example = "5")
    private String repoTerm;

    @ApiModelProperty(name = "vpayDate", value = "首次结算日", example = "2021-01-01")
    private String vpayDate;

    @ApiModelProperty(name = "mpayDate", value = "到期结算日", example = "2021-01-01")
    private String mpayDate;

    @ApiModelProperty(name = "actCapDays", value = "实际占款天数（天）", example = "5")
    private Long actCapDays;

    @ApiModelProperty(name = "rebackPrice", value = "回购利率%", example = "3")
    private BigDecimal rebackPrice;

    @ApiModelProperty(name = "tradeAmt", value = "交易金额（元）", example = "10000")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name = "shareAmt", value = "交易份额", example = "10000")
    private BigDecimal shareAmt;

    @ApiModelProperty(name = "mIntAmt", value = "回购利息", example = "1.1")
    private BigDecimal mIntAmt;

    @ApiModelProperty(name = "vTradeAmt", value = "首次结算金额", example = "10001")
    private BigDecimal vTradeAmt;

    @ApiModelProperty(name = "mTradeAmt", value = "到期结算金额", example = "10001")
    private BigDecimal mTradeAmt;

    @ApiModelProperty(name = "fCprice", value = "首次净价", example = "99")
    private BigDecimal fCprice;

    @ApiModelProperty(name = "fYield", value = "首次收益率%", example = "1.1")
    private BigDecimal fYield;

    @ApiModelProperty(name = "mCprice", value = "到期净价", example = "99")
    private BigDecimal mCprice;

    @ApiModelProperty(name = "mYield", value = "到期收益率%", example = "1.1")
    private BigDecimal mYield;

    @ApiModelProperty(name = "secTotalPrin", value = "券面总额（万元）", example = "100")
    private BigDecimal secTotalPrin;

    @ApiModelProperty(name = "pledgeStockName", value = "质押券信息", example = "100")
    private String pledgeStockName;

    @ApiModelProperty(name = "detailList", value = "质押券明细", example = "100")
    private List<CfetsPledgeBondInfoDto> detailList;

    @ApiModelProperty(name = "chlId", value = "通道代码", example = "字符串1")
    private String chlId;

    @ApiModelProperty(name = "chlIdName", value = "通道名称", example = "字符串1")
    private String chlIdName;

    @ApiModelProperty(name = "orgName", value = "机构名称", example = "字符串1")
    private String orgName;

    @ApiModelProperty(name = "pledgeFinprodId", value = "标的资产代码", example = "字符串1")
    private String pledgeFinprodId;

    @ApiModelProperty(name = "pledgeFinprodName", value = "标的资产名称", example = "字符串1")
    private String pledgeFinprodName;

    @ApiModelProperty(name = "tradeMarket", value = "交易场所", example = "字符串1")
    private String tradeMarket;

    @ApiModelProperty(name = "paBussType", value = "拆借类型", example = "字符串1")
    private String paBussType;

    @ApiModelProperty(name = "assetType1", value = "资产二级分类", example = "字符串1")
    private String assetType1;

    @ApiModelProperty(name = "assetType10", value = "资产三级分类", example = "字符串1")
    private String assetType10;

    @ApiModelProperty(name = "assetType11", value = "资产四级分类", example = "字符串1")
    private String assetType11;

    @ApiModelProperty(name = "finprodType2", value = "资产类型", example = "字符串1")
    private String finprodType2;

    @ApiModelProperty(name = "prinAmt", value = "本金金额", example = "10000")
    private BigDecimal prinAmt;

    @ApiModelProperty(name = "actPayAmt", value = "实付金额", example = "10000")
    private BigDecimal actPayAmt;

    @ApiModelProperty(name = "recommender", value = "资产推荐方", example = "字符串1")
    private String recommender;

    @ApiModelProperty(name = "recommenderName", value = "资产推荐方名称", example = "字符串1")
    private String recommenderName;

    @ApiModelProperty(name = "registOrgStr", value = "登记托管机构", example = "字符串1")
    private String registOrgStr;

    @ApiModelProperty(name = "deliveryResult", value = "交收状态")
    private String deliveryResult;

    @ApiModelProperty(name = "deliverySchedule", value = "交收进度")
    private String deliverySchedule;

    public String getEffectFlag() {
        return effectFlag;
    }

    public void setEffectFlag(String effectFlag) {
        this.effectFlag = effectFlag;
    }

    public String getTotleTradeId() {
        return totleTradeId;
    }

    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getSecAcctIdStr() {
        return secAcctIdStr;
    }

    public void setSecAcctIdStr(String secAcctIdStr) {
        this.secAcctIdStr = secAcctIdStr;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getSettleSpeedStr() {
        return settleSpeedStr;
    }

    public void setSettleSpeedStr(String settleSpeedStr) {
        this.settleSpeedStr = settleSpeedStr;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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

    public BigDecimal getShareNum() {
        return shareNum;
    }

    public void setShareNum(BigDecimal shareNum) {
        this.shareNum = shareNum;
    }

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    public BigDecimal getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(BigDecimal allPrice) {
        this.allPrice = allPrice;
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

    public BigDecimal getJyFeeAmt() {
        return jyFeeAmt;
    }

    public void setJyFeeAmt(BigDecimal jyFeeAmt) {
        this.jyFeeAmt = jyFeeAmt;
    }

    public BigDecimal getJsFeeAmt() {
        return jsFeeAmt;
    }

    public void setJsFeeAmt(BigDecimal jsFeeAmt) {
        this.jsFeeAmt = jsFeeAmt;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(String repoTerm) {
        this.repoTerm = repoTerm;
    }

    public String getVpayDate() {
        return vpayDate;
    }

    public void setVpayDate(String vpayDate) {
        this.vpayDate = vpayDate;
    }

    public String getMpayDate() {
        return mpayDate;
    }

    public void setMpayDate(String mpayDate) {
        this.mpayDate = mpayDate;
    }

    public Long getActCapDays() {
        return actCapDays;
    }

    public void setActCapDays(Long actCapDays) {
        this.actCapDays = actCapDays;
    }

    public BigDecimal getRebackPrice() {
        return rebackPrice;
    }

    public void setRebackPrice(BigDecimal rebackPrice) {
        this.rebackPrice = rebackPrice;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    public BigDecimal getmTradeAmt() {
        return mTradeAmt;
    }

    public void setmTradeAmt(BigDecimal mTradeAmt) {
        this.mTradeAmt = mTradeAmt;
    }

    public BigDecimal getmCprice() {
        return mCprice;
    }

    public void setmCprice(BigDecimal mCprice) {
        this.mCprice = mCprice;
    }

    public BigDecimal getmYield() {
        return mYield;
    }

    public void setmYield(BigDecimal mYield) {
        this.mYield = mYield;
    }

    public BigDecimal getSecTotalPrin() {
        return secTotalPrin;
    }

    public void setSecTotalPrin(BigDecimal secTotalPrin) {
        this.secTotalPrin = secTotalPrin;
    }

    public String getOtherEffectFlag() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易流程状态, this.getEffectFlag());
    }

    public String getOtherCcy() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());
    }

    public String getOtherPs() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, super.getPs());
    }

    public BigDecimal getShareNumWanYuan() {
        return ArithmeticUtil.divide(this.shareNum, new BigDecimal(10000), 6, RoundingMode.HALF_UP);
    }

    public String getOtherSettleSpeed() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, this.getSettleSpeed());
    }

    public List<CfetsPledgeBondInfoDto> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<CfetsPledgeBondInfoDto> detailList) {
        this.detailList = detailList;
    }

    public String getPledgeStockName() {
        return pledgeStockName;
    }

    public void setPledgeStockName(String pledgeStockName) {
        this.pledgeStockName = pledgeStockName;
    }

    public BigDecimal getvTradeAmt() {
        return vTradeAmt;
    }

    public void setvTradeAmt(BigDecimal vTradeAmt) {
        this.vTradeAmt = vTradeAmt;
    }

    public BigDecimal getfCprice() {
        return fCprice;
    }

    public void setfCprice(BigDecimal fCprice) {
        this.fCprice = fCprice;
    }

    public BigDecimal getfYield() {
        return fYield;
    }

    public void setfYield(BigDecimal fYield) {
        this.fYield = fYield;
    }

    public String getChlId() {
        return chlId;
    }

    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    public String getChlIdName() {
        return chlIdName;
    }

    public void setChlIdName(String chlIdName) {
        this.chlIdName = chlIdName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getPledgeFinprodId() {
        return pledgeFinprodId;
    }

    public void setPledgeFinprodId(String pledgeFinprodId) {
        this.pledgeFinprodId = pledgeFinprodId;
    }

    public String getPledgeFinprodName() {
        return pledgeFinprodName;
    }

    public void setPledgeFinprodName(String pledgeFinprodName) {
        this.pledgeFinprodName = pledgeFinprodName;
    }

    public String getPaBussType() {
        return SysDataUtil.getNameFromDataDict(DictCode.拆借类型, paBussType);
    }

    public void setPaBussType(String paBussType) {
        this.paBussType = paBussType;
    }

    public String getAssetType1() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产二级分类, assetType1);
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public String getAssetType10() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产三级分类, assetType10);
    }

    public void setAssetType10(String assetType10) {
        this.assetType10 = assetType10;
    }

    public String getAssetType11() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, assetType11);
    }

    public void setAssetType11(String assetType11) {
        this.assetType11 = assetType11;
    }

    @Override
    public String getTradeMarket() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, tradeMarket);
    }

    @Override
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    /**
     * 获取 otherTradeMarket
     *
     * @return otherTradeMarket.
     */
    public String getOtherTradeMarket() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, tradeMarket);
    }

    public String getPurCfmDate() {
        return purCfmDate;
    }

    public void setPurCfmDate(String purCfmDate) {
        this.purCfmDate = purCfmDate;
    }

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    /**
     * 获取资产类型 otherFinprodType2
     *
     * @return otherFinprodType2 资产类型对应name
     */
    public String getOtherFinprodType2() {
        if (StringUtil.equals(finprodType2, BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue())) {
            return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, BussinessDictCodeList.平安资产类型_一级投标债券.getValue());
        }
        return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, finprodType2);
    }

    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    public BigDecimal getActPayAmt() {
        return actPayAmt;
    }

    public void setActPayAmt(BigDecimal actPayAmt) {
        this.actPayAmt = actPayAmt;
    }

    public String getRecommender() {
        return recommender;
    }

    public void setRecommender(String recommender) {
        this.recommender = recommender;
    }

    public String getRecommenderName() {
        return recommenderName;
    }

    public void setRecommenderName(String recommenderName) {
        this.recommenderName = recommenderName;
    }

    @ApiModelProperty(value = "指令编号")
    private String instructionId;

    @ApiModelProperty(value = "委托编号")
    private String brokerEntrustId;

    @ApiModelProperty(value = "成交编号")
    private String brokerTradeId;

    @ApiModelProperty(name = "portfolioId", value = "组合代码")
    private String portfolioId;

    @ApiModelProperty(name = "portfolioName", value = "组合名称")
    private String portfolioName;

    @ApiModelProperty(value = "资金账户")
    private String financeAccount;

    @ApiModelProperty(value = "交易市场")
    private String market;

    @ApiModelProperty(value = "交易市场")
    private String marketStr;

    @ApiModelProperty(name = "tradePlat", value = "交易平台")
    private String tradePlat;

    @ApiModelProperty(name = "tradePlatStr", value = "交易平台")
    private String tradePlatStr;

    @ApiModelProperty(value = "资产代码")
    private String stockCode;

    @ApiModelProperty(value = "资产名称")
    private String stockName;

    @ApiModelProperty(name = "finprodType2Str", value = "资产类型")
    private String finprodType2Str;

    @ApiModelProperty(value = "委托方向")
    private String entrustBs;

    @ApiModelProperty(value = "委托方向")
    private String entrustBsStr;

    @ApiModelProperty(value = "成交数量")
    private BigDecimal tradedVolume;

    
    @ApiModelProperty(value = "成交价格")
    private BigDecimal tradedPrice;

    
    @ApiModelProperty(value = "成交金额")
    private BigDecimal tradedBalance;

    @ApiModelProperty(value = "成交时间")
    private Date createTime;

    @ApiModelProperty(value = "交易日期",hidden = true)
    private String tradingDay;

    @ApiModelProperty(value = "委托属性", hidden = true)
    private String entrustProp;

    @ApiModelProperty(value = "委托方向标识", hidden = true)
    private String bsFlag;

    @ApiModelProperty(value = "主键ID", hidden = true)
    private String id;

    @ApiModelProperty(value = "定位串", hidden = true)
    private String positionStr;

    @ApiModelProperty(value = "委托编号", hidden = true)
    private String entrustId;

    @ApiModelProperty(value = "订单编号", hidden = true)
    private String parentOrderId;

    @ApiModelProperty(value = "网点号:资金账户编号", hidden = true)
    private String accountId;

    @ApiModelProperty(value = "资金账户名称", hidden = true)
    private String accountName;

    @ApiModelProperty(value = "资产类型", hidden = true)
    private String assetType;

    @ApiModelProperty(value = "业务标识：5 公平；6 批量", hidden = true)
    private String businessFlag;

    @ApiModelProperty(value = "委托类别", hidden = true)
    private String entrustType;

    @ApiModelProperty(value = "成交类别", hidden = true)
    private int tradeType;
 
    @ApiModelProperty(value = "全价价格", hidden = true)
    private BigDecimal fullPrice;
 
    @ApiModelProperty(value = "清算金额", hidden = true)
    private BigDecimal clearBalance;

    @ApiModelProperty(value = "成交时间", hidden = true)
    private String tradeTime;
 
    @ApiModelProperty(value = "质押券折算比例", hidden = true)
    private BigDecimal discount;

    @ApiModelProperty(value = "到期续作类型：0 非第三方；1 第三方", hidden = true)
    private String transCode;

    @ApiModelProperty(value = "对手方交易商编号", hidden = true)
    private String propSeatNo;

    @ApiModelProperty(value = "本地委托编号", hidden = true)
    private String localEntrustId;

    @ApiModelProperty(value = "更新时间", hidden = true)
    private Date updateTime;

    @ApiModelProperty(value = "记录数", hidden = true)
    public int count;

    @ApiModelProperty(value = "报文编号", hidden = true)
    private String reportNo;

    @ApiModelProperty(value = "无撤销委托标识", hidden = true)
    private boolean noCancelEntrust;

    @ApiModelProperty(value = "冲正标识", hidden = true)
    private String offsetFlag;

    @ApiModelProperty(value = "套期保值标识", hidden = true)
    private String hedgeFlag;

    @ApiModelProperty(value = "错误信息", hidden = true)
    private String errorMsg;

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId;
    }

    public String getBrokerEntrustId() {
        return brokerEntrustId;
    }

    public void setBrokerEntrustId(String brokerEntrustId) {
        this.brokerEntrustId = brokerEntrustId;
    }

    public String getBrokerTradeId() {
        return brokerTradeId;
    }

    public void setBrokerTradeId(String brokerTradeId) {
        this.brokerTradeId = brokerTradeId;
    }

    @Override
    public String getPortfolioId() {
        return portfolioId;
    }

    @Override
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    @Override
    public String getPortfolioName() {
        return portfolioName;
    }

    @Override
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getFinanceAccount() {
        return financeAccount;
    }

    public void setFinanceAccount(String financeAccount) {
        this.financeAccount = financeAccount;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getMarketStr() {
        if(StringUtil.isEmpty(market)){
            return null;
        }
        return Market.getMarketName(market);
    }

    public void setMarketStr(String marketStr) {
        this.marketStr = marketStr;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getTradePlatStr() {
        if(StringUtil.isEmpty(tradePlat)){
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.交易平台, tradePlat);
    }

    public void setTradePlatStr(String tradePlatStr) {
        this.tradePlatStr = tradePlatStr;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getFinprodType2Str() {
        return finprodType2Str;
    }

    public void setFinprodType2Str(String finprodType2Str) {
        this.finprodType2Str = finprodType2Str;
    }

    public String getEntrustBs() {
        return entrustBs;
    }

    public void setEntrustBs(String entrustBs) {
        this.entrustBs = entrustBs;
    }

    public String getEntrustBsStr() {
        if(StringUtil.isEmpty(entrustBs)){
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, entrustBs);
    }

    public void setEntrustBsStr(String entrustBsStr) {
        this.entrustBsStr = entrustBsStr;
    }

    public BigDecimal getTradedVolume() {
        return tradedVolume;
    }

    public void setTradedVolume(BigDecimal tradedVolume) {
        this.tradedVolume = tradedVolume;
    }

    public BigDecimal getTradedPrice() {
        if (StringUtil.isNotEmpty(tradedPrice)){
            return tradedPrice;
        }
        return new BigDecimal("0.00");
    }

    public void setTradedPrice(BigDecimal tradedPrice) {
        this.tradedPrice = tradedPrice;
    }

    public BigDecimal getTradedBalance() {
        if (StringUtil.isNotEmpty(tradedBalance)){
            return tradedBalance;
        }
        return new BigDecimal("0.00");
    }

    public void setTradedBalance(BigDecimal tradedBalance) {
        this.tradedBalance = tradedBalance;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(String tradingDay) {
        this.tradingDay = tradingDay;
    }

    public String getEntrustProp() {
        return entrustProp;
    }

    public void setEntrustProp(String entrustProp) {
        this.entrustProp = entrustProp;
    }

    public String getBsFlag() {
        return bsFlag;
    }

    public void setBsFlag(String bsFlag) {
        this.bsFlag = bsFlag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPositionStr() {
        return positionStr;
    }

    public void setPositionStr(String positionStr) {
        this.positionStr = positionStr;
    }

    @Override
    public String getEntrustId() {
        return entrustId;
    }

    @Override
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getBusinessFlag() {
        return businessFlag;
    }

    public void setBusinessFlag(String businessFlag) {
        this.businessFlag = businessFlag;
    }

    public String getEntrustType() {
        return entrustType;
    }

    public void setEntrustType(String entrustType) {
        this.entrustType = entrustType;
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    public BigDecimal getFullPrice() {
        if (StringUtil.isNotEmpty(fullPrice)){
            return fullPrice;
        }
        return new BigDecimal("0.00");
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    public BigDecimal getClearBalance() {
        if (StringUtil.isNotEmpty(clearBalance)){
            return clearBalance;
        }
        return new BigDecimal("0.00");
    }

    public void setClearBalance(BigDecimal clearBalance) {
        this.clearBalance = clearBalance;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public BigDecimal getDiscount() {
        if (StringUtil.isNotEmpty(discount)){
            return discount;
        }
        return new BigDecimal("0.00");
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getPropSeatNo() {
        return propSeatNo;
    }

    public void setPropSeatNo(String propSeatNo) {
        this.propSeatNo = propSeatNo;
    }

    public String getLocalEntrustId() {
        return localEntrustId;
    }

    public void setLocalEntrustId(String localEntrustId) {
        this.localEntrustId = localEntrustId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public boolean isNoCancelEntrust() {
        return noCancelEntrust;
    }

    public void setNoCancelEntrust(boolean noCancelEntrust) {
        this.noCancelEntrust = noCancelEntrust;
    }

    public String getOffsetFlag() {
        return offsetFlag;
    }

    public void setOffsetFlag(String offsetFlag) {
        this.offsetFlag = offsetFlag;
    }

    public String getHedgeFlag() {
        return hedgeFlag;
    }

    public void setHedgeFlag(String hedgeFlag) {
        this.hedgeFlag = hedgeFlag;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getRegistOrgStr() {
        return registOrgStr;
    }

    public void setRegistOrgStr(String registOrgStr) {
        this.registOrgStr = registOrgStr;
    }

    /******* 一级投标 start********/

    @ApiModelProperty(name = "firstBidStatus", value = "中标状态", example = "A1")
    private String firstBidStatus;

    @ApiModelProperty(name = "firstBidStatusStr", value = "中标状态名称", example = "A1")
    private String firstBidStatusStr;

    @ApiModelProperty(name = "bidId", value = "投标编号", example = "111")
    private String bidId;

    @ApiModelProperty(name = "tenderTarget", value = "招标标的")
    private String tenderTarget;

    @ApiModelProperty(name = "tenderTargetStr", value = "招标标的字典值")
    private String tenderTargetStr;

    @ApiModelProperty(required = false, name = "issueDate", value = "投标日")
    private Date issueDate;

    @ApiModelProperty(name = "issueDateStart", value = "投标日开始日期,字符串", example = "2021-12-21")
    private Date issueDateStart;

    @ApiModelProperty(name = "issueDateEnd", value = "投标日结束日期,字符串", example = "2021-12-21")
    private Date issueDateEnd;

    @ApiModelProperty(required = false, name = "issueDateStr", value = "投标日格式化")
    private String issueDateStr;

    @ApiModelProperty(name = "etrBidPriceTotal", value = "指令投标券面总额(万元) 需前端转化", example = "1")
    private BigDecimal etrBidPriceTotal;

    @ApiModelProperty(name = "totalBiddingNum", value = "汇总投标券面总额")
    private BigDecimal totalBiddingNum;

    @ApiModelProperty(name="winPrinAmt",value="中标券面总额（万元）")
    private BigDecimal winPrinAmt;

    @ApiModelProperty(name = "bidPrinAmt", value = "投标券面总额", example = "1")
    private BigDecimal bidPrinAmt;

    @ApiModelProperty(name = "bidPrice", value = "投标标位", example = "1")
    private String bidPrice;

    @ApiModelProperty(name = "winPrice", value = "中标标位", example = "100.0000")
    private String winPrice;

    @ApiModelProperty(name = "winType", value = "中标方式")
    private String winType;

    @ApiModelProperty(name = "winTypeStr", value = "中标方式字符串")
    private String winTypeStr;

    @ApiModelProperty(name = "price", value = "净价", example = "1")
    private BigDecimal price;

    @ApiModelProperty(name = "payableAmount", value = "缴款金额", example = "1000.00")
    private BigDecimal payableAmount;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费", example = "1000.00")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "payDate", value = "缴款日")
    private Date payDate;

    @ApiModelProperty(name = "payDateStr", value = "缴款日格式化,展示用")
    private String payDateStr;

    @ApiModelProperty(name = "assignRegdate", value = "债权债务登记日")
    private Date assignRegdate;

    @ApiModelProperty(name = "assignRegdateStr", value = "债权债务登记日格式化,展示用")
    private String assignRegdateStr;

    @ApiModelProperty(name = "listingDate", value = "上市日")
    private Date listingDate;

    @ApiModelProperty(name = "listingDateStr", value = "上市日日格式化,展示用", example = "2021-01-01")
    private String listingDateStr;

    @ApiModelProperty(name = "biddingMethod", value = "投标方式", example = "01")
    private String biddingMethod;

    @ApiModelProperty(name = "biddingMethodStr", value = "投标方式名称,展示用", example = "自主投标")
    private String biddingMethodStr;

    @ApiModelProperty(name = "payType", value = "缴款方式", example = "01")
    private String payType;

    @ApiModelProperty(name = "payTypeStr", value = "缴款方式名称,展示用", example = "一级分销")
    private String payTypeStr;

    @ApiModelProperty(name = "bidConfirmDate", value = "投标确认日")
    private Date bidConfirmDate;

    @ApiModelProperty(required = false, name = "bidConfirmDateStr", value = "投标确认日格式化")
    private String bidConfirmDateStr;

    @ApiModelProperty(name="tradeMarketMode",value="交易市场",example="ZC1")
    private String tradeMarketMode;

    @ApiModelProperty(name="tradeMarketModeStr",value="交易市场",example="ZC1")
    private String tradeMarketModeStr;

    @ApiModelProperty(name = "secAcctName", value = "证券账户名称,展示用")
    private String secAcctName;

    @ApiModelProperty(name = "secType", value = "债券分类")
    private String secType;

    @ApiModelProperty(name = "secTypeStr", value = "债券分类名称,展示用")
    private String secTypeStr;

    @ApiModelProperty(name = "subscriptionAmount", value = "认购量,需转化为万元", example = "1000.00")
    private BigDecimal subscriptionAmount;

    @ApiModelProperty(name = "issuer", value = "发行人id", example = "001")
    private String issuer;

    @ApiModelProperty(name = "issuerName", value = "发行人名称,展示用", example = "发行人1")
    private String issuerName;

    @ApiModelProperty(name = "ouPartyId", value = "本方id", example = "114912384")
    private String ouPartyId;

    @ApiModelProperty(name = "ouPartyIdAbbr", value = "本方简称", example = "广发银行")
    private String ouPartyIdAbbr;

    @ApiModelProperty(name = "ourTrader", value = "本方交易员id(申购人)", example = "001")
    private String ourTrader;

    @ApiModelProperty(name = "ourTraderName", value = "本方交易员名称", example = "交易员1")
    private String ourTraderName;

    @ApiModelProperty(required = false, name = "finprodId", value = "资产代码,系统内代码一般不用于显示")
    private String finprodId;

    @ApiModelProperty(required = false, name = "finprodMarketId", value = "市场代码,显示用代码")
    private String finprodMarketId;

    @ApiModelProperty(required = false, name = "finprodAbbr", value = "资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name = "busiCode", value = "业务编号,调用交易查询用", example = "A1")
    private String busiCode;

    @ApiModelProperty(name="tgBusiType",value="业务类型,调用交易查询用",example="A1")
    private String tgBusiType;

    @ApiModelProperty(name="bidModle",value="投标方式,调用交易查询用")
    private String bidModle;

    public String getFirstBidStatus() {
        return firstBidStatus;
    }

    public void setFirstBidStatus(String firstBidStatus) {
        this.firstBidStatus = firstBidStatus;
    }

    public String getFirstBidStatusStr() {
        if (StringUtil.isEmpty(getFirstBidStatus())) {
            return "--";
        }
        return SysDataUtil.getNameFromDataDict(DictCode.一级投标中标状态, getFirstBidStatus());
    }

    public void setFirstBidStatusStr(String firstBidStatusStr) {
        this.firstBidStatusStr = firstBidStatusStr;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }
    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public String getTenderTargetStr() {
        if (StringUtil.isEmpty(getTenderTarget())) {
            return "";
        }
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, getTenderTarget());
    }

    public void setTenderTargetStr(String tenderTargetStr) {
        this.tenderTargetStr = tenderTargetStr;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getIssueDateStart() {
        return issueDateStart;
    }

    public void setIssueDateStart(Date issueDateStart) {
        this.issueDateStart = issueDateStart;
    }

    public Date getIssueDateEnd() {
        return issueDateEnd;
    }

    public void setIssueDateEnd(Date issueDateEnd) {
        this.issueDateEnd = issueDateEnd;
    }

    public String getIssueDateStr() {
        if (null == this.getIssueDateEnd()) {
            return DateUtil.format(this.getIssueDateStart());
        }
        if (null != this.getIssueDateStart() && null != this.getIssueDateEnd()) {
            return DateUtil.format(this.getIssueDateStart()) + "~" + DateUtil.format(this.getIssueDateEnd());
        }
        return issueDateStr;
    }

    public void setIssueDateStr(String issueDateStr) {
        this.issueDateStr = issueDateStr;
    }

    public BigDecimal getEtrBidPriceTotal() {
        return etrBidPriceTotal;
    }

    public void setEtrBidPriceTotal(BigDecimal etrBidPriceTotal) {
        this.etrBidPriceTotal = etrBidPriceTotal;
    }

    public BigDecimal getTotalBiddingNum() {
        return totalBiddingNum;
    }

    public void setTotalBiddingNum(BigDecimal totalBiddingNum) {
        this.totalBiddingNum = totalBiddingNum;
    }

    public BigDecimal getBidPrinAmt() {
        return bidPrinAmt;
    }

    public void setBidPrinAmt(BigDecimal bidPrinAmt) {
        this.bidPrinAmt = bidPrinAmt;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(String winPrice) {
        this.winPrice = winPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getPayDateStr() {

        if (null != this.getPayDate()) {
            return DateUtil.format(this.getPayDate());
        }
        return payDateStr;
    }

    public void setPayDateStr(String payDateStr) {
        this.payDateStr = payDateStr;
    }

    public Date getAssignRegdate() {
        return assignRegdate;
    }

    public void setAssignRegdate(Date assignRegdate) {
        this.assignRegdate = assignRegdate;
    }

    public String getAssignRegdateStr() {
        if (null != this.getAssignRegdate()) {
            return DateUtil.format(this.getAssignRegdate());
        }
        return assignRegdateStr;
    }

    public void setAssignRegdateStr(String assignRegdateStr) {
        this.assignRegdateStr = assignRegdateStr;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public String getListingDateStr() {
        if (null != this.getListingDate()) {
            return DateUtil.format(this.getListingDate());
        }
        return listingDateStr;
    }

    public void setListingDateStr(String listingDateStr) {
        this.listingDateStr = listingDateStr;
    }

    public String getBiddingMethod() {
        return biddingMethod;
    }

    public void setBiddingMethod(String biddingMethod) {
        this.biddingMethod = biddingMethod;
    }

    public String getBiddingMethodStr() {
        if (StringUtil.isEmpty(getBiddingMethod())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.投标方式, getBiddingMethod());
    }

    public void setBiddingMethodStr(String biddingMethodStr) {
        this.biddingMethodStr = biddingMethodStr;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayTypeStr() {
        if (StringUtil.isEmpty(getPayType())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.缴款方式, getPayType());
    }

    public void setPayTypeStr(String payTypeStr) {
        this.payTypeStr = payTypeStr;
    }

    public Date getBidConfirmDate() {
        return bidConfirmDate;
    }

    public void setBidConfirmDate(Date bidConfirmDate) {
        this.bidConfirmDate = bidConfirmDate;
    }

    public String getBidConfirmDateStr() {
        if (null != this.getBidConfirmDate()) {
            return DateUtil.format(this.getBidConfirmDate());
        }
        return bidConfirmDateStr;
    }

    public void setBidConfirmDateStr(String bidConfirmDateStr) {
        this.bidConfirmDateStr = bidConfirmDateStr;
    }

    public String getWinType() {
        return winType;
    }

    public void setWinType(String winType) {
        this.winType = winType;
    }

    public String getWinTypeStr() {
        return winTypeStr;
    }

    public void setWinTypeStr(String winTypeStr) {
        this.winTypeStr = winTypeStr;
    }

    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode;
    }

    public String getTradeMarketModeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易市场, getTenderTarget());
    }

    public void setTradeMarketModeStr(String tradeMarketModeStr) {
        this.tradeMarketModeStr = tradeMarketModeStr;
    }
    public String getSecAcctName() {
        return secAcctName;
    }

    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
    }

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public String getSecTypeStr() {
        if (StringUtil.isEmpty(getSecType())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, getSecType());
    }

    public void setSecTypeStr(String secTypeStr) {
        this.secTypeStr = secTypeStr;
    }

    public BigDecimal getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public void setSubscriptionAmount(BigDecimal subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getOuPartyId() {
        return ouPartyId;
    }

    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId;
    }

    public String getOuPartyIdAbbr() {
        return ouPartyIdAbbr;
    }

    public void setOuPartyIdAbbr(String ouPartyIdAbbr) {
        this.ouPartyIdAbbr = ouPartyIdAbbr;
    }

    public String getOurTrader() {
        return ourTrader;
    }

    public void setOurTrader(String ourTrader) {
        this.ourTrader = ourTrader;
    }

    public String getOurTraderName() {
        return ourTraderName;
    }

    public void setOurTraderName(String ourTraderName) {
        this.ourTraderName = ourTraderName;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    @Override
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    @Override
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    @Override
    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    @Override
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public BigDecimal getWinPrinAmt() {
        return winPrinAmt;
    }

    public void setWinPrinAmt(BigDecimal winPrinAmt) {
        this.winPrinAmt = winPrinAmt;
    }

    public String getDeliveryResult() {
        return deliveryResult;
    }

    public void setDeliveryResult(String deliveryResult) {
        this.deliveryResult = deliveryResult;
    }

    public String getDeliverySchedule() {
        return deliverySchedule;
    }

    public void setDeliverySchedule(String deliverySchedule) {
        this.deliverySchedule = deliverySchedule;
    }

    @Override
    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    @Override
    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public String getTgBusiType() {
        return tgBusiType;
    }

    public void setTgBusiType(String tgBusiType) {
        this.tgBusiType = tgBusiType;
    }

    public String getBidModle() {
        return bidModle;
    }

    public void setBidModle(String bidModle) {
        this.bidModle = bidModle;
    }
}
