package com.joyintech.tams.settle.batch.datagather.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.tams.common.base.entity.SettleTransDataPledge;
import com.joyintech.tams.common.base.entity.SettleTransDataTotal;

import java.util.List;

/**
 * @author 何阳阳
 * @date 2020/7/16
 * @see InvestTradeReaderDto
 * @since 1.0
 */
public class InvestTradeReaderDto extends NeedOperateEntity {

    /**
     * fasle表示调用自动匹配页面接口  true表示存续期  批处理处理交易数据
     */
    private boolean createTrade;
    /**
     * 成交数据明细数据
     */
    private List<SettleTransDataDto> settleTransDataList;
    /**
     * 成交数据明细数据（新加申购确认）
     */
    private List<SettleTransDataDto> settleTransDataSgqrList;

    public List<SettleTransDataDto> getSettleTransDataSgqrList() {
        return settleTransDataSgqrList;
    }

    public void setSettleTransDataSgqrList(List<SettleTransDataDto> settleTransDataSgqrList) {
        this.settleTransDataSgqrList = settleTransDataSgqrList;
    }

    /**
     * 除权交易数据
     */
    private List<SettleDelDealDetailDto> settleTransDataDelDtoList;

    public List<SettleTransDataTotal> getSettleTransDataTotalList() {
        return settleTransDataTotalList;
    }

    public void setSettleTransDataTotalList(List<SettleTransDataTotal> settleTransDataTotalList) {
        this.settleTransDataTotalList = settleTransDataTotalList;
    }

    /**
     * 债券一级投标交收结果汇总数据
     */
    private List<SettleTransDataTotal> settleTransDataTotalList;



    public InvestTradeReaderDto() {
    }

    /**
     * 成交数据明细质押品信息
     */
    private List<SettleTransDataPledge> settleTransDataPledgeList;

    public InvestTradeReaderDto(List<SettleTransDataDto> settleTransDataList) {
        this.settleTransDataList = settleTransDataList;
    }

    public List<SettleTransDataDto> getSettleTransDataList() {
        return settleTransDataList;
    }

    public void setSettleTransDataList(List<SettleTransDataDto> settleTransDataList) {
        this.settleTransDataList = settleTransDataList;
    }

    public List<SettleDelDealDetailDto> getSettleTransDataDelDtoList() {
        return settleTransDataDelDtoList;
    }

    public void setSettleTransDataDelDtoList(List<SettleDelDealDetailDto> settleTransDataDelDtoList) {
        this.settleTransDataDelDtoList = settleTransDataDelDtoList;
    }

    public List<SettleTransDataPledge> getSettleTransDataPledgeList() {
        return settleTransDataPledgeList;
    }

    public void setSettleTransDataPledgeList(List<SettleTransDataPledge> settleTransDataPledgeList) {
        this.settleTransDataPledgeList = settleTransDataPledgeList;
    }

    public boolean isCreateTrade() {
        return createTrade;
    }

    public void setCreateTrade(boolean createTrade) {
        this.createTrade = createTrade;
    }
}
