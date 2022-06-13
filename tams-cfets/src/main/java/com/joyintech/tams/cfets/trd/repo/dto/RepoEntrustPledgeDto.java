package com.joyintech.tams.cfets.trd.repo.dto;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @author jiangpeng
 * @date 2021年12月07日 16:25
 */
@ApiModel(value = "质押券信息", description = "本币业务交易-质押式回购-质押券信息")
public class RepoEntrustPledgeDto {
    @ApiModelProperty(required = false, name = "bondCode", value = "债券市场代码", example = "xxx.IB")
    private String bondCode;

    @ApiModelProperty(required = false, name = "bondAbbrName", value = "债券简称", example = "债券01")
    private String bondAbbrName;

    @ApiModelProperty(required = false, name = "finprodId", value = "债券代码", example = "F01_xxx.IB")
    private String finprodId;

    @ApiModelProperty(required = false, name = "gradeResult", value = "债券评级", example = "AAA")
    private String gradeResult;

    @ApiModelProperty(required = false, name = "period", value = "代偿期", example = "1.23")
    private BigDecimal period;

    @ApiModelProperty(required = false, name = "periodStr", value = "代偿期", example = "1.23Y")
    private String periodStr;

    @ApiModelProperty(required = false, name = "pledgeStockName", value = "债券库名称", example = "债券库01")
    private String pledgeStockName;

    @ApiModelProperty(required = false, name = "pledgeStockId", value = "债券库代码", example = "99999")
    private String pledgeStockId;

    @ApiModelProperty(required = false, name = "mdate", value = "债券到期日")
    private Date mdate;

    @ApiModelProperty(required = false, name = "mdateStr", value = "债券到期日")
    private String mdateStr;

    @ApiModelProperty(required = false, name = "avaPledgePrin", value = "可质押券面总额(元)")
    private BigDecimal avaPledgePrin;

    @ApiModelProperty(required = false, name = "avaPledgePrinStr", value = "可质押券面总额(万元)")
    private String avaPledgePrinStr;

    @ApiModelProperty(required = false, name = "pledgePrin", value = "质押券面额(元)")
    private BigDecimal pledgePrin;

    @ApiModelProperty(required = false, name = "pledgePrinStr", value = "质押券面额(万元)")
    private String pledgePrinStr;

    @ApiModelProperty(required = false, name = "firstNetPrice", value = "首次净价")
    private String firstNetPrice;

    @ApiModelProperty(required = false, name = "pledgeRatio", value = "折算比例%")
    private BigDecimal pledgeRatio;

    @ApiModelProperty(required = false, name = "prin", value = "质押券面总额(元)")
    private String prin;

    @ApiModelProperty(required = false, name = "repRegistOrg", value = "登记托管机构code")
    private String repRegistOrg;

    @ApiModelProperty(required = false, name = "repRegistOrgStr", value = "登记托管机构名称")
    private String repRegistOrgStr;

    @ApiModelProperty(required = false, name = "portfolioId", value = "组合代码")
    private String portfolioId;

    @ApiModelProperty(required = false, name = "portfolioName", value = "组合名称")
    private String portfolioName;

    @ApiModelProperty(required = false, name = "entrustId", value = "指令代码")
    private String entrustId;

    @ApiModelProperty(name = "convertAmt", value = "汇总质押券折算总额", example = "200000")
    private BigDecimal convertAmt;

    public String getBondCode() {
        return bondCode;
    }

    public void setBondCode(String bondCode) {
        this.bondCode = bondCode;
    }

    public String getBondAbbrName() {
        return bondAbbrName;
    }

    public void setBondAbbrName(String bondAbbrName) {
        this.bondAbbrName = bondAbbrName;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getGradeResult() {
        return gradeResult;
    }

    public void setGradeResult(String gradeResult) {
        this.gradeResult = gradeResult;
    }

    public BigDecimal getPeriod() {
        return period;
    }

    public void setPeriod(BigDecimal period) {
        this.period = period;
    }

    public String getPledgeStockName() {
        return pledgeStockName;
    }

    public void setPledgeStockName(String pledgeStockName) {
        this.pledgeStockName = pledgeStockName;
    }

    public String getPledgeStockId() {
        return pledgeStockId;
    }

    public void setPledgeStockId(String pledgeStockId) {
        this.pledgeStockId = pledgeStockId;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getMdateStr() {
        return DateUtil.format(getMdate());
    }

    public void setMdateStr(String mdateStr) {
        this.mdateStr = mdateStr;
    }

    public BigDecimal getAvaPledgePrin() {
        return avaPledgePrin;
    }

    public void setAvaPledgePrin(BigDecimal avaPledgePrin) {
        this.avaPledgePrin = avaPledgePrin;
    }

    public BigDecimal getPledgePrin() {
        return pledgePrin;
    }

    public void setPledgePrin(BigDecimal pledgePrin) {
        this.pledgePrinStr = null;
        this.pledgePrin = pledgePrin;
    }

    public String getAvaPledgePrinStr() {
        return ArithmeticUtil.divide(this.getAvaPledgePrin(), new BigDecimal(10000), 2, RoundingMode.HALF_UP).toString();
    }

    public void setAvaPledgePrinStr(String avaPledgePrinStr) {
        this.avaPledgePrinStr = avaPledgePrinStr;
    }

    public String getPledgePrinStr() {
        if (StringUtil.isNotEmpty(pledgePrinStr)) {
            return pledgePrinStr;
        }
        return ArithmeticUtil.divide(this.getPledgePrin(), new BigDecimal(10000), 2, RoundingMode.HALF_UP).toString();
    }

    public void setPledgePrinStr(String pledgePrinStr) {
        this.pledgePrinStr = pledgePrinStr;
    }

    public String getFirstNetPrice() {
        return firstNetPrice;
    }

    public void setFirstNetPrice(String firstNetPrice) {
        this.firstNetPrice = firstNetPrice;
    }

    public BigDecimal getPledgeRatio() {
        return pledgeRatio;
    }

    public void setPledgeRatio(BigDecimal pledgeRatio) {
        this.pledgeRatio = pledgeRatio;
    }

    public String getPrin() {
        return prin;
    }

    public void setPrin(String prin) {
        this.prin = prin;
    }

    public String getRepRegistOrg() {
        return repRegistOrg;
    }

    public void setRepRegistOrg(String repRegistOrg) {
        this.repRegistOrg = repRegistOrg;
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

    public void setRepRegistOrgStr(String repRegistOrgStr) {
        this.repRegistOrgStr = repRegistOrgStr;
    }

    public String getRepRegistOrgStr() {
        if (getRepRegistOrg() != null) {
            return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构_中债, this.getRepRegistOrg());
        } else {
            return null;
        }
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public BigDecimal getConvertAmt() {
        if (!StringUtil.isNotEmpty(this.convertAmt)
                && StringUtil.isNotEmpty(this.getPledgePrin()) && StringUtil.isNotEmpty(this.getPledgeRatio())) {
            return ArithmeticUtil.multiply(this.getPledgePrin(), ArithmeticUtil.divide(this.getPledgeRatio(), new BigDecimal(100), 6));
        }
        return convertAmt;
    }

    public void setConvertAmt(BigDecimal convertAmt) {
        this.convertAmt = convertAmt;
    }

    public String getPeriodStr() {
        return periodStr;
    }

    public void setPeriodStr(String periodStr) {
        this.periodStr = periodStr;
    }
}
