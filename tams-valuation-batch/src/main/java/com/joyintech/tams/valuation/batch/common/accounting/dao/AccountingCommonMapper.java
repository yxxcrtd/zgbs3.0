/*
 * 文件名：AccountingCommonMapper.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.accounting.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.tams.common.base.entity.BokAcctLayeringInfo;
import com.joyintech.tams.common.base.entity.BokBankBalance;
import com.joyintech.tams.common.base.entity.BokBankSceConfig;
import com.joyintech.tams.common.base.entity.BokBookdetailCurrent;
import com.joyintech.tams.common.base.entity.BokBookdetailCurrentExt;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokFeeTableSet;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.common.base.entity.BokSubject;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BalanceDetailInfoEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookNewestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.CorrespondSubjectEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.PrdCashEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceAll;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 *
 * @author wangzhiang
 * @version 1.0 2018年5月31日
 * @see AccountingCommonMapper
 * @since 1.0
 */
@Repository
public interface AccountingCommonMapper {

    /**
     * 根据条件删除 账务明细（历史表） 数据
     *
     * @param bookSetId 账套代码
     * @return 删除 账务明细（历史表） 数据
     * @see
     * @since 1.0
     */
    int deleteBookDetailAll(Map<String, Object> param);

    /**
     * 根据条件删除 账务明细（当前表） 数据
     *
     * @param bookType 凭证类型
     * @param bookSetId 账套代码
     * @return 删除 账务明细（当前表） 数据
     * @see
     * @since 1.0
     */
    int deleteBookDetailCurrent(Map<String, Object> param);

    /**
     * 根据条件删除统计分析明细表（账务明细）
     *
     * @param bookSetId 账套代码
     * @return 统计分析明细表（账务明细）
     * @see
     * @since 1.0
     */
	void deleteBokStatDetPosition(Map<String, Object> param);
	
    /**
     * 根据条件删除统计分析账套汇总表
     *
     * @param bookSetId 账套代码
     * @return 统计分析账套汇总表
     * @see
     * @since 1.0
     */
	void deleteBokStatBokPosition(Map<String, Object> param);
	
    /**
     * 根据条件删除删除账套估值汇总表（账套）
     *
     * @param bookSetId 账套代码
     * @return 删除删除账套估值汇总表（账套）
     * @see
     * @since 1.0
     */
	void deleteBokValSummary(Map<String, Object> param);
	
    /**
     * 科目余额查询-当前最新余额查询
     *
     * @param searchDto 参数Dto
     * @return 当前表科目余额结果集
     * @see
     * @since 1.0
     */
    List<SubjectBalance> queryCurrentLatestBalance(SubjectBalanceSearchDto searchDto);

    /**
     * 最新科目余额查询-历史表
     *
     * @param searchDto 参数Dto
     * @return 最新科目余额查询-历史表
     * @see
     * @since 1.0
     */
    List<SubjectBalanceAll> querySubjectBalanceOfHistory(SubjectBalanceSearchDto searchDto);

    /**
     * 科目余额方向查询
     *
     * @param bokDetailId 账务明细代码
     * @param booksetId 账套代码
     * @return 余额方向
     * @see
     * @since 1.0
     */
    String getBalFlag(@Param("subjectNo") String subjectNo, @Param("booksetId") String booksetId);

    /**
     * 基础科目性质查询
     *
     * @param groupId 分组代码
     * @param acctStandardId 会计准则代码
     * @return 科目性质列表
     * @see
     * @since 1.0
     */
    List<String> getValSubjectNatureList(@Param("groupId") String groupId,@Param("acctStandardId") String acctStandardId);
    /**
     * 帐套资产负债查询
     *
     * @param sdateStart 查询开始日
     * @param sdateEnd 查询结束日 
     * @param bookDate 入账日
     * @param booksetId 帐套代码
     * @return 帐套资产负债信息
     * @see
     * @since 1.0
     */
    /*List<BokStatBokPosition> getValStatBokPosition(
                                                   @Param("sdateStart") Date sdateStart,
                                                   @Param("sdateEnd") Date sdateEnd,
                                                   @Param("bookDate") Date bookDate,
                                                   @Param("booksetId") String booksetId,
                                                   @Param("queryDates") List<Date> queryDates,
                                                   @Param("finprodId") String finprodId);*/
    List<BokStatBokPosition> getValStatBokPosition(BokStatBokPositionDto position);

    /**
     * TISHKBS-1998 【估值核算-估值表】货币型产品估值表中的“本日收益-费后”统计的是当日应付利润发生额，但是并未将手工账的应付利润发生额统计进去
     * @param params
     * @return
     */
    BigDecimal getVouchAmt(Map<String, Object> params);
    /**
     * 查询传入条件下帐套资产负债信息
     *
     * @param bookDate 入账日
     * @param happenDate 发生日
     * @param booksetId 账套代码
     * @return ValStatBokPosition
     * @see
     * @since 1.0
     */
    int queryValStatBokPosition(@Param("bookDate") Date bookDate,
                                @Param("happenDate") Date happenDate,
                                @Param("booksetId") String booksetId);

