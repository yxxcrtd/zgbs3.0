package com.joyintech.service.entrust.etrcommanddispatcher.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;

public class EtrCommandDispatcherDto extends BaseEntity{
    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 委托代码
     */
    private String entrustId;

    /**
     * 组合代码
     */
    private String portfolioId;
    
    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 金融产品代码
     */
    private String finprodId;
    
    /**
     * 金融产品简称
     */
    private String finprodAbbr;
    
    /**
     * 金融产品全称
     */
    private String finprodName;

    /**
     * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType2;
    
    /**
     * 网点账号
     */
    private String acctNo;
    
    /**
     * 币种
     */
    private String CCY;
    
    /**
     * 资金账号
     */
    private String busiAcctNo;
    
    /***
     * 户名
     */
    private String acctName;
    
    /**
     * 证券账户代码
     */
    private String secAcctId;
    
    /**
     * 证券账户名称
     */
    private String secAcctName;
    
    /**
     * 股东代码
     */
    private String stockHolderNo;
    

    /**
     * 交易场所，银行间、上交所、深交所、柜面等
     */
    private String tradeMarket;

    /**
     * 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    private String tradePlat;
    
    /**
     * 市场代码
     */
    private String finprodMarketId;
    
    /**
     * 证券账户代码
     */
    private String PS;
    

    /**
     * 交易方式，竞价交易/大宗交易/盘后定价交易/大宗交易专场
     */
    private String dealMode;
    
    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 交易对手名称
     */
    private String counterName;
    /**
     * 深交所成交申报需要带对手方席位
     */
    private String counterSeat;
    
    /**
     * 对手方交易员
     */
    private String counterTrader;

    /**
     * 对手方联系方式
     */
    private String contactInfo;

    /**
     * 对手方交易商代码
     */
    private String counterDealerId;
    

    /**
     *约定号
     */
    private String appointmentNumber;

    /**
     *成交编号
     */
    private String dealNo;
    /**
     *初始交易编号
     */
    private String trdMatchId;
    /**
     *交易所请求号
     */
    private String quoterefId;
   
    /**
     * 委托日期
     */
    private Date tradeDate;

    /**
     * 委托开始日
     */
    private Date VDATE;

    /**
     * 委托结束日
     */
    private Date MDATE;
    
    /**
     * 是否自动分发
     */

   private String isAutoReceive; 
   
   /**
    * 衍生品买卖方向
    */
   private String derivativeBsDir;
   
   /**
    * 衍生品开平仓标志
    */
   private String derivativeOcFlag;
   
    /**
     * 补充协议
     */
    private String remark;
    /**
     * 委托额度，交易金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustAmt;
    
    /**
     * 委托数量，数量或者券面总额/100
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustNum;
  

    /**
     * 委托状态
     */
    private String entrustStatus;
    
    /**
     * 委托价格，净价、利率等
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustPrice;
    
    /**
     * 全价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fullPrice;
    
    /**
     * 价格模式
     */
    private String priceMode;
    
    /**
     * 已完成数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustLeftAmt;

    /**
     * 清算速度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settleSpeed;
    
    
    /**
     * 起息日
     */
    private Date valueDate;

    /**
     * 起息日2
     */
    private Date valueDate2;

    /**
     * 到期日
     */
    private Date maturityDate;
    

    /**
     * 到期日2
     */
    private Date maturityDate2;
    
    /**
     * 期限天数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal termDays;

    /**
     * 期限天数2
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal termDays2;

    /**
     * 申购类型
     */
    private String purchType;
    
    /**
     * 最低成交金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minAmt;

    /**
     * 目标金融产品代码
     */
    private String tarFinprodId;

    /**
     * 目标金融产品简称
     */
    private String tarinprodAbbr;

    /**
     * 目标金融产品市场代码
     */
    private String tarinprodMarketId;

    /**
     *交易员编号
     */
    private String tradeId;

    /**
     * 交易员姓名
     */
    private String traderName;
    

    /**
     * 投资经理
     */
    private String invManagerId;

    /**
     * 资产类型
     */
    private String assetType;
    
    /**
     * 资产二级分类
     */
    private String assetType1;

    /**
     * 资产三级分类
     */
    private String assetType10;
    
    /**
     * 资产四级分类
     */
    private String assetType11;
    
    /**
     * 配售代码
     */
    private String saleCode;
    
    /**
     * 投资目的
     */
    private String invAim;
    
    /**
     * 转股代码
     */
    private String conShareId;

