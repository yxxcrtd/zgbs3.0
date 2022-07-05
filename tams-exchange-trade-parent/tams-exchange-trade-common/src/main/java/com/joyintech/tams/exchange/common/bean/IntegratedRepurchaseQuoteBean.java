package com.joyintech.tams.exchange.common.bean;


import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "质押式协议回购行情返回报文", description = "质押式协议回购行情返回报文")
public class IntegratedRepurchaseQuoteBean {
    @ApiModelProperty(name = "errorCode", value = "错误码")
    private int errorCode;
    @ApiModelProperty(name = "errorMsg", value = "错误描述")
    private String errorMsg;

    @ApiModelProperty(name = "brokerId", value = "柜台ID")
    private String brokerId;
    @ApiModelProperty(name = "brokerName", value = "柜台名称")
    private String brokerName;
    @ApiModelProperty(name = "cbpconferId", value = "约定号")
    private String cbpconferId;
    @ApiModelProperty(name = "entrustNo", value = "委托编号")
    private int entrustNo;
    @ApiModelProperty(name = "entrustProp", value = "委托属性")
    private String entrustProp;
    @ApiModelProperty(name = "entrustBs", value = "回购方向（1-正回购，2-逆回购）")
    private String entrustBs;
    @ApiModelProperty(name = "exchangeType", value = "交易类别")
    private String exchangeType;
    @ApiModelProperty(name = "stockType", value = "证券类别")
    private String stockType;
    @ApiModelProperty(name = "stockCode", value = "质押券代码")
    private String stockCode;
    @ApiModelProperty(name = "stockName", value = "质押券名称")
    private String stockName;
    @ApiModelProperty(name = "expireYearRate", value = "回购利率（到期年收益率）")
    private double expireYearRate;
    @ApiModelProperty(name = "repoTerm", value = "回购期限")
    private int repoTerm;
    @ApiModelProperty(name = "fundUsedDays", value = "实际占款天数")
    private int fundUsedDays;
    @ApiModelProperty(name = "firstExchdate", value = "首次结算日")
    private int firstExchdate;
    @ApiModelProperty(name = "repurchaseDate", value = "回购到期日")
    private int repurchaseDate;
    @ApiModelProperty(name = "settleEndDate", value = "到期结算日")
    private int settleEndDate;
    @ApiModelProperty(name = "componentNum", value = "成份股个数")
    private int componentNum;
    @ApiModelProperty(name = "oldImpawnCode", value = "原质押券代码")
    private String oldImpawnCode;
    @ApiModelProperty(name = "impawnAmount", value = "质押券数量")
    private int impawnAmount;
    @ApiModelProperty(name = "funderRatio", value = "折算比例")
    private double funderRatio;
    @ApiModelProperty(name = "impawnBalance", value = "成交金额")
    private double impawnBalance;
    @ApiModelProperty(name = "prevImpawnBalance", value = "原成交金额")
    private double prevImpawnBalance;
    @ApiModelProperty(name = "profit", value = "回购利息")
    private double profit;
    @ApiModelProperty(name = "settleBalance", value = "到期结算金额")
    private double settleBalance;
    @ApiModelProperty(name = "assetNetValue", value = "质押券面总额合计")
    private double assetNetValue;
    @ApiModelProperty(name = "bprsrcStatus", value = "协议回购质权人类型")
    private String bprsrcStatus;
    @ApiModelProperty(name = "origEntrustDate", value = "初始交易日期")
    private int origEntrustDate;
    @ApiModelProperty(name = "origCbpconferId", value = "原约定号")
    private String origCbpconferId;
    @ApiModelProperty(name = "agencyNo", value = "发起方交易商代码")
    private String agencyNo;
    @ApiModelProperty(name = "traderId", value = "发起方交易员代码")
    private String traderId;
    @ApiModelProperty(name = "fundAccount", value = "资金账号")
    private String fundAccount;
    @ApiModelProperty(name = "stockAccount", value = "证券账户")
    private String stockAccount;
    @ApiModelProperty(name = "seatNo", value = "席位编号")
    private String seatNo;
    @ApiModelProperty(name = "oppoAgency", value = "对手方交易商代码")
    private String oppoAgency;
    @ApiModelProperty(name = "oppoTraderId", value = "对手方交易员代码")
    private String oppoTraderId;
    @ApiModelProperty(name = "agencyName", value = "发起方交易商名称")
    private String agencyName;
    @ApiModelProperty(name = "positionStr", value = "定位串")
    private String positionStr;
    @ApiModelProperty(name = "oppoAgencyName", value = "对手方交易商名称")
    private String oppoAgencyName;
    @ApiModelProperty(name = "propStockAccount", value = "对方申报账号")
    private String propStockAccount;
    @ApiModelProperty(name = "propSeatNo", value = "对方席位号")
    private String propSeatNo;
    @ApiModelProperty(name = "remark", value = "补充条款")
    private String remark;
    @ApiModelProperty(name = "propAccountName", value = "对方账户名称")
    private String propAccountName;
    @ApiModelProperty(name = "realDateBack", value = "实际购回日期")
    private int realDateBack;
    @ApiModelProperty(name = "pledgeeName", value = "质权人")
    private String pledgeeName;
    @ApiModelProperty(name = "execid", value = "执行编号")
    private String execid;
    @ApiModelProperty(name = "deliverySide", value = "质押方向")
    private String deliverySide;
    @ApiModelProperty(name = "stkprop", value = "股份性质")
    private String stkprop;
    
