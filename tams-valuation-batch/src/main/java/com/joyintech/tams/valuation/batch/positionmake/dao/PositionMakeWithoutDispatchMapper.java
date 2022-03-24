package com.joyintech.tams.valuation.batch.positionmake.dao;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.batch.positionmake.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 头寸生成批处理 - 数据获取
 *
 * 【说明：】
 * 【PositionMakeMapper：走整体任务走的，依赖BOK_BATCH_TASK_TODO表，当前已经不用。(2019.08.05后不再与PositionMakeWithoutDispatchMapper同步更新)】
 * 【PositionMakeWithoutDispatchMapper：可走单独任务，不依赖BOK_BATCH_TASK_TODO表，当前正在使用。】
 *
 * @author Administrator
 *
 */
@Repository
public interface PositionMakeWithoutDispatchMapper {

	/**
	 * 获取【组合资产估值】数据 - 账套直投资产信息
	 * @param param
	 * @return
	 */
	List<PtlSecValutionWithOtherFieldDto> getPtlSecValution(Map<String, Object> param);

	/**
	 * 获取【组合资产估值】数据 - 账套直投资产信息
	 * @param param
	 * @return
	 */
	List<PtlSecValutionWithOtherFieldDto> getPtlSecValutionF20(Map<String, Object> param);
	
	/**
	 * 获取【组合资产估值】数据 - 账套通道投资资产信息
	 * @param param
	 * @return
	 */
	List<PtlSecValutionWithOtherFieldDto> getPtlSecValutionWithTd(Map<String, Object> param);

	/**
	 * 获取【产品费用头寸】数据 - 非通道费用头寸
	 * @param param
	 * @return
	 */
	List<BokPositionPrdfeeWithOtherFieldDto> getBokPositionPrdfee(Map<String, Object> param);

	/**
	 * 获取【产品费用头寸】数据 - 通道费用、增值税头寸
	 * @param param
	 * @return
	 */
	List<BokPositionPrdfeeWithOtherFieldDto> getBokPositionPrdfeeWithTd(Map<String, Object> param);

	/**
	 * 获取【产品头寸】数据
	 * @param param
	 * @return
	 */
	List<BokPositionPrdWithOtherFieldDto> getBokPositionPrd(Map<String, Object> param);

	/**
	 * 获取【组合估值头寸】数据
	 * @param param
	 * @return
	 */
	List<PtlPortfolioValuationWithOtherFieldDto> getPtlPortfolioValuation(Map<String, Object> param);

	/**
	 * 获取所有账套组合信息
	 * @param param
	 * @return
	 */
	List<BookSetInfoDto> getBookSetInfo(Map<String, Object> param);

	/**
	 * 获取基础科目性质对应费用类型
	 * @param subjectNatureList
	 * @return
	 */
	List<SubjectNatureFeeTypeInfo> getSubjectNatureFeeTypeInfo(
            @Param("list")
                    List<String> subjectNatureList, @Param("accountEntryType")String accountEntryType);



	/**
	 * 删除【组合资产估值】数据
	 * @param ptlSecValutionList
	 * @return
	 */
	int deletePtlSecValution(@Param("list")List<PtlSecValution> ptlSecValutionList,@Param("cdate")Date cdate,@Param("genType")String genType);
	/**
	 * 插入【组合资产估值】数据
	 * @param ptlSecValutionList
	 * @return
	 */
	int insertPtlSecValution(
            @Param("list")
                    List<PtlSecValution> ptlSecValutionList);


	/**
	 * 删除【产品费用头寸】数据
	 * @param bokPositionPrdfeeList
	 * @param cdate
	 * @return
	 */
	int deleteBokPositionPrdfee(
            @Param("list")
                    List<BokPositionPrdfee> bokPositionPrdfeeList,
            @Param("cdate")
                    Date cdate);

	/**
	 * 插入【产品费用头寸】数据
	 * @param bokPositionPrdfeeList
	 * @return
	 */
	int insertBokPositionPrdfee(
            @Param("list")
                    List<BokPositionPrdfee> bokPositionPrdfeeList);


	/**
	 * 删除【产品头寸】数据
	 * @param bokPositionPrdList
	 * @param cdate
	 * @return
	 */
	int deleteBokPositionPrd(
            @Param("list")
                    List<BokPositionPrd> bokPositionPrdList,
            @Param("cdate")
                    Date cdate);
	/**
	 * 插入【产品头寸】数据
	 * @param bokPositionPrdList
	 * @return
	 */
	int insertBokPositionPrd(
            @Param("list")
                    List<BokPositionPrd> bokPositionPrdList);


