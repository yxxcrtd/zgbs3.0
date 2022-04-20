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
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingNeedOperateEntity;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingResult;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.collections.CollectionUtils;
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
 * @see ProfitAndLossAccountingLayeringProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProfitAndLossAccountingLayeringProcessorService {

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
	public ProfitAndLossAccountingResult execute(Map<String, Object> params) {
		// 获取reader返回值
		ProfitAndLossAccountingNeedOperateEntity accEntity = (ProfitAndLossAccountingNeedOperateEntity) params
				.get(GlobalConstant.READER_RESULT);

		// 若reader传递的参数为空，不进行后续处理
		if (accEntity == null) {
			return null;
		}

		ArrayList<ValBokBookdetailBatch> list = new ArrayList<ValBokBookdetailBatch>();
		Map<String, ValBokBookdetailBatch> listMap = new HashMap<>();
		// 全局变量.空账务明细代码 = 损益结转账务处理结果.账务明细代码
		String bokDetailId = accEntity.getBokDetailId();
		// 设值科目余额查询参数Dto
		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		// 账套代码 = 损益结转数据.账套代码
		searchDto.setBooksetId(accEntity.getBooksetId());
		// 发生日 = 损益结转数据.发生日
		searchDto.setHappenDate(accEntity.getHappenDate());
		// 入账日 = 损益结转数据.入账日
		searchDto.setBookDate(accEntity.getBookDate());
		// 科目类型=所有者权益类
		ArrayList<String> 科目类型列表 = new ArrayList<>();
		科目类型列表.add(DictCodeList.科目类型_所有者权益类.getValue());
		searchDto.setSubjectTypeList(科目类型列表);
		// 1.0.所有者权益类科目最新余额查询
		List<SubjectBalance> 所有者权益类科目最新余额列表 = accountingCommonService.getSubjectLatestBalance(
				searchDto);
		// **结果集中原币余额为0并且原币余额为0的不做后续处理,结果集中删除
		所有者权益类科目最新余额列表 = 所有者权益类科目最新余额列表.stream().filter(
				item -> !(ArithmeticUtil.equals(BigDecimal.ZERO, item.getBalance())
						&&ArithmeticUtil.equals(BigDecimal.ZERO, item.getbAmt()))).collect(
				Collectors.toList());
		Map<String, SubjectBalance> 所有者权益类科目最新余额列表Map = new HashMap<>();
		// 以账务明细代码+币种+基础科目性质 分组
		for (SubjectBalance item : 所有者权益类科目最新余额列表) {
			所有者权益类科目最新余额列表Map.put(
					item.getBokDetailId() + item.getOCcy() + item.getBaseSubjectNature(), item);
		}

		// 1-2 计算实收资本比例
		// 上述1-1查询结果中筛选基础科目性质=实收资本的科目余额信息组成MAP
		// KEY:账务明细代码 VALUE:原币余额（发生日）
		Map<String, BigDecimal> 实收资本科目余额集合 = 所有者权益类科目最新余额列表.stream().filter(
				item -> DictCodeBaseSubjectNatrue.实收资本.getValue().equals(
						item.getBaseSubjectNature())).collect(
				Collectors.toMap(SubjectBalance::getBokDetailId, SubjectBalance::getbAmt));

		// 1-3 查询无实收资本的权益类科目余额
		// 上述1-1查询结果集,筛选条件:账务明细代码不在1.2结果集keyset中.
		List<SubjectBalance> 非实收资本科目余额集合 = 所有者权益类科目最新余额列表.stream().filter(
				item -> !实收资本科目余额集合.keySet().contains(item.getBokDetailId())).collect(
				Collectors.toList());

		// 1-4.查询账套下分层结构
		// 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->查询分层结构子结构
		Map<String, Object> paramsMap = new HashMap<>();
		// 账套代码 = DTO.账套代码
		paramsMap.put("booksetId", accEntity.getBooksetId());
		// 查询分层结构子结构
		List<StayDrawSubprdEntity> listStayDrawSubprdEntity = accountingCommonService.querySubBokAcctInfo(
				paramsMap);
		// 定义 变量.子分层信息.账务明细代码
		List<String> listBokDetailId = new ArrayList<String>();
		Map<String, List<StayDrawSubprdEntity>> 子分层信息MAP = new HashMap<>();
		if (listStayDrawSubprdEntity != null) {
			// 循环设值
			Map<String, List<StayDrawSubprdEntity>> 分层层级分组MAP = new HashMap<>();
			String key = "";
			for (StayDrawSubprdEntity item : listStayDrawSubprdEntity) {
				// 子分层信息.实收资本 = 1-2处理结果.get(子分层信息.账务明细代码)
				item.setCapital(实收资本科目余额集合.get(item.getBokDetailId()));
				// 变量.子分层信息.账务明细代码.add(账务明细代码)
				listBokDetailId.add(item.getBokDetailId());
				// 去除层级为1的分层信息，层级为1的只可能有1个
				if ("1".equals(item.getLev())) {
					continue;
				}
				// 取当前层级最后一个-前面的值如：1-2-3取值1-2作为key，存入分层map中，以此类推下层会以上层层级分组存入map中
				key = item.getLev().substring(0, item.getLev().lastIndexOf("-"));
				if (CollectionUtils.isEmpty(分层层级分组MAP.get(key))) {
					分层层级分组MAP.put(key, new ArrayList<StayDrawSubprdEntity>(Arrays.asList(item)));
				}
				else {
					分层层级分组MAP.get(key).add(item);
				}
			}
			// 定义 变量.List<核算分层信息> list2
			List<StayDrawSubprdEntity> list2 = new ArrayList<>();
			// 分层信息.排序,排序条件分层信息.实收资本升序,先对list本身进行排序后再入map
			listStayDrawSubprdEntity.sort((a, b) -> {
				return ArithmeticUtil.subtract(a.getCapital(), b.getCapital()).compareTo(
						BigDecimal.ZERO);
			});
			for (StayDrawSubprdEntity item : listStayDrawSubprdEntity) {
				// 第一层只有1个，则放入整个list
				if ("1".equals(item.getLev())) {
					子分层信息MAP.put(item.getBokDetailId(), listStayDrawSubprdEntity);
					continue;
				}
				// 后面根据层级查询按层级分组后的map，如1-2会查出分组map中对应1-2的list，即下一层的list
				list2 = 分层层级分组MAP.get(item.getLev());
				if (CollectionUtils.isEmpty(list2)) {
					list2 = new ArrayList<>();
				}
				// 加上自身
				list2.add(item);
				// 存入子分层信息MAP中
				子分层信息MAP.put(item.getBokDetailId(), list2);
			}
		}

		// 2 权益科目结转处理
		// 定义 返回值 List<批处理用账务明细> 并初始化
		// 循环 1-3 查询结果, 做权益分层处理,逐条处理对象定义为entity
		// 定义 变量.分层信息
		List<StayDrawSubprdEntity> layeringInfoList = new ArrayList<>();
		BokBussDetsub detsub=new BokBussDetsub();
		for (SubjectBalance entity : 非实收资本科目余额集合) {
			// if (entity.账务明细代码 not in 子分层信息.账务明细代码)
			if (!listBokDetailId.contains(entity.getBokDetailId())) {
				// 分层信息 = 子分层信息MAP.get(损益结转数据.账务明细代码)
				layeringInfoList = 子分层信息MAP.get(bokDetailId);
			}
			else {
				// 分层信息 = 子分层信息MAP.get(entity.账务明细代码)
				layeringInfoList = 子分层信息MAP.get(entity.getBokDetailId());
			}
			// I.生成上层账务明细数据
			// 账务明细初始化,创建账务明细信息共通处理
			ValBokBookdetailBatch detail = createValBokBookdetailBatch(accEntity, DictCodeListTableId.会计场景代码_产品_分层核算权益拆分.getValue());
			detail.setBaseSubjectNature(entity.getBaseSubjectNature());
			// 账务明细.借贷方向 = entity.发生日余额方向取反
			detail.setCdFlag(entity.getBalFlagHappenDate().equals(
					DictCodeList.余额方向_贷方.getValue()) ? DictCodeList.借贷方向_借.getValue() : DictCodeList.借贷方向_贷.getValue());
			// 账务明细.原币发生额 = entity.原币余额(发生日)
			detail.setoHappenAmt(ArithmeticUtil.round(entity.getBalance(), NumberEnum.MONEY));
			// 其他字段设值同entity
			// 原币
			detail.setoCcy(entity.getOCcy());
			/*增加本币处理逻辑   add by chufengjie 20190723 start*/
			// 本币
			detail.setbCcy(entity.getbCcy());
			// 账务明细.本币发生额 = entity.本币余额(发生日)
			detail.setbHappenAmt(ArithmeticUtil.round(entity.getbAmt(), NumberEnum.MONEY));
			/*增加本币处理逻辑   add by chufengjie 20190723 end*/
			// 业务明细代码
			detail.setBusiId(entity.getBusiId());
			// 基础科目性质
			detail.setBaseSubjectNature(entity.getBaseSubjectNature());
			// 账务明细代码
			detail.setBokDetailId(entity.getBokDetailId());
			// 科目号
			detail.setSubjectNo(entity.getSubjectNo());
			// 四级科目号
			String detailSubjectNo=detail.getDetailSubjectNo();
			//调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
			if(!StringUtils.isNotEmpty(detailSubjectNo)){
				detsub=bookAccountGenLogic.genBokBussDetsub(detail);
				if(detsub!=null){
					detail.setDetailSubjectNo(detsub.getDetailSubjectNo());
					detail.setValBokBussDetsub(detsub);
				}
			}else{
				detail.setDetailSubjectNo(detailSubjectNo);
			}
			// 计算余额 调用共通处理 余额方向处理_发生额加余额
			calBalance(entity.getBalFlag(), entity, detail);
			// 账务明细入集合List<批处理用账务明细>
			list.add(cloneValBokBookdetailBatch(detail));
			// 以账务明细代码，币种，基础科目性质分组
			listMap.put(entity.getBokDetailId() + entity.getOCcy() + entity.getBaseSubjectNature(),
					detail);

			// II.生成分层账务明细数据
			// 返回金额列表 = 金额拆分共通处理(entity.本币余额(发生日),分层信息)
			List<SubjectBalance> splitAmountList = this.splitAmount(entity.getbAmt(),entity.getBalance(),layeringInfoList);
			// 循环 返回金额列表
			// 定义 变量.账务明细
			ValBokBookdetailBatch detail2 = new ValBokBookdetailBatch();
			// 定义 变量.处理前余额
			SubjectBalance data = new SubjectBalance();
			for (SubjectBalance balance : splitAmountList) {
				// 账务明细初始化,创建账务明细信息共通处理
				detail2 = createValBokBookdetailBatch(accEntity, DictCodeListTableId.会计场景代码_产品_分层核算权益拆分.getValue());
				// 账务明细.借贷方向 = entity.余额方向(发生日)
				detail2.setCdFlag(entity.getBalFlagHappenDate());
//                // 账务明细.原币发生额 = 返回金额列表.金额
//                detail2.setoHappenAmt(balance.getBalance());
				// 账务明细.原币发生额 = 0
				// detail2.setoHappenAmt(BigDecimal.ZERO);
				detail2.setoHappenAmt(balance.getBalance());
				// 其他字段设值同上层账务明细数据
				// 原币
				detail2.setoCcy(entity.getOCcy());
				/*增加本币处理逻辑   add by chufengjie 20190723 start*/
				// 本币 = 返回金额列表.金额
				detail2.setbCcy(entity.getbCcy());
				// 账务明细.本币发生额 = 返回金额列表.金额
				/**MODIFY BY WUJIE ON 20190919 START
				 detail2.setoHappenAmt(balance.getBalance());
				 **/
				// detail2.setbHappenAmt(balance.getBalance());
				detail2.setbHappenAmt(balance.getbAmt());
				/**MODIFY BY WUJIE ON 20190919 END**/
				/*增加本币处理逻辑   add by chufengjie 20190723 end*/
				// 业务明细代码
				detail2.setBusiId(entity.getBusiId());
				// 基础科目性质
				detail2.setBaseSubjectNature(entity.getBaseSubjectNature());
				// 账务明细代码
				detail2.setBokDetailId(balance.getBokDetailId());
				// 科目号
				detail2.setSubjectNo(entity.getSubjectNo());
				// 四级科目号
				detailSubjectNo=detail2.getDetailSubjectNo();
				//调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
				if(!StringUtils.isNotEmpty(detailSubjectNo)){
					detsub=bookAccountGenLogic.genBokBussDetsub(detail2);
					if(detsub!=null){
						detail2.setDetailSubjectNo(detsub.getDetailSubjectNo());
						detail2.setValBokBussDetsub(detsub);
					}
				}else{
					detail2.setDetailSubjectNo(detailSubjectNo);
				}

				// 处理前余额 = 返回值 List<批处理用账务明细>获取,条件:账务明细代码 基础科目性质 币种
				String key = balance.getBokDetailId() + entity.getOCcy()+ entity.getBaseSubjectNature();

				if (listMap.get(key) != null) {
					data = new SubjectBalance();
					data.setBalance(listMap.get(key).getoAmtHappenDate());
					data.setBalFlagHappenDate(listMap.get(key).getBalFlagHappenDate());
					data.setNum(listMap.get(key).getNumAmtHappenDate());
					data.setBalFlag(listMap.get(key).getBalFlagHappenDate());
				}
				else {
					data = 所有者权益类科目最新余额列表Map.get(key);
				}
				if(data==null){
					data = new SubjectBalance();
					data.setBalance(BigDecimal.ZERO);
					data.setBalFlagHappenDate(DictCodeList.余额方向_贷方.getValue());
					data.setNum(BigDecimal.ZERO);
					data.setBalFlag(DictCodeList.余额方向_贷方.getValue());
				}
				// 计算余额 调用共通处理 余额方向处理_发生额加余额
				calBalance(data.getBalFlag(), data, detail2);
				list.add(cloneValBokBookdetailBatch(detail2));
				listMap.put(key, detail2);
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
			String bookId = x.getBooksetId() + x.getTableId() + x.getHappenDate()
					+ BookConstant.DEF_BOK_DETAIL_ID + x.getOffsetFlag();
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

	/**
	 * 金额拆分
	 *
	 * @param bAmtAmount 总金额
	 * @param layeringInofList 分层信息
	 * @return
	 * @see
	 * @since 1.0
	 */
	private List<SubjectBalance> splitAmount(BigDecimal bAmtAmount,BigDecimal balanceAmount,
											 List<StayDrawSubprdEntity> layeringInofList) {
		// 计算实收资本总计 = 分层信息.实收资本之和
		BigDecimal totalCapital = BigDecimal.ZERO;
		for (StayDrawSubprdEntity item : layeringInofList) {
			totalCapital = ArithmeticUtil.doAdd(totalCapital, item.getCapital());
		}
		// 定义 List<拆分金额>
		List<SubjectBalance> splitAmountList = new ArrayList<>();
		// 定义 变量.拆分金额累加
		BigDecimal amountBAmt = BigDecimal.ZERO;
		BigDecimal amountBalance = BigDecimal.ZERO;

		// 循环分层信息,逐条计算
		for (int i = 0; i < layeringInofList.size(); i++ ) {
			// if(分层信息.实收资本 == 0)
			if (ArithmeticUtil.equals(layeringInofList.get(i).getCapital(), BigDecimal.ZERO)) {
				continue;
			}
			// 拆分金额 初始化
			SubjectBalance balance = new SubjectBalance();
			// 拆分金额.账务明细代码 = 分层信息.账务明细代码
			balance.setBokDetailId(layeringInofList.get(i).getBokDetailId());

			// IF 当前循环为分层信息最后一条时
			if (i == layeringInofList.size() - 1) {
				// 拆分金额.金额 = 总金额 - List<拆分金额>.金额
				balance.setbAmt(ArithmeticUtil.subtract(bAmtAmount, amountBAmt));
				balance.setBalance(ArithmeticUtil.subtract(balanceAmount, amountBalance));
			}
			else {
				// 拆分金额.金额 = 总金额*分层信息.实收资本/实收资本总计 *金额计算共通小数位处理
				balance.setbAmt(ArithmeticUtil.divide(
						ArithmeticUtil.multiply(bAmtAmount, layeringInofList.get(i).getCapital()),
						totalCapital, NumberEnum.MONEY.getCode()));
				balance.setBalance(ArithmeticUtil.divide(
						ArithmeticUtil.multiply(balanceAmount, layeringInofList.get(i).getCapital()),
						totalCapital, NumberEnum.MONEY.getCode()));
			}
			// 变量.List<拆分金额>.金额
			amountBAmt = ArithmeticUtil.doAdd(amountBAmt, balance.getbAmt());
			amountBalance= ArithmeticUtil.doAdd(amountBalance, balance.getBalance());
			// List<拆分金额>.add(拆分金额)
			splitAmountList.add(balance);
		}
		return splitAmountList;
	}

}
