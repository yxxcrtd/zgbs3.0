/*
 * 文件名：ResetBaseRuleInfoReaderResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.batch.cfetssettle.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.tams.common.base.entity.TrdBankManualSetDetail;
import com.joyintech.tams.common.base.entity.TrdBankManualSettleFlow;
import com.joyintech.tams.common.base.entity.TrdBankManualSettleInfo;
import com.joyintech.tams.invest.batch.resetbaseruleinfo.dto.ResetBaseRuleInfoDto;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 *  reader结果
 * 〈功能详细描述〉
 * @author sjj
 * @version 1.0 2021年11月19日
 * @see CfetsSettleReaderResult
 * @since 1.0
 */
public class CfetsSettleReaderResult extends NeedOperateEntity{


    private TrdBankManualSettleInfo trdBankManualSettleInfo;

    private TrdBankManualSettleFlow trdBankManualSettleFlow;

    private List<TrdBankManualSetDetail> listTrdBnakSetDetails;

    //投管业务类型
    private String tgBusiType;

    //币种
    private String ccy;

    //交易方向
    private String ps;

    //资产类型
    private String finprodType2;
    //业务类型
    private String busiType;
    //汇总交易编号
    private String totalTradeId;
    //操作类型，交收/回溯
    private String buttonType;
    //交易编号
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

    public String getTgBusiType() {
        return tgBusiType;
    }

    public void setTgBusiType(String tgBusiType) {
        this.tgBusiType = tgBusiType;
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

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getTotalTradeId() {
        return totalTradeId;
    }

    public void setTotalTradeId(String totalTradeId) {
        this.totalTradeId = totalTradeId;
    }

    public String getButtonType() {
        return buttonType;
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    @Override
    public String getTradeId() {
        return tradeId;
    }

    @Override
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }
}
