package com.joyintech.tams.valuation.batch.positionmake.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.positionmake.dao.PositionMakeWithoutDispatchMapper;
import com.joyintech.tams.valuation.batch.positionmake.dto.*;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.ValDateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Scope("prototype")
public class PositionMakeProcessorNewService {

	//	@Autowired
	//	private PositionMakeMapper positionMakeMapper;

	@Autowired
	private PositionMakeWithoutDispatchMapper positionMakeWithoutDispatchMapper;


	@Autowired
	private AccountingCommonService accountingCommonService;

	/**
	 * 调用投管接口平台
	 */
	public PositionMakeWriterResult execute(Map<String, Object> params){
		// process返回值
		PositionMakeWriterResult result = new PositionMakeWriterResult();
		if (MapUtils.isEmpty(params)) {
			return result;
		}
		PositionMakeReaderResult acctEntity = (PositionMakeReaderResult) params.get(GlobalConstant.READER_RESULT);
		// 数据参数
		String seqNo = (String)params.get("seqNo");
		String taskId = (String)params.get("taskId");
		Date booksetDate = (Date)params.get(BookConstant.BOOKSET_DATE);
		Date bookDate = (Date)params.get(BookConstant.BOOK_DATE);
		Date happenDate = (Date)params.get(BookConstant.HAPPEN_DATE);
		String isSubBookset = (String)params.get(BookConstant.IS_SUB_BOOKSET);
		if (!DictCodeListExt1.是或否_否.getValue().equals(isSubBookset)) {
			return result;
		}

		// 数据参数
		Map<String, Object> sqlMap = new HashMap<String, Object>();
		sqlMap.put("seqNo", seqNo);
		sqlMap.put("booksetDate", booksetDate);
		sqlMap.put("bookDate", bookDate);
		sqlMap.put("happenDate", happenDate);
		sqlMap.put("taskId", taskId);
		sqlMap.put("finprodType", DictCodeListExt1.金融产品类型_投资组合.getValue());// 金融产品类型
		sqlMap.put("busiDetailType", DictCodeListExt1.金融产品类型_理财产品.getValue());// 业务明细类型
		sqlMap.put("busiDetailTypeSub", DictCodeListExt1.金融产品类型_理财子产品.getValue());// 业务明细类型
		sqlMap.put("profitType", DictCodeList.收益类型_净值型.getValue());// 收益类型
		sqlMap.put("profitType02", DictCodeList.收益类型_收益型.getValue());// 收益类型
		sqlMap.put("booksetIds", Arrays.asList(acctEntity.getBooksetId()));
		/**
		 * 子组合特殊处理：按产品出头寸
		 * ADD BY WUJIE ON 20191121 START
		 */
		sqlMap.put("portfolioType", DictCodeListExt1.组合类型_子组合.getValue());// 组合类型：子组合 03
		/**ADD BY WUJIE ON 20191121 END**/

		// 1.查询组合资产估值【PTL_SEC_VALUTION】
		List<PtlSecValution> ptlSecValutionList = this.getPtlSecValutionList(sqlMap, bookDate, happenDate);

		// 2.查询产品费用头寸【BOK_POSITION_PRDFEE】
		List<BokPositionPrdfee> bokPositionPrdfeeList = this.getBokPositionPrdfeeList(sqlMap, bookDate, happenDate);

		// 3.查询产品头寸【BOK_POSITION_PRD】
		List<BokPositionPrd> bokPositionPrdList = this.getBokPositionPrdList(sqlMap, bookDate, happenDate);

		// 4.查询组合费用头寸【PTL_FEE_POSITION】
		List<PtlFeePosition> ptlFeePositionList = this.getPtlFeePositionList(sqlMap, bookDate, happenDate);

		// 5.查询组合估值头寸【PTL_PORTFOLIO_VALUATION】
		List<PtlPortfolioValuation> ptlPortfolioValuationList = this.getPtlPortfolioValuation(sqlMap, bookDate, happenDate);

		// 6.查询组合资产应收款头寸LIST 【PTL_SEC_RECEIVABLES】 add by yanya on 20191225
		List<PtlSecReceivables> ptlSecReceivablesList = this.getPtlSecReceivablesList(sqlMap, bookDate, happenDate);

		// 定义返回值
		result.setPtlSecValutionList(ptlSecValutionList);// 组合资产估值
		result.setBokPositionPrdfeeList(bokPositionPrdfeeList);// 产品费用头寸
		result.setBokPositionPrdList(bokPositionPrdList);// 产品头寸
		result.setPtlFeePositionList(ptlFeePositionList);// 组合费用头寸
		result.setPtlPortfolioValuationList(ptlPortfolioValuationList);// 组合估值头寸
		result.setPtlSecReceivablesList(ptlSecReceivablesList);//组合资产应收款头寸
		return result;
	}

	/**
	 * 调用共通查询发生日前一日余额 调用共通查询：科目余额查询-期待方向，查询现金类科目余额
	 *
	 * @param readerEntity reader传递的参数
	 * @param expBalanceDirect 期待余额方向
	 * @param baseSubjectNature 基础科目性质
	 * @param taskPri 任务优先级
	 * @return
	 * @see
	 * @since 1.0
	 */

	private SubjectBalance getSubjectBalance2(String booksetId, String bokDetailId,Date happenDate, Date bookDate,
											  String expBalanceDirect, List<String> baseSubjectNatureList) {
		// 初始化返回结果
		SubjectBalance balance = new SubjectBalance();
		// 设值科目余额查询参数Dto
		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		searchDto.setBooksetId(booksetId);
		searchDto.setBokDetailId(bokDetailId);
		searchDto.setHappenDate(happenDate);
		searchDto.setBookDate(bookDate);
		searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
		// 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
		List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
			searchDto, expBalanceDirect);

