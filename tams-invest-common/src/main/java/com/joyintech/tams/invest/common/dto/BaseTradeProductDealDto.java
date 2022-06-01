/*
 * 文件名：BaseTradeProductDealDto.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：huwenbin 修改时间：2018年9月4日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.dto;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 金融产品交易信息实体 〈功能详细描述〉
 *
 * @author huwenbin
 * @version 1.0 2018年9月4日
 * @see BaseTradeProductDealDto
 * @since 1.0
 */
public class BaseTradeProductDealDto {

    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 债券借贷融入融出券代码
     */
    private String secLendFinprodId;

    public boolean isDayEndBatch() {
        return isDayEndBatch;
    }

    public void setDayEndBatch(boolean dayEndBatch) {
        isDayEndBatch = dayEndBatch;
    }

    /**
     * 是否是日终批数据
     */
    private boolean isDayEndBatch;

    /**
     * 金融产品汇总交易
     */
    private TrdFinProductDealTotal trdFinProductDealTotal;

    /**
     * 金融产品汇总交易扩展表
     */
    private PaTrdFinProductDealTotal paTrdFinProductDealTotal;

    /**
     *
     */
    private TrdDealTotalAdd trdDealTotalAdd;

    /**
     * 实体组合代码
     */
    private String actPortfolioId;

    /**
     * 金融产品交易
     */
    private TrdProductDeal trdProductDeal;

    /**
     * 金融产品交易附属表
     */
    private TrdProductDealAdd trdProductDealAdd;

    /**
     * 金融产品交易扩展表
     */
    private PaTrdProductDeal paTrdProductDeal;

    /**
     * 划款指令表
     */
    private List<StlPayOrdersDto> stlPayOrdersList;

    // /**
    // * 交易资金指令表
    // */
    // private List<StlOrders> stlOrdersList;

    /**
     * 金融产品实物交易表
     */
    private List<TrdFinProductBarter> trdFinProductBarterList;

    /**
     * 交易汇总费用表
     */
    private List<TrdTradeFeeTotal> trdTradeFeeTotalList;

    /**
     * 交易费用明细表
     */
    private List<TrdTradeFeeDetail> trdTradeFeeDetailList;

    // TrdBankManualSettleInfo
    private TrdBankManualSettleInfo trdBankManualSettleInfo;
    // TrdBankManualSettleFlow
    private TrdBankManualSettleFlow trdBankManualSettleFlow;

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

    /**
     * 交易费用表(通道费）
     */
    private TrdTradeFeeDetail trdTradeFeeDetail;

    /**
     * 框架共通组件.附件组件
     */
    private MstFileInputParameter mstFileInputParameter;

    /**
     *
     */
    private MstFileOutputParameter mstFileOutputParameter;

    /**
     * 买断式回购特殊处理(债券数量)
     */
    private BigDecimal secAmount;

    /**
     * 添加质押券信息
     */
    private BussRepoSecDto bussRepoSec;

    /**
     * 资产二级类型
     */
    private String assetType2;

    /**
     * 组合交易
     */
    private List<PtlTrade> ptlTrade;

    /**
     * 计划支付日
     */
    private Date payDate;

    /**
     * 应收款类型，还本结转，收息结转，还本不带息利息结转
     */
    private String recType;
    
    /**
     * 应收款数量  实际更新应收款数量
     */
    private BigDecimal recAmount;

    private String preTradeId;

    private String fTradeId;
    
    /**add by taoran 20200303*/
    /**
     * 申购类型(可转债交易字段)
     */
    private String purchType;
	/**
     * 来自场内交易下单标识
     */
    private String isFromExchange;
    
    /**
     * 债券一级市场缴款页面标识
     */
    private String bondFirstSign;
    
    /**
     * 是否走工作流（结算下行过来的数据没有流程中的记录）
     */
    private boolean isNotWorkflow;

    /**
     * 申购方式(可转债交易字段)
     */
    private String purchModle;
    
