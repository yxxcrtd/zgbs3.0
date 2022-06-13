package com.joyintech.tams.settle.batch.datagather.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 投管交易DTO
 * @author 何阳阳
 * @date 2020/7/16
 * @see InvestTradeWriteDto
 * @since 1.0
 */
public class InvestTradeWriteDto extends NeedToWriteEntity {

    /**
     * 证券品种
     */
    private String secType;

    /**
     * 金融产品汇总交易
     */
    private List<TotalTrdProductDealDto> finProductDealTotalList;
    
    /**
     * 金融产品汇总附属交易
     */
    private List<TrdDealTotalAdd> trdDealTotalAddList;
    
    /**
     * 实物交易汇总表
     */
    private List<TrdFinProductBarterTotal> trdBarterTotalList;
    
    /**
     * 实物交易表
     */
    private List<TrdFinProductBarter> trdBarterList;

    /**
     * 金融产品交易明细
     */
    private List<TrdProductDealDto> productDealList;

    /**
     * 组合交易DTO
     */
    private List<PtlTrade> ptlTradeList;

    /**
     * 金融产品交易费用明细
     */
    private List<TrdTradeFeeDetail> tradeFeeDetailList;

    /**
     * 划款指令明细
     */
    private List<StlPayOrders> stlPayOrdersList;

    /**
     * 交易类金融信息
     */
    private List<FinTradeProductDto> tradeProductList;

    /**
     * 回购质押品
     */
    private List<FinDealPledge> dealPledgeList;

    private List<TrdProductDealAdd> trdProductDealAddList;
    
    /**
     * 日间委托完成度表有一笔交易编号等于自身委托编号的记录，这条记录每次正向跑批时需要实时更新
     */
    private List<EtrFinishStatus> etrFinishStatusList;
    
    /**
     * 成交编号业务编号关联表（只针对除权登记数据）
     */
    private List<SettleTransTgMapping> settleTransTgMappingList;

    private List<EtrSecPositionFlow> etrSecPositionFlowList;

    private List<PtlSecPositionFlow> ptlSecPositionFlowList;

    // TrdBankManualSettleInfo
    private List<TrdBankManualSettleInfo> trdBankManualSettleInfoList;
    // TrdBankManualSettleFlow
    private List<TrdBankManualSettleFlow> trdBankManualSettleFlowList;

    public InvestTradeWriteDto() {
    	finProductDealTotalList = new ArrayList<TotalTrdProductDealDto>();
    	trdDealTotalAddList = new ArrayList<TrdDealTotalAdd>();
    	trdBarterTotalList = new ArrayList<TrdFinProductBarterTotal>();
    	trdBarterList = new ArrayList<TrdFinProductBarter>();
    	productDealList = new ArrayList<TrdProductDealDto>();
    	ptlTradeList = new ArrayList<PtlTrade>();
    	tradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();
    	stlPayOrdersList = new ArrayList<StlPayOrders>();
    	tradeProductList = new ArrayList<FinTradeProductDto>();
    	dealPledgeList = new ArrayList<FinDealPledge>();
    	etrFinishStatusList = new ArrayList<EtrFinishStatus>();
    	settleTransTgMappingList = new ArrayList<SettleTransTgMapping>();
        trdBankManualSettleInfoList = new ArrayList<TrdBankManualSettleInfo>();
        trdBankManualSettleFlowList = new ArrayList<TrdBankManualSettleFlow>();
    }

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public List<PtlTrade> getPtlTradeList() {
        return ptlTradeList;
    }

    public void setPtlTradeList(List<PtlTrade> ptlTradeList) {
        this.ptlTradeList = ptlTradeList;
    }

    public List<TotalTrdProductDealDto> getFinProductDealTotalList() {
        return finProductDealTotalList;
    }

    public void setFinProductDealTotalList(List<TotalTrdProductDealDto> finProductDealTotalList) {
        this.finProductDealTotalList = finProductDealTotalList;
    }

    public List<TrdDealTotalAdd> getTrdDealTotalAddList() {
        return trdDealTotalAddList;
    }

    public void setTrdDealTotalAddList(List<TrdDealTotalAdd> trdDealTotalAddList) {
        this.trdDealTotalAddList = trdDealTotalAddList;
    }

	public List<TrdFinProductBarterTotal> getTrdBarterTotalList() {
		return trdBarterTotalList;
	}

