package com.joyintech.tams.cfets.manualsettle.dto;

import com.joyintech.tams.common.base.entity.TrdBankManualSetDetail;
import com.joyintech.tams.common.base.entity.TrdBankManualSettleFlow;
import com.joyintech.tams.common.base.entity.TrdBankManualSettleInfo;

import java.util.List;

public class CfetsManualSettleInsertDto {

    private TrdBankManualSettleInfo trdBankManualSettleInfo;

    private TrdBankManualSettleFlow trdBankManualSettleFlow;

    private List<TrdBankManualSetDetail> listTrdBnakSetDetails;

    //币种
    private String ccy;

    //交易方向
    private String ps;

    //原交易编号
    private String tradeId;


    public TrdBankManualSettleInfo getTrdBankManualSettleInfo() {
        return trdBankManualSettleInfo;
    }

    public void setTrdBankManualSettleInfo(TrdBankManualSettleInfo trdBankManualSettleInfo) {
        this.trdBankManualSettleInfo = trdBankManualSettleInfo;
    }

    public TrdBankManualSettleFlow getTrdBankManualSettleFlow() {
        return trdBankManualSettleFlow;
    }

    public void setTrdBankManualSettleFlow(TrdBankManualSettleFlow trdBankManualSettleFlow) {
        this.trdBankManualSettleFlow = trdBankManualSettleFlow;
    }

    public List<TrdBankManualSetDetail> getListTrdBnakSetDetails() {
        return listTrdBnakSetDetails;
    }

    public void setListTrdBnakSetDetails(List<TrdBankManualSetDetail> listTrdBnakSetDetails) {
        this.listTrdBnakSetDetails = listTrdBnakSetDetails;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }
}
