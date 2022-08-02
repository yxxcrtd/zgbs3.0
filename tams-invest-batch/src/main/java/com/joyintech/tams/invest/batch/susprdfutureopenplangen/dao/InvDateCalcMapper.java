/*
 * 文件名：DateCalcMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.susprdfutureopenplangen.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 付息计划，开放计划，利率重置，申购计划，赎回计划计算用和日期关联的Mapper
 * 
 * @author 何阳阳
 * @version 1.0 2018年9月21日
 * @see InvDateCalcMapper
 * @since 1.0
 */
@Repository
public interface InvDateCalcMapper {

    /**
     * 交易日历的节假日取得
     * 
     * @param paraMap 参数
     * @return 节假日列表
     */
    List<String> getDealCalendarHoliday(Map<String, Object> paraMap);

    /**
     * 查询日期参数是否节假日
     * 
     * @param dateStr 日期字符串
     * @param calId 日历ID
     * @return 是否节假日
     */
    int selectDealCalendarHolidayCount(@Param("dateStr")String dateStr, @Param("calId")String calId);
}
