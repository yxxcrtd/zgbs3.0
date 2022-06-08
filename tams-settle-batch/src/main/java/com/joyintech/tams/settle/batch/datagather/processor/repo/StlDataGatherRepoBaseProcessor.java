package com.joyintech.tams.settle.batch.datagather.processor.repo;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.constant.StlDataDealProcessConstant;
import com.joyintech.tams.settle.batch.datagather.dto.FinTradeProductDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.processor.StlDataGatherProcessor;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 回购交易处理
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherRepoBaseProcessor
 * @since 1.0
 */
public abstract class StlDataGatherRepoBaseProcessor extends StlDataGatherProcessor {
    @Autowired
    private SettleCommonService settleCommonService;

    protected void setDealTotal(TotalTrdProductDealDto dealTotal, List<TrdProductDealDto> dealList) {
    }

    @Override
    protected String getSecType() {
        return DictCodeListExt1.证券品种_回购.getValue();
    }

    @Override
    protected List<String> getFinprodType() {return Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());}

    @Override
    protected List<TotalTrdProductDealDto> generateDealTotal(List<TrdProductDealDto> dealList) {
        // 交易明细
        TotalTrdProductDealDto dealTotal = new TotalTrdProductDealDto();
        // 交易方向为”还本到账“的交易明细
        TrdProductDealDto dealDto2 = findByPs(dealList, Arrays.asList(DictCodeList.交易方向_还本到账.getValue()));
        // 交易方向为”利息到账“的交易明细
        TrdProductDealDto dealDto3 = findByPs(dealList, Arrays.asList(DictCodeList.交易方向_收息到账.getValue()));
        // 交易方向为”本金到账“的成交明细
        TrdProductDealDto dealDto4 = findByPs(dealList, Arrays.asList(DictCodeList.交易方向_本金返还.getValue()));
        // 变量：随交易支付费用
        BigDecimal totalFeeAmtPay = BigDecimal.ZERO;
        // 变量：未随交易支付费用
        BigDecimal totalFeeAmtUnpay = BigDecimal.ZERO;
        for (TrdProductDealDto item : dealList) {
            totalFeeAmtPay = ArithmeticUtil.doAdd(totalFeeAmtPay, item.getFeeAmtPay());
            totalFeeAmtUnpay = ArithmeticUtil.doAdd(totalFeeAmtUnpay, item.getFeeAmtUnpay());
        }
        // 汇总交易编号
        dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_质押式回购交易));
        // 业务类型
        dealTotal.setBusiType(DictCodeList.共通业务类型_质押式回购存续期.getValue());
        // 设置汇总交易信息
        setDealTotal(dealTotal, dealList);
        if(null != dealDto2) {
            // 起息日
            dealTotal.setVdate(dealDto2.getVdate());
            // 到期日
            dealTotal.setMdate(dealDto2.getMdate());
        }
        if(null != dealDto3) {
            // 利息总额
            dealTotal.setIntAmt(dealDto3.getIntAmt());
            // 全价总额
            dealTotal.setFpriceAmt(dealDto3.getFpriceAmt());
            // 交易金额
            dealTotal.setTradeAmt(dealDto3.getTradeAmt());
        }
        if(null != dealDto4) {
            // 全价总额
            dealTotal.setFpriceAmt(ArithmeticUtil.doAdd(dealDto4.getFpriceAmt(), dealTotal.getFpriceAmt()));
            // 交易金额
            dealTotal.setTradeAmt(ArithmeticUtil.doAdd(dealDto4.getTradeAmt(), dealTotal.getTradeAmt()));
        }
        // 随交易支付费用
        dealTotal.setFeeAmtPay(totalFeeAmtPay);
        // 未随交易支付费用
        dealTotal.setFeeAmtUnpay(totalFeeAmtUnpay);
        // 总交易费用
        dealTotal.setFeeAmt(ArithmeticUtil.doAdd(totalFeeAmtPay, totalFeeAmtUnpay));
        // 首期结算方式
        dealTotal.setDeliveryType("DVP");
        // 到期结算方式
        dealTotal.setmDeliveryType("DVP");
        // 交易状态
        dealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        dealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        // 是否外部交易
        dealTotal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 备注
        dealTotal.setRemark("数据清算自动处理");
        return Arrays.asList(dealTotal);
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
            ptlTrade.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
            return ptlTrade;
        }).collect(Collectors.toList());
    }

    @Override
    protected List<TrdTradeFeeDetail> generateTradeFeeDetail(List<TrdProductDealDto> dealList) {
        List<TrdTradeFeeDetail> feeDetailList = new ArrayList<>(dealList.size() * 6);
        dealList.forEach(deal -> {
            if (deal.getJsfAmt() != null && deal.getJsfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 经手费
                feeDetailList.add(buildTrdTradeFeeDetail(deal.getTradeId(),DictCodeListExt1.一次性费用类型_经手费.getValue(),DictCodeList.是否_是.getValue(),deal.getJsfAmt()));
            }
            if (deal.getGhfAmt() != null && deal.getGhfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 过户费
                feeDetailList.add(buildTrdTradeFeeDetail(deal.getTradeId(),DictCodeListExt1.一次性费用类型_过户费.getValue(),DictCodeList.是否_是.getValue(),deal.getGhfAmt()));
            }
            if (deal.getZgfAmt() != null && deal.getZgfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 证管费
                feeDetailList.add(buildTrdTradeFeeDetail(deal.getTradeId(),DictCodeListExt1.一次性费用类型_证管费.getValue(),DictCodeList.是否_是.getValue(),deal.getZgfAmt()));
            }
            if (deal.getSxfAmt() != null && deal.getSxfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 手续费
                feeDetailList.add(buildTrdTradeFeeDetail(deal.getTradeId(),DictCodeListExt1.一次性费用类型_交易手续费.getValue(),DictCodeList.是否_是.getValue(),deal.getSxfAmt()));
            }
            if (deal.getSettleFee() != null && deal.getSettleFee().compareTo(new BigDecimal(0)) > 0) {
                // 结算费
                feeDetailList.add(buildTrdTradeFeeDetail(deal.getTradeId(),DictCodeListExt1.一次性费用类型_结算费.getValue(),DictCodeList.是否_是.getValue(),deal.getSettleFee()));
            }
            if (deal.getStampFee() != null && deal.getStampFee().compareTo(new BigDecimal(0)) > 0) {
                // 樱花谁
                feeDetailList.add(buildTrdTradeFeeDetail(deal.getTradeId(),DictCodeListExt1.一次性费用类型_印花税.getValue(),DictCodeList.是否_是.getValue(),deal.getStampFee()));
            }
            // 佣金
            if (deal.getCommissionAmt() != null && deal.getCommissionAmt().compareTo(new BigDecimal(0)) > 0) {
            	feeDetailList.add(buildTrdTradeFeeDetail(deal.getTradeId(),DictCodeListExt1.一次性费用类型_佣金.getValue(),deal.getIsPayWithTrade(),deal.getCommissionAmt()));
            }
        });
        return feeDetailList;
    }

    protected abstract boolean needFinDealPledge(List<SettleTransDataPledge> settleTransDataPledgeList);

    @Override
    protected List<FinDealPledge> generateFinDealPledgeList(List<TrdProductDealDto> dealList, List<SettleTransDataPledge> settleTransDataPledgeList, List<SettleTransDataDto> settleTransDataDtos) {
        if(needFinDealPledge(settleTransDataPledgeList)) {
            List<FinDealPledge> dealPledgeList = new ArrayList<>(1);
            TrdProductDealDto productDealDto = dealList.get(0);
            SettleTransDataPledge settleTransDataPledge = settleTransDataPledgeList.get(0);
            // 变量：质押面值
            BigDecimal pledgeFaceValue = BigDecimal.ZERO;
            // 变量：质押数量
            BigDecimal pledgeNumber = BigDecimal.ZERO;
            // 变量：质押金额
            BigDecimal pledgeAmt = BigDecimal.ZERO;
            for(SettleTransDataPledge item : settleTransDataPledgeList) {
                pledgeFaceValue = ArithmeticUtil.doAdd(pledgeFaceValue, item.getPledgeFaceValue());
                pledgeNumber = ArithmeticUtil.doAdd(pledgeNumber, item.getPledgeNumber());
                pledgeAmt = ArithmeticUtil.doAdd(pledgeAmt, item.getPledgeAmt());
            }
            FinDealPledge finDealPledge = new FinDealPledge();
            // 金融产品代码
            finDealPledge.setFinprodId(productDealDto.getFinprodId());
            // 分支序号
            finDealPledge.setBranch(productDealDto.getBranch());
            // 质押金融产品代码
            finDealPledge.setPledgeFinprodId(settleTransDataPledge.getPledgeFinprodId());
            // 质押面值
            finDealPledge.setPledgeFaceValue(pledgeFaceValue);
            // 质押数量
            finDealPledge.setPledgeNumber(pledgeNumber);
            // 质押率
            finDealPledge.setPledgeRatio(settleTransDataPledge.getPledgeRatio());
            // 质押金额
            finDealPledge.setPledgeAmt(pledgeAmt);

            dealPledgeList.add(finDealPledge);
            return dealPledgeList;
        }
        return null;
    }

    /**
     * 生成金融产品交易明细
     */
    protected TrdProductDealDto createProductDeal(SettleTransDataDto settleTransData) {
        TrdProductDealDto trdProductDeal = new TrdProductDealDto();
        // 交易编号，按现有的交易编号生成规则，自动生成
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), settleTransData.getFinprodType2())) {
        	trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_正回购录入));
        }
        else {
        	trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_逆回购录入));
        }
        
        // 业务类型
        trdProductDeal.setBusiType(null);
        // 金融产品类型（投管分类）
        trdProductDeal.setFinprodType2(settleTransData.getFinprodType2());
        // 金融产品类型
        trdProductDeal.setFinprodType(settleTransData.getFinprodType());
        // 分支序号
        trdProductDeal.setBranch(0L);
        // 交易日
        trdProductDeal.setTradeDate(settleTransData.getTradeDate());
        // 起息日
        trdProductDeal.setVdate(settleTransData.getVdate());
        // 到期日
        trdProductDeal.setMdate(settleTransData.getMdate());
        // 交付日
        trdProductDeal.setPayDate(settleTransData.getMpayDate());
        // 交割日
        trdProductDeal.setSettleDate(settleTransData.getSettleDate());
        // 清算速度
        trdProductDeal.setSettleSpeed(settleTransData.getSettleSpeed());
        // 交易币种
        trdProductDeal.setCcy(settleTransData.getCcy());
        // 净值日期
        trdProductDeal.setCpriceDate(null);
        // 单位净价
        trdProductDeal.setUnitCprice(null);
        // 单位利息
        trdProductDeal.setUnitInt(null);
        // 单位全价
        trdProductDeal.setUnitFprice(null);
        // 单位面值
        trdProductDeal.setParValue(null);
        // 净价总额
        trdProductDeal.setCpriceAmt(null);
        // 投资目的
        trdProductDeal.setInvAim(null);
        // 结算方式
        trdProductDeal.setSettMode("DVP");
        // 交易方向
        trdProductDeal.setPs(settleTransData.getPs());
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
        // 权益登记日
        trdProductDeal.setRegDate(null);
        // 权益登记日份额
        trdProductDeal.setRegDateAmt(null);
        // 委托代码
        trdProductDeal.setEntrustId(settleTransData.getOrderId());
    	// 资金账户
        trdProductDeal.setAcctNo(settleTransData.getAcctNo());

        return trdProductDeal;
    }

    /**
     * 创建费用明细
     * @param tradeId 交易编号
     * @param feeType 费用类型
     * @param payWithTrade 是否随交易支付
     * @param feeAmt 费用金额
     * @return 费用明细
     */
    private TrdTradeFeeDetail buildTrdTradeFeeDetail(String tradeId, String feeType, String payWithTrade, BigDecimal feeAmt) {
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
        // 登记托管机构
        feeDetail.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
        return feeDetail;
    }

    /**
     * 根据交易明细创建指令
     * @param dealDto 交易明细
     * @return
     */
    protected StlPayOrders createStlPayOrders(TrdProductDealDto dealDto) {
        // 指令
        StlPayOrders stlPayOrders = new StlPayOrders();
        // 指令编号
        stlPayOrders.setOrderId(settleCommonService.buildBussinessIdBySeqno("9998"));
        // 结算日期
        stlPayOrders.setSettleDate(dealDto.getSettleDate());
        // 本方资金账户
        stlPayOrders.setMyAcctNo(dealDto.getAcctNo());
        // 结算金额
        stlPayOrders.setSettleAmt(dealDto.getTradeAmt());
        // 币种
        stlPayOrders.setCcy(dealDto.getCcy());
        // 是否需要发送
        stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
        // 是否汇总交易
        stlPayOrders.setIsTotalTrade(DictCodeList.是否_是.getValue());
        // 交易编号
        stlPayOrders.setTradeId(dealDto.getTradeId());
        // 指令状态
        stlPayOrders.setOrderType(DictCodeListExt1.交易状态_初始.getValue());
        // 投资组合代码
        stlPayOrders.setPortfolioId(dealDto.getPortfolioId());
        // 理财产品代码
        stlPayOrders.setProdId(dealDto.getFinprodId());
        // 备注
        stlPayOrders.setRemark("数据清算自动处理");
        // 划款类型
        stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
        return stlPayOrders;
    }

    /**
     * 更加交易明细和成交明细创建金融类交易信息
     * @param trdProductDealDto 投管交易明细
     * @param transDataDto 成交明细
     * @param totalmIntAmt 到期利息
     * @param totalmTradeAmt 到期本金
     * @return 金融类交易信息
     */
    protected FinTradeProductDto createTradeProductDto(TrdProductDealDto trdProductDealDto, SettleTransDataDto transDataDto, BigDecimal mIntAmt, BigDecimal mTradeAmt) {
        FinTradeProductDto tradeProductDto = new FinTradeProductDto();
        // 金融产品代码
        tradeProductDto.setFinprodId(trdProductDealDto.getFinprodId());
        // 分支序号
        tradeProductDto.setBranch(trdProductDealDto.getBranch());
        // 金融产品类型（估值核算）
        tradeProductDto.setFinprodType(trdProductDealDto.getFinprodType());
        // 金融产品类型（投管分类）
        tradeProductDto.setFinprodType2(trdProductDealDto.getFinprodType2());
        // 收益类型
        tradeProductDto.setProfitType(DictCodeList.收益类型_收益型.getValue());
        // 名义本金
        tradeProductDto.setPrin(trdProductDealDto.getShareAmt());
        // 本金币种
        tradeProductDto.setCcy(trdProductDealDto.getCcy());
        // 起息日
        tradeProductDto.setVdate(transDataDto.getVdate());
        // 到期日
        tradeProductDto.setMdate(transDataDto.getMdate());
        // 首期交付日
        tradeProductDto.setVpayDate(transDataDto.getVpayDate());
        // 到期交付日
        tradeProductDto.setMpayDate(transDataDto.getMpayDate());
        // 期限(天)
        tradeProductDto.setTerm(DateUtil.getBetweenDayNumber(transDataDto.getVdate(), transDataDto.getMdate()));
        // 利率类型
        tradeProductDto.setIntType(DictCodeList.利率类型_固定利率.getValue());
        // 计息基础
        tradeProductDto.setBasis(transDataDto.getBasis());
        // 到期结算方式
        tradeProductDto.setmDeliveryType("DVP");
        // 到期本金
        tradeProductDto.setmPrinAmt(trdProductDealDto.getShareAmt());
        // 到期利息
        BigDecimal mAmt = ArithmeticUtil.multiply(ArithmeticUtil.multiply(tradeProductDto.getmPrinAmt(), tradeProductDto.getIntRate()),
                ArithmeticUtil.divide(new BigDecimal(DateUtil.getBetweenDayNumber(tradeProductDto.getVpayDate(), tradeProductDto.getMpayDate())), 365, 14));
        // 到期利息
        tradeProductDto.setmIntAmt(mAmt);
        // 到期金额
        tradeProductDto.setmTradeAmt(tradeProductDto.getmPrinAmt().add(tradeProductDto.getmIntAmt()));
        // 交易场所
        tradeProductDto.setTradeMarket(trdProductDealDto.getTradeMarket());
        // 交易日历
        if(StringUtil.equals(trdProductDealDto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
            tradeProductDto.setCalendarId(CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID));
        }
        else if(StringUtil.equals(trdProductDealDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
            tradeProductDto.setCalendarId(CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SZJYS_CALID));
        }
        // 期限品种
        tradeProductDto.setTermType(transDataDto.getTermType());
        // 实际占款天数
        tradeProductDto.setActCapDays(DateUtil.getBetweenDayNumber(transDataDto.getVpayDate(), transDataDto.getMpayDate()));
        // 金融产品分类6（回购类型）
        tradeProductDto.setFinprodType6(transDataDto.getRepoType());
        // 金融产品分类7（回购交易方向）
        if(StringUtil.equals(transDataDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
            tradeProductDto.setFinprodType7(DictCodeListExt1.平安业务类型_正回购.getValue());
        }
        else {
            tradeProductDto.setFinprodType7(DictCodeListExt1.平安业务类型_逆回购.getValue());
        }
        // 金融产品分类8（投管资产业务类型）
        tradeProductDto.setFinprodType8(DictCodeList.平安资产类型_质押式回购.getValue());
        return tradeProductDto;
    }

    /**
     * 根据交易方向获取交易明细
     * @param dealList 交易明细列表
     * @param psList 交易方向列表
     * @return 成交明细
     */
    protected <T> T findByPs(List<T> list, List<String> psList) {
        if(CollectionUtils.isNotEmpty(list) && CollectionUtils.isNotEmpty(psList)) {
            for (T item : list) {
                if(item instanceof TrdProductDealDto) {
                    if(psList.contains(((TrdProductDealDto)item).getPs())) {
                        return item;
                    }
                }
                else if(item instanceof SettleTransDataDto) {
                    if(psList.contains(((SettleTransDataDto)item).getPs())) {
                        return item;
                    }
                }
            }
        }
        return null;
    }
}
