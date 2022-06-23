package com.joyintech.tams.settle.batch.datagather.processor.bond;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductBarter;
import com.joyintech.tams.common.base.entity.TrdFinProductBarterTotal;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.settle.batch.datadeal.service.StlDataDealProcessCommonService;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;

/**
 * 债券交易处理
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherBond4Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherBond4Processor extends StlDataGatherBondBaseProcessor {

    @Autowired
    protected StlDataDealProcessCommonService stlDataDealProcessCommonService;

    @Override
    protected void setDealTotal(TotalTrdProductDealDto dealTotal, TrdProductDealDto trdProductDealDto) {
        // 业务类型
        dealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        // 交收方式
        dealTotal.setSettlemenType(trdProductDealDto.getSettlemenType());
        // 转股数
        dealTotal.setConNum(trdProductDealDto.getConNum());
        // 零星股数 
        dealTotal.setSpoShareNum(trdProductDealDto.getSpoShareNum());
        // 转股价格
        dealTotal.setZgUnitCprice(trdProductDealDto.getZgUnitCprice());
        // 股票代码
        dealTotal.setFinprodId2(trdProductDealDto.getFinprodId2());
        // 金融产品类型2
        dealTotal.setFinprodTypeExt(trdProductDealDto.getFinprodTypeExt());
    }

    @Override
    protected List<TrdTradeFeeDetail> generateTradeFeeDetail(List<TrdProductDealDto> dealList) {
        return createTradeFeeDetail(dealList,true,null);
    }

    @Override
    protected List<StlPayOrders> generateStlPayOrdersList(List<TotalTrdProductDealDto> dealTotalList, List<TrdProductDealDto> dealList, List<SettleTransDataDto> settleTransDataDtos) {
        List<StlPayOrders> payOrdersList = new ArrayList<>(dealTotalList.size());
        if(CollectionUtils.isNotEmpty(dealTotalList)) {
            for (int i = 0; i < dealTotalList.size(); i++) {
                TrdFinProductDealTotal total = dealTotalList.get(i);
                TrdProductDealDto productDealDto = dealList.get(i);
                StlPayOrders stlPayOrders = createStlPayOrders(total, productDealDto, settleTransDataDtos);
                // 收付方向
                stlPayOrders.setPs(DictCodeList.收付方向_收入.getValue());
                // 理财产品代码
                stlPayOrders.setProdId(total.getProdId());
                payOrdersList.add(stlPayOrders);
            }
        }
        return payOrdersList;
    }

    /**
     * 债转股
     * @param settleTransDataDtoList
     * @return
     */
    @Override
    protected List<TrdProductDealDto> createDeal(List<SettleTransDataDto> settleTransDataDtoList, boolean buildTradeId) {
        // 从交易信息中获取债券交易
        SettleTransDataDto settleTransDataDto = getBondDto(settleTransDataDtoList);
        // 从交易信息中获取股票交易
        SettleTransDataDto sharesData = getSharesDto(settleTransDataDtoList);
        // 随交易支付费用
        BigDecimal totalFeeAmtPay = BigDecimal.ZERO;
        // 交易金额
        BigDecimal tradeAmt = BigDecimal.ZERO;
        // 未随交易支付费用
        BigDecimal totalFeeAmtUnpay = BigDecimal.ZERO;
        List<String> dealIdList = new ArrayList<>();
        for (SettleTransDataDto item : settleTransDataDtoList) {
            totalFeeAmtPay = ArithmeticUtil.doAdd(totalFeeAmtPay, item.getJsfAmt(), item.getGhfAmt(), item.getZgfAmt(), item.getSxfAmt(), item.getJsfAmt());
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), item.getIsPayWithTrade())) {
                totalFeeAmtPay = ArithmeticUtil.doAdd(totalFeeAmtPay, item.getCommissionAmt());
            }
            else {
                totalFeeAmtUnpay = ArithmeticUtil.doAdd(totalFeeAmtUnpay, item.getCommissionAmt());
            }
            if(StringUtil.isEmpty(item.getFinprodType2())) {
                tradeAmt = item.getTradeAmt();
            }
            dealIdList.add(item.getTradeId());
        }
        // 上中登的需要去金融产品类型_投管分类是空的那条数据的金额，上中登没有那条数据，需要取债券的那条数据
        if (tradeAmt == BigDecimal.ZERO) {
        	for (SettleTransDataDto item : settleTransDataDtoList) {
        		if(StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    tradeAmt = item.getTradeAmt();
                }
        	}
        }
        
        // 交易字段设置共通
        TrdProductDealDto trdProductDeal = createProductDeal(settleTransDataDto, buildTradeId);
        // 交易编号（共通方法已设置）
        // 业务类型（共通方法已设置）
        // 金融产品代码（共通方法已设置）
        // 金融产品类型（投管分类）（共通方法已设置）
        // 分支序号（共通方法已设置）
        // 交易日（共通方法已设置）
        // 起息日（共通方法已设置）
        // 到期日（共通方法已设置）
        // 交付日（共通方法已设置）
        // 交割日（共通方法已设置）
        // 清算速度
        trdProductDeal.setSettleSpeed(settleTransDataDto.getSettleSpeed());
        // 交易币种（共通方法已设置）
        // 净值日期（共通方法已设置）
        // 净值日期
