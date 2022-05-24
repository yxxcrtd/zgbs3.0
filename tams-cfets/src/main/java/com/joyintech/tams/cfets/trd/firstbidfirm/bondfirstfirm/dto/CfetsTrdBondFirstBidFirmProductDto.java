package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @author kchen
 */
@ApiModel(value = "一级投标确认信息产品列表", description = "一级投标确认信息产品列表")
public class CfetsTrdBondFirstBidFirmProductDto {

    @ApiModelProperty(required = false, name = "portfolioId", value = "组合ID")
    private String portfolioId;

    @ApiModelProperty(required = false, name = "portfolioName", value = "组合名称")
    private String portfolioName;

    @ApiModelProperty(name="bidPrice",value="投标标位",example="1")
    private String bidPrice;

    @ApiModelProperty(required = false, name = "bidPrinAmt", value = "投标券面总额,需转化为万元")
    private BigDecimal bidPrinAmt;

    @ApiModelProperty(required = false, name = "businessModel", value = "业务模式代码")
    private String businessModel;

    @ApiModelProperty(required = false, name = "businessModelStr", value = "业务模式名称")
    private String businessModelStr;

    @ApiModelProperty(name="portfolioTotalBidPrinAmt",value="组合汇总投标券面总额",example="1")
    private BigDecimal portfolioTotalBidPrinAmt;

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public BigDecimal getBidPrinAmt() {
        return bidPrinAmt;
    }

    public void setBidPrinAmt(BigDecimal bidPrinAmt) {
        this.bidPrinAmt = bidPrinAmt;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
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

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public BigDecimal getPortfolioTotalBidPrinAmt() {
        return portfolioTotalBidPrinAmt;
    }

    public void setPortfolioTotalBidPrinAmt(BigDecimal portfolioTotalBidPrinAmt) {
        this.portfolioTotalBidPrinAmt = portfolioTotalBidPrinAmt;
    }
}
