package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ApiModel(value = "指令信息", description = "一级申购指令信息")
public class CfetsTrdBondFirstSubEntrustDetailDto {

    @ApiModelProperty(required = false, name = "entrustDate", value = "指令日期")
    private Date entrustDate;

    @ApiModelProperty(required = false, name = "entrustDateStr", value = "指令日期格式化")
    private String entrustDateStr;

    @ApiModelProperty(required = false, name = "finprodId", value = "资产代码,系统内代码一般不用于显示")
    private String finprodId;

    @ApiModelProperty(required = false, name = "finprodMarketId", value = "市场代码,显示用代码")
    private String finprodMarketId;

    @ApiModelProperty(required = false, name = "finprodAbbr", value = "资产简称")
    private String finprodAbbr;

    @ApiModelProperty(required = false, name = "issueClass", value = "发行方式")
    private String issueClass;

    @ApiModelProperty(required = false, name = "issueClassStr", value = "发行方式名称,展示用")
    private String issueClassStr;

    @ApiModelProperty(required = false, name = "issueDate", value = "发行日")
    private Date issueDate;

    @ApiModelProperty(required = false, name = "issueDateStr", value = "发行日格式化")
    private String issueDateStr;

    @ApiModelProperty(required = false, name = "payDate", value = "缴款日")
    private Date payDate;

    @ApiModelProperty(required = false, name = "payDateStr", value = "缴款日格式化")
    private String payDateStr;

    @ApiModelProperty(required = false, name = "issuePrice", value = "发行价格")
    private BigDecimal issuePrice;

    @ApiModelProperty(required = false, name = "referenceRate", value = "参考收益率")
    private BigDecimal referenceRate;

    @ApiModelProperty(required = false, name = "rate", value = "票面利率")
    private BigDecimal rate;

    @ApiModelProperty(required = false, name = "secManageAcctId", value = "证券账户")
    private String secManageAcctId;

    @ApiModelProperty(required = false, name = "secAcctName", value = "证券账户名称")
    private String secAcctName;

    @ApiModelProperty(required = false, name = "invManagerName", value = "投资经理")
    private String invManagerName;

    @ApiModelProperty(required = false, name = "ccy", value = "币种")
    private String ccy;

    @ApiModelProperty(required = false, name = "ccyStr", value = "币种名称")
    private String ccyStr;

    @ApiModelProperty(required = false, name = "remark", value = "备注")
    private String remark;

    @ApiModelProperty(required = false, name = "productDtos", value = "投标产品列表")
    private List<CfetsTrdBondFirstSubEntrustProductDto> productDtos;

    public Date getEntrustDate() {
        return entrustDate;
    }

    public void setEntrustDate(Date entrustDate) {
        this.entrustDate = entrustDate;
    }

    public String getEntrustDateStr() {
        if (null != this.getEntrustDate()) {
            return DateUtil.format(this.getEntrustDate());
        }
        return entrustDateStr;
    }

    public void setEntrustDateStr(String entrustDateStr) {
        this.entrustDateStr = entrustDateStr;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public void setIssueDateStr(String issueDateStr) {
        this.issueDateStr = issueDateStr;
    }

    public String getIssueDateStr() {
        if (null != this.getIssueDate()) {
            return DateUtil.format(this.getIssueDate());
        }
        return issueDateStr;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public void setPayDateStr(String payDateStr) {
        this.payDateStr = payDateStr;
    }

    public String getPayDateStr() {
        if (null != this.getPayDate()) {
            return DateUtil.format(this.getPayDate());
        }
        return payDateStr;
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

    public String getInvManagerName() {
        return invManagerName;
    }

    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getIssueClassStr() {
        if (StringUtil.isEmpty(getIssueClass())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.新债_发行方式, getIssueClass());
    }

    public void setIssueClassStr(String issueClassStr) {
        this.issueClassStr = issueClassStr;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public BigDecimal getReferenceRate() {
        return referenceRate;
    }

    public void setReferenceRate(BigDecimal referenceRate) {
        this.referenceRate = referenceRate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCcyStr() {
        if (StringUtil.isEmpty(this.getCcy())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCcy());
    }

    public void setCcyStr(String ccyStr) {
        this.ccyStr = ccyStr;
    }

    public List<CfetsTrdBondFirstSubEntrustProductDto> getProductDtos() {
        return productDtos;
    }

    public void setProductDtos(List<CfetsTrdBondFirstSubEntrustProductDto> productDtos) {
        this.productDtos = productDtos;
    }
}



