/*
 * 文件名：InvestFeePayService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：HuangJian
 * 修改时间：2018年9月25日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.investfeepay.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.calculation.entity.DealTypesFeeDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.investfeepay.dao.InvestFeePayMapper;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投资端费用支付-- process相关业务处理
 * 
 * @author HuangJian
 * @version 1.0 2018年9月25日
 * @see InvestFeePayProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class InvestFeePayProcessorNewService {

	/**
	 * 记账引擎
	 */
	@Autowired
	private BookAccountGenLogicNew bookLogicNew;

	/**
	 * 记账公共类
	 */
	@Autowired
	private BookCommonLogic bookCommonLogic;

	/**
	 * 计算类共通方法
	 */
	@Autowired
	private CalculationCommonService calculationCommonService;

	/**
	 * 账务类共通方法
	 */
	@Autowired
	private AccountingCommonService accountingCommonService;

	/**
	 * 
	 */
	@Autowired
	private InvestFeePayMapper investFeePayMapper;
	
    
    @Autowired
    private BookAccountCommonService bookAccountCommonService;

    Logger logger = Logger.getLogger(this.getClass());

	/**
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @param param
	 *            Map<String, Object>
	 * @return BookGenerateResult
	 * @see
	 * @since 1.0
	 */
	public BookGenerateResult execute(Map<String, Object> param) {
		// 获取reader返回值
		FinProductDealInfoResult acctEntity = (FinProductDealInfoResult) param.get(GlobalConstant.READER_RESULT);

		// 若reader传递的参数或金融产品交易信息列表为空，不进行后续处理
		if (acctEntity == null || acctEntity.getListFinProductDealInfoComDTO() == null
				|| acctEntity.getListFinProductDealInfoComDTO().isEmpty()) {
			return null;
		}
		// 获取待处理任务信息
		SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo) param.get("BATCHINFO");
		// 交易基本信息列表list
		List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO();

		// 查询费用配置信息

		List<BokFeeTableSet> listFeeTableSet = getFeeTableSetList(GlobalConstant.FEE_TABLE_SET_PS);
		// 定义此process的返回变量（因为浮动管理费要塞入帐套资产负债插入及更新，故用此实体类）
		BookGenerateResult returnResult = new BookGenerateResult();
		// add by weipeng on 20220303：由于浮动管理费和固定管理费对应基础科目性质为一个，查询余额需要按照科目查询
		List<String> listSubjectInternalCode = new ArrayList<>();
		listSubjectInternalCode.add(DictCodeBaseSubjectNatrueNew.应付交易费用_费用.getValue());
		listSubjectInternalCode.add(DictCodeBaseSubjectNatrueNew.应付运营费.getValue());
		Map<String, List<BokSubject>> subjectMap= bookLogicNew.getFeeSubjectMapByCodes(listSubjectInternalCode, acctEntity.getBooksetId(), acctEntity.getStandardCcy());

		// 交易明细 ： 交易类账务处理结果.交易明细列表
		for (FinProductDealInfoComDTO item : listFinProductDealInfoComDTONow) {

			if (DictCodeListExt1.金融产品类型_交易费用.getValue().equals(item.getFinprodType())
					|| DictCodeListExt1.金融产品类型_预提费用.getValue().equals(item.getFinprodType())) {
				getDealFee(returnResult, item, listFeeTableSet, acctEntity, batchExecuteInfo, subjectMap.get(DictCodeBaseSubjectNatrueNew.应付交易费用_费用.getValue()));
			} else {
				// 其他处理
				setQT(acctEntity, item, listFeeTableSet, returnResult, batchExecuteInfo, subjectMap);
			}

		}
		// 调用记账引擎返回：账务明细（当前表） 明细科目
		return returnResult;
	}

	/**
	 * 
	 * 交易费用处理<一句话功能简述> <功能详细描述>
	 * 
	 * @param returnResult
	 * @param item
	 * @param listFeeTableSet
	 * @param acctEntity
	 * @param batchExecuteInfo
	 * @see
	 * @since 1.0
	 */
	private void getDealFee(BookGenerateResult returnResult, FinProductDealInfoComDTO item,
			List<BokFeeTableSet> listFeeTableSet, FinProductDealInfoResult acctEntity,
			SysBatchExecInfo batchExecuteInfo, List<BokSubject> bokSubjectList) {
	    // 定义 变量.待处理费用明细列表
	    List<TrdTradeFeeDetail> listFeeDetail = new ArrayList<TrdTradeFeeDetail>();
		// add by weipeng on 20220316 增加辅助核算项配置信息
		// 定义 变量.辅助核算项配置信息
		SubjectMappingConfig mappingConfig = new SubjectMappingConfig();
		// 登记托管机构 = 交易信息.登记托管机构
		mappingConfig.setRegistOrg(item.getRegistOrg());
		// 交易场所 = 交易信息.交易场所
		mappingConfig.setTradeMarket(item.getTradeMarket());
		// 资金账户类型
		mappingConfig.setAccountType(item.getAcctType());
		// 费用类型 = 费用明细.费用类型
		mappingConfig.setFeeType(item.getFeeType());
	    // 循环去除作为待摊费用的明细
	    for(TrdTradeFeeDetail detail : item.getValTradeFeeDetailList()){
			// add by weipeng on 20220316 增加辅助核算项配置信息
	        // 待摊费用单独处理逻辑
	        if(DictCodeList.是否_是.getValue().equals(detail.getIsPrepaid())){
	            // 初始化定义 - 记账场景要素管理
	            AccountDriver ad = new AccountDriver();
	            // 初始化定义 - 存放该笔记账的场景要素 map定义
	            Map<String, Object> accountSceneMap = new HashMap<>();

	            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
	            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
	            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
	            Map<String, Object> accountCurrencyMap = new HashMap<>();
	            TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
	            // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
	            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, item);
	            // 设置账务引擎参数
	            ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
	            List<String> tableIds = investFeePayMapper.getTableIdByFee(item.getPs(),
                    item.getFinprodType(), detail.getFeeType());
	            ad.setTableId(tableIds != null ?tableIds.get(0) : DictCodeListTableId.会计场景代码_交易费用支付.getValue());
	            // 结息日 <- 交易.到期日
	            accountSceneMap.put(BookConstant.MDATE, item.getMdate());
	            accountSceneMap.put(BookConstant.BOOK_REMARK, GlobalConstant.投资类费用
	                    + SysDataUtil.getNameFromDataDict(DictCode.交易方向, item.getPs()) + GlobalConstant.交易登账);

	            //变量.账务明细代码 = 调用账务明细代码共通（交易明细.业务明细代码,交易明细.业务明细类型,交易明细.币种)
	            BokDetailId bokDetailId = bookCommonLogic.getValBokDetailId(item.getTradeId(),null, DictCodeListExt1.金融产品类型_待摊费用.getValue(), item.getCcy());
	            //场景元素.BOK_DETAIL_ID1  变量.账务明细代码
	            accountSceneMap.put(BookConstant.BOK_DETAIL_ID1, bokDetailId);
	            //场景元素.是否待摊 <- 费用明细.是否待摊
	            accountSceneMap.put(BookConstant.IS_PREPAID, detail.getIsPrepaid());
	            if(StringUtil.equals(DictCodeListExt1.资金账户类型_产品托管户.getValue(), item.getAcctType())){
					// 金额元素.交易金额 = 交易明细.交易金额
					accountAmountMap.put(BookConstant.DEALAMT_HQ, detail.getFeeAmt());
				}
	            else{
					// 金额元素.交易金额 = 交易明细.交易金额
					accountAmountMap.put(BookConstant.DEALAMT_JS, detail.getFeeAmt());
				}

                // 金额元素.待摊费用 = 交易明细.交易金额
                accountAmountMap.put(BookConstant.DTFEE, detail.getFeeAmt());
	            // 记账金额元素币种信息塞值
	            // 因为与记账金额元素map的key及数量一致，故采用循环
	            for (String keyNow : accountAmountMap.keySet()) {
	                accountCurrencyMap.put(keyNow, item.getCcy());// 币种 -
	            }

	            ad.setAccountSceneMap(accountSceneMap);
	            ad.setAccountAmountMap(accountAmountMap);
	            ad.setAccountCurrencyMap(accountCurrencyMap);

				// add by weipeng on 20220315：待摊费用区分费用类型，增加辅助核算项 start
				ad.setSubjectMappingConfigDefault(mappingConfig);
				// add by weipeng on 20220315：待摊费用区分费用类型，增加辅助核算项 end

	            BookGenerateResult resultNowNow = new BookGenerateResult();
	            resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
	            returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
	        }
	        else{
	            listFeeDetail.add(detail);
	        }
	    }
	    // if(变量.待处理费用明细列表 = 空){
	    if(CollectionUtils.isEmpty(listFeeDetail)){
	        return;
	    }
	    
		// 按照金融产品类型、场景代码、交易方向、费用类型为key进行分组封装成费用MAP（key:金融产品类型，交易方向，费用类型，value：费用明细）
		Map<String, List<BokFeeTableSet>> resultMap = new HashMap<>();
		List<String> feeList = new ArrayList<String>();
