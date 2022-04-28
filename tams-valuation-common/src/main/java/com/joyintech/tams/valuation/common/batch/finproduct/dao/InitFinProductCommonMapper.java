/*
 * 文件名：FinProductCommonMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.batch.finproduct.dao;

import com.joyintech.tams.common.base.entity.BokAmoConfig;
import com.joyintech.tams.common.base.entity.BokFinProductValuationPara;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 标的类共通方法 - 数据库数据获取
 * 
 * @author wangzhiang
 * @version 1.0 2018年5月31日
 * @see InitFinProductCommonMapper
 * @since 1.0
 */
@Repository("InitFinProductCommonMapper")
public interface InitFinProductCommonMapper {

    /**
     * 查询标的信息
     *
     * @param paMap 传递参数
     * @return TargetFinProduct
     * @see
     * @since 1.0
     */
    List<TargetFinProduct> getTargetFinProduct(Map<String, Object> paMap);

    /**
     * 查询标的现金流计划
     *
     * @param paMap 传递参数
     * @return TargetCashPlan
     * @see
     * @since 1.0
     */
    List<TargetCashPlan> getTargetCashPlanList(Map<String, Object> paMap);

    /**
     * 查询标的现金流计算规则
     *
     * @param paMap 传递参数
     * @return TargetCashCalcRule
     * @see
     * @since 1.0
     */
    List<TargetCashCalcRule> getTargetCashCalcRuleList(Map<String, Object> paMap);

    /**
     * 查询标的现金流支付规则
     *
     * @param paMap 传递参数
     * @return TargetCashPayRule
     * @see
     * @since 1.0
     */
    List<TargetCashPayRule> getTargetCashPayRuleList(Map<String, Object> paMap);

    /**
     * 下一行权计划查询 <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<FinExercisePlan> queryFinExercisePlan(Map<String, Object> map);

    /**
     * 标的附加信息查询 <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<FinProductAdd> queryFinProductAdd(Map<String, Object> map);

    /**
     * 账套金融产品估值参数查询 <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<BokFinProductValuationPara> queryBokFinProductValuationPara(Map<String, Object> map);

    /**
     *
     * 摊销重算配置查询<一句话功能简述> <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<BokAmoConfig> getBokAmoConfig(Map<String, Object> map);

    /**
     * 区间到账应计利息
     *
     */
    List<InitTrdProductDealDto> intervalArrivalAccount(Map<String, Object> map);

    /* @Author 殷亚
     * @Description 获取金融产品标的信息
     * @Date  2022-3-21 10:48
     * @Version 1.0
     * @Param [map]
     * @Return java.util.List<com.joyintech.tams.valuation.common.batch.finproduct.entity.TradeInfoDto>
     **/
    List<TradeInfoDto> queryTradeInfoList(Map<String, Object> map);

}
