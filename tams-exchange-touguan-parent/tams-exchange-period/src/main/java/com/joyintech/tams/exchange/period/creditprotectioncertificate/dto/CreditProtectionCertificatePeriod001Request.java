package com.joyintech.tams.exchange.period.creditprotectioncertificate.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class CreditProtectionCertificatePeriod001Request {


    @ApiModelProperty(name="busiTypePosition",value="业务类型",example="206")
    private String businessType;

    @ApiModelProperty(name="busiTypePosition",value="业务类型数组",example="206")
    private String businessTypeList;

    @ApiModelProperty(name="businessCode",value="业务编号",example="206")
    private String businessCode;

    @ApiModelProperty(name="finprodId",value="资产代码",example="字符串1,字符串2,...")
    private String finprodId;

    @ApiModelProperty(name="finprodName",value="资产名称",example="字符串1")
    private String finprodName;

    @ApiModelProperty(name="settleMethod",value="结算方式",example="01")
    private String settleMethod;

    @ApiModelProperty(name="settleMethod",value="有效状态",example="01")
    private String validStatus;

    @ApiModelProperty(name="search",value="关键字",example="12")
    private String search;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    @ApiModelProperty(name="tradeDate",value="交易日期")
    private Date tradeDate;


    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getSettleMethod() {
        return settleMethod;
    }

    public void setSettleMethod(String settleMethod) {
        this.settleMethod = settleMethod;
    }

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getBusinessTypeList() {
        return businessTypeList;
    }

    public void setBusinessTypeList(String businessTypeList) {
        this.businessTypeList = businessTypeList;
    }
}

