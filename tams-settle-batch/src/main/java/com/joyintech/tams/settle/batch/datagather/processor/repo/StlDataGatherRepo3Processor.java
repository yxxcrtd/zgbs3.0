package com.joyintech.tams.settle.batch.datagather.processor.repo;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransDataPledge;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 债券质押式协议回购（提前终止）
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherRepo3Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherRepo3Processor extends StlDataGatherRepoBaseProcessor {
    @Autowired
    private StlDataGatherService stlDataGatherService;

    @Override
    protected void setDealTotal(TotalTrdProductDealDto dealTotal, List<TrdProductDealDto> dealList) {
        // 交易方向为”还本到账“的交易明细
        TrdProductDealDto dealDto = findByPs(dealList, Arrays.asList(DictCodeList.交易方向_还本到账.getValue()));
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
            dealTotal.setPs(DictCodeList.交易方向_提前终止.getValue());
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
            // 委托代码
            dealTotal.setEntrustId(dealDto.getEntrustId());
            // 投管业务类型
            if(StringUtil.equals(dealDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                dealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_正回购到期.getValue());
            }
            else {
                dealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_逆回购到期.getValue());
            }
        }
    }

    @Override
    protected List<TrdProductDealDto> generateProductDeal(List<SettleTransDataDto> settleTransDataDtos) {
        List<TrdProductDealDto> dealDtoList = new ArrayList<>(2);
        settleTransDataDtos.forEach(settleTransData -> {
            TrdProductDealDto trdProductDeal = createProductDeal(settleTransData);
            // 交易方向：还本到账、利息到账 对应的成交明细
            if(StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_还本到账.getValue()) || StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_收息到账.getValue())) {
                // 起息日
                trdProductDeal.setVdate(settleTransData.getVdate());
                // 到期日
                trdProductDeal.setMdate(settleTransData.getMdate());
            }
            else {
                // 起息日
                trdProductDeal.setVdate(null);
                // 到期日
                trdProductDeal.setMdate(null);
            }
            // 交易份额，交易本金，利息总额，全价总额，随交易支付费用，交易金额，未随交易支付费用，总交易费用，成交编号
            calcFeeAmtPay(trdProductDeal, settleTransDataDtos);
            // 投管业务类型
            if(StringUtil.equals(settleTransData.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_正回购到期.getValue());
            }
            else {
                trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_逆回购到期.getValue());
            }
            // 投管交易编号
            trdProductDeal.setFinprodId(stlDataGatherService.selectBTradeId(settleTransData.getApplyTradeId(), null));

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
                    stlPayOrders.setPs(DictCodeList.收付方向_收入.getValue());
                }
                else {
                    stlPayOrders.setPs(DictCodeListExt1.收付方向_支出.getValue());
                }
                payOrdersList.add(stlPayOrders);
            });
        }
        return payOrdersList;
    }

    /**
     * 设置各种金额
     */
    private void calcFeeAmtPay(TrdProductDealDto trdProductDeal, List<SettleTransDataDto> settleTransDataDtoList) {
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
        List<String> dealIdList = new ArrayList<>();
        for (SettleTransDataDto item : settleTransDataDtoList) {
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
        }
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
    }

    @Override
    protected boolean needFinDealPledge(List<SettleTransDataPledge> settleTransDataPledgeList) {
        return false;
    }
}
