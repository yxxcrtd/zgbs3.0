package com.joyintech.tams.settle.batch.datagather.processor;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datagather.dto.*;
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
 * 基金处理
 * 
 * @author 王波
 * @date 2020/7/17
 * @see StlDataGatherFund1Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherFund1Processor extends StlDataGatherProcessor {
	
	@Autowired(required = false)
    private StlDataGatherService stlDataGatherService;

	@Autowired
	private SettleCommonService settleCommonService;

	@Override
	public InvestTradeWriteDto process(NeedOperateEntity operateEntity) throws Exception {
		InvestTradeReaderDto investTradeReaderDto = (InvestTradeReaderDto)operateEntity;
		// 成交明细不为空
		if (CollectionUtils.isNotEmpty(investTradeReaderDto.getSettleTransDataList())) {
			return super.process(operateEntity);
		}
		return null;
	}

	@Override
	protected String getSecType() {
		return DictCodeListExt1.证券品种_基金.getValue();
	}

	@Override
	protected List<String> getFinprodType() {return Arrays.asList(DictCodeListExt1.金融产品类型_投管用_基金.getValue());}

	/**
	 * 获取 随交易支付费用
	 * 
	 * @param settleTransDataDto
	 * @return
	 */
	private BigDecimal getFeeAmtPay(SettleTransDataDto settleTransDataDto) {
		BigDecimal feeAmtPay = BigDecimal.ZERO;
		feeAmtPay = ArithmeticUtil.doAdd(settleTransDataDto.getJsfAmt(), settleTransDataDto.getGhfAmt(),
				settleTransDataDto.getZgfAmt(), settleTransDataDto.getSxfAmt(), settleTransDataDto.getSettleFee(), settleTransDataDto.getStampFee());
		if (StringUtil.equals(DictCodeList.是否_是.getValue(), settleTransDataDto.getIsPayWithTrade())) {
			feeAmtPay = ArithmeticUtil.doAdd(feeAmtPay, settleTransDataDto.getCommissionAmt());
		}
		return feeAmtPay;
	}

	/**
	 * 获取 组合交易DTO
	 * @param dealList 交易明细
	 * @param settleTransDataDtos 成交数据明细
	 * @return 组合交易DTO
	 */
    protected List<PtlTrade> generatePtlTrade(List<TrdProductDealDto> dealList) {
		List<PtlTrade> res = new ArrayList<>();
    	dealList.forEach(deal-> {
			PtlTrade ptlTrade = new PtlTrade();
			// 组合代码
			ptlTrade.setPortfolioId(deal.getPortfolioId());
			// 业务编号
			ptlTrade.setBusiNo(deal.getTradeId());
			// 业务表
			ptlTrade.setBusiTable(DictCodeListStl.业务表_金融产品交易表.getValue());
			// 业务明细代码
			ptlTrade.setBusiId(deal.getFinprodId());
			// 投资目的
			ptlTrade.setInvAim(deal.getInvAim());
			// 证券托管户代码
			ptlTrade.setSecManageAcctId(deal.getSecManageAcctId());
			// 实际发生日
			ptlTrade.setSettleDate(deal.getSettleDate());
            // 业务类型
            ptlTrade.setBusiType(DictCodeList.共通业务类型_基金类资产交易.getValue());
			res.add(ptlTrade);
		});
    	return res;
    }

    @Override
	protected List<TrdProductDealDto> generateProductDeal(List<SettleTransDataDto> settleTransDataDtos) {
		List<TrdProductDealDto> listTrdProductDeal = new ArrayList<>();

		TrdProductDealDto trdProductDeal = new TrdProductDealDto();
		setTrdProductDeal(trdProductDeal, settleTransDataDtos);
		listTrdProductDeal.add(trdProductDeal);

		return listTrdProductDeal;
	}

	/**
	 * 金融产品交易费用明细DTO
	 * @param dealList 金融产品交易明细
	 * @param settleTransDataDtos 成交数据明细
	 * @return 金融产品交易费用明细DTO
	 */
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

	/**
	 * 生成指令
	 * @param dealList 金融产品交易明细
	 * @param settleTransDataDtos 成交数据明细
	 * @return 指令列表
	 */
	protected List<StlPayOrders> generateStlPayOrdersList(List<TotalTrdProductDealDto> dealTotalList, List<TrdProductDealDto> dealList, List<SettleTransDataDto> settleTransDataDtos) {
		List<StlPayOrders> res = new ArrayList<>();
		dealList.forEach(deal -> {
			StlPayOrders stlPayOrders = new StlPayOrders();
			// 指令编号
			stlPayOrders.setOrderId(settleCommonService.buildBussinessIdBySeqno("9998"));
			// 结算日期
			stlPayOrders.setSettleDate(deal.getSettleDate());
			//本方资金账户
			stlPayOrders.setMyAcctNo(deal.getAcctNo());
			// 收付方向
			if (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_买入.getValue())) {
				stlPayOrders.setPs(DictCodeListExt1.收付方向_支出.getValue());
			} else {
				stlPayOrders.setPs(DictCodeList.收付方向_收入.getValue());
			}
			// 结算金额
			stlPayOrders.setSettleAmt(deal.getTradeAmt());
			// 币种
			stlPayOrders.setCcy(deal.getCcy());
			// 是否需要发送
			stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
			// 是否汇总交易
			stlPayOrders.setIsTotalTrade(DictCodeList.是否_否.getValue());
			// 交易编号
			stlPayOrders.setTradeId(deal.getTradeId());
			// 指令状态
			stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
			// 投资组合代码
			stlPayOrders.setPortfolioId(deal.getPortfolioId());
	        // 划款类型
	        stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
			res.add(stlPayOrders);

		});
		return res;
	}

	/**
	 * 设置金融产品交易明细DTO
	 * @param trdProductDeal 金融产品交易明细DTO
	 * @param settleTransDataDtos 成交记录列表
	 */
	private void setTrdProductDeal(TrdProductDealDto trdProductDeal, List<SettleTransDataDto> settleTransDataDtos) {
		// 取第一条记录
		SettleTransDataDto settleTransData = settleTransDataDtos.get(0);
		// ∑交易金额_原始
		BigDecimal tradeAmtRawSum = BigDecimal.ZERO;
		// ∑交易份额
		BigDecimal shareAmtSum = BigDecimal.ZERO;
		// ∑全价总额
		BigDecimal fpriceAmtSum = BigDecimal.ZERO;
		// ∑随交易支付费用
		BigDecimal feeAmtPaySum = BigDecimal.ZERO;
		// ∑未随交易支付费用
		BigDecimal feeAmtUnPaySum = BigDecimal.ZERO;
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

		// ∑交易金额
		BigDecimal tradeAmtSum = BigDecimal.ZERO;
		for (SettleTransDataDto settleTransDataDto : settleTransDataDtos) {
			tradeAmtRawSum = ArithmeticUtil.doAdd(tradeAmtRawSum, settleTransDataDto.getTradeAmt());
			shareAmtSum = ArithmeticUtil.doAdd(shareAmtSum, settleTransDataDto.getShareAmt());
			tradeAmtSum = ArithmeticUtil.doAdd(tradeAmtSum, settleTransDataDto.getTradeAmt());
			fpriceAmtSum = ArithmeticUtil.doAdd(fpriceAmtSum, settleTransDataDto.getFpriceAmt());
			feeAmtPaySum = ArithmeticUtil.doAdd(feeAmtPaySum, getFeeAmtPay(settleTransDataDto));
			if (StringUtil.equals(DictCodeList.是否_否.getValue(), settleTransDataDto.getIsPayWithTrade())) {
				feeAmtUnPaySum = ArithmeticUtil.doAdd(feeAmtUnPaySum, settleTransDataDto.getCommissionAmt());
			}
			jsfAmt = ArithmeticUtil.doAdd(jsfAmt, settleTransDataDto.getJsfAmt());
			ghfAmt = ArithmeticUtil.doAdd(ghfAmt, settleTransDataDto.getGhfAmt());
			zgfAmt = ArithmeticUtil.doAdd(zgfAmt, settleTransDataDto.getZgfAmt());
			sxfAmt = ArithmeticUtil.doAdd(sxfAmt, settleTransDataDto.getSxfAmt());
			stampFeeAmt = ArithmeticUtil.doAdd(stampFeeAmt, settleTransDataDto.getStampFee());
			settleFee = ArithmeticUtil.doAdd(settleFee, settleTransDataDto.getSettleFee());
			commissionAmt = ArithmeticUtil.doAdd(commissionAmt, settleTransDataDto.getCommissionAmt());
		}
		// 交易编号，按现有的交易编号生成规则，自动生成
		if (StringUtil.equals(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue(), settleTransData.getBusiType())) {
			if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), settleTransData.getPs())) {
	        	trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_货币基金申购));
	        }
	        else {
	        	trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_货币基金赎回));
	        }
		}
		else {
			if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), settleTransData.getPs())) {
	        	trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_净值型资产申购));
	        }
	        else {
	        	trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_净值型资产赎回));
	        }
		}
		// 业务类型
		trdProductDeal.setBusiType(settleTransData.getBusiType());
		// 金融产品代码
		trdProductDeal.setFinprodId(settleTransData.getFinprodId());
		// 金融产品类型
		trdProductDeal.setFinprodType(settleTransData.getFinprodType());
		// 金融产品类型（投管分类）
		trdProductDeal.setFinprodType2(settleTransData.getFinprodType2());
		// 分支序号 - 0
		trdProductDeal.setBranch(0l);
		// 交易日期
		trdProductDeal.setTradeDate(settleTransData.getTradeDate());
		// 交割日期
		trdProductDeal.setSettleDate(settleTransData.getSettleDate());
		// 清算速度
		trdProductDeal.setSettleSpeed(settleTransData.getSettleSpeed());
		// 交易币种
		trdProductDeal.setCcy(settleTransData.getCcy());
		// 净值日期
		trdProductDeal.setCpriceDate(settleTransData.getTradeDate());
		// 单位净价
		BigDecimal unitCprice = ArithmeticUtil.divide(fpriceAmtSum, shareAmtSum, 14);
		trdProductDeal.setUnitCprice(unitCprice);
		// 交易本金
		trdProductDeal.setPrinAmt(shareAmtSum);
		// 随交易支付费用
		trdProductDeal.setFeeAmtPay(feeAmtPaySum);
		// ∑成交数据明细.交易份额
		trdProductDeal.setShareAmt(shareAmtSum);
		// ∑成交数据明细.交易金额
		trdProductDeal.setTradeAmt(tradeAmtSum);
		// 全价总额
		trdProductDeal.setFpriceAmt(fpriceAmtSum);
		// 未随交易支付费用
		trdProductDeal.setFeeAmtUnpay(feeAmtUnPaySum);
		// 总交易费用
		trdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(trdProductDeal.getFeeAmtPay(), trdProductDeal.getFeeAmtUnpay()));
		// 交易方向
		trdProductDeal.setPs(settleTransData.getPs());
		// 交易状态
		trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		// 设置 是否已撤销，默认“否”
		trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
		// 是否外部交易
		trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
		// 交易场所
		trdProductDeal.setTradeMarket(settleTransData.getTradeMarket());
		// 交易平台
		trdProductDeal.setTradePlat(settleTransData.getTradePlat());
		// 交易市场
		trdProductDeal.setTradeMarketMode(settleTransData.getTradeMarketMode());
		// 证券管理户代码
		trdProductDeal.setSecManageAcctId(settleTransData.getSecAcctId());

		// 登记托管机构
		trdProductDeal.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());		
		// 交收方式
		trdProductDeal.setSettlemenType(settleTransData.getSettleMode());

		// 投管业务类型
		if (StringUtil.equals(settleTransData.getBusiType(), DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue())
				&& StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_买入.getValue())) {
			trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_货币基金买入.getValue());
		} else if (StringUtil.equals(settleTransData.getBusiType(), DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue())
				&& StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_卖出.getValue())) {
			trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_货币基金卖出.getValue());
		} else if (!StringUtil.equals(settleTransData.getBusiType(), DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue())
				&& StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_卖出.getValue())) {
			trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_非货币基金卖出.getValue());
		} else if (!StringUtil.equals(settleTransData.getBusiType(), DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue())
				&& StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_买入.getValue())) {
			trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_非货币基金买入.getValue());
		}

		// 席位号
		trdProductDeal.setSeatId(settleTransData.getSeatNumber());
        // 投资组合代码
        trdProductDeal.setPortfolioId(settleTransData.getPortfolioId());
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
        // 委托代码
        trdProductDeal.setEntrustId(settleTransData.getOrderId());
    	// 资金账户
    	trdProductDeal.setAcctNo(settleTransDataDtos.get(0).getAcctNo());

        // 成交编号
        String dealId = settleTransDataDtos.stream()
                .map(SettleTransDataDto::getTradeId)
                .collect(Collectors.joining(GlobalConstant.COMMA));
        trdProductDeal.setDealId(dealId);
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
