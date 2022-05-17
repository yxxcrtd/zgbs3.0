package com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto;


import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TraderInfoDto;
import com.joyintech.tams.invest.common.util.SysDataUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value="信用保护凭证行情信息返回参数",description="信用保护凭证行情信息返回参数")
public class MskCreditInfoProResponse {

    @ApiModelProperty(name="secid",value="凭证代码",example="A1", required = true)
    private String secid;

    @ApiModelProperty(name="cdate",value="估值日",example="A1", required = true)
    private String cdate;

    @ApiModelProperty(name="mdprice",value="估值全价",example="A1", required = true)
    private String mdprice;

    @ApiModelProperty(name="mcprice",value="估值净价",example="A1", required = true)
    private String mcprice;

    @ApiModelProperty(name="mint",value="估值利息",example="A1", required = true)
    private String mint;

    @ApiModelProperty(name="myield",value="估值收益率",example="A1", required = true)
    private String myield;

    @ApiModelProperty(name="mMduration",value="估价修正久期",example="A1", required = true)
    private String mMduration;

    @ApiModelProperty(name="mCnvxty",value="市价凸性",example="A1", required = true)
    private String mCnvxty;

    @ApiModelProperty(name="issueMarketNum",value="发行市场数",example="A1", required = true)
    private String issueMarketNum;

    @ApiModelProperty(name="creditType",value="信用类型",example="A1", required = true)
    private String creditType;

    @ApiModelProperty(name="valueSource",value="估值来源",example="A1", required = true)
    private String valueSource;

    @ApiModelProperty(name="cdateStart",value="起息日开始日",example="2021-01-01")
    private String cdateStart;

    @ApiModelProperty(name="cdateEnd",value="起息日结束日",example="2021-01-01")
    private String cdateEnd;

    public String getCdateStart() {
        return cdateStart;
    }

    public void setCdateStart(String cdateStart) {
        this.cdateStart = cdateStart;
    }

    public String getCdateEnd() {
        return cdateEnd;
    }

    public void setCdateEnd(String cdateEnd) {
        this.cdateEnd = cdateEnd;
    }

    public String getSecid() {
        return secid;
    }

    public void setSecid(String secid) {
        this.secid = secid;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getMdprice() {
        return mdprice;
    }

    public void setMdprice(String mdprice) {
        this.mdprice = mdprice;
    }

    public String getMcprice() {
        return mcprice;
    }

    public void setMcprice(String mcprice) {
        this.mcprice = mcprice;
    }

    public String getMint() {
        return mint;
    }

    public void setMint(String mint) {
        this.mint = mint;
    }

    public String getMyield() {
        return myield;
    }

    public void setMyield(String myield) {
        this.myield = myield;
    }

    public String getmMduration() {
        return mMduration;
    }

    public void setmMduration(String mMduration) {
        this.mMduration = mMduration;
    }

    public String getmCnvxty() {
        return mCnvxty;
    }

    public void setmCnvxty(String mCnvxty) {
        this.mCnvxty = mCnvxty;
    }

    public String getIssueMarketNum() {
        return issueMarketNum;
    }

    public void setIssueMarketNum(String issueMarketNum) {
        this.issueMarketNum = issueMarketNum;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public String getValueSource() {
        return valueSource;
    }

    public void setValueSource(String valueSource) {
        this.valueSource = valueSource;
    }

}
