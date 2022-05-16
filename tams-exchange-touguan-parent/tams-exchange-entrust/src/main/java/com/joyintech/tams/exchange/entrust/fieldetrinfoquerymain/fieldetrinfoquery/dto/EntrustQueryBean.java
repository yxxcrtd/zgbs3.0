package com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.thymeleaf.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "委托信息", description = "委托信息")
public class EntrustQueryBean implements Serializable {
    private static final long serialVersionUID = 3485015523935180239L;

    @ApiModelProperty(name = "id", value = "委托ID")
    private String id;

    @ApiModelProperty(name = "tradingDay", value = "业务日期")
    private String tradingDay;

    @ApiModelProperty(name = "instructionId", value = "指令编号")
    private String instructionId;

    @ApiModelProperty(name = "brokerEntrustId", value = "委托编号")
    private String brokerEntrustId;

    @ApiModelProperty(name = "portfolioId", value = "组合代码")
    private String portfolioId;

    @ApiModelProperty(name = "portfolioName", value = "组合名称")
    private String portfolioName;

    @ApiModelProperty(name = "financeAccount", value = "资金账户")
    private String financeAccount;

    @ApiModelProperty(name = "secAcctId", value = "证券账户")
    private String secAcctId;

    @ApiModelProperty(name = "market", value = "交易市场")
    private String market;

    @ApiModelProperty(name = "marketStr", value = "交易市场")
    private String marketStr;

    @ApiModelProperty(name = "tradePlat", value = "交易平台")
    private String tradePlat;

    @ApiModelProperty(name = "tradePlat", value = "交易平台")
    private String tradePlatStr;

    @ApiModelProperty(name = "stockCode", value = "资产代码")
    private String stockCode;

    @ApiModelProperty(name = "stockName", value = "资产名称")
    private String stockName;

    @ApiModelProperty(name = "finprodType2", value = "资产类型")
    private String finprodType2;

    @ApiModelProperty(name = "finprodType2Str", value = "资产类型")
    private String finprodType2Str;

    @ApiModelProperty(name = "entrustBs", value = "委托方向")
    private String entrustBs;

    @ApiModelProperty(name = "entrustBsStr", value = "委托方向")
    private String entrustBsStr;

    @ApiModelProperty(name = "entrustProp", value = "价格类型")
    private String entrustProp;

    @ApiModelProperty(name = "entrustPropStr", value = "价格类型")
    private String entrustPropStr;

    @ApiModelProperty(name = "entrustPrice", value = "委托价格")
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustPrice;

    @ApiModelProperty(name = "entrustVolume", value = "委托数量")
    private long entrustVolume;

    @ApiModelProperty(name = "entrustBalance", value = "委托金额")
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustBalance;

    @ApiModelProperty(name = "entrustStatus", value = "委托状态")
    private int entrustStatus;

    @ApiModelProperty(name = "entrustStatusStr", value = "委托状态")
    private String entrustStatusStr;

    @ApiModelProperty(name = "tradedVolume", value = "已成交数量")
    private long tradedVolume;

    @ApiModelProperty(name = "tradedBalance", value = "已成交金额")
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal tradedBalance;

    @ApiModelProperty(name = "tradedPrice", value = "成交价格")
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal tradedPrice;

    @ApiModelProperty(name = "invManagerId", value = "投资经理")
    private String invManagerId;

    @ApiModelProperty(name = "userId", value = "交易员")
    private String userId;

    @ApiModelProperty(name = "username", value = "交易员名称")
    private String username;

    @ApiModelProperty(name = "frozenBalance", value = "冻结金额")
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal frozenBalance;

    @ApiModelProperty(name = "frozenVolume", value = "冻结数量")
    private long frozenVolume;

    @ApiModelProperty(name = "cancelVolume", value = "撤销数量")
    private long cancelVolume;

    @ApiModelProperty(name = "createTime", value = "委托时间")
    private Date createTime;

    /**
     * ////////////////////////////查询无用字段
     */
    @ApiModelProperty(name = "positionStr", value = "定位串", hidden = true)
    private String positionStr;

    @ApiModelProperty(name = "parentOrderId", value = "订单编号", hidden = true)
    private String parentOrderId;

    @ApiModelProperty(name = "accountId", value = "网点账号", hidden = true)
    private String accountId;

    @ApiModelProperty(name = "accountName", value = "网点账号名称", hidden = true)
    private String accountName;

    @ApiModelProperty(name = "assetType", value = "资产类型", hidden = true)
    private String assetType;

    @ApiModelProperty(name = "entrustType", value = "委托类型", hidden = true)
    private String entrustType;

    @ApiModelProperty(name = "bsFlag", value = "买卖标志", hidden = true)
    private String bsFlag;

    @ApiModelProperty(name = "fullPrice", value = "全价价格", hidden = true)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal fullPrice;