		// 获取科目余额
		if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
			balance = subjectBalanceList.get(0);
		}

		return balance;
	}


	/**
	 * 查询组合资产估值【PTL_SEC_VALUTION】
	 *
	 * @param sqlMap
	 * @param bookDate
	 * @param happenDate
	 * @return
	 */
	public List<PtlSecValution> getPtlSecValutionList(Map<String, Object> sqlMap, Date bookDate, Date happenDate) {

		// 定义组合估值返回list
		List<PtlSecValution> result = new ArrayList<PtlSecValution>();

		// 1.1 账套直投资产信息
		// 查询组合资产信息A
		List<PtlSecValutionWithOtherFieldDto> ptlSecValutionListWithNoTd = positionMakeWithoutDispatchMapper.getPtlSecValution(sqlMap);
		//查询组合资产信息(资金账户)
		List<PtlSecValutionWithOtherFieldDto> ptlSecValutionListWithNoTdF20 = positionMakeWithoutDispatchMapper.getPtlSecValutionF20(sqlMap);
		// 调用共通查询：科目余额查询-期待方向，查询现金类科目余额
		if (ptlSecValutionListWithNoTdF20 != null && ptlSecValutionListWithNoTdF20.size() > 0) {
			for (PtlSecValutionWithOtherFieldDto item : ptlSecValutionListWithNoTdF20) {
				List<String> subjectBaseNatureList = new ArrayList<>();
				subjectBaseNatureList.add(DictCodeBaseSubjectNatrueNew.活期存款_本金.getValue());
				subjectBaseNatureList.add(DictCodeBaseSubjectNatrueNew.结算备付金_本金.getValue());
				subjectBaseNatureList.add(DictCodeBaseSubjectNatrueNew.存出保证金_本金.getValue());
				//查询现金科目余额
				SubjectBalance balance = this.getSubjectBalanceByNatures(item.getBooksetId(), item.getBokDetailId(),
					happenDate, bookDate, DictCodeList.余额方向_借方.getValue(), subjectBaseNatureList);
				item.setDscCostAmt(balance.getBalance());
				item.setDscCostAmtB(balance.getbAmt());
				item.setShareAmt(balance.getNum());
				//查询应计利息
				subjectBaseNatureList.clear();
				subjectBaseNatureList.add(DictCodeBaseSubjectNatrueNew.活期存款_应计利息.getValue());
				subjectBaseNatureList.add(DictCodeBaseSubjectNatrueNew.结算备付金_应计利息.getValue());
				subjectBaseNatureList.add(DictCodeBaseSubjectNatrueNew.存出保证金_应计利息.getValue());
				SubjectBalance balanceYjlx = this.getSubjectBalanceByNatures(item.getBooksetId(), item.getBokDetailId(),
					happenDate, bookDate, DictCodeList.余额方向_借方.getValue(), subjectBaseNatureList);
				item.setTdyIntincexp(balanceYjlx.getBalance());
				item.setTdyIntincexpB(balanceYjlx.getBalance());
				item.setCdate(happenDate);
				item.setbCcy(item.getCcy());
				ptlSecValutionListWithNoTd.add(item);
			}
		}

		// 查询成本信息B
		List<PtlSecValutionWithOtherFieldDto> listCostInfo = positionMakeWithoutDispatchMapper.getCostInfo(sqlMap);
		// 定义 变量.组合资产信息C
		List<PtlSecValutionWithOtherFieldDto> listCostInfoC = new ArrayList<>();
		/**add by wujie on 20201109 start remark:处理成本为0时，减值余额不为0**/
		//定义 变量.减值准备D
		List<PtlSecValutionWithOtherFieldDto> listJzzbD = new ArrayList<>();
		/**add by wujie on 20201109 e**/

		// LOOP 上面查询结果 ENTITY
		if (CollectionUtils.isNotEmpty(listCostInfo)) {
			for (PtlSecValutionWithOtherFieldDto item : listCostInfo) {
				// 调用共通【账务类共通方法】，基础科目性质查询
				List<String> subjectNatureList = accountingCommonService.getValSubjectNatureListWithAcctStandardId(
					DictCodeListNatureGroup.分组代码_成本类对应科目查询.getValue(), DictCodeListExt2.会计准则_企业准则.getValue());
				// 调用共通【账务类共通方法】，科目余额查询-期待方向
				List<SubjectBalance> listSubjectBalance = this.getSubjectBalance(item.getBooksetId(),
					happenDate, bookDate, DictCodeList.余额方向_借方.getValue(), subjectNatureList);
				for(SubjectBalance balance : listSubjectBalance){
					PtlSecValutionWithOtherFieldDto dto = new PtlSecValutionWithOtherFieldDto();
					BeanUtils.copyProperties(item, dto);
					// ENTITY.业务明细代码 = 科目余额.业务明细代码
					dto.setBusiId(balance.getBusiId());
					dto.setFinprodId(balance.getBusiId());
					// ENTITY.数量 = 科目余额.科目余额数量
					dto.setShareAmt(balance.getNum());
					// ENTITY.摊余总成本 = 科目余额.科目余额
					dto.setDscCostAmt(balance.getBalance());
					// ENTITY.摊销总成本_本位币 = 科目余额.本币余额
					dto.setDscCostAmtB(balance.getbAmt());
					// ENTITY.币种 = 科目余额.币种
					dto.setCcy(balance.getOCcy());
					/** 增加原币 add by yanya on 20191213 bug[BSTOO-819]**/
					dto.setbCcy(balance.getbCcy());
					// ENTITY.账务明细代码 = 科目余额.账务明细代码
					dto.setBokDetailId(balance.getBokDetailId());
					// ENTITY.投资目的不为空，则 ENTITY.投资目的= 科目余额.投资目的
					if(StringUtil.isNotEmpty(balance.getInvAim())){
						dto.setInvAim(balance.getInvAim());
					}
					listCostInfoC.add(dto);
				}
				/**add by wujie on 20201109 start remark:处理成本为0时，减值余额不为0**/
				// 调用共通【账务类共通方法】，基础科目性质查询
				subjectNatureList = accountingCommonService.getValSubjectNatureListWithAcctStandardId(
					DictCodeListNatureGroup.分组代码_减值计提减值准备类对应科目查询.getValue(), DictCodeListExt2.会计准则_企业准则.getValue());
				// 调用共通【账务类共通方法】，科目余额查询-期待方向
				listSubjectBalance = this.getSubjectBalance(item.getBooksetId(),
					happenDate, bookDate, DictCodeList.余额方向_借方.getValue(), subjectNatureList);
				for(SubjectBalance balance : listSubjectBalance){
					if(balance.getBalance()==null || BigDecimal.ZERO.compareTo(balance.getBalance()) == 0){
						continue;
					}
					PtlSecValutionWithOtherFieldDto dto = new PtlSecValutionWithOtherFieldDto();
					BeanUtils.copyProperties(item, dto);
					// ENTITY.业务明细代码 = 科目余额.业务明细代码
					dto.setBusiId(balance.getBusiId());
					dto.setFinprodId(balance.getBusiId());
					// ENTITY.币种 = 科目余额.币种
					dto.setCcy(balance.getOCcy());
					dto.setbCcy(balance.getbCcy());
					// ENTITY.账务明细代码 = 科目余额.账务明细代码
					dto.setBokDetailId(balance.getBokDetailId());
					// ENTITY.投资目的不为空，则 ENTITY.投资目的= 科目余额.投资目的
					if(StringUtil.isNotEmpty(balance.getInvAim())){
						dto.setInvAim(balance.getInvAim());
					}
					// ENTITY.减值余额 = 科目余额.科目余额
					dto.setTdyLossAmt(balance.getBalance());
					// ENTITY.减值余额_本位币 = 科目余额.本币余额
					dto.setTdyLossAmtB(balance.getbAmt());
					listJzzbD.add(dto);
				}
				/**add by wujie on 20201109 end**/
			}

			// 循环组合资产信息结果B，如果该组合代码+资产代码在组合资产信息A中不存在，则A.add(B);
			if (CollectionUtils.isNotEmpty(ptlSecValutionListWithNoTd)) {
				Map<String, PtlSecValutionWithOtherFieldDto> dtoMap = new HashMap<>();
				ptlSecValutionListWithNoTd.forEach(item -> {
					dtoMap.put(item.getPortfolioId() + item.getFinprodId()+item.getInvAim(), item);
				});
				for (PtlSecValutionWithOtherFieldDto item : listCostInfoC) {
					// 如果该组合代码+资产代码在组合资产信息A中不存在，则A.add(B);
					String key =item.getPortfolioId()+item.getFinprodId()+item.getInvAim();
					if(!dtoMap.containsKey(key)){
						ptlSecValutionListWithNoTd.add(item);
					}
					// 若在A表中已存在，则判断是否是货币基金
					else{
						PtlSecValutionWithOtherFieldDto dto = dtoMap.get(key);
						// 货币基金时，A中的数量以及摊余总成本用B中的数据替换
						if(DictCodeListExt1.金融产品类型_基金.getValue().equals(dto.getFinprodType())
						   && DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())){
							// A表ENTITY.数量 = B表ENTITY.数量
							dto.setShareAmt(item.getShareAmt());
							// A表ENTITY.摊余总成本 = B表ENTITY.摊余总成本
							dto.setDscCostAmt(item.getDscCostAmt());
							// 组合资产信息ENTITY.摊余总成本_本位币= 成本ENTITY.摊余总成本_本位币
							dto.setDscCostAmtB(item.getDscCostAmtB());
							// 更新A表
							ptlSecValutionListWithNoTd.set(ptlSecValutionListWithNoTd.indexOf(dto), dto);
						}
						// 计划类货币型资产时，A中的数量以及摊余总成本用B中的数据替换
						if(DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(dto.getFinprodType())
						   && DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())){
							// A表ENTITY.数量 = B表ENTITY.数量
							dto.setShareAmt(item.getShareAmt());
							// A表ENTITY.摊余总成本 = B表ENTITY.摊余总成本
							dto.setDscCostAmt(item.getDscCostAmt());
							// 组合资产信息ENTITY.摊余总成本_本位币= 成本ENTITY.摊余总成本_本位币
							dto.setDscCostAmtB(item.getDscCostAmtB());
							// 更新A表
							ptlSecValutionListWithNoTd.set(ptlSecValutionListWithNoTd.indexOf(dto), dto);
						}
						// added by wangzhiang 平安个性化：回购到期时，推送到期结算金额-名义本金的利息 20190619 start
						else if ((DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(dto.getFinprodType())
								  || DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(dto.getFinprodType())
								  || DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(dto.getFinprodType())
								  || DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(dto.getFinprodType()))
								 && ValDateUtil.isSameDate(ValDateUtil.addDay(happenDate, 1), item.getMdate())) {
							dto.setTdyIntincexp(getRepoInt(item.getShareAmt(), dto.getmTradeAmt(), dto.getPrin()));
							// 组合资产信息ENTITY.今日应计利息_本位币 = 组合资产信息ENTITY.今日应计利息
							dto.setTdyIntincexpB(dto.getTdyIntincexp());
						}
						//						else if (DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(dto.getFinprodType())
						//								 || DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(dto.getFinprodType())) {
						//							if (DictCodeList.交易流通场所_上海证券交易所.getValue().equals(item.getTradeMarket())
						//								|| DictCodeList.交易流通场所_深圳证券交易所.getValue().equals(item.getTradeMarket())) {
						//								if (DictCodeListExt1.交易所回购计提天数方式_交易天数.getValue().equals(item.getSeRepDayType())
						//									&& DateUtil.isSameDate(happenDate, item.getMdate())) {
						//									dto.setTdyIntincexp(
						//										getRepoInt(item.getShareAmt(), dto.getmTradeAmt(), dto.getPrin()));
						//                                    // 组合资产信息ENTITY.今日应计利息_本位币 = 组合资产信息ENTITY.今日应计利息
						//                                    dto.setTdyIntincexpB(dto.getTdyIntincexp());
						//								}
						//								else if (DateUtil.isSameDate(DateUtil.addDay(happenDate, 1), item.getMpayDate())) {
						//									dto.setTdyIntincexp(
						//										getRepoInt(item.getShareAmt(), dto.getmTradeAmt(), dto.getPrin()));
						//                                    // 组合资产信息ENTITY.今日应计利息_本位币 = 组合资产信息ENTITY.今日应计利息
						//                                    dto.setTdyIntincexpB(dto.getTdyIntincexp());
						//								}
						//							}
						//							else if (DateUtil.isSameDate(DateUtil.addDay(happenDate, 1), item.getMdate())) {
						//								dto.setTdyIntincexp(getRepoInt(item.getShareAmt(), dto.getmTradeAmt(), dto.getPrin()));
						//                                // 组合资产信息ENTITY.今日应计利息_本位币 = 组合资产信息ENTITY.今日应计利息
						//                                dto.setTdyIntincexpB(dto.getTdyIntincexp());
						//							}
						//						}
						// added by wangzhiang 平安个性化：回购到期时，推送到期结算金额-名义本金的利息 20190619 end
						/** added by weipeng T+1债券在T日推送了债券信息，推送了数量以及清算款科目，摊余总成本推送为0，债券在这里特殊处理 start **/
						if(DictCodeListExt1.金融产品类型_债券.getValue().equals(dto.getFinprodType())){
							// 若是公允价值变动损益类，直接成本重新赋值
							if(DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(dto.getInvAim())){
								// 组合资产信息ENTITY.摊余总成本 = B表ENTITY.摊余总成本
								dto.setDscCostAmt(item.getDscCostAmt());
								// 组合资产信息ENTITY.摊余总成本_本位币= 成本ENTITY.摊余总成本_本位币
								dto.setDscCostAmtB(item.getDscCostAmtB());
							} else if(DictCodeListExt1.投资目的_卖空.getValue().equals(dto.getInvAim())){
								/*针对融入券，卖空时的场景*/
								// 组合资产信息ENTITY.摊余总成本 = B表ENTITY.摊余总成本
								dto.setDscCostAmt(item.getDscCostAmt());
								// 组合资产信息ENTITY.摊余总成本_本位币= 成本ENTITY.摊余总成本_本位币
								dto.setDscCostAmtB(item.getDscCostAmtB());
							}
							else{
								List<String> subjectNatureList = new ArrayList<String>();
								if(DictCodeListExt1.投资目的_摊余成本类.getValue().equals(dto.getInvAim())){
									subjectNatureList = Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue());
								}
								else if(DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(dto.getInvAim())){
									subjectNatureList = Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue());
								}
								// 调用共通【账务类共通方法】，科目余额查询-期待方向
								SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
								searchDao.setHappenDate(dto.getHappenDate());
								searchDao.setBookDate(dto.getBookDate());
								searchDao.setBooksetId(dto.getBooksetId());
								searchDao.setBusiId(dto.getFinprodId());
								searchDao.setBaseSubjectNatureList(subjectNatureList);
								List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
									searchDao, DictCodeList.余额方向_借方.getValue());
								if(CollectionUtils.isNotEmpty(listSubjectBalance)){
									// 组合资产信息ENTITY.摊余总成本 = B表ENTITY.摊余总成本
									dto.setDscCostAmt(ArithmeticUtil.doAdd(item.getDscCostAmt(), listSubjectBalance.get(0).getBalance()));
									// 组合资产信息ENTITY.摊余总成本_本位币= 成本ENTITY.摊余总成本_本位币
									dto.setDscCostAmtB(ArithmeticUtil.doAdd(item.getDscCostAmtB(), listSubjectBalance.get(0).getbAmt()));
								}
							}
							// 更新A表
							ptlSecValutionListWithNoTd.set(ptlSecValutionListWithNoTd.indexOf(dto), dto);
						}
						/** added by weipeng T+1债券在T日推送了债券信息，推送了数量以及清算款科目，摊余总成本推送为0，债券在这里特殊处理 end **/
					}
				}
			}
			// 否则直接放入A表
			else{
				ptlSecValutionListWithNoTd = new ArrayList<>();
				ptlSecValutionListWithNoTd.addAll(listCostInfoC);
			}
		}

		/**add by wujie on 20201109 start remark:处理成本为0时，减值余额不为0**/
		if (CollectionUtils.isEmpty(ptlSecValutionListWithNoTd)
			&& CollectionUtils.isNotEmpty(listJzzbD)){
			ptlSecValutionListWithNoTd = new ArrayList<>();
			ptlSecValutionListWithNoTd.addAll(listJzzbD);
		}
		else  if (CollectionUtils.isNotEmpty(ptlSecValutionListWithNoTd)
				  && CollectionUtils.isNotEmpty(listJzzbD)) {
			Map<String, PtlSecValutionWithOtherFieldDto> dtoMap = new HashMap<>();
			ptlSecValutionListWithNoTd.forEach(item -> {
				dtoMap.put(item.getPortfolioId() + item.getBokDetailId(), item);
			});
			for (PtlSecValutionWithOtherFieldDto item : listJzzbD) {
				// 如果该组合代码+账务明细代码在组合资产信息A中不存在，则A.add(C);
				String key =item.getPortfolioId()+item.getBokDetailId();
				if(!dtoMap.containsKey(key)){
					ptlSecValutionListWithNoTd.add(item);
				}
				else{
					PtlSecValutionWithOtherFieldDto dto = dtoMap.get(key);
					// ENTITY.减值余额 = D表ENTITY.减值余额
					dto.setTdyLossAmt(item.getTdyLossAmt());
					// ENTITY.减值余额_本位币 = D表ENTITY.减值余额_本位币
					dto.setTdyLossAmtB(item.getTdyLossAmtB());
					// 更新A表
					ptlSecValutionListWithNoTd.set(ptlSecValutionListWithNoTd.indexOf(dto), dto);
				}
			}
		}
		/**add by wujie on 20201109 end remark:处理成本为0时，减值余额不为0**/


		// 设计变更【价差收入、计提利率增加，邮储报表增加】。即直投资产，增加价差信息================begin。

		// 查询[LIST-价差信息C]
		// 调用共通【账务类共通方法】，基础科目性质查询
		List<String> subjectNatureListForJc = accountingCommonService.getValSubjectNatureListWithAcctStandardId(
			DictCodeListNatureGroup.分组代码_成本类对应科目查询.getValue(), DictCodeListExt2.会计准则_企业准则.getValue());

		// 查询[LIST-价差信息C]
		Map<String,Object> sqlmapForJc = new HashMap<String,Object>();
		sqlmapForJc.put("list",subjectNatureListForJc);//科目性质
		sqlmapForJc.put("booksetIds", sqlMap.get("booksetIds"));
		sqlmapForJc.put("bookDate", bookDate);
		sqlmapForJc.put("happenDate", happenDate);
		List<JcInfoDto> JcInfoDtoList =positionMakeWithoutDispatchMapper.getJcInfoDtoList(sqlmapForJc);

		// 循环【资产估值（直投资产）list】进行塞值。
		for(PtlSecValutionWithOtherFieldDto dto : ptlSecValutionListWithNoTd){
			// 循环[LIST-价差信息C]，匹配对应的数据
			for(JcInfoDto jcDto : JcInfoDtoList){
				//if 资产估值list.账套代码 = LIST-价差信息C.账套代码&&资产估值list.账务明细代码 = LIST-价差信息C.账务明细代码
				// && 资产估值list.入账日 = LIST-价差信息C.入账日 &&资产估值list.发生日 = LIST-价差信息C.发生日
				if(dto.getBooksetId().equals(jcDto.getBooksetId())
				   && dto.getBokDetailId().equals(jcDto.getBokDetailId())
				   && dto.getBookDate().equals(jcDto.getBookDate())
				   && dto.getHappenDate().equals(jcDto.getHappenDate())){

					//资产估值.当日价差收入 = LIST-价差信息C.价差收入_原币
					dto.setTdyDsclossAdd(jcDto.getJcForO());
					//资产估值.当日价差收入_本币 = LIST-价差信息C.价差收入_本币
					dto.setTdyDsclossAddB(jcDto.getJcForB());

					// 跳出当前内循环
					break;
				}
			}
		}

		// 设计变更【价差收入、计提利率增加，邮储报表增加】。即直投资产，增加价差信息================end。


		// 1.2 账套通道投资资产信息
		List<PtlSecValutionWithOtherFieldDto> ptlSecValutionListWithTd = positionMakeWithoutDispatchMapper.getPtlSecValutionWithTd(sqlMap);

		// 组合资产估值list
		List<PtlSecValutionWithOtherFieldDto> ptlSecValutionList = new ArrayList<PtlSecValutionWithOtherFieldDto>();
		ptlSecValutionList.addAll(ptlSecValutionListWithNoTd);// 资产估值list.add（直投资产）
		ptlSecValutionList.addAll(ptlSecValutionListWithTd);//资产估值list.add（通道资产）

		// 将资产估值list.账套代码保存到账套列表
		Set<String> booksetIdSet = new HashSet<String>();
		for (PtlSecValutionWithOtherFieldDto dto : ptlSecValutionList) {
			booksetIdSet.add(dto.getBooksetId());
		}

		// 定义【其他应收款余额列表】
		List<BlanceDto> BlanceDtoList = new ArrayList<BlanceDto>();

		// 1.3 账套投资资产 待清算资金处理
		for (String booksetIdNow : booksetIdSet) {
			// 调用共通【账务类共通方法】.科目余额查询-期待方向
			SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
			//ADD JINMAODONG START 20190909 查询待清算资金时不能使用分支序号，否则查不到
			searchDao.setBranchHandleFlag("01");


			searchDao.setHappenDate(happenDate);
			searchDao.setBookDate(bookDate);
			searchDao.setBooksetId(booksetIdNow);
			searchDao.setBaseSubjectNatureList(new ArrayList<String>(
				Arrays.asList(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(),
					DictCodeBaseSubjectNatrueNew.应收利息_资金.getValue(),DictCodeBaseSubjectNatrueNew.应收利息_融出.getValue(),
					DictCodeBaseSubjectNatrueNew.其他应付款_本金.getValue(), DictCodeBaseSubjectNatrueNew.其他应付款_利息.getValue(),
					DictCodeBaseSubjectNatrueNew.其他应付款期权费.getValue(),DictCodeBaseSubjectNatrueNew.其他应收款期权费.getValue())));
			List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(searchDao,
				DictCodeList.余额方向_借方.getValue());

			// 返回值塞入【其他应收款余额列表】
			if (listSubjectBalance != null && !listSubjectBalance.isEmpty()) {
				for (SubjectBalance dto : listSubjectBalance) {
					// 2019年10月12日 魏鹏：股票是jixu生成，头寸生成中不生成股票
					if(DictCodeListExt1.金融产品类型_股票.getValue().equals(dto.getBusiDetailType())){
						continue;
					}
					BlanceDto blanceDto = new BlanceDto();
					blanceDto.setBooksetId(booksetIdNow);
					blanceDto.setBookDetailId(dto.getBokDetailId());
					blanceDto.setBusiId(dto.getBusiId());
					blanceDto.setCcy(dto.getOCcy());
					blanceDto.setDqskBlance(dto.getBalance());//其他应收款余额列表.待清算资金 = 返回值.原币余额
					blanceDto.setDqskBlanceForBccy(dto.getbAmt());//其他应收款余额列表.待清算资金_本位币 = 返回值.本位币余额

					BlanceDtoList.add(blanceDto);
				}
			}

			// 查询T+1未清算交易
           /* Map<String, Object> sqlMapTOne = new HashMap<String, Object>();
            sqlMapTOne.put("booksetId", booksetIdNow);
            sqlMapTOne.put("cDate", bookDate);
            List<BlanceDto> wqskList = positionMakeWithoutDispatchMapper.getWqsDeal(sqlMapTOne);

            // 值塞入【其他应收款余额列表】
            if (wqskList != null && !wqskList.isEmpty()) {
                BlanceDtoList.addAll(wqskList);
            }*/
		}

		// 1.4 塞入组合资产估值数据

		// 记录 账务明细代码是不存在在【资产估值list】中的数据
		List<BlanceDto> outOfDate = new ArrayList<BlanceDto>();

		// 循环【其他应收款余额列表】
		for (BlanceDto blanceDto : BlanceDtoList) {

			// 账务明细代码是否包含在【资产估值list】中，默认为false
			Boolean isHave = false;

			// 循环【资产估值list】
			for (PtlSecValutionWithOtherFieldDto dto : ptlSecValutionList) {
				// 账务明细代码相同的
				if (dto.getBokDetailId().equals(blanceDto.getBookDetailId()) && dto.getBooksetId().equals(blanceDto.getBooksetId())) {
					// 资产估值list.待清算资金 = 资产估值list.待清算资金 + 其他应收余额列表.待清算资金
					dto.setDelayPayAmt(ArithmeticUtil.doAdd(dto.getDelayPayAmt(), blanceDto.getDqskBlance()));
					// 资产估值list.待清算资金_本位币 += 其他应收余额列表.待清算资金_本位币
					dto.setDelayPayAmtB(ArithmeticUtil.doAdd(dto.getDelayPayAmtB(), blanceDto.getDqskBlanceForBccy()));

					// 包含在【资产估值list】中
					isHave = true;
				}
			}

			// 记录 账务明细代码是不存在在【资产估值list】中的数据
			if (!isHave) {
				outOfDate.add(blanceDto);
			}
		}

		// List<PtlSecValutionWithOtherFieldDto> 转换为List<PtlSecValution> 塞入【资产估值】list中
		for (PtlSecValutionWithOtherFieldDto dto : ptlSecValutionList) {
			PtlSecValution ptlSecValution = new PtlSecValution();
			BeanUtils.copyProperties(dto, ptlSecValution);
			//add jinmaodong 2019-08-23 增加生成方式-批处理生成
			ptlSecValution.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());
			result.add(ptlSecValution);
		}

		// (其他应收款余额列表.账务明细代码 NOT IN 资产估值list.账务明细代码)打的数据单独存在【组合资产估值】中
		if (outOfDate != null && outOfDate.size() > 0) {
			// 根据账套获取对应的组合信息
			List<PortfolioIdInfoDto> portfolioIdList = positionMakeWithoutDispatchMapper.getPortfolioId(outOfDate,
				DictCodeListExt1.金融产品类型_投资组合.getValue());

			// 循环账套处理
			for (PortfolioIdInfoDto vo : portfolioIdList) {

				// 获取该账套下数据中包含的业务明细代码
				Set<String> busIdSets = new HashSet<>();
				for (BlanceDto out : outOfDate) {
					if (vo.getBooksetId().equals(out.getBooksetId())) {
						busIdSets.add(out.getBusiId());
					}
				}

				// 根据业务明细代码set组装【资产估值】数据
				for (String busId : busIdSets) {
					PtlSecValution ptlSecValution = new PtlSecValution();
					ptlSecValution.setCdate(bookDate);
					ptlSecValution.setPortfolioId(vo.getPortfolioId());
					ptlSecValution.setSecAcctId("999999");
					ptlSecValution.setFinprodId(busId);
					ptlSecValution.setInvAim("999999");
					//add jinmaodong 2019-08-23 增加生成方式-几许生成
					ptlSecValution.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());

					// 累加多余数据中，当前账套和业务明细代码下的待清算资金 作为【待清算资金】
					for (BlanceDto out : outOfDate) {
						if (StringUtil.equals(vo.getBooksetId(),out.getBooksetId()) && StringUtil.equals(busId,out.getBusiId())) {
							ptlSecValution.setCcy(out.getCcy());// 币种
							ptlSecValution.setDelayPayAmt(
								ArithmeticUtil.doAdd(ptlSecValution.getDelayPayAmt(), out.getDqskBlance()));// 待清算资金
							// 待清算资金_本位币 += 待清算资金_本位币
							ptlSecValution.setDelayPayAmtB(ArithmeticUtil.doAdd(ptlSecValution.getDelayPayAmtB(), out.getDqskBlanceForBccy()));
						}
					}

					// 添加到【资产估值】list中
					result.add(ptlSecValution);
				}

			}
		}

		return result;
	}

	/**
	 * 计算回购到期利息
	 * @param shareAmt
	 * @param getmTradeAmt
	 * @param prin
	 * @return
	 */
	private BigDecimal getRepoInt(BigDecimal shareAmt, BigDecimal mTradeAmt, BigDecimal prin) {
		// 变量.占比到期金额 = 参数.持仓份额 * 参数.标的信息.到期金额/ 参数.标的信息.名义本金
		BigDecimal 占比到期金额 = ArithmeticUtil.divide(ArithmeticUtil.multiply(shareAmt, mTradeAmt), prin);
		// 累计应计利息 = (变量.占比到期金额 - 参数.持仓份额)
		BigDecimal 累计应计利息 = ArithmeticUtil.doSub(占比到期金额, shareAmt);
		return ArithmeticUtil.round(累计应计利息, NumberEnum.MONEY);
	}

	/**
	 * 查询组合费用头寸【PTL_FEE_POSITION】
	 *
	 * @param sqlMap
	 * @param bookDate
	 * @param happenDate
	 * @return
	 */
	public List<PtlFeePosition> getPtlFeePositionList(Map<String, Object> sqlMap, Date bookDate, Date happenDate) {

		// 4.1 查询所有账套组合信息
		List<BookSetInfoDto> booksetInfoList = positionMakeWithoutDispatchMapper.getBookSetInfo(sqlMap);
		// 4.2 查询基础科目性质
		List<String> subjectNatureList = accountingCommonService.getValSubjectNatureList(
			DictCodeListNatureGroup.分组代码_投资端费用科目查询.getValue());
		// 4.3 查询基础科目性质对应费用类型
		// 组装MAP<基础科目性质，费用信息>
		Map<String, SubjectNatureFeeTypeInfo> subjectNatureFeeTypeMap = new HashMap<String, SubjectNatureFeeTypeInfo>();
		if (subjectNatureList != null && !subjectNatureList.isEmpty()) {
			/**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 start **/
			String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
			/**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 end **/
			List<SubjectNatureFeeTypeInfo> subjectNatureFeeTypeInfoList = positionMakeWithoutDispatchMapper.getSubjectNatureFeeTypeInfo(
				subjectNatureList,accountEntryType);
			// 组装MAP<基础科目性质，费用信息>
			for (SubjectNatureFeeTypeInfo vo : subjectNatureFeeTypeInfoList) {
				subjectNatureFeeTypeMap.put(vo.getBaseSubjectNature(), vo);
			}
		}
		// 4.4 查询所有费用信息
		// 定义 组合费用LIST
		List<PtlFeePosition> ptlFeePositionList = new ArrayList<PtlFeePosition>();
		for (BookSetInfoDto vo : booksetInfoList) {
			// 调用共通【账务类共通方法】.科目余额查询-期待方向
			SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
			searchDao.setHappenDate(happenDate);
			searchDao.setBookDate(bookDate);
			searchDao.setBooksetId(vo.getBooksetId());
			searchDao.setBaseSubjectNatureList(subjectNatureList);
			List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(searchDao,
				DictCodeList.余额方向_贷方.getValue());

			// 定义 MAP<费用类型，组合费费用ENTITY> 组合费用MAP
			Map<String, PtlFeePosition> ptlFeePositionMap = new HashMap<String, PtlFeePosition>();

			// 循环科目余额list
			for (SubjectBalance subVo : listSubjectBalance) {
				// 获取费用信息 = 4.3返回MAP.GET(科目余额ENTIRY.基础科目性质)
				SubjectNatureFeeTypeInfo info = subjectNatureFeeTypeMap.get(subVo.getBaseSubjectNature());
				// 过滤空值
				if (info == null) {
					continue;
				}
				// 定义 费用类型KEY = 获取费用信息.费用类型-获取费用信息.费用场所
				String feeTypeKey = info.getFeeTypeId() + "-" + info.getFeePlace();

				// IF(组合费用MAP.CONTAINSKEY(费用类型KEY))
				if (ptlFeePositionMap.containsKey(feeTypeKey)) {
					// 组合费用MAP.GET(费用类型Key).当日累计应付费用 = 科目余额ENTITY.科目余额 + 组合费用MAP.GET(费用类型Key).当日累计应付费用
					ptlFeePositionMap.get(feeTypeKey).setTdyAccumulPayFee(
						ArithmeticUtil.doAdd(subVo.getBalance(), ptlFeePositionMap.get(feeTypeKey).getTdyAccumulPayFee()));

					// 组合费用MAP.GET(费用类型KEY).当日累计应付费用_本位币 = 科目余额ENTITY.本币余额+ 组合费用MAP.GET(费用类型KEY).当日累计应付费用_本位币
					ptlFeePositionMap.get(feeTypeKey).setTdyAccumulPayFeeB(
						ArithmeticUtil.doAdd(subVo.getbAmt(), ptlFeePositionMap.get(feeTypeKey).getTdyAccumulPayFeeB()));

				}
				else {
					// 定义 组合费用ENTITY
					PtlFeePosition dto = new PtlFeePosition();
					// 组合费用ENTITY.投资组合代码 = 账套ENTIRY.投资组合代码
					dto.setPortfolioId(vo.getPortfolioId());
					// 组合费用ENTITY.日期 = 参数.入账日
					dto.setCdate(bookDate);
					// 组合费用ENTITY.费用类型 = 费用信息 .费用类型
					dto.setPtlFeeType(info.getFeeTypeId());
					// 组合费用ENTITY.费用场所 = 费用信息 .费用场所
					dto.setPtlFeePlace(info.getFeePlace());
					// 组合费用MAP.GET(费用类型Key).当日累计应付费用 = 科目余额ENTITY.科目余额
					dto.setTdyAccumulPayFee(subVo.getBalance());
					// 组合费用MAP.GET(费用类型KEY).当日累计应付费用_本位币  = 科目余额ENTITY.科目余额_本位币
					dto.setTdyAccumulPayFeeB(subVo.getbAmt());
					// 组合费用MAP.PUT(费用类型KEY,组合费用ENTITY)
					ptlFeePositionMap.put(feeTypeKey, dto);
				}
			}

			// 组合费用LIST.ADD(根据组合费用MAP组装 组合费用LIST_TEMP)
			for (String keyNow : ptlFeePositionMap.keySet()) {
				ptlFeePositionList.add(ptlFeePositionMap.get(keyNow));
			}

			Map<String,Object> searchMap = new HashMap<>();
			searchMap.putAll(sqlMap);
			searchMap.put("booksetId", vo.getBooksetId());
			List<PtlFeePosition> resultWithZzs = positionMakeWithoutDispatchMapper.getPtlFeePositionWithZzs(searchMap);
			if (CollectionUtils.isNotEmpty(resultWithZzs)){
				ptlFeePositionList.addAll(resultWithZzs);
			}
		}

		return ptlFeePositionList;
	}

	/**
	 * 查询产品头寸【BOK_POSITION_PRD】
	 *
	 * @param sqlMap
	 * @param bookDate
	 * @param happenDate
	 * @return
	 */
	public List<BokPositionPrd> getBokPositionPrdList(Map<String, Object> sqlMap, Date bookDate, Date happenDate) {

		// 查询数据
		List<BokPositionPrdWithOtherFieldDto> bokPositionPrdWithOtherFieldList = positionMakeWithoutDispatchMapper.getBokPositionPrd(sqlMap);

		// 过滤为Null情况
		if (bokPositionPrdWithOtherFieldList == null || bokPositionPrdWithOtherFieldList.isEmpty()) {
			return null;
		}

		/**ADD BY WUJIE ON 20191024 START REMARK:查询估值小数位设置参数**/
		List<String> codeList = new ArrayList<String>();
		codeList.add(ValDictCodeList.账套配置表参数项_产品费后估值小数位数_估值表展示.getValue());
		codeList.add(ValDictCodeList.账套配置表参数项_产品费后估值小数尾数处理方式_估值表展示.getValue());
		codeList.add(ValDictCodeList.账套配置表参数项_累计单位净值小数位数_估值表展示.getValue());
		codeList.add(ValDictCodeList.账套配置表参数项_累计单位净值小数尾数_估值表展示.getValue());
		codeList.add(ValDictCodeList.账套配置表参数项_7日年化收益率小数位数_估值表展示.getValue());
		codeList.add(ValDictCodeList.账套配置表参数项_7日年化收益率小数尾数_估值表展示.getValue());
		codeList.add(ValDictCodeList.账套配置表参数项_本日年化收益率小数位数_估值表展示.getValue());
		codeList.add(ValDictCodeList.账套配置表参数项_本日年化收益率小数尾数_估值表展示.getValue());
		/**ADD BY WUJIE ON 20191024 END REMARK:查询估值小数位设置参数**/

		/**MODIFY BY WUJIE ON 20191024 START**/
		List<BokPositionPrd> result = new ArrayList<BokPositionPrd>();
		/**MODIFY BY WUJIE ON 20191024 END**/

		// 获取累计应付利润(当日累计未付客户收益)
		for (BokPositionPrdWithOtherFieldDto dto : bokPositionPrdWithOtherFieldList) {
			List<String> bokDetailIds = null;
			if (!"1".equals(dto.getLev())) {
				Map<String, Object> params = new HashMap<>();
				// 账套代码 = DTO.账套代码
				params.put("booksetId", dto.getBooksetId());
				// 级别序号 = DTO.级别序号
				params.put("lev", dto.getLev());
				// 查询分层结构子结构
				List<StayDrawSubprdEntity> sublist = accountingCommonService.querySubBokAcctInfo(params);
				if (CollectionUtils.isNotEmpty(sublist)) {
					bokDetailIds = new ArrayList<String>();
					for (StayDrawSubprdEntity v : sublist) {
						bokDetailIds.add(v.getBokDetailId());
					}
				}
			}

			// 调用共通【账务类共通方法】.科目余额查询-期待方向
			SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
			searchDao.setHappenDate(happenDate);
			searchDao.setBookDate(bookDate);
			searchDao.setBooksetId(dto.getBooksetId());
			searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.应付利润.getValue())));

			searchDao.setBokDetailIdList(bokDetailIds);
			// 收益类型区分,获取科目余额
			List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(searchDao, DictCodeList.余额方向_贷方.getValue());

			// ENTITY.累计应付利润(当日累计未付客户收益) = 共通返回科目余额
			if (listSubjectBalance != null && !listSubjectBalance.isEmpty()) {
				dto.setTdyUnpaidIntincexp(listSubjectBalance.get(0).getbAmt());
			}

			/**ADD BY WUJIE ON 20191024 START REMARK:估值小数位数处理，当日累计净值的处理**/
			Map<String, Object> bokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(dto.getBooksetId(), codeList);

			String showAfterDecDigits = null;
			if(bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_产品费后估值小数位数_估值表展示.getValue())!=null){
				showAfterDecDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_产品费后估值小数位数_估值表展示.getValue());
			}

			String showAfterDecType = null;
			if(bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_产品费后估值小数尾数处理方式_估值表展示.getValue())!=null){
				showAfterDecType = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_产品费后估值小数尾数处理方式_估值表展示.getValue());
			}

			String showTotbounsDecDigits = null;
			if(bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_累计单位净值小数位数_估值表展示.getValue())!=null){
				showTotbounsDecDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_累计单位净值小数位数_估值表展示.getValue());
			}

			String showTotbounsDecType = null;
			if(bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_累计单位净值小数尾数_估值表展示.getValue())!=null){
				showTotbounsDecType = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_累计单位净值小数尾数_估值表展示.getValue());
			}

			String day7YeildDecDigits = null;
			if(bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_7日年化收益率小数位数_估值表展示.getValue())!=null){
				day7YeildDecDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_7日年化收益率小数位数_估值表展示.getValue());
			}
			String day7YeildDecType = null;
			if(bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_7日年化收益率小数尾数_估值表展示.getValue())!=null){
				day7YeildDecType = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_7日年化收益率小数尾数_估值表展示.getValue());
			}

			String tdyYeildDecDigits = null;
			if(bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本日年化收益率小数位数_估值表展示.getValue())!=null){
				tdyYeildDecDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本日年化收益率小数位数_估值表展示.getValue());
			}
			String tdyYeildDecType = null;
			if(bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本日年化收益率小数尾数_估值表展示.getValue())!=null){
				tdyYeildDecDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本日年化收益率小数尾数_估值表展示.getValue());
			}

			//当日单位净值；按照品费后估值小数位数设置处理
			dto.setTdyUnitValue(accountingCommonService.filterByDecTypeAndDigits(dto.getTdyUnitValue(),showAfterDecType, showAfterDecDigits));

			//产品累计单位净值:
			//1.先对查询的【当日累计净值】按照累计单位净值小数位数设置处理
			//2.处理后的【当日累计净值】+当日单位净值，然后再按照累计单位净值小数位数设置处理
			BigDecimal prodTotUnitNet = dto.getTdyUnitValueSum()==null? BigDecimal.ZERO : dto.getTdyUnitValueSum();
			prodTotUnitNet = ArithmeticUtil.doAdd(accountingCommonService.filterByDecTypeAndDigits(prodTotUnitNet, showTotbounsDecType, showTotbounsDecDigits),
				dto.getTdyUnitValue());
			prodTotUnitNet = accountingCommonService.filterByDecTypeAndDigits(prodTotUnitNet,showTotbounsDecType,showTotbounsDecDigits);
			dto.setTdyUnitValueSum(prodTotUnitNet);

			//7日年化收益率:估值表是先乘以100后再按照7日年化收益率小数位数设置处理
			BigDecimal prodYield7 = ArithmeticUtil.multiply((dto.getYield7() == null) ? BigDecimal.ZERO:dto.getYield7(),new BigDecimal(100));
			prodYield7 = accountingCommonService.filterByDecTypeAndDigits(prodYield7,day7YeildDecType, day7YeildDecDigits);
			prodYield7 = ArithmeticUtil.divide(prodYield7, new BigDecimal(100));
			dto.setYield7(prodYield7);

			/**ADD BY WUJIE ON 20191024 END**/

			/** 20191128 WEIPENG:货币型产品本日收益率需要存值  start**/
			//当日收益率:估值表是先乘以100后再按照本日年化收益率小数位数设置处理
			BigDecimal prodTdyYield = ArithmeticUtil.multiply((dto.getTdyYield() == null) ? BigDecimal.ZERO:dto.getTdyYield(),new BigDecimal(100));
			prodTdyYield = accountingCommonService.filterByDecTypeAndDigits(prodTdyYield,tdyYeildDecType, tdyYeildDecDigits);
			prodTdyYield = ArithmeticUtil.divide(prodTdyYield, new BigDecimal(100));
			dto.setTdyYield(prodTdyYield);
			/** 20191128 WEIPENG:货币型产品本日收益率需要存值  end**/

			/**MODIFY BY WUJIE ON 20191024 START**/
			BokPositionPrd bokPositionPrd = new BokPositionPrd();
			BeanUtils.copyProperties(dto, bokPositionPrd);
			result.add(bokPositionPrd);
			/**MODIFY BY WUJIE ON 20191024 END**/
		}

		// List<BokPositionPrdWithOtherFieldDto> 转换为List<BokPositionPrd>
		/**MODIFY BY WUJIE ON 20191024
		 List<BokPositionPrd> result = new ArrayList<BokPositionPrd>();
		 for (BokPositionPrdWithOtherFieldDto dto : bokPositionPrdWithOtherFieldList) {
		 BokPositionPrd bokPositionPrd = new BokPositionPrd();
		 BeanUtils.copyProperties(dto, bokPositionPrd);
		 result.add(bokPositionPrd);
		 }
		 **/
		return result;
	}

	/**
	 * 查询组合估值头寸【PTL_PORTFOLIO_VALUATION】
	 *
	 * @param sqlMap
	 * @param bookDate
	 * @param happenDate
	 * @return
	 */
	public List<PtlPortfolioValuation> getPtlPortfolioValuation(Map<String, Object> sqlMap, Date bookDate, Date happenDate) {
		// 5.1 查询数据
		List<PtlPortfolioValuationWithOtherFieldDto> PtlPortfolioValuationWithOtherFieldList = positionMakeWithoutDispatchMapper.getPtlPortfolioValuation(
			sqlMap);

		// 过滤为Null情况
		if (PtlPortfolioValuationWithOtherFieldList == null || PtlPortfolioValuationWithOtherFieldList.isEmpty()) {
			return null;
		}

		// 5.2 组合当日资产总值设定
		for (PtlPortfolioValuationWithOtherFieldDto dto : PtlPortfolioValuationWithOtherFieldList) {
			// 调用共通获取账套配置表中估值表模板字段
			List<String> paramCodeList = new ArrayList<String>();
			paramCodeList.add(ValDictCodeList.账套配置表参数项_估值表模板.getValue());
			Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(dto.getBooksetId(),
				paramCodeList);
			String valTemplate = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_估值表模板.getValue());

			// 查询【当日资产总值】
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("valTemplate", valTemplate);
			searchMap.put("booksetId", dto.getBooksetId());
			searchMap.put("valDate", dto.getCdate());
			BigDecimal tdyAssetValue = positionMakeWithoutDispatchMapper.getTdyAssetValue(searchMap);
			dto.setTdyAssetValue(tdyAssetValue);
		}

		// List<PtlPortfolioValuationWithOtherFieldDto> 转换为List<PtlPortfolioValuation>
		List<PtlPortfolioValuation> result = new ArrayList<PtlPortfolioValuation>();
		for (PtlPortfolioValuationWithOtherFieldDto dto : PtlPortfolioValuationWithOtherFieldList) {
			PtlPortfolioValuation ptlPortfolioValuation = new PtlPortfolioValuation();
			BeanUtils.copyProperties(dto, ptlPortfolioValuation);
			result.add(ptlPortfolioValuation);
		}

		return result;
	}

	/**
	 * 获取产品费用头寸【BOK_POSITION_PRDFEE】
	 *
	 * @param sqlMap
	 * @param bookDate
	 * @param happenDate
	 * @return
	 */
	private List<BokPositionPrdfee> getBokPositionPrdfeeList(Map<String, Object> sqlMap, Date bookDate, Date happenDate) {

		// 定义产品费用头寸返回值集合。
		List<BokPositionPrdfee> result = new ArrayList<BokPositionPrdfee>();

		// 2.1 非通道费用头寸
		List<BokPositionPrdfeeWithOtherFieldDto> bokPositionPrdfeeWithOtherFieldListWithNoTd = positionMakeWithoutDispatchMapper.getBokPositionPrdfee(
			sqlMap);

		// 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->费用场景配置查询
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isCalculation", DictCodeList.是否_是.getValue());
		List<BokFeeTableSet> feeList = accountingCommonService.getFeeTableSetQuery(map);

		// 组装MAP<费用类型，基础科目性质>
		Map<String, String> sjtMap = new HashMap<String, String>();
		if (feeList != null && !feeList.isEmpty()) {
			for (BokFeeTableSet feeDto : feeList) {
				sjtMap.put(feeDto.getFeeTypeId(), feeDto.getBaseSubjectNature());
			}
		}

		// 循环产品费用头寸，塞入当日累计未付客户收益
		for (BokPositionPrdfeeWithOtherFieldDto dto : bokPositionPrdfeeWithOtherFieldListWithNoTd) {
			List<String> bokDetailIds = null;
			if (!"1".equals(dto.getLev())) {
				Map<String, Object> params = new HashMap<>();
				// 账套代码 = DTO.账套代码
				params.put("booksetId", dto.getBooksetId());
				// 级别序号 = DTO.级别序号
				params.put("lev", dto.getLev());
				// 查询分层结构子结构
				List<StayDrawSubprdEntity> sublist = accountingCommonService.querySubBokAcctInfo(params);
				if (CollectionUtils.isNotEmpty(sublist)) {
					bokDetailIds = new ArrayList<String>();
					for (StayDrawSubprdEntity v : sublist) {
						bokDetailIds.add(v.getBokDetailId());
					}
				}
			}
			// 调用共通【账务类共通方法】.科目余额查询-期待方向
			SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
			searchDao.setHappenDate(happenDate);
			searchDao.setBookDate(bookDate);
			searchDao.setBooksetId(dto.getBooksetId());
			searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(sjtMap.get(dto.getFeeType()))));
			searchDao.setBokDetailIdList(bokDetailIds);

			List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(searchDao,
				DictCodeList.余额方向_贷方.getValue());

			// ENTITY.累计应付利润(当日累计未付客户收益) = 共通返回科目余额
			if (listSubjectBalance != null && !listSubjectBalance.isEmpty()) {
				dto.setTdyUnpaidIntincexp(listSubjectBalance.get(0).getBalance());
			}
		}

		// 2.2 通道费用、增值税头寸
		List<BokPositionPrdfeeWithOtherFieldDto> bokPositionPrdfeeWithOtherFieldListWithTd = positionMakeWithoutDispatchMapper
			.getBokPositionPrdfeeWithTd(sqlMap);

		// 通道费用、增值税头寸 集合
		List<BokPositionPrdfeeWithOtherFieldDto> bokPositionPrdfeeWithOtherFieldListWithTdAll = new ArrayList<BokPositionPrdfeeWithOtherFieldDto>();

		// LOOP 变量.通道账套信息
		for (BokPositionPrdfeeWithOtherFieldDto dto : bokPositionPrdfeeWithOtherFieldListWithTd) {
			// 定义 变量.通道费计提信息 =
			// 调用共通方法查询：凭证最新账务查询（场景代码=“1701”，发生开始日=“参数.发生日”，发生结束日=“参数.发生日”，基础科目性质=“应付交易费费”，入账日=DTO.入账日，账套代码=变量.通道账套信息.子账套代码）
			List<String> listBaseSubjectNature = new ArrayList<>();
			listBaseSubjectNature.add(DictCodeBaseSubjectNatrueNew.应付交易费用_费用.getValue());
			List<BookLatestInfoDto> listBookLatestInfo = accountingCommonService.getBookLatestInfo(dto.getBooksetId(),
				bookDate, null, listBaseSubjectNature, DictCodeListTableId.会计场景代码_通道费_计提.getValue(), null, null, null,
				happenDate, happenDate);
			// 变量.每日新增费用
			BigDecimal varTdyIntincexpAdd = BigDecimal.ZERO;
			// 变量.每日新增费用_本位币
			BigDecimal varTdyIntincexpAddForB = BigDecimal.ZERO;
			if (CollectionUtils.isNotEmpty(listBookLatestInfo)) {
				for (BookLatestInfoDto bookLatestInfoDto : listBookLatestInfo) {
					// 变量.每日新增费用 = 变量.每日新增费用 + 变量.通道费计提信息列表.原币发生额
					varTdyIntincexpAdd = ArithmeticUtil.doAdd(varTdyIntincexpAdd, bookLatestInfoDto.getbHappenAmt());
					//变量.每日新增费用_本位币 = 变量.每日新增费用_本位币 + 变量.通道费计提信息列表.本位币发生额
					varTdyIntincexpAddForB = ArithmeticUtil.doAdd(varTdyIntincexpAddForB, bookLatestInfoDto.getbHappenAmt());
				}
			}

//			// 通道费、通道增值税
//			Map<String, Object> nowMap = new HashMap<String, Object>();
//			nowMap.put("booksetId", dto.getBooksetId());
//			nowMap.put("booksetDate", dto.getCdate());
//			List<BokPositionPrdfee> resultWithTdZzs = positionMakeWithoutDispatchMapper.getBokPositionPrdfeeWithTdZzs(nowMap);
//
//			// LOOP 上述结果->费用明细
//			if (CollectionUtils.isNotEmpty(resultWithTdZzs)) {
//				for (BokPositionPrdfee bokPositionPrdfee : resultWithTdZzs) {
//					BokPositionPrdfeeWithOtherFieldDto newdto = new BokPositionPrdfeeWithOtherFieldDto();
//					BeanUtil.copyPropertiesIgnoreNull(newdto,dto);
//					// ENTITY.当日累计未付费用 = 费用明细.当日累计未付费用
//					newdto.setTdyUnpaidIntincexp(bokPositionPrdfee.getTdyUnpaidIntincexp());
//					// ENTITY.费用类型 = 费用明细.费用类型
//					newdto.setFeeType(bokPositionPrdfee.getFeeType());
//
//					// ENTITY.当日新增费用
//					if (DictCodeListExt1.一次性费用类型_通道费.getValue().equals(bokPositionPrdfee.getFeeType())) {
//						// ENTITY.当日新增费用 = 变量.每日新增费用
//						newdto.setTdyIntincexpAdd(varTdyIntincexpAdd);
//						// ENTITY.当日新增费用_本位币 = 变量.每日新增费用_本位币
//						newdto.setTdyIntincexpAddB(varTdyIntincexpAddForB);
//					} else {
//						// ENTITY.当日新增费用 = null
//						newdto.setTdyIntincexpAdd(null);
//						// ENTITY.当日新增费用_本位币 = null
//						newdto.setTdyIntincexpAddB(null);
//					}
//
//					// 费用头寸.add（ENTITY）
//					bokPositionPrdfeeWithOtherFieldListWithTdAll.add(newdto);
//				}
//			}
		}

		// 2.3 增值税费用
		// 2.3.1 非通道增值税
		List<BokPositionPrdfee> resultWithZzs = positionMakeWithoutDispatchMapper.getBokPositionPrdfeeWithZzs(sqlMap);
		if (resultWithZzs != null && !resultWithZzs.isEmpty()) {
			result.addAll(resultWithZzs);
		}

		// List<BokPositionPrdfeeWithOtherFieldDto> 转换为List<BokPositionPrdfee>
		List<BokPositionPrdfeeWithOtherFieldDto> allList = new ArrayList<BokPositionPrdfeeWithOtherFieldDto>();
		allList.addAll(bokPositionPrdfeeWithOtherFieldListWithNoTd);// 非通道费用头寸 集合
		allList.addAll(bokPositionPrdfeeWithOtherFieldListWithTdAll);//通道费用、增值税头寸 集合
		for (BokPositionPrdfeeWithOtherFieldDto dto : allList) {
			BokPositionPrdfee bokPositionPrdfee = new BokPositionPrdfee();
			BeanUtils.copyProperties(dto, bokPositionPrdfee);

			//与储蜜确认：[产品费用头寸]中当日新增费用 = 当日新增费用_本位币
			bokPositionPrdfee.setTdyIntincexpAdd(dto.getTdyIntincexpAddB());

			result.add(bokPositionPrdfee);
		}

		//手工凭证
		sqlMap.put("cdFlag",DictCodeList.借贷方向_借.getValue());
		sqlMap.put("C03", DictCodeListExt1.现金流二级类型_固定管理费.getValue());
		sqlMap.put("C04", DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
		sqlMap.put("C05", DictCodeListExt1.现金流二级类型_托管费.getValue());
		sqlMap.put("C06", DictCodeListExt1.现金流二级类型_销售费.getValue());
		sqlMap.put("C09", DictCodeListExt1.现金流二级类型_外包服务费.getValue());
		sqlMap.put("C10", DictCodeListExt1.现金流二级类型_代销费.getValue());
		sqlMap.put("subjectType02", DictCodeListExt2.科目类别_负债类.getValue());
		sqlMap.put("finprodTypeF26", DictCodeListExt1.金融产品类型_通道.getValue());
		sqlMap.put("booksetType02", DictCodeList.账套类别_理财账套.getValue());
		sqlMap.put("bookType03", DictCodeList.凭证类型_手工.getValue());
		List<BokPositionPrdfee> resultManual = positionMakeWithoutDispatchMapper.getManualList(sqlMap);
		if (resultManual != null && !resultManual.isEmpty()) {
			result.addAll(resultManual);
		}

		return result;
	}

	/**
	 * 调用共通查询：科目余额查询-期待方向
	 *
	 * @param booksetId 账套代码
	 * @param happenDate 发生日
	 * @param bookDate 入账日
	 * @param expBalanceDirect 期待余额方向
	 * @param baseSubjectNature 基础科目性质
	 * @return SubjectBalance 科目余额dto
	 * @see
	 * @since 1.0
	 */
	private List<SubjectBalance> getSubjectBalance(String booksetId, Date happenDate, Date bookDate,
												   String expBalanceDirect, List<String> baseSubjectNatureList) {
		// 设值科目余额查询参数Dto
		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		searchDto.setBooksetId(booksetId);
		searchDto.setHappenDate(happenDate);
		searchDto.setBookDate(bookDate);
		searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
		// 调用公用方法：【科目余额查询-期待方向 】,获取科目余额信息
		return accountingCommonService.getSubjectBalanceExpectDirection(
			searchDto, expBalanceDirect);
	}

	/**
	 * 查询组合资产应收款头寸LIST 【PTL_SEC_RECEIVABLES】
	 * @param sqlMap
	 * @param bookDate
	 * @param happenDate
	 * @return
	 */
	private List<PtlSecReceivables> getPtlSecReceivablesList(Map<String, Object> sqlMap, Date bookDate, Date happenDate) {
		// 定义产品费用头寸返回值集合。
		List<PtlSecReceivables> result = new ArrayList<PtlSecReceivables>();
		//        Map<String, Object> param = new HashMap<>();
		//        param.put("happenDate", happenDate);
		//        param.put("bookDate", bookDate);
		//删除已有头寸  add by xr  删除逻辑当调整交易 重新跑批时，此时若reader返回为空会导致旧数据无法删除 start
		List<PtlSecReceivables> listInsert=new ArrayList<PtlSecReceivables>();
		List<String> prodidlist=(List<String>) sqlMap.get("booksetIds");
		if(!StringUtil.isEmptyList(prodidlist)){
			for (String boksetid:prodidlist){
				PtlSecReceivables secr=new PtlSecReceivables();
				secr.setPortfolioId(boksetid);
				listInsert.add(secr);
			}
		}
		positionMakeWithoutDispatchMapper.deletePtlSecReceivables(listInsert, bookDate);
		//删除已有头寸  add by xr end
		//1.查询应收款科目余额信息
		/**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 start **/
		String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
		sqlMap.put("accountEntryType",accountEntryType);
		/**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 end **/
		List<PtlSecReceivablesDto> dealList = positionMakeWithoutDispatchMapper.getPtlSecReceivablesList(sqlMap);
		//IF 变量.应收款科目余额List IS NULL OR 变量.应收款科目余额List.SIZE==0
		if(StringUtil.isEmptyList(dealList)) {
			return result;
		}
		//2.查询罚息金额、累计计提罚息金额
		//LOOP 变量.应收款科目余额List 应收款科目余额ENTITY
		for(PtlSecReceivablesDto dealDto : dealList) {
			//调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
			// 设值科目余额查询参数Dto
			SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
			//发生日	参数.发生日
			searchDto.setHappenDate(happenDate);
			//入账日	参数.发生日
			searchDto.setBookDate(bookDate);
			//账务明细代码	应收款科目余额ENTITY.账务明细代码
			searchDto.setBokDetailId(dealDto.getBokDetailId());
			//账套代码	应收款科目余额ENTITY.账套代码
			searchDto.setBooksetId(dealDto.getBooksetId());
			//基础科目性质"逾期资产应计利息"
			searchDto.setBaseSubjectNatureList(Arrays.asList(DictCodeBaseSubjectNatrueNew.逾期资产应计利息.getValue()));
			//分支序号	应收款科目余额ENTITY.分支序号
			searchDto.setBranch(dealDto.getBranch());
			//期待余额方向  借方
			List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(searchDto,
				DictCodeList.余额方向_借方.getValue());
			// 定义 变量.应收罚息余额 = 返回值.科目余额.原币余额
			BigDecimal ysfxBalance = BigDecimal.ZERO;
			if(!StringUtil.isEmptyList(listSubjectBalance)) {
				ysfxBalance = listSubjectBalance.get(0).getBalance();
			}
			//IF 应收款科目余额ENTITY.原币科目余额 =0 AND 变量.应收罚息余额  = 0
			if((dealDto.getoAmtHappenDate() == null || dealDto.getoAmtHappenDate().compareTo(BigDecimal.ZERO) == 0)
			   && (ysfxBalance == null || ysfxBalance.compareTo(BigDecimal.ZERO) == 0)) {
				continue;
			}
			BigDecimal ljfx = this.getPtlSecReceivablesSum(DictCodeBaseSubjectNatrueNew.逾期资产应计利息.getValue(), DictCodeList.借贷方向_借.getValue(),
				happenDate, dealDto.getBooksetId(), dealDto.getBokDetailId(), dealDto.getBranch(), dealDto.getoCcy());
			result.add(getPtlSecReceivables(dealDto, happenDate, ljfx, ysfxBalance));
		}

		return result;
	}

	/**
	 * 设置组合资产应收款头寸 dto
	 * @param dealDto
	 * @param happenDate
	 * @param ljfx
	 * @param ysfxBalance
	 * @return
	 */
	private PtlSecReceivables getPtlSecReceivables(PtlSecReceivablesDto dealDto, Date happenDate, BigDecimal ljfx, BigDecimal ysfxBalance) {
		PtlSecReceivables ptlSecReceivables = new PtlSecReceivables();
		//组合代码	= 应收款科目余额ENTITY.组合代码
		ptlSecReceivables.setPortfolioId(dealDto.getPortfolioId());
		//日期 = 参数.发生日
		ptlSecReceivables.setCdate(happenDate);
		//证券管理账户/通道代码	= 应收款科目余额ENTITY.通道代码
		ptlSecReceivables.setSecAcctId(dealDto.getChannelId());
		//金融产品代码 = 应收款科目余额ENTITY.金融产品代码
		ptlSecReceivables.setFinprodId(dealDto.getFinprodId());
		//分支序号 = 应收款科目余额ENTITY.分支序号
		ptlSecReceivables.setBranch(dealDto.getBranch());
		//投资目的	= 应收款科目余额ENTITY.投资目的
		ptlSecReceivables.setInvAim(dealDto.getInvAim());
		//原币	= 应收款科目余额ENTITY.原币
		ptlSecReceivables.setCcy(dealDto.getoCcy());
		//应收款	= 应收款科目余额ENTITY.原币科目余额
		ptlSecReceivables.setReceAmt(dealDto.getoAmtHappenDate());
		//罚息金额	= 变量.应收罚息余额
		ptlSecReceivables.setPenaltyIntAmt(ysfxBalance);
		//累计计提罚息金额 = 变量.累计计提罚息金额
		ptlSecReceivables.setPenaltyIntTotAmt(ljfx);
		//生成方式	= 数据字典：生成方式.批处理
		ptlSecReceivables.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());
		return ptlSecReceivables;
	}
	/**
	 * 累计计提罚息金额
	 * @param baseSubjectNature
	 * @param cdFlag
	 * @param happenDate
	 * @param booksetId
	 * @param bokDetailId
	 * @param branch
	 * @param oCcy
	 * @return
	 */
	private BigDecimal getPtlSecReceivablesSum(String baseSubjectNature, String cdFlag, Date happenDate, String booksetId,
											   String bokDetailId, Long branch, String oCcy) {
		Map<String, Object> ljfxMap = new HashMap<>();
		ljfxMap.put("baseSubjectNature", baseSubjectNature);/** 逾期资产应计利息**/
		ljfxMap.put("cdFlag", cdFlag);/**借贷方向 =	数据字典：借贷方向.借方**/
		ljfxMap.put("happenDate", happenDate);/**发生日期 参数.发生日**/
		ljfxMap.put("booksetId", booksetId);/**账套代码 =	应收款科目余额ENTITY.账套代码**/
		ljfxMap.put("bokDetailId", bokDetailId);/**账务明细代码 =应收款科目余额ENTITY.账务明细代码**/
		ljfxMap.put("branch", branch);/**分支序号 = 应收款科目余额ENTITY.分支序号**/
		ljfxMap.put("oCcy", oCcy);/**原币 = 应收款科目余额ENTITY.原币**/
		BigDecimal ljfx = positionMakeWithoutDispatchMapper.getPtlSecReceivablesSum(ljfxMap);

		return ljfx;
	}

	/**
	 * 调用共通查询发生日前一日余额 调用共通查询：科目余额查询-期待方向，查询现金类科目余额
	 *
	 * @param readerEntity reader传递的参数
	 * @param expBalanceDirect 期待余额方向
	 * @param baseSubjectNature 基础科目性质
	 * @param taskPri 任务优先级
	 * @return
	 * @see
	 * @since 1.0
	 */

	private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId,Date happenDate, Date bookDate,
											 String expBalanceDirect, String baseSubjectNature) {
		// 初始化返回结果
		SubjectBalance balance = new SubjectBalance();

		// 塞入要查询的基础科目性质
		List<String> baseSubjectNatureList = null;
		if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
			baseSubjectNatureList = new ArrayList<String>();
			baseSubjectNatureList.add(baseSubjectNature);
		}

		// 设值科目余额查询参数Dto
		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		searchDto.setBooksetId(booksetId);
		searchDto.setBokDetailId(bokDetailId);
		searchDto.setHappenDate(happenDate);
		searchDto.setBookDate(bookDate);
		searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
		// 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
		List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
			searchDto, expBalanceDirect);

		// 获取科目余额
		if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
			balance = subjectBalanceList.get(0);
		}

		return balance;
	}

	/**
	 * 调用共通查询发生日前一日余额 调用共通查询：科目余额查询-期待方向，查询现金类科目余额
	 *
	 * @param readerEntity reader传递的参数
	 * @param expBalanceDirect 期待余额方向
	 * @param baseSubjectNature 基础科目性质
	 * @param taskPri 任务优先级
	 * @return
	 * @see
	 * @since 1.0
	 */

	private SubjectBalance getSubjectBalanceByNatures(String booksetId, String bokDetailId,Date happenDate, Date bookDate,
											 String expBalanceDirect, List<String> baseSubjectNatureList) {
		// 初始化返回结果
		SubjectBalance balance = new SubjectBalance();

		// 设值科目余额查询参数Dto
		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		searchDto.setBooksetId(booksetId);
		searchDto.setBokDetailId(bokDetailId);
		searchDto.setHappenDate(happenDate);
		searchDto.setBookDate(bookDate);
		searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
		// 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
		List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
			searchDto, expBalanceDirect);

		// 获取科目余额
		if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
			balance = subjectBalanceList.get(0);
		}

		return balance;
	}
}
