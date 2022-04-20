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
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.dao.BokDetailIdMapper;
import com.joyintech.tams.common.base.entity.BokBussDetsub;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokDetailIdKey;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dao.ProfitAndLossAccountingNewMapper;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.*;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 利润分配批处理 - Service
 * 
 * @author chengzhong
 * @version 1.0 2018年6月5日
 * @see ProfitAndLossAccountingStep1ProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProfitAndLossAccountingStep1ProcessorNewService {

	@Autowired
	private ProfitAndLossAccountingNewMapper mapper;

	@Autowired
	private AccountingCommonService accountingCommonService;
	@Autowired
	BookAccountGenLogic bookAccountGenLogic;
	@Autowired
	private BokDetailIdMapper bokDetailIdMapper;
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
		// 定义 List<批处理用账务明细> 并初始化
		ArrayList<ValBokBookdetailBatch> list = new ArrayList<ValBokBookdetailBatch>();
		Map<String, ValBokBookdetailBatch> listMap = new HashMap<>();
		// 变量.账套代码 = reader处理结果.账套代码
		String booksetId = accEntity.getBooksetId();
		// 全局变量.业务明细代码= 损益结转账务处理结果.核算主体代码
		String busiId = accEntity.getBusiId();
		// 全局变量.账务明细代码 = 损益结转账务处理结果.账务明细代码
		String bokDetailId = accEntity.getBokDetailId();
		// 1.1 查询账套下分层结构
		// 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->查询分层结构子结构
		Map<String, Object> params = new HashMap<>();
		params.put("booksetId", booksetId);
		List<StayDrawSubprdEntity> listStayDrawSubprdEntity = accountingCommonService.querySubBokAcctInfo(
				params);
		// 定义 变量.子分层信息.账务明细代码集合
		List<String> listBokDetailId = new ArrayList<>();
		// 定义 Map<账务明细代码,账务明细代码(表信息)> 子账务明细代码 = 通过子分层信息.账务明细代码查询账务明细代码表信息.
		Map<String, BokDetailId> bokDetailIdMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(listStayDrawSubprdEntity)) {
			listStayDrawSubprdEntity.forEach(item -> {
				BokDetailIdKey key = new BokDetailIdKey();
				key.setBokDetailId(item.getBokDetailId());
				key.setBooksetId(booksetId);
				bokDetailIdMap.put(item.getBokDetailId(),bokDetailIdMapper.selectByPrimaryKey(key));
				listBokDetailId.add(item.getBokDetailId());
			});
		}
		// 1.2 查询账套下损益科目余额
		// 设值科目余额查询参数Dto
		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		// 账套代码 = 损益结转数据.账套代码
		searchDto.setBooksetId(accEntity.getBooksetId());
		// 发生日 = 损益结转数据.发生日
		searchDto.setHappenDate(accEntity.getHappenDate());
		// 入账日 = 损益结转数据.入账日
		searchDto.setBookDate(accEntity.getBookDate());
		ArrayList<String> 科目类型列表 = new ArrayList<>();
		// 科目类型=“损益类
		科目类型列表.add(DictCodeList.科目类型_损益类.getValue());
		searchDto.setSubjectTypeList(科目类型列表);
		// 参照共通【科目余额查询】方法，参照【详细设计-账务类共通方法设计.xlsx的【科目余额查询】sheet】
		List<SubjectBalance> 损益类科目最新余额集合 = accountingCommonService.getSubjectLatestBalance(
				searchDto);
		// *结果集中原币余额为0的不做后续处理,结果集中删除
		损益类科目最新余额集合=损益类科目最新余额集合.stream().filter(
				item -> !(ArithmeticUtil.equals(BigDecimal.ZERO, item.getBalance())
						&&ArithmeticUtil.equals(BigDecimal.ZERO, item.getbAmt()))).collect(
				Collectors.toList());

		科目类型列表.clear();
		科目类型列表.add(DictCodeList.科目类型_损益类.getValue());
		科目类型列表.add(DictCodeList.科目类型_所有者权益类.getValue());
		ArrayList<String> 科目性质列表 = new ArrayList<>();
		科目性质列表.clear();
		科目性质列表.add(DictCodeBaseSubjectNatrueNew.本期利润_已实现.getValue());
		科目性质列表.add(DictCodeBaseSubjectNatrueNew.本期利润_未实现.getValue());
		科目性质列表.add(DictCodeBaseSubjectNatrueNew.利润分配_未分配利润_已实现.getValue());
		科目性质列表.add(DictCodeBaseSubjectNatrueNew.利润分配_未分配利润_未实现.getValue());
