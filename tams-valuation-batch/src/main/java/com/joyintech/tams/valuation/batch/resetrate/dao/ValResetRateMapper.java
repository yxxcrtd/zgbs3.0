/*
 * 文件名：ResetRateMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetrate.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.joyintech.tams.common.base.entity.InterfFloatingcouponsrate;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.MstIntRateInfoDto;
import com.joyintech.tams.valuation.common.asset.dto.FinRateInfoDto;

/**
 *  利率重置
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月21日
 * @see ValResetRateMapper
 * @since 1.0
 */
@Repository
public interface ValResetRateMapper {

    void deleteFinCashCalRule(Map<String,Object> map);
    
    List<FinRateInfoDto> selectAllRateInfo(Map<String,Object> map);
    
    List<InterfFloatingcouponsrate> selectInterfFloatingcouponsrate(Map<String,Object> map);

    MstIntRateInfoDto getMstIntRateInfo(Map<String,Object> paramMap); 
    
    MstIntRateInfoDto getMstIntRateInfo2(Map<String,Object> paramMap); 
    
    Date getPlanVdateBySysDate(Map<String,Object> paramMap);
    
    List<String> getDealCalendarHoliday(Map<String, Object> paraMap);
    

}
