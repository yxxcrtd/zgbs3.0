package com.joyintech.tams.cfets.etr.repo.dto;

import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="指令查询入参",description="指令查询入参")
public class CfetsEtrRepoZLCXResponse {

    /**
     * 组合代码
     */
    @ApiModelProperty(name="portfolioId",value="组合代码")
    private String portfolioId;
    /**
     * 组合名称
     */
    @ApiModelProperty(name="portfolioName",value="组合名称")
    private String portfolioName;
    /**
     * 交易方向
     */
    @ApiModelProperty(name="ps",value="交易方向")
    private String ps;
    /**
     * 天数
     */
    @ApiModelProperty(name="repoTerm",value="回购期限")
    private String repoTerm;
    /**
     * 期限品种
     */
    @ApiModelProperty(name="termType",value="期限品种")
    private String termType;
    /**
     * 投资经理
     */
    @ApiModelProperty(name="invManagerId",value="投资经理")
    private String invManagerId;
    /**
     * 指令编号
     */
    @ApiModelProperty(name="entrustId",value="指令编号")
    private String entrustId;

    @ApiModelProperty(name="effectiveStatus",value="有效状态Code")
    private String effectiveStatus;

    @ApiModelProperty(name="effectiveStatusStr",value="有效状态")
    private String effectiveStatusStr;

    @ApiModelProperty(name="entrustStatus",value="指令状态Code")
    private String entrustStatus;

    /**
     * 价格模式
     */
    @ApiModelProperty(name="priceModel",value="价格模式")
    private String priceModel;

    /**
     * 清算速度
     */
    @ApiModelProperty(name="settleSpreed",value="清算速度")
    private String settleSpreed;

    /**
     * 指令日期
     */
    @ApiModelProperty(name="entrustDate",value="指令日期")
    private Date entrustDate;

    /**
     * 指令日期
     */
    @ApiModelProperty(name="entrustDate",value="指令日期")
    private String entrustDateStr;


    /**
     * 指令日期
     */
    @ApiModelProperty(name="mdate",value="到期日")
    private String mdate;

    /**
     * 回购利率
     */
    @ApiModelProperty(name="price",value="回购利率")
    private BigDecimal price;

    /**
     * 已完成额度
     */
    private BigDecimal finishAmt;

    /**
     * 交易完成度
     */
    private BigDecimal transProcess;
    /**
     * 交易完成度
     */
    private BigDecimal execProcess;

    @ApiModelProperty(name="execFinishAmt",value="已完成指令额度",example="1")
    private BigDecimal execFinishAmt;

    @ApiModelProperty(name="execRemainderAmt",value="剩余指令额度",example="1")
    private BigDecimal execRemainderAmt;

    @ApiModelProperty(name="transFinishAmt",value="已成交额度",example="1")
    private BigDecimal transFinishAmt;

    @ApiModelProperty(name="transRemainderAmt",value="剩余成交额度",example="1")
    private BigDecimal transRemainderAmt;


    /**
     * 备注
     */
    @ApiModelProperty(name="remark",value="备注",example="A1")
    private String remark;

    @ApiModelProperty(name="approver",value="审批人",example="1")
    private String approver;
    /**
     * 指令金额
     */
    @ApiModelProperty(name="entrustAmt",value="指令金额")
    private BigDecimal entrustAmt;

    //add by chenjiaxing ZGZGST-2609 指令管理-银行间指令-银行间质押式回购指令画面，交易对手显示不对 20210916 start
    /**
     * 交易对手代码
     */
    @ApiModelProperty(name="counterId",value="交易对手代码")
    private String counterId;

    /**
     * 交易对手代码
     */
    @ApiModelProperty(name="counterName",value="交易对手")
    private String counterName;

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

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getEffectiveStatus() {
        return effectiveStatus;
    }

    public void setEffectiveStatus(String effectiveStatus) {
        this.effectiveStatus = effectiveStatus;
    }

    public String getEffectiveStatusStr() {
        return effectiveStatusStr;
    }

    public void setEffectiveStatusStr(String effectiveStatusStr) {
        this.effectiveStatusStr = effectiveStatusStr;
    }

    public String getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus;
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

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public String getSettleSpreed() {
        return settleSpreed;
    }

    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
    }

    public Date getEntrustDate() {
        return entrustDate;
    }

    public void setEntrustDate(Date entrustDate) {
        this.entrustDate = entrustDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFinishAmt() {
        return finishAmt;
    }

    public void setFinishAmt(BigDecimal finishAmt) {
        this.finishAmt = finishAmt;
    }



    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getSettleSpreedStr() {
        return "T+" + this.getSettleSpreed();
    }

    public String getPriceModelStr() {
        return SysDatadictUtil.getNameFromDataDict(DictCode.价格模式,this.getPriceModel());
    }

    public String getPsStr() {
        return SysDatadictUtil.getNameFromDataDict(DictCode.回购拆借方向,this.getPs());
    }

    public String getEntrustStatusStr() {
        return SysDatadictUtil.getNameFromDataDict(DictCode.委托状态,this.getEntrustStatus());
    }

    public String getEntrustDateStr() {
        return entrustDateStr;
    }

    public void setEntrustDateStr(String entrustDateStr) {
        this.entrustDateStr = entrustDateStr;
    }

    public BigDecimal getTransProcess() {
        return transProcess;
    }

    public void setTransProcess(BigDecimal transProcess) {
        this.transProcess = transProcess;
    }

    public BigDecimal getExecProcess() {
        return execProcess;
    }

    public void setExecProcess(BigDecimal execProcess) {
        this.execProcess = execProcess;
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

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }
}
