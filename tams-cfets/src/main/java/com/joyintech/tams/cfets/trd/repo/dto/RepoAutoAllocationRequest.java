package com.joyintech.tams.cfets.trd.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 自动分配请求参数
 *
 * @author jiangpeng
 * @date 2021/12/9 10:04
 */
@ApiModel(value = "自动分配请求参数", description = "自动分配请求参数")
public class RepoAutoAllocationRequest {

    @ApiModelProperty(required = true, name = "distribution", value = "分配方式", example = "139")
    private String distribution;

    @ApiModelProperty(required = true, name = "ps", value = "交易方向", example = "03")
    private String ps;

    @ApiModelProperty(required = true, name = "tradeAmt", value = "交易金额(万元)", example = "200")
    private BigDecimal tradeAmt;

    @ApiModelProperty(required = true, name = "shareAmt", value = "质押券总额(元)", example = "1800000")
    private BigDecimal shareAmt;

    @ApiModelProperty(required = true, name = "convertAmt", value = "质押券折算总额(元)", example = "1800000")
    private BigDecimal convertAmt;

    @ApiModelProperty(name = "dealFeeAmt", value = "交易手续费(元)", example = "1200")
    private BigDecimal dealFeeAmt;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费(元)", example = "1200")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "mIntAmt", value = "回购利息", example = "230.2")
    private BigDecimal mIntAmt;

    @ApiModelProperty(name = "mtradeAmt", value = "到期结算金额(元)", example = "10001")
    private BigDecimal mtradeAmt;

    @ApiModelProperty(name = "secManageAcctId", value = "证券账户")
    private String secManageAcctId;

    @ApiModelProperty(name = "termType", value = "期限品种")
    private String termType;

    @ApiModelProperty(name = "delEtrId", value = "删除指令ID", example = "WT001")
    private String delEtrId;

    @ApiModelProperty(name = "entrustIdList", value = "指令代码List", example = "WT001")
    private List<String> entrustIdList;

    @ApiModelProperty(required = true, name = "ptlAllocations", value = "组合分配列表")
    private List<RepoPtlAllocationDto> ptlAllocations;

    @ApiModelProperty(name = "repoEntrustPledgeDtos", value = "质押券信息")
    private List<RepoEntrustPledgeDto> repoEntrustPledgeDtos;

    @ApiModelProperty(name = "settleSpreedCode", value = "清算速度", example = "T+0")
    private String settleSpreedCode;

    @ApiModelProperty(name = "repoTerm", value = "回购期限", example = "7")
    private int repoTerm;

    @ApiModelProperty(name = "price", value = "回购利率%", example = "7.02")
    private BigDecimal price;

    @ApiModelProperty(name = "settlementEndDate", value = "到期结算日", example = "2021-07-15")
    private Date settlementEndDate;

    @ApiModelProperty(name = "quotaPageModel", value = "页面模式", example = "回复:dialogueResponse,新增:new,修改:edit")
    private String quotaPageModel;

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
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

    public List<RepoPtlAllocationDto> getPtlAllocations() {
        return ptlAllocations;
    }

    public void setPtlAllocations(List<RepoPtlAllocationDto> ptlAllocations) {
        this.ptlAllocations = ptlAllocations;
    }

    public BigDecimal getConvertAmt() {
        return convertAmt;
    }

    public void setConvertAmt(BigDecimal convertAmt) {
        this.convertAmt = convertAmt;
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

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public List<RepoEntrustPledgeDto> getRepoEntrustPledgeDtos() {
        return repoEntrustPledgeDtos;
    }

    public void setRepoEntrustPledgeDtos(List<RepoEntrustPledgeDto> repoEntrustPledgeDtos) {
        this.repoEntrustPledgeDtos = repoEntrustPledgeDtos;
    }

    public String getDelEtrId() {
        return delEtrId;
    }

    public void setDelEtrId(String delEtrId) {
        this.delEtrId = delEtrId;
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

    public List<String> getEntrustIdList() {
        return entrustIdList;
    }

    public void setEntrustIdList(List<String> entrustIdList) {
        this.entrustIdList = entrustIdList;
    }

    public Date getSettlementEndDate() {
        return settlementEndDate;
    }

    public void setSettlementEndDate(Date settlementEndDate) {
        this.settlementEndDate = settlementEndDate;
    }

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public String getQuotaPageModel() {
        return quotaPageModel;
    }

    public void setQuotaPageModel(String quotaPageModel) {
        this.quotaPageModel = quotaPageModel;
    }
}

