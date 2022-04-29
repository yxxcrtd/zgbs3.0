/*
 * 文件名：ResetBaseRuleInfoMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetbaseruleinfo.dao;

import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.MstIndexComponentDto;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.MstIntRateInfoDto;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.ResetBaseRuleInfoDto;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 业绩基准重置
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see ValResetBaseRuleInfoMapper
 * @since 1.0
 */
@Repository
public interface ValResetBaseRuleInfoMapper {

    /**
     * 非复合基准 + 复合基准
     * <功能详细描述>
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    List<ResetBaseRuleInfoDto> selectAllBaseRuleInfo(Map<String,Object> paramMap);

    /**
     * 复合基准
     * <功能详细描述>
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    @PreDestroy
    List<ResetBaseRuleInfoDto> selectAllBaseRuleInfoIsFh(Map<String,Object> paramMap);
    
    /**
     * 获取基准成分信息
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    List<MstIndexComponentDto> selectMstIndexComponentInfo(Map<String,Object> paramMap);
    
    /**
     * 获取利率行情信息
     * <功能详细描述>
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    MstIntRateInfoDto getMstIntRateInfo(Map<String,Object> paramMap);
    
    /**
     * 获取最大申购开始日
     * <功能详细描述>
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    Date getMaxOpenVdate(Map<String,Object> paramMap);
    
    /**
     * 获取起息日
     * <功能详细描述>
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    Date getVdate(Map<String,Object> paramMap);
    
    BigDecimal getClostPrice(Map<String,Object> paramMap);
    
    BigDecimal getPClostPrice(Map<String,Object> paramMap);
    
    void deleteMstRateInfo(Map<String,Object> paramMap);

}