    @ApiModelProperty(name = "discount", value = "质押券折算比例", hidden = true)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal discount;

    @ApiModelProperty(name = "transCode", value = "到期续作类型（0-非第三方，1-第三方）", hidden = true)
    private String transCode;

    @ApiModelProperty(name = "expireYearRate", value = "到期交割比率", hidden = true)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal expireYearRate;

    @ApiModelProperty(name = "pretermYearRate", value = "年化收益率", hidden = true)
    private long pretermYearRate;

    @ApiModelProperty(name = "liaisonName", value = "联络人姓名", hidden = true)
    private String liaisonName;

    @ApiModelProperty(name = "liaisonTel", value = "对手方交易员ID", hidden = true)
    private String liaisonTel;

    @ApiModelProperty(name = "propSeatNo", value = "对手方交易商ID", hidden = true)
    private String propSeatNo;

    @ApiModelProperty(name = "sellerNo", value = "本方交易员ID", hidden = true)
    private String sellerNo;

    @ApiModelProperty(name = "propSeatName", value = "对手方交易商名称", hidden = true)
    private String propSeatName;

    @ApiModelProperty(name = "contractId", value = "交易所请求编号", hidden = true)
    private String contractId;

    @ApiModelProperty(name = "firstSettleDate", value = "首次结算日", hidden = true)
    private String firstSettleDate;

    @ApiModelProperty(name = "backDate", value = "到期结算日", hidden = true)
    private String backDate;

    @ApiModelProperty(name = "compactTerm", value = "回购期限", hidden = true)
    private int compactTerm;

    @ApiModelProperty(name = "backBalance", value = "到期结算金额", hidden = true)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal backBalance;

    @ApiModelProperty(name = "origSerialNo", value = "原交易编号", hidden = true)
    private String origSerialNo;

    @ApiModelProperty(name = "reductionType", value = "类型", hidden = true)
    private String reductionType;

    @ApiModelProperty(name = "reportTime", value = "申报时间", hidden = true)
    private String reportTime;

    @ApiModelProperty(name = "tradeTime", value = "成交时间", hidden = true)
    private String tradeTime;

    @ApiModelProperty(name = "cancelTime", value = "撤销时间", hidden = true)
    private String cancelTime;

    @ApiModelProperty(name = "errorCode", value = "错误码", hidden = true)
    private int errorCode;

    @ApiModelProperty(name = "errorMsg", value = "错误描述", hidden = true)
    private String errorMsg;

    @ApiModelProperty(name = "localEntrustId", value = "本地委托编号", hidden = true)
    private String localEntrustId;

    @ApiModelProperty(name = "ipMac", value = "IP及MAC信息", hidden = true)
    private String ipMac;

    @ApiModelProperty(name = "remark", value = "备注", hidden = true)
    private String remark;

    @ApiModelProperty(name = "updateTime", value = "修改时间", hidden = true)
    private Date updateTime;

    @ApiModelProperty(name = "origEntrustId", value = "原委托ID", hidden = true)
    private String origEntrustId;

    @ApiModelProperty(name = "entrustId", value = "委托编号", hidden = true)
    private String entrustId;

    @ApiModelProperty(name = "count", value = "记录数", hidden = true)
    private int count;

    @ApiModelProperty(name = "isFullPrice", value = "是否全价（Y-是 ，N-否）", hidden = true)
    private String isFullPrice;

    @ApiModelProperty(name = "reportNo", value = "柜台报盘编号", hidden = true)
    private String reportNo;

    @ApiModelProperty(name = "noCancelEntrust", value = "是否撤单", hidden = true)
    private boolean noCancelEntrust = false;

    @ApiModelProperty(name = "operateUser", value = "操作人员ID 执行撤销异常委托操作人员ID", hidden = true)
    private String operateUser;

    @ApiModelProperty(name = "progress", value = "委托进度", hidden = true)
    private BigDecimal progress;

    public boolean isNoCancelEntrust() {
        return noCancelEntrust;
    }

    public void setNoCancelEntrust(boolean noCancelEntrust) {
        this.noCancelEntrust = noCancelEntrust;
    }

