package com.joyintech.tams.exchange.common.bean;

import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.db.domain.BatchInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(value = "普通下单请求报文", description = "普通下单请求报文")
public class OrderPlaceBean extends BaseOrderRequest implements Serializable {
    private static final long serialVersionUID = 5892155368791512437L;

//    @ApiModelProperty(required = true, name = "instructionId", value = "指令编号", example = "W000111")
//    private String instructionId;

    @ApiModelProperty(required = true, name = "portfolioId", value = "组合代码", example = "Z000111")
    private String portfolioId;

    @ApiModelProperty(required = true, name = "userId", value = "操作人ID", example = "U001")
    private String userId;

    @ApiModelProperty(required = true, name = "username", value = "操作人名称", example = "张三")
    private String username;

    @ApiModelProperty(required = true, name = "accountId", value = "网点账号", example = "A0001:12345")
    private String accountId;

    @ApiModelProperty(required = false, name = "algorithmId", value = "算法单ID", example = "S2222222")
    private String algorithmId;

    @ApiModelProperty(required = false, name = "bsFlag", value = "买卖类别", example = "0")
    private String bsFlag;

    @ApiModelProperty(required = true, name = "stockCode", value = "证券代码", example = "000001.SZ")
    private String stockCode;

//    @ApiModelProperty(required = false, name = "entrustProp", value = "委托属性", example = "0")
//    private String entrustProp;

    @ApiModelProperty(required = true, name = "entrustBs", value = "交易方向", example = "1")
    private String entrustBs;

//    @ApiModelProperty(required = false, name = "entrustVolume", value = "委托数量", example = "1000")
//    private long entrustVolume;

//    @ApiModelProperty(required = false, name = "entrustPrice", value = "净价价格", example = "2.34")
//    private BigDecimal entrustPrice;

//    @ApiModelProperty(required = false, name = "fullPrice", value = "全价价格", example = "2.34")
//    private BigDecimal fullPrice;

    @ApiModelProperty(required = false, name = "yield", value = "收益率", example = "2.34")
    private BigDecimal yield;

    @ApiModelProperty(required = false, name = "batchInfo", value = "批量下单信息")
    private BatchInfo batchInfo;

    @ApiModelProperty(required = false, name = "offsetFlag", value = "冲正标识", example = "0")
    private String offsetFlag;

    @ApiModelProperty(required = false, name = "hedgeFlag", value = "套期保值标志", example = "1")
    private String hedgeFlag;
    
//    @ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
//    private String softSkipMode;

//    public String getSoftSkipMode() {
//		return softSkipMode;
//	}
//
//	public void setSoftSkipMode(String softSkipMode) {
//		this.softSkipMode = softSkipMode;
//	}
//
//	public String getInstructionId() {
//        return instructionId;
//    }
//
//    public void setInstructionId(String instructionId) {
//        this.instructionId = instructionId;
//    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(String algorithmId) {
        this.algorithmId = algorithmId;
    }

    public String getBsFlag() {
        return bsFlag;
    }

    public void setBsFlag(String bsFlag) {
        this.bsFlag = bsFlag;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

//    public long getEntrustVolume() {
//        return entrustVolume;
//    }
//
//    public void setEntrustVolume(long entrustVolume) {
//        this.entrustVolume = entrustVolume;
//    }
//
//    public BigDecimal getEntrustPrice() {
//        return entrustPrice;
//    }
//
//    public void setEntrustPrice(BigDecimal entrustPrice) {
//        this.entrustPrice = entrustPrice;
//    }
//
//    public BigDecimal getFullPrice() {
//        return fullPrice;
//    }
//
//    public void setFullPrice(BigDecimal fullPrice) {
//        this.fullPrice = fullPrice;
//    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

//    public String getEntrustProp() {
//        return entrustProp;
//    }
//
//    public void setEntrustProp(String entrustProp) {
//        this.entrustProp = entrustProp;
//    }

    public String getEntrustBs() {
        return entrustBs;
    }

    public void setEntrustBs(String entrustBs) {
        this.entrustBs = entrustBs;
    }

    public BatchInfo getBatchInfo() {
        return batchInfo;
    }

    public void setBatchInfo(BatchInfo batchInfo) {
        this.batchInfo = batchInfo;
    }

    public String getOffsetFlag() {
        return offsetFlag;
    }

    public void setOffsetFlag(String offsetFlag) {
        this.offsetFlag = offsetFlag;
    }

    public String getHedgeFlag() {
        return hedgeFlag;
    }

    public void setHedgeFlag(String hedgeFlag) {
        this.hedgeFlag = hedgeFlag;
    }

    @Override
    public String toString() {
        return "OrderPlaceBean{" +
                "instructionId='" + instructionId + '\'' +
                ", portfolioId='" + portfolioId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", accountId='" + accountId + '\'' +
                ", algorithmId='" + algorithmId + '\'' +
                ", bsFlag='" + bsFlag + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", entrustProp='" + entrustProp + '\'' +
                ", entrustBs='" + entrustBs + '\'' +
                ", entrustVolume=" + entrustVolume +
                ", entrustPrice=" + entrustPrice +
                ", yield=" + yield +
                ", fullPrice=" + fullPrice +
                ", batchInfo=" + batchInfo +
                ", offsetFlag='" + offsetFlag + '\'' +
                ", hedgeFlag='" + hedgeFlag + '\'' +
                '}';
    }
}