    /**
     * 待计提金融产品现金流查询
     *
     * @param happenDate 发生日
     * @param taskId 任务代码
     * @param cashTypeList 现金流类型集合
     * @return 金融产品信息集合
     * @see
     * @since 1.0
     */
    List<PrdCashEntity> queryFinPrdCash(Map<String, Object> params);

    /**
     * 账套科目余额方向信息
     *
     * @return 账套科目表集合
     * @see
     * @since 1.0
     */
    List<BalanceDetailInfoEntity> queryAccountBalanceDirection(Map<String, Object> params);

    /**
     * 查询上一工作日
     *
     * @param cdate 查询日期
     * @param isHoliday 是否节假日(0代表非节假日)
     * @param calendarId 日历编码
     * @return 日期
     * @see
     * @since 1.0
     */
    Date getCDate(@Param("cdate") Date cdate, @Param("isHoliday") String isHoliday,
                  @Param("calendarId") String calendarId);

    /**
     * 是否节假日
     *
     * @param cdate 查询日期
     * @param calendarId 日历编码
     * @return 是否节假日(0：工作日；其他：节假日)
     * @see
     * @since 1.0
     */
    String getIsHoliday(@Param("cdate") Date cdate, @Param("calendarId") String calendarId);

//    /**
//     * 账务明细余额-区间
//     *
//     * @param searchDto 参数Dto
//     * @return 账务明细余额-区间
//     * @see
//     * @since 1.0
//     */
//    List<SubjectBalance> getBookDetailInterval(BookDetailBalanceInvSearchDto searchDto);

    /**
     * 凭证合并配置查询
     *
     * @param booksetId 账套代码
     * @return 凭证合并配置
     * @see
     * @since 1.0
     */
    List<BokBooksetExt> getBookAccountMergeConfig(@Param("booksetId") String booksetId);

    /**
     * 获取最大凭证序号
     *
     * @param vouchYear 年份
     * @param vouchMonth 月份
     * @return
     * @see
     * @since 1.0
     */
    String getMaxBookAccountNum(@Param("vouchYear") String vouchYear,
                                @Param("vouchMonth") String vouchMonth,
                                @Param("booksetId") String booksetId);

    /**
     * 对应科目查询
     *
     * @param detailSubjectNoTail 四级科目尾号
     * @param booksetId 账套代码
     * @param baseSubjectNature 基础科目性质列表
     * @return 科目信息列表
     * @see
     * @since 1.0
     */
    List<CorrespondSubjectEntity> getCorrespondSubject(@Param("detailSubjectNoTail") String detailSubjectNoTail,
                                                 @Param("booksetId") String booksetId,
                                                 @Param("baseSubjectNature") List<String> baseSubjectNature);
    
    /**
     * 
     * 交易最新账务查询
     * @param booksetId 账套代码
     * @param bookDate 入账日
     * @param tradeId 交易编号
     * @return 交易最新账务
     * @see
     * @since 1.0
     */
    List<BookNewestInfoDto> searchNewestBook(@Param("booksetId") String booksetId,
                                                 @Param("bookDate") Date bookDate,
                                                 @Param("tradeId") String tradeId);
    
    /**
     * 
     * 账套参数查询
     * @param booksetId 账套代码
     * @param paramCodeList 参数项
     * @return 账套配置表DTO
     * @see
     * @since 1.0
     */
    List<BokBooksetExt> getValBokBooksetExt(@Param("booksetId") String booksetId,
                                         @Param("paramCodeList") List<String> paramCodeList);
    /**
     * 
     * 凭证最新账务查询
     * @param paMap
     * @return
     * @see
     * @since 1.0
     */
    List<BookLatestInfoDto> getBookLatestInfo(Map<String, Object> paMap);
    
    /**
     * 
     * 账务类共通方法设计 - 统计分析账务明细汇总查询 
     * @param paMap
     * @return
     * @see
     * @since 1.0
     */
    List<BokStatDetPosiiton> getCountAnalysis(Map<String, Object> paMap);
    
    /**
     * 
     * 待计提现金流查询(分层)
     * @return
     * @see
     * @since 1.0
     */
    List<StayDrawSubprdEntity> getStayDrawSubprdInfo(Map<String,Object> map);
    
    /**
     * 
     * 凭证最新账务查询
     * @param paMap
     * @return
     * @see
     * @since 1.0
     */
    int getBookLatestInfoOnway(Map<String, Object> paMap);
    
    /**
     * 
     * 最新结息日查询
     * @param paMap
     * @return
     * @see
     * @since 1.0
     */
    List<BokBookdetailCurrent> getNewestMdate(Map<String, Object> paMap);

    /**
     * 费用场景配置查询
     * 
     * @param map 
     * @return List<FeeTableSet>
     * @see
     * @since 1.0
     */
    List<BokFeeTableSet> getFeeTableSetQuery(Map<String, Object> map);

