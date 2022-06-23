package com.joyintech.tams.settle.batch.datagather.processor.repo;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransDataPledge;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.settle.batch.datagather.dto.FinTradeProductDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 债券质押式回购（到期续作）
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherRepo2Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherRepo2Processor extends StlDataGatherRepoBaseProcessor {

    @Override
    protected void setDealTotal(TotalTrdProductDealDto dealTotal, List<TrdProductDealDto> dealList) {
        // 取交易方向为”买入“的交易明细
        TrdProductDealDto dealDto = findByPs(dealList, Arrays.asList(DictCodeList.交易方向_买入.getValue()));
        if(null != dealDto) {
            // 金融产品代码
            dealTotal.setFinprodId(dealDto.getFinprodId());
            // 金融产品类型（投管分类）
            dealTotal.setFinprodType2(dealDto.getFinprodType2());
            // 分支序号
            dealTotal.setBranch(dealDto.getBranch());
            // 交易日
            dealTotal.setTradeDate(dealDto.getTradeDate());
            // 交付日
            dealTotal.setPayDate(dealDto.getPayDate());
            // 交割日
            dealTotal.setSettleDate(dealDto.getSettleDate());
            // 清算速度
            dealTotal.setSettleSpeed(dealDto.getSettleSpeed());
            // 交易币种
            dealTotal.setCcy(dealDto.getCcy());
            // 交易份额
            dealTotal.setShareAmt(dealDto.getShareAmt());
            // 交易方向
            dealTotal.setPs(DictCodeList.交易方向_到期续做.getValue());
            // 证券管理户代码
            dealTotal.setSecManageAcctId(dealDto.getSecManageAcctId());
            // 交易场所
            dealTotal.setTradeMarket(dealDto.getTradeMarket());
            // 交易平台
            dealTotal.setTradePlat(dealDto.getTradePlat());
            // 交易市场
            dealTotal.setTradeMarketMode(dealDto.getTradeMarketMode());
            // 投资组合代码
            dealTotal.setPortfolioId(dealDto.getPortfolioId());
            // 交收方式
            dealTotal.setSettlemenType(dealDto.getSettlemenType());
            // 席位号
            dealTotal.setSeatId(dealDto.getSeatId());
            // 利率
            dealTotal.setRate(dealDto.getRate());
            // 回购期限（天）
            dealTotal.setRepoTerm(dealDto.getRepoTerm());
            // 期限品种
            dealTotal.setTermType(dealDto.getTermType());
            // 投管业务类型 TODO
            // 委托代码
            dealTotal.setEntrustId(dealDto.getEntrustId());
        }
    }

    @Override
    protected List<TrdProductDealDto> generateProductDeal(List<SettleTransDataDto> settleTransDataDtos) {
        List<TrdProductDealDto> dealDtoList = new ArrayList<>(3);
        settleTransDataDtos.forEach(settleTransData -> {
            TrdProductDealDto trdProductDeal = createProductDeal(settleTransData);
            // 金融产品代码
            trdProductDeal.setFinprodId(trdProductDeal.getTradeId());
            // 交易方向：还本到账、利息到账 对应的成交明细
            if(StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_还本到账.getValue()) || StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_收息到账.getValue())) {
                // 起息日
                trdProductDeal.setVdate(settleTransData.getVdate());
                // 到期日
                trdProductDeal.setMdate(settleTransData.getMdate());
                if(StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_还本到账.getValue())) {
                    // 交易方向
                    trdProductDeal.setPs(DictCodeList.交易方向_还本到账.getValue());
                }
                else {
                    // 交易方向
                    trdProductDeal.setPs(DictCodeList.交易方向_收息到账.getValue());
                }
                // 投管业务类型
                if(StringUtil.equals(settleTransData.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                    trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_正回购到期.getValue());
                }
                else if(StringUtil.equals(settleTransData.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                    trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_逆回购到期.getValue());
                }
            }
            else {
                // 起息日
                trdProductDeal.setVdate(null);
                // 到期日
                trdProductDeal.setMdate(null);
            }
            // 交易方向：到期续作
            if(StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_到期续做.getValue())) {
                // 交付日
                trdProductDeal.setPayDate(settleTransData.getVpayDate());
                // 交易方向
                trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
                // 投管业务类型
                if(StringUtil.equals(settleTransData.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                    trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_正回购.getValue());
                }
                else if(StringUtil.equals(settleTransData.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                    trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_逆回购.getValue());
                }
            }
            // 交易份额
            trdProductDeal.setShareAmt(settleTransData.getShareAmt());
            // 交易本金
            trdProductDeal.setPrinAmt(settleTransData.getPrinAmt());
            // 利息总额
            trdProductDeal.setIntAmt(settleTransData.getIntAmt());
            // 全价总额
            trdProductDeal.setFpriceAmt(settleTransData.getFpriceAmt());
            // 随交易支付费用
            BigDecimal totalFeeAmtPay = ArithmeticUtil.doAdd(settleTransData.getJsfAmt(), settleTransData.getGhfAmt(), settleTransData.getZgfAmt(), settleTransData.getSxfAmt(), settleTransData.getJsfAmt());
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), settleTransData.getIsPayWithTrade())) {
                totalFeeAmtPay = ArithmeticUtil.doAdd(totalFeeAmtPay, settleTransData.getCommissionAmt());
            }
            else {
                // 未随交易支付费用
                trdProductDeal.setFeeAmtUnpay(settleTransData.getCommissionAmt());
            }
            trdProductDeal.setFeeAmtPay(totalFeeAmtPay);
            // 交易金额
            trdProductDeal.setTradeAmt(settleTransData.getTradeAmt());
            // 总交易费用
            trdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(totalFeeAmtPay, trdProductDeal.getFeeAmtUnpay()));
            // 成交编号
            trdProductDeal.setDealId(settleTransData.getTradeId());
            dealDtoList.add(trdProductDeal);
        });
        return dealDtoList;
    }

    @Override
    protected List<StlPayOrders> generateStlPayOrdersList(List<TotalTrdProductDealDto> dealTotalList, List<TrdProductDealDto> dealList, List<SettleTransDataDto> settleTransDataDtos) {
        List<StlPayOrders> payOrdersList = new ArrayList<>(dealList.size());
        if(CollectionUtils.isNotEmpty(dealList)) {
            dealList.forEach(dealDto -> {
                StlPayOrders stlPayOrders = createStlPayOrders(dealDto);
                // 收付方向
                if(StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), dealDto.getFinprodType2())) {
                    if(StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dealDto.getPs())) {
                        stlPayOrders.setPs(DictCodeListExt1.收付方向_支出.getValue());
                    }
                    else if(StringUtil.equals(DictCodeList.交易方向_本金返还.getValue(), dealDto.getPs()) || StringUtil.equals(DictCodeList.交易方向_收息到账.getValue(), dealDto.getPs())) {
                        stlPayOrders.setPs(DictCodeList.收付方向_收入.getValue());
                    }
                }
                else if(StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), dealDto.getFinprodType2())) {
                    if(StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dealDto.getPs())) {
                        stlPayOrders.setPs(DictCodeList.收付方向_收入.getValue());
                    }
                    else if(StringUtil.equals(DictCodeList.交易方向_本金返还.getValue(), dealDto.getPs()) || StringUtil.equals(DictCodeList.交易方向_收息到账.getValue(), dealDto.getPs())) {
                        stlPayOrders.setPs(DictCodeListExt1.收付方向_支出.getValue());
                    }
                }
                payOrdersList.add(stlPayOrders);
            });
        }
        return payOrdersList;
    }

    @Override
    protected List<FinTradeProductDto> generateFinTradeProductList(List<TrdProductDealDto> dealList, List<SettleTransDataDto> settleTransDataDtos) {
        // 变量：到期利息总额
        BigDecimal totalmIntAmt = BigDecimal.ZERO;
        // 变量：到期金融总额
        BigDecimal totalmTradeAmt = BigDecimal.ZERO;
        for (SettleTransDataDto item : settleTransDataDtos) {
            totalmIntAmt = ArithmeticUtil.doAdd(totalmIntAmt, item.getmIntAmt());
            totalmTradeAmt = ArithmeticUtil.doAdd(totalmTradeAmt, item.getmTradeAmt());
        }
        // 交易方向为”到期续作“的成交明细
        SettleTransDataDto transDataDto = findByPs(settleTransDataDtos, Arrays.asList(DictCodeList.交易方向_到期续做.getValue()));
        FinTradeProductDto  finTradeProductDto = createTradeProductDto(dealList.get(0), transDataDto, totalmIntAmt, totalmTradeAmt);
        // 固定利率
        finTradeProductDto.setIntRate(transDataDto.getUnitCprice());
        return Arrays.asList(finTradeProductDto);
    }

    @Override
    protected boolean needFinDealPledge(List<SettleTransDataPledge> settleTransDataPledgeList) {
    	if (settleTransDataPledgeList == null || settleTransDataPledgeList.size() < 1) {
    		return false;
    	}
        return true;
    }
}
