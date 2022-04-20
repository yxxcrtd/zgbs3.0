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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 利润分配批处理 - Service
 * 
 * @author chengzhong
 * @version 1.0 2018年6月5日
 * @see ProfitAndLossAccountingStep3ProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProfitAndLossAccountingStep3ProcessorService {

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
		ArrayList<ValBokBookdetailBatch> list = new ArrayList<ValBokBookdetailBatch>();
		ArrayList<String> 科目性质列表 = new ArrayList<>();
		科目性质列表.add(DictCodeBaseSubjectNatrue.本期利润_已实现.getValue());
		科目性质列表.add(DictCodeBaseSubjectNatrue.本期利润_未实现.getValue());
		// 设值科目余额查询参数Dto
		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		// 账套代码
		searchDto.setBooksetId(accEntity.getBooksetId());
		// 发生日
		searchDto.setHappenDate(accEntity.getHappenDate());
		// 入账日
		searchDto.setBookDate(accEntity.getBookDate());
		// 基础科目性质
		searchDto.setBaseSubjectNatureList(科目性质列表);

		//全局变量.空业务明细代码= 损益结转账务处理结果.核算主体代码
		String busiId=accEntity.getBusiId();
		//全局变量.空账务明细代码 = 损益结转账务处理结果.账务明细代码

		// 1.0.本期利润科目最新余额查询
		List<SubjectBalance> 本期利润科目最新余额列表 = accountingCommonService.getSubjectLatestBalance(
				searchDto);
		本期利润科目最新余额列表 = 本期利润科目最新余额列表.stream().filter(
				i -> !(ArithmeticUtil.equals(BigDecimal.ZERO, i.getBalance())
						&& ArithmeticUtil.equals(BigDecimal.ZERO, i.getbAmt()))).collect(
				Collectors.toList());
		ArrayList<String> 科目类型列表 = new ArrayList<>();
		科目类型列表.add(DictCodeList.科目类型_损益类.getValue());
		科目类型列表.add(DictCodeList.科目类型_所有者权益类.getValue());
		科目性质列表.clear();
		科目性质列表.add(DictCodeBaseSubjectNatrue.本期利润_已实现.getValue());
		科目性质列表.add(DictCodeBaseSubjectNatrue.本期利润_未实现.getValue());
		科目性质列表.add(DictCodeBaseSubjectNatrue.未分配利润_已实现.getValue());
		科目性质列表.add(DictCodeBaseSubjectNatrue.未分配利润_未实现.getValue());
		// 1.2.损益结转科目查询
		Map<String, List<ProfitAndLossAccountingSubject>> 损益结转科目集合 = mapper.selectPalSubject(
				accEntity.getBooksetId(), 科目类型列表, 科目性质列表).stream().collect(
				Collectors.groupingBy(ProfitAndLossAccountingSubject::getBaseSubjectNature));
		科目性质列表.clear();
		科目性质列表.add(DictCodeBaseSubjectNatrue.未分配利润_未实现.getValue());

		// 设值科目余额查询参数Dto
		searchDto = new SubjectBalanceSearchDto();
		searchDto.setBooksetId(accEntity.getBooksetId());
		searchDto.setHappenDate(accEntity.getHappenDate());
		searchDto.setBookDate(accEntity.getBookDate());
		searchDto.setBaseSubjectNatureList(科目性质列表);
		// 1.4 未分配利润-未实现余额查询
		Map<String, List<SubjectBalance>> 未分配利润_未实现余额集合 = accountingCommonService.getSubjectLatestBalance(searchDto)
				.stream().collect(Collectors.groupingBy(SubjectBalance::getBokDetailId));
		科目性质列表.clear();
		科目性质列表.add(DictCodeBaseSubjectNatrue.未分配利润_已实现.getValue());
		// 1.5 未分配利润-已实现余额查询
		Map<String, List<SubjectBalance>> 未分配利润_已实现余额集合 = accountingCommonService.getSubjectLatestBalance(searchDto)
				.stream().collect(Collectors.groupingBy(SubjectBalance::getBokDetailId));
		// 2.0 本期利润处理
		BokBussDetsub detsub=new BokBussDetsub();
		ProfitAndLossAccountingSubject subject =new ProfitAndLossAccountingSubject();
		// 定义 变量.处理前余额
		SubjectBalance balanceData =null;
		//根据本期利润entity科目余额信息,生成新的账务明细数据
		ValBokBookdetailBatch detail =null;
		//根据本期利润科目余额信息,生成未分配利润的账务明细数据
		ValBokBookdetailBatch detail2 = null;
		for (SubjectBalance data : 本期利润科目最新余额列表) {
			detail = new ValBokBookdetailBatch();
			detail2 = new ValBokBookdetailBatch();
			if(DictCodeBaseSubjectNatrue.本期利润_已实现.getValue().equals(data.getBaseSubjectNature())){
				detail = createValBokBookdetailBatch(accEntity, DictCodeListTableId.会计场景代码_产品_本期利润已实现结转.getValue());
				detail2 = createValBokBookdetailBatch(accEntity, DictCodeListTableId.会计场景代码_产品_本期利润已实现结转.getValue());
			}else if(DictCodeBaseSubjectNatrue.本期利润_未实现.getValue().equals(data.getBaseSubjectNature())){
				detail = createValBokBookdetailBatch(accEntity,DictCodeListTableId.会计场景代码_产品_本期利润未实现结转.getValue());
				detail2 = createValBokBookdetailBatch(accEntity,DictCodeListTableId.会计场景代码_产品_本期利润未实现结转.getValue());
			}
			detail.setSubjectNo(data.getSubjectNo());
			detail.setBokDetailId(data.getBokDetailId());
			// 四级科目号
			String detailSubjectNo=data.getDetailSubjectNo();
			//调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
			if(!org.apache.commons.lang.StringUtils.isNotEmpty(detailSubjectNo)){
				detsub=bookAccountGenLogic.genBokBussDetsub(detail);
				if(detsub!=null){
					detail.setDetailSubjectNo(detsub.getDetailSubjectNo());
				}
			}else{
				detail.setDetailSubjectNo(detailSubjectNo);
			}
			// 业务明细代码
			detail.setBusiId(busiId);
			detail.setBaseSubjectNature(data.getBaseSubjectNature());
			String cdFlag="";
			if(DictCodeList.余额方向_贷方.getValue().equals(data.getBalFlagHappenDate())){
				cdFlag=DictCodeList.借贷方向_借.getValue();
			}else if(DictCodeList.余额方向_借方.getValue().equals(data.getBalFlagHappenDate())){
				cdFlag=DictCodeList.借贷方向_贷.getValue();
			}
			detail.setCdFlag(cdFlag);
			detail.setoHappenAmt(ArithmeticUtil.round(data.getBalance(), NumberEnum.MONEY));
			detail.setoCcy(data.getOCcy());
			/*增加本币处理逻辑   add by chufengjie 20190723 start*/
			detail.setbHappenAmt(ArithmeticUtil.round(data.getbAmt(), NumberEnum.MONEY));
			detail.setbCcy(data.getbCcy());
			/*增加本币处理逻辑   add by chufengjie 20190723 end*/
			//II.根据本期利润科目余额信息,生成未分配利润的账务明细数据
			if(DictCodeBaseSubjectNatrue.本期利润_已实现.getValue().equals(data.getBaseSubjectNature())){
				detail2.setBaseSubjectNature(DictCodeBaseSubjectNatrue.未分配利润_已实现.getValue());
				// 科目号
				subject= 损益结转科目集合.get(detail2.getBaseSubjectNature()).get(0);
				detail2.setSubjectNo(subject.getSubjectNo());
			}else{
				detail2.setBaseSubjectNature(DictCodeBaseSubjectNatrue.未分配利润_未实现.getValue());
				// 科目号
				subject= 损益结转科目集合.get(detail2.getBaseSubjectNature()).get(0);
				detail2.setSubjectNo(subject.getSubjectNo());
			}
			detail2.setBokDetailId(data.getBokDetailId());
			// 业务明细代码
			detail2.setBusiId(busiId);
			detail2.setoHappenAmt(ArithmeticUtil.round(data.getBalance(), NumberEnum.MONEY));
			detail2.setoCcy(data.getOCcy());
			detail2.setCdFlag(data.getBalFlagHappenDate());
			/*增加本币处理逻辑   add by chufengjie 20190723 start*/
			detail2.setbHappenAmt(ArithmeticUtil.round(data.getbAmt(), NumberEnum.MONEY));
			detail2.setbCcy(data.getbCcy());
			/*增加本币处理逻辑   add by chufengjie 20190723 end*/
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

			/**
			 * add by wujie on 20190705 start
			 */
			balanceData = null;
			/**add by wujie o 20190705 end**/
			String key = data.getBokDetailId();
			if(DictCodeBaseSubjectNatrue.本期利润_已实现.getValue().equals(data.getBaseSubjectNature())){
				if(!未分配利润_已实现余额集合.isEmpty()){
					if(未分配利润_已实现余额集合.get(key)!=null){
						balanceData =未分配利润_已实现余额集合.get(key).get(0);
					}
				}
			}else if(DictCodeBaseSubjectNatrue.本期利润_未实现.getValue().equals(data.getBaseSubjectNature())){
				if(!未分配利润_未实现余额集合.isEmpty()){
					if(未分配利润_未实现余额集合.get(key)!=null){
						balanceData = 未分配利润_未实现余额集合.get(key).get(0);
					}
				}
			}
			if(balanceData==null){
				balanceData = new SubjectBalance();
				balanceData.setBalance(BigDecimal.ZERO);
				balanceData.setBalFlagHappenDate(DictCodeList.余额方向_贷方.getValue());
				balanceData.setNum(BigDecimal.ZERO);
				balanceData.setBalFlag(DictCodeList.余额方向_贷方.getValue());
			}
			// 计算余额
			calBalance(data.getBalFlag(), data, detail);
			list.add(cloneValBokBookdetailBatch(detail));
			// 计算余额
			calBalance(balanceData.getBalFlag(), balanceData, detail2);
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
