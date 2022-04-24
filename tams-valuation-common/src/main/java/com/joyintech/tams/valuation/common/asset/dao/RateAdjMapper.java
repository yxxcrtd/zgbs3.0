/*
 * 文件名：RateAdjMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.asset.dao;

import com.joyintech.tams.common.base.entity.FinCashCalcRuleKey;
import com.joyintech.tams.common.base.entity.PaFinCashCalcRuleKey;
import com.joyintech.tams.valuation.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPayRuleDto;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.valuation.common.asset.dto.RateAdjDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 *  利率重置
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月22日
 * @see RateAdjMapper
 * @since 1.0
 */
@Repository
public interface RateAdjMapper {
    
    List<String> getDealCalendarHoliday(Map<String, Object> paraMap);
    
    FinCashPayRuleDto getFinPayInterestStdInfoByProductId(Map<String,Object> param);
    
    List<RateAdjDto> selectInitAssetRateAdj(Map<String,Object> param);
    
    List<FinCashPlanDto> getFinCashPlanByProductId(Map<String,Object> param);
    
    AssetCommonInfoDto getAssetBasicInfoByFinprodId(Map<String,Object> param);
    
    /**
     * 根据cashId删除计算规则
     * <一句话功能简述>
     * <功能详细描述>
     * @param key
     * @return
     * @see
     * @since 1.0
     */
    int deleteCashCalcRuleByCashId(FinCashCalcRuleKey key);
    
    /**
     * 根据cashId删除计算规则扩展
     * <功能详细描述>
     * @param key
     * @return
     * @see
     * @since 1.0
     */
    int deleteCashCalcRuleAddByCashId(PaFinCashCalcRuleKey key);
}