	public void setTrdBarterTotalList(List<TrdFinProductBarterTotal> trdBarterTotalList) {
		this.trdBarterTotalList = trdBarterTotalList;
	}

	public List<TrdProductDealDto> getProductDealList() {
		return productDealList;
	}

	public void setProductDealList(List<TrdProductDealDto> productDealList) {
		this.productDealList = productDealList;
	}

	public List<TrdTradeFeeDetail> getTradeFeeDetailList() {
        return tradeFeeDetailList;
    }

    public void setTradeFeeDetailList(List<TrdTradeFeeDetail> tradeFeeDetailList) {
        this.tradeFeeDetailList = tradeFeeDetailList;
    }

    public List<StlPayOrders> getStlPayOrdersList() {
        return stlPayOrdersList;
    }

    public void setStlPayOrdersList(List<StlPayOrders> stlPayOrdersList) {
        this.stlPayOrdersList = stlPayOrdersList;
    }

    public List<FinTradeProductDto> getTradeProductList() {
        return tradeProductList;
    }

    public void setTradeProductList(List<FinTradeProductDto> tradeProductList) {
        this.tradeProductList = tradeProductList;
    }

    public List<FinDealPledge> getDealPledgeList() {
        return dealPledgeList;
    }

    public void setDealPledgeList(List<FinDealPledge> dealPledgeList) {
        this.dealPledgeList = dealPledgeList;
    }

	public List<TrdFinProductBarter> getTrdBarterList() {
		return trdBarterList;
	}

	public void setTrdBarterList(List<TrdFinProductBarter> trdBarterList) {
		this.trdBarterList = trdBarterList;
	}

	public List<EtrFinishStatus> getEtrFinishStatusList() {
		return etrFinishStatusList;
	}

	public void setEtrFinishStatusList(List<EtrFinishStatus> etrFinishStatusList) {
		this.etrFinishStatusList = etrFinishStatusList;
	}

	public List<SettleTransTgMapping> getSettleTransTgMappingList() {
		return settleTransTgMappingList;
	}

    public List<TrdProductDealAdd> getTrdProductDealAddList() {
        return trdProductDealAddList;
    }

    public void setTrdProductDealAddList(List<TrdProductDealAdd> trdProductDealAddList) {
        this.trdProductDealAddList = trdProductDealAddList;
    }

    public void setSettleTransTgMappingList(List<SettleTransTgMapping> settleTransTgMappingList) {
		this.settleTransTgMappingList = settleTransTgMappingList;
	}

    public List<EtrSecPositionFlow> getEtrSecPositionFlowList() {
        return etrSecPositionFlowList;
    }

    public void setEtrSecPositionFlowList(List<EtrSecPositionFlow> etrSecPositionFlowList) {
        this.etrSecPositionFlowList = etrSecPositionFlowList;
    }

    public List<PtlSecPositionFlow> getPtlSecPositionFlowList() {
        return ptlSecPositionFlowList;
    }

    public void setPtlSecPositionFlowList(List<PtlSecPositionFlow> ptlSecPositionFlowList) {
        this.ptlSecPositionFlowList = ptlSecPositionFlowList;
    }

    //add by wujie on 20220301 start remark:调用估值交易入账参数对象
    private String busiType;

    private  List<AllTradeCommonInputDto> listInputDto;

    public List<AllTradeCommonInputDto> getListInputDto() {
        return listInputDto;
    }

    public void setListInputDto(List<AllTradeCommonInputDto> listInputDto) {
        this.listInputDto = listInputDto;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }


    //add by wujie on 20220301 end remark:调用估值交易入账参数对象

    //add by liujiasheng 20220512
    public List<TrdBankManualSettleInfo> getTrdBankManualSettleInfoList() {
        return trdBankManualSettleInfoList;
    }

    public void setTrdBankManualSettleInfoList(List<TrdBankManualSettleInfo> trdBankManualSettleInfoList) {
        this.trdBankManualSettleInfoList = trdBankManualSettleInfoList;
    }

    public List<TrdBankManualSettleFlow> getTrdBankManualSettleFlowList() {
        return trdBankManualSettleFlowList;
    }

    public void setTrdBankManualSettleFlowList(List<TrdBankManualSettleFlow> trdBankManualSettleFlowList) {
        this.trdBankManualSettleFlowList = trdBankManualSettleFlowList;
    }
}
