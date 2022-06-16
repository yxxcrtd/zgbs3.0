package com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrdispatcher.dto;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.EtrEntrustBidPrice;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 场外指令分发
 * 指令明细返回数据
 * @author sjj
 * @version 1.0 2021年10月26日
 * @see FieldEtrDispatcherTradeListResponse
 * @since 1.0
 */
@ApiModel(value="指令明细返回数据",description="指令明细数据")
public class FieldEtrDispatcherInfoResponse extends FieldEtrDispatcherBaseInfo {

    @ApiModelProperty(name="busiTypePositionStr",value="业务类型",example="债券一级投标/现券买卖CODE",required = true)
    private String busiTypePositionStr;

    //债券一级投标 start
    @ApiModelProperty(name="mainConsignee",value="主承销商",example="字符串1")
    private String mainConsignee;

    @ApiModelProperty(name="paymentDate",value="缴款日",example="date'2021-01-01'")
    private Date paymentDate;

    @ApiModelProperty(name="listingDate",value="上市日",example="date'2021-01-01'")
    private Date listingDate;

    @ApiModelProperty(name="entrustNum",value="投标券面总额汇总",example="字符串1")
    private BigDecimal entrustNum;

    @ApiModelProperty(name="etrBidDate",value="投标日期",example="2021-01-01")
    private String etrBidDate;

    @ApiModelProperty(name="bidPriceList",value="投标标位",example="List<EtrEntrustBidPrice>")
    private List<EtrEntrustBidPrice> bidPriceList;

    @ApiModelProperty(name="biddingProductDtos",value="投标产品列表")
    private List<FieldEtrBiddingProductDto> biddingProductDtos;

    //债券一级投标 end

    //债券交易 start
    @ApiModelProperty(name="settleSpeed",value="清算速度CODE",example="T+0")
    private String settleSpeed;

    @ApiModelProperty(name="otherSettleSpeed",value="清算速度",example="字符串1")
    private String otherSettleSpeed;

    @ApiModelProperty(name="priceModel",value="价格模式Code",example="01")
    private String priceModel;

    @ApiModelProperty(name="otherPriceModel",value="价格模式",example="A1")
    private String otherPriceModel;

    @ApiModelProperty(name="secAcctId",value="证券账户",example="XXX证券账户")
    private String secAcctId;

    @ApiModelProperty(name="shareNum",value="券面总额",example="1000000")
    private BigDecimal shareNum;

    @ApiModelProperty(name="entrustOwnNum",value="本方券券面总额",example="1000000")
    private BigDecimal entrustOwnNum;

    @ApiModelProperty(name="integrateSecAmount",value="融入券券面总额",example="1000000")
    private BigDecimal integrateSecAmount;

    @ApiModelProperty(name="unitPrice",value="净价",example="100")
    private BigDecimal unitPrice;

    @ApiModelProperty(name="allPrice",value="全价",example="100")
    private BigDecimal allPrice;

    @ApiModelProperty(name="matureYield",value="到期收益率",example="1.11")
    private BigDecimal matureYield;

    @ApiModelProperty(name="exerYield",value="行权收益率",example="1.11")
    private BigDecimal exerYield;

    @ApiModelProperty(name="fpriceAmt",value="全价总额",example="1000000")
    private BigDecimal fpriceAmt;

    @ApiModelProperty(name="remark",value="备注",example="A1")
    private String remark;
    //债券交易 end

    private String addCla;

    @ApiModelProperty(name="pledgeBondList",value="质押券信息",example="pledgeBondList")
    private List<PledgeBondInfoDto> pledgeBondList;

    //回购 start
    @ApiModelProperty(name="repoTerm",value="回购期限",example="A1")
    private String repoTerm;

    @ApiModelProperty(name="pledgeStockName",value="质押券库",example="A1")
    private String pledgeStockName;

    @ApiModelProperty(name="firstprice",value="首次净价",example="A1")
    private String firstprice;

    @ApiModelProperty(name="totalPrinAmt",value="汇总券面总额",example="10000")
    private BigDecimal totalPrinAmt;

    @ApiModelProperty(name="pledgeStandardNumber",value="标准券数量",example="10000")
    private BigDecimal pledgeStandardNumber;

    @ApiModelProperty(name="pledgeRatio",value="折算率",example="10000")
    private BigDecimal pledgeRatio;

