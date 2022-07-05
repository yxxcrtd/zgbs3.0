package com.joyintech.tams.exchange.common.db.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(value="历史指令质押券信息",description="历史指令质押券信息")
public class InstructionPledgeHistory implements Serializable, KeyMethodInterface<String>{

    @ApiModelProperty(required = false,name="id",value="指令质押券信息ID")
    @NotNull
    @Size(max = 64)
    @Id
    private String id;

    @ApiModelProperty(required = false,name="entrustId",value="委托代码")
    @NotNull
    @Size(max = 32)
    private String entrustId = "";

    @ApiModelProperty(required = false,name="finprodId",value="金融产品代码")
    @Size(max = 50)
    private String finprodId = "";

    @ApiModelProperty(required = false,name="finprodAbbr",value="金融产品简称")
    @Size(max = 200)
    private String finprodAbbr = "";

    @ApiModelProperty(required = false,name="tradeMarket",value="交易场所")
    @Size(max = 50)
    private String tradeMarket = "";

    @ApiModelProperty(required = false,name="finprodMarketId",value="市场代码")
    @Size(max = 20)
    private String finprodMarketId = "";

    @ApiModelProperty(required = false,name="pledgeFaceValue",value="质押面值")
    @Digits(integer = 30, fraction = 2)
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal pledgeFaceValue = new BigDecimal(0);

    @ApiModelProperty(required = false,name="pledgeNumber",value="质押数量")
    @Digits(integer = 30, fraction = 2)
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal pledgeNumber = new BigDecimal(0);

    @ApiModelProperty(required = false,name="usedPledgeNumber",value="已使用抵押券数量")
    @Digits(integer = 30, fraction = 2)
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal usedPledgeNumber = new BigDecimal(0);

    @ApiModelProperty(required = false,name="pledgeRatio",value="质押率")
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal pledgeRatio = new BigDecimal(0);

    @ApiModelProperty(required = false,name="pledgeAmt",value="质押金额")
    @Digits(integer = 30, fraction = 2)
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal pledgeAmt = new BigDecimal(0);

    @ApiModelProperty(required = false,name="oldFinprodId",value="原老金融产品代码")
    @Size(max = 50)
    private String oldFinprodId;

    @ApiModelProperty(required = false,name="oldFinprodAbbr",value="原金融产品简称")
    @Size(max = 200)
    private String oldFinprodAbbr;

    @ApiModelProperty(required = false,name="oldTradeMarket",value="原交易场所")
    @Size(max = 50)
    private String oldTradeMarket;

    @ApiModelProperty(required = false,name="oldFinprodMarketId",value="原市场代码")
    @Size(max = 20)
    private String oldFinprodMarketId;

    @ApiModelProperty(required = false,name="oldPledgeFaceValue",value="原质押面值")
    @Digits(integer = 30, fraction = 2)
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal oldPledgeFaceValue;

    @ApiModelProperty(required = false,name="oldPledgeNumber",value="原质押数量")
    @Digits(integer = 30, fraction = 2)
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal oldPledgeNumber;

    @ApiModelProperty(required = false,name="pledgeAmt",value="原质押率")
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal oldPledgeRatio;

    @ApiModelProperty(required = false,name="oldPledgeAmt",value="原质押金额")
    @Digits(integer = 30, fraction = 2)
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal oldPledgeAmt;

    @ApiModelProperty(required = false,name="count",value="数量")
    private int count;

    @ApiModelProperty(required = false,name="displaceDir",value="置换方向")
    private String displaceDir;

    @Id
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public BigDecimal getPledgeFaceValue() {
        return pledgeFaceValue;
    }

    public void setPledgeFaceValue(BigDecimal pledgeFaceValue) {
        this.pledgeFaceValue = pledgeFaceValue;
    }

    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    public BigDecimal getUsedPledgeNumber() {
        return usedPledgeNumber;
    }

    public void setUsedPledgeNumber(BigDecimal usedPledgeNumber) {
        this.usedPledgeNumber = usedPledgeNumber;
    }

    public BigDecimal getPledgeRatio() {
        return pledgeRatio;
    }

    public void setPledgeRatio(BigDecimal pledgeRatio) {
        this.pledgeRatio = pledgeRatio;
    }

    public BigDecimal getPledgeAmt() {
        return pledgeAmt;
    }

    public void setPledgeAmt(BigDecimal pledgeAmt) {
        this.pledgeAmt = pledgeAmt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getOldFinprodId() {
        return oldFinprodId;
    }

    public void setOldFinprodId(String oldFinprodId) {
        this.oldFinprodId = oldFinprodId;
    }

    public String getOldFinprodAbbr() {
        return oldFinprodAbbr;
    }

    public void setOldFinprodAbbr(String oldFinprodAbbr) {
        this.oldFinprodAbbr = oldFinprodAbbr;
    }

    public String getOldTradeMarket() {
        return oldTradeMarket;
    }

    public void setOldTradeMarket(String oldTradeMarket) {
        this.oldTradeMarket = oldTradeMarket;
    }

    public String getOldFinprodMarketId() {
        return oldFinprodMarketId;
    }

    public void setOldFinprodMarketId(String oldFinprodMarketId) {
        this.oldFinprodMarketId = oldFinprodMarketId;
    }

    public BigDecimal getOldPledgeFaceValue() {
        return oldPledgeFaceValue;
    }

    public void setOldPledgeFaceValue(BigDecimal oldPledgeFaceValue) {
        this.oldPledgeFaceValue = oldPledgeFaceValue;
    }

    public BigDecimal getOldPledgeNumber() {
        return oldPledgeNumber;
    }

    public void setOldPledgeNumber(BigDecimal oldPledgeNumber) {
        this.oldPledgeNumber = oldPledgeNumber;
    }

    public BigDecimal getOldPledgeRatio() {
        return oldPledgeRatio;
    }

    public void setOldPledgeRatio(BigDecimal oldPledgeRatio) {
        this.oldPledgeRatio = oldPledgeRatio;
    }

    public BigDecimal getOldPledgeAmt() {
        return oldPledgeAmt;
    }

    public void setOldPledgeAmt(BigDecimal oldPledgeAmt) {
        this.oldPledgeAmt = oldPledgeAmt;
    }

    public String getDisplaceDir() {
        return displaceDir;
    }

    public void setDisplaceDir(String displaceDir) {
        this.displaceDir = displaceDir;
    }
}
