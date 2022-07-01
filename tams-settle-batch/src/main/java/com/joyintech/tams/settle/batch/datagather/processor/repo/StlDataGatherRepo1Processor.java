package com.joyintech.tams.settle.batch.datagather.processor.repo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.SettleTransDataPledge;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.settle.batch.datagather.dto.FinTradeProductDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;

/**
 * 债券质押式回购（首期）
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherRepo1Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherRepo1Processor extends StlDataGatherRepoBaseProcessor {
	
	@Autowired(required = false)
    private StlDataGatherService stlDataGatherService;

    @Override
    protected List<TotalTrdProductDealDto> generateDealTotal(List<TrdProductDealDto> dealList) {
        return null;
    }

    @Override
    protected List<TrdProductDealDto> generateProductDeal(List<SettleTransDataDto> settleTransDataDtos) {
    	List<TrdProductDealDto> list = new ArrayList<TrdProductDealDto>();
    	for (SettleTransDataDto settleTransData : settleTransDataDtos) {
	        // 创建交易信息
	        TrdProductDealDto trdProductDeal = createProductDeal(settleTransData);
	        // 金融产品代码
	        trdProductDeal.setFinprodId(trdProductDeal.getTradeId());
	        // 起息日
	        trdProductDeal.setVdate(settleTransData.getVdate());
	        // 到期日
	        trdProductDeal.setMdate(settleTransData.getMdate());
	        // 交付日
	        trdProductDeal.setPayDate(settleTransData.getVpayDate());
	        // 交易份额，交易本金，随交易支付费用，交易金额，未随交易支付费用，总交易费用，成交编号
	        //calcFeeAmtPay(trdProductDeal, settleTransDataDtos);
	        // 清算速度
	        trdProductDeal.setSettleSpeed(0L);
	        // 交易份额
	        trdProductDeal.setShareAmt(settleTransData.getShareAmt());
	        // 交易本金
	        trdProductDeal.setPrinAmt(settleTransData.getShareAmt());
	        // 利息总额
	        trdProductDeal.setIntAmt(settleTransData.getIntAmt());
	        // 全价总额
	        trdProductDeal.setFpriceAmt(settleTransData.getFpriceAmt());
	        // 随交易支付费用
	        if(StringUtil.equals(DictCodeList.是否_是.getValue(), settleTransData.getIsPayWithTrade())) {
	        	trdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(settleTransData.getJsfAmt(), settleTransData.getGhfAmt(), settleTransData.getZgfAmt(), settleTransData.getSxfAmt(), settleTransData.getSettleFee(), settleTransData.getStampFee(), settleTransData.getCommissionAmt()));
	        }
	        else {
	        	trdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(settleTransData.getJsfAmt(), settleTransData.getGhfAmt(), settleTransData.getZgfAmt(), settleTransData.getSxfAmt(), settleTransData.getSettleFee(), settleTransData.getStampFee()));
	        }
	        // 交易金额
	        trdProductDeal.setTradeAmt(settleTransData.getTradeAmt());
	        // 未随交易支付费用
	        if(!StringUtil.equals(DictCodeList.是否_是.getValue(), settleTransData.getIsPayWithTrade())) {
	        	trdProductDeal.setFeeAmtUnpay(settleTransData.getCommissionAmt());
	        }
	        // 总交易费用
	        trdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(trdProductDeal.getFeeAmtPay(), trdProductDeal.getFeeAmtUnpay()));
	        // 成交编号
	        trdProductDeal.setDealId(settleTransData.getTradeId());
	        // 利息总额
	        trdProductDeal.setIntAmt(null);
	        // 全价总额
	        trdProductDeal.setFpriceAmt(null);
	        // 交易方向
	        trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
	        // 汇总交易编号
	        trdProductDeal.setTotleTradeId(null);
	        // 投管业务类型
	        if(StringUtil.equals(settleTransData.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
	            trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_正回购.getValue());
	        }
	        else {
	            trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_逆回购.getValue());
	        }
            BigDecimal hglx = ArithmeticUtil.multiply(ArithmeticUtil.multiply(settleTransData.getmPrinAmt(), settleTransData.getIntRate()), 
					ArithmeticUtil.divide(new BigDecimal(DateUtil.getBetweenDayNumber(settleTransData.getVpayDate(), settleTransData.getMpayDate())), 365, 14));
	        // 到期利息
	        trdProductDeal.setmIntAmt(hglx);
	        // 到期金额
	        trdProductDeal.setmTradeAmt(ArithmeticUtil.doAdd(settleTransData.getmPrinAmt(), hglx));
	        // 利率
	        trdProductDeal.setRate(settleTransData.getIntRate());
	        // 印花税
	        trdProductDeal.setStampFee(settleTransData.getStampFee());
	        // 经手费
	        trdProductDeal.setJsfAmt(settleTransData.getJsfAmt());
	        // 过户费
	        trdProductDeal.setGhfAmt(settleTransData.getGhfAmt());
	        // 证管费
	        trdProductDeal.setZgfAmt(settleTransData.getZgfAmt());
	        // 手续费
	        trdProductDeal.setSxfAmt(settleTransData.getSxfAmt());
	        // 结算费
	        trdProductDeal.setSettleFee(settleTransData.getSettleFee());
	        // 佣金
	        trdProductDeal.setCommissionAmt(settleTransData.getCommissionAmt());
	        list.add(trdProductDeal);
    	}
        return list;
    }

    @Override
    protected List<StlPayOrders> generateStlPayOrdersList(List<TotalTrdProductDealDto> dealTotalList, List<TrdProductDealDto> dealList, List<SettleTransDataDto> settleTransDataDtos) {
        List<StlPayOrders> payOrdersList = new ArrayList<>(dealList.size());
        if(CollectionUtils.isNotEmpty(dealList)) {
            dealList.forEach(dealDto -> {
                StlPayOrders stlPayOrders = createStlPayOrders(dealDto);
                // 收付方向
                if(StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), dealDto.getFinprodType2())) {
                    stlPayOrders.setPs(DictCodeListExt1.收付方向_支出.getValue());
                }
                else {
                    stlPayOrders.setPs(DictCodeList.收付方向_收入.getValue());
                }
                payOrdersList.add(stlPayOrders);
            });
        }
        return payOrdersList;
    }

    @Override
    protected List<FinTradeProductDto> generateFinTradeProductList(List<TrdProductDealDto> dealList, List<SettleTransDataDto> settleTransDataDtos) {
        List<FinTradeProductDto> tradeProductDtoList = new ArrayList<>();
        for (TrdProductDealDto trdProductDealDto : dealList) {
        	FinTradeProductDto finTradeProductDto = createTradeProductDto(trdProductDealDto, settleTransDataDtos.get(0), trdProductDealDto.getmIntAmt(), trdProductDealDto.getmTradeAmt());
            finTradeProductDto.setIntRate(trdProductDealDto.getRate());
        	tradeProductDtoList.add(finTradeProductDto);
        }
        return tradeProductDtoList;
    }

    @Override
    protected boolean needFinDealPledge(List<SettleTransDataPledge> settleTransDataPledgeList) {
    	if (settleTransDataPledgeList == null || settleTransDataPledgeList.size() < 1) {
    		return false;
    	}
        return true;
    }
    
    protected List<EtrFinishStatus> generateEtrFinishStatusList(List<TotalTrdProductDealDto> dealTotalList, List<TrdProductDealDto> dealList){
    	List<EtrFinishStatus> etrFinishStatusList = new ArrayList<>();
    	EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
    	BigDecimal tradeAmt = BigDecimal.ZERO;
    	BigDecimal shareAmt = BigDecimal.ZERO;
    	for (TrdProductDealDto trdProductDealDto : dealList) {
    		tradeAmt = ArithmeticUtil.doAdd(tradeAmt, trdProductDealDto.getTradeAmt());
    		shareAmt = ArithmeticUtil.doAdd(shareAmt, trdProductDealDto.getShareAmt());
    	}
    	etrFinishStatus.setEntrustId(dealList.get(0).getEntrustId());
		etrFinishStatus.setTradeId(dealList.get(0).getEntrustId());
		EtrEntrustAdd etrEntrustAdd = stlDataGatherService.selectEtrEntrustByEntrustId(dealList.get(0).getEntrustId());
		if (etrEntrustAdd != null && StringUtil.equals(etrEntrustAdd.getTargetType(), DictCodeListExt3.目标类型_数量.getValue())){
			etrFinishStatus.setEntFinishAmt(shareAmt);
		}
		else {
			etrFinishStatus.setEntFinishAmt(tradeAmt);
		}
		etrFinishStatusList.add(etrFinishStatus);
    	return etrFinishStatusList;
    }
}