    /**
     * 待计提子产品现金流查询_指定帐套的核算主体和TA产品
     * <功能详细描述>
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    List<StayDrawSubprdEntity> getPrdInfoOrTAInfo(Map<String, Object> params);

    /**
     * 子产品待计提现金流查询_理财产品
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    List<StayDrawSubprdEntity> getStayDrawSubprdInfoByPrd(Map<String, Object> params);

    /**
     * 查找分层子结构
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    List<StayDrawSubprdEntity> querySubBokAcctInfo(Map<String, Object> params);

    /**
     * 预期收益型TA中收查询——指定帐套的核算主体和TA产品
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    List<StayDrawSubprdEntity> queryFinPrdInfoByPrd(Map<String, Object> params);
    
    /**
     * 账务类共通方法设计 - 账务明细辅助查询
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    BokBookdetailCurrentExt queryBokBookdetailCurrentExt(Map<String, Object> params);

    /**
     *  删除账务明细辅助表数据 - 类似账务明细删除方法【deleteBookDetailCurrent】
     * 
     * @param booksetIds 账套代码列表
     * @param bookType 凭证类型
     * @return
     * @see
     * @since 1.0
     */
    int deleteBokBookdetailCurrentExt(Map<String, Object> param);
    
    /**
     * 获取对应科目查询
     *
     * @param subjectNo 科目号
     * @param booksetId 账套代码
     * @return 科目信息列表
     * @see
     * @since 1.0
     */
	List<BokSubject> getSubjectInfo(Map<String, Object> map);
	/**
     *  删除账套回补信息表数据
     *
     * @param booksetIds 账套代码列表
     * @param bookType 凭证类型
     * @return
     * @see
     * @since 1.0
     */
    int deleteBokClawbackInfo(Map<String, Object> param);

    /**
     *  删除账套回补信息明细表数据
     *
     * @param booksetIds 账套代码列表
     * @param bookType 凭证类型
     * @return
     * @see
     * @since 1.0
     */
    int deleteBokClawbackInfoDetail(Map<String, Object> param);
    
    /**
     * 删除统计分析明细表（费用）
     * @param booksetIds 代码
     * @return
     */
    int deleteBokStatFeePosition(@Param("booksetIds") List<String> booksetIds);
    
	/**
	 * 根据账务明细代码，获取分层信息
	 * @param bokDetailId
	 * @return
	 */
	BokAcctLayeringInfo getBokAcctLayeringInfoByBokDetailId(@Param("bokDetailId") String bokDetailId);

	/**
	 * 根据业务明细代码和分支序号，获取分层信息
	 * @param busiId
	 * @param branch
	 * @return
	 */
	BokAcctLayeringInfo getLayeringInfoByBusiIdAndBranch(@Param("busiId") String busiId,
			                                             @Param("branch") Long branch);

	/**
     * 区间到账应计利息
     * 
     */
	List<TrdProductDeal> intervalArrivalAccount(Map<String, Object> map);
    
    /**
	 * 共通【银行账场景配置查询】
	 * 
	 * @param param
	 * @return
	 */
	List<BokBankSceConfig> getBokBankSceConfig(Map<String, Object> param);

	/**
     * 银行账计提余额
     * @param map
     * @return
     */
    List<BokBankBalance> selectBokBankBalance(Map<String, Object> map);
    
    /**
     * 科目余额查询
     * @param map
     * @return
     */
    BigDecimal selectSubjectBalance(Map<String, Object> map);
    
    /**
     * 公允价值变动科目余额查询
     * @param map
     * @return
     */
    BigDecimal selectFloatSubjectBalance(Map<String, Object> map);


    /**
     *  删除资产行情信息表数据
     * @return
     * @see
     * @since 1.0
     */
    int deleteBokAssetMarketInfo(Map<String, Object> param);

    /**
     * 对应科目查询
     *
     * @param detailSubjectNoTail 四级科目尾号
     * @param booksetId 账套代码
     * @param baseSubjectNature 基础科目性质列表
     * @return 科目信息列表
     * @see
     * @since 1.0
     */
    List<CorrespondSubjectEntity> getCorrespondSubjectNew(@Param("detailSubjectNoTail") String detailSubjectNoTail,
                                                       @Param("booksetId") String booksetId,
                                                       @Param("baseSubjectNature") List<String> baseSubjectNature);

    /**
     *
     * 账套参数查询
     * @param booksetId 账套代码
     * @param paramCode 参数项
     * @return 账套配置表DTO
     * @see
     * @since 1.0
     */
    BokBooksetExt getBokBooksetExt(@Param("booksetId") String booksetId,
                                   @Param("paramCode") String paramCode);

    /* @Author 殷亚
     * @Description 账套分层子结构全量查询
     * @Date  2022-3-10 18:48
     * @Version 1.0
     * @Param []
     * @Return java.util.List<com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity>
     **/
    List<StayDrawSubprdEntity> queryAllSubBokAcctInfo();

    /**
     *
     * 账套参数全量查询
     * @return 账套配置表DTO
     * @see
     * @since 1.0
     */
    List<BokBooksetExt> getAllBokBooksetExt();

    /**
     *
     * 账套参数全量查询
     * @return 账套配置表DTO
     * @see
     * @since 1.0
     */
    List<BokSubject> getAllBokSubject();
}
