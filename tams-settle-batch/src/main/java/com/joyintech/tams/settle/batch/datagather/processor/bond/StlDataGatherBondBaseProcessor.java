package com.joyintech.tams.settle.batch.datagather.processor.bond;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.constant.StlDataDealProcessConstant;
import com.joyintech.tams.settle.batch.datadeal.service.StlDataDealProcessCommonService;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.processor.StlDataGatherProcessor;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 债券买入、卖出、行权回售
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherBondBaseProcessor
 * @since 1.0
 */
public abstract class StlDataGatherBondBaseProcessor extends StlDataGatherProcessor {
	
	@Autowired(required = false)
    private StlDataGatherService stlDataGatherService;

    @Autowired
    protected StlDataDealProcessCommonService stlDataDealProcessCommonService;

    @Autowired
    private SettleCommonService settleCommonService;


    protected abstract void setDealTotal(TotalTrdProductDealDto dealTotal, TrdProductDealDto trdProductDealDto);

    @Override
    protected String getSecType() {
        return DictCodeListExt1.证券品种_债券.getValue();
    }

    @Override
    protected List<String> getFinprodType() {return Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券.getValue());}

    /**
     * 生成汇总交易，每条明细生成一条汇总交易
     * @param dealList 交易明细列表
     * @return
     */
    @Override
    protected List<TotalTrdProductDealDto> generateDealTotal(List<TrdProductDealDto> dealList) {
        List<TotalTrdProductDealDto> dealTotalList = new ArrayList<>();
        dealList.forEach(trdProductDealDto -> {
            // 交易汇总信息
            TotalTrdProductDealDto dealTotal = new TotalTrdProductDealDto();
            // 汇总交易编号
        	if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dealList.get(0).getPs())) {
        		dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券买入));
        	}
        	if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), dealList.get(0).getPs())) {
        		dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券卖出));
        	}
        	if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), dealList.get(0).getPs())) {
        		dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券卖出));
        	}
        	if (StringUtil.equals(DictCodeList.交易方向_申购.getValue(), dealList.get(0).getPs())) {
        		dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券买入));
        	}
        	if (StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), dealList.get(0).getPs())) {
        		dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券买入));
        	}
        	if (StringUtil.equals(DictCodeListExt1.交易方向_债转股.getValue(), dealList.get(0).getPs())) {
        		dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeListExt3.共通业务类型_可转债转股交易));
        	}
        	// TODO
        	if (StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(), dealList.get(0).getPs())) {
                dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券买入));
            }
            
            // 设置汇总交易信息
            setDealTotal(dealTotal, trdProductDealDto);
            // 金融产品代码
            dealTotal.setFinprodId(trdProductDealDto.getFinprodId());
            // 金融产品类型（投管分类）
            dealTotal.setFinprodType2(trdProductDealDto.getFinprodType2());
            // 分支序号
            dealTotal.setBranch(0L);
            // 交易日
            dealTotal.setTradeDate(trdProductDealDto.getTradeDate());
            // 起息日
            dealTotal.setVdate(null);
            // 到期日
            dealTotal.setMdate(null);
            // 交付日
            dealTotal.setPayDate(trdProductDealDto.getPayDate());
            // 交割日
            dealTotal.setSettleDate(trdProductDealDto.getSettleDate());
            if (StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), dealList.get(0).getPs())) {
                // 交割日期是缴款日期
                dealTotal.setSettleDate(stlDataDealProcessCommonService.getNextWorkDay(trdProductDealDto.getTradeDate(), 
                    CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID)));
            }
            // 清算速度
            dealTotal.setSettleSpeed(trdProductDealDto.getSettleSpeed());
            // 交易币种
            dealTotal.setCcy(trdProductDealDto.getCcy());
            // 交易份额
            dealTotal.setShareAmt(trdProductDealDto.getShareAmt());
            // 随交易支付费用
            dealTotal.setFeeAmtPay(trdProductDealDto.getFeeAmtPay());
            // 交易金额
            dealTotal.setTradeAmt(trdProductDealDto.getTradeAmt());
            // 未随交易支付费用
            dealTotal.setFeeAmtUnpay(trdProductDealDto.getFeeAmtUnpay());
            // 总交易费用
            dealTotal.setFeeAmt(trdProductDealDto.getFeeAmt());
            // 交易方向
            dealTotal.setPs(trdProductDealDto.getPs());
            // 投资目的
            dealTotal.setInvAim(trdProductDealDto.getInvAim());
            // 交易状态
            dealTotal.setTradeStatus(trdProductDealDto.getTradeStatus());
            // 是否已撤销
            dealTotal.setIsCancel(trdProductDealDto.getIsCancel());
            // 证券管理户代码
            dealTotal.setSecManageAcctId(trdProductDealDto.getSecManageAcctId());
            // 前置交易编号
            dealTotal.setbTradeId(trdProductDealDto.getbTradeId());
            // 是否外部交易
            dealTotal.setIsOutTrade(trdProductDealDto.getIsOutTrade());
            // 交易场所
            dealTotal.setTradeMarket(trdProductDealDto.getTradeMarket());
            // 交易平台
            dealTotal.setTradePlat(trdProductDealDto.getTradePlat());
            // 交易市场
            dealTotal.setTradeMarketMode(trdProductDealDto.getTradeMarketMode());
            // 投资组合代码
            dealTotal.setPortfolioId(trdProductDealDto.getPortfolioId());
            // 备注
            dealTotal.setRemark(trdProductDealDto.getRemark());
            // 投管业务类型
            dealTotal.setTgBusiType(trdProductDealDto.getTgBusiType());
            // 席位号
            dealTotal.setSeatId(trdProductDealDto.getSeatId());
            // 利率
            dealTotal.setRate(null);
            // 回购期限(天)
            dealTotal.setRepoTerm(null);
            // 期限品种
            dealTotal.setTermType(null);
            // 委托代码
            dealTotal.setEntrustId(trdProductDealDto.getEntrustId());
            // 设置汇总交易编号
            trdProductDealDto.setTotleTradeId(dealTotal.getTotleTradeId());
            dealTotalList.add(dealTotal);
        });
        return dealTotalList;
    }

    @Override
    protected List<PtlTrade> generatePtlTrade(List<TrdProductDealDto> dealList) {
        return dealList.stream().map(trdProductDealDto -> {
            PtlTrade ptlTrade = new PtlTrade();
            // 组合代码
            ptlTrade.setPortfolioId(trdProductDealDto.getPortfolioId());
            // 业务编号
            ptlTrade.setBusiNo(trdProductDealDto.getTradeId());
            // 业务表
            ptlTrade.setBusiTable(DictCodeListStl.业务表_金融产品交易表.getValue());
            // 业务明细代码
            ptlTrade.setBusiId(trdProductDealDto.getFinprodId());
            // 投资目的
            ptlTrade.setInvAim(trdProductDealDto.getInvAim());
            // 证券托管户代码
            ptlTrade.setSecManageAcctId(trdProductDealDto.getSecManageAcctId());
            // 实际发生日
            ptlTrade.setSettleDate(trdProductDealDto.getSettleDate());
            // 业务类型
            ptlTrade.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
            return ptlTrade;
        }).collect(Collectors.toList());
    }

    @Override
    protected List<TrdProductDealDto> generateProductDeal(List<SettleTransDataDto> settleTransDataDtos) {
        return createDeal(settleTransDataDtos, true);
    }

    protected StlPayOrders createStlPayOrders(TrdFinProductDealTotal total, TrdProductDealDto productDealDto, List<SettleTransDataDto> settleTransDataDtos) {
        // 指令
        StlPayOrders stlPayOrders = new StlPayOrders();
        // 指令编号
        //stlPayOrders.setOrderId(IDGenerator.generate());
        stlPayOrders.setOrderId(settleCommonService.buildBussinessIdBySeqno("9998"));
        // 指令类型
        stlPayOrders.setOrderType(null);
        // 结算日期
        stlPayOrders.setSettleDate(total.getSettleDate());
        // 本方资金账户
        stlPayOrders.setMyAcctNo(productDealDto.getAcctNo());
        // 结算金额
        stlPayOrders.setSettleAmt(total.getTradeAmt());
        // 币种
        stlPayOrders.setCcy(total.getCcy());
        // 是否需要发送
        stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
        // 是否汇总交易
        stlPayOrders.setIsTotalTrade(DictCodeList.是否_是.getValue());
        // 交易编号
        stlPayOrders.setTradeId(total.getTotleTradeId());
        // 指令状态
        stlPayOrders.setOrderType(DictCodeListExt1.交易状态_初始.getValue());
        // 投资组合代码
        stlPayOrders.setPortfolioId(total.getPortfolioId());
        // 备注
        stlPayOrders.setRemark("数据清算自动处理");
        // 划款类型
        stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
        return stlPayOrders;
    }

    protected List<TrdTradeFeeDetail> createTradeFeeDetail(List<TrdProductDealDto> dealList, boolean useTotalTradeId, String ccy) {
        List<TrdTradeFeeDetail> res  = new ArrayList<>(dealList.size() * 7);
        dealList.forEach(deal -> {
            if (deal.getJsfAmt() != null && deal.getJsfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 经手费
                res.add(buildTrdTradeFeeDetail(useTotalTradeId ? deal.getTotleTradeId() : deal.getTradeId(),DictCodeListExt1.一次性费用类型_经手费.getValue(),DictCodeList.是否_是.getValue(),deal.getJsfAmt(),ccy));
            }
            if (deal.getGhfAmt() != null && deal.getGhfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 过户费
                res.add(buildTrdTradeFeeDetail(useTotalTradeId ? deal.getTotleTradeId() : deal.getTradeId(),DictCodeListExt1.一次性费用类型_过户费.getValue(),DictCodeList.是否_是.getValue(),deal.getGhfAmt(),ccy));
            }
            if (deal.getZgfAmt() != null && deal.getZgfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 证管费
                res.add(buildTrdTradeFeeDetail(useTotalTradeId ? deal.getTotleTradeId() : deal.getTradeId(),DictCodeListExt1.一次性费用类型_证管费.getValue(),DictCodeList.是否_是.getValue(),deal.getZgfAmt(),ccy));
            }
            if (deal.getSxfAmt() != null && deal.getSxfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 手续费
                res.add(buildTrdTradeFeeDetail(useTotalTradeId ? deal.getTotleTradeId() : deal.getTradeId(),DictCodeListExt1.一次性费用类型_交易手续费.getValue(),DictCodeList.是否_是.getValue(),deal.getSxfAmt(),ccy));
            }
            if (deal.getSettleFee() != null && deal.getSettleFee().compareTo(new BigDecimal(0)) > 0) {
                // 结算费
                res.add(buildTrdTradeFeeDetail(useTotalTradeId ? deal.getTotleTradeId() : deal.getTradeId(),DictCodeListExt1.一次性费用类型_结算费.getValue(),DictCodeList.是否_是.getValue(),deal.getSettleFee(),ccy));
            }
            if (deal.getStampFee() != null && deal.getStampFee().compareTo(new BigDecimal(0)) > 0) {
                // 印花税
                res.add(buildTrdTradeFeeDetail(useTotalTradeId ? deal.getTotleTradeId() : deal.getTradeId(),DictCodeListExt1.一次性费用类型_印花税.getValue(),DictCodeList.是否_是.getValue(),deal.getStampFee(),ccy));
            }
            // 债券卖出交易没有配置佣金费用但是登费用汇总时是否随交易支付必填
            if (deal.getCommissionAmt() != null && deal.getCommissionAmt().compareTo(new BigDecimal(0)) > 0) {
                // 佣金
                res.add(buildTrdTradeFeeDetail(useTotalTradeId ? deal.getTotleTradeId() : deal.getTradeId(),DictCodeListExt1.一次性费用类型_佣金.getValue(),deal.getIsPayWithTrade(),deal.getCommissionAmt(),ccy));
            }
        });
        return res;
    }

    /**
     * 创建费用明细
     * @param tradeId 交易编号
     * @param feeType 费用类型
     * @param payWithTrade 是否随交易支付
     * @param feeAmt 费用金额
     * @param ccy 币种
     * @return 费用明细
     */
    private TrdTradeFeeDetail buildTrdTradeFeeDetail(String tradeId, String feeType, String payWithTrade, BigDecimal feeAmt, String ccy) {
        // 经手费
        TrdTradeFeeDetail feeDetail = new TrdTradeFeeDetail();
        // 交易编号
        feeDetail.setTradeId(tradeId);
        // 费用类型
        feeDetail.setFeeType(feeType);
        // 是否随交易支付
        feeDetail.setIsPayWithTrade(payWithTrade);
        // 费用金额
        feeDetail.setFeeAmt(feeAmt);
        // 币种
        feeDetail.setCcy(ccy);
        // 登记托管机构
        feeDetail.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
        return feeDetail;
    }

    /**
     * 债券买入、卖出、行权回售
     * @param settleTransDataDtoList
     * @param buildTradeId
     * @return
     */
    protected abstract List<TrdProductDealDto> createDeal(List<SettleTransDataDto> settleTransDataDtoList, boolean buildTradeId);

    /**
     * 生成金融产品交易明细
     */
    protected TrdProductDealDto createProductDeal(SettleTransDataDto settleTransData, boolean buildTradeId) {
        TrdProductDealDto trdProductDeal = new TrdProductDealDto();
        // 交易编号，按现有的交易编号生成规则，自动生成
        if(buildTradeId) {
        	if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), settleTransData.getPs())) {
        		trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券买入));
        	}
        	if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), settleTransData.getPs())) {
        		trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券卖出));
        	}
        	if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), settleTransData.getPs())) {
        		trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券卖出));
        	}
        	if (StringUtil.equals(DictCodeList.交易方向_申购.getValue(), settleTransData.getPs())) {
        		trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券买入));
        	}
        	if (StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), settleTransData.getPs())) {
        		trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券买入));
        	}
        	if (StringUtil.equals(DictCodeListExt1.交易方向_债转股.getValue(), settleTransData.getPs())) {
        		trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeListExt3.共通业务类型_可转债转股交易));
        	}
            // TODO
            if (StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(), settleTransData.getPs())) {
                trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券买入));
            }
        }
        // 业务类型
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        // 金融产品代码
        trdProductDeal.setFinprodId(settleTransData.getFinprodId());
        // 金融产品类型（投管分类）
        trdProductDeal.setFinprodType2(settleTransData.getFinprodType2());
        // 金融产品类型
        trdProductDeal.setFinprodType(settleTransData.getFinprodType());
        // 分支序号
        trdProductDeal.setBranch(0L/*settleTransData.getBranch()*/);
        // 交易日
        trdProductDeal.setTradeDate(settleTransData.getTradeDate());
        // 起息日
        trdProductDeal.setVdate(null);
        // 到期日
        trdProductDeal.setMdate(null);
        // 交付日
        trdProductDeal.setPayDate(settleTransData.getVpayDate());
        // 交割日
        trdProductDeal.setSettleDate(settleTransData.getSettleDate());
        // 交易币种
        trdProductDeal.setCcy(settleTransData.getCcy());
        // 净值日期
        trdProductDeal.setCpriceDate(null);
        // 投资目的
        trdProductDeal.setInvAim(settleTransData.getInvAim());
        // 交易状态
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        // 原交易编号
        trdProductDeal.setpTradeId(null);
        // 是否结清
        trdProductDeal.setIsClean(null);
        // 汇总交易编号（父类中设置）
        // 反向交易编号
        trdProductDeal.setrTradeId(null);
        // 反向交易投资目的
        trdProductDeal.setInvAim2(null);
        // 通道代码
        trdProductDeal.setChlId(null);
        // 交易对手
        trdProductDeal.setCounterId(null);
        // 是否外部交易
        trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所
        trdProductDeal.setTradeMarket(settleTransData.getTradeMarket());
        // 交易平台
        trdProductDeal.setTradePlat(settleTransData.getTradePlat());
        // 交易市场
        trdProductDeal.setTradeMarketMode(settleTransData.getTradeMarketMode());
        // 本方交易员
        trdProductDeal.setTrader(null);
        // 对手方交易员
        trdProductDeal.setCounterTrader(null);
        // 对手方联系方式
        trdProductDeal.setCounterContact(null);
        // 证券管理户代码
        trdProductDeal.setSecManageAcctId(settleTransData.getSecAcctId());
        // 关联系统交易编号
        trdProductDeal.setfTradeId(null);
        // 现金流代码
        trdProductDeal.setCashId(null);
        // 登记托管机构
        trdProductDeal.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
        // 备注
        trdProductDeal.setRemark(null);
        // 撤销说明
        trdProductDeal.setCancelRemark(null);
        // 销售代码
        trdProductDeal.setSaleCode(null);
        // 计提份额
        trdProductDeal.setAccrNum(null);
        // 交收方式
        trdProductDeal.setSettlemenType(settleTransData.getSettleMode());
        // 席位号
        trdProductDeal.setSeatId(settleTransData.getSeatNumber());
        // 投资组合代码
        trdProductDeal.setPortfolioId(settleTransData.getPortfolioId());
        // 委托代码
        trdProductDeal.setEntrustId(settleTransData.getOrderId());
        // 成交编号（金额计算时已设置）
        // 权益登记日
        trdProductDeal.setRegDate(null);
        // 权益登记日份额
        trdProductDeal.setRegDateAmt(null);
        // 是否优先配售
        trdProductDeal.setIsPurchTypeYx(settleTransData.getIsPurchTypeYx());
        // 委托代码
        trdProductDeal.setEntrustId(settleTransData.getOrderId());
    	// 资金账户
        trdProductDeal.setAcctNo(settleTransData.getAcctNo());
        return trdProductDeal;
    }

    /**
     * 设置各种金额
     */
    protected void calcFeeAmtPay(TrdProductDealDto trdProductDeal, List<SettleTransDataDto> settleTransDataDtoList) {
        // 净价总额合计
        BigDecimal totalCpriceAmt = BigDecimal.ZERO;
        // 利息总额
        BigDecimal totalIntAmt = BigDecimal.ZERO;
        // 交易份额合计
        BigDecimal totalShareAmt = BigDecimal.ZERO;
        // 全价总额
        BigDecimal totalFpriceAmt = BigDecimal.ZERO;
        // 随交易支付费用
        BigDecimal totalFeeAmtPay = BigDecimal.ZERO;
        // 交易金额
        BigDecimal totalTradeAmt = BigDecimal.ZERO;
        // 未随交易支付费用
        BigDecimal totalFeeAmtUnpay = BigDecimal.ZERO;
        // 佣金
        BigDecimal commissionAmt = BigDecimal.ZERO;
        
        // 印花税
        BigDecimal totleStampFee = BigDecimal.ZERO;
        // 经手费
        BigDecimal totleJsfAmt = BigDecimal.ZERO;
        // 过户费
        BigDecimal totleGhfAmt = BigDecimal.ZERO;
        // 证管费
        BigDecimal totleZgfAmt = BigDecimal.ZERO;
        // 手续费
        BigDecimal totleSxfAmt = BigDecimal.ZERO;
        // 结算费
        BigDecimal totleSettleFee = BigDecimal.ZERO;
        
        List<String> dealIdList = new ArrayList<>();
        for (SettleTransDataDto item : settleTransDataDtoList) {
            totalCpriceAmt = ArithmeticUtil.doAdd(totalCpriceAmt, item.getCpriceAmt());
            totalShareAmt = ArithmeticUtil.doAdd(totalShareAmt, item.getShareAmt());
            totalIntAmt = ArithmeticUtil.doAdd(totalIntAmt, item.getIntAmt());
            totalFpriceAmt = ArithmeticUtil.doAdd(totalFpriceAmt, item.getFpriceAmt());
            totalFeeAmtPay = ArithmeticUtil.doAdd(totalFeeAmtPay, item.getJsfAmt(), item.getGhfAmt(), item.getZgfAmt(), item.getSxfAmt(), item.getJsfAmt());
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), item.getIsPayWithTrade())) {
                totalFeeAmtPay = ArithmeticUtil.doAdd(totalFeeAmtPay, item.getCommissionAmt());
            }
            else {
                totalFeeAmtUnpay = ArithmeticUtil.doAdd(totalFeeAmtUnpay, item.getCommissionAmt());
            }
            totalTradeAmt = ArithmeticUtil.doAdd(totalTradeAmt, item.getTradeAmt());
            dealIdList.add(item.getTradeId());
            
            commissionAmt = ArithmeticUtil.doAdd(commissionAmt, item.getCommissionAmt());
            trdProductDeal.setIsPayWithTrade(item.getIsPayWithTrade());
            
            totleStampFee = ArithmeticUtil.doAdd(totleStampFee, item.getStampFee());
            totleJsfAmt = ArithmeticUtil.doAdd(totleJsfAmt, item.getJsfAmt());
            totleGhfAmt = ArithmeticUtil.doAdd(totleGhfAmt, item.getGhfAmt());
            totleZgfAmt = ArithmeticUtil.doAdd(totleZgfAmt, item.getZgfAmt());
            totleSxfAmt = ArithmeticUtil.doAdd(totleSxfAmt, item.getSxfAmt());
            totleSettleFee = ArithmeticUtil.doAdd(totleSettleFee, item.getSettleFee());
            
        }
        // 净价总额
        trdProductDeal.setCpriceAmt(totalCpriceAmt);
        // 交易份额
        trdProductDeal.setShareAmt(totalShareAmt);
        // 交易本金
        trdProductDeal.setPrinAmt(trdProductDeal.getCpriceAmt());
        // 利息总额
        trdProductDeal.setIntAmt(totalIntAmt);
        // 全价总额
        trdProductDeal.setFpriceAmt(totalFpriceAmt);
        // 随交易支付费用
        trdProductDeal.setFeeAmtPay(totalFeeAmtPay);
        // 交易金额
        trdProductDeal.setTradeAmt(totalTradeAmt);
        // 未随交易支付费用
        trdProductDeal.setFeeAmtUnpay(totalFeeAmtUnpay);
        // 总交易费用
        trdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(totalFeeAmtPay, totalFeeAmtUnpay));
        // 成交编号
        trdProductDeal.setDealId(StringUtil.joinString(dealIdList, GlobalConstant.COMMA));
        // 佣金
        trdProductDeal.setCommissionAmt(commissionAmt);

        // 印花税
        trdProductDeal.setStampFee(totleStampFee);
        // 经手费
        trdProductDeal.setJsfAmt(totleJsfAmt);
        // 过户费
        trdProductDeal.setGhfAmt(totleGhfAmt);
        // 证管费
        trdProductDeal.setZgfAmt(totleZgfAmt);
        // 手续费
        trdProductDeal.setSxfAmt(totleSxfAmt);
        // 结算费
        trdProductDeal.setSettleFee(totleSettleFee);
    }
    
    protected List<EtrFinishStatus> generateEtrFinishStatusList(List<TotalTrdProductDealDto> dealTotalList, List<TrdProductDealDto> dealList){
    	List<EtrFinishStatus> etrFinishStatusList = new ArrayList<>(dealTotalList.size());
    	if(CollectionUtils.isNotEmpty(dealTotalList)) {
    		dealTotalList.forEach(x ->{
    			EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
    			etrFinishStatus.setEntrustId(x.getEntrustId());
    			etrFinishStatus.setTradeId(x.getEntrustId());
    			etrFinishStatus.setEntFinishAmt(x.getTradeAmt());
    			// 因为reader是根据orderId进process，实际一次process只会查询一次委托信息，性能有待优化
    			EtrEntrustAdd etrEntrustAdd = stlDataGatherService.selectEtrEntrustByEntrustId(x.getEntrustId());
    			if (etrEntrustAdd != null && StringUtil.equals(etrEntrustAdd.getTargetType(), DictCodeListExt3.目标类型_数量.getValue())){
    				etrFinishStatus.setEntFinishAmt(x.getShareAmt());
    			}
    			etrFinishStatusList.add(etrFinishStatus);
    		});
    	}
    	
    	return etrFinishStatusList;
    }

}