    // add by etliu 20210128 start
    /**
     * 银行间上行报价编号
     */
    private String quoteId;

    /**
     * 除权日
     */
    private Date exRightsDate;
    /**
     *数据已入新头寸，Y：已入
     */
    private String isExistNew;

    /**
     * 业务阶段 01 登记 02 成交 03 交收 04 交收失败
     */
    private String stage;

    public Date getExRightsDate() {
        return exRightsDate;
    }

    public void setExRightsDate(Date exRightsDate) {
        this.exRightsDate = exRightsDate;
    }

    /**
     * 获取 quoteId
     * @return quoteId.
     */
    public String getQuoteId() {
        return quoteId;
    }

    /**
     * 设置 quoteId
     * @param quoteId quoteId
     */
    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }
    // add by etliu 20210128 end

    public String getPurchType() {
		return purchType;
	}

	public void setPurchType(String purchType) {
		this.purchType = purchType;
	}

	/**add by taoran 20200303*/
    
    public String getfTradeId() {
        return fTradeId;
    }

    public void setfTradeId(String fTradeId) {
        this.fTradeId = fTradeId;
    }

    public String getPreTradeId() {
        return preTradeId;
    }

    public void setPreTradeId(String preTradeId) {
        this.preTradeId = preTradeId;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    /**
     * 获取 trdProductDeal
     *
     * @return trdProductDeal.
     */
    public TrdProductDeal getTrdProductDeal() {
        return trdProductDeal;
    }

    /**
     * 设置 trdProductDeal
     *
     * @param trdProductDeal trdProductDeal
     */
    public void setTrdProductDeal(TrdProductDeal trdProductDeal) {
        this.trdProductDeal = trdProductDeal;
    }

    /**
     * 获取 trdProductDealAdd
     *
     * @return trdProductDealAdd.
     */
    public TrdProductDealAdd getTrdProductDealAdd() {
        return trdProductDealAdd;
    }

    /**
     * 设置 trdProductDealAdd
     *
     * @param trdProductDealAdd trdProductDealAdd
     */
    public void setTrdProductDealAdd(TrdProductDealAdd trdProductDealAdd) {
        this.trdProductDealAdd = trdProductDealAdd;
    }

    /**
     * 获取 stlPayOrdersList
     *
     * @return stlPayOrdersList.
     */
    public List<StlPayOrdersDto> getStlPayOrdersList() {
        return stlPayOrdersList;
    }

    /**
     * 设置 stlPayOrdersList
     *
     * @param stlPayOrdersList stlPayOrdersList
     */
    public void setStlPayOrdersList(List<StlPayOrdersDto> stlPayOrdersList) {
        this.stlPayOrdersList = stlPayOrdersList;
    }

    // /**
    // * 获取 stlOrdersList
    // *
    // * @return stlOrdersList.
    // */
    // public List<StlOrders> getStlOrdersList() {
    // return stlOrdersList;
    // }
    //
    // /**
    // * 设置 stlOrdersList
    // *
    // * @param stlOrdersList stlOrdersList
    // */
    // public void setStlOrdersList(List<StlOrders> stlOrdersList) {
    // this.stlOrdersList = stlOrdersList;
    // }

    /**
     * 获取 trdFinProductBarterList
     *
     * @return trdFinProductBarterList.
     */
    public List<TrdFinProductBarter> getTrdFinProductBarterList() {
        return trdFinProductBarterList;
    }

    /**
     * 设置 trdFinProductBarterList
     *
     * @param trdFinProductBarterList trdFinProductBarterList
     */
    public void setTrdFinProductBarterList(List<TrdFinProductBarter> trdFinProductBarterList) {
        this.trdFinProductBarterList = trdFinProductBarterList;
    }

    public List<TrdTradeFeeTotal> getTrdTradeFeeTotalList() {
        return trdTradeFeeTotalList;
    }

    public void setTrdTradeFeeTotalList(List<TrdTradeFeeTotal> trdTradeFeeTotalList) {
        this.trdTradeFeeTotalList = trdTradeFeeTotalList;
    }

    /**
     * 获取 trdTradeFeeDetailList
     *
     * @return trdTradeFeeDetailList.
     */
    public List<TrdTradeFeeDetail> getTrdTradeFeeDetailList() {
        return trdTradeFeeDetailList;
    }

    /**
     * 设置 trdTradeFeeDetailList
     *
     * @param trdTradeFeeDetailList trdTradeFeeDetailList
     */
    public void setTrdTradeFeeDetailList(List<TrdTradeFeeDetail> trdTradeFeeDetailList) {
        this.trdTradeFeeDetailList = trdTradeFeeDetailList;
    }

    /**
     * 获取 mstFileInputParameter
     *
     * @return mstFileInputParameter.
     */
    public MstFileInputParameter getMstFileInputParameter() {
        return mstFileInputParameter;
    }

    /**
     * 设置 mstFileInputParameter
     *
     * @param mstFileInputParameter mstFileInputParameter
     */
    public void setMstFileInputParameter(MstFileInputParameter mstFileInputParameter) {
        this.mstFileInputParameter = mstFileInputParameter;
    }

    /**
     * 获取 mstFileOutputParameter
     *
     * @return mstFileOutputParameter.
     */
    public MstFileOutputParameter getMstFileOutputParameter() {
        return mstFileOutputParameter;
    }

    /**
     * 设置 mstFileOutputParameter
     *
     * @param mstFileOutputParameter mstFileOutputParameter
     */
    public void setMstFileOutputParameter(MstFileOutputParameter mstFileOutputParameter) {
        this.mstFileOutputParameter = mstFileOutputParameter;
    }

    /**
     * 获取 paTrdProductDeal
     *
     * @return paTrdProductDeal
     */
    public PaTrdProductDeal getPaTrdProductDeal() {
        return paTrdProductDeal;
    }

    /**
     * 设置 paTrdProductDeal
     *
     * @param paTrdProductDeal paTrdProductDeal
     */
    public void setPaTrdProductDeal(PaTrdProductDeal paTrdProductDeal) {
        this.paTrdProductDeal = paTrdProductDeal;
    }

    public BigDecimal getSecAmount() {
        return secAmount;
    }

    public void setSecAmount(BigDecimal secAmount) {
        this.secAmount = secAmount;
    }

    public List<PtlTrade> getPtlTrade() {
        return ptlTrade;
    }

    public void setPtlTrade(List<PtlTrade> ptlTrade) {
        this.ptlTrade = ptlTrade;
    }

    /**
     * 获取 actPortfolioId
     *
     * @return actPortfolioId
     */
    public String getActPortfolioId() {
        return actPortfolioId;
    }

    /**
     * 设置 actPortfolioId
     *
     * @param actPortfolioId actPortfolioId
     */
    public void setActPortfolioId(String actPortfolioId) {
        this.actPortfolioId = actPortfolioId;
    }

    public TrdFinProductDealTotal getTrdFinProductDealTotal() {
        return trdFinProductDealTotal;
    }

    public void setTrdFinProductDealTotal(TrdFinProductDealTotal trdFinProductDealTotal) {
        this.trdFinProductDealTotal = trdFinProductDealTotal;
    }

    public PaTrdFinProductDealTotal getPaTrdFinProductDealTotal() {
        return paTrdFinProductDealTotal;
    }

    public void setPaTrdFinProductDealTotal(PaTrdFinProductDealTotal paTrdFinProductDealTotal) {
        this.paTrdFinProductDealTotal = paTrdFinProductDealTotal;
    }

    /**
     * 获取 bussRepoSec
     *
     * @return bussRepoSec
     */
    public BussRepoSecDto getBussRepoSec() {
        return bussRepoSec;
    }

    /**
     * 设置 bussRepoSec
     *
     * @param bussRepoSec bussRepoSec
     */
    public void setBussRepoSec(BussRepoSecDto bussRepoSec) {
        this.bussRepoSec = bussRepoSec;
    }

    /**
     * 获取 secLendFinprodId
     *
     * @return secLendFinprodId
     */
    public String getSecLendFinprodId() {
        return secLendFinprodId;
    }

    /**
     * 设置 secLendFinprodId
     *
     * @param secLendFinprodId secLendFinprodId
     */
    public void setSecLendFinprodId(String secLendFinprodId) {
        this.secLendFinprodId = secLendFinprodId;
    }

    /**
     * 获取 assetType2
     *
     * @return assetType2
     */
    public String getAssetType2() {
        return assetType2;
    }

    /**
     * 设置 assetType2
     *
     * @param assetType2 assetType2
     */
    public void setAssetType2(String assetType2) {
        this.assetType2 = assetType2;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getRecType() {
        return recType;
    }

    public void setRecType(String recType) {
        this.recType = recType;
    }

	public BigDecimal getRecAmount() {
		return recAmount;
	}

	public void setRecAmount(BigDecimal recAmount) {
		this.recAmount = recAmount;
	}

	public TrdTradeFeeDetail getTrdTradeFeeDetail() {
		return trdTradeFeeDetail;
	}

	public void setTrdTradeFeeDetail(TrdTradeFeeDetail trdTradeFeeDetail) {
		this.trdTradeFeeDetail = trdTradeFeeDetail;
	}

    public String getPurchModle() {
        return purchModle;
    }

    public void setPurchModle(String purchModle) {
        this.purchModle = purchModle;
    }

    public TrdDealTotalAdd getTrdDealTotalAdd() {
        return trdDealTotalAdd;
    }

    public void setTrdDealTotalAdd(TrdDealTotalAdd trdDealTotalAdd) {
        this.trdDealTotalAdd = trdDealTotalAdd;
    }

    /**
     * 是否自动批处理
     */
    private boolean isBatch;

    public boolean isBatch() {
        if(isBatch){
            return true;
        }else{
            return false;
        }
    }

    public void setBatch(boolean batch) {
        isBatch = batch;
    }
	/**
     * 获取 isFromExchange
     * @return isFromExchange.
     */
    public String getIsFromExchange() {
        return isFromExchange;
    }

    /**
     * 设置 isFromExchange
     * @param isFromExchange isFromExchange
     */
    public void setIsFromExchange(String isFromExchange) {
        this.isFromExchange = isFromExchange;
    }

    public String getBondFirstSign() {
		return bondFirstSign;
	}

	public void setBondFirstSign(String bondFirstSign) {
		this.bondFirstSign = bondFirstSign;
	}

	/**
	 * 获取 isNotWorkflow
	 * @return isNotWorkflow.
	 */
	public boolean isNotWorkflow() {
		return isNotWorkflow;
	}

	/**
	 * 设置 isNotWorkflow
	 * @param isNotWorkflow isNotWorkflow
	 */
	public void setNotWorkflow(boolean isNotWorkflow) {
		this.isNotWorkflow = isNotWorkflow;
	}
    /**
     * 是否现金交割
     */
    private String isCashPay;
    /**
     * 是否现金交割
     */
    public String getIsCashPay() {
        return isCashPay;
    }
    /**
     * 是否现金交割
     */
    public void setIsCashPay(String isCashPay) {
        this.isCashPay = isCashPay;
    }
    /**
     * 是否借贷交易
     */
    private String isSLDeal;
    /**
     * 是否借贷交易
     */
    public String getIsSLDeal() {
        return isSLDeal;
    }
    /**
     * 是否借贷交易
     */
    public void setIsSLDeal(String isSLDeal) {
        this.isSLDeal = isSLDeal;
    }
/**
     * 是否软硬控
     */
    private String softSkipMode;

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }
    public String getIsExistNew() {
        return isExistNew;
    }

    public void setIsExistNew(String isExistNew) {
        this.isExistNew = isExistNew;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