//        trdProductDeal.setCpriceDate(null);
//        // 单位净价
//        trdProductDeal.setUnitCprice(null);
//        // 单位利息
//        trdProductDeal.setUnitInt(null);
//        // 单位全价
//        trdProductDeal.setUnitFprice(null);
//        // 单位面值
//        trdProductDeal.setParValue(null);
//        // 净价总额
//        trdProductDeal.setCpriceAmt(null);
        // 交易份额
        trdProductDeal.setShareAmt(settleTransDataDto.getShareAmt());
//        // 交易本金
//        trdProductDeal.setPrinAmt(null);
//        // 利息总额
//        trdProductDeal.setIntAmt(null);
//        // 全价总额
//        trdProductDeal.setFpriceAmt(null);
        // 随交易支付费用
        trdProductDeal.setFeeAmtPay(totalFeeAmtPay);
        // 交易金额
        trdProductDeal.setTradeAmt(tradeAmt);
        // 未随交易支付费用
        trdProductDeal.setFeeAmtUnpay(totalFeeAmtUnpay);
        // 总交易费用
        trdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(totalFeeAmtPay, totalFeeAmtUnpay));
        // 交易方向
        trdProductDeal.setPs(DictCodeListExt1.交易方向_债转股.getValue());
        // 到期收益率
//        trdProductDeal.setYield(null);
//        // 行权收益率
//        trdProductDeal.setExerYield(null);
//        // 投资目的（共通方法已设置）
//        // 结算方式
        trdProductDeal.setSettMode(null);
        // 交易状态（共通方法已设置）
        // 是否已撤销（共通方法已设置）
        // 原交易编号（共通方法已设置）
        // 是否结清（共通方法已设置）
        // 汇总交易编号（共通方法已设置）
        // 反向交易编号（共通方法已设置）
        // 反向交易投资目的（共通方法已设置）
        // 前置交易编号