    public EntrustQueryBean() {
        this.fullPrice = new BigDecimal("0.00");
        this.entrustPrice = new BigDecimal("0.00");
        this.entrustBalance = new BigDecimal("0.00");
        this.discount = new BigDecimal("0.00");
        this.expireYearRate = new BigDecimal("0.00");
        this.backBalance = new BigDecimal("0.00");
        this.frozenBalance = new BigDecimal("0.00");
        this.tradedBalance = new BigDecimal("0.00");
        this.tradedPrice = new BigDecimal("0.00");
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId == null ? null : parentOrderId.trim();
    }

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId == null ? null : instructionId.trim();
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId == null ? null : accountId.trim();
    }

    public String getFinanceAccount() {
        return financeAccount;
    }

    public void setFinanceAccount(String financeAccount) {
        this.financeAccount = financeAccount == null ? null : financeAccount.trim();
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName == null ? null : accountName.trim();
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType == null ? null : assetType.trim();
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market == null ? null : market.trim();
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode == null ? null : stockCode.trim();
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName == null ? null : stockName.trim();
    }

    public String getEntrustType() {
        return entrustType;
    }

    public void setEntrustType(String entrustType) {
        this.entrustType = entrustType == null ? null : entrustType.trim();
    }

    public String getEntrustProp() {
        return entrustProp;
    }

    public void setEntrustProp(String entrustProp) {
        this.entrustProp = entrustProp == null ? null : entrustProp.trim();
    }

    public String getEntrustBs() {
        return entrustBs;
    }

    public void setEntrustBs(String entrustBs) {
        this.entrustBs = entrustBs == null ? null : entrustBs.trim();
    }

    public String getBsFlag() {
        return bsFlag;
    }

    public void setBsFlag(String bsFlag) {
        this.bsFlag = bsFlag == null ? null : bsFlag.trim();
    }

    public long getEntrustVolume() {
        // 质押券出入库 ZGCPCN-404
        if(StringUtils.equals(getEntrustBs(), "T68") || StringUtils.equals(getEntrustBs(), "T69")){
            return ArithmeticUtil.divide(new BigDecimal(String.valueOf(entrustVolume)),new BigDecimal("100"),2).longValue();
        }
        return entrustVolume;
    }

    public void setEntrustVolume(long entrustVolume) {
        this.entrustVolume = entrustVolume;
    }

    public BigDecimal getEntrustPrice() {
        // 质押券出入库
        if(StringUtils.equals(getEntrustBs(), "T68") || StringUtils.equals(getEntrustBs(), "T69")){
            return null;
        }
        return entrustPrice;
    }

    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    public BigDecimal getEntrustBalance() {
        // 质押券出入库
        if(StringUtils.equals(getEntrustBs(), "T68") || StringUtils.equals(getEntrustBs(), "T69")){
            return null;
        }
        return entrustBalance;
    }

    public void setEntrustBalance(BigDecimal entrustBalance) {
        this.entrustBalance = entrustBalance;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode == null ? null : transCode.trim();
    }

    public BigDecimal getExpireYearRate() {
        return expireYearRate;
    }

    public void setExpireYearRate(BigDecimal expireYearRate) {
        this.expireYearRate = expireYearRate;
    }

    public long getPretermYearRate() {
        return pretermYearRate;
    }

    public void setPretermYearRate(long pretermYearRate) {
        this.pretermYearRate = pretermYearRate;
    }

    public String getLiaisonName() {
        return liaisonName;
    }

    public void setLiaisonName(String liaisonName) {
        this.liaisonName = liaisonName == null ? null : liaisonName.trim();
    }

    public String getLiaisonTel() {
        return liaisonTel;
    }

    public void setLiaisonTel(String liaisonTel) {
        this.liaisonTel = liaisonTel == null ? null : liaisonTel.trim();
    }

    public String getPropSeatNo() {
        return propSeatNo;
    }

    public void setPropSeatNo(String propSeatNo) {
        this.propSeatNo = propSeatNo == null ? null : propSeatNo.trim();
    }

    public String getSellerNo() {
        return sellerNo;
    }

    public void setSellerNo(String sellerNo) {
        this.sellerNo = sellerNo == null ? null : sellerNo.trim();
    }

    public String getPropSeatName() {
        return propSeatName;
    }

    public void setPropSeatName(String propSeatName) {
        this.propSeatName = propSeatName == null ? null : propSeatName.trim();
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId == null ? null : contractId.trim();
    }

    public String getFirstSettleDate() {
        return firstSettleDate;
    }

    public void setFirstSettleDate(String firstSettleDate) {
        this.firstSettleDate = firstSettleDate == null ? null : firstSettleDate.trim();
    }

    public String getBackDate() {
        return backDate;
    }

    public void setBackDate(String backDate) {
        this.backDate = backDate == null ? null : backDate.trim();
    }

    public int getCompactTerm() {
        return compactTerm;
    }

    public void setCompactTerm(int compactTerm) {
        this.compactTerm = compactTerm;
    }

    public BigDecimal getBackBalance() {
        return backBalance;
    }

    public void setBackBalance(BigDecimal backBalance) {
        this.backBalance = backBalance;
    }

    public String getOrigSerialNo() {
        return origSerialNo;
    }

    public void setOrigSerialNo(String origSerialNo) {
        this.origSerialNo = origSerialNo == null ? null : origSerialNo.trim();
    }

    public String getReductionType() {
        return reductionType;
    }

    public void setReductionType(String reductionType) {
        this.reductionType = reductionType == null ? null : reductionType.trim();
    }

    public BigDecimal getFrozenBalance() {
        return frozenBalance;
    }

    public void setFrozenBalance(BigDecimal frozenBalance) {
        this.frozenBalance = frozenBalance;
    }

    public long getFrozenVolume() {
        return frozenVolume;
    }

    public void setFrozenVolume(long frozenVolume) {
        this.frozenVolume = frozenVolume;
    }

    public BigDecimal getTradedBalance() {
        // 质押券出入库
        if(StringUtils.equals(getEntrustBs(), "T68") || StringUtils.equals(getEntrustBs(), "T69")){
            return null;
        }
        return tradedBalance;
    }

    public void setTradedBalance(BigDecimal tradedBalance) {
        this.tradedBalance = tradedBalance;
    }

    public long getTradedVolume() {
        // 质押券出入库 ZGCPCN-404
        if(StringUtils.equals(getEntrustBs(), "T68") || StringUtils.equals(getEntrustBs(), "T69")){
            return ArithmeticUtil.divide(new BigDecimal(String.valueOf(tradedVolume)),new BigDecimal("100"),2).longValue();
        }
        return tradedVolume;
    }
    public void setTradedVolume(long tradedVolume) {
        this.tradedVolume = tradedVolume;
    }

    public BigDecimal getTradedPrice() {
        // 质押券出入库
        if(StringUtils.equals(getEntrustBs(), "T68") || StringUtils.equals(getEntrustBs(), "T69")){
            return null;
        }
        return tradedPrice;
    }

    public void setTradedPrice(BigDecimal tradedPrice) {
        this.tradedPrice = tradedPrice;
    }

    public long getCancelVolume() {
        return cancelVolume;
    }

    public void setCancelVolume(long cancelVolume) {
        this.cancelVolume = cancelVolume;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime == null ? null : reportTime.trim();
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime == null ? null : tradeTime.trim();
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime == null ? null : cancelTime.trim();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg == null ? null : errorMsg.trim();
    }

    public String getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(String tradingDay) {
        this.tradingDay = tradingDay == null ? null : tradingDay.trim();
    }

    public String getLocalEntrustId() {
        return localEntrustId;
    }

    public void setLocalEntrustId(String localEntrustId) {
        this.localEntrustId = localEntrustId == null ? null : localEntrustId.trim();
    }

    public String getBrokerEntrustId() {
        return brokerEntrustId;
    }

    public void setBrokerEntrustId(String brokerEntrustId) {
        this.brokerEntrustId = brokerEntrustId == null ? null : brokerEntrustId.trim();
    }

    public String getIpMac() {
        return ipMac;
    }

    public void setIpMac(String ipMac) {
        this.ipMac = ipMac == null ? null : ipMac.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(int entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getOrigEntrustId() {
        return origEntrustId;
    }

    public void setOrigEntrustId(String origEntrustId) {
        this.origEntrustId = origEntrustId == null ? null : origEntrustId.trim();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPositionStr() {
        return positionStr;
    }

    public void setPositionStr(String positionStr) {
        this.positionStr = positionStr;
    }

    public String getIsFullPrice() {
        return isFullPrice;
    }

    public void setIsFullPrice(String isFullPrice) {
        this.isFullPrice = isFullPrice;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
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

    public String getTradePlatStr() {
        if(StringUtil.isEmpty(tradePlat)){
            return null;
        }
//        return DictCodeList.from(DictCode.交易平台, tradePlat).getCodeName();
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, tradePlat);
    }

    public void setTradePlatStr(String tradePlatStr) {
        this.tradePlatStr = tradePlatStr;
    }

    public String getFinprodType2Str() {
        if(StringUtil.isEmpty(finprodType2)){
            return null;
        }
//        return DictCodeList.from(DictCode.平安资产类型, finprodType2).getCodeName();
        return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, finprodType2);
    }

    public void setFinprodType2Str(String finprodType2Str) {
        this.finprodType2Str = finprodType2Str;
    }

    public String getEntrustBsStr() {
        if(StringUtil.isEmpty(entrustBs)){
            return null;
        }
//        return DictCodeListExt2.from(DictCode.交易方向, entrustBs).getCodeName();
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, entrustBs);
    }

    public void setEntrustBsStr(String entrustBsStr) {
        this.entrustBsStr = entrustBsStr;
    }

    public String getEntrustPropStr() {
        return entrustPropStr;
    }

    public void setEntrustPropStr(String entrustPropStr) {
        this.entrustPropStr = entrustPropStr;
    }

    public String getEntrustStatusStr() {
        return EntrustStatus.getEntrustStatusName(entrustStatus);
    }

    public void setEntrustStatusStr(String entrustStatusStr) {
        this.entrustStatusStr = entrustStatusStr;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public void setProgress(BigDecimal progress) {
        this.progress = progress;
    }
}
