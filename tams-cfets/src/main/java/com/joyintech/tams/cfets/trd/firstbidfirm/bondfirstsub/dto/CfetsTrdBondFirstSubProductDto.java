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

    @ApiModelProperty(required = false, name = "entrustId", value = "指令代码", example = "WTBOND202204191858")
    private String entrustId;

    @ApiModelProperty(required = false, name = "portfolioId", value = "组合代码,页面不显示")
    private String portfolioId;

    @ApiModelProperty(required = false, name = "portfolioName", value = "组合名称")
    private String portfolioName;

    @ApiModelProperty(required = false, name = "avaNum", value = "剩余指令额度,需转化为万元 查看页面不显示")
    private BigDecimal avaNum;

    @ApiModelProperty(required = false, name = "subscriptionAmount", value = "认购量")
    private BigDecimal subscriptionAmount;

    @ApiModelProperty(required = false, name = "payableAmount", value = "应缴款金额")
    private BigDecimal payableAmount;

    @ApiModelProperty(required = false, name = "businessModel", value = "业务模式代码")
    private String businessModel;

    @ApiModelProperty(required = false, name = "businessModelStr", value = "业务模式名称")
    private String businessModelStr;

    @ApiModelProperty(required = false, name = "secManageAcctId", value = "证券账户")
    private String secManageAcctId;

    @ApiModelProperty(required = false, name = "secAcctName", value = "证券账户名称")
    private String secAcctName;

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
}
