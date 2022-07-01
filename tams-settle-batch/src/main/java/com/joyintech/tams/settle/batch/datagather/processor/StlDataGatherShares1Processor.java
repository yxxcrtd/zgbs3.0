package com.joyintech.tams.settle.batch.datagather.processor;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 股票新股中签交易处理
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherShares1Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherShares1Processor extends StlDataGatherProcessor {
	
	@Autowired(required = false)
    private StlDataGatherService stlDataGatherService;

	@Autowired
	private SettleCommonService settleCommonService;

    @Override
    protected List<TrdProductDealDto> generateProductDeal(List<SettleTransDataDto> settleTransDataDtos) {
    	List<TrdProductDealDto> trdProductDealList = new ArrayList<>();
    	TrdProductDealDto trdProductDeal = new TrdProductDealDto();
		setSharesTrdProductDeal(trdProductDeal, settleTransDataDtos);
		trdProductDealList.add(trdProductDeal);
    	
    	return trdProductDealList;
    }

	@Override
	protected String getSecType() {
		return DictCodeListExt1.证券品种_股票.getValue();
	}

    @Override
    protected List<String> getFinprodType() {return Arrays.asList(DictCodeListExt1.金融产品类型_投管用_股票.getValue());}

    /**
     * 生成股票新股中签交易TRD_PRODUCT_DEAL的数据
     * @param trdProductDeal TrdProductDealDto
     * @param settleTransDataDtos List<SettleTransDataDto>
     */
    private void setSharesTrdProductDeal(TrdProductDealDto trdProductDeal, List<SettleTransDataDto> settleTransDataDtos) {
    	if (settleTransDataDtos != null && settleTransDataDtos.size() > 0) {
    		// 交易金额_原始汇总
    		BigDecimal tradeAmtRawSum = BigDecimal.ZERO;
    		// ∑交易份额
    		BigDecimal shareAmtSum = BigDecimal.ZERO;
    		// ∑交易本金
    		BigDecimal prinAmtSum = BigDecimal.ZERO;
    		// ∑交易金额
    		BigDecimal tradeAmtSum = BigDecimal.ZERO;
    		// 随交易支付费用
    		BigDecimal feeAmtPay = BigDecimal.ZERO;
    		// 未随交易支付费用
    		BigDecimal feeAmtUnpay = BigDecimal.ZERO;
    		// 经手费
    		BigDecimal jsfAmt = BigDecimal.ZERO;
    		// 过户费
    		BigDecimal ghfAmt = BigDecimal.ZERO;
    		// 证管费
    		BigDecimal zgfAmt = BigDecimal.ZERO;
    		// 手续费
    		BigDecimal sxfAmt = BigDecimal.ZERO;
    		// 印花税
    		BigDecimal stampFeeAmt = BigDecimal.ZERO;
    		// 结算费
    		BigDecimal settleFee = BigDecimal.ZERO;
    		// 佣金
    		BigDecimal commissionAmt = BigDecimal.ZERO;
    		// ∑全价总额
    		BigDecimal cpriceAmtSum = BigDecimal.ZERO;
    		
    		for (SettleTransDataDto settleTransDataDto : settleTransDataDtos) {
    			tradeAmtRawSum = ArithmeticUtil.doAdd(tradeAmtRawSum, settleTransDataDto.getTradeAmtRaw());
    			shareAmtSum = ArithmeticUtil.doAdd(shareAmtSum, settleTransDataDto.getShareAmt());
    			prinAmtSum = ArithmeticUtil.doAdd(prinAmtSum, settleTransDataDto.getPrinAmt());
    			tradeAmtSum = ArithmeticUtil.doAdd(tradeAmtSum, settleTransDataDto.getTradeAmt());
    			cpriceAmtSum = ArithmeticUtil.doAdd(cpriceAmtSum, settleTransDataDto.getFpriceAmt());
    			if (StringUtil.equals(DictCodeList.是否_是.getValue(), settleTransDataDto.getIsPayWithTrade())) {
    				feeAmtPay = ArithmeticUtil.doAdd(feeAmtPay, settleTransDataDto.getJsfAmt(), settleTransDataDto.getGhfAmt(), settleTransDataDto.getZgfAmt(), 
    						settleTransDataDto.getSxfAmt(), settleTransDataDto.getSettleFee(), settleTransDataDto.getStampFee(), settleTransDataDto.getCommissionAmt());
    			}
    			else {
    				feeAmtPay = ArithmeticUtil.doAdd(feeAmtPay, settleTransDataDto.getJsfAmt(), settleTransDataDto.getGhfAmt(), settleTransDataDto.getZgfAmt(), 
    						settleTransDataDto.getSxfAmt(), settleTransDataDto.getSettleFee(), settleTransDataDto.getStampFee());
    				feeAmtUnpay = ArithmeticUtil.doAdd(feeAmtUnpay, settleTransDataDto.getCommissionAmt());
    			}
    			jsfAmt = ArithmeticUtil.doAdd(jsfAmt, settleTransDataDto.getJsfAmt());
    			ghfAmt = ArithmeticUtil.doAdd(ghfAmt, settleTransDataDto.getGhfAmt());
    			zgfAmt = ArithmeticUtil.doAdd(zgfAmt, settleTransDataDto.getZgfAmt());
    			sxfAmt = ArithmeticUtil.doAdd(sxfAmt, settleTransDataDto.getSxfAmt());
    			settleFee = ArithmeticUtil.doAdd(settleFee, settleTransDataDto.getSettleFee());
    			stampFeeAmt = ArithmeticUtil.doAdd(stampFeeAmt, settleTransDataDto.getStampFee());
    			commissionAmt = ArithmeticUtil.doAdd(commissionAmt, settleTransDataDto.getCommissionAmt());
    		}
	    	// 交易编号
	    	String tradeId = settleCommonService.buildBussinessCodeExt(DictCodeListExt2.共通业务类型_股票投资交易);
	    	trdProductDeal.setTradeId(tradeId);
	    	// 金融产品代码
	    	trdProductDeal.setFinprodId(settleTransDataDtos.get(0).getFinprodId());
	    	// 金融产品类型
	    	trdProductDeal.setFinprodType(settleTransDataDtos.get(0).getFinprodType());
	    	// 金融产品类型（投管分类）
	    	trdProductDeal.setFinprodType2(settleTransDataDtos.get(0).getFinprodType2());
			// 业务类型
			trdProductDeal.setBusiType(settleTransDataDtos.get(0).getBusiType());
	    	// 分支序号
	    	trdProductDeal.setBranch(0L);
	    	// 交易日
	    	trdProductDeal.setTradeDate(settleTransDataDtos.get(0).getTradeDate());
	    	// 交割日
	    	trdProductDeal.setSettleDate(settleTransDataDtos.get(0).getSettleDate());
	    	// 清算速度
	    	trdProductDeal.setSettleSpeed(settleTransDataDtos.get(0).getSettleSpeed());
	    	// 交易币种
	    	trdProductDeal.setCcy(settleTransDataDtos.get(0).getCcy());
			// 净值日期
			trdProductDeal.setCpriceDate(settleTransDataDtos.get(0).getTradeDate());
	    	// 单位净价
    		BigDecimal unitCprice = ArithmeticUtil.divide(cpriceAmtSum, shareAmtSum, 14);
    		trdProductDeal.setUnitCprice(unitCprice);
    		// 配股需要计算价格
    		if (StringUtil.equals(settleTransDataDtos.get(0).getPs(), DictCodeListExt2.交易方向_配股.getValue())){
    			trdProductDeal.setUnitCprice(ArithmeticUtil.divide(tradeAmtSum, shareAmtSum, 14));
    		}
	    	// 净价总额
    		trdProductDeal.setCpriceAmt(cpriceAmtSum);
	    	// 交易份额
    		trdProductDeal.setShareAmt(shareAmtSum);
	    	// 交易本金
    		trdProductDeal.setPrinAmt(prinAmtSum);
	    	// 随交易支付费用
    		trdProductDeal.setFeeAmtPay(feeAmtPay);
	    	// 交易金额
    		trdProductDeal.setTradeAmt(tradeAmtSum);
	    	// 未随交易支付费用
    		trdProductDeal.setFeeAmtUnpay(feeAmtUnpay);
	    	// 总交易费用
    		trdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(feeAmtPay, feeAmtUnpay));
	    	// 交易方向
    		trdProductDeal.setPs(settleTransDataDtos.get(0).getPs());
	    	// 交易状态
    		trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
	    	// 是否已撤销
    		trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
	    	// 是否外部交易
	    	trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
	    	// 交易场所
	    	trdProductDeal.setTradeMarket(settleTransDataDtos.get(0).getTradeMarket());
	    	// 交易平台
	    	trdProductDeal.setTradePlat(settleTransDataDtos.get(0).getTradePlat());
	    	// 交易市场
	    	trdProductDeal.setTradeMarketMode(settleTransDataDtos.get(0).getTradeMarketMode());
	    	// 证券管理户代码
	    	trdProductDeal.setSecManageAcctId(settleTransDataDtos.get(0).getSecAcctId());
	    	// 登记托管机构
	    	trdProductDeal.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
	    	// 投管业务类型
	    	trdProductDeal.setTgBusiType(DictCodeListExt2.平安业务类型_股票交易.getValue());
	    	// 交收方式
	    	trdProductDeal.setSettlemenType(settleTransDataDtos.get(0).getSettleMode());
	    	// 席位号
	    	trdProductDeal.setSeatId(settleTransDataDtos.get(0).getSeatNumber());
	    	// 投资组合代码
	    	trdProductDeal.setPortfolioId(settleTransDataDtos.get(0).getPortfolioId());
	    	// 委托代码
	    	trdProductDeal.setEntrustId(settleTransDataDtos.get(0).getOrderId());
	    	// 成交编号
	    	String dealId = settleTransDataDtos.stream().map(SettleTransDataDto::getTradeId).collect(Collectors.joining(GlobalConstant.COMMA));
	    	trdProductDeal.setDealId(dealId);
	    	// 经手费
	    	trdProductDeal.setJsfAmt(jsfAmt);
    		// 过户费
	    	trdProductDeal.setGhfAmt(ghfAmt);
    		// 证管费
	    	trdProductDeal.setZgfAmt(zgfAmt);
    		// 手续费
	    	trdProductDeal.setSxfAmt(sxfAmt);
    		// 结算费
	    	trdProductDeal.setSettleFee(settleFee);
	    	// 印花税
	    	trdProductDeal.setStampFee(stampFeeAmt);
    		// 佣金
	    	trdProductDeal.setCommissionAmt(commissionAmt);
	    	// 资金账户
	    	trdProductDeal.setAcctNo(settleTransDataDtos.get(0).getAcctNo());
	        // 委托代码
	    	trdProductDeal.setEntrustId(settleTransDataDtos.get(0).getOrderId());
    	}
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
            ptlTrade.setBusiType(DictCodeListExt2.共通业务类型_股票投资交易.getValue());
            return ptlTrade;
        }).collect(Collectors.toList());
    }

    @Override
    protected List<TrdTradeFeeDetail> generateTradeFeeDetail(List<TrdProductDealDto> dealList) {
    	List<TrdTradeFeeDetail> result = new ArrayList<TrdTradeFeeDetail>();
    	if (dealList != null && dealList.size() > 0) {
    		for (TrdProductDealDto trdProductDealDto : dealList) {
    			// 经手费
    	    	if (trdProductDealDto != null && trdProductDealDto.getJsfAmt().compareTo(new BigDecimal(0)) > 0) {
    	    		TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
    	    		// 交易编号
    	    		trdTradeFeeDetail.setTradeId(trdProductDealDto.getTradeId());
    	    		// 费用类型
    	    		trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_经手费.getValue());
    	    		// 是否随交易支付
    	    		trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
    	    		// 费用金额
    	    		trdTradeFeeDetail.setFeeAmt(trdProductDealDto.getJsfAmt());
    	            // 登记托管机构
    	    		trdTradeFeeDetail.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
    	    		result.add(trdTradeFeeDetail);
    	    	}
        		// 过户费
    	    	if (trdProductDealDto != null && trdProductDealDto.getGhfAmt().compareTo(new BigDecimal(0)) > 0) {
    	    		TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
    	    		// 交易编号
    	    		trdTradeFeeDetail.setTradeId(trdProductDealDto.getTradeId());
    	    		// 费用类型
    	    		trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_过户费.getValue());
    	    		// 是否随交易支付
    	    		trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
    	    		// 费用金额
    	    		trdTradeFeeDetail.setFeeAmt(trdProductDealDto.getGhfAmt());
    	            // 登记托管机构
    	    		trdTradeFeeDetail.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
    	    		result.add(trdTradeFeeDetail);
    	    	}
        		// 证管费
    	    	if (trdProductDealDto != null && trdProductDealDto.getZgfAmt().compareTo(new BigDecimal(0)) > 0) {
    	    		TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
    	    		// 交易编号
    	    		trdTradeFeeDetail.setTradeId(trdProductDealDto.getTradeId());
    	    		// 费用类型
    	    		trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_证管费.getValue());
    	    		// 是否随交易支付
    	    		trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
    	    		// 费用金额
    	    		trdTradeFeeDetail.setFeeAmt(trdProductDealDto.getZgfAmt());
    	            // 登记托管机构
    	    		trdTradeFeeDetail.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
    	    		result.add(trdTradeFeeDetail);
    	    	}
        		// 手续费
    	    	if (trdProductDealDto != null && trdProductDealDto.getSxfAmt().compareTo(new BigDecimal(0)) > 0) {
    	    		TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
    	    		// 交易编号
    	    		trdTradeFeeDetail.setTradeId(trdProductDealDto.getTradeId());
    	    		// 费用类型
    	    		trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_交易手续费.getValue());
    	    		// 是否随交易支付
    	    		trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
    	    		// 费用金额
    	    		trdTradeFeeDetail.setFeeAmt(trdProductDealDto.getSxfAmt());
    	            // 登记托管机构
    	    		trdTradeFeeDetail.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
    	    		result.add(trdTradeFeeDetail);
    	    	}
        		// 结算费
    	    	if (trdProductDealDto != null && trdProductDealDto.getSettleFee().compareTo(new BigDecimal(0)) > 0) {
    	    		TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
    	    		// 交易编号
    	    		trdTradeFeeDetail.setTradeId(trdProductDealDto.getTradeId());
    	    		// 费用类型
    	    		trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_结算费.getValue());
    	    		// 是否随交易支付
    	    		trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
    	    		// 费用金额
    	    		trdTradeFeeDetail.setFeeAmt(trdProductDealDto.getSettleFee());
    	            // 登记托管机构
    	    		trdTradeFeeDetail.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
    	    		result.add(trdTradeFeeDetail);
    	    	}
        		// 印花税
    	    	if (trdProductDealDto != null && trdProductDealDto.getStampFee().compareTo(new BigDecimal(0)) > 0) {
    	    		TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
    	    		// 交易编号
    	    		trdTradeFeeDetail.setTradeId(trdProductDealDto.getTradeId());
    	    		// 费用类型
    	    		trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_印花税.getValue());
    	    		// 是否随交易支付
    	    		trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
    	    		// 费用金额
    	    		trdTradeFeeDetail.setFeeAmt(trdProductDealDto.getStampFee());
    	            // 登记托管机构
    	    		trdTradeFeeDetail.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
    	    		result.add(trdTradeFeeDetail);
    	    	}
        		// 佣金
    	    	if (trdProductDealDto != null && trdProductDealDto.getCommissionAmt().compareTo(new BigDecimal(0)) > 0) {
    	    		TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
    	    		// 交易编号
    	    		trdTradeFeeDetail.setTradeId(trdProductDealDto.getTradeId());
    	    		// 费用类型
    	    		trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
    	    		// 是否随交易支付
    	    		if (trdProductDealDto.getFeeAmtUnpay() != null && trdProductDealDto.getFeeAmtUnpay().compareTo(new BigDecimal(0)) > 0) {
    	    			trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
    	    		}
    	    		else {
    	    			trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
    	    		}
    	    		// 费用金额
    	    		trdTradeFeeDetail.setFeeAmt(trdProductDealDto.getCommissionAmt());
    	            // 登记托管机构
    	    		trdTradeFeeDetail.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
    	    		result.add(trdTradeFeeDetail);
    	    	}
    		}
    	}
    	return result;
    }


    @Override
    protected List<StlPayOrders> generateStlPayOrdersList(List<TotalTrdProductDealDto> totalTrdProductDealDtoList, List<TrdProductDealDto> dealList, List<SettleTransDataDto> settleTransDataDtos) {
    	return dealList.stream().map(trdProductDealDto -> {
    		StlPayOrders stlPayOrders = new StlPayOrders();
    		// 指令编号
    		stlPayOrders.setOrderId(settleCommonService.buildBussinessIdBySeqno("9998"));
    		// 结算日期
    		stlPayOrders.setSettleDate(trdProductDealDto.getSettleDate());
    		// 本方资金账户
    		stlPayOrders.setMyAcctNo(trdProductDealDto.getAcctNo());
    		// 收付方向
    		if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), trdProductDealDto.getPs())) {
    			stlPayOrders.setPs(DictCodeList.收付方向_收入.getValue());
    		}
    		else {
    			stlPayOrders.setPs(DictCodeListExt1.收付方向_支出.getValue());
    		}
    		// 结算金额
    		stlPayOrders.setSettleAmt(trdProductDealDto.getTradeAmt());
    		// 币种
    		stlPayOrders.setCcy(trdProductDealDto.getCcy());
    		// 是否需要发送
    		stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
    		// 是否汇总交易
    		stlPayOrders.setIsTotalTrade(DictCodeList.是否_否.getValue());
    		// 交易编号
    		stlPayOrders.setTradeId(trdProductDealDto.getTradeId());
    		// 指令状态
    		stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
    		// 投资组合代码
    		stlPayOrders.setPortfolioId(trdProductDealDto.getPortfolioId());
	        // 划款类型
	        stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
            return stlPayOrders;
        }).collect(Collectors.toList());
    }
    
    protected List<EtrFinishStatus> generateEtrFinishStatusList(List<TotalTrdProductDealDto> dealTotalList, List<TrdProductDealDto> dealList){
    	List<EtrFinishStatus> etrFinishStatusList = new ArrayList<>(dealList.size());
    	if(CollectionUtils.isNotEmpty(dealList)) {
    		dealList.forEach(x ->{
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