    @ApiModelProperty(name="pledgeNumber",value="质押数量",example="10000")
    private BigDecimal pledgeNumber;

    @ApiModelProperty(name="mIntAmt",value="回购利息",example="10000")
    private BigDecimal mIntAmt;

    @ApiModelProperty(name="mTradeAmt",value="到期结算金额",example="10000")
    private BigDecimal mTradeAmt;

    @ApiModelProperty(name="vpayDate",value="首期结算日",example="2021-01-01")
    private String vpayDate;

    @ApiModelProperty(name="vpayDate",value="到期结算日",example="2021-01-01")
    private String mpayDate;

    @ApiModelProperty(name="pledStrdPrinAmt",value="券面金额",example="10000")
    private BigDecimal pledStrdPrinAmt;

    @ApiModelProperty(name="tradeId",value="初始交易编号",example="字符串1")
    private String tradeId;

    @ApiModelProperty(name="oldPledgeRatio",value="原折算率",example="10000")
    private BigDecimal oldPledgeRatio;

    @ApiModelProperty(name="oldPledgeNumber",value="原质押数量",example="10000")
    private BigDecimal oldPledgeNumber;

    @ApiModelProperty(name="oldPledStrdPrinAmt",value="原券面金额",example="10000")
    private BigDecimal oldPledStrdPrinAmt;

    @ApiModelProperty(name="counterName",value="交易对手名称")
    private String counterName;

    @ApiModelProperty(name="issueClass",value="发行方式")
    private String issueClass;

    @ApiModelProperty(name="issueClassStr",value="发行方式字符串")
    private String issueClassStr;

    @ApiModelProperty(name="tenderTarget",value="招标标的")
    private String tenderTarget;

    @ApiModelProperty(name="tenderTargetStr",value="招标标的字符串")
    private String tenderTargetStr;

    @ApiModelProperty(name="issueDate",value="发行日")
    private Date issueDate;

    @ApiModelProperty(name="tenderSdate",value="投标起始日")
    private Date tenderSdate;

    @ApiModelProperty(name="tenderEdate",value="投标截止日")
    private Date tenderEdate;

    @ApiModelProperty(name="payEdate",value="缴款截止日")
    private Date payEdate;

    @ApiModelProperty(name="payEdateStr",value="缴款截止日格式化")
    private String payEdateStr;

    @ApiModelProperty(name="issueDateStr",value="发行日格式化")
    private String issueDateStr;

    private String declaType;

    private String declaTypeStr;

    private String finprodId;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public BigDecimal getPledgeStandardNumber() {
        return pledgeStandardNumber;
    }

    public void setPledgeStandardNumber(BigDecimal pledgeStandardNumber) {
        this.pledgeStandardNumber = pledgeStandardNumber;
    }

    public BigDecimal getOldPledgeRatio() {
        return oldPledgeRatio;
    }

    public void setOldPledgeRatio(BigDecimal oldPledgeRatio) {
        this.oldPledgeRatio = oldPledgeRatio;
    }

    public BigDecimal getOldPledgeNumber() {
        return oldPledgeNumber;
    }

    public void setOldPledgeNumber(BigDecimal oldPledgeNumber) {
        this.oldPledgeNumber = oldPledgeNumber;
    }

    public BigDecimal getOldPledStrdPrinAmt() {
        return oldPledStrdPrinAmt;
    }

    public void setOldPledStrdPrinAmt(BigDecimal oldPledStrdPrinAmt) {
        this.oldPledStrdPrinAmt = oldPledStrdPrinAmt;
    }

    public String getOldFinprodMarketId() {
        return oldFinprodMarketId;
    }