    /**
     * 转股价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal conPrice;

    /**
     * 转股价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal conNum;

    /*审批标志*/
    private String approverFlage;

    /**本方交易员*/
    private String ourTrader;
    /**
     * 首次交收日
     */
    private Date vPayDate;
    /**
     * 到期交收日
     */
    private Date mPayDate; 
    /**
     * 占款天数
     */
    private BigDecimal capDays;
    /**
     * 到期应计利息
     */
    private BigDecimal mIntAmt;
    /**
     * 到期结算金额
     */
    private BigDecimal mTradeAmt; 
    /**
     * 首期回购方向
     */
    private String firstRepoPs;

    /**
     * 协议回购到期续作指令必填
     * 0-非第三方
     * 1-第三方
     */
    private String pledgeeType;
    /**
     * 指令类型
     */
    private String entrustClass;

    /**
     *业务模式
     */
    private String businessModel;
    /**
     * 指令业务类型
     */
    private String entrustBussType;

    /**
     * 计价方式
     */
    private String priceType;

    /**
     * 是否非担保
     */
    private String isNoGuard;

    /**
     * 目标类型
     */
    private String targetType;

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getIsNoGuard() {
        return isNoGuard;
    }

    public void setIsNoGuard(String isNoGuard) {
        this.isNoGuard = isNoGuard;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    

    public String getPledgeeType() {
        return pledgeeType;
    }

    public void setPledgeeType(String pledgeeType) {
        this.pledgeeType = pledgeeType;
    }
    
    public String getConShareId() {
        return conShareId;
    }

    public void setConShareId(String conShareId) {
        this.conShareId = conShareId;
    }

    public BigDecimal getConPrice() {
        return conPrice;
    }

    public void setConPrice(BigDecimal conPrice) {
        this.conPrice = conPrice;
    }

    public BigDecimal getConNum() {
        return conNum;
    }

    public void setConNum(BigDecimal conNum) {
        this.conNum = conNum;
    }
    
    /**
     * 取得 投资目的
     * @return 投资目的
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 投资目的
     * @param invAim 投资目的
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim == null ? null : invAim.trim();
    }

    /**
     * 取得 委托代码
     * @return 委托代码
     */
    public String getEntrustId() {
        return entrustId;
    }

    /**
     * 设置 委托代码
     * @param entrustId 委托代码
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId == null ? null : entrustId.trim();
    }

    /**
     * 取得 组合代码
     * @return 组合代码
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 组合代码
     * @param portfolioId 组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    /**
     * 取得 金融产品代码
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @return 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @param finprodType2 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2 == null ? null : finprodType2.trim();
    }

    /**
     * 取得 交易场所，银行间、上交所、深交所、柜面等
     * @return 交易场所，银行间、上交所、深交所、柜面等
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易场所，银行间、上交所、深交所、柜面等
     * @param tradeMarket 交易场所，银行间、上交所、深交所、柜面等
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    /**
     * 取得 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     * @return 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    public String getTradePlat() {
        return tradePlat;
    }

    /**
     * 设置 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     * @param tradePlat 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat == null ? null : tradePlat.trim();
    }

    /**
     * 取得 交易对手
     * @return 交易对手
     */
    public String getCounterId() {
        return counterId;
    }

    /**
     * 设置 交易对手
     * @param counterId 交易对手
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId == null ? null : counterId.trim();
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    /**
     * 取得 对手方交易员
     * @return 对手方交易员
     */
    public String getCounterTrader() {
        return counterTrader;
    }

    /**
     * 设置 对手方交易员
     * @param counterTrader 对手方交易员
     */
    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader == null ? null : counterTrader.trim();
    }

