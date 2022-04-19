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
 * 股票除权、现金分红、送转交易处理
 * @author chenzhuo
 * @date 2020/7/17
 * @see StlDataGatherShares2Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherShares2Processor extends StlDataGatherProcessor {

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
		// GNS-932:不从SettleTransDataDelDtoList读取除权数据 modify by fuyongsheng 20220224 start
//		List<SettleDelDealDetailDto> list = investTradeReaderDto.getSettleTransDataDelDtoList();
//		if (CollectionUtils.isNotEmpty(list)){
//			Map<String, List<SettleDelDealDetailDto>> collects = list.stream().collect(Collectors.groupingBy(x -> {
//				return x.getFinprodId() +","+ x.getSecAcctId();
//			}));
//			Iterator<String> iterator = collects.keySet().iterator();
//			while (iterator.hasNext()) {
//				String tempStr = iterator.next();
//
//				//保证批可重复执行 （先删除）
//				String[] para = tempStr.split(",");
//				Map<String ,Object> map = new HashMap<>();
//				map.put("finprodId",para[0]);
//				map.put("secAcctId",para[1]);
//				map.put("dealDate", getBatchExecuteInfo().getExecuteDate());
//				map.put("busiType",DictCodeListExt2.共通业务类型_股票分红交易.getValue());
//				List<String> totleTradeIdList = stlDataGatherMapper.selectCqTotleTradeId(map);
//				if(CollectionUtils.isNotEmpty(totleTradeIdList)){
//					for (String totleTradeId:totleTradeIdList){
//						List<String> tradeIdList = stlDataGatherMapper.selectCqtradeId(totleTradeId);
//						if(CollectionUtils.isNotEmpty(tradeIdList)){
//							for (String tradeId : tradeIdList){
//								stlDataGatherMapper.deleteCqTrdProductDealAdd(tradeId);
//								stlDataGatherMapper.deleteCqPaTrdProductDeal(tradeId);
//								stlDataGatherMapper.deleteCqPtlTrade(tradeId);
//							}
//						}
//						stlDataGatherMapper.deleteCqTrdFinProductDealTotal(totleTradeId);
//						stlDataGatherMapper.deleteCqTrdProductDeal(totleTradeId);
//						stlDataGatherMapper.deleteCqPaTrdFinProductDealTotal(totleTradeId);
//						stlDataGatherMapper.deleteCqTrdDealTotalAdd(totleTradeId);
//					}
//				}
//
//				List<SettleDelDealDetailDto> settleTransDataDelDtoList = collects.get(tempStr);
//				if (settleTransDataDelDtoList != null && settleTransDataDelDtoList.size() > 0) {
//					// ∑除权交易明细.权益登记日份额
//					BigDecimal registShareAll = BigDecimal.ZERO;
//					// ∑除权交易明细.分红金额
//					BigDecimal tradeAmtAll = BigDecimal.ZERO;
//					for (SettleDelDealDetailDto transDataDelDto : settleTransDataDelDtoList) {
//						registShareAll = ArithmeticUtil.doAdd(registShareAll, transDataDelDto.getRedDateAmt());
//						tradeAmtAll = ArithmeticUtil.doAdd(tradeAmtAll, transDataDelDto.getBonusAmt());
//					}
//					// 金融产品交易汇总信息
//					TotalTrdProductDealDto totalTrdProductDealDto = setCqTotalTrdProductDealDto(settleTransDataDelDtoList, registShareAll);
//					investTradeWriteDto.getFinProductDealTotalList().add(totalTrdProductDealDto);
//
//					for (SettleDelDealDetailDto settleDelDealDetailDto : settleTransDataDelDtoList) {
//						// 金融产品交易明细
//						TrdProductDealDto dealDto = setCqTrdProductDealDto(totalTrdProductDealDto, settleDelDealDetailDto, registShareAll);
//						investTradeWriteDto.getProductDealList().add(dealDto);
//
//						// 成交编号业务编号关联表（只针对除权登记数据）
//						SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
//						settleTransTgMapping.setTradeId(IDGenerator.generate(20));
//						settleTransTgMapping.setTradeIdTg(dealDto.getTotleTradeId());
//						settleTransTgMapping.setTgDataStatus(DictCodeListStl.投管交易数据状态_处理结束.getValue());
//						settleTransTgMapping.setDealDate(getBatchExecuteInfo().getExecuteDate());
//						investTradeWriteDto.getSettleTransTgMappingList().add(settleTransTgMapping);
//
//						// 组合交易
//						investTradeWriteDto.getPtlTradeList().add(setPtlTrade(dealDto));
//					}
//				}
//			}
//		}
		// GNS-932:不从SettleTransDataDelDtoList读取除权数据 modify by fuyongsheng 20220224 end

		//第二步 ： 生成现金分红、送转、除权的交易
		List<SettleTransDataDto> dataList = investTradeReaderDto.getSettleTransDataList();
		if(CollectionUtils.isNotEmpty(dataList)){
			Map<String, List<SettleTransDataDto>> collect = dataList.stream().collect(Collectors.groupingBy(x -> {
				return x.getFinprodId() + "," + x.getSecAcctId();
			}));
			Iterator<String> iterator = collect.keySet().iterator();
			while (iterator.hasNext()){
				String next = iterator.next();
				List<SettleTransDataDto> settleTransDataDtos = collect.get(next);

				// 总持仓数量
				BigDecimal amountAll = BigDecimal.ZERO;
				for (SettleTransDataDto dto : settleTransDataDtos) {
					amountAll = ArithmeticUtil.doAdd(amountAll, dto.getAmount());
				}

				//现金分红 和 送转的交易（要先登一笔除权登记，再登交易）
				if(StringUtil.equals(settleTransDataDtos.get(0).getPs(),DictCodeList.交易方向_现金分红.getValue())||
						StringUtil.equals(settleTransDataDtos.get(0).getPs(),DictCodeList.交易方向_红利再投.getValue())
					// GNS-932:上交所也在这里生成除权交易 mdify by fuyongsheng 20220224 start
					// &&StringUtil.equals(settleTransDataDtos.get(0).getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())
					// GNS-932:上交所也在这里生成除权交易 mdify by fuyongsheng 20220224 end
				){
					// GNS-877:股票存续期不生成除权交易的交易汇总表 modify by fuyongsheng 20220227 start
					TotalTrdProductDealDto totalCqTrdProductDealDto = setTotalTrdProductDealDto(settleTransDataDtos,true);
//					investTradeWriteDto.getFinProductDealTotalList().add(totalCqTrdProductDealDto);
//
//					SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
//					settleTransTgMapping.setTradeId(IDGenerator.generate(20));
//					settleTransTgMapping.setTradeIdTg(totalCqTrdProductDealDto.getTotleTradeId());
//					settleTransTgMapping.setTgDataStatus(DictCodeListStl.投管交易数据状态_处理结束.getValue());
					SysBatchExecInfo execInfo = getBatchExecuteInfo();
//					settleTransTgMapping.setDealDate(execInfo.getExecuteDate());
//					investTradeWriteDto.getSettleTransTgMappingList().add(settleTransTgMapping);
					// GNS-877:股票存续期不生成除权交易的交易汇总表 modify by fuyongsheng 20220227 end

					TotalTrdProductDealDto totalTrdProductDealDto = setTotalTrdProductDealDto(settleTransDataDtos,false);
					//前置交易编号
					totalTrdProductDealDto.setbTradeId(null);
					investTradeWriteDto.getFinProductDealTotalList().add(totalTrdProductDealDto);

					SettleTransTgMapping settleTransTgMapping1 = new SettleTransTgMapping();
					settleTransTgMapping1.setTradeId(settleTransDataDtos.get(0).getTradeId());
					settleTransTgMapping1.setTradeIdTg(totalTrdProductDealDto.getTotleTradeId());
					settleTransTgMapping1.setTgDataStatus(DictCodeListStl.投管交易数据状态_处理结束.getValue());
					settleTransTgMapping1.setDealDate(execInfo.getExecuteDate());
					investTradeWriteDto.getSettleTransTgMappingList().add(settleTransTgMapping1);

					for (SettleTransDataDto settleTransDataDto : settleTransDataDtos) {
						TrdProductDealDto dealCqDto = setTrdProductDealDto(totalCqTrdProductDealDto, settleTransDataDto,true, amountAll);
						investTradeWriteDto.getProductDealList().add(dealCqDto);
						TrdProductDealDto dealDto = setTrdProductDealDto(totalTrdProductDealDto, settleTransDataDto,false, amountAll);
						// GNS-877:除权登记的交易的totleTradeId应与现金分红或者送转的交易汇总表一致 modify by fuyongsheng 20220227 start
						dealCqDto.setTotleTradeId(dealDto.getTotleTradeId());
						// GNS-877:除权登记的交易的totleTradeId应与现金分红或者送转的交易汇总表一致 modify by fuyongsheng 20220227 end
						dealDto.setbTradeId(dealCqDto.getTradeId());
						investTradeWriteDto.getProductDealList().add(dealDto);
						PtlTrade ptlCqTrade = setPtlTrade(dealCqDto);
						ptlCqTrade.setSettleDate(dealCqDto.getSettleDate());
						investTradeWriteDto.getPtlTradeList().add(ptlCqTrade);
						PtlTrade ptlTrade = setPtlTrade(dealDto);
						ptlTrade.setSettleDate(dealDto.getSettleDate());
						investTradeWriteDto.getPtlTradeList().add(ptlTrade);

						if(StringUtil.equals(settleTransDataDto.getPs(), DictCodeList.交易方向_现金分红.getValue())) {
							// 划款指令明细
							StlPayOrders stlPayOrders = generateStlPayOrders(dealDto);
							investTradeWriteDto.getStlPayOrdersList().add(stlPayOrders);
						}
					}
				}
				// GNS-932:上交所不生成送转交易 modify by fuyongsheng 20220224 start
//				else if(StringUtil.equals(settleTransDataDtos.get(0).getPs(),DictCodeList.交易方向_红利再投.getValue())
//						&&StringUtil.equals(settleTransDataDtos.get(0).getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())){
//					String bTradeId = stlDataGatherMapper.selectCqBtradeId(settleTransDataDtos.get(0).getSecAcctId(),settleTransDataDtos.get(0).getFinprodId(),getBatchExecuteInfo().getExecuteDate());
//					TotalTrdProductDealDto totalTrdProductDealDto = setTotalTrdProductDealDto(settleTransDataDtos,false);
//					//前置交易编号
//					totalTrdProductDealDto.setbTradeId(bTradeId);
//					investTradeWriteDto.getFinProductDealTotalList().add(totalTrdProductDealDto);
//					for (SettleTransDataDto settleTransDataDto : settleTransDataDtos) {
//						TrdProductDealDto dealDto = setTrdProductDealDto(totalTrdProductDealDto, settleTransDataDto,false, amountAll);
//						investTradeWriteDto.getProductDealList().add(dealDto);
//						PtlTrade ptlTrade = setPtlTrade(dealDto);
//						ptlTrade.setSettleDate(dealDto.getSettleDate());
//						investTradeWriteDto.getPtlTradeList().add(ptlTrade);
//					}
//				}
				// GNS-932:上交所不生成送转交易 modify by fuyongsheng 20220224 end
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
	 * 除权交易（送转上交所） 金融产品汇总交易DTO
	 *
	 * @param settleTransDataDelDtos 除权交易
	 * @return 金融产品汇总交易DTO
	 */
	private TotalTrdProductDealDto setCqTotalTrdProductDealDto(List<SettleDelDealDetailDto> settleTransDataDelDtos,
															   BigDecimal registShareAll) {
		//返回值
		TotalTrdProductDealDto dealTotal = new TotalTrdProductDealDto();

		SettleDelDealDetailDto settleTransDataDelDto = settleTransDataDelDtos.get(0);

		//  汇总交易编号
		dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易));
		// 业务类型
		dealTotal.setBusiType(DictCodeListExt2.共通业务类型_股票分红交易.getValue());
		// 金融产品代码
		dealTotal.setFinprodId(settleTransDataDelDto.getFinprodId());
		// 金融产品类型（估值核算）
		dealTotal.setFinprodType(settleTransDataDelDto.getFinprodType2());
		// 金融产品类型（投管分类）
		dealTotal.setFinprodType2(settleTransDataDelDto.getFinprodType2());
		// 分支序号
		dealTotal.setBranch(0L);
		// 交易日
		dealTotal.setTradeDate(settleTransDataDelDto.getDelDate());
		// 交割日
		dealTotal.setSettleDate(settleTransDataDelDto.getDelDate());
		// 交易币种
		dealTotal.setCcy(settleTransDataDelDto.getCcy());
		// GNS-909:修改除权登记交易赋值 modify by fuyongsheng 20220218 start
		// 单位净价
		dealTotal.setUnitCprice(ArithmeticUtil.divide(settleTransDataDelDto.getPreDevidengAmt(),
				registShareAll, 8 ,RoundingMode.HALF_UP));
		// 交易份额
		// dealTotal.setShareAmt(registShareAll);
		dealTotal.setShareAmt(settleTransDataDelDto.getPreDevidengAmt());
		// 单位利息
		// dealTotal.setUnitInt(settleTransDataDelDto.getPreDevidengAmt());
		dealTotal.setUnitInt(new BigDecimal(0));
		// 交易本金
		// dealTotal.setPrinAmt(registShareAll);
		// 利息总额
		// dealTotal.setIntAmt(tradeAmtAll);
		//全价总额
		dealTotal.setFpriceAmt(settleTransDataDelDto.getPreDevidengAmt());
		// GNS-909:修改除权登记交易赋值 modify by fuyongsheng 20220218 end
		// 交易方向
		dealTotal.setPs(DictCodeList.交易方向_除权登记.getValue());
		// 交易状态
		dealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		// 是否已撤销
		dealTotal.setIsCancel(DictCodeList.是否_否.getValue());
		// 证券管理户代码
		dealTotal.setSecManageAcctId(settleTransDataDelDto.getSecAcctId());
		//交易场所
		dealTotal.setTradeMarket(settleTransDataDelDto.getTradeMarket());
		// 权益登记日
		dealTotal.setRegDate(settleTransDataDelDto.getRegDate());
		// 备注
		dealTotal.setRemark(DEAL_REMARK);
		//预计分红日
		// dealTotal.setBonusCfmDate(settleTransDataDelDto.getDevidentDate());
		dealTotal.setBonusCfmDate(settleTransDataDelDto.getDelDate());
		//分红类型
		dealTotal.setPayType(DictCodeList.股票分红类型_送转.getValue());

		return dealTotal;
	}

	/**
	 * 除权交易 金融产品交易明细设置
	 * @param totalTrdProductDealDto 金融产品汇总交易
	 * @param settleDelDealDetailDto 交易明细列表
	 */
	private TrdProductDealDto setCqTrdProductDealDto(TotalTrdProductDealDto totalTrdProductDealDto,
													 SettleDelDealDetailDto settleDelDealDetailDto, BigDecimal registShareAll) {
		//返回值
		TrdProductDealDto trdProductDealDto = new TrdProductDealDto();
		// 交易编号，按现有的交易编号生成规则，自动生成
		trdProductDealDto.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易));
		// 金融产品代码
		trdProductDealDto.setFinprodId(settleDelDealDetailDto.getFinprodId());
		// 金融产品类型（估值核算）
		trdProductDealDto.setFinprodType(settleDelDealDetailDto.getFinprodType2());
		// 金融产品类型（投管分类）
		trdProductDealDto.setFinprodType2(settleDelDealDetailDto.getFinprodType2());
		// 分支序号
		trdProductDealDto.setBranch(0L);
		// 交易日
		// GNS-909: modify by fuyongsheng 20220219 start
		// trdProductDealDto.setTradeDate(settleDelDealDetailDto.getDelDate());
		trdProductDealDto.setTradeDate(settleDelDealDetailDto.getRegDate());
		// GNS-909: modify by fuyongsheng 20220219 end
		// 交割日
		trdProductDealDto.setSettleDate(settleDelDealDetailDto.getDelDate());
		//交易币种
		trdProductDealDto.setCcy(settleDelDealDetailDto.getCcy());
		// 单位利息
		// GNS-909: modify by fuyongsheng 20220219 start
		// trdProductDealDto.setUnitInt(settleDelDealDetailDto.getPreDevidengAmt());
		trdProductDealDto.setUnitInt(new BigDecimal(0));
		// GNS-909: modify by fuyongsheng 20220219 end
		//交易份额
		// trdProductDealDto.setShareAmt(settleDelDealDetailDto.getRedDateAmt());
		trdProductDealDto.setShareAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleDelDealDetailDto.getPreDevidengAmt(),
				settleDelDealDetailDto.getRedDateAmt()), registShareAll,8, RoundingMode.HALF_UP));
		// 全价总额
		// trdProductDealDto.setFpriceAmt(settleDelDealDetailDto.getBonusAmt());
		trdProductDealDto.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleDelDealDetailDto.getPreDevidengAmt(),
				settleDelDealDetailDto.getRedDateAmt()), registShareAll,8, RoundingMode.HALF_UP));
		// 单位净价
		trdProductDealDto.setUnitCprice(ArithmeticUtil.divide(settleDelDealDetailDto.getPreDevidengAmt(),
				registShareAll, 8, RoundingMode.HALF_UP));
		// 交易方向
		trdProductDealDto.setPs(DictCodeList.交易方向_除权登记.getValue());
		// 交易状态
		trdProductDealDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		// 是否已撤销
		trdProductDealDto.setIsCancel(DictCodeList.是否_否.getValue());
		// 汇总交易编号
		trdProductDealDto.setTotleTradeId(totalTrdProductDealDto.getTotleTradeId());
		// 是否外部交易
		trdProductDealDto.setIsOutTrade(DictCodeList.是否_否.getValue());
		// 交易场所
		trdProductDealDto.setTradeMarket(settleDelDealDetailDto.getTradeMarket());
		// 证券管理户代码
		trdProductDealDto.setSecManageAcctId(settleDelDealDetailDto.getSecAcctId());
		//备注
		trdProductDealDto.setRemark(DEAL_REMARK);

		// 投资组合代码
		trdProductDealDto.setPortfolioId(settleDelDealDetailDto.getPortfolioId());
		// 权益登记日
		trdProductDealDto.setRegDate(settleDelDealDetailDto.getRegDate());
		// GNS-909:预计分红日登录除权日 modify by fuyongsheng 20220219 start
		//预计分红日
		// trdProductDealDto.setBonusCfmDate(settleDelDealDetailDto.getDevidentDate());
		trdProductDealDto.setBonusCfmDate(settleDelDealDetailDto.getDelDate());
		// GNS-909:预计分红日登录除权日 modify by fuyongsheng 20220219 end
		trdProductDealDto.setRegDateAmt(settleDelDealDetailDto.getRedDateAmt());
		//分红类型
		trdProductDealDto.setPayType(DictCodeList.股票分红类型_送转.getValue());

		return trdProductDealDto;
	}

	/**
	 * 生成投资组合交易
	 *
	 * @param deal            交易明细
	 * @return 投资组合交易
	 */
	protected PtlTrade setPtlTrade(TrdProductDealDto deal) {
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
	 * 除权交易和交易 金融产品汇总交易DTO
	 *
	 * @param isCq 是否除权交易
	 * @return 金融产品汇总交易DTO
	 */
	private TotalTrdProductDealDto setTotalTrdProductDealDto(List<SettleTransDataDto> settleTransDataDtos,boolean isCq) {
		//返回值
		TotalTrdProductDealDto dealTotal = new TotalTrdProductDealDto();

		SettleTransDataDto settleTransDataDto = settleTransDataDtos.get(0);

		// 总持仓数量
		BigDecimal amountAll = BigDecimal.ZERO;
		for (SettleTransDataDto dto : settleTransDataDtos) {
			amountAll = ArithmeticUtil.doAdd(amountAll, dto.getAmount());
		}
		dealTotal.setAmount(amountAll);

		//  汇总交易编号
		dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易));
		// 业务类型
		dealTotal.setBusiType(DictCodeListExt2.共通业务类型_股票分红交易.getValue());
		// 金融产品代码
		dealTotal.setFinprodId(settleTransDataDto.getFinprodId());
		// 金融产品类型（估值核算）
		dealTotal.setFinprodType(settleTransDataDto.getFinprodType2());
		// 金融产品类型（投管分类）
		dealTotal.setFinprodType2(settleTransDataDto.getFinprodType2());
		// 分支序号
		dealTotal.setBranch(0L);
		// 交易日
		dealTotal.setTradeDate(settleTransDataDto.getSettleDate());
		// 交割日
		dealTotal.setSettleDate(settleTransDataDto.getSettleDate());
		// 交易币种
		dealTotal.setCcy(settleTransDataDto.getCcy());
		if(StringUtil.equals(settleTransDataDto.getPs(),DictCodeList.交易方向_现金分红.getValue())){
			//单位净价
			// GNS-932:现金分红的单位净价为空 modify by fuyongsheng 20220224 start
			// dealTotal.setUnitCprice(settleTransDataDto.getUnitCprice());
			dealTotal.setUnitCprice(null);
			// 单位利息：上交所=存续期明细.单位利息，深交所=存续期明细.全价总额/sum(持仓明细.数量)
			if(DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(settleTransDataDto.getTradeMarket())){
				dealTotal.setUnitInt(settleTransDataDto.getUnitInt());
			}else{
				dealTotal.setUnitInt(ArithmeticUtil.divide(settleTransDataDto.getFpriceAmt(),amountAll,4));
			}
			// GNS-932:现金分红的单位净价为空 modify by fuyongsheng 20220224 end
			//分红类型
			dealTotal.setPayType(DictCodeList.股票分红类型_现金分红.getValue());
		}else {
			// GNS-866:设置小数点后保留位数,防止无线部循环小数错误 modify by fuyongsheng 20220215 start
			// dealTotal.setUnitCprice(settleTransDataDto.getShareAmt().divide(amountAll));
			dealTotal.setUnitCprice(ArithmeticUtil.divide(settleTransDataDto.getShareAmt(), amountAll,8,RoundingMode.HALF_UP));
			// GNS-866:设置小数点后保留位数,防止无线部循环小数错误 modify by fuyongsheng 20220215 end
			//分红类型
			dealTotal.setPayType(DictCodeList.股票分红类型_送转.getValue());
			// GNS-932:送转的单位利息为空 modify by fuyongsheng 20220224 start
			dealTotal.setUnitInt(null);
			// GNS-932:送转的单位利息为空 modify by fuyongsheng 20220224 end
		}
		// modify by etliu 20220219 start
		// GNS-875：【广农商场内清算】股票现金分红，权益登记日日终上交所解析JSMX03文件；深交所解析SJSJG文件后，成交数据明细表、各交易表和头寸表数据登录不正确
