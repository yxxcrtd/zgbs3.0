package com.joyintech.tams.cfets.trd.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 计算请求参数
 *
 * @author jiangpeng
 * @date 2021/12/08
 */
@ApiModel(value = "计算请求参数", description = "计算请求参数")
public class RepoCalRequest {

    @ApiModelProperty(required = true, name = "tradeDate", value = "交易日期", example = "2021-07-08")
    private Date tradeDate;

    @ApiModelProperty(required = true, name = "tradeAmt", value = "交易金额", example = "20000")
    private BigDecimal tradeAmt;

    @ApiModelProperty(required = true, name = "ps", value = "交易方向", example = "03")
    private String ps;

    @ApiModelProperty(required = true, name = "secManageAcctId", value = "证券账户代码", example = "jpzqyhj001")
    private String secManageAcctId;

    @ApiModelProperty(required = true, name = "pledgeNum", value = "质押券数量", example = "2")
    private int pledgeNum;

    @ApiModelProperty(required = true, name = "termType", value = "期限品种", example = "R007")
    private String termType;

    @ApiModelProperty(name = "settleSpreedCode", value = "清算速度", example = "T+0")
    private String settleSpreedCode;

    @ApiModelProperty(name = "repoTerm", value = "回购期限", example = "7")
    private int repoTerm;

    @ApiModelProperty(name = "price", value = "回购利率%", example = "7.02")
    private BigDecimal price;

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public int getPledgeNum() {
        return pledgeNum;
    }

    public void setPledgeNum(int pledgeNum) {
        this.pledgeNum = pledgeNum;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getSettleSpreedCode() {
        return settleSpreedCode;
    }

    public void setSettleSpreedCode(String settleSpreedCode) {
        this.settleSpreedCode = settleSpreedCode;
    }

    public int getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(int repoTerm) {
        this.repoTerm = repoTerm;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