//		if (item.getValTradeFeeDetailList() != null && item.getValTradeFeeDetailList().size() > 0) {
		for (TrdTradeFeeDetail detail : listFeeDetail) {
			feeList.add(detail.getFeeType());
		}
//		}
		for (BokFeeTableSet feeTableSet : listFeeTableSet) {
			// 结算手续费去重复
			if ((DictCodeListExt1.金融产品类型_交易费用.getValue().equals(feeTableSet.getFinprodType())
					|| DictCodeListExt1.金融产品类型_预提费用.getValue().equals(feeTableSet.getFinprodType()))
					&& item.getPs().equals(feeTableSet.getPs())
					&& feeList.contains(feeTableSet.getFeeTypeId())) {
				String key = feeTableSet.getFinprodType() + feeTableSet.getPs() + feeTableSet.getTableId();
				if (resultMap.get(key) == null) {
					List<BokFeeTableSet> listFeeListDto = new ArrayList<BokFeeTableSet>();
					listFeeListDto.add(feeTableSet);
					resultMap.put(key, listFeeListDto);
				} else {
					List<BokFeeTableSet> list = resultMap.get(key);
					list.add(feeTableSet);
					resultMap.put(key, list);
				}
			}
		}

		for (String skey : resultMap.keySet()) {

			// 初始化定义 - 记账场景要素管理
			AccountDriver ad = new AccountDriver();
			// 初始化定义 - 存放该笔记账的场景要素 map定义
			Map<String, Object> accountSceneMap = new HashMap<>();

			// 初始化定义 - 存放该笔记账的相关金额要素 map定义
			Map<String, BigDecimal> accountAmountMap = new HashMap<>();
			// 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
			Map<String, Object> accountCurrencyMap = new HashMap<>();

			TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
			// 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
			accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, item);
			// 设置账务引擎参数
			ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
			// 结息日 <- 交易.到期日
			accountSceneMap.put(BookConstant.MDATE, item.getMdate());
			// 登记托管机构 <---交易.登记托管机构
           if (StringUtil.isNotEmpty(item.getRegistOrg())
               && !(StringUtil.equals(item.getRegistOrg(),DictCodeList.登记托管机构_上海清算所.getValue())||
               StringUtil.equals(item.getRegistOrg(),DictCodeList.登记托管机构_中央国债登记结算有限责任公司.getValue()))) {
               accountSceneMap.put(BookConstant.REGIST_ORG,"99"); 
            }else {
                accountSceneMap.put(BookConstant.REGIST_ORG, item.getRegistOrg());
            }
			
			accountSceneMap.put(BookConstant.BOOK_REMARK, GlobalConstant.投资类费用
					+ SysDataUtil.getNameFromDataDict(DictCode.交易方向, item.getPs()) + GlobalConstant.交易登账);
			//场景元素.是否待摊 <- 费用明细.是否待摊
            accountSceneMap.put(BookConstant.IS_PREPAID, DictCodeList.是否_否.getValue());

			List<BokFeeTableSet> list = resultMap.get(skey);

			ad.setTableId(list != null && list.size() > 0 ? list.get(0).getTableId() : null);
			// 调用共通 --费用处理共通
			// 变量.交易各种费用
			DealTypesFeeDto dealTypesFeeDto = calculationCommonService
					.quaryDealTypesFeeamt(listFeeDetail, item.getProdId(), item.getPortfolioId());

			// 设置变量账务明细代码
			String bokDetailId = null;
			//交易明细.是否作为待摊费用 = 是  假设item.getIsPrepaidExpenses() == 1的时候表示为待摊费用
