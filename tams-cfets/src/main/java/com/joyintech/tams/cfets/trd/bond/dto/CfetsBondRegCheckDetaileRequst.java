package com.joyintech.tams.cfets.trd.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zznn
 * @date 2021/11/25
 */
@ApiModel(value="登记相关操作检查入参明细",description="登记报价相关操作检查入参明细,如撤回")
public class CfetsBondRegCheckDetaileRequst {
    @ApiModelProperty(name="quoteId",value="报价编号、业务编号" ,required = true)
    private String quoteId;

    @ApiModelProperty(name="validStatus",value="有效状态",required = true)
    private String validStatus;

    @ApiModelProperty(name="ps",value="交易方向",required = true)
    private String ps;

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }
}