    public void setOldFinprodMarketId(String oldFinprodMarketId) {
        this.oldFinprodMarketId = oldFinprodMarketId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public BigDecimal getPledStrdPrinAmt() {
        return pledStrdPrinAmt;
    }

    public void setPledStrdPrinAmt(BigDecimal pledStrdPrinAmt) {
        this.pledStrdPrinAmt = pledStrdPrinAmt;
    }

    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
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

    public BigDecimal getPledgeRatio() {
        return pledgeRatio;
    }

    public void setPledgeRatio(BigDecimal pledgeRatio) {
        this.pledgeRatio = pledgeRatio;
    }

    @ApiModelProperty(name="repoPledgeModel",value="场内质押式回购质押券模式",example="01无质押券库，02单个质押库，03多个质押券库")
    private String repoPledgeModel;
    //回购 end

    //利率互换 start
    @ApiModelProperty(name="busiPs",value="互换方式",example="收取固定/收取浮动")
    private String busiPs;

    @ApiModelProperty(name="entrustAmt",value="名义本金",example="1000")
    private BigDecimal entrustAmt;

    @ApiModelProperty(name="dealMode",value="挂钩利率",example="个人定期(整存整取)一年")
    private String dealMode;

    private String otherPs;

    @ApiModelProperty(name="termType",value="期限",example="1Y/1M")
    private String termType;

    @ApiModelProperty(name="dueDate",value="到期日",example="[2017-01-01 - 2017-02-01]")
    private String dueDate;
    //利率互换 end

    @ApiModelProperty(name="isRight",value="是否含权",example="A1")
    private String isRight;

    @ApiModelProperty(name="secType",value="债券分类",example="字符串1")
    private String secType;

    @ApiModelProperty(name="oldFinprodMarketId",value="原质押券",example="字符串1")
    private String oldFinprodMarketId;

    public String getSecType() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, this.secType);
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public String getBusiPs() {
        return busiPs;
    }

    public void setBusiPs(String busiPs) {
        this.busiPs = busiPs;
    }

    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    public String getDealMode() {
        return dealMode;
    }

    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getMainConsignee() {
        return mainConsignee;
    }

    public void setMainConsignee(String mainConsignee) {
        this.mainConsignee = mainConsignee;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    public List<EtrEntrustBidPrice> getBidPriceList() {
        return bidPriceList;
    }

    public void setBidPriceList(List<EtrEntrustBidPrice> bidPriceList) {
        this.bidPriceList = bidPriceList;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public BigDecimal getShareNum() {
        return shareNum;
    }

    public void setShareNum(BigDecimal shareNum) {
        this.shareNum = shareNum;
    }

    public BigDecimal getEntrustOwnNum() {
        return entrustOwnNum;
    }

    public void setEntrustOwnNum(BigDecimal entrustOwnNum) {
        this.entrustOwnNum = entrustOwnNum;
    }

    public BigDecimal getIntegrateSecAmount() {
        return integrateSecAmount;
    }

    public void setIntegrateSecAmount(BigDecimal integrateSecAmount) {
        this.integrateSecAmount = integrateSecAmount;
    }

    public BigDecimal getUnitPrice() {
        if(StringUtils.equals(DictCodeList.平安资产类型_基金.getValue(),this.getAssetType())
                && StringUtils.equals("PRM03",this.getPriceModel())){
            return BigDecimal.valueOf(0.00);
        }
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

    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAddCla() {
        return addCla;
    }

    public void setAddCla(String addCla) {
        this.addCla = addCla;
    }

    public String getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(String repoTerm) {
        this.repoTerm = repoTerm;
    }

    public String getFirstprice() {
        return firstprice;
    }

    public void setFirstprice(String firstprice) {
        this.firstprice = firstprice;
    }

    public BigDecimal getTotalPrinAmt() {
        return totalPrinAmt;
    }

    public void setTotalPrinAmt(BigDecimal totalPrinAmt) {
        this.totalPrinAmt = totalPrinAmt;
    }

    public List<PledgeBondInfoDto> getPledgeBondList() {
        return pledgeBondList;
    }

    public void setPledgeBondList(List<PledgeBondInfoDto> pledgeBondList) {
        this.pledgeBondList = pledgeBondList;
    }

    public String getPledgeStockName() {
        return pledgeStockName;
    }

    public void setPledgeStockName(String pledgeStockName) {
        this.pledgeStockName = pledgeStockName;
    }

    public String getRepoPledgeModel() {
        return repoPledgeModel;
    }

    public void setRepoPledgeModel(String repoPledgeModel) {
        this.repoPledgeModel = repoPledgeModel;
    }

    public String getOtherBusiModel() {
//        String flag = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        if("02".equals(flag)){
            return SysDataUtil.getNameFromDataDict(DictCode.业务模式, super.getBusiModel());
//        }
//        return SysDataUtil.getNameFromDataDict(DictCode.投资目的, super.getBusiModel());
    }

    /**
     * 获取 otherTradeMarket
     * @return otherTradeMarket.
     */
    public String getOtherTradeMarket() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }

    public String getOtherCcy() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());
    }

