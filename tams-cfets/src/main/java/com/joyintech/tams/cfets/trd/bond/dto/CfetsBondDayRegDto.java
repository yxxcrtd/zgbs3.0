package com.joyintech.tams.cfets.trd.bond.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zznn
 * @date 2021/11/23
 */
@ApiModel(value="当日登记",description="当日登记")
public class CfetsBondDayRegDto {
    @ApiModelProperty(name="validStatus",value="有效状态")
    private String validStatus;

    @ApiModelProperty(name="validStatusStr",value="有效状态名称")
    private String validStatusStr;

    @ApiModelProperty(name="ps",value="交易方向")
    private String ps;

    @ApiModelProperty(name="psStr",value="本方方向名称")
    private String psStr;

    @ApiModelProperty(name="quoteType",value="报价方式")
    private String quoteType;

    @ApiModelProperty(name="quoteTypeStr",value="报价方式名称")
    private String quoteTypeStr;

    @ApiModelProperty(name="finprodId",value="资产代码")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodName",value="资产名称")
    private String finprodName;

    @ApiModelProperty(name="shareAmt",value="券面总额（万元）")
    private BigDecimal shareAmt;

    @ApiModelProperty(name="intAmt",value="应计利息总额")
    private BigDecimal intAmt;

    @ApiModelProperty(name="unitInt",value="应计利息")
    private BigDecimal unitInt;

    @ApiModelProperty(name="price",value="净价（元）")
    private BigDecimal price;


    @ApiModelProperty(name="matureYield",value="到期收益率（%）")
    private BigDecimal matureYield;

    @ApiModelProperty(name="exerYield",value="行权收益率（%）")
    private BigDecimal exerYield;

    @ApiModelProperty(name="ouPartyId",value="本方")
    private String ouPartyId;

    @ApiModelProperty(name="ouTraderId",value="本方交易员")
    private String ouTraderId;

    @ApiModelProperty(name="ouPartyName",value="本方名称")
    private String ouPartyName;

    @ApiModelProperty(name="ouTraderName",value="本方交易员名称")
    private String ouTraderName;

    @ApiModelProperty(name="cpPartyId",value="对手方")
    private String cpPartyId;

    @ApiModelProperty(name="cpTraderId",value="对手方交易员")
    private String cpTraderId;

    @ApiModelProperty(name="cpPartyName",value="对手方名称")
    private String cpPartyName;

    @ApiModelProperty(name="ouTraderName",value="对手方交易员名称")
    private String cpTraderName;

    @ApiModelProperty(name="settleSpeed",value="清算速度")
    private Long settleSpeed;

    @ApiModelProperty(name="ccy",value="币种")
    private String settCcy;

    @ApiModelProperty(name="ccyStr",value="币种")
    private String  settCcyStr;

    @ApiModelProperty(name="settleSpeedStr",value="清算速度")
    private String settleSpeedStr;

    @ApiModelProperty(name="settleMethod",value="结算方式")
    private String  settleMethod;

    @ApiModelProperty(name="settleMethodStr",value="结算方式名称")
    private String  settleMethodStr;

    @ApiModelProperty(name="settleType",value="清算类型名称")
    private String  settleType;

    @ApiModelProperty(name="settleTypeStr",value="清算类型名称")
    private String  settleTypeStr;

    @ApiModelProperty(required = false,name="tradeMarket",value="交易场所代码")
    private String tradeMarket;

    @ApiModelProperty(required = false,name="tradeMarketStr",value="交易场所名称")
    private String tradeMarketStr;

    @ApiModelProperty(name="actSetteDate",value="实际行权日")
    private Date actSetteDate;

    @ApiModelProperty(name="theoreticalExerciseDate",value="理论行权日")
    private Date theoreticalExerciseDate;

    @ApiModelProperty(name="settleDate",value="结算日期")
    private Date settleDate;

    @ApiModelProperty(name="tradeDate",value="登记日期")
    private Date tradeDate;

    @ApiModelProperty(name="quoteId",value="业务编号")
    private String quoteId;

    @ApiModelProperty(name="statisticalTimes",value="导出次数")
    private int statisticalTimes;

    @ApiModelProperty(name="assetType",value="债券分类")
    private String assetType;

    @ApiModelProperty(name="assetTypeStr",value="债券分类名称")
    private String assetTypeStr;

    @ApiModelProperty(name="exeId",value="成交编号")
    private String trdId;

    @ApiModelProperty(name="dealDate",value="成交日期")
    private Date dealDate;

