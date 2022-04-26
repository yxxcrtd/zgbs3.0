/*
 * 文件名：PaActiveRemind002Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年8月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.remind.dao;

import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.SysActivereminderSet;
import com.joyintech.tams.invest.remind.dto.PaActiveRemind001Dto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年8月22日
 * @see PaActiveRemind002Mapper
 * @since 1.0
 */
@Repository
public interface PaActiveRemind002Mapper {
    /**
     * 查询主动提醒配置表
     * @param remType 配置类型
     * @return 配置值集合
     */
    List<SysActivereminderSet> searchSysRemSet(@Param("remType") String remType);

    /**
     *
     * 实时查询主动提醒_产品募集
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR1S1(Map<String, Object> param);

    /**
     *
     * 实时查询主动提醒_产品起息
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR1S2(Map<String, Object> param);

    /**
     *
     * 实时查询主动提醒_产品分红方案登记
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR1S3(Map<String, Object> param);

    /**
     *
     * 实时查询主动提醒_子周期强制赎回提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR1S4(Map<String, Object> param);

    /**
     *
     * 实时查询主动提醒_产品到期
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR1S5(Map<String, Object> param);

    /**
     *
     * 实时查询主动提醒_产品费用支付
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR1S6(Map<String, Object> param);


    /**
     *
     * 实时查询主动提醒_产品的最新净值小于1
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR1S7(Map<String, Object> param);
    /**
    *
    * 净值披露
    * <功能详细描述>
    * @param param 传入查询参数
    * @return 返回查询出信息的集合
    * @see
    * @since 1.0
    */
   List<PaActiveRemind001Dto> selramindR6S1(Map<String, Object> param);
   
   /**
   *
   * 债券评级提醒
   * <功能详细描述>
   * @param param 传入查询参数
   * @return 返回查询出信息的集合
   * @see
   * @since 1.0
   */
   List<PaActiveRemind001Dto> selramindR7S1(Map<String, Object> param);

    /**
     *
     * 数据修改提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR9S1(Map<String, Object> param);

   /**
   *
   * 项目类资产标的修改提醒
   * <功能详细描述>
   * @param param 传入查询参数
   * @return 返回查询出信息的集合
   * @see
   * @since 1.0
   */
   List<PaActiveRemind001Dto> selramindR7S2(Map<String, Object> param);
   /**
   *
   * 舆情提醒
   * <功能详细描述>
   * @param param 传入查询参数
   * @return 返回查询出信息的集合
   * @see
   * @since 1.0
   */
   List<PaActiveRemind001Dto> selramindR7S3(Map<String, Object> param);
   /**
   *
   * 债券借贷融出的券还本收息提醒
   * <功能详细描述>
   * @param param 传入查询参数
   * @return 返回查询出信息的集合
   * @see
   * @since 1.0
   */
   List<PaActiveRemind001Dto> selramindR3S16(Map<String, Object> param);
    /**
     *
     * 查询项目类-提前5天主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR2S1(Map<String, Object> param);
    /**
     *
     * 查询项目类-提前1个月主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR2S2(Map<String, Object> param);
    /**
     *
     * 查询项目类-提前3个月主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR2S3(Map<String, Object> param);
    /**
     *
     * 查询计划类(利率型)—提前5天主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR2S4(Map<String, Object> param);
    /**
     *
     * 查询计划类(利率型)—提前1个月主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR2S5(Map<String, Object> param);
    /**
     *
     * 查询计划类(利率型)—提前3个月主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR2S6(Map<String, Object> param);
    /**
     *
     * 查询计划类（净值型）回款确认主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S3(Map<String, Object> param);
    /**
     *
     * 查询计划类（利率型）存续期主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S10(Map<String, Object> param);
    /**
     *
     * 查询计划类（利率型）存续期确认主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S11(Map<String, Object> param);
    /**
     *
     * 查询计划类（货币型）回款确认主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S12(Map<String, Object> param);
    /**
     *
     * 查询债券-提前三个月主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
//    List<PaActiveRemind001Dto> selBondThreeMon(Map<String, Object> param);
    /**
     *
     * 查询债券-提前1个月主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
//    List<PaActiveRemind001Dto> selBondOneMon(Map<String, Object> param);
    /**
     *
     * 查询债券-提前5天主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
//    List<PaActiveRemind001Dto> selBondFiveDay(Map<String, Object> param);
    /**
     *
     * 查询基金类（货币型）申购确认主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
//    List<PaActiveRemind001Dto> currencyPurchaseConfirm(Map<String, Object> param);
    /**
     *
     * 查询基金类（货币型）赎回确认主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
//    List<PaActiveRemind001Dto> currencyRedemptionConfirm(Map<String, Object> param);
    /**
     *
     * 查询基金类（净值型）申购确认主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
//    List<PaActiveRemind001Dto> networthPurchaseConfirm(Map<String, Object> param);
    /**
     *
     * 查询基金类（净值型）赎回确认主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
//    List<PaActiveRemind001Dto> networthRedemptionConfirm(Map<String, Object> param);
    /**
     *
     * 查询计划类（净值型）申购确认主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
//    List<PaActiveRemind001Dto> PlanPurchaseConfirm(Map<String, Object> param);
    /**
     *
     * 查询计划类（净值型）赎回确认主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
//    List<PaActiveRemind001Dto> PlanRedemptionConfirm(Map<String, Object> param);
    /**
     *
     * 查询基金类（净值型）回款确认主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
//    List<PaActiveRemind001Dto> networthPaybackConfirm(Map<String, Object> param);
    /**
     *
     * 查询基金类（货币型）回款确认主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
//    List<PaActiveRemind001Dto> currencyPaybackConfirm(Map<String, Object> param);
    /**
     *
     * 查询债券存续期主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> bondDuration(Map<String, Object> param);
    /**
     *
     * 查询项目类资产存续期主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> projectAssetDuration(Map<String, Object> param);
    /**
     *
     * 债券行权主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> bondExercise(Map<String, Object> param);
    /**
     *
     * 债券行权主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR4S1(Map<String, Object> param);
    /**
     *
     * 债券覆盖主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR4S2(Map<String, Object> param);
    
    /**
    *
    * 风险主动提醒详情
    * <功能详细描述>
    * @param param 传入查询参数
    * @return 返回查询出信息的集合
    * @see
    * @since 1.0
    */
   List<PaActiveRemind001Dto> selramindR5S1(Map<String, Object> param);
   /**
    *
    * 风险主动提醒详情
    * <功能详细描述>
    * @param param 传入查询参数
    * @return 返回查询出信息的集合
    * @see
    * @since 1.0
    */
   List<PaActiveRemind001Dto> selramindR5S2(Map<String, Object> param);
   
