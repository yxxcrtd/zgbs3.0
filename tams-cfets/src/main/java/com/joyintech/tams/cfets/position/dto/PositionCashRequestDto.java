package com.joyintech.tams.cfets.position.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 头寸现金查询，请求参数
 */
@ApiModel(value="现金查询请求参数",description="现金查询请求参数")
public class PositionCashRequestDto {

    @ApiModelProperty(name="portfolioIdList",value="组合代码集合",example="[字符串1,字符串2,...]", required = true)
    private List<String> portfolioIdList;

    @ApiModelProperty(name="cdate",value="交易日期",example="2021-10-20", required = true)
    private String cdate;

    @ApiModelProperty(name="acctId",value="账户",example="xxx")
    private String acctId;

    @ApiModelProperty(name="ccy",value="币种",example="CNY")
    private String ccy;

    @ApiModelProperty(name="calId",value="交易日历",example="03", notes = "默认为03银行间")
    private String calId;

    @ApiModelProperty(name="acctTypeList",value="交易现金查询账户类型集合",example="['A01','A07']")
    private List<String> acctTypeList;

    @ApiModelProperty(name="acctTypes",value="账户现金查询账户类型集合",example="['A01','A07']")
    private List<String> acctTypes;

    @ApiModelProperty(name="flowType",value="头寸信息类型",example="etr 指令 trd 交易")
    private String flowType;

    List<PositionCashSecDto>  ptlList;

    public List<String> getPortfolioIdList() {
        return portfolioIdList;
    }

    public void setPortfolioIdList(List<String> portfolioIdList) {
        this.portfolioIdList = portfolioIdList;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getAcctId() {
        return acctId;
    }

    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCalId() {
        return calId;
    }

    public void setCalId(String calId) {
        this.calId = calId;
    }

    public List<String> getAcctTypeList() {
        return acctTypeList;
    }

    public void setAcctTypeList(List<String> acctTypeList) {
        this.acctTypeList = acctTypeList;
    }

    public List<String> getAcctTypes() {
        return acctTypes;
    }

    public void setAcctTypes(List<String> acctTypes) {
        this.acctTypes = acctTypes;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public List<PositionCashSecDto> getPtlList() {
        return ptlList;
    }

    public void setPtlList(List<PositionCashSecDto> ptlList) {
        this.ptlList = ptlList;
    }
}
