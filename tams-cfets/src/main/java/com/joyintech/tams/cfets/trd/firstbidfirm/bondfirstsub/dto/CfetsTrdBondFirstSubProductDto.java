package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @author jiangpeng
 * @date 2022年04月19日 18:12
 */
@ApiModel(value = "申购产品列表", description = "一级申购指令信息-申购产品列表")
public class CfetsTrdBondFirstSubProductDto {

    @ApiModelProperty(name = "index", value = "序号", example = "1")
    private String index;

    @ApiModelProperty(name = "entrustId", value = "指令代码", example = "WTBOND202204191858")
    private String entrustId;

    @ApiModelProperty(name = "portfolioId", value = "组合代码,页面不显示")
    private String portfolioId;

    @ApiModelProperty(name = "portfolioName", value = "组合名称")
    private String portfolioName;

    @ApiModelProperty(name = "avaNum", value = "剩余指令额度,需转化为万元 查看页面不显示")
    private BigDecimal avaNum;

    /**
     * 计算尾插时暂时记录扣除分配后的剩余指令额度
     * @author jiangpeng
     * @date 2022/4/24 9:41
     */
    private BigDecimal avaNumTemp;

    @ApiModelProperty(name = "subscriptionAmount", value = "认购量")
    private BigDecimal subscriptionAmount;

    @ApiModelProperty(name = "payableAmount", value = "应缴款金额")
    private BigDecimal payableAmount;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费", example = "1000.00")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "businessModel", value = "业务模式代码")
    private String businessModel;

    @ApiModelProperty(name = "businessModelStr", value = "业务模式名称")
    private String businessModelStr;

    @ApiModelProperty(name = "secManageAcctId", value = "证券账户")
    private String secManageAcctId;

    @ApiModelProperty(name = "secAcctName", value = "证券账户名称")
    private String secAcctName;

    @ApiModelProperty(name = "cashT", value = "T+1日可用现金(元)")
    private BigDecimal cashT;

    @ApiModelProperty(name = "cashT1", value = "T日可用现金(元)")
    private BigDecimal cashT1;

    @ApiModelProperty(name = "realCash", value = "托管户账户现金(元)")
    private BigDecimal realCash;

    @ApiModelProperty(name = "secDesirableCash", value = "券商户可取现金(元)")
    private BigDecimal secDesirableCash;

    /**
     * 交易成员ID，21位编号
     */
    private String partyId;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

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

    public BigDecimal getAvaNum() {
        return avaNum;
    }

    public void setAvaNum(BigDecimal avaNum) {
        this.avaNum = avaNum;
    }

    public BigDecimal getAvaNumTemp() {
        return avaNumTemp;
    }

    public void setAvaNumTemp(BigDecimal avaNumTemp) {
        this.avaNumTemp = avaNumTemp;
    }

    public BigDecimal getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public void setSubscriptionAmount(BigDecimal subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
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

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getSecAcctName() {
        return secAcctName;
    }

    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
    }

    public String getBusinessModelStr() {
        if (StringUtil.isEmpty(getBusinessModel())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, getBusinessModel());
    }

    public void setBusinessModelStr(String businessModelStr) {
        this.businessModelStr = businessModelStr;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public BigDecimal getCashT() {
        return cashT;
    }

    public void setCashT(BigDecimal cashT) {
        this.cashT = cashT;
    }

    public BigDecimal getCashT1() {
        return cashT1;
    }

    public void setCashT1(BigDecimal cashT1) {
        this.cashT1 = cashT1;
    }

    public BigDecimal getRealCash() {
        return realCash;
    }

    public void setRealCash(BigDecimal realCash) {
        this.realCash = realCash;
    }

    public BigDecimal getSecDesirableCash() {
        return secDesirableCash;
    }

    public void setSecDesirableCash(BigDecimal secDesirableCash) {
        this.secDesirableCash = secDesirableCash;
    }
}