   /**
   *
   * 风险主动提醒详情
   * <功能详细描述>
   * @param param 传入查询参数
   * @return 返回查询出信息的集合
   * @see
   * @since 1.0
   */
  List<PaActiveRemind001Dto> selramindR5(Map<String, Object> param);

    /*add by chenjiaxing 固定值参数更新提醒 20210726 start*/
    /**
     *
     * 风险主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR5S3(Map<String, Object> param);
    /*add by chenjiaxing 固定值参数更新提醒 20210726 end*/
    /**
     *
     * 申赎及存续期确认-基金类（净值型）回款确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S1(Map<String, Object> param);
    /**
     *
     * 申赎及存续期确认-基金类（货币型）回款确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S2(Map<String, Object> param);

    /**
     *
     * 主动提醒 回款及存续期确认--债券存续期
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S5(Map<String, Object> param);
    /**
     *
     * 主动提醒 回款及存续期确认--债券存续期回款确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S6(Map<String, Object> param);
    /**
     *
     * 主动提醒 回款及存续期确认--项目类存续期
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S8(Map<String, Object> param);

    /**
     *
     * 主动提醒 回款及存续期确认--存款类存续期
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S13(Map<String, Object> param);

    /**
     *
     * 主动提醒 回款及存续期确认--质押式回购存续期
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S14(Map<String, Object> param);

    /**
     *
     * 主动提醒 回款及存续期确认--买断式回购存续期
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S15(Map<String, Object> param);
    /**
     *
     * 主动提醒 回款及存续期确认--申赎及存续期确认-回购回款确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR3S4(Map<String, Object> param);

    /*add by chenjiaxing 分销缴款提醒 20210726 start*/
    /**
     *
     * 分销缴款提醒主动提醒详情
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR11S1(Map<String, Object> param);
    /*add by chenjiaxing 分销缴款提醒 20210726 end*/



    /**
     *
     * 主动提醒 债券上市流通--债券上市流通操作
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR10S1(Map<String, Object> param);

    /**
     *
     * 主动提醒 债券上市流通--资产要素登记修改
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的集合
     * @see
     * @since 1.0
     */
    List<PaActiveRemind001Dto> selramindR10S2(Map<String, Object> param);
}
