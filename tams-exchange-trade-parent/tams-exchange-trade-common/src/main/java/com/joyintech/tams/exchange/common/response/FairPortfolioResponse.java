package com.joyintech.tams.exchange.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.constant.joyin.DictCode;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @author lifangpeng
 * @date 2022/5/19 19:37
 */
public class FairPortfolioResponse extends Instruction {

    @JsonProperty("portfolioInfo")
    private PortfolioInfoResponse portfolioInfo;

    @JsonProperty("instructionEntrustPrice")
    private BigDecimal instructionEntrustPrice;

    @JsonProperty("instructionTotalAmt")
    private BigDecimal instructionTotalAmt;

    @JsonProperty("instructionTotalNum")
    private Long instructionTotalNum;

    @JsonProperty("instructionLeftAmt")
    private BigDecimal instructionLeftAmt;

    @JsonProperty("disRate")
    private BigDecimal disRate;

    @JsonProperty("instructionLeftNum")
    private Long instructionLeftNum;

    @JsonProperty("priceModeStr")
    private String priceModeStr;

    public PortfolioInfoResponse getPortfolioInfo() {
        return portfolioInfo;
    }

    public void setPortfolioInfo(PortfolioInfoResponse portfolioInfo) {
        this.portfolioInfo = portfolioInfo;
    }

    public BigDecimal getInstructionEntrustPrice() {
        return instructionEntrustPrice;
    }

    public void setInstructionEntrustPrice(BigDecimal instructionEntrustPrice) {
        this.instructionEntrustPrice = instructionEntrustPrice;
    }

    public BigDecimal getInstructionTotalAmt() {
        return instructionTotalAmt;
    }

    public void setInstructionTotalAmt(BigDecimal instructionTotalAmt) {
        this.instructionTotalAmt = instructionTotalAmt;
    }

    public Long getInstructionTotalNum() {
        return instructionTotalNum;
    }

    public void setInstructionTotalNum(Long instructionTotalNum) {
        this.instructionTotalNum = instructionTotalNum;
    }

    public BigDecimal getInstructionLeftAmt() {
        return instructionLeftAmt;
    }

    public void setInstructionLeftAmt(BigDecimal instructionLeftAmt) {
        this.instructionLeftAmt = instructionLeftAmt;
    }

    public BigDecimal getDisRate() {
        return disRate;
    }

    public void setDisRate(BigDecimal disRate) {
        this.disRate = disRate;
    }

    public Long getInstructionLeftNum() {
        return instructionLeftNum;
    }

    public void setInstructionLeftNum(Long instructionLeftNum) {
        this.instructionLeftNum = instructionLeftNum;
    }

    public String getPriceModeStr() {
        if (StringUtil.isEmpty(this.getPriceMode())) {
            return null;
        }
        return DictCodeListExt2.from(DictCode.价格模式, this.getPriceMode()).getCodeName();
    }

    public void setPriceModeStr(String priceModeStr) {
        this.priceModeStr = priceModeStr;
    }
}
