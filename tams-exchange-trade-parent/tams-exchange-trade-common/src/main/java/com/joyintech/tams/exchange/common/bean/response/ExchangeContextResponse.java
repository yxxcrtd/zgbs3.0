package com.joyintech.tams.exchange.common.bean.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author swwan
 * @date 2022/1/8 - 15:10
 */
@ApiModel(value = "场内交易系统上下文", description = "场内交易系统上下文")
public class ExchangeContextResponse {

    @ApiModelProperty(value = "用户编号")
    private String userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "平台日")
    private Date platDate;

    @ApiModelProperty(value = "是否是上交所交易日 0：是；1：否")
    private String shTradingDay;

    @ApiModelProperty(value = "是否是深交所交易日 0：是；1：否")
    private String szTradingDay;

    @ApiModelProperty(value = "是否启动公平交易开关 01：开；02：关")
    private String fairMode;

    @ApiModelProperty(value = "未接收指令的公平处理模式 01：是；02：否")
    private String fairModeWithUnacceptInstruction;

    @ApiModelProperty(value = "委托价格不优于指令价格时的公平处理模式 01：禁止；02：剔除并继续")
    private String fairModeEntrustPriceBestThanInstruction;

    @ApiModelProperty(value = "闭市后是否禁止下场内交易指令 Y：禁止 N：不禁止")
    private String tradeTimeSwitch;

    @ApiModelProperty(value = "债券交易数量单位，值域为01-张；02-手")
    private String bondTradedUnit;

    @ApiModelProperty(value = "交易端指令列表自动刷新时间(单位毫秒)")
    private String trdInstructionRefreshTime;

    @ApiModelProperty(value = "交易端行情列表自动刷新时间(单位毫秒)")
    private String trdQuoteRefreshTime;

    @ApiModelProperty(value = "交易端委托列表自动刷新时间(单位毫秒)")
    private String trdEntrustRefreshTime;

    @ApiModelProperty(value = "非交易时间段是否禁止场内交易下单（Y：禁止 N：不禁止）")
    private String trdTradeTimeSwitch;

    @ApiModelProperty(value = "场内交易下单价格超过有效价格申报范围（01 禁止；02 仅提示；03 允许）")
    private String trdPriceScopeControl;

    @ApiModelProperty(value = "场内新债默认行情类型控制：01-匹配成交，02-协商成交，03-点击成交，04-询价成交，05-竞买成交")
    private String trdNewBondQuotationTypeControl;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getPlatDate() {
        return platDate;
    }

    public void setPlatDate(Date platDate) {
        this.platDate = platDate;
    }

    public String getShTradingDay() {
        return shTradingDay;
    }

    public void setShTradingDay(String shTradingDay) {
        this.shTradingDay = shTradingDay;
    }

    public String getSzTradingDay() {
        return szTradingDay;
    }

    public void setSzTradingDay(String szTradingDay) {
        this.szTradingDay = szTradingDay;
    }

    public String getFairMode() {
        return fairMode;
    }

    public void setFairMode(String fairMode) {
        this.fairMode = fairMode;
    }

    public String getFairModeWithUnacceptInstruction() {
        return fairModeWithUnacceptInstruction;
    }

    public void setFairModeWithUnacceptInstruction(String fairModeWithUnacceptInstruction) {
        this.fairModeWithUnacceptInstruction = fairModeWithUnacceptInstruction;
    }

    public String getFairModeEntrustPriceBestThanInstruction() {
        return fairModeEntrustPriceBestThanInstruction;
    }

    public void setFairModeEntrustPriceBestThanInstruction(String fairModeEntrustPriceBestThanInstruction) {
        this.fairModeEntrustPriceBestThanInstruction = fairModeEntrustPriceBestThanInstruction;
    }

    public String getTradeTimeSwitch() {
        return tradeTimeSwitch;
    }

    public void setTradeTimeSwitch(String tradeTimeSwitch) {
        this.tradeTimeSwitch = tradeTimeSwitch;
    }

    public String getBondTradedUnit() {
        return bondTradedUnit;
    }

    public void setBondTradedUnit(String bondTradedUnit) {
        this.bondTradedUnit = bondTradedUnit;
    }

    public String getTrdInstructionRefreshTime() {
        return trdInstructionRefreshTime;
    }

    public void setTrdInstructionRefreshTime(String trdInstructionRefreshTime) {
        this.trdInstructionRefreshTime = trdInstructionRefreshTime;
    }

    public String getTrdQuoteRefreshTime() {
        return trdQuoteRefreshTime;
    }

    public void setTrdQuoteRefreshTime(String trdQuoteRefreshTime) {
        this.trdQuoteRefreshTime = trdQuoteRefreshTime;
    }

    public String getTrdEntrustRefreshTime() {
        return trdEntrustRefreshTime;
    }

    public void setTrdEntrustRefreshTime(String trdEntrustRefreshTime) {
        this.trdEntrustRefreshTime = trdEntrustRefreshTime;
    }

    public String getTrdTradeTimeSwitch() {
        return trdTradeTimeSwitch;
    }

    public void setTrdTradeTimeSwitch(String trdTradeTimeSwitch) {
        this.trdTradeTimeSwitch = trdTradeTimeSwitch;
    }

    public String getTrdPriceScopeControl() {
        return trdPriceScopeControl;
    }

    public void setTrdPriceScopeControl(String trdPriceScopeControl) {
        this.trdPriceScopeControl = trdPriceScopeControl;
    }

    public String getTrdNewBondQuotationTypeControl() {
        return trdNewBondQuotationTypeControl;
    }

    public void setTrdNewBondQuotationTypeControl(String trdNewBondQuotationTypeControl) {
        this.trdNewBondQuotationTypeControl = trdNewBondQuotationTypeControl;
    }
}
