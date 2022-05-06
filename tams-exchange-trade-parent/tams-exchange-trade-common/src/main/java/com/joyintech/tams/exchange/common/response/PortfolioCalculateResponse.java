package com.joyintech.tams.exchange.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.constant.joyin.DictCode;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@ApiModel(value = "组合计算", description = "组合计算")
public class PortfolioCalculateResponse {
    @JsonProperty("portfolioInfo")
    private PortfolioInfoResponse portfolioInfo;

    @JsonProperty("portfolioName")
    private String portfolioName;

    @JsonProperty("portfolioId")
    private String portfolioId;

    @JsonProperty("finprodName")
    private String finprodName;

    @JsonProperty("acctNo")
    private String acctNo;

    @JsonProperty("fullPrice")
    private BigDecimal fullPrice;

    @JsonProperty("priceMode")
    private String priceMode;

    @JsonProperty("priceModeStr")
    private String priceModeStr;

    @JsonProperty("instructionId")
    private String instructionId;

    @JsonProperty("finprodMarketId")
    private String finprodMarketId;

    @JsonProperty("entrustAmt")
    private BigDecimal entrustAmt;

    @JsonProperty("entrustPrice")
    private BigDecimal entrustPrice;

    @JsonProperty("entrustProp")
    private String entrustProp;

    @JsonProperty("instructionTotalAmt")
    private BigDecimal instructionTotalAmt;

    @JsonProperty("instructionTotalNum")
    private Long instructionTotalNum;

    @JsonProperty("instructionLeftAmt")
    private BigDecimal instructionLeftAmt;

    @JsonProperty("disRate")
    private BigDecimal disRate;

    @JsonProperty("entrustNum")
    private Long entrustNum;

    @JsonProperty("conNum")
    private Long conNum;

    @JsonProperty("instructionLeftNum")
    private Long instructionLeftNum;

    public PortfolioCalculateResponse portfolioInfo(PortfolioInfoResponse portfolioInfo) {
        this.portfolioInfo = portfolioInfo;
        return this;
    }

    /**
     * Get portfolioInfo
     *
     * @return portfolioInfo
     */
    @ApiModelProperty(value = "组合信息")
    public PortfolioInfoResponse getPortfolioInfo() {
        return portfolioInfo;
    }

    public void setPortfolioInfo(PortfolioInfoResponse portfolioInfo) {
        this.portfolioInfo = portfolioInfo;
    }