    @ApiModelProperty(name = "swapIntoBonds", value = "换入质押券代码")
    private String swapIntoBonds;

    @ApiModelProperty(name = "swapIntoBondsName", value = "换入质押券名称")
    private String swapIntoBondsName;

    @ApiModelProperty(name = "swapIntoBondsVolume", value = "换入质押券数量")
    private BigDecimal swapIntoBondsVolume;

    @ApiModelProperty(name = "swapOutBonds", value = "换出质押券代码")
    private String swapOutBonds;

    @ApiModelProperty(name = "swapOutBondsName", value = "换出质押券名称")
    private String swapOutBondsName;

    @ApiModelProperty(name = "swapOutBondsVolume", value = "换出质押券数量")
    private BigDecimal swapOutBondsVolume;

    public String getSwapIntoBonds() {
		return swapIntoBonds;
	}

	public void setSwapIntoBonds(String swapIntoBonds) {
		this.swapIntoBonds = swapIntoBonds;
	}

	public String getSwapIntoBondsName() {
		return swapIntoBondsName;
	}

	public void setSwapIntoBondsName(String swapIntoBondsName) {
		this.swapIntoBondsName = swapIntoBondsName;
	}

	public BigDecimal getSwapIntoBondsVolume() {
		return swapIntoBondsVolume;
	}

	public void setSwapIntoBondsVolume(BigDecimal swapIntoBondsVolume) {
		this.swapIntoBondsVolume = swapIntoBondsVolume;
	}

	public String getSwapOutBonds() {
		return swapOutBonds;
	}

	public void setSwapOutBonds(String swapOutBonds) {
		this.swapOutBonds = swapOutBonds;
	}

	public String getSwapOutBondsName() {
		return swapOutBondsName;
	}

	public void setSwapOutBondsName(String swapOutBondsName) {
		this.swapOutBondsName = swapOutBondsName;
	}

	public BigDecimal getSwapOutBondsVolume() {
		return swapOutBondsVolume;
	}

	public void setSwapOutBondsVolume(BigDecimal swapOutBondsVolume) {
		this.swapOutBondsVolume = swapOutBondsVolume;
	}

	public String getStkprop() {
		return stkprop;
	}

	public void setStkprop(String stkprop) {
		this.stkprop = stkprop;
	}

	public String getDeliverySide() {
		return deliverySide;
	}

	public void setDeliverySide(String deliverySide) {
		this.deliverySide = deliverySide;
	}

	public String getExecid() {
		return execid;
	}

	public void setExecid(String execid) {
		this.execid = execid;
	}

