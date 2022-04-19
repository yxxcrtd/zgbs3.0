package com.joyintech.tams.cfets.trd.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 报价初始化请求参数
 *
 * @author jiangpeng
 * @date 2021/12/9 16:00
 */
@ApiModel(value = "报价初始化请求参数", description = "本币业务交易-质押式回购-报价")
public class QuotedPriceRequest {

    @ApiModelProperty( name = "entrustId", value = "指令代码", example = "WTREPO001")
    private String entrustId;

    @ApiModelProperty( name = "operatorType", value = "操作类型", example = "restore")
    private String operatorType;

    @ApiModelProperty( name = "quoteId", value = "交易编号", example = "JYREPO001")
    private String quoteId;

    @ApiModelProperty( name = "validStatus", value = "流程状态", example = "01")
    private String validStatus;

    @ApiModelProperty( name = "pageId", value = "页面ID", example = "IbuQuoteRepo003")
    private String pageId;

    @ApiModelProperty( name = "sendStatus", value = "发送状态", example = "01")
    private String sendStatus;

    @ApiModelProperty( name = "tradeStatus", value = "成交状态", example = "09")
    private String tradeStatus;

    @ApiModelProperty( name = "quotaPageModel", value = "页面模式,新增,复制", example = "new")
    private String quotaPageModel;

    @ApiModelProperty( name = "quoteRoleType", value = "本方角色", example = "02")
    private String quoteRoleType;

    @ApiModelProperty( name = "requests", value = "多选时传值")
    List<QuotedPriceRequest> requests;

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

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

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getQuotaPageModel() {
        return quotaPageModel;
    }

    public void setQuotaPageModel(String quotaPageModel) {
        this.quotaPageModel = quotaPageModel;
    }

    public String getQuoteRoleType() {
        return quoteRoleType;
    }

    public void setQuoteRoleType(String quoteRoleType) {
        this.quoteRoleType = quoteRoleType;
    }

    public List<QuotedPriceRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<QuotedPriceRequest> requests) {
        this.requests = requests;
    }
}