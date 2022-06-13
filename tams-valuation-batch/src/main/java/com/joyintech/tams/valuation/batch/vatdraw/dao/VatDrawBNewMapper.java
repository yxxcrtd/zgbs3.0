/*
 * 文件名：VatDrawMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年8月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdraw.dao;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.batch.vatdraw.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 增值税计提批处理Mapper
 * 
 * @author weipeng
 * @version 1.0 2018年8月1日
 * @see VatDrawBNewMapper
 * @since 1.0
 */
@Repository
public interface VatDrawBNewMapper {

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
    List<PrdCashDto> queryPrdCashDto(Map<String, Object> params);

    /**
     * 查询账务明细最大的处理序列
     *
     * @param happenDate 发生日期
     * @param bookDate 入账日期
     * @return 账务明细最大的处理序列
     * @see
     * @since 1.0
     */
    TaxBookOrderDto queryMaxBookOrders(
        @Param("happenDate")
            Date happenDate,
        @Param("bookDate")
            Date bookDate,
        @Param("booksetId")
            String booksetId);

    /**
     * 查询账套下不启用场景配置.配置代码
     *
     * @param listConfigDto 账套税务配置
     * @return
     * @see
     * @since 1.0
     */
    List<String> queryCfBokConfig(
        @Param("listConfigDto")
            List<BooksetTaxConfigDto> listConfigDto);

    /**
     * 查询税额计提分类配置
     *
     * @return 税额计提分类配置
     * @see
     * @since 1.0
     */
    List<ValTaxCalConfigDto> queryTaxCalConfig(Map<String, Object> param);


    /**
     * 取发生日期初增值税明细余额
     *
     * @param happenDate 发生日
     * @param bookDate 入账日
     * @param bookOrder 处理序列
     * @param booksetId 账套代码
     * @return 发生日期初增值税明细余额
     * @see
     * @since 1.0
     */
    List<TaxBokBalanceDto> queryTaxBokBalance(
        @Param("happenDate")
            Date happenDate,
        @Param("bookDate")
            Date bookDate,
        @Param("bookOrder")
            BigDecimal bookOrder,
        @Param("booksetId")
            String booksetId);

    /**
     * 查询各账套税汇总昨日余额
     *
     * @param happenDate 发生日期
     * @param bookDate 入账日期
     * @param bookOrder 处理序列
     * @param booksetId 账套代码
     * @return 账套税汇总昨日余额
     * @see
     * @since 1.0
     */
    List<TaxBokBalanceSum> queryYtsTaxBalanceSum(
        @Param("happenDate")
            Date happenDate,
        @Param("bookDate")
            Date bookDate,
        @Param("bookOrder")
            BigDecimal bookOrder,
        @Param("booksetId")
            String booksetId);

    /**
     * 汇总利息收入确认
     *
     * @param params 账套代码、发生日期、入账日期、增值税类型、计税方式
     * @return 汇总利息收入
     * @see
     * @since 1.0
     */
    List<TaxBalanceSumDto> collectCarryAmt(Map<String, Object> params);

    /**
     * 汇总预估利息收入计提
     *
     * @param params 账套代码、发生日期、入账日期、增值税类型、计税方式、确认标识
     * @return 汇总利息收入
     * @see
     * @since 1.0
     */
    TaxBalanceSumDto collectBalance(Map<String, Object> params);

    /**
     * 汇总价差收入处理
     *
     * @param params 账套代码、发生日期、入账日期、增值税类型、计税方式、确认标识
     * @return 汇总价差收入
     * @see
     * @since 1.0
     */
    TaxBalanceSumDto collectPriceDiffIncome(Map<String, Object> params);

    /**
     * 批量插入增值税账务明细流水表
     *
     * @param list 增值税账务明细流水集合
     * @return 插入条数
     * @see
     * @since 1.0
     */
    int insertTaxBokDetail(List<TaxBokDetail> list);

    /**
     * 批量插入增值税账务明细余额表
     *
     * @param list 增值税账务明细余额集合
     * @return 插入条数
     * @see
     * @since 1.0
     */
    int insertTaxBokBalance(List<TaxBokBalance> list);

    /**
     * 批量插入增值税账务明细余额汇总表
     *
     * @param list 增值税账务明细余额汇总集合
     * @return 插入条数
     * @see
     * @since 1.0
     */
    int insertTaxBokBalanceSum(List<TaxBokBalanceSum> list);

    /**
     * 批量插入增值税已抵扣增值额
     * @param list
     * @return
     */
    int insertTaxDeductedBalance(List<TaxDeductedBalance> list);

    /**
     * 查询现金流计划表
     * @param tradeId
     * @param psT09
     * @param cashType
     * @return
     * @see
     * @since 1.0
     */
    FinCashPlan queryCashPlan(
        @Param("tradeId")
            String tradeId,
        @Param("cashTypeC01")
            String cashTypeC01,
        @Param("psT09")
            String psT09);

	/**
	 * 查询息票品种
	 * @param busiId
	 * @param type
	 * @return
	 */
	String queryCouponSpecies(
        @Param("busiId")
            String busiId,
        @Param("type")
            String type);

	/**
	 * 查询增值税已抵扣增值额最大处理序列
	 * @return
	 */
	BigDecimal queryMaxDeductBookOrder();

	/**
	 * 统计账务明细代码待抵扣增值额
	 * @param booksetId
	 * @return
	 */
	List<TaxTurnedBalance> queryTaxTurnedBalList(
        @Param("booksetId")
            String booksetId,
        @Param("happenDate")
            Date happenDate,
        @Param("bookDate")
            Date bookDate);

	/**
	 * 取账套的本位币
	 *
	 * @param booksetId
	 * @return
	 * @see
	 * @since 1.0
	 */
	String getStandardCcy(
        @Param("booksetId")
            String booksetId);

    /**
     * 根据发行标的组装上市标的的计税流水
     *
     * @param orginInfoMap
     * @return 上市标的账务明细代码
     * @see
     * @since 1.0
     */
    String getTaxBokDetailsByOrgin(Map<String, String> orginInfoMap);

    /**
     * 判断资产是否是上市标的
     *
     * @param busiId
     * @return 标的基本信息
     * @see
     * @since 1.0
     */
    Boolean getIsShbdByBusiId(@Param("busiId") String busiId);
}
