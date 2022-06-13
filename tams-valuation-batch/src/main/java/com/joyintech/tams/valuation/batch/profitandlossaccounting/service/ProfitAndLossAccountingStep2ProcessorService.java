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
import com.joyintech.tams.common.base.entity.BokBussDetsub;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
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
 * @see ProfitAndLossAccountingStep2ProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProfitAndLossAccountingStep2ProcessorService {

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
		// 1-1.1查询账套下损益平准金/未分配利润科目余额
		// 设值科目余额查询参数Dto
		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		// 账套代码 = 损益结转数据.账套代码
		searchDto.setBooksetId(accEntity.getBooksetId());
		// 发生日 = 损益结转数据.发生日
		searchDto.setHappenDate(accEntity.getHappenDate());
		// 入账日 = 损益结转数据.入账日
		searchDto.setBookDate(accEntity.getBookDate());
		ArrayList<String> 科目性质列表 = new ArrayList<>();
		// 科目类型=“损益类
		科目性质列表.add(DictCodeBaseSubjectNatrue.损益平准金_已实现.getValue());
		科目性质列表.add(DictCodeBaseSubjectNatrue.损益平准金_未实现.getValue());
		searchDto.setBaseSubjectNatureList(科目性质列表);
		// 参照共通【科目余额查询】方法，参照【详细设计-账务类共通方法设计.xlsx的【科目余额查询】sheet】
		List<SubjectBalance> 损益平准金最新余额集合 = accountingCommonService.getSubjectLatestBalance(searchDto);
		// *结果集中原币余额为0的不做后续处理,结果集中删除
		损益平准金最新余额集合=损益平准金最新余额集合.stream().filter(
				item -> !(ArithmeticUtil.equals(BigDecimal.ZERO, item.getBalance())
						&& ArithmeticUtil.equals(BigDecimal.ZERO, item.getbAmt()))).collect(
				Collectors.toList());
		// 1-1.2 本期利润_已实现余额查询
		科目性质列表.clear();
		科目性质列表.add(DictCodeBaseSubjectNatrue.未分配利润_已实现.getValue());
		searchDto.setBaseSubjectNatureList(科目性质列表);
		// 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
		List<SubjectBalance> listRealized = accountingCommonService.getSubjectLatestBalance(searchDto);
		Map<String, List<SubjectBalance>> 未分配利润_已实现余额集合 = new HashMap<>();
		if (CollectionUtils.isNotEmpty(listRealized)) {
			未分配利润_已实现余额集合 = listRealized.stream().filter(
					item -> !ArithmeticUtil.equals(BigDecimal.ZERO, item.getBalance())).collect(
					Collectors.groupingBy(item -> item.getBokDetailId() + item.getOCcy()));
		}

		// 1-1.3 本期利润_未实现余额查询
		科目性质列表.clear();
		科目性质列表.add(DictCodeBaseSubjectNatrue.未分配利润_未实现.getValue());
		searchDto.setBaseSubjectNatureList(科目性质列表);
		// 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
		List<SubjectBalance> listUnrealized = accountingCommonService.getSubjectLatestBalance(searchDto);
		Map<String, List<SubjectBalance>> 未分配利润_未实现余额集合 = new HashMap<>();
		if (CollectionUtils.isNotEmpty(listUnrealized)) {
			未分配利润_未实现余额集合 = listUnrealized.stream().filter(
					item -> !ArithmeticUtil.equals(BigDecimal.ZERO, item.getBalance())).collect(
					Collectors.groupingBy(item -> item.getBokDetailId() + item.getOCcy()));
		}

		// 1-1.4.损益结转科目查询
		ArrayList<String> 科目类型列表 = new ArrayList<>();
		科目类型列表.add(DictCodeList.科目类型_损益类.getValue());
		科目类型列表.add(DictCodeList.科目类型_所有者权益类.getValue());
		科目性质列表.clear();
		科目性质列表.add(DictCodeBaseSubjectNatrue.未分配利润_已实现.getValue());
		科目性质列表.add(DictCodeBaseSubjectNatrue.未分配利润_未实现.getValue());
		Map<String, List<ProfitAndLossAccountingSubject>> 损益结转科目集合 = mapper.selectPalSubject(
				booksetId, 科目类型列表, 科目性质列表).stream().collect(
				Collectors.groupingBy(ProfitAndLossAccountingSubject::getBaseSubjectNature));
		// 2 已实现损益、未实现损益结转处理
		// 2.1 生成损益科目的结转数据
		// 2.1.1循环 1-1.账套下损益科目余额,循环对象 损益entity
		ValBokBookdetailBatch detail =null;
		ValBokBookdetailBatch detail2=null;
		String detailSubjectNo="";
		BokBussDetsub detsub=new BokBussDetsub();
		SubjectBalance data = null;
		for (SubjectBalance item : 损益平准金最新余额集合) {
			// I.生成已实现账务明细数据
			// 账务明细.场景代码 = 会计场景代码_产品_损益平准金结转
			detail = createValBokBookdetailBatch(accEntity, DictCodeListTableId.会计场景代码_产品_损益平准金结转.getValue());
			// 账务明细.借贷方向 = 损益平准金entity.发生日余额方向取反
			if (DictCodeList.余额方向_借方.getValue().equals(item.getBalFlagHappenDate())) {
				detail.setCdFlag(DictCodeList.借贷方向_贷.getValue());
			}
			else {
				detail.setCdFlag(DictCodeList.借贷方向_借.getValue());
			}
			// 账务明细.原币 = 损益平准金entity.原币
			detail.setoCcy(item.getOCcy());
			// 账务明细.原币发生额 = 损益平准金entity.发生日科目余额
			detail.setoHappenAmt(item.getBalance());

			/*增加本币处理逻辑   add by chufengjie 20190723 start*/
			// 账务明细.本币 = 损益平准金entity.本币
			detail.setbCcy(item.getbCcy());
			// 账务明细.本币发生额 = 损益平准金entity.本币发生日科目余额
			detail.setbHappenAmt(item.getbAmt());
			/*增加本币处理逻辑   add by chufengjie 20190723 end*/

			// 账务明细.发生数量 = 损益平准金entity.数量
			detail.setHappenNumber(item.getNum());
			// 其他字段设值同损益平准金entity
			// 业务明细代码
			detail.setBusiId(item.getBusiId());
			// 基础科目性质
			detail.setBaseSubjectNature(item.getBaseSubjectNature());
			// 账务明细代码
			detail.setBokDetailId(item.getBokDetailId());
			// 科目号
			detail.setSubjectNo(item.getSubjectNo());
			// 四级科目号
			detailSubjectNo=item.getDetailSubjectNo();
			//调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
			if(!org.apache.commons.lang.StringUtils.isNotEmpty(detailSubjectNo)){
				detsub=bookAccountGenLogic.genBokBussDetsub(detail);
				if(detsub!=null){
					detail.setDetailSubjectNo(detsub.getDetailSubjectNo());
				}
			}else{
				detail.setDetailSubjectNo(detailSubjectNo);
			}
			// 其他字段设值同 币种余额entity
			// 调用共通处理 余额方向处理_发生额加余额
			calBalance(item.getBalFlag(), item, detail);
			list.add(cloneValBokBookdetailBatch(detail));

			// II.根据损益平准金科目余额信息,生成未分配利润的账务明细数据
			// 本期利润已实现科目
			// 账务明细_2.场景代码 = 会计场景代码_产品_损益平准金结转
			detail2 = createValBokBookdetailBatch(accEntity,DictCodeListTableId.会计场景代码_产品_损益平准金结转.getValue());
			// if 损益平准金entity.基础科目性质 = 损益平准金_已实现
			if (DictCodeBaseSubjectNatrue.损益平准金_已实现.getValue().equals(item.getBaseSubjectNature())) {
				// 账务明细_2.科目号 = 未分配利润_已实现
				ProfitAndLossAccountingSubject subject = 损益结转科目集合.get(DictCodeBaseSubjectNatrue.未分配利润_已实现.getValue()).get(0);
				detail2.setSubjectNo(subject.getSubjectNo());
				// 账务明细_2.基础科目性质 = 未分配利润-已实现
				detail2.setBaseSubjectNature(DictCodeBaseSubjectNatrue.未分配利润_已实现.getValue());
			}
			else {
				// 账务明细_2.科目号 = 未分配利润_未实现
				ProfitAndLossAccountingSubject subject = 损益结转科目集合.get(DictCodeBaseSubjectNatrue.未分配利润_未实现.getValue()).get(0);
				detail2.setSubjectNo(subject.getSubjectNo());
				// 账务明细_2.基础科目性质 = 未分配利润-未实现
				detail2.setBaseSubjectNature(DictCodeBaseSubjectNatrue.未分配利润_未实现.getValue());
			}
			// 四级科目号
			detailSubjectNo=detail2.getDetailSubjectNo();
			//调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
			if(!StringUtils.isNotEmpty(detailSubjectNo)){
				detsub=bookAccountGenLogic.genBokBussDetsub(detail2);
				if(detsub!=null){
					detail2.setDetailSubjectNo(detsub.getDetailSubjectNo());
				}
			}else{
				detail2.setDetailSubjectNo(detailSubjectNo);
			}
			// 账务明细.借贷方向 = 损益平准金entity.发生日余额方向
			detail2.setCdFlag(item.getBalFlag());
			// 账务明细.原币 = 损益平准金entity.原币
			detail2.setoCcy(item.getOCcy());
			// 账务明细.原币发生额 = 0
			detail2.setoHappenAmt(item.getBalance());
			/*增加本币处理逻辑   add by chufengjie 20190723 start*/
			// 账务明细.原币 = 损益平准金entity.原币
			detail2.setbCcy(item.getbCcy());
			// 账务明细.原币发生额 = 0
			detail2.setbHappenAmt(item.getbAmt());
			/*增加本币处理逻辑   add by chufengjie 20190723 end*/
			// 账务明细.发生数量 = 损益平准金entity.数量
			detail2.setHappenNumber(item.getNum());
			// 其他字段设值同损益平准金entity
			// 业务明细代码
			detail2.setBusiId(item.getBusiId());
			// 账务明细代码
			detail2.setBokDetailId(item.getBokDetailId());
//			// 四级科目号
//			detail2.setDetailSubjectNo(detail.getDetailSubjectNo());
			// 其他字段设值同 币种余额entity
			// if 损益平准金entity.基础科目性质 = 损益平准金_已实现
			/**
			 * add by wujie on 20190705 start
			 */
			data = null;
			/**add by wujie o 20190705 end**/
			if(DictCodeBaseSubjectNatrue.损益平准金_已实现.getValue().equals(item.getBaseSubjectNature())){
				//处理前余额 = 未分配利润-已实现 查询结果,通过币种和账务明细代码获取
				if(未分配利润_已实现余额集合.get(item.getBokDetailId() + item.getOCcy())!=null){
					data = 未分配利润_已实现余额集合.get(item.getBokDetailId() + item.getOCcy()).get(0);
				}
			}
			else{
				// 处理前余额 = 未分配利润-未实现 查询结果,通过币种和账务明细代码获取
				if(未分配利润_未实现余额集合.get(item.getBokDetailId() + item.getOCcy())!=null){
					data=未分配利润_未实现余额集合.get(item.getBokDetailId() + item.getOCcy()).get(0);
				}
			}
			if(data==null){
				data = new SubjectBalance();
				data.setBalance(BigDecimal.ZERO);
				data.setBalFlagHappenDate(DictCodeList.余额方向_贷方.getValue());
				data.setNum(BigDecimal.ZERO);
				data.setBalFlag(DictCodeList.余额方向_贷方.getValue());
			}
			// 调用共通处理 余额方向处理_发生额加余额
			calBalance(data.getBalFlag(), data, detail2);
			list.add(cloneValBokBookdetailBatch(detail2));
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
			data.setBalFlagHappenDate(balFlag);
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
		/*增加本币处理逻辑   add by chufengjie 20190723 start*/
		// 本币余额
		detail.setbAmtHappenDate(
				ArithmeticUtil.round((BigDecimal)mapHappenDate.get("benbalance"), NumberEnum.MONEY));
		/*增加本币处理逻辑   add by chufengjie 20190723 end*/
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
