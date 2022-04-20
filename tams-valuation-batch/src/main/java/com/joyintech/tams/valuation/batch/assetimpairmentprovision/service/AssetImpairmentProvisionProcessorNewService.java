/*
 * 文件名：AssetIntdateCarryoverProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年5月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.assetimpairmentprovision.service;

import com.google.common.collect.Maps;
import com.joyintech.calculation.CalculationException;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
import com.joyintech.tams.common.base.entity.ModEclResult;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.batch.assetimpairmentprovision.dao.AssetImpairmentProvisionMapper;
import com.joyintech.tams.valuation.batch.assetimpairmentprovision.dto.AssetImpairmentProvisionReaderResult;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accounting.service.LossCommonBatchService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.batch.finproduct.service.AccountingCommonBatchService;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 资产减值计提批处理- processor相关业务处理
 * 
 * @author chufengjie
 * @version 1.0 2020年10月13日
 * @see AssetImpairmentProvisionProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class AssetImpairmentProvisionProcessorNewService {

	@Autowired
	private AccountingCommonService accountingCommonService;

	@Autowired
	private FinProductCommonService finProductCommonService;

//	@Autowired
//	private AssetImpairmentProvisionMapper assetImpairmentProvisionMapper;
	
	@Autowired
	private LossCommonBatchService lossCommonBatchService;

	@Autowired
	private AccountingCommonBatchService	accountingCommonBatchService;
	

	/**
	 * 记账公共类
	 */
	@Autowired
	private BookCommonLogic bookCommonLogic;

	/**
	 * 记账引擎
	 */
	@Autowired
	private BookAccountGenLogicNew bookLogicNew;
	/**
	 * DBUtil
	 */
	@Autowired
	private DBUtil dBUtil;

	@Autowired
	private AssetImpairmentProvisionMapper assetImpairmentProvisionMapper;

	/**
	 * process 数据处理
	 * 
	 * @param param1
	 *            传递参数
	 * @return BookGenerateResult
	 * @see
	 * @since 1.0
	 */
	public BookGenerateResultWithBokSummary execute(Map<String, Object> param1) {

		// 定义返回值list
		BookGenerateResultWithBokSummary result = new BookGenerateResultWithBokSummary();
		// 获取reader返回值
		AssetImpairmentProvisionReaderResult acctEntity = (AssetImpairmentProvisionReaderResult) param1
				.get(GlobalConstant.READER_RESULT);

		// 若reader传递的参数或计提基础信息列表为空，不进行后续处理
		if (acctEntity == null) {
			return null;
		}

		// 获取从task拿到的参数
		SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo) param1.get("BATCHINFO");
		Date happenDate = (Date) param1.get("HAPPEN_DATE"); // 发生日：批处理任务执行信息.处理日（发生日）
		String taskId = batchExecuteInfo.getTaskId();
		SysBatchTask batchTask = (SysBatchTask) param1.get("BATCHTASK");
		Long taskPri = batchTask.getTaskPri();
		// 1.8.标的类金融产品信息
        // 定义查询list
        List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>();
        // 定义查询类型 实体类
        TargetSearchDto 标的信息 = new TargetSearchDto();
        标的信息.setQueryType(ValDictCodeList.标的_信息.getValue());
        // 查询实体类塞入查询实体类List中
        targetSearchDtoList.add(标的信息);
        Map<TargetSearchDto, Object> resultMap = finProductCommonService.getTargetInfo(acctEntity.getBusiId(),
                targetSearchDtoList);
        TargetFinProduct targetFinProduct = (TargetFinProduct) resultMap.get(标的信息);

        // 确保不为null
        if (targetFinProduct == null) {
           return null;
        }
		// 数据准备
		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		// 发生日 = 账务处理结果.发生日
		searchDto.setHappenDate(acctEntity.getHappenDate());
		// 入账日 = 账务处理结果.入账日
		searchDto.setBookDate(acctEntity.getBookDate());
		// 账务明细代码 = 账务处理结果.账务明细代码
		searchDto.setBokDetailId(acctEntity.getBokDetailId());
		// 账套代码 = 账务处理结果.账套代码
		searchDto.setBooksetId(acctEntity.getBooksetId());
		// 任务优先级 = 批处理参数.任务优先级
		searchDto.setTaskPri(batchTask.getTaskPri());
		String batchSeqno = batchExecuteInfo.getBatchExedetailSeqNo();

		// 1.调用共通查询发生日最新余额
		// 1.1.当日计提成本类科目余额
		// 基础科目性质查询
		List<String> costSubjectNatureList = accountingCommonService
				.getValSubjectNatureList(DictCodeListNatureGroup.分组代码_减值计提成本类对应科查询.getValue());
		searchDto.setBaseSubjectNatureList(costSubjectNatureList);
		List<SubjectBalance> costSubjectBalanceList = accountingCommonService
				.getSubjectBalanceExpectDirection(searchDto, DictCodeList.余额方向_借方.getValue());
		// 变量.成本科目余额 = 科目余额
		BigDecimal costSubjectBalance = BigDecimal.ZERO;
		// 变量.原币 = 原币种
		//String oCcy  = null;
		if(costSubjectBalanceList !=null&& costSubjectBalanceList.size() > 0){
			costSubjectBalance = costSubjectBalanceList.get(0).getBalance();
			//oCcy = costSubjectBalanceList.get(0).getOCcy();
		}else{
			costSubjectBalance = BigDecimal.ZERO;
		}

		// 1.2.应计利息类科目余额
		// 基础科目性质查询
		List<String> intrestSubjectNatureList = accountingCommonService
				.getValSubjectNatureList(DictCodeListNatureGroup.分组代码_减值计提利息类对应科目查询.getValue());
		searchDto.setBaseSubjectNatureList(intrestSubjectNatureList);
		List<SubjectBalance> intrestSubjectBalanceList = accountingCommonService
				.getSubjectBalanceExpectDirection(searchDto, DictCodeList.余额方向_借方.getValue());
		// 变量.应计利息余额 = 科目余额
		BigDecimal intrestSubjectBalance = BigDecimal.ZERO;
		if(intrestSubjectBalanceList !=null&& intrestSubjectBalanceList.size() > 0){
			intrestSubjectBalance = intrestSubjectBalanceList.get(0).getBalance();
		}else{
			intrestSubjectBalance = BigDecimal.ZERO;
		}

		// 1.3.溢折价类科目余额
		// 基础科目性质查询
		List<String> premiumSubjectNatureList = accountingCommonService
				.getValSubjectNatureList(DictCodeListNatureGroup.分组代码_减值计提利息调整类对应科目查询.getValue());
		searchDto.setBaseSubjectNatureList(premiumSubjectNatureList);
		List<SubjectBalance> premiumSubjectBalanceList = accountingCommonService
				.getSubjectBalanceExpectDirection(searchDto, DictCodeList.余额方向_借方.getValue());
		// 变量.利息调整 = 科目余额
		BigDecimal premiumSubjectBalance = BigDecimal.ZERO;
		if(premiumSubjectBalanceList !=null&& premiumSubjectBalanceList.size() > 0){
			premiumSubjectBalance = premiumSubjectBalanceList.get(0).getBalance();
		}else{
			premiumSubjectBalance = BigDecimal.ZERO;
		}

		// 1.4.其他应收款-本金