//        科目性质列表.add(DictCodeBaseSubjectNatrueNew.待分配利润.getValue());
		// 1.2.损益结转科目查询
		Map<String, List<ProfitAndLossAccountingSubject>> 损益结转科目集合 = mapper.selectPalSubject(
				booksetId, 科目类型列表, 科目性质列表).stream().collect(
				Collectors.groupingBy(ProfitAndLossAccountingSubject::getBaseSubjectNature));

		// 1.3 公允价值变动损益的基础科目性质查询
		List<String> 公允价值变动损益基础科目性质列表 = accountingCommonService.getValSubjectNatureList(
				DictCodeListExt1.分组代码_公允价值变动损益对应查询科目.getValue());

		// 1.4 本期利润_已实现余额查询
		科目性质列表.clear();
		科目性质列表.add(DictCodeBaseSubjectNatrueNew.本期利润_已实现.getValue());
		searchDto.setBaseSubjectNatureList(科目性质列表);
		// 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
		List<SubjectBalance> listRealized = accountingCommonService.getSubjectBalanceExpectDirection(
				searchDto, DictCodeList.余额方向_贷方.getValue());
		Map<String, List<SubjectBalance>> 本期利润_已实现余额集合 = new HashMap<>();
		if (CollectionUtils.isNotEmpty(listRealized)) {
			本期利润_已实现余额集合 = listRealized.stream().collect(
					Collectors.groupingBy(item -> item.getBokDetailId() + item.getbCcy()));
		}

		// 1.5 本期利润_未实现余额查询
		科目性质列表.clear();
		科目性质列表.add(DictCodeBaseSubjectNatrueNew.本期利润_未实现.getValue());
		searchDto.setBaseSubjectNatureList(科目性质列表);
		// 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
		List<SubjectBalance> listUnrealized = accountingCommonService.getSubjectBalanceExpectDirection(
				searchDto, DictCodeList.余额方向_贷方.getValue());
		Map<String, List<SubjectBalance>> 本期利润_未实现余额集合 = new HashMap<>();
		if (CollectionUtils.isNotEmpty(listUnrealized)) {
			本期利润_未实现余额集合 = listUnrealized.stream().collect(
					Collectors.groupingBy(item -> item.getBokDetailId() + item.getbCcy()));
		}
		// 2 已实现损益、未实现损益结转处理
		// 2.1 生成损益结转初始化金额
		// 定义MAP<币种+账务明细代码, 币种余额entity> 币种余额
		Map<CcyBalanceDtoKey, CcyBalanceDto> 币种余额 = new HashMap<>();
		// 2.2 生成损益科目的结转数据
		// 2.2.1循环 1-2.账套下损益科目余额,循环对象 损益entity
		BokBussDetsub detsub=new BokBussDetsub();
		CcyBalanceDtoKey ccyBalanceDtoKey=null;
		CcyBalanceDto 余额entity =null;
		// 生成账务明细数据
		ValBokBookdetailBatch valBokBookdetailBatch = new ValBokBookdetailBatch();
		ProfitAndLossAccountingSubject subject = new ProfitAndLossAccountingSubject();
		for (SubjectBalance item : 损益类科目最新余额集合) {
			// 调用 权益账务明细代码获取 处理
			// 定义:权益账务明细代码
			String equityBokDetailId = "";
			// IF (损益entity.账务明细代码 in 子分层信息.账务明细代码)
			if (listBokDetailId.contains(item.getBokDetailId())) {
				// 权益账务明细代码 = 损益entity.账务明细代码
				equityBokDetailId = item.getBokDetailId();
			}
			else {
				// 权益账务明细代码 = 损益结转数据.账务明细代码
				equityBokDetailId = bokDetailId;
			}
			ccyBalanceDtoKey= new CcyBalanceDtoKey();
			ccyBalanceDtoKey.setCcy(item.getbCcy());
			ccyBalanceDtoKey.setBokDetailId(equityBokDetailId);
			余额entity = 币种余额.get(ccyBalanceDtoKey);
			// 如果entity为空,初始化entity ,相关金额赋值为0, entity入币种余额map
			if (余额entity == null) {
				余额entity = new CcyBalanceDto();
				币种余额.put(ccyBalanceDtoKey, 余额entity);
			}
			// 根据损益科目余额信息,生成新的账务明细数据
			// if 损益entity.基础科目性质 IN （1.3查询结果的基础性质）
			if (CollectionUtils.isNotEmpty(公允价值变动损益基础科目性质列表)
					&& 公允价值变动损益基础科目性质列表.contains(item.getBaseSubjectNature())) {
				// 账务明细.场景代码 = 会计场景代码_产品_未实现损益结转
				// 会计场景代码_产品损益结转处理_分红_损益平准金结转
				valBokBookdetailBatch = createValBokBookdetailBatch(accEntity, DictCodeListTableId.会计场景代码_产品_未实现损益结转.getValue());
				// if 损益entity.余额方向=“贷方”
				if (DictCodeList.余额方向_贷方.getValue().equals(item.getBalFlagHappenDate())) {
					// 余额entity.未实现损益贷方科目余额 = 余额entity.未实现损益贷方科目余额 + 损益entity.本币余额（发生日）
					// TODO 待确认？1.1结果的余额方向为贷方的数量余额（发生日）
					余额entity.setUnRealizedBalanceC(ArithmeticUtil.doAdd(余额entity.getUnRealizedBalanceC(), item.getbAmt()));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 start
					余额entity.setUnRealizedBalanceC_YB(ArithmeticUtil.doAdd(余额entity.getUnRealizedBalanceC_YB(), item.getBalance()));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 END
				}
				else {
					// entity.未实现损益借方科目余额 = entity.未实现损益借方科目余额+ 1.1结果的余额方向为借方的数量余额（发生日）
					// TODO 待确认？1.1结果的余额方向为贷方的数量余额（发生日）
					余额entity.setUnRealizedBalanceD(ArithmeticUtil.doAdd(余额entity.getUnRealizedBalanceD(), item.getbAmt()));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 start
					余额entity.setUnRealizedBalanceD_YB(ArithmeticUtil.doAdd(余额entity.getUnRealizedBalanceD_YB(), item.getBalance()));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 END

				}
			}
			else {
				// 账务明细.场景代码 = 会计场景代码_产品_已实现损益结转
				valBokBookdetailBatch = createValBokBookdetailBatch(accEntity, DictCodeListTableId.会计场景代码_产品_已实现损益结转.getValue());
				// if 损益entity.余额方向=“贷方”
				if (DictCodeList.余额方向_贷方.getValue().equals(item.getBalFlagHappenDate())) {
					// entity.已实现损益贷方科目余额 = entity.已实现损益贷方科目余额+ 1.1结果的余额方向为贷方的数量余额（发生日）
					// TODO 待确认？1.1结果的余额方向为贷方的数量余额（发生日）
					余额entity.setRealizedBalanceC(ArithmeticUtil.doAdd(余额entity.getRealizedBalanceC(), item.getbAmt()));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 start
					余额entity.setRealizedBalanceC_YB(ArithmeticUtil.doAdd(余额entity.getRealizedBalanceC_YB(), item.getBalance()));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 END

				}
				else {
					// entity.已实现损益借方科目余额 = entity.已实现损益借方科目余额+ 1.1结果的余额方向为借方的数量余额（发生日）
					// TODO 待确认？1.1结果的余额方向为贷方的数量余额（发生日）
					余额entity.setRealizedBalanceD(ArithmeticUtil.doAdd(余额entity.getRealizedBalanceD(), item.getbAmt()));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 start
					余额entity.setRealizedBalanceD_YB(ArithmeticUtil.doAdd(余额entity.getRealizedBalanceD_YB(), item.getBalance()));
					//update by xr for tishkbs640 货币型产品结转原币余额为0 END

				}
			}
			// 业务明细代码 <- 全局变量.空账务明细代码
			valBokBookdetailBatch.setBusiId(busiId);
			// 基本科目性质
			valBokBookdetailBatch.setBaseSubjectNature(item.getBaseSubjectNature());
			valBokBookdetailBatch.setSubjectNo(item.getSubjectNo());
			valBokBookdetailBatch.setBokDetailId(item.getBokDetailId());
			// 四级科目号
			String detailSubjectNo=item.getDetailSubjectNo();
			//调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
			if(!StringUtils.isNotEmpty(detailSubjectNo)){
				detsub=bookAccountGenLogic.genBokBussDetsub(valBokBookdetailBatch);
				if(detsub!=null){
					valBokBookdetailBatch.setDetailSubjectNo(detsub.getDetailSubjectNo());
				}
			}else{
				valBokBookdetailBatch.setDetailSubjectNo(detailSubjectNo);
			}
			// 账务明细.借贷方向 = 损益entity.余额方向取反
			if (DictCodeList.余额方向_贷方.getValue().equals(item.getBalFlagHappenDate())) {
				valBokBookdetailBatch.setCdFlag(DictCodeList.余额方向_借方.getValue());
			}
			else {
				valBokBookdetailBatch.setCdFlag(DictCodeList.余额方向_贷方.getValue());
			}
			// 账务明细.原币 = 损益entity.原币
			valBokBookdetailBatch.setoCcy(item.getOCcy());
			// 账务明细.原币发生额 = 损益entity.科目余额
			valBokBookdetailBatch.setoHappenAmt(item.getBalance());


			// 账务明细.本币发生额 = 损益entity.本币科目余额
			valBokBookdetailBatch.setbHappenAmt(item.getbAmt());
			// 账务明细.原币 = 损益entity.原币
			valBokBookdetailBatch.setbCcy(item.getbCcy());

			// 账务明细.发生数量 = 损益entity.数量
			valBokBookdetailBatch.setHappenNumber(item.getNum());

			// 其他字段设值同损益entity
			// 计算余额
			calBalance(item.getBalFlag(), item, valBokBookdetailBatch);
			list.add(cloneValBokBookdetailBatch(valBokBookdetailBatch));
			// 以账务明细代码，币种，基础科目性质分组
			listMap.put(item.getBokDetailId() + item.getbCcy(), valBokBookdetailBatch);
		}
		// 2.2.2生成本期利润账务明细数据
		// 循环 币种余额 当前循环对象币种余额entry
		SubjectBalance data=null;
		ValBokBookdetailBatch detail=null;
		String detailSubjectNo="";
		for (CcyBalanceDtoKey key : 币种余额.keySet()) {
			// 币种余额entity = 币种余额entry.value
			CcyBalanceDto entity = 币种余额.get(key);
			if (entity == null) {
				continue;
			}
			// I.生成已实现账务明细数据
			// 变量 余额差 = 币种余额entity.已实现损益借方科目余额 - 币种余额entity.已实现损益贷方科目余额
			BigDecimal 余额差 = ArithmeticUtil.doSub(entity.getRealizedBalanceD(),entity.getRealizedBalanceC());
			BigDecimal 余额差原币 = ArithmeticUtil.doSub(entity.getRealizedBalanceD_YB(),entity.getRealizedBalanceC_YB());
			if(!ArithmeticUtil.equals(BigDecimal.ZERO, 余额差)){
				// 本期利润已实现科目
				detail = createValBokBookdetailBatch(accEntity,DictCodeListTableId.会计场景代码_产品_已实现损益结转.getValue());
				if (余额差.compareTo(BigDecimal.ZERO) > 0) {
					detail.setCdFlag(DictCodeList.借贷方向_借.getValue());
				}
				else {
					detail.setCdFlag(DictCodeList.借贷方向_贷.getValue());
				}
				// 账务明细.原币 = 币种余额entry.key.币种
				detail.setoCcy(key.getCcy());
				// 账务明细.原币发生额 = 0
				//update by xr for tishkbs640 货币型产品结转原币余额为0 start
				detail.setoHappenAmt(余额差原币.abs());
				//update by xr for tishkbs640 货币型产品结转原币余额为0 end
				//账务明细.本币 =币种余额entry.key.币种
				detail.setbCcy(key.getCcy());
				//账务明细.本币发生额 = 余额差.abs()
				detail.setbHappenAmt(余额差.abs());
				// 账务明细.发生数量 = 0;
				detail.setHappenNumber(BigDecimal.ZERO);
				if (!DictCodeList.特殊核算模型_收益分层核算.getValue().equals(accEntity.getBokSpecMode())) {
					// 账务明细.账务明细代码 = 全局账务明细代码
					detail.setBokDetailId(bokDetailId);
					// 账务明细.业务明细代码 = 子账务明细代码.get(全局变量.账务明细代码).业务明细代码
					detail.setBusiId(bokDetailIdMap.get(bokDetailId).getBusiId());
				} else {
					// 账务明细.账务明细代码 = 币种余额entry.key.账务明细代码
					detail.setBokDetailId(key.getBokDetailId());
					// 账务明细.业务明细代码 = 子账务明细代码.get(账务明细.账务明细代码).业务明细代码
					detail.setBusiId(bokDetailIdMap.get(detail.getBokDetailId()).getBusiId());
				}
				// 账务明细.科目号 = 本期利润_已实现
				subject = 损益结转科目集合.get(DictCodeBaseSubjectNatrueNew.本期利润_已实现.getValue()).get(0);
				detail.setSubjectNo(subject.getSubjectNo());
				// 账务明细.基础科目性质 = 本期利润-已实现
				detail.setBaseSubjectNature(DictCodeBaseSubjectNatrueNew.本期利润_已实现.getValue());
				// 账务明细.四级科目号 = 本期利润_已实现+子账务明细代码.get(账务明细.账务明细代码).四级科目尾号
				// 四级科目号
				detailSubjectNo=detail.getDetailSubjectNo();
				//调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
				if(!StringUtils.isNotEmpty(detailSubjectNo)){
					detsub=bookAccountGenLogic.genBokBussDetsub(detail);
					if(detsub!=null){
						detail.setDetailSubjectNo(detsub.getDetailSubjectNo());
					}
				}else{
					detail.setDetailSubjectNo(detailSubjectNo);
				}
				// 账务明细.分支序号 = 子账务明细代码.get(账务明细.账务明细代码).分支序号
				detail.setBranch(bokDetailIdMap.get(detail.getBokDetailId()).getBranch());
				// 其他字段设值同 币种余额entity
				/**
				 * add by wujie on 20190705 start
				 */
				data = null;
				/**add by wujie o 20190705 end**/
				String keyNew = detail.getBokDetailId() + key.getCcy() + detail.getBaseSubjectNature();
				if (listMap.get(keyNew) != null) {
					data = new SubjectBalance();
					data.setBalance(listMap.get(keyNew).getoAmtHappenDate());
					data.setBalFlagHappenDate(listMap.get(keyNew).getBalFlagHappenDate());
					data.setNum(listMap.get(keyNew).getNumAmtHappenDate());
					data.setBalFlag(listMap.get(keyNew).getBalFlagHappenDate());
					data.setbAmt(listMap.get(keyNew).getbAmtHappenDate());
				} else if (CollectionUtils.isNotEmpty(本期利润_已实现余额集合.get(detail.getBokDetailId() + key.getCcy()))) {
					data = 本期利润_已实现余额集合.get(detail.getBokDetailId() + key.getCcy()).get(0);
				}
				if(data==null){
					data = new SubjectBalance();
					data.setBalance(BigDecimal.ZERO);
					data.setBalFlagHappenDate(DictCodeList.余额方向_贷方.getValue());
					data.setNum(BigDecimal.ZERO);
					data.setBalFlag(DictCodeList.余额方向_贷方.getValue());
					data.setbAmt(BigDecimal.ZERO);
				}
				calBalance(data.getBalFlag(), data, detail);
				list.add(cloneValBokBookdetailBatch(detail));
				listMap.put(keyNew, detail);
			}


			// II.生成未实现账务明细数据
			// 本期利润已实现科目
			// 变量 余额差 = 币种余额entity.未实现损益借方科目余额 - 币种余额entity.未实现损益贷方科目余额
			余额差 = ArithmeticUtil.doSub(entity.getUnRealizedBalanceD(),entity.getUnRealizedBalanceC());
			if(!ArithmeticUtil.equals(BigDecimal.ZERO, 余额差)){
				// 账务明细.场景代码 = 会计场景代码_产品_未实现损益结转
				detail = createValBokBookdetailBatch(accEntity, DictCodeListTableId.会计场景代码_产品_未实现损益结转.getValue());
				if (余额差.compareTo(BigDecimal.ZERO) > 0) {
					detail.setCdFlag(DictCodeList.借贷方向_借.getValue());
				}
				else {
					detail.setCdFlag(DictCodeList.借贷方向_贷.getValue());
				}
				// 账务明细.原币 = 币种余额entry.key.币种
				detail.setoCcy(key.getCcy());
				//update by xr for tishkbs640 货币型产品结转原币余额为0 start                 // 账务明细.原币发生额 = 0
				余额差原币 = ArithmeticUtil.doSub(entity.getUnRealizedBalanceD_YB(),entity.getUnRealizedBalanceC_YB());
				detail.setoHappenAmt(余额差原币.abs());
				//update by xr for tishkbs640 货币型产品结转原币余额为0 end
				//账务明细.本币 =币种余额entry.key.币种
				detail.setbCcy(key.getCcy());
				//账务明细.本币发生额 = 余额差.abs()
				detail.setbHappenAmt(余额差.abs());

				// 账务明细.发生数量 = 0;
				detail.setHappenNumber(BigDecimal.ZERO);
				if (!DictCodeList.特殊核算模型_收益分层核算.getValue().equals(accEntity.getBokSpecMode())) {
					// 账务明细.账务明细代码 = 全局变量.账务明细代码
					detail.setBokDetailId(bokDetailId);
					// 账务明细.业务明细代码 = 子账务明细代码.get(全局变量.账务明细代码).业务明细代码
					detail.setBusiId(bokDetailIdMap.get(bokDetailId).getBusiId());
				} else {
					// 账务明细.账务明细代码 = 币种余额entry.key.账务明细代码
					detail.setBokDetailId(key.getBokDetailId());
					// 账务明细.业务明细代码 = 子账务明细代码.get(账务明细.账务明细代码).业务明细代码
					detail.setBusiId(bokDetailIdMap.get(detail.getBokDetailId()).getBusiId());
				}
				// 账务明细.科目号 = 本期利润_未实现
				subject = 损益结转科目集合.get(DictCodeBaseSubjectNatrueNew.本期利润_未实现.getValue()).get(0);
				detail.setSubjectNo(subject.getSubjectNo());
				// 账务明细.基础科目性质 = 本期利润-未实现
				detail.setBaseSubjectNature(DictCodeBaseSubjectNatrueNew.本期利润_未实现.getValue());
				// 四级科目号
				detailSubjectNo=detail.getDetailSubjectNo();
				//调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
				if(!StringUtils.isNotEmpty(detailSubjectNo)){
					detsub=bookAccountGenLogic.genBokBussDetsub(detail);
					if(detsub!=null){
						detail.setDetailSubjectNo(detsub.getDetailSubjectNo());
					}
				}else{
					detail.setDetailSubjectNo(detailSubjectNo);
				}
				// 账务明细.分支序号 = 子账务明细代码.get(账务明细.账务明细代码).分支序号
				detail.setBranch(bokDetailIdMap.get(detail.getBokDetailId()).getBranch());
				// 其他字段设值同 币种余额entity
				/**
				 * add by wujie on 20190705 start
				 */
				data = null;
				/**add by wujei on 20190705 end**/
				String keyNew = detail.getBokDetailId() + key.getCcy() + detail.getBaseSubjectNature();
				if (listMap.get(keyNew) != null) {
					data = new SubjectBalance();
					data.setBalance(listMap.get(keyNew).getoAmtHappenDate());
					data.setBalFlagHappenDate(listMap.get(keyNew).getBalFlagHappenDate());
					data.setNum(listMap.get(keyNew).getNumAmtHappenDate());
					data.setBalFlag(listMap.get(keyNew).getBalFlagHappenDate());
					data.setbAmt(listMap.get(keyNew).getbAmtHappenDate());
				} else if (CollectionUtils.isNotEmpty(本期利润_未实现余额集合.get(detail.getBokDetailId() + key.getCcy()))){
					data = 本期利润_未实现余额集合.get(detail.getBokDetailId() + key.getCcy()).get(0);
				}
				if(data==null){
					data = new SubjectBalance();
					data.setBalance(BigDecimal.ZERO);
					data.setBalFlagHappenDate(DictCodeList.余额方向_贷方.getValue());
					data.setNum(BigDecimal.ZERO);
					data.setBalFlag(DictCodeList.余额方向_贷方.getValue());
					data.setbAmt(BigDecimal.ZERO);
				}
				calBalance(data.getBalFlag(), data, detail);
				list.add(cloneValBokBookdetailBatch(detail));
				listMap.put(keyNew, detail);
			}

		}
		list.forEach(x -> {
			// 流水号
			x.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
			// 处理序列 <- 自动生成的序列号20位
			x.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
			// 冲销标识
			x.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
			// 凭证说明
			x.setBookRemark(SysDataUtil.getNameFromDataDict(DictCode.会计场景代码, x.getTableId()));
			// 凭证编号
			// 计提类：场景代码 + 发生日 + 账务明细代码 + 冲销标识
			String bookId = x.getBooksetId() + x.getTableId()
					+ x.getHappenDate() + BookConstant.DEF_BOK_DETAIL_ID
					+ x.getOffsetFlag();
			bookId = EncryptUtil.getInstance().md5(bookId);
			x.setBookId(bookId);
			// 分支号
			x.setBranch(BookConstant.DEFALUT_BRANCH);
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
			data.setBalFlagHappenDate(DictCodeList.余额方向_借方.getValue());
		}
		Map<String, Object> mapHappenDate = accountingCommonService.balanceDirectDealAccrualAddBalance(
				balFlag, data.getBalFlagHappenDate(), data.getBalance(), data.getNum(),
				detail.getCdFlag(), detail.getoHappenAmt(), detail.getHappenNumber(),
				/**MODIFY BY WUJI ON 20190724 START
				 * null,null**/
				data.getbAmt(),detail.getbHappenAmt()
				/**MODIFY BY WUJIE ON 2010724 END**/
		);
		// 原币余额
		detail.setoAmtHappenDate(
				ArithmeticUtil.round((BigDecimal)mapHappenDate.get("balance"), NumberEnum.MONEY));
		// 本币余额
		detail.setbAmtHappenDate(
				ArithmeticUtil.round((BigDecimal)mapHappenDate.get("benbalance"), NumberEnum.MONEY));
		// 数量
		detail.setNumAmtHappenDate(
				ArithmeticUtil.round((BigDecimal)mapHappenDate.get("num"), NumberEnum.MONEY));
		// 余额方向
		detail.setBalFlagHappenDate((String)mapHappenDate.get("balDirect"));
	}

	private ValBokBookdetailBatch cloneValBokBookdetailBatch(ValBokBookdetailBatch entity) {
		ValBokBookdetailBatch clone = new ValBokBookdetailBatch();
		BeanUtil.copyPropertiesIgnoreNull(clone, entity);
		return clone;
	}

	private ValBokBookdetailBatch createValBokBookdetailBatch(ProfitAndLossAccountingNeedOperateEntity entity,
															  String tableId) {
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
		return detail;
	}

}