	/**
	 * 删除【组合费用头寸】数据
	 * @param ptlFeePositionList
	 * @param cdate
	 * @return
	 */
	int deletePtlFeePosition(
            @Param("list")
                    List<PtlFeePosition> ptlFeePositionList,
            @Param("cdate")
                    Date cdate);

	/**
	 * 插入【组合费用头寸】数据
	 * @param ptlFeePositionList
	 * @return
	 */
	int insertPtlFeePosition(
            @Param("list")
                    List<PtlFeePosition> ptlFeePositionList);


	/**
	 * 删除【组合估值头寸】数据
	 * @param ptlPortfolioValuationList
	 * @param cdate
	 * @return
	 */
	int deletePtlPortfolioValuation(
            @Param("list")
                    List<PtlPortfolioValuation> ptlPortfolioValuationList,
            @Param("cdate")
                    Date cdate);
	/**
	 * 插入【组合估值头寸】数据
	 * @param ptlPortfolioValuationList
	 * @return
	 */
	int insertPtlPortfolioValuation(
            @Param("list")
                    List<PtlPortfolioValuation> ptlPortfolioValuationList);

	/**
	 * 获取【组合估值头寸】中的【当日资产总值】
	 * @param param
	 * @return
	 */
	BigDecimal getTdyAssetValue(Map<String, Object> param);

	/**
	 * 获取产品费用头寸 - 非通道增值税
	 * @param param
	 * @return
	 */
	List<BokPositionPrdfee> getBokPositionPrdfeeWithZzs(Map<String, Object> param);

	/**
	 * 获取产品费用头寸 - 通道费、通道增值税
	 * @param param
	 * @return
	 */
	List<BokPositionPrdfee> getBokPositionPrdfeeWithTdZzs(Map<String, Object> param);

	/**
	 * 获取组合增值税
	 * @param param
	 * @return
	 */
	List<PtlFeePosition> getPtlFeePositionWithZzs(Map<String, Object> param);
	/**
	 * 查询T+1未清算交易
	 * @param param
	 * @return
	 */
	List<BlanceDto> getWqsDeal(Map<String, Object> param);

	/**
	 * 获取投资组合
	 * @param list
	 * @param finprodType
	 * @return
	 */
	List<PortfolioIdInfoDto> getPortfolioId(
            @Param("list")
                    List<BlanceDto> list,
            @Param("finprodType")
                    String finprodType);
	
	/**
	 * 获取【组合资产估值】数据 - 成本信息
	 * @param param
	 * @return
	 */
	List<PtlSecValutionWithOtherFieldDto> getCostInfo(Map<String, Object> param);


	/**
	 * 查询[LIST-价差信息C]
	 * @param param
	 * @return
	 */
	List<JcInfoDto> getJcInfoDtoList(Map<String, Object> param);
	
	/**
	 * 查询本次待处理账套
	 * @param param
	 * @return
	 */
	List<PositionMakeReaderResult> getBooksets(Map<String, Object> param);

	/**
	 * 查询[LIST-应收款科目余额信息]
	 * @param param
	 * @return
	 */
	List<PtlSecReceivablesDto>  getPtlSecReceivablesList(Map<String, Object> param);

	/**
	 * 累计计提罚息金额
	 * @param map
	 * @return
	 */
	BigDecimal getPtlSecReceivablesSum(Map<String, Object> map);

	/**
	 * 删除【组合资产应收款头寸】数据
	 * @param ptlSecReceivablesList
	 * @param cdate
	 * @return
	 */
	int deletePtlSecReceivables(@Param("list") List<PtlSecReceivables> ptlSecReceivablesList, @Param("cdate") Date cdate);

	/**
	 * 插入【组合资产应收款头寸】数据
	 * @param ptlSecReceivablesList
	 * @return
	 */
	int insertPtlSecReceivables(@Param("list") List<PtlSecReceivables> ptlSecReceivablesList);
	//查询手工凭证
    List<BokPositionPrdfee> getManualList(Map<String, Object> sqlMap);

	/**
	 * 查询本地待同步组合代码
	 * ADD BY WUJIE ON 20210517
	 */
	List<String> getPortfolioIds(Map<String, Object> param);
	
	/**
	 * 查询本地待同步产品代码
	 * ADD BY WUJIE ON 20210518
	 */
	List<String> getProdIds(Map<String, Object> param);
}
