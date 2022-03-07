/*
 * 文件名：ProfitDistributionBookProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年6月5日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.profitandlossaccounting.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.BokBussDetsub;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dao.ProfitAndLossAccountingMapper;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingNeedOperateEntity;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingResult;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingSubject;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 利润分配批处理 - Service
 * 
 * @author chengzhong
 * @version 1.0 2018年6月5日
 * @see ProfitAndLossAccountingStep4ProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProfitAndLossAccountingStep4ProcessorService {

	@Autowired
	private ProfitAndLossAccountingMapper mapper;

	@Autowired
	private AccountingCommonService accountingCommonService;

	@Autowired
	BookAccountGenLogic bookAccountGenLogic;

	@Autowired
	private DBUtil dBUtil;

	/**
	 * process 数据处理
	 * 
	 * @return BookGenerateResult 入账结果
	 * @throws Exception
	 *             异常
	 * @see
	 * @since 1.0
	 */
	public ProfitAndLossAccountingResult execute(Map<String, Object> param) {
		// 获取reader返回值
		ProfitAndLossAccountingNeedOperateEntity accEntity = (ProfitAndLossAccountingNeedOperateEntity) param
				.get(GlobalConstant.READER_RESULT);

		// 若reader传递的参数为空，不进行后续处理
		if (accEntity == null) {
			return null;
		}
		// 1.数据准备
		// 定义 List<批处理用账务明细> 并初始化
		ArrayList<ValBokBookdetailBatch> list = new ArrayList<ValBokBookdetailBatch>();
		// 变量.账套代码 = reader处理结果.账套代码
		String booksetId = accEntity.getBooksetId();
		// 1-1.查询账套下未分配利润/未分配利润科目余额
		// 1-1.1 查询账套下未分配利润科目余额
		// 设值科目余额查询参数Dto
		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		// 账套代码 = 损益结转数据.账套代码
		searchDto.setBooksetId(accEntity.getBooksetId());
		// 发生日 = 损益结转数据.发生日
		searchDto.setHappenDate(accEntity.getHappenDate());
		// 入账日 = 损益结转数据.入账日
		searchDto.setBookDate(accEntity.getBookDate());
		ArrayList<String> 科目性质列表 = new ArrayList<>();
		科目性质列表.add(DictCodeBaseSubjectNatrue.未分配利润_已实现.getValue());
		科目性质列表.add(DictCodeBaseSubjectNatrue.未分配利润_未实现.getValue());
		科目性质列表.add(DictCodeBaseSubjectNatrue.待分配利润.getValue());
		searchDto.setBaseSubjectNatureList(科目性质列表);
		// 参照共通【科目余额查询】方法，参照【详细设计-账务类共通方法设计.xlsx的【科目余额查询】sheet】
		List<SubjectBalance> 未分配利润最新余额集合 = accountingCommonService.getSubjectLatestBalance(searchDto);

		/*
		 * Map<String, BigDecimal> 实收资本科目余额集合 = 所有者权益类科目最新余额列表.stream().filter( item -> DictCodeBaseSubjectNatrue.实收资本.getValue().equals(
		 * item.getBaseSubjectNature())).collect( Collectors.toMap(SubjectBalance::getBokDetailId, SubjectBalance::getBalance));
		 */
		// 以账务明细代码+币种+基础科目性质 分组
		未分配利润最新余额集合 = 未分配利润最新余额集合.stream().filter(item -> !(ArithmeticUtil.equals(BigDecimal.ZERO, item.getBalance())
				&& ArithmeticUtil.equals(BigDecimal.ZERO, item.getbAmt()))).collect(
				Collectors.toList());

		// 以账务明细代码+币种,Map<基础科目性质,科目余额信息>
		Map<String, Map<String, SubjectBalance>> 未分配利润最新余额集合Map = new HashMap<>();
		// 已Map<基础科目性质,科目余额信息>生成基础科目性质信息

		// 获取当前账务明细代码信息
		Set<String> bokDetails = 未分配利润最新余额集合.stream().map(SubjectBalance::getBokDetailId).collect(Collectors.toSet());
		// 币种集合
		Set<String> ccySets = 未分配利润最新余额集合.stream().map(SubjectBalance::getOCcy).collect(Collectors.toSet());
		// 科目基础性质集合
		Set<String> baseSubjectNatures = 未分配利润最新余额集合.stream().map(SubjectBalance::getBaseSubjectNature).collect(
				Collectors.toSet());
		// 先循环币种信息
		for (String ccy : ccySets) {
			for (String bokDetail : bokDetails) {

				SubjectBalance balance = new SubjectBalance();
				Map<String, SubjectBalance> 基础科目性质信息map = new HashMap<String, SubjectBalance>();
				List<SubjectBalance> 基础科目性质信息List = new ArrayList<SubjectBalance>();
				List<SubjectBalance> bokdetailList = 未分配利润最新余额集合.stream().filter(
						item -> StringUtils.equals(item.getBokDetailId(), bokDetail)).collect(Collectors.toList());
				for (String baseSubjectNature : baseSubjectNatures) {
					基础科目性质信息List = bokdetailList.stream().filter(
							item -> StringUtils.equals(item.getBaseSubjectNature(), baseSubjectNature)).collect(Collectors.toList());
					if (基础科目性质信息List.size() > 0) {
						balance = 基础科目性质信息List.get(0);
						基础科目性质信息map.put(baseSubjectNature, balance);
					}
				}
				未分配利润最新余额集合Map.put(bokDetail + ccy, 基础科目性质信息map);
			}

		}
		// 1-1.2 待分配利润余额查询
		科目性质列表.clear();
		科目性质列表.add(DictCodeBaseSubjectNatrue.待分配利润.getValue());
		searchDto.setBaseSubjectNatureList(科目性质列表);
		// 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
		Map<String, List<SubjectBalance>> 待分配利润余额集合 = accountingCommonService.getSubjectLatestBalance(searchDto).stream().collect(
				Collectors.groupingBy(item -> item.getBokDetailId() + item.getOCcy()));

		// 1-1.2.损益结转科目查询
		ArrayList<String> 科目类型列表 = new ArrayList<>();
		科目类型列表.add(DictCodeList.科目类型_损益类.getValue());
		科目类型列表.add(DictCodeList.科目类型_所有者权益类.getValue());
		科目性质列表.clear();
		科目性质列表.add(DictCodeBaseSubjectNatrue.待分配利润.getValue());
		Map<String, List<ProfitAndLossAccountingSubject>> 损益结转科目集合 = mapper.selectPalSubject(booksetId, 科目类型列表,
				科目性质列表).stream().collect(Collectors.groupingBy(ProfitAndLossAccountingSubject::getBaseSubjectNature));
		// 2 未分配利润结转处理
		// 2.1 生成未分配利润科目的结转数据
		// 2.1.1循环 1-1.查询结果中未分配利润科目余额,循环对象 未分配利润entity
		ValBokBookdetailBatch detail = null;
		ValBokBookdetailBatch detail2 = null;
		ValBokBookdetailBatch detail3 = null;
		BokBussDetsub detsub = new BokBussDetsub();
		SubjectBalance data = null;
		String detailSubjectNo = "";
		for (String key : 未分配利润最新余额集合Map.keySet()) {
			// 定义 结转金额 = 0
			BigDecimal carryingAmt = BigDecimal.ZERO;
			//update by xr for tishkbs640 货币型产品结转原币余额为0 start
			BigDecimal carryingAmt_yb = BigDecimal.ZERO;
			//update by xr for tishkbs640 货币型产品结转原币余额为0 end
			// 未分配利润_已实现
			SubjectBalance balnce_ysx = 未分配利润最新余额集合Map.get(key).get(DictCodeBaseSubjectNatrue.未分配利润_已实现.getValue());
			if (balnce_ysx != null) {
				detail = createValBokBookdetailBatch(accEntity, DictCodeListTableId.会计场景代码_产品_未分配利润结转.getValue());
				// 账务明细.借贷方向 = 未分配利润_已实现.发生日余额方向取反
				if (DictCodeList.余额方向_借方.getValue().equals(balnce_ysx.getBalFlag())) {
					detail.setCdFlag(DictCodeList.借贷方向_贷.getValue());
					// 结转金额 = 结转金额 + 账务明细.借贷方向="借方"?账务明细.本币发生额 : -1*账务明细.本币发生额
					carryingAmt = ArithmeticUtil.doAdd(carryingAmt,
							ArithmeticUtil.multiply(balnce_ysx.getbAmt(), new BigDecimal(-1)));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 start
					carryingAmt_yb = ArithmeticUtil.doAdd(carryingAmt_yb,
							ArithmeticUtil.multiply(balnce_ysx.getBalance(), new BigDecimal(-1)));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 end

				}
				else {
					// 结转金额 = 结转金额 + 账务明细.借贷方向="借方"?账务明细.本币发生额 : -1*账务明细.本币发生额
					carryingAmt = ArithmeticUtil.doAdd(carryingAmt, balnce_ysx.getbAmt());
					//update by xr for tishkbs640 货币型产品结转原币余额为0 start
					carryingAmt_yb = ArithmeticUtil.doAdd(carryingAmt_yb, balnce_ysx.getBalance());
					//update by xr for tishkbs640 货币型产品结转原币余额为0 end
					detail.setCdFlag(DictCodeList.借贷方向_借.getValue());
				}
				if (!ArithmeticUtil.equals(balnce_ysx.getbAmt(), BigDecimal.ZERO)) {
					// 账务明细.原币 = 未分配利润entity.原币
					detail.setoCcy(balnce_ysx.getOCcy());
					// 账务明细.原币发生额 = 未分配利润entity.发生日科目余额
					detail.setoHappenAmt(balnce_ysx.getBalance());
					/*增加本币处理逻辑   add by chufengjie 20190723 start*/
					// 账务明细.本币 = 未分配利润entity.本币
					detail.setbCcy(balnce_ysx.getbCcy());
					// 账务明细.本币发生额 = 未分配利润entity.本币发生日科目余额
					detail.setbHappenAmt(balnce_ysx.getbAmt());
					/*增加本币处理逻辑   add by chufengjie 20190723 end*/
					// 账务明细.发生数量 = 未分配利润entity.数量
					detail.setHappenNumber(balnce_ysx.getNum());

					// 其他字段设值同未分配利润entity
					// 业务明细代码
					detail.setBusiId(balnce_ysx.getBusiId());
					// 基础科目性质
					detail.setBaseSubjectNature(balnce_ysx.getBaseSubjectNature());
					// 账务明细代码
					detail.setBokDetailId(balnce_ysx.getBokDetailId());
					// 科目号
					detail.setSubjectNo(balnce_ysx.getSubjectNo());
					// 四级科目号
					// 四级科目号
					detailSubjectNo = balnce_ysx.getDetailSubjectNo();
					// 调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
					if (!StringUtils.isNotEmpty(detailSubjectNo)) {
						detsub = bookAccountGenLogic.genBokBussDetsub(detail);
						if (detsub != null) {
							detail.setDetailSubjectNo(detsub.getDetailSubjectNo());
						}
					}
					else {
						detail.setDetailSubjectNo(detailSubjectNo);
					}
					// 调用共通处理 余额方向处理_发生额加余额
					calBalance(balnce_ysx.getBalFlag(), balnce_ysx, detail);
					//对detail进行判断，如果本币金额大于0，不进行任何变化。如果小于0，则原币，本币金额*（-1），借贷方向相反
					if(detail.getbHappenAmt().compareTo(BigDecimal.ZERO)<0){
						if(detail.getCdFlag().equals(DictCodeList.余额方向_借方.getValue())){
							detail.setCdFlag(DictCodeList.余额方向_贷方.getValue());
						}else{
							detail.setCdFlag(DictCodeList.余额方向_借方.getValue());
						}
						detail.setbHappenAmt(ArithmeticUtil.multiply(detail.getbHappenAmt(),new BigDecimal(-1)));
						detail.setoHappenAmt(ArithmeticUtil.multiply(detail.getoHappenAmt(),new BigDecimal(-1)));
					}
					list.add(detail);
				}

			}
			// 未分配利润_未实现
			SubjectBalance balnce_wsx = 未分配利润最新余额集合Map.get(key).get(DictCodeBaseSubjectNatrue.未分配利润_未实现.getValue());
			if (balnce_wsx != null) {
				detail3 = createValBokBookdetailBatch(accEntity, DictCodeListTableId.会计场景代码_产品_未分配利润结转.getValue());
				// 账务明细.借贷方向 = 未分配利润_已实现.发生日余额方向取反
				if (DictCodeList.余额方向_借方.getValue().equals(balnce_wsx.getBalFlag())) {
					detail3.setCdFlag(DictCodeList.借贷方向_贷.getValue());
					// 结转金额 = 结转金额 + 账务明细.借贷方向="借方"?账务明细.本币发生额 : -1*账务明细.本币发生额
					carryingAmt = ArithmeticUtil.doAdd(carryingAmt,
							ArithmeticUtil.multiply(balnce_wsx.getbAmt(), new BigDecimal(-1)));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 start
					carryingAmt_yb = ArithmeticUtil.doAdd(carryingAmt_yb,
							ArithmeticUtil.multiply(balnce_wsx.getBalance(), new BigDecimal(-1)));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 end
				}
				else {
					// 结转金额 = 结转金额 + 账务明细.借贷方向="借方"?账务明细.本币发生额 : -1*账务明细.本币发生额
					carryingAmt = ArithmeticUtil.doAdd(carryingAmt, balnce_wsx.getbAmt());
					//update by xr for tishkbs640 货币型产品结转原币余额为0 start
					carryingAmt_yb = ArithmeticUtil.doAdd(carryingAmt_yb,balnce_wsx.getBalance());
					//update by xr for tishkbs640 货币型产品结转原币余额为0 end
					detail3.setCdFlag(DictCodeList.借贷方向_借.getValue());
				}
				if (!ArithmeticUtil.equals(balnce_wsx.getbAmt(), BigDecimal.ZERO)) {
					// 账务明细.原币 = 未分配利润entity.原币
					detail3.setoCcy(balnce_wsx.getOCcy());
					// 账务明细.原币发生额 = 未分配利润entity.发生日科目余额
					detail3.setoHappenAmt(balnce_wsx.getBalance());
					/*增加本币处理逻辑   add by chufengjie 20190723 start*/
					// 账务明细.本币 = 未分配利润entity.本币
					detail3.setbCcy(balnce_wsx.getbCcy());
					// 账务明细.本币发生额 = 未分配利润entity.本币发生日科目余额
					detail3.setbHappenAmt(balnce_wsx.getbAmt());
					/*增加本币处理逻辑   add by chufengjie 20190723 end*/
					// 账务明细.发生数量 = 未分配利润entity.数量
					detail3.setHappenNumber(balnce_wsx.getNum());

					// 其他字段设值同未分配利润entity
					// 业务明细代码
					detail3.setBusiId(balnce_wsx.getBusiId());
					// 基础科目性质
					detail3.setBaseSubjectNature(balnce_wsx.getBaseSubjectNature());
					// 账务明细代码
					detail3.setBokDetailId(balnce_wsx.getBokDetailId());
					// 科目号
					detail3.setSubjectNo(balnce_wsx.getSubjectNo());
					// 四级科目号
					detailSubjectNo = balnce_wsx.getDetailSubjectNo();
					// 调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
					if (!StringUtils.isNotEmpty(detailSubjectNo)) {
						detsub = bookAccountGenLogic.genBokBussDetsub(detail3);
						if (detsub != null) {
							detail3.setDetailSubjectNo(detsub.getDetailSubjectNo());
						}
					}
					else {
						detail3.setDetailSubjectNo(detailSubjectNo);
					}
					// 调用共通处理 余额方向处理_发生额加余额
					calBalance(balnce_wsx.getBalFlag(), balnce_wsx, detail3);
					//对detail3进行判断，如果本币金额大于0，不进行任何变化。如果小于0，则原币，本币金额*（-1），借贷方向相反
					if(detail3.getbHappenAmt().compareTo(BigDecimal.ZERO)<0){
						if(detail3.getCdFlag().equals(DictCodeList.余额方向_借方.getValue())){
							detail3.setCdFlag(DictCodeList.余额方向_贷方.getValue());
						}else{
							detail3.setCdFlag(DictCodeList.余额方向_借方.getValue());
						}
						detail3.setbHappenAmt(ArithmeticUtil.multiply(detail3.getbHappenAmt(),new BigDecimal(-1)));
						detail3.setoHappenAmt(ArithmeticUtil.multiply(detail3.getoHappenAmt(),new BigDecimal(-1)));
					}
					list.add(detail3);
				}

			}
			// II.根据未分配利润科目余额信息,生成待分配利润的账务明细数据
			if (balnce_ysx != null || balnce_wsx != null) {
				if (!ArithmeticUtil.equals(carryingAmt, BigDecimal.ZERO)) {
					detail2 = createValBokBookdetailBatch(accEntity, DictCodeListTableId.会计场景代码_产品_未分配利润结转.getValue());
					// 账务明细_2.科目号 = 未分配利润_已实现
					ProfitAndLossAccountingSubject subject = 损益结转科目集合.get(DictCodeBaseSubjectNatrue.待分配利润.getValue()).get(0);
					detail2.setSubjectNo(subject.getSubjectNo());
					// 账务明细_2.基础科目性质 = 未分配利润-已实现
					detail2.setBaseSubjectNature(DictCodeBaseSubjectNatrue.待分配利润.getValue());
					// 账务明细.借贷方向 = 未分配利润entity.发生日余额方向
					if (carryingAmt.compareTo((BigDecimal.ZERO)) > 0) {
						detail2.setCdFlag(DictCodeList.借贷方向_贷.getValue());
					}
					else {
						detail2.setCdFlag(DictCodeList.借贷方向_借.getValue());
					}

					// 账务明细.原币发生额
					detail2.setoHappenAmt(carryingAmt_yb.abs());
					/*增加本币处理逻辑   add by chufengjie 20190723 start*/
					//账务明细.本币发生额 = 结转金额.abs()
					detail2.setbHappenAmt(carryingAmt.abs());
					/*增加本币处理逻辑   add by chufengjie 20190723 end*/
					if (balnce_ysx != null) {
						// 账务明细.原币 = 未分配利润entity.原币
						detail2.setoCcy(balnce_ysx.getOCcy());
						/*增加本币处理逻辑   add by chufengjie 20190723 start*/
						// 账务明细.本币 = 未分配利润entity.本币
						detail2.setbCcy(balnce_ysx.getbCcy());
						/*增加本币处理逻辑   add by chufengjie 20190723 end*/
						// 账务明细.发生数量 = 未分配利润entity.数量
						detail2.setHappenNumber(balnce_ysx.getNum());
						// 其他字段设值同未分配利润entity
						// 业务明细代码
						detail2.setBusiId(balnce_ysx.getBusiId());
						// 账务明细代码
						detail2.setBokDetailId(balnce_ysx.getBokDetailId());
					}
					else {
						// 账务明细.原币 = 未分配利润entity.原币
						detail2.setoCcy(balnce_wsx.getOCcy());
						/*增加本币处理逻辑   add by chufengjie 20190723 start*/
						// 账务明细.本币 = 未分配利润entity.本币
						detail2.setbCcy(balnce_wsx.getbCcy());
						/*增加本币处理逻辑   add by chufengjie 20190723 end*/
						// 账务明细.发生数量 = 未分配利润entity.数量
						detail2.setHappenNumber(balnce_wsx.getNum());
						// 其他字段设值同未分配利润entity
						// 业务明细代码
						detail2.setBusiId(balnce_wsx.getBusiId());
						// 账务明细代码
						detail2.setBokDetailId(balnce_wsx.getBokDetailId());
					}
					// 四级科目号
					detailSubjectNo = detail2.getDetailSubjectNo();
					// 调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
					if (!StringUtils.isNotEmpty(detailSubjectNo)) {
						detsub = bookAccountGenLogic.genBokBussDetsub(detail2);
						if (detsub != null) {
							detail2.setDetailSubjectNo(detsub.getDetailSubjectNo());
						}
					}
					else {
						detail2.setDetailSubjectNo(detailSubjectNo);
					}
					// 其他字段设值同 币种余额entity
					// if 未分配利润entity.基础科目性质 = 未分配利润_已实现
					/**
					 * add by wujie on 20190701 start
					 */
					data = null;
					/**add by wujie on 20190701 end**/
					if (!待分配利润余额集合.isEmpty()) {
						if (待分配利润余额集合.get(key) != null) {
							data = 待分配利润余额集合.get(key).get(0);
						}
					}
					if (data == null) {
						data = new SubjectBalance();
						data.setBalance(BigDecimal.ZERO);
						data.setBalFlagHappenDate(DictCodeList.余额方向_贷方.getValue());
						data.setNum(BigDecimal.ZERO);
						data.setBalFlag(DictCodeList.余额方向_贷方.getValue());
					}
					// 调用共通处理 余额方向处理_发生额加余额
					calBalance(data.getBalFlag(), data, detail2);
					list.add(detail2);
				}

			}
		}

		list.forEach(x -> {
			// 流水号
			x.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
			// 处理序列 <- 自动生成的序列号20位
			x.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
//            // 本位币发生额
//            x.setbHappenAmt(x.getoHappenAmt());
//            // 本位币余额（发生日）
//            x.setbAmtHappenDate(x.getoAmtHappenDate());
			// 冲销标识
			x.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
			// 凭证说明
			x.setBookRemark(SysDataUtil.getNameFromDataDict(DictCode.会计场景代码, x.getTableId()));
			// 凭证编号
			// 计提类：场景代码 + 发生日 + 账务明细代码 + 冲销标识
			String bookId = x.getBooksetId() + x.getTableId() + x.getHappenDate() + BookConstant.DEF_BOK_DETAIL_ID
					+ x.getOffsetFlag();
			bookId = EncryptUtil.getInstance().md5(bookId);
			x.setBookId(bookId);
		});
		ProfitAndLossAccountingResult result = new ProfitAndLossAccountingResult(list);
		result.setProdId(accEntity.getBooksetId());
		return result;
	}

	/**
	 * <一句话功能简述> <功能详细描述>
	 *
	 * @param data
	 * @param detail
	 * @see
	 * @since 1.0
	 */
	private void calBalance(String balFlag, SubjectBalance data, ValBokBookdetailBatch detail) {
		balFlag = balFlag != null ? balFlag : DictCodeList.余额方向_借贷双方.getValue();
		if (data.getBalFlagHappenDate() == null) {
			data.setBalFlagHappenDate(balFlag);
		}
		Map<String, Object> mapHappenDate = accountingCommonService.balanceDirectDealAccrualAddBalance(balFlag,
				data.getBalFlagHappenDate(), data.getBalance(), data.getNum(), detail.getCdFlag(), detail.getoHappenAmt(),
				detail.getHappenNumber(),
				/**MODIFY BY WUJI ON 20190724 START
				 * null,null**/
				data.getbAmt(),detail.getbHappenAmt()
				/**MODIFY BY WUJIE ON 2010724 END**/
		);
		// 原币余额
		detail.setoAmtHappenDate(ArithmeticUtil.round((BigDecimal)mapHappenDate.get("balance"), NumberEnum.MONEY));
		/*增加本币处理逻辑   add by chufengjie 20190723 start*/
		// 本币余额
		detail.setbAmtHappenDate(ArithmeticUtil.round((BigDecimal)mapHappenDate.get("benbalance"), NumberEnum.MONEY));
		/*增加本币处理逻辑   add by chufengjie 20190723 end*/
		// 数量
		detail.setNumAmtHappenDate(ArithmeticUtil.round((BigDecimal)mapHappenDate.get("num"), NumberEnum.MONEY));
		// 余额方向
		detail.setBalFlagHappenDate((String)mapHappenDate.get("balDirect"));
	}

	private ValBokBookdetailBatch createValBokBookdetailBatch(ProfitAndLossAccountingNeedOperateEntity entity, String tableId) {
		ValBokBookdetailBatch detail = new ValBokBookdetailBatch();
		// 场景代码
		detail.setTableId(tableId);
		// 凭证类型
		detail.setBookType(DictCodeList.凭证类型_日终.getValue());
		// 账套代码
		detail.setBooksetId(entity.getBooksetId());
		// 发生日
		detail.setHappenDate(entity.getHappenDate());
		// 入账日
		detail.setBookDate(entity.getBookDate());
		// 账套日期
		detail.setBooksetDate(entity.getBooksetDate());
		// 任务代码
		detail.setTaskId(entity.getTaskId());
		// 任务优先级
		detail.setTaskPri(entity.getTaskPri());
		// 原币
		detail.setbCcy(entity.getStandardCcy());
		// 分支号
		detail.setBranch(BookConstant.DEFALUT_BRANCH);
		return detail;
	}

}