    public PortfolioCalculateResponse portfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
        return this;
    }

    /**
     * 组合名称
     *
     * @return portfolioName
     */
    @ApiModelProperty(value = "组合名称")
    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public PortfolioCalculateResponse portfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
        return this;
    }

    /**
     * 组合代码
     *
     * @return portfolioId
     */
    @ApiModelProperty(value = "组合代码")
    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public PortfolioCalculateResponse finprodName(String finprodName) {
        this.finprodName = finprodName;
        return this;
    }

    /**
     * 资产名称
     *
     * @return finprodName
     */
    @ApiModelProperty(value = "资产名称")
    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public PortfolioCalculateResponse acctNo(String acctNo) {
        this.acctNo = acctNo;
        return this;
    }

    /**
     * 资金账户
     *
     * @return acctNo
     */
    @ApiModelProperty(value = "资金账户")
    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public PortfolioCalculateResponse fullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
        return this;
    }

    /**
     * 全价
     *
     * @return fullPrice
     */
    @ApiModelProperty(value = "全价")
    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    public PortfolioCalculateResponse priceMode(String priceMode) {
        this.priceMode = priceMode;
        return this;
    }

    /**
     * 价格模式
     *
     * @return priceMode
     */
    @ApiModelProperty(value = "价格模式")
    public String getPriceMode() {
        return priceMode;
    }

    public void setPriceMode(String priceMode) {
        this.priceMode = priceMode;
    }

    /**
     * 价格模式含义
     *
     * @return priceModeStr
     */
    @ApiModelProperty(value = "价格模式含义")
    public String getPriceModeStr() {
        if (StringUtil.isEmpty(priceMode)) {
            return null;
        }
        return DictCodeListExt2.from(DictCode.价格模式, priceMode).getCodeName();
    }

    public void setPriceModeStr(String priceModeStr) {
        this.priceModeStr = priceModeStr;
    }

    public PortfolioCalculateResponse instructionId(String instructionId) {
        this.instructionId = instructionId;
        return this;
    }

    /**
     * 指令代码
     *
     * @return instructionId
     */
    @ApiModelProperty(value = "指令代码")
    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId;
    }

    public PortfolioCalculateResponse finprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
        return this;
    }

    /**
     * 资产代码
     *
     * @return finprodMarketId
     */
    @ApiModelProperty(value = "资产代码")
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public PortfolioCalculateResponse entrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
        return this;
    }

    /**
     * 委托金额
     *
     * @return entrustAmt
     */
    @ApiModelProperty(value = "委托金额")
    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    public PortfolioCalculateResponse entrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
        return this;
    }

    /**
     * 委托价格
     *
     * @return entrustPrice
     */
    @ApiModelProperty(value = "委托价格")
    public BigDecimal getEntrustPrice() {
        return entrustPrice;
    }

    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    public PortfolioCalculateResponse entrustProp(String entrustProp) {
        this.entrustProp = entrustProp;
        return this;
    }

    /**
     * 委托模式
     *
     * @return entrustProp
     */
    @ApiModelProperty(value = "委托模式")
    public String getEntrustProp() {
        return entrustProp;
    }

    public void setEntrustProp(String entrustProp) {
        this.entrustProp = entrustProp;
    }

    public PortfolioCalculateResponse instructionTotalAmt(BigDecimal instructionTotalAmt) {
        this.instructionTotalAmt = instructionTotalAmt;
        return this;
    }

    /**
     * 指令总金额
     *
     * @return instructionTotalAmt
     */
    @ApiModelProperty(value = "指令总金额")
    public BigDecimal getInstructionTotalAmt() {
        return instructionTotalAmt;
    }

    public void setInstructionTotalAmt(BigDecimal instructionTotalAmt) {
        this.instructionTotalAmt = instructionTotalAmt;
    }

    public PortfolioCalculateResponse instructionTotalNum(Long instructionTotalNum) {
        this.instructionTotalNum = instructionTotalNum;
        return this;
    }

    /**
     * 指令总数量
     *
     * @return instructionTotalNum
     */
    @ApiModelProperty(value = "指令总数量")
    public Long getInstructionTotalNum() {
        return instructionTotalNum;
    }

    public void setInstructionTotalNum(Long instructionTotalNum) {
        this.instructionTotalNum = instructionTotalNum;
    }

    public PortfolioCalculateResponse instructionLeftAmt(BigDecimal instructionLeftAmt) {
        this.instructionLeftAmt = instructionLeftAmt;
        return this;
    }

    /**
     * 指令剩余金额
     *
     * @return instructionLeftAmt
     */
    @ApiModelProperty(value = "指令剩余金额")
    public BigDecimal getInstructionLeftAmt() {
        return instructionLeftAmt;
    }

    public void setInstructionLeftAmt(BigDecimal instructionLeftAmt) {
        this.instructionLeftAmt = instructionLeftAmt;
    }

    public PortfolioCalculateResponse disRate(BigDecimal disRate) {
        this.disRate = disRate;
        return this;
    }

    /**
     * 分配比例
     *
     * @return disRate
     */
    @ApiModelProperty(value = "分配比例")
    public BigDecimal getDisRate() {
        return disRate;
    }

    public void setDisRate(BigDecimal disRate) {
        this.disRate = disRate;
    }

    public PortfolioCalculateResponse entrustNum(Long entrustNum) {
        this.entrustNum = entrustNum;
        return this;
    }

    /**
     * 委托数量
     *
     * @return entrustNum
     */
    @ApiModelProperty(value = "委托数量")
    public Long getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(Long entrustNum) {
        this.entrustNum = entrustNum;
    }

    public PortfolioCalculateResponse conNum(Long conNum) {
        this.conNum = conNum;
        return this;
    }

    /**
     * 转股数量
     *
     * @return conNum
     */
    @ApiModelProperty(value = "转股数量")
    public Long getConNum() {
        return conNum;
    }

    public void setConNum(Long conNum) {
        this.conNum = conNum;
    }

    public PortfolioCalculateResponse instructionLeftNum(Long instructionLeftNum) {
        this.instructionLeftNum = instructionLeftNum;
        return this;
    }

    /**
     * 指令剩余数量
     *
     * @return instructionLeftNum
     */
    @ApiModelProperty(value = "指令剩余数量")
    @NotNull


    public Long getInstructionLeftNum() {
        return instructionLeftNum;
    }

    public void setInstructionLeftNum(Long instructionLeftNum) {
        this.instructionLeftNum = instructionLeftNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PortfolioCalculateResponse that = (PortfolioCalculateResponse) o;
        return Objects.equals(portfolioInfo, that.portfolioInfo) && Objects.equals(portfolioName, that.portfolioName) && Objects.equals(portfolioId, that.portfolioId) && Objects.equals(finprodName, that.finprodName) && Objects.equals(acctNo, that.acctNo) && Objects.equals(fullPrice, that.fullPrice) && Objects.equals(priceMode, that.priceMode) && Objects.equals(instructionId, that.instructionId) && Objects.equals(finprodMarketId, that.finprodMarketId) && Objects.equals(entrustAmt, that.entrustAmt) && Objects.equals(entrustPrice, that.entrustPrice) && Objects.equals(entrustProp, that.entrustProp) && Objects.equals(instructionTotalAmt, that.instructionTotalAmt) && Objects.equals(instructionTotalNum, that.instructionTotalNum) && Objects.equals(instructionLeftAmt, that.instructionLeftAmt) && Objects.equals(disRate, that.disRate) && Objects.equals(entrustNum, that.entrustNum) && Objects.equals(conNum, that.conNum) && Objects.equals(instructionLeftNum, that.instructionLeftNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portfolioInfo, portfolioName, portfolioId, finprodName, acctNo, fullPrice, priceMode, instructionId, finprodMarketId, entrustAmt, entrustPrice, entrustProp, instructionTotalAmt, instructionTotalNum, instructionLeftAmt, disRate, entrustNum, conNum, instructionLeftNum);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PortfolioCalculateResponse {\n");

        sb.append("    portfolioInfo: ").append(toIndentedString(portfolioInfo)).append("\n");
        sb.append("    portfolioName: ").append(toIndentedString(portfolioName)).append("\n");
        sb.append("    portfolioId: ").append(toIndentedString(portfolioId)).append("\n");
        sb.append("    finprodName: ").append(toIndentedString(finprodName)).append("\n");
        sb.append("    acctNo: ").append(toIndentedString(acctNo)).append("\n");
        sb.append("    fullPrice: ").append(toIndentedString(fullPrice)).append("\n");
        sb.append("    priceMode: ").append(toIndentedString(priceMode)).append("\n");
        sb.append("    instructionId: ").append(toIndentedString(instructionId)).append("\n");
        sb.append("    finprodMarketId: ").append(toIndentedString(finprodMarketId)).append("\n");
        sb.append("    entrustAmt: ").append(toIndentedString(entrustAmt)).append("\n");
        sb.append("    entrustPrice: ").append(toIndentedString(entrustPrice)).append("\n");
        sb.append("    entrustProp: ").append(toIndentedString(entrustProp)).append("\n");
        sb.append("    instructionTotalAmt: ").append(toIndentedString(instructionTotalAmt)).append("\n");
        sb.append("    instructionTotalNum: ").append(toIndentedString(instructionTotalNum)).append("\n");
        sb.append("    instructionLeftAmt: ").append(toIndentedString(instructionLeftAmt)).append("\n");
        sb.append("    disRate: ").append(toIndentedString(disRate)).append("\n");
        sb.append("    entrustNum: ").append(toIndentedString(entrustNum)).append("\n");
        sb.append("    conNum: ").append(toIndentedString(conNum)).append("\n");
        sb.append("    instructionLeftNum: ").append(toIndentedString(instructionLeftNum)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}



