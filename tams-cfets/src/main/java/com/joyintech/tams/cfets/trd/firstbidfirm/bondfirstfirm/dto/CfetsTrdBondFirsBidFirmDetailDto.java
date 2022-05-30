package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

@ApiModel(value = "一级投标指令信息", description = "一级投标指令信息")
public class CfetsTrdBondFirsBidFirmDetailDto {

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

    @ApiModelProperty(required = false, name = "issueDate", value = "投标日")
    private Date issueDate;

    @ApiModelProperty(name = "issueDateStart", value = "投标日开始日期,字符串", example = "2021-12-21")
    private Date issueDateStart;

    @ApiModelProperty(name = "issueDateEnd", value = "投标日结束日期,字符串", example = "2021-12-21")
    private Date issueDateEnd;

    @ApiModelProperty(required = false, name = "issueDateStr", value = "投标日格式化")
    private String issueDateStr;

    @ApiModelProperty(required = false, name = "payEdate", value = "缴款截止日")
    private Date payEdate;

    @ApiModelProperty(required = false, name = "payEdateStr", value = "缴款截止日格式化")
    private String payEdateStr;

    @ApiModelProperty(name="counterId",value="交易对手",example="A1")
    private String counterId;

    @ApiModelProperty(name="counterName",value="交易对手名称",example="A1")
    private String counterName;

    @ApiModelProperty(required = false, name = "remark", value = "备注")
    private String remark;

    @ApiModelProperty(required = false, name = "productDtos", value = "投标产品列表")
    private List<CfetsTrdBondFirstBidFirmProductDto> productDtos;

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
        if (null != this.getIssueDateStart() && null != this.getIssueDateEnd()) {
            return DateUtil.format(this.getIssueDateStart()) + "~" + DateUtil.format(this.getIssueDateEnd());
        }
        return issueDateStr;
    }

    public Date getPayEdate() {
        return payEdate;
    }

    public void setPayEdate(Date payEdate) {
        this.payEdate = payEdate;
    }

    public String getPayEdateStr() {
        if (null != this.getPayEdate()) {
            return DateUtil.format(this.getPayEdate());
        }
        return payEdateStr;
    }

    public void setPayEdateStr(String payEdateStr) {
        this.payEdateStr = payEdateStr;
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

    public List<CfetsTrdBondFirstBidFirmProductDto> getProductDtos() {
        return productDtos;
    }

    public void setProductDtos(List<CfetsTrdBondFirstBidFirmProductDto> productDtos) {
        this.productDtos = productDtos;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public Date getIssueDateStart() {
        return issueDateStart;
    }

    public void setIssueDateStart(Date issueDateStart) {
        this.issueDateStart = issueDateStart;
    }

    public Date getIssueDateEnd() {
        return issueDateEnd;
    }

    public void setIssueDateEnd(Date issueDateEnd) {
        this.issueDateEnd = issueDateEnd;
    }
}



