package com.joyintech.tams.invest.common.trade.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 授信接口，入参参数实体
 */
public class CreditLinesInputDto extends BaseEntity {

    private static final long serialVersionUID = -3126787359934680928L;
    /**
     * 委托代码/交易编号
     */
    private String tradeId;

    /**
     * 预占编号
     */
    private String entrustId;

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     *  到期日
     */
    private Date mdate;

    /**
     * 授信占用调用来源：01、画面端 02、接口
     */
    private String creditCheckType;

    /**
     * 授信类型 01：指令、02：交易
     */
    private String creditCallType;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 委托类型/金融产品类型
     */
    private String finprodType2;

    /**
     * 交易对手
     */
    private String counterId;
    /**
     * 合作机构
     */
    private String chlId;
    /**
     * 币种
     */
    private String ccy;

    /**
     * 交易金额
     */
    private BigDecimal tradeAmt;
    /**
     * 交易状态
     */
    private String tradeStatus;
    /**
     * 交易份额
     */
    private BigDecimal shareAmt;
    /**
     * 交易方向
     */
    private String ps;

    /**
     * 货币对
     */
    private String ccyPair;

    /**
     * 期限
     */
    private Long term;

    /**
     * 期限
     */
    private Long realTerm;

    /**
     * 暂存无标的交易类型
     */
    private String tgBusiType;

    /**
     * 回购拆借方向
     */
    private String psPayment;

    /**
     * 质押式回购参数暂存
     */
    private String saleCode;

    /**
     * 是否穿透
     */
    private String isBottom;

    /**
     * 顶层金融产品代码
     */
    private String pFinprodId;

    private List<CreditLinesInputDto> inputDtoList;

    /**
     * 折算利率
     */
    private BigDecimal rate;

    /**
     * 单位净价
     */
    private BigDecimal netPrice;
    /**
     * 序号
     */
    private Integer rownum;

    //债转股 股票代码
    private String zhaiZhuanGuId;
    //债转股 股票数量
    private BigDecimal zhaiZhuanGuNum;
    //债转股 股票净价
    private BigDecimal zhaiZhuanGuDanJia;

    /**
     * 风险控制类别
     * 风险控制类别_软控(DictCode.风险控制类别,"01","软控"),
     * 风险控制类别_硬控(DictCode.风险控制类别,"02","硬控"),
     * @return
     */
    private String creditControl;


    public String getZhaiZhuanGuId() {
        return zhaiZhuanGuId;
    }

    public void setZhaiZhuanGuId(String zhaiZhuanGuId) {
        this.zhaiZhuanGuId = zhaiZhuanGuId;
    }

    public BigDecimal getZhaiZhuanGuNum() {
        return zhaiZhuanGuNum;
    }

    public void setZhaiZhuanGuNum(BigDecimal zhaiZhuanGuNum) {
        this.zhaiZhuanGuNum = zhaiZhuanGuNum;
    }

    public BigDecimal getZhaiZhuanGuDanJia() {
        return zhaiZhuanGuDanJia;
    }

    public void setZhaiZhuanGuDanJia(BigDecimal zhaiZhuanGuDanJia) {
        this.zhaiZhuanGuDanJia = zhaiZhuanGuDanJia;
    }

    public Integer getRownum() {
        return rownum;
    }

    public void setRownum(Integer rownum) {
        this.rownum = rownum;
    }

    /**
     * 是否是 净值型产品、货币型产品、基金
     */
    private String isSpecial;

    /**
     * 是否是基金买入
     */
    private String isjijinmairu;

    public String getIsjijinmairu() {
        return isjijinmairu;
    }

    public void setIsjijinmairu(String isjijinmairu) {
        this.isjijinmairu = isjijinmairu;
    }

    public String getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(String isSpecial) {
        this.isSpecial = isSpecial;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    /**
     * 质押品信息
     */
    private List<PledgeBondInfoDto> pledgeBondInfoDtoList;
    
    

    public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getCreditCheckType() {
        return creditCheckType;
    }

    public void setCreditCheckType(String creditCheckType) {
        this.creditCheckType = creditCheckType;
    }

    public String getCreditCallType() {
        return creditCallType;
    }

    public void setCreditCallType(String creditCallType) {
        this.creditCallType = creditCallType;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getChlId() {
        return chlId;
    }

    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getCcyPair() {
        return ccyPair;
    }

    public void setCcyPair(String ccyPair) {
        this.ccyPair = ccyPair;
    }

    public Long getTerm() {
        return term;
    }

    public void setTerm(Long term) {
        this.term = term;
    }

    public Long getRealTerm() {
        return realTerm;
    }

    public void setRealTerm(Long realTerm) {
        this.realTerm = realTerm;
    }

    public String getTgBusiType() {
        return tgBusiType;
    }

    public void setTgBusiType(String tgBusiType) {
        this.tgBusiType = tgBusiType;
    }

    public String getPsPayment() {
        return psPayment;
    }

    public void setPsPayment(String psPayment) {
        this.psPayment = psPayment;
    }

    public String getSaleCode() {
        return saleCode;
    }

    /**
     * 获取 isBottom
     * @return isBottom.
     */
    public String getIsBottom() {
        return isBottom;
    }
    
    /**
     * 设置 isBottom
     * @param isBottom isBottom
     */
    public void setIsBottom(String isBottom) {
        this.isBottom = isBottom;
    }
    
    /**
     * 获取 顶层金融产品代码
     * @return pFinprodId 顶层金融产品代码.
     */
    public String getpFinprodId() {
        return pFinprodId;
    }


    public List<CreditLinesInputDto> getInputDtoList() {
        return inputDtoList;
    }

    public void setInputDtoList(List<CreditLinesInputDto> inputDtoList) {
        this.inputDtoList = inputDtoList;
    }

    /**
     * 设置 顶层金融产品代码
     * @param pFinprodId 顶层金融产品代码
     */
    public void setpFinprodId(String pFinprodId) {
        this.pFinprodId = pFinprodId;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }


    public List<PledgeBondInfoDto> getPledgeBondInfoDtoList() {
        return pledgeBondInfoDtoList;
    }

    public void setPledgeBondInfoDtoList(List<PledgeBondInfoDto> pledgeBondInfoDtoList) {
        this.pledgeBondInfoDtoList = pledgeBondInfoDtoList;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 买断式回购中质押券标的代码
     */
    private String ZhiYaQuan;

    public String getZhiYaQuan() {
        return ZhiYaQuan;
    }

    public void setZhiYaQuan(String zhiYaQuan) {
        ZhiYaQuan = zhiYaQuan;
    }

    public String getCreditControl() {
        return creditControl;
    }

    public void setCreditControl(String creditControl) {
        this.creditControl = creditControl;
    }
}