//        trdProductDeal.setbTradeId(null);
        // 通道代码（共通方法已设置）
        // 交易对手（共通方法已设置）
        // 是否外部交易（共通方法已设置）
        // 交易场所（共通方法已设置）
        // 交易平台（共通方法已设置）
        // 交易市场（共通方法已设置）
        // 本方交易员（共通方法已设置）
        // 对手方交易员（共通方法已设置）
        // 对手方联系方式（共通方法已设置）
        // 证券管理户代码（共通方法已设置）
        // 关联系统交易编号（共通方法已设置）
        // 现金流代码（共通方法已设置）
        // 登记托管机构（共通方法已设置）
        // 备注（共通方法已设置）
        // 撤销说明（共通方法已设置）
        // 投管业务类型
        trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_债转股.getValue());
        // 销售代码（共通方法已设置）
        // 计提份额（共通方法已设置）
        // 交收方式（共通方法已设置）
        // 席位号（共通方法已设置）
        // 投资组合代码（共通方法已设置）
        // 委托代码（共通方法已设置）
        // 成交编号
        trdProductDeal.setDealId(StringUtil.joinString(dealIdList, GlobalConstant.COMMA));
        // 权益登记日（共通方法已设置）
        // 权益登记日份额（共通方法已设置）
        // 金融产品代码2
        trdProductDeal.setFinprodId2(sharesData.getFinprodId());
        // 金融产品类型2
        trdProductDeal.setFinprodTypeExt(sharesData.getFinprodType2());
        // 分支序号2
        trdProductDeal.setBranch2(0L);
        // 单位净价2
        trdProductDeal.setUnitCprice2(sharesData.getUnitCprice());
        // 交易本金2
        trdProductDeal.setPrinAmt2(sharesData.getPrinAmt());
        // 交易份额2
        trdProductDeal.setShareAmt2(sharesData.getShareAmt());
        // 利息总额2
        trdProductDeal.setIntAmt2(sharesData.getIntAmt());
        // 投资目的2
        trdProductDeal.setInvAim2(sharesData.getInvAim());
        
        // 从交易信息中获取债转股价格那笔明细
        List<SettleTransDataDto> convertDtoList = settleTransDataDtoList.stream().filter(item -> StringUtil.isEmpty(item.getFinprodType2())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(convertDtoList)){
        	// SettleTransDataDto convertDto = getConvertDto(settleTransDataDtoList);
        	// 转股价格
            trdProductDeal.setZgUnitCprice(convertDtoList.get(0).getUnitCprice());
        }
        else{
        	// 深中登没有单独的 那笔现金交易
        	trdProductDeal.setZgUnitCprice(settleTransDataDto.getUnitCprice());
        }
        // 转股数
        trdProductDeal.setConNum(ArithmeticUtil.multiply(sharesData.getShareAmt(), BigDecimal.valueOf(100)));
        // 零股数量  = 转股券面总额/转股价格 - 转股数
        trdProductDeal.setSpoShareNum(ArithmeticUtil.subtract(
        		BigDecimal.valueOf(ArithmeticUtil.divide(ArithmeticUtil.multiply(trdProductDeal.getShareAmt(), BigDecimal.valueOf(100))
				, trdProductDeal.getZgUnitCprice(), 14).intValue())
        		, ArithmeticUtil.multiply(sharesData.getShareAmt(), BigDecimal.valueOf(100))));
        
        return Arrays.asList(trdProductDeal);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<TrdDealTotalAdd> generateDealTotalAdd(List<TotalTrdProductDealDto> totleList) {
    	List<TrdDealTotalAdd> trdTotalAddList = new ArrayList<TrdDealTotalAdd>();
    	totleList.forEach(x ->{
    		TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
    		trdDealTotalAdd.setTotleTradeId(x.getTotleTradeId());
    		// 零星股数
    		trdDealTotalAdd.setSpoShareNum(x.getSpoShareNum());
    		trdTotalAddList.add(trdDealTotalAdd);
    	});
    	
    	return trdTotalAddList;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<TrdFinProductBarterTotal> generateDealBarterTotal(List<TotalTrdProductDealDto> totleList) {
    	List<TrdFinProductBarterTotal> trdBarterTotalList = new ArrayList<TrdFinProductBarterTotal>();
    	totleList.forEach(x ->{
    		TrdFinProductBarterTotal trdFinProductBarterTotal = new TrdFinProductBarterTotal();
    		trdFinProductBarterTotal.setTradeId(x.getTotleTradeId());
    		// 金融产品代码
    		trdFinProductBarterTotal.setFinprodId(x.getFinprodId2());
    		// 金融产品类型
    		trdFinProductBarterTotal.setFinprodType(x.getFinprodTypeExt());
    		// 转股价格
    		trdFinProductBarterTotal.setUnitCprice(x.getZgUnitCprice());
    		// 转股数
    		trdFinProductBarterTotal.setShareAmt(x.getConNum());
    		trdBarterTotalList.add(trdFinProductBarterTotal);
    	});
    	
    	return trdBarterTotalList;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<TrdFinProductBarter> generateDealBarterList(List<TrdProductDealDto> dealList) {
    	List<TrdFinProductBarter> trdBarterList = new ArrayList<TrdFinProductBarter>();
    	dealList.forEach(x ->{
    		TrdFinProductBarter trdFinProductBarter = new TrdFinProductBarter();
    		trdFinProductBarter.setTradeId(x.getTradeId());
    		// 金融产品代码
    		trdFinProductBarter.setFinprodId(x.getFinprodId2());
    		// 金融产品类型
    		trdFinProductBarter.setFinprodType(x.getFinprodTypeExt());
    		// 转股价格
    		trdFinProductBarter.setUnitCprice(x.getZgUnitCprice());
    		// 转股数
    		trdFinProductBarter.setShareAmt(x.getConNum());
    		trdBarterList.add(trdFinProductBarter);
    	});
    	
    	return trdBarterList;
    }

    private SettleTransDataDto getBondDto(List<SettleTransDataDto> settleTransDataDtos) {
        return settleTransDataDtos.stream()
                .filter(item -> StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue()))
                .findFirst().get();
    }
    private SettleTransDataDto getSharesDto(List<SettleTransDataDto> settleTransDataDtos) {
        return settleTransDataDtos.stream()
                .filter(item -> StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue()))
                .findFirst().get();
    }
    /**
     * 取债转股价格那笔成交明细
     * 
     * @param settleTransDataDtos  成交明细
     * @return 债转股价格那笔成交明细
     * @see
     * @since 1.0
     */
    private SettleTransDataDto getConvertDto(List<SettleTransDataDto> settleTransDataDtos) {
        return settleTransDataDtos.stream()
                .filter(item -> StringUtil.isEmpty(item.getFinprodType2()))
                .findFirst().get();
    }
}
