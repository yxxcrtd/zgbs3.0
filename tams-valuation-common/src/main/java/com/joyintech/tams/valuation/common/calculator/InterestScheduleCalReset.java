/*
 * 文件名：InterestScheduleCalReset.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.calculator;

import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinRateInfo;
import com.joyintech.tams.valuation.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPlanDetailDto;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPlanDto;


import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 付息计划生成 (重新生成)
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月17日
 * @see InterestScheduleCalReset
 * @since 1.0
 */
public class InterestScheduleCalReset {

 
 /**
  * 付息计划生成 (重新生成)
  * @param lis 付息计划列表 （传入的list会被修改）
  * @param scai 资产基本信息
                                                                                资产代码
                                                                                 起息日
                                                                                 到期日
                                                                                 计息基础
                                                                                 利率类型
                                                                                 初始利率（或称票面利率）
                                                                                 币种
                                                                                 资产类型
                                                                                 本金
                                                                                 交易日历
  * @param lirs  还本计划列表
  * @param irr 利率重置规则
  * @param liy 利率报价列表
  * @param isr 付息规则
  * @param holidayList 节假日列表
  * @param sysdate 重新生成日期 （平台日）
  * @param <T> 泛型
  * @param class1 需要返回的类型
  * @param classdetail 付息区间详细传 InvScheduleDetail.class
  * @return @return 付息计划
 * @throws ParseException 
  * @see
  * @since 1.0
  */
 public static <T extends FinCashPlanDto> List<T> interestScheduleCalA(List<T> lis, AssetCommonInfoDto scai,
                                                                       List<FinCashPlanDto> lirs,
                                                                       FinRateInfo irr, List<? extends FinCashCalcRule> liy, FinCashPayRule isr,
                                                                       List<String> holidayList, Date sysdate, Class<T> class1,
                                                                       Class<? extends FinCashPlanDetailDto> classdetail) throws ParseException {
     InterestScheduleCal.interestScheduleCalA(lis, scai, lirs, irr, liy, isr, holidayList, scai.getAmount(), classdetail);
     return lis;
 }
}
