package com.joyintech.tams.invest.product.valuation.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.product.valuation.dao.FinProductValuation001Dao;
import com.joyintech.tams.invest.product.valuation.dto.AssetDealInfoDto;
import com.joyintech.tams.invest.product.valuation.dto.FinProductValuation001Dto;
import com.joyintech.tams.invest.product.valuation.dto.FinProductValuationDetail001Dto;
import com.joyintech.tams.invest.product.valuation.dto.ProdDealInfoDto;
import com.joyintech.tams.invest.product.valuation.dto.ProdFeeInfoDto;
import com.joyintech.tams.invest.product.valuation.dto.ProdInfoDto;
import com.joyintech.tams.invest.product.valuation.utils.ValuationUtils;

@Service
public class FinProductValuation001Service extends BaseService<FinProductValuation001Dto> {

	private final static Logger logger = LoggerFactory.getLogger(FinProductValuation001Service.class);

	// 资产估值明细
	private ThreadLocal<List<FinProductValuationDetail001Dto>> assetInfoList = ThreadLocal.withInitial(ArrayList::new);

	// 费用估值明细
	private ThreadLocal<List<FinProductValuationDetail001Dto>> feeInfoList = ThreadLocal.withInitial(ArrayList::new);

	@Autowired
	private FinProductValuation001Dao finProductValuation001Dao;

	/**
	 * 查询产品信息
	 * 
	 * @param prodcode
	 * @return
	 */
	public ProdInfoDto selectProdInfo(String prodcode) {
		return finProductValuation001Dao.selectProdInfo(prodcode);
	}

	/**
	 * 查询估值信息
	 * 
	 * @param pageParam
	 * @return
	 */
	public Page<FinProductValuation001Dto> selectForPage(Map<String, Object> pageParam) {
		return super.queryForPage(() -> finProductValuation001Dao.selectForPage(pageParam));
	}

	/**
	 * 计算估值
	 * 
	 * @param condition
	 * @param start
	 * @param vldate
	 */
	@Transactional(rollbackFor = Exception.class)
	public void calcValue(FinProductValuation001Dto condition, Date start, Date vldate) {

		// 查询存续本金
		List<ProdDealInfoDto> prodList = finProductValuation001Dao
				.selectDurationProd(StringUtil.splitToList(",", condition.getProdcode()), vldate);
		if (StringUtil.isEmptyList(prodList)) {
			return;
		}
		ExecutorService executor = Executors.newFixedThreadPool(1);
		final CountDownLatch counter = new CountDownLatch(prodList.size());
		prodList.forEach(prodInfo -> {
			if (prodInfo.getPrinamt().compareTo(BigDecimal.ZERO) <= 0) {
				counter.countDown();
				return;
			}
			executor.execute(() -> {
				try {
					buildValuation(prodInfo, start, vldate);
				} catch (Exception e) {
					logger.info("估值出错!");
					throw new IllegalArgumentException(e);
				} finally {
					this.assetInfoList.remove();
					this.feeInfoList.remove();
					counter.countDown();
				}
			});
		});
		try {
			counter.await();
		} catch (InterruptedException e) {
			logger.info("估值出错!!");
			//add by chenxiaoqiu 20191128
			Thread.currentThread().interrupt();
			throw new IllegalArgumentException(e);
		} finally {
			executor.shutdown();
		}
	}

