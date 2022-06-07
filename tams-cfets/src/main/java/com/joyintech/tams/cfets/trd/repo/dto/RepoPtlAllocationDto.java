package com.joyintech.tams.cfets.trd.repo.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author jiangpeng
 * @date 2021/12/9 10:54
 */
@ApiModel(value = "组合分配", description = "组合分配")
public class RepoPtlAllocationDto {

    @ApiModelProperty(name = "entrustId", value = "指令代码", example = "WTREPO202107081619")
    private String entrustId;

    @ApiModelProperty(name = "portfolioId", value = "组合代码", example = "jp001")
    private String portfolioId;

    @ApiModelProperty(name = "portfolioName", value = "组合名称", example = "江鹏产品001")
    private String portfolioName;

    @ApiModelProperty(name = "avaNum", value = "剩余指令额度(元)", example = "1000000")
    private BigDecimal avaNum;

    @ApiModelProperty(name = "avaNumold", value = "剩余指令额度(元)(备份)")
    private BigDecimal avaNumOld;

    @ApiModelProperty(name = "tradeAmt", value = "交易金额(元)", example = "200000")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name = "dealFeeAmt", value = "交易手续费(元)", example = "1200")
    private BigDecimal dealFeeAmt;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费(元)", example = "1300")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "mIntAmt", value = "回购利息(元)", example = "1300")
    private BigDecimal mIntAmt;

    @ApiModelProperty(name = "mtradeAmt", value = "到期结算金额(元)", example = "1300")
    private BigDecimal mtradeAmt;

    @ApiModelProperty(name = "shareAmt", value = "质押券总额(元)", example = "1300")
    private BigDecimal shareAmt;

    @ApiModelProperty(name = "convertAmt", value = "质押券折算总额(元)", example = "1300")
    private BigDecimal convertAmt;

    @ApiModelProperty(name = "priceModel", value = "价格模式", example = "PRM01")
    private String priceModel;

    @ApiModelProperty(name = "priceModelStr", value = "价格模式名称", example = "指定价格")
    private String priceModelStr;

    @ApiModelProperty(name = "price", value = "回购利率%", example = "2.1")
    private BigDecimal price;

    @ApiModelProperty(name = "secManageAcctId", value = "证券账户", example = "jpzqyhj001")
    private String secManageAcctId;

    @ApiModelProperty(name = "repoEntrustPledgeDtos", value = "质押券信息")
    private List<RepoEntrustPledgeDto> repoEntrustPledgeDtos;

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public BigDecimal getAvaNum() {
        return avaNum;
    }

    public void setAvaNum(BigDecimal avaNum) {
        this.avaNum = avaNum;
    }

    public BigDecimal getAvaNumOld() {
        return avaNumOld;
    }

    public void setAvaNumOld(BigDecimal avaNumOld) {
        this.avaNumOld = avaNumOld;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal shareAmt) {
        this.tradeAmt = shareAmt;
    }

    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    public BigDecimal getMtradeAmt() {
        return mtradeAmt;
    }

    public void setMtradeAmt(BigDecimal mtradeAmt) {
        this.mtradeAmt = mtradeAmt;
    }

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public BigDecimal getConvertAmt() {
        return convertAmt;
    }

    public void setConvertAmt(BigDecimal convertAmt) {
        this.convertAmt = convertAmt;
    }

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getPriceModelStr() {
        if (StringUtil.isEmpty(this.getPriceModel())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.价格模式, getPriceModel());
    }

    public void setPriceModelStr(String priceModelStr) {
        this.priceModelStr = priceModelStr;
    }

    public List<RepoEntrustPledgeDto> getRepoEntrustPledgeDtos() {
        return repoEntrustPledgeDtos;
    }

    public void setRepoEntrustPledgeDtos(List<RepoEntrustPledgeDto> repoEntrustPledgeDtos) {
        this.repoEntrustPledgeDtos = repoEntrustPledgeDtos;
    }
}