    /**
     * 取得 对手方联系方式
     * @return 对手方联系方式
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * 设置 对手方联系方式
     * @param contactInfo 对手方联系方式
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo == null ? null : contactInfo.trim();
    }

    /**
     * 取得交易方式,竞价交易/大宗交易/盘后定价交易/大宗交易专场
     * @return
     */
    public String getDealMode() {
        return dealMode;
    }
    /**
     * 设置交易方式,竞价交易/大宗交易/盘后定价交易/大宗交易专场
     * @return
     */
    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }

    /**
     * 取得 委托额度，交易金额
     * @return 委托额度，交易金额
     */
    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    /**
     * 设置 委托额度，交易金额
     * @param entrustAmt 委托额度，交易金额
     */
    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    /**
     * 取得 委托数量，数量或者券面总额/100
     * @return 委托数量，数量或者券面总额/100
     */
    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    /**
     * 设置 委托数量，数量或者券面总额/100
     * @param entrustNum 委托数量，数量或者券面总额/100
     */
    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

   



    /**
     * 取得 委托状态
     * @return 委托状态
     */
    public String getEntrustStatus() {
        return entrustStatus;
    }

    /**
     * 设置 委托状态
     * @param entrustStatus 委托状态
     */
    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus == null ? null : entrustStatus.trim();
    }


    /**
     * 取得 投资经理
     * @return 投资经理
     */
    public String getInvManagerId() {
        return invManagerId;
    }

    /**
     * 设置 投资经理
     * @param invManagerId 投资经理
     */
    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId == null ? null : invManagerId.trim();
    }

    
    /**
     * 上市板
     * @return
     */
    private String board;

    /**
     * 获取上市板
     * @return
     */
	public String getBoard() {
		return board;
	}
	/**
	 * 设置上市板
	 * @param board
	 */

	public void setBoard(String board) {
		this.board = board;
	}

    
    /**
     * 获取价格模式
     * @return
     */
    public String getPriceMode() {
        return priceMode;
    }

    /**
     * 设置价格模式
     * @param priceModel
     */
    public void setPriceMode(String priceMode) {
        this.priceMode = priceMode;
    }

	public String getPortfolioName() {
		return portfolioName;
	}

	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}

	public String getFinprodAbbr() {
		return finprodAbbr;
	}

	public void setFinprodAbbr(String finprodAbbr) {
		this.finprodAbbr = finprodAbbr;
	}

	public String getFinprodName() {
		return finprodName;
	}

	public void setFinprodName(String finprodName) {
		this.finprodName = finprodName;
	}

	public String getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

	public String getCCY() {
		return CCY;
	}

	public void setCCY(String cCY) {
		CCY = cCY;
	}

	public String getBusiAcctNo() {
		return busiAcctNo;
	}

	public void setBusiAcctNo(String busiAcctNo) {
		this.busiAcctNo = busiAcctNo;
	}

	public String getAcctName() {
		return acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}

	public String getSecAcctId() {
		return secAcctId;
	}

	public void setSecAcctId(String secAcctId) {
		this.secAcctId = secAcctId;
	}

	public String getSecAcctName() {
		return secAcctName;
	}

	public void setSecAcctName(String secAcctName) {
		this.secAcctName = secAcctName;
	}

	public String getStockHolderNo() {
		return stockHolderNo;
	}

	public void setStockHolderNo(String stockHolderNo) {
		this.stockHolderNo = stockHolderNo;
	}

	public String getFinprodMarketId() {
		return finprodMarketId;
	}

	public void setFinprodMarketId(String finprodMarketId) {
		this.finprodMarketId = finprodMarketId;
	}

	public String getPS() {
		return PS;
	}

	public void setPS(String pS) {
		PS = pS;
	}

	public String getCounterSeat() {
		return counterSeat;
	}

	public void setCounterSeat(String counterSeat) {
		this.counterSeat = counterSeat;
	}

	public String getCounterDealerId() {
		return counterDealerId;
	}

	public void setCounterDealerId(String counterDealerId) {
		this.counterDealerId = counterDealerId;
	}

	public String getAppointmentNumber() {
		return appointmentNumber;
	}

	public void setAppointmentNumber(String appointmentNumber) {
		this.appointmentNumber = appointmentNumber;
	}

	public String getDealNo() {
		return dealNo;
	}

	public void setDealNo(String dealNo) {
		this.dealNo = dealNo;
	}

	public String getTrdMatchId() {
		return trdMatchId;
	}

	public void setTrdMatchId(String trdMatchId) {
		this.trdMatchId = trdMatchId;
	}

	public String getQuoterefId() {
		return quoterefId;
	}

	public void setQuoterefId(String quoterefId) {
		this.quoterefId = quoterefId;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public Date getVDATE() {
		return VDATE;
	}

	public void setVDATE(Date vDATE) {
		VDATE = vDATE;
	}

	public Date getMDATE() {
		return MDATE;
	}

	public void setMDATE(Date mDATE) {
		MDATE = mDATE;
	}

	public BigDecimal getEntrustPrice() {
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

	public BigDecimal getEntrustLeftAmt() {
		return entrustLeftAmt;
	}

	public void setEntrustLeftAmt(BigDecimal entrustLeftAmt) {
		this.entrustLeftAmt = entrustLeftAmt;
	}

	public BigDecimal getSettleSpeed() {
		return settleSpeed;
	}

	public void setSettleSpeed(BigDecimal settleSpeed) {
		this.settleSpeed = settleSpeed;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getValueDate2() {
		return valueDate2;
	}

	public void setValueDate2(Date valueDate2) {
		this.valueDate2 = valueDate2;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public Date getMaturityDate2() {
		return maturityDate2;
	}

	public void setMaturityDate2(Date maturityDate2) {
		this.maturityDate2 = maturityDate2;
	}

	public BigDecimal getTermDays() {
		return termDays;
	}

	public void setTermDays(BigDecimal termDays) {
		this.termDays = termDays;
	}

	public BigDecimal getTermDays2() {
		return termDays2;
	}

	public void setTermDays2(BigDecimal termDays2) {
		this.termDays2 = termDays2;
	}

	public String getPurchType() {
		return purchType;
	}

	public void setPurchType(String purchType) {
		this.purchType = purchType;
	}

	public BigDecimal getMinAmt() {
		return minAmt;
	}

	public void setMinAmt(BigDecimal minAmt) {
		this.minAmt = minAmt;
	}

	public String getTarFinprodId() {
		return tarFinprodId;
	}

	public void setTarFinprodId(String tarFinprodId) {
		this.tarFinprodId = tarFinprodId;
	}

	public String getTarinprodAbbr() {
		return tarinprodAbbr;
	}

	public void setTarinprodAbbr(String tarinprodAbbr) {
		this.tarinprodAbbr = tarinprodAbbr;
	}

	public String getTarinprodMarketId() {
		return tarinprodMarketId;
	}

	public void setTarinprodMarketId(String tarinprodMarketId) {
		this.tarinprodMarketId = tarinprodMarketId;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getTraderName() {
		return traderName;
	}

	public void setTraderName(String traderName) {
		this.traderName = traderName;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getAssetType1() {
		return assetType1;
	}

	public void setAssetType1(String assetType1) {
		this.assetType1 = assetType1;
	}

	public String getAssetType10() {
		return assetType10;
	}

	public void setAssetType10(String assetType10) {
		this.assetType10 = assetType10;
	}

	public String getAssetType11() {
		return assetType11;
	}

	public void setAssetType11(String assetType11) {
		this.assetType11 = assetType11;
	}

	public String getSaleCode() {
		return saleCode;
	}

	public void setSaleCode(String saleCode) {
		this.saleCode = saleCode;
	}

    public String getApproverFlage() {
        return approverFlage;
    }

    public void setApproverFlage(String approverFlage) {
        this.approverFlage = approverFlage;
    }

    public String getOurTrader() {
        return ourTrader;
    }

    public void setOurTrader(String ourTrader) {
        this.ourTrader = ourTrader;
    }

	public String getIsAutoReceive() {
		return isAutoReceive;
	}

	public void setIsAutoReceive(String isAutoReceive) {
		this.isAutoReceive = isAutoReceive;
	}

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getvPayDate() {
        return vPayDate;
    }

    public void setvPayDate(Date vPayDate) {
        this.vPayDate = vPayDate;
    }

    public Date getmPayDate() {
        return mPayDate;
    }

    public void setmPayDate(Date mPayDate) {
        this.mPayDate = mPayDate;
    }

    public BigDecimal getCapDays() {
        return capDays;
    }

    public void setCapDays(BigDecimal capDays) {
        this.capDays = capDays;
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

    public String getFirstRepoPs() {
        return firstRepoPs;
    }

    public void setFirstRepoPs(String firstRepoPs) {
        this.firstRepoPs = firstRepoPs;
    }

	public String getDerivativeBsDir() {
		return derivativeBsDir;
	}

	public void setDerivativeBsDir(String derivativeBsDir) {
		this.derivativeBsDir = derivativeBsDir;
	}

	public String getDerivativeOcFlag() {
		return derivativeOcFlag;
	}

	public void setDerivativeOcFlag(String derivativeOcFlag) {
		this.derivativeOcFlag = derivativeOcFlag;
	}

    private String busiTypeTg;

    public String getBusiTypeTg() {
        return busiTypeTg;
    }

    public void setBusiTypeTg(String busiTypeTg) {
        this.busiTypeTg = busiTypeTg;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public String getEntrustBussType() {
        return entrustBussType;
    }

    public void setEntrustBussType(String entrustBussType) {
        this.entrustBussType = entrustBussType;
    }

    public String getEntrustClass() {
        return entrustClass;
    }

    public void setEntrustClass(String entrustClass) {
        this.entrustClass = entrustClass;
    }
}