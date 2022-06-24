/*
 * 文件名：StlDataGatherFund2Processor.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年07月30日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.settle.batch.datagather.processor;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.service.StlDataDealProcessCommonService;
import com.joyintech.tams.settle.batch.datagather.dto.*;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基金分红处理
 *
 * @author 王波
 * @date 2020年07月30日
 * @see StlDataGatherFund2Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherFund2Processor extends StlDataGatherProcessor {

	private static final String DEAL_REMARK = "数据清算自动处理";

	@Autowired
	private SettleCommonService settleCommonService;

	@Autowired
	private StlDataDealProcessCommonService stlDataDealProcessCommonService;

	@Override
	protected String getSecType() {
		return DictCodeListExt1.证券品种_基金.getValue();
	}

	@Override
	protected List<String> getFinprodType() {return Arrays.asList(DictCodeListExt1.金融产品类型_投管用_基金.getValue());}

	@Override
	public InvestTradeWriteDto process(NeedOperateEntity operateEntity) throws Exception {

		InvestTradeReaderDto item = (InvestTradeReaderDto) operateEntity;

		InvestTradeWriteDto investTradeWriteDto = new InvestTradeWriteDto();

		// 第一步：先生成非货基的除权交易数据
		List<SettleDelDealDetailDto> delDtoList = item.getSettleTransDataDelDtoList();
		if (delDtoList != null && delDtoList.size() > 0) {
			Map<String,List<SettleDelDealDetailDto>> tempMap
					= delDtoList.stream().collect(Collectors.groupingBy(x -> {
				return x.getFinprodId() +","+ x.getSecAcctId();
			}));

			Iterator<String> iterator = tempMap.keySet().iterator();
			while (iterator.hasNext()) {
				String tempStr = iterator.next();

				// modify by etliu 20220227 start
				// 广农商二阶段 GNS-931:【广农商场内清算】上/深交所 基金存续期重复跑批，生成数据有误
				// 交易撤销批处理会撤销当天生成的交易，这里的删除逻辑没有必要
//				//保证批可重复执行 （先删除）
//				String[] para = tempStr.split(",");
//				Map<String ,Object> map = new HashMap<>();
//				map.put("finprodId",para[0]);
//				map.put("secAcctId",para[1]);
//				map.put("dealDate", getBatchExecuteInfo().getExecuteDate());
//				map.put("busiType", DictCodeList.共通业务类型_基金类资产存续期.getValue());
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
				// modify by etliu 20220227 end

				List<SettleDelDealDetailDto> settleTransDataDelDtoList = tempMap.get(tempStr);
				if (settleTransDataDelDtoList != null && settleTransDataDelDtoList.size() > 0) {
					// 金融产品交易汇总信息
					TotalTrdProductDealDto dealTotal = new TotalTrdProductDealDto();
					TotalTrdProductDealDto totalTrdProductDealDto = setCqDealTotal(dealTotal, settleTransDataDelDtoList);
					investTradeWriteDto.getFinProductDealTotalList().add(totalTrdProductDealDto);

					for (SettleDelDealDetailDto settleDelDealDetailDto : settleTransDataDelDtoList) {
						// 金融产品交易明细
						TrdProductDealDto dealDto = generateCqProductDelDeal(totalTrdProductDealDto, settleDelDealDetailDto);
						investTradeWriteDto.getProductDealList().add(dealDto);

						// 成交编号业务编号关联表（只针对除权登记数据）
						SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
						settleTransTgMapping.setTradeId(IDGenerator.generate(20));
						// modify by etliu 20220227 start
						// 广农商二阶段 GNS-931:【广农商场内清算】上/深交所 基金存续期重复跑批，生成数据有误
//						settleTransTgMapping.setTradeIdTg(dealDto.getTradeId());
						settleTransTgMapping.setTradeIdTg(totalTrdProductDealDto.getTotleTradeId());
						// modify by etliu 20220227 end
						settleTransTgMapping.setTgDataStatus(DictCodeListStl.投管交易数据状态_处理结束.getValue());
						settleTransTgMapping.setDealDate(getBatchExecuteInfo().getExecuteDate());
						investTradeWriteDto.getSettleTransTgMappingList().add(settleTransTgMapping);

						// List<组合交易DTO>
						investTradeWriteDto.getPtlTradeList().add(generatePtlTrade(dealDto));
					}
				}
			}
		}

		List<SettleTransDataDto> settleTransDataDtoList = item.getSettleTransDataList();
		//第二步 ： 生成货基和非货基的红利再投、现金分红的交易
		if(CollectionUtils.isNotEmpty(settleTransDataDtoList)){
			Map<String,List<SettleTransDataDto>> tempMap = settleTransDataDtoList.stream().collect(Collectors.groupingBy(x->{
				return x.getFinprodId() +","+ x.getSecAcctId();
			}));
			Iterator<String> iterator = tempMap.keySet().iterator();
			while (iterator.hasNext()){
				List<SettleTransDataDto> settleTransDataDtos = tempMap.get(iterator.next());
				// 金融产品交易汇总信息
				TotalTrdProductDealDto dealTotal = generateFundDealTotal(settleTransDataDtos);
				investTradeWriteDto.getFinProductDealTotalList().add(dealTotal);
				for (SettleTransDataDto settleTransDataDto : settleTransDataDtos) {
					// 金融产品交易信息
					TrdProductDealDto trdProductDealDto = generateProductDeal(settleTransDataDto, settleTransDataDtos);
					trdProductDealDto.setTotleTradeId(dealTotal.getTotleTradeId());//汇总交易编号
					// GNS-924:深交所的现金分红交易unitCprice应跟total表一致 modify by fuyongsheng 20220222 start
					if(StringUtil.equals(settleTransDataDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
						trdProductDealDto.setUnitCprice(dealTotal.getUnitCprice());
					}
					// GNS-924:深交所的现金分红交易unitCprice应跟total表一致 modify by fuyongsheng 20220222 end
					investTradeWriteDto.getProductDealList().add(trdProductDealDto);

					// GNS-924:登录settleTransTgMapping表 modify by fuyongsheng 20220220 start
					// 成交编号业务编号关联表
					SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
					settleTransTgMapping.setTradeId(IDGenerator.generate(20));
					settleTransTgMapping.setTradeIdTg(dealTotal.getTotleTradeId());
					settleTransTgMapping.setTgDataStatus(DictCodeListStl.投管交易数据状态_处理结束.getValue());
					settleTransTgMapping.setDealDate(getBatchExecuteInfo().getExecuteDate());
					investTradeWriteDto.getSettleTransTgMappingList().add(settleTransTgMapping);
					// GNS-924:登录settleTransTgMapping表 modify by fuyongsheng 20220220 end

					// 组合交易
					investTradeWriteDto.getPtlTradeList().add(generatePtlTrade(trdProductDealDto));
					if(StringUtil.equals(settleTransDataDto.getPs(), DictCodeList.交易方向_现金分红.getValue())) {
						// 划款指令明细
						StlPayOrders stlPayOrders = generateStlPayOrders(trdProductDealDto);
						investTradeWriteDto.getStlPayOrdersList().add(stlPayOrders);
					}

					//第三步 ：若 存续期明细.交易方向 = 红利再投 AND 存续期明细.零碎份额资金 > 0 还需登录一笔现金分红交易
					if(StringUtil.equals(settleTransDataDto.getPs(), DictCodeList.交易方向_红利再投.getValue())
							// GNS-866: 增加空判断 modify by fuyongsheng 20220215 start
							&& settleTransDataDto.getFragmentaryFunds() != null
							// GNS-866: 增加空判断 modify by fuyongsheng 20220215 end
							&& settleTransDataDto.getFragmentaryFunds().compareTo(new BigDecimal(0))>0) {
						settleTransDataDto.setIntAmt(settleTransDataDto.getFragmentaryFunds());
						settleTransDataDto.setFpriceAmt(new BigDecimal(0));
						settleTransDataDto.setTradeAmt(settleTransDataDto.getFragmentaryFunds());
						settleTransDataDto.setPs(DictCodeList.交易方向_现金分红.getValue());
						// 金融产品交易信息
						TrdProductDealDto trdProductDealDto1 = generateProductDeal(settleTransDataDto, settleTransDataDtos);
						// GNS-923:零股现金生成的现金分红交易无需登录cpriceAmt modify by fuyongsheng start
						trdProductDealDto1.setCpriceAmt(null);
						// GNS-923:零股现金生成的现金分红交易无需登录cpriceAmt modify by fuyongsheng end
						trdProductDealDto1.setTotleTradeId(dealTotal.getTotleTradeId());//汇总交易编号
						investTradeWriteDto.getProductDealList().add(trdProductDealDto1);

						// GNS-924:登录settleTransTgMapping表 modify by fuyongsheng 20220220 start
						// 成交编号业务编号关联表
						settleTransTgMapping = new SettleTransTgMapping();
						settleTransTgMapping.setTradeId(IDGenerator.generate(20));
						settleTransTgMapping.setTradeIdTg(dealTotal.getTotleTradeId());
						settleTransTgMapping.setTgDataStatus(DictCodeListStl.投管交易数据状态_处理结束.getValue());
						settleTransTgMapping.setDealDate(getBatchExecuteInfo().getExecuteDate());
						investTradeWriteDto.getSettleTransTgMappingList().add(settleTransTgMapping);
						// GNS-924:登录settleTransTgMapping表 modify by fuyongsheng 20220220 end

						// 组合交易
						investTradeWriteDto.getPtlTradeList().add(generatePtlTrade(trdProductDealDto1));
						// 划款指令明细
						StlPayOrders stlPayOrders = generateStlPayOrders(trdProductDealDto1);
						investTradeWriteDto.getStlPayOrdersList().add(stlPayOrders);
					}
				}

			}

		}



		investTradeWriteDto.setSecType(this.getSecType());

		return investTradeWriteDto;
	}

	/**
	 * 生成现金分红和红利再投汇总交易
	 *
	 * @param settleTransDataDtos 成交明细
	 * @return 汇总交易
	 */
	private TotalTrdProductDealDto generateFundDealTotal(List<SettleTransDataDto> settleTransDataDtos) {
		// 金融产品交易汇总信息
		TotalTrdProductDealDto dealTotal = new TotalTrdProductDealDto();

		SettleTransDataDto settleTransDataDto = settleTransDataDtos.get(0);
		//  汇总交易编号
		dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易));
		// 业务类型 TRD14
		dealTotal.setBusiType(DictCodeList.共通业务类型_基金类资产存续期.getValue());
		// 金融产品代码
		dealTotal.setFinprodId(settleTransDataDto.getFinprodId());
		// 金融产品类型（估值核算）
		dealTotal.setFinprodType(settleTransDataDto.getFinprodType());
		// 金融产品类型（投管分类）
		dealTotal.setFinprodType2(settleTransDataDto.getFinprodType2());
		// 分支序号
		dealTotal.setBranch(settleTransDataDto.getBranch());
		// 交易日
		dealTotal.setTradeDate(settleTransDataDto.getTradeDate());
		// 交割日
		dealTotal.setSettleDate(settleTransDataDto.getSettleDate());
		// 交易币种
		dealTotal.setCcy(settleTransDataDto.getCcy());
		//单位净价
		dealTotal.setUnitCprice(settleTransDataDto.getUnitCprice());
		// 交易本金
		dealTotal.setPrinAmt(settleTransDataDto.getPrinAmt());
		// 利息总额
		dealTotal.setIntAmt(settleTransDataDto.getIntAmt());
		//全价总额
		dealTotal.setFpriceAmt(settleTransDataDto.getFpriceAmt());
		// 随交易支付费用
