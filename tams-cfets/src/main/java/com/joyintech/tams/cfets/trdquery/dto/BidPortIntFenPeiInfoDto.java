package com.joyintech.tams.cfets.trdquery.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

//组合分配信息共通DTO
public class BidPortIntFenPeiInfoDto {

    //分配方式
    @ApiModelProperty(name="dipType",value="分配方式",example="A1")
    private String dipType;
    //指令代码
    @ApiModelProperty(name="entrustId",value="指令代码",example="A1")
    private String entrustId;

    //组合名称
    @ApiModelProperty(name="portfolioName",value="组合名称",example="A1")
    private String portfolioName;

  /*
    汇总投标券面总额(万元)
    理论中标券面总额 (万元)
    实际中标券面总额（万元）
    投标标位
            中标标位
    中标净价(元)
    应计利息(元)
    缴款金额(元)
    结算服务费(元)
    业务模式
            证券账户
    投资经理
*/
  //中标标位
    @ApiModelProperty(name="winPrice",value="中标标位",example="A1")
    private BigDecimal winPrice;

    @ApiModelProperty(name="winCprice",value="中标净价(元)",example="A1")
    private BigDecimal winCprice;

    //券面总额(万元)
    @ApiModelProperty(name="prinAmt",value="券面总额(万元)",example="A1")
    private BigDecimal prinAmt;
    //结算服务费（元）
    @ApiModelProperty(name="settleFeeAmt",value="结算服务费（元）",example="A1")
    private BigDecimal settleFeeAmt;
    //交易金额（元）
    @ApiModelProperty(name="tradeAmt",value="交易金额（元）",example="A1")
    private BigDecimal tradeAmt;
    //应计利息总额（元）
    @ApiModelProperty(name="intAmt",value="应计利息总额（元）",example="字符串1")
    private BigDecimal intAmt;
    //缴款金额（元）
    @ApiModelProperty(name="payAmt",value="缴款金额")
    private BigDecimal payAmt;
    //投标标位
    @ApiModelProperty(name="bidPrice",value="投标标位")
    private BigDecimal bidPrice;
    //业务模式
    @ApiModelProperty(name="invAim",value="业务模式")
    private String invAim;
    //证券账户
    @ApiModelProperty(name="invAim",value="证券账户")
    private String secManageAcctId;

    @ApiModelProperty(name="invAim",value="证券账户")
    private String secManageAcctIdStr;
    //投资经理
    @ApiModelProperty(name="invManagerId",value="投资经理")
    private String invManagerId;

    @ApiModelProperty(name="winPrinAmt",value="汇总投标券面总额(万元)",example="A1")
    private BigDecimal winPrinAmt;

    @ApiModelProperty(name="theWinPrinamt",value="理论中标券面总额 (万元)",example="A1")
    private BigDecimal theWinPrinamt;

    @ApiModelProperty(name="actWinPrinamt",value="实际中标券面总额（万元）",example="A1")
    private BigDecimal actWinPrinamt;

    @ApiModelProperty(name="feeAmt",value="总交易费用",example="A1")
    private BigDecimal feeAmt;

   /* 投标确认.分配方式
            系统自动采番
    金融产品交易.委托代码
    组合.组合名称
    金融产品交易.交易本金/10000
    金融产品交易.总交易费用
    金融产品交易.投资目的
    金融产品交易.证券账户代码
*/

    public String getDipType() {
        return dipType;
    }

    public void setDipType(String dipType) {
        this.dipType = dipType;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    public BigDecimal getSettleFeeAmt() {
        return settleFeeAmt;
    }

    public void setSettleFeeAmt(BigDecimal settleFeeAmt) {
        this.settleFeeAmt = settleFeeAmt;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getSecManageAcctIdStr() {
        return secManageAcctIdStr;
    }

    public void setSecManageAcctIdStr(String secManageAcctIdStr) {
        this.secManageAcctIdStr = secManageAcctIdStr;
    }

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }
}