//			if (item.getIsPrepaidExpenses() == 1){
//				BokDetailId BokDetail = bookCommonLogic.getValBokDetailId(item.getFinprodId(),null, item.getFinprodType(), item.getCcy());
//				bokDetailId = BokDetail.getBusiId();
//			}
			// 印花税账务明细代码
			if (dealTypesFeeDto.getStampDutyCode() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getStampDutyCode().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getStampDutyCode().getBokDetailId();
			}
			else if (dealTypesFeeDto.getStampDutyCodeForDealPay() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getStampDutyCodeForDealPay().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getStampDutyCodeForDealPay().getBokDetailId();
			}
			// 经手费账务明细代码
			else if (dealTypesFeeDto.getBrokerageCode() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getBrokerageCode().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getBrokerageCode().getBokDetailId();
			}
			else if (dealTypesFeeDto.getBrokerageCodeForDealPay() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getBrokerageCodeForDealPay().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getBrokerageCodeForDealPay().getBokDetailId();
			}
			// 证管费账务明细代码
			else if (dealTypesFeeDto.getCertificateFeeCode() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getCertificateFeeCode().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getCertificateFeeCode().getBokDetailId();
			}
			else if (dealTypesFeeDto.getCertificateFeeCodeForDealPay() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getCertificateFeeCodeForDealPay().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getCertificateFeeCodeForDealPay().getBokDetailId();
			}
			// 结算费账务明细代码
			else if (dealTypesFeeDto.getClearingFeeCode() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getClearingFeeCode().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getClearingFeeCode().getBokDetailId();
			}
			else if (dealTypesFeeDto.getClearingFeeCodeForDealPay() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getClearingFeeCodeForDealPay().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getClearingFeeCodeForDealPay().getBokDetailId();
			}
			// 佣金账务明细代码
			else if (dealTypesFeeDto.getCommissionCode() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getCommissionCode().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getCommissionCode().getBokDetailId();
			}
			else if (dealTypesFeeDto.getCommissionCodeForDealPay() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getCommissionCodeForDealPay().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getCommissionCodeForDealPay().getBokDetailId();
			}
			// 交易手续费账务明细代码
			else if (dealTypesFeeDto.getTransactionFeeCode() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getTransactionFeeCode().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getTransactionFeeCode().getBokDetailId();
			}
			else if (dealTypesFeeDto.getTransactionFeeCodeForDealPay() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getTransactionFeeCodeForDealPay().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getTransactionFeeCodeForDealPay().getBokDetailId();
			}
			// 结算手续费账务明细代码
			else if (dealTypesFeeDto.getSettlementFeeCode() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getSettlementFeeCode().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getSettlementFeeCode().getBokDetailId();
			} 
			else if (dealTypesFeeDto.getSettlementFeeCodeForDealPay() != null
					&& !StringUtils.isEmpty(dealTypesFeeDto.getSettlementFeeCodeForDealPay().getBokDetailId())) {
				bokDetailId = dealTypesFeeDto.getSettlementFeeCodeForDealPay().getBokDetailId();
			} 
			else {
				bokDetailId = item.getBokDetailId();

			}

			if (list != null && list.size() > 0) {
				// LOOP 变量.费用列表
				for (BokFeeTableSet fee : list) {
					// 查询参数设值
					SubjectBalanceSearchDto searchDto = setSubjectBalanceSearchDto(item, fee, bokDetailId);
					// add by weipeng on 20220316 由于除佣金外一次性费用合并为一个基础科目性质，费用余额查询增加科目号匹配,且账户维护费没有应付科目
					if(!DictCodeListExt1.一次性费用类型_佣金.getValue().equals(fee.getFeeTypeId())
					   && !DictCodeListExt1.一次性费用类型_账户维护费.getValue().equals(fee.getFeeTypeId())){
						// 根据辅助核算项映射查询对应科目信息
						BokSubject feeSubject = bookLogicNew.getSubjectByInnerCode(bokSubjectList, mappingConfig);
						if(feeSubject != null){
							searchDto.setSubjectNo(feeSubject.getSubjectNo());
						}
					}

					// 经手费 交易各类费用经手费不为空
					if (DictCodeListExt1.一次性费用类型_经手费.getValue().equals(fee.getFeeTypeId())
							&& !StringUtils.isEmpty(bokDetailId)) {
						accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCode());
						if (DictCodeList.是否_是.getValue().equals(item.getIsClean())) {
							// 调用共通方法：科目余额汇总查询
							Map<String, BigDecimal> map = accountingCommonService.getSubjectBalanceCollection(searchDto,
									DictCodeList.余额方向_贷方.getValue(),null);
							// 科目余额
							BigDecimal subjectBalance = map.get(item.getCcy());
							// 金额元素.应付费用 <--返回值.科目余额
							accountAmountMap.put(BookConstant.HANDFEE, subjectBalance);
							// if 变量.交易各种费用.经手费 - 金额元素.应付费用 >0
							BigDecimal v1 = dealTypesFeeDto.getBrokerage() == null ? BigDecimal.ZERO
									: dealTypesFeeDto.getBrokerage();
							BigDecimal v2 = subjectBalance == null ? BigDecimal.ZERO : subjectBalance;

							if (v1.compareTo(v2) < 0) {
								// 金额元素.经手费费用调整_贷 = -1*(变量.交易各种费用.经手费 -
								// 金额元素.应付费用)
								accountAmountMap.put(BookConstant.HANDFEE_C,
										ArithmeticUtil.multiply(new BigDecimal("-1"), ArithmeticUtil.subtract(v1, v2)));
							} else {
								// 金额元素.经手费费用调整_借 = 变量.交易各种费用.经手费 -
								// 金额元素.应付费用
								accountAmountMap.put(BookConstant.HANDFEE_D, ArithmeticUtil.subtract(v1, v2));
							}

						} else {
							// 金额元素.应付费用 变量.交易各种费用.经手费
							accountAmountMap.put(BookConstant.HANDFEE, dealTypesFeeDto.getBrokerage());
						}
					}
					// 印花税 交易各类费用印花税不为空
					else if (DictCodeListExt1.一次性费用类型_印花税.getValue().equals(fee.getFeeTypeId())
							&& !StringUtils.isEmpty(bokDetailId)) {
						// 变量.交易各种费用.印花税
				        accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCode());
						if (DictCodeList.是否_是.getValue().equals(item.getIsClean())) {
							// 调用共通方法：科目余额汇总查询
							Map<String, BigDecimal> map = accountingCommonService.getSubjectBalanceCollection(searchDto,
									DictCodeList.余额方向_贷方.getValue(),null);
							// 科目余额
							BigDecimal subjectBalance = map.get(item.getCcy());
							// 金额元素.应付费用 <--返回值.科目余额
							accountAmountMap.put(BookConstant.STAMPTAX, subjectBalance);
							// if 变量.交易各种费用.印花税 - 金额元素.应付费用 >0
							BigDecimal v1 = dealTypesFeeDto.getStampDuty() == null ? BigDecimal.ZERO
									: dealTypesFeeDto.getStampDuty();
							BigDecimal v2 = subjectBalance == null ? BigDecimal.ZERO : subjectBalance;

							if (v1.compareTo(v2) < 0) {
								// 金额元素.印花税费用调整_贷 = -1*（变量.交易各种费用.印花税 -
								// 金额元素.应付费用）
								accountAmountMap.put(BookConstant.STAMPTAX_C,
										ArithmeticUtil.multiply(new BigDecimal("-1"), ArithmeticUtil.subtract(v1, v2)));
							} else {
								// 金额元素.印花税费用调整_借 = 变量.交易各种费用.印花税 -
								// 金额元素.应付费用
								accountAmountMap.put(BookConstant.STAMPTAX_D, ArithmeticUtil.subtract(v1, v2));
							}

						} else {
							// 金额元素.应付费用 变量.交易各种费用.印花税
							accountAmountMap.put(BookConstant.STAMPTAX, dealTypesFeeDto.getStampDuty());
						}
					}
					// 证管费 交易各类费用证管费不为空
					else if (DictCodeListExt1.一次性费用类型_证管费.getValue().equals(fee.getFeeTypeId())
							&& !StringUtils.isEmpty(bokDetailId)) {
						// 变量.交易各种费用.证管费
				        accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,
				            dealTypesFeeDto.getCertificateFeeCode());
						if (DictCodeList.是否_是.getValue().equals(item.getIsClean())) {
							// 调用共通方法：科目余额汇总查询
							Map<String, BigDecimal> map = accountingCommonService.getSubjectBalanceCollection(searchDto,
									DictCodeList.余额方向_贷方.getValue(),null);
							// 科目余额
							BigDecimal subjectBalance = map.get(item.getCcy());
							// 金额元素.应付费用 <--返回值.科目余额
							accountAmountMap.put(BookConstant.CERTIFEE, subjectBalance);

							// if 变量.交易各种费用.证管费 - 金额元素.应付费用 >0
							BigDecimal v1 = dealTypesFeeDto.getCertificateFee() == null ? BigDecimal.ZERO
									: dealTypesFeeDto.getCertificateFee();
							BigDecimal v2 = subjectBalance == null ? BigDecimal.ZERO : subjectBalance;

							if (v1.compareTo(v2) < 0) {
								// 金额元素.证管费费用调整_贷 = -1*(变量.交易各种费用.证管费 -
								// 金额元素.应付费用)
								accountAmountMap.put(BookConstant.CERTIFEE_C,
										ArithmeticUtil.multiply(new BigDecimal("-1"), ArithmeticUtil.subtract(v1, v2)));
							} else {
								// 金额元素.证管费费用调整_借 = 变量.交易各种费用.证管费 -
								// 金额元素.应付费用
								accountAmountMap.put(BookConstant.CERTIFEE_D, ArithmeticUtil.subtract(v1, v2));
							}

						} else {
							// 金额元素.应付费用 变量.交易各种费用.证管费
							accountAmountMap.put(BookConstant.CERTIFEE, dealTypesFeeDto.getCertificateFee());
						}
					}
					// 佣金 交易各类费用佣金不为空
					else if (DictCodeListExt1.一次性费用类型_佣金.getValue().equals(fee.getFeeTypeId())
							&& !StringUtils.isEmpty(bokDetailId)) {
						// 变量.交易各种费用.佣金
				        accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,
				            dealTypesFeeDto.getCommissionCode());
						if (DictCodeList.是否_是.getValue().equals(item.getIsClean())) {
							// 调用共通方法：科目余额汇总查询
							Map<String, BigDecimal> map = accountingCommonService.getSubjectBalanceCollection(searchDto,
									DictCodeList.余额方向_贷方.getValue(),null);
							// 科目余额
							BigDecimal subjectBalance = map.get(item.getCcy());
							// 金额元素.应付费用 <--返回值.科目余额
							accountAmountMap.put(BookConstant.COMMISSION, subjectBalance);
							// if 变量.交易各种费用.佣金 - 金额元素.应付费用 >0
							BigDecimal v1 = dealTypesFeeDto.getCommission() == null ? BigDecimal.ZERO
									: dealTypesFeeDto.getCommission();
							BigDecimal v2 = subjectBalance == null ? BigDecimal.ZERO : subjectBalance;

							if (v1.compareTo(v2) < 0) {
								// 金额元素.佣金费用调整_贷 = -1*(变量.交易各种费用.佣金 -
								// 金额元素.应付费用)
								accountAmountMap.put(BookConstant.COMMISSION_C,
										ArithmeticUtil.multiply(new BigDecimal("-1"), ArithmeticUtil.subtract(v1, v2)));
							} else {
								// 金额元素.佣金费用调整_借 = 变量.交易各种费用.佣金 -
								// 金额元素.应付费用
								accountAmountMap.put(BookConstant.COMMISSION_D, ArithmeticUtil.subtract(v1, v2));
							}
						} else {
							// 金额元素.应付费用 变量.交易各种费用.佣金
							accountAmountMap.put(BookConstant.COMMISSION, dealTypesFeeDto.getCommission());
						}
					}
					// 交易手续费 交易各类费用交易手续费不为空
					else if (DictCodeListExt1.一次性费用类型_交易手续费.getValue().equals(fee.getFeeTypeId())
							&& !StringUtils.isEmpty(bokDetailId)) {
						// 变量.交易各种费用.交易手续费
				        accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE,
				            dealTypesFeeDto.getTransactionFeeCode());
						if (DictCodeList.是否_是.getValue().equals(item.getIsClean())) {
							// 调用共通方法：科目余额汇总查询
							Map<String, BigDecimal> map = accountingCommonService.getSubjectBalanceCollection(searchDto,
									DictCodeList.余额方向_贷方.getValue(),null);
							// 科目余额
							BigDecimal subjectBalance = map.get(item.getCcy());
							// 金额元素.应付费用 <--返回值.科目余额
							accountAmountMap.put(BookConstant.TRADEFEE, subjectBalance);
							// if 变量.交易各种费用.交易手续费 - 金额元素.应付费用 >0
							BigDecimal v1 = dealTypesFeeDto.getTransactionFee() == null ? BigDecimal.ZERO
									: dealTypesFeeDto.getTransactionFee();
							BigDecimal v2 = subjectBalance == null ? BigDecimal.ZERO : subjectBalance;
							if (v1.compareTo(v2) < 0) {
								// 金额元素.交易手续费费用调整_贷 =
								// -1*(变量.交易各种费用.交易手续费 - 金额元素.应付费用)
								accountAmountMap.put(BookConstant.TRADEFEE_C,
										ArithmeticUtil.multiply(new BigDecimal("-1"), ArithmeticUtil.subtract(v1, v2)));

							} else {
								// 金额元素.交易手续费费用调整_借 = 变量.交易各种费用.交易手续费 -
								// 金额元素.应付费用
								accountAmountMap.put(BookConstant.TRADEFEE_D, ArithmeticUtil.subtract(v1, v2));
							}

						} else {
							// 金额元素.应付费用 变量.交易各种费用.交易手续费
							accountAmountMap.put(BookConstant.TRADEFEE, dealTypesFeeDto.getTransactionFee());
						}
					}
					// 结算费 交易各类费用结算费不为空
					else if (DictCodeListExt1.一次性费用类型_结算费.getValue().equals(fee.getFeeTypeId())
							&& !StringUtils.isEmpty(bokDetailId)) {
						// 变量.交易各种费用.结算费
				        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,
				            dealTypesFeeDto.getClearingFeeCode());
						if (DictCodeList.是否_是.getValue().equals(item.getIsClean())) {
							// 调用共通方法：科目余额汇总查询
							Map<String, BigDecimal> map = accountingCommonService.getSubjectBalanceCollection(searchDto,
									DictCodeList.余额方向_贷方.getValue(),null);
							// 科目余额
							BigDecimal subjectBalance = map.get(item.getCcy());
							// 金额元素.应付费用 <--返回值.科目余额
							accountAmountMap.put(BookConstant.SETTLEFEE, subjectBalance);
							// if 变量.交易各种费用.结算费 - 金额元素.应付费用 >0
							BigDecimal v1 = dealTypesFeeDto.getClearingFee() == null ? BigDecimal.ZERO
									: dealTypesFeeDto.getClearingFee();
							BigDecimal v2 = subjectBalance == null ? BigDecimal.ZERO : subjectBalance;

							if (v1.compareTo(v2) < 0) {
								// 金额元素.结算费费用调整_贷 = -1*(变量.交易各种费用.结算费 -
								// 金额元素.应付费用)
								accountAmountMap.put(BookConstant.SETTLEFEE_C,
										ArithmeticUtil.multiply(new BigDecimal("-1"), ArithmeticUtil.subtract(v1, v2)));

							} else {
								// 金额元素.结算费费用调整_借 = 变量.交易各种费用.结算费 -
								// 金额元素.应付费用
								accountAmountMap.put(BookConstant.SETTLEFEE_D, ArithmeticUtil.subtract(v1, v2));
							}

						} else {
							// 金额元素.应付费用 变量.交易各种费用.结算费
							accountAmountMap.put(BookConstant.SETTLEFEE, dealTypesFeeDto.getClearingFee());
						}
					}
					// 结算手续费 交易各类费用结算手续费不为空
					else if (!StringUtils.isEmpty(bokDetailId)
							&& DictCodeListExt1.一次性费用类型_结算手续费.getValue().equals(fee.getFeeTypeId())) {
						// 变量.交易各种费用.结算手续费
				        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,
				            dealTypesFeeDto.getSettlementFeeCode());
						if (DictCodeList.是否_是.getValue().equals(item.getIsClean())) {
							// 调用共通方法：科目余额汇总查询
							Map<String, BigDecimal> map = accountingCommonService.getSubjectBalanceCollection(searchDto,
									DictCodeList.余额方向_贷方.getValue(),null);
							// 科目余额
							BigDecimal subjectBalance = map.get(item.getCcy());
							// 金额元素.应付费用 <--返回值.科目余额
							accountAmountMap.put(BookConstant.SETTLEMENTFEE, subjectBalance);
							// if 变量.交易各种费用.结算手续费 - 金额元素.应付费用 >0
							BigDecimal v1 = dealTypesFeeDto.getSettlementFee() == null ? BigDecimal.ZERO
									: dealTypesFeeDto.getSettlementFee();
							BigDecimal v2 = subjectBalance == null ? BigDecimal.ZERO : subjectBalance;

							if (v1.compareTo(v2) < 0) {
								// 金额元素.结算手续费费用调整_贷 =
								// -1*(变量.交易各种费用.结算手续费 - 金额元素.应付费用)
								accountAmountMap.put(BookConstant.SETTLEMENTFEE_C,
										ArithmeticUtil.multiply(new BigDecimal("-1"), ArithmeticUtil.subtract(v1, v2)));

							} else {
								// 金额元素.结算手续费费用调整_借 = 变量.交易各种费用.结算手续费 -
								// 金额元素.应付费用
								accountAmountMap.put(BookConstant.SETTLEMENTFEE_D, ArithmeticUtil.subtract(v1, v2));
							}

						} else {
							// 金额元素.应付费用 变量.交易各种费用.结算手续费
							accountAmountMap.put(BookConstant.SETTLEMENTFEE, dealTypesFeeDto.getSettlementFee());
						}
					}else if (DictCodeListExt1.一次性费用类型_账户维护费.getValue().equals(fee.getFeeTypeId())
							  && !StringUtils.isEmpty(bokDetailId)) {
						// modify by weipeng on 20220316：账户维护费没有应付科目，不存在是否结清概念，按实付冲减
						// 金额元素.预提金额
						accountAmountMap.put(BookConstant.YTAMT, item.getTradeAmt());
						// 金额元素.实付金额
						accountAmountMap.put(BookConstant.SFAMT, item.getTradeAmt());
					} else {
						// 金额元素.交易金额 = 交易明细.交易金额
						accountAmountMap.put(BookConstant.DEALAMT, item.getTradeAmt());
					}
				}
				//// 金额元素.交易金额 = 交易明细.交易金额
				accountAmountMap.put(BookConstant.DEALAMT, item.getTradeAmt());
			}
			// 记账金额元素币种信息塞值
			// 因为与记账金额元素map的key及数量一致，故采用循环
			for (String keyNow : accountAmountMap.keySet()) {
				accountCurrencyMap.put(keyNow, item.getCcy());// 币种 -
																// 交易明细.交易币种
			}

			ad.setAccountSceneMap(accountSceneMap);
			ad.setAccountAmountMap(accountAmountMap);
			ad.setAccountCurrencyMap(accountCurrencyMap);
			// add by weipeng on 20220316 增加辅助核算项配置信息
			ad.setSubjectMappingConfigDefault(mappingConfig);

			BookGenerateResult resultNowNow = new BookGenerateResult();
			resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
			returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
		}

	}

	/**
	 * 查询费用配置信息
	 * 
	 * @param ps
	 *            交易方向
	 * @return List<FeeTableSet>
	 * @see
	 * @since 1.0
	 */
	private List<BokFeeTableSet> getFeeTableSetList(String ps) {
		// 费用配置信息
		return investFeePayMapper.getFeeTableSetList(ps);
	}

	/**
	 * 科目余额汇总参数DTO设值
	 * 
	 * @param item
	 *            FinProductDealInfoComDTO
	 * @param feeListDto
	 *            FeeListDto
	 * @param bokDetailId
	 *            String
	 * @return SubjectBalanceSearchDto
	 * @see
	 * @since 1.0
	 */
	private SubjectBalanceSearchDto setSubjectBalanceSearchDto(FinProductDealInfoComDTO item, BokFeeTableSet feeListDto,
			String bokDetailId) {
		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		// 账套代码
		searchDto.setBooksetId(item.getBooksetId());
		List<String> baseSubjectNatureList = new ArrayList<>();
		baseSubjectNatureList.add(feeListDto.getBaseSubjectNature());
		// 基础科目性质
		searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
		// 账务明细代码
		searchDto.setBokDetailId(bokDetailId);
		// 设置发生日
		searchDto.setHappenDate(DateUtil.addDay(item.getMdate(), -1));
		// 入账日
		searchDto.setBookDate(item.getBookDate());
		return searchDto;
	}

	/**
	 * 其他处理方式
	 * 
	 * @param acctEntity
	 *            FinProductDealInfoResult
	 * @param item
	 *            FinProductDealInfoComDTO
	 * @param listFeeTableSet
	 *            List<FeeTableSet>
	 * @param returnResult
	 *            BookGenerateResult
	 * @param batchExecuteInfo
	 *            SysBatchExecInfo
	 * @see
	 * @since 1.0
	 */
	private void setQT(FinProductDealInfoResult acctEntity, FinProductDealInfoComDTO item,
			List<BokFeeTableSet> listFeeTableSet, BookGenerateResult returnResult, SysBatchExecInfo batchExecuteInfo,
					   Map<String, List<BokSubject>> subjectMap) {

		// add by weipeng on 20220316 增加辅助核算项配置信息
		// 定义 变量.辅助核算项配置信息
		SubjectMappingConfig mappingConfig = new SubjectMappingConfig();
		// 登记托管机构 = 交易信息.登记托管机构
		mappingConfig.setRegistOrg(item.getRegistOrg());
		// 交易场所 = 交易信息.交易场所
		mappingConfig.setTradeMarket(item.getTradeMarket());
		// 资金账户类型
		mappingConfig.setAccountType(item.getAcctType());
		// 费用类型 = 费用明细.费用类型（投资端其他费用）
		mappingConfig.setFeeType(item.getFeeType());
		// 现金流二级类型  = 费用明细.费用类型（产品端费用）
		mappingConfig.setCashType2(item.getFeeType());

		// 1）共通元素设置
		// 初始化定义 - 记账场景要素管理
		AccountDriver ad = new AccountDriver();
		// 初始化定义 - 存放该笔记账的场景要素 map定义
		Map<String, Object> accountSceneMap = new HashMap<>();
		// 初始化定义 - 存放该笔记账的相关金额要素 map定义
		Map<String, BigDecimal> accountAmountMap = new HashMap<>();
		// 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
		Map<String, Object> accountCurrencyMap = new HashMap<>();
		TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
		// 设置账务引擎参数
		ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
		// 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
		accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, item);
		// 结息日 <- 交易.到期日
		accountSceneMap.put(BookConstant.MDATE, item.getMdate());
		// 登记托管机构 <---交易.登记托管机构
		accountSceneMap.put(BookConstant.REGIST_ORG, item.getRegistOrg());
		// 凭证说明
		accountSceneMap.put(BookConstant.BOOK_REMARK, GlobalConstant.投资类费用
				+ SysDataUtil.getNameFromDataDict(DictCode.交易方向, item.getPs()) + GlobalConstant.交易登账);

		//交易明细.是否作为待摊费用 = 是  替换账务明细代码为待摊费用的标的对应的账务明细代码
		if(!CollectionUtils.isEmpty(item.getValTradeFeeDetailList())){
		    String isPrepaird = item.getValTradeFeeDetailList().get(0).getIsPrepaid();
	        if (DictCodeList.是否_是.getValue().equals(isPrepaird)){
	            //变量.账务明细代码 = 调用账务明细代码共通（交易明细.业务明细代码,交易明细.业务明细类型,交易明细.币种)
	            BokDetailId bokDetail = bookCommonLogic.getValBokDetailId(item.getTradeId(),null, DictCodeListExt1.金融产品类型_待摊费用.getValue(), item.getCcy());
	            //场景元素.BOK_DETAIL_ID1  变量.账务明细代码 
	            accountSceneMap.put(BookConstant.BOK_DETAIL_ID1, bokDetail);
	            //场景元素.是否待摊 <- 是
	            accountSceneMap.put(BookConstant.IS_PREPAID, DictCodeList.是否_是.getValue());
	        }
	        else{
	            //场景元素.是否待摊 <- 否
	            accountSceneMap.put(BookConstant.IS_PREPAID, DictCodeList.是否_否.getValue());
	        }
		}
		

		List<BokFeeTableSet> listFeeListDto = new ArrayList<>();
		for (BokFeeTableSet feeTableSet : listFeeTableSet) {
			// 金融产品代码
			if (feeTableSet.getFinprodType().equals(item.getFinprodType())) {
				// 变量.场景代码 = 1结果对应.场景代码
				accountSceneMap.put(BookConstant.TABLE_ID, feeTableSet.getTableId());
				listFeeListDto.add(feeTableSet);
			}
		}

		// 场景代码
		ad.setTableId(listFeeListDto.get(0).getTableId());
		if (DictCodeList.是否_是.getValue().equals(item.getIsClean())) {
			BokFeeTableSet feeSet = listFeeListDto.get(0);
			List<BokSubject> bokSubjectList = subjectMap.get(feeSet.getBaseSubjectNature());
			// add by weipeng on 20220316 增加辅助核算项配置信息
			// 查询参数设值
			SubjectBalanceSearchDto searchDto = setSubjectBalanceSearchDto(item, feeSet,
				item.getBokDetailId());
			// add by weipeng on 20220316 由于除佣金外一次性费用合并为一个基础科目性质，费用余额查询增加科目号匹配
			// 根据辅助核算项映射查询对应科目信息
			BokSubject feeSubject = bookLogicNew.getSubjectByInnerCode(bokSubjectList, mappingConfig);
			if(feeSubject != null && feeSubject.getSubjectNo() != null){
				searchDto.setSubjectNo(feeSubject.getSubjectNo());
			}

			// 调用共通方法：科目余额汇总查询
			Map<String, BigDecimal> map = accountingCommonService.getSubjectBalanceCollection(searchDto,
					DictCodeList.余额方向_贷方.getValue(),null);
			// 科目余额
			BigDecimal subjectBalance = map.get(item.getCcy());
			// 金额元素.应计费用 = 返回值.科目余额
			accountAmountMap.put(BookConstant.ACCOUNTS_FEE, subjectBalance);
			// 差额
			BigDecimal xe = ArithmeticUtil.subtract(item.getTradeAmt(), subjectBalance);
			// 2018年11月27日 魏鹏：会计分录不分借贷
			//accountAmountMap.put(BookConstant.MARGINAL_COST, xe);
			 if(xe.compareTo(BigDecimal.ZERO)>=0){
			 // 金额元素.费用调整 = 交易明细.交易金额 - 金额元素.应计费用(暂时用费用差额)
			 accountAmountMap.put(BookConstant.MARGINAL_COST_D,xe);
			 }else{
			 // 金额元素.费用调整 = 交易明细.交易金额 - 金额元素.应计费用(暂时用费用差额)
			 accountAmountMap.put(BookConstant.MARGINAL_COST_C,ArithmeticUtil.multiply(xe,new
			 BigDecimal(-1)));
			 }

			// 金额元素.实付费用 = 交易明细.交易金额
			accountAmountMap.put(BookConstant.REAL_PAYMEET, item.getTradeAmt());

		} else {
			// 金额元素.应计费用 = [待处理交易].[交易金额]
			accountAmountMap.put(BookConstant.ACCOUNTS_FEE, item.getTradeAmt());
			// 金额元素.实付费用 = 交易明细.交易金额
			accountAmountMap.put(BookConstant.REAL_PAYMEET, item.getTradeAmt());
		}

		// 记账金额元素币种信息塞值
		// 因为与记账金额元素map的key及数量一致，故采用循环
		for (String keyNow : accountAmountMap.keySet()) {
			accountCurrencyMap.put(keyNow, item.getCcy());// 币种 - 交易明细.交易币种
		}

		ad.setAccountSceneMap(accountSceneMap);
		ad.setAccountAmountMap(accountAmountMap);
		ad.setAccountCurrencyMap(accountCurrencyMap);
		// add by weipeng on 20220316：增加辅助核算项配置
		ad.setSubjectMappingConfigDefault(mappingConfig);
		BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
		returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
	}
	
    /**
     * 组装账务明细(资金帐户/现金类)
     *
     * @param busiId
     * @param bokDetailId
     * @return
     * @see
     * @since 1.0
     */
    public BokDetailId getValBokDetailId(String busiId, String bokDetailId,
                                            String busiDetailType,String ccy) {
        BokDetailId valBokDetailId = new BokDetailId();
        valBokDetailId.setBusiId(busiId);
        valBokDetailId.setBusiDetailType(busiDetailType);
        /**add by wujie on 20190805 start**/
        valBokDetailId.setCcy(ccy);
        /**add by wujie on 20190805 end**/
        if (bokDetailId == null) {
            valBokDetailId.setBokDetailId(bookAccountCommonService.genMd5BokDetailId(valBokDetailId));
        }
        else {
            valBokDetailId.setBokDetailId(bokDetailId);
        }
        return valBokDetailId;
    }
}
