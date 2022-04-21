package com.joyintech.tams.exchange.common.bean.request;

import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author swwan
 * @date 2022/4/21 - 12:09
 */
@ApiModel(value="交易商信息查询报文",description="交易商信息查询报文")
public class TradeInfoProRequest {

    @ApiModelProperty(value="资金账户",example="A0001")
    private String acctNo;

    @NotBlank(message = "交易场所不可为空")
    @ApiModelProperty(value="交易场所,03|04",example="03")
    private String tradePlace;

    @ApiModelProperty(value="本方标识,Y|N",example="Y")
    private String isOur;

    @ApiModelProperty(value="交易员代码",example="zxjt001-zt-001")
    private String traderNo;

    @ApiModelProperty(value="交易员名称",example="交易员1号")
    private String traderName;

    @ApiModelProperty(value="交易主体名称",example="zxjt001-zt")
    private String dealerPartyNo;

    @ApiModelProperty(value="交易主体名称",example="中信建投交易主体")
    private String dealerPartyName;

    @ApiModelProperty(value="交易商代码",example="zxjt001")
    private String dealerNo;

    @ApiModelProperty(value="交易商名称",example="中信建投")
    private String dealerName;

    public String getIsOur() {
        return isOur;
    }

    public void setIsOur(String isOur) {
        this.isOur = isOur;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    public String getDealerNo() {
        return dealerNo;
    }

    public void setDealerNo(String dealerNo) {
        this.dealerNo = dealerNo;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getTraderNo() {
        return traderNo;
    }

    public void setTraderNo(String traderNo) {
        this.traderNo = traderNo;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public String getDealerPartyNo() {
        return dealerPartyNo;
    }

    public void setDealerPartyNo(String dealerPartyNo) {
        this.dealerPartyNo = dealerPartyNo;
    }

    public String getDealerPartyName() {
        return dealerPartyName;
    }

    public void setDealerPartyName(String dealerPartyName) {
        this.dealerPartyName = dealerPartyName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("acctNo", acctNo)
                .add("tradePlace", tradePlace)
                .add("isOur", isOur)
                .add("traderNo", traderNo)
                .add("traderName", traderName)
                .add("dealerPartyNo", dealerPartyNo)
                .add("dealerPartyName", dealerPartyName)
                .add("dealerNo", dealerNo)
                .add("dealerName", dealerName)
                .toString();
    }
}
