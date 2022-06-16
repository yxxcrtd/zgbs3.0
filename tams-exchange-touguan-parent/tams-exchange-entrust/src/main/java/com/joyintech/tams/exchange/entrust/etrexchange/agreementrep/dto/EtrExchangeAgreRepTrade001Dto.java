package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.validator.EtrExchangeAgreRepTradeValidator;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.etragrereptrade.validator.EtrAgreRepTradeValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@PageId("EtrExchangeAgreRepTrade001")
@ValidLogic(targetClass = EtrExchangeAgreRepTradeValidator.class)
public class EtrExchangeAgreRepTrade001Dto extends BaseEntity implements ProcessEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3436776283481983592L;
	
	private String tradeId;
	private String intiTradeId;
	private String origRepSecId;
	private BigDecimal origRepSecNum;
	private BigDecimal origPrintAmt;
	private BigDecimal origConRatio;
	private String  portfolioId;
	private String  acctNo;
	private String secAcctId;
	private String tradeMarket;
	private Integer term;
	private BigDecimal tradeAmt;
	private BigDecimal yield;
	private String fristSettleDate;
	private String expireSettleDate;
	private Integer occupyDays;
	private BigDecimal intAmt;
	private BigDecimal expireSettleAmt;
	private String counterId;
	private String counterTrader;
	private String trader;
	private String seatId;
	private String addCla;
	private String entrustId;
	private String finprodId;
	private BigDecimal pledgeFaceValue;
	private String newfinprodId;
	private BigDecimal newpledgeFaceValue;
	private String repSecId;
	private BigDecimal conRatio;
	private BigDecimal repSecNum;
	private BigDecimal printAmt;
	private String newRepSecId;
	private BigDecimal newConRatio;
	private BigDecimal newRepSecNum;
	private BigDecimal newPrintAmt;
	private String outfinprodId;
	private BigDecimal outRepSecNum;
	private String outRepSecId;
	private BigDecimal outAmt;  //换出金额
	private BigDecimal outPledgeFaceValue;
	private String reqOnId;
	private String vdate;
	private String mdate;
	private String acctTradeMarket;
	private String psList;
	private String operateType;
	private String invManagerId;
	private String entrustBussType;
	private String agreeOnId;
    private String elementId;
    private String pageId;
    private String businessTypeName;
    private String thirdTrade;
    private Date tradeDate;
    // 协议回购首期交易的到期结算金额
    private BigDecimal settleAmt;
    // 续做类型
	private String redoType;
	private String finprodType2;
	private String fromEtrExchange;
	private String busiPs;
	private String assetType2;

	/**
	 * 质押券单位
	 */
	private String pledgeUnit;

	/**
	 * 交易方向(页面：业务类型)
	 */
	private String ps;

	/**
	 * 回购方向
	 */
	private String repPs;

	/**
	 *
	 */
	/**
	 * 首期的金融类产品标的信息
	 */
	private FinTradeProduct firstFinTradeProduct;
	/**
	 * 可用持仓
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal accAvailablePos;
	@ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
	private String softSkipMode;

	/**
	 * 风险试算结果
	 */
	private String riskTrialSpan;

	/**
	 * 风险试算实体类
	 */
	private List<RiskTrialRetDto> riskTrialRetDto;

	/**
	 * 回购到期日
	 */
	private String hgMDate;
	/**
	 * 头寸业务类型
	 */
	private String busiTypePosition;

	public String getRiskTrialSpan() {
		return riskTrialSpan;
	}

	public void setRiskTrialSpan(String riskTrialSpan) {
		this.riskTrialSpan = riskTrialSpan;
	}

	public List<RiskTrialRetDto> getRiskTrialRetDto() {
		return riskTrialRetDto;
	}

	public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
		this.riskTrialRetDto = riskTrialRetDto;
	}

	public String getTradeId() {
		return tradeId;
	}
	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}
	public String getIntiTradeId() {
		return intiTradeId;
	}
	public void setIntiTradeId(String intiTradeId) {
		this.intiTradeId = intiTradeId;
	}
	public String getOrigRepSecId() {
		return origRepSecId;
	}
	public void setOrigRepSecId(String origRepSecId) {
		this.origRepSecId = origRepSecId;
	}
	public BigDecimal getOrigRepSecNum() {
		return origRepSecNum;
	}
	public void setOrigRepSecNum(BigDecimal origRepSecNum) {
		this.origRepSecNum = origRepSecNum;
	}
	public BigDecimal getOrigPrintAmt() {
		return origPrintAmt;
	}
	public void setOrigPrintAmt(BigDecimal origPrintAmt) {
		this.origPrintAmt = origPrintAmt;
	}
	public BigDecimal getOrigConRatio() {
		return origConRatio;
	}
	public void setOrigConRatio(BigDecimal origConRatio) {
		this.origConRatio = origConRatio;
	}
	public String getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}
	public String getAcctNo() {
		return acctNo;
	}
	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}
	public String getTradeMarket() {
		return tradeMarket;
	}
	public void setTradeMarket(String tradeMarket) {
		this.tradeMarket = tradeMarket;
	}
	public Integer getTerm() {
		return term;
	}
	public void setTerm(Integer term) {
		this.term = term;
	}
	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}
	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}
	public BigDecimal getYield() {
		return yield;
	}
	public void setYield(BigDecimal yield) {
		this.yield = yield;
	}
	public String getFristSettleDate() {
		return fristSettleDate;
	}
	public void setFristSettleDate(String fristSettleDate) {
		this.fristSettleDate = fristSettleDate;
	}
	public String getExpireSettleDate() {
		return expireSettleDate;
	}
	public void setExpireSettleDate(String expireSettleDate) {
		this.expireSettleDate = expireSettleDate;
	}
	public Integer getOccupyDays() {
		return occupyDays;
	}
	public void setOccupyDays(Integer occupyDays) {
		this.occupyDays = occupyDays;
	}
	public BigDecimal getIntAmt() {
		return intAmt;
	}
	public void setIntAmt(BigDecimal intAmt) {
		this.intAmt = intAmt;
	}
	public BigDecimal getExpireSettleAmt() {
		return expireSettleAmt;
	}
	public void setExpireSettleAmt(BigDecimal expireSettleAmt) {
		this.expireSettleAmt = expireSettleAmt;
	}
	public String getCounterId() {
		return counterId;
	}
	public void setCounterId(String counterId) {
		this.counterId = counterId;
	}
	public String getCounterTrader() {
		return counterTrader;
	}
	public void setCounterTrader(String counterTrader) {
		this.counterTrader = counterTrader;
	}
	public String getTrader() {
		return trader;
	}
	public void setTrader(String trader) {
		this.trader = trader;
	}
	public String getSeatId() {
		return seatId;
	}
	public void setSeatId(String seatId) {
		this.seatId = seatId;
	}
	public String getRepPs() {
		return repPs;
	}
	public void setRepPs(String repPs) {
		this.repPs = repPs;
	}
	public String getSecAcctId() {
		return secAcctId;
	}
	public void setSecAcctId(String secAcctId) {
		this.secAcctId = secAcctId;
	}
	public String getAddCla() {
		return addCla;
	}
	public void setAddCla(String addCla) {
		this.addCla = addCla;
	}
	@PrimaryKey(value = Field.PK_1)
	public String getEntrustId() {
		return entrustId;
	}
	public void setEntrustId(String entrustId) {
		this.entrustId = entrustId;
	}
	public String getFinprodId() {
		return finprodId;
	}
	public void setFinprodId(String finprodId) {
		this.finprodId = finprodId;
	}
	public BigDecimal getPledgeFaceValue() {
		return pledgeFaceValue;
	}
	public void setPledgeFaceValue(BigDecimal pledgeFaceValue) {
		this.pledgeFaceValue = pledgeFaceValue;
	}
	public String getNewfinprodId() {
		return newfinprodId;
	}
	public void setNewfinprodId(String newfinprodId) {
		this.newfinprodId = newfinprodId;
	}
	public BigDecimal getNewpledgeFaceValue() {
		return newpledgeFaceValue;
	}
	public void setNewpledgeFaceValue(BigDecimal newpledgeFaceValue) {
		this.newpledgeFaceValue = newpledgeFaceValue;
	}
	public String getRepSecId() {
		return repSecId;
	}
	public void setRepSecId(String repSecId) {
		this.repSecId = repSecId;
	}
	public BigDecimal getConRatio() {
		return conRatio;
	}
	public void setConRatio(BigDecimal conRatio) {
		this.conRatio = conRatio;
	}
	public BigDecimal getRepSecNum() {
		return repSecNum;
	}
	public void setRepSecNum(BigDecimal repSecNum) {
		this.repSecNum = repSecNum;
	}
	public BigDecimal getPrintAmt() {
		return printAmt;
	}
	public void setPrintAmt(BigDecimal printAmt) {
		this.printAmt = printAmt;
	}
	public String getNewRepSecId() {
		return newRepSecId;
	}
	public void setNewRepSecId(String newRepSecId) {
		this.newRepSecId = newRepSecId;
	}
	public BigDecimal getNewConRatio() {
		return newConRatio;
	}
	public void setNewConRatio(BigDecimal newConRatio) {
		this.newConRatio = newConRatio;
	}
	public BigDecimal getNewRepSecNum() {
		return newRepSecNum;
	}
	public void setNewRepSecNum(BigDecimal newRepSecNum) {
		this.newRepSecNum = newRepSecNum;
	}
	public BigDecimal getNewPrintAmt() {
		return newPrintAmt;
	}
	public void setNewPrintAmt(BigDecimal newPrintAmt) {
		this.newPrintAmt = newPrintAmt;
	}
	public String getReqOnId() {
		return reqOnId;
	}
	public void setReqOnId(String reqOnId) {
		this.reqOnId = reqOnId;
	}
	public String getVdate() {
		return vdate;
	}
	public void setVdate(String vdate) {
		this.vdate = vdate;
	}
	public String getMdate() {
		return mdate;
	}
	public void setMdate(String mdate) {
		this.mdate = mdate;
	}
	public String getAcctTradeMarket() {
		return acctTradeMarket;
	}
	public void setAcctTradeMarket(String acctTradeMarket) {
		this.acctTradeMarket = acctTradeMarket;
	}
	public String getPsList() {
		return psList;
	}
	public void setPsList(String psList) {
		this.psList = psList;
	}
	public String getOperateType() {
		return operateType;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	public String getInvManagerId() {
		return invManagerId;
	}
	public void setInvManagerId(String invManagerId) {
		this.invManagerId = invManagerId;
	}
	public String getEntrustBussType() {
		return entrustBussType;
	}
	public void setEntrustBussType(String entrustBussType) {
		this.entrustBussType = entrustBussType;
	}
	public String getPs() {
		return ps;
	}
	public void setPs(String ps) {
		this.ps = ps;
	}
	public String getAgreeOnId() {
		return agreeOnId;
	}
	public void setAgreeOnId(String agreeOnId) {
		this.agreeOnId = agreeOnId;
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
	public String getBusinessTypeName() {
		return businessTypeName;
	}
	public void setBusinessTypeName(String businessTypeName) {
		this.businessTypeName = businessTypeName;
	}

	public String getThirdTrade() {
		return thirdTrade;
	}

	public void setThirdTrade(String thirdTrade) {
		this.thirdTrade = thirdTrade;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public BigDecimal getAccAvailablePos() {
		return accAvailablePos;
	}

	public void setAccAvailablePos(BigDecimal accAvailablePos) {
		this.accAvailablePos = accAvailablePos;
	}

	public FinTradeProduct getFirstFinTradeProduct() {
		return firstFinTradeProduct;
	}

	public void setFirstFinTradeProduct(FinTradeProduct firstFinTradeProduct) {
		this.firstFinTradeProduct = firstFinTradeProduct;
		if (this.firstFinTradeProduct != null){
			this.settleAmt = this.firstFinTradeProduct.getmTradeAmt();
		}
	}

	public BigDecimal getSettleAmt() {
		return settleAmt;
	}

	public void setSettleAmt(BigDecimal settleAmt) {
		this.settleAmt = settleAmt;
	}

	public String getRedoType() {
		return redoType;
	}

	public void setRedoType(String redoType) {
		this.redoType = redoType;
	}

	public String getFinprodType2() {
		return finprodType2;
	}

	public void setFinprodType2(String finprodType2) {
		this.finprodType2 = finprodType2;
	}

	public String getFromEtrExchange() {
		return fromEtrExchange;
	}

	public void setFromEtrExchange(String fromEtrExchange) {
		this.fromEtrExchange = fromEtrExchange;
	}

	public String getBusiPs() {
		return busiPs;
	}

	public void setBusiPs(String busiPs) {
		this.busiPs = busiPs;
	}

	public String getAssetType2() {
		return assetType2;
	}

	public void setAssetType2(String assetType2) {
		this.assetType2 = assetType2;
	}

	public String getPledgeUnit() {
		return pledgeUnit;
	}

	public void setPledgeUnit(String pledgeUnit) {
		this.pledgeUnit = pledgeUnit;
	}

	public String getSoftSkipMode() {
		return softSkipMode;
	}

	public void setSoftSkipMode(String softSkipMode) {
		this.softSkipMode = softSkipMode;
	}

	public String getHgMDate() {
		return hgMDate;
	}

	public void setHgMDate(String hgMDate) {
		this.hgMDate = hgMDate;
	}

	public String getBusiTypePosition() {
		return busiTypePosition;
	}

	public void setBusiTypePosition(String busiTypePosition) {
		this.busiTypePosition = busiTypePosition;
	}

	public BigDecimal getOutRepSecNum() {
		return outRepSecNum;
	}

	public void setOutRepSecNum(BigDecimal outRepSecNum) {
		this.outRepSecNum = outRepSecNum;
	}

	public String getOutRepSecId() {
		return outRepSecId;
	}

	public void setOutRepSecId(String outRepSecId) {
		this.outRepSecId = outRepSecId;
	}

	public BigDecimal getOutAmt() {
		return outAmt;
	}

	public void setOutAmt(BigDecimal outAmt) {
		this.outAmt = outAmt;
	}

	public BigDecimal getOutPledgeFaceValue() {
		return outPledgeFaceValue;
	}

	public void setOutPledgeFaceValue(BigDecimal outPledgeFaceValue) {
		this.outPledgeFaceValue = outPledgeFaceValue;
	}

	public String getOutfinprodId() {
		return outfinprodId;
	}

	public void setOutfinprodId(String outfinprodId) {
		this.outfinprodId = outfinprodId;
	}
}
