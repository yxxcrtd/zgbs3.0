package com.joyintech.tams.invest.common.position.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.FinDealPledge;

import java.util.List;

public class PositionBaseData extends BaseEntity {

    /**
     * 首期交易信息
     */
    private FinTradeProductDto finTradeProductDto;

    /**
     * 指令信息
     */
    private EtrEntrust etrEntrust;

    /**
     * 指令附属信息
     */
    private EtrEntrustAddDto etrEntrustAddDto;

    /**
     * 质押券信息
     * @return
     */
    // private FinDealPledge finDealPledge;
    private List<FinDealPledge> finDealPledgeList;
    public FinTradeProductDto getFinTradeProductDto() {
        return finTradeProductDto;
    }

    public void setFinTradeProductDto(FinTradeProductDto finTradeProductDto) {
        this.finTradeProductDto = finTradeProductDto;
    }

    public EtrEntrust getEtrEntrust() {
        return etrEntrust;
    }

    public void setEtrEntrust(EtrEntrust etrEntrust) {
        this.etrEntrust = etrEntrust;
    }

    public EtrEntrustAddDto getEtrEntrustAddDto() {
        return etrEntrustAddDto;
    }

    public void setEtrEntrustAddDto(EtrEntrustAddDto etrEntrustAddDto) {
        this.etrEntrustAddDto = etrEntrustAddDto;
    }

//    public FinDealPledge getFinDealPledge() {
//        return finDealPledge;
//    }
//
//    public void setFinDealPledge(FinDealPledge finDealPledge) {
//        this.finDealPledge = finDealPledge;
//    }

    public List<FinDealPledge> getFinDealPledgeList() {
        return finDealPledgeList;
    }

    public void setFinDealPledgeList(List<FinDealPledge> finDealPledgeList) {
        this.finDealPledgeList = finDealPledgeList;
    }
}