	/**
	 * 删除估值
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteValuation() {
		finProductValuation001Dao.deleteValuation();
		finProductValuation001Dao.deleteValuationDetail();
	}

	/**
	 * 计算估值
	 * 
	 * @param prodInfo
	 * @param start
	 * @param vldate
	 * @return
	 */
	private BigDecimal buildValuation(ProdDealInfoDto prodInfo, Date start, Date vldate) {

		// 产品份额
		BigDecimal prinqty = prodInfo.getPrinqty();
		// 计算资产价值
		BigDecimal assetValue = calcAssetValue(prodInfo, start, vldate);
		// 计算产品费用
		BigDecimal feeValue = calcFeeValue(prodInfo, start, vldate);
		// 产品净资产
		BigDecimal prodNetValue = BigDecimal.ZERO;
		// 产品净值
		BigDecimal netValue = BigDecimal.ZERO;
		// 分红净值
		BigDecimal unitBonus = BigDecimal.ZERO;
		// 产品累计净值
		BigDecimal totNetValue = BigDecimal.ZERO;
		// 累计折合年华收益率
		BigDecimal totCurYield = BigDecimal.ZERO;
		// 七日年化
		BigDecimal yield7 = BigDecimal.ZERO;
		if (DictCodeList.收益类型_净值型.getValue().equals(prodInfo.getProfittype())) {

			prodNetValue = assetValue.subtract(feeValue);
			netValue = prodNetValue.divide(prinqty, 4, RoundingMode.HALF_UP);
			unitBonus = finProductValuation001Dao.selectUnitBonus("F16_".concat(prodInfo.getProdcode()), null, vldate);
			totNetValue = netValue.add(unitBonus == null ? BigDecimal.ZERO : unitBonus);
			// 计息基础
			BigDecimal basis = ValuationUtils.convertBasis(prodInfo.getBasis());
			totCurYield = totNetValue.subtract(BigDecimal.ONE)
					.multiply(basis == null ? BigDecimal.valueOf(365L) : basis)
					.divide(BigDecimal.valueOf(DateUtil.getBetweenDayNumber(prodInfo.getVdate(), vldate) + 1), 4,
							RoundingMode.HALF_UP)
					.multiply(BigDecimal.valueOf(100L));

			long T1 = DateUtil.compareDate(DateUtil.plusDays(DateUtil.format(vldate), -6),
					DateUtil.format(prodInfo.getVdate()));
			// 年化周期
			BigDecimal T = BigDecimal
					.valueOf(T1 >= 0 ? 7L : DateUtil.getBetweenDayNumber(prodInfo.getVdate(), vldate) + 1);
			BigDecimal r1 = finProductValuation001Dao.selectBeforeNetValue("F16_".concat(prodInfo.getProdcode()),
					DateUtil.addDay(vldate, T.subtract(BigDecimal.ONE).negate().intValue()));
			BigDecimal rbonus = finProductValuation001Dao.selectUnitBonus("F16_".concat(prodInfo.getProdcode()), null,
					DateUtil.addDay(vldate, T.subtract(BigDecimal.ONE).negate().intValue()));
			r1 = r1 == null ? BigDecimal.ONE : r1.add(rbonus == null ? BigDecimal.ZERO : rbonus);
			yield7 = totNetValue.subtract(r1).multiply(basis == null ? BigDecimal.valueOf(365L) : basis)
					.divide(T, 12, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100L));
		}

