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
@ApiModel(value = "申购产品列表", description = "一级申购信息-申购产品列表")
public class CfetsTrdBondFirstSubEntrustProductDto {

    @ApiModelProperty(required = false, name = "index", value = "序号")
    private int index;

    @ApiModelProperty(required = false, name = "portfolioName", value = "组合名称")
    private String portfolioName;

    @ApiModelProperty(required = false, name = "bidPrinAmt", value = "投标券面总额,需转化为万元")
    private BigDecimal bidPrinAmt;

    @ApiModelProperty(required = false, name = "businessModel", value = "业务模式代码")
    private String businessModel;

    @ApiModelProperty(required = false, name = "businessModelStr", value = "业务模式名称")
    private String businessModelStr;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

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
}
