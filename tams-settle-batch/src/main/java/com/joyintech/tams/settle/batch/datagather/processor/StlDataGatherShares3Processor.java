package com.joyintech.tams.settle.batch.datagather.processor;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.SettleTransTgMapping;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.constant.StlDataDealProcessConstant;
import com.joyintech.tams.settle.batch.datadeal.service.StlDataDealProcessCommonService;
import com.joyintech.tams.settle.batch.datagather.dto.*;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 股票申购确认交易处理
 * @author fuyongsheng
 * @date 2022/4/8
 * @see StlDataGatherShares3Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherShares3Processor extends StlDataGatherProcessor {

	private static final String DEAL_REMARK = "数据清算自动处理";

	@Autowired
	private SettleCommonService settleCommonService;

	@Autowired
	private StlDataDealProcessCommonService stlDataDealProcessCommonService;

	@Override
	public InvestTradeWriteDto process(NeedOperateEntity operateEntity) throws Exception {
		//接收参数
		InvestTradeReaderDto investTradeReaderDto = (InvestTradeReaderDto)operateEntity;
		//返回值
		InvestTradeWriteDto investTradeWriteDto = new InvestTradeWriteDto();

		// 生成申购确认的交易
		List<SettleTransDataDto> dataSqqrList = investTradeReaderDto.getSettleTransDataSgqrList();
		if(CollectionUtils.isNotEmpty(dataSqqrList)){
			Map<String, List<SettleTransDataDto>> collect = dataSqqrList.stream().collect(Collectors.groupingBy(x -> {
				return x.getSecAcctId() + x.getFinprodId() + x.getSeatNumber() + x.getPs();
			}));
			Iterator<String> iterator = collect.keySet().iterator();
			while (iterator.hasNext()){
				String next = iterator.next();
				List<SettleTransDataDto> settleTransDataDtos = collect.get(next);
				settleTransDataDtos.sort((a,b)->{
					return a.getShareAmtSg().compareTo(b.getShareAmtSg());
				});
				BigDecimal shareAmtSum = settleTransDataDtos.stream().map(SettleTransDataDto::getShareAmtSg).reduce(BigDecimal.ZERO,BigDecimal::add);
				List<BigDecimal> l = new ArrayList<>();
				for (int i = 0; i < 5; i++) {
					l.add(new BigDecimal(0));
				}
				for (int i = 0; i < settleTransDataDtos.size(); i++) {
					SettleTransDataDto settleTransDataDto = settleTransDataDtos.get(i);
					TrdProductDealDto dealDto;
					if(i==settleTransDataDtos.size()-1){
						dealDto = setTrdProductDealSgDto(settleTransDataDto,l,shareAmtSum,true);
					}else {
						dealDto = setTrdProductDealSgDto(settleTransDataDto,l,shareAmtSum,false);
					}

					// GZYHTZJY-1087:股票申购确认需登录SettleTransTgMapping modify by fuyongsheng 20220117 start
					SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
					settleTransTgMapping.setTradeId(IDGenerator.generate(20));
					// GNS-950:修改TradeIdTg赋值 modify by fuyongsheng 20220223 start
					// settleTransTgMapping.setTradeIdTg(dealDto.getTotleTradeId());
					settleTransTgMapping.setTradeIdTg(dealDto.getTradeId());
					// GNS-950:修改TradeIdTg赋值 modify by fuyongsheng 20220223 end
					// modify by etliu 20220222 start
					// 广农商二阶段 GNS-931:【广农商场内清算】上/深交所 基金存续期重复跑批，生成数据有误
//					settleTransTgMapping.setDealDate(dealDto.getTradeDate());
					settleTransTgMapping.setDealDate(getBatchExecuteInfo().getExecuteDate());
					// modify by etliu 20220222 end
					settleTransTgMapping.setTgDataStatus(DictCodeListStl.投管交易数据状态_处理结束.getValue());
					investTradeWriteDto.getSettleTransTgMappingList().add(settleTransTgMapping);
					// GZYHTZJY-1087:股票申购确认需登录SettleTransTgMapping modify by fuyongsheng 20220117 end

					PtlTrade ptlTrade = setPtlTrade(dealDto);
					ptlTrade.setSettleDate(dealDto.getSettleDate());
					investTradeWriteDto.getPtlTradeList().add(ptlTrade);
					investTradeWriteDto.getProductDealList().add(dealDto);
				}
			}
		}
		// GZYHTZJY-1050:赋值sectype modify fuyongsheng 20220106 start
		investTradeWriteDto.setSecType(getSecType());
		// GZYHTZJY-1050:赋值sectype modify fuyongsheng 20220106 end
		return investTradeWriteDto;
	}

	@Override
	protected String getSecType() {
		return DictCodeListExt1.证券品种_股票.getValue();
	}

	@Override
	protected List<String> getFinprodType() {return Arrays.asList(DictCodeListExt1.金融产品类型_投管用_股票.getValue());}

	/**
	 * 生成投资组合交易
	 *
	 * @param deal            交易明细
	 * @return 投资组合交易
	 */
	private PtlTrade setPtlTrade(TrdProductDealDto deal) {
		PtlTrade ptlTrade = new PtlTrade();
		// 组合代码
		ptlTrade.setPortfolioId(deal.getPortfolioId());
		// 业务编号
		ptlTrade.setBusiNo(deal.getTradeId());
		// 业务表
		ptlTrade.setBusiTable(DictCodeListStl.业务表_金融产品交易表.getValue());
		// 业务明细代码
		ptlTrade.setBusiId(deal.getFinprodId());
		//业务类型
		ptlTrade.setBusiType("TRD59");
		// 证券托管户代码
		ptlTrade.setSecManageAcctId(deal.getSecManageAcctId());
		// 实际发生日
		ptlTrade.setSettleDate(deal.getTradeDate());
		return ptlTrade;
	}

	/**
	 * 申购确认交易 金融产品交易明细设置
	 * @param settleTransDataDto 金融产品交易明细
	 * @param list 除权交易明细列表
	 */
	private TrdProductDealDto setTrdProductDealSgDto(SettleTransDataDto settleTransDataDto,List<BigDecimal> list,BigDecimal shareAmtSum,boolean last) {
		//返回值
		TrdProductDealDto trdProductDealDto = new TrdProductDealDto();
		// 交易编号，按现有的交易编号生成规则，自动生成
		trdProductDealDto.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易));
		// 金融产品代码
		trdProductDealDto.setFinprodId(settleTransDataDto.getFinprodId());
		// 金融产品类型（估值核算）
		trdProductDealDto.setFinprodType(settleTransDataDto.getFinprodType2());
		// 金融产品类型（投管分类）
		trdProductDealDto.setFinprodType2(settleTransDataDto.getFinprodType2());
		// 分支序号
		trdProductDealDto.setBranch(0L);
		// 交易日
		trdProductDealDto.setTradeDate(settleTransDataDto.getTradeDate());
		// 交割日
		trdProductDealDto.setSettleDate(settleTransDataDto.getSettleDate());
		//交易币种
		trdProductDealDto.setCcy(settleTransDataDto.getCcy());
		//单位净价
		trdProductDealDto.setUnitCprice(settleTransDataDto.getUnitCprice());
		// 单位利息
		trdProductDealDto.setUnitInt(settleTransDataDto.getUnitInt());
		//单位全价
		trdProductDealDto.setUnitFprice(settleTransDataDto.getUnitFprice());
		//单位面值
		trdProductDealDto.setParValue(settleTransDataDto.getParValue());
		if(last){
			//交易份额
			trdProductDealDto.setShareAmt(null == settleTransDataDto.getShareAmt() ? BigDecimal.ZERO : settleTransDataDto.getShareAmt().subtract(list.get(0)));
			//净价总额
			trdProductDealDto.setCpriceAmt(null == settleTransDataDto.getCpriceAmt() ? BigDecimal.ZERO : settleTransDataDto.getCpriceAmt().subtract(list.get(1)));
			//交易本金
			trdProductDealDto.setPrinAmt(null == settleTransDataDto.getPrinAmt() ? BigDecimal.ZERO : settleTransDataDto.getPrinAmt().subtract(list.get(2)));
			// 全价总额
			trdProductDealDto.setFpriceAmt(null == settleTransDataDto.getFpriceAmt() ? BigDecimal.ZERO : settleTransDataDto.getFpriceAmt().subtract(list.get(3)));
			//交易金额
			trdProductDealDto.setTradeAmt(null == settleTransDataDto.getTradeAmt() ? BigDecimal.ZERO : settleTransDataDto.getTradeAmt().subtract(list.get(4)));
		}else {
			//交易份额
			trdProductDealDto.setShareAmt(null == settleTransDataDto.getShareAmt() ? BigDecimal.ZERO : settleTransDataDto.getShareAmt().multiply(settleTransDataDto.getShareAmtSg()).divide(shareAmtSum, 4, RoundingMode.HALF_UP));
			list.set(0,list.get(0).add(trdProductDealDto.getShareAmt()));
			//净价总额
			trdProductDealDto.setCpriceAmt(null == settleTransDataDto.getCpriceAmt() ? BigDecimal.ZERO : settleTransDataDto.getCpriceAmt().multiply(settleTransDataDto.getShareAmtSg()).divide(shareAmtSum, 4, RoundingMode.HALF_UP));
			list.set(1, list.get(1).add(trdProductDealDto.getCpriceAmt()));
			//交易本金
			trdProductDealDto.setPrinAmt(null == settleTransDataDto.getPrinAmt() ? BigDecimal.ZERO : settleTransDataDto.getPrinAmt().multiply(settleTransDataDto.getShareAmtSg()).divide(shareAmtSum, 4, RoundingMode.HALF_UP));
			list.set(2, list.get(2).add(trdProductDealDto.getPrinAmt()));
			// 全价总额
			trdProductDealDto.setFpriceAmt(null == settleTransDataDto.getFpriceAmt() ? BigDecimal.ZERO : settleTransDataDto.getFpriceAmt().multiply(settleTransDataDto.getShareAmtSg()).divide(shareAmtSum, 4, RoundingMode.HALF_UP));
			list.set(3, list.get(3).add(trdProductDealDto.getFpriceAmt()));
			//交易金额
			trdProductDealDto.setTradeAmt(null == settleTransDataDto.getTradeAmt() ? BigDecimal.ZERO : settleTransDataDto.getTradeAmt().multiply(settleTransDataDto.getShareAmtSg()).divide(shareAmtSum, 4, RoundingMode.HALF_UP));
			list.set(4, list.get(4).add(trdProductDealDto.getTradeAmt()));
		}
		// 交易方向
		trdProductDealDto.setPs(settleTransDataDto.getPs());
		//投资目的
		trdProductDealDto.setInvAim(settleTransDataDto.getInvAim());
		//结算方式
		trdProductDealDto.setSettMode("03");
		// 交易状态
		trdProductDealDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		// 是否已撤销
		trdProductDealDto.setIsCancel(DictCodeList.是否_否.getValue());
		//是否结清
		trdProductDealDto.setIsClean("Y");
		//前置交易编号
		trdProductDealDto.setbTradeId(settleTransDataDto.getbTradeId());
		// 是否外部交易
		trdProductDealDto.setIsOutTrade(DictCodeList.是否_是.getValue());
		// 交易场所
		trdProductDealDto.setTradeMarket(settleTransDataDto.getTradeMarket());
		//交易平台
		trdProductDealDto.setTradePlat(settleTransDataDto.getTradePlat());
		//交易市场
		trdProductDealDto.setTradeMarketMode(settleTransDataDto.getTradeMarketMode());
		//关联系统交易编号
		trdProductDealDto.setfTradeId(settleTransDataDto.getSeqNo());
		//登记托管机构
		trdProductDealDto.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
		// 证券管理户代码
		trdProductDealDto.setSecManageAcctId(settleTransDataDto.getSecAcctId());
		//备注
		trdProductDealDto.setRemark(DEAL_REMARK);
		//交收方式
		trdProductDealDto.setSettlemenType(settleTransDataDto.getSettleMode());
		//席位号
		trdProductDealDto.setSeatId(settleTransDataDto.getSeatNumber());
		//委托代码
		trdProductDealDto.setEntrustId(settleTransDataDto.getEntrustId());
		// 投资组合代码
		trdProductDealDto.setPortfolioId(settleTransDataDto.getPortfolioId());

		return trdProductDealDto;
	}

}
