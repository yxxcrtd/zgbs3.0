package com.joyintech.tams.cfets.manualsettle.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 银行间手工交收
 * 银行间手工交收流水返回DTO
 * @author sjj
 * @version 1.0 2021年11月12日
 * @see CfetsManualSettleFlowRespond
 * @since 1.0
 */

@ApiModel(value="银行间手工交收流水返回参数",description="银行间手工交收流水返回参数")
public class CfetsManualSettleFlowRespond {

    @ApiModelProperty(name="seqNo",value="序号",example="1")
    private int seqNo;

    @ApiModelProperty(name="settleDate",value="交收日期",example="2021-01-01")
    private String settleDate;

    @ApiModelProperty(name="jiaoShouDate",value="交收时间",example="HH:SS")
    private String jiaoShouDate;

    @ApiModelProperty(name="finprodMarketId",value="资产简称",example="字符串1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="字符串1")
    private String finprodAbbr;

    @ApiModelProperty(name="settleNum",value="交收数量",example="A1")
    private BigDecimal settleNum;

    @ApiModelProperty(name="settleAmt",value="交收金额",example="A1")
    private BigDecimal settleAmt;

    @ApiModelProperty(name="operType",value="操作类型",example="字符串1")
    private String operType;

    @ApiModelProperty(name="createUser",value="操作员",example="字符串1")
    private String createUser;

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getJiaoShouDate() {
        return jiaoShouDate;
    }

    public void setJiaoShouDate(String jiaoShouDate) {
        this.jiaoShouDate = jiaoShouDate;
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

    public BigDecimal getSettleNum() {
        return settleNum;
    }

    public void setSettleNum(BigDecimal settleNum) {
        this.settleNum = settleNum;
    }

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    //操作类型
    public String getOperTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交收操作类型, this.getOperType());
    }
}