//		searchDto.setBaseSubjectNatureList(
//				new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue())));
//		Map<String, BigDecimal> receivableMap = accountingCommonService
//				.getSubjectBalanceCollection(searchDto, DictCodeList.余额方向_借方.getValue(),null);
		// 变量.其他应收本金 = 科目余额
		BigDecimal receivableBjSubjectBalance = BigDecimal.ZERO;
//		if(receivableMap !=null&& receivableMap.get(targetFinProduct.getCcy())!=null){
//			receivableBjSubjectBalance = receivableMap.get(targetFinProduct.getCcy());
//		}else{
//			receivableBjSubjectBalance = BigDecimal.ZERO;
//		}

		// 1.5.其他应收款-利息
		// 变量.其他应收利息 = 科目余额
		searchDto.setBaseSubjectNatureList(new ArrayList<String>(
				Arrays.asList(DictCodeBaseSubjectNatrueNew.应收利息_资金.getValue(),DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue())));
		Map<String, BigDecimal> receivableLxMap = accountingCommonService
				.getSubjectBalanceCollection(searchDto, DictCodeList.余额方向_借方.getValue(), null);
		// 变量.其他应收本金 = 科目余额
		BigDecimal receivableLxSubjectBalance  = BigDecimal.ZERO;
		if(receivableLxMap !=null && receivableLxMap.get(targetFinProduct.getCcy())!=null){
			receivableLxSubjectBalance = receivableLxMap.get(targetFinProduct.getCcy());
		}else{
			receivableLxSubjectBalance = BigDecimal.ZERO;
		}

		// 1.6.逾期利息
		searchDto.setBaseSubjectNatureList(
				new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.应收逾期款_利息.getValue())));
		Map<String, BigDecimal> overdueLxMap = accountingCommonService
				.getSubjectBalanceCollection(searchDto, DictCodeList.余额方向_借方.getValue(), null);
		// 变量.逾期利息 = 科目余额
		BigDecimal overdueLxSubjectBalance = BigDecimal.ZERO;
		if(overdueLxMap !=null&& overdueLxMap.get(targetFinProduct.getCcy())!=null){
			overdueLxSubjectBalance = overdueLxMap.get(targetFinProduct.getCcy());
		}else{
			overdueLxSubjectBalance = BigDecimal.ZERO;
		}

		// 1.7 调用共通查询减值准备科目余额
		// 基础科目性质查询
		List<String> jzzbSubjectNatureList = accountingCommonService
				.getValSubjectNatureList(DictCodeListNatureGroup.分组代码_减值计提减值准备类对应科目查询.getValue());
		searchDto.setBaseSubjectNatureList(jzzbSubjectNatureList);
		List<SubjectBalance> jzzbSubjectBalanceList = accountingCommonService
				.getSubjectBalanceExpectDirection(searchDto, DictCodeList.余额方向_借方.getValue());
		// 变量.昨日减值余额
		BigDecimal yLossSubjectBalance = BigDecimal.ZERO;
		// 变量.昨日减值本币余额
		BigDecimal yLossbSubjectBalance = BigDecimal.ZERO;
		if(jzzbSubjectBalanceList !=null&& jzzbSubjectBalanceList.size() > 0){
			// 变量.昨日减值余额 = 科目余额
			yLossSubjectBalance = jzzbSubjectBalanceList.get(0).getBalance();
			// 变量.昨日减值本币余额 = 本币科目余额
			yLossbSubjectBalance = jzzbSubjectBalanceList.get(0).getbAmt();
		}else{
			yLossSubjectBalance = BigDecimal.ZERO;
			yLossbSubjectBalance = BigDecimal.ZERO;
		}

		// 2.计提减值
		// 变量.EAD = 变量.成本科目余额+变量.应计利息余额+变量.利息调整+变量.其他应收本金+变量.其他应收利息+变量.逾期利息
		// (其中有空值，空值按0处理)
		costSubjectBalance = costSubjectBalance == null ? BigDecimal.ZERO : costSubjectBalance;
		intrestSubjectBalance = intrestSubjectBalance == null ? BigDecimal.ZERO : intrestSubjectBalance;
		premiumSubjectBalance = premiumSubjectBalance == null ? BigDecimal.ZERO : premiumSubjectBalance;
		receivableBjSubjectBalance = receivableBjSubjectBalance == null ? BigDecimal.ZERO : receivableBjSubjectBalance;
		receivableLxSubjectBalance = receivableLxSubjectBalance == null ? BigDecimal.ZERO : receivableLxSubjectBalance;
		overdueLxSubjectBalance = overdueLxSubjectBalance == null ? BigDecimal.ZERO : overdueLxSubjectBalance;
		BigDecimal variableEda = ArithmeticUtil.doAdd(costSubjectBalance, intrestSubjectBalance, premiumSubjectBalance,
				receivableBjSubjectBalance, receivableLxSubjectBalance, overdueLxSubjectBalance);
		// if 变量.EAD = 0 返回
		if (variableEda.compareTo(BigDecimal.ZERO)==0 && yLossSubjectBalance.compareTo(BigDecimal.ZERO)==0) {
			return null;
		}

		//========= modify by weipeng :加入减值模型相关处理 start========
		// 定义 变量.减值余额
		BigDecimal lossBalance = BigDecimal.ZERO;
		// 获取系统参数——减值方案类型
		String lossType = CacheUtil.getSysParam(DictCode.减值方案类型.getValue());
		// 减值方案不为空时，查询对应减值模型结果
		if(StringUtil.isNotEmpty(lossType)){
			Map<String, Object> params = new HashMap<>();
			params.put("booksetId", acctEntity.getBooksetId());
			params.put("assetId", acctEntity.getBusiId());
			params.put("invAim", acctEntity.getInvAim());
			params.put("happenDate", acctEntity.getHappenDate());
			// 定义 变量.减值模型ECL结果
			List<ModEclResult> modEclResultList = assetImpairmentProvisionMapper.getModelEclResult(params);
			// 若减值结果为空
			if(CollectionUtils.isEmpty(modEclResultList)){
				// 减值方案为普华时
				if(DictCodeListExt3.减值方案类型_普华.getValue().equals(lossType)){
//					modEclResultList = assetImpairmentProvisionMapper.getModelEclResultLatest(params);
					String msg = "当前批处理日期未进行减值批处理，无法生成对应减值数据，请先执行对应日期减值批处理任务！";
					throw new CalculationException(msg);
				}
				else if(DictCodeListExt3.减值方案类型_毕马威.getValue().equals(lossType)){
					modEclResultList = assetImpairmentProvisionMapper.getModelEclResultLatest(params);
				}
			}
			if(!CollectionUtils.isEmpty(modEclResultList)){
				if(DictCodeListExt3.减值方案类型_普华.getValue().equals(lossType)){
					for(ModEclResult item : modEclResultList){
						// 变量.减值余额 = 变量.减值余额 + 减值模型ECL结果.前瞻后ECL预期信用损失
						lossBalance = ArithmeticUtil.doAdd(lossBalance, item.getProspectiveEcl());
					}
				}
				// 若采用毕马威方案，则ecl损失比例直接查询，无需重新计算
				else if(DictCodeListExt3.减值方案类型_毕马威.getValue().equals(lossType)){
					ModEclResult modEclResult= modEclResultList.get(0);
					// 定义 变量.ecl损失比例 =  减值模型ECL结果.ecl损失比例
					BigDecimal eclRatio = modEclResult.getEclRatio();
					// 变量.减值余额 = EAD * ECL信用损失比例 *-1
					lossBalance = ArithmeticUtil.multiply(variableEda,eclRatio);
					lossBalance = ArithmeticUtil.round(lossBalance, NumberEnum.MONEY);
					// 若采用的是毕马威方案，则返回结果EAD需要更新入库
					if(DateUtil.compareDate(happenDate, modEclResult.getDecDate()) == 0){
						// 变量.减值模型ECL结果.EAD = 变量.EAD
						modEclResult.setEad(variableEda);
						//  变量.减值模型ECL结果.前瞻后ECL预期信用损失= 变量.减值余额
						modEclResult.setProspectiveEcl(lossBalance);
						// 将ECL结果添加到返回结果中
						result.setModEclResult(modEclResult);
					}
				}
			}

		}
		// 若未走减值模型逻辑，则继续走原先逻辑
		else{
//			List<String> subjectNatureList = accountingCommonService
//					.getValSubjectNatureList(DictCodeListNatureGroup.分组代码_成本类对应科目查询.getValue());

			BigDecimal eclRatio= accountingCommonBatchService.calcEclByTrdLoss(acctEntity.getTrdAssetLossList(),  targetFinProduct,
					acctEntity.getBooksetId(), acctEntity.getInvAim(), acctEntity.getHappenDate());
			// 变量.减值余额 = EAD * ECL信用损失比例
			lossBalance = ArithmeticUtil.multiply(variableEda,eclRatio);
			lossBalance = ArithmeticUtil.round(lossBalance, NumberEnum.MONEY);
		}
		//  变量.减值余额 =  变量.减值余额 * -1
		lossBalance = ArithmeticUtil.multiply(lossBalance,new BigDecimal(-1));

		//========= modify by weipeng :加入减值模型相关处理 end========

		// 变量.减值金额 = 变量.减值余额 - 变量.昨日减值余额
		BigDecimal lossAmount = lossBalance.subtract(yLossSubjectBalance);
		// 变量.减值余额本币 = 0
		BigDecimal lossBalanceBen = BigDecimal.ZERO;
		// 变量.减值金额本币 = 0
		BigDecimal lossAmountBen = BigDecimal.ZERO;

		if (!StringUtil.equals(targetFinProduct.getCcy(), acctEntity.getStandardCcy())) {
			// 调用计算类方法共通【汇率查询】
			List<CurrencyMarketDto> list = finProductCommonService.getCurrencyMarket(acctEntity.getHappenDate(),
					acctEntity.getBooksetId(), targetFinProduct.getCcy(), acctEntity.getStandardCcy());
			BigDecimal exchangeRate = list.get(0).getClosePrice();
			// 变量.减值余额本币 = 变量.减值余额* 汇率
			lossBalanceBen = lossBalance.multiply(exchangeRate);
			// 变量.减值金额本币 = 变量.减值余额本币 - 变量.昨日减值本币余额
			lossAmountBen = lossBalanceBen.subtract(yLossbSubjectBalance);
		} else {
			// 变量.减值余额本币 = 变量.减值余额
			lossBalanceBen = lossBalance;
			// 变量.减值金额本币 = 变量.减值余额
			lossAmountBen = lossAmount;
		}

		// 根据入参DTO.业务明细类型不同使用不同的会计事件
		// 组装记账元素
		// 初始化定义 - 记账场景要素管理
		AccountDriver ad = new AccountDriver();
		// 初始化定义 - 存放该笔记账的场景要素 map定义
		Map<String, Object> accountSceneMap = Maps.newHashMap();
		
		// 初始化定义 - 存放该笔记账的相关金额要素 map定义
		Map<String, BigDecimal> accountAmountMap = Maps.newHashMap();
		// 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
		Map<String, Object> accountCurrencyMap = Maps.newHashMap();
		Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();

		// 塞入记账场景树节点 = 会计场景
		ad.setTreeNode(acctEntity.getBookSetTreeNode());
		if (targetFinProduct.getFinprodType().equals(DictCodeListExt1.金融产品类型_存款.getValue())) {
			ad.setTableId(DictCodeListTableId.会计场景代码_存放同业_减值计提.getValue());
		} else if (targetFinProduct.getFinprodType().equals(DictCodeListExt1.金融产品类型_同业借款.getValue())) {
			ad.setTableId(DictCodeListTableId.会计场景代码_拆放同业_减值计提.getValue());
		} else if (targetFinProduct.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券.getValue())) {
			if (acctEntity.getInvAim().equals(DictCodeListExt1.投资目的_摊余成本类.getValue())) {
				ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类债券_减值计提.getValue());
			} else if (acctEntity.getInvAim().equals(DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue())) {
				ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类债券_减值计提.getValue());
			}
		} else if (targetFinProduct.getFinprodType().equals(DictCodeListExt1.金融产品类型_计划类资产.getValue())) {
			if (acctEntity.getInvAim().equals(DictCodeListExt1.投资目的_摊余成本类.getValue())) {
				/** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
				//如果为其他债券
				if(StringUtil.equals(DictCodeList.是否_是.getValue(), targetFinProduct.getIsQtzq())) {
					ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类其他债权投资_减值计提.getValue());
				}
				else {
					ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类收益型计划类资产_减值计提.getValue());
				}
				/** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
			} else if (acctEntity.getInvAim().equals(DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue())) {
				/** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
				//如果为其他债券
				if(StringUtil.equals(DictCodeList.是否_是.getValue(), targetFinProduct.getIsQtzq())) {
					ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类其他债权投资_减值计提.getValue());
				}
				else {
					ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类收益型资产_减值计提.getValue());
				}
				/** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
			}
		}
		else if (targetFinProduct.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
			ad.setTableId(DictCodeListTableId.会计场景代码_质押式逆回购_减值计提.getValue());
		}
		else if (targetFinProduct.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue())) {
			ad.setTableId(DictCodeListTableId.会计场景代码_买断式逆回购_减值计提.getValue());
		}

		// 记账场景要素map塞值,(调用共通方法-计提类场景元素赋值)
		Map<String, Object> lossParams = new HashMap<>();
		lossParams.put("taskId", taskId);
		lossParams.put("taskPri", taskPri);
		lossParams.put("oCcy", targetFinProduct.getCcy());		
		accountSceneMap = bookCommonLogic.accrualElementAssignForTaFeeLoss(acctEntity, lossParams);
		/**add by wujie on 20201106 start remark:计划类收益型资产出单边账问题修复**/
		// 境内外
		accountSceneMap.put(BookConstant.BLN_AREA, targetFinProduct.getBlnArea());
		/**add by wujie on 20201106 end remark:计划类收益型资产出单边账问题修复**/

		/**add by wujie on 20201106 end remark:计划类收益型资产出单边账问题修复**/
		// 分类1
		accountSceneMap.put(BookConstant.TYPE_1, targetFinProduct.getType1());
		// 分类2
		accountSceneMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());
		// 分类3
		accountSceneMap.put(BookConstant.TYPE_3, targetFinProduct.getType3());
		// 分类4
		accountSceneMap.put(BookConstant.TYPE_4, targetFinProduct.getType4());
		// 分类5
		accountSceneMap.put(BookConstant.TYPE_5, targetFinProduct.getType5());
		// 分类6
		accountSceneMap.put(BookConstant.TYPE_6, targetFinProduct.getType6());
		// 账套代码
		accountSceneMap.put(BookConstant.BOOKSET_ID, acctEntity.getBooksetId());
		// 冲正标识
		accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue());
		// 交易场所
		accountSceneMap.put(BookConstant.TRADE_MARKET, targetFinProduct.getTradeMarket());

    	String finprodTypeName = null;
    	if(targetFinProduct.getFinprodType()!=null){
    	    
            finprodTypeName = SysDataUtil.getNameFromDataDict(DictCode.金融产品类型, targetFinProduct.getFinprodType());
        }
		// 备注
		accountSceneMap.put(BookConstant.BOOK_REMARK, finprodTypeName + "每日减值计提");

		// if（变量.减值金额>= 0）
		if (lossAmount.compareTo(BigDecimal.ZERO) >= 0) {
			// 减值金额（借）<- 变量.减值金额
			accountAmountMap.put(BookConstant.IMPAMT_D, ArithmeticUtil.round(lossAmount, NumberEnum.MONEY));
			// 减值金额损益（贷） <- 变量.减值金额
			accountAmountMap.put(BookConstant.IMPAMTLOSS_C, ArithmeticUtil.round(lossAmount, NumberEnum.MONEY));

			accountAmountBCCYAMTMap.put(BookConstant.IMPAMT_D + BookConstant.BCCYAMT,
			    ArithmeticUtil.round(lossAmountBen, NumberEnum.MONEY));
			accountAmountBCCYAMTMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT,
			    ArithmeticUtil.round(lossAmountBen, NumberEnum.MONEY));
		} else {
			// 减值金额（贷）<- 变量.减值金额*-1
			accountAmountMap.put(BookConstant.IMPAMT_C,
					ArithmeticUtil.round(lossAmount.multiply(new BigDecimal(-1)), NumberEnum.MONEY));
			// 减值金额损益（借）<- 变量.减值金额*-1
			accountAmountMap.put(BookConstant.IMPAMTLOSS_D,
					ArithmeticUtil.round(lossAmount.multiply(new BigDecimal(-1)), NumberEnum.MONEY));
			
			accountAmountBCCYAMTMap.put(BookConstant.IMPAMT_C + BookConstant.BCCYAMT,
                ArithmeticUtil.round(lossAmountBen.multiply(new BigDecimal(-1)), NumberEnum.MONEY));
            accountAmountBCCYAMTMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT,
                ArithmeticUtil.round(lossAmountBen.multiply(new BigDecimal(-1)), NumberEnum.MONEY));
		}

		// 调用账务明细生成共通
		// 记账金额元素币种信息塞值--key是随记账金额map的
		for (String key : accountAmountMap.keySet()) {
			accountCurrencyMap.put(key, targetFinProduct.getCcy());
		}
		 for (String key : accountAmountBCCYAMTMap.keySet()) {
             accountCurrencyMap.put(key, acctEntity.getStandardCcy());
         }
		// 本位币设置
		accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
		accountAmountMap.putAll(accountAmountBCCYAMTMap);
		// map塞入场景要素管理中
		ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
		ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
		ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
		// add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
		ad.setSubjectMappingConfigDefault(acctEntity.getSubjectMappingConfig());// 辅助核算项
		// add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end

		// 记账凭证生成
		BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);

		// 变量.账务明细 = 调用账务明细生成共通返回结果中 基础科目性质 in （减值计提-减值准备科目分组）的第一条数据
		List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
		List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue())
					|| dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.定期存款_减值准备.getValue())
					|| dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.买入返售金融资产_减值准备.getValue())
					|| dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue())) {
				valBokBookdetailBatchResult.add(dto);
			}
		}
		ValBokBookdetailBatch valBokBookdetailBatchDto = null;
		if(valBokBookdetailBatchResult !=null&& valBokBookdetailBatchResult.size() > 0){
			valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
		}
		
		// 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
		result.getValBokBookdetailBatch().addAll(valBokBookdetailBatch);
        if(valBokBookdetailBatchDto!=null){
            // 初始化 统计分析明细表（资产减值）DTO
            BokStatLossPosiiton bokStatLossPosiiton = new BokStatLossPosiiton();
            // 流水 =自动生成
            bokStatLossPosiiton.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
            // 账务明细流水 =变量.账务明细.流水
            bokStatLossPosiiton.setBokSeqno(valBokBookdetailBatchDto.getSeqno());
            // 账套代码 =变量.账务明细.账套代码
            bokStatLossPosiiton.setBooksetId(valBokBookdetailBatchDto.getBooksetId());
            // 发生日期 =变量.账务明细.发生日期
            bokStatLossPosiiton.setHappenDate(valBokBookdetailBatchDto.getHappenDate());
            // 入账日期 =变量.账务明细.入账日期
            bokStatLossPosiiton.setBookDate(valBokBookdetailBatchDto.getBookDate());
            // 账套日期 =变量.账务明细.账套日期
            bokStatLossPosiiton.setBooksetDate(valBokBookdetailBatchDto.getBooksetDate());
            // 账务明细代码 =变量.账务明细.账务明细代码
            bokStatLossPosiiton.setBokDetailId(valBokBookdetailBatchDto.getBokDetailId());
            // 业务明细代码 =变量.账务明细.业务明细代码
            bokStatLossPosiiton.setBusiId(valBokBookdetailBatchDto.getBusiId());
            // 方案明细代码 =入参DTO.交易编号
            bokStatLossPosiiton.setSchemeDetailId(valBokBookdetailBatchDto.getTaskId());
            // 减值处理方式 变量.减值金额 >=0时，数据字典.减值处理方式.减值 else 数据字典.减值处理方式.转回
            if (lossAmount.compareTo(BigDecimal.ZERO)  <= 0) {
                bokStatLossPosiiton.setLossExeType(DictCodeListExt3.减值处理方式_减值.getValue());
            } else {
                bokStatLossPosiiton.setLossExeType(DictCodeListExt3.减值处理方式_转回.getValue());
            }
            // 原币 =变量.原币
            bokStatLossPosiiton.setCcy(targetFinProduct.getCcy());
            // 本位币 =入参DTO.本位币
            bokStatLossPosiiton.setbCcy(acctEntity.getStandardCcy());
            // 当日账面价值 =变量.成本科目余额+变量.应计利息余额+变量.利息调整+变量.其他应收本金
            bokStatLossPosiiton.setTdyCostAmt(ArithmeticUtil.doAdd(costSubjectBalance, 
                    premiumSubjectBalance, receivableBjSubjectBalance));
            // 当日账面应收利息 =变量.其他应收利息+变量.逾期利息
            bokStatLossPosiiton.setTdyIntAmt(ArithmeticUtil.doAdd(receivableLxSubjectBalance, intrestSubjectBalance,overdueLxSubjectBalance));
            // 当日减值金额 =变量.减值金额
            bokStatLossPosiiton.setTdyLossamtAdd(lossAmount);
            // 当日累计减值金额 =变量.减值余额
            bokStatLossPosiiton.setTdyLossamt(lossBalance);
            
          
            // 当日减值金额_本位币 =变量.减值金额本币
            bokStatLossPosiiton.setTdyLossamtAddB(lossAmountBen);
            // 当日累计减值金额_本位币 =变量.减值余额本币
            bokStatLossPosiiton.setTdyLossamtB(lossBalanceBen);
            // 减值生成方式
            bokStatLossPosiiton.setLossGenType( DictCodeListExt3.减值生成方式_减值计提批处理.getValue());
        
            // 统计分析明细表（资产减值）
            result.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
        }
		
		return result;

	}
}