    public String getOtherPs() {
        if(StringUtils.equals(DictCodeListExt3.交易方向_提前购回.getValue(),this.getPs())
        ||StringUtils.equals(DictCodeListExt3.交易方向_到期购回.getValue(),this.getPs())
                ||StringUtils.equals(DictCodeListExt3.交易方向_中途换券.getValue(),this.getPs())
                ||StringUtils.equals(DictCodeListExt3.交易方向_解除质押.getValue(),this.getPs())
                ||StringUtils.equals(DictCodeListExt3.交易方向_到期续做.getValue(),this.getPs())
        ){
            if(StringUtils.equals(DictCodeList.回购拆借方向_正回购.getValue(),this.getBusiPs())){
                return DictCodeList.回购拆借方向_正回购.getCodeName();
            }
            if(StringUtils.equals(DictCodeList.回购拆借方向_逆回购.getValue(),this.getBusiPs())){
                return DictCodeList.回购拆借方向_逆回购.getCodeName();
            }
        }
        return this.otherPs;
    }

    public void setOtherPs(String otherPs) {
        this.otherPs = otherPs;
    }

    public String getEtrBidDate() {
        return etrBidDate;
    }

    public void setEtrBidDate(String etrBidDate) {
        this.etrBidDate = etrBidDate;
    }

    public String getOtherPriceModel() {
        return SysDataUtil.getNameFromDataDict(DictCode.价格模式, this.getPriceModel());
    }

    public String getOtherSettleSpeed() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, this.getSettleSpeed());
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public String getBusiTypePositionStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.投管业务类型, this.getBusiTypePosition());
    }

    public void setBusiTypePositionStr(String busiTypePositionStr) {
        this.busiTypePositionStr = busiTypePositionStr;
    }

    public String getOtherAssetType() {
        return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, super.getAssetType());
    }

    public String getOtherTargetType() {
        return SysDataUtil.getNameFromDataDict(DictCode.目标类型, super.getTargetType());
    }

    public String getDeclaType() {
        return declaType;
    }

    public void setDeclaType(String declaType) {
        this.declaType = declaType;
    }

    public String getDeclaTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.申报类型, this.getDeclaType());
    }

    public void setDeclaTypeStr(String declaTypeStr) {
        this.declaTypeStr = declaTypeStr;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getIssueClassStr() {
        if (StringUtil.isEmpty(getIssueClass())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.新债_发行方式, getIssueClass());
    }

    public void setIssueClassStr(String issueClassStr) {
        this.issueClassStr = issueClassStr;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }
    public String getTenderTargetStr() {
        if (StringUtil.isEmpty(getIssueClass())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, getIssueClass());
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

    public Date getTenderSdate() {
        return tenderSdate;
    }

    public void setTenderSdate(Date tenderSdate) {
        this.tenderSdate = tenderSdate;
    }

    public Date getTenderEdate() {
        return tenderEdate;
    }

    public void setTenderEdate(Date tenderEdate) {
        this.tenderEdate = tenderEdate;
    }

    public Date getPayEdate() {
        return payEdate;
    }

    public void setPayEdate(Date payEdate) {
        this.payEdate = payEdate;
    }

    public String getPayEdateStr() {
        if (null != this.getPayEdate()) {
            return DateUtil.format(this.getPayEdate());
        }
        return payEdateStr;
    }

    public void setPayEdateStr(String payEdateStr) {
        this.payEdateStr = payEdateStr;
    }

    public String getIssueDateStr() {
        if (null != this.getIssueDate()) {
            return DateUtil.format(this.getIssueDate());
        }
        if (null != this.getTenderSdate() &&  null != this.getTenderEdate()) {
            return DateUtil.format(this.getTenderSdate())+"~"+DateUtil.format(this.getTenderEdate());
        }
        return issueDateStr;
    }

    public void setIssueDateStr(String issueDateStr) {
        this.issueDateStr = issueDateStr;
    }

    public List<FieldEtrBiddingProductDto> getBiddingProductDtos() {
        return biddingProductDtos;
    }

    public void setBiddingProductDtos(List<FieldEtrBiddingProductDto> biddingProductDtos) {
        this.biddingProductDtos = biddingProductDtos;
    }
}