//        BigDecimal feeAmtPay = ArithmeticUtil.doAdd(settleTransDataDto.getJsfAmt(), settleTransDataDto.getGhfAmt(), settleTransDataDto.getZgfAmt(),
//				settleTransDataDto.getSxfAmt(), settleTransDataDto.getSettleFee(), settleTransDataDto.getStampFee());
//        dealTotal.setFeeAmtPay(feeAmtPay);
		// 交易金额
		dealTotal.setTradeAmt(settleTransDataDto.getTradeAmt());
		// 未随交易支付费用
//        dealTotal.setFeeAmtUnpay(BigDecimal.ZERO);
		// 总交易费用
//        dealTotal.setFeeAmt(feeAmtPay);
		// 交易方向
		dealTotal.setPs(settleTransDataDto.getPs());
		// 交易状态
		dealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		// 是否已撤销
		dealTotal.setIsCancel(DictCodeList.是否_否.getValue());
		//证券管理户代码
		dealTotal.setSecManageAcctId(settleTransDataDto.getSecAcctId());
		//交易场所
		dealTotal.setTradeMarket(settleTransDataDto.getTradeMarket());
		//权益登记日
		dealTotal.setRegDate(settleTransDataDto.getRegDate());
		//前置交易编号
		dealTotal.setbTradeId(settleTransDataDto.getbTradeId());
		//预计分红日
		dealTotal.setBonusCfmDate(settleTransDataDto.getSettleDate());
		// 备注
		dealTotal.setRemark(DEAL_REMARK);
		// GNS-923:红利再投SHARE_AMT应是多个组合之和 modify by fuyongsheng 20220222 start
		// 交易份额
		dealTotal.setShareAmt(settleTransDataDtos.stream().map(SettleTransData::getShareAmt).reduce(BigDecimal.ZERO, ArithmeticUtil::doAdd));
		// GNS-923:红利再投SHARE_AMT应是多个组合之和 modify by fuyongsheng 20220222 end
		// GNS-924:现金分红登录字段有误 modify by fuyongsheng 20220220 start
		if(DictCodeList.交易方向_现金分红.getValue().equals(settleTransDataDto.getPs())){
			// 深交所的交易本金登录持仓总额 modify by fuyongsheng 20220221 start
			if(StringUtil.equals(dealTotal.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
				// 交易本金
				dealTotal.setPrinAmt(dealTotal.getShareAmt());
				// 单位净价
				dealTotal.setUnitCprice(ArithmeticUtil.divide(dealTotal.getTradeAmt(),dealTotal.getPrinAmt(),4,RoundingMode.HALF_UP));
			}
			// 深交所的交易本金登录持仓总额 modify by fuyongsheng 20220221 end
		}
//		else{
//			// 交易份额
//			dealTotal.setShareAmt(settleTransDataDto.getShareAmt());
//		}
		// 除权除息日
		dealTotal.setExDevidentDate(settleTransDataDto.getExDevidentDate());
		// GNS-924:现金分红登录字段有误 modify by fuyongsheng 20220220 end
//        if (cqTotalTrdProductDealDto != null) {
		// 前置交易编号
//	        dealTotal.setbTradeId(cqTotalTrdProductDealDto.getTotleTradeId());
		// 权益登记日
//			dealTotal.setRegDate(cqTotalTrdProductDealDto.getRegDate());
		// 单位净价
//			dealTotal.setUnitCprice(cqTotalTrdProductDealDto.getUnitCprice());
//			// 分红到账日
//			dealTotal.setBonusCfmDate(cqTotalTrdProductDealDto.getBonusCfmDate());
//        }
//        else {
//        	String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID);
//        	// 权益登记日
//        	dealTotal.setRegDate(stlDataDealProcessCommonService.getBeforeWorkDay(settleTransDataDto.getTradeDate(), calendarId));
//        }
		// 分红类型
		// GNS-921:分红类型赋值 modify by fuyongsheng 20220224 start
		if (StringUtil.equals(DictCodeList.交易方向_红利再投.getValue(), settleTransDataDto.getPs())) {
			dealTotal.setPayType(DictCodeList.分红方式_红利再投.getValue());
		}
		else {
			dealTotal.setPayType(DictCodeList.分红方式_现金分红.getValue());
		}
		// GNS-921:分红类型赋值 modify by fuyongsheng 20220224 end


		return dealTotal;
	}

	/**
	 * 生成 金融产品交易
	 *
	 * @param settleTransDataDto 金融产品汇总交易DTO TotalTrdProductDealDto
	 * @param settleTransDataDto 成交明细
	 * @return TrdProductDealDto
	 */
	private TrdProductDealDto generateProductDeal(SettleTransDataDto settleTransDataDto, List<SettleTransDataDto> settleTransDataDtos) {

		TrdProductDealDto trdProductDealDto = new TrdProductDealDto();

		BigDecimal sumShareAmt = new BigDecimal(0);
		for (SettleTransDataDto settleTransDataDto1 : settleTransDataDtos){
			sumShareAmt=sumShareAmt.add(settleTransDataDto1.getShareAmt());
		}
		// GNS-872:使用工具类进行运算 modify by fuyongsheng 20220219 start
		// BigDecimal rate = settleTransDataDto.getShareAmt().divide(sumShareAmt);
		BigDecimal rate = ArithmeticUtil.divide(settleTransDataDto.getShareAmt(), sumShareAmt, 8, RoundingMode.HALF_UP);
		// GNS-872:使用工具类进行运算 modify by fuyongsheng 20220219 end
		// 交易编号，按现有的交易编号生成规则，自动生成
		trdProductDealDto.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易));
		//业务类型
		trdProductDealDto.setBusiType(settleTransDataDto.getBusiType());
		// 金融产品代码
		trdProductDealDto.setFinprodId(settleTransDataDto.getFinprodId());
		// 金融产品类型（投管分类）
		trdProductDealDto.setFinprodType2(settleTransDataDto.getFinprodType2());
		// 分支序号
		trdProductDealDto.setBranch(0L);
		// 交易日期
		trdProductDealDto.setTradeDate(settleTransDataDto.getTradeDate());
		// 交割日期
		trdProductDealDto.setSettleDate(settleTransDataDto.getSettleDate());
		// 交易币种
		trdProductDealDto.setCcy(settleTransDataDto.getCcy());
		// 单位净价
		trdProductDealDto.setUnitCprice(settleTransDataDto.getUnitCprice());
		//交易份额
		trdProductDealDto.setShareAmt(settleTransDataDto.getShareAmt());
		//交易本金
		// GNS-866: 使用ArithmeticUtil进行计算，防止null值 modify by fuyongsheng 20220215 start
		// trdProductDealDto.setPrinAmt(settleTransDataDto.getPrinAmt().multiply(rate));
		// GNS-924:深交所的现金分红交易本金 modify by fuyongsheng 20220221 start
		if(StringUtil.equals(settleTransDataDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
			trdProductDealDto.setPrinAmt(ArithmeticUtil.multiply(settleTransDataDto.getFpriceAmt(), rate));
		}else{
			trdProductDealDto.setPrinAmt(ArithmeticUtil.multiply(settleTransDataDto.getPrinAmt(), rate));
		}
		// GNS-924:深交所的现金分红交易本金 modify by fuyongsheng 20220221 end
		// 利息总额
		// trdProductDealDto.setIntAmt(settleTransDataDto.getIntAmt().multiply(rate));
		trdProductDealDto.setIntAmt(ArithmeticUtil.multiply(settleTransDataDto.getIntAmt(), rate));
		//全价总额
		// trdProductDealDto.setFpriceAmt(settleTransDataDto.getFpriceAmt().multiply(rate));
		trdProductDealDto.setFpriceAmt(ArithmeticUtil.multiply(settleTransDataDto.getFpriceAmt(), rate));
		// 交易金额
		// trdProductDealDto.setTradeAmt(settleTransDataDto.getTradeAmt().multiply(rate));
		trdProductDealDto.setTradeAmt(ArithmeticUtil.multiply(settleTransDataDto.getTradeAmt(), rate));
		//净价总额
		// trdProductDealDto.setCpriceAmt(settleTransDataDto.getFpriceAmt().subtract(settleTransDataDto.getIntAmt()));
		trdProductDealDto.setCpriceAmt(ArithmeticUtil.subtract(settleTransDataDto.getFpriceAmt(), settleTransDataDto.getIntAmt()));
		// GNS-866: 使用ArithmeticUtil进行计算，防止null值 modify by fuyongsheng 20220215 end
		//交易方向
		trdProductDealDto.setPs(settleTransDataDto.getPs());
		// 交易状态
		trdProductDealDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		// 设置 是否已撤销，默认“否”
		trdProductDealDto.setIsCancel(DictCodeList.是否_否.getValue());
		// 是否外部交易
		trdProductDealDto.setIsOutTrade(DictCodeList.是否_否.getValue());
		// 交易场所
		trdProductDealDto.setTradeMarket(settleTransDataDto.getTradeMarket());
		// 证券管理户代码
		trdProductDealDto.setSecManageAcctId(settleTransDataDto.getSecAcctId());
		// 登记托管机构
		trdProductDealDto.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
		// 投资组合代码
		trdProductDealDto.setPortfolioId(settleTransDataDto.getPortfolioId());
		// 权益登记日份额
		trdProductDealDto.setRegDateAmt(settleTransDataDto.getShareAmt());
		//预计分红日
		trdProductDealDto.setBonusCfmDate(settleTransDataDto.getSettleDate());
		// 成交编号
//        trdProductDealDto.setDealId(settleTransDataDto.getTradeId());
		if (StringUtil.equals(DictCodeList.交易方向_红利再投.getValue(), settleTransDataDto.getPs())) {
			trdProductDealDto.setPayType(DictCodeList.分红方式_红利再投.getValue());
		}
		else {
			trdProductDealDto.setPayType(DictCodeList.分红方式_现金分红.getValue());
		}
		//资金账户
		trdProductDealDto.setAcctNo(settleTransDataDto.getAcctNo());
		// GNS-924:增加备注 modify by fuyongsheng 20220220 start
		// 备注
		trdProductDealDto.setRemark(DEAL_REMARK);
		// GNS-924:增加备注 modify by fuyongsheng 20220220 end
		// GNS-923:登录权益登记日 modify by fuyongsheng 20220222 start
		//权益登记日
		trdProductDealDto.setRegDate(settleTransDataDto.getRegDate());
		// GNS-923:登录权益登记日 modify by fuyongsheng 20220222 end
		return trdProductDealDto;
	}

	/**
	 * 金融产品交易费用明细DTO
	 *
	 * @param deal                金融产品交易明细
	 * @param settleTransDataDto 成交数据明细
	 * @return 金融产品交易费用明细DTO
	 */
	private List<TrdTradeFeeDetail> generateTradeFeeDetail(TrdProductDealDto deal, SettleTransDataDto settleTransDataDto) {
		List<TrdTradeFeeDetail> res = new ArrayList<>();
		if (StringUtil.isNotEmpty(settleTransDataDto.getJsfAmt()) && settleTransDataDto.getJsfAmt().compareTo(new BigDecimal(0)) != 0) {
			// 经手费
			TrdTradeFeeDetail jsf = new TrdTradeFeeDetail();
			// 交易编号
			jsf.setTradeId(deal.getTradeId());

			// 费用类型
			jsf.setFeeType(DictCodeListExt1.一次性费用类型_经手费.getValue());
			// 是否随交易支付
			jsf.setIsPayWithTrade(DictCodeList.是否_是.getValue());
			// 费用金额
			jsf.setFeeAmt(settleTransDataDto.getJsfAmt());
			// 登记托管机构
			jsf.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
			res.add(jsf);
		}

		if (StringUtil.isNotEmpty(settleTransDataDto.getGhfAmt()) && settleTransDataDto.getGhfAmt().compareTo(new BigDecimal(0)) != 0) {
			// 过户费
			TrdTradeFeeDetail ghf = new TrdTradeFeeDetail();
			// 交易编号
			ghf.setTradeId(deal.getTradeId());

			// 费用类型
			ghf.setFeeType(DictCodeListExt1.一次性费用类型_过户费.getValue());
			// 是否随交易支付
			ghf.setIsPayWithTrade(DictCodeList.是否_是.getValue());
			// 费用金额
			ghf.setFeeAmt(settleTransDataDto.getGhfAmt());
			// 登记托管机构
			ghf.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
			res.add(ghf);
		}

		if (StringUtil.isNotEmpty(settleTransDataDto.getZgfAmt()) && settleTransDataDto.getZgfAmt().compareTo(new BigDecimal(0)) != 0) {
			// 证管费
			TrdTradeFeeDetail zgf = new TrdTradeFeeDetail();
			// 交易编号
			zgf.setTradeId(deal.getTradeId());

			// 费用类型
			zgf.setFeeType(DictCodeListExt1.一次性费用类型_证管费.getValue());
			// 是否随交易支付
			zgf.setIsPayWithTrade(DictCodeList.是否_是.getValue());
			// 费用金额
			zgf.setFeeAmt(settleTransDataDto.getZgfAmt());
			// 登记托管机构
			zgf.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
			res.add(zgf);
		}

		if (StringUtil.isNotEmpty(settleTransDataDto.getSxfAmt()) && settleTransDataDto.getSxfAmt().compareTo(new BigDecimal(0)) != 0) {
			// 手续费
			TrdTradeFeeDetail sxf = new TrdTradeFeeDetail();
			// 交易编号
			sxf.setTradeId(deal.getTradeId());

			// 费用类型
			sxf.setFeeType(DictCodeListExt1.一次性费用类型_交易手续费.getValue());
			// 是否随交易支付
			sxf.setIsPayWithTrade(DictCodeList.是否_是.getValue());
			// 费用金额
			sxf.setFeeAmt(settleTransDataDto.getSxfAmt());
			// 登记托管机构
			sxf.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
			res.add(sxf);
		}


		if (StringUtil.isNotEmpty(settleTransDataDto.getSettleFee()) && settleTransDataDto.getSettleFee().compareTo(new BigDecimal(0)) != 0) {
			// 结算费
			TrdTradeFeeDetail settleFee = new TrdTradeFeeDetail();
			// 交易编号
			settleFee.setTradeId(deal.getTradeId());

			// 费用类型
			settleFee.setFeeType(DictCodeListExt1.一次性费用类型_结算费.getValue());
			// 是否随交易支付
			settleFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
			// 费用金额
			settleFee.setFeeAmt(settleTransDataDto.getSettleFee());
			// 登记托管机构
			settleFee.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
			res.add(settleFee);
		}

		if (StringUtil.isNotEmpty(settleTransDataDto.getCommissionAmt()) && settleTransDataDto.getCommissionAmt().compareTo(new BigDecimal(0)) != 0) {
			// 佣金
			TrdTradeFeeDetail commision = new TrdTradeFeeDetail();
			// 交易编号
			commision.setTradeId(deal.getTradeId());

			// 费用类型
			commision.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
			// 是否随交易支付
			commision.setIsPayWithTrade(settleTransDataDto.getIsPayWithTrade());
			// 费用金额
			commision.setFeeAmt(settleTransDataDto.getCommissionAmt());
			// 登记托管机构
			commision.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
			res.add(commision);
		}
		return res;
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

	/**
	 * 除权交易金融产品汇总交易DTO
	 *
	 * @param dealTotal 金融产品汇总交易
	 * @param settleTransDataDelDtos 除权交易
	 * @return 金融产品汇总交易DTO
	 */
	private TotalTrdProductDealDto setCqDealTotal(TotalTrdProductDealDto dealTotal, List<SettleDelDealDetailDto> settleTransDataDelDtos) {

		SettleDelDealDetailDto settleTransDataDelDto = settleTransDataDelDtos.get(0);

		// ∑除权交易明细.权益登记日份额
		BigDecimal registShareAll = BigDecimal.ZERO;

		// ∑除权交易明细.分红金额
		BigDecimal tradeAmtAll = BigDecimal.ZERO;

		for (SettleDelDealDetailDto transDataDelDto : settleTransDataDelDtos) {
			registShareAll = ArithmeticUtil.doAdd(registShareAll, transDataDelDto.getRedDateAmt());
			tradeAmtAll = ArithmeticUtil.doAdd(tradeAmtAll, transDataDelDto.getBonusAmt());
		}

		//  汇总交易编号
		dealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易));
		// 业务类型
		dealTotal.setBusiType(DictCodeList.共通业务类型_基金类资产存续期.getValue());
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
		// 单位净价
		dealTotal.setUnitCprice(settleTransDataDelDto.getUnitBonusUnitValue());
		// 交易本金
		dealTotal.setPrinAmt(registShareAll);
		// 利息总额
		dealTotal.setIntAmt(tradeAmtAll);
		//全价总额
		dealTotal.setFpriceAmt(tradeAmtAll);
		// 交易金额
		dealTotal.setTradeAmt(tradeAmtAll);
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
		//除权除息日
		dealTotal.setExDevidentDate(settleTransDataDelDto.getDelDate());
		// 备注
		dealTotal.setRemark(DEAL_REMARK);

		// 分红类型
		dealTotal.setPayType(settleTransDataDelDto.getBonusMode());
		// 分红到账日
		dealTotal.setBonusCfmDate(settleTransDataDelDto.getDevidentDate());

		return dealTotal;
	}

	/**
	 * 根据除权交易生成金融产品交易明细
	 * @param totalTrdProductDealDto 汇总交易明细
	 * @param settleDelDealDetailDto 除权交易明细
	 * @return 金融产品交易明细
	 */
	private TrdProductDealDto generateCqProductDelDeal(TotalTrdProductDealDto totalTrdProductDealDto, SettleDelDealDetailDto settleDelDealDetailDto) {

		TrdProductDealDto trdProductDealDto = new TrdProductDealDto();
		setCqTrdProductDealDel(trdProductDealDto, totalTrdProductDealDto, settleDelDealDetailDto);
		return trdProductDealDto;
	}

	/**
	 * 除权交易 金融产品交易明细设置
	 * @param trdProductDealDto 金融产品交易明细
	 * @param totalTrdProductDealDto 除权交易明细列表
	 */
	private void setCqTrdProductDealDel(TrdProductDealDto trdProductDealDto, TotalTrdProductDealDto totalTrdProductDealDto,SettleDelDealDetailDto settleDelDealDetailDto) {

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
		trdProductDealDto.setTradeDate(settleDelDealDetailDto.getDelDate());
		// 交割日
		trdProductDealDto.setSettleDate(settleDelDealDetailDto.getDelDate());
		//交易币种
		trdProductDealDto.setCcy(settleDelDealDetailDto.getCcy());
		// 单位净价
		trdProductDealDto.setUnitCprice(settleDelDealDetailDto.getUnitBonusUnitValue());
		// 交易本金
		trdProductDealDto.setPrinAmt(settleDelDealDetailDto.getRedDateAmt());
		// 利息总额
		trdProductDealDto.setIntAmt(settleDelDealDetailDto.getBonusAmt());
		// 全价总额
		trdProductDealDto.setFpriceAmt(settleDelDealDetailDto.getBonusAmt());
		// 交易金额
		trdProductDealDto.setTradeAmt(settleDelDealDetailDto.getBonusAmt());
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
		// 登记托管机构
		trdProductDealDto.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
		//备注
		trdProductDealDto.setRemark(DEAL_REMARK);
		// 投资组合代码
		trdProductDealDto.setPortfolioId(settleDelDealDetailDto.getPortfolioId());
		// 权益登记日
		trdProductDealDto.setRegDate(settleDelDealDetailDto.getRegDate());
		// 权益登记日份额
		trdProductDealDto.setRegDateAmt(settleDelDealDetailDto.getRedDateAmt());
		//预计分红日
		trdProductDealDto.setBonusCfmDate(settleDelDealDetailDto.getDevidentDate());
	}

	/**
	 * 生成投资组合交易
	 *
	 * @param deal            交易明细
	 * @return 投资组合交易
	 */
	protected PtlTrade generatePtlTrade(TrdProductDealDto deal) {
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
		ptlTrade.setBusiType("TRD14");
		// 证券托管户代码
		ptlTrade.setSecManageAcctId(deal.getSecManageAcctId());
		// 实际发生日
		ptlTrade.setSettleDate(deal.getTradeDate());
		return ptlTrade;
	}
}