		FinProductValuation001Dto dto = new FinProductValuation001Dto();
		dto.setProdcode(prodInfo.getProdcode());
		dto.setProd4short(prodInfo.getProdname());
		dto.setProfitType(prodInfo.getProfittype());
		dto.setVldate(vldate);
		dto.setProdNetAsset(prodNetValue);
		dto.setProdTotQty(prinqty);
		dto.setUnpayProfit(BigDecimal.ZERO);
		dto.setYield(BigDecimal.ZERO);
		dto.setTotYield(BigDecimal.ZERO);
		dto.setNetValue(netValue);
		dto.setTotNetValue(totNetValue);
		dto.setCurYield(BigDecimal.ZERO);
		dto.setTotCurYield(totCurYield);
		dto.setYield7(yield7);
		Long pvalueid = insertValue(dto);
		insertAssetInfoDetail(assetInfoList.get(), feeInfoList.get(), pvalueid);
		return netValue;
	}

	/**
	 * 计算资产价值
	 * 
	 * @param prodInfo
	 * @param start
	 * @param vldate
	 * @return
	 */
	private BigDecimal calcAssetValue(ProdDealInfoDto prodInfo, Date start, Date vldate) {

		// 查询投资端估值日交易信息
		List<AssetDealInfoDto> dealList = finProductValuation001Dao.selectAssetDealInfo(prodInfo, vldate);
		dealList.forEach(dealInfo -> {
			FinProductValuationDetail001Dto dto = new FinProductValuationDetail001Dto();
			dto.setValueid(finProductValuation001Dao.selectValueid());
			dto.setVldate(vldate);
			dto.setAssetType(dealInfo.getAssettype());
			dto.setFirstType(dealInfo.getFirsttype());
			dto.setAssetCode(dealInfo.getAssetcode());
			dto.setAssetName(dealInfo.getAssetname());
			dto.setInvtGoals(dealInfo.getTarget());
			dto.setAssetMdate(dealInfo.getMdate());
			dto.setCcy(dealInfo.getCcy());
			dto.setBasis(dealInfo.getBasis());
			dto.setChannel(dealInfo.getChannel());
			dto.setInvtYield(dealInfo.getYield());
			dto.setBondAcct(dealInfo.getBondacct());
			// 获取T-1日持仓
			BigDecimal ystposition = finProductValuation001Dao.selectBeforePosition(dealInfo.getPtlid(),
					dealInfo.getFinprodid(), start);
			ystposition = ystposition == null ? BigDecimal.ZERO : ystposition;
			BigDecimal tdprinamt = dealInfo.getPrinamt();
			dto.setFaceAmt(tdprinamt);
			BigDecimal tdintamt = BigDecimal.ZERO;
			if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(dealInfo.getAssettype())) {
				// 市价 和久期
				Map<String, BigDecimal> bondext = finProductValuation001Dao.selectMktprice(dealInfo.getAssetcode(),
						start, DictCodeList.估值来源_中债登_CBC.getValue());
				dto.setDuration(bondext == null ? BigDecimal.ZERO : bondext.get("DURATION"));
				if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(dealInfo.getTarget())) {
					// 获取T-1日估值信息
					Map<String, BigDecimal> preValue = finProductValuation001Dao.selectBeforeValue(dealInfo.getPtlid(),
							dealInfo.getFinprodid(), start);
					// 实际收益率
					BigDecimal actyield = preValue == null ? BigDecimal.ZERO : preValue.get("ACTYIELD");
					if (ystposition.compareTo(BigDecimal.ZERO) > 0) { // T-1日有持仓: T-1日估值 + T日计提利息收入
						// T-1估值
						BigDecimal netamt = preValue == null ? BigDecimal.ZERO : preValue.get("NETAMT");
						// 资产价值
						dto.setAssetValue(netamt
								.add(netamt.multiply(
										actyield.compareTo(BigDecimal.ZERO) == 0 ? dealInfo.getYield() : actyield))
								.add(tdprinamt));
						// 单位价值
						dto.setCprice(dto.getAssetValue().divide(tdprinamt, 4, RoundingMode.HALF_UP));
					} else { // T-1日无持仓: 交易金额汇总 + T日计提利息收入
						// 估值日 日计提利息
						tdintamt = ValuationUtils.calcIntamt(tdprinamt, actyield, dealInfo.getBasis(), vldate, vldate);
						dto.setAssetValue(tdprinamt.add(tdintamt)); // 资产价值
						dto.setCprice(dto.getAssetValue().divide(tdprinamt, 4, RoundingMode.HALF_UP)); // 单位价值
					}
				} else {
					BigDecimal mktprice = bondext == null ? BigDecimal.valueOf(100L) : bondext.get("PRICE");
					dto.setCprice(mktprice == null ? BigDecimal.ZERO : mktprice); // 单位价值
					dto.setAssetValue(
							mktprice.multiply(tdprinamt).divide(BigDecimal.valueOf(100L), 4, RoundingMode.HALF_UP)); // 资产价值
				}
			} else if (DictCodeListExt1.金融产品类型_基金.getValue().equals(dealInfo.getAssettype())
					&& DictCodeList.基金类资产类型_货币基金.getValue().equals(dealInfo.getFirsttype())
					|| (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(dealInfo.getAssetcode())
							&& DictCodeList.收益类型_货币型.getValue().equals(dealInfo.getProfittype()))) {
				// 获取万份收益
				BigDecimal cprice = finProductValuation001Dao.selectBeforePrice(
						dealInfo.getFinprodid().substring(
								dealInfo.getFinprodid().lastIndexOf("_", dealInfo.getFinprodid().length() - 1) + 1),
						start);
				cprice = cprice == null ? BigDecimal.ONE : cprice;
				if (ystposition.compareTo(BigDecimal.ZERO) > 0) {
					// 获取T-1日估值
					Map<String, BigDecimal> preValue = finProductValuation001Dao.selectBeforeValue(dealInfo.getPtlid(),
							dealInfo.getFinprodid(), start);
					// 当日计提
					tdintamt = tdprinamt.multiply(cprice).divide(BigDecimal.valueOf(10000L), 4, RoundingMode.HALF_UP);
					dto.setAssetValue(tdintamt.add(preValue == null ? BigDecimal.ZERO : preValue.get("T-1VALUE")));
				} else {
					tdintamt = tdprinamt.multiply(cprice).divide(BigDecimal.valueOf(10000L), 4, RoundingMode.HALF_UP);
					dto.setAssetValue(tdprinamt.add(tdintamt));
				}
				dto.setCprice(dto.getAssetValue().divide(tdprinamt, 4, RoundingMode.HALF_UP));
			} else if ((DictCodeListExt1.金融产品类型_基金.getValue().equals(dealInfo.getAssettype())
					&& DictCodeList.基金类资产类型_非货币基金.getValue().equals(dealInfo.getFirsttype()))
					|| (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(dealInfo.getAssettype())
							&& DictCodeList.收益类型_净值型.getValue().equals(dealInfo.getProfittype()))) {
				// 要从核算取单位价值
				BigDecimal cprice = finProductValuation001Dao.selectBeforePrice(dealInfo.getFinprodid(), start);
				cprice = cprice == null ? BigDecimal.ONE : cprice;
				dto.setCprice(cprice); // 单位价值
				dto.setAssetValue(dto.getCprice().multiply(dealInfo.getPrinamt().setScale(2, RoundingMode.HALF_UP))); // 资产价值
			} else {
				if (ystposition.compareTo(BigDecimal.ZERO) > 0) { // T-1日有持仓: T-1日估值+T日计提利息收入
					// 获取T-1日估值
					Map<String, BigDecimal> preValue = finProductValuation001Dao.selectBeforeValue(dealInfo.getPtlid(),
							dealInfo.getFinprodid(), start);
					// 估值日 日计提利息
					tdintamt = ValuationUtils.calcIntamt(tdprinamt, dealInfo.getYield(), dealInfo.getBasis(), start,
							vldate);
					dto.setAssetValue(tdintamt.add(preValue == null ? BigDecimal.ZERO : preValue.get("T_1VALUE"))); // 资产价值
					dto.setCprice(dto.getAssetValue().divide(tdprinamt, 4, RoundingMode.HALF_UP)); // 单位价值
				} else { // T-1日无持仓: 交易金额汇总 + T日计提利息收入
					// 估值日 日计提利息
					tdintamt = ValuationUtils.calcIntamt(tdprinamt, dealInfo.getYield(), dealInfo.getBasis(), vldate,
							vldate);
					dto.setAssetValue(tdprinamt.add(tdintamt)); // 资产价值
					dto.setCprice(dto.getAssetValue().divide(tdprinamt, 4, RoundingMode.HALF_UP)); // 单位价值
				}
			}
			// 负债
			if (DictCodeListExt1.金融产品类型_投管用_拆入.getValue().equals(dealInfo.getAssettype())
					|| DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(dealInfo.getAssettype())
					|| DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(dealInfo.getAssettype())) {
				dto.setVltype("DEBTS");
				dto.setAssetValue(dto.getAssetValue().negate());
			} else {
				dto.setVltype("ASSET");
			}
			if (dto.getAssetValue().abs().compareTo(BigDecimal.ZERO) > 0) {
				assetInfoList.get().add(dto);
			}
		});

		// 查询托管行活期利息
		Map<String, BigDecimal> hqintamt = finProductValuation001Dao.selectBeforeValue(prodInfo.getPtlid(),
				prodInfo.getAcctno(), start);
		if (hqintamt != null && hqintamt.get("INTAMT").compareTo(BigDecimal.ZERO) > 0) {
			FinProductValuationDetail001Dto hqdto = new FinProductValuationDetail001Dto();
			hqdto.setValueid(finProductValuation001Dao.selectValueid());
			hqdto.setAssetCode(prodInfo.getProdcode());
			hqdto.setAssetName(prodInfo.getProdname());
			hqdto.setAssetMdate(prodInfo.getMdate());
			hqdto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
			hqdto.setAssetValue(hqintamt.get("INTAMT"));
			hqdto.setAssetType("HQZH");
			hqdto.setVltype("ASSET");
			assetInfoList.get().add(hqdto);
		}

		// 查询剩余现金
		PtlCashPositionFlowDto cashDto = new PtlCashPositionFlowDto();
		BasePtlCashPositionInfoService cashsrv = (BasePtlCashPositionInfoService) SpringContextUtil
				.getBean(BasePtlCashPositionInfoService.class);
		cashDto.setPortfolioId(prodInfo.getPtlid());
		cashDto.setCdate(vldate);
		cashDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
		cashDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
		BigDecimal holdCash = cashsrv.queryOtherCashPosition(cashDto);
		if (holdCash != null && holdCash.compareTo(BigDecimal.ZERO) > 0) {
			FinProductValuationDetail001Dto holdDto = new FinProductValuationDetail001Dto();
			holdDto.setValueid(finProductValuation001Dao.selectValueid());
			holdDto.setAssetCode(prodInfo.getProdcode());
			holdDto.setAssetName(prodInfo.getProdname());
			holdDto.setAssetMdate(prodInfo.getMdate());
			holdDto.setCcy(cashDto.getCcy());
			holdDto.setAssetValue(holdCash);
			holdDto.setAssetType("HOLDCASH");
			holdDto.setVltype("ASSET");
			assetInfoList.get().add(holdDto);
		}

		if (assetInfoList.get().size() > 0) {
			return assetInfoList.get().stream().map(FinProductValuationDetail001Dto::getAssetValue)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 计算产品费用
	 * 
	 * @param prodInfo
	 * @param start
	 * @param vldate
	 * @return
	 */
	private BigDecimal calcFeeValue(ProdDealInfoDto prodInfo, Date start, Date vldate) {
		// 查询产品费用信息
		List<ProdFeeInfoDto> feeList = finProductValuation001Dao.selectFeeInfo(prodInfo, vldate);
		feeList.forEach(fee -> {
			BigDecimal unpaidfee = finProductValuation001Dao.selectFeePosition(prodInfo.getProdcode(), fee.getFeetype(),
					start);
			unpaidfee = unpaidfee == null ? BigDecimal.ZERO : unpaidfee;
			FinProductValuationDetail001Dto dto = new FinProductValuationDetail001Dto();
			dto.setValueid(finProductValuation001Dao.selectValueid());
			dto.setVltype("FEE");
			BigDecimal tdfeeamt = BigDecimal.ZERO;
			// 1.存续本金型
			if (DictCodeList.计费基数_费用品种_存续本金.getValue().equals(fee.getBasicbill())
					&& prodInfo.getPrinamt().compareTo(BigDecimal.ZERO) > 0) {
				tdfeeamt = ValuationUtils.calcIntamt(prodInfo.getPrinamt(), fee.getYield(), fee.getBasis(), start,
						vldate);
				dto.setFeename(fee.getFeename());
				dto.setBasis(fee.getBasis());
			} else { // 2.资产规模型
				BigDecimal assetamt = finProductValuation001Dao.selectAssetDealInfo(prodInfo, vldate).stream()
						.map(AssetDealInfoDto::getPrinamt).reduce(BigDecimal.ZERO, BigDecimal::add);
				if (assetamt.compareTo(BigDecimal.ZERO) > 0) {
					tdfeeamt = ValuationUtils.calcIntamt(assetamt, fee.getYield(), fee.getBasis(), start, vldate);
					dto.setFeename(fee.getFeename());
					dto.setBasis(fee.getBasis());
				}
			}
			if (tdfeeamt.compareTo(BigDecimal.ZERO) > 0) {
				dto.setFeeamt(unpaidfee.add(tdfeeamt));
				feeInfoList.get().add(dto);
			}
		});

		if (feeInfoList.get().size() > 0) {
			return feeInfoList.get().stream().map(FinProductValuationDetail001Dto::getFeeamt).reduce(BigDecimal.ZERO,
					BigDecimal::add);
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 插入估值信息
	 * 
	 * @param dto
	 * @return
	 */
	private Long insertValue(FinProductValuation001Dto dto) {
		finProductValuation001Dao.insertValue(dto);
		return dto.getValueid();
	}

	/**
	 * 插入估值明细
	 * 
	 * @param assetInfoList
	 * @param feeInfoList
	 * @param pvalueid
	 */
	private void insertAssetInfoDetail(List<FinProductValuationDetail001Dto> assetInfoList,
			List<FinProductValuationDetail001Dto> feeInfoList, Long pvalueid) {
		if (!StringUtil.isEmptyList(assetInfoList)) {
			finProductValuation001Dao.insertAssetInfoDetail(assetInfoList, pvalueid);
		}
		if (!StringUtil.isEmptyList(feeInfoList)) {
			finProductValuation001Dao.insertFeeInfoDetail(feeInfoList, pvalueid);
		}
	}

}
