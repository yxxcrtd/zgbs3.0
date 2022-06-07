package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto;

import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.util.SysDataUtil;
import io.swagger.annotations.ApiModelProperty;

public class TraderInfoDto {

    private String seqNo;
    private String dealerPartyNo;

    private String dealerPartyType;

    private String dealerPartyName;

    private String trader;

    private String traderNo;

    private String isOur;

    public String getDealerPartyNo() {
        return dealerPartyNo;
    }

    public void setDealerPartyNo(String dealerPartyNo) {
        this.dealerPartyNo = dealerPartyNo;
    }

    public String getDealerPartyType() {
        return dealerPartyType;
    }

    public void setDealerPartyType(String dealerPartyType) {
        this.dealerPartyType = dealerPartyType;
    }

    public String getDealerPartyName() {
        return dealerPartyName;
    }

    public void setDealerPartyName(String dealerPartyName) {
        this.dealerPartyName = dealerPartyName;
    }

    @Length(fieldName = "交易员名称", max = 15, groups = {ValidateGroupEdit.class})
    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getTraderNo() {
        return traderNo;
    }

    public void setTraderNo(String traderNo) {
        this.traderNo = traderNo;
    }

    public String getIsOur() {
        return isOur;
    }

    public void setIsOur(String isOur) {
        this.isOur = isOur;
    }
    public String getDealerPartyTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易主体类型, this.getDealerPartyType());
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }
}
