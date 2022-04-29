/*
 * 文件名：PaActiveRemind001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年8月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.remind.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.tams.invest.remind.dto.PaActiveRemind002Dto;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年8月21日
 * @see PaActiveRemind001Mapper
 * @since 1.0
 */
@Repository
public interface PaActiveRemind001Mapper {
    /**
     * 实时询主动提醒数量_产品端
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR1(Map<String, Object> param);

    /**
     * 实时询主动提醒_净值小于1产品列表
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    List<String> searchCountR1S7(Map<String, Object> param);

    /**
     * 实时询主动提醒数量_315检查
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR2(Map<String, Object> param);

    /**
     * 实时询主动提醒数量_回款及存续期确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR3(Map<String, Object> param);

    /**
     *
     * 债券-提前3个月提醒信息查询
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int selBondThreeMon(Map<String, Object> param);
    /**
     *
     * 债券-提前1个月提醒信息查询
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int selBondOneMon(Map<String,Object> param);
    /**
     *
     * 债券-提前5天提醒信息查询
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int selBondFiveDay(Map<String, Object> param);
    /**
     *
     * 基金类(货币型)申购确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int currencyPurchaseConfirm(Map<String, Object> param);
    /**
     *
     * 基金类(货币型)赎回确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int currencyRedemptionConfirm(Map<String, Object> param);
    /**
     *
     * 基金类（净值型）申购确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int networthPurchaseConfirm(Map<String, Object> param);
    /**
     *
     * 基金类（净值型）赎回确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int networthRedemptionConfirm(Map<String, Object> param);
    /**
     *
     * 计划类（净值型）申购确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int PlanPurchaseConfirm(Map<String, Object> param);
    /**
     *
     * 计划类（净值型）赎回确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int PlanRedemptionConfirm(Map<String, Object> param);
    /**
     *
     * 查询基金类（净值型）回款确认主动提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int networthPaybackConfirm(Map<String, Object> param);
    /**
     *
     * 查询基金类（货币型）回款确认主动提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int currencyPaybackConfirm(Map<String, Object> param);
    /**
     *
     * 查询计划类（净值型）回款确认主动提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
//    int PlanPaybackConfirm(Map<String, Object> param);
    /**
     *
     * 查询债券存续期主动提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    int bondDuration(Map<String, Object> param);
    /**
     *
     * 查询项目类资产存续期主动提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    int projectAssetDuration(Map<String, Object> param);
    /**
     *
     * 查询计划类（利率型）存续期主动提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    int planRateDuration(Map<String, Object> param);
    /**
     *
     * 债券行权主动提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    int bondExercise(Map<String, Object> param);
    /**
     *
     * 债券行权及覆盖主动提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR4(Map<String, Object> param);

    /**
     * 实时询主动提醒数量_风险提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR5(Map<String, Object> param);

    //add by chenjiaxing 固定值参数更新提醒 20210726
    /**
     * 实时询主动提醒数量_风险提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR5S3(Map<String, Object> param);
    //add by chenjiaxing 固定值参数更新提醒 20210726
    /**
     * 净值披露
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR6S1(Map<String, Object> param);
    /**
     * 债券评级变动
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR7S1(Map<String, Object> param);
    /**
     * 项目类资产
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR7S2(Map<String, Object> param);
    /**
     * 舆情提醒
     * <功能详细描述>
     * @param string 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    List<PaActiveRemind002Dto> searchCountR7S3(Map<String, Object> param);
    /**
     * 申赎及存续期确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR3S16(Map<String, Object> param);
    /**
     * 实时查询主动提醒数量---债券存续期
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR3S5(Map<String, Object> param);
    /**
     * 实时查询主动提醒数量---债券存续期回款确认
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR3S6(Map<String, Object> param);

    /**
     * 实时询主动提醒数量_项目类资产存续期
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR3S8(Map<String, Object> param);
    /**
     * 实时询主动提醒数量_质押式回购存续期
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR3S14(Map<String, Object> param);

    /**
     * 实时询主动提醒数量_买断式回购存续期
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR3S15(Map<String, Object> param);

    /**
     * 实时询主动提醒数量_存款类资产存续期
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR3S13(Map<String, Object> param);

    /**
     * 获取所有资产信息
     */
    List<PaActiveRemind002Dto> searchAssetInfo();

    /**
     * 查询舆情提醒 R7S3数量
     * @param param
     * @return
     */
    Map<String, Object> searchCountR7S3new(Map<String, Object> param);

    /**
     * 查询数据修改 R9S1数量
     * @param param
     * @return
     */
    Map<String, Object> searchCountR9S1(Map<String, Object> param);

    //add by chenjiaxing 分销缴款提醒 20210728
    /**
     * 实时询主动提醒数量_风险提醒
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询出信息的条数
     * @see
     * @since 1.0
     */
    Map<String, Object> searchCountR11S1(Map<String, Object> param);
    //add by chenjiaxing 分销缴款提醒 20210728

    /**
     查询数据修改 R10S1数量
     * @param param
     * @return
     */
    Map<String, Object> searchCountR10S1(Map<String, Object> param);

    /**
     查询数据修改 R10S2数量
     * @param param
     * @return
     */
    Map<String, Object> searchCountR10S2(Map<String, Object> param);


    /**
     查询标准券额度不足 R12S1数量
     * @param param
     * @return
     */
    Map<String, Object> searchCountR12S1(Map<String, Object> param);


    /**
     * 标准券额度不足提醒查询资产代码
     * @param param
     * @return
     */
    Map<String, Object> searchFinprodId(Map<String, Object> param);

}
