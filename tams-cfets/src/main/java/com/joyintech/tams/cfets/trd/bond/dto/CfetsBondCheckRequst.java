package com.joyintech.tams.cfets.trd.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zznn
 * @date 2021/11/22
 */
@ApiModel(value="报价相关操作检查入参",description="报价相关操作检查入参,如撤回、复制、修改")
public class CfetsBondCheckRequst {

    @ApiModelProperty(name="quoteId",value="报价编号")
    private String quoteId;

    @ApiModelProperty(name="quoteRoleType",value="本方角色")
    private String quoteRoleType;

    @ApiModelProperty(name="tradeStatus",value="报价状态")
    private String tradeStatus;

    @ApiModelProperty(name="sendStatus",value="发送状态")
    private String sendStatus;

    @ApiModelProperty(name="validStatus",value="有效状态")
    private String validStatus;

    @ApiModelProperty(name="quoteType",value="报价类型")
    private String quoteType;


    @ApiModelProperty(name="operationType",value="操作类型",example = "复制:operationType=copy,修改:operationType = edit,撤回:operationType=recall,回复:operationType = answer,回退:operationType = doBack")
    private String operationType;

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuoteRoleType() {
        return quoteRoleType;
    }

    public void setQuoteRoleType(String quoteRoleType) {
        this.quoteRoleType = quoteRoleType;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}