//		//交易份额
//		dealTotal.setShareAmt(amountAll);
		// modify by etliu 20220219 end

		//全价总额
		dealTotal.setFpriceAmt(settleTransDataDto.getFpriceAmt());
		// 交易状态
		dealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		// 是否已撤销
		dealTotal.setIsCancel(DictCodeList.是否_否.getValue());
		// 证券管理户代码
		dealTotal.setSecManageAcctId(settleTransDataDto.getSecAcctId());
		//交易场所
		dealTotal.setTradeMarket(settleTransDataDto.getTradeMarket());
		// 权益登记日
		dealTotal.setRegDate(settleTransDataDto.getRegDate());
		// 备注
		dealTotal.setRemark(DEAL_REMARK);
		//预计分红日
		dealTotal.setBonusCfmDate(settleTransDataDto.getSettleDate());

		if(isCq){
			// 交易方向
			dealTotal.setPs(DictCodeList.交易方向_除权登记.getValue());
			// GNS-909: 修改深交所除权登记交易字段赋值 modify by fuyongsheng 20220219 start
			if (StringUtil.equals(DictCodeList.股票分红类型_送转.getValue(), dealTotal.getPayType())) {
				dealTotal.setShareAmt(settleTransDataDto.getShareAmt());
				// dealTotal.setFpriceAmt(settleTransDataDto.getShareAmt());
				dealTotal.setFpriceAmt(null);
			}
			// GNS-909: 修改深交所除权登记交易字段赋值 modify by fuyongsheng 20220219 end
		}else {
			// 交易方向
			// dealTotal.setPs(settleTransDataDto.getPs());
			if(StringUtil.equals(settleTransDataDto.getPs(),DictCodeList.交易方向_现金分红.getValue())){
				//交易金额
				dealTotal.setTradeAmt(settleTransDataDto.getTradeAmt());
				// GNS-877:现金分红的交易汇总表交易方向保持与页面逻辑同步 modify by fuyongsheng 20220227 start
				dealTotal.setPs(DictCodeList.股票分红类型_现金分红.getValue());
				// GNS-877:现金分红的交易汇总表交易方向保持与页面逻辑同步 modify by fuyongsheng 20220227 end
			}
			// GNS-932:修改送转汇总交易表赋值 modify by fuyongsheng 20220225 start
			else{
				dealTotal.setShareAmt(settleTransDataDto.getShareAmt());
				dealTotal.setFpriceAmt(null);
				// GNS-877:现金分红的交易汇总表交易方向保持与页面逻辑同步 modify by fuyongsheng 20220227 start
				dealTotal.setPs(DictCodeList.股票分红类型_送转.getValue());
				// GNS-877:现金分红的交易汇总表交易方向保持与页面逻辑同步 modify by fuyongsheng 20220227 end
			}
			// GNS-932:修改送转汇总交易表赋值 modify by fuyongsheng 20220225 end
		}
		return dealTotal;
	}

	/**
	 * 除权交易和交易（现金分红，送转深交所） 金融产品交易明细设置
	 * @param totalTrdProductDealDto 金融产品交易明细
	 * @param settleTransDataDto 除权交易明细列表
	 */
	private TrdProductDealDto setTrdProductDealDto(TotalTrdProductDealDto totalTrdProductDealDto,
												   SettleTransDataDto settleTransDataDto,boolean isCq, BigDecimal amountAll) {
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
		trdProductDealDto.setTradeDate(settleTransDataDto.getSettleDate());
		// 交割日
		trdProductDealDto.setSettleDate(settleTransDataDto.getSettleDate());
		//交易币种
		trdProductDealDto.setCcy(settleTransDataDto.getCcy());
		//单位净价
		trdProductDealDto.setUnitCprice(totalTrdProductDealDto.getUnitCprice());
		// 单位利息
		trdProductDealDto.setUnitInt(totalTrdProductDealDto.getUnitInt());
		//交易份额
		trdProductDealDto.setShareAmt(settleTransDataDto.getAmount());
		// 全价总额
		// GNS-863,GNS-885: 设置精确位数 modify by fuyongsheng start
		// trdProductDealDto.setFpriceAmt(settleTransDataDto.getFpriceAmt().multiply(settleTransDataDto.getAmount().divide(totalTrdProductDealDto.getAmount())));
		trdProductDealDto.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataDto.getFpriceAmt(),
				settleTransDataDto.getAmount()), totalTrdProductDealDto.getAmount(),4, RoundingMode.HALF_UP));
		// GNS-863,GNS-885: 设置精确位数 modify by fuyongsheng end
		// 交易状态
		trdProductDealDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		// 是否已撤销
		trdProductDealDto.setIsCancel(DictCodeList.是否_否.getValue());
		// 汇总交易编号
		trdProductDealDto.setTotleTradeId(totalTrdProductDealDto.getTotleTradeId());
		// 是否外部交易
		trdProductDealDto.setIsOutTrade(DictCodeList.是否_否.getValue());
		// 交易场所
		trdProductDealDto.setTradeMarket(settleTransDataDto.getTradeMarket());
		// 证券管理户代码
		trdProductDealDto.setSecManageAcctId(settleTransDataDto.getSecAcctId());
		//备注
		trdProductDealDto.setRemark(DEAL_REMARK);

		// 投资组合代码
		trdProductDealDto.setPortfolioId(settleTransDataDto.getPortfolioId());
		// 权益登记日
		trdProductDealDto.setRegDate(settleTransDataDto.getTradeDate());
		//预计分红日
		trdProductDealDto.setBonusCfmDate(settleTransDataDto.getSettleDate());
		//权益登记日份额
		trdProductDealDto.setRegDateAmt(settleTransDataDto.getAmount());
		//资金账户
		trdProductDealDto.setAcctNo(settleTransDataDto.getAcctNo());

		if(isCq) {
			// 交易方向
			trdProductDealDto.setPs(DictCodeList.交易方向_除权登记.getValue());
			// GNS-909: modify by fuyongsheng 20220219 start
			if(!StringUtil.equals(settleTransDataDto.getPs(), DictCodeList.交易方向_现金分红.getValue())){
				trdProductDealDto.setPayType(DictCodeList.股票分红类型_送转.getValue());
				// 单位利息
				// trdProductDealDto.setUnitInt(new BigDecimal(0));
				trdProductDealDto.setUnitInt(null);
				// 交易份额
				trdProductDealDto.setShareAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataDto.getShareAmt(),
						settleTransDataDto.getAmount()), amountAll,8, RoundingMode.HALF_UP));
				// 全价总额
				// trdProductDealDto.setFpriceAmt(settleDelDealDetailDto.getBonusAmt());
