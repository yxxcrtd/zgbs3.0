package com.joyintech.tams.invest.portfolio.ptlcashflowgapanalysis.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;

/**
 * 单一发行人持仓分析实体类Dto
 * @author pancz
 *
 */
public class PtlCashFlowGapAnalysis001Dto extends BaseEntity {
	
	/**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 8496377411327582051L;

    /**
     * 组合代码
     */
    private String portfolioId;
    /**
     * 资金账户
     */
    private String fundAccount;

    /**
     * 金额显示精度
     */
    private String displayAccuracy;

    /**
     * 日期
     */
    private Date cDate;
    
     private String cDateStr;
   

	/**
	 * 投管用业务类型或平安业务类型
	 */
	private String cashFlowType;

	/**
	 * 现金流类型
	 */
	private String cashType;
    /**
     * 费用类型
     */
    private String feetype;

    /**
     * 资产标的
     */
    private String assetSign;

    /**
     * 现金流
     */
    private BigDecimal cashFlow;

    /**
     * 币种
     */
    private String ccy;
    
    /**
     * 汇总维度
     */
    private String aggregDimension;
    /**
     * 流入现金流
     * @return
     */
    private BigDecimal inCashflow;
    /**
     * 流出现金流
     * @return
     */
    private BigDecimal outCashflow;
    /**
     * 净现金流
     * @return
     */
    private BigDecimal netCashflow;

	/**
	 * 是否是平安业务类型
	 */
	private boolean paType;
	/**
	 * 交易方向
	 */
	private String ps;
	/**
	 * 组合系列
	 */
	private String portfolioSeries;
	/**
	 * 获取 paType
	 *
	 * @return paType
	 */
	public boolean isPaType() {
		return paType;
	}

	/**
	 * 设置 paType
	 *
	 * @param paType paType
	 */
	public void setPaType(boolean paType) {
		this.paType = paType;
	}

	public String getAggregDimension() {
		return aggregDimension;
	}

	public void setAggregDimension(String aggregDimension) {
		this.aggregDimension = aggregDimension;
	}

	public BigDecimal getInCashflow() {
		return inCashflow;
	}

	public void setInCashflow(BigDecimal inCashflow) {
		this.inCashflow = inCashflow;
	}

	public BigDecimal getOutCashflow() {
		return outCashflow;
	}

	public void setOutCashflow(BigDecimal outCashflow) {
		this.outCashflow = outCashflow;
	}

	public BigDecimal getNetCashflow() {
		return netCashflow;
	}

	public void setNetCashflow(BigDecimal netCashflow) {
		this.netCashflow = netCashflow;
	}

	public String getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}

	public String getFundAccount() {
		return fundAccount;
	}

	public void setFundAccount(String fundAccount) {
		this.fundAccount = fundAccount;
	}

	public String getDisplayAccuracy() {
		return displayAccuracy;
	}

	public void setDisplayAccuracy(String displayAccuracy) {
		this.displayAccuracy = displayAccuracy;
	}

	public Date getcDate() {
		return cDate;
	}

	public void setcDate(Date cDate) {
		this.cDate = cDate;
	}

	public String getCashFlowType() {
		return cashFlowType;
	}
	public String getCashFlowTypeStr() {
		String cashFlowTypeStr = null;
		if (StringUtil.isNotEmpty(this.getCashFlowType())) {
			if (isPaType()) {
				cashFlowTypeStr = SysDataUtil.getNameFromDataDict(DictCode.金融产品类型_投管用, this.getCashFlowType());
				if (cashFlowTypeStr.equals(this.getCashFlowType())) {
					if (StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_销售手续费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_固定管理费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_托管费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_代销费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_浮动差额中收.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_浮动管理费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_其他费用.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_交易手续费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_结算服务费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_账户维护费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_增值附加税费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_外包服务费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_通道托管费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_通道管理费.getValue())
					|| StringUtil.equals(this.getCashFlowType(), DictCodeList.费用品种类型_佣金.getValue())
					||StringUtil.equals(this.getCashFlowType(), DictCodeListExt2.费用品种类型_投资顾问费.getValue())){
						cashFlowTypeStr =  "理财产品";
					}else{
						cashFlowTypeStr = SysDataUtil.getNameFromDataDict(DictCode.平安业务类型, this.getCashFlowType());
					}
				}
				cashFlowTypeStr = cashFlowTypeStr ;
			}
			else {
				cashFlowTypeStr = SysDataUtil.getNameFromDataDict(DictCode.金融产品类型_投管用, this.getCashFlowType());
                cashFlowTypeStr = cashFlowTypeStr + "";
			}
		}
		return cashFlowTypeStr;
	}

	public void setCashFlowType(String cashFlowType) {
		this.cashFlowType = cashFlowType;
	}

	public String getAssetSign() {
		return assetSign;
	}

	public void setAssetSign(String assetSign) {
		this.assetSign = assetSign;
	}

	public BigDecimal getCashFlow() {
		return cashFlow;
	}

	public void setCashFlow(BigDecimal cashFlow) {
		this.cashFlow = cashFlow;
	}

	public String getCcy() {
		return ccy;
	}

	public String getCcyStr() {
		if (StringUtil.isNotEmpty(ccy)) {
			return SysDataUtil.getNameFromDataDict(DictCode.币种, ccy);
		}
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	/**
	 * 获取 cashType
	 *
	 * @return cashType
	 */
	public String getCashType() {
		return cashType;
	}

	/**
	 * 设置 cashType
	 *
	 * @param cashType cashType
	 */
	public void setCashType(String cashType) {
		this.cashType = cashType;
	}

	public String getcDateStr() {
		return cDateStr;
	}

	public void setcDateStr(String cDateStr) {
		this.cDateStr = cDateStr;
	}
	
	public String getFeetype() {
		return feetype;
	}

	public void setFeetype(String feetype) {
		this.feetype = feetype;
	}
	
	public String getFeetypeStr() {
		if (StringUtil.isNotEmpty(feetype)) {
			return SysDataUtil.getNameFromDataDict(DictCode.费用品种类型, feetype);
		}
		return feetype;
	}
	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}
	public String getPsStr() {
		String psStr = null;
		if (StringUtil.isNotEmpty(this.getCashType())) {
			if (isPaType()) {
				psStr = SysDataUtil.getNameFromDataDict(DictCode.交易方向, this.getCashType());
				if (StringUtil.isEmpty(psStr)) {
					psStr =  "";
				}
				psStr = psStr ;
			}
			else {
				psStr = SysDataUtil.getNameFromDataDict(DictCode.现金流类型, this.getCashType());
				if(StringUtil.isEmpty(psStr) && "行权".equals(this.getCashType())){
					psStr="行权";
				}
				if (StringUtil.isEmpty(psStr)) {
					psStr =  "";
				}
				psStr = psStr + "预测";
			}
		}
		return psStr;
	}

	public String getPortfolioSeries() {
		return portfolioSeries;
	}

	public String getPortfolioSeriesStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.组合系列, this.getPortfolioSeries());
	}

	public void setPortfolioSeries(String portfolioSeries) {
		this.portfolioSeries = portfolioSeries;
	}
}