    @ApiModelProperty(name="calMode",value="计算方式  锚定值")
    private String calMode;


    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getValidStatusStr() {
        if (StringUtils.equals(this.getValidStatus(), BussinessDictCodeList.有效状态_流程中.getValue())) {
            return BussinessDictCodeList.有效状态_流程中.getCodeName();
        } else if (StringUtils.equals(this.getValidStatus(), BussinessDictCodeList.有效状态_撤销.getValue())){
            return BussinessDictCodeList.有效状态_撤销.getCodeName();
        }else{
            return BussinessDictCodeList.有效状态_有效.getCodeName();
        }
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPsStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易方向, this.getPs());
    }

    public void setPsStr(String psStr) {
        this.psStr = psStr;
    }

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getMatureYield() {
        return matureYield;
    }

    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    public BigDecimal getExerYield() {
        return exerYield;
    }

    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    public Long getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(Long settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getSettCcy() {
        return settCcy;
    }

    public void setSettCcy(String settCcy) {
        this.settCcy = settCcy;
    }

    public String getSettCcyStr() {
        if(StringUtil.isEmpty(this.settCcyStr)){
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.币种, this.getSettCcy());
        }
        return this.settCcyStr;
    }

    public void setSettCcyStr(String settCcyStr) {
        this.settCcyStr = settCcyStr;
    }

    public String getSettleSpeedStr() {
        if(StringUtil.isEmpty(this.settleSpeedStr)){
            if (this.getSettleSpeed() != null && this.getSettleSpeed() == 0L) {
                return BussinessDictCodeList.清算速度_T加0.getCodeName();
            }
            else if (this.getSettleSpeed() != null && this.getSettleSpeed() == 1L) {
                return BussinessDictCodeList.清算速度_T加1.getCodeName();
            }
            else if (this.getSettleSpeed() != null && this.getSettleSpeed() == 2L) {
                return BussinessDictCodeList.清算速度_T加2.getCodeName();
            }
        }
        return this.settleSpeedStr;
    }

    public void setSettleSpeedStr(String settleSpeedStr) {
        this.settleSpeedStr = settleSpeedStr;
    }

    public String getOuPartyId() {
        return ouPartyId;
    }

    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId;
    }

    public String getOuTraderId() {
        return ouTraderId;
    }

    public void setOuTraderId(String ouTraderId) {
        this.ouTraderId = ouTraderId;
    }

    public String getOuPartyName() {
        return ouPartyName;
    }

    public void setOuPartyName(String ouPartyName) {
        this.ouPartyName = ouPartyName;
    }

    public String getOuTraderName() {
        return ouTraderName;
    }

    public void setOuTraderName(String ouTraderName) {
        this.ouTraderName = ouTraderName;
    }

    public String getCpPartyId() {
        return cpPartyId;
    }

    public void setCpPartyId(String cpPartyId) {
        this.cpPartyId = cpPartyId;
    }

    public String getCpTraderId() {
        return cpTraderId;
    }

    public void setCpTraderId(String cpTraderId) {
        this.cpTraderId = cpTraderId;
    }

    public String getCpPartyName() {
        return cpPartyName;
    }

    public void setCpPartyName(String cpPartyName) {
        this.cpPartyName = cpPartyName;
    }

    public String getCpTraderName() {
        return cpTraderName;
    }

    public void setCpTraderName(String cpTraderName) {
        this.cpTraderName = cpTraderName;
    }

    public void setValidStatusStr(String validStatusStr) {
        this.validStatusStr = validStatusStr;
    }

    public String getSettleMethod() {
        return settleMethod;
    }

    public void setSettleMethod(String settleMethod) {
        this.settleMethod = settleMethod;
    }

    public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    public Date getTheoreticalExerciseDate() {
        return theoreticalExerciseDate;
    }

    public void setTheoreticalExerciseDate(Date theoreticalExerciseDate) {
        this.theoreticalExerciseDate = theoreticalExerciseDate;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuoteTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.报价方式_登记, this.getQuoteType());
    }

    public void setQuoteTypeStr(String quoteTypeStr) {
        this.quoteTypeStr = quoteTypeStr;
    }

    public String getSettleMethodStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.结算方式, this.getSettleMethod());
    }

    public void setSettleMethodStr(String settleMethodStr) {
        this.settleMethodStr = settleMethodStr;
    }

    public String getSettleTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.清算类型, this.getSettleType());
    }

    public void setSettleTypeStr(String settleTypeStr) {
        this.settleTypeStr = settleTypeStr;
    }

    public String getTradeMarketStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, getTradeMarket());
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public int getStatisticalTimes() {
        return statisticalTimes;
    }

    public void setStatisticalTimes(int statisticalTimes) {
        this.statisticalTimes = statisticalTimes;
    }

    public Date getActSetteDate() {
        return actSetteDate;
    }

    public void setActSetteDate(Date actSetteDate) {
        this.actSetteDate = actSetteDate;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getTrdId() {
        return trdId;
    }

    public void setTrdId(String trdId) {
        this.trdId = trdId;
    }

    public Date getDealDate() {
        return dealDate;
    }

    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    public String getAssetTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产五级分类, getAssetType());
    }

    public void setAssetTypeStr(String assetTypeStr) {
        this.assetTypeStr = assetTypeStr;
    }

    public String getCalMode() {
        return calMode;
    }

    public void setCalMode(String calMode) {
        this.calMode = calMode;
    }
}