	public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
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
        this.errorMsg = errorMsg;
    }

    public String getCbpconferId() {
        return cbpconferId;
    }

    public void setCbpconferId(String cbpconferId) {
        this.cbpconferId = cbpconferId;
    }

    public int getEntrustNo() {
        return entrustNo;
    }

    public void setEntrustNo(int entrustNo) {
        this.entrustNo = entrustNo;
    }

    public String getEntrustProp() {
        return entrustProp;
    }

    public void setEntrustProp(String entrustProp) {
        this.entrustProp = entrustProp;
    }

    public String getEntrustBs() {
        return entrustBs;
    }

    public void setEntrustBs(String entrustBs) {
        this.entrustBs = entrustBs;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
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

    public double getExpireYearRate() {
        return expireYearRate;
    }

    public void setExpireYearRate(double expireYearRate) {
        this.expireYearRate = expireYearRate;
    }

    public int getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(int repoTerm) {
        this.repoTerm = repoTerm;
    }

    public int getFundUsedDays() {
        return fundUsedDays;
    }

    public void setFundUsedDays(int fundUsedDays) {
        this.fundUsedDays = fundUsedDays;
    }

    public int getFirstExchdate() {
        return firstExchdate;
    }

    public void setFirstExchdate(int firstExchdate) {
        this.firstExchdate = firstExchdate;
    }

    public int getRepurchaseDate() {
        return repurchaseDate;
    }

    public void setRepurchaseDate(int repurchaseDate) {
        this.repurchaseDate = repurchaseDate;
    }

    public int getSettleEndDate() {
        return settleEndDate;
    }

    public void setSettleEndDate(int settleEndDate) {
        this.settleEndDate = settleEndDate;
    }

    public int getComponentNum() {
        return componentNum;
    }

    public void setComponentNum(int componentNum) {
        this.componentNum = componentNum;
    }

    public String getOldImpawnCode() {
        return oldImpawnCode;
    }

    public void setOldImpawnCode(String oldImpawnCode) {
        this.oldImpawnCode = oldImpawnCode;
    }

    public int getImpawnAmount() {
        return impawnAmount;
    }

    public void setImpawnAmount(int impawnAmount) {
        this.impawnAmount = impawnAmount;
    }

    public double getFunderRatio() {
        return funderRatio;
    }

    public void setFunderRatio(double funderRatio) {
        this.funderRatio = funderRatio;
    }

    public double getImpawnBalance() {
        return impawnBalance;
    }

    public void setImpawnBalance(double impawnBalance) {
        this.impawnBalance = impawnBalance;
    }

    public double getPrevImpawnBalance() {
        return prevImpawnBalance;
    }

    public void setPrevImpawnBalance(double prevImpawnBalance) {
        this.prevImpawnBalance = prevImpawnBalance;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getSettleBalance() {
        return settleBalance;
    }

    public void setSettleBalance(double settleBalance) {
        this.settleBalance = settleBalance;
    }

    public double getAssetNetValue() {
        return assetNetValue;
    }

    public void setAssetNetValue(double assetNetValue) {
        this.assetNetValue = assetNetValue;
    }

    public String getBprsrcStatus() {
        return bprsrcStatus;
    }

    public void setBprsrcStatus(String bprsrcStatus) {
        this.bprsrcStatus = bprsrcStatus;
    }

    public int getOrigEntrustDate() {
        return origEntrustDate;
    }

    public void setOrigEntrustDate(int origEntrustDate) {
        this.origEntrustDate = origEntrustDate;
    }

    public String getOrigCbpconferId() {
        return origCbpconferId;
    }

    public void setOrigCbpconferId(String origCbpconferId) {
        this.origCbpconferId = origCbpconferId;
    }

    public String getAgencyNo() {
        return agencyNo;
    }

    public void setAgencyNo(String agencyNo) {
        this.agencyNo = agencyNo;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getFundAccount() {
        return fundAccount;
    }

    public void setFundAccount(String fundAccount) {
        this.fundAccount = fundAccount;
    }

    public String getStockAccount() {
        return stockAccount;
    }

    public void setStockAccount(String stockAccount) {
        this.stockAccount = stockAccount;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public String getOppoAgency() {
        return oppoAgency;
    }

    public void setOppoAgency(String oppoAgency) {
        this.oppoAgency = oppoAgency;
    }

    public String getOppoTraderId() {
        return oppoTraderId;
    }

    public void setOppoTraderId(String oppoTraderId) {
        this.oppoTraderId = oppoTraderId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getPositionStr() {
        return positionStr;
    }

    public void setPositionStr(String positionStr) {
        this.positionStr = positionStr;
    }

    public String getOppoAgencyName() {
        return oppoAgencyName;
    }

    public void setOppoAgencyName(String oppoAgencyName) {
        this.oppoAgencyName = oppoAgencyName;
    }

    public String getPropStockAccount() {
        return propStockAccount;
    }

    public void setPropStockAccount(String propStockAccount) {
        this.propStockAccount = propStockAccount;
    }

    public String getPropSeatNo() {
        return propSeatNo;
    }

    public void setPropSeatNo(String propSeatNo) {
        this.propSeatNo = propSeatNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPropAccountName() {
        return propAccountName;
    }

    public void setPropAccountName(String propAccountName) {
        this.propAccountName = propAccountName;
    }

    public int getRealDateBack() {
        return realDateBack;
    }

    public void setRealDateBack(int realDateBack) {
        this.realDateBack = realDateBack;
    }

    public String getPledgeeName() {
        return pledgeeName;
    }

    public void setPledgeeName(String pledgeeName) {
        this.pledgeeName = pledgeeName;
    }

}