//				trdProductDealDto.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataDto.getShareAmt(),
//						settleTransDataDto.getAmount()), amountAll,8, RoundingMode.HALF_UP));
				// 全价总额
				trdProductDealDto.setFpriceAmt(null);
				// 单位净价
				trdProductDealDto.setUnitCprice(ArithmeticUtil.divide(settleTransDataDto.getShareAmt(), amountAll, 8, RoundingMode.HALF_UP));
			}
			else {
				// 交易份额
				trdProductDealDto.setShareAmt(null);
			}
			// GNS-909: modify by fuyongsheng 20220219 end
		}else {
			// 交易方向
			trdProductDealDto.setPs(settleTransDataDto.getPs());
			if(StringUtil.equals(settleTransDataDto.getPs(),DictCodeList.交易方向_现金分红.getValue())){
				// GNS-863: 设置精确位数 modify by fuyongsheng start
				trdProductDealDto.setTradeAmt(settleTransDataDto.getTradeAmt().multiply(settleTransDataDto.getAmount())
						.divide(totalTrdProductDealDto.getAmount(),4, BigDecimal.ROUND_HALF_UP));
				// GNS-863: 设置精确位数 modify by fuyongsheng end

				// modify by etliu 20220219 start
				// GNS-875:【广农商场内清算】股票现金分红，权益登记日日终上交所解析JSMX03文件；深交所解析SJSJG文件后，成交数据明细表、各交易表和头寸表数据登录不正确
				// 交易份额
				trdProductDealDto.setShareAmt(null);
				// modify by etliu 20220219 end
			}else{
				// 交易份额
				trdProductDealDto.setShareAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataDto.getShareAmt(),
						settleTransDataDto.getAmount()), amountAll,8, RoundingMode.HALF_UP));
				// 全价总额
				trdProductDealDto.setFpriceAmt(null);
				String calendarId;
				if(DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(trdProductDealDto.getTradeMarket())){
					calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID);
				}else{
					calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SZJYS_CALID);
				}
				// 交割日
				trdProductDealDto.setSettleDate(stlDataDealProcessCommonService.getNextWorkDay(settleTransDataDto.getSettleDate(), calendarId));
			}
		}

		// modify by etliu 20220219 start
		// GNS-875:【广农商场内清算】股票现金分红，权益登记日日终上交所解析JSMX03文件；深交所解析SJSJG文件后，成交数据明细表、各交易表和头寸表数据登录不正确
		if (trdProductDealDto.getUnitCprice() != null && BigDecimal.ZERO.compareTo(trdProductDealDto.getUnitCprice()) >= 0) {
			trdProductDealDto.setUnitCprice(null);
		}
		// modify by etliu 20220219 end

		return trdProductDealDto;
	}

	/**
	 * 生成划款明细
	 * @param deal
	 * @return
	 */
	protected StlPayOrders generateStlPayOrders(TrdProductDealDto deal) {
		StlPayOrders stlPayOrders = new StlPayOrders();
		// 指令编号
		stlPayOrders.setOrderId(settleCommonService.buildBussinessIdBySeqno("9998"));
		// 划款类型
		stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
		// 结算日期
		stlPayOrders.setSettleDate(deal.getSettleDate());
		//本方资金账户
		stlPayOrders.setMyAcctNo(deal.getAcctNo());
		// 收付方向
		stlPayOrders.setPs(DictCodeList.收付方向_收入.getValue());
		// 结算金额
		stlPayOrders.setSettleAmt(deal.getTradeAmt());
		// 币种
		stlPayOrders.setCcy(deal.getCcy());
		// 是否需要发送
		stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
		// 是否汇总交易
		stlPayOrders.setIsTotalTrade(DictCodeList.是否_是.getValue());
		// 交易编号
		stlPayOrders.setTradeId(deal.getTradeId());
		// 指令状态
		stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
		// 投资组合代码
		stlPayOrders.setPortfolioId(deal.getPortfolioId());
		stlPayOrders.setRemark(DEAL_REMARK);

		return stlPayOrders;
	}

}
